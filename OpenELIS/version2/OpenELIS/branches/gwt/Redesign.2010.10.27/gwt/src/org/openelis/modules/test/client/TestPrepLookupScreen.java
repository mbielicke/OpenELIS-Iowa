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

import org.openelis.domain.TestPrepViewDO;
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
import org.openelis.gwt.widget.table.event.BeforeCellEditedEvent;
import org.openelis.gwt.widget.table.event.BeforeCellEditedHandler;
import org.openelis.manager.TestPrepManager;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.shared.HandlerRegistration;

public class TestPrepLookupScreen extends Screen implements
                                                HasActionHandlers<TestPrepLookupScreen.Action> {
    public enum Action {
        SELECTED_PREP_ROW, CANCEL
    };

    protected Table   prepTestTable;
    private TestPrepManager manager;

    public TestPrepLookupScreen() throws Exception {
        super((ScreenDefInt)GWT.create(TestPrepLookupDef.class));

        // Setup link between Screen and widget Handlers
        initialize();

        // Initialize Screen
        setState(State.DEFAULT);
    }

    private void initialize() {
        prepTestTable = (Table)def.getWidget("prepTestTable");
        addScreenHandler(prepTestTable, new ScreenEventHandler<ArrayList<Row>>() {
            public void onDataChange(DataChangeEvent event) {
                prepTestTable.setModel(getTableModel());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                prepTestTable.setEnabled(true);
            }
        });

        prepTestTable.addBeforeCellEditedHandler(new BeforeCellEditedHandler() {
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {
                event.cancel();
            }
        });

        final Button okButton = (Button)def.getWidget("ok");
        addScreenHandler(okButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                ok();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                okButton.setEnabled(true);
            }
        });

        final Button cancelButton = (Button)def.getWidget("cancel");
        addScreenHandler(cancelButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                cancel();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                cancelButton.setEnabled(true);
            }
        });

    }

    private void ok() {
        if (validate()) {
            Row selectedRow = prepTestTable.getRowAt(prepTestTable.getSelectedRow());

            window.close();

            //if (selectedRow != null)
                ActionEvent.fire(this, Action.SELECTED_PREP_ROW, selectedRow);
        }
    }

    private void cancel() {
        if (validate()){
            window.close();
            ActionEvent.fire(this, Action.CANCEL, null);
        }
        
        
    }

    public boolean validate() {
        return super.validate();
    }

    private ArrayList<Row> getTableModel() {
        ArrayList<Row> model = new ArrayList<Row>();

        if (manager == null)
            return model;

        try {
            for (int iter = 0; iter < manager.count(); iter++ ) {
                TestPrepViewDO prepRow = (TestPrepViewDO)manager.getPrepAt(iter);

                Row row = new Row(1);
                //row.key = prepRow.getPrepTestId();

                row.setCell(0,prepRow.getPrepTestName() + ", " + prepRow.getMethodName());

                model.add(row);
            }
        } catch (Exception e) {

            e.printStackTrace();
            return null;
        }

        return model;
    }

    public void setManager(TestPrepManager man) {
        manager = man;

        DataChangeEvent.fire(this);
    }
    
    public HandlerRegistration addActionHandler(ActionHandler<Action> handler) {
        return addHandler(handler, ActionEvent.getType());
    }
}
