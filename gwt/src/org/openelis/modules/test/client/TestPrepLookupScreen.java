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
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.table.TableDataRow;
import org.openelis.gwt.widget.table.TableWidget;
import org.openelis.gwt.widget.table.event.BeforeCellEditedEvent;
import org.openelis.gwt.widget.table.event.BeforeCellEditedHandler;
import org.openelis.gwt.widget.tree.TreeDataItem;
import org.openelis.manager.TestPrepManager;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.shared.HandlerRegistration;

public class TestPrepLookupScreen extends Screen implements
                                                HasActionHandlers<TestPrepLookupScreen.Action> {
    public enum Action {
        SELECTED_PREP_ROW
    };

    protected TableWidget   prepTestTable;
    private TreeDataItem    selectedParentRow;

    private TestPrepManager manager;

    public TestPrepLookupScreen() throws Exception {
        super((ScreenDefInt)GWT.create(TestPrepLookupDef.class));

        // Setup link between Screen and widget Handlers
        initialize();

        // Initialize Screen
        setState(State.DEFAULT);
    }

    private void initialize() {
        prepTestTable = (TableWidget)def.getWidget("prepTestTable");
        addScreenHandler(prepTestTable, new ScreenEventHandler<ArrayList<TableDataRow>>() {
            public void onDataChange(DataChangeEvent event) {
                prepTestTable.load(getTableModel());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                prepTestTable.enable(true);
            }
        });

        prepTestTable.addBeforeCellEditedHandler(new BeforeCellEditedHandler() {
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {
                event.cancel();
            }
        });

        final AppButton okButton = (AppButton)def.getWidget("ok");
        addScreenHandler(okButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                ok();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                okButton.enable(true);
            }
        });

        final AppButton cancelButton = (AppButton)def.getWidget("cancel");
        addScreenHandler(cancelButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                cancel();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                cancelButton.enable(true);
            }
        });

    }

    private void ok() {
        if (validate()) {
            TableDataRow selectedRow = prepTestTable.getSelection();

            window.close();

            if (selectedRow != null)
                ActionEvent.fire(this, Action.SELECTED_PREP_ROW, new TestPrepLookupBundle(selectedRow, selectedParentRow));
        }
    }

    private void cancel() {
        if (validate())
            window.close();
    }

    public boolean validate() {
        return super.validate();
    }

    private ArrayList<TableDataRow> getTableModel() {
        ArrayList<TableDataRow> model = new ArrayList<TableDataRow>();

        if (manager == null)
            return model;

        try {
            for (int iter = 0; iter < manager.count(); iter++ ) {
                TestPrepViewDO prepRow = (TestPrepViewDO)manager.getPrepAt(iter);

                TableDataRow row = new TableDataRow(1);
                row.key = prepRow.getPrepTestId();

                row.cells.get(0).value = prepRow.getPrepTestName() + ", " + prepRow.getMethodName();

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
    
    public void setSelectedTreeRow(TreeDataItem selectedTreeRow) {
        selectedParentRow = selectedTreeRow;
    }

    public HandlerRegistration addActionHandler(ActionHandler<Action> handler) {
        return addHandler(handler, ActionEvent.getType());
    }
    
    public class TestPrepLookupBundle {
        public TableDataRow selectedPrepRow;
        public TreeDataItem selectedTreeRow;
        
        public TestPrepLookupBundle(){}
        
        public TestPrepLookupBundle(TableDataRow selectedPrepRow, TreeDataItem selectedTreeRow){
            this.selectedPrepRow = selectedPrepRow;
            this.selectedTreeRow = selectedTreeRow;
        }
    }
}
