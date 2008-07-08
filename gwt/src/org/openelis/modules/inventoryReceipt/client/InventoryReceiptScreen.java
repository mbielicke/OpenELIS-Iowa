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
* Copyright (C) OpenELIS.  All Rights Reserved.
*/
package org.openelis.modules.inventoryReceipt.client;

import java.util.ArrayList;

import org.openelis.gwt.common.DatetimeRPC;
import org.openelis.gwt.common.data.CheckField;
import org.openelis.gwt.common.data.DataModel;
import org.openelis.gwt.common.data.DataObject;
import org.openelis.gwt.common.data.DataSet;
import org.openelis.gwt.common.data.DateField;
import org.openelis.gwt.common.data.DateObject;
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
import org.openelis.gwt.widget.FormInt;
import org.openelis.gwt.widget.FormInt.State;
import org.openelis.gwt.widget.table.EditTable;
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
                tableRow.addHidden(InventoryReceiptMeta.INVENTORY_LOCATION_META.getStorageLocationId(), loc);
            }else if(sender == itemLotNum.getWidget()){
                StringField lotNum = new StringField();
                lotNum.setValue(((TextBox)itemLotNum.getWidget()).getText());
                TableRow tableRow = receiptsController.model.getRow(receiptsController.selected);
                tableRow.addHidden(InventoryReceiptMeta.INVENTORY_LOCATION_META.getLotNumber(), lotNum);
            }else if(sender == itemExpDate.getWidget()){
                DateField expDate = new DateField();
                expDate.setValue(((FormCalendarWidget)itemExpDate.getWidget()).getValue());
                TableRow tableRow = receiptsController.model.getRow(receiptsController.selected);
                tableRow.addHidden(InventoryReceiptMeta.INVENTORY_LOCATION_META.getExpirationDate(), expDate);
            }
        }
        
    }
    
    public void afterDraw(boolean sucess) {
        orgAptSuiteText = (TextBox)getWidget(InventoryReceiptMeta.ORDER_META.ORDER_ORGANIZATION_META.ADDRESS.getMultipleUnit());        
        orgAddressText = (TextBox)getWidget(InventoryReceiptMeta.ORDER_META.ORDER_ORGANIZATION_META.ADDRESS.getStreetAddress());
        orgCityText = (TextBox)getWidget(InventoryReceiptMeta.ORDER_META.ORDER_ORGANIZATION_META.ADDRESS.getCity());
        orgStateText = (TextBox)getWidget(InventoryReceiptMeta.ORDER_META.ORDER_ORGANIZATION_META.ADDRESS.getState());
        orgZipCodeText = (TextBox)getWidget(InventoryReceiptMeta.ORDER_META.ORDER_ORGANIZATION_META.ADDRESS.getZipCode());
        itemDescText = (TextBox)getWidget(InventoryReceiptMeta.ORDER_META.ORDER_ITEM_META.INVENTORY_ITEM_META.getDescription());
        itemStoreText = (TextBox)getWidget(InventoryReceiptMeta.ORDER_META.ORDER_ITEM_META.INVENTORY_ITEM_META.getStoreId());
        itemPurUnitsText = (TextBox)getWidget(InventoryReceiptMeta.ORDER_META.ORDER_ITEM_META.INVENTORY_ITEM_META.getPurchasedUnitsId());
        itemLocation = (ScreenAutoDropdown)widgets.get(InventoryReceiptMeta.INVENTORY_LOCATION_META.getStorageLocationId());
        itemLotNum = (ScreenTextBox)widgets.get(InventoryReceiptMeta.INVENTORY_LOCATION_META.getLotNumber());
        itemExpDate = (ScreenCalendar)widgets.get(InventoryReceiptMeta.INVENTORY_LOCATION_META.getExpirationDate());
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
    
    public void query() {
        super.query();
        
        addToExisiting.enable(false);
        itemExpDate.enable(false);
    }
    
    public void update() {
        receiptsController.setAutoAdd(true);
        super.update();
    }
    
    public void abort() {
        receiptsController.setAutoAdd(false);
        
        super.abort();
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
        
        if(!addToExisiting.isEnabled() && receiptsController.model.numRows() > 0){
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
            
            if(tableRow.getHidden(InventoryReceiptMeta.INVENTORY_LOCATION_META.getStorageLocationId()) != null)
                ((AutoCompleteDropdown)itemLocation.getWidget()).setSelected((ArrayList)((DropDownField)tableRow.getHidden(InventoryReceiptMeta.INVENTORY_LOCATION_META.getStorageLocationId())).getValue());
            else
                ((AutoCompleteDropdown)itemLocation.getWidget()).reset();
            
            if(tableRow.getHidden(InventoryReceiptMeta.INVENTORY_LOCATION_META.getLotNumber()) != null)
                ((TextBox)itemLotNum.getWidget()).setText((String)((StringField)tableRow.getHidden(InventoryReceiptMeta.INVENTORY_LOCATION_META.getLotNumber())).getValue());
            else
                ((TextBox)itemLotNum.getWidget()).setText("");
            
            if(tableRow.getHidden(InventoryReceiptMeta.INVENTORY_LOCATION_META.getExpirationDate()) != null)
                ((FormCalendarWidget)itemExpDate.getWidget()).setText(((DatetimeRPC)((DateField)tableRow.getHidden(InventoryReceiptMeta.INVENTORY_LOCATION_META.getExpirationDate())).getValue()).toString());
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
        if(state == FormInt.State.ADD || state == FormInt.State.UPDATE)           
            return true;
        return false;
    }

    public boolean doAutoAdd(int row, int col, TableController controller) {
        if(col == 0 && doAutoAdd)
            return true;
        else
            return false;
    }

    public void finishedEditing(final int row, int col, final TableController controller) {
        //we need to try and lookup the order using the order number that they have entered
        if(col == 0 && !startedLoadingTable && tableRowEmpty(controller.model.getRow(row))){
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
                    for(int i=0; i<model.size(); i++){
                        DataSet set = model.get(i);
                        
                        NumberField orderNumber = new NumberField(NumberObject.Type.INTEGER);
                        DateField receivedDate = new DateField();
                        StringField upc = new StringField();
                        DropDownField inventoryItem = new DropDownField();
                        DropDownField org = new DropDownField();
                        NumberField qty = new NumberField(NumberObject.Type.INTEGER);
                        NumberField cost = new NumberField(NumberObject.Type.DOUBLE);
                        StringField qc = new StringField();
                        StringField extRef = new StringField();
                        StringField multUnit = new StringField();
                        StringField streetAddress = new StringField();
                        StringField city = new StringField();
                        StringField state = new StringField();
                        StringField zipCode = new StringField();
                        StringField itemDesc = new StringField();
                        StringField itemStore = new StringField();
                        StringField itemPurUnit = new StringField();
                        StringField itemIsBulk = new StringField();
                        StringField itemIsLotMaintained = new StringField();
                        StringField itemIsSerialMaintained = new StringField();
                        NumberField orderItemId = new NumberField(NumberObject.Type.INTEGER);
                        
                        TableRow tableRow;
                        
                        if(i == 0)
                            tableRow = controller.model.getRow(row);
                         else
                            tableRow = new TableRow();                        
                        
                        orderNumber.setValue((Integer)((NumberObject)set.getObject(0)).getValue());
                        receivedDate.setRequired(true);
                        receivedDate.setValue((DatetimeRPC)((DateObject)set.getObject(1)).getValue());
                        
                        //inventory item data set
                        DataSet inventoryItemSet = new DataSet();
                        NumberObject itemId = (NumberObject)set.getObject(2);
                        StringObject itemText = (StringObject)set.getObject(3);
                        inventoryItemSet.setKey(itemId);
                        inventoryItemSet.addObject(itemText);
                        inventoryItem.setRequired(true);
                        inventoryItem.setValue(inventoryItemSet);
                        
                        orderItemId.setValue((Integer)((NumberObject)set.getObject(4)).getValue());
                        
                        //org dataset
                        DataSet orgSet = new DataSet();
                        NumberObject orgId = (NumberObject)set.getObject(5);
                        StringObject orgText = (StringObject)set.getObject(6);
                        orgSet.setKey(orgId);
                        orgSet.addObject(orgText);
                        org.setRequired(true);
                        org.setValue(orgSet);
                        qty.setRequired(true);
                        cost.setRequired(true);
                        
                        multUnit.setValue((String)((StringObject)set.getObject(7)).getValue());
                        streetAddress.setValue((String)((StringObject)set.getObject(8)).getValue());
                        city.setValue((String)((StringObject)set.getObject(9)).getValue());
                        state.setValue((String)((StringObject)set.getObject(10)).getValue());
                        zipCode.setValue((String)((StringObject)set.getObject(11)).getValue());
                        itemDesc.setValue((String)((StringObject)set.getObject(12)).getValue());
                        itemStore.setValue((String)((StringObject)set.getObject(13)).getValue());
                        itemPurUnit.setValue((String)((StringObject)set.getObject(14)).getValue());
                        itemIsBulk.setValue((String)((StringObject)set.getObject(15)).getValue());
                        itemIsLotMaintained.setValue((String)((StringObject)set.getObject(16)).getValue());
                        itemIsSerialMaintained.setValue((String)((StringObject)set.getObject(17)).getValue());
                        
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
                            tableRow.setColumn(1, receivedDate);
                            tableRow.setColumn(2, upc);
                            tableRow.setColumn(3, inventoryItem);
                            tableRow.setColumn(4, org);  
                            
                            tableRow.addHidden("multUnit", multUnit);
                            tableRow.addHidden("streetAddress", streetAddress);
                            tableRow.addHidden("city", city);
                            tableRow.addHidden("state", state);
                            tableRow.addHidden("zipCode", zipCode);
                            tableRow.addHidden("itemDesc", itemDesc);
                            tableRow.addHidden("itemStore", itemStore);
                            tableRow.addHidden("itemPurUnit", itemPurUnit);
                            tableRow.addHidden("itemIsBulk", itemIsBulk);
                            tableRow.addHidden("itemIsLotMaintained", itemIsLotMaintained);
                            tableRow.addHidden("itemIsSerialMaintained", itemIsSerialMaintained);
                            tableRow.addHidden("orderItemId", orderItemId);
                            
                            tableRow.addHidden("disableOrderId", disableOrderId);
                            tableRow.addHidden("disableUpc", disableUpc);
                            tableRow.addHidden("disableInvItem", disableInvItem);
                            tableRow.addHidden("disableOrg", disableOrg);
                            
                            orgAptSuiteText.setText((String)multUnit.getValue());
                            orgAddressText.setText((String)streetAddress.getValue());
                            orgCityText.setText((String)city.getValue());
                            orgStateText.setText((String)state.getValue());
                            orgZipCodeText.setText((String)zipCode.getValue());
                            itemDescText.setText((String)itemDesc.getValue());
                            itemStoreText.setText((String)itemStore.getValue());
                            itemPurUnitsText.setText((String)itemPurUnit.getValue());
                            
                            if(model.size() == 1)
                                ((EditTable)controller).load(0);
                            
                        }else{
                            tableRow.addColumn(orderNumber);
                            tableRow.addColumn(receivedDate);
                            tableRow.addColumn(upc);
                            tableRow.addColumn(inventoryItem);
                            tableRow.addColumn(org);
                            tableRow.addColumn(qty);
                            tableRow.addColumn(cost);
                            tableRow.addColumn(qc);
                            tableRow.addColumn(extRef);
                            
                            tableRow.addHidden("multUnit", multUnit);
                            tableRow.addHidden("streetAddress", streetAddress);
                            tableRow.addHidden("city", city);
                            tableRow.addHidden("state", state);
                            tableRow.addHidden("zipCode", zipCode);
                            tableRow.addHidden("itemDesc", itemDesc);
                            tableRow.addHidden("itemStore", itemStore);
                            tableRow.addHidden("itemPurUnit", itemPurUnit);
                            tableRow.addHidden("itemIsBulk", itemIsBulk);
                            tableRow.addHidden("itemIsLotMaintained", itemIsLotMaintained);
                            tableRow.addHidden("itemIsSerialMaintained", itemIsSerialMaintained);
                            tableRow.addHidden("disableOrderId", disableOrderId);
                            tableRow.addHidden("disableUpc", disableUpc);
                            tableRow.addHidden("disableInvItem", disableInvItem);
                            tableRow.addHidden("disableOrg", disableOrg);
                            tableRow.addHidden("orderItemId", orderItemId);
                            
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
        }else if(col == 2 && row >=0){
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
                    }else{
                        DataSet set = model.get(0);
                        
                        DropDownField inventoryItem = new DropDownField();
                        DataSet inventoryItemSet = new DataSet();
                        NumberObject itemId = (NumberObject)set.getObject(0);
                        StringObject itemText = (StringObject)set.getObject(1);
                        inventoryItemSet.setKey(itemId);
                        inventoryItemSet.addObject(itemText);
                        inventoryItem.setRequired(true);
                        inventoryItem.setValue(inventoryItemSet);
                        
                        tableRow.setColumn(3, inventoryItem);
                        
                        ((EditTable)controller).load(0);
                        controller.select(row, 3);
                    }
                        
                    window.setStatus("","");
                }
                
                public void onFailure(Throwable caught){
                    Window.alert(caught.getMessage());
                    window.setStatus("","");
                }
            });
            
        }else if(col == 3 && row >= 0){
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
        }else if(col == 4 && row >= 0){
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
    
    private boolean tableRowEmpty(TableRow row){
        boolean empty = true;
        
        if(row.getColumn(0).getValue() == null || "".equals(row.getColumn(0).getValue()))
            return false;
        
        //we dont need to check the first column
        for(int i=1; i<row.numColumns(); i++){
            if(row.getColumn(i).getValue() != null && !"".equals(row.getColumn(i).getValue())){
                empty = false;
                break;
            }
        }
        
        return empty;
    }
    
    private void onRemoveReceiptRowButtonClick() {
        int selectedRow = receiptsController.selected;
        if (selectedRow > -1 && receiptsController.model.numRows() > 1) {
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

}
