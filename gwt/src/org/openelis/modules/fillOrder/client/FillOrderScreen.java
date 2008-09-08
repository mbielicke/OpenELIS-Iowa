/**
* The contents of this file are subject to the Mozilla Public License
* Version 1.1 (the "License"); you may not use this file except in
* compliance with the License. You may obtain a copy of the License at
* http://www.mozilla.org/MPL/
* 
* Software distributed under the License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
* License for the specific language governing rights and limitations under
* the License.
* 
* The Original Code is OpenELIS code.
* 
* Copyright (C) The University of Iowa.  All Rights Reserved.
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
import org.openelis.gwt.common.data.ModelObject;
import org.openelis.gwt.common.data.NumberField;
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
import org.openelis.gwt.widget.FormInt.State;
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
    
    private FillOrderMetaMap OrderMeta = new FillOrderMetaMap();
    
    private EditTable fillItemsController;
    
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
    }
    
    public void afterDraw(boolean success) {
        TableWidget d;
        QueryTable q;
        ScreenTableWidget sw;
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
        addCommandListener(fillItemsController);
        
        ButtonPanel bpanel = (ButtonPanel)getWidget("buttons");
        
        CommandChain chain = new CommandChain();
        chain.addCommand(this);
        chain.addCommand(keyList);
        chain.addCommand(bpanel);
        
        if (costCenterDropdown == null) {
            costCenterDropdown = (DataModel)initData.get("costCenter");
            shipFromDropdown = (DataModel)initData.get("shipFrom");
            statusDropdown = (DataModel)initData.get("status");
        }
        
        //
        // cost center dropdown
        //
        drop = (AutoCompleteDropdown)getWidget(OrderMeta.getCostCenterId());
        drop.setModel(costCenterDropdown);

        sw = (ScreenTableWidget)widgets.get("fillItemsTable");
        d = (TableWidget)sw.getWidget();
        q = (QueryTable)sw.getQueryWidget().getWidget();
        
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
        
    }
    
    public void fetch() {
        //we dont need to call the super because we arent using the datamodel for lookups
        //super.fetch();
        
        DataModel model = keyList.getList();
        
        fillItemsController.model.reset();
        fillItemsController.reset();
        
        loadFillItemsTableFromModel(model);
               
        if(model.size() > 0){
            changeState(State.DISPLAY);
            fillItemsController.enabled(true);
        }
    }
    
    public void update() {
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
    }
    
    public void commit() {
        if (state == State.UPDATE)
            onProcessingCommitClick();
        else
            super.commit();
    }
    
    public void abort() {
        if(state == State.UPDATE){
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
        if(state == FormInt.State.DISPLAY)           
            return true;
        return false;
    }

    public boolean canEdit(int row, int col, TableController controller) {
        if(state == FormInt.State.DISPLAY && col == 0)           
            return true;
        
       return false;
    }

    public boolean canDelete(int row, TableController controller) {
        return true;
    }

    public boolean action(int row, int col, TableController controller) {  
        TableRow tableRow=null;
        
        if(row >=0 && controller.model.numRows() > row)
            tableRow = controller.model.getRow(row);
        
        if(tableRow != null){
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
            
        }else{
            orgAptSuiteText.setText("");
            orgAddressText.setText("");
            orgCityText.setText("");
            orgStateText.setText("");
            orgZipCodeText.setText("");
            requestedByText.setText("");
            costCenterDrop.reset();
        }
        
        return false;
    }

    public boolean canInsert(int row, TableController controller) {
        return false;     
    }

    public void finishedEditing(int row, int col, TableController controller) {}

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
    
    /*private boolean tableRowEmpty(TableRow row){
        boolean empty = true;
        
        //we want to ignore the first column since it is a check field
        for(int i=1; i<row.numColumns(); i++){
            if(row.getColumn(i).getValue() != null && !"".equals(row.getColumn(i).getValue())){
                empty = false;
                break;
            }
        }
        
        return empty;
    }*/
    
    private void onRemoveRowButtonClick() {
        int selectedRow = fillItemsController.selected;
        if (selectedRow > -1 && fillItemsController.model.numRows() > 0) {
            TableRow row = fillItemsController.model.getRow(selectedRow);
            fillItemsController.model.hideRow(row);

            // reset the model
            fillItemsController.reset();
            // need to set the deleted flag to "Y" also
            StringField deleteFlag = new StringField();
            deleteFlag.setValue("Y");

            row.addHidden("deleteFlag", deleteFlag);
        }
    }

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
    
    private void onProcessingCommitClick() {
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
                                                   (String)row.getHidden("zipCode").getValue()));

        shippingPopupPanel.add(pickerWindow);
        int left = this.getAbsoluteLeft();
        int top = this.getAbsoluteTop();
        shippingPopupPanel.setPopupPosition(left, top);
        shippingPopupPanel.show();
    }
}
