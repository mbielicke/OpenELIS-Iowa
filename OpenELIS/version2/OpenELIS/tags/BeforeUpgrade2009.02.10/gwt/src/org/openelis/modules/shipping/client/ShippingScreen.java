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

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.SourcesTabEvents;
import com.google.gwt.user.client.ui.TabListener;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

import org.openelis.gwt.common.DatetimeRPC;
import org.openelis.gwt.common.Form;
import org.openelis.gwt.common.RPC;
import org.openelis.gwt.common.data.Data;
import org.openelis.gwt.common.data.DataMap;
import org.openelis.gwt.common.data.DataModel;
import org.openelis.gwt.common.data.DataObject;
import org.openelis.gwt.common.data.DataSet;
import org.openelis.gwt.common.data.DateField;
import org.openelis.gwt.common.data.DropDownField;
import org.openelis.gwt.common.data.KeyListManager;
import org.openelis.gwt.common.data.NumberField;
import org.openelis.gwt.common.data.NumberObject;
import org.openelis.gwt.common.data.StringField;
import org.openelis.gwt.common.data.StringObject;
import org.openelis.gwt.event.CommandListener;
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

import java.util.ArrayList;

public class ShippingScreen extends OpenELISScreenForm<RPC<Form,Data>,Form> implements ClickListener, TableManager, ChangeListener, TabListener{

    private static boolean loaded = false;
    public enum Action {Drawn}
    private static DataModel statusDropdownModel, shipFromDropdownModel, shippingMethodDropdownModel;
    private Integer shipFromId, shipToId;
    private String shipToText, multUnitText, streetAddressText, cityText, stateText, zipCodeText;
    private AppButton removeRowButton;
    private TextBox shippedToAptSuite, shippedToAddress, shippedToCity, shippedToState, shippedToZipCode;
    private AutoComplete shippedToDropdown;
    private TableWidget itemsTable, trackingNumbersTable;
    private Dropdown statusDropdown;
    private DataModel itemsShippedModel, checkedOrderIds;
    private ShippingDataService data;
    
    private ShippingMetaMap ShippingMeta = new ShippingMetaMap();
    private CommandListener listener;
    private KeyListManager keyList = new KeyListManager();
    
    public ShippingScreen() {
        super("org.openelis.modules.shipping.server.ShippingService", !loaded, new RPC<Form,Data>());
    }
    
    public void setShippingData(ShippingDataService data){
        this.data = data;
    }
    
    public void loadShippingScreenFromData(){
        this.shipFromId = data.getShipFromId();
        this.shipToId = data.getShipToId();
        this.shipToText = data.getShipToText();   
        this.multUnitText = data.getMultUnitText();
        this.streetAddressText = data.getStreetAddressText();
        this.cityText = data.getCityText();
        this.stateText = data.getStateText();
        this.zipCodeText = data.getZipCodeText();
        this.itemsShippedModel = data.getItemsShippedModel();
        
        //this is used to lock and unlock the records
        this.checkedOrderIds = data.getCheckedOrderIds();
    }
    
    public void clearShippingData(){
        this.shipFromId = null;
        this.shipToId = null;
        this.shipToText = null;   
        this.multUnitText = null;
        this.streetAddressText = null;
        this.cityText = null;
        this.stateText = null;
        this.zipCodeText = null;
        this.itemsShippedModel = null;
        
        //this is used to lock and unlock the records
        this.checkedOrderIds = null;
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
        
        ((Form)rpc.form.getField("shippingItems")).setFieldValue("itemsTable", itemsTable.model.getData());
        ((Form)rpc.form.getField("shippingItems")).setFieldValue("trackingNumbersTable", trackingNumbersTable.model.getData());
        
        if(data != null)
            add();
    }
    
    public void add() {
       super.add();
       
       window.setStatus("","spinnerIcon");
       
       Data[] args = new Data[0]; 
         
       screenService.getObject("getAddAutoFillValues", args, new AsyncCallback<DataModel<DataSet>>(){
           public void onSuccess(DataModel<DataSet> model){    
             // get the datamodel, load the fields in the form
               DataSet set = model.get(0);
               
               //set the values in the rpc
               rpc.form.setFieldValue(ShippingMeta.getStatusId(), (ArrayList)((DropDownField)set.get(0)).getSelections());
               rpc.form.setFieldValue(ShippingMeta.getProcessedDate(), (DatetimeRPC)((DateField)set.get(1)).getValue());
               rpc.form.setFieldValue(ShippingMeta.getProcessedById(), (String)((StringField)set.get(2)).getValue());
               rpc.form.setFieldValue("systemUserId", (Integer)((NumberField)set.get(3)).getValue());
                      
               if(data != null)
                   initScreen();
               
               loadScreen(rpc.form);
               
               trackingNumbersTable.model.enableAutoAdd(true);
               
               window.setStatus("","");
           }
           
           public void onFailure(Throwable caught){
               Window.alert(caught.getMessage());
           }
       }); 
       
       statusDropdown.setFocus(true);
    }
    
    public void initScreen(){
        //add();
        
        loadShippingScreenFromData();
        
        //set the values after the screen is in add mode
        rpc.form.setFieldValue(ShippingMeta.getShippedFromId(), new DataSet(new NumberObject(shipFromId)));
        
        if(shipToId != null){
            DataModel shipToModel = new DataModel();
            shipToModel.add(new NumberObject(shipToId),new StringObject(shipToText));
            ((DropDownField)rpc.form.getField(ShippingMeta.ORGANIZATION_META.getName())).setModel(shipToModel);
            rpc.form.setFieldValue(ShippingMeta.ORGANIZATION_META.getName(), shipToModel.get(0));
        }
        
        rpc.form.setFieldValue(ShippingMeta.ORGANIZATION_META.ADDRESS.getMultipleUnit(), multUnitText);
        rpc.form.setFieldValue(ShippingMeta.ORGANIZATION_META.ADDRESS.getStreetAddress(), streetAddressText);
        rpc.form.setFieldValue(ShippingMeta.ORGANIZATION_META.ADDRESS.getCity(), cityText);
        rpc.form.setFieldValue(ShippingMeta.ORGANIZATION_META.ADDRESS.getState(), stateText);
        rpc.form.setFieldValue(ShippingMeta.ORGANIZATION_META.ADDRESS.getZipCode(), zipCodeText);
        loadItemsShippedTableFromModel(itemsShippedModel);
        //loadItemsShippedTableFromTreeModel(itemsShippedModel);

      //  loadScreen(rpc);
        data = null;
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
    
    public void commit() {
        if(state == FormInt.State.ADD){
            /*if(checkedOrderIds != null && checkedOrderIds.size() > 0){
                DataModel model = new DataModel();
                for(int i=0; i<checkedOrderIds.size(); i++){
                    DataSet set = new DataSet();
                    set.setKey(new NumberField((Integer)checkedOrderIds.get(i)));
                    model.add(set);
                }
              */      
                //rpc.setFieldValue("unlockModel", checkedOrderIds);    
                
                Data[] args = new Data[] {checkedOrderIds}; 
                
                screenService.getObject("unlockOrderRecords", args, new AsyncCallback<StringObject>(){
                    public void onSuccess(StringObject obj){
                        trackingNumbersTable.model.enableAutoAdd(false);
                        checkedOrderIds.clear();
                        superCommit();
                        
                    }
                    
                    public void onFailure(Throwable caught){
                        Window.alert(caught.getMessage());
                    }
                });
           // }
        }

        trackingNumbersTable.model.enableAutoAdd(false);
        super.commit();
    }
    
    protected void superCommit(){
        super.commit();
        clearShippingData();
    }
    
    public void abort() {
        if(state == FormInt.State.ADD && checkedOrderIds != null){
            //we need to unlock the order records
            
          /*  DataModel model = new DataModel();
            if(checkedOrderIds != null && checkedOrderIds.size() > 0){
                for(int i=0; i<checkedOrderIds.size(); i++){
                    DataSet set = new DataSet();
                    set.setKey(new NumberField((Integer)checkedOrderIds.get(i).ge));
                    model.add(set);
               }
            }
            */
             // prepare the argument list for the getObject function
            Data[] args = new Data[] {checkedOrderIds}; 
            
            screenService.getObject("unlockOrderRecords", args, new AsyncCallback<StringObject>(){
                public void onSuccess(StringObject obj){
                    trackingNumbersTable.model.enableAutoAdd(false);
                    superAbort();    
                    
                }
                
                public void onFailure(Throwable caught){
                    Window.alert(caught.getMessage());
                }
            });
        }else{
            trackingNumbersTable.model.enableAutoAdd(false);
            super.abort();
        }
    }
    
    protected void superAbort(){
        super.abort();
        clearShippingData();
    }
    
    //
    //start table manager methods
    //
    public boolean canAdd(TableWidget widget, DataSet set, int row) {
        return false;
    }

    public boolean canAutoAdd(TableWidget widget, DataSet addRow) {
        return ((DataObject)addRow.get(0)).getValue() != null && !((DataObject)addRow.get(0)).getValue().equals(0);
    }

    public boolean canDelete(TableWidget widget, DataSet set, int row) {
        return false;
    }

    public boolean canEdit(TableWidget widget, DataSet set, int row, int col) {
        return true;
    }

    public boolean canSelect(TableWidget widget, DataSet set, int row) {
        if(state == FormInt.State.ADD || state == FormInt.State.UPDATE)           
            return true;
        return false;
    }
    
    public boolean canDrag(TableWidget widget, DataSet item, int row) {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean canDrop(TableWidget widget, Widget dragWidget, DataSet dropTarget, int targetRow) {
        // TODO Auto-generated method stub
        return false;
    }

    public void drop(TableWidget widget, Widget dragWidget, DataSet dropTarget, int targetRow) {
        // TODO Auto-generated method stub
        
    } 
    
    public void drop(TableWidget widget, Widget dragWidget) {
        // TODO Auto-generated method stub
        
    }
    //
    //end table manager methods
    //
    
    private void loadItemsShippedTableFromModel(DataModel<DataSet> model){
        itemsTable.model.clear();
        for(int i=0; i<model.size(); i++){
            DataSet<Data> set = model.get(i);
            
            DataSet<Data> tableRow = itemsTable.model.createRow();
            
            tableRow.get(0).setValue(set.get(0).getValue());
            tableRow.get(1).setValue(((DropDownField)set.get(1)).getTextValue());
            
            tableRow.setData((DataMap)set.getData().clone());
            
            itemsTable.model.addRow(tableRow);
        }
        
        if(model.size() > 0)
            itemsTable.model.refresh();
    }
    
    /*private void loadItemsShippedTableFromTreeModel(TreeDataModel model){
        for(int i=0; i<model.size(); i++){
            DataSet tableRow;
            TreeDataItem row = model.get(i);
            
            if(row.getItems().size() > 0){
                for(int j=0; j<row.getItems().size(); j++){
                    tableRow = itemsTable.model.createRow();
                    TreeDataItem childRow = row.getItems().get(j);
                    
                    tableRow.get(0).setValue(childRow.get(0).getValue());
                    tableRow.get(1).setValue(childRow.get(1).getValue());
                    
                    tableRow.setData((DataMap)childRow.getData().clone());
                    
                    itemsTable.model.addRow(tableRow);
                }
            }else{
                tableRow = itemsTable.model.createRow();
                tableRow.get(0).setValue(row.get(0).getValue());
                tableRow.get(1).setValue(row.get(1).getValue());
                
                tableRow.setData((DataMap)row.getData().clone());
                
                itemsTable.model.addRow(tableRow);
            }
        }
        if(model.size() > 0)
            itemsTable.model.refresh();
    }*/
    
    private void onRemoveRowButtonClick() {
        int selectedRow = trackingNumbersTable.model.getSelectedIndex();
        
        if (selectedRow > -1 && trackingNumbersTable.model.numRows() > 0) 
            trackingNumbersTable.model.deleteRow(selectedRow);
        
    }

    public boolean onBeforeTabSelected(SourcesTabEvents sender, int tabIndex) {
        if(state != FormInt.State.QUERY){
            if (tabIndex == 0 && !((Form)rpc.form.getField("shippingItems")).load)
                fillShippingItems();
                
            else if (tabIndex == 1 && !((Form)rpc.form.getField("orderShippingNotes")).load) 
                fillOrderShippingNotes();
              
        }
        return true;
    }

    public void onTabSelected(SourcesTabEvents sender, int tabIndex) { }
    
    private void fillShippingItems() {
        if(key == null)
            return;
        
        window.setStatus("","spinnerIcon");

        // prepare the argument list for the getObject function
        Data[] args = new Data[] {key, rpc.form.getField("shippingItems")};

        screenService.getObject("loadShippingItems", args, new AsyncCallback<Form>() {
            public void onSuccess(Form result) {
                load(result);
                rpc.form.setField("shippingItems", result);
                window.setStatus("","");
            }

            public void onFailure(Throwable caught) {
                Window.alert(caught.getMessage());
                window.setStatus("","");
            }
        });
    }
    
    private void fillOrderShippingNotes() {
        if(key == null)
            return;
        
        window.setStatus("","spinnerIcon");

        // prepare the argument list for the getObject function
        Data[] args = new Data[] {key, rpc.form.getField("orderShippingNotes")};

        screenService.getObject("loadOrderShippingNotes", args, new AsyncCallback<Form>() {
            public void onSuccess(Form result) {
                load(result);
                rpc.form.setField("orderShippingNotes", result);
                window.setStatus("","");
            }

            public void onFailure(Throwable caught) {
                Window.alert(caught.getMessage());
                window.setStatus("","");
            }
        });
    }
}