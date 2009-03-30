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

import org.openelis.gwt.common.DataSorter;
import org.openelis.gwt.common.DataSorterInt;
import org.openelis.gwt.common.Form;
import org.openelis.gwt.common.FormErrorException;
import org.openelis.gwt.common.Query;
import org.openelis.gwt.common.data.CheckField;
import org.openelis.gwt.common.data.TableDataModel;
import org.openelis.gwt.common.data.DataObject;
import org.openelis.gwt.common.data.TableDataRow;
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
import org.openelis.gwt.widget.ModelUtil;
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
import org.openelis.gwt.widget.tree.TreeRenderer;
import org.openelis.gwt.widget.tree.TreeWidget;
import org.openelis.gwt.widget.tree.event.SourcesTreeModelEvents;
import org.openelis.gwt.widget.tree.event.SourcesTreeWidgetEvents;
import org.openelis.gwt.widget.tree.event.TreeModelListener;
import org.openelis.gwt.widget.tree.event.TreeWidgetListener;
import org.openelis.metamap.FillOrderMetaMap;
import org.openelis.modules.main.client.OpenELISScreenForm;
import org.openelis.modules.shipping.client.ShippingDataService;
import org.openelis.modules.shipping.client.ShippingItemsData;
import org.openelis.modules.shipping.client.ShippingScreen;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.SyncCallback;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

public class FillOrderScreen extends OpenELISScreenForm<FillOrderForm, Query<TableDataRow<Integer>>> implements
                                                                                             ClickListener,
                                                                                             AutoCompleteCallInt,
                                                                                             TableManager,
                                                                                             TableWidgetListener,
                                                                                             TableModelListener,
                                                                                             TreeManager,
                                                                                             TreeWidgetListener,
                                                                                             CommandListener,
                                                                                             TreeModelListener,
                                                                                             DataSorterInt{
    
    private Dropdown            costCenter;
    private TreeWidget          orderItemsTree;
    private ResultsTable        fillItemsTable;
    //private QueryTable          fillItemsQueryTable;
    private AppButton           removeRowButton, addLocationButton;
    private DataSorter sorter;

    private TreeDataModel       combinedTree, emptyTreeModel;
    private List<Integer>                combinedTreeIds;
    private TableDataModel<TableDataRow<Integer>>  lockedIndexes = new TableDataModel<TableDataRow<Integer>>();
    private Integer             lastShippedTo   = null;
    private Integer             lastShippedFrom = null;
    private Object              lastTreeValue;
    private Integer             orderPendingValue;

    private FillOrderMetaMap    OrderMeta       = new FillOrderMetaMap();

    private KeyListManager      keyList         = new KeyListManager();

    AsyncCallback<FillOrderForm> checkModels     = new AsyncCallback<FillOrderForm>() {
                                                    public void onSuccess(FillOrderForm form) {
                                                        if (form.statuses != null) {
                                                            setStatusIdModel(form.statuses);
                                                            form.statuses = null;
                                                        }
                                                        if (form.costCenters != null) {
                                                            setCostCentersModel(form.costCenters);
                                                            form.costCenters = null;
                                                        }
                                                        if (form.shipFroms != null) {
                                                            setShipFromModel(form.shipFroms);
                                                            form.shipFroms = null;
                                                        }
                                                    }

                                                    public void onFailure(Throwable caught) {

                                                    }
                                                };

    public FillOrderScreen() {
        super("org.openelis.modules.fillOrder.server.FillOrderService");
        query = new Query<TableDataRow<Integer>>();
        getScreen(new FillOrderForm());
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
        } else if (action == ShippingScreen.Action.Commited) {
            setOrdersToProcessedCommit();
        } else if (action == ShippingScreen.Action.Aborted) {
            onShippingScreenAbort();
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
        //fillItemsQueryTable = (QueryTable)((ScreenQueryTable)sw.getQueryWidget()).getWidget();
        fillItemsTable.table.addTableWidgetListener(this);
        fillItemsTable.model.addTableModelListener(this);
        sorter = new DataSorter();
        ((TableModel)fillItemsTable.model).sorter = this;
        
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

        setCostCentersModel(form.costCenters);
        setStatusIdModel(form.statuses);
        setShipFromModel(form.shipFroms);
        
        orderPendingValue = form.orderPendingValue;

        /*
         * Null out the form models so they are not sent with future form calls
         */
        form.costCenters = null;
        form.statuses = null;
        form.shipFroms = null;

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
        form.fillItemsTable.setValue(fillItemsTable.model.getData());
        form.itemInformation.originalOrderItemsTree.setValue(emptyTreeModel);
        

    }

    public void add() {
        // super.add();
        form.entityKey = null;
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

        fillItemsTable.table.model.setCell(0, 2, new TableDataRow<Integer>(orderPendingValue));
        fillItemsTable.table.select(0, 1);

        orderItemsTree.model.clear();
        lockedIndexes.clear();
        combinedTree.clear();
        
    }

    public void commit() {
        if (state == State.ADD) {
            //FIXME change this to select first checked row if not already checked
            if(lockedIndexes.size() > 0 && (fillItemsTable.model.getSelectedIndex() == -1 || !isRowChecked(fillItemsTable.model.getSelectedIndex())))
                fillItemsTable.model.selectRow(((Integer)lockedIndexes.get(0).key).intValue());
                
            clearErrors();
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
            clearErrors();
            //resetForm();
            //load();
            enable(false);
            changeState(State.DISPLAY);
            //clear the tree so we dont have to piecemeal clear it
            combinedTree.clear();
            
            unlockRows((TableDataModel<TableDataRow<Integer>>)lockedIndexes.clone(), true);
            
            //reassert the subform so it shows the current selected row
            //TODO this isnt loading the tree correctly because unlock rows hasnt come back yet
            //loadSubForm(fillItemsTable.model.getSelectedIndex());

            //combinedTree.clear();
//            orderItemsTree.model.clear();
  //          lastShippedFrom = new Integer(-1);
    //        lastShippedTo = new Integer(-1);

            /*
            TableDataModel<TableDataRow<Integer>> unlockModel = new TableDataModel<TableDataRow<Integer>>();
            for(int i=0; i<fillItemsTable.model.numRows(); i++){
                if(isRowChecked(i)){
                    fillItemsTable.model.setCell(i, 0, CheckBox.UNCHECKED);
                    unlockModel.add(fillItemsTable.model.getRow(i));
                }
            }
            */
            
            
            
            /*nlockRow(final int row, true);
            // prepare the argument list for the getObject function
            FillOrderItemInfoForm foirpc = new FillOrderItemInfoForm();
            foirpc.key = form.key;
            foirpc.form = form.itemInformation;
            foirpc.tableData = keyList.getList();

            screenService.call("commitQueryAndUnlock",
                               foirpc,
                               new AsyncCallback<FillOrderItemInfoForm>() {
                                   public void onSuccess(FillOrderItemInfoForm result) {
                                       fillItemsTable.model.load(result.tableData);

                                       window.setStatus("", "");
                                       changeState(State.DISPLAY);
                                   }

                                   public void onFailure(Throwable caught) {
                                       Window.alert(caught.getMessage());
                                       window.setStatus("", "");
                                   }
                               });
                               */
                              
        } else
            super.abort();
    }
    
    public void setOrdersToProcessedCommit(){
        TableDataModel<TableDataRow<Integer>> lockedSets = getLockedSetsFromLockedList(lockedIndexes);
        //set the order to processed
        //FillOrderItemInfoForm foiirpc = new FillOrderItemInfoForm();
        //foiirpc.orm = form.itemInformation;
        form.itemInformation.originalOrderItemsTree.setValue(combinedTree);
        
        form.itemInformation.tableData = lockedSets;

        screenService.call("setOrderToProcessed", form.itemInformation, new AsyncCallback<FillOrderItemInfoForm>() {
            public void onSuccess(FillOrderItemInfoForm result) {
                //set the rows that were selected because we refetched the data
                for(int i=0; i<lockedIndexes.size(); i++){
                    int row = lockedIndexes.get(i).key;
                    result.tableData.get(i).setData((FillOrderItemInfoForm)fillItemsTable.model.getRow(row).getData());
                    fillItemsTable.model.setRow(row, result.tableData.get(i));
                    fillItemsTable.model.setCell(row, 0, CheckBox.UNCHECKED);
                }
            }

            public void onFailure(Throwable caught) {
                Window.alert(caught.getMessage());
            }
        });
        
        unlockRows((TableDataModel<TableDataRow<Integer>>)lockedIndexes.clone(), true);
    }
    
    public void onShippingScreenAbort(){
        //clear the tree so we dont have to piecemeal clear it
        combinedTree.clear();
        
        unlockRows((TableDataModel<TableDataRow<Integer>>)lockedIndexes.clone(), true);
        
        //reassert the subform so it shows the current selected row
        loadSubForm(fillItemsTable.model.getSelectedIndex());
        
        window.setStatus(consts.get("shippingScreenAbort"),"ErrorPanel");
    }

    //
    // start table manager methods
    //
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
        return state == State.QUERY || (state == State.ADD && col == 0);

    }

    public boolean canSelect(TableWidget widget, TableDataRow set, int row) {
        return state == State.QUERY || state == State.ADD || state == State.DISPLAY;

    }

    public boolean canDrag(TableWidget widget, TableDataRow item, int row) {
        return false;
    }

    public boolean canDrop(TableWidget widget,
                           Widget dragWidget,
                           TableDataRow dropTarget,
                           int targetRow) {
        return false;
    }

    public void drop(TableWidget widget,
                     Widget dragWidget,
                     TableDataRow dropTarget,
                     int targetRow) {
    }

    public void drop(TableWidget widget, Widget dragWidget) {
    }

    //
    // end table manager methods
    //
    @SuppressWarnings(value={"unchecked"})
    private void onProcessingCommitClick() {
        if(lockedIndexes.size()  == 0){
            //throw error and return
            window.setStatus(consts.get("fillOrderNoneChecked"), "ErrorPanel");
            return;
        }

        //FillOrderItemInfoForm foiirpc = new FillOrderItemInfoForm();
        //foiirpc.form = form.itemInformation;
        form.itemInformation.originalOrderItemsTree.setValue(combinedTree);
        
        screenService.call("validateOrders", form.itemInformation, new SyncCallback() {
            public void onSuccess(Object obj) {
                FillOrderItemInfoForm result = (FillOrderItemInfoForm)obj;
                FillOrderItemInfoForm rForm = result;
                if(rForm.status.equals(Form.Status.invalid)){
                    if(rForm.getErrors().size() > 0){
                        if(rForm.getErrors().size() > 1){
                            window.setMessagePopup((String[])rForm.getErrors().toArray(new String[rForm.getErrors().size()]), "ErrorPanel");
                            window.setStatus("(Error 1 of "+rForm.getErrors().size()+") "+(String)rForm.getErrors().get(0), "ErrorPanel");
                        }else
                            window.setStatus((String)rForm.getErrors().get(0),"ErrorPanel");
                    }
                    return;
                }

                //get the first row that is checked
                TableDataRow<Integer> row = fillItemsTable.model.getRow(lockedIndexes.get(0).key);

                if (((CheckField)row.cells[9]).isChecked())
                    setOrdersToProcessedCommit();   
                else    
                    createShippingScreen(row);
            }

            public void onFailure(Throwable caught) {
                Window.alert(caught.getMessage());
            }
        });
    }
    
    private void createShippingScreen(TableDataRow<Integer> row){
        if (lockedIndexes.size() == 0 || row == null)
            return;
        
        PopupPanel shippingPopupPanel = new PopupPanel(false, true);
        ScreenWindow pickerWindow = new ScreenWindow(shippingPopupPanel, "Shipping", "shippingScreen", "Loading...");

        FillOrderItemInfoForm tableRowSubRPC = (FillOrderItemInfoForm)row.getData();
        ShippingScreen shippingScreen = new ShippingScreen();

        ShippingDataService data = new ShippingDataService();
        data.setShipFromId((Integer)((DropDownField<Integer>)row.cells[4]).getSelectedKey());
        data.setShipToId((Integer)((DropDownField<Integer>)row.cells[5]).getSelectedKey());
        data.setShipToText((String)((DropDownField<Integer>)row.cells[5]).getTextValue());
        data.setMultUnitText(tableRowSubRPC.multUnit.getValue());
        data.setStreetAddressText(tableRowSubRPC.streetAddress.getValue());
        data.setCityText(tableRowSubRPC.city.getValue());
        data.setStateText(tableRowSubRPC.state.getValue());
        data.setZipCodeText(tableRowSubRPC.zipCode.getValue());
        data.setItemsShippedModel(createDataModelFromTree((TreeDataModel)combinedTree.clone()));
        
        shippingScreen.setTarget(this);
        shippingScreen.setShippingData(data);

        pickerWindow.setContent(shippingScreen);
        shippingPopupPanel.add(pickerWindow);
        int left = this.getAbsoluteLeft();
        int top = this.getAbsoluteTop();
        shippingPopupPanel.setPopupPosition(left, top);
        shippingPopupPanel.show();
    }

    private void rebuildOrderItemsTree(int row) {
        boolean isChecked, alreadyCombined;
        TableDataRow<Integer> tableRow = fillItemsTable.model.getRow(row);
        
        FillOrderItemInfoForm subRpc = (FillOrderItemInfoForm)tableRow.getData();
        
        if (subRpc == null)
            return;
       
        TreeDataModel orgTreeModel = subRpc.originalOrderItemsTree.getValue();
        isChecked = isRowChecked(row);
        alreadyCombined = combinedTreeIds.contains(getOrderId(row));
        
        if (isChecked && !alreadyCombined) {
            // take the treefield in the form and move it to the original param in the form for safe keeping if the original is null
            for (int i = 0; i < orgTreeModel.size(); i++) {
                TreeDataItem set = (TreeDataItem)orgTreeModel.get(i);

                TreeDataItem childRow = orderItemsTree.model.createTreeItem("orderItem");
                //TreeDataItem parentItemRow = orderItemsTree.model.createTreeItem("top");
                // TreeDataItem locRow = orderItemsTree.model.createTreeItem("orderItem");
                set.addItem(childRow);
                //parentItemRow.addItem(set);
                childRow.cells[0].setValue(set.cells[0].getValue());
                childRow.cells[1].setValue(set.cells[1].getValue());
                childRow.setData(set.getData());
                set.setData(new IntegerObject((Integer)set.cells[0].getValue()));

                if (set.cells[2].getValue() != null) {
                    ((DropDownField)childRow.cells[2]).setModel(((DropDownField)set.cells[2]).getModel());
                    ((DropDownField)childRow.cells[2]).setValue(((DropDownField)set.cells[2]).getValue());
                }

                combinedTree.add(set);
                
            }
            combinedTreeIds.add(getOrderId(row));
            
        } else if(!isChecked && alreadyCombined){
            int k = 0;
            while (k < combinedTree.size()) {

                if (((Integer)combinedTree.get(k).cells[1].getValue()).equals(tableRow.cells[1].getValue())) { // we need to remove this row and children
                    combinedTree.delete(k);
                    k--;
                }
                k++;
            }
            combinedTreeIds.remove(getOrderId(row));
        }
        
        if(combinedTreeIds.contains(getOrderId(row)) && 
               (lastShippedFrom == null || lastShippedTo == null)){
            lastShippedFrom = (Integer)((DropDownField)tableRow.cells[4]).getSelectedKey();
            lastShippedTo = (Integer)((DropDownField)tableRow.cells[5]).getSelectedKey();
            
        }else if (lockedIndexes.size() == 0) {
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
        //removeRowButton.changeState(ButtonState.DISABLED);
        //addLocationButton.changeState(ButtonState.DISABLED);
        if(state != State.QUERY)
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

              if(CheckBox.CHECKED.equals(checkedValue)){
                  if(((DropDownField<Integer>)fillItemsTable.model.getObject(row, 2)).getSelectedKey().equals(orderPendingValue))
                      lockAndReloadRow(row);
                  else{
                      window.setStatus(consts.get("fillOrderOnlyPendingRowsCanBeChecked"), "ErrorPanel");
                      fillItemsTable.model.setCell(row, 0, CheckBox.UNCHECKED);
                  }
              }else if(CheckBox.UNCHECKED.equals(checkedValue))
                  unlockRow(row, false);
  
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
        if(ModelUtil.getRowByKey(lockedIndexes,row) != null)
            return;
        
        // ship from and ship to need to be the same since we are sending a 
        // single mailing out. Also will not allow mixed internal and external 
        // orders since ship from/to on internal orders are null
        Integer shippedFrom = (Integer)((DropDownField)fillItemsTable.model.getObject(row, 4)).getSelectedKey();
        Integer shippedTo = (Integer)((DropDownField)fillItemsTable.model.getObject(row, 5)).getSelectedKey();
        boolean haveMultiRow = lockedIndexes.size() > 0;
        if (haveMultiRow && (ifDifferent(lastShippedFrom, shippedFrom) || ifDifferent(lastShippedTo, shippedTo))){
            window.setStatus(consts.get("shipFromshipToInvalidException"), "ErrorPanel");
            fillItemsTable.model.setCell(row, 0, "N");
            return;
        }
        
        // fetch the row and lock it
        Integer orderId = (Integer)fillItemsTable.model.getCell(row, 1);
        FillOrderItemInfoForm foiirpc = new FillOrderItemInfoForm();
        foiirpc.entityKey = orderId;
        
        screenService.call("fetchOrderItemAndLock", foiirpc, new SyncCallback() {
               public void onSuccess(Object obj) {
                   FillOrderItemInfoForm result = (FillOrderItemInfoForm)obj;

                   // load the data in the table
                   //TableDataRow<Integer> newTableRow = result.tableData.get(0);
                   //newTableRow.get(0).setValue("Y");
                   //Window.alert("["+row+"]");
                   result.tableData.get(0).setData((FillOrderItemInfoForm)fillItemsTable.model.getRow(row).getData());
                   fillItemsTable.model.setRow(row, result.tableData.get(0));
                   fillItemsTable.model.setCell(row, 0, CheckBox.CHECKED);
                   //fillItemsTable.model.setRow(row, result.tableData.get(0));
                   
                   //add the order id to the checked order ids model
                   TableDataRow<Integer> set = new TableDataRow<Integer>();
                   set.key = (row);
                   lockedIndexes.add(set);
                   
                   Integer shippedFrom = (Integer)((DropDownField)fillItemsTable.model.getObject(row, 4)).getSelectedKey();
                   Integer shippedTo = (Integer)((DropDownField)fillItemsTable.model.getObject(row, 5)).getSelectedKey();
                   boolean haveMultiRow = lockedIndexes.size() > 1;
                   if (haveMultiRow && (ifDifferent(lastShippedFrom, shippedFrom) || ifDifferent(lastShippedTo, shippedTo))){
                       // uncheck the row and throw a form error
                       window.setStatus(consts.get("shipFromshipToInvalidException"), "ErrorPanel");
                       fillItemsTable.model.setCell(row, 0, "N");
                       unlockRow(row, false);
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
    
    public void unlockRow(final int row, final boolean forced){
        if(ModelUtil.getRowByKey(lockedIndexes,row) == null)
            return;
        
        TableDataModel<TableDataRow<Integer>> unlockModel = new TableDataModel<TableDataRow<Integer>>();
        unlockModel.add(new TableDataRow<Integer>(row));
        
        unlockRows(unlockModel, forced);
    }
    
    public void unlockRows(final TableDataModel<TableDataRow<Integer>> lockedRowIndexes, final boolean forced){
        if(lockedRowIndexes.size() == 0)
            return;
        
        boolean unlock;
        TableDataModel<TableDataRow<Integer>> lockedSets = getLockedSetsFromLockedList(lockedRowIndexes);
        //unlock the record
        FillOrderItemInfoForm foiirpc = new FillOrderItemInfoForm();
        foiirpc.tableData = lockedSets;

        FillOrderItemInfoForm tableRowSubRPC = (FillOrderItemInfoForm)lockedSets.get(0).getData();
        
        unlock = true;
        if (tableRowSubRPC != null && tableRowSubRPC.changed && !forced) 
            unlock = Window.confirm(consts.get("fillOrderItemsChangedConfirm"));
        
        if(unlock){
            screenService.call("unlockOrders", foiirpc, new AsyncCallback<FillOrderItemInfoForm>() {
                public void onSuccess(FillOrderItemInfoForm result) {
                    for(int i=0; i<lockedRowIndexes.size(); i++){
                        //uncheck the row
                        fillItemsTable.model.setCell(lockedRowIndexes.get(i).key, 0, CheckBox.UNCHECKED);
                        
                        lockedIndexes.delete(ModelUtil.getRowByKey(lockedIndexes,lockedRowIndexes.get(i).key));
                        
                        //rebuild tree for selected row
                        rebuildOrderItemsTree(lockedRowIndexes.get(i).key);
                    }
                    
                    //load sub form for selected table row
                    loadSubForm(fillItemsTable.model.getSelectedIndex());
                    
               }

               public void onFailure(Throwable caught) {
                   // we need to recheck the checkbox
                   //TODO not sure how to handle this    fillItemsTable.model.setCell(row, 0, "Y");
                   Window.alert(caught.getMessage());
               }
            });
        } else {
            // set the check box back to checked
            //TODO not sure how to hanlde this fillItemsTable.model.setCell(row, 0, "Y");
        }
    }
    
    public void fetchSubForm(final int row, boolean refresh){
        TableDataRow<FillOrderItemInfoForm> tableRow = null;
        
        if(row >= 0){
            tableRow = fillItemsTable.model.getRow(row);

            FillOrderItemInfoForm foii = (FillOrderItemInfoForm)tableRow.getData();
            
            if(foii == null || refresh){
                //fetch the data
                FillOrderItemInfoForm foirpc = new FillOrderItemInfoForm(form.itemInformation.node);
                foirpc.entityKey = getOrderId(row);
                foirpc.originalOrderItemsTree.setValue(emptyTreeModel.clone());
                
                
                screenService.call("getOrderItemsOrderNotes", foirpc, new AsyncCallback<FillOrderItemInfoForm>() {
                   public void onSuccess(FillOrderItemInfoForm result) {
                       fillItemsTable.model.getRow(row).setData(result);

                       //rebuild the tree
                       rebuildOrderItemsTree(row);
                       
                       loadSubForm(row);
                     
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
            
            orderItemsTree.model.unload();
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
        if (state == State.ADD)
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
                                                                .cells[0]
                                                                .getValue();
                    }

                    item.parent.setData(new IntegerObject(newTotalQuantity));
                    // orderItemsTree.model.refresh();
                }

                combinedTree = (TreeDataModel)orderItemsTree.model.unload();

            } else if (col == 3) {
                TreeDataItem item = orderItemsTree.model.getRow(row);
                ArrayList selections = (ArrayList)((DropDownField)item.cells[3]).getValue();

                TableDataRow<Field> set = null;
                if (selections.size() > 0)
                    set = (TableDataRow<Field>)selections.get(0);

                if (set != null && set.size() > 1) {
                    // set the lot num and quantity on hand
                    orderItemsTree.model.setCell(row, 4, (String)((DataObject)set.cells[1]).getValue());
                    orderItemsTree.model.setCell(row, 5, (Integer)((DataObject)set.cells[2]).getValue());
                }else{
                    orderItemsTree.model.setCell(row, 4, null);
                    orderItemsTree.model.setCell(row, 5, null);
                }

                combinedTree = (TreeDataModel)orderItemsTree.model.unload();

            }

            // need to add a changed flag to the fill order table row
            int currentTableRow = fillItemsTable.model.getSelectedIndex();
            FillOrderOrderItemsKey hiddenData = (FillOrderOrderItemsKey)orderItemsTree.model.getRow(row).getData();
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
    public void callForMatches(final AutoComplete widget, TableDataModel model, String text) {
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

    private TableDataModel createDataModelFromTree(TreeDataModel treeModel) {
        TableDataModel model = new TableDataModel();
        for (int i = 0; i < treeModel.size(); i++) {
            TreeDataItem row = treeModel.get(i);

            if (row.getItems().size() > 0) {
                for (int j = 0; j < row.getItems().size(); j++) {
                    TableDataRow<Field> tableRow = new TableDataRow<Field>(2);
                    TreeDataItem childRow = row.getItems().get(j);
                    if (!((Integer)childRow.cells[0].getValue()).equals(new Integer(0))) {
                        tableRow.cells[0] = (childRow.cells[0]);
                        tableRow.cells[1] = (childRow.cells[2]);

                        FillOrderOrderItemsKey key = (FillOrderOrderItemsKey)childRow.getData();
                        ShippingItemsData rowData = new ShippingItemsData();
                        rowData.referenceTableId = key.referenceTableId;
                        rowData.referenceId = key.referenceId;
                        tableRow.setData(rowData);

                        model.add(tableRow);
                    }
                }
            } else {
                if (!((Integer)row.cells[0].getValue()).equals(new Integer(0))) {
                    TableDataRow tableRow = new TableDataRow(2);
                    tableRow.cells[0] = (row.cells[0]);
                    tableRow.cells[1] = (row.cells[2]);

                    FillOrderOrderItemsKey key = (FillOrderOrderItemsKey)row.getData();
                    ShippingItemsData rowData = new ShippingItemsData();
                    rowData.referenceTableId = key.referenceTableId;
                    rowData.referenceId = key.referenceId;
                    tableRow.setData(rowData);

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
        item.cells[1].setValue(selectedRow.cells[1].getValue());
        ((DropDownField)item.cells[2]).setModel(((DropDownField)selectedRow.cells[2]).getModel());
        item.cells[2].setValue(selectedRow.cells[2].getValue());

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
        removeRowButton.changeState(ButtonState.DISABLED);
        addLocationButton.changeState(ButtonState.DISABLED);
    }

    public void rowUpdated(SourcesTreeModelEvents sender, int row) {
    }

    public void unload(SourcesTreeModelEvents sender) {
        removeRowButton.changeState(ButtonState.DISABLED);
        addLocationButton.changeState(ButtonState.DISABLED);
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
                Integer currentQty = (Integer)row.cells[0].getValue();
                if (currentQty > 0 && totalQty.compareTo((Integer)row.cells[0].getValue()) < 0) {
                    valid = false;
                    ((IntegerField)row.cells[0]).addError(consts.get("fillOrderQtyException"));
                }
            }
            
            //iterate through the children to make sure we will have enough qty on hand
            for(int j=0; j<row.getItems().size(); j++){
                TreeDataItem childRow = row.getItem(j);
                int qtyOnHand = ((Integer)childRow.cells[5].getValue()).intValue();
                int qtyRequested = ((Integer)childRow.cells[5].getValue()).intValue();
                if((qtyOnHand - qtyRequested < 0)){
                    valid = false;
                    ((IntegerField)childRow.cells[5]).addError(consts.get("notEnoughQuantityOnHand"));
                }
                
                if(qtyRequested < 0){
                    valid = false;
                    ((IntegerField)childRow.cells[5]).addError(consts.get("invalidQuantityException"));
                }
            }
        }
        
        if(!valid)
            ((TreeRenderer)orderItemsTree.renderer).dataChanged(orderItemsTree.model);
        
        return valid;
    }

    public void setStatusIdModel(TableDataModel<TableDataRow<Integer>> statusIdsModel) {
        ((TableDropdown)fillItemsTable.table.columns.get(2).getColumnWidget()).setModel(statusIdsModel);
      //  ((TableDropdown)fillItemsQueryTable.columns.get(2).getColumnWidget()).setModel(statusIdsModel);
    }

    public void setCostCentersModel(TableDataModel<TableDataRow<Integer>> costCentersModel) {
        costCenter.setModel(costCentersModel);
    }

    public void setShipFromModel(TableDataModel<TableDataRow<Integer>> shipFromsModel) {
        ((TableDropdown)fillItemsTable.table.columns.get(4).getColumnWidget()).setModel(shipFromsModel);
      //  ((TableDropdown)fillItemsQueryTable.columns.get(4).getColumnWidget()).setModel(shipFromsModel);
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
    
    public TableDataModel<TableDataRow<Integer>> getLockedSetsFromLockedList(TableDataModel<TableDataRow<Integer>> lockedList){
        TableDataModel<TableDataRow<Integer>> returnModel = new TableDataModel<TableDataRow<Integer>>();
        for(int i=0; i<lockedList.size(); i++)
            returnModel.add(fillItemsTable.model.getRow(lockedList.get(i).key));
        
        return returnModel;
    }

    public void sort(TableDataModel data, int col, SortDirection direction) {
        if(state != State.ADD)
            sorter.sort(data, col, direction);
        else
            window.setStatus("cant sort", "ErrorPanel");
    }
}
