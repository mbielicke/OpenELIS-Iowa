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
package org.openelis.modules.sample1.client;

import java.util.ArrayList;
import java.util.EnumSet;

import org.openelis.constants.Messages;
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
import org.openelis.gwt.screen.ScreenDefInt;
import org.openelis.gwt.screen.ScreenEventHandler;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.AutoComplete;
import org.openelis.gwt.widget.QueryFieldUtil;
import org.openelis.gwt.widget.table.TableDataRow;
import org.openelis.gwt.widget.table.TableRow;
import org.openelis.gwt.widget.table.TableWidget;
import org.openelis.gwt.widget.table.event.CellEditedEvent;
import org.openelis.gwt.widget.table.event.CellEditedHandler;
import org.openelis.gwt.widget.table.event.RowAddedEvent;
import org.openelis.gwt.widget.table.event.RowAddedHandler;
import org.openelis.gwt.widget.table.event.RowDeletedEvent;
import org.openelis.gwt.widget.table.event.RowDeletedHandler;
import org.openelis.manager.SampleManager1;
import org.openelis.modules.project.client.ProjectService;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Window;

public class SampleProjectLookupScreen1 extends Screen implements HasActionHandlers<SampleProjectLookupScreen1.Action> {

    private SampleManager1 manager;
    protected AppButton projectRemoveButton;
    protected AutoComplete<Integer> project;
    
    public enum Action {
        OK
    };

    private TableWidget sampleProjectTable;

    public SampleProjectLookupScreen1() throws Exception {
        super((ScreenDefInt)GWT.create(SampleProjectLookupDef1.class));
        
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
        
        sampleProjectTable.addBeforeSelectionHandler(new BeforeSelectionHandler<TableRow>() {
            public void onBeforeSelection(BeforeSelectionEvent<TableRow> event) {
                //always allow selection
            }
        });

        sampleProjectTable.addSelectionHandler(new SelectionHandler<TableRow>() {
            public void onSelection(SelectionEvent<TableRow> event) {
                if(EnumSet.of(State.ADD, State.UPDATE).contains(state))
                    projectRemoveButton.enable(true);
            }
        });

        project = ((AutoComplete<Integer>)sampleProjectTable.getColumns().get(0).colWidget);
        sampleProjectTable.addCellEditedHandler(new CellEditedHandler() {
            public void onCellUpdated(CellEditedEvent event) {
                int row,col;
                String des;
                SampleProjectViewDO data;
                TableDataRow tableRow, selectedRow;
                Object val;
             
                row = event.getRow();
                col = event.getCol();
                tableRow = sampleProjectTable.getRow(row);
                
                try {
                    data = manager.project.get(row);
                } catch(Exception e){
                    Window.alert(e.getMessage());
                    return;
                }
                    
                val = tableRow.cells.get(col).value;
                
                switch (col) {
                    case 0:
                        selectedRow = project.getSelection();
                        des = null;
                        
                        if (selectedRow.key != null)
                            des = (String)selectedRow.cells.get(1).value;
                            
                        sampleProjectTable.setCell(sampleProjectTable.getSelectedRow(), 1, des);
                        
                        data.setProjectId((Integer)selectedRow.key);
                        data.setProjectName((String)selectedRow.cells.get(0).value);
                        data.setProjectDescription(des);
                        break;
                    case 1:
                        data.setProjectDescription((String)val);
                        break;
                    case 2:
                        data.setIsPermanent((String)val);
                        break;
                }
            }
        });

        project.addGetMatchesHandler(new GetMatchesHandler(){
            public void onGetMatches(GetMatchesEvent event) {
                TableDataRow row;
                ProjectDO data;
                ArrayList<ProjectDO> list;
                ArrayList<TableDataRow> model;

                window.setBusy();
                try {
                    list = ProjectService.get().fetchActiveByName(QueryFieldUtil.parseAutocomplete(event.getMatch()));
                    model = new ArrayList<TableDataRow>();
                    for (int i = 0; i < list.size(); i++ ) {
                        row = new TableDataRow(4);
                        data = list.get(i);

                        row.key = data.getId();
                        row.cells.get(0).setValue(data.getName());
                        row.cells.get(1).setValue(data.getDescription());
                       
                        model.add(row);
                    }
                    project.showAutoMatches(model);
                } catch (Throwable e) {
                    e.printStackTrace();
                    Window.alert(e.getMessage());
                }
                window.clearStatus();
            }
        });
        
        sampleProjectTable.addRowAddedHandler(new RowAddedHandler() {
            public void onRowAdded(RowAddedEvent event) {
                SampleProjectViewDO data;
                
                //data = new SampleProjectViewDO();
                //data.setIsPermanent("N");
                //manager.addProject(data);
                data = manager.project.add();
                data.setIsPermanent("N");
                projectRemoveButton.enable(true);
            }
        });

        sampleProjectTable.addRowDeletedHandler(new RowDeletedHandler() {
            public void onRowDeleted(RowDeletedEvent event) {
                manager.project.remove(event.getIndex());
                projectRemoveButton.enable(false);
            }
        });
        
        projectRemoveButton = (AppButton)def.getWidget("projectRemoveButton");
        addScreenHandler(projectRemoveButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                int r;
                
                r = sampleProjectTable.getSelectedRow();
                if (r > -1 && sampleProjectTable.numRows() > 0)
                    sampleProjectTable.deleteRow(r);
                
            }
            public void onStateChange(StateChangeEvent<State> event) {
                projectRemoveButton.enable(false);
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
        
        final AppButton okButton = (AppButton)def.getWidget("ok");
        addScreenHandler(okButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                ok();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                okButton.enable(true);
            }
        });

    }
    
    public void ok() {
        sampleProjectTable.finishEditing();
        
        if ( !validate()) {
            window.setError(Messages.get().correctErrors());
            return;
        }
        
        ActionEvent.fire(this, Action.OK, null);
        window.close();
    }
        
    private ArrayList<TableDataRow> getTableModel() {
        ArrayList<TableDataRow> model;
        SampleProjectViewDO data;
        TableDataRow row;
        
        model = new ArrayList<TableDataRow>();
        
        if(manager == null)
            return model;
        
        try {   
            for(int iter = 0; iter < manager.project.count(); iter++) {
                data = manager.project.get(iter);
            
                row = new TableDataRow(3);
                row.key = data.getId();
               
                row.cells.get(0).setValue(new TableDataRow(data.getProjectId(), data.getProjectName()));
                row.cells.get(1).setValue(data.getProjectDescription());
                row.cells.get(2).setValue(data.getIsPermanent());
                model.add(row);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }       
        
        return model;
    }
   
    public void setManager(SampleManager1 man){
        manager = man;
        DataChangeEvent.fire(this);
        
        DeferredCommand.addCommand(new Command() {
            public void execute() {
                if(sampleProjectTable.numRows() > 0)
                    sampleProjectTable.select(0, 0);
            }
        });
    }
    
    public SampleManager1 getManager(){
        return manager;
    }
    
    public void setScreenState(State state){
        setState(state);
    }
    
    public HandlerRegistration addActionHandler(ActionHandler<Action> handler) {
        return addHandler(handler, ActionEvent.getType());
    }
}