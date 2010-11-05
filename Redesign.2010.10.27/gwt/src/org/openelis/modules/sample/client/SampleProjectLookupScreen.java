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
import java.util.EnumSet;

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
import org.openelis.gwt.services.ScreenService;
import org.openelis.gwt.widget.AutoCompleteValue;
import org.openelis.gwt.widget.Button;
import org.openelis.gwt.widget.AutoComplete;
import org.openelis.gwt.widget.Item;
import org.openelis.gwt.widget.QueryFieldUtil;
import org.openelis.gwt.widget.table.Row;
import org.openelis.gwt.widget.table.Table;
import org.openelis.gwt.widget.table.event.CellEditedEvent;
import org.openelis.gwt.widget.table.event.CellEditedHandler;
import org.openelis.gwt.widget.table.event.RowAddedEvent;
import org.openelis.gwt.widget.table.event.RowAddedHandler;
import org.openelis.gwt.widget.table.event.RowDeletedEvent;
import org.openelis.gwt.widget.table.event.RowDeletedHandler;
import org.openelis.manager.SampleProjectManager;

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

public class SampleProjectLookupScreen extends Screen implements HasActionHandlers<SampleProjectLookupScreen.Action> {

    private SampleProjectManager manager;
    protected Button projectRemoveButton;
    
    public enum Action {
        OK
    };

    private Table sampleProjectTable;

    public SampleProjectLookupScreen() throws Exception {
        super((ScreenDefInt)GWT.create(SampleProjectLookupDef.class));
        service = new ScreenService("controller?service=org.openelis.modules.project.server.ProjectService");
        
        // Setup link between Screen and widget Handlers
        initialize();

        // Initialize Screen
        setState(State.DEFAULT);

    }
    
    private void initialize(){
        sampleProjectTable = (Table)def.getWidget("sampleProjectTable");
        addScreenHandler(sampleProjectTable, new ScreenEventHandler<ArrayList<Row>>() {
            public void onDataChange(DataChangeEvent event) {
                sampleProjectTable.setModel(getTableModel());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                sampleProjectTable.setEnabled(EnumSet.of(State.ADD,State.UPDATE).contains(event.getState()));
                sampleProjectTable.setQueryMode(event.getState() == State.QUERY);
            }
        });
        
        sampleProjectTable.addBeforeSelectionHandler(new BeforeSelectionHandler<Integer>() {
            public void onBeforeSelection(BeforeSelectionEvent<Integer> event) {
                //always allow selection
            }
        });

        sampleProjectTable.addSelectionHandler(new SelectionHandler<Integer>() {
            public void onSelection(SelectionEvent<Integer> event) {
                if(EnumSet.of(State.ADD, State.UPDATE).contains(state))
                    projectRemoveButton.setEnabled(true);
            }
        });

        final AutoComplete project = (AutoComplete)sampleProjectTable.getColumnWidget(0);
        sampleProjectTable.addCellEditedHandler(new CellEditedHandler() {
            public void onCellUpdated(CellEditedEvent event) {
                int row,col;
                row = event.getRow();
                col = event.getCol();
                SampleProjectViewDO projectDO;
                Row tableRow = sampleProjectTable.getRowAt(row);
                try{
                    projectDO = manager.getProjectAt(row);
                }catch(Exception e){
                    Window.alert(e.getMessage());
                    return;
                }
                    
                Object val = tableRow.getCell(col);
                
                switch (col) {
                    case 0:
                        Item<Integer> selectedRow = project.getSelectedItem();
                        String des = null;
                        
                        if(selectedRow.getKey() != null)
                            des = (String)selectedRow.getCell(1);
                            
                        sampleProjectTable.setValueAt(sampleProjectTable.getSelectedRow(), 1, des);
                        
                        projectDO.setProjectId((Integer)selectedRow.getKey());
                        projectDO.setProjectName((String)selectedRow.getCell(0));
                        projectDO.setProjectDescription(des);
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
                QueryFieldUtil parser;
                Item<Integer> row;
                ProjectDO data;
                ArrayList<ProjectDO> list;
                ArrayList<Item<Integer>> model;

                parser = new QueryFieldUtil();
                try {
                	parser.parse(event.getMatch());
                }catch(Exception e){
                	
                }

                window.setBusy();
                try {
                    list = service.callList("fetchActiveByName", parser.getParameter().get(0));
                    model = new ArrayList<Item<Integer>>();
                    for (int i = 0; i < list.size(); i++ ) {
                        row = new Item<Integer>(4);
                        data = list.get(i);

                        row.setKey(data.getId());
                        row.setCell(0,data.getName());
                        row.setCell(1,data.getDescription());
                       
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
                manager.addProject(new SampleProjectViewDO());
                projectRemoveButton.setEnabled(true);
            }
        });

        sampleProjectTable.addRowDeletedHandler(new RowDeletedHandler() {
            public void onRowDeleted(RowDeletedEvent event) {
                manager.removeProjectAt(event.getIndex());
                projectRemoveButton.setEnabled(false);
            }
        });
        
        projectRemoveButton = (Button)def.getWidget("projectRemoveButton");
        addScreenHandler(projectRemoveButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                int selectedRow = sampleProjectTable.getSelectedRow();
                if (selectedRow > -1 && sampleProjectTable.getRowCount() > 0) {
                    sampleProjectTable.removeRowAt(selectedRow);
                }
            }
            public void onStateChange(StateChangeEvent<State> event) {
                projectRemoveButton.setEnabled(false);
            }
            
        });
        
        final Button projectAddButton = (Button)def.getWidget("projectAddButton");
        addScreenHandler(projectAddButton,new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                sampleProjectTable.addRow();
                sampleProjectTable.selectRowAt(sampleProjectTable.getRowCount()-1);
                sampleProjectTable.scrollToVisible(sampleProjectTable.getSelectedRow());
                sampleProjectTable.startEditing(sampleProjectTable.getRowCount()-1, 0);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                projectAddButton.setEnabled(EnumSet.of(State.ADD,State.UPDATE).contains(event.getState()));
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

    }
    
    public void ok() {
        sampleProjectTable.finishEditing();
        
        if ( !validate()) {
            window.setError(consts.get("correctErrors"));
            return;
        }
        
        ActionEvent.fire(this, Action.OK, null);
        window.close();
    }
        
    private ArrayList<Row> getTableModel() {
        ArrayList<Row> model = new ArrayList<Row>();
        
        if(manager == null)
            return model;
        
        try 
        {   
            for(int iter = 0;iter < manager.count();iter++) {
                SampleProjectViewDO projectRow = (SampleProjectViewDO)manager.getProjectAt(iter);
            
               Row row = new Row(3);
               //row.key = projectRow.getId();
               
               row.setCell(0,new AutoCompleteValue(projectRow.getProjectId(),projectRow.getProjectName()));
               row.setCell(1,projectRow.getProjectDescription());
               row.setCell(2,projectRow.getIsPermanent());
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
        
        DeferredCommand.addCommand(new Command() {
            public void execute() {
                if(sampleProjectTable.getRowCount() > 0)
                    sampleProjectTable.startEditing(0, 0);
            }
        });
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