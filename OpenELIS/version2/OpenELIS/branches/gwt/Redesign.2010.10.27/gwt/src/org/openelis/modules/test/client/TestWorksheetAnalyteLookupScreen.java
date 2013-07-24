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
import org.openelis.gwt.widget.Button;
import org.openelis.gwt.widget.table.Row;
import org.openelis.gwt.widget.table.Table;
import org.openelis.gwt.widget.table.event.CellEditedEvent;
import org.openelis.gwt.widget.table.event.CellEditedHandler;
import org.openelis.manager.TestAnalyteManager;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;

public class TestWorksheetAnalyteLookupScreen extends Screen
                                                            implements
                                                            HasActionHandlers<TestWorksheetAnalyteLookupScreen.Action> {

    protected Table                      testAnalyteTable;
    protected Button                     okButton, cancelButton;

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

        testAnalyteTable = (Table)def.getWidget("testAnalyteTable");
        addScreenHandler(testAnalyteTable, new ScreenEventHandler<ArrayList<Row>>() {
            public void onDataChange(DataChangeEvent event) {
                testAnalyteTable.setModel(getTestAnalyteTable());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                testAnalyteTable.setEnabled(true);
            }
        });

        testAnalyteTable.addCellEditedHandler(new CellEditedHandler() {
            public void onCellUpdated(CellEditedEvent event) {
                int r, c;
                Row row;
                String val;
                TestAnalyteViewDO data;
                Integer group;

                r = event.getRow();
                c = event.getCol();

                if (c != 1)
                    return;

                row = testAnalyteTable.getRowAt(r);
                val = (String)row.getCell(c);
                group = manager.getAnalyteAt(r, 0).getRowGroup();

                for (int i = 0; i < manager.rowCount(); i++ ) {
                    data = manager.getAnalyteAt(i, 0);
                    if (group.equals(data.getRowGroup()))
                        testAnalyteTable.setValueAt(i, c, val);
                }

                if ("Y".equals(val))
                    window.setDone(consts.get("additionalAnalytesAdded"));

            }

        });

        okButton = (Button)def.getWidget("ok");
        addScreenHandler(okButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                ok();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                okButton.setEnabled(true);
            }
        });

        cancelButton = (Button)def.getWidget("cancel");
        addScreenHandler(cancelButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                cancel();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                cancelButton.setEnabled(true);
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

    private ArrayList<Row> getTestAnalyteTable() {
        ArrayList<Row> model;
        TestAnalyteViewDO data;
        Row row;
        String name;

        model = new ArrayList<Row>();

        if (manager == null)
            return model;

        for (int i = 0; i < manager.rowCount(); i++ ) {
            data = manager.getAnalyteAt(i, 0);
            row = new Row(2);
            name = data.getAnalyteName();
            if (name != null && !"".equals(name)) {
                //row.key = data.getId();
                row.setData(data);
                row.setCell(0,name);
                row.setCell(1,"N");

                model.add(row);
            }
        }

        return model;
    }

    private void fillSelectionList() {
        Row row;

        selections = new ArrayList<TestAnalyteViewDO>();

        for (int i = 0; i < testAnalyteTable.getRowCount(); i++ ) {
            row = testAnalyteTable.getRowAt(i);
            if ("Y".equals(row.getCell(1)))
                selections.add((TestAnalyteViewDO)row.getData());
        }

    }

}