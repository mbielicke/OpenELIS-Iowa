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

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

import org.openelis.gwt.common.DefaultRPC;
import org.openelis.gwt.common.Form;
import org.openelis.gwt.common.FormErrorException;
import org.openelis.gwt.common.data.CheckField;
import org.openelis.gwt.common.data.Data;
import org.openelis.gwt.common.data.DataMap;
import org.openelis.gwt.common.data.DataModel;
import org.openelis.gwt.common.data.DataObject;
import org.openelis.gwt.common.data.DataSet;
import org.openelis.gwt.common.data.DateField;
import org.openelis.gwt.common.data.DropDownField;
import org.openelis.gwt.common.data.Field;
import org.openelis.gwt.common.data.FieldType;
import org.openelis.gwt.common.data.IntegerObject;
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
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.AutoComplete;
import org.openelis.gwt.widget.AutoCompleteCallInt;
import org.openelis.gwt.widget.ButtonPanel;
import org.openelis.gwt.widget.CheckBox;
import org.openelis.gwt.widget.Dropdown;
import org.openelis.gwt.widget.FormInt;
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

import java.util.ArrayList;

public class FillOrderScreen extends OpenELISScreenForm<DefaultRPC,Form,Integer> implements ClickListener, AutoCompleteCallInt, TableManager, TableWidgetListener, TableModelListener, TreeManager, TreeWidgetListener, CommandListener, TreeModelListener{
    
    private static boolean loaded = false;
    private static DataModel costCenterDropdown, shipFromDropdown, statusDropdown;
    private ScreenTableWidget fillItemsTableScreenWidget;
    private TreeWidget orderItemsTree;
    private TableWidget fillItemsTable;
    private QueryTable fillItemsQueryTable;    
    
    private TextBox requestedByText, orgAptSuiteText, orgAddressText, orgCityText, orgStateText, orgZipCodeText;
    private TextArea orderShippingNotes;
    private Dropdown costCenterDrop;
    private AppButton              removeRowButton, addLocationButton;
    
    private TreeDataModel checkedTreeData;
    private DataModel checkedOrderIds = new DataModel();
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
        super("org.openelis.modules.fillOrder.server.FillOrderService", !loaded,new DefaultRPC());
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
        if (sender == removeRowButton)
            onRemoveRowButtonClick();
        else if (sender == addLocationButton)
            onAddLocationButtonClick();
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
        orderItemsTree.model.addTreeModelListener(this);
        
        removeRowButton = (AppButton)getWidget("removeRowButton");
        addLocationButton = (AppButton)getWidget("addLocationButton");
        
        ButtonPanel bpanel = (ButtonPanel)getWidget("buttons");
        
        CommandChain chain = new CommandChain();
        chain.addCommand(this);
        chain.addCommand(keyList);
        chain.addCommand(bpanel);
        
        if (costCenterDropdown == null) {
            costCenterDropdown = (DataModel)initData.get("costCenter");
            shipFromDropdown = (DataModel)initData.get("shipFrom");
            statusDropdown = (DataModel)initData.get("status");
            orderPendingValue = ((NumberObject)initData.get("pendingValue")).getIntegerValue();
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
        
        rpc.form.setFieldValue("fillItemsTable", fillItemsTable.model.getData());
        rpc.form.setFieldValue("orderItemsTree", orderItemsTree.model.getData());
        
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
        
        removeRowButton.changeState(ButtonState.DISABLED);
        addLocationButton.changeState(ButtonState.DISABLED);
    }

    public void query() {
        super.query();
        
        removeRowButton.changeState(ButtonState.DISABLED);
        addLocationButton.changeState(ButtonState.DISABLED);
        
        fillItemsQueryTable.setCellValue(OrderMeta.getStatusId(), 2, new DataSet(new NumberObject(orderPendingValue)));
        fillItemsQueryTable.select(0, 1);
        
        orderItemsTree.model.clear();
        checkedOrderIds.clear();
        checkedTreeData.clear();
    }
    
    public void commit() {
        if (state == State.ADD){
            orderItemsTree.model.load((TreeDataModel)checkedTreeData.clone());
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
                window.setStatus(consts.get("correctErrors"),"ErrorPanel");
            }
        }else
            super.commit();

    }
    
    public void abort() {
        if(state == State.ADD){
            window.setStatus("","spinnerIcon");
            clearErrors();
            resetForm();
            load();
            enable(false);
            
//            fillItemsTable.unselect(-1);
            checkedOrderIds.clear();
            checkedTreeData.clear();
            orderItemsTree.model.clear();
            lastShippedFrom = new Integer(-1);
            lastShippedTo = new Integer(-1);
            
            // prepare the argument list for the getObject function
            FieldType[] args = new FieldType[] {keyList.getList()}; 
            
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
    
    private void loadFillItemsTableFromModel(DataModel<Integer> model){
        for(int i=0; i<model.size(); i++){
            DataSet<Integer> set = model.get(i);
            
            DataSet<Object> row = (DataSet<Object>)fillItemsTable.model.createRow();
            
            Integer orderId = set.getKey();
            
            if(checkedOrderIds.getByKey(new NumberObject(orderId)) != null)
                row.get(0).setValue(CheckBox.CHECKED);
            
            row.get(1).setValue(set.getKey());
            row.get(2).setValue(((DropDownField)set.get(0)).getValue());
            row.get(3).setValue(((DateField)set.get(1)).getValue());
            row.get(4).setValue(((DropDownField)set.get(2)).getValue());
            
            if(set.get(3).getValue() != null){
                ((DropDownField)row.get(5)).setModel(((DropDownField)set.get(3)).getModel());
                row.get(5).setValue(((DropDownField)set.get(3)).getValue());
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
    
    private void replaceRowDataInFillItemsTable(DataModel<Integer> model, int rowIndex){
        DataSet<Integer> set = model.get(0);

        DataSet<Object> row = fillItemsTable.model.getRow(rowIndex);
            
        Integer orderId = set.getKey();
            
        if(checkedOrderIds.getByKey(new NumberObject(orderId)) != null)
            row.get(0).setValue(CheckBox.CHECKED);
            
        row.get(1).setValue(set.getKey());
        row.get(2).setValue(((DropDownField)set.get(0)).getValue());
        row.get(3).setValue(((DateField)set.get(1)).getValue());
        row.get(4).setValue(((DropDownField)set.get(2)).getValue());
        ((DropDownField)row.get(5)).setModel(((DropDownField)set.get(3)).getModel());
        row.get(5).setValue(((DropDownField)set.get(3)).getValue());
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
    
    private void loadOrderItemsTableFromModel(DataModel<Integer> model){
        orderItemsTree.model.clear();
        for(int i=0; i < model.size(); i++){
            DataSet<Integer> set = model.get(i);
            TreeDataItem row = orderItemsTree.model.createTreeItem("orderItem");
            
            row.get(0).setValue(set.get(0).getValue());
            row.get(1).setValue(set.get(1).getValue());
            
            if(set.get(2).getValue() != null){
                ((DropDownField)row.get(2)).setModel(((DropDownField)set.get(2)).getModel());
                ((DropDownField)row.get(2)).setValue(((DropDownField)set.get(2)).getValue());
            }
            
            if(set.get(3).getValue() != null){
                ((DropDownField)row.get(3)).setModel(((DropDownField)set.get(3)).getModel());
                ((DropDownField)row.get(3)).setValue(((DropDownField)set.get(3)).getValue());
            }
            
            row.get(4).setValue(set.get(4).getValue());
            row.get(5).setValue(set.get(5).getValue());
            
            DataMap rowHiddenMap = new DataMap();
            rowHiddenMap.put("referenceTableId", set.get(6));
            rowHiddenMap.put("referenceId", new NumberObject(set.getKey()));
            row.setData(rowHiddenMap);
            
            orderItemsTree.model.addRow(row);
        }
        orderItemsTree.model.refresh();
    }
    
    private void onProcessingCommitClick() {
        //get the first row in the table that is selected
        TableModel model = (TableModel)fillItemsTable.model;
        int i=0;
        while(i<model.numRows() && checkedOrderIds.getByKey(new NumberObject((Integer)model.getRow(i).get(1).getValue())) == null)
            i++;
        
        DataSet<Object> row = model.getRow(i);
        
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
            
            super.commit();
        }
        
        //clear out all the temp values
        checkedOrderIds.clear();
        checkedTreeData.clear();
        orderItemsTree.model.clear();
        lastShippedFrom = new Integer(-1);
        lastShippedTo = new Integer(-1);
        
    }
    
    private void fillOrderCheck(final int rowIndex, final boolean checked){
        final DataSet<Integer> row = fillItemsTable.model.getRow(rowIndex);
        final DataMap map;
        
        if(row.getData() != null)
            map = (DataMap)row.getData();
        else
            map = new DataMap();
        
        DataModel<Integer> orderItemsModel = (DataModel<Integer>)map.get("orderItems");
            
           if(orderItemsModel != null){
               if(checked){     //add the order id to the selected list
                   DataSet<Integer> set = new DataSet<Integer>();
                   set.setKey(((NumberObject)row.get(1)).getIntegerValue());
                   checkedOrderIds.add(set);
                   lastShippedFrom = (Integer)((DropDownField)row.get(4)).getSelectedKey();
                   lastShippedTo = (Integer)((DropDownField)row.get(5)).getSelectedKey();
                   
               }else{            //remove the order id from the selected list
                   checkedOrderIds.remove(checkedOrderIds.getByKey(new NumberObject(((NumberField)row.get(1)).getValue())));
                   if(checkedOrderIds.size() == 0){
                       lastShippedFrom = new Integer(-1);
                       lastShippedTo = new Integer(-1);
                   }
                       
               }
               
               if(checked){     //we need to add the order items from the selected row to the tree
                   for(int i=0; i<orderItemsModel.size(); i++){
                       DataSet<Integer> set = (DataSet<Integer>)orderItemsModel.get(i).clone();
                      

                          TreeDataItem parentItemRow = orderItemsTree.model.createTreeItem("top");
                           TreeDataItem locRow = orderItemsTree.model.createTreeItem("orderItem"); 
                           parentItemRow.addItem(locRow);
                           parentItemRow.get(0).setValue(set.get(0).getValue());
                           parentItemRow.get(1).setValue(set.get(1).getValue());
                           parentItemRow.setData(new IntegerObject((Integer)set.get(0).getValue()));                           
                           
                           if(set.get(2).getValue() != null){
                               ((DropDownField)parentItemRow.get(2)).setModel(((DropDownField)set.get(2)).getModel());
                               ((DropDownField)parentItemRow.get(2)).setValue(((DropDownField)set.get(2)).getValue());
                           }
                           
                           locRow.get(0).setValue(set.get(0).getValue());
                           locRow.get(1).setValue(set.get(1).getValue());
                           locRow.get(2).setValue(set.get(2).getValue());
                           
                           if(set.get(2).getValue() != null){
                               ((DropDownField)locRow.get(2)).setModel(((DropDownField)set.get(2)).getModel());
                               ((DropDownField)locRow.get(2)).setValue(((DropDownField)set.get(2)).getValue());
                           }

                           FillOrderOrderItemsKey hiddenData = new FillOrderOrderItemsKey();
                           hiddenData.referenceTableId = (Integer)set.get(6).getValue();
                           hiddenData.referenceId = set.getKey();
                           hiddenData.tableRowId = currentTableRow;
                           hiddenData.locId = (Integer)((DropDownField)set.get(3)).getSelectedKey();
                           locRow.setData(hiddenData);
                           
                           checkedTreeData.add(parentItemRow);
                   }  
               }else{   //we need to rebuild the order items model from scratch using the checkedOrderItems list
                  int k=0;
                   while(k<checkedTreeData.size()){
                       
                       if(((Integer)checkedTreeData.get(k).get(1).getValue()).equals(row.get(1).getValue())){ //we need to remove this row and children
                           //FIXME this is removed for some reason..fix  checkedTreeData.delete(checkedTreeData.get(k));
                           k--;
                       }
                       k++;
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
                
                NumberObject orderIdObj = new NumberObject(((NumberField)row.get(1)).getValue());
                
                // prepare the argument list for the getObject function
                FieldType[] args = new FieldType[] {orderIdObj}; 
                
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
                       removeRowButton.changeState(ButtonState.DISABLED);
            addLocationButton.changeState(ButtonState.DISABLED);
            
            final DataSet<Integer> tableRow;
            
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
                        
                        NumberObject orderIdObj = new NumberObject(((NumberObject)tableRow.get(1)).getIntegerValue());
                        
                        // prepare the argument list for the getObject function
                        FieldType[] args = new FieldType[] {orderIdObj}; 
                        
                        screenService.getObject("getOrderItemsOrderNotes", args, new AsyncCallback<DataModel>(){
                            public void onSuccess(DataModel model){
                                if(model != null){
                                    map.put("orderItems", model);
                                    
                                    loadOrderItemsTableFromModel(model);
                                    
                                    StringObject orderNotes = (StringObject)((DataSet<Data>)model.get(0)).getData();
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
                     costCenterDrop.setSelections(new ArrayList<DataSet<Object>>());
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
            costCenterDrop.setSelections(((ArrayList<DataSet<Object>>)((DropDownField)map.get("costCenter")).getValue()));
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

                if(checkedValue.equals(CheckBox.CHECKED)){
                    //we need to fetch the row, make sure it isnt locked, and reload the data
                    NumberObject orderIdObj = new NumberObject((Integer)fillItemsTable.model.getCell(row, 1));
                    
                    // prepare the argument list for the getObject function
                    FieldType[] args = new FieldType[] {orderIdObj}; 
                    
                    screenService.getObject("fetchOrderItemAndLock", args, new AsyncCallback<DataModel<Integer>>(){
                        public void onSuccess(DataModel<Integer> model){
                            if(model != null){
                                replaceRowDataInFillItemsTable(model, row);
                                ((DataMap)fillItemsTable.model.getRow(row).getData()).put("orderItems", model.get(0).getData());
                                
                                StringObject orderNotes = (StringObject)((DataSet)((DataModel<DataSet>)model.get(0).getData()).get(0)).getData();
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
                    FieldType[] args = new FieldType[] {orderIdObj}; 
                    
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
        if("orderItem".equals(set.leafType) && currentTableRow > -1 && "Y".equals(fillItemsTable.model.getCell(currentTableRow, 0)) && (col == 0 || col == 3))
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
        if(orderItemsTree.model.getCell(row, col) != null && !orderItemsTree.model.getCell(row, col).equals(lastTreeValue)){
            if(col == 0){
                //we need to add up all the child nodes and get a new parent quantity, if necessary
                TreeDataItem item = orderItemsTree.model.getRow(row);
                Integer newTotalQuantity = new Integer(0);
                if(item.parent != null){
                    for(int i=0; i<item.parent.getItems().size(); i++){
                        newTotalQuantity += (Integer)item.parent.getItems().get(i).get(0).getValue();
                    }
                    
                    ((DataMap)item.parent.getData()).put("totalQty", new NumberObject(newTotalQuantity));
 //                   orderItemsTree.model.refresh();
               }

                checkedTreeData = (TreeDataModel)orderItemsTree.model.unload().clone();
                
            }else if(col == 2){
                TreeDataItem item = orderItemsTree.model.getRow(row);
                ArrayList selections = (ArrayList)((DropDownField)item.get(3)).getValue();
               
                DataSet<Field> set = null;
                if(selections.size() > 0)
                    set = (DataSet<Field>)selections.get(0);
                
               if(set != null && set.size() > 1){
                   //set the lot num
                   orderItemsTree.model.setCell(row, 4, (String)((DataObject)set.get(1)).getValue());
                   
                   //set the new quantity on hand
                   orderItemsTree.model.setCell(row, 5, (Integer)((DataObject)set.get(2)).getValue());
               }

               checkedTreeData = (TreeDataModel)orderItemsTree.model.unload().clone();
               
            }
            
            
            //need to add a changed flag to the fill order table row
            DataMap map = (DataMap)fillItemsTable.model.getRow(
                                       ((NumberObject)((DataMap)orderItemsTree.model.getRow(row).getData()).get("tableRowId")).getIntegerValue()).getData();
            map.put("changed", new CheckField("Y"));
        }
        
        lastTreeValue = null;
        
    }

    public void startedEditing(SourcesTreeWidgetEvents sender, int row, int col) {
        if("orderItem".equals(((TreeWidget)sender).model.getRow(row).leafType) && currentTableRow > -1 && 
                        "Y".equals(fillItemsTable.model.getCell(currentTableRow, 0)) && (col == 0 || col == 3)){
            currentTreeRow = row;

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

        paramsObj.put("id", new NumberObject((Integer)((DropDownField)orderItemsTree.model.getObject(currentTreeRow, 2)).getSelectedKey()));
        
        // prepare the argument list for the getObject function
        FieldType[] args = new FieldType[] {catObj, model, matchObj, paramsObj}; 
        
        
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
                    DataSet<Field> tableRow = new DataSet<Field>();
                    TreeDataItem childRow = row.getItems().get(j);
                    if(!((Integer)childRow.get(0).getValue()).equals(new Integer(0))){
                        tableRow.add(childRow.get(0));
                        tableRow.add(childRow.get(2));
                        
                        //DataMap rowDataMap = (DataMap)((DataMap)childRow.getData()).clone();
                        //rowDataMap.put("locId", new NumberObject((Integer)((DropDownField)childRow.get(3)).getSelectedKey()));
                        tableRow.setData(new IntegerObject((Integer)((DropDownField)childRow.get(3)).getSelectedKey()));
                        model.add(tableRow);
                    }
                }
            }else{
                if(!((Integer)row.get(0).getValue()).equals(new Integer(0))){
                    DataSet tableRow = new DataSet();
                    tableRow.add(row.get(0));
                    tableRow.add(row.get(2));
                    
                    //TODO made this change...not sure what the ramifications are yet
                    tableRow.setData(new IntegerObject((Integer)((DropDownField)row.get(3)).getSelectedKey()));
                    //tableRow.setData((FieldType)((IntegerObject)row.getData()).clone());
                    
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
    
    public void onRemoveRowButtonClick(){
        orderItemsTree.model.deleteRow(currentTreeRow);
        currentTreeRow=-1;
        checkedTreeData = (TreeDataModel)orderItemsTree.model.unload().clone();
    }
    
    public void onAddLocationButtonClick(){
        TreeDataItem selectedRow = orderItemsTree.model.getRow(currentTreeRow);
        TreeDataItem item = orderItemsTree.model.createTreeItem("orderItem");
        //item.get(0).setValue(selectedRow.get(0).getValue());
        item.get(1).setValue(selectedRow.get(1).getValue());
        ((DropDownField)item.get(2)).setModel(((DropDownField)selectedRow.get(2)).getModel());
        item.get(2).setValue(selectedRow.get(2).getValue());
        
        DataMap rowHiddenMap = (DataMap)((DataMap)selectedRow.getData()).clone();
        item.setData(rowHiddenMap);
        
        if(selectedRow.parent == null){
            selectedRow.addItem(item);
            if(!selectedRow.open)
                selectedRow.toggle();
        }else{
            selectedRow.parent.addItem(item);
            
        }
        
        //TODO eventually select the first column of the added row

        orderItemsTree.model.refresh();
    }

    //
    //start tree model listener methods
    //
    public void cellUpdated(SourcesTreeModelEvents sender, int row, int cell) {}

    public void dataChanged(SourcesTreeModelEvents sender) {}

    public void rowAdded(SourcesTreeModelEvents sender, int rows) {}

    public void rowClosed(SourcesTreeModelEvents sender, int row, TreeDataItem item) {}

    public void rowDeleted(SourcesTreeModelEvents sender, int row) {}

    public void rowOpened(SourcesTreeModelEvents sender, int row, TreeDataItem item) {}

    public void rowSelectd(SourcesTreeModelEvents sender, int row) {
        boolean parentNull = ((TreeDataItem)orderItemsTree.model.getRow(row)).parent == null;
        if(parentNull || (!parentNull && ((TreeDataItem)orderItemsTree.model.getRow(row)).childIndex == 0))
            removeRowButton.changeState(ButtonState.DISABLED);
        else
            removeRowButton.changeState(ButtonState.UNPRESSED);
        
        addLocationButton.changeState(ButtonState.UNPRESSED);
        currentTreeRow = row;
                
    }

    public void rowUnselected(SourcesTreeModelEvents sender, int row) {}

    public void rowUpdated(SourcesTreeModelEvents sender, int row) {}

    public void unload(SourcesTreeModelEvents sender) {}
    //
    //end tree model listener
    //
    
    protected boolean validate() {
        boolean valid = true;
        //we need to iterate through the tree and make sure there is enough qty set to fill the orders
        for(int i=0; i<checkedTreeData.size(); i++){
            TreeDataItem row = checkedTreeData.get(i);
            if(((DataMap)row.getData()).get("totalQty") != null){
                Integer totalQty = ((NumberObject)((DataMap)row.getData()).get("totalQty")).getIntegerValue();
                if(totalQty.compareTo((Integer)row.get(0).getValue()) < 0){
                    valid = false;
                    ((NumberField)row.get(0)).addError(consts.get("fillOrderQtyException"));
                }
            }
                
        }
        return valid;
    }
}
