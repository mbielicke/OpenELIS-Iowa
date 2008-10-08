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

import org.openelis.gwt.common.DatetimeRPC;
import org.openelis.gwt.common.data.DataModel;
import org.openelis.gwt.common.data.DataObject;
import org.openelis.gwt.common.data.DataSet;
import org.openelis.gwt.common.data.DateField;
import org.openelis.gwt.common.data.DropDownField;
import org.openelis.gwt.common.data.KeyListManager;
import org.openelis.gwt.common.data.ModelObject;
import org.openelis.gwt.common.data.NumberField;
import org.openelis.gwt.common.data.NumberObject;
import org.openelis.gwt.common.data.StringField;
import org.openelis.gwt.common.data.StringObject;
import org.openelis.gwt.common.data.TableRow;
import org.openelis.gwt.screen.CommandChain;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.AutoCompleteDropdown;
import org.openelis.gwt.widget.ButtonPanel;
import org.openelis.gwt.widget.FormInt;
import org.openelis.gwt.widget.table.EditTable;
import org.openelis.gwt.widget.table.TableController;
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
    private AutoCompleteDropdown shippedToDropdown;
    
    private AutoCompleteDropdown statusDropdown;
    
    private ShippingMetaMap ShippingMeta = new ShippingMetaMap();
    private EditTable itemsController, trackingNumbersController;
    
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
            if(shippedToDropdown.getSelected().size() > 0){
                DataSet selectedRow = (DataSet)shippedToDropdown.getSelected().get(0);
                
                //load address
                shippedToAddress.setText((String)((StringObject)selectedRow.getObject(1)).getValue());
                //load city
                shippedToCity.setText((String)((StringObject)selectedRow.getObject(2)).getValue());
                //load state
                shippedToState.setText((String)((StringObject)selectedRow.getObject(3)).getValue());               
                //load apt/suite
                shippedToAptSuite.setText((String)((StringObject)selectedRow.getObject(4)).getValue());
                //load zipcode
                shippedToZipCode.setText((String)((StringObject)selectedRow.getObject(5)).getValue());
            }            
        }   
    }
    
    public void afterDraw(boolean success) {
        AutoCompleteDropdown drop;
        loaded = true;
        
        //shipped to address fields
        shippedToAptSuite  = (TextBox)getWidget(ShippingMeta.ORGANIZATION_META.ADDRESS.getMultipleUnit());
        shippedToAddress = (TextBox)getWidget(ShippingMeta.ORGANIZATION_META.ADDRESS.getStreetAddress());
        shippedToCity = (TextBox)getWidget(ShippingMeta.ORGANIZATION_META.ADDRESS.getCity());
        shippedToState = (TextBox)getWidget(ShippingMeta.ORGANIZATION_META.ADDRESS.getState());
        shippedToZipCode = (TextBox)getWidget(ShippingMeta.ORGANIZATION_META.ADDRESS.getZipCode());
        
        shippedToDropdown = (AutoCompleteDropdown)getWidget(ShippingMeta.ORGANIZATION_META.getName());

        removeRowButton = (AppButton)getWidget("removeRowButton");
        
        itemsController = ((TableWidget)getWidget("itemsTable")).controller;
        itemsController.setAutoAdd(false);

        trackingNumbersController = ((TableWidget)getWidget("trackingNumbersTable")).controller;
        addCommandListener(trackingNumbersController);

        ButtonPanel bpanel = (ButtonPanel)getWidget("buttons");
        
        CommandChain chain = new CommandChain();
        chain.addCommand(this);
        chain.addCommand(keyList);
        chain.addCommand(bpanel);
        
        statusDropdown = (AutoCompleteDropdown)getWidget(ShippingMeta.getStatusId()); 
        
        if (statusDropdownModel == null) {           
            statusDropdownModel = (DataModel)initData.get("status");
            shipFromDropdownModel = (DataModel)initData.get("shipFrom");
            shippingMethodDropdownModel = (DataModel)initData.get("shippingMethod");
        }
        
        //
        // status dropdown
        //
        drop = (AutoCompleteDropdown)getWidget(ShippingMeta.getStatusId());
        drop.setModel(statusDropdownModel);
        
        //
        // ship from dropdown
        //
        drop = (AutoCompleteDropdown)getWidget(ShippingMeta.getShippedFromId());
        drop.setModel(shipFromDropdownModel);
        
        //
        // shipping method dropdown
        //
        drop = (AutoCompleteDropdown)getWidget(ShippingMeta.getShippedMethodId());
        drop.setModel(shippingMethodDropdownModel);

        super.afterDraw(success);
        
        if(loadedFromAnotherScreen)
            putInAddAndLoadData();
    }
    
    public void add() {
       super.add();
       
       window.setStatus("","spinnerIcon");
       
       DataObject[] args = new DataObject[0]; 
         
       screenService.getObject("getAddAutoFillValues", args, new AsyncCallback(){
           public void onSuccess(Object result){    
             // get the datamodel, load the fields in the form
               DataModel model = (DataModel) ((ModelObject)result).getValue();
               DataSet set = model.get(0);
               
               //set the values in the rpc
               rpc.setFieldValue(ShippingMeta.getStatusId(), (Integer)((DropDownField)set.getObject(0)).getValue());
               rpc.setFieldValue(ShippingMeta.getProcessedDate(), (DatetimeRPC)((DateField)set.getObject(1)).getValue());
               rpc.setFieldValue(ShippingMeta.getProcessedById(), (String)((StringField)set.getObject(2)).getValue());
               rpc.setFieldValue("systemUserId", (Integer)((NumberField)set.getObject(3)).getValue());
                            
               loadScreen(rpc);
               
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
        rpc.setFieldValue(ShippingMeta.getShippedFromId(), shipFromId);
        
        if(shipToId != null){
            DataSet shipToSet = new DataSet();
            NumberObject id = new NumberObject(NumberObject.Type.INTEGER);
            StringObject text = new StringObject();
            id.setValue(shipToId);
            text.setValue(shipToText);
            shipToSet.setKey(id);
            shipToSet.addObject(text);
            rpc.setFieldValue(ShippingMeta.ORGANIZATION_META.getName(), shipToSet);
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
    }
    
    public void abort() {
        itemsController.setAutoAdd(false);
        trackingNumbersController.setAutoAdd(true);
        super.abort();  
    }

    protected AsyncCallback afterCommitAdd = new AsyncCallback() {
        public void onSuccess(Object result){
            itemsController.setAutoAdd(false);
            trackingNumbersController.setAutoAdd(true);
            
            //we need to do this reset to get rid of the last row
            itemsController.reset();
            trackingNumbersController.reset();
        }
        
        public void onFailure(Throwable caught){
            
        }
    };
    
    protected AsyncCallback afterCommitUpdate = new AsyncCallback() {
        public void onSuccess(Object result){
            itemsController.setAutoAdd(false);
            trackingNumbersController.setAutoAdd(true);
            
            //we need to do this reset to get rid of the last row
            itemsController.reset();
            trackingNumbersController.reset();
        }
        
        public void onFailure(Throwable caught){
            
        }
    };
    
    //
    //start table manager methods
    //
    public boolean canSelect(int row, TableController controller) {        
        if(state == FormInt.State.ADD || state == FormInt.State.UPDATE)           
            return true;
        return false;
    }

    public boolean canEdit(int row, int col, TableController controller) {
       return true;
    }

    public boolean canDelete(int row, TableController controller) {
        return true;
    }

    public boolean action(int row, int col, TableController controller) {  
        return false;
    }

    public boolean canInsert(int row, TableController controller) {
        return false;     
    }

    public void finishedEditing(int row, int col, TableController controller) {}

    public boolean doAutoAdd(TableRow autoAddRow, TableController controller) {
        return autoAddRow.getColumn(0).getValue() != null && !autoAddRow.getColumn(0).getValue().equals(0);
    }

    public void rowAdded(int row, TableController controller) {}

    public void getNextPage(TableController controller) {}

    public void getPage(int page) {}

    public void getPreviousPage(TableController controller) {}

    public void setModel(TableController controller, DataModel model) {}

    public void validateRow(int row, TableController controller) {}

    public void setMultiple(int row, int col, TableController controller) {}
    //
    //end table manager methods
    //
    
    private void loadItemsShippedTableFromModel(DataModel model){
        itemsController.model.reset();
        for(int i=0; i<model.size(); i++){
            DataSet set = model.get(i);
            
            TableRow tableRow = new TableRow();     
            
            tableRow.addColumn((StringField)set.getObject(0));
            tableRow.addHidden("referenceTableId", (NumberField)set.getObject(3));
            tableRow.addHidden("referenceId", (NumberField)set.getKey());
            
            ((EditTable)itemsController).addRow(tableRow);
        }
    }
    
    private void onRemoveRowButtonClick() {
        int selectedRow = trackingNumbersController.selected;
        if (selectedRow > -1 && trackingNumbersController.model.numRows() > 0) {
            TableRow row = trackingNumbersController.model.getRow(selectedRow);
            trackingNumbersController.model.hideRow(row);
            
            // reset the model
            trackingNumbersController.reset();
            // need to set the deleted flag to "Y" also
            StringField deleteFlag = new StringField();
            deleteFlag.setValue("Y");

            row.addHidden("deleteFlag", deleteFlag);
        }
    }
    
}
