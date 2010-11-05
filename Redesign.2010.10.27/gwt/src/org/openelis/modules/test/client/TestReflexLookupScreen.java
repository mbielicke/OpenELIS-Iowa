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
import org.openelis.gwt.widget.Button;
import org.openelis.gwt.widget.table.Row;
import org.openelis.gwt.widget.table.Table;
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

    protected Table                     reflexTestTable;
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
        reflexTestTable = (Table)def.getWidget("reflexTestTable");
        addScreenHandler(reflexTestTable, new ScreenEventHandler<ArrayList<Row>>() {
            public void onDataChange(DataChangeEvent event) {
                reflexTestTable.setModel(getTableModel());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                reflexTestTable.setEnabled(true);
            }
        });

        reflexTestTable.addBeforeCellEditedHandler(new BeforeCellEditedHandler() {
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

    private ArrayList<Row> getTableModel() {
        ArrayList<Row> model;
        TestReflexViewDO reflexDO;
        Row row;
        
        model  = new ArrayList<Row>();
        
        if (reflexTests == null)
            return model;

        try {
            for (int iter = 0; iter < reflexTests.size(); iter++ ) {
                reflexDO = reflexTests.get(iter);
                row = new Row(1);
                
                if(promptId.equals(reflexDO.getFlagsId()) || promptNonDupId.equals(reflexDO.getFlagsId())){
                    row.setData(new TestIdDupsVO(reflexDO.getAddTestId(), promptNonDupId.equals(reflexDO.getFlagsId())));
                    row.setCell(0,reflexDO.getAddTestName() + ", " + reflexDO.getAddMethodName());
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
        Integer[] selRows;
        ArrayList<Row> selectedRows;
        Row row;
        int i,j;
        
        if(returnList == null){
            returnList = new ArrayList<TestIdDupsVO>();
            selRows = reflexTestTable.getSelectedRows();
            
            selectedRows = new ArrayList<Row>();
            
            for(i = 0; i < selRows.length; i++) 
            	selectedRows.add(reflexTestTable.getRowAt(i));
                
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
                returnList.add((TestIdDupsVO)row.getData());
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