/** Exhibit A - UIRF Open-source Based Public Software License.
* 
* The contents of this file are subject to the UIRF Open-source Based
* Public Software License(the "License"); you may not use this file except
* in compliance with the License. You may obtain a copy of the License at
* openelis.uhl.uiowa.edu
* 
* Software distributed under the License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
* License for the specific language governing rights and limitations
* under the License.
* 
* The Original Code is OpenELIS code.
* 
* The Initial Developer of the Original Code is The University of Iowa.
* Portions created by The University of Iowa are Copyright 2006-2008. All
* Rights Reserved.
* 
* Contributor(s): ______________________________________.
* 
* Alternatively, the contents of this file marked
* "Separately-Licensed" may be used under the terms of a UIRF Software
* license ("UIRF Software License"), in which case the provisions of a
* UIRF Software License are applicable instead of those above. 
*/
package org.openelis.modules.sample.client;

import java.util.ArrayList;

import org.openelis.domain.AuxFieldGroupDO;
import org.openelis.gwt.event.ActionEvent;
import org.openelis.gwt.event.ActionHandler;
import org.openelis.gwt.event.DataChangeEvent;
import org.openelis.gwt.event.HasActionHandlers;
import org.openelis.gwt.event.StateChangeEvent;
import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.screen.ScreenDefInt;
import org.openelis.gwt.screen.ScreenEventHandler;
import org.openelis.gwt.services.ScreenService;
import org.openelis.gwt.widget.Button;
import org.openelis.gwt.widget.table.Row;
import org.openelis.gwt.widget.table.Table;
import org.openelis.gwt.widget.table.event.BeforeCellEditedEvent;
import org.openelis.gwt.widget.table.event.BeforeCellEditedHandler;
import org.openelis.manager.AuxFieldManager;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Window;

public class AuxGroupLookupScreen extends Screen implements HasActionHandlers<AuxGroupLookupScreen.Action>{
    public enum Action {OK};
    protected Button                okButton, cancelButton;
    protected Table                 auxGroupsTable;
    private ArrayList<Row>          groupsModel; 
    
    public AuxGroupLookupScreen() throws Exception {
        super((ScreenDefInt)GWT.create(AuxGroupLookupDef.class));
        service = new ScreenService("OpenELISServlet?service=org.openelis.modules.auxiliary.server.AuxiliaryService");
        
        // Setup link between Screen and widget Handlers
        initialize();

        // Initialize Screen
        setState(State.DEFAULT);
    }

    private void initialize() {
        auxGroupsTable = (Table)def.getWidget("auxGroupsTable");
        addScreenHandler(auxGroupsTable, new ScreenEventHandler<ArrayList<Row>>() {
            public void onDataChange(DataChangeEvent event) {
                auxGroupsTable.setModel(getTableModel());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                auxGroupsTable.setEnabled(true);
            }
        });

        auxGroupsTable.addBeforeCellEditedHandler(new BeforeCellEditedHandler(){
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {
                event.cancel();
            }
        });
        
        auxGroupsTable.addBeforeSelectionHandler(new BeforeSelectionHandler<Integer>(){
           public void onBeforeSelection(BeforeSelectionEvent<Integer> event) {
               //do nothing
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
    
    private void ok(){
    	Integer[] sels;
        ArrayList<Row> selections; 
        ArrayList<AuxFieldManager> returnList;
        
        sels = auxGroupsTable.getSelectedRows();
        
        selections = new ArrayList<Row>();
        
        for(int i = 0; i < sels.length; i++)
        	selections.add(auxGroupsTable.getRowAt(sels[i]));

        returnList = new ArrayList<AuxFieldManager>();
        try{
            for(int i=0; i<selections.size(); i++)
                returnList.add(AuxFieldManager.fetchByGroupIdWithValues((Integer)selections.get(i).getData()));
        }catch(Exception e){
            Window.alert(e.getMessage());
        }
            
        if(selections.size() > 0)
            ActionEvent.fire(this, Action.OK, returnList);
        
        window.close();
    }
    
    private void cancel(){
        window.close();
    }
    
    private ArrayList<Row> getTableModel() {
        ArrayList<AuxFieldGroupDO> groups;
        AuxFieldGroupDO groupDO;
        if(groupsModel != null)
            return groupsModel;
        
        groupsModel = new ArrayList<Row>();
        try{
            groups = service.callList("fetchActive");
        }catch(Exception e){
            Window.alert(e.getMessage());
            return groupsModel;
        }
        
        for(int i=0; i<groups.size(); i++) {
            groupDO = groups.get(i);
            
           Row row = new Row(2);
           row.setData(groupDO.getId());

           row.setCell(0,groupDO.getName());
           row.setCell(1,groupDO.getDescription());
           
           groupsModel.add(row);
        }
            
        return groupsModel;
    }
    
    public void draw(){
        DataChangeEvent.fire(this);
    }
    
    public HandlerRegistration addActionHandler(ActionHandler<Action> handler) {
        return addHandler(handler, ActionEvent.getType());
    }
}