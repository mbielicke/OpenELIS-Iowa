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
package org.openelis.modules.sampleOrganization.client;

import java.util.ArrayList;
import java.util.EnumSet;

import org.openelis.cache.DictionaryCache;
import org.openelis.common.AutocompleteRPC;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.OrganizationAutoDO;
import org.openelis.domain.SampleOrganizationDO;
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
import org.openelis.gwt.widget.Dropdown;
import org.openelis.gwt.widget.table.TableDataRow;
import org.openelis.gwt.widget.table.TableRow;
import org.openelis.gwt.widget.table.TableWidget;
import org.openelis.gwt.widget.table.event.CellEditedEvent;
import org.openelis.gwt.widget.table.event.CellEditedHandler;
import org.openelis.gwt.widget.table.event.RowAddedEvent;
import org.openelis.gwt.widget.table.event.RowAddedHandler;
import org.openelis.gwt.widget.table.event.RowDeletedEvent;
import org.openelis.gwt.widget.table.event.RowDeletedHandler;
import org.openelis.manager.SampleOrganizationManager;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Window;

public class SampleOrganizationScreen  extends Screen implements HasActionHandlers<SampleOrganizationScreen.Action> {

    private SampleOrganizationManager manager;

    public enum Action {
        COMMIT
    };

    private TableWidget sampleOrganizationTable;
    private boolean loaded = false;

    public SampleOrganizationScreen() throws Exception {
        // Call base to get ScreenDef and draw screen
        super("OpenELISServlet?service=org.openelis.modules.sampleOrganization.server.SampleOrganizationService");
        
        // Setup link between Screen and widget Handlers
        initialize();

        // Initialize Screen
        setState(State.DEFAULT);
    }
    
    private void initialize(){
        sampleOrganizationTable = (TableWidget)def.getWidget("sampleOrganizationTable");
        addScreenHandler(sampleOrganizationTable, new ScreenEventHandler<ArrayList<TableDataRow>>() {
            public void onDataChange(DataChangeEvent event) {
                sampleOrganizationTable.load(getTableModel());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                sampleOrganizationTable.enable(EnumSet.of(State.ADD,State.UPDATE).contains(event.getState()));
                sampleOrganizationTable.setQueryMode(event.getState() == State.QUERY);
            }
        });

        final AutoComplete<Integer> organization = ((AutoComplete<Integer>)sampleOrganizationTable.columns.get(2).colWidget);
        sampleOrganizationTable.addCellEditedHandler(new CellEditedHandler() {
            public void onCellUpdated(CellEditedEvent event) {
                int row,col;
                row = event.getRow();
                col = event.getCell();
                SampleOrganizationDO orgDO;
                TableDataRow tableRow = sampleOrganizationTable.getRow(row);
                try{
                    orgDO = manager.getOrganizationAt(row);
                }catch(Exception e){
                    Window.alert(e.getMessage());
                    return;
                }
                    
                Object val = tableRow.cells.get(col).value;
                
                switch (col) {
                    case 0:
                        orgDO.setTypeId((Integer)val);
                        break;
                    case 1:
                        orgDO.setOrganizationId((Integer)val);
                        orgDO.getOrganization().setOrganizationId((Integer)val);
                        break;
                    case 2:
                        TableDataRow selectedRow = organization.getSelection();
                        Integer id = null;
                        String city = null;
                        String state = null;

                        if (selectedRow.key != null) {
                            id = (Integer)selectedRow.key;
                            city = (String)selectedRow.cells.get(2).value;
                            state = (String)selectedRow.cells.get(3).value;
                        }

                        sampleOrganizationTable.setCell(sampleOrganizationTable.getSelectedIndex(),
                                                        1,
                                                        id);
                        sampleOrganizationTable.setCell(sampleOrganizationTable.getSelectedIndex(),
                                                        3,
                                                        city);
                        sampleOrganizationTable.setCell(sampleOrganizationTable.getSelectedIndex(),
                                                        4,
                                                        state);

                        orgDO.getOrganization().setName((String)selectedRow.cells.get(0).value);
                        break;
                    case 3:
                        orgDO.getOrganization().getAddressDO().setCity((String)val);
                        break;
                    case 4:
                        orgDO.getOrganization().getAddressDO().setState((String)val);
                        break;
                }
            }
        });
        
        organization.addGetMatchesHandler(new GetMatchesHandler(){
            public void onGetMatches(GetMatchesEvent event) {
                AutocompleteRPC rpc = new AutocompleteRPC();
                rpc.match = event.getMatch();
                try {
                    rpc = service.call("getOrganizationMatches", rpc);
                    ArrayList<TableDataRow> model = new ArrayList<TableDataRow>();
                        
                    for (int i=0; i<rpc.model.size(); i++){
                        OrganizationAutoDO autoDO = (OrganizationAutoDO)rpc.model.get(i);
                        
                        TableDataRow row = new TableDataRow(4);
                        row.key = autoDO.getId();
                        row.cells.get(0).value = autoDO.getName();
                        row.cells.get(1).value = autoDO.getAddress();
                        row.cells.get(2).value = autoDO.getCity();
                        row.cells.get(3).value = autoDO.getState();
                        model.add(row);
                    } 
                    
                    organization.showAutoMatches(model);
                        
                }catch(Exception e) {
                    Window.alert(e.getMessage());                     
                }
            }
        });
        
        sampleOrganizationTable.addRowAddedHandler(new RowAddedHandler() {
            public void onRowAdded(RowAddedEvent event) {
                manager.addOrganization(new SampleOrganizationDO());
            }
        });

        sampleOrganizationTable.addRowDeletedHandler(new RowDeletedHandler() {
            public void onRowDeleted(RowDeletedEvent event) {
                manager.removeOrganizationAt(event.getIndex());
            }
        });
        final AppButton organizationRemoveButton = (AppButton)def.getWidget("organizationRemoveButton");
        addScreenHandler(organizationRemoveButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                int selectedRow = sampleOrganizationTable.getSelectedIndex();
                if (selectedRow > -1 && sampleOrganizationTable.numRows() > 0) {
                    sampleOrganizationTable.deleteRow(selectedRow);
                }
            }
            public void onStateChange(StateChangeEvent<State> event) {
                organizationRemoveButton.enable(EnumSet.of(State.ADD,State.UPDATE).contains(event.getState()));
            }
            
        });
        
        final AppButton organizationAddButton = (AppButton)def.getWidget("organizationAddButton");
        addScreenHandler(organizationAddButton,new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                sampleOrganizationTable.addRow();
                sampleOrganizationTable.selectRow(sampleOrganizationTable.numRows()-1);
                sampleOrganizationTable.scrollToSelection();
                sampleOrganizationTable.startEditing(sampleOrganizationTable.numRows()-1, 0);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                organizationAddButton.enable(EnumSet.of(State.ADD,State.UPDATE).contains(event.getState()));
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
        sampleOrganizationTable.finishEditing();
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
                SampleOrganizationDO orgDO = (SampleOrganizationDO)manager.getOrganizationAt(iter);
            
               TableDataRow row = new TableDataRow(5);
               row.key = orgDO.getId();
               
               row.cells.get(0).value = orgDO.getTypeId();
               row.cells.get(1).value = orgDO.getOrganizationId();
               row.cells.get(2).value = new TableDataRow(orgDO.getOrganizationId(), orgDO.getOrganization().getName());
               row.cells.get(3).value = orgDO.getOrganization().getAddressDO().getCity();
               row.cells.get(4).value = orgDO.getOrganization().getAddressDO().getState();
               
               model.add(row);
               
            }
        } catch (Exception e) {
    
            e.printStackTrace();
            return null;
        }       
        
        return model;
    }
    
    private void setOrganizationTypes(ArrayList<DictionaryDO> list) {
        ArrayList<TableDataRow> model = new ArrayList<TableDataRow>();
        model.add(new TableDataRow(null, ""));
        for(DictionaryDO resultDO :  list){
            model.add(new TableDataRow(resultDO.getId(),resultDO.getEntry()));
        } 
        ((Dropdown<Integer>)sampleOrganizationTable.columns.get(0).getColumnWidget()).setModel(model);
    }
    
    public void setManager(SampleOrganizationManager man){
        manager = man;
        
        if(!loaded)
            setOrganizationTypes(DictionaryCache.getListByCategorySystemName("organization_type"));
        
        DataChangeEvent.fire(this);
    }
    
    public SampleOrganizationManager getManager(){
        return manager;
    }
    
    public void setScreenState(State state){
        setState(state);
    }
    
    public HandlerRegistration addActionHandler(ActionHandler<Action> handler) {
        return addHandler(handler, ActionEvent.getType());
    }
}