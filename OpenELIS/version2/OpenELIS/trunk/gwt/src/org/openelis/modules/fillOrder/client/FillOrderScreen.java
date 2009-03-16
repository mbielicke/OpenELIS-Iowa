/**
 * Exhibit A - UIRF Open-source Based Public Software License.
 * 
 * The contents of this file are subject to the UIRF Open-source Based Public Software License(the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at openelis.uhl.uiowa.edu
 * 
 * Software distributed under the License is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for the specific language governing rights and limitations under the License.
 * 
 * The Original Code is OpenELIS code.
 * 
 * The Initial Developer of the Original Code is The University of Iowa. Portions created by The University of Iowa are Copyright 2006-2008. All Rights Reserved.
 * 
 * Contributor(s): ______________________________________.
 * 
 * Alternatively, the contents of this file marked "Separately-Licensed" may be used under the terms of a UIRF Software license ("UIRF Software License"), in which case the provisions of a UIRF Software License are applicable instead of those above.
 */
package org.openelis.modules.fillOrder.client;

import java.util.ArrayList;
import java.util.List;

import org.openelis.gwt.common.Form;
import org.openelis.gwt.common.FormErrorException;
import org.openelis.gwt.common.data.CheckField;
import org.openelis.gwt.common.data.DataModel;
import org.openelis.gwt.common.data.DataObject;
import org.openelis.gwt.common.data.DataSet;
import org.openelis.gwt.common.data.DropDownField;
import org.openelis.gwt.common.data.Field;
import org.openelis.gwt.common.data.IntegerField;
import org.openelis.gwt.common.data.IntegerObject;
import org.openelis.gwt.common.data.KeyListManager;
import org.openelis.gwt.common.data.TreeDataItem;
import org.openelis.gwt.common.data.TreeDataModel;
import org.openelis.gwt.event.CommandListener;
import org.openelis.gwt.screen.CommandChain;
import org.openelis.gwt.screen.ScreenQueryTable;
import org.openelis.gwt.screen.ScreenResultsTable;
import org.openelis.gwt.screen.ScreenWindow;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.AutoComplete;
import org.openelis.gwt.widget.AutoCompleteCallInt;
import org.openelis.gwt.widget.ButtonPanel;
import org.openelis.gwt.widget.CheckBox;
import org.openelis.gwt.widget.Dropdown;
import org.openelis.gwt.widget.FormInt;
import org.openelis.gwt.widget.ResultsTable;
import org.openelis.gwt.widget.AppButton.ButtonState;
import org.openelis.gwt.widget.table.QueryTable;
import org.openelis.gwt.widget.table.TableDropdown;
import org.openelis.gwt.widget.table.TableManager;
import org.openelis.gwt.widget.table.TableModel;
import org.openelis.gwt.widget.table.TableWidget;
import org.openelis.gwt.widget.table.event.SourcesTableModelEvents;
import org.openelis.gwt.widget.table.event.SourcesTableWidgetEvents;
import org.openelis.gwt.widget.table.event.TableModelListener;
import org.openelis.gwt.widget.table.event.TableWidgetListener;
import org.openelis.gwt.widget.tree.TreeManager;
import org.openelis.gwt.widget.tree.TreeWidget;
import org.openelis.gwt.widget.tree.event.SourcesTreeModelEvents;
import org.openelis.gwt.widget.tree.event.SourcesTreeWidgetEvents;
import org.openelis.gwt.widget.tree.event.TreeModelListener;
import org.openelis.gwt.widget.tree.event.TreeWidgetListener;
import org.openelis.metamap.FillOrderMetaMap;
import org.openelis.modules.main.client.OpenELISScreenForm;
import org.openelis.modules.shipping.client.ShippingDataService;
import org.openelis.modules.shipping.client.ShippingScreen;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.SyncCallback;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class FillOrderScreen extends OpenELISScreenForm<FillOrderRPC, FillOrderForm, Integer> implements
                                                                                             ClickListener,
                                                                                             AutoCompleteCallInt,
                                                                                             TableManager,
                                                                                             TableWidgetListener,
                                                                                             TableModelListener,
                                                                                             TreeManager,
                                                                                             TreeWidgetListener,
                                                                                             CommandListener,
                                                                                             TreeModelListener {
    
    private Dropdown            costCenter;
    private TreeWidget          orderItemsTree;
    private ResultsTable        fillItemsTable;
    private QueryTable          fillItemsQueryTable;
    private AppButton           removeRowButton, addLocationButton;

    private TreeDataModel       combinedTree, emptyTreeModel;
    private List<Integer>                combinedTreeIds;
    private DataModel<Integer>  lockedOrderIds = new DataModel();
    private Integer             lastShippedTo   = null;
    private Integer             lastShippedFrom = null;
    private Object              lastTreeValue;
    private Integer             orderPendingValue;

    private FillOrderMetaMap    OrderMeta       = new FillOrderMetaMap();

    private KeyListManager      keyList         = new KeyListManager();

    AsyncCallback<FillOrderRPC> checkModels     = new AsyncCallback<FillOrderRPC>() {
                                                    public void onSuccess(FillOrderRPC rpc) {
                                                        if (rpc.statuses != null) {
                                                            setStatusIdModel(rpc.statuses);
                                                            rpc.statuses = null;
                                                        }
                                                        if (rpc.costCenters != null) {
                                                            setCostCentersModel(rpc.costCenters);
                                                            rpc.costCenters = null;
                                                        }
                                                        if (rpc.shipFroms != null) {
                                                            setShipFromModel(rpc.shipFroms);
                                                            rpc.shipFroms = null;
                                                        }
                                                    }

                                                    public void onFailure(Throwable caught) {

                                                    }
                                                };

    public FillOrderScreen() {
        super("org.openelis.modules.fillOrder.server.FillOrderService");

        forms.put("display", new FillOrderForm());
        getScreen(new FillOrderRPC());
    }

    public void performCommand(Enum action, Object obj) {
        if (action == KeyListManager.Action.FETCH) {
            if (state == State.DISPLAY || state == State.DEFAULT) {
                if (keyList.getList().size() > 0)
                    changeState(State.DISPLAY);
                else
                    changeState(State.DEFAULT);
            }
        } else if (action == KeyListManager.Action.SELECTION) {
            // do nothing for now
        } else if (action == KeyListManager.Action.GETPAGE) {
            // do nothing for now
        } else {
            super.performCommand(action, obj);
        }
    }

    public void onClick(Widget sender) {
        if (sender == removeRowButton)
            onRemoveRowButtonClick();
        else if (sender == addLocationButton)
            onAddLocationButtonClick();
    }

    public void afterDraw(boolean success) {
        ScreenResultsTable sw = (ScreenResultsTable)widgets.get("fillItemsTable");
        fillItemsTable = (ResultsTable)sw.getWidget();
        fillItemsQueryTable = (QueryTable)((ScreenQueryTable)sw.getQueryWidget()).getWidget();
        fillItemsTable.table.addTableWidgetListener(this);
        fillItemsTable.model.addTableModelListener(this);
        orderItemsTree = (TreeWidget)getWidget("orderItemsTree");
        orderItemsTree.addTreeWidgetListener(this);
        orderItemsTree.model.addTreeModelListener(this);

        removeRowButton = (AppButton)getWidget("removeRowButton");
        addLocationButton = (AppButton)getWidget("addLocationButton");

        ButtonPanel bpanel = (ButtonPanel)getWidget("buttons");

        CommandChain chain = new CommandChain();
        chain.addCommand(this);
        chain.addCommand(keyList);
        chain.addCommand(bpanel);
        chain.addCommand(fillItemsTable);

        costCenter = (Dropdown)getWidget(OrderMeta.getCostCenterId());

        setCostCentersModel(rpc.costCenters);
        setStatusIdModel(rpc.statuses);
        setShipFromModel(rpc.shipFroms);
        
        orderPendingValue = rpc.orderPendingValue;

        /*
         * Null out the rpc models so they are not sent with future rpc calls
         */
        rpc.costCenters = null;
        rpc.statuses = null;
        rpc.shipFroms = null;

        combinedTree = (TreeDataModel)orderItemsTree.model.getData().clone();
        combinedTreeIds = new ArrayList<Integer>();
        
        updateChain.add(0, checkModels);
        fetchChain.add(0, checkModels);
        abortChain.add(0, checkModels);
        deleteChain.add(0, checkModels);
        commitUpdateChain.add(0, checkModels);
        commitAddChain.add(0, checkModels);

        super.afterDraw(success);

        emptyTreeModel = orderItemsTree.model.getData();
        rpc.form.setFieldValue("fillItemsTable", fillItemsTable.model.getData());
        rpc.form.setFieldValue("orderItemsTree", emptyTreeModel);
        

    }

    public void add() {
        // super.add();
        key = null;
        enable(true);
        changeState(State.ADD);
        window.setStatus(consts.get("enterInformationPressCommit"), "");

        removeRowButton.changeState(ButtonState.DISABLED);
        addLocationButton.changeState(ButtonState.DISABLED);
    }

    public void query() {
        super.query();

        removeRowButton.changeState(ButtonState.DISABLED);
        addLocationButton.changeState(ButtonState.DISABLED);

        fillItemsQueryTable.setCellValue(OrderMeta.getStatusId(), 2, new DataSet<Integer>(orderPendingValue));
        fillItemsQueryTable.select(0, 1);

        orderItemsTree.model.clear();
        lockedOrderIds.clear();
        combinedTree.clear();
    }

    public void commit() {
        if (state == State.ADD) {
            orderItemsTree.model.load(combinedTree);
            submitForm();
            form.validate();
            if (form.status == Form.Status.valid && validate()) {
                clearErrors();
                window.setStatus("", "");
                lastShippedFrom = new Integer(-1);
                lastShippedTo = new Integer(-1);
                onProcessingCommitClick();
            } else {
                drawErrors();
                window.setStatus(consts.get("correctErrors"), "ErrorPanel");
            }
        } else
            super.commit();

    }

    public void abort() {
        if (state == State.ADD) {
            window.setStatus("", "spinnerIcon");
            clearErrors();
            resetForm();
            load();
            enable(false);

            // fillItemsTable.unselect(-1);
            lockedOrderIds.clear();
            combinedTree.clear();
            orderItemsTree.model.clear();
            lastShippedFrom = new Integer(-1);
            lastShippedTo = new Integer(-1);

            // prepare the argument list for the getObject function
            FillOrderItemInfoRPC foirpc = new FillOrderItemInfoRPC();
            foirpc.key = rpc.key;
            foirpc.form = rpc.form.itemInformation;
            foirpc.tableData = keyList.getList();

            screenService.call("commitQueryAndUnlock",
                               foirpc,
                               new AsyncCallback<FillOrderItemInfoRPC>() {
                                   public void onSuccess(FillOrderItemInfoRPC result) {
                                       fillItemsTable.model.load(result.tableData);

                                       window.setStatus("", "");
                                       changeState(State.DISPLAY);
                                   }

                                   public void onFailure(Throwable caught) {
                                       Window.alert(caught.getMessage());
                                       window.setStatus("", "");
                                   }
                               });
        } else
            super.abort();
    }
    
    public void onShippingScreenCommit(){
        Window.alert("commit");
        //unlock the records
        //set the order to processed
    }
    
    public void onShippingScreenAbort(){
        Window.alert("abort");
        
        //unlock the records
        
    }

    //
    // start table manager methods
    //
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
        return state == FormInt.State.ADD && col == 0;

    }

    public boolean canSelect(TableWidget widget, DataSet set, int row) {
        return state == FormInt.State.ADD || state == FormInt.State.DISPLAY;

    }

    public boolean canDrag(TableWidget widget, DataSet item, int row) {
        return false;
    }

    public boolean canDrop(TableWidget widget,
                           Widget dragWidget,
                           DataSet dropTarget,
                           int targetRow) {
        return false;
    }

    public void drop(TableWidget widget,
                     Widget dragWidget,
                     DataSet dropTarget,
                     int targetRow) {
    }

    public void drop(TableWidget widget, Widget dragWidget) {
    }

    //
    // end table manager methods
    //
    
    private void onProcessingCommitClick() {
        // get the first row in the table that is selected
        TableModel model = (TableModel)fillItemsTable.model;
        int i = 0;
        while (i < model.numRows() && lockedOrderIds.getByKey(getOrderId(i)) == null)
            i++;

        DataSet<Object> row = model.getRow(i);

        if (((CheckField)row.get(9)).isChecked()) {
           
            
        }else{
            
            PopupPanel shippingPopupPanel = new PopupPanel(false, true);
            ScreenWindow pickerWindow = new ScreenWindow(shippingPopupPanel, "Shipping", "shippingScreen", "Loading...");

            if (lockedOrderIds.size() > 0 && row != null) {
                FillOrderItemInfoForm tableRowSubRPC = (FillOrderItemInfoForm)row.getData();
                ShippingScreen shippingScreen = new ShippingScreen();

                ShippingDataService data = new ShippingDataService();
                data.setShipFromId((Integer)((DropDownField<Integer>)row.get(4)).getSelectedKey());
                data.setShipToId((Integer)((DropDownField<Integer>)row.get(5)).getSelectedKey());
                data.setShipToText((String)((DropDownField<Integer>)row.get(5)).getTextValue());
                data.setMultUnitText(tableRowSubRPC.multUnit.getValue());
                data.setStreetAddressText(tableRowSubRPC.streetAddress.getValue());
                data.setCityText(tableRowSubRPC.city.getValue());
                data.setStateText(tableRowSubRPC.state.getValue());
                data.setZipCodeText(tableRowSubRPC.zipCode.getValue());
                data.setItemsShippedModel(createDataModelFromTree((TreeDataModel)combinedTree.clone()));
                data.setFillOrderScreen(this);

                shippingScreen.setShippingData(data);

                pickerWindow.setContent(shippingScreen);
                shippingPopupPanel.add(pickerWindow);
                int left = this.getAbsoluteLeft();
                int top = this.getAbsoluteTop();
                shippingPopupPanel.setPopupPosition(left, top);
                shippingPopupPanel.show();

                enable(false);
                changeState(State.DEFAULT);
            }
        }
        
        super.commit();
        
        // clear out all the temp values
        //lockedOrderIds.clear();
        //combinedTree.clear();
        //lastShippedFrom = new Integer(-1);
        //lastShippedTo = new Integer(-1);

    }

    private void rebuildOrderItemsTree(int row) {
        boolean isChecked, alreadyCombined;
        DataSet<Integer> tableRow = fillItemsTable.model.getRow(row);
        
        FillOrderItemInfoForm subRpc = (FillOrderItemInfoForm)tableRow.getData();
        
        if (subRpc == null)
            return;
       
        TreeDataModel orgTreeModel = subRpc.originalOrderItemsTree.getValue();
        isChecked = isRowChecked(row);
        alreadyCombined = combinedTreeIds.contains(getOrderId(row));
        
        if (isChecked && !alreadyCombined) {
            // take the treefield in the form and move it to the original param in the rpc for safe keeping if the original is null
            for (int i = 0; i < orgTreeModel.size(); i++) {
                TreeDataItem set = (TreeDataItem)orgTreeModel.get(i);

                TreeDataItem parentItemRow = orderItemsTree.model.createTreeItem("top");
                // TreeDataItem locRow = orderItemsTree.model.createTreeItem("orderItem");
                parentItemRow.addItem(set);
                parentItemRow.get(0).setValue(set.get(0).getValue());
                parentItemRow.get(1).setValue(set.get(1).getValue());
                parentItemRow.setData(new IntegerObject((Integer)set.get(0)
                                                                    .getValue()));

                if (set.get(2).getValue() != null) {
                    ((DropDownField)parentItemRow.get(2)).setModel(((DropDownField)set.get(2)).getModel());
                    ((DropDownField)parentItemRow.get(2)).setValue(((DropDownField)set.get(2)).getValue());
                }

                FillOrderOrderItemsKey hiddenData = (FillOrderOrderItemsKey)set.getData();
                hiddenData.tableRowId = new Integer(row);

                combinedTree.add(parentItemRow);
                
            }
            combinedTreeIds.add(getOrderId(row));
            
        } else if(!isChecked && alreadyCombined){
            int k = 0;
            while (k < combinedTree.size()) {

                if (((Integer)combinedTree.get(k).get(1).getValue()).equals(tableRow.get(1)
                                                                                  .getValue())) { // we need to remove this row and children
                    combinedTree.delete(k);
                    k--;
                }
                k++;
            }
            combinedTreeIds.remove(getOrderId(row));
        }
        
        if(combinedTreeIds.contains(getOrderId(row)) && 
               (lastShippedFrom == null || lastShippedTo == null)){
            lastShippedFrom = (Integer)((DropDownField)tableRow.get(4)).getSelectedKey();
            lastShippedTo = (Integer)((DropDownField)tableRow.get(5)).getSelectedKey();
            
        }else if (lockedOrderIds.size() == 0) {
            lastShippedFrom = null;
            lastShippedTo = null;
            
        }
    }

    //
    // start table model listener methods
    //
    public void cellUpdated(SourcesTableModelEvents sender, int row, int cell) {
    }

    public void dataChanged(SourcesTableModelEvents sender) {
    }

    public void rowAdded(SourcesTableModelEvents sender, int rows) {
    }

    public void rowDeleted(SourcesTableModelEvents sender, int row) {
    }

    public void rowSelected(SourcesTableModelEvents sender, final int row) {
        removeRowButton.changeState(ButtonState.DISABLED);
        addLocationButton.changeState(ButtonState.DISABLED);
        
        fetchSubForm(row, false);
       
    }

    public void rowUnselected(SourcesTableModelEvents sender, int row) {
    }

    public void rowUpdated(SourcesTableModelEvents sender, int row) {
    }

    public void unload(SourcesTableModelEvents sender) {
    }
    //
    // end table model listener methods
    //

    //
    // start table widget listener methods
    //
    public void finishedEditing(SourcesTableWidgetEvents sender, int row, int col) {
          if(col == 0){ 
              String checkedValue = (String)fillItemsTable.model.getCell(row, 0);

              if(CheckBox.CHECKED.equals(checkedValue))
                  lockAndReloadRow(row);
              else if(CheckBox.UNCHECKED.equals(checkedValue))
                  unlockRow(row);
  
          }
    }

    public void startEditing(SourcesTableWidgetEvents sender, int row, int col) {
        
    }

    public void stopEditing(SourcesTableWidgetEvents sender, int row, int col) {
    }
    //
    // end table widget listener methods
    //
    
    public void lockAndReloadRow(final int row){
        if(lockedOrderIds.getByKey(getOrderId(row)) != null)
            return;
        
        // ship from and ship to need to be the same since we are sending a 
        // single mailing out. Also will not allow mixed internal and external 
        // orders since ship from/to on internal orders are null
        Integer shippedFrom = (Integer)((DropDownField)fillItemsTable.model.getObject(row, 4)).getSelectedKey();
        Integer shippedTo = (Integer)((DropDownField)fillItemsTable.model.getObject(row, 5)).getSelectedKey();
        boolean haveMultiRow = lockedOrderIds.size() > 0;
        if (haveMultiRow && (ifDifferent(lastShippedFrom, shippedFrom) || ifDifferent(lastShippedTo, shippedTo))){
            window.setStatus(consts.get("shipFromshipToInvalidException"), "ErrorPanel");
            fillItemsTable.model.setCell(row, 0, "N");
            return;
        }
        
        // fetch the row and lock it
        Integer orderId = (Integer)fillItemsTable.model.getCell(row, 1);
        FillOrderItemInfoRPC foiirpc = new FillOrderItemInfoRPC();
        foiirpc.key = orderId;
        
        screenService.call("fetchOrderItemAndLock", foiirpc, new SyncCallback() {
               public void onSuccess(Object obj) {
                   FillOrderItemInfoRPC result = (FillOrderItemInfoRPC)obj;

                   // load the data in the table
                   DataSet<Integer> newTableRow = result.tableData.get(0);
                   newTableRow.get(0).setValue("Y");
                   fillItemsTable.model.setRow(row, result.tableData.get(0));
                   fillItemsTable.model.refresh();

                   //add the order id to the checked order ids model
                   DataSet<Integer> set = new DataSet<Integer>();
                   set.setKey(getOrderId(row));
                   lockedOrderIds.add(set);
                   
                   Integer shippedFrom = (Integer)((DropDownField)fillItemsTable.model.getObject(row, 4)).getSelectedKey();
                   Integer shippedTo = (Integer)((DropDownField)fillItemsTable.model.getObject(row, 5)).getSelectedKey();
                   boolean haveMultiRow = lockedOrderIds.size() > 1;
                   if (haveMultiRow && (ifDifferent(lastShippedFrom, shippedFrom) || ifDifferent(lastShippedTo, shippedTo))){
                       // uncheck the row and throw a form error
                       window.setStatus(consts.get("shipFromshipToInvalidException"), "ErrorPanel");
                       fillItemsTable.model.setCell(row, 0, "N");
                       unlockRow(row);
                   } else {
                       window.setStatus("", "");
                       fetchSubForm(row, true);
                       //FIXME not sure where this call gets put... fillOrderCheck(row, true);
                   }

               }

               public void onFailure(Throwable caught) {
                   // we need to uncheck the checkbox
                   fillItemsTable.model.setCell(row, 0, "N");

                   Window.alert(caught.getMessage());
               }
           });
    }
    
    public void unlockRow(final int row){
        boolean unlock;
        
        if(lockedOrderIds.getByKey(getOrderId(row)) == null)
            return;
        
        //unlock the record
        FillOrderItemInfoRPC foiirpc = new FillOrderItemInfoRPC();
        foiirpc.key = getOrderId(row);

        FillOrderItemInfoForm tableRowSubRPC = (FillOrderItemInfoForm)fillItemsTable.model.getRow(row).getData();
        
        unlock = true;
        if (tableRowSubRPC.changed) 
            unlock = Window.confirm(consts.get("fillOrderItemsChangedConfirm"));
        
        if(unlock){
            screenService.call("unlockOrder", foiirpc, new AsyncCallback<FillOrderItemInfoRPC>() {
                public void onSuccess(FillOrderItemInfoRPC result) {
                    lockedOrderIds.delete(lockedOrderIds.getByKey(getOrderId(row)));
                    
                    rebuildOrderItemsTree(row);
                    
                    loadSubForm(row);
               }

               public void onFailure(Throwable caught) {
                   // we need to recheck the checkbox
                   fillItemsTable.model.setCell(row, 0, "Y");
                   Window.alert(caught.getMessage());
               }
            });
        } else {
            // set the check box back to checked
            fillItemsTable.model.setCell(row, 0, "Y");
        }
    }
    
    public void fetchSubForm(final int row, boolean refresh){
        DataSet<FillOrderItemInfoRPC> tableRow = null;
        
        if(row >= 0){
            tableRow = fillItemsTable.model.getRow(row);

            FillOrderItemInfoForm foii = (FillOrderItemInfoForm)tableRow.getData();
            
            if(foii == null || refresh){
                //fetch the data
                FillOrderItemInfoRPC foirpc = new FillOrderItemInfoRPC();
                foirpc.key = getOrderId(row);
                foirpc.form = new FillOrderItemInfoForm(rpc.form.itemInformation.node);
                foirpc.form.originalOrderItemsTree.setValue(emptyTreeModel.clone());
                
                window.setStatus("","spinnerIcon");

                screenService.call("getOrderItemsOrderNotes", foirpc, new AsyncCallback<FillOrderItemInfoRPC>() {
                   public void onSuccess(FillOrderItemInfoRPC result) {
                       fillItemsTable.model.getRow(row).setData(result.form);

                       //rebuild the tree
                       rebuildOrderItemsTree(row);
                       
                       loadSubForm(row);
                     
                       window.setStatus("", "");
                       
                   }

                   public void onFailure(Throwable caught) {
                       Window.alert(caught.getMessage());
                   }
               });
            }else{
                loadSubForm(row);
            }
        }

    }
    
    public void loadSubForm(int row){
        if(row == fillItemsTable.model.getSelectedIndex()){
            FillOrderItemInfoForm subRPC = (FillOrderItemInfoForm)fillItemsTable.model.getRow(row).getData();
            
            if(isRowChecked(row)){
                subRPC.displayOrderItemsTree.setValue(combinedTree);
            }else{
                subRPC.displayOrderItemsTree.setValue(subRPC.originalOrderItemsTree.getValue());
            }
            
            load(subRPC);
        }
    }
   
    //
    // start tree manager methods
    //
    public boolean canAdd(TreeWidget widget, TreeDataItem set, int row) {
        return false;
    }

    public boolean canClose(TreeWidget widget, TreeDataItem set, int row) {
        return true;
    }

    public boolean canDelete(TreeWidget widget, TreeDataItem set, int row) {
        return false;
    }

    public boolean canDrag(TreeWidget widget, TreeDataItem item, int row) {
        return false;
    }

    public boolean canDrop(TreeWidget widget,
                           Widget dragWidget,
                           TreeDataItem dropTarget,
                           int targetRow) {
        return false;
    }

    public boolean canEdit(TreeWidget widget, TreeDataItem set, int row, int col) {
        // make sure the row is checked, we arent a parent leaf, and its col 0 or 3
        return (col == 0 || col == 3) && orderItemsTree.model.getData() == combinedTree && "orderItem".equals(set.leafType);

    }

    public boolean canOpen(TreeWidget widget, TreeDataItem addRow, int row) {
        return true;
    }

    public boolean canSelect(TreeWidget widget, TreeDataItem set, int row) {
        if (state == FormInt.State.ADD)
            return true;
        return false;
    }

    public void drop(TreeWidget widget,
                     Widget dragWidget,
                     TreeDataItem dropTarget,
                     int targetRow) {
    }

    public void drop(TreeWidget widget, Widget dragWidget) {
    }
    //
    // end tree manager methods
    //

    //
    // start tree widget listener methods
    //
    public void finishedEditing(SourcesTreeWidgetEvents sender, int row, int col) {
        // we need to set the changed flag is the value is changed
        // we also need to save the new model if the value is changed
        if (orderItemsTree.model.getCell(row, col) != null && !orderItemsTree.model.getCell(row, col).equals(lastTreeValue)) {
            if (col == 0) {
                // we need to add up all the child nodes and get a new parent quantity, if necessary
                TreeDataItem item = orderItemsTree.model.getRow(row);
                Integer newTotalQuantity = new Integer(0);
                if (item.parent != null) {
                    for (int i = 0; i < item.parent.getItems().size(); i++) {
                        newTotalQuantity += (Integer)item.parent.getItems()
                                                                .get(i)
                                                                .get(0)
                                                                .getValue();
                    }

                    item.parent.setData(new IntegerObject(newTotalQuantity));
                    // orderItemsTree.model.refresh();
                }

                combinedTree = (TreeDataModel)orderItemsTree.model.unload()
                                                                     .clone();

            } else if (col == 2) {
                TreeDataItem item = orderItemsTree.model.getRow(row);
                ArrayList selections = (ArrayList)((DropDownField)item.get(3)).getValue();

                DataSet<Field> set = null;
                if (selections.size() > 0)
                    set = (DataSet<Field>)selections.get(0);

                if (set != null && set.size() > 1) {
                    // set the lot num
                    orderItemsTree.model.setCell(row,
                                                 4,
                                                 (String)((DataObject)set.get(1)).getValue());

                    // set the new quantity on hand
                    orderItemsTree.model.setCell(row,
                                                 5,
                                                 (Integer)((DataObject)set.get(2)).getValue());
                }

                combinedTree = (TreeDataModel)orderItemsTree.model.unload().clone();

            }

            // need to add a changed flag to the fill order table row
            int currentTableRow = fillItemsTable.model.getSelectedIndex();
            FillOrderOrderItemsKey hiddenData = (FillOrderOrderItemsKey)orderItemsTree.model.getRow(row).getData();
            hiddenData.tableRowId = new Integer(currentTableRow);
            FillOrderItemInfoForm tableRowSubRpc = (FillOrderItemInfoForm)fillItemsTable.model.getRow(currentTableRow).getData();
            tableRowSubRpc.changed = true;
        }

        lastTreeValue = null;

    }

    public void startedEditing(SourcesTreeWidgetEvents sender, int row, int col) {
            lastTreeValue = orderItemsTree.model.getCell(row, col);
    }

    public void stopEditing(SourcesTreeWidgetEvents sender, int row, int col) {
    }

    //
    // end tree widget listener methods
    //

    //
    // auto complete method
    //
    public void callForMatches(final AutoComplete widget, DataModel model, String text) {
        Integer currentTreeRow = orderItemsTree.model.getSelectedIndex();
        FillOrderLocationAutoRPC folarpc = new FillOrderLocationAutoRPC();
        folarpc.cat = widget.cat;
        folarpc.match = text;
        folarpc.id = (Integer)((DropDownField)orderItemsTree.model.getObject(currentTreeRow, 2)).getSelectedKey();

        screenService.call("getMatchesObj",
                           folarpc,
                           new AsyncCallback<FillOrderLocationAutoRPC>() {
                               public void onSuccess(FillOrderLocationAutoRPC result) {
                                   widget.showAutoMatches(result.autoMatches);
                               }

                               public void onFailure(Throwable caught) {
                                   if (caught instanceof FormErrorException) {
                                       window.setStatus(caught.getMessage(),
                                                        "ErrorPanel");
                                   } else
                                       Window.alert(caught.getMessage());
                               }
                           });
    }

    private DataModel createDataModelFromTree(TreeDataModel treeModel) {
        DataModel model = new DataModel();
        for (int i = 0; i < treeModel.size(); i++) {
            TreeDataItem row = treeModel.get(i);

            if (row.getItems().size() > 0) {
                for (int j = 0; j < row.getItems().size(); j++) {
                    DataSet<Field> tableRow = new DataSet<Field>();
                    TreeDataItem childRow = row.getItems().get(j);
                    if (!((Integer)childRow.get(0).getValue()).equals(new Integer(0))) {
                        tableRow.add(childRow.get(0));
                        tableRow.add(childRow.get(2));

                        tableRow.setData(new IntegerObject((Integer)((DropDownField)childRow.get(3)).getSelectedKey()));
                        model.add(tableRow);
                    }
                }
            } else {
                if (!((Integer)row.get(0).getValue()).equals(new Integer(0))) {
                    DataSet tableRow = new DataSet();
                    tableRow.add(row.get(0));
                    tableRow.add(row.get(2));

                    // TODO made this change...not sure what the ramifications are yet
                    tableRow.setData(new IntegerObject((Integer)((DropDownField)row.get(3)).getSelectedKey()));
                    // tableRow.setData((FieldType)((IntegerObject)row.getData()).clone());

                    model.add(tableRow);
                }
            }
        }
        return model;
    }

    public boolean canDrop(TreeWidget widget,
                           Widget dragWidget,
                           Widget dropWidget) {
        return false;
    }

    public void onRemoveRowButtonClick() {
        orderItemsTree.model.deleteRow(orderItemsTree.model.getSelectedIndex());
        combinedTree = (TreeDataModel)orderItemsTree.model.unload().clone();
    }

    public void onAddLocationButtonClick() {
        TreeDataItem selectedRow = null;
        int currentTreeRow = orderItemsTree.model.getSelectedIndex();
        // make sure the selected row is at the lowest level
        if (orderItemsTree.model.getRow(currentTreeRow).getItems().size() == 0)
            selectedRow = orderItemsTree.model.getRow(currentTreeRow);
        else
            selectedRow = orderItemsTree.model.getRow(currentTreeRow)
                                              .getItem(0);

        TreeDataItem item = orderItemsTree.model.createTreeItem("orderItem");
        // item.get(0).setValue(selectedRow.get(0).getValue());
        item.get(1).setValue(selectedRow.get(1).getValue());
        ((DropDownField)item.get(2)).setModel(((DropDownField)selectedRow.get(2)).getModel());
        item.get(2).setValue(selectedRow.get(2).getValue());

        FillOrderOrderItemsKey treeRowHiddenData = (FillOrderOrderItemsKey)((FillOrderOrderItemsKey)selectedRow.getData()).clone();
        item.setData(treeRowHiddenData);

        if (selectedRow.parent == null) {
            selectedRow.addItem(item);
            if (!selectedRow.open)
                selectedRow.toggle();
        } else {
            selectedRow.parent.addItem(item);

        }

        // TODO eventually select the first column of the added row

        orderItemsTree.model.refresh();
    }

    //
    // start tree model listener methods
    //
    public void cellUpdated(SourcesTreeModelEvents sender, int row, int cell) {
    }

    public void dataChanged(SourcesTreeModelEvents sender) {
    }

    public void rowAdded(SourcesTreeModelEvents sender, int rows) {
    }

    public void rowClosed(SourcesTreeModelEvents sender,
                          int row,
                          TreeDataItem item) {
    }

    public void rowDeleted(SourcesTreeModelEvents sender, int row) {
    }

    public void rowOpened(SourcesTreeModelEvents sender,
                          int row,
                          TreeDataItem item) {
    }

    public void rowSelectd(SourcesTreeModelEvents sender, int row) {
        boolean parentNull = ((TreeDataItem)orderItemsTree.model.getRow(row)).parent == null;
        if (parentNull || (!parentNull && ((TreeDataItem)orderItemsTree.model.getRow(row)).childIndex == 0))
            removeRowButton.changeState(ButtonState.DISABLED);
        else
            removeRowButton.changeState(ButtonState.UNPRESSED);

        if (state == State.ADD && CheckBox.CHECKED.equals(fillItemsTable.table.model.getCell(fillItemsTable.model.getSelectedIndex(), 0)))
            addLocationButton.changeState(ButtonState.UNPRESSED);

    }

    public void rowUnselected(SourcesTreeModelEvents sender, int row) {
    }

    public void rowUpdated(SourcesTreeModelEvents sender, int row) {
    }

    public void unload(SourcesTreeModelEvents sender) {
    }

    //
    // end tree model listener
    //

    protected boolean validate() {
        boolean valid = true;
        // we need to iterate through the tree and make sure there is enough qty set to fill the orders
        for (int i = 0; i < combinedTree.size(); i++) {
            TreeDataItem row = combinedTree.get(i);
            if (row.getData() != null) {
                Integer totalQty = ((IntegerObject)row.getData()).getValue();
                if (totalQty.compareTo((Integer)row.get(0).getValue()) < 0) {
                    valid = false;
                    ((IntegerField)row.get(0)).addError(consts.get("fillOrderQtyException"));
                }
            }

        }
        return valid;
    }

    public void setStatusIdModel(DataModel<Integer> statusIdsModel) {
        ((TableDropdown)fillItemsTable.table.columns.get(2).getColumnWidget()).setModel(statusIdsModel);
        ((TableDropdown)fillItemsQueryTable.columns.get(2).getColumnWidget()).setModel(statusIdsModel);
    }

    public void setCostCentersModel(DataModel<Integer> costCentersModel) {
        costCenter.setModel(costCentersModel);
    }

    public void setShipFromModel(DataModel<Integer> shipFromsModel) {
        ((TableDropdown)fillItemsTable.table.columns.get(4).getColumnWidget()).setModel(shipFromsModel);
        ((TableDropdown)fillItemsQueryTable.columns.get(4).getColumnWidget()).setModel(shipFromsModel);
    }
    
    public boolean ifDifferent(Object arg1, Object arg2){
        return (arg1 == null && arg2 != null) || (arg1 != null && !arg1.equals(arg2));
        
    }
    
    public boolean isRowChecked(int row){
        return "Y".equals(fillItemsTable.model.getCell(row, 0));
    }
    
    public Integer getOrderId(int row){
        return (Integer)fillItemsTable.table.model.getCell(row, 1);
    }
    
    
}
