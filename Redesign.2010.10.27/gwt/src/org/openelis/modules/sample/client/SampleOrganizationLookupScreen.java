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

import org.openelis.cache.DictionaryCache;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.OrganizationDO;
import org.openelis.domain.SampleOrganizationDO;
import org.openelis.domain.SampleOrganizationViewDO;
import org.openelis.gwt.common.FormErrorException;
import org.openelis.gwt.common.LocalizedException;
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
import org.openelis.gwt.widget.AutoCompleteValue;
import org.openelis.gwt.widget.Button;
import org.openelis.gwt.widget.AutoComplete;
import org.openelis.gwt.widget.Dropdown;
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
import org.openelis.manager.SampleOrganizationManager;

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

public class SampleOrganizationLookupScreen  extends Screen implements HasActionHandlers<SampleOrganizationLookupScreen.Action> {

    private SampleOrganizationManager manager;
    protected Button organizationRemoveButton;
    private boolean canAddReportTo, canAddBillTo, canAddSecondReportTo;
    private Integer reportToId, billToId, secondReportToId;
    
    public enum Action {
        OK
    };

    private Table sampleOrganizationTable;
    
    public SampleOrganizationLookupScreen() throws Exception {
        super((ScreenDefInt)GWT.create(SampleOrganizationLookupDef.class));
        service = new ScreenService("controller?service=org.openelis.modules.organization.server.OrganizationService");
        
        setCanAddReportTo(true);
        setCanAddBillTo(true);
        setCanAddSecondReportTo(true);
        
        // Setup link between Screen and widget Handlers
        initialize();

        // Initialize Screen
        setState(State.DEFAULT);
        
        initializeDropdowns();
    }
    
    private void initialize(){
        sampleOrganizationTable = (Table)def.getWidget("sampleOrganizationTable");
        addScreenHandler(sampleOrganizationTable, new ScreenEventHandler<ArrayList<Row>>() {
            public void onDataChange(DataChangeEvent event) {
                sampleOrganizationTable.setModel(getTableModel());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                sampleOrganizationTable.setEnabled(EnumSet.of(State.ADD,State.UPDATE).contains(event.getState()));
                sampleOrganizationTable.setQueryMode(event.getState() == State.QUERY);
            }
        });
        
        sampleOrganizationTable.addBeforeSelectionHandler(new BeforeSelectionHandler<Integer>() {
            public void onBeforeSelection(BeforeSelectionEvent<Integer> event) {
                //always allow selection
            }
        });

        sampleOrganizationTable.addSelectionHandler(new SelectionHandler<Integer>() {
            public void onSelection(SelectionEvent<Integer> event) {
                if(EnumSet.of(State.ADD, State.UPDATE).contains(state))
                    organizationRemoveButton.setEnabled(true);
            }
        });

        final AutoComplete organization = ((AutoComplete)sampleOrganizationTable.getColumnWidget(2));
        sampleOrganizationTable.addCellEditedHandler(new CellEditedHandler() {
            public void onCellUpdated(CellEditedEvent event) {
                int row,col;
                Object val;
                
                row = event.getRow();
                col = event.getCol();
                
                SampleOrganizationViewDO orgDO;
                
                try{
                    orgDO = manager.getOrganizationAt(row);
                }catch(Exception e){
                    com.google.gwt.user.client.Window.alert(e.getMessage());
                    return;
                }
                    
                val = sampleOrganizationTable.getValueAt(row, col);
                
                switch (col) {
                    case 0:
                        sampleOrganizationTable.clearExceptions(row, col);
                        if(reportToId.equals((Integer)val) && !canAddReportTo)
                            sampleOrganizationTable.addException(row, col, new LocalizedException("cantAddReportToException"));
                        
                        else if(billToId.equals((Integer)val) && !canAddBillTo)
                            sampleOrganizationTable.addException(row, col, new LocalizedException("cantAddBillToException"));
                        
                        else if(secondReportToId.equals((Integer)val) && !canAddSecondReportTo)
                            sampleOrganizationTable.addException(row, col, new LocalizedException("cantAddSecondReortToException"));
                        
                        orgDO.setTypeId((Integer)val);
                        break;
                    case 1:
                        orgDO.setOrganizationAttention((String)val);
                        break;
                    case 2:
                        Item<Integer> selectedRow = organization.getSelectedItem();
                        Integer id = null;
                        String city = null;
                        String state = null;

                        if (selectedRow.getKey() != null) {
                            id = (Integer)selectedRow.getKey();
                            city = (String)selectedRow.getCell(2);
                            state = (String)selectedRow.getCell(3);
                        }

                        sampleOrganizationTable.setValueAt(sampleOrganizationTable.getSelectedRow(),
                                                        3,
                                                        city);
                        sampleOrganizationTable.setValueAt(sampleOrganizationTable.getSelectedRow(),
                                                        4,
                                                        state);

                        orgDO.setOrganizationId(id);
                        orgDO.setOrganizationName((String)selectedRow.getCell(0));
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
                Item<Integer> row;
                OrganizationDO data;
                ArrayList<OrganizationDO> list;
                ArrayList<Item<Integer>> model;

                parser = new QueryFieldUtil();
                try {
                	parser.parse(event.getMatch());
                }catch(Exception e) {
                	
                }

                window.setBusy();
                try {
                    list = service.callList("fetchByIdOrName", parser.getParameter().get(0));
                    model = new ArrayList<Item<Integer>>();
                    for (int i = 0; i < list.size(); i++ ) {
                        row = new Item<Integer>(4);
                        data = list.get(i);

                        row.setKey(data.getId());
                        row.setCell(0,data.getName());
                        row.setCell(1,data.getAddress().getStreetAddress());
                        row.setCell(2,data.getAddress().getCity());
                        row.setCell(3,data.getAddress().getState());

                        model.add(row);
                    }
                    organization.showAutoMatches(model);
                } catch (Throwable e) {
                    e.printStackTrace();
                    com.google.gwt.user.client.Window.alert(e.getMessage());
                }
                window.clearStatus();
            }
        });
        
        sampleOrganizationTable.addRowAddedHandler(new RowAddedHandler() {
            public void onRowAdded(RowAddedEvent event) {
                manager.addOrganization(new SampleOrganizationViewDO());
                organizationRemoveButton.setEnabled(true);
            }
        });

        sampleOrganizationTable.addRowDeletedHandler(new RowDeletedHandler() {
            public void onRowDeleted(RowDeletedEvent event) {
                manager.removeOrganizationAt(event.getIndex());
                organizationRemoveButton.setEnabled(false);
            }
        });
        
        organizationRemoveButton = (Button)def.getWidget("organizationRemoveButton");
        addScreenHandler(organizationRemoveButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                int selectedRow = sampleOrganizationTable.getSelectedRow();
                if (selectedRow > -1 && sampleOrganizationTable.getRowCount() > 0) {
                    sampleOrganizationTable.removeRowAt(selectedRow);
                }
            }
            public void onStateChange(StateChangeEvent<State> event) {
                organizationRemoveButton.setEnabled(false);
            }
            
        });
        
        final Button organizationAddButton = (Button)def.getWidget("organizationAddButton");
        addScreenHandler(organizationAddButton,new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                sampleOrganizationTable.addRow();
                sampleOrganizationTable.selectRowAt(sampleOrganizationTable.getRowCount()-1);
                sampleOrganizationTable.scrollToVisible(sampleOrganizationTable.getSelectedRow());
                sampleOrganizationTable.startEditing(sampleOrganizationTable.getRowCount()-1, 0);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                organizationAddButton.setEnabled(EnumSet.of(State.ADD,State.UPDATE).contains(event.getState()));
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
        sampleOrganizationTable.finishEditing();
        if(validate()){
            ActionEvent.fire(this, Action.OK, null);
            window.close();
        }
    }
    
    private ArrayList<Row> getTableModel() {
        ArrayList<Row> model = new ArrayList<Row>();
        
        if(manager == null)
            return model;
        
        try 
        {   
            for(int iter = 0;iter < manager.count();iter++) {
                SampleOrganizationViewDO orgDO = (SampleOrganizationViewDO)manager.getOrganizationAt(iter);
            
               Row row = new Row(5);
               //row.key = orgDO.getId();
               
               row.setCell(0,orgDO.getTypeId());
               row.setCell(1,orgDO.getOrganizationAttention());
               row.setCell(2,new AutoCompleteValue(orgDO.getOrganizationId(), orgDO.getOrganizationName()));
               row.setCell(3,orgDO.getOrganizationCity());
               row.setCell(4,orgDO.getOrganizationState());
               
               model.add(row);
               
            }
        } catch (Exception e) {
    
            e.printStackTrace();
            return null;
        }       
        
        return model;
    }
    
    public void setCanAddReportTo(boolean canAddReportTo){
        this.canAddReportTo = canAddReportTo;
    }
    
    public void setCanAddBillTo(boolean canAddBillTo){
        this.canAddBillTo = canAddBillTo;
    }
    
    public void setCanAddSecondReportTo(boolean canAddSecondReportTo){
        this.canAddSecondReportTo = canAddSecondReportTo;
    }
    
    private void initializeDropdowns() {
        ArrayList<DictionaryDO> list;
        ArrayList<Item<Integer>> model;
        
        model = new ArrayList<Item<Integer>>();
        model.add(new Item<Integer>(null, ""));
        
        try{
            list = DictionaryCache.getListByCategorySystemName("organization_type");
            for(DictionaryDO resultDO :  list){
                model.add(new Item<Integer>(resultDO.getId(),resultDO.getEntry()));
            } 
            ((Dropdown<Integer>)sampleOrganizationTable.getColumnWidget(0)).setModel(model);
            
            //load the type ids
            reportToId = DictionaryCache.getIdFromSystemName("org_report_to");
            billToId = DictionaryCache.getIdFromSystemName("org_bill_to");
            secondReportToId = DictionaryCache.getIdFromSystemName("org_second_report_to");
        
        }catch(Exception e){
            com.google.gwt.user.client.Window.alert("initializedropdowns: "+e.getMessage());
        }
    }
    
    public void setManager(SampleOrganizationManager man){
        manager = man;
        
        DataChangeEvent.fire(this);
        
        DeferredCommand.addCommand(new Command() {
            public void execute() {
                if(sampleOrganizationTable.getRowCount() > 0)
                    sampleOrganizationTable.startEditing(0, 0);
            }
        });
    }
    
    public SampleOrganizationManager getManager(){
        return manager;
    }
    
    public void setScreenState(State state){
        setState(state);
    }
    
    public boolean validate() {
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
            com.google.gwt.user.client.Window.alert(e.getMessage());
            return false;
        }
        
        if (!superValue)
            window.setError(consts.get("correctErrors"));
        
        return superValue;
    }
    
    public HandlerRegistration addActionHandler(ActionHandler<Action> handler) {
        return addHandler(handler, ActionEvent.getType());
    }
}