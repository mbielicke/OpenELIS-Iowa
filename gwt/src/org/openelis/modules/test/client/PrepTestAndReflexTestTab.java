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
package org.openelis.modules.test.client;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import org.openelis.cache.CategoryCache;
import org.openelis.constants.Messages;
import org.openelis.domain.Constants;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.TestAnalyteViewDO;
import org.openelis.domain.TestMethodVO;
import org.openelis.domain.TestPrepViewDO;
import org.openelis.domain.TestReflexViewDO;
import org.openelis.domain.TestResultViewDO;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.gwt.event.ActionEvent;
import org.openelis.gwt.event.ActionHandler;
import org.openelis.gwt.event.DataChangeEvent;
import org.openelis.gwt.event.GetMatchesEvent;
import org.openelis.gwt.event.GetMatchesHandler;
import org.openelis.gwt.event.StateChangeEvent;
import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.screen.ScreenDefInt;
import org.openelis.gwt.screen.ScreenEventHandler;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.AutoComplete;
import org.openelis.gwt.widget.Dropdown;
import org.openelis.gwt.widget.Label;
import org.openelis.gwt.widget.QueryFieldUtil;
import org.openelis.gwt.widget.ScreenWindowInt;
import org.openelis.gwt.widget.table.TableDataRow;
import org.openelis.gwt.widget.table.TableRow;
import org.openelis.gwt.widget.table.TableWidget;
import org.openelis.gwt.widget.table.event.BeforeCellEditedEvent;
import org.openelis.gwt.widget.table.event.BeforeCellEditedHandler;
import org.openelis.gwt.widget.table.event.CellEditedEvent;
import org.openelis.gwt.widget.table.event.CellEditedHandler;
import org.openelis.gwt.widget.table.event.RowAddedEvent;
import org.openelis.gwt.widget.table.event.RowAddedHandler;
import org.openelis.gwt.widget.table.event.RowDeletedEvent;
import org.openelis.gwt.widget.table.event.RowDeletedHandler;
import org.openelis.manager.TestAnalyteManager;
import org.openelis.manager.TestManager;
import org.openelis.manager.TestPrepManager;
import org.openelis.manager.TestReflexManager;
import org.openelis.manager.TestResultManager;
import org.openelis.meta.TestMeta;
import org.openelis.modules.test.client.AnalyteAndResultTab.Action;
import org.openelis.ui.widget.WindowInt;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.Window;

public class PrepTestAndReflexTestTab extends Screen
                                                    implements
                                                    ActionHandler<AnalyteAndResultTab.Action> {

    private TestManager        manager;
    private TestAnalyteManager testAnalyteManager;
    private TestResultManager  testResultManager;

    private boolean            loaded;

    private TableWidget        testPrepTable, testReflexTable;
    private AppButton          addPrepTestButton, removePrepTestButton,
                    addReflexTestButton, removeReflexTestButton;
    private AutoComplete<Integer> prepTestAuto, reflexTestAuto, analyteAuto, resultAuto;
    private Label<String>         prepMethodName, reflexMethodName;

    public PrepTestAndReflexTestTab(ScreenDefInt def, WindowInt window) {
        setDefinition(def);
        setWindow(window);

        initialize();
        initializeDropdowns();
    }

    private void initialize() {

        testPrepTable = (TableWidget)def.getWidget("testPrepTable");
        addScreenHandler(testPrepTable,
                         new ScreenEventHandler<ArrayList<TableDataRow>>() {
                             public void onDataChange(DataChangeEvent event) {
                                 if (state != State.QUERY)
                                     testPrepTable.load(getPrepTestModel());
                             }

                             public void onStateChange(StateChangeEvent<State> event) {
                                 testPrepTable.enable(true);
                                 testPrepTable.setQueryMode(event.getState() == State.QUERY);
                             }
                         });

        testPrepTable.addBeforeCellEditedHandler(new BeforeCellEditedHandler() {
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {
                if (state != State.ADD && state != State.UPDATE && state != State.QUERY)
                    event.cancel();
            }
        });

        testPrepTable.addCellEditedHandler(new CellEditedHandler() {
            public void onCellUpdated(CellEditedEvent event) {
                int r, c;
                Object val;
                TableDataRow row;
                TestPrepViewDO data;

                r = event.getRow();
                c = event.getCol();

                val = testPrepTable.getObject(r, c);

                try {
                    data = manager.getPrepTests().getPrepAt(r);
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                    return;
                }

                switch (c) {
                    case 0:
                        row = (TableDataRow)val;
                        data.setPrepTestId((Integer) (row.key));
                        data.setPrepTestName(prepTestAuto.getTextBoxDisplay());
                        data.setMethodName(prepMethodName.getText());
                        break;
                    case 2:
                        data.setIsOptional((String)val);
                        break;
                }

            }
        });

        testPrepTable.addRowAddedHandler(new RowAddedHandler() {
            public void onRowAdded(RowAddedEvent event) {
                int r;
                TestPrepViewDO data;

                r = event.getIndex();
                try {
                    data = new TestPrepViewDO();
                    data.setIsOptional("N");
                    manager.getPrepTests().addPrep(data);
                    testPrepTable.setCell(r, 2, "N");
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                }
            }
        });

        testPrepTable.addRowDeletedHandler(new RowDeletedHandler() {
            public void onRowDeleted(RowDeletedEvent event) {
                try {
                    manager.getPrepTests().removePrepAt(event.getIndex());
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                }
            }
        });

        prepTestAuto = (AutoComplete<Integer>)testPrepTable.getColumnWidget(TestMeta.getPrepPrepTestName());
        prepTestAuto.addGetMatchesHandler(new GetMatchesHandler() {
            public void onGetMatches(GetMatchesEvent event) {
                TestMethodVO data;
                ArrayList<TestMethodVO> list;
                ArrayList<TableDataRow> model;

                window.setBusy();
                try {
                    list = TestService.get().fetchActiveByName(QueryFieldUtil.parseAutocomplete(event.getMatch()));
                    model = new ArrayList<TableDataRow>();
                    for (int i = 0; i < list.size(); i++ ) {
                        data = list.get(i);
                        model.add(new TableDataRow(data.getTestId(),
                                                   data.getTestName(),
                                                   data.getMethodName(),
                                                   data.getTestDescription()));
                    }
                    ((AutoComplete)event.getSource()).showAutoMatches(model);
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                }
                window.clearStatus();

            }
        });

        prepMethodName = (Label<String>)testPrepTable.getColumnWidget("method");

        prepTestAuto.addSelectionHandler(new SelectionHandler<TableRow>() {
            public void onSelection(SelectionEvent<TableRow> event) {
                TableDataRow selectedRow;
                String value;
                int r;

                selectedRow = event.getSelectedItem().row;
                r = testPrepTable.getSelectedRow();

                // set the method
                if (selectedRow != null && selectedRow.key != null) {
                    value = (String)selectedRow.cells.get(1).getValue();
                    testPrepTable.setCell(r, 1, value);
                } else {
                    testPrepTable.setCell(r, 1, null);
                }
            }

        });

        addPrepTestButton = (AppButton)def.getWidget("addPrepTestButton");
        addScreenHandler(addPrepTestButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                int r;

                r = testPrepTable.numRows();
                testPrepTable.addRow();
                testPrepTable.selectRow(r);
                testPrepTable.scrollToSelection();
                testPrepTable.startEditing(r, 0);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                addPrepTestButton.enable(EnumSet.of(State.ADD, State.UPDATE)
                                                .contains(event.getState()));
            }
        });

        removePrepTestButton = (AppButton)def.getWidget("removePrepTestButton");
        addScreenHandler(removePrepTestButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                int i;

                i = testPrepTable.getSelectedRow();
                if (i > -1 && testPrepTable.numRows() > 0)
                    testPrepTable.deleteRow(i);

            }

            public void onStateChange(StateChangeEvent<State> event) {
                removePrepTestButton.enable(EnumSet.of(State.ADD, State.UPDATE)
                                                   .contains(event.getState()));
            }
        });

        testReflexTable = (TableWidget)def.getWidget("testReflexTable");
        addScreenHandler(testReflexTable,
                         new ScreenEventHandler<ArrayList<TableDataRow>>() {
                             public void onDataChange(DataChangeEvent event) {
                                 if (state != State.QUERY)
                                     testReflexTable.load(getReflexTestModel());
                             }

                             public void onStateChange(StateChangeEvent<State> event) {
                                 testReflexTable.enable(true);
                                 testReflexTable.setQueryMode(event.getState() == State.QUERY);
                             }
                         });

        testReflexTable.addBeforeCellEditedHandler(new BeforeCellEditedHandler() {
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {
                int r, c;
                TableDataRow val;

                if (state != State.ADD && state != State.UPDATE && state != State.QUERY)
                    event.cancel();

                r = event.getRow();
                c = event.getCol();

                if (c == 3) {
                    val = (TableDataRow)testReflexTable.getObject(r, 2);
                    if (val == null || val.key == null) {
                        Window.alert(Messages.get().selectAnaBeforeRes());
                        event.cancel();
                    }
                }

            }

        });

        testReflexTable.addCellEditedHandler(new CellEditedHandler() {
            public void onCellUpdated(CellEditedEvent event) {
                int r, c;
                Object val;
                TestReflexViewDO data;
                TableDataRow row;

                r = event.getRow();
                c = event.getCol();
                val = testReflexTable.getObject(r, c);
                try {
                    data = manager.getReflexTests().getReflexAt(r);
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                    return;
                }

                switch (c) {
                    case 0:
                        if (val != null) {
                            row = (TableDataRow)val;
                            data.setAddTestId((Integer) (row.key));
                            data.setAddTestName(reflexTestAuto.getTextBoxDisplay());
                            data.setAddMethodName(reflexMethodName.getText());
                        } else {
                            data.setAddTestId(null);
                            data.setAddTestName(null);
                            data.setAddMethodName(null);
                        }
                        break;
                    case 2:
                        if (val != null) {
                            row = (TableDataRow)val;
                            data.setTestAnalyteId((Integer) (row.key));
                            data.setTestAnalyteName(analyteAuto.getTextBoxDisplay());
                        } else {
                            data.setTestAnalyteId(null);
                            data.setTestAnalyteName(null);
                        }
                        break;
                    case 3:
                        if (val != null) {
                            row = (TableDataRow)val;
                            data.setTestResultId((Integer) (row.key));
                            data.setTestResultValue(resultAuto.getTextBoxDisplay());
                        } else {
                            data.setTestResultId(null);
                            data.setTestResultValue(null);
                        }
                        break;
                    case 4:
                        if (val != null)
                            data.setFlagsId((Integer)val);
                        else
                            data.setFlagsId(null);
                        break;
                }
            }
        });

        testReflexTable.addRowAddedHandler(new RowAddedHandler() {
            public void onRowAdded(RowAddedEvent event) {
                try {
                    manager.getReflexTests().addReflex(new TestReflexViewDO());
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                }
            }
        });

        testReflexTable.addRowDeletedHandler(new RowDeletedHandler() {
            public void onRowDeleted(RowDeletedEvent event) {
                try {
                    manager.getReflexTests().removeReflexAt(event.getIndex());
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                }
            }
        });

        reflexTestAuto = (AutoComplete<Integer>)testReflexTable.getColumnWidget(TestMeta.getReflexAddTestName());
        reflexTestAuto.addGetMatchesHandler(new GetMatchesHandler() {
            public void onGetMatches(GetMatchesEvent event) {
                TestMethodVO data;
                ArrayList<TestMethodVO> list;
                ArrayList<TableDataRow> model;

                window.setBusy();
                try {
                    list = TestService.get().fetchActiveByName(QueryFieldUtil.parseAutocomplete(event.getMatch()));
                    model = new ArrayList<TableDataRow>();
                    for (int i = 0; i < list.size(); i++ ) {
                        data = list.get(i);
                        model.add(new TableDataRow(data.getTestId(),
                                                   data.getTestName(),
                                                   data.getMethodName(),
                                                   data.getTestDescription()));
                    }
                    ((AutoComplete)event.getSource()).showAutoMatches(model);
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                }
                window.clearStatus();

            }
        });

        reflexMethodName = (Label<String>)testReflexTable.getColumnWidget("method");

        reflexTestAuto.addSelectionHandler(new SelectionHandler<TableRow>() {
            public void onSelection(SelectionEvent<TableRow> event) {
                TableDataRow selectedRow;
                String value;
                int index;

                selectedRow = event.getSelectedItem().row;
                index = testReflexTable.getSelectedRow();

                // set the method
                if (selectedRow != null && selectedRow.key != null) {
                    value = (String)selectedRow.cells.get(1).getValue();
                    testReflexTable.setCell(index, 1, value);
                } else {
                    testReflexTable.setCell(index, 1, null);
                }

            }

        });

        analyteAuto = (AutoComplete<Integer>)testReflexTable.getColumnWidget(TestMeta.getReflexTestAnalyteName());
        analyteAuto.addGetMatchesHandler(new GetMatchesHandler() {
            public void onGetMatches(GetMatchesEvent event) {
                TestAnalyteViewDO data;
                ArrayList<TableDataRow> model;
                TableDataRow row;
                String name;

                model = new ArrayList<TableDataRow>();
                for (int i = 0; i < testAnalyteManager.rowCount(); i++ ) {
                    data = testAnalyteManager.getAnalyteAt(i, 0);
                    name = data.getAnalyteName();
                    if (name != null && name.startsWith(event.getMatch())) {
                        row = new TableDataRow(data.getId(), name);
                        model.add(row);
                    }
                }

                if (model.size() == 0)
                    model.add(new TableDataRow(null, ""));

                analyteAuto.showAutoMatches(model);
            }

        });

        resultAuto = (AutoComplete<Integer>)testReflexTable.getColumnWidget(TestMeta.getReflexTestResultValue());
        resultAuto.addGetMatchesHandler(new GetMatchesHandler() {
            public void onGetMatches(GetMatchesEvent event) {
                TestResultViewDO data;
                ArrayList<TableDataRow> model;
                TableDataRow trow, arow;
                Integer rg;
                int r, size;
                String value;

                r = testReflexTable.getSelectedRow();
                trow = testReflexTable.getRow(r);
                arow = (TableDataRow)trow.cells.get(2).getValue();

                model = new ArrayList<TableDataRow>();

                if (arow.key == null) {
                    model.add(new TableDataRow(null, ""));
                    resultAuto.showAutoMatches(model);
                    return;
                }

                rg = getResultGroupForTestAnalyte((Integer)arow.key);

                if (rg == null) {
                    model.add(new TableDataRow(null, ""));
                    resultAuto.showAutoMatches(model);
                    return;
                }

                size = testResultManager.getResultGroupSize(rg);

                for (int i = 0; i < size; i++ ) {
                    data = testResultManager.getResultAt(rg, i);
                    if (DataBaseUtil.isSame(Constants.dictionary().TEST_RES_TYPE_DICTIONARY,
                                            data.getTypeId()))
                        value = data.getDictionary();
                    else
                        value = data.getValue();

                    if ( !Constants.dictionary().TEST_RES_TYPE_DEFAULT.equals(data.getTypeId()) &&
                        value != null)
                        model.add(new TableDataRow(data.getId(), value));
                }

                if (model.size() == 0)
                    model.add(new TableDataRow(null, ""));

                resultAuto.showAutoMatches(model);
            }

        });

        addReflexTestButton = (AppButton)def.getWidget("addReflexTestButton");
        addScreenHandler(addReflexTestButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                int r;

                r = testReflexTable.numRows();
                testReflexTable.addRow();
                testReflexTable.selectRow(r);
                testReflexTable.scrollToSelection();
                testReflexTable.startEditing(r, 0);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                addReflexTestButton.enable(EnumSet.of(State.ADD, State.UPDATE)
                                                  .contains(event.getState()));
            }
        });

        removeReflexTestButton = (AppButton)def.getWidget("removeReflexTestButton");
        addScreenHandler(removeReflexTestButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                int r;

                r = testReflexTable.getSelectedRow();
                if (r > -1 && testReflexTable.numRows() > 0)
                    testReflexTable.deleteRow(r);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                removeReflexTestButton.enable(EnumSet.of(State.ADD, State.UPDATE)
                                                     .contains(event.getState()));
            }
        });

    }

    public void setManager(TestManager manager) {
        this.manager = manager;
        loaded = false;
    }

    public void draw() {
        if ( !loaded) {
            try {
                if (state == State.UPDATE || state == State.ADD) {
                    testAnalyteManager = manager.getTestAnalytes();
                    testResultManager = manager.getTestResults();
                }
                DataChangeEvent.fire(this);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        loaded = true;
    }

    public void onAction(ActionEvent<AnalyteAndResultTab.Action> event) {
        TestAnalyteViewDO ana;
        TestResultViewDO res;

        if (state == State.QUERY)
            return;

        if (event.getAction() == Action.ANALYTE_CHANGED) {
            ana = (TestAnalyteViewDO)event.getData();
            setAnalyteErrors(ana.getId(),
                             ana.getAnalyteName(),
                             "analyteNameChanged",
                             true);
        } else if (event.getAction() == Action.ANALYTE_DELETED) {
            ana = (TestAnalyteViewDO)event.getData();
            setAnalyteErrors(ana.getId(), ana.getAnalyteName(), "analyteDeleted", false);
        }
        if (event.getAction() == Action.RESULT_CHANGED) {
            res = (TestResultViewDO)event.getData();
            setResultErrors(res, "resultValueChanged", true);
        } else if (event.getAction() == Action.RESULT_DELETED) {
            res = (TestResultViewDO)event.getData();
            setResultErrors(res, "resultDeleted", false);
        }

    }

    protected void clearKeys(TestPrepManager tpm, TestReflexManager tfm) {
        TestPrepViewDO prep;
        TestReflexViewDO ref;
        int i;

        for (i = 0; i < tpm.count(); i++ ) {
            prep = tpm.getPrepAt(i);
            prep.setId(null);
            prep.setTestId(null);
        }

        for (i = 0; i < tfm.count(); i++ ) {
            ref = tfm.getReflexAt(i);
            ref.setId(null);
            ref.setTestAnalyteId(ref.getTestAnalyteId() * ( -1));
            ref.setTestResultId(ref.getTestResultId() * ( -1));
            ref.setTestId(null);
        }
    }

    protected void finishEditing() {
        testReflexTable.finishEditing();
    }

    private ArrayList<TableDataRow> getPrepTestModel() {
        ArrayList<TableDataRow> model;
        TestPrepManager tpm;
        TestPrepViewDO data;
        TableDataRow row;

        model = new ArrayList<TableDataRow>();

        if (manager == null)
            return model;

        try {
            tpm = manager.getPrepTests();
            for (int i = 0; i < tpm.count(); i++ ) {
                data = tpm.getPrepAt(i);
                row = new TableDataRow(3);
                row.key = data.getId();

                row.cells.get(0).setValue(new TableDataRow(data.getPrepTestId(),
                                                           data.getPrepTestName()));
                row.cells.get(1).setValue(data.getMethodName());
                row.cells.get(2).setValue(data.getIsOptional());
                model.add(row);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return model;
    }

    private ArrayList<TableDataRow> getReflexTestModel() {
        ArrayList<TableDataRow> model;
        TestReflexManager trm;
        TestReflexViewDO data;
        TableDataRow row;

        model = new ArrayList<TableDataRow>();

        if (manager == null)
            return model;

        try {
            trm = manager.getReflexTests();
            for (int i = 0; i < trm.count(); i++ ) {
                data = trm.getReflexAt(i);
                row = new TableDataRow(5);
                row.key = data.getId();

                row.cells.get(0).setValue(new TableDataRow(data.getAddTestId(),
                                                           data.getAddTestName()));
                row.cells.get(1).setValue(data.getAddMethodName());
                row.cells.get(2).setValue(new TableDataRow(data.getTestAnalyteId(),
                                                           data.getTestAnalyteName()));
                row.cells.get(3).setValue(new TableDataRow(data.getTestResultId(),
                                                           data.getTestResultValue()));
                row.cells.get(4).setValue(data.getFlagsId());

                model.add(row);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return model;
    }

    private void initializeDropdowns() {
        ArrayList<TableDataRow> model;
        List<DictionaryDO> list;
        TableDataRow row;

        list = CategoryCache.getBySystemName("test_reflex_flags");
        model = new ArrayList<TableDataRow>();
        model.add(new TableDataRow(null, ""));
        for (DictionaryDO data : list) {
            row = new TableDataRow(data.getId(), data.getEntry());
            row.enabled = ("Y".equals(data.getIsActive()));
            model.add(row);
        }
        ((Dropdown)testReflexTable.getColumnWidget(TestMeta.getReflexFlagsId())).setModel(model);
    }

    private Integer getResultGroupForTestAnalyte(Integer taId) {
        TestAnalyteViewDO data;
        for (int i = 0; i < testAnalyteManager.rowCount(); i++ ) {
            data = testAnalyteManager.getAnalyteAt(i, 0);
            if (taId.equals(data.getId()))
                return data.getResultGroup();
        }

        return null;
    }

    private void setAnalyteErrors(Integer id, String name, String key, boolean matchLabel) {
        TableDataRow row;
        String val;

        for (int i = 0; i < testReflexTable.numRows(); i++ ) {
            row = (TableDataRow)testReflexTable.getObject(i, 2);
            val = (String)row.cells.get(0).getValue();

            if (id.equals(row.key)) {
                if ( (matchLabel && ! (val.equals(name))) || !matchLabel) {
                    row = new TableDataRow(null, "");
                    testReflexTable.setCell(i, 2, row);
                    testReflexTable.setCellException(i, 2, new Exception(key));
                }
            }
        }
    }

    private void setResultErrors(TestResultViewDO data, String key, boolean matchLabel) {
        TableDataRow row;
        String val, value;
        Integer id;

        id = data.getId();
        for (int i = 0; i < testReflexTable.numRows(); i++ ) {
            row = (TableDataRow)testReflexTable.getObject(i, 3);
            val = (String)row.cells.get(0).getValue();
            if (DataBaseUtil.isSame(Constants.dictionary().TEST_RES_TYPE_DICTIONARY, data.getTypeId()))
                value = data.getDictionary();
            else
                value = data.getValue();
            if (id.equals(row.key)) {
                if ( (matchLabel && ! (val.equals(value))) || !matchLabel) {
                    row = new TableDataRow(null, "");
                    testReflexTable.setCell(i, 3, row);
                    testReflexTable.setCellException(i, 3, new Exception(key));
                }
            }
        }
    }

}
