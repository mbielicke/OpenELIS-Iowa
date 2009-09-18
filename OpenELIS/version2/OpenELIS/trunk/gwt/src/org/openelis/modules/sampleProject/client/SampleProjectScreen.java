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
package org.openelis.modules.sampleProject.client;

import java.util.ArrayList;
import java.util.EnumSet;

import org.openelis.common.AutocompleteRPC;
import org.openelis.domain.ProjectDO;
import org.openelis.domain.SampleProjectViewDO;
import org.openelis.gwt.event.ActionEvent;
import org.openelis.gwt.event.ActionHandler;
import org.openelis.gwt.event.DataChangeEvent;
import org.openelis.gwt.event.GetMatchesEvent;
import org.openelis.gwt.event.GetMatchesHandler;
import org.openelis.gwt.event.HasActionHandlers;
import org.openelis.gwt.event.StateChangeEvent;
import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.screen.ScreenEventHandler;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.AutoComplete;
import org.openelis.gwt.widget.table.TableDataRow;
import org.openelis.gwt.widget.table.TableWidget;
import org.openelis.gwt.widget.table.event.CellEditedEvent;
import org.openelis.gwt.widget.table.event.CellEditedHandler;
import org.openelis.gwt.widget.table.event.RowAddedEvent;
import org.openelis.gwt.widget.table.event.RowAddedHandler;
import org.openelis.gwt.widget.table.event.RowDeletedEvent;
import org.openelis.gwt.widget.table.event.RowDeletedHandler;
import org.openelis.manager.SampleProjectManager;
import org.openelis.metamap.SampleProjectMetaMap;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Window;

public class SampleProjectScreen extends Screen implements HasActionHandlers<SampleProjectScreen.Action> {

    private SampleProjectManager manager;

    public enum Action {
        COMMIT
    };

    private SampleProjectMetaMap       meta = new SampleProjectMetaMap();
    private TableWidget sampleProjectTable;

    public SampleProjectScreen() throws Exception {
        // Call base to get ScreenDef and draw screen
        super("OpenELISServlet?service=org.openelis.modules.sampleProject.server.SampleProjectService");
        
        // Setup link between Screen and widget Handlers
        initialize();

        // Initialize Screen
        setState(State.DEFAULT);

    }
    
    private void initialize(){
        sampleProjectTable = (TableWidget)def.getWidget("sampleProjectTable");
        addScreenHandler(sampleProjectTable, new ScreenEventHandler<ArrayList<TableDataRow>>() {
            public void onDataChange(DataChangeEvent event) {
                sampleProjectTable.load(getTableModel());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                sampleProjectTable.enable(EnumSet.of(State.ADD,State.UPDATE).contains(event.getState()));
                sampleProjectTable.setQueryMode(event.getState() == State.QUERY);
            }
        });

        final AutoComplete<Integer> project = ((AutoComplete<Integer>)sampleProjectTable.columns.get(0).colWidget);
        sampleProjectTable.addCellEditedHandler(new CellEditedHandler() {
            public void onCellUpdated(CellEditedEvent event) {
                int row,col;
                row = event.getRow();
                col = event.getCell();
                SampleProjectViewDO projectDO;
                TableDataRow tableRow = sampleProjectTable.getRow(row);
                try{
                    projectDO = manager.getProjectAt(row);
                }catch(Exception e){
                    Window.alert(e.getMessage());
                    return;
                }
                    
                Object val = tableRow.cells.get(col).value;
                
                switch (col) {
                    case 0:
                        TableDataRow selectedRow = project.getSelection();
                        String des = null;
                        
                        if(selectedRow.key != null)
                            des = (String)selectedRow.cells.get(1).value;
                            
                        sampleProjectTable.setCell(sampleProjectTable.getSelectedIndex(), 1, des);
                        
                        projectDO.setProjectId((Integer)selectedRow.key);
                        projectDO.setProjectName((String)selectedRow.cells.get(0).value);
                        break;
                    case 1:
                        projectDO.setProjectDescription((String)val);
                        break;
                    case 2:
                        projectDO.setIsPermanent((String)val);
                        break;
                }
            }
        });

        project.addGetMatchesHandler(new GetMatchesHandler(){
            public void onGetMatches(GetMatchesEvent event) {
                AutocompleteRPC rpc = new AutocompleteRPC();
                rpc.match = event.getMatch();
                try {
                    rpc = service.call("getProjectMatches", rpc);
                    ArrayList<TableDataRow> model = new ArrayList<TableDataRow>();
                        
                    for (int i=0; i<rpc.model.size(); i++){
                        ProjectDO autoDO = (ProjectDO)rpc.model.get(i);
                        
                        TableDataRow row = new TableDataRow(2);
                        row.key = autoDO.getId();
                        row.cells.get(0).value = autoDO.getName();
                        row.cells.get(1).value = autoDO.getDescription();
                        model.add(row);
                    } 
                    
                    project.showAutoMatches(model);
                        
                }catch(Exception e) {
                    Window.alert(e.getMessage());                     
                }
            }
        });
        
        sampleProjectTable.addRowAddedHandler(new RowAddedHandler() {
            public void onRowAdded(RowAddedEvent event) {
                manager.addProject(new SampleProjectViewDO());
            }
        });

        sampleProjectTable.addRowDeletedHandler(new RowDeletedHandler() {
            public void onRowDeleted(RowDeletedEvent event) {
                manager.removeProjectAt(event.getIndex());
            }
        });
        final AppButton projectRemoveButton = (AppButton)def.getWidget("projectRemoveButton");
        addScreenHandler(projectRemoveButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                int selectedRow = sampleProjectTable.getSelectedIndex();
                if (selectedRow > -1 && sampleProjectTable.numRows() > 0) {
                    sampleProjectTable.deleteRow(selectedRow);
                }
            }
            public void onStateChange(StateChangeEvent<State> event) {
                projectRemoveButton.enable(EnumSet.of(State.ADD,State.UPDATE).contains(event.getState()));
            }
            
        });
        
        final AppButton projectAddButton = (AppButton)def.getWidget("projectAddButton");
        addScreenHandler(projectAddButton,new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                sampleProjectTable.addRow();
                sampleProjectTable.selectRow(sampleProjectTable.numRows()-1);
                sampleProjectTable.scrollToSelection();
                sampleProjectTable.startEditing(sampleProjectTable.numRows()-1, 0);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                projectAddButton.enable(EnumSet.of(State.ADD,State.UPDATE).contains(event.getState()));
            }
        });
        
        final AppButton commitButton = (AppButton)def.getWidget("commit");
        addScreenHandler(commitButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                commit();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                commitButton.enable(true);
            }
        });

    }
    
    public void commit() {
        sampleProjectTable.finishEditing();
        ActionEvent.fire(this, Action.COMMIT, null);
        window.close();
    }
        
    private ArrayList<TableDataRow> getTableModel() {
        ArrayList<TableDataRow> model = new ArrayList<TableDataRow>();
        
        if(manager == null)
            return model;
        
        try 
        {   
            for(int iter = 0;iter < manager.count();iter++) {
                SampleProjectViewDO projectRow = (SampleProjectViewDO)manager.getProjectAt(iter);
            
               TableDataRow row = new TableDataRow(3);
               row.key = projectRow.getId();
               
               row.cells.get(0).value = new TableDataRow(projectRow.getProjectId(),projectRow.getProjectName());
               row.cells.get(1).value = projectRow.getProjectDescription();
               row.cells.get(2).value = projectRow.getIsPermanent();
               model.add(row);
               
            }
        } catch (Exception e) {
    
            e.printStackTrace();
            return null;
        }       
        
        return model;
    }
   
    public void setManager(SampleProjectManager man){
        manager = man;
        DataChangeEvent.fire(this);
    }
    
    public SampleProjectManager getManager(){
        return manager;
    }
    
    public void setScreenState(State state){
        setState(state);
    }
    
    public HandlerRegistration addActionHandler(ActionHandler<Action> handler) {
        return addHandler(handler, ActionEvent.getType());
    }
}