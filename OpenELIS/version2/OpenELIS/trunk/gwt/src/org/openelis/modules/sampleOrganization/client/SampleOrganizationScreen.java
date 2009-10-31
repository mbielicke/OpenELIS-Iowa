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
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.OrganizationDO;
import org.openelis.domain.SampleOrganizationDO;
import org.openelis.domain.SampleOrganizationViewDO;
import org.openelis.gwt.common.FormErrorException;
import org.openelis.gwt.common.ValidationErrorsList;
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
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.AutoComplete;
import org.openelis.gwt.widget.Dropdown;
import org.openelis.gwt.widget.QueryFieldUtil;
import org.openelis.gwt.widget.table.TableDataRow;
import org.openelis.gwt.widget.table.TableWidget;
import org.openelis.gwt.widget.table.event.CellEditedEvent;
import org.openelis.gwt.widget.table.event.CellEditedHandler;
import org.openelis.gwt.widget.table.event.RowAddedEvent;
import org.openelis.gwt.widget.table.event.RowAddedHandler;
import org.openelis.gwt.widget.table.event.RowDeletedEvent;
import org.openelis.gwt.widget.table.event.RowDeletedHandler;
import org.openelis.manager.SampleOrganizationManager;
import org.openelis.modules.organization.client.OrganizationDef;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Window;

public class SampleOrganizationScreen  extends Screen implements HasActionHandlers<SampleOrganizationScreen.Action> {

    private SampleOrganizationManager manager;

    public enum Action {
        COMMIT
    };

    private TableWidget sampleOrganizationTable;
    
    public SampleOrganizationScreen() throws Exception {
        super((ScreenDefInt)GWT.create(SampleOrganizationDef.class));
        service = new ScreenService("controller?service=org.openelis.modules.organization.server.OrganizationService");
        
        // Setup link between Screen and widget Handlers
        initialize();

        // Initialize Screen
        setState(State.DEFAULT);
        
        setOrganizationTypes(DictionaryCache.getListByCategorySystemName("organization_type"));
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

        final AutoComplete<Integer> organization = ((AutoComplete<Integer>)sampleOrganizationTable.getColumns().get(2).colWidget);
        sampleOrganizationTable.addCellEditedHandler(new CellEditedHandler() {
            public void onCellUpdated(CellEditedEvent event) {
                int row,col;
                row = event.getRow();
                col = event.getCol();
                SampleOrganizationViewDO orgDO;
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

                        sampleOrganizationTable.setCell(sampleOrganizationTable.getSelectedRow(),
                                                        1,
                                                        id);
                        sampleOrganizationTable.setCell(sampleOrganizationTable.getSelectedRow(),
                                                        3,
                                                        city);
                        sampleOrganizationTable.setCell(sampleOrganizationTable.getSelectedRow(),
                                                        4,
                                                        state);

                        orgDO.setOrganizationId(id);
                        orgDO.setOrganizationName((String)selectedRow.cells.get(0).value);
                        orgDO.setOrganizationCity(city);
                        orgDO.setOrganizationState(state);
                        break;
                    case 3:
                        orgDO.setOrganizationCity((String)val);
                        break;
                    case 4:
                        orgDO.setOrganizationState((String)val);
                        break;
                }
            }
        });
        
        organization.addGetMatchesHandler(new GetMatchesHandler(){
            public void onGetMatches(GetMatchesEvent event) {
                QueryFieldUtil parser;
                TableDataRow row;
                OrganizationDO data;
                ArrayList<OrganizationDO> list;
                ArrayList<TableDataRow> model;

                parser = new QueryFieldUtil();
                parser.parse(event.getMatch());

                window.setBusy();
                try {
                    list = service.callList("fetchByIdOrName", parser.getParameter().get(0));
                    model = new ArrayList<TableDataRow>();
                    for (int i = 0; i < list.size(); i++ ) {
                        row = new TableDataRow(4);
                        data = list.get(i);

                        row.key = data.getId();
                        row.cells.get(0).value = data.getName();
                        row.cells.get(1).value = data.getAddress().getStreetAddress();
                        row.cells.get(2).value = data.getAddress().getCity();
                        row.cells.get(3).value = data.getAddress().getState();

                        model.add(row);
                    }
                    organization.showAutoMatches(model);
                } catch (Throwable e) {
                    e.printStackTrace();
                    Window.alert(e.getMessage());
                }
                window.clearStatus();
            }
        });
        
        sampleOrganizationTable.addRowAddedHandler(new RowAddedHandler() {
            public void onRowAdded(RowAddedEvent event) {
                manager.addOrganization(new SampleOrganizationViewDO());
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
                int selectedRow = sampleOrganizationTable.getSelectedRow();
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
        if(validate()){
            ActionEvent.fire(this, Action.COMMIT, null);
            window.close();
        }
    }
    
    private ArrayList<TableDataRow> getTableModel() {
        ArrayList<TableDataRow> model = new ArrayList<TableDataRow>();
        
        if(manager == null)
            return model;
        
        try 
        {   
            for(int iter = 0;iter < manager.count();iter++) {
                SampleOrganizationViewDO orgDO = (SampleOrganizationViewDO)manager.getOrganizationAt(iter);
            
               TableDataRow row = new TableDataRow(5);
               row.key = orgDO.getId();
               
               row.cells.get(0).value = orgDO.getTypeId();
               row.cells.get(1).value = orgDO.getOrganizationId();
               row.cells.get(2).value = new TableDataRow(orgDO.getOrganizationId(), orgDO.getOrganizationName());
               row.cells.get(3).value = orgDO.getOrganizationCity();
               row.cells.get(4).value = orgDO.getOrganizationState();
               
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
        ((Dropdown<Integer>)sampleOrganizationTable.getColumns().get(0).getColumnWidget()).setModel(model);
    }
    
    public void setManager(SampleOrganizationManager man){
        manager = man;
        
        DataChangeEvent.fire(this);
    }
    
    public SampleOrganizationManager getManager(){
        return manager;
    }
    
    public void setScreenState(State state){
        setState(state);
    }
    
    protected boolean validate() {
        boolean superValue = super.validate();
        
        try{
            ValidationErrorsList errorsList = new ValidationErrorsList();
            int numBillTo, numReportTo;
            
            numBillTo = 0;
            numReportTo = 0;
            for(int i=0; i<manager.count(); i++){
                SampleOrganizationDO orgDO = manager.getOrganizationAt(i);
                if(DictionaryCache.getIdFromSystemName("org_bill_to").equals(orgDO.getTypeId()))
                    numBillTo++;
                
                if(DictionaryCache.getIdFromSystemName("org_report_to").equals(orgDO.getTypeId()))
                    numReportTo++;
            }
            
            if(numBillTo > 1)
                errorsList.add(new FormErrorException("multipleBillToException"));
            
            if(numReportTo > 1)
                errorsList.add(new FormErrorException("multipleReportToException"));
            
            if(errorsList.size() > 0)
                throw errorsList;
            
        }catch(ValidationErrorsList e){
            showErrors(e);
            return false;
        }catch(Exception e){
            Window.alert(e.getMessage());
            return false;
        }
        
        return superValue;
    }
    
    public HandlerRegistration addActionHandler(ActionHandler<Action> handler) {
        return addHandler(handler, ActionEvent.getType());
    }
}