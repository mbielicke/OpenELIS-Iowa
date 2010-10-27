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

import org.openelis.cache.DictionaryCache;
import org.openelis.domain.TestIdDupsVO;
import org.openelis.domain.TestReflexViewDO;
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

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Window;

public class TestReflexLookupScreen extends Screen implements
                                                  HasActionHandlers<TestReflexLookupScreen.Action> {
    public enum Action {
        SELECTED_REFLEX_ROW, CANCEL
    };

    protected TableWidget               reflexTestTable;
    private ArrayList<TestReflexViewDO> reflexTests;
    private ArrayList<TestIdDupsVO>     returnList;
    
    private Integer autoAddId, autoAddNonDupId, promptId, promptNonDupId;

    public TestReflexLookupScreen() throws Exception {
        super((ScreenDefInt)GWT.create(TestReflexLookupDef.class));

        // Setup link between Screen and widget Handlers
        initialize();
        
        initializeDropdowns();

        // Initialize Screen
        setState(State.DEFAULT);
    }

    private void initialize() {
        reflexTestTable = (TableWidget)def.getWidget("reflexTestTable");
        addScreenHandler(reflexTestTable, new ScreenEventHandler<ArrayList<TableDataRow>>() {
            public void onDataChange(DataChangeEvent event) {
                reflexTestTable.load(getTableModel());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                reflexTestTable.enable(true);
            }
        });

        reflexTestTable.addBeforeCellEditedHandler(new BeforeCellEditedHandler() {
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
            getReflexList();
            window.close();

            if (returnList != null && returnList.size() > 0)
                ActionEvent.fire(this, Action.SELECTED_REFLEX_ROW, returnList);
        }
    }

    private void cancel() {
        if (validate()) {
            window.close();
            ActionEvent.fire(this, Action.CANCEL, null);
        }
    }

    private ArrayList<TableDataRow> getTableModel() {
        ArrayList<TableDataRow> model;
        TestReflexViewDO reflexDO;
        TableDataRow row;
        
        model  = new ArrayList<TableDataRow>();
        
        if (reflexTests == null)
            return model;

        try {
            for (int iter = 0; iter < reflexTests.size(); iter++ ) {
                reflexDO = reflexTests.get(iter);
                row = new TableDataRow(1);
                
                if(promptId.equals(reflexDO.getFlagsId()) || promptNonDupId.equals(reflexDO.getFlagsId())){
                    row.data = new TestIdDupsVO(reflexDO.getAddTestId(), promptNonDupId.equals(reflexDO.getFlagsId()));
                    row.cells.get(0).value = reflexDO.getAddTestName() + ", " + reflexDO.getAddMethodName();
                    model.add(row);
                }
            }
        } catch (Exception e) {

            e.printStackTrace();
            return null;
        }

        return model;
    }
    
    private void initializeDropdowns(){
        try {
            autoAddId = DictionaryCache.getIdFromSystemName("reflex_auto");
            autoAddNonDupId = DictionaryCache.getIdFromSystemName("reflex_auto_ndup");
            promptId = DictionaryCache.getIdFromSystemName("reflex_prompt");
            promptNonDupId = DictionaryCache.getIdFromSystemName("reflex_prompt_ndup");
            
            
        } catch (Exception e) {
            Window.alert("ReflexTestUtility constructor: " + e.getMessage());
        }
    }
    
    public ArrayList<TestIdDupsVO> getReflexList(){
        TestReflexViewDO reflexDO;
        ArrayList<TableDataRow> selectedRows;
        TableDataRow row;
        int i,j;
        
        if(returnList == null){
            returnList = new ArrayList<TestIdDupsVO>();
            selectedRows = reflexTestTable.getSelections();
                
            //add non prompt reflex tests to the return list
            for(i=0; i<reflexTests.size(); i++){
                reflexDO = reflexTests.get(i);
                
                if(autoAddId.equals(reflexDO.getFlagsId()))
                    returnList.add(new TestIdDupsVO(reflexDO.getAddTestId(), false));
                else if (autoAddNonDupId.equals(reflexDO.getFlagsId()))
                    returnList.add(new TestIdDupsVO(reflexDO.getAddTestId(), true));
            }
            
            //add prompt reflex tests that are selected to the return list
            for(j=0; j<selectedRows.size(); j++){
                row = selectedRows.get(j);
                returnList.add((TestIdDupsVO)row.data);
            }
        }
        
        return returnList;
    }
    
    public boolean needToDrawPopup(){
        TestReflexViewDO reflexDO;
        for(int i=0; i<reflexTests.size(); i++){
            reflexDO = reflexTests.get(i);
            
            if(promptId.equals(reflexDO.getFlagsId()) || promptNonDupId.equals(reflexDO.getFlagsId()))
                return true;
        }
        return false;
    }

    public void setData(ArrayList<TestReflexViewDO> reflexTests) {
        this.reflexTests = reflexTests;
        returnList = null;

        DataChangeEvent.fire(this);
    }

    public HandlerRegistration addActionHandler(ActionHandler<Action> handler) {
        return addHandler(handler, ActionEvent.getType());
    }
}