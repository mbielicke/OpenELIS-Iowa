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
package org.openelis.modules.buildKits.client;

import java.util.ArrayList;

import org.openelis.gwt.common.FormErrorException;
import org.openelis.gwt.common.Query;
import org.openelis.gwt.common.data.AbstractField;
import org.openelis.gwt.common.data.DoubleField;
import org.openelis.gwt.common.data.DropDownField;
import org.openelis.gwt.common.data.IntegerField;
import org.openelis.gwt.common.data.KeyListManager;
import org.openelis.gwt.common.data.StringObject;
import org.openelis.gwt.common.data.TableDataModel;
import org.openelis.gwt.common.data.TableDataRow;
import org.openelis.gwt.screen.CommandChain;
import org.openelis.gwt.screen.ScreenCheck;
import org.openelis.gwt.screen.ScreenInputWidget;
import org.openelis.gwt.screen.ScreenWindow;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.AutoComplete;
import org.openelis.gwt.widget.AutoCompleteCallInt;
import org.openelis.gwt.widget.ButtonPanel;
import org.openelis.gwt.widget.CheckBox;
import org.openelis.gwt.widget.table.TableManager;
import org.openelis.gwt.widget.table.TableModel;
import org.openelis.gwt.widget.table.TableWidget;
import org.openelis.gwt.widget.table.event.SourcesTableWidgetEvents;
import org.openelis.gwt.widget.table.event.TableWidgetListener;
import org.openelis.metamap.InventoryItemMetaMap;
import org.openelis.modules.fillOrder.client.FillOrderItemInfoForm;
import org.openelis.modules.inventoryReceipt.client.InvReceiptItemInfoForm;
import org.openelis.modules.inventoryReceipt.client.InventoryReceiptScreen;
import org.openelis.modules.main.client.OpenELISScreenForm;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class BuildKitsScreen extends OpenELISScreenForm<BuildKitsForm,Query<TableDataRow<Integer>>> implements ClickListener, AutoCompleteCallInt, ChangeListener, TableManager, TableWidgetListener{

    private KeyListManager keyList = new KeyListManager();
    
    private AutoComplete kitDropdown, kitLocationDropdown;
    private TableWidget subItemsTable;
    private TextBox numRequestedText;
    private int currentTableRow = -1;
    private ScreenCheck addToExisiting;
    private Integer currentKitDropdownValue;
    private Integer lastLocValue;
    private AppButton transferButton;
    
    private InventoryItemMetaMap InventoryItemMeta = new InventoryItemMetaMap();
    
    public BuildKitsScreen() {                
        super("org.openelis.modules.buildKits.server.BuildKitsService");
        query = new Query<TableDataRow<Integer>>();
        getScreen(new BuildKitsForm());
    }
    
    public void onClick(Widget sender) {
        if (sender == transferButton)
            onTransferRowButtonClick();
    }
    
    public void onChange(Widget sender) {
        super.onChange(sender);
        if(sender == kitDropdown && kitDropdown.getSelections().size() > 0 && !kitDropdown.getSelections().get(0).key.equals(currentKitDropdownValue)){
            currentKitDropdownValue = (Integer)kitDropdown.getSelections().get(0).key;

            // prepare the arguments
            //BuildKitsForm bkrpc = new BuildKitsForm();
            //bkrpc.screenKey = form.screenKey;
            form.kitId = currentKitDropdownValue;
            //bkrpc = form;
            
            screenService.call("getComponentsFromId", form, new AsyncCallback<BuildKitsForm>() {
                public void onSuccess(BuildKitsForm result) {
                   subItemsTable.model.clear();

                   for(int i=0; i<result.subItemsModel.size(); i++){
                       TableDataRow<Integer> set = result.subItemsModel.get(i);
                       TableDataRow<Integer> tableRow = subItemsTable.model.createRow();
                       //id
                       //name
                       //qty
                       tableRow.key = set.key;
                       tableRow.cells[0].setValue(set.cells[0].getValue());
                       tableRow.cells[3].setValue(set.cells[1].getValue());
                       
                       if(numRequestedText.getText() != null && !"".equals(numRequestedText.getText())){
                           Integer unit = new Integer((int)((Double)((DoubleField)tableRow.cells[3]).getValue()).doubleValue());
                           tableRow.cells[4].setValue(unit * Integer.valueOf(numRequestedText.getText()));
                       }
                       
                       subItemsTable.model.addRow(tableRow);
                   }
                }
                
                public void onFailure(Throwable caught) {
                    Window.alert(caught.getMessage());
                }
            });
        }else if(sender == numRequestedText){
            //if the kit isnt selected do nothing
            if(!"".equals(numRequestedText.getText()) && kitDropdown.getSelections().size() > 0){
                Integer requested = Integer.valueOf(numRequestedText.getText());
                
                TableModel model = (TableModel)subItemsTable.model;

                for(int i=0; i<model.numRows(); i++){

                    if(model.getCell(i, 1) == null){
                        //we just take unit times num requested 
                        Integer unit = new Integer((int)((Double)((DoubleField)model.getObject(i, 3)).getValue()).doubleValue());
                        IntegerField total = (IntegerField)model.getObject(i, 4);
                        
                        total.setValue(unit * requested);
                        
                        subItemsTable.model.setCell(i, 4, total.getValue());
                        
                    }else{
                        //we need to look at the quantity on hand to make sure we can build the requested number of kits
                        Integer unit = new Integer((int)((Double)model.getCell(i, 3)).doubleValue());
                        Integer qtyOnHand = (Integer)model.getCell(i, 5);

                        Integer totalProposed = unit * requested;
                        IntegerField total = (IntegerField)model.getObject(i, 4);

                        if(totalProposed.compareTo(qtyOnHand) > 0){
                            total.setValue(new Integer(totalProposed));
                            subItemsTable.model.clearCellError(i, 4);
                            subItemsTable.model.setCellError(i, 4, consts.get("totalIsGreaterThanOnHandException"));
                        }else{
                            subItemsTable.model.clearCellError(i, 4);
                            total.setValue(totalProposed);
                        }
                        
                        subItemsTable.model.setCell(i, 4, total.getValue());
                    }
                }
            }
        }
    }

    public void afterDraw(boolean success) {
        ButtonPanel bpanel = (ButtonPanel) getWidget("buttons");
        
        kitDropdown = (AutoComplete)getWidget(InventoryItemMeta.getName());
        
        kitLocationDropdown = (AutoComplete)getWidget(InventoryItemMeta.INVENTORY_LOCATION.INVENTORY_LOCATION_STORAGE_LOCATION.getLocation());
         
        numRequestedText = (TextBox)getWidget("numRequested");
        
        startWidget = (ScreenInputWidget)widgets.get(InventoryItemMeta.getName());
        addToExisiting = (ScreenCheck)widgets.get("addToExisting");
        
        transferButton = (AppButton)getWidget("transferButton");
        
        CommandChain chain = new CommandChain();
        chain.addCommand(this);
        chain.addCommand(keyList);
        chain.addCommand(bpanel);
        
        subItemsTable = (TableWidget)getWidget("subItemsTable");
        subItemsTable.addTableWidgetListener(this);
        
        commitAddChain.add(afterCommitAdd);
        commitUpdateChain.add(afterCommitUpdate);
        
        super.afterDraw(success);
        
        form.subItemsTable.setValue(subItemsTable.model.getData());
    }
    
    public void commit() {
        super.commit();
        currentKitDropdownValue = null;
    }
    
    protected AsyncCallback afterCommitUpdate = new AsyncCallback() {
        public void onSuccess(Object result){
            subItemsTable.model.enableAutoAdd(false);
        }
        
        public void onFailure(Throwable caught){
            
        }
  };
    
    protected AsyncCallback afterCommitAdd = new AsyncCallback() {
      public void onFailure(Throwable caught) {
          
      }
      public void onSuccess(Object result) {
          subItemsTable.model.enableAutoAdd(false);
      }   
  };
    
    public void abort() {
        if(state == State.ADD){
          //unlock the record
            InvReceiptItemInfoForm iriif = new InvReceiptItemInfoForm();
            iriif.lockedLocIds = getLockedSetsFromTable();

            screenService.call("unlockLocations", iriif, new AsyncCallback<FillOrderItemInfoForm>() {
                public void onSuccess(FillOrderItemInfoForm result) {
                    superAbort();
                    
               }

               public void onFailure(Throwable caught) {
                   Window.alert(caught.getMessage());
               }
                });
        }else
            super.abort();
        currentKitDropdownValue = null;
    }
    
    private void superAbort(){
        super.abort();
    }

    //
    // start table manager methods
    public boolean canAdd(TableWidget widget, TableDataRow set, int row) {
        return false;
    }

    public boolean canAutoAdd(TableWidget widget, TableDataRow addRow) {
        return false;
    }

    public boolean canDelete(TableWidget widget, TableDataRow set, int row) {
        return false;
    }

    public boolean canEdit(TableWidget widget, TableDataRow set, int row, int col) {
        currentTableRow = row;
        if(col == 4)
            return false;
        
        return true;
    }

    public boolean canSelect(TableWidget widget, TableDataRow set, int row) {
        if(state == State.ADD)           
            return true;
        return false;
    }
    
    //
    //end table manager methods
    //
    
    //
    //start table widget listener methods
    //
    public void finishedEditing(SourcesTableWidgetEvents sender, final int row, int col) {
        DropDownField<Object> locationField;
        if(col == 1 && row < subItemsTable.model.numRows()){
            Integer currentLocValue = (Integer)((DropDownField)subItemsTable.model.getObject(row, col)).getSelectedKey();
            if((lastLocValue == null && currentLocValue != null) || 
                            (lastLocValue != null && !lastLocValue.equals(currentLocValue))){
                    //we need to send lock/fetch call back
                    BuildKitsForm bkf = new BuildKitsForm();
                    bkf.lastLocId = lastLocValue;
                    bkf.locId = currentLocValue;
                    screenService.call("fetchLocationAndLock", bkf, new AsyncCallback<BuildKitsForm>() {
                        public void onSuccess(BuildKitsForm result) {
                            TableDataRow<Integer> tableRow = subItemsTable.model.getRow(row);
                            TableDataRow<Integer> selectedRow = ((ArrayList<TableDataRow<Integer>>)((DropDownField<Integer>)tableRow.getCells().get(1)).getValue()).get(0);
                            
                            subItemsTable.model.setCell(row, 2, ((StringObject)selectedRow.getCells().get(1)).getValue());
                            subItemsTable.model.setCell(row, 5, result.qtyOnHand);
                            
                            if(subItemsTable.model.getCell(row, 4) != null && ((Integer)subItemsTable.model.getCell(row, 5)).compareTo((Integer)subItemsTable.model.getCell(row, 4)) < 0){
                                subItemsTable.model.clearCellError(row, 4);
                                subItemsTable.model.setCellError(row, 4, consts.get("totalIsGreaterThanOnHandException"));
                            }else
                                subItemsTable.model.clearCellError(row, 4);
                        }

                        public void onFailure(Throwable caught) {
                            Window.alert(caught.getMessage());
                            //clear row
                            TableDataRow<Integer> tableRow = subItemsTable.model.getRow(row);
                            ((DropDownField<Integer>)tableRow.cells[1]).setModel(null);
                            ((DropDownField<Integer>)tableRow.cells[1]).clear();
                            tableRow.cells[2].setValue(null);
                            tableRow.cells[5].setValue(null);
                            
                            subItemsTable.model.refresh();
                        }
                    });
                }
            }
    }

    public void startEditing(SourcesTableWidgetEvents sender, int row, int col) {
        if(col == 1 && row < subItemsTable.model.numRows())
            lastLocValue = (Integer)((DropDownField<Integer>)subItemsTable.model.getObject(row, col)).getSelectedKey();
         
    }

    public void stopEditing(SourcesTableWidgetEvents sender, int row, int col) {}
    //
    //end table widget listener methods
    //

    //
    //auto complete method
    //
    public void callForMatches(final AutoComplete widget, TableDataModel model, String text) {
    	SubLocationAutoRPC autoRPC = new SubLocationAutoRPC();
    	autoRPC.cat = widget.cat;
    	autoRPC.match = text;
    	
        if(widget == kitLocationDropdown){
            autoRPC.addToExisting = ((CheckBox)addToExisiting.getWidget()).getState();    
        }else{
            autoRPC.id = (Integer) subItemsTable.model.getRow(currentTableRow).key;
        }
        
        screenService.call("getMatchesObj", autoRPC, new AsyncCallback<SubLocationAutoRPC>() {
            public void onSuccess(SubLocationAutoRPC result) {
                widget.showAutoMatches(result.matchesModel);
            }
            
            public void onFailure(Throwable caught) {
                if(caught instanceof FormErrorException){
                    window.setStatus(caught.getMessage(), "ErrorPanel");
                }else
                    Window.alert(caught.getMessage());
            }
        });
    }
    
    protected void validate(AbstractField field) {
        if("numRequested".equals(field.key)){
            try{
                Integer requested = Integer.valueOf(numRequestedText.getText());
                boolean setError = false;
                TableModel model = (TableModel)subItemsTable.model;
                
                for(int i=0; i<model.numRows(); i++){
                    Integer unit = new Integer((int)((Double)model.getCell(i, 3)).doubleValue());
                    Integer qtyOnHand = (Integer)model.getCell(i, 5);
                    Integer totalProposed = unit * requested;
                    
                    if(totalProposed.compareTo(qtyOnHand) > 0){
                        setError = true;
                        break;
                    }
                }
                    
                if(setError)
                    form.numRequested.addError("Transfer in more components or lower the number requested. Not enough quantity on hand.");
            }catch(Exception e){
                //do nothing
            }
        }
    }
    
    private void onTransferRowButtonClick() {
        Object[] args = new Object[1];
        args[0] = new StringObject("transfer");
        /*
        PopupPanel inventoryTransferPopupPanel = new PopupPanel(false, true);
        ScreenWindow pickerWindow = new ScreenWindow(inventoryTransferPopupPanel,
                                                     "Inventory Transfer",
                                                     "inventoryTransferScreen",
                                                     "Loading...");
        
        pickerWindow.setContent(new InventoryReceiptScreen(args));

        inventoryTransferPopupPanel.add(pickerWindow);
        int left = this.getAbsoluteLeft();
        int top = this.getAbsoluteTop();
        inventoryTransferPopupPanel.setPopupPosition(left, top);
        inventoryTransferPopupPanel.show();
        */
        ScreenWindow modal = new ScreenWindow(null,"Inventory Transfer","inventoryTransferScreen","Loading...",true);
        modal.setName(consts.get("inventoryTransfer"));
        modal.setContent(new InventoryReceiptScreen(args));
        
    }
    
    private TableDataModel<TableDataRow<Integer>> getLockedSetsFromTable(){
        TableDataModel<TableDataRow<Integer>> returnModel = new TableDataModel<TableDataRow<Integer>>();
        for(int i=0; i<subItemsTable.model.numRows(); i++){
            ArrayList<TableDataRow<Integer>> selections = ((DropDownField<Integer>)subItemsTable.model.getObject(i, 1)).getValue();
            
            if(selections.size() == 1){
                returnModel.add(new TableDataRow<Integer>(selections.get(0).key));
            }
        }
            
        return returnModel;
    }
}