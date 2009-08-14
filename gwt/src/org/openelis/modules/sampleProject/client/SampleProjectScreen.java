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

import org.openelis.domain.ProjectDO;
import org.openelis.domain.SampleProjectDO;
import org.openelis.gwt.event.ActionEvent;
import org.openelis.gwt.event.ActionHandler;
import org.openelis.gwt.event.DataChangeEvent;
import org.openelis.gwt.event.GetMatchesEvent;
import org.openelis.gwt.event.GetMatchesHandler;
import org.openelis.gwt.event.HasActionHandlers;
import org.openelis.gwt.event.StateChangeEvent;
import org.openelis.gwt.screen.rewrite.Screen;
import org.openelis.gwt.screen.rewrite.ScreenEventHandler;
import org.openelis.gwt.widget.rewrite.AppButton;
import org.openelis.gwt.widget.rewrite.AutoComplete;
import org.openelis.gwt.widget.table.rewrite.TableDataRow;
import org.openelis.gwt.widget.table.rewrite.TableRow;
import org.openelis.gwt.widget.table.rewrite.TableWidget;
import org.openelis.gwt.widget.table.rewrite.event.CellEditedEvent;
import org.openelis.gwt.widget.table.rewrite.event.CellEditedHandler;
import org.openelis.gwt.widget.table.rewrite.event.RowAddedEvent;
import org.openelis.gwt.widget.table.rewrite.event.RowAddedHandler;
import org.openelis.gwt.widget.table.rewrite.event.RowDeletedEvent;
import org.openelis.gwt.widget.table.rewrite.event.RowDeletedHandler;
import org.openelis.manager.SampleProjectManager;
import org.openelis.metamap.SampleProjectMetaMap;
import org.openelis.modules.environmentalSampleLogin.client.AutocompleteRPC;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Window;

public class SampleProjectScreen extends Screen implements HasActionHandlers<SampleProjectScreen.Action> {

    private SampleProjectManager projectManager;

    public enum Action {
        COMMIT, ABORT
    };

    private SampleProjectMetaMap       meta = new SampleProjectMetaMap();

    public SampleProjectScreen() throws Exception {
        // Call base to get ScreenDef and draw screen
        super("OpenELISServlet?service=org.openelis.modules.sampleProject.server.SampleProjectService");
        
        // Setup link between Screen and widget Handlers
        initialize();

        // Initialize Screen
        setState(State.DEFAULT);

    }
    
    private void initialize(){
        final TableWidget sampleProjectTable = (TableWidget)def.getWidget("sampleProjectTable");
        addScreenHandler(sampleProjectTable, new ScreenEventHandler<ArrayList<TableDataRow>>() {
            public void onDataChange(DataChangeEvent event) {
                sampleProjectTable.load(getTableModel());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                sampleProjectTable.enable(true);
            }
        });

        sampleProjectTable.addCellEditedHandler(new CellEditedHandler() {
            public void onCellUpdated(CellEditedEvent event) {
                int row,col;
                row = event.getRow();
                col = event.getCell();
                SampleProjectDO projectDO;
                TableDataRow tableRow = sampleProjectTable.getRow(row);
                try{
                    projectDO = projectManager.getProjectAt(row);
                }catch(Exception e){
                    Window.alert(e.getMessage());
                    return;
                }
                    
                Object val = tableRow.cells.get(col).value;
                
                switch (col){
                    case 0:
                            projectDO.setProjectId((Integer)((Object[])val)[0]);    
                            break;
                    case 2:
                            projectDO.setIsPermanent((String)val);
                            break;
                }
            }
        });
        
        final AutoComplete<Integer> project = ((AutoComplete<Integer>)sampleProjectTable.columns.get(0).colWidget);
        project.addSelectionHandler(new SelectionHandler<TableRow>(){
            public void onSelection(SelectionEvent<TableRow> event) {
                TableRow autoRow = event.getSelectedItem();
                sampleProjectTable.setCell(sampleProjectTable.getSelectedIndex(), 1, autoRow.row.cells.get(1).value);
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
                projectManager.addProject(new SampleProjectDO());
            }
        });

        sampleProjectTable.addRowDeletedHandler(new RowDeletedHandler() {
            public void onRowDeleted(RowDeletedEvent event) {
                projectManager.removeProjectAt(event.getIndex());
            }
        });
        
        final AppButton commitButton = (AppButton)def.getWidget("popupSelect");
        addScreenHandler(commitButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                commit();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                commitButton.enable(true);
            }
        });

        final AppButton abortButton = (AppButton)def.getWidget("popupCancel");
        addScreenHandler(abortButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                abort();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                abortButton.enable(true);
            }
        });
    }
    
    public void commit() {
        ActionEvent.fire(this, Action.COMMIT, null);
        window.close();
    }

    public void abort() {
        ActionEvent.fire(this, Action.ABORT, null);
        window.close();
    }
        
    private ArrayList<TableDataRow> getTableModel() {
        ArrayList<TableDataRow> model = new ArrayList<TableDataRow>();
        
        if(projectManager == null)
            return model;
        
        try 
        {   
            for(int iter = 0;iter < projectManager.count();iter++) {
                SampleProjectDO projectRow = (SampleProjectDO)projectManager.getProjectAt(iter);
            
               TableDataRow row = new TableDataRow(3);
               row.key = projectRow.getId();
               
               row.cells.get(0).value = new Object[] {projectRow.getProjectId(),projectRow.getProject().getName()};
               row.cells.get(1).value = projectRow.getProject().getDescription();
               row.cells.get(2).value = projectRow.getIsPermanent();
               model.add(row);
               
            }
        } catch (Exception e) {
    
            e.printStackTrace();
            return null;
        }       
        
        return model;
    }
   
    public void setProjectManager(SampleProjectManager man){
        projectManager = man;
        DataChangeEvent.fire(this);
    }
    
    public SampleProjectManager getProjectManager(){
        return projectManager;
    }
    
    public HandlerRegistration addActionHandler(ActionHandler<Action> handler) {
        return addHandler(handler, ActionEvent.getType());
    }
}