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
import java.util.HashMap;

import org.openelis.cache.DictionaryCache;
import org.openelis.domain.TestPrepViewDO;
import org.openelis.domain.TestSectionViewDO;
import org.openelis.gwt.event.ActionEvent;
import org.openelis.gwt.event.ActionHandler;
import org.openelis.gwt.event.DataChangeEvent;
import org.openelis.gwt.event.HasActionHandlers;
import org.openelis.gwt.event.StateChangeEvent;
import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.screen.ScreenDefInt;
import org.openelis.gwt.screen.ScreenEventHandler;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.Dropdown;
import org.openelis.gwt.widget.table.TableDataRow;
import org.openelis.gwt.widget.table.TableWidget;
import org.openelis.gwt.widget.table.event.BeforeCellEditedEvent;
import org.openelis.gwt.widget.table.event.BeforeCellEditedHandler;
import org.openelis.manager.TestManager;
import org.openelis.manager.TestPrepManager;
import org.openelis.manager.TestSectionManager;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Window;

public class TestPrepLookupScreen extends Screen implements
                                                HasActionHandlers<TestPrepLookupScreen.Action> {
    public enum Action {
        SELECTED_PREP_ROW, CANCEL
    };

    protected TableWidget     prepTestTable;
    private   Integer         testSectionDefaultId;
    private   TestPrepManager manager;

    public TestPrepLookupScreen() throws Exception {
        super((ScreenDefInt)GWT.create(TestPrepLookupDef.class));

        // Setup link between Screen and widget Handlers
        initialize();

        // Initialize Screen
        setState(State.DEFAULT);
        
        initializeDropdowns();
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
            @SuppressWarnings("unchecked")
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {
                int                     i, j;
                ArrayList<TableDataRow> model;
                TableDataRow            row;
                TestSectionManager      tsMan;
                TestSectionViewDO       tsVDO;
                //
                //  
                //
                if (event.getCol() == 1) {
                    model = ((Dropdown<Integer>)prepTestTable.getColumns().get(1).getColumnWidget()).getData();
                    for (i = 0; i < model.size(); i++) {
                        row = model.get(i);
                        tsMan = (TestSectionManager) row.data;
                        for (j = 0; j < tsMan.count(); j++) {
                            tsVDO = tsMan.getSectionAt(j);
                            if (tsVDO.getSectionId().equals(row.key)) {
                                row.enabled = true;
                                break;
                            }
                        }
                        if (j == tsMan.count())
                            row.enabled = false;
                    }
                } else {
                    event.cancel();
                }
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

    private void initializeDropdowns() {
        try {
            testSectionDefaultId = DictionaryCache.getIdFromSystemName("test_section_default");
        } catch (Exception e) {
            Window.alert("inializeDropdowns: " + e.getMessage());
            window.close();
        }
    }

    private void ok() {
        TableDataRow selectedRow;

        if (validate()) {
            selectedRow = prepTestTable.getSelection();
            if (selectedRow.cells.get(1).value == null) {
                Window.alert("Selected Prep Test must have a Section assigned");
            } else {
                window.close();
                ActionEvent.fire(this, Action.SELECTED_PREP_ROW, selectedRow);
            }
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

    @SuppressWarnings("unchecked")
    private ArrayList<TableDataRow> getTableModel() {
        int                     i, j;
        Integer                 defaultId;
        ArrayList<TableDataRow> model, sModel;
        HashMap<Integer,TestSectionViewDO> sections;
        TableDataRow            row, sRow;
        TestManager             tMan;
        TestPrepViewDO          prepRow;
        TestSectionManager      tsMan;
        TestSectionViewDO       tsVDO;

        model = new ArrayList<TableDataRow>();
        if (manager == null)
            return model;

        sections = new HashMap<Integer,TestSectionViewDO>();
        try {
            sModel = new ArrayList<TableDataRow>();
            for (i = 0; i < manager.count(); i++) {
                prepRow = (TestPrepViewDO)manager.getPrepAt(i);
                defaultId = null;

                tMan = TestManager.fetchById(prepRow.getPrepTestId());
                tsMan = tMan.getTestSections();
                for (j = 0; j < tsMan.count(); j++) {
                    tsVDO = tsMan.getSectionAt(j);
                    if (testSectionDefaultId.equals(tsVDO.getFlagId()))
                        defaultId = tsVDO.getSectionId();
                    if (!sections.containsKey(tsVDO.getSectionId())) {
                        sRow = new TableDataRow(1);
                        sRow.key = tsVDO.getSectionId();
                        sRow.cells.get(0).value = tsVDO.getSection();
                        sRow.data = tsVDO;
                        sModel.add(sRow);
                        sections.put(tsVDO.getSectionId(), tsVDO);
                    }
                }

                row = new TableDataRow(2);
                row.key = prepRow.getPrepTestId();
                row.cells.get(0).value = prepRow.getPrepTestName() + ", " + prepRow.getMethodName();
                if (defaultId != null)
                    row.cells.get(1).value = defaultId;
                row.data = tsMan;

                model.add(row);
            }
            ((Dropdown<Integer>)prepTestTable.getColumns().get(1).getColumnWidget()).setModel(sModel);
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
