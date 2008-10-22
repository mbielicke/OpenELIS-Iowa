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
package org.openelis.modules.inventoryReceipt.client;

import java.util.ArrayList;
import java.util.HashMap;

import org.openelis.gwt.common.DatetimeRPC;
import org.openelis.gwt.common.FormErrorException;
import org.openelis.gwt.common.FormRPC.Status;
import org.openelis.gwt.common.data.CheckField;
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
import org.openelis.gwt.screen.CommandChain;
import org.openelis.gwt.screen.ScreenAutoCompleteWidget;
import org.openelis.gwt.screen.ScreenCalendar;
import org.openelis.gwt.screen.ScreenCheck;
import org.openelis.gwt.screen.ScreenTextBox;
import org.openelis.gwt.widget.AToZTable;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.AutoComplete;
import org.openelis.gwt.widget.AutoCompleteCallInt;
import org.openelis.gwt.widget.ButtonPanel;
import org.openelis.gwt.widget.CheckBox;
import org.openelis.gwt.widget.FormCalendarWidget;
import org.openelis.gwt.widget.table.TableManager;
import org.openelis.gwt.widget.table.TableWidget;
import org.openelis.gwt.widget.table.event.SourcesTableModelEvents;
import org.openelis.gwt.widget.table.event.SourcesTableWidgetEvents;
import org.openelis.gwt.widget.table.event.TableModelListener;
import org.openelis.gwt.widget.table.event.TableWidgetListener;
import org.openelis.metamap.InventoryReceiptMetaMap;
import org.openelis.modules.main.client.OpenELISScreenForm;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class InventoryReceiptScreen extends OpenELISScreenForm implements ClickListener, ChangeListener, TableManager, TableWidgetListener, TableModelListener, AutoCompleteCallInt {
    
    private TableWidget receiptsTable;
    private boolean doAutoAdd = true;
    
    private AppButton        removeReceiptButton;
    private TextBox orgAptSuiteText, orgAddressText, orgCityText, orgStateText, orgZipCodeText,
                    itemDescText, itemStoreText, itemDisUnitsText;
    ScreenTextBox itemLotNum;
    private ScreenAutoCompleteWidget itemLocation;
    private ScreenCalendar itemExpDate;
    private ScreenCheck addToExisiting;
    private ButtonPanel      atozButtons;
    private KeyListManager   keyList = new KeyListManager();
    
    private InventoryReceiptMetaMap InventoryReceiptMeta = new InventoryReceiptMetaMap();
    
    public InventoryReceiptScreen() {
        super("org.openelis.modules.inventoryReceipt.server.InventoryReceiptService",false);
    }

    public void performCommand(Enum action, Object obj) {
        if(action == KeyListManager.Action.FETCH){
            fetch();
        }else{
            super.performCommand(action, obj);
        }
    }        
    
    public void onClick(Widget sender) {
        if (sender == removeReceiptButton)
            onRemoveReceiptRowButtonClick();
        else if(sender == addToExisiting && addToExisiting.isEnabled() && receiptsTable.model.getSelectedIndex() > -1 && receiptsTable.model.numRows() > 0){
            CheckField existing = new CheckField();
            if(((CheckBox)addToExisiting.getWidget()).getState() == CheckBox.CHECKED)
                existing.setValue(CheckBox.UNCHECKED);
            else
                existing.setValue(CheckBox.CHECKED);
        
            DataSet tableRow = receiptsTable.model.getRow(receiptsTable.model.getSelectedIndex());
            ((DataMap)tableRow.getData()).put("addToExisting", existing);  
            ((AutoComplete)itemLocation.getWidget()).setSelections(new ArrayList<DataSet>());
        }
        
    }
    
    public void onChange(Widget sender) {
        super.onChange(sender);
         
        if(receiptsTable.model.numRows() > 0){
            if(sender == itemLocation.getWidget()){
                DropDownField loc = new DropDownField();
                loc.setValue(((AutoComplete)itemLocation.getWidget()).getSelections());
                DataSet tableRow = receiptsTable.model.getRow(receiptsTable.model.getSelectedIndex());
                ((DataMap)tableRow.getData()).put(InventoryReceiptMeta.TRANS_RECEIPT_LOCATION_META.INVENTORY_LOCATION_META.getStorageLocationId(), loc);
            }else if(sender == itemLotNum.getWidget()){
                StringField lotNum = new StringField();
                lotNum.setValue(((TextBox)itemLotNum.getWidget()).getText());
                DataSet tableRow = receiptsTable.model.getRow(receiptsTable.model.getSelectedIndex());
                ((DataMap)tableRow.getData()).put(InventoryReceiptMeta.TRANS_RECEIPT_LOCATION_META.INVENTORY_LOCATION_META.getLotNumber(), lotNum);
            }else if(sender == itemExpDate.getWidget()){
                DateField expDate = new DateField();
                expDate.setValue(((FormCalendarWidget)itemExpDate.getWidget()).getValue());
                DataSet tableRow = receiptsTable.model.getRow(receiptsTable.model.getSelectedIndex());
                ((DataMap)tableRow.getData()).put(InventoryReceiptMeta.TRANS_RECEIPT_LOCATION_META.INVENTORY_LOCATION_META.getExpirationDate(), expDate);
            }
        }
    }
    
    public void afterDraw(boolean sucess) {
        AToZTable atozTable;
        orgAptSuiteText = (TextBox)getWidget(InventoryReceiptMeta.ORGANIZATION_META.ADDRESS.getMultipleUnit());        
        orgAddressText = (TextBox)getWidget(InventoryReceiptMeta.ORGANIZATION_META.ADDRESS.getStreetAddress());
        orgCityText = (TextBox)getWidget(InventoryReceiptMeta.ORGANIZATION_META.ADDRESS.getCity());
        orgStateText = (TextBox)getWidget(InventoryReceiptMeta.ORGANIZATION_META.ADDRESS.getState());
        orgZipCodeText = (TextBox)getWidget(InventoryReceiptMeta.ORGANIZATION_META.ADDRESS.getZipCode());
        itemDescText = (TextBox)getWidget(InventoryReceiptMeta.INVENTORY_ITEM_META.getDescription());
        itemStoreText = (TextBox)getWidget(InventoryReceiptMeta.INVENTORY_ITEM_META.getStoreId());
        itemDisUnitsText = (TextBox)getWidget(InventoryReceiptMeta.INVENTORY_ITEM_META.getDispensedUnitsId());
        itemLocation = (ScreenAutoCompleteWidget)widgets.get(InventoryReceiptMeta.TRANS_RECEIPT_LOCATION_META.INVENTORY_LOCATION_META.getStorageLocationId());
        itemLotNum = (ScreenTextBox)widgets.get(InventoryReceiptMeta.TRANS_RECEIPT_LOCATION_META.INVENTORY_LOCATION_META.getLotNumber());
        itemExpDate = (ScreenCalendar)widgets.get(InventoryReceiptMeta.TRANS_RECEIPT_LOCATION_META.INVENTORY_LOCATION_META.getExpirationDate());
        addToExisiting = (ScreenCheck)widgets.get("addToExisting");
        removeReceiptButton = (AppButton)getWidget("removeReceiptButton");
        
        //
        // disable auto add and make sure there are no rows in the table
        //
        receiptsTable = (TableWidget)getWidget("receiptsTable");
        receiptsTable.model.enableAutoAdd(false);
        receiptsTable.addTableWidgetListener(this);
        receiptsTable.model.addTableModelListener(this);
        
        atozTable = (AToZTable)getWidget("azTable");
        ButtonPanel bpanel = (ButtonPanel)getWidget("buttons");
        atozButtons = (ButtonPanel)getWidget("atozButtons");
        
        CommandChain formChain = new CommandChain();
        formChain.addCommand(this);
        formChain.addCommand(bpanel);
        formChain.addCommand(keyList);
        formChain.addCommand(atozTable);
        formChain.addCommand(atozButtons);
        
        super.afterDraw(sucess);
        
        rpc.setFieldValue("receiptsTable", receiptsTable.model.getData());
    }
    
    public void add() {
        //
        // make sure the contact table gets set before the main add
        //
        receiptsTable.model.enableAutoAdd(true);
        super.add();
        
        itemLotNum.enable(false);
        itemLocation.enable(false);
        itemExpDate.enable(false);
        addToExisiting.enable(false);
        
        //TODO not sure how to replace this  receiptsController.onCellClicked((SourcesTableEvents)receiptsController.view.table, 0, 0);
       
    }
    
    public void fetch() {
        //we dont need to call the super because we arent using the datamodel for lookups
        //we are using it to fill the table
        //super.fetch();
        
        DataModel model = keyList.getList();
        
        receiptsTable.model.clear();
        
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
        resetRPC();
        load();
        
        window.setStatus(consts.get("lockForUpdate"),"spinnerIcon");
        
        // prepare the argument list for the getObject function
        Data[] args = new Data[] {keyList.getList()}; 
        
        screenService.getObject("commitQueryAndLock", args, new AsyncCallback(){
            public void onSuccess(Object result){                    
                DataModel model = (DataModel)result;
                
                keyList.setModel(model);
                keyList.select(0);
                
                enable(true);
                window.setStatus(consts.get("updateFields"),"");
                changeState(State.UPDATE);
            }
            
            public void onFailure(Throwable caught){
                Window.alert(caught.getMessage());
                enable(true);
                window.setStatus(consts.get("updateFields"),"");
                changeState(State.UPDATE);
            }
        });
    }
    
    public void abort() {
        receiptsTable.model.enableAutoAdd(false);
        
        if (state == State.UPDATE) {
            window.setStatus("","spinnerIcon");
            clearErrors();
            resetRPC();
            load();
            enable(false);
            
            // prepare the argument list for the getObject function
            Data[] args = new Data[] {keyList.getList()}; 
            
            screenService.getObject("commitQueryAndUnlock", args, new AsyncCallback(){
                public void onSuccess(Object result){                    
                    DataModel model = (DataModel)result;
                    
                    keyList.setModel(model);
                    keyList.select(0);

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
            rpc.validate();
            validate();
            if (rpc.status == Status.valid){}
               //TODO not sure how to replace this      receiptsController.onCellClicked((SourcesTableEvents)receiptsController.view.table, 0, 1);
        }
    }

    //
    //start table manager methods
    //
    public boolean canAdd(TableWidget widget, DataSet set, int row) {
        return true;
    }

    public boolean canAutoAdd(TableWidget widget, DataSet addRow) {
        if(state != State.UPDATE){
            return !tableRowEmpty(addRow, true);
        }
        
        return false;
    }

    public boolean canDelete(TableWidget widget, DataSet set, int row) {
        return true;
    }

    public boolean canEdit(TableWidget widget, DataSet set, int row, int col) {
        int numRows = receiptsTable.model.numRows();
        if(col == 0 && row > -1 && numRows > 0 && row < numRows){
            DataSet tableRow = receiptsTable.model.getRow(row);
            DataMap map = (DataMap)tableRow.getData();
            CheckField disableOrderId = null;
            
            if(tableRow != null && tableRow.getData() != null)
                disableOrderId = (CheckField)map.get("disableOrderId");
            
            //order id is disabled on auto created rows
            if(disableOrderId != null && CheckBox.CHECKED.equals(disableOrderId.getValue()))
                return false;
       
        }else if(col == 2 && row > -1 && numRows > 0 && row < numRows){
            DataSet tableRow = receiptsTable.model.getRow(row);
            DataMap map = (DataMap)tableRow.getData();
            CheckField disableUpc = null;
            
            if(tableRow != null && tableRow.getData() != null)
                disableUpc = (CheckField)map.get("disableUpc");
            
            //upc is disabled on auto created rows
            if(disableUpc != null && CheckBox.CHECKED.equals(disableUpc.getValue()))
                return false;
            
        }else if(col == 3 && row > -1 && numRows > 0 && row < numRows){
            DataSet tableRow = receiptsTable.model.getRow(row);
            DataMap map = (DataMap)tableRow.getData();
            CheckField disableInvItem = null;
            
            if(tableRow != null && tableRow.getData() != null)
                disableInvItem = (CheckField)map.get("disableInvItem");
            
            //inv item is disabled on auto created rows
            if(disableInvItem != null && CheckBox.CHECKED.equals(disableInvItem.getValue()))
                return false;
            
        }else if(col == 4 && row > -1 && numRows > 0 && row < numRows){
            DataSet tableRow = receiptsTable.model.getRow(row);
            DataMap map = (DataMap)tableRow.getData();
            CheckField disableOrg = null;
            
            if(tableRow != null && tableRow.getData() != null)
                disableOrg = (CheckField)map.get("disableOrg");
            
            //upc is disabled on auto created rows
            if(disableOrg != null && CheckBox.CHECKED.equals(disableOrg.getValue()))
                return false;
            
        }

        return true;
    }

    public boolean canSelect(TableWidget widget, DataSet set, int row) {
        return true;
    }
    //
    //end table manager methods
    //
    
    //
    //start table listener methods
    //
    public void finishedEditing(SourcesTableWidgetEvents sender, final int row, int col) {
        //we need to try and lookup the order using the order number that they have entered
        if(col == 0 && row < receiptsTable.model.numRows() && tableRowEmpty(receiptsTable.model.getRow(row), false)){
            window.setStatus("","spinnerIcon");
            
            NumberObject orderIdObj = new NumberObject((Integer)((NumberField)receiptsTable.model.getRow(row).get(0)).getValue());
            
            // prepare the argument list for the getObject function
            Data[] args = new Data[] {orderIdObj}; 
            
            screenService.getObject("getReceipts", args, new AsyncCallback(){
                public void onSuccess(Object result){    
                  // get the datamodel, load it in the notes panel and set the value in the rpc
                    DataModel model = (DataModel)result;
                    DataMap map = new DataMap();
                    doAutoAdd = false;
                        
                    loadReceiptsTableFromModel(model,row);
                    
                    /*for(int i=0; i<model.size(); i++){
                        DataSet set = model.get(i);
                        
                        DataSet tableRow;
                        
                        if(i == 0){
                            tableRow = receiptsTable.model.getRow(row);
                            map = (DataMap)tableRow.getData();
                            
                            if(map == null)
                                map = new DataMap();
                        } else{
                            tableRow = receiptsTable.model.createRow();
                            map = new DataMap();
                        }
                        
                        if(model.size() == 0)
                            map.put("invalidOrderId", new StringField(""));
                        else
                            map.remove("invalidOrderId");
                        
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
                        loadReceiptsTableFromModel(model);
                        
                        if(i == 0){
                            receiptsTable.model.setCell(row, 0, ((NumberField)set.get(0)).getValue());
                            receiptsTable.model.setCell(row, 1, ((DateField)set.get(1)).getValue());
                            receiptsTable.model.setCell(row, 2, ((StringField)set.get(2)).getValue());
                            
                            ((DropDownField)receiptsTable.model.getObject(row, 3)).setModel(((DropDownField)set.get(3)).getModel());
                            receiptsTable.model.setCell(row, 3, ((DropDownField)set.get(3)).getModel().get(0));
                            
                            ((DropDownField)receiptsTable.model.getObject(row, 4)).setModel(((DropDownField)set.get(4)).getModel());
                            receiptsTable.model.setCell(row, 4, ((DropDownField)set.get(4)).getModel().get(0));
                            
                            receiptsTable.model.setCell(row, 5, ((NumberField)set.get(5)).getValue());
                            receiptsTable.model.setCell(row, 6, ((NumberField)set.get(6)).getValue());
                            receiptsTable.model.setCell(row, 7, ((NumberField)set.get(7)).getValue());
                            receiptsTable.model.setCell(row, 8, ((StringField)set.get(8)).getValue());
                            receiptsTable.model.setCell(row, 9, ((StringField)set.get(9)).getValue());
                            
                            map.put("multUnit", (StringField)set.get(10));
                            map.put("streetAddress", (StringField)set.get(11));
                            map.put("city", (StringField)set.get(12));
                            map.put("state", (StringField)set.get(13));
                            map.put("zipCode", (StringField)set.get(14));
                            map.put("itemDesc", (StringField)set.get(15));
                            map.put("itemStore", (StringField)set.get(16));
                            map.put("itemDisUnit", (StringField)set.get(17));
                            map.put("itemIsBulk", (StringField)set.get(18));
                            map.put("itemIsLotMaintained", (StringField)set.get(19));
                            map.put("itemIsSerialMaintained", (StringField)set.get(20));
                            map.put("orderItemId", (NumberField)set.get(21));
                            map.put("addToExisting", (CheckField)set.get(22));
                            map.put(InventoryReceiptMeta.TRANS_RECEIPT_LOCATION_META.INVENTORY_LOCATION_META.getStorageLocationId(), (DropDownField)set.get(23));
                            map.put(InventoryReceiptMeta.TRANS_RECEIPT_LOCATION_META.INVENTORY_LOCATION_META.getLotNumber(), (StringField)set.get(24));
                            map.put(InventoryReceiptMeta.TRANS_RECEIPT_LOCATION_META.INVENTORY_LOCATION_META.getExpirationDate(), (DateField)set.get(25));
                            map.put("id", (NumberField)set.get(26));
                            
                            map.put("disableOrderId", disableOrderId);
                            map.put("disableUpc", disableUpc);
                            map.put("disableInvItem", disableInvItem);
                            map.put("disableOrg", disableOrg);
                            
                            orgAptSuiteText.setText((String)((StringField)set.get(9)).getValue());
                            orgAddressText.setText((String)((StringField)set.get(10)).getValue());
                            orgCityText.setText((String)((StringField)set.get(11)).getValue());
                            orgStateText.setText((String)((StringField)set.get(12)).getValue());
                            orgZipCodeText.setText((String)((StringField)set.get(13)).getValue());
                            itemDescText.setText((String)((StringField)set.get(14)).getValue());
                            itemStoreText.setText((String)((StringField)set.get(15)).getValue());
                            itemDisUnitsText.setText((String)((StringField)set.get(17)).getValue());
                            
                            ((DataSet)receiptsTable.model.getRow(row)).setData(map);
                            
                        }else{
                            tableRow.get(0).setValue(((NumberField)set.get(0)).getValue());
                            tableRow.get(1).setValue(((DateField)set.get(1)).getValue());
                            tableRow.get(2).setValue(((StringField)set.get(2)).getValue());
                            
                            ((DropDownField)tableRow.get(3)).setModel(((DropDownField)set.get(3)).getModel());
                            tableRow.get(3).setValue(((DropDownField)set.get(3)).getModel().get(0));
                            
                            ((DropDownField)tableRow.get(4)).setModel(((DropDownField)set.get(4)).getModel());
                            tableRow.get(4).setValue(((DropDownField)set.get(4)).getModel().get(0));

                            tableRow.get(5).setValue(((NumberField)set.get(5)).getValue());
                            tableRow.get(6).setValue(((NumberField)set.get(6)).getValue());
                            tableRow.get(7).setValue(((NumberField)set.get(7)).getValue());
                            tableRow.get(8).setValue(((StringField)set.get(8)).getValue());
                            tableRow.get(9).setValue(((StringField)set.get(9)).getValue());
                            
                            map.put("multUnit", (StringField)set.get(10));
                            map.put("streetAddress", (StringField)set.get(11));
                            map.put("city", (StringField)set.get(12));
                            map.put("state", (StringField)set.get(13));
                            map.put("zipCode", (StringField)set.get(14));
                            map.put("itemDesc", (StringField)set.get(15));
                            map.put("itemStore", (StringField)set.get(16));
                            map.put("itemDisUnit", (StringField)set.get(17));
                            map.put("itemIsBulk", (StringField)set.get(18));
                            map.put("itemIsLotMaintained", (StringField)set.get(19));
                            map.put("itemIsSerialMaintained", (StringField)set.get(20));
                            map.put("orderItemId", (NumberField)set.get(21));
                            map.put("addToExisting", (CheckField)set.get(22));
                            map.put(InventoryReceiptMeta.TRANS_RECEIPT_LOCATION_META.INVENTORY_LOCATION_META.getStorageLocationId(), (DropDownField)set.get(23));
                            map.put(InventoryReceiptMeta.TRANS_RECEIPT_LOCATION_META.INVENTORY_LOCATION_META.getLotNumber(), (StringField)set.get(24));
                            map.put(InventoryReceiptMeta.TRANS_RECEIPT_LOCATION_META.INVENTORY_LOCATION_META.getExpirationDate(), (DateField)set.get(25));
                            map.put("id", (NumberField)set.get(26));
                            
                            map.put("disableOrderId", disableOrderId);
                            map.put("disableUpc", disableUpc);
                            map.put("disableInvItem", disableInvItem);
                            map.put("disableOrg", disableOrg);
                            
                            tableRow.setData(map);
                            receiptsTable.model.addRow(tableRow);
                        }
                    }*/
                        
                    doAutoAdd = true;
                    
                    receiptsTable.select(row, 1);
                    
                    window.setStatus("","");
                }
                
                public void onFailure(Throwable caught){
                    Window.alert(caught.getMessage());
                    window.setStatus("","");
                    doAutoAdd = true;
                }
            });
        }else if(col == 2 && row > -1 && row < receiptsTable.model.numRows()){
            final DataSet tableRow = receiptsTable.model.getRow(row);
            StringObject upcValue = new StringObject((String)((StringField)tableRow.get(2)).getValue());
            
            window.setStatus("","spinnerIcon");
            
            //prepare the argument list for the getObject function
            Data[] args = new Data[] {upcValue}; 
            
            screenService.getObject("getInvItemFromUPC", args, new AsyncCallback(){
                public void onSuccess(Object result){   
                    DataModel model = (DataModel)result;
                    
                    if(model.size() > 1){
                        //we need to have the user select which item they want
                    }else if(model.size() == 1){
                        loadReceiptsTableFromModel(model, row);
                        /*
                        DataSet set = model.get(0);
                        
                        StringObject store = (StringObject)set.get(1);
                        StringField itemStore = new StringField();
                        StringObject desc = (StringObject)set.get(2);
                        StringField itemDesc = new StringField();
                        StringObject disUnits = (StringObject)set.get(3);
                        StringField itemDisUnit = new StringField();
                        StringObject itemIsBulk =(StringObject)set.get(4);
                        StringField isBulk =  new StringField();
                        StringObject itemIsLotMaintained = (StringObject)set.get(5);
                        StringField isLotMaintained = new StringField();
                        StringObject itemIsSerialMaintained = (StringObject)set.get(6);
                        StringField isSerialMaintained = new StringField();
                        itemDesc.setValue(desc.getValue());
                        itemStore.setValue(store.getValue());
                        itemDisUnit.setValue(disUnits.getValue());
                        isBulk.setValue(itemIsBulk.getValue());
                        isLotMaintained.setValue(itemIsLotMaintained.getValue());
                        isSerialMaintained.setValue(itemIsSerialMaintained.getValue());
                        
                        //name, store
                        DataModel invItemModel = new DataModel();
                        DataSet invItemSet = new DataSet();
                        invItemSet.setKey((NumberObject)set.getKey());
                        invItemSet.add((StringObject)set.get(0));
                        invItemSet.add(new StringField((String)((StringObject)set.get(1)).getValue()));
                        DataMap hiddenItems = new DataMap();
                        hiddenItems.put("desc",new StringField((String)((StringObject)set.get(2)).getValue()));
                        hiddenItems.put("itemDisUnit", (StringObject)set.get(3));
                        invItemSet.setData(hiddenItems);
                        invItemModel.add(invItemSet);
                        
                        ((DropDownField)tableRow.get(3)).setModel(invItemModel);
                        ((DropDownField)tableRow.get(3)).setValue(invItemModel.get(0));
                        */
                        //set the text boxes
                        //itemDescText.setText((String)desc.getValue());
                       // itemStoreText.setText((String)store.getValue());
                       // itemDisUnitsText.setText((String)disUnits.getValue());
                        
                        /*
                        DataMap map = (DataMap)tableRow.getData();
                        if(map == null)
                            map = new DataMap();
                        
                        map.put("itemDesc", itemDesc);
                        map.put("itemStore", itemStore);
                        map.put("itemDisUnit", itemDisUnit);
                        map.put("itemIsBulk", isBulk);
                        map.put("itemIsLotMaintained", isLotMaintained);
                        map.put("itemIsSerialMaintained", isSerialMaintained);
                        */
                    }
                        
                    window.setStatus("","");
                }
                
                public void onFailure(Throwable caught){
                    Window.alert(caught.getMessage());
                    window.setStatus("","");
                }
            });
            
        }else if(col == 3 && row > -1 && row < receiptsTable.model.numRows()){
            DataSet tableRow = receiptsTable.model.getRow(row);
            ArrayList selections = (ArrayList)((DropDownField)tableRow.get(3)).getSelections();
           
            DataSet set = null;
            if(selections.size() > 0)
                set = (DataSet)selections.get(0);
            
           if(set != null && set.size() > 1){
                //set the text boxes
               DataMap dropdownMap = (DataMap)set.getData();
               StringField descObj = (StringField)dropdownMap.get("desc");
               StringField isBulkObj = (StringField)dropdownMap.get("isBulk");
               StringField isLotMaintainedObj = (StringField)dropdownMap.get("isLotMaintained");            
               StringField isSerialMaintainedObj = (StringField)dropdownMap.get("isSerialMaintained");
               StringField dispensedUnits = (StringField)dropdownMap.get("dispensedUnits");
               
               itemDescText.setText((String)descObj.getValue());
               itemStoreText.setText((String)((StringField)set.get(1)).getValue());
               itemDisUnitsText.setText((String)dispensedUnits.getValue());
                
                DataMap map = (DataMap)tableRow.getData();
                if(map == null)
                    map = new DataMap();
                
                map.put("itemDesc", descObj);
                map.put("itemStore", set.get(1));
                map.put("itemIsBulk", isBulkObj);
                map.put("itemIsLotMaintained", isLotMaintainedObj);
                map.put("itemIsSerialMaintained", isSerialMaintainedObj);
                map.put("itemDisUnit", dispensedUnits);
                
                tableRow.setData(map);
           }
        }else if(col == 4 && row > -1 && row < receiptsTable.model.numRows()){
            DataSet tableRow = receiptsTable.model.getRow(row);
            ArrayList selections = (ArrayList)((DropDownField)tableRow.get(4)).getSelections();
            DataSet set = null;
            if(selections.size() > 0)
                set = (DataSet)selections.get(0);
            
            if(set != null && set.size() > 1){
                DataMap orgDropdownMap = (DataMap)set.getData();
                //set the text boxes
                orgAddressText.setText((String)((StringObject)set.get(1)).getValue());
                orgCityText.setText((String)((StringObject)set.get(2)).getValue());
                orgStateText.setText((String)((StringObject)set.get(3)).getValue());               
                orgAptSuiteText.setText((String)((StringObject)orgDropdownMap.get("aptSuite")).getValue());
                orgZipCodeText.setText((String)((StringObject)orgDropdownMap.get("zipCode")).getValue());
                
                StringField multUnit = new StringField();
                StringField streetAddress = new StringField();
                StringField city = new StringField();
                StringField state = new StringField();
                StringField zipCode = new StringField();
                
                multUnit.setValue((String)((StringObject)orgDropdownMap.get("aptSuite")).getValue());
                streetAddress.setValue((String)((StringObject)set.get(1)).getValue());
                city.setValue((String)((StringObject)set.get(2)).getValue());
                state.setValue((String)((StringObject)set.get(3)).getValue());
                zipCode.setValue((String)((StringObject)orgDropdownMap.get("zipCode")).getValue());
                
                DataMap map = (DataMap)tableRow.getData();
                if(map == null)
                    map = new DataMap();
                
                map.put("multUnit", multUnit);
                map.put("streetAddress", streetAddress);
                map.put("city", city);
                map.put("state", state);
                map.put("zipCode", zipCode);
                tableRow.setData(map);
                
            }
        }
    }

    public void startEditing(SourcesTableWidgetEvents sender, int row, int col) {}

    public void stopEditing(SourcesTableWidgetEvents sender, int row, int col) {}
    //
    //end table listener methods
    //
    
    
    private boolean tableRowEmpty(DataSet row, boolean checkFirstColumn){
        boolean empty = true;
        
        if(checkFirstColumn){
            for(int i=0; i<row.size(); i++){
                if(i != 1 && row.get(i).getValue() != null && !"".equals(row.get(i).getValue())){
                    empty = false;
                    break;
                }
            }
        }else{
            if(row.get(0).getValue() == null || "".equals(row.get(0).getValue()))
                return false;
            
            //we dont need to check the first column
            for(int i=2; i<row.size(); i++){
                if(row.get(i).getValue() != null && !"".equals(row.get(i).getValue())){
                    empty = false;
                    break;
                }
            }
        }
        return empty;
    }
    
    private void onRemoveReceiptRowButtonClick() {
        int selectedRow = receiptsTable.model.getSelectedIndex();
        if (selectedRow > -1 && receiptsTable.model.numRows() > 0) {
            receiptsTable.model.deleteRow(selectedRow);

        }
    }
    
    private void loadReceiptsTableFromModel(DataModel model){
        loadReceiptsTableFromModel(model, -1);
    }
    
    //first method: hand datamap off row
    private void loadDataMapIntoTableRow(DataSet row){
        DataMap map = (DataMap)row.getData();
        
        row.get(0).setValue(getValueFromHashWithNulls(map, "orderNumber"));
        row.get(1).setValue(getValueFromHashWithNulls(map, "receivedDate"));
        row.get(2).setValue(getValueFromHashWithNulls(map, "upc"));
        
        if(map.get("inventoryItem") != null){
            ((DropDownField)row.get(3)).setModel(((DropDownField)map.get("inventoryItem")).getModel());
            ((DropDownField)row.get(3)).setValue(((DropDownField)map.get("inventoryItem")).getSelections());
        }
        if(map.get("org") != null){
            ((DropDownField)row.get(4)).setModel(((DropDownField)map.get("org")).getModel());
            ((DropDownField)row.get(4)).setValue(((DropDownField)map.get("org")).getSelections());
        }
        
        row.get(5).setValue(getValueFromHashWithNulls(map, "qtyReceived"));
        row.get(6).setValue(getValueFromHashWithNulls(map, "qtyRequested"));
        row.get(7).setValue(getValueFromHashWithNulls(map, "cost"));
        row.get(8).setValue(getValueFromHashWithNulls(map, "qc"));
        row.get(9).setValue(getValueFromHashWithNulls(map, "extRef"));
    }
    //2nd method: set data from hidden data (param: table row object)
    private void createRowAndSetRowData(int row, DataMap tableData){
        DataSet tableRow;
        if(row > -1)
            tableRow = receiptsTable.model.getRow(row);
        else
            tableRow = receiptsTable.model.createRow();   
        
        tableRow.setData(tableData);
        
        loadDataMapIntoTableRow(tableRow);
        
        if(row > -1)
            receiptsTable.model.refresh();
        else
            receiptsTable.model.addRow(tableRow);
    }
    
    //helper method: get value from hash, dealing with nulls (params: hash, key)
    private Object getValueFromHashWithNulls(HashMap<String, Data> hash, String key){
        if(hash.get(key) != null)
            return ((DataObject)hash.get(key)).getValue();
            
       return null;
    }
    
    //if row > -1 we will start from that row
    private void loadReceiptsTableFromModel(DataModel model, int row){
        /*for(int i=0; i<model.size(); i++){
            DataSet set = model.get(i);
            DataMap fetchMap = (DataMap)set.get(0);
            
            DataSet tableRow;
            if(i==0 && row > -1)
                tableRow = receiptsTable.model.getRow(row);
            else
                tableRow = receiptsTable.model.createRow();    
            
            tableRow.get(0).setValue(fetchMap.get("orderNumber").getValue());
            tableRow.get(1).setValue(fetchMap.get("receivedDate").getValue());
            tableRow.get(2).setValue(fetchMap.get("upc").getValue());
            ((DropDownField)tableRow.get(3)).setModel(((DropDownField)fetchMap.get("inventoryItem")).getModel());
            ((DropDownField)tableRow.get(3)).setValue(((DropDownField)fetchMap.get("inventoryItem")).getSelections());
            ((DropDownField)tableRow.get(4)).setModel(((DropDownField)fetchMap.get("org")).getModel());
            ((DropDownField)tableRow.get(4)).setValue(((DropDownField)fetchMap.get("org")).getSelections());
            tableRow.get(5).setValue(fetchMap.get("qtyReceived").getValue());
            tableRow.get(6).setValue(fetchMap.get("qtyRequested").getValue());
            tableRow.get(7).setValue(fetchMap.get("cost").getValue());
            tableRow.get(8).setValue(fetchMap.get("qc").getValue());
            tableRow.get(9).setValue(fetchMap.get("extRef").getValue());
            
            //we may need to disable some columns
            CheckField disableOrderId = new CheckField();
            CheckField disableUpc = new CheckField();
            CheckField disableInvItem = new CheckField();
            CheckField disableOrg = new CheckField();
            
            //if we have an order id then it was auto added and we need to disable these columns
            if(fetchMap.get("orderNumber").getValue() != null){
                disableOrderId.setValue(CheckBox.CHECKED);
                disableUpc.setValue(CheckBox.CHECKED);
                disableInvItem.setValue(CheckBox.CHECKED);
                disableOrg.setValue(CheckBox.CHECKED);
            
                fetchMap.put("disableOrderId", disableOrderId);
                fetchMap.put("disableUpc", disableUpc);
                fetchMap.put("disableInvItem", disableInvItem);
                fetchMap.put("disableOrg", disableOrg);
            }
            
            tableRow.setData(fetchMap);
            
            if(i > 0 || row == -1)
                receiptsTable.model.addRow(tableRow);
        }
        
        receiptsTable.model.refresh();
        */
    }

    //
    //auto complete call
    //
    public void callForMatches(final AutoComplete widget, DataModel model, String text) {
        
        DataMap params = new DataMap();
        params.put("addToExisting", rpc.getField("addToExisting"));
        StringObject catObj = new StringObject(widget.cat);
        StringObject matchObj = new StringObject(text);
        
        
        // prepare the argument list for the getObject function
        Data[] args = new Data[] {catObj, model, matchObj, params}; 
        
        
        screenService.getObject("getMatchesObj", args, new AsyncCallback() {
            public void onSuccess(Object result) {
                widget.showAutoMatches((DataModel)result);
            }
            
            public void onFailure(Throwable caught) {
                if(caught instanceof FormErrorException){
                    window.setStatus(caught.getMessage(), "ErrorPanel");
                }else
                    Window.alert(caught.getMessage());
            }
        });
    }

    //
    //start table model listener methods
    //
    public void cellUpdated(SourcesTableModelEvents sender, int row, int cell) {}

    public void dataChanged(SourcesTableModelEvents sender) {}

    public void rowAdded(SourcesTableModelEvents sender, int rows) {}

    public void rowDeleted(SourcesTableModelEvents sender, int row) {}

    public void rowSelectd(SourcesTableModelEvents sender, int row) {
        DataSet tableRow=null;
        
        if(!addToExisiting.isEnabled() && receiptsTable.model.numRows() > 0 && (state == State.ADD || state == State.UPDATE)){
            itemLotNum.enable(true);
            itemLocation.enable(true);
            itemExpDate.enable(true);
            addToExisiting.enable(true);
        }
        
        if(row >=0 && receiptsTable.model.numRows() > row)
            tableRow = receiptsTable.model.getRow(row);
        
        DataMap map = new DataMap();
        if(tableRow != null && tableRow.getData() != null)
            map = (DataMap)tableRow.getData();
        
        if(tableRow != null){
            if(map.get("streetAddress") != null && map.get("city") != null){
                orgAptSuiteText.setText((String)((StringField)map.get("multUnit")).getValue());
                orgAddressText.setText((String)((StringField)map.get("streetAddress")).getValue());
                orgCityText.setText((String)((StringField)map.get("city")).getValue());
                orgStateText.setText((String)((StringField)map.get("state")).getValue());
                orgZipCodeText.setText((String)((StringField)map.get("zipCode")).getValue());
            }
            
            if(map.get("itemDesc") != null)
                itemDescText.setText((String)((StringField)map.get("itemDesc")).getValue());
            if(map.get("itemStore") != null)
                itemStoreText.setText((String)((StringField)map.get("itemStore")).getValue());
            if(map.get("itemDisUnit") != null)
                itemDisUnitsText.setText((String)((StringField)map.get("itemDisUnit")).getValue());
            
            if(map.get(InventoryReceiptMeta.TRANS_RECEIPT_LOCATION_META.INVENTORY_LOCATION_META.getStorageLocationId()) != null){
                ((AutoComplete)itemLocation.getWidget()).model.load(((DropDownField)map.get(InventoryReceiptMeta.TRANS_RECEIPT_LOCATION_META.INVENTORY_LOCATION_META.getStorageLocationId())).getModel());
                ((AutoComplete)itemLocation.getWidget()).setSelections(((DropDownField)map.get(InventoryReceiptMeta.TRANS_RECEIPT_LOCATION_META.INVENTORY_LOCATION_META.getStorageLocationId())).getSelections());
            }
            else
                ((AutoComplete)itemLocation.getWidget()).setSelections(new ArrayList<DataSet>());
            
            if(map.get(InventoryReceiptMeta.TRANS_RECEIPT_LOCATION_META.INVENTORY_LOCATION_META.getLotNumber()) != null)
                ((TextBox)itemLotNum.getWidget()).setText((String)((StringField)map.get(InventoryReceiptMeta.TRANS_RECEIPT_LOCATION_META.INVENTORY_LOCATION_META.getLotNumber())).getValue());
            else
                ((TextBox)itemLotNum.getWidget()).setText("");
            
            if(map.get(InventoryReceiptMeta.TRANS_RECEIPT_LOCATION_META.INVENTORY_LOCATION_META.getExpirationDate()) != null && 
                            ((DateField)map.get(InventoryReceiptMeta.TRANS_RECEIPT_LOCATION_META.INVENTORY_LOCATION_META.getExpirationDate())).getValue() != null)
                ((FormCalendarWidget)itemExpDate.getWidget()).setText(((DatetimeRPC)((DateField)map.get(InventoryReceiptMeta.TRANS_RECEIPT_LOCATION_META.INVENTORY_LOCATION_META.getExpirationDate())).getValue()).toString());
            else
                ((FormCalendarWidget)itemExpDate.getWidget()).setText("");
            
            if(map.get("addToExisting") != null)
                ((CheckBox)addToExisiting.getWidget()).setState((String)((CheckField)map.get("addToExisting")).getValue());
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
            itemDisUnitsText.setText("");
            ((AutoComplete)itemLocation.getWidget()).setSelections(new ArrayList<DataSet>());
            ((TextBox)itemLotNum.getWidget()).setText("");
            ((FormCalendarWidget)itemExpDate.getWidget()).setText("");
            ((CheckBox)addToExisiting.getWidget()).setState(CheckBox.UNCHECKED);
        }
    }

    public void rowUnselected(SourcesTableModelEvents sender, int row) {}

    public void rowUpdated(SourcesTableModelEvents sender, int row) {}

    public void unload(SourcesTableModelEvents sender) {}
    //
    //end table model listener methods
    //
}