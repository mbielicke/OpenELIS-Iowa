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

import org.openelis.cache.CategoryCache;
import org.openelis.cache.DictionaryCache;
import org.openelis.domain.AddressDO;
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
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.AutoComplete;
import org.openelis.gwt.widget.Dropdown;
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
    protected AppButton               organizationRemoveButton;
    private boolean                   canAddReportTo, canAddBillTo, canAddSecondReportTo;
    private Integer                   reportToId, billToId, secondReportToId;
    protected ScreenService           orgService;
    
    public enum Action {
        OK
    };

    private TableWidget sampleOrganizationTable;
    
    public SampleOrganizationLookupScreen() throws Exception {
        super((ScreenDefInt)GWT.create(SampleOrganizationLookupDef.class));
        
        service = new ScreenService("controller?service=org.openelis.modules.organization.server.OrganizationService");
        orgService = new ScreenService("controller?service=org.openelis.modules.organization.server.OrganizationService");
        
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
        
        sampleOrganizationTable.addBeforeSelectionHandler(new BeforeSelectionHandler<TableRow>() {
            public void onBeforeSelection(BeforeSelectionEvent<TableRow> event) {
                //always allow selection
            }
        });

        sampleOrganizationTable.addSelectionHandler(new SelectionHandler<TableRow>() {
            public void onSelection(SelectionEvent<TableRow> event) {
                if(EnumSet.of(State.ADD, State.UPDATE).contains(state))
                    organizationRemoveButton.enable(true);
            }
        });

        final AutoComplete<Integer> organization = ((AutoComplete<Integer>)sampleOrganizationTable.getColumns().get(2).colWidget);
        sampleOrganizationTable.addCellEditedHandler(new CellEditedHandler() {
            public void onCellUpdated(CellEditedEvent event) {
                int r,c;
                Object val;
                TableDataRow row;
                OrganizationDO org;
                AddressDO addr;
                SampleOrganizationViewDO data;
                
                r = event.getRow();
                c = event.getCol();
                
                try{
                    data = manager.getOrganizationAt(r);
                }catch(Exception e){
                    Window.alert(e.getMessage());
                    return;
                }
                    
                val = sampleOrganizationTable.getObject(r, c);
                
                switch (c) {
                    case 0:
                        sampleOrganizationTable.clearCellExceptions(r, c);
                        if(reportToId.equals((Integer)val) && !canAddReportTo)
                            sampleOrganizationTable.setCellException(r, c, new LocalizedException("cantAddReportToException"));
                        
                        else if(billToId.equals((Integer)val) && !canAddBillTo)
                            sampleOrganizationTable.setCellException(r, c, new LocalizedException("cantAddBillToException"));
                        
                        else if(secondReportToId.equals((Integer)val) && !canAddSecondReportTo)
                            sampleOrganizationTable.setCellException(r, c, new LocalizedException("cantAddSecondReortToException"));
                        
                        data.setTypeId((Integer)val);
                        break;
                    case 1:
                        data.setOrganizationAttention((String)val);
                        break;
                    case 2:
                        row = (TableDataRow)val;
                        if (row != null) {
                            org = (OrganizationDO)row.data;
                            data.setOrganizationId(org.getId());
                            data.setOrganizationName(org.getName());
                            
                            addr = org.getAddress();
                            data.setOrganizationMultipleUnit(addr.getMultipleUnit());
                            data.setOrganizationStreetAddress(addr.getStreetAddress());
                            data.setOrganizationCity(addr.getCity());
                            data.setOrganizationState(addr.getState());
                            data.setOrganizationZipCode(addr.getZipCode());
                            data.setOrganizationCountry(addr.getCountry());
                            
                            sampleOrganizationTable.setCell(r, 3, addr.getMultipleUnit());
                            sampleOrganizationTable.setCell(r, 4, addr.getStreetAddress());
                            sampleOrganizationTable.setCell(r, 5, addr.getCity());
                            sampleOrganizationTable.setCell(r, 6, addr.getState());
                            sampleOrganizationTable.setCell(r, 7, addr.getZipCode());
                            sampleOrganizationTable.setCell(r, 8, addr.getCountry());
                        } else {
                            data.setOrganizationId(null);
                            data.setOrganizationName(null);
                            data.setOrganizationMultipleUnit(null);
                            data.setOrganizationStreetAddress(null);
                            data.setOrganizationCity(null);
                            data.setOrganizationState(null);
                            data.setOrganizationZipCode(null);
                            data.setOrganizationCountry(null);
                            
                            sampleOrganizationTable.setCell(r, 3, null);
                            sampleOrganizationTable.setCell(r, 4, null);
                            sampleOrganizationTable.setCell(r, 5, null);
                            sampleOrganizationTable.setCell(r, 6, null);
                            sampleOrganizationTable.setCell(r, 7, null);
                            sampleOrganizationTable.setCell(r, 8, null);
                        }
                        
                        try {
                            if (SampleOrganizationUtility.isHoldRefuseSampleForOrg(data.getOrganizationId()))
                                Window.alert(consts.get("orgMarkedAsHoldRefuseSample") + "'" +
                                             data.getOrganizationName() + "'");
                        } catch (Exception e) {
                            Window.alert(e.getMessage());
                            e.printStackTrace();
                        }
                        break;
                }
            }
        });
        
        organization.addGetMatchesHandler(new GetMatchesHandler(){
            public void onGetMatches(GetMatchesEvent event) {
                TableDataRow row;
                OrganizationDO data;
                ArrayList<OrganizationDO> list;
                ArrayList<TableDataRow> model;

                window.setBusy();
                try {
                    list = service.callList("fetchByIdOrName", QueryFieldUtil.parseAutocomplete(event.getMatch()));
                    model = new ArrayList<TableDataRow>();
                    for (int i = 0; i < list.size(); i++ ) {
                        row = new TableDataRow(4);
                        data = list.get(i);

                        row.key = data.getId();
                        row.cells.get(0).value = data.getName();
                        row.cells.get(1).value = data.getAddress().getStreetAddress();
                        row.cells.get(2).value = data.getAddress().getCity();
                        row.cells.get(3).value = data.getAddress().getState();
                        row.data = data;
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
                organizationRemoveButton.enable(true);
            }
        });

        sampleOrganizationTable.addRowDeletedHandler(new RowDeletedHandler() {
            public void onRowDeleted(RowDeletedEvent event) {
                manager.removeOrganizationAt(event.getIndex());
                organizationRemoveButton.enable(false);
            }
        });
        
        organizationRemoveButton = (AppButton)def.getWidget("organizationRemoveButton");
        addScreenHandler(organizationRemoveButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                int selectedRow = sampleOrganizationTable.getSelectedRow();
                if (selectedRow > -1 && sampleOrganizationTable.numRows() > 0) {
                    sampleOrganizationTable.deleteRow(selectedRow);
                }
            }
            public void onStateChange(StateChangeEvent<State> event) {
                organizationRemoveButton.enable(false);
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
        sampleOrganizationTable.finishEditing();
        if (validate()){
            ActionEvent.fire(this, Action.OK, null);
            window.close();
        }
    }
    
    private ArrayList<TableDataRow> getTableModel() {
        ArrayList<TableDataRow> model;
        SampleOrganizationViewDO data;
        TableDataRow row;

        model = new ArrayList<TableDataRow>();
        if (manager == null)
            return model;

        try {
            for (int i = 0; i < manager.count(); i++ ) {
                data = (SampleOrganizationViewDO)manager.getOrganizationAt(i);

                row = new TableDataRow(9);
                row.key = data.getId();
                row.cells.get(0).setValue(data.getTypeId());
                row.cells.get(1).setValue(data.getOrganizationAttention());
                row.cells.get(2).setValue(new TableDataRow(data.getOrganizationId(), data.getOrganizationName()));
                row.cells.get(3).setValue(data.getOrganizationMultipleUnit());
                row.cells.get(4).setValue(data.getOrganizationStreetAddress());
                row.cells.get(5).setValue(data.getOrganizationCity());
                row.cells.get(6).setValue(data.getOrganizationState());
                row.cells.get(7).setValue(data.getOrganizationZipCode());
                row.cells.get(8).setValue(data.getOrganizationCountry());
                
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
        ArrayList<TableDataRow> model;
        
        model = new ArrayList<TableDataRow>();
        model.add(new TableDataRow(null, ""));
        
        try{
            list = CategoryCache.getBySystemName("organization_type");
            for(DictionaryDO resultDO :  list){
                model.add(new TableDataRow(resultDO.getId(),resultDO.getEntry()));
            } 
            ((Dropdown<Integer>)sampleOrganizationTable.getColumns().get(0).getColumnWidget()).setModel(model);
            

            model = new ArrayList<TableDataRow>();
            model.add(new TableDataRow(null, ""));
            list = CategoryCache.getBySystemName("state");
            for (DictionaryDO data : list) {
                if ("Y".equals(data.getIsActive()))
                    model.add(new TableDataRow(data.getEntry(), data.getEntry()));
            }
            ((Dropdown<Integer>)sampleOrganizationTable.getColumns().get(6).getColumnWidget()).setModel(model);
            
            model = new ArrayList<TableDataRow>();
            model.add(new TableDataRow(null, ""));
            list = CategoryCache.getBySystemName("country");
            for (DictionaryDO data : list) {
                if ("Y".equals(data.getIsActive()))
                    model.add(new TableDataRow(data.getEntry(), data.getEntry()));
            }
            ((Dropdown<Integer>)sampleOrganizationTable.getColumns().get(8).getColumnWidget()).setModel(model);
            
            //load the type ids
            reportToId = DictionaryCache.getIdBySystemName("org_report_to");
            billToId = DictionaryCache.getIdBySystemName("org_bill_to");
            secondReportToId = DictionaryCache.getIdBySystemName("org_second_report_to");
        }catch(Exception e){
            Window.alert("initializedropdowns: "+e.getMessage());
        }
    }
    
    public void setManager(SampleOrganizationManager man){
        manager = man;
        
        DataChangeEvent.fire(this);
        
        DeferredCommand.addCommand(new Command() {
            public void execute() {
                if(sampleOrganizationTable.numRows() > 0)
                    sampleOrganizationTable.select(0, 0);
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
                if(DictionaryCache.getIdBySystemName("org_bill_to").equals(orgDO.getTypeId()))
                    numBillTo++;
                
                if(DictionaryCache.getIdBySystemName("org_report_to").equals(orgDO.getTypeId()))
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
        
        if (!superValue)
            window.setError(consts.get("correctErrors"));
        
        return superValue;
    }
    
    public HandlerRegistration addActionHandler(ActionHandler<Action> handler) {
        return addHandler(handler, ActionEvent.getType());
    }
}