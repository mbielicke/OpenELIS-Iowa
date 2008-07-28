/**
* The contents of this file are subject to the Mozilla Public License
* Version 1.1 (the "License"); you may not use this file except in
* compliance with the License. You may obtain a copy of the License at
* http://www.mozilla.org/MPL/
* 
* Software distributed under the License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
* License for the specific language governing rights and limitations under
* the License.
* 
* The Original Code is OpenELIS code.
* 
* Copyright (C) The University of Iowa.  All Rights Reserved.
*/
package org.openelis.modules.inventoryReceipt.client;

import java.util.ArrayList;

import org.openelis.gwt.common.DatetimeRPC;
import org.openelis.gwt.common.data.CheckField;
import org.openelis.gwt.common.data.DataModel;
import org.openelis.gwt.common.data.DataObject;
import org.openelis.gwt.common.data.DataSet;
import org.openelis.gwt.common.data.DateField;
import org.openelis.gwt.common.data.DropDownField;
import org.openelis.gwt.common.data.ModelObject;
import org.openelis.gwt.common.data.NumberField;
import org.openelis.gwt.common.data.NumberObject;
import org.openelis.gwt.common.data.StringField;
import org.openelis.gwt.common.data.StringObject;
import org.openelis.gwt.common.data.TableRow;
import org.openelis.gwt.screen.ScreenAutoDropdown;
import org.openelis.gwt.screen.ScreenCalendar;
import org.openelis.gwt.screen.ScreenCheck;
import org.openelis.gwt.screen.ScreenTextBox;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.AutoCompleteDropdown;
import org.openelis.gwt.widget.ButtonPanel;
import org.openelis.gwt.widget.CheckBox;
import org.openelis.gwt.widget.FormCalendarWidget;
import org.openelis.gwt.widget.table.EditTable;
import org.openelis.gwt.widget.table.TableAutoDropdown;
import org.openelis.gwt.widget.table.TableController;
import org.openelis.gwt.widget.table.TableManager;
import org.openelis.gwt.widget.table.TableWidget;
import org.openelis.metamap.InventoryReceiptMetaMap;
import org.openelis.modules.main.client.OpenELISScreenForm;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.SourcesTableEvents;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class InventoryReceiptScreen extends OpenELISScreenForm implements ClickListener, ChangeListener, TableManager {
    
    private EditTable        receiptsController;
    private boolean doAutoAdd = true;
    private boolean startedLoadingTable = false;
    
    private AppButton        removeReceiptButton;
    private TextBox orgAptSuiteText, orgAddressText, orgCityText, orgStateText, orgZipCodeText,
                    itemDescText, itemStoreText, itemPurUnitsText;
    ScreenTextBox itemLotNum;
    private ScreenAutoDropdown itemLocation;
    private ScreenCalendar itemExpDate;
    private ScreenCheck addToExisiting;
    
    private InventoryReceiptMetaMap InventoryReceiptMeta = new InventoryReceiptMetaMap();
    
    public InventoryReceiptScreen() {
        super("org.openelis.modules.inventoryReceipt.server.InventoryReceiptService",false);
    }

    public void onClick(Widget sender) {
        if (sender == removeReceiptButton)
            onRemoveReceiptRowButtonClick();
        else if(sender == addToExisiting && addToExisiting.isEnabled() && receiptsController.model.numRows() > 0){
            CheckField existing = new CheckField();
            if(((CheckBox)addToExisiting.getWidget()).getState() == CheckBox.CHECKED)
                existing.setValue(CheckBox.UNCHECKED);
            else
                existing.setValue(CheckBox.CHECKED);
        
            TableRow tableRow = receiptsController.model.getRow(receiptsController.selected);
            tableRow.addHidden("addToExisting", existing);  
            ((AutoCompleteDropdown)itemLocation.getWidget()).reset();
        }
        
    }
    
    public void onChange(Widget sender) {
        super.onChange(sender);
         
        if(receiptsController.model.numRows() > 0){
            if(sender == itemLocation.getWidget()){
                DropDownField loc = new DropDownField();
                loc.setValue(((AutoCompleteDropdown)itemLocation.getWidget()).getSelected());
                TableRow tableRow = receiptsController.model.getRow(receiptsController.selected);
                tableRow.addHidden(InventoryReceiptMeta.TRANS_RECEIPT_LOCATION_META.INVENTORY_LOCATION_META.getStorageLocationId(), loc);
            }else if(sender == itemLotNum.getWidget()){
                StringField lotNum = new StringField();
                lotNum.setValue(((TextBox)itemLotNum.getWidget()).getText());
                TableRow tableRow = receiptsController.model.getRow(receiptsController.selected);
                tableRow.addHidden(InventoryReceiptMeta.TRANS_RECEIPT_LOCATION_META.INVENTORY_LOCATION_META.getLotNumber(), lotNum);
            }else if(sender == itemExpDate.getWidget()){
                DateField expDate = new DateField();
                expDate.setValue(((FormCalendarWidget)itemExpDate.getWidget()).getValue());
                TableRow tableRow = receiptsController.model.getRow(receiptsController.selected);
                tableRow.addHidden(InventoryReceiptMeta.TRANS_RECEIPT_LOCATION_META.INVENTORY_LOCATION_META.getExpirationDate(), expDate);
            }
        }
        
    }
    
    public void afterDraw(boolean sucess) {
        orgAptSuiteText = (TextBox)getWidget(InventoryReceiptMeta.ORGANIZATION_META.ADDRESS.getMultipleUnit());        
        orgAddressText = (TextBox)getWidget(InventoryReceiptMeta.ORGANIZATION_META.ADDRESS.getStreetAddress());
        orgCityText = (TextBox)getWidget(InventoryReceiptMeta.ORGANIZATION_META.ADDRESS.getCity());
        orgStateText = (TextBox)getWidget(InventoryReceiptMeta.ORGANIZATION_META.ADDRESS.getState());
        orgZipCodeText = (TextBox)getWidget(InventoryReceiptMeta.ORGANIZATION_META.ADDRESS.getZipCode());
        itemDescText = (TextBox)getWidget(InventoryReceiptMeta.INVENTORY_ITEM_META.getDescription());
        itemStoreText = (TextBox)getWidget(InventoryReceiptMeta.INVENTORY_ITEM_META.getStoreId());
        itemPurUnitsText = (TextBox)getWidget(InventoryReceiptMeta.INVENTORY_ITEM_META.getPurchasedUnitsId());
        itemLocation = (ScreenAutoDropdown)widgets.get(InventoryReceiptMeta.TRANS_RECEIPT_LOCATION_META.INVENTORY_LOCATION_META.getStorageLocationId());
        itemLotNum = (ScreenTextBox)widgets.get(InventoryReceiptMeta.TRANS_RECEIPT_LOCATION_META.INVENTORY_LOCATION_META.getLotNumber());
        itemExpDate = (ScreenCalendar)widgets.get(InventoryReceiptMeta.TRANS_RECEIPT_LOCATION_META.INVENTORY_LOCATION_META.getExpirationDate());
        addToExisiting = (ScreenCheck)widgets.get("addToExisting");
        removeReceiptButton = (AppButton)getWidget("removeReceiptButton");
        
        //
        // disable auto add and make sure there are no rows in the table
        //
        receiptsController = ((TableWidget)getWidget("receiptsTable")).controller;
        receiptsController.setAutoAdd(false);
        
        setBpanel((ButtonPanel)getWidget("buttons"));
        super.afterDraw(sucess);
    }
    
    public void add() {
        //
        // make sure the contact table gets set before the main add
        //
        receiptsController.setAutoAdd(true);
        super.add();
        
        itemLotNum.enable(false);
        itemLocation.enable(false);
        itemExpDate.enable(false);
        addToExisiting.enable(false);
        
        receiptsController.onCellClicked((SourcesTableEvents)receiptsController.view.table, 0, 0);
       
    }
    
    public void fetch() {
        //we dont need to call the super because we arent using the datamodel for lookups
        //we are using it to fill the table
        //super.fetch();
        
        DataModel model = modelWidget.getModel();
        
        receiptsController.model.reset();
        receiptsController.reset();
        
        loadReceiptsTableFromModel(model);
               
        if(model.size() > 0)
            changeState(State.DISPLAY);
       
    }
    
    public void query() {
        super.query();
        
        addToExisiting.enable(false);
        itemExpDate.enable(false);
        itemLotNum.enable(false);
        itemLocation.enable(false);
        removeReceiptButton.changeState(AppButton.ButtonState.DISABLED);
    }
    
    public void update() {
        doReset();
        
        window.setStatus(consts.get("lockForUpdate"),"spinnerIcon");
        
        ModelObject modelObj = new ModelObject();
        modelObj.setValue(modelWidget.getModel());
        
        // prepare the argument list for the getObject function
        DataObject[] args = new DataObject[] {modelObj}; 
        
        screenService.getObject("commitQueryAndLock", args, new AsyncCallback(){
            public void onSuccess(Object result){                    
                DataModel model = (DataModel)((ModelObject)result).getValue();
                
                modelWidget.setModel(model);
                modelWidget.select(0);
                
                afterUpdate(true);
            }
            
            public void onFailure(Throwable caught){
                Window.alert(caught.getMessage());
                afterUpdate(true);
            }
        });
    }
    
    public void abort() {
        receiptsController.setAutoAdd(false);
        
        if (state == State.UPDATE) {
            window.setStatus("","spinnerIcon");
            clearErrors();
            doReset();
            enable(false);
            
            ModelObject modelObj = new ModelObject();
            modelObj.setValue(modelWidget.getModel());
            
            // prepare the argument list for the getObject function
            DataObject[] args = new DataObject[] {modelObj}; 
            
            screenService.getObject("commitQueryAndUnlock", args, new AsyncCallback(){
                public void onSuccess(Object result){                    
                    DataModel model = (DataModel)((ModelObject)result).getValue();
                    
                    modelWidget.setModel(model);
                    modelWidget.select(0);

                    window.setStatus("","");
                    changeState(State.DISPLAY);
                }
                
                public void onFailure(Throwable caught){
                    Window.alert(caught.getMessage());
                    window.setStatus("","");
               }
            });
            
        }else{
            super.abort();
        }
    }
    
    public void commit() {
        super.commit();
        
        if (state == State.ADD || state == State.UPDATE) {
            if (!rpc.validate() || !validate())
                receiptsController.onCellClicked((SourcesTableEvents)receiptsController.view.table, 0, 1);
        }
    }
    public void afterCommitAdd(boolean success) {
        receiptsController.setAutoAdd(false);
 
        super.afterCommitAdd(success);
    }
    
    public void afterCommitUpdate(boolean success) {
        receiptsController.setAutoAdd(false);
        //we need to do this reset to get rid of the last row
        receiptsController.reset();
        
        super.afterCommitUpdate(success);
    }

    //start table manager methods
    public boolean action(int row, int col, TableController controller) {
        TableRow tableRow=null;
        
        if(!addToExisiting.isEnabled() && receiptsController.model.numRows() > 0 && (state == State.ADD || state == State.UPDATE)){
            itemLotNum.enable(true);
            itemLocation.enable(true);
            itemExpDate.enable(true);
            addToExisiting.enable(true);
        }
        
        if(row >=0 && controller.model.numRows() > row)
            tableRow = controller.model.getRow(row);
        
        if(tableRow != null){
            if(tableRow.getHidden("streetAddress") != null && tableRow.getHidden("city") != null){
                orgAptSuiteText.setText((String)((StringField)tableRow.getHidden("multUnit")).getValue());
                orgAddressText.setText((String)((StringField)tableRow.getHidden("streetAddress")).getValue());
                orgCityText.setText((String)((StringField)tableRow.getHidden("city")).getValue());
                orgStateText.setText((String)((StringField)tableRow.getHidden("state")).getValue());
                orgZipCodeText.setText((String)((StringField)tableRow.getHidden("zipCode")).getValue());
            }
            
            if(tableRow.getHidden("itemDesc") != null)
                itemDescText.setText((String)((StringField)tableRow.getHidden("itemDesc")).getValue());
            if(tableRow.getHidden("itemStore") != null)
                itemStoreText.setText((String)((StringField)tableRow.getHidden("itemStore")).getValue());
            if(tableRow.getHidden("itemPurUnit") != null)
                itemPurUnitsText.setText((String)((StringField)tableRow.getHidden("itemPurUnit")).getValue());
            
            if(tableRow.getHidden(InventoryReceiptMeta.TRANS_RECEIPT_LOCATION_META.INVENTORY_LOCATION_META.getStorageLocationId()) != null)
                ((AutoCompleteDropdown)itemLocation.getWidget()).setSelected((ArrayList)((DropDownField)tableRow.getHidden(InventoryReceiptMeta.TRANS_RECEIPT_LOCATION_META.INVENTORY_LOCATION_META.getStorageLocationId())).getSelections());
            else
                ((AutoCompleteDropdown)itemLocation.getWidget()).reset();
            
            if(tableRow.getHidden(InventoryReceiptMeta.TRANS_RECEIPT_LOCATION_META.INVENTORY_LOCATION_META.getLotNumber()) != null)
                ((TextBox)itemLotNum.getWidget()).setText((String)((StringField)tableRow.getHidden(InventoryReceiptMeta.TRANS_RECEIPT_LOCATION_META.INVENTORY_LOCATION_META.getLotNumber())).getValue());
            else
                ((TextBox)itemLotNum.getWidget()).setText("");
            
            if(tableRow.getHidden(InventoryReceiptMeta.TRANS_RECEIPT_LOCATION_META.INVENTORY_LOCATION_META.getExpirationDate()) != null && 
                            ((DateField)tableRow.getHidden(InventoryReceiptMeta.TRANS_RECEIPT_LOCATION_META.INVENTORY_LOCATION_META.getExpirationDate())).getValue() != null)
                ((FormCalendarWidget)itemExpDate.getWidget()).setText(((DatetimeRPC)((DateField)tableRow.getHidden(InventoryReceiptMeta.TRANS_RECEIPT_LOCATION_META.INVENTORY_LOCATION_META.getExpirationDate())).getValue()).toString());
            else
                ((FormCalendarWidget)itemExpDate.getWidget()).setText("");
            
            if(tableRow.getHidden("addToExisting") != null)
                ((CheckBox)addToExisiting.getWidget()).setState((String)((CheckField)tableRow.getHidden("addToExisting")).getValue());
            else
                ((CheckBox)addToExisiting.getWidget()).setState(CheckBox.UNCHECKED);
        }else{
            orgAptSuiteText.setText("");
            orgAddressText.setText("");
            orgCityText.setText("");
            orgStateText.setText("");
            orgZipCodeText.setText("");
            itemDescText.setText("");
            itemStoreText.setText("");
            itemPurUnitsText.setText("");
            ((AutoCompleteDropdown)itemLocation.getWidget()).reset();
            ((TextBox)itemLotNum.getWidget()).setText("");
            ((FormCalendarWidget)itemExpDate.getWidget()).setText("");
            ((CheckBox)addToExisiting.getWidget()).setState(CheckBox.UNCHECKED);
        }
        
        return false;
    }

    public boolean canDelete(int row, TableController controller) {
        return true;
    }

    public boolean canEdit(int row, int col, TableController controller) {
        int numRows = controller.model.numRows();
        if(col == 0 && row > -1 && numRows > 0 && row < numRows){
            TableRow tableRow = controller.model.getRow(row);
            CheckField disableOrderId = null;
            
            if(tableRow != null)
                disableOrderId = (CheckField)tableRow.getHidden("disableOrderId");
            
            //order id is disabled on auto created rows
            if(disableOrderId != null && CheckBox.CHECKED.equals(disableOrderId.getValue()))
                return false;
       
        }else if(col == 2 && row > -1 && numRows > 0 && row < numRows){
            TableRow tableRow = controller.model.getRow(row);
            CheckField disableUpc = null;
            
            if(tableRow != null)
                disableUpc = (CheckField)tableRow.getHidden("disableUpc");
            
            //upc is disabled on auto created rows
            if(disableUpc != null && CheckBox.CHECKED.equals(disableUpc.getValue()))
                return false;
            
        }else if(col == 3 && row > -1 && numRows > 0 && row < numRows){
            TableRow tableRow = controller.model.getRow(row);
            CheckField disableInvItem = null;
            
            if(tableRow != null)
                disableInvItem = (CheckField)tableRow.getHidden("disableInvItem");
            
            //inv item is disabled on auto created rows
            if(disableInvItem != null && CheckBox.CHECKED.equals(disableInvItem.getValue()))
                return false;
            
        }else if(col == 4 && row > -1 && numRows > 0 && row < numRows){
            TableRow tableRow = controller.model.getRow(row);
            CheckField disableOrg = null;
            
            if(tableRow != null)
                disableOrg = (CheckField)tableRow.getHidden("disableOrg");
            
            //upc is disabled on auto created rows
            if(disableOrg != null && CheckBox.CHECKED.equals(disableOrg.getValue()))
                return false;
            
        }

        return true;
    }

    public boolean canInsert(int row, TableController controller) {
        return false;
    }

    public boolean canSelect(int row, TableController controller) {          
        return true;
    }

    public boolean doAutoAdd(int row, int col, TableController controller) {
        if(state != State.UPDATE){
            if(row > -1 && row < controller.model.numRows() && !tableRowEmpty(controller.model.getRow(row), true))
                return true;
            else if(row > -1 && row == controller.model.numRows() && !tableRowEmpty(((EditTable)controller).autoAddRow, true))
                return true;
        }
      
        return false;
    }

    public void finishedEditing(final int row, final int col, final TableController controller) {
        //we need to try and lookup the order using the order number that they have entered
        
        /*
         * Try calling setDisplay on the TableCellWidget after cahnge the field value
         */
        if(col == 0 && !startedLoadingTable && row > -1 && row < controller.model.numRows() && tableRowEmpty(controller.model.getRow(row), false)){
            startedLoadingTable = true;
            window.setStatus("","spinnerIcon");
            
            NumberObject orderIdObj = new NumberObject((Integer)((NumberField)controller.model.getRow(row).getColumn(0)).getValue());
            
            // prepare the argument list for the getObject function
            DataObject[] args = new DataObject[] {orderIdObj}; 
            
            screenService.getObject("getReceipts", args, new AsyncCallback(){
                public void onSuccess(Object result){    
                  // get the datamodel, load it in the notes panel and set the value in the rpc
                    DataModel model = (DataModel)((ModelObject)result).getValue();
                    
                    doAutoAdd = false;
                    
                    if(model.size() == 0)
                        controller.model.getRow(row).addHidden("invalidOrderId", new StringField(""));
                    else
                        controller.model.getRow(row).removeHidden("invalidOrderId");
                        
                    for(int i=0; i<model.size(); i++){
                        DataSet set = model.get(i);
                        
                        TableRow tableRow;
                        
                        if(i == 0)
                            tableRow = controller.model.getRow(row);
                         else
                            tableRow = new TableRow();                        
                        
                        //disable the order id column on auto generated rows
                        CheckField disableOrderId = new CheckField();
                        disableOrderId.setValue(CheckBox.CHECKED);
                       
                        //disable the upc column on auto generated rows
                        CheckField disableUpc = new CheckField();
                        disableUpc.setValue(CheckBox.CHECKED);
                        
                        //disable the inv item column on auto generated rows
                        CheckField disableInvItem = new CheckField();
                        disableInvItem.setValue(CheckBox.CHECKED);
                        
                        //disable the org column on auto generated rows
                        CheckField disableOrg = new CheckField();
                        disableOrg.setValue(CheckBox.CHECKED);
                        
                        //((TableCellWidget)controller.view.table.getWidget(row,2)).enable(false);
                        
                        if(i == 0){
                            NumberField orderId = (NumberField)set.getObject(0); 
                            
                            tableRow.setColumn(0,orderId);
                            tableRow.setColumn(1,(DateField)set.getObject(1));
                            tableRow.setColumn(2,(StringField)set.getObject(2));
                            tableRow.setColumn(3,(DropDownField)set.getObject(3));
                            tableRow.setColumn(4,(DropDownField)set.getObject(4));
                            tableRow.setColumn(5,(NumberField)set.getObject(5));
                            tableRow.setColumn(6,(NumberField)set.getObject(6));
                            tableRow.setColumn(7,(NumberField)set.getObject(7));
                            tableRow.setColumn(8,(StringField)set.getObject(8));
                            tableRow.setColumn(9,(StringField)set.getObject(9));
                            
                            tableRow.addHidden("multUnit", (StringField)set.getObject(10));
                            tableRow.addHidden("streetAddress", (StringField)set.getObject(11));
                            tableRow.addHidden("city", (StringField)set.getObject(12));
                            tableRow.addHidden("state", (StringField)set.getObject(13));
                            tableRow.addHidden("zipCode", (StringField)set.getObject(14));
                            tableRow.addHidden("itemDesc", (StringField)set.getObject(15));
                            tableRow.addHidden("itemStore", (StringField)set.getObject(16));
                            tableRow.addHidden("itemPurUnit", (StringField)set.getObject(17));
                            tableRow.addHidden("itemIsBulk", (StringField)set.getObject(18));
                            tableRow.addHidden("itemIsLotMaintained", (StringField)set.getObject(19));
                            tableRow.addHidden("itemIsSerialMaintained", (StringField)set.getObject(20));
                            tableRow.addHidden("orderItemId", (NumberField)set.getObject(21));
                            tableRow.addHidden("addToExisting", (CheckField)set.getObject(22));
                            tableRow.addHidden(InventoryReceiptMeta.TRANS_RECEIPT_LOCATION_META.INVENTORY_LOCATION_META.getStorageLocationId(), (DropDownField)set.getObject(23));
                            tableRow.addHidden(InventoryReceiptMeta.TRANS_RECEIPT_LOCATION_META.INVENTORY_LOCATION_META.getLotNumber(), (StringField)set.getObject(24));
                            tableRow.addHidden(InventoryReceiptMeta.TRANS_RECEIPT_LOCATION_META.INVENTORY_LOCATION_META.getExpirationDate(), (DateField)set.getObject(25));
                            tableRow.addHidden("id", (NumberField)set.getObject(26));
                            
                            tableRow.addHidden("disableOrderId", disableOrderId);
                            tableRow.addHidden("disableUpc", disableUpc);
                            tableRow.addHidden("disableInvItem", disableInvItem);
                            tableRow.addHidden("disableOrg", disableOrg);
                            
                            orgAptSuiteText.setText((String)((StringField)set.getObject(9)).getValue());
                            orgAddressText.setText((String)((StringField)set.getObject(10)).getValue());
                            orgCityText.setText((String)((StringField)set.getObject(11)).getValue());
                            orgStateText.setText((String)((StringField)set.getObject(12)).getValue());
                            orgZipCodeText.setText((String)((StringField)set.getObject(13)).getValue());
                            itemDescText.setText((String)((StringField)set.getObject(14)).getValue());
                            itemStoreText.setText((String)((StringField)set.getObject(15)).getValue());
                            itemPurUnitsText.setText((String)((StringField)set.getObject(16)).getValue());
                            
                            if(model.size() == 1)
                                ((EditTable)controller).load(0);
                            
                        }else{
                            NumberField orderId = (NumberField)set.getObject(0); 
                            
                            tableRow.addColumn(orderId);
                            tableRow.addColumn((DateField)set.getObject(1));
                            tableRow.addColumn((StringField)set.getObject(2));
                            tableRow.addColumn((DropDownField)set.getObject(3));
                            tableRow.addColumn((DropDownField)set.getObject(4));
                            tableRow.addColumn((NumberField)set.getObject(5));
                            tableRow.addColumn((NumberField)set.getObject(6));
                            tableRow.addColumn((NumberField)set.getObject(7));
                            tableRow.addColumn((StringField)set.getObject(8));
                            tableRow.addColumn((StringField)set.getObject(9));
                            
                            tableRow.addHidden("multUnit", (StringField)set.getObject(10));
                            tableRow.addHidden("streetAddress", (StringField)set.getObject(11));
                            tableRow.addHidden("city", (StringField)set.getObject(12));
                            tableRow.addHidden("state", (StringField)set.getObject(13));
                            tableRow.addHidden("zipCode", (StringField)set.getObject(14));
                            tableRow.addHidden("itemDesc", (StringField)set.getObject(15));
                            tableRow.addHidden("itemStore", (StringField)set.getObject(16));
                            tableRow.addHidden("itemPurUnit", (StringField)set.getObject(17));
                            tableRow.addHidden("itemIsBulk", (StringField)set.getObject(18));
                            tableRow.addHidden("itemIsLotMaintained", (StringField)set.getObject(19));
                            tableRow.addHidden("itemIsSerialMaintained", (StringField)set.getObject(20));
                            tableRow.addHidden("orderItemId", (NumberField)set.getObject(21));
                            tableRow.addHidden("addToExisting", (CheckField)set.getObject(22));
                            tableRow.addHidden(InventoryReceiptMeta.TRANS_RECEIPT_LOCATION_META.INVENTORY_LOCATION_META.getStorageLocationId(), (DropDownField)set.getObject(23));
                            tableRow.addHidden(InventoryReceiptMeta.TRANS_RECEIPT_LOCATION_META.INVENTORY_LOCATION_META.getLotNumber(), (StringField)set.getObject(24));
                            tableRow.addHidden(InventoryReceiptMeta.TRANS_RECEIPT_LOCATION_META.INVENTORY_LOCATION_META.getExpirationDate(), (DateField)set.getObject(25));
                            tableRow.addHidden("id", (NumberField)set.getObject(26));
                            
                            tableRow.addHidden("disableOrderId", disableOrderId);
                            tableRow.addHidden("disableUpc", disableUpc);
                            tableRow.addHidden("disableInvItem", disableInvItem);
                            tableRow.addHidden("disableOrg", disableOrg);
                            
                            ((EditTable)controller).addRow(tableRow);
                        }
                    }
                        
                    doAutoAdd = true;
                    
                    controller.select(row, 1);
                    
                    window.setStatus("","");
                    startedLoadingTable = false;
                }
                
                public void onFailure(Throwable caught){
                    Window.alert(caught.getMessage());
                    window.setStatus("","");
                    startedLoadingTable = false;
                    doAutoAdd = true;
                }
            });
        }else if(col == 2 && row > -1 && row < controller.model.numRows()){
            final TableRow tableRow = controller.model.getRow(row);
            StringObject upcValue = new StringObject((String)((StringField)tableRow.getColumn(2)).getValue());
            
            window.setStatus("","spinnerIcon");
            
            //prepare the argument list for the getObject function
            DataObject[] args = new DataObject[] {upcValue}; 
            
            screenService.getObject("getInvItemFromUPC", args, new AsyncCallback(){
                public void onSuccess(Object result){   
                    DataModel model = (DataModel)((ModelObject)result).getValue();
                    
                    if(model.size() > 1){
                        //we need to have the user select which item they want
                    }else if(model.size() == 1){
                        DataSet set = model.get(0);
                        
                        DropDownField inventoryItem = new DropDownField();
                        DataSet inventoryItemSet = new DataSet();
                        NumberObject itemId = (NumberObject)set.getKey();
                        StringObject itemText = (StringObject)set.getObject(0);
                        StringObject store = (StringObject)set.getObject(1);
                        StringField itemStore = new StringField();
                        StringObject desc = (StringObject)set.getObject(2);
                        StringField itemDesc = new StringField();
                        StringObject purUnits = (StringObject)set.getObject(3);
                        StringField itemPurUnit = new StringField();
                        itemDesc.setValue(desc.getValue());
                        itemStore.setValue(store.getValue());
                        itemPurUnit.setValue(purUnits.getValue());
                        
                        inventoryItemSet.setKey(itemId);
                        inventoryItemSet.addObject(itemText);
                        inventoryItemSet.addObject(store);
                        inventoryItemSet.addObject(desc);
                        inventoryItemSet.addObject(purUnits);
                        inventoryItem.setRequired(true);
                        inventoryItem.setValue(inventoryItemSet);
                       
                        //set the text boxes
                        itemDescText.setText((String)((StringObject)desc).getValue());
                        itemStoreText.setText((String)((StringObject)store).getValue());
                        itemPurUnitsText.setText((String)((StringObject)purUnits).getValue());
                        
                        tableRow.addHidden("itemDesc", itemDesc);
                        tableRow.addHidden("itemStore", itemStore);
                        tableRow.addHidden("itemPurUnit", itemPurUnit);
                        
                        tableRow.setColumn(3, inventoryItem);
                        
                        ((TableAutoDropdown)((EditTable)controller).view.table.getWidget(row, 3)).setField(inventoryItem);
                        ((TableAutoDropdown)((EditTable)controller).view.table.getWidget(row, 3)).setDisplay();
                      
                    }
                        
                    window.setStatus("","");
                }
                
                public void onFailure(Throwable caught){
                    Window.alert(caught.getMessage());
                    window.setStatus("","");
                }
            });
            
        }else if(col == 3 && row > -1 && row < controller.model.numRows()){
            TableRow tableRow = controller.model.getRow(row);
            ArrayList selections = (ArrayList)((DropDownField)tableRow.getColumn(3)).getSelections();
           
            DataSet set = null;
            if(selections.size() > 0)
                set = (DataSet)selections.get(0);
            
           if(set != null && set.size() > 1){
                //set the text boxes
                itemDescText.setText((String)((StringObject)set.getObject(2)).getValue());
                itemStoreText.setText((String)((StringObject)set.getObject(1)).getValue());
                itemPurUnitsText.setText((String)((StringObject)set.getObject(3)).getValue());
                
                StringField itemDesc = new StringField();
                StringField itemStore = new StringField();
                StringField itemPurUnit = new StringField();
                
                itemDesc.setValue((String)((StringObject)set.getObject(2)).getValue());
                itemStore.setValue((String)((StringObject)set.getObject(1)).getValue());
                itemPurUnit.setValue((String)((StringObject)set.getObject(3)).getValue());
                
                tableRow.addHidden("itemDesc", itemDesc);
                tableRow.addHidden("itemStore", itemStore);
                tableRow.addHidden("itemPurUnit", itemPurUnit);
           }
        }else if(col == 4 && row > -1 && row < controller.model.numRows()){
            TableRow tableRow = controller.model.getRow(row);
            ArrayList selections = (ArrayList)((DropDownField)tableRow.getColumn(4)).getSelections();
            DataSet set = null;
            if(selections.size() > 0)
                set = (DataSet)selections.get(0);
            
            if(set != null && set.size() > 1){
                //set the text boxes
                orgAddressText.setText((String)((StringObject)set.getObject(1)).getValue());
                orgCityText.setText((String)((StringObject)set.getObject(2)).getValue());
                orgStateText.setText((String)((StringObject)set.getObject(3)).getValue());               
                orgAptSuiteText.setText((String)((StringObject)set.getObject(4)).getValue());
                orgZipCodeText.setText((String)((StringObject)set.getObject(5)).getValue());
                
                StringField multUnit = new StringField();
                StringField streetAddress = new StringField();
                StringField city = new StringField();
                StringField state = new StringField();
                StringField zipCode = new StringField();
                
                multUnit.setValue((String)((StringObject)set.getObject(4)).getValue());
                streetAddress.setValue((String)((StringObject)set.getObject(1)).getValue());
                city.setValue((String)((StringObject)set.getObject(2)).getValue());
                state.setValue((String)((StringObject)set.getObject(3)).getValue());
                zipCode.setValue((String)((StringObject)set.getObject(5)).getValue());
                
                tableRow.addHidden("multUnit", multUnit);
                tableRow.addHidden("streetAddress", streetAddress);
                tableRow.addHidden("city", city);
                tableRow.addHidden("state", state);
                tableRow.addHidden("zipCode", zipCode);    
            }
        }
    }

    public void getNextPage(TableController controller) {
        // TODO Auto-generated method stub
        
    }

    public void getPage(int page) {
        // TODO Auto-generated method stub
        
    }

    public void getPreviousPage(TableController controller) {
        // TODO Auto-generated method stub
        
    }

    public void rowAdded(int row, TableController controller) {
        // TODO Auto-generated method stub
        
    }

    public void setModel(TableController controller, DataModel model) {
        // TODO Auto-generated method stub
        
    }

    public void setMultiple(int row, int col, TableController controller) {
        // TODO Auto-generated method stub
        
    }
    //end table manager methods
    
    private boolean tableRowEmpty(TableRow row, boolean checkFirstColumn){
        boolean empty = true;
        
        if(checkFirstColumn){
            for(int i=0; i<row.numColumns(); i++){
                if(i != 1 && row.getColumn(i).getValue() != null && !"".equals(row.getColumn(i).getValue())){
                    empty = false;
                    break;
                }
            }
        }else{
            if(row.getColumn(0).getValue() == null || "".equals(row.getColumn(0).getValue()))
                return false;
            
            //we dont need to check the first column
            for(int i=2; i<row.numColumns(); i++){
                if(row.getColumn(i).getValue() != null && !"".equals(row.getColumn(i).getValue())){
                    empty = false;
                    break;
                }
            }
        }
        return empty;
    }
    
    private void onRemoveReceiptRowButtonClick() {
        int selectedRow = receiptsController.selected;
        if (selectedRow > -1 && receiptsController.model.numRows() > 0) {
            TableRow row = receiptsController.model.getRow(selectedRow);
            receiptsController.model.hideRow(row);

            // reset the model
            receiptsController.reset();
            // need to set the deleted flag to "Y" also
            StringField deleteFlag = new StringField();
            deleteFlag.setValue("Y");

            row.addHidden("deleteFlag", deleteFlag);
        }
    }
    
    private void loadReceiptsTableFromModel(DataModel model){
        for(int i=0; i<model.size(); i++){
            DataSet set = model.get(i);
            
            TableRow tableRow = new TableRow();     
            NumberField orderId = (NumberField)set.getObject(0); 
                            
            tableRow.addColumn(orderId);
            tableRow.addColumn((DateField)set.getObject(1));
            tableRow.addColumn((StringField)set.getObject(2));
            tableRow.addColumn((DropDownField)set.getObject(3));
            tableRow.addColumn((DropDownField)set.getObject(4));
            tableRow.addColumn((NumberField)set.getObject(5));
            tableRow.addColumn((NumberField)set.getObject(6));
            tableRow.addColumn((NumberField)set.getObject(7));
            tableRow.addColumn((StringField)set.getObject(8));
            tableRow.addColumn((StringField)set.getObject(9));
            
            tableRow.addHidden("multUnit", (StringField)set.getObject(10));
            tableRow.addHidden("streetAddress", (StringField)set.getObject(11));
            tableRow.addHidden("city", (StringField)set.getObject(12));
            tableRow.addHidden("state", (StringField)set.getObject(13));
            tableRow.addHidden("zipCode", (StringField)set.getObject(14));
            tableRow.addHidden("itemDesc", (StringField)set.getObject(15));
            tableRow.addHidden("itemStore", (StringField)set.getObject(16));
            tableRow.addHidden("itemPurUnit", (StringField)set.getObject(17));
            tableRow.addHidden("itemIsBulk", (StringField)set.getObject(18));
            tableRow.addHidden("itemIsLotMaintained", (StringField)set.getObject(19));
            tableRow.addHidden("itemIsSerialMaintained", (StringField)set.getObject(20));
            tableRow.addHidden("orderItemId", (NumberField)set.getObject(21));
            tableRow.addHidden("addToExisting", (CheckField)set.getObject(22));
            tableRow.addHidden(InventoryReceiptMeta.TRANS_RECEIPT_LOCATION_META.INVENTORY_LOCATION_META.getStorageLocationId(), (DropDownField)set.getObject(23));
            tableRow.addHidden(InventoryReceiptMeta.TRANS_RECEIPT_LOCATION_META.INVENTORY_LOCATION_META.getLotNumber(), (StringField)set.getObject(24));
            tableRow.addHidden(InventoryReceiptMeta.TRANS_RECEIPT_LOCATION_META.INVENTORY_LOCATION_META.getExpirationDate(), (DateField)set.getObject(25));
            tableRow.addHidden("id", (NumberField)set.getObject(26));
            tableRow.addHidden("transReceiptOrderId", (NumberField)set.getObject(27));
            
            //we may need to disable some columns
            CheckField disableOrderId = new CheckField();
            CheckField disableUpc = new CheckField();
            CheckField disableInvItem = new CheckField();
            CheckField disableOrg = new CheckField();
            
            //if we have an order id then it was auto added and we need to disable these columns
            if(orderId.getValue() != null){
                disableOrderId.setValue(CheckBox.CHECKED);
                disableUpc.setValue(CheckBox.CHECKED);
                disableInvItem.setValue(CheckBox.CHECKED);
                disableOrg.setValue(CheckBox.CHECKED);
            
                tableRow.addHidden("disableOrderId", disableOrderId);
                tableRow.addHidden("disableUpc", disableUpc);
                tableRow.addHidden("disableInvItem", disableInvItem);
                tableRow.addHidden("disableOrg", disableOrg);
            }
            
            ((EditTable)receiptsController).addRow(tableRow);
        }
    }
}
