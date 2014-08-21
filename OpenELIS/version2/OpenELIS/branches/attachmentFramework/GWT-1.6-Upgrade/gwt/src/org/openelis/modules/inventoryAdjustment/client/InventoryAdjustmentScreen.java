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
package org.openelis.modules.inventoryAdjustment.client;

import java.util.ArrayList;

import org.openelis.gwt.common.FormErrorException;
import org.openelis.gwt.common.data.DataModel;
import org.openelis.gwt.common.data.DataSet;
import org.openelis.gwt.common.data.DropDownField;
import org.openelis.gwt.common.data.IntegerField;
import org.openelis.gwt.common.data.IntegerObject;
import org.openelis.gwt.common.data.KeyListManager;
import org.openelis.gwt.common.data.StringObject;
import org.openelis.gwt.screen.CommandChain;
import org.openelis.gwt.screen.ScreenCalendar;
import org.openelis.gwt.screen.ScreenDropDownWidget;
import org.openelis.gwt.screen.ScreenTextBox;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.AutoComplete;
import org.openelis.gwt.widget.AutoCompleteCallInt;
import org.openelis.gwt.widget.ButtonPanel;
import org.openelis.gwt.widget.Dropdown;
import org.openelis.gwt.widget.FormInt;
import org.openelis.gwt.widget.table.TableManager;
import org.openelis.gwt.widget.table.TableModel;
import org.openelis.gwt.widget.table.TableWidget;
import org.openelis.gwt.widget.table.event.SourcesTableWidgetEvents;
import org.openelis.gwt.widget.table.event.TableWidgetListener;
import org.openelis.metamap.InventoryAdjustmentMetaMap;
import org.openelis.modules.main.client.OpenELISScreenForm;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Widget;

public class InventoryAdjustmentScreen extends OpenELISScreenForm<InventoryAdjustmentRPC,InventoryAdjustmentForm,Integer> implements TableManager, TableWidgetListener, ClickListener, AutoCompleteCallInt {

    private TableWidget        adjustmentsTable;
    private AppButton        removeRowButton;
    
    private ScreenTextBox   idText, userText, descText;
    private Dropdown store;
    private ScreenCalendar adjustmentDateText;
    private KeyListManager   keyList = new KeyListManager();
    
    private InventoryAdjustmentMetaMap InventoryAdjustmentMeta = new InventoryAdjustmentMetaMap();
    private static String storeIdKey;
    
    
    AsyncCallback<InventoryAdjustmentRPC> checkModels = new AsyncCallback<InventoryAdjustmentRPC>() {
        public void onSuccess(InventoryAdjustmentRPC rpc) {
            if(rpc.stores != null) {
                setStoresModel(rpc.stores);
                rpc.stores = null;
            }
        }
        
        public void onFailure(Throwable caught) {
            
        }
    };
    
    public InventoryAdjustmentScreen() {
        super("org.openelis.modules.inventoryAdjustment.server.InventoryAdjustmentService");
        
        forms.put("display",new InventoryAdjustmentForm());
        getScreen(new InventoryAdjustmentRPC());
    }
    
    public void onClick(Widget sender) {
        if (sender == removeRowButton)
            onRemoveRowButtonClick();
    }
    
/*    public void onChange(Widget sender) {
        if(sender == storesDropdown.getWidget()){
            Dropdown storeD = (Dropdown)storesDropdown.getWidget();
            form.setFieldValue(storeIdKey, storeD.getSelections());
           
        }else
            super.onChange(sender);
    }
    */
    
    public void afterDraw(boolean sucess) {
        removeRowButton = (AppButton)getWidget("removeRowButton");
        adjustmentsTable = (TableWidget)getWidget("adjustmentsTable");
        adjustmentsTable.addTableWidgetListener(this);
        adjustmentsTable.model.enableAutoAdd(false);
        
        storeIdKey = InventoryAdjustmentMeta.TRANS_ADJUSTMENT_LOCATION_META.INVENTORY_LOCATION_META.INVENTORY_ITEM_META.getStoreId();
        
        idText = (ScreenTextBox) widgets.get(InventoryAdjustmentMeta.getId());
        adjustmentDateText = (ScreenCalendar) widgets.get(InventoryAdjustmentMeta.getAdjustmentDate());
        userText = (ScreenTextBox) widgets.get(InventoryAdjustmentMeta.getSystemUserId());
        store = (Dropdown) getWidget(storeIdKey);
        descText = (ScreenTextBox) widgets.get(InventoryAdjustmentMeta.getDescription());
        
        ButtonPanel bpanel = (ButtonPanel)getWidget("buttons");
        
        CommandChain chain = new CommandChain();
        chain.addCommand(this);
        chain.addCommand(keyList);
        chain.addCommand(bpanel);
        
        setStoresModel(rpc.stores);
        rpc.stores = null;
        
        updateChain.add(afterUpdate);
        commitAddChain.add(afterCommitAdd);
        commitUpdateChain.add(afterCommitUpdate);
        
        updateChain.add(0,checkModels);
        fetchChain.add(0,checkModels);
        abortChain.add(0,checkModels);
        deleteChain.add(0,checkModels);
        commitUpdateChain.add(0,checkModels);
        commitAddChain.add(0,checkModels);
        
        super.afterDraw(sucess);
        
        form.setFieldValue("adjustmentsTable", adjustmentsTable.model.getData());
        
    }
    
    public void add() {
        //
        // make sure the contact table gets set before the main add
        //
        adjustmentsTable.model.enableAutoAdd(true);
        super.add();
        
        idText.enable(false);
        adjustmentDateText.enable(false);
        userText.enable(false);
        
        InventoryAdjustmentRPC iarpc = new InventoryAdjustmentRPC();
        iarpc.key = rpc.key;
        iarpc.form = rpc.form;
        
        screenService.call("getAddAutoFillValues", iarpc, new AsyncCallback<InventoryAdjustmentRPC>(){
            public void onSuccess(InventoryAdjustmentRPC result){    

                //load the values
                adjustmentDateText.load(result.form.adjustmentDate);
                userText.load(result.form.systemUser);
                
                //set the values in the rpc
                rpc.form.systemUser.setValue(result.form.systemUser.getValue());
                rpc.form.adjustmentDate.setValue(result.form.adjustmentDate.getValue());
                rpc.form.systemUserId = result.form.systemUserId;
                descText.setFocus(true);
                window.setStatus("","");
            }
            
            public void onFailure(Throwable caught){
                Window.alert(caught.getMessage());
            }
        }); 
    }
    
    protected AsyncCallback afterUpdate = new AsyncCallback() {
        public void onSuccess(Object result){
            idText.enable(false);
            adjustmentDateText.enable(false);
            userText.enable(false);
            ((ScreenDropDownWidget)widgets.get(storeIdKey)).enable(false);
            descText.setFocus(true);
            removeRowButton.changeState(AppButton.ButtonState.DISABLED);
        }
        
        public void onFailure(Throwable caught){
            
        }
    };
    
    public void abort() {
        adjustmentsTable.model.enableAutoAdd(false);
        super.abort();
    }
    
    public void query() {
        super.query();
        userText.enable(false);
        idText.setFocus(true);
    }
    
    protected AsyncCallback afterCommitUpdate = new AsyncCallback() {
        public void onSuccess(Object result){
            adjustmentsTable.model.enableAutoAdd(false);
        }
        
        public void onFailure(Throwable caught){
            
        }
  };
    
    protected AsyncCallback afterCommitAdd = new AsyncCallback() {
      public void onFailure(Throwable caught) {
          
      }
      public void onSuccess(Object result) {
          adjustmentsTable.model.enableAutoAdd(false);
      }   
  };
    
    //
    //start table manager methods
    //
    public boolean canAdd(TableWidget widget, DataSet set, int row) {
        return true;
    }

    public boolean canAutoAdd(TableWidget widget, DataSet addRow) {
        return !tableRowEmpty(addRow);
    }

    public boolean canDelete(TableWidget widget, DataSet set, int row) {
        return true;
    }

    public boolean canEdit(TableWidget widget, DataSet set, int row, int col) {
        int numRows = adjustmentsTable.model.numRows();
        if(state == FormInt.State.UPDATE && (col == 0 || col == 1) && row > -1 && numRows > 0 && row < numRows)
            return false;
        
       return true;
    }

    public boolean canSelect(TableWidget widget, DataSet set, int row) {
        if(state == FormInt.State.ADD || state == FormInt.State.UPDATE)           
            return true;
        return false;
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
        if(row >= adjustmentsTable.model.numRows())
            return;
        final DataSet<Object> tableRow = adjustmentsTable.model.getRow(row);
        final DropDownField<Object> invItemField = (DropDownField)tableRow.get(1);
        switch (col){
            case 0:
                if(store.getSelections().size() == 0){
                    window.setStatus(consts.get("inventoryAdjLocAutoException"),"ErrorPanel");
                    return;
                }
                
                InventoryAdjustmentRPC iarpc = new InventoryAdjustmentRPC();
                iarpc.key = rpc.key;
                iarpc.form = rpc.form;
                
                if(store.getSelections().size() > 0)
                    iarpc.storeId = (Integer)store.getSelections().get(0).getKey();
                iarpc.locId = (Integer)adjustmentsTable.model.getCell(row, 0);
                
                //we need to make sure the location id isnt already in the table
                if(locationIdAlreadyExists(iarpc.locId, row)){
                    ((IntegerField)tableRow.get(0)).clearErrors();
                    adjustmentsTable.model.setCellError(row, 0, consts.get("fieldUniqueException"));
                    return;
                }
                
                window.setStatus("","spinnerIcon");
                
                // prepare the argument list for the getObject function
                //FieldType[] args = new FieldType[] {locIdObj, storeIdObj}; 
                
                screenService.call("getInventoryItemInformation", iarpc, new AsyncCallback<InventoryAdjustmentRPC>(){
                    public void onSuccess(InventoryAdjustmentRPC result){    
                        if(row < adjustmentsTable.model.numRows()){
                        Integer currentId = (Integer)adjustmentsTable.model.getCell(row, 0);
                        Integer oldId = result.locId;
                        
                        //make sure the row hasnt been deleted and it still has the same values
                        if(currentId.equals(oldId)){
                              invItemField.setModel(result.invItemModel);
                              adjustmentsTable.model.setCell(row, 1, result.invItemModel.get(0));
                              adjustmentsTable.model.setCell(row, 2, result.storageLocation);
                              adjustmentsTable.model.setCell(row, 3, result.qtyOnHand);
                        }
                        }
                      window.setStatus("","");
                    }
                    
                    public void onFailure(Throwable caught){
                        Window.alert(caught.getMessage());
                        window.setStatus("","");
                    }
                });
                break;
            case 1:
                ArrayList selections = invItemField.getValue();
      
                if(selections.size() > 0){
                    DataSet selectedRow = (DataSet)selections.get(0);
      
                    if(selectedRow.size() > 1){
                        //we need to make sure this inventory item isnt already in the table
                        if(locationIdAlreadyExists(((IntegerObject)selectedRow.getData()).getValue(), row)){
                            ((DropDownField)tableRow.get(1)).clearErrors();
                            adjustmentsTable.model.setCellError(row, 1, consts.get("fieldUniqueException"));
                         
                        }else{
                            adjustmentsTable.model.setCell(row, 0, ((IntegerObject)selectedRow.getData()).getValue());
                            adjustmentsTable.model.setCell(row, 2, ((StringObject)selectedRow.get(2)).getValue());
                            adjustmentsTable.model.setCell(row, 3, ((IntegerObject)selectedRow.get(5)).getValue());
                            
                        }
                    }    
                }

                break;
            case 4:
                Integer qtyOnHand = null;
                Integer physicalCount = null;
                Integer adjQty = null;
                
                qtyOnHand = (Integer)tableRow.get(3).getValue();
                physicalCount = (Integer)tableRow.get(4).getValue();
                
                if(qtyOnHand != null && physicalCount != null){
                    adjQty = physicalCount - qtyOnHand;
                    
                    adjustmentsTable.model.setCell(row, 5, adjQty);
                }
                break;
        }
    }

    public void startEditing(SourcesTableWidgetEvents sender, int row, int col) {}

    public void stopEditing(SourcesTableWidgetEvents sender, int row, int col) {}
    //
    //end table listener methods
    //
    
    private boolean tableRowEmpty(DataSet<Integer> row){
        boolean empty = true;
        
        for(int i=0; i<row.size(); i++){
            if(row.get(i).getValue() != null && !"".equals(row.get(i).getValue())){
                empty = false;
                break;
            }
        }
        
        return empty;
    }
    
    private void onRemoveRowButtonClick() {
        int selectedRow = adjustmentsTable.model.getSelectedIndex();
        if (selectedRow > -1 && adjustmentsTable.model.numRows() > 0) {
            adjustmentsTable.model.deleteRow(selectedRow);

        }
    }
    
    private boolean locationIdAlreadyExists(Integer locationId, int currentRow){
        boolean exists = false;
        
        if(locationId != null){        
            TableModel model = (TableModel)adjustmentsTable.model;
            
            for(int i=0; i<model.numRows(); i++){
                if(i != currentRow && locationId.equals(model.getCell(i, 0))){
                    exists = true;
                    break;
                }            
            }
        }
        
        return exists;
    }

    public void callForMatches(final AutoComplete widget, DataModel model, String text) {
        // prepare the arguments
        InventoryAdjustmentItemAutoRPC iaarpc = new InventoryAdjustmentItemAutoRPC();
        iaarpc.key = rpc.key;
        iaarpc.cat = widget.cat;
        iaarpc.text = text;
        if(store.getSelections().size() > 0)
            iaarpc.storeId = (Integer)store.getSelections().get(0).getKey();
        
        screenService.call("getMatchesObj", iaarpc, new AsyncCallback<InventoryAdjustmentItemAutoRPC>() {
            public void onSuccess(InventoryAdjustmentItemAutoRPC result) {
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
    
    public void setStoresModel(DataModel<Integer> storesModel) {
        store.setModel(storesModel);
    }
}
