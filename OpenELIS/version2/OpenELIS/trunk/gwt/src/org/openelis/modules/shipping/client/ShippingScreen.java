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

import org.openelis.gwt.common.Form;
import org.openelis.gwt.common.data.DataModel;
import org.openelis.gwt.common.data.DataObject;
import org.openelis.gwt.common.data.DataSet;
import org.openelis.gwt.common.data.DropDownField;
import org.openelis.gwt.common.data.FieldType;
import org.openelis.gwt.common.data.IntegerObject;
import org.openelis.gwt.common.data.KeyListManager;
import org.openelis.gwt.common.data.StringObject;
import org.openelis.gwt.event.CommandListener;
import org.openelis.gwt.screen.CommandChain;
import org.openelis.gwt.screen.ScreenDropDownWidget;
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
import com.google.gwt.user.client.ui.SourcesTabEvents;
import com.google.gwt.user.client.ui.TabListener;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class ShippingScreen extends OpenELISScreenForm<ShippingRPC, ShippingForm, Integer> implements ClickListener, TableManager, ChangeListener, TabListener{

    public enum Action {Drawn}
    private Integer shipFromId, shipToId;
    private String shipToText, multUnitText, streetAddressText, cityText, stateText, zipCodeText;
    private AppButton removeRowButton;
    private TextBox shippedToAptSuite, shippedToAddress, shippedToCity, shippedToState, shippedToZipCode;
    private AutoComplete shippedToDropdown;
    private TableWidget itemsTable, trackingNumbersTable;
    private Dropdown status, shippedFrom, shippedMethod;
    private DataModel itemsShippedModel, checkedOrderIds;
    private ShippingDataService data;
    
    private ShippingMetaMap ShippingMeta = new ShippingMetaMap();
    private CommandListener listener;
    private KeyListManager keyList = new KeyListManager();
    
    AsyncCallback<ShippingRPC> checkModels = new AsyncCallback<ShippingRPC>() {
        public void onSuccess(ShippingRPC rpc) {
            if(rpc.status != null) {
                setStatusIdModel(rpc.status);
                rpc.status = null;
            }
            if(rpc.shippedFrom != null) {
                setShippedFromModel(rpc.shippedFrom);
                rpc.shippedFrom = null;
            }
            if(rpc.shippedMethod != null) {
                setShippedMethodModel(rpc.shippedMethod);
                rpc.shippedMethod = null;
            }
        }
        
        public void onFailure(Throwable caught) {
            
        }
    };
    
    public ShippingScreen() {                
        super("org.openelis.modules.shipping.server.ShippingService");
        
        forms.put("display",new ShippingForm());
        
        getScreen(new ShippingRPC());
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
                
                ShippingShipToKey hiddenData = (ShippingShipToKey)selectedRow.getData();
                //load apt/suite
                shippedToAptSuite.setText(hiddenData.aptSuite);
                //load zipcode
                shippedToZipCode.setText(hiddenData.zipCode);
            }else{
                shippedToAddress.setText("");
                shippedToCity.setText("");
                shippedToState.setText("");
                shippedToAptSuite.setText("");
                shippedToZipCode.setText("");
            }
        }   
    }
    
    public void afterDraw(boolean success) {
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
        
        status = (Dropdown)getWidget(ShippingMeta.getStatusId()); 
        shippedFrom = (Dropdown)getWidget(ShippingMeta.getShippedFromId());
        shippedMethod = (Dropdown)getWidget(ShippingMeta.getShippedMethodId());
        
        setStatusIdModel(rpc.status);
        setShippedFromModel(rpc.shippedFrom);
        setShippedMethodModel(rpc.shippedMethod);
        
        /*
         * Null out the rpc models so they are not sent with future rpc calls
         */
        rpc.status = null;
        rpc.shippedFrom = null;
        rpc.shippedMethod = null;
        
        updateChain.add(0,checkModels);
        fetchChain.add(0,checkModels);
        abortChain.add(0,checkModels);
        deleteChain.add(0,checkModels);
        commitUpdateChain.add(0,checkModels);
        commitAddChain.add(0,checkModels);
        
        super.afterDraw(success);
        
        rpc.form.shippingItemsForm.itemsTable.setValue(itemsTable.model.getData());
        rpc.form.shippingItemsForm.trackingNumbersTable.setValue(trackingNumbersTable.model.getData());
        
        if(data != null)
            add();
    }
    
    public void add() {
       super.add();
       
       window.setStatus("","spinnerIcon");
       
       FieldType[] args = new FieldType[0]; 
         
       ShippingRPC srpc = new ShippingRPC();
       srpc.key = rpc.key;
       srpc.form = rpc.form;
       
       
       screenService.call("getAddAutoFillValues", srpc, new AsyncCallback<ShippingRPC>(){
           public void onSuccess(ShippingRPC result){    
               //set the values in the rpc
               rpc.form.statusId.setValue(result.form.statusId.getValue());
               rpc.form.processedDate.setValue(result.form.processedDate.getValue());
               rpc.form.processedBy.setValue(result.form.processedBy.getValue());
               rpc.form.systemUserId = result.form.systemUserId;
               //rpc.form.setFieldValue(ShippingMeta.getProcessedDate(), (DatetimeRPC)((DateField)set.get(1)).getValue());
               //rpc.form.setFieldValue(ShippingMeta.getProcessedById(), (String)((StringField)set.get(2)).getValue());
               //rpc.form.setFieldValue("systemUserId", ((NumberField)set.get(3)).getValue());
                      
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
       
       ((ScreenDropDownWidget)widgets.get(ShippingMeta.getStatusId())).setFocus(true);
    }
    
    public void initScreen(){
        //add();
        
        loadShippingScreenFromData();
        
        //set the values after the screen is in add mode
        rpc.form.shippedFromId.setValue(new DataSet<Integer>(shipFromId));
        
        if(shipToId != null){
            DataModel<Integer> shipToModel = new DataModel<Integer>();
            shipToModel.add(new DataSet<Integer>(shipToId,new StringObject(shipToText)));
            rpc.form.organization.setModel(shipToModel);
            rpc.form.organization.setValue(shipToModel.get(0));
        }
        
        rpc.form.multipleUnit.setValue(multUnitText);
        rpc.form.streetAddress.setValue(streetAddressText);
        rpc.form.city.setValue(cityText);
        rpc.form.state.setValue(stateText);
        rpc.form.zipcode.setValue(zipCodeText);
        loadItemsShippedTableFromModel(itemsShippedModel);
        //loadItemsShippedTableFromTreeModel(itemsShippedModel);

      //  loadScreen(rpc);
        data = null;
    }
    
    public void query() {
        super.query();
        removeRowButton.changeState(AppButton.ButtonState.DISABLED);
        status.setFocus(true);
    }
    
    public void update() {
        super.update();
        ((ScreenDropDownWidget)widgets.get(ShippingMeta.getStatusId())).setFocus(true);
        trackingNumbersTable.model.enableAutoAdd(true);
    }
    
    public void commit() {
        if(state == FormInt.State.ADD){
                ShippingRPC srpc = new ShippingRPC();
                srpc.key = rpc.key;
                srpc.form = rpc.form;
                srpc.checkedOrderIds = checkedOrderIds;
                
                screenService.call("unlockOrderRecords", srpc, new AsyncCallback<ShippingRPC>(){
                    public void onSuccess(ShippingRPC result){
                        trackingNumbersTable.model.enableAutoAdd(false);
                        checkedOrderIds.clear();
                        superCommit();
                        
                    }
                    
                    public void onFailure(Throwable caught){
                        Window.alert(caught.getMessage());
                    }
                });
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
        	ShippingRPC srpc = new ShippingRPC();
            srpc.key = rpc.key;
            srpc.form = rpc.form;
            srpc.checkedOrderIds = checkedOrderIds;
            
            screenService.call("unlockOrderRecords", srpc, new AsyncCallback<ShippingRPC>(){
                public void onSuccess(ShippingRPC result){
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
    
    private void loadItemsShippedTableFromModel(DataModel<Integer> model){
        itemsTable.model.clear();
        for(int i=0; i<model.size(); i++){
            DataSet<Integer> set = model.get(i);
            
            DataSet<Object> tableRow = (DataSet<Object>)itemsTable.model.createRow();
            
            tableRow.get(0).setValue(set.get(0).getValue());
            tableRow.get(1).setValue(((DropDownField)set.get(1)).getTextValue());
            
            tableRow.setData((FieldType)((IntegerObject)set.getData()).clone());
            
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
        
        ShippingItemsRPC sirpc = new ShippingItemsRPC();
        sirpc.key = rpc.key;
        sirpc.form = rpc.form.shippingItemsForm;

        screenService.call("loadShippingItems", sirpc, new AsyncCallback<ShippingItemsRPC>() {
            public void onSuccess(ShippingItemsRPC result) {
                load(result.form);
                rpc.form.fields.put("shippingItems", rpc.form.shippingItemsForm = result.form);

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
        ShippingNotesRPC snrpc = new ShippingNotesRPC();
        snrpc.key = rpc.key;
        snrpc.form = rpc.form.shippingNotesForm;
        
        screenService.call("loadOrderShippingNotes", snrpc, new AsyncCallback<ShippingNotesRPC>() {
            public void onSuccess(ShippingNotesRPC result) {
                load(result.form);
                rpc.form.fields.put("orderShippingNotes", rpc.form.shippingNotesForm = result.form);
                
                window.setStatus("","");
            }

            public void onFailure(Throwable caught) {
                Window.alert(caught.getMessage());
                window.setStatus("","");
            }
        });
    }

    public void setStatusIdModel(DataModel<Integer> statusIdsModel) {
        status.setModel(statusIdsModel);
    }
    
    public void setShippedFromModel(DataModel<Integer> shippedFromsModel) {
        shippedFrom.setModel(shippedFromsModel);
    }
    
    public void setShippedMethodModel(DataModel<Integer> shippedMethodsModel) {
        shippedMethod.setModel(shippedMethodsModel);
    }
}