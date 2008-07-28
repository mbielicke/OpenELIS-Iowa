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
package org.openelis.modules.order.client;

import java.util.ArrayList;
import java.util.HashMap;

import org.openelis.gwt.common.data.BooleanObject;
import org.openelis.gwt.common.data.DataModel;
import org.openelis.gwt.common.data.DataObject;
import org.openelis.gwt.common.data.DataSet;
import org.openelis.gwt.common.data.DropDownField;
import org.openelis.gwt.common.data.ModelField;
import org.openelis.gwt.common.data.ModelObject;
import org.openelis.gwt.common.data.NumberField;
import org.openelis.gwt.common.data.NumberObject;
import org.openelis.gwt.common.data.StringField;
import org.openelis.gwt.common.data.StringObject;
import org.openelis.gwt.common.data.TableField;
import org.openelis.gwt.common.data.TableModel;
import org.openelis.gwt.common.data.TableRow;
import org.openelis.gwt.screen.ScreenAutoDropdown;
import org.openelis.gwt.screen.ScreenTextArea;
import org.openelis.gwt.screen.ScreenTextBox;
import org.openelis.gwt.screen.ScreenWindow;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.AutoCompleteDropdown;
import org.openelis.gwt.widget.ButtonPanel;
import org.openelis.gwt.widget.FormInt;
import org.openelis.gwt.widget.table.EditTable;
import org.openelis.gwt.widget.table.TableController;
import org.openelis.gwt.widget.table.TableLabel;
import org.openelis.gwt.widget.table.TableManager;
import org.openelis.gwt.widget.table.TableWidget;
import org.openelis.metamap.OrderMetaMap;
import org.openelis.modules.main.client.OpenELISScreenForm;
import org.openelis.modules.standardnotepicker.client.StandardNotePickerScreen;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SourcesTabEvents;
import com.google.gwt.user.client.ui.TabListener;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class OrderScreen extends OpenELISScreenForm implements TableManager, ClickListener, TabListener, ChangeListener {
    
    private static boolean loaded = false;
    private boolean startedLoadingTable = false;
    
    private static DataModel statusDropdown, costCenterDropdown;
    
    private AutoCompleteDropdown orgDropdown, billToDropdown, reportToDropdown;
    
    private AppButton        removeItemButton, standardNoteCustomerButton, standardNoteShippingButton;
    
    private ScreenTextBox          orderNum, neededInDays, orderDate, requestedBy;
    private TextBox orgAptSuite, orgAddress, orgCity, orgState, orgZipCode,
                    reportToAptSuite, reportToAddress, reportToCity, reportToState, reportToZipCode,
                    billToAptSuite, billToAddress, billToCity, billToState, billToZipCode;
    
    private ScreenTextArea   shippingNoteText, customerNoteText;
    
    private ScreenAutoDropdown status;
    
    private EditTable itemsController, receiptsController;
    
    private String orderType;
    
    private boolean     loadItems           = true, 
                        loadCustomerNotes   = true, 
                        loadShippingNotes   = true,
                        loadReportToBillTo  = true,
                        loadReceipts        = true,
                        clearItems          = false, 
                        clearCustomerNotes  = false, 
                        clearShippingNotes  = false,
                        clearReportToBillTo = false,
                        clearReceipts       = false;
    
    private OrderMetaMap OrderMeta = new OrderMetaMap();
    
    public OrderScreen(DataObject[] args) {                
        super("org.openelis.modules.order.server.OrderService");
        
        orderType = (String)((StringObject)args[0]).getValue();
        
        HashMap hash = new HashMap();
        hash.put("type", args[0]);
        
        BooleanObject loadedObj = new BooleanObject((loaded ? "Y" : "N"));
        hash.put("loaded", loadedObj);
        
        getXMLData(hash);
    }

    public void onClick(Widget sender) {
        if (sender == standardNoteCustomerButton)
            onStandardNoteCustomerButtonClick();
        else if(sender == standardNoteShippingButton)
            onStandardNoteShippingButtonClick();
        else if(sender == removeItemButton)
            onRemoveItemButtonClick();
    }
    
    public void onChange(Widget sender) {
        super.onChange(sender);
        
        if(sender == orgDropdown){
            if(orgDropdown.getSelected().size() > 0){
                DataSet selectedRow = (DataSet)orgDropdown.getSelected().get(0);
                
                //load address
                orgAddress.setText((String)((StringObject)selectedRow.getObject(1)).getValue());
                //load city
                orgCity.setText((String)((StringObject)selectedRow.getObject(2)).getValue());
                //load state
                orgState.setText((String)((StringObject)selectedRow.getObject(3)).getValue());               
                //load apt/suite
                orgAptSuite.setText((String)((StringObject)selectedRow.getObject(4)).getValue());
                //load zipcode
                orgZipCode.setText((String)((StringObject)selectedRow.getObject(5)).getValue());
            }            
        }else if(sender == reportToDropdown){
            if(reportToDropdown.getSelected().size() > 0){
                DataSet selectedRow = (DataSet)reportToDropdown.getSelected().get(0);
                
                //load address
                reportToAddress.setText((String)((StringObject)selectedRow.getObject(1)).getValue());
                //load city
                reportToCity.setText((String)((StringObject)selectedRow.getObject(2)).getValue());
                //load state
                reportToState.setText((String)((StringObject)selectedRow.getObject(3)).getValue());               
                //load apt/suite
                reportToAptSuite.setText((String)((StringObject)selectedRow.getObject(4)).getValue());
                //load zipcode
                reportToZipCode.setText((String)((StringObject)selectedRow.getObject(5)).getValue());
            }
        }else if(sender == billToDropdown){
            if(billToDropdown.getSelected().size() > 0){
                DataSet selectedRow = (DataSet)billToDropdown.getSelected().get(0);
                
                //load address
                billToAddress.setText((String)((StringObject)selectedRow.getObject(1)).getValue());
                //load city
                billToCity.setText((String)((StringObject)selectedRow.getObject(2)).getValue());
                //load state
                billToState.setText((String)((StringObject)selectedRow.getObject(3)).getValue());               
                //load apt/suite
                billToAptSuite.setText((String)((StringObject)selectedRow.getObject(4)).getValue());
                //load zipcode
                billToZipCode.setText((String)((StringObject)selectedRow.getObject(5)).getValue());
            }
        }
    }
    
    public void afterDraw(boolean sucess) {
        AutoCompleteDropdown drop;
        
        loaded = true;
        
        orderNum = (ScreenTextBox)widgets.get(OrderMeta.getId());
        neededInDays = (ScreenTextBox)widgets.get(OrderMeta.getNeededInDays());
        status = (ScreenAutoDropdown)widgets.get(OrderMeta.getStatusId());
        orderDate = (ScreenTextBox)widgets.get(OrderMeta.getOrderedDate());
        requestedBy = (ScreenTextBox)widgets.get(OrderMeta.getRequestedBy());
        shippingNoteText = (ScreenTextArea)widgets.get(OrderMeta.ORDER_SHIPPING_NOTE_META.getText());
        customerNoteText = (ScreenTextArea)widgets.get(OrderMeta.ORDER_CUSTOMER_NOTE_META.getText());
        
        itemsController = ((TableWidget)getWidget("itemsTable")).controller;
        itemsController.setAutoAdd(false);
        
        if("external".equals(orderType)){
            receiptsController = ((TableWidget)getWidget("receiptsTable")).controller;
            receiptsController.setAutoAdd(false);
        }
        
        //buttons
        removeItemButton = (AppButton)getWidget("removeItemButton");
        standardNoteCustomerButton = (AppButton)getWidget("standardNoteCustomerButton");
        standardNoteShippingButton = (AppButton)getWidget("standardNoteShippingButton");
        
        //organization address fields
        orgAptSuite  = (TextBox)getWidget(OrderMeta.ORDER_ORGANIZATION_META.ADDRESS.getMultipleUnit());
        orgAddress = (TextBox)getWidget(OrderMeta.ORDER_ORGANIZATION_META.ADDRESS.getStreetAddress());
        orgCity = (TextBox)getWidget(OrderMeta.ORDER_ORGANIZATION_META.ADDRESS.getCity());
        orgState = (TextBox)getWidget(OrderMeta.ORDER_ORGANIZATION_META.ADDRESS.getState());
        orgZipCode = (TextBox)getWidget(OrderMeta.ORDER_ORGANIZATION_META.ADDRESS.getZipCode());
        
        //report to address fields
        reportToAptSuite = (TextBox)getWidget(OrderMeta.ORDER_REPORT_TO_META.ADDRESS.getMultipleUnit());
        reportToAddress = (TextBox)getWidget(OrderMeta.ORDER_REPORT_TO_META.ADDRESS.getStreetAddress());
        reportToCity = (TextBox)getWidget(OrderMeta.ORDER_REPORT_TO_META.ADDRESS.getCity());
        reportToState = (TextBox)getWidget(OrderMeta.ORDER_REPORT_TO_META.ADDRESS.getState());
        reportToZipCode = (TextBox)getWidget(OrderMeta.ORDER_REPORT_TO_META.ADDRESS.getZipCode());
        
        //bill to address fields
        billToAptSuite = (TextBox)getWidget(OrderMeta.ORDER_BILL_TO_META.ADDRESS.getMultipleUnit());
        billToAddress = (TextBox)getWidget(OrderMeta.ORDER_BILL_TO_META.ADDRESS.getStreetAddress());
        billToCity = (TextBox)getWidget(OrderMeta.ORDER_BILL_TO_META.ADDRESS.getCity());
        billToState = (TextBox)getWidget(OrderMeta.ORDER_BILL_TO_META.ADDRESS.getState());
        billToZipCode = (TextBox)getWidget(OrderMeta.ORDER_BILL_TO_META.ADDRESS.getZipCode());
        
        orgDropdown = (AutoCompleteDropdown)getWidget(OrderMeta.ORDER_ORGANIZATION_META.getName()); 
        billToDropdown = (AutoCompleteDropdown)getWidget(OrderMeta.ORDER_BILL_TO_META.getName());
        reportToDropdown = (AutoCompleteDropdown)getWidget(OrderMeta.ORDER_REPORT_TO_META.getName());
        
        if (statusDropdown == null) {
            statusDropdown = (DataModel)initData.get("status");
            costCenterDropdown = (DataModel)initData.get("costCenter");
        }

        drop = (AutoCompleteDropdown)getWidget(OrderMeta.getStatusId());
        drop.setModel(statusDropdown);
        
       // drop = (AutoCompleteDropdown)getWidget("store");
       // if(drop != null)
       //     drop.setModel(storeDropdown);
        
        drop = (AutoCompleteDropdown)getWidget(OrderMeta.getCostCenterId());
        drop.setModel(costCenterDropdown);
        
        setBpanel((ButtonPanel)getWidget("buttons"));
        super.afterDraw(sucess);
    }
    
    public void query() {
        super.query();
        
        rpc.setFieldValue("orderType", orderType);
        
        orderNum.setFocus(true);
        
        //disable the remove row button, standard note buttons, and note text fields
        //make sure the fields arent null as some order screens dont have them
        if(shippingNoteText != null)
            shippingNoteText.enable(false);
        
        if(customerNoteText != null)
            customerNoteText.enable(false);
        
        if(standardNoteCustomerButton != null)
            standardNoteCustomerButton.changeState(AppButton.ButtonState.DISABLED);
        
        if(standardNoteShippingButton != null)
            standardNoteShippingButton.changeState(AppButton.ButtonState.DISABLED);
        
        removeItemButton.changeState(AppButton.ButtonState.DISABLED);
    }
    
    public void add() {
        itemsController.setAutoAdd(true);
        super.add();
        
        rpc.setFieldValue("orderType", orderType);
        
        
        window.setStatus("","spinnerIcon");
          
        DataObject[] args = new DataObject[0]; 
          
        screenService.getObject("getAddAutoFillValues", args, new AsyncCallback(){
            public void onSuccess(Object result){    
              // get the datamodel, load the fields in the form
                DataModel model = (DataModel) ((ModelObject)result).getValue();
                DataSet set = model.get(0);

                //load the values
                status.load((DropDownField)set.getObject(0));
                orderDate.load((StringField)set.getObject(1));
                requestedBy.load((StringField)set.getObject(2));
                
                //set the values in the rpc
                rpc.setFieldValue(OrderMeta.getStatusId(), (Integer)((DropDownField)set.getObject(0)).getValue());
                rpc.setFieldValue(OrderMeta.getOrderedDate(), (String)((StringField)set.getObject(1)).getValue());
                rpc.setFieldValue(OrderMeta.getRequestedBy(), (String)((StringField)set.getObject(2)).getValue());
                
                window.setStatus("","");
            }
            
            public void onFailure(Throwable caught){
                Window.alert(caught.getMessage());
            }
        }); 
        
        orderNum.enable(false);
        status.enable(false);
        orderDate.enable(false);
        
        neededInDays.setFocus(true);
    }
    
    public void update() {
        itemsController.setAutoAdd(true);
        super.update();
    }
    public void afterUpdate(boolean success) {
        super.afterUpdate(success);
        if (success) {
            loadCustomerNotes = true;
            loadItems = true;
            loadReceipts = true;
            loadReportToBillTo = true;
            loadShippingNotes = true;
            
            loadTabs();
        }
        rpc.setFieldValue("orderType", orderType);

        orderNum.enable(false);
        orderDate.enable(false);
        
        neededInDays.setFocus(true);
    }
    
    public void abort() {
        itemsController.setAutoAdd(false);
        
        if(state == FormInt.State.ADD || state == FormInt.State.QUERY){
            loadItems = false;
            clearItems = true;
            
            loadReceipts = false;
            clearReceipts = true;
            
            loadCustomerNotes = false;
            clearCustomerNotes = true;
            
            loadReportToBillTo = false;
            clearReportToBillTo = true;
            
            loadShippingNotes = false;
            clearShippingNotes = true;
        }else{
            loadItems = true;
            loadReceipts = true;
            loadCustomerNotes = true;
            loadReportToBillTo = true;
            loadShippingNotes = true;
        }
        
        //the super needs to go before the load tabs method or the table wont load.
        super.abort();
        
        loadTabs();
    }
    
    public void afterCommitAdd(boolean success) {
        itemsController.setAutoAdd(false);
        super.afterCommitAdd(success);
        
        if(success){ 
            loadItems = true;
            clearItems = false;
            
            loadReceipts = true;
            clearReceipts = false;
            
            loadCustomerNotes = true;
            clearCustomerNotes = false;
            
            loadReportToBillTo = true;
            clearReportToBillTo = false;
            
            loadShippingNotes = true;
            clearShippingNotes = false;
            
            Integer orderId = (Integer)rpc.getFieldValue(OrderMeta.getId());
            NumberObject orderIdObj = new NumberObject(orderId);
            
            // done because key is set to null in AppScreenForm for the add operation 
            if(key ==null){  
                key = new DataSet();
                key.setKey(orderIdObj);
                
            }else{
                key.setKey(orderIdObj);
                
            }
            
            loadTabs();
        }
    }
    
    public void afterCommitUpdate(boolean success) {
        itemsController.setAutoAdd(false);
        
        //we need to do this reset to get rid of the last row
        itemsController.reset();
        
        super.afterCommitUpdate(success);
    }
    public void afterCommitQuery(boolean success) {
      super.afterCommitQuery(success);
      rpc.setFieldValue("orderType", orderType);
    }
    
    public void afterFetch(boolean success) {
        if (success) {
            loadItems = true; 
            loadReceipts = true;
            loadCustomerNotes = true; 
            loadShippingNotes = true;
            loadReportToBillTo = true;
            loadTabs();
        }
        super.afterFetch(success);
    }
    
    private void loadTabs() {
        int selectedTab = -1;
        TabPanel tabPanel = (TabPanel)getWidget("tabPanel");
        
        if(tabPanel != null)
            selectedTab = tabPanel.getTabBar().getSelectedTab();

        if ((selectedTab == 0 || selectedTab == -1) && loadItems) {
            // if there was data previously then clear the items table
            if (clearItems) {
                clearItems();
            }
            // load the items table
            fillItemsModel();
            // don't load it again unless the mode changes or a new fetch is
            // done
            loadItems = false;
            
        } else if ((selectedTab == 1 && "kits".equals(orderType) && loadCustomerNotes)) {
            // if there was data previously then clear the locations table otherwise
            // don't
            if (clearCustomerNotes) {
                clearCustomerNotes();
            }
            // load the customer notes
            fillCustomerNotes();
            // don't load it again unless the mode changes or a new fetch is
            // done
            loadCustomerNotes = false;

        } else if ((selectedTab == 1 && "external".equals(orderType) && loadReceipts)) {
            // if there was data previously then clear the locations table otherwise
            // don't
            if (clearReceipts) {
                clearReceipts();
            }
            // load the customer notes
            fillReceipts();
            // don't load it again unless the mode changes or a new fetch is
            // done
            loadReceipts = false;

        }else if (selectedTab == 2 && loadShippingNotes) {
            // if there was data previously then clear the comments panel otherwise
            // don't
            if (clearShippingNotes) {
                clearShippingNotes();
            }
            // load the comments model
            fillShippingNotes();
            // don't load it again unless the mode changes or a new fetch is
            // done
            loadShippingNotes = false;

        } else if (selectedTab == 3 && loadReportToBillTo) {
            if (clearReportToBillTo) {
                clearReportToBillTo();
            }
            // load the report to bill to tab
            fillReportToBillTo();
            // don't load it again unless the mode changes or a new fetch is
            // done
            loadReportToBillTo = false;

        }
    }
    
    public boolean onBeforeTabSelected(SourcesTabEvents sender, int index) {
        if (index == 0 && loadItems) {
            if (clearItems)
                clearItems();
            fillItemsModel();
            loadItems = false;
        } else if (index == 1 && "kits".equals(orderType) && loadCustomerNotes) {
            if (clearCustomerNotes) {
                clearCustomerNotes();
            }
            fillCustomerNotes();
            loadCustomerNotes = false;
        } else if ((index == 1 && "external".equals(orderType) && loadReceipts)) {
            if (clearReceipts)
                clearReceipts();
            fillReceipts();
            loadReceipts = false;
        } else if (index == 2 && loadShippingNotes) {
            if (clearShippingNotes)
                clearShippingNotes();
            fillShippingNotes();
            loadShippingNotes = false;
        } else if (index == 3 && loadReportToBillTo) {
            if (clearReportToBillTo) 
                clearReportToBillTo();
            fillReportToBillTo();
            loadReportToBillTo = false;
        }
        return true;
    }

    public void onTabSelected(SourcesTabEvents sender, int tabIndex) {
        // TODO Auto-generated method stub
        
    }

    //
    //start table manager methods
    //
    public boolean action(int row, int col, TableController controller) {
        return false;
    }

    public boolean canDelete(int row, TableController controller) {
        return true;
    }

    public boolean canEdit(int row, int col, TableController controller) {
       return true;
    }

    public boolean canInsert(int row, TableController controller) {
        return false;
    }

    public boolean canSelect(int row, TableController controller) {
        if(state == FormInt.State.ADD || state == FormInt.State.UPDATE)           
            return true;
        return false;
    }

    public boolean doAutoAdd(int row, int col, TableController controller) {
        if(row > -1 && row < controller.model.numRows() && !tableRowEmpty(controller.model.getRow(row)))
            return true;
        else if(row > -1 && row == controller.model.numRows() && !tableRowEmpty(((EditTable)controller).autoAddRow))
            return true;
      
        return false;
    }

    public void finishedEditing(int row, int col, TableController controller) {
       //Window.alert("("+col+") && ("+row+") && ("+controller.model.numRows()+") && ("+!startedLoadingTable+") total:("+(col == 1 && row > -1 && row < controller.model.numRows() && !startedLoadingTable)+")");
        if(col == 1 && row > -1 && row < controller.model.numRows() && !startedLoadingTable){
            startedLoadingTable = true;
            
            TableRow tableRow = ((EditTable)controller).model.getRow(row);
            DropDownField invItemField = (DropDownField)tableRow.getColumn(1);
            ArrayList selections = invItemField.getSelections();
  
            if(selections.size() > 0){
                DataSet selectedRow = (DataSet)selections.get(0);
  
                if(selectedRow.size() > 1){
                    StringField storeLabel = new StringField();
                    StringField locationLabel = new StringField();
                    NumberField locationId = new NumberField(NumberObject.Type.INTEGER);
                    storeLabel.setValue((String)((StringObject)selectedRow.getObject(1)).getValue());

                    tableRow.setColumn(2, storeLabel);
                    
                    ((TableLabel)((EditTable)controller).view.table.getWidget(row, 2)).setField(storeLabel);
                    ((TableLabel)((EditTable)controller).view.table.getWidget(row, 2)).setDisplay();
                    
                    if(tableRow.numColumns() == 4){
                        locationLabel.setValue((String)((StringObject)selectedRow.getObject(2)).getValue());
                        tableRow.setColumn(3, locationLabel);
                        ((TableLabel)((EditTable)controller).view.table.getWidget(row, 3)).setField(locationLabel);
                        ((TableLabel)((EditTable)controller).view.table.getWidget(row, 3)).setDisplay();
                        
                        locationId.setValue((Integer)((NumberObject)selectedRow.getObject(6)).getValue());
                        tableRow.addHidden("locationId", locationId);
                    }

                    startedLoadingTable = false;
                }
            }
        }
    }

    public void getNextPage(TableController controller) {}

    public void getPage(int page) {}

    public void getPreviousPage(TableController controller) {}

    public void rowAdded(int row, TableController controller) {}

    public void setModel(TableController controller, DataModel model) {}

    public void setMultiple(int row, int col, TableController controller) {}
    //
    //end table manager methods
    //
    
    private void onRemoveItemButtonClick() {
        int selectedRow = itemsController.selected;
        if (selectedRow > -1 && itemsController.model.numRows() > 0) {
            TableRow row = itemsController.model.getRow(selectedRow);
            itemsController.model.hideRow(row);

            // reset the model
            itemsController.reset();
            // need to set the deleted flag to "Y" also
            StringField deleteFlag = new StringField();
            deleteFlag.setValue("Y");

            row.addHidden("deleteFlag", deleteFlag);
        }
    }
    
    private void onStandardNoteCustomerButtonClick() {
        PopupPanel standardNotePopupPanel = new PopupPanel(false, true);
        ScreenWindow pickerWindow = new ScreenWindow(standardNotePopupPanel, "Choose Standard Note", "standardNotePicker", "Loading...");
        pickerWindow.setContent(new StandardNotePickerScreen((TextArea)customerNoteText.getWidget()));

        standardNotePopupPanel.add(pickerWindow);
        int left = this.getAbsoluteLeft();
        int top = this.getAbsoluteTop();
        standardNotePopupPanel.setPopupPosition(left, top);
        standardNotePopupPanel.show();
    }
    
    private void onStandardNoteShippingButtonClick() {
        PopupPanel standardNotePopupPanel = new PopupPanel(false, true);
        ScreenWindow pickerWindow = new ScreenWindow(standardNotePopupPanel, "Choose Standard Note", "standardNotePicker", "Loading...");
        pickerWindow.setContent(new StandardNotePickerScreen((TextArea)shippingNoteText.getWidget()));

        standardNotePopupPanel.add(pickerWindow);
        int left = this.getAbsoluteLeft();
        int top = this.getAbsoluteTop();
        standardNotePopupPanel.setPopupPosition(left, top);
        standardNotePopupPanel.show();
    }
    
    private void fillItemsModel() {
        Integer orderId = null;
        NumberObject orderIdObj;
        StringObject orderTypeObj = new StringObject();
        TableField f;

        if (key == null || key.getKey() == null) {
            clearItems = false;
            return;
        }

        window.setStatus("","spinnerIcon");
        
        orderId = (Integer)key.getKey().getValue();
        orderIdObj = new NumberObject(orderId);
        orderTypeObj.setValue(orderType);

        f = new TableField();
        f.setValue(itemsController.model);

        // prepare the argument list for the getObject function
        DataObject[] args = new DataObject[] {orderIdObj, f, orderTypeObj};

        screenService.getObject("getItemsModel", args, new AsyncCallback() {
            public void onSuccess(Object result) {
                // get the table model and load it
                // in the table
                //rpc.setFieldValue("itemsTable",
                //                  (TableModel)((TableField)result).getValue());
                
                itemsController.loadModel((TableModel)((TableField)result).getValue());

                clearItems = itemsController.model.numRows() > 0;
                
                window.setStatus("","");
            }

            public void onFailure(Throwable caught) {
                Window.alert(caught.getMessage());
            }
        });
    }
    
    private void fillReceipts() {
        Integer orderId = null;
        NumberObject orderIdObj;
        TableField f;

        if (key == null || key.getKey() == null) {
            clearReceipts = false;
            return;
        }

        window.setStatus("","spinnerIcon");
        
        orderId = (Integer)key.getKey().getValue();
        orderIdObj = new NumberObject(orderId);

        f = new TableField();
        f.setValue(receiptsController.model);

        // prepare the argument list for the getObject function
        DataObject[] args = new DataObject[] {orderIdObj, f};

        screenService.getObject("getReceiptsModel", args, new AsyncCallback() {
            public void onSuccess(Object result) {
                receiptsController.loadModel((TableModel)((TableField)result).getValue());

                clearReceipts = receiptsController.model.numRows() > 0;
                
                window.setStatus("","");
            }

            public void onFailure(Throwable caught) {
                Window.alert(caught.getMessage());
            }
        });
    }
    
    private void fillCustomerNotes() {
        Integer orderId = null;
        NumberObject orderIdObj;

        if (key == null || key.getKey() == null) {
            clearCustomerNotes = false;
            return;
        }

        window.setStatus("","spinnerIcon");
        
        orderId = (Integer)key.getKey().getValue();
        orderIdObj = new NumberObject(orderId);

        // prepare the argument list for the getObject function
        DataObject[] args = new DataObject[] {orderIdObj};

        screenService.getObject("getCustomerNotes", args, new AsyncCallback() {
            public void onSuccess(Object result) {
                
                String notesText = (String)((StringField)result).getValue();
                
                ((TextArea)customerNoteText.getWidget()).setText(notesText);
                
                window.setStatus("","");
            }

            public void onFailure(Throwable caught) {
                Window.alert(caught.getMessage());
            }
        });
    }
    
    private void fillShippingNotes() {
        Integer orderId = null;
        NumberObject orderIdObj;

        if (key == null || key.getKey() == null) {
            clearShippingNotes = false;
            return;
        }

        window.setStatus("","spinnerIcon");
        
        orderId = (Integer)key.getKey().getValue();
        orderIdObj = new NumberObject(orderId);

        // prepare the argument list for the getObject function
        DataObject[] args = new DataObject[] {orderIdObj};

        screenService.getObject("getShippingNotes", args, new AsyncCallback() {
            public void onSuccess(Object result) {
                
                String notesText = (String)((StringField)result).getValue();
                
                ((TextArea)shippingNoteText.getWidget()).setText(notesText);
                
                window.setStatus("","");
            }

            public void onFailure(Throwable caught) {
                Window.alert(caught.getMessage());
            }
        });
    }
    
    private void fillReportToBillTo() {
        Integer orderId = null;
        NumberObject orderIdObj;

        if (key == null || key.getKey() == null) {
            clearReportToBillTo = false;
            return;
        }

        window.setStatus("","spinnerIcon");
        
        orderId = (Integer)key.getKey().getValue();
        orderIdObj = new NumberObject(orderId);

        // prepare the argument list for the getObject function
        DataObject[] args = new DataObject[] {orderIdObj};

        screenService.getObject("getReportToBillTo", args, new AsyncCallback() {
            public void onSuccess(Object result) {
                if(result != null){
                    DataModel model = (DataModel)((ModelField)result).getValue();
                    DataSet set = model.get(0);
                    
                    ArrayList billToDataSets = new ArrayList();
                    billToDataSets.add((DataSet)model.get(2));
                    
                    billToDropdown.setSelected(billToDataSets);
                    billToAptSuite.setText((String)((StringObject)set.getObject(2)).getValue());
                    billToAddress.setText((String)((StringObject)set.getObject(3)).getValue());
                    billToCity.setText((String)((StringObject)set.getObject(4)).getValue());
                    billToState.setText((String)((StringObject)set.getObject(5)).getValue());
                    billToZipCode.setText((String)((StringObject)set.getObject(6)).getValue());
                    
                    ArrayList reportToDataSets = new ArrayList();
                    reportToDataSets.add((DataSet)model.get(1));
                    
                    reportToDropdown.setSelected(reportToDataSets);
                    reportToAptSuite.setText((String)((StringObject)set.getObject(9)).getValue());
                    reportToAddress.setText((String)((StringObject)set.getObject(10)).getValue());
                    reportToCity.setText((String)((StringObject)set.getObject(11)).getValue());
                    reportToState.setText((String)((StringObject)set.getObject(12)).getValue());
                    reportToZipCode.setText((String)((StringObject)set.getObject(13)).getValue());

                }
                window.setStatus("","");
            }

            public void onFailure(Throwable caught) {
                Window.alert(caught.getMessage());
            }
        });
    }
    
    private void clearItems() {
        itemsController.model.reset();
        //itemsController.setModel(itemsController.model);
        //rpc.setFieldValue("itemsTable", itemsController.model);
    }
    
    private void clearReceipts() {
        receiptsController.model.reset();
        //receiptsController.setModel(receiptsController.model);
    }
    
    private void clearCustomerNotes() {
        ((TextArea)customerNoteText.getWidget()).setText("");
    }
    
    private void clearShippingNotes() {
        ((TextArea)shippingNoteText.getWidget()).setText("");
    }
    
    private void clearReportToBillTo() {
        
    }
    
    private boolean tableRowEmpty(TableRow row){
        boolean empty = true;
        
        for(int i=0; i<row.numColumns(); i++){
            if(row.getColumn(i).getValue() != null && !"".equals(row.getColumn(i).getValue())){
                empty = false;
                break;
            }
        }
        
        return empty;
    }
}
