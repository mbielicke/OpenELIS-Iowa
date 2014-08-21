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

import org.openelis.gwt.common.FormErrorException;
import org.openelis.gwt.common.data.DataModel;
import org.openelis.gwt.common.data.DataSet;
import org.openelis.gwt.common.data.DropDownField;
import org.openelis.gwt.common.data.KeyListManager;
import org.openelis.gwt.common.data.StringField;
import org.openelis.gwt.common.data.StringObject;
import org.openelis.gwt.screen.CommandChain;
import org.openelis.gwt.screen.ScreenAutoCompleteWidget;
import org.openelis.gwt.screen.ScreenCalendar;
import org.openelis.gwt.screen.ScreenCheck;
import org.openelis.gwt.screen.ScreenQueryTable;
import org.openelis.gwt.screen.ScreenResultsTable;
import org.openelis.gwt.screen.ScreenTextBox;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.AutoComplete;
import org.openelis.gwt.widget.AutoCompleteCallInt;
import org.openelis.gwt.widget.ButtonPanel;
import org.openelis.gwt.widget.CalendarLookUp;
import org.openelis.gwt.widget.CheckBox;
import org.openelis.gwt.widget.FormInt;
import org.openelis.gwt.widget.ResultsTable;
import org.openelis.gwt.widget.AppButton.ButtonState;
import org.openelis.gwt.widget.table.QueryTable;
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

public class InventoryReceiptScreen extends OpenELISScreenForm<InventoryReceiptRPC, InventoryReceiptForm, Integer> implements ClickListener, ChangeListener, TableManager, TableWidgetListener, TableModelListener, AutoCompleteCallInt {
    
    private ResultsTable receiptsTable;
    private QueryTable receiptsQueryTable;
    
    private AppButton        removeReceiptButton;
    private TextBox orgAptSuiteText, orgAddressText, orgCityText, orgStateText, orgZipCodeText,
                    itemDescText, itemStoreText, itemDisUnitsText;
    ScreenTextBox itemLotNum;
    private ScreenAutoCompleteWidget itemLocation;
    private ScreenCalendar itemExpDate;
    private ScreenCheck addToExisiting;
    private ButtonPanel      atozButtons;
    private KeyListManager   keyList = new KeyListManager();
    private String screenType;
    private Integer currentEditingRow = -1;
    private String tableCheckedValue;
    private InventoryReceiptMetaMap InventoryReceiptMeta = new InventoryReceiptMetaMap();
    
    public InventoryReceiptScreen() {                
        super("org.openelis.modules.inventoryReceipt.server.InventoryReceiptService");
        
        screenType = "receipt";
      
        forms.put("display",new InventoryReceiptForm());
        InventoryReceiptRPC receiptRPC = new InventoryReceiptRPC();
        receiptRPC.screenType = screenType;
        
        getScreen(receiptRPC);
    }
    
    public InventoryReceiptScreen(Object[] args) {                
        super("org.openelis.modules.inventoryReceipt.server.InventoryReceiptService");
        
        screenType = (String)((StringObject)args[0]).getValue();
        
        forms.put("display",new InventoryReceiptForm());
        InventoryReceiptRPC receiptRPC = new InventoryReceiptRPC();
   //     receiptRPC.screenType = screenType;
        
        getScreen(receiptRPC);
    }

    public void performCommand(Enum action, Object obj) {
        if(action == KeyListManager.Action.FETCH){
            if(state == State.DISPLAY || state == State.DEFAULT){
                if(keyList.getList().size() > 0)
                    changeState(State.DISPLAY);
                else
                    changeState(State.DEFAULT);
            }
        }else if(action == KeyListManager.Action.SELECTION){
            //do nothing for now
        }else if(action == KeyListManager.Action.GETPAGE){
            //do nothing for now
        }else{
            super.performCommand(action, obj);
        }
    } 

    
    public void onClick(Widget sender) {
        if (sender == removeReceiptButton)
            onRemoveReceiptRowButtonClick();
        else if(sender == addToExisiting && addToExisiting.isEnabled() && receiptsTable.model.getSelectedIndex() > -1 && receiptsTable.model.numRows() > 0){
            DataSet tableRow = receiptsTable.model.getRow(receiptsTable.model.getSelectedIndex());
            InvReceiptItemInfoRPC hiddenRPC = (InvReceiptItemInfoRPC)tableRow.getKey();
            
            if(((CheckBox)addToExisiting.getWidget()).getState() == CheckBox.CHECKED)
                hiddenRPC.form.addToExisting.setValue(CheckBox.UNCHECKED);

            else
                hiddenRPC.form.addToExisting.setValue(CheckBox.CHECKED);
            
            ((AutoComplete)itemLocation.getWidget()).setSelections(null);
            hiddenRPC.form.storageLocationId.clear();
        }
    }
    
    public void onChange(Widget sender) {
        super.onChange(sender);
         
        if(receiptsTable.model.numRows() > 0){
            if(sender == itemLocation.getWidget()){
                DataSet<InvReceiptItemInfoRPC> tableRow = receiptsTable.model.getRow(receiptsTable.model.getSelectedIndex());
                InvReceiptItemInfoRPC hiddenRPC = tableRow.getKey();
                hiddenRPC.form.storageLocationId.setValue(((AutoComplete)itemLocation.getWidget()).getSelections());
                
            }else if(sender == itemLotNum.getWidget()){
                DataSet<InvReceiptItemInfoRPC> tableRow = receiptsTable.model.getRow(receiptsTable.model.getSelectedIndex());
                InvReceiptItemInfoRPC hiddenRPC = tableRow.getKey();
                hiddenRPC.form.lotNumber.setValue(((TextBox)itemLotNum.getWidget()).getText());
                
            }else if(sender == itemExpDate.getWidget()){
                DataSet<InvReceiptItemInfoRPC> tableRow = receiptsTable.model.getRow(receiptsTable.model.getSelectedIndex());
                InvReceiptItemInfoRPC hiddenRPC = tableRow.getKey();
                hiddenRPC.form.expirationDate.setValue((String)((CalendarLookUp)itemExpDate.getWidget()).getValue());
                
            }
        }
    }
    
    public void afterDraw(boolean sucess) {
        //AToZTable atozTable;
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
        ScreenResultsTable sw = (ScreenResultsTable)widgets.get("receiptsTable");
        receiptsTable = (ResultsTable)sw.getWidget();
        receiptsQueryTable = (QueryTable)((ScreenQueryTable)sw.getQueryWidget()).getWidget();
        receiptsTable.model.enableAutoAdd(false);
        receiptsTable.table.addTableWidgetListener(this);
        receiptsTable.model.addTableModelListener(this);
        receiptsTable.model.setManager(this); //TODO this is here for now..take out when not needed
        
        //atozTable = (AToZTable)getWidget("azTable");
        ButtonPanel bpanel = (ButtonPanel)getWidget("buttons");
        //atozButtons = (ButtonPanel)getWidget("atozButtons");
        
        CommandChain formChain = new CommandChain();
        formChain.addCommand(this);
        formChain.addCommand(bpanel);
        formChain.addCommand(keyList);
        formChain.addCommand(receiptsTable);
        //formChain.addCommand(atozButtons);
        
        updateChain.add(afterUpdate);
        commitUpdateChain.add(afterCommitUpdate);
        commitAddChain.add(afterCommitAdd);
        
        super.afterDraw(sucess);
        
        rpc.form.setFieldValue("receiptsTable", receiptsTable.model.getData());
    }
    protected AsyncCallback afterUpdate = new AsyncCallback() {
        public void onSuccess(Object result){
            if("transfer".equals(screenType)){
                removeReceiptButton.changeState(ButtonState.DISABLED);
            }
        }
        
        public void onFailure(Throwable caught){
            
        }
    };
    
    
    public void add() {
        super.add();
        receiptsTable.model.enableAutoAdd(true);
        
        itemLotNum.enable(false);
        if(itemLocation != null)
            itemLocation.enable(false);
        itemExpDate.enable(false);
        if(addToExisiting != null)
            addToExisiting.enable(false);
        receiptsTable.table.select(0, 0);
        
        //TODO not sure how to replace this  receiptsController.onCellClicked((SourcesTableEvents)receiptsController.view.table, 0, 0);
       
    }
    
    /*
    public void fetch() {
        //we dont need to call the super because we arent using the datamodel for lookups
        //we are using it to fill the table
        //super.fetch();
        
        DataModel<DataSet> model = keyList.getList();
        
        receiptsTable.model.clear();
        
        for(int i=0; i<model.size(); i++){
            createRowAndSetRowData(i, (DataMap)model.get(i).getData());    
        }
        
        //loadReceiptsTableFromModel(model);
               
        if(model.size() > 0)
            changeState(State.DISPLAY);
       
    }*/
    public void fetch() {
        //do nothing
    }
    
    public void query() {
        super.query();
        
        if("receipt".equals(screenType)){
        addToExisiting.enable(false);
        itemExpDate.enable(false);
        itemLotNum.enable(false);
        itemLocation.enable(false);
        }
        removeReceiptButton.changeState(AppButton.ButtonState.DISABLED);
        receiptsQueryTable.select(0,0);
        
    }
    
    public void update() {
        window.setStatus(consts.get("lockForUpdate"),"spinnerIcon");
        
        InventoryReceiptRPC irrpc = new InventoryReceiptRPC();
        irrpc.key = rpc.key;
        irrpc.form = rpc.form;
        irrpc.tableRows = keyList.getList(); 
        
        screenService.call("commitQueryAndLock", irrpc, new AsyncCallback<InventoryReceiptRPC>(){
            public void onSuccess(InventoryReceiptRPC result){           
                resetForm();
                load();
                //keyList.setModel(result.tableRows);
                //keyList.select(0);
                
                enable(true);
                window.setStatus(consts.get("updateFields"),"");
                changeState(State.UPDATE);
                receiptsTable.model.load(result.tableRows);
                receiptsTable.table.select(0, 0);
            }
            
            public void onFailure(Throwable caught){
                Window.alert(caught.getMessage());
          //      enable(true);
                window.setStatus("","");
          //      changeState(State.UPDATE);
            }
        });
    }
    
    public void abort() {
        receiptsTable.table.finishEditing();
        receiptsTable.model.enableAutoAdd(false);
        
        if (state == State.UPDATE) {
            window.setStatus("","spinnerIcon");
            clearErrors();
            resetForm();
            load();
            enable(false);
            
            InventoryReceiptRPC irrpc = new InventoryReceiptRPC();
            irrpc.key = rpc.key;
            irrpc.form = rpc.form;
            irrpc.tableRows = keyList.getList(); 
            
            screenService.call("commitQueryAndUnlock", irrpc, new AsyncCallback<InventoryReceiptRPC>(){
                public void onSuccess(InventoryReceiptRPC result){                    
                    receiptsTable.model.load(result.tableRows);

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
    
    protected AsyncCallback afterCommitUpdate = new AsyncCallback() {
        public void onFailure(Throwable caught) {   
        }
        public void onSuccess(Object result) {
            receiptsTable.model.enableAutoAdd(false);
            
        }
    };
    
    protected AsyncCallback afterCommitAdd = new AsyncCallback() {
        public void onFailure(Throwable caught) {   
        }
        public void onSuccess(Object result) {
            receiptsTable.model.enableAutoAdd(false);
            
        }
    };
    
    //
    //start table manager methods
    //
    public boolean canAdd(TableWidget widget, DataSet set, int row) {
        return true;
    }

    public boolean canAutoAdd(TableWidget widget, DataSet addRow) {
        if(state != State.UPDATE){
            if("receipt".equals(screenType))
                return !receiptTableRowEmpty(addRow, true);
            else
                return !transferTableRowEmpty(addRow);
                
        }
        
        return false;
    }

    public boolean canDelete(TableWidget widget, DataSet set, int row) {
        return true;
    }

    public boolean canEdit(TableWidget widget, DataSet set, int row, int col) {
        if(state != State.UPDATE && state != State.ADD)
            return false;
        
        int numRows = receiptsTable.model.numRows();
        if("receipt".equals(screenType)){
            if(col == 0 && row > -1){
                DataSet<InvReceiptItemInfoRPC> tableRow = receiptsTable.model.getRow(row);
                InvReceiptItemInfoRPC hiddenRPC = tableRow.getKey();
                
                //order id is disabled on auto created rows
                if(hiddenRPC != null && hiddenRPC.disableOrderId)
                    return false;
           
            }else if(col == 2 && row > -1){
                DataSet<InvReceiptItemInfoRPC> tableRow = receiptsTable.model.getRow(row);
                InvReceiptItemInfoRPC hiddenRPC = tableRow.getKey();
                
                //upc is disabled on auto created rows
                if(hiddenRPC != null && hiddenRPC.disableUpc)
                    return false;
                
            }else if(col == 3 && row > -1){
                DataSet<InvReceiptItemInfoRPC> tableRow = receiptsTable.model.getRow(row);
                InvReceiptItemInfoRPC hiddenRPC = tableRow.getKey();
                
                //inv item is disabled on auto created rows
                if(hiddenRPC != null && hiddenRPC.disableInvItem)
                    return false;
                
            }else if(col == 4 && row > -1){
                DataSet<InvReceiptItemInfoRPC> tableRow = receiptsTable.model.getRow(row);
                InvReceiptItemInfoRPC hiddenRPC = tableRow.getKey();
                
                //upc is disabled on auto created rows
                if(hiddenRPC != null && hiddenRPC.disableOrg)
                    return false;
            }
            currentEditingRow = row;
            return true;
        }else{
            if(state == FormInt.State.UPDATE || state == FormInt.State.ADD){
            currentEditingRow = row;
            
            if(col == 3 && receiptsTable.model.numRows() > 0)
                tableCheckedValue = (String)receiptsTable.model.getCell(row, 3);
            
            return true;
            }
        }
        return false;
    }

    public boolean canSelect(TableWidget widget, DataSet set, int row) {
        return true;
    }
    
    public boolean canDrag(TableWidget widget, DataSet item, int row) {
        return false;
    }

    public boolean canDrop(TableWidget widget, Widget dragWidget, DataSet dropTarget, int targetRow) {
        return false;
    }

    public void drop(TableWidget widget, Widget dragWidget, DataSet dropTarget, int targetRow) {}

    public void drop(TableWidget widget, Widget dragWidget) {}
    //
    //end table manager methods
    //
    
    //
    //start table listener methods
    //
    public void finishedEditing(SourcesTableWidgetEvents sender, final int row, int col) {
        if("receipt".equals(screenType)){
            //we need to try and lookup the order using the order number that they have entered
            if(col == 0 && row < receiptsTable.model.numRows() && receiptTableRowEmpty(receiptsTable.model.getRow(row), false)){
                window.setStatus("","spinnerIcon");

                // prepare the argument list
                InventoryReceiptRPC irrpc = new InventoryReceiptRPC();
                irrpc.key = rpc.key;
                irrpc.form = rpc.form;
                irrpc.orderId = (Integer)receiptsTable.model.getCell(row, 0);
                
                screenService.call("getReceipts", irrpc, new AsyncCallback<InventoryReceiptRPC>(){
                    public void onSuccess(InventoryReceiptRPC result){    
                        if(result.tableRows.size() > 0){
                            createReceiptRows(row, result.tableRows);
                            receiptsTable.table.select(row, 1);
                        }
                        
                        window.setStatus("","");
                    }
                    
                    public void onFailure(Throwable caught){
                        Window.alert(caught.getMessage());
                        window.setStatus("","");
                    }
                });
            }else if(col == 2 && row > -1 && row < receiptsTable.model.numRows()){
                final DataSet<InvReceiptItemInfoRPC> tableRow = receiptsTable.model.getRow(row);
                final String upcValue = (String)receiptsTable.model.getCell(row, 2);
                
                if(upcValue != null && !"".equals(upcValue)){
                window.setStatus("","spinnerIcon");

                //prepare the argument list
                InventoryReceiptRPC irrpc = new InventoryReceiptRPC();
                irrpc.key = rpc.key;
                irrpc.form = rpc.form;
                irrpc.upcValue = upcValue;
                
                screenService.call("getInvItemFromUPC", irrpc, new AsyncCallback<InventoryReceiptRPC>(){
                    public void onSuccess(InventoryReceiptRPC result){   
                        if(result.invItemsByUPC.size() > 1){
                            //we need to have the user select which item they want
                        }else if(result.invItemsByUPC.size() == 1){
                            //we need to set the values in the datamap
                            
                            InvReceiptItemInfoRPC hiddenRPC = (InvReceiptItemInfoRPC)tableRow.getKey();
                            if(hiddenRPC == null){
                                hiddenRPC = new InvReceiptItemInfoRPC();
                                
                                hiddenRPC.form = new InvReceiptItemInfoForm(rpc.form.itemInformation.node);
                                tableRow.setKey(hiddenRPC);
                            }
                            
                            DataSet set = result.invItemsByUPC.get(0);
                       
                            //name
                            DataModel<Integer> invItemModel = new DataModel<Integer>();
                            invItemModel.add(new DataSet<Integer>((Integer)set.getKey(), set.get(0)));
               
                            hiddenRPC.disableInvItem = true;
                            hiddenRPC.disableOrderId = true;
                            hiddenRPC.itemIsSerialMaintained = ((StringObject)set.get(6)).getValue();
                            hiddenRPC.itemIsLotMaintained = ((StringObject)set.get(5)).getValue();;
                            hiddenRPC.itemIsBulk = ((StringObject)set.get(4)).getValue();;
                            hiddenRPC.form.description.setValue(((StringObject)set.get(2)).getValue());
                            hiddenRPC.form.dispensedUnits.setValue(((StringObject)set.get(3)).getValue());
                            hiddenRPC.form.storeId.setValue(((StringObject)set.get(1)).getValue());
                            
                            ((DropDownField<Integer>)tableRow.get(3)).setModel(invItemModel);
                            ((DropDownField<Integer>)tableRow.get(3)).setValue(invItemModel.get(0));
                            
                            receiptsTable.model.refresh();
                            
                            //set the text boxes
                            itemDescText.setText(hiddenRPC.form.description.getValue());
                            itemStoreText.setText(hiddenRPC.form.storeId.getValue());
                            itemDisUnitsText.setText(hiddenRPC.form.dispensedUnits.getValue());
                        }
                            
                        window.setStatus("","");
                    }
                    
                    public void onFailure(Throwable caught){
                        Window.alert(caught.getMessage());
                        window.setStatus("","");
                    }
                });
                }
                
            }else if(col == 3 && row > -1 && row < receiptsTable.model.numRows()){
                DataSet<InvReceiptItemInfoRPC> tableRow = receiptsTable.model.getRow(row);
                ArrayList<DataSet<Integer>> selections = (ArrayList<DataSet<Integer>>)((DropDownField)tableRow.get(3)).getValue();
               
                DataSet<Integer> set = null;
                if(selections.size() > 0)
                    set = (DataSet<Integer>)selections.get(0);
                
               if(set != null && set.size() > 1){
                    //set the text boxes
                   ReceiptInvItemKey dropdownData = (ReceiptInvItemKey)set.getData();
                   
                   itemDescText.setText(dropdownData.desc);
                   itemStoreText.setText(((StringField)set.get(1)).getValue());
                   itemDisUnitsText.setText(dropdownData.dispensedUnits);
                    
                   InvReceiptItemInfoRPC hiddenRPC = (InvReceiptItemInfoRPC)tableRow.getKey();
                    if(hiddenRPC == null){
                        hiddenRPC = new InvReceiptItemInfoRPC();
                        
                        hiddenRPC.form = new InvReceiptItemInfoForm(rpc.form.itemInformation.node);
                        tableRow.setKey(hiddenRPC);
                    }
                    
                    hiddenRPC.form.description.setValue(dropdownData.desc);
                    hiddenRPC.form.storeId.setValue(((StringField)set.get(1)).getValue());
                    hiddenRPC.form.dispensedUnits.setValue(dropdownData.dispensedUnits);
                    hiddenRPC.itemIsBulk = dropdownData.isBulk;
                    hiddenRPC.itemIsLotMaintained = dropdownData.isLotMaintained;
                    hiddenRPC.itemIsSerialMaintained = dropdownData.isSerialMaintained;
                    
               }
            }else if(col == 4 && row > -1 && row < receiptsTable.model.numRows()){
                DataSet<InvReceiptItemInfoRPC> tableRow = receiptsTable.model.getRow(row);
                ArrayList<DataSet<Integer>> selections = (ArrayList<DataSet<Integer>>)((DropDownField)tableRow.get(4)).getValue();
                DataSet<Integer> set = null;
                if(selections.size() > 0)
                    set = (DataSet<Integer>)selections.get(0);
                
                if(set != null && set.size() > 1){
                    InvReceiptOrgKey orgKey = (InvReceiptOrgKey)set.getData();
                    //set the text boxes
                    orgAddressText.setText(((StringObject)set.get(1)).getValue());
                    orgCityText.setText(((StringObject)set.get(2)).getValue());
                    orgStateText.setText(((StringObject)set.get(3)).getValue());               
                    orgAptSuiteText.setText(orgKey.aptSuite);
                    orgZipCodeText.setText(orgKey.zipCode);
                    
                    InvReceiptItemInfoRPC hiddenRPC = (InvReceiptItemInfoRPC)tableRow.getKey();
                    if(hiddenRPC == null){
                        hiddenRPC = new InvReceiptItemInfoRPC();
                        
                        hiddenRPC.form = new InvReceiptItemInfoForm(rpc.form.itemInformation.node);
                        tableRow.setKey(hiddenRPC);
                    }
                    
                    hiddenRPC.form.multUnit.setValue(orgKey.aptSuite);
                    hiddenRPC.form.streetAddress.setValue(((StringObject)set.get(1)).getValue());
                    hiddenRPC.form.city.setValue(((StringObject)set.get(2)).getValue());
                    hiddenRPC.form.state.setValue(((StringObject)set.get(3)).getValue());
                    hiddenRPC.form.zipCode.setValue(orgKey.zipCode);
                    
                }
            }
        }else if("transfer".equals(screenType)){
            /*if(col == 0 && row < receiptsTable.model.numRows()){
                DataSet<Data> tableRow = receiptsTable.model.getRow(row);
                ArrayList selections = (ArrayList)((DropDownField)tableRow.get(0)).getValue();
               
                DataSet<Data> set = null;
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
                   DateField expDate = (DateField)dropdownMap.get("expDate");
                   StringField lotNum = (StringField)dropdownMap.get("lotNum");
                   DropDownField<Integer> storageLocation = new DropDownField<Integer>();
                   DataModel<Integer> locModel = new DataModel<Integer>();
                   locModel.add(new DataSet<Integer>((Integer)dropdownMap.get("locId").getValue(), new StringObject((String)set.get(2).getValue())));
                   storageLocation.setModel(locModel);
                   storageLocation.setValue(locModel.get(0));
                   
                   tableRow.get(1).setValue((String)set.get(2).getValue());
                   tableRow.get(2).setValue((Integer)set.get(3).getValue());
                   
                   itemDescText.setText((String)descObj.getValue());
                   itemStoreText.setText((String)((StringField)set.get(1)).getValue());
                   itemDisUnitsText.setText((String)dispensedUnits.getValue());
                   
                   ((TextBox)itemLotNum.getWidget()).setText((String)lotNum.getValue());
                   itemExpDate.load(expDate);                   
                    
                    DataMap map = (DataMap)tableRow.getData();
                    if(map == null)
                        map = new DataMap();
                    
                    map.put("itemDesc", descObj);
                    map.put("itemStore", set.get(1));
                    map.put("itemIsBulk", isBulkObj);
                    map.put("itemIsLotMaintained", isLotMaintainedObj);
                    map.put("itemIsSerialMaintained", isSerialMaintainedObj);
                    map.put("itemDisUnit", dispensedUnits);
                    map.put("expDate", expDate);
                    map.put("lotNum", lotNum);
                    map.put(InventoryReceiptMeta.TRANS_RECEIPT_LOCATION_META.INVENTORY_LOCATION_META.getStorageLocationId(), storageLocation);
                    
                    tableRow.setData(map);
                    receiptsTable.model.refresh();
               }
               
               if(receiptsTable.model.getCell(row, 6) != null && ((Integer)receiptsTable.model.getCell(row, 6)).compareTo((Integer)receiptsTable.model.getCell(row, 2)) > 0){
                   receiptsTable.model.clearCellError(row, 6);
                   receiptsTable.model.setCellError(row, 6, consts.get("notEnoughQuantityOnHand"));
               }
               
            }else if(col == 3 && row < receiptsTable.model.numRows()){
                DataSet tableRow = receiptsTable.model.getRow(row);
                ArrayList selections = (ArrayList)((DropDownField)tableRow.get(3)).getValue();
               
                DataSet set = null;
                if(selections.size() > 0)
                    set = (DataSet)selections.get(0);
                
               if(set != null && set.size() > 1){
                    //set the text boxes
                   DataMap dropdownMap = (DataMap)set.getData();
                   NumberField parentRatioObj = (NumberField)dropdownMap.get("parentRatio");
                   
                   DataMap map = (DataMap)tableRow.getData();
                    if(map == null)
                        map = new DataMap();
                    
                    map.put("parentRatio", parentRatioObj);
                    
                    tableRow.setData(map);
               }

            }else if(col == 4 && row < receiptsTable.model.numRows()){
                String checkedValue = (String)receiptsTable.model.getCell(row, 2);

                //if the checkbox changes value we need to clear out the to location column
                if(!checkedValue.equals(tableCheckedValue))
                    receiptsTable.model.setCell(row, 5 , null);
            }else if(col == 6 && row < receiptsTable.model.numRows()){ //qty column
                if(receiptsTable.model.getCell(row, 2) != null && ((Integer)receiptsTable.model.getCell(row, 2)).compareTo((Integer)receiptsTable.model.getCell(row, 6)) < 0){
                    receiptsTable.model.clearCellError(row, 6);
                    receiptsTable.model.setCellError(row, 6, consts.get("notEnoughQuantityOnHand"));
                }
                
            }*/
        }
    }

    public void startEditing(SourcesTableWidgetEvents sender, int row, int col) {}

    public void stopEditing(SourcesTableWidgetEvents sender, int row, int col) {}
    //
    //end table listener methods
    //
    
    
    private boolean receiptTableRowEmpty(DataSet<InvReceiptItemInfoRPC> row, boolean checkFirstColumn){
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
    
    
    private boolean transferTableRowEmpty(DataSet<InvReceiptItemInfoRPC> row){
        boolean empty = true;
        
        for(int i=0; i<row.size(); i++){
            if(row.get(i).getValue() != null && !"".equals(row.get(i).getValue())){
                empty = false;
                break;
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
/*    
    private void loadDataMapIntoTransferTableRow(DataSet<Data> row){
        DataMap map = (DataMap)row.getData();
        
        //row.get(0).setValue(getValueFromHashWithNulls(map, "orderNumber"));
        //row.get(1).setValue(getValueFromHashWithNulls(map, "receivedDate"));
        //row.get(2).setValue(getValueFromHashWithNulls(map, "upc"));
        
        //if(map.get("org") != null){
        //    ((DropDownField)row.get(4)).setModel(((DropDownField)map.get("org")).getModel());
        //    ((DropDownField)row.get(4)).setValue(((DropDownField)map.get("org")).getSelections());
       // }
        
        //row.get(5).setValue(getValueFromHashWithNulls(map, "qtyReceived"));
        
        if(map.get("fromInventoryItem") != null){
            ((DropDownField)row.get(0)).setModel(((DropDownField)map.get("fromInventoryItem")).getModel());
            ((DropDownField)row.get(0)).setValue(((DropDownField)map.get("fromInventoryItem")).getValue());
        }
        
        String fromLocationName = InventoryReceiptMeta.TRANS_RECEIPT_LOCATION_META.INVENTORY_LOCATION_META.getStorageLocationId();
        if(map.get(fromLocationName) != null){
            row.get(1).setValue(((DropDownField)map.get(fromLocationName)).getTextValue());
        }
        
        //if(map.get("inventoryItem") != null){
        //    ((DropDownField)row.get(2)).setModel(((DropDownField)map.get("inventoryItem")).getModel());
        //    ((DropDownField)row.get(2)).setValue(((DropDownField)map.get("inventoryItem")).getSelections());
       // }
        
        row.get(2).setValue(getValueFromHashWithNulls(map, "fromQtyOnHand"));
        
        if(map.get("inventoryItem") != null){
            ((DropDownField)row.get(3)).setModel(((DropDownField)map.get("inventoryItem")).getModel());
            ((DropDownField)row.get(3)).setValue(((DropDownField)map.get("inventoryItem")).getValue());
        }
        row.get(4).setValue(((CheckField)map.get("addToExisting")).getValue());
        
        if(map.get("toInventoryLocation") != null){
            ((DropDownField)row.get(5)).setModel(((DropDownField)map.get("toInventoryLocation")).getModel());
            ((DropDownField)row.get(5)).setValue(((DropDownField)map.get("toInventoryLocation")).getValue());
        }
        
        row.get(6).setValue(getValueFromHashWithNulls(map, "qtyRequested"));
        

    }
    */
    
    private void createReceiptRows(int row, DataModel<InvReceiptItemInfoRPC> model){
        for(int i=0; i<model.size(); i++)
            receiptsTable.model.addRow(model.get(i));
        
        //delete the row for now
        receiptsTable.model.deleteRow(row);
    }
    
    //
    //auto complete call
    //
    public void callForMatches(final AutoComplete widget, DataModel model, String text) {
        
        ReceiptInvLocationAutoRPC rilrpc = new ReceiptInvLocationAutoRPC();
        rilrpc.cat = widget.cat;
        rilrpc.match = text;
        
        if("toInventoryItemTrans".equals(widget.cat)){
            if(receiptsTable.model.numRows() > 0)
                rilrpc.invItemId = (Integer)((DropDownField)receiptsTable.model.getObject(currentEditingRow, 0)).getSelectedKey();

        }else{
            //add to existing
            if(addToExisiting != null)
                rilrpc.addToExisting = ((CheckBox)addToExisiting.getWidget()).getState();
            else{
                if(receiptsTable.model.numRows() > 0)
                    rilrpc.addToExisting = (String)receiptsTable.model.getCell(currentEditingRow, 4);
            }
            
            //inv item id
            if(receiptsTable.model.numRows() > 0) //<--this is for receipt
                rilrpc.invItemId = (Integer)((DropDownField)receiptsTable.model.getObject(currentEditingRow, 3)).getSelectedKey();
        }
        
        // prepare the argument list for the getObject function
        screenService.call("getMatchesCall", rilrpc, new AsyncCallback<ReceiptInvLocationAutoRPC>() {
            public void onSuccess(ReceiptInvLocationAutoRPC result) {
                widget.showAutoMatches(result.autoMatches);
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

    public void rowSelected(SourcesTableModelEvents sender, int row) {
        DataSet<InvReceiptItemInfoRPC> tableRow=null;
        if(addToExisiting != null && !addToExisiting.isEnabled() && receiptsTable.model.numRows() > 0 && (state == State.ADD || state == State.UPDATE)){
            itemLotNum.enable(true);
            itemLocation.enable(true);
            itemExpDate.enable(true);
            addToExisiting.enable(true);
        }
        
        if(row >=0 && receiptsTable.model.numRows() > row)
            tableRow = receiptsTable.model.getRow(row);
        
        if(tableRow != null && tableRow.getKey() != null)
            load(tableRow.getKey().form);
        else{
            resetForm(rpc.form.itemInformation);
            load(rpc.form.itemInformation);
        }
    }

    public void rowUnselected(SourcesTableModelEvents sender, int row) {}

    public void rowUpdated(SourcesTableModelEvents sender, int row) {}

    public void unload(SourcesTableModelEvents sender) {}
    //
    //end table model listener methods
    //
}