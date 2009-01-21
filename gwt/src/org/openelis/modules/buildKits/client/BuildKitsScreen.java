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

import org.openelis.gwt.common.FormErrorException;
import org.openelis.gwt.common.data.AbstractField;
import org.openelis.gwt.common.data.Data;
import org.openelis.gwt.common.data.DataMap;
import org.openelis.gwt.common.data.DataModel;
import org.openelis.gwt.common.data.DataObject;
import org.openelis.gwt.common.data.DataSet;
import org.openelis.gwt.common.data.DropDownField;
import org.openelis.gwt.common.data.KeyListManager;
import org.openelis.gwt.common.data.NumberField;
import org.openelis.gwt.common.data.NumberObject;
import org.openelis.gwt.common.data.StringObject;
import org.openelis.gwt.screen.CommandChain;
import org.openelis.gwt.screen.ScreenCheck;
import org.openelis.gwt.screen.ScreenInputWidget;
import org.openelis.gwt.screen.ScreenWindow;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.AutoComplete;
import org.openelis.gwt.widget.AutoCompleteCallInt;
import org.openelis.gwt.widget.ButtonPanel;
import org.openelis.gwt.widget.FormInt;
import org.openelis.gwt.widget.table.TableManager;
import org.openelis.gwt.widget.table.TableModel;
import org.openelis.gwt.widget.table.TableWidget;
import org.openelis.gwt.widget.table.event.SourcesTableWidgetEvents;
import org.openelis.gwt.widget.table.event.TableWidgetListener;
import org.openelis.metamap.InventoryItemMetaMap;
import org.openelis.modules.inventoryReceipt.client.InventoryReceiptScreen;
import org.openelis.modules.main.client.OpenELISScreenForm;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class BuildKitsScreen extends OpenELISScreenForm implements ClickListener, AutoCompleteCallInt, ChangeListener, TableManager, TableWidgetListener{

    private KeyListManager keyList = new KeyListManager();
    
    private AutoComplete kitDropdown, kitLocationDropdown;
    private TableWidget subItemsTable;
    private TextBox numRequestedText;
    private int currentTableRow = -1;
    private ScreenCheck addToExisiting;
    private Integer currentKitDropdownValue;
    private AppButton transferButton;
    
    private InventoryItemMetaMap InventoryItemMeta = new InventoryItemMetaMap();
    
    public BuildKitsScreen() {
        super("org.openelis.modules.buildKits.server.BuildKitsService", false);
    }
    
    public void onClick(Widget sender) {
        if (sender == transferButton)
            onTransferRowButtonClick();
    }
    
    public void onChange(Widget sender) {
        super.onChange(sender);
        if(sender == kitDropdown && kitDropdown.getSelections().size() > 0 && !((NumberObject)kitDropdown.getSelections().get(0).getKey()).getValue().equals(currentKitDropdownValue)){
            currentKitDropdownValue = (Integer)((NumberObject)kitDropdown.getSelections().get(0).getKey()).getValue();
            NumberObject idObj = (NumberObject)((DataSet)kitDropdown.getSelections().get(0)).getKey();
            // prepare the argument list for the getObject function
            DataObject[] args = new DataObject[] {idObj}; 
            
            screenService.getObject("getComponentsFromId", args, new AsyncCallback<DataModel>() {
                public void onSuccess(DataModel model) {
                   subItemsTable.model.clear();

                   for(int i=0; i<model.size(); i++){
                       DataSet set = model.get(i);
                       DataSet tableRow = subItemsTable.model.createRow();
                       //id
                       //name
                       //qty
                       tableRow.setKey(set.getKey());
                       tableRow.get(0).setValue(set.get(0).getValue());
                       tableRow.get(3).setValue(set.get(1).getValue());
                       
                       if(numRequestedText.getText() != null && !"".equals(numRequestedText.getText())){
                           Integer unit = new Integer((int)((Double)((NumberField)tableRow.get(3)).getValue()).doubleValue());
                           tableRow.get(4).setValue(unit * Integer.valueOf(numRequestedText.getText()));
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
                        Integer unit = new Integer((int)((Double)((NumberField)model.getObject(i, 3)).getValue()).doubleValue());
                        NumberField total = (NumberField)model.getObject(i, 4);
                        
                        total.setValue(unit * requested);
                        
                        subItemsTable.model.setCell(i, 4, total.getValue());
                        
                    }else{
                        //we need to look at the quantity on hand to make sure we can build the requested number of kits
                        Integer unit = new Integer((int)((Double)model.getCell(i, 3)).doubleValue());
                        Integer qtyOnHand = (Integer)model.getCell(i, 5);

                        Integer totalProposed = unit * requested;
                        NumberField total = (NumberField)model.getObject(i, 4);

                        if(totalProposed.compareTo(qtyOnHand) > 0){
                            total.setValue(new Integer(totalProposed));
                            subItemsTable.model.clearCellError(i, 4);
                            subItemsTable.model.setCellError(i, 4, "NOT ENOUGH ITEMS ON HAND");
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
        
        super.afterDraw(success);
        
        rpc.setFieldValue("subItemsTable", subItemsTable.model.getData());
    }
    
    public void commit() {
        super.commit();
        currentKitDropdownValue = null;
    }
    
    public void abort() {
        super.abort();
        currentKitDropdownValue = null;
    }

    //
    // start table manager methods
    public boolean canAdd(TableWidget widget, DataSet set, int row) {
        return false;
    }

    public boolean canAutoAdd(TableWidget widget, DataSet addRow) {
        return false;
    }

    public boolean canDelete(TableWidget widget, DataSet set, int row) {
        return false;
    }

    public boolean canEdit(TableWidget widget, DataSet set, int row, int col) {
        currentTableRow = row;
        if(col == 4)
            return false;
        
        return true;
    }

    public boolean canSelect(TableWidget widget, DataSet set, int row) {
        if(state == FormInt.State.ADD)           
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
    //start table widget listener methods
    //
    public void finishedEditing(SourcesTableWidgetEvents sender, int row, int col) {
        DropDownField locationField;
        if(col == 1 && row < subItemsTable.model.numRows()){
            locationField = (DropDownField)subItemsTable.model.getObject(row, col);
            if(locationField.getValue() != null){
                DataSet tableRow = subItemsTable.model.getRow(row);
                DataSet selectedRow = ((DropDownField)tableRow.get(1)).getSelections().get(0);
                DataMap map = (DataMap)selectedRow.getData();
            
                subItemsTable.model.setCell(row, 2, ((StringObject)map.get("lotNumber")).getValue());
                subItemsTable.model.setCell(row, 5, ((NumberObject)map.get("qtyOnHand")).getValue());
                
                if(subItemsTable.model.getCell(row, 4) != null && ((Integer)subItemsTable.model.getCell(row, 5)).compareTo((Integer)subItemsTable.model.getCell(row, 4)) < 0){
                    subItemsTable.model.clearCellError(row, 4);
                    subItemsTable.model.setCellError(row, 4, "NOT ENOUGH ITEMS ON HAND");
                }
            }
        }     
    }

    public void startEditing(SourcesTableWidgetEvents sender, int row, int col) {}

    public void stopEditing(SourcesTableWidgetEvents sender, int row, int col) {}
    //
    //end table widget listener methods
    //

    //
    //auto complete method
    //
    public void callForMatches(final AutoComplete widget, DataModel model, String text) {
        StringObject catObj = new StringObject(widget.cat);
        StringObject matchObj = new StringObject(text);
        DataMap paramsObj = new DataMap();
        
        if(widget == kitLocationDropdown){
            paramsObj.put("addToExisting", rpc.getField("addToExisting"));    
        }else{
            paramsObj.put("id", (NumberObject)subItemsTable.model.getRow(currentTableRow).getKey());
        }
        
        // prepare the argument list for the getObject function
        Data[] args = new Data[] {catObj, model, matchObj, paramsObj}; 
        
        
        screenService.getObject("getMatchesObj", args, new AsyncCallback<DataModel>() {
            public void onSuccess(DataModel model) {
                widget.showAutoMatches(model);
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
                    rpc.setFieldError("numRequested", "Transfer in more components or lower the number requested. Not enough quantity on hand.");
            }catch(Exception e){
                //do nothing
            }
        }
    }
    
    private void onTransferRowButtonClick() {
        Object[] args = new Object[1];
        args[0] = new StringObject("transfer");
        
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
        
    }
}