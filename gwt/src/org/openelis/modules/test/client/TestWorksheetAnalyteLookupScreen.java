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

import org.openelis.domain.TestAnalyteViewDO;
import org.openelis.gwt.event.ActionEvent;
import org.openelis.gwt.event.ActionHandler;
import org.openelis.gwt.event.DataChangeEvent;
import org.openelis.gwt.event.HasActionHandlers;
import org.openelis.gwt.event.StateChangeEvent;
import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.screen.ScreenDefInt;
import org.openelis.gwt.screen.ScreenEventHandler;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.table.TableDataRow;
import org.openelis.gwt.widget.table.TableWidget;
import org.openelis.manager.TestAnalyteManager;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.shared.HandlerRegistration;

public class TestWorksheetAnalyteLookupScreen extends Screen
                                                            implements
                                                            HasActionHandlers<TestWorksheetAnalyteLookupScreen.Action> {

    protected TableWidget                testAnalyteTable;
    protected AppButton                  okButton, cancelButton;

    private ArrayList<TestAnalyteViewDO> selections;
    private TestAnalyteManager           manager;

    public enum Action {
        OK, CANCEL
    };

    public TestWorksheetAnalyteLookupScreen(TestAnalyteManager manager) throws Exception {
        super((ScreenDefInt)GWT.create(TestWorksheetAnalyteLookupDef.class));

        this.manager = manager;

        // Setup link between Screen and widget Handlers
        initialize();

        setState(State.DEFAULT);
        DataChangeEvent.fire(this);
    }

    private void initialize() {
        selections = null;

        testAnalyteTable = (TableWidget)def.getWidget("testAnalyteTable");
        addScreenHandler(testAnalyteTable, new ScreenEventHandler<ArrayList<TableDataRow>>() {
            public void onDataChange(DataChangeEvent event) {
                testAnalyteTable.load(getTestAnalyteTable());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                testAnalyteTable.enable(true);
            }
        });

        okButton = (AppButton)def.getWidget("ok");
        addScreenHandler(okButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                ok();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                okButton.enable(true);
            }
        });

        cancelButton = (AppButton)def.getWidget("cancel");
        addScreenHandler(cancelButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                cancel();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                cancelButton.enable(true);
            }
        });

    }

    public void ok() {
        fillSelectionList();
        ActionEvent.fire(this, Action.OK, selections);
        window.close();
    }

    public void cancel() {
        ActionEvent.fire(this, Action.CANCEL, null);
        window.close();
    }

    public void setScreenState(State state) {
        setState(state);
    }

    public HandlerRegistration addActionHandler(ActionHandler<TestWorksheetAnalyteLookupScreen.Action> handler) {
        return addHandler(handler, ActionEvent.getType());
    }

    public void refresh(TestAnalyteManager manager) {
        this.manager = manager;
        DataChangeEvent.fire(this);
        //testAnalyteTable.load(getTestAnalyteTable());
    }

    private ArrayList<TableDataRow> getTestAnalyteTable() {
        ArrayList<TableDataRow> model;
        TestAnalyteViewDO data;
        TableDataRow row;
        String name;

        model = new ArrayList<TableDataRow>();

        if (manager == null)
            return model;

        for (int i = 0; i < manager.rowCount(); i++ ) {
            data = manager.getAnalyteAt(i, 0);
            row = new TableDataRow(2);
            name = data.getAnalyteName();
            if (name != null && !"".equals(name)) {
                row.key = data.getId();
                row.data = data;
                row.cells.get(0).setValue(name);
                row.cells.get(1).setValue("N");

                model.add(row);
            }
        }

        return model;
    }

    private void fillSelectionList() {
        TableDataRow row;

        selections = new ArrayList<TestAnalyteViewDO>();

        for (int i = 0; i < testAnalyteTable.numRows(); i++ ) {
            row = testAnalyteTable.getRow(i);
            if ("Y".equals(row.cells.get(1).getValue()))
                selections.add((TestAnalyteViewDO)row.data);
        }

    }

}
