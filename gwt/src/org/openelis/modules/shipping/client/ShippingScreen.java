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
package org.openelis.modules.shipping.client;

import java.util.ArrayList;

import org.openelis.gwt.common.DatetimeRPC;
import org.openelis.gwt.common.data.Data;
import org.openelis.gwt.common.data.DataMap;
import org.openelis.gwt.common.data.DataModel;
import org.openelis.gwt.common.data.DataSet;
import org.openelis.gwt.common.data.DateField;
import org.openelis.gwt.common.data.DropDownField;
import org.openelis.gwt.common.data.KeyListManager;
import org.openelis.gwt.common.data.NumberField;
import org.openelis.gwt.common.data.NumberObject;
import org.openelis.gwt.common.data.StringField;
import org.openelis.gwt.common.data.StringObject;
import org.openelis.gwt.screen.CommandChain;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.AutoComplete;
import org.openelis.gwt.widget.ButtonPanel;
import org.openelis.gwt.widget.Dropdown;
import org.openelis.gwt.widget.FormInt;
import org.openelis.gwt.widget.table.TableManager;
import org.openelis.gwt.widget.table.TableWidget;
import org.openelis.metamap.ShippingMetaMap;
import org.openelis.modules.main.client.OpenELISScreenForm;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class ShippingScreen extends OpenELISScreenForm implements ClickListener, TableManager, ChangeListener{

    private static boolean loaded = false;
    
    private static DataModel statusDropdownModel, shipFromDropdownModel, shippingMethodDropdownModel;
    private boolean loadedFromAnotherScreen = false;
    private Integer shipFromId, shipToId;
    private String shipToText, multUnitText, streetAddressText, cityText, stateText, zipCodeText;
    private DataModel itemsShippedModel;
    private AppButton removeRowButton;
    private TextBox shippedToAptSuite, shippedToAddress, shippedToCity, shippedToState, shippedToZipCode;
    private AutoComplete shippedToDropdown;
    private TableWidget itemsTable, trackingNumbersTable;
    private Dropdown statusDropdown;
    
    private ShippingMetaMap ShippingMeta = new ShippingMetaMap();
    
    private KeyListManager keyList = new KeyListManager();
    
    public ShippingScreen() {
        super("org.openelis.modules.shipping.server.ShippingService", !loaded);
    }
    
    public ShippingScreen(Integer shipFromId, Integer shipToId, String shipToText, String multUnitText, String streetAddressText, String cityText, String stateText, String zipCodeText, DataModel itemsShippedModel) {
        super("org.openelis.modules.shipping.server.ShippingService", !loaded);
        loadedFromAnotherScreen = true;
        
        this.shipFromId = shipFromId;
        this.shipToId = shipToId;
        this.shipToText = shipToText;   
        this.multUnitText = multUnitText;
        this.streetAddressText = streetAddressText;
        this.cityText = cityText;
        this.stateText = stateText;
        this.zipCodeText = zipCodeText;
        this.itemsShippedModel = itemsShippedModel;
    }
    
    public void onClick(Widget sender) {
        if (sender == removeRowButton)
            onRemoveRowButtonClick();
    }
    
    public void onChange(Widget sender) {
        super.onChange(sender);
        
        if(sender == shippedToDropdown){
            if(shippedToDropdown.getSelections().size() > 0){
                DataSet selectedRow = (DataSet)shippedToDropdown.getSelections().get(0);
                
                //load address
                shippedToAddress.setText((String)((StringObject)selectedRow.get(1)).getValue());
                //load city
                shippedToCity.setText((String)((StringObject)selectedRow.get(2)).getValue());
                //load state
                shippedToState.setText((String)((StringObject)selectedRow.get(3)).getValue());
                
                DataMap map = (DataMap)selectedRow.getData();
                //load apt/suite
                shippedToAptSuite.setText((String)((StringObject)map.get("aptSuite")).getValue());
                //load zipcode
                shippedToZipCode.setText((String)((StringObject)map.get("zipCode")).getValue());
            }            
        }   
    }
    
    public void afterDraw(boolean success) {
        Dropdown drop;
        loaded = true;
        
        //shipped to address fields
        shippedToAptSuite  = (TextBox)getWidget(ShippingMeta.ORGANIZATION_META.ADDRESS.getMultipleUnit());
        shippedToAddress = (TextBox)getWidget(ShippingMeta.ORGANIZATION_META.ADDRESS.getStreetAddress());
        shippedToCity = (TextBox)getWidget(ShippingMeta.ORGANIZATION_META.ADDRESS.getCity());
        shippedToState = (TextBox)getWidget(ShippingMeta.ORGANIZATION_META.ADDRESS.getState());
        shippedToZipCode = (TextBox)getWidget(ShippingMeta.ORGANIZATION_META.ADDRESS.getZipCode());
        
        shippedToDropdown = (AutoComplete)getWidget(ShippingMeta.ORGANIZATION_META.getName());

        removeRowButton = (AppButton)getWidget("removeRowButton");
        
        itemsTable = (TableWidget)getWidget("itemsTable");
        itemsTable.model.enableAutoAdd(false);
        
        trackingNumbersTable = (TableWidget)getWidget("trackingNumbersTable");
        trackingNumbersTable.model.enableAutoAdd(false);

        ButtonPanel bpanel = (ButtonPanel)getWidget("buttons");
        
        CommandChain chain = new CommandChain();
        chain.addCommand(this);
        chain.addCommand(keyList);
        chain.addCommand(bpanel);
        
        statusDropdown = (Dropdown)getWidget(ShippingMeta.getStatusId()); 
        
        if (statusDropdownModel == null) {           
            statusDropdownModel = (DataModel)initData.get("status");
            shipFromDropdownModel = (DataModel)initData.get("shipFrom");
            shippingMethodDropdownModel = (DataModel)initData.get("shippingMethod");
        }
        
        //
        // status dropdown
        //
        drop = (Dropdown)getWidget(ShippingMeta.getStatusId());
        drop.setModel(statusDropdownModel);
        
        //
        // ship from dropdown
        //
        drop = (Dropdown)getWidget(ShippingMeta.getShippedFromId());
        drop.setModel(shipFromDropdownModel);
        
        //
        // shipping method dropdown
        //
        drop = (Dropdown)getWidget(ShippingMeta.getShippedMethodId());
        drop.setModel(shippingMethodDropdownModel);

        super.afterDraw(success);
        
        rpc.setFieldValue("itemsTable", itemsTable.model.getData());
        rpc.setFieldValue("trackingNumbersTable", trackingNumbersTable.model.getData());
        
        if(loadedFromAnotherScreen)
            putInAddAndLoadData();
    }
    
    public void add() {
       super.add();
       
       window.setStatus("","spinnerIcon");
       
       Data[] args = new Data[0]; 
         
       screenService.getObject("getAddAutoFillValues", args, new AsyncCallback<DataModel>(){
           public void onSuccess(DataModel model){    
             // get the datamodel, load the fields in the form
               DataSet set = model.get(0);
               
               //set the values in the rpc
               rpc.setFieldValue(ShippingMeta.getStatusId(), (ArrayList)((DropDownField)set.get(0)).getSelections());
               rpc.setFieldValue(ShippingMeta.getProcessedDate(), (DatetimeRPC)((DateField)set.get(1)).getValue());
               rpc.setFieldValue(ShippingMeta.getProcessedById(), (String)((StringField)set.get(2)).getValue());
               rpc.setFieldValue("systemUserId", (Integer)((NumberField)set.get(3)).getValue());
                            
               loadScreen(rpc);
               
               trackingNumbersTable.model.enableAutoAdd(true);
               
               window.setStatus("","");
           }
           
           public void onFailure(Throwable caught){
               Window.alert(caught.getMessage());
           }
       }); 
       
       statusDropdown.setFocus(true);
    }
    
    private void putInAddAndLoadData(){
        add();
        
        //set the values after the screen is in add mode
        rpc.setFieldValue(ShippingMeta.getShippedFromId(), new DataSet(new NumberObject(shipFromId)));
        
        if(shipToId != null){
            DataModel shipToModel = new DataModel();
            shipToModel.add(new NumberObject(shipToId),new StringObject(shipToText));
            ((DropDownField)rpc.getField(ShippingMeta.ORGANIZATION_META.getName())).setModel(shipToModel);
            rpc.setFieldValue(ShippingMeta.ORGANIZATION_META.getName(), shipToModel.get(0));
        }
        
        rpc.setFieldValue(ShippingMeta.ORGANIZATION_META.ADDRESS.getMultipleUnit(), multUnitText);
        rpc.setFieldValue(ShippingMeta.ORGANIZATION_META.ADDRESS.getStreetAddress(), streetAddressText);
        rpc.setFieldValue(ShippingMeta.ORGANIZATION_META.ADDRESS.getCity(), cityText);
        rpc.setFieldValue(ShippingMeta.ORGANIZATION_META.ADDRESS.getState(), stateText);
        rpc.setFieldValue(ShippingMeta.ORGANIZATION_META.ADDRESS.getZipCode(), zipCodeText);
        loadItemsShippedTableFromModel(itemsShippedModel);

        loadScreen(rpc);
    }
    
    public void query() {
        super.query();
        removeRowButton.changeState(AppButton.ButtonState.DISABLED);
        statusDropdown.setFocus(true);
    }
    
    public void update() {
        super.update();
        statusDropdown.setFocus(true);
        trackingNumbersTable.model.enableAutoAdd(true);
    }
    
    public void abort() {
        trackingNumbersTable.model.enableAutoAdd(false);
        super.abort();  
    }

    protected AsyncCallback afterCommitAdd = new AsyncCallback() {
        public void onSuccess(Object result){
            trackingNumbersTable.model.enableAutoAdd(false);
        }
        
        public void onFailure(Throwable caught){
            
        }
    };
    
    protected AsyncCallback afterCommitUpdate = new AsyncCallback() {
        public void onSuccess(Object result){
            trackingNumbersTable.model.enableAutoAdd(false);            
        }
        
        public void onFailure(Throwable caught){
            
        }
    };
    
    //
    //start table manager methods
    //
    public boolean canAdd(TableWidget widget, DataSet set, int row) {
        return false;
    }

    public boolean canAutoAdd(TableWidget widget, DataSet addRow) {
        return addRow.get(0).getValue() != null && !addRow.get(0).getValue().equals(0);
    }

    public boolean canDelete(TableWidget widget, DataSet set, int row) {
        return false;
    }

    public boolean canEdit(TableWidget widget, DataSet set, int row, int col) {
        return false;
    }

    public boolean canSelect(TableWidget widget, DataSet set, int row) {
        if(state == FormInt.State.ADD || state == FormInt.State.UPDATE)           
            return true;
        return false;
    }
    //
    //end table manager methods
    //
    
    private void loadItemsShippedTableFromModel(DataModel model){
        itemsTable.model.clear();
        for(int i=0; i<model.size(); i++){
            DataSet set = model.get(i);
            
            DataSet tableRow = new DataSet();     
            
            tableRow.add((StringField)set.get(0));
            
            DataMap map = new DataMap();
            map.put("referenceId", (NumberField)set.getKey());
            map.put("referenceTableId", (NumberField)set.get(3));
            
            tableRow.setData(map);
            
            itemsTable.model.addRow(tableRow);
        }
    }
    
    private void onRemoveRowButtonClick() {
        int selectedRow = trackingNumbersTable.model.getSelectedIndex();
        
        if (selectedRow > -1 && trackingNumbersTable.model.numRows() > 0) 
            trackingNumbersTable.model.deleteRow(selectedRow);
        
    }    
}
