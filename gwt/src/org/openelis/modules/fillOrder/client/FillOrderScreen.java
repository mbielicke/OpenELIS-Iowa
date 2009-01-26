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
package org.openelis.modules.fillOrder.client;

import java.util.ArrayList;

import org.openelis.gwt.common.FormErrorException;
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
import org.openelis.gwt.common.data.TreeDataItem;
import org.openelis.gwt.common.data.TreeDataModel;
import org.openelis.gwt.event.CommandListener;
import org.openelis.gwt.screen.CommandChain;
import org.openelis.gwt.screen.ScreenTableWidget;
import org.openelis.gwt.screen.ScreenWindow;
import org.openelis.gwt.widget.AutoComplete;
import org.openelis.gwt.widget.AutoCompleteCallInt;
import org.openelis.gwt.widget.ButtonPanel;
import org.openelis.gwt.widget.CheckBox;
import org.openelis.gwt.widget.Dropdown;
import org.openelis.gwt.widget.FormInt;
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
import org.openelis.gwt.widget.tree.event.SourcesTreeWidgetEvents;
import org.openelis.gwt.widget.tree.event.TreeWidgetListener;
import org.openelis.metamap.FillOrderMetaMap;
import org.openelis.modules.main.client.OpenELISScreenForm;
import org.openelis.modules.shipping.client.ShippingDataService;
import org.openelis.modules.shipping.client.ShippingScreen;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class FillOrderScreen extends OpenELISScreenForm implements ClickListener, AutoCompleteCallInt, TableManager, TableWidgetListener, TableModelListener, TreeManager, TreeWidgetListener, CommandListener{
    
    private static boolean loaded = false;
    private static DataModel costCenterDropdown, shipFromDropdown, statusDropdown;
    private ScreenTableWidget fillItemsTableScreenWidget;
    private TreeWidget orderItemsTree;
    private TableWidget fillItemsTable;
    private QueryTable fillItemsQueryTable;    
    
    private TextBox requestedByText, orgAptSuiteText, orgAddressText, orgCityText, orgStateText, orgZipCodeText;
    private TextArea orderShippingNotes;
    private Dropdown costCenterDrop;
    
    private TreeDataModel checkedTreeData;
    private DataModel checkedOrderIds = new DataModel();
    //private List checkedOrderIds = new ArrayList();
    private String tableCheckedValue;
    private int currentTableRow = -2;
    private int currentTreeRow = -1;
    private Integer lastShippedTo = new Integer(-1);
    private Integer lastShippedFrom = new Integer(-1);
    private Object lastTreeValue;
    private Integer orderPendingValue;
    
    private FillOrderMetaMap OrderMeta = new FillOrderMetaMap();
    
    private KeyListManager keyList = new KeyListManager();

    public FillOrderScreen() {
        super("org.openelis.modules.fillOrder.server.FillOrderService", !loaded);
    }
    
    public void performCommand(Enum action, Object obj) {
        if(action == KeyListManager.Action.FETCH){
            fetch();
        //}else if(action == ShippingScreen.Action.Drawn){
        //    ((ShippingScreen)obj).add();
        }else{
            super.performCommand(action, obj);
        }
    }      
    
    public void onClick(Widget sender) {

    }
    
    public void afterDraw(boolean success) {
        TableWidget d;
        QueryTable q;
        Dropdown drop;
        
        loaded = true;
        
        requestedByText = (TextBox)getWidget(OrderMeta.getRequestedBy());
        orgAptSuiteText = (TextBox)getWidget(OrderMeta.ORDER_ORGANIZATION_META.ADDRESS.getMultipleUnit());
        orgAddressText = (TextBox)getWidget(OrderMeta.ORDER_ORGANIZATION_META.ADDRESS.getStreetAddress());
        orgCityText = (TextBox)getWidget(OrderMeta.ORDER_ORGANIZATION_META.ADDRESS.getCity());
        orgStateText = (TextBox)getWidget(OrderMeta.ORDER_ORGANIZATION_META.ADDRESS.getState());
        orgZipCodeText = (TextBox)getWidget(OrderMeta.ORDER_ORGANIZATION_META.ADDRESS.getZipCode());
        costCenterDrop = (Dropdown)getWidget(OrderMeta.getCostCenterId());
        orderShippingNotes = (TextArea)getWidget("orderShippingNotes");
        
        fillItemsTable = (TableWidget)getWidget("fillItemsTable");
        fillItemsTable.addTableWidgetListener(this);
        fillItemsTable.model.addTableModelListener(this);
        orderItemsTree = (TreeWidget)getWidget("orderItemsTree");
        orderItemsTree.addTreeWidgetListener(this);
        
        ButtonPanel bpanel = (ButtonPanel)getWidget("buttons");
        
        CommandChain chain = new CommandChain();
        chain.addCommand(this);
        chain.addCommand(keyList);
        chain.addCommand(bpanel);
        
        if (costCenterDropdown == null) {
            costCenterDropdown = (DataModel)initData.get("costCenter");
            shipFromDropdown = (DataModel)initData.get("shipFrom");
            statusDropdown = (DataModel)initData.get("status");
            orderPendingValue = (Integer)((NumberObject)initData.get("pendingValue")).getValue();
        }
        
        //
        // cost center dropdown
        //
        drop = (Dropdown)getWidget(OrderMeta.getCostCenterId());
        drop.setModel(costCenterDropdown);

        fillItemsTableScreenWidget = (ScreenTableWidget)widgets.get("fillItemsTable");
        fillItemsQueryTable = (QueryTable)fillItemsTableScreenWidget.getQueryWidget().getWidget();
        
        //
        // status dropdown
        //
        ((TableDropdown)fillItemsTable.columns.get(2).getColumnWidget()).setModel(statusDropdown);
        ((TableDropdown)fillItemsQueryTable.columns.get(2).getColumnWidget()).setModel(statusDropdown);
        ((DropDownField)forms.get("query").getField(OrderMeta.getStatusId())).setModel(statusDropdown);
        
        //
        // ship from dropdown
        //
        ((TableDropdown)fillItemsTable.columns.get(4).getColumnWidget()).setModel(shipFromDropdown);
        ((TableDropdown)fillItemsQueryTable.columns.get(4).getColumnWidget()).setModel(shipFromDropdown);
        
        checkedTreeData = (TreeDataModel)orderItemsTree.model.getData().clone();
        
        super.afterDraw(success);
        
        rpc.setFieldValue("fillItemsTable", fillItemsTable.model.getData());
        
    }
    
    public void fetch() {
        //we dont need to call the super because we arent using the datamodel for lookups
        //super.fetch();
        fillItemsTable.model.clear();
        
        DataModel model = keyList.getList();
        
        loadFillItemsTableFromModel(model);
               
        if(model.size() > 0)
            changeState(State.DISPLAY);
    }
    
    public void add() {
        //super.add();
        key = null;
        enable(true);
        changeState(State.ADD);
        window.setStatus(consts.get("enterInformationPressCommit"),"");
    }

    public void query() {
        super.query();
        
        fillItemsQueryTable.setCellValue(OrderMeta.getStatusId(), 2, new DataSet(new NumberObject(orderPendingValue)));
        fillItemsQueryTable.select(0, 1);
        
        orderItemsTree.model.clear();
        checkedOrderIds.clear();
        checkedTreeData.clear();
    }
    
    public void commit() {
        if (state == State.ADD){
            window.setStatus("", "");
            lastShippedFrom = new Integer(-1);
            lastShippedTo = new Integer(-1);
            onProcessingCommitClick();
        }else
            super.commit();
            
        checkedOrderIds.clear();
        checkedTreeData.clear();
    }
    
    public void abort() {
        if(state == State.ADD){
            window.setStatus("","spinnerIcon");
            clearErrors();
            resetRPC();
            load();
            enable(false);
            
//            fillItemsTable.unselect(-1);
            checkedOrderIds.clear();
            checkedTreeData.clear();
            orderItemsTree.model.clear();
            lastShippedFrom = new Integer(-1);
            lastShippedTo = new Integer(-1);
            
            // prepare the argument list for the getObject function
            Data[] args = new Data[] {keyList.getList()}; 
            
            screenService.getObject("commitQueryAndUnlock", args, new AsyncCallback<DataModel>(){
                public void onSuccess(DataModel model){                    
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
        }else
            super.abort();
    }
   
    //
    //start table manager methods
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
        if(widget == fillItemsTableScreenWidget.getWidget()){
            if(state == FormInt.State.ADD && col == 0){     
                //we need to set the value of the check
                currentTableRow = row;
                tableCheckedValue = (String)((TableWidget)fillItemsTableScreenWidget.getWidget()).model.getCell(row, 0);
                
                return true;
            }
        }
        
        /*else if(controller == orderItemsController){
            //if the fillItemsController row is checked then then col==1 returns true
            return true;
        }*/
        
       return false;
    }

    public boolean canSelect(TableWidget widget, DataSet set, int row) {
        if(state == FormInt.State.ADD || state == FormInt.State.DISPLAY)           
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
    
    private void loadFillItemsTableFromModel(DataModel model){
        for(int i=0; i<model.size(); i++){
            DataSet set = model.get(i);
            
            DataSet row = fillItemsTable.model.createRow();
            
            Integer orderId = (Integer)((NumberField)set.getKey()).getValue();
            
            if(checkedOrderIds.getByKey(new NumberObject(orderId)) != null)
                row.get(0).setValue(CheckBox.CHECKED);
            
            row.get(1).setValue((Integer)((NumberField)set.getKey()).getValue());
            row.get(2).setValue(((DropDownField)set.get(0)).getSelections());
            row.get(3).setValue(((DateField)set.get(1)).getValue());
            row.get(4).setValue(((DropDownField)set.get(2)).getSelections());
            
            if(set.get(3).getValue() != null){
                ((DropDownField)row.get(5)).setModel(((DropDownField)set.get(3)).getModel());
                row.get(5).setValue(((DropDownField)set.get(3)).getSelections());
            }
            row.get(6).setValue(((StringField)set.get(4)).getValue());
            row.get(7).setValue(((NumberField)set.get(5)).getValue());
            row.get(8).setValue(((NumberField)set.get(6)).getValue());
            
            row.get(9).setValue(set.get(7).getValue());
  
            //hidden columns
            DataMap map = new DataMap();
            map.put("requestedBy", (StringField)set.get(8));
            map.put("costCenter", (DropDownField)set.get(9));
            map.put("multUnit", (StringField)set.get(10));
            map.put("streetAddress", (StringField)set.get(11));
            map.put("city", (StringField)set.get(12));
            map.put("state", (StringField)set.get(13));
            map.put("zipCode", (StringField)set.get(14));
            row.setData(map);
            
            fillItemsTable.model.addRow(row);
        }
    }
    
    private void replaceRowDataInFillItemsTable(DataModel model, int rowIndex){
        DataSet set = model.get(0);

        DataSet row = fillItemsTable.model.getRow(rowIndex);
            
        Integer orderId = (Integer)((NumberField)set.getKey()).getValue();
            
        if(checkedOrderIds.getByKey(new NumberObject(orderId)) != null)
            row.get(0).setValue(CheckBox.CHECKED);
            
        row.get(1).setValue((Integer)((NumberField)set.getKey()).getValue());
        row.get(2).setValue(((DropDownField)set.get(0)).getSelections());
        row.get(3).setValue(((DateField)set.get(1)).getValue());
        row.get(4).setValue(((DropDownField)set.get(2)).getSelections());
        ((DropDownField)row.get(5)).setModel(((DropDownField)set.get(3)).getModel());
        row.get(5).setValue(((DropDownField)set.get(3)).getSelections());
        row.get(6).setValue(((StringField)set.get(4)).getValue());
        row.get(7).setValue(((NumberField)set.get(5)).getValue());
        row.get(8).setValue(((NumberField)set.get(6)).getValue());
        row.get(9).setValue(set.get(7).getValue());
  
        //hidden columns
        DataMap map = new DataMap();
        map.put("requestedBy", (StringField)set.get(8));
        map.put("costCenter", (DropDownField)set.get(9));
        map.put("multUnit", (StringField)set.get(10));
        map.put("streetAddress", (StringField)set.get(11));
        map.put("city", (StringField)set.get(12));
        map.put("state", (StringField)set.get(13));
        map.put("zipCode", (StringField)set.get(14));
        row.setData(map);
        
        fillItemsTable.model.refresh();
    }
    
    private void loadOrderItemsTableFromModel(DataModel model){
        orderItemsTree.model.clear();
        for(int i=0; i < model.size(); i++){
            DataSet set = model.get(i);
            TreeDataItem row = orderItemsTree.model.createTreeItem("orderItem", (NumberObject)set.getKey());
            
            row.get(0).setValue(set.get(0).getValue());
            row.get(1).setValue(set.get(1).getValue());
            row.get(2).setValue(set.get(2).getValue());
            
            if(set.get(3).getValue() != null){
                ((DropDownField)row.get(3)).setModel(((DropDownField)set.get(3)).getModel());
                ((DropDownField)row.get(3)).setValue(((DropDownField)set.get(3)).getSelections());
            }
            
            row.get(4).setValue(set.get(4).getValue());
            row.get(5).setValue(set.get(5).getValue());
            
            DataMap rowHiddenMap = new DataMap();
            rowHiddenMap.put("referenceTableId", set.get(6));
            rowHiddenMap.put("referenceId", set.get(7));
            rowHiddenMap.put("invItemId", set.getKey());
            row.setData(rowHiddenMap);
            
            orderItemsTree.model.addRow(row);
        }
        orderItemsTree.model.refresh();
    }
    
    private void onProcessingCommitClick() {
        //get the first row in the table that is selected
        TableModel model = (TableModel)fillItemsTable.model;
        int i=0;
        while(i<model.numRows() && checkedOrderIds.getByKey(new NumberObject((Integer)((NumberField)((DataSet)model.getRow(i)).get(1)).getValue())) == null)
            i++;
        
        DataSet row = model.getRow(i);
        
        if(!((CheckField)row.get(9)).isChecked()){
            PopupPanel shippingPopupPanel = new PopupPanel(false, true);
            ScreenWindow pickerWindow = new ScreenWindow(shippingPopupPanel,
                                                         "Shipping",
                                                         "shippingScreen",
                                                         "Loading...");
            
            if(checkedOrderIds.size() > 0 && row != null){
                DataMap map = (DataMap)row.getData();
                ShippingScreen shippingScreen = new ShippingScreen();
                
                ShippingDataService data = new ShippingDataService();
                data.setShipFromId((Integer)((DropDownField)row.get(4)).getSelectedKey());
                data.setShipToId((Integer)((DropDownField)row.get(5)).getSelectedKey());
                data.setShipToText((String)((DropDownField)row.get(5)).getTextValue());
                data.setMultUnitText((String)((DataObject)map.get("multUnit")).getValue());
                data.setStreetAddressText((String)((DataObject)map.get("streetAddress")).getValue());
                data.setCityText((String)((DataObject)map.get("city")).getValue());
                data.setStateText((String)((DataObject)map.get("state")).getValue());
                data.setZipCodeText((String)((DataObject)map.get("zipCode")).getValue());
                data.setItemsShippedModel(createDataModelFromTree((TreeDataModel)checkedTreeData.clone()));
                data.setCheckedOrderIds((DataModel)checkedOrderIds.clone());
            
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
        }else{
            Window.alert("we need to handle these internal orders");
        }
    }
    
    private void fillOrderCheck(final int rowIndex, final boolean checked){
        final DataSet row = fillItemsTable.model.getRow(rowIndex);
        final DataMap map;
        
        if(row.getData() != null)
            map = (DataMap)row.getData();
        else
            map = new DataMap();
        
        DataModel orderItemsModel = (DataModel)map.get("orderItems");
            
           if(orderItemsModel != null){
               if(checked){     //add the order id to the selected list
                   DataSet set = new DataSet();
                   set.setKey(new NumberObject((Integer)((NumberField)row.get(1)).getValue()));
                   checkedOrderIds.add(set);
                   lastShippedFrom = (Integer)((DropDownField)row.get(4)).getSelectedKey();
                   lastShippedTo = (Integer)((DropDownField)row.get(5)).getSelectedKey();
                   
               }else{            //remove the order id from the selected list
                   checkedOrderIds.remove(checkedOrderIds.getByKey(new NumberObject((Integer)((NumberField)row.get(1)).getValue())));
                   if(checkedOrderIds.size() == 0){
                       lastShippedFrom = new Integer(-1);
                       lastShippedTo = new Integer(-1);
                   }
                       
               }
               
               if(checked){     //we need to add the order items from the selected row to the tree
                   for(int i=0; i<orderItemsModel.size(); i++){
                       DataSet set = (DataSet)orderItemsModel.get(i).clone();
                       int j=0;
                       
                       while(j<checkedTreeData.size() && !set.getKey().equals(checkedTreeData.get(j).getKey()))
                           j++;
                       
                       if(j == checkedTreeData.size()){   //we dont have a node for this item yet, we can add a node with no children
                           /*TreeDataItem newTreeRow = orderItemsTree.model.createTreeItem("orderItem", (NumberObject)set.getKey());                           
                           newTreeRow.get(0).setValue(set.get(0).getValue());
                           newTreeRow.get(1).setValue(set.get(1).getValue());
                           newTreeRow.get(2).setValue(set.get(2).getValue());
                           
                           if(set.get(3).getValue() != null){
                               ((DropDownField)newTreeRow.get(3)).setModel(((DropDownField)set.get(3)).getModel());
                               ((DropDownField)newTreeRow.get(3)).setValue(((DropDownField)set.get(3)).getSelections());
                           }

                           newTreeRow.get(4).setValue(set.get(4).getValue());
                           newTreeRow.get(5).setValue(set.get(5).getValue());
                           
                           DataMap rowHiddenMap = new DataMap();
                           rowHiddenMap.put("referenceTableId", set.get(6));
                           rowHiddenMap.put("referenceId", set.get(7));
                           rowHiddenMap.put("tableRowId", new NumberObject(currentTableRow));
                           rowHiddenMap.put("invItemId", set.getKey());
                           newTreeRow.setData(rowHiddenMap);
                           
                           checkedTreeData.add(newTreeRow);
                           */
                           TreeDataItem parentItemRow = orderItemsTree.model.createTreeItem("top", (NumberObject)set.getKey());
                           TreeDataItem locRow = orderItemsTree.model.createTreeItem("orderItem", (NumberObject)set.getKey()); 
                           parentItemRow.addItem(locRow);
                           parentItemRow.get(0).setValue(set.get(0).getValue());
                           parentItemRow.get(1).setValue(set.get(1).getValue());
                           parentItemRow.get(2).setValue(set.get(2).getValue());
                           
                           locRow.get(0).setValue(set.get(0).getValue());
                           locRow.get(1).setValue(set.get(1).getValue());
                           locRow.get(2).setValue(set.get(2).getValue());
                           
                           /*if(set.get(3).getValue() != null){
                               ((DropDownField)newTreeRow.get(3)).setModel(((DropDownField)set.get(3)).getModel());
                               ((DropDownField)newTreeRow.get(3)).setValue(((DropDownField)set.get(3)).getSelections());
                           }

                           newTreeRow.get(4).setValue(set.get(4).getValue());
                           newTreeRow.get(5).setValue(set.get(5).getValue());
                           */
                           DataMap rowHiddenMap = new DataMap();
                           rowHiddenMap.put("referenceTableId", set.get(6));
                           rowHiddenMap.put("referenceId", set.get(7));
                           rowHiddenMap.put("tableRowId", new NumberObject(currentTableRow));
                           rowHiddenMap.put("invItemId", set.getKey());
                           locRow.setData(rowHiddenMap);
                           
                           checkedTreeData.add(parentItemRow);
                       }/*else{                            //we already have a node for this item.  If it has no children we need to create a new parent.
                          TreeDataItem itemSet = checkedTreeData.get(j);
                          if(itemSet.getItems().size() == 0){
                              TreeDataItem parentTreeItem = checkedTreeData.createTreeItem("top", (NumberObject)set.getKey());
                              parentTreeItem.get(1).setValue(set.get(1).getValue());
                              parentTreeItem.get(2).setValue(set.get(2).getValue());
                              
                              TreeDataItem firstChildTreeItem = (TreeDataItem)itemSet.clone();
                              parentTreeItem.get(0).setValue((Integer)set.get(0).getValue()+(Integer)firstChildTreeItem.get(0).getValue());
                              
                              TreeDataItem secChildTreeItem = checkedTreeData.createTreeItem("orderItem", (NumberObject)set.getKey());
                              secChildTreeItem.get(0).setValue(set.get(0).getValue());
                              secChildTreeItem.get(1).setValue(set.get(1).getValue());
                              secChildTreeItem.get(2).setValue(set.get(2).getValue());
                              
                              if(set.get(3).getValue() != null){
                                  ((DropDownField)secChildTreeItem.get(3)).setModel(((DropDownField)set.get(3)).getModel());
                                  ((DropDownField)secChildTreeItem.get(3)).setValue(((DropDownField)set.get(3)).getSelections());
                              }
                              
                              secChildTreeItem.get(4).setValue(set.get(4).getValue());
                              secChildTreeItem.get(5).setValue(set.get(5).getValue());
                              
                              DataMap rowHiddenMap = new DataMap();
                              rowHiddenMap.put("referenceTableId", set.get(6));
                              rowHiddenMap.put("referenceId", set.get(7));
                              rowHiddenMap.put("tableRowId", new NumberObject(currentTableRow));
                              rowHiddenMap.put("invItemId", set.getKey());
                              secChildTreeItem.setData(rowHiddenMap);
                             
                              parentTreeItem.addItem(firstChildTreeItem);
                              parentTreeItem.addItem(secChildTreeItem);
                              
                              checkedTreeData.remove(j);
                              checkedTreeData.add(j, parentTreeItem);
                          }else{    //we dont need to create a parent...just add another child node
                              TreeDataItem newChild = checkedTreeData.createTreeItem("orderItem", (NumberObject)set.getKey());
                              newChild.get(0).setValue(set.get(0).getValue());
                              newChild.get(1).setValue(set.get(1).getValue());
                              newChild.get(2).setValue(set.get(2).getValue());
                              
                              if(set.get(3).getValue() != null){
                                  ((DropDownField)newChild.get(3)).setModel(((DropDownField)set.get(3)).getModel());
                                  ((DropDownField)newChild.get(3)).setValue(((DropDownField)set.get(3)).getSelections());
                              }
                              
                              newChild.get(4).setValue(set.get(4).getValue());
                              newChild.get(5).setValue(set.get(5).getValue());
                              
                              DataMap rowHiddenMap = new DataMap();
                              rowHiddenMap.put("referenceTableId", set.get(6));
                              rowHiddenMap.put("referenceId", set.get(7));
                              rowHiddenMap.put("tableRowId", new NumberObject(currentTableRow));
                              rowHiddenMap.put("invItemId", set.getKey());
                              newChild.setData(rowHiddenMap);
                              
                              ((TreeDataItem)checkedTreeData.get(j)).addItem(newChild);
                              
                              //update the qty on the parent node
                              itemSet.get(0).setValue((Integer)itemSet.get(0).getValue()+(Integer)set.get(0).getValue());
                          }
                       }*/
                           
                   }  
               }else{   //we need to rebuild the order items model from scratch using the checkedOrderItems list
                   checkedTreeData.clear();
                   TableModel model = (TableModel)fillItemsTable.model;
                   for(int k=0; k<model.numRows(); k++){
                       if(checkedOrderIds.getByKey(new NumberObject((Integer)model.getCell(k, 1))) != null){
                           DataSet itemsRow = model.getRow(k);
                           final DataMap rebuildMap = (DataMap)itemsRow.getData();
                          
                           DataModel rebuildModel = (DataModel)rebuildMap.get("orderItems");
                           
                           //rebuild the tree model
                           for(int l=0;l<rebuildModel.size(); l++){
                               DataSet set = (DataSet)rebuildModel.get(l).clone();
                               int j=0;
                               while(j<checkedTreeData.size() && !set.getKey().equals(checkedTreeData.get(j).getKey()))
                                   j++;
                               
                               if(j == checkedTreeData.size()){   //we dont have a node for this item yet, we can add a node with no children
                                   TreeDataItem newTreeRow = orderItemsTree.model.createTreeItem("orderItem", (NumberObject)set.getKey());                           
                                   newTreeRow.get(0).setValue(set.get(0).getValue());
                                   newTreeRow.get(1).setValue(set.get(1).getValue());
                                   newTreeRow.get(2).setValue(set.get(2).getValue());
                                   
                                   if(set.get(3).getValue() != null){
                                       ((DropDownField)newTreeRow.get(3)).setModel(((DropDownField)set.get(3)).getModel());
                                       ((DropDownField)newTreeRow.get(3)).setValue(((DropDownField)set.get(3)).getSelections());
                                   }
                                   
                                   newTreeRow.get(4).setValue(set.get(4).getValue());
                                   newTreeRow.get(5).setValue(set.get(5).getValue());
                                   
                                   checkedTreeData.add(newTreeRow);
                               }else{                            //we already have a node for this item.  If it has no children we need to create a new parent.
                                  TreeDataItem itemSet = checkedTreeData.get(j);
                                  if(itemSet.getItems().size() == 0){
                                      TreeDataItem parentTreeItem = checkedTreeData.createTreeItem("top", (NumberObject)set.getKey());
                                      parentTreeItem.get(1).setValue(set.get(1).getValue());
                                      parentTreeItem.get(2).setValue(set.get(2).getValue());
                                      
                                      TreeDataItem firstChildTreeItem = (TreeDataItem)itemSet.clone();
                                      
                                      TreeDataItem secChildTreeItem = checkedTreeData.createTreeItem("orderItem", (NumberObject)set.getKey());
                                      secChildTreeItem.get(0).setValue(set.get(0).getValue());
                                      secChildTreeItem.get(1).setValue(set.get(1).getValue());
                                      secChildTreeItem.get(2).setValue(set.get(2).getValue());
                                      
                                      if(set.get(3).getValue() != null){
                                          ((DropDownField)secChildTreeItem.get(3)).setModel(((DropDownField)set.get(3)).getModel());
                                          ((DropDownField)secChildTreeItem.get(3)).setValue(((DropDownField)set.get(3)).getSelections());
                                      }
                                      
                                      secChildTreeItem.get(4).setValue(set.get(4).getValue());
                                      secChildTreeItem.get(5).setValue(set.get(5).getValue());
                                     
                                      parentTreeItem.addItem(firstChildTreeItem);
                                      parentTreeItem.addItem(secChildTreeItem);
                                      
                                      checkedTreeData.remove(j);
                                      checkedTreeData.add(j, parentTreeItem);
                                  }else{    //we dont need to create a parent...just add another child node
                                      TreeDataItem newChild = checkedTreeData.createTreeItem("orderItem", (NumberObject)set.getKey());
                                      ((TreeDataItem)checkedTreeData.get(j)).addItem(newChild);
                                  }
                               }
                           }
                           
                       }
                   }
                   
               }                
                
               if(!checked && checkedTreeData.size() == 0)
                   loadOrderItemsTableFromModel(orderItemsModel);
               else{
                   orderItemsTree.model.setModel((TreeDataModel)checkedTreeData.clone());
                   orderItemsTree.model.refresh();
               }

           }else{
                window.setStatus("","spinnerIcon");
                
                NumberObject orderIdObj = new NumberObject((Integer)((NumberField)row.get(1)).getValue());
                
                // prepare the argument list for the getObject function
                Data[] args = new Data[] {orderIdObj}; 
                
                screenService.getObject("getOrderItemsOrderNotes", args, new AsyncCallback<DataModel>(){
                    public void onSuccess(DataModel orderItemsModel){
                        if(orderItemsModel != null){
                            DataMap map = (DataMap)row.getData();
                            
                            if(map == null)
                                map = new DataMap();

                            map.put("orderItems", orderItemsModel);
                            row.setData(map);
                            
                            fillOrderCheck(rowIndex, checked);
                        }
                        
                        window.setStatus("","");
                    }
                    
                    public void onFailure(Throwable caught){
                        Window.alert(caught.getMessage());
                    }
                });
            }
        }

    //
    //start table model listener methods
    //
    public void cellUpdated(SourcesTableModelEvents sender, int row, int cell) {}

    public void dataChanged(SourcesTableModelEvents sender) {}

    public void rowAdded(SourcesTableModelEvents sender, int rows) {}

    public void rowDeleted(SourcesTableModelEvents sender, int row) {}

    public void rowSelected(SourcesTableModelEvents sender, int row) {
        if(sender == fillItemsTable.model){
            final DataSet tableRow;
            
            if(row >=0 && fillItemsTable.model.numRows() > row)
                tableRow = fillItemsTable.model.getRow(row);
            else 
                tableRow = null;
            
            if(tableRow != null){
                if(CheckBox.CHECKED.equals(((CheckField)tableRow.get(0)).getValue())){
                    //if the row is already checked we need to show the check items model
                    orderItemsTree.model.load((TreeDataModel)checkedTreeData.clone());
                }else{
                    final DataMap map;
                    
                    if(tableRow.getData() != null)
                        map = (DataMap)tableRow.getData();
                    else
                        map = new DataMap();
                    
                    DataModel modelField = (DataModel)map.get("orderItems");
                        if(modelField == null){
                        //we need to look up the order items if they arent there yet
                        //window.setStatus("","spinnerIcon");
                        
                        NumberObject orderIdObj = new NumberObject((Integer)((NumberField)tableRow.get(1)).getValue());
                        
                        // prepare the argument list for the getObject function
                        Data[] args = new Data[] {orderIdObj}; 
                        
                        screenService.getObject("getOrderItemsOrderNotes", args, new AsyncCallback<DataModel>(){
                            public void onSuccess(DataModel model){
                                if(model != null){
                                    map.put("orderItems", model);
                                    
                                    loadOrderItemsTableFromModel(model);
                                    
                                    StringObject orderNotes = (StringObject)model.get(0).getData();
                                    if(orderNotes != null){
                                        map.put("orderNotes", orderNotes);
                                        orderShippingNotes.setText((String)orderNotes.getValue());
                                    }else{
                                        map.put("orderNotes", new StringObject());
                                        orderShippingNotes.setText("");
                                    }
                                }
                                
                          //      window.setStatus("","");
                            }
                            
                            public void onFailure(Throwable caught){
                                Window.alert(caught.getMessage());
                            }
                        });
                        }else
                            loadOrderItemsTableFromModel(modelField);
                        
                        fillShipToAddressInfo(map);
                    }
              
                 }else{
                     orgAptSuiteText.setText("");
                     orgAddressText.setText("");
                     orgCityText.setText("");
                     orgStateText.setText("");
                     orgZipCodeText.setText("");
                     requestedByText.setText("");
                     costCenterDrop.setSelections(new ArrayList<DataSet>());
                 }
            }
    }

    public void rowUnselected(SourcesTableModelEvents sender, int row) {}

    public void rowUpdated(SourcesTableModelEvents sender, int row) {}

    public void unload(SourcesTableModelEvents sender) {}
    //
    //end table model listener methods
    //

    private void fillShipToAddressInfo(DataMap map){
        if(map.get("orderNotes") != null)
            orderShippingNotes.setText((String)((StringObject)map.get("orderNotes")).getValue());
        else
            orderShippingNotes.setText("");
        
        if(map.get("streetAddress") != null && map.get("city") != null){
            orgAptSuiteText.setText((String)((StringField)map.get("multUnit")).getValue());
            orgAddressText.setText((String)((StringField)map.get("streetAddress")).getValue());
            orgCityText.setText((String)((StringField)map.get("city")).getValue());
            orgStateText.setText((String)((StringField)map.get("state")).getValue());
            orgZipCodeText.setText((String)((StringField)map.get("zipCode")).getValue());
        }else{
            orgAptSuiteText.setText("");
            orgAddressText.setText("");
            orgCityText.setText("");
            orgStateText.setText("");
            orgZipCodeText.setText("");   
        }
        
        if(map.get("requestedBy") != null)
            requestedByText.setText((String)((StringField)map.get("requestedBy")).getValue());
        else
            requestedByText.setText("");
        
        if(map.get("costCenter") != null)
            costCenterDrop.setSelections(((DropDownField)map.get("costCenter")).getSelections());
        else
            costCenterDrop.setSelections(new ArrayList());
    }
    
    //
    //start table widget listener methods
    //
    public void finishedEditing(SourcesTableWidgetEvents sender, final int row, int col) {
        if(col == 0){ 
            final String checkedValue = (String)fillItemsTable.model.getCell(row, 0);
            if(!checkedValue.equals(tableCheckedValue)){
                /* NEW */
                if(checkedValue.equals(CheckBox.CHECKED)){
                    //we need to fetch the row, make sure it isnt locked, and reload the data
                    NumberObject orderIdObj = new NumberObject((Integer)fillItemsTable.model.getCell(row, 1));
                    
                    // prepare the argument list for the getObject function
                    Data[] args = new Data[] {orderIdObj}; 
                    
                    screenService.getObject("fetchOrderItemAndLock", args, new AsyncCallback<DataModel>(){
                        public void onSuccess(DataModel model){
                            if(model != null){
                                replaceRowDataInFillItemsTable(model, row);
                                ((DataMap)fillItemsTable.model.getRow(row).getData()).put("orderItems", model.get(0).getData());
                                
                                StringObject orderNotes = (StringObject)((DataModel)model.get(0).getData()).get(0).getData();
                                if(orderNotes != null)
                                    ((DataMap)fillItemsTable.model.getRow(row).getData()).put("orderNotes", orderNotes);
                                else
                                    ((DataMap)fillItemsTable.model.getRow(row).getData()).put("orderNotes", new StringObject());
                                
                                loadOrderItemsTableFromModel((DataModel)model.get(0).getData());
                                fillShipToAddressInfo((DataMap)fillItemsTable.model.getRow(row).getData());
                                
                                //we need to check and see if we need to throw the ship from/ship to error
                                if(checkedOrderIds.size() > 0 && ((lastShippedFrom == null && ((DropDownField)fillItemsTable.model.getObject(row, 4)).getSelectedKey() != null) || 
                                                (lastShippedTo == null && ((DropDownField)fillItemsTable.model.getObject(row, 5)).getSelectedKey() != null) ||
                                                (lastShippedFrom != null && !lastShippedFrom.equals(((DropDownField)fillItemsTable.model.getObject(row, 4)).getSelectedKey())) || 
                                                (lastShippedTo != null && !lastShippedTo.equals(((DropDownField)fillItemsTable.model.getObject(row, 5)).getSelectedKey())))) {
                                                    
                                    //uncheck the row and throw a form error
                                    window.setStatus(consts.get("shipFromshipToInvalidException"),"ErrorPanel");
                                    fillItemsTable.model.setCell(row, 0, "N");
                                    //TODO we need to unlock the record because we hit thisn error
                                }else{
                                    window.setStatus("","");
                                    fillOrderCheck(row, true);
                                }
                                
                            }
                        }
                        
                        public void onFailure(Throwable caught){
                            //we need to uncheck the checkbox
                            fillItemsTable.model.setCell(row, 0, "N");
                            
                            Window.alert(caught.getMessage());
                        }
                    });
                }else{
                    //we need to fetch the row, unlock the record
                    NumberObject orderIdObj = new NumberObject((Integer)fillItemsTable.model.getCell(row, 1));
                    
                    // prepare the argument list for the getObject function
                    Data[] args = new Data[] {orderIdObj}; 
                    
                    if(((DataMap)fillItemsTable.model.getRow(row).getData()).get("changed") != null){
                        if(Window.confirm(consts.get("fillOrderItemsChangedConfirm")))
                            screenService.getObject("fetchOrderItemAndUnlock", args, new AsyncCallback<DataModel>(){
                                public void onSuccess(DataModel model){
                                    fillOrderCheck(row, "Y".equals(checkedValue));
                                    
                                }
                                
                                public void onFailure(Throwable caught){
                                    //we need to uncheck the checkbox
                                    fillItemsTable.model.setCell(row, 0, "N");
                                    
                                    
                                    Window.alert(caught.getMessage());
                                }
                            });
                        else{
                            //set the check box back to checked
                            fillItemsTable.model.setCell(row, 0, "Y");
                        }
                    }else
                        screenService.getObject("fetchOrderItemAndUnlock", args, new AsyncCallback<DataModel>(){
                            public void onSuccess(DataModel model){
                                fillOrderCheck(row, "Y".equals(checkedValue));
                                
                            }
                            
                            public void onFailure(Throwable caught){
                                //we need to uncheck the checkbox
                                fillItemsTable.model.setCell(row, 0, "N");
                                
                                Window.alert(caught.getMessage());
                            }
                        });
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
    //start tree manager methods
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

    public boolean canDrop(TreeWidget widget, Widget dragWidget, TreeDataItem dropTarget, int targetRow) {
        return false;
    }

    public boolean canEdit(TreeWidget widget, TreeDataItem set, int row, int col) {
        //make sure the row is checked, we arent a parent leaf, and its col 0 or 2
        if("orderItem".equals(set.leafType) && currentTableRow > -1 && "Y".equals(fillItemsTable.model.getCell(currentTableRow, 0)) && (col == 0 || col == 2))
            return true;
       
        return false;
    }

    public boolean canOpen(TreeWidget widget, TreeDataItem addRow, int row) {
        return true;
    }

    public boolean canSelect(TreeWidget widget, TreeDataItem set, int row) {
        if(state == FormInt.State.ADD)
            return true;
        return false;
    }

    public void drop(TreeWidget widget, Widget dragWidget, TreeDataItem dropTarget, int targetRow) {}

    public void drop(TreeWidget widget, Widget dragWidget) {}
    //
    //end tree manager methods
    //

    //
    //start tree widget listener methodsl
    //
    public void finishedEditing(SourcesTreeWidgetEvents sender, int row, int col) {
        //we need to set the changed flag is the value is changed
        //we also need to save the new model if the value is changed
        if(lastTreeValue != null && !orderItemsTree.model.getCell(row, col).equals(lastTreeValue)){
            if(col == 0){
                //we need to add up all the child nodes and get a new parent quantity, if necessary
                TreeDataItem item = orderItemsTree.model.getRow(row);
                Integer newTotalQuantity = new Integer(0);
                if(item.parent != null){
                    for(int i=0; i<item.parent.getItems().size(); i++){
                        newTotalQuantity += (Integer)item.parent.getItems().get(i).get(0).getValue();
                    }
                    
                    item.parent.get(0).setValue(newTotalQuantity);
                    orderItemsTree.model.refresh();
                }
                
            }else if(col == 2){
                TreeDataItem item = orderItemsTree.model.getRow(row);
                ArrayList selections = (ArrayList)((DropDownField)item.get(2)).getSelections();
               
                DataSet set = null;
                if(selections.size() > 0)
                    set = (DataSet)selections.get(0);
                
               if(set != null && set.size() > 1){
                    //set the new quantity on hand
                   orderItemsTree.model.setCell(row, 4, (Integer)set.get(1).getValue());
                   //orderItemsTree.model.refresh();
               }
            }
            
            checkedTreeData = orderItemsTree.model.unload();
            
            //need to add a changed flag to the fill order table row
            DataMap map = (DataMap)fillItemsTable.model.getRow(
                                       (Integer)((NumberObject)((DataMap)orderItemsTree.model.getRow(row).getData()).get("tableRowId")).getValue()).getData();
            map.put("changed", new CheckField("Y"));
        }
        
        lastTreeValue = null;
        
    }

    public void startedEditing(SourcesTreeWidgetEvents sender, int row, int col) {
        if("orderItem".equals(((TreeWidget)sender).model.getRow(row).leafType) && currentTableRow > -1 && 
                        "Y".equals(fillItemsTable.model.getCell(currentTableRow, 0)) && (col == 0 || col == 2)){
            currentTreeRow = row;
            //if(col == 3)
              //  lastTreeValue = (Integer)((DropDownField)orderItemsTree.model.getObject(row, col)).getSelectedKey();
            //else
                lastTreeValue = orderItemsTree.model.getCell(row, col);
        }
    }

    public void stopEditing(SourcesTreeWidgetEvents sender, int row, int col) {}
    //
    //end tree widget listener methods
    //

    //
    //auto complete method
    //
    public void callForMatches(final AutoComplete widget, DataModel model, String text) {
        StringObject catObj = new StringObject(widget.cat);
        StringObject matchObj = new StringObject(text);
        DataMap paramsObj = new DataMap();

        paramsObj.put("id", (NumberObject)((DataMap)orderItemsTree.model.getRow(currentTreeRow).getData()).get("invItemId"));
        
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
    
    private DataModel createDataModelFromTree(TreeDataModel treeModel){
        DataModel model = new DataModel();
        for(int i=0; i<treeModel.size(); i++){
            TreeDataItem row = treeModel.get(i);
            
            if(row.getItems().size() > 0){
                for(int j=0; j<row.getItems().size(); j++){
                    DataSet tableRow = new DataSet();
                    TreeDataItem childRow = row.getItems().get(j);
                    if(!((Integer)childRow.get(0).getValue()).equals(new Integer(0))){
                        tableRow.add(childRow.get(0));
                        tableRow.add(childRow.get(1));
                        
                        tableRow.setData((DataMap)childRow.getData().clone());
                        model.add(tableRow);
                    }
                }
            }else{
                if(!((Integer)row.get(0).getValue()).equals(new Integer(0))){
                    DataSet tableRow = new DataSet();
                    tableRow.add(row.get(0));
                    tableRow.add(row.get(1));
                    
                    tableRow.setData((DataMap)row.getData().clone());
                    
                    model.add(tableRow);
                }
            }
        }
        return model;
    }

    public boolean canDrop(TreeWidget widget, Widget dragWidget, Widget dropWidget) {
        // TODO Auto-generated method stub
        return false;
    }
}
