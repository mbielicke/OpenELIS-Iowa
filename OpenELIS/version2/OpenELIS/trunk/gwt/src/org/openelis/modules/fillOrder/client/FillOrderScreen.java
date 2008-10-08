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
import java.util.List;

import org.openelis.gwt.common.data.CheckField;
import org.openelis.gwt.common.data.DataModel;
import org.openelis.gwt.common.data.DataObject;
import org.openelis.gwt.common.data.DataSet;
import org.openelis.gwt.common.data.DateField;
import org.openelis.gwt.common.data.DropDownField;
import org.openelis.gwt.common.data.KeyListManager;
import org.openelis.gwt.common.data.ModelField;
import org.openelis.gwt.common.data.ModelObject;
import org.openelis.gwt.common.data.NumberField;
import org.openelis.gwt.common.data.NumberObject;
import org.openelis.gwt.common.data.StringField;
import org.openelis.gwt.common.data.TableModel;
import org.openelis.gwt.common.data.TableRow;
import org.openelis.gwt.screen.CommandChain;
import org.openelis.gwt.screen.ScreenTableWidget;
import org.openelis.gwt.screen.ScreenWindow;
import org.openelis.gwt.widget.AutoCompleteDropdown;
import org.openelis.gwt.widget.ButtonPanel;
import org.openelis.gwt.widget.CheckBox;
import org.openelis.gwt.widget.FormInt;
import org.openelis.gwt.widget.table.EditTable;
import org.openelis.gwt.widget.table.QueryTable;
import org.openelis.gwt.widget.table.TableAutoDropdown;
import org.openelis.gwt.widget.table.TableController;
import org.openelis.gwt.widget.table.TableManager;
import org.openelis.gwt.widget.table.TableWidget;
import org.openelis.metamap.FillOrderMetaMap;
import org.openelis.modules.main.client.OpenELISScreenForm;
import org.openelis.modules.shipping.client.ShippingScreen;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class FillOrderScreen extends OpenELISScreenForm implements ClickListener, TableManager{
    
    private static boolean loaded = false;
    private static DataModel costCenterDropdown, shipFromDropdown, statusDropdown;
    private static Integer orderItemReferenceTableId;
    private static DropDownField pendingStatusField;
    private DataModel selectedOrderItems = new DataModel();
    private List checkedOrderIds = new ArrayList();
    ScreenTableWidget fillItemsTable;
    private boolean ranAction = false;
    private int lastIndex = -2;
    private boolean checked = false;
    private boolean clicked = false;
    private boolean ranFinishedEditing = true;
    
    private FillOrderMetaMap OrderMeta = new FillOrderMetaMap();
    
    private EditTable fillItemsController, orderItemsController;
    
    private TextBox requestedByText, orgAptSuiteText, orgAddressText, orgCityText, orgStateText, orgZipCodeText;
    private AutoCompleteDropdown costCenterDrop;
    
    private List fillItemsCheckedRowsList = new ArrayList();
    
    private KeyListManager keyList = new KeyListManager();

    public FillOrderScreen() {
        super("org.openelis.modules.fillOrder.server.FillOrderService", !loaded);
    }
    
    public void performCommand(Enum action, Object obj) {
        if(action == KeyListManager.Action.FETCH){
            fetch();
        }else{
            super.performCommand(action, obj);
        }
    }      
    
    public void onClick(Widget sender) {
        /*if(sender instanceof CheckBox){
            Window.alert("onClick() - ranFinsiedEditing("+ranFinishedEditing+")");
            if(!ranFinishedEditing){
                ranAction = true;
                fillOrderCheck(fillItemsController.selected, ((CheckBox)sender).getState().equals(CheckBox.CHECKED));
            }
            
            clicked = true;
            checked = CheckBox.CHECKED.equals(((CheckBox)sender).getState());
        }*/
    }
    
    public void afterDraw(boolean success) {
        TableWidget d;
        QueryTable q;
        AutoCompleteDropdown drop;
        
        loaded = true;
        
        requestedByText = (TextBox)getWidget(OrderMeta.getRequestedBy());
        orgAptSuiteText = (TextBox)getWidget(OrderMeta.ORDER_ORGANIZATION_META.ADDRESS.getMultipleUnit());
        orgAddressText = (TextBox)getWidget(OrderMeta.ORDER_ORGANIZATION_META.ADDRESS.getStreetAddress());
        orgCityText = (TextBox)getWidget(OrderMeta.ORDER_ORGANIZATION_META.ADDRESS.getCity());
        orgStateText = (TextBox)getWidget(OrderMeta.ORDER_ORGANIZATION_META.ADDRESS.getState());
        orgZipCodeText = (TextBox)getWidget(OrderMeta.ORDER_ORGANIZATION_META.ADDRESS.getZipCode());
        costCenterDrop = (AutoCompleteDropdown)getWidget(OrderMeta.getCostCenterId());
        
        fillItemsController = ((TableWidget)getWidget("fillItemsTable")).controller;
        orderItemsController = ((TableWidget)getWidget("orderItemsTable")).controller;
        //addCommandListener(fillItemsController);
        
        ButtonPanel bpanel = (ButtonPanel)getWidget("buttons");
        
        CommandChain chain = new CommandChain();
        chain.addCommand(this);
        chain.addCommand(keyList);
        chain.addCommand(bpanel);
        
        if (costCenterDropdown == null) {
            costCenterDropdown = (DataModel)initData.get("costCenter");
            shipFromDropdown = (DataModel)initData.get("shipFrom");
            statusDropdown = (DataModel)initData.get("status");
            orderItemReferenceTableId = (Integer)((NumberObject)initData.get("orderItemReferenceTableId")).getValue();
        }
        
        //
        // cost center dropdown
        //
        drop = (AutoCompleteDropdown)getWidget(OrderMeta.getCostCenterId());
        drop.setModel(costCenterDropdown);

        fillItemsTable = (ScreenTableWidget)widgets.get("fillItemsTable");
        d = (TableWidget)fillItemsTable.getWidget();
        q = (QueryTable)fillItemsTable.getQueryWidget().getWidget();
        
        //
        // status dropdown
        //
        ((TableAutoDropdown)d.controller.editors[2]).setModel(statusDropdown);
        ((TableAutoDropdown)q.editors[2]).setModel(statusDropdown);
        
        //
        // ship from dropdown
        //
        ((TableAutoDropdown)d.controller.editors[4]).setModel(shipFromDropdown);
        ((TableAutoDropdown)q.editors[4]).setModel(shipFromDropdown);
        
        super.afterDraw(success);
        
        /*if(pendingStatusField == null){
            Integer pendingValue = (Integer)((NumberObject)initData.get("pendingValue")).getValue();
            pendingStatusField = ((DropDownField)forms.get("query").getField(OrderMeta.getStatusId())).getInstance();   
            pendingStatusField.setValue(pendingValue);
        }*/
        
    }
    
    public void fetch() {
        //we dont need to call the super because we arent using the datamodel for lookups
        //super.fetch();
        
        DataModel model = keyList.getList();
        
        fillItemsController.model.reset();
        fillItemsController.reset();
        
        loadFillItemsTableFromModel(model);
               
        if(model.size() > 0)
            changeState(State.DISPLAY);
            //fillItemsController.enabled(true);
        //}
    }
    
    public void add() {
        //super.add();
        key = null;
        enable(true);
        changeState(State.ADD);
        window.setStatus(consts.get("enterInformationPressCommit"),"");
    }
    
/*    public void update() {
        
        }
        
        resetRPC();
        load();
        
        window.setStatus(consts.get("lockForUpdate"),"spinnerIcon");
        
        ModelObject modelObj = new ModelObject();
        modelObj.setValue(keyList.getList());
        
        // prepare the argument list for the getObject function
        DataObject[] args = new DataObject[] {modelObj}; 
        
        screenService.getObject("commitQueryAndLock", args, new AsyncCallback(){
            public void onSuccess(Object result){                    
                DataModel model = (DataModel)((ModelObject)result).getValue();
                
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
    }*/
    
    /*public void query() {
        super.query();
        ((TableAutoDropdown)((QueryTable)fillItemsTable.getQueryWidget().getWidget()).editors[2]).setField(pendingStatusField);
        ((TableAutoDropdown)((QueryTable)fillItemsTable.getQueryWidget().getWidget()).editors[2]).saveValue();
        ((TableAutoDropdown)((QueryTable)fillItemsTable.getQueryWidget().getWidget()).editors[2]).setDisplay();
        //((TableAutoDropdown)((QueryTable)fillItemsTable.getQueryWidget().getWidget()).editors[2]).setDisplay();
        //fillItemsController.load(0);
    }*/
    
    public void commit() {
        if (state == State.ADD){
            //we need to keep track of which rows are selected...
            fillItemsCheckedRowsList.clear();
            TableModel model = fillItemsController.model;
            for(int i=0; i<model.numRows(); i++){
                CheckField check = (CheckField)model.getFieldAt(i, 0);
                if(CheckBox.CHECKED.equals(check.getValue())){
                    NumberField orderId = (NumberField)model.getFieldAt(i, 1);
                    fillItemsCheckedRowsList.add((Integer)orderId.getValue());
                }
            }
            
            onProcessingCommitClick();
        }else if(state == State.QUERY)
            fillItemsCheckedRowsList.clear();

        super.commit();
    }
    
    public void abort() {
        if(state == State.ADD){
            window.setStatus("","spinnerIcon");
            clearErrors();
            resetRPC();
            load();
            enable(false);
            
            ModelObject modelObj = new ModelObject();
            modelObj.setValue(keyList.getList());
            
            // prepare the argument list for the getObject function
            DataObject[] args = new DataObject[] {modelObj}; 
            
            screenService.getObject("commitQueryAndUnlock", args, new AsyncCallback(){
                public void onSuccess(Object result){                    
                    DataModel model = (DataModel)((ModelObject)result).getValue();
                    
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
    public boolean canSelect(int row, TableController controller) {        
        if(state == FormInt.State.ADD || state == FormInt.State.DISPLAY)           
            return true;
        return false;
    }

    public boolean canEdit(int row, int col, TableController controller) {
        if(controller == fillItemsController){
            if(state == FormInt.State.ADD && col == 0)           
                return true;
        }else if(controller == orderItemsController){
            //if the fillItemsController row is checked then then col==1 returns true
            return true;
        }
        
       return false;
    }

    public boolean canDelete(int row, TableController controller) {
        return true;
    }

    public boolean action(final int row, int col, TableController controller) {  
        //Window.alert("action()");
        if(controller == fillItemsController){
            final TableRow tableRow;
            
            if(row >=0 && controller.model.numRows() > row)
                tableRow = controller.model.getRow(row);
            else 
                tableRow = null;
            
            if(tableRow != null){
             //   if(col != 0){
                    if(CheckBox.CHECKED.equals(((CheckField)tableRow.getColumn(0)).getValue())){
                        //if the row is already checked we need to show the check items model
                        loadOrderItemsTableFromModel(selectedOrderItems);
                    }else{
                        ModelField modelField = (ModelField)tableRow.getHidden("orderItems");
                        if(modelField == null){
                        //we need to look up the order items if they arent there yet
                        window.setStatus("","spinnerIcon");
                        
                        NumberObject orderIdObj = new NumberObject((Integer)((NumberField)tableRow.getColumn(1)).getValue());
                        
                        // prepare the argument list for the getObject function
                        DataObject[] args = new DataObject[] {orderIdObj}; 
                        
                        screenService.getObject("getOrderItems", args, new AsyncCallback(){
                            public void onSuccess(Object result){
                                if(result != null){
                                    ModelField modelField = new ModelField();
                                    modelField.setValue(((ModelObject)result).getValue());
                                    tableRow.addHidden("orderItems", modelField);
                                    
                                    loadOrderItemsTableFromModel((DataModel)modelField.getValue());
                                }
                                
                                window.setStatus("","");
                            }
                            
                            public void onFailure(Throwable caught){
                                Window.alert(caught.getMessage());
                            }
                        });
                        }else
                            loadOrderItemsTableFromModel((DataModel)modelField.getValue());
                        
                        
                        if(tableRow.getHidden("streetAddress") != null && tableRow.getHidden("city") != null){
                            orgAptSuiteText.setText((String)((StringField)tableRow.getHidden("multUnit")).getValue());
                            orgAddressText.setText((String)((StringField)tableRow.getHidden("streetAddress")).getValue());
                            orgCityText.setText((String)((StringField)tableRow.getHidden("city")).getValue());
                            orgStateText.setText((String)((StringField)tableRow.getHidden("state")).getValue());
                            orgZipCodeText.setText((String)((StringField)tableRow.getHidden("zipCode")).getValue());
                        }
                        
                        if(tableRow.getHidden("requestedBy") != null)
                            requestedByText.setText((String)((StringField)tableRow.getHidden("requestedBy")).getValue());
                        
                        if(tableRow.getHidden("costCenter") != null)
                            costCenterDrop.setSelected((ArrayList)((DropDownField)tableRow.getHidden("costCenter")).getSelections());
                    }
                
                /*}else{
                    if(tableRow.getHidden("orderItems") == null){
                        //if(!ranAction){
                        //    ranAction = true;
                        //    fillOrderCheck(row, ((CheckField)tableRow.getColumn(0)).getValue().equals(CheckBox.CHECKED));
                      //  }
                    //}else{
//                  we need to look up the order items if they arent there yet
                    window.setStatus("","spinnerIcon");
                    
                    NumberObject orderIdObj = new NumberObject((Integer)((NumberField)tableRow.getColumn(1)).getValue());
                    
                    // prepare the argument list for the getObject function
                    DataObject[] args = new DataObject[] {orderIdObj}; 
                    
                    screenService.getObject("getOrderItems", args, new AsyncCallback(){
                        public void onSuccess(Object result){
                            if(result != null){
                                ModelField modelField = new ModelField();
                                modelField.setValue(((ModelObject)result).getValue());
                                tableRow.addHidden("orderItems", modelField);
                                
                             //   if(!ranAction){
                             //       ranAction = true;
                             //       fillOrderCheck(row, ((CheckField)tableRow.getColumn(0)).getValue().equals(CheckBox.CHECKED));
                             //   }

                            }
                            
                            window.setStatus("","");
                        }
                        
                        public void onFailure(Throwable caught){
                            Window.alert(caught.getMessage());
                        }
                    });
                    }
               // }
                }*/
            }else{
                orgAptSuiteText.setText("");
                orgAddressText.setText("");
                orgCityText.setText("");
                orgStateText.setText("");
                orgZipCodeText.setText("");
                requestedByText.setText("");
                costCenterDrop.reset();
            }
        }
                
        return false;
    }

    public boolean canInsert(int row, TableController controller) {
        return false;     
    }

    public void finishedEditing(int row, int col, TableController controller) {
        /*Window.alert("finishedEditing()");
        lastIndex = -2;
        ranFinishedEditing = true;*/
        if(row > -1 && col == 0){
            fillOrderCheck(row, CheckBox.CHECKED.equals(((CheckField)((EditTable)controller).model.getFieldAt(row, col)).getValue()));
        }
    }

    public boolean doAutoAdd(TableRow row, TableController controller) {
        //return !tableRowEmpty(row);
        return false;
    }

    public void rowAdded(int row, TableController controller) {}

    public void getNextPage(TableController controller) {}

    public void getPage(int page) {}

    public void getPreviousPage(TableController controller) {}

    public void setModel(TableController controller, DataModel model) {}

    public void validateRow(int row, TableController controller) {}

    public void setMultiple(int row, int col, TableController controller) {}
    //
    //end table manager methods
    //
    
    private void loadFillItemsTableFromModel(DataModel model){
        for(int i=0; i<model.size(); i++){
            DataSet set = model.get(i);
            
            TableRow tableRow = new TableRow();     
            Integer orderId = (Integer)((NumberField)set.getKey()).getValue();
            
            if(fillItemsCheckedRowsList.contains(orderId))
                tableRow.addColumn(new CheckField(CheckBox.CHECKED));
            else
                tableRow.addColumn(new CheckField());
            
            tableRow.addColumn((NumberField)set.getKey());
            tableRow.addColumn((DropDownField)set.getObject(0));
            tableRow.addColumn((DateField)set.getObject(1));
            tableRow.addColumn((DropDownField)set.getObject(2));
            tableRow.addColumn((DropDownField)set.getObject(3));
            tableRow.addColumn((StringField)set.getObject(4));
            tableRow.addColumn((NumberField)set.getObject(5));
            tableRow.addColumn((NumberField)set.getObject(6));
  
            //hidden columns
            tableRow.addHidden("requestedBy", (StringField)set.getObject(7));
            tableRow.addHidden("costCenter", (DropDownField)set.getObject(8));
            tableRow.addHidden("multUnit", (StringField)set.getObject(9));
            tableRow.addHidden("streetAddress", (StringField)set.getObject(10));
            tableRow.addHidden("city", (StringField)set.getObject(11));
            tableRow.addHidden("state", (StringField)set.getObject(12));
            tableRow.addHidden("zipCode", (StringField)set.getObject(13));
            
            ((EditTable)fillItemsController).addRow(tableRow);
        }
    }
    
    private void loadOrderItemsTableFromModel(DataModel model){
        orderItemsController.model.reset();
        for(int i=0; i<model.size(); i++){
            DataSet set = model.get(i);
            
            TableRow tableRow = new TableRow();     
            tableRow.addColumn((StringField)set.getObject(0));
            tableRow.addColumn((NumberField)set.getObject(1));
            tableRow.addColumn((StringField)set.getObject(2));
            
            ((EditTable)orderItemsController).addRow(tableRow);
        }
    }
    
    private void onProcessingCommitClick() {
    //    fillOrderCheck(0, true);
        PopupPanel shippingPopupPanel = new PopupPanel(false, true);
        ScreenWindow pickerWindow = new ScreenWindow(shippingPopupPanel,
                                                     "Shipping",
                                                     "shippingScreen",
                                                     "Loading...");
        //get the first row in the table that is selected
        TableModel model = fillItemsController.model;
        int i=0;
        while(i<model.numRows() && !fillItemsCheckedRowsList.contains((Integer)((NumberField)model.getFieldAt(i, 1)).getValue()))
            i++;
        
        /*
         * tableRow.addHidden("multUnit", (StringField)set.getObject(9));
            tableRow.addHidden("streetAddress", (StringField)set.getObject(10));
            tableRow.addHidden("city", (StringField)set.getObject(11));
            tableRow.addHidden("state", (StringField)set.getObject(12));
            tableRow.addHidden("zipCode", (StringField)set.getObject(13));
         */
        TableRow row = model.getRow(i);
        
        pickerWindow.setContent(new ShippingScreen((Integer)((DropDownField)row.getColumn(4)).getValue(), (Integer)((DropDownField)row.getColumn(5)).getValue(), 
                                                   (String)((DropDownField)row.getColumn(5)).getTextValue(), (String)row.getHidden("multUnit").getValue(), 
                                                   (String)row.getHidden("streetAddress").getValue(), (String)row.getHidden("city").getValue(), (String)row.getHidden("state").getValue(), 
                                                   (String)row.getHidden("zipCode").getValue(), selectedOrderItems));

        shippingPopupPanel.add(pickerWindow);
        int left = this.getAbsoluteLeft();
        int top = this.getAbsoluteTop();
        shippingPopupPanel.setPopupPosition(left, top);
        shippingPopupPanel.show();
    }
    
    private void fillOrderCheck(int rowIndex, boolean checked){
      //  Window.alert("1");
      //  if(!ranAction && lastIndex != fillItemsController.selected)
      //      return;
     //   else{
            final TableRow row = fillItemsController.model.getRow(rowIndex);
            ModelField modelField = (ModelField)row.getHidden("orderItems");
            final DataModel orderItemsModel;
            
            if(modelField != null)
                orderItemsModel = (DataModel)modelField.getValue();
            else
                orderItemsModel = null;
            if(checked){     //add the order id to the selected list
                checkedOrderIds.add((Integer)((NumberField)row.getColumn(1)).getValue());
            }else{            //remove the order id from the selected list
                checkedOrderIds.remove((Integer)((NumberField)row.getColumn(1)).getValue());
    }
            
           if(orderItemsModel != null){
               if(checked){     //we need to add the order items from the selected row
                   for(int i=0; i<orderItemsModel.size(); i++){
                       DataSet set = orderItemsModel.get(i).getInstance();
                       int j=0;
                       if(selectedOrderItems.size() > 0)
                       while(j<selectedOrderItems.size() && !set.getKey().equals(selectedOrderItems.get(j).getKey()))
                           j++;
                       
                       if(j == selectedOrderItems.size()){   //we need to add a new item
                          selectedOrderItems.add(set);
                       }else{                            //we need to add this quantity to an exisiting row
                          DataSet itemSet = selectedOrderItems.get(j);
                          NumberObject itemQuantityObject = (NumberObject)itemSet.getObject(1);
                          itemQuantityObject.setValue((Integer)itemQuantityObject.getValue() + (Integer)((NumberObject)set.getObject(1)).getValue());
                       }
                           
                   }  
               }else{           //we need to rebuild the order items model from scratch using the checkedOrderItems list
                   selectedOrderItems.clear();
                   TableModel model = fillItemsController.model;
                   for(int k=0; k<model.numRows(); k++){
                       if(checkedOrderIds.contains((Integer)((NumberField)model.getFieldAt(k, 1)).getValue())){
                           TableRow itemsRow = model.getRow(k);
                           DataModel orderChildModel = (DataModel)((ModelField)itemsRow.getHidden("orderItems")).getValue();
                           
                           for(int l=0;l<orderChildModel.size(); l++)
                               selectedOrderItems.add(orderItemsModel.get(l).getInstance());
                           
                       }
                   }
                   
               }                
                
               if(!checked && selectedOrderItems.size() == 0)
                   loadOrderItemsTableFromModel(orderItemsModel);
               else
                   loadOrderItemsTableFromModel(selectedOrderItems);
               
                //orderItemsController.reset();
            }else{
                window.setStatus("","spinnerIcon");
                
                NumberObject orderIdObj = new NumberObject((Integer)((NumberField)row.getColumn(1)).getValue());
                
                // prepare the argument list for the getObject function
                DataObject[] args = new DataObject[] {orderIdObj}; 
                
                screenService.getObject("getOrderItems", args, new AsyncCallback(){
                    public void onSuccess(Object result){
                        if(result != null){
                            ModelField modelField = new ModelField();
                            DataModel orderItemsModel = (DataModel)((ModelObject)result).getValue();
                            modelField.setValue(((ModelObject)result).getValue());

                            row.addHidden("orderItems", modelField);
                            
                            for(int i=0; i<orderItemsModel.size(); i++){
                                selectedOrderItems.add(orderItemsModel.get(i).getInstance());
                            }
                            
                            loadOrderItemsTableFromModel(selectedOrderItems);
                            //orderItemsController.reset();
                        }
                        
                        window.setStatus("","");
                    }
                    
                    public void onFailure(Throwable caught){
                        Window.alert(caught.getMessage());
                    }
                });
            }
           ranAction = false;
        }
        
        
    //}
}
