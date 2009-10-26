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
package org.openelis.modules.auxGroupPicker.client;

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
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.table.TableDataRow;
import org.openelis.gwt.widget.table.TableRow;
import org.openelis.gwt.widget.table.TableWidget;
import org.openelis.gwt.widget.table.event.BeforeCellEditedEvent;
import org.openelis.gwt.widget.table.event.BeforeCellEditedHandler;
import org.openelis.manager.AuxFieldManager;
import org.openelis.modules.qaeventPicker.client.QaeventPickerScreen.Action;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Window;

public class AuxGroupPickerScreen extends Screen implements HasActionHandlers<AuxGroupPickerScreen.Action>{
    public enum Action {COMMIT};
    protected AppButton commitButton, abortButton;
    protected TableWidget auxGroupsTable;
    private ArrayList<TableDataRow> groupsModel; 
    
    public AuxGroupPickerScreen() throws Exception {
        super((ScreenDefInt)GWT.create(AuxGroupPickerDef.class));
        service = new ScreenService("OpenELISServlet?service=org.openelis.modules.auxiliary.server.AuxiliaryService");
        
        // Setup link between Screen and widget Handlers
        initialize();

        // Initialize Screen
        setState(State.DEFAULT);
    }

    private void initialize() {
        auxGroupsTable = (TableWidget)def.getWidget("auxGroupsTable");
        addScreenHandler(auxGroupsTable, new ScreenEventHandler<ArrayList<TableDataRow>>() {
            public void onDataChange(DataChangeEvent event) {
                auxGroupsTable.load(getTableModel());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                auxGroupsTable.enable(true);
            }
        });

        auxGroupsTable.addBeforeCellEditedHandler(new BeforeCellEditedHandler(){
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {
                event.cancel();
            }
        });
        
        auxGroupsTable.addBeforeSelectionHandler(new BeforeSelectionHandler<TableRow>(){
           public void onBeforeSelection(BeforeSelectionEvent<TableRow> event) {
               //do nothing
           }
        });
        
        commitButton = (AppButton)def.getWidget("commit");
        addScreenHandler(commitButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                commit();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                commitButton.enable(true);
            }
        });

        abortButton = (AppButton)def.getWidget("abort");
        addScreenHandler(abortButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                abort();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                abortButton.enable(true);
            }
        });
    }
    
    private void commit(){
        ArrayList<TableDataRow> selections = auxGroupsTable.getSelections();
        ArrayList<AuxFieldManager> returnList;

        returnList = new ArrayList<AuxFieldManager>();
        try{
            for(int i=0; i<selections.size(); i++)
                returnList.add(AuxFieldManager.fetchByAuxFieldGroupId((Integer)selections.get(i).key));
        }catch(Exception e){
            Window.alert(e.getMessage());
        }
            
        if(selections.size() > 0)
            ActionEvent.fire(this, Action.COMMIT, returnList);
        
        window.close();
    }
    
    private void abort(){
        window.close();
    }
    
    private ArrayList<TableDataRow> getTableModel() {
        ArrayList<AuxFieldGroupDO> groups;
        AuxFieldGroupDO groupDO;
        if(groupsModel != null)
            return groupsModel;
        
        groupsModel = new ArrayList<TableDataRow>();
        try{
            groups = service.callList("fetchActive");
        }catch(Exception e){
            Window.alert(e.getMessage());
            return groupsModel;
        }
        
        for(int i=0; i<groups.size(); i++) {
            groupDO = groups.get(i);
            
           TableDataRow row = new TableDataRow(2);
           row.key = groupDO.getId();

           row.cells.get(0).value = groupDO.getName();
           row.cells.get(1).value = groupDO.getDescription();
           
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