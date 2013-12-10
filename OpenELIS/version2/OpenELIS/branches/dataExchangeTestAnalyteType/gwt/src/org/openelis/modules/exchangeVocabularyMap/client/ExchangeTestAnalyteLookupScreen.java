/**
 * Exhibit A - UIRF Open-source Based Public Software License.
 * 
 * The contents of this file are subject to the UIRF Open-source Based Public
 * Software License(the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * openelis.uhl.uiowa.edu
 * 
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 * 
 * The Original Code is OpenELIS code.
 * 
 * The Initial Developer of the Original Code is The University of Iowa.
 * Portions created by The University of Iowa are Copyright 2006-2008. All
 * Rights Reserved.
 * 
 * Contributor(s): ______________________________________.
 * 
 * Alternatively, the contents of this file marked "Separately-Licensed" may be
 * used under the terms of a UIRF Software license ("UIRF Software License"), in
 * which case the provisions of a UIRF Software License are applicable instead
 * of those above.
 */
package org.openelis.modules.exchangeVocabularyMap.client;

import java.util.ArrayList;
import java.util.EnumSet;

import org.openelis.constants.Messages;
import org.openelis.domain.TestAnalyteViewDO;
import org.openelis.domain.TestMethodVO;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.event.ActionEvent;
import org.openelis.gwt.event.ActionHandler;
import org.openelis.gwt.event.DataChangeEvent;
import org.openelis.gwt.event.GetMatchesEvent;
import org.openelis.gwt.event.GetMatchesHandler;
import org.openelis.gwt.event.HasActionHandlers;
import org.openelis.gwt.event.StateChangeEvent;
import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.screen.ScreenDefInt;
import org.openelis.gwt.screen.ScreenEventHandler;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.AutoComplete;
import org.openelis.gwt.widget.QueryFieldUtil;
import org.openelis.gwt.widget.table.TableDataRow;
import org.openelis.gwt.widget.table.TableWidget;
import org.openelis.manager.TestAnalyteManager;
import org.openelis.modules.test.client.TestService;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Window;

public class ExchangeTestAnalyteLookupScreen extends Screen
                                                           implements
                                                           HasActionHandlers<ExchangeTestAnalyteLookupScreen.Action> {

    private AppButton                       commitButton, abortButton;
    private TableWidget                     testAnalyteTable;
    private AutoComplete<Integer>           test;
    private ExchangeTestAnalyteLookupScreen screen;
    private TestAnalyteManager              testAnalyteManager;

    public enum Action {
        OK, CANCEL
    };

    public ExchangeTestAnalyteLookupScreen() throws Exception {
        super((ScreenDefInt)GWT.create(ExchangeVocabularyTestAnalyteLookupDef.class));

        // Setup link between Screen and widget Handlers
        initialize();

        setState(State.DEFAULT);
        DataChangeEvent.fire(this);
    }

    private void initialize() {
        screen = this;

        test = (AutoComplete<Integer>)def.getWidget("test");
        addScreenHandler(test, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                test.setSelection(null, "");
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                Integer testId;

                testId = event.getValue();
                /*
                 * fetch the test analytes for the selected test and show them
                 * in the table
                 */
                if (testId == null) {
                    testAnalyteManager = null;
                } else {
                    try {
                        testAnalyteManager = TestAnalyteManager.fetchByTestId(testId);
                    } catch (Exception e) {
                        Window.alert(e.getMessage());
                        e.printStackTrace();
                        testAnalyteManager = null;
                    }
                }

                DataChangeEvent.fire(screen, testAnalyteTable);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                test.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });

        test.addGetMatchesHandler(new GetMatchesHandler() {
            public void onGetMatches(GetMatchesEvent event) {
                String search;

                search = QueryFieldUtil.parseAutocomplete(event.getMatch());

                window.setBusy(Messages.get().fetching());
                TableDataRow row;
                ArrayList<TableDataRow> model;
                ArrayList<TestMethodVO> list;

                model = new ArrayList<TableDataRow>();
                try {
                    list = TestService.get().fetchActiveByName(search);
                    for (TestMethodVO data : list) {
                        row = new TableDataRow(1);
                        row.key = data.getTestId();
                        row.cells.get(0)
                                 .setValue(DataBaseUtil.concatWithSeparator(data.getTestName(),
                                                                            ", ",
                                                                            data.getMethodName()));
                        model.add(row);
                        row.data = data;
                    }
                    test.showAutoMatches(model);
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                    e.printStackTrace();
                }

                window.clearStatus();
            }
        });

        testAnalyteTable = (TableWidget)def.getWidget("testAnalyteTable");
        addScreenHandler(testAnalyteTable, new ScreenEventHandler<ArrayList<TableDataRow>>() {
            public void onDataChange(DataChangeEvent event) {
                testAnalyteTable.load(getTableModel());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                testAnalyteTable.enable(true);
            }
        });

        testAnalyteTable.multiSelect(false);

        commitButton = (AppButton)def.getWidget("ok");
        addScreenHandler(commitButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                ok();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                commitButton.enable(true);
            }
        });

        abortButton = (AppButton)def.getWidget("cancel");
        addScreenHandler(abortButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                cancel();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                abortButton.enable(true);
            }
        });
    }

    private void ok() {
        TestMethodVO t;
        TestAnalyteViewDO ta;
        ExchangeTestAnalyteVO eta;
        TableDataRow taSel;
        ArrayList<TableDataRow> taSels;

        taSels = testAnalyteTable.getSelections();
        if (taSels.size() == 0) {
            window.close();
            return;
        }

        /*
         * populate the VO with the data for the chosen test, method and test
         * analyte
         */
        eta = new ExchangeTestAnalyteVO();
        t = (TestMethodVO)test.getSelection().data;
        eta.setTestName(t.getTestName());
        eta.setMethodName(t.getMethodName());

        taSel = taSels.get(0);
        ta = (TestAnalyteViewDO)taSel.data;
        eta.setTestAnalyteId(ta.getId());
        eta.setAnalyteName(ta.getAnalyteName());
        eta.setRow((Integer)taSel.getCells().get(1));
        eta.setColumn((Integer)taSel.getCells().get(2));

        ActionEvent.fire(this, Action.OK, eta);

        window.close();
    }

    private void cancel() {
        window.close();
    }

    private ArrayList<TableDataRow> getTableModel() {
        TableDataRow row;
        TestAnalyteViewDO data;
        ArrayList<TableDataRow> model;

        model = new ArrayList<TableDataRow>();
        if (testAnalyteManager == null)
            return model;

        /*
         * show the test analyte's name and the row and column it belongs to
         */
        for (int i = 0; i < testAnalyteManager.rowCount(); i++ ) {
            for (int j = 0; j < testAnalyteManager.columnCount(i); j++ ) {
                data = testAnalyteManager.getAnalyteAt(i, j);
                row = new TableDataRow(3);
                row.key = data.getId();
                row.cells.get(0).setValue(data.getAnalyteName());
                row.cells.get(1).setValue(i + 1);
                row.cells.get(2).setValue(j + 1);
                row.data = data;
                model.add(row);
            }
        }

        return model;
    }

    public HandlerRegistration addActionHandler(ActionHandler<ExchangeTestAnalyteLookupScreen.Action> handler) {
        return addHandler(handler, ActionEvent.getType());
    }

    /**
     * This class is used to provide the data related to the test analyte chosen
     * by the user e.g. the test and method names, to the screen bringing up
     * this popup
     */
    class ExchangeTestAnalyteVO {
        private String testName, methodName, analyteName;
        private Integer testAnalyteId, row, column;

        public String getTestName() {
            return testName;
        }

        public void setTestName(String testName) {
            this.testName = DataBaseUtil.trim(testName);
        }

        public String getMethodName() {
            return methodName;
        }

        public void setMethodName(String methodName) {
            this.methodName = DataBaseUtil.trim(methodName);
        }

        public Integer getTestAnalyteId() {
            return testAnalyteId;
        }

        public void setTestAnalyteId(Integer testAnalyteId) {
            this.testAnalyteId = testAnalyteId;
        }

        public String getAnalyteName() {
            return analyteName;
        }

        public void setAnalyteName(String analyteName) {
            this.analyteName = DataBaseUtil.trim(analyteName);
        }

        public Integer getRow() {
            return row;
        }

        public void setRow(Integer row) {
            this.row = row;
        }

        public Integer getColumn() {
            return column;
        }

        public void setColumn(Integer column) {
            this.column = column;
        }
    }
}