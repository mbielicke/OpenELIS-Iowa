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
package org.openelis.modules.order.client;

import java.util.ArrayList;
import java.util.HashMap;

import org.openelis.gwt.common.FormRPC;
import org.openelis.gwt.common.data.BooleanObject;
import org.openelis.gwt.common.data.DataModel;
import org.openelis.gwt.common.data.DataObject;
import org.openelis.gwt.common.data.DataSet;
import org.openelis.gwt.common.data.DropDownField;
import org.openelis.gwt.common.data.KeyListManager;
import org.openelis.gwt.common.data.ModelObject;
import org.openelis.gwt.common.data.NumberField;
import org.openelis.gwt.common.data.NumberObject;
import org.openelis.gwt.common.data.StringField;
import org.openelis.gwt.common.data.StringObject;
import org.openelis.gwt.common.data.TableRow;
import org.openelis.gwt.screen.CommandChain;
import org.openelis.gwt.screen.ScreenAutoDropdown;
import org.openelis.gwt.screen.ScreenMenuItem;
import org.openelis.gwt.screen.ScreenMenuPanel;
import org.openelis.gwt.screen.ScreenTextArea;
import org.openelis.gwt.screen.ScreenTextBox;
import org.openelis.gwt.screen.ScreenWindow;
import org.openelis.gwt.widget.AToZTable;
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
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class OrderScreen extends OpenELISScreenForm implements TableManager, ClickListener, TabListener, ChangeListener {
    
    private static boolean loaded = false;
    private boolean startedLoadingTable = false;
    
    private static DataModel statusDropdown, costCenterDropdown, shipFromDropdown;
    
    private AutoCompleteDropdown orgDropdown, billToDropdown, reportToDropdown;
    
    private AppButton        removeItemButton, standardNoteCustomerButton, standardNoteShippingButton;
    
    private ScreenMenuPanel duplicateMenuPanel;
    
    private ScreenTextBox          orderNum, neededInDays, orderDate, requestedBy;
    private TextBox orgAptSuite, orgAddress, orgCity, orgState, orgZipCode,
                    reportToAptSuite, reportToAddress, reportToCity, reportToState, reportToZipCode,
                    billToAptSuite, billToAddress, billToCity, billToState, billToZipCode;
    
    private KeyListManager keyList = new KeyListManager();
    
    private ScreenTextArea   shippingNoteText, customerNoteText;
    
    private ScreenAutoDropdown status;
    
    private EditTable itemsController, receiptsController;
    
    private String orderType;
    
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
        if(sender instanceof ScreenMenuItem){
            if("duplicateRecord".equals(((String)((ScreenMenuItem)sender).objClass))){
                onDuplicateRecordClick();
            }
        }else if (sender == standardNoteCustomerButton)
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
        
        AToZTable atozTable = (AToZTable) getWidget("azTable");
        ButtonPanel atozButtons = (ButtonPanel)getWidget("atozButtons");
        ButtonPanel bpanel = (ButtonPanel)getWidget("buttons");
        
        CommandChain chain = new CommandChain();
        chain.addCommand(this);
        chain.addCommand(keyList);
        chain.addCommand(bpanel);
        chain.addCommand(atozTable);
        chain.addCommand(atozButtons);
        
        itemsController = ((TableWidget)getWidget("itemsTable")).controller;
        //itemsController.setAutoAdd(false);
        addCommandListener(itemsController);
        
        if("external".equals(orderType)){
            receiptsController = ((TableWidget)getWidget("receiptsTable")).controller;
          //  receiptsController.setAutoAdd(false);
            addCommandListener(receiptsController);
        }
        
        duplicateMenuPanel = (ScreenMenuPanel)widgets.get("optionsMenu");
        
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
            shipFromDropdown = (DataModel)initData.get("shipFrom");
        }

        drop = (AutoCompleteDropdown)getWidget(OrderMeta.getStatusId());
        drop.setModel(statusDropdown);
        
       // drop = (AutoCompleteDropdown)getWidget("store");
       // if(drop != null)
       //     drop.setModel(storeDropdown);
        
        drop = (AutoCompleteDropdown)getWidget(OrderMeta.getCostCenterId());
        drop.setModel(costCenterDropdown);
        
        if("kits".equals(orderType)){
            drop = (AutoCompleteDropdown)getWidget(OrderMeta.getShipFromId());
            drop.setModel(shipFromDropdown);
        }
        
        //addCommandListener((ButtonPanel)getWidget("buttons"));
        //((ButtonPanel)getWidget("buttons")).addCommandListener(this);
        
        //set order type in display and query rpcs
        ((FormRPC)forms.get("display")).setFieldValue("orderType", orderType);
        ((FormRPC)forms.get("query")).setFieldValue("orderType", orderType);
        
        updateChain.add(afterUpdate);
        //commitQueryChain.add(afterCommitQuery);
        
        super.afterDraw(sucess);
    }
     
    /*protected AsyncCallback afterCommitAdd = new AsyncCallback() {
         public void onSuccess(Object result){
             Window.alert("AFTER!!");
             itemsController.setAutoAdd(false);
             itemsController.reset();
         }
         
         public void onFailure(Throwable caught){
             
         }
      };*/
      
    /*
   protected AsyncCallback afterCommitQuery = new AsyncCallback() {
          public void onFailure(Throwable caught) {
          
          }

          public void onSuccess(Object result) {
              rpc.setFieldValue("orderType", orderType);    
          }
    };
    */
    
    public void update() {
        if(rpc.getField("originalStatus") != null)
            rpc.setFieldValue("originalStatus", (Integer)((AutoCompleteDropdown)status.getWidget()).getSelectedValue());
        super.update();
        
    }
    
    public void query() {
        super.query();
        
        //rpc.setFieldValue("orderType", orderType);

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
        //itemsController.setAutoAdd(true);
        super.add();
        
        //rpc.setFieldValue("orderType", orderType);
        
        
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
        
    protected AsyncCallback afterUpdate = new AsyncCallback() {
        public void onFailure(Throwable caught) {   
            Window.alert("failure");
        }
        public void onSuccess(Object result) {
    //        rpc.setFieldValue("orderType", orderType);

            orderNum.enable(false);
            orderDate.enable(false);
            
            neededInDays.setFocus(true);
        }
    };
    
    /*protected AsyncCallback afterCommitUpdate = new AsyncCallback() {
        public void onFailure(Throwable caught) {   
        }
        public void onSuccess(Object result) {
            itemsController.setAutoAdd(false);
            itemsController.reset();
        }
    };*/
    
    
    public boolean onBeforeTabSelected(SourcesTabEvents sender, int index) {
        if(state != FormInt.State.QUERY){
            if (index == 0 && !((FormRPC)rpc.getField("items")).load) {
                fillItemsModel(false);
                
            } else if (index == 1 && "kits".equals(orderType) && !((FormRPC)rpc.getField("custNote")).load) {
                fillCustomerNotes();
                
            } else if ((index == 1 && "external".equals(orderType) && !((FormRPC)rpc.getField("receipts")).load)) {
                fillReceipts();
                
            } else if (index == 1 && "internal".equals(orderType) && !((FormRPC)rpc.getField("shippingNote")).load) {
                fillShippingNotes();

            } else if (index == 2 && !((FormRPC)rpc.getField("shippingNote")).load) {
                fillShippingNotes();
                
            } else if (index == 3 && !((FormRPC)rpc.getField("reportToBillTo")).load) {
                fillReportToBillTo();
                
            }
        }
        return true;
    }

    public void onTabSelected(SourcesTabEvents sender, int tabIndex) { }

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

    public boolean doAutoAdd(TableRow row, TableController controller) {
        return !tableRowEmpty(row);
    }
    
    public void finishedEditing(int row, int col, TableController controller) {
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
                    NumberField qtyOnHand = new NumberField(NumberObject.Type.INTEGER);
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
                        qtyOnHand.setValue((Integer)((NumberObject)selectedRow.getObject(5)).getValue());
                        tableRow.addHidden("locationId", locationId);
                        tableRow.addHidden("qtyOnHand", qtyOnHand);
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
    
    public void changeState(FormInt.State state) {
        if(duplicateMenuPanel != null){ 
            if(state == FormInt.State.DISPLAY){
                ((ScreenMenuItem)((ScreenMenuItem) duplicateMenuPanel.menuItems.get(0)).menuItemsPanel.menuItems.get(0)).enable(true);

            }else{  
                ((ScreenMenuItem)((ScreenMenuItem)duplicateMenuPanel.menuItems.get(0)).menuItemsPanel.menuItems.get(0)).enable(false);
            }
        }
        
        super.changeState(state);
    }
    
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
    
    private void onDuplicateRecordClick(){
        if(state == FormInt.State.DISPLAY){
            //we need to do the duplicate method
            FormRPC displayRPC = rpc.clone();
            displayRPC.setFieldValue(OrderMeta.getId(), null);
            displayRPC.setFieldValue(OrderMeta.getExternalOrderNumber(), null);
            displayRPC.setFieldValue("orderType", orderType);
            ((FormRPC)displayRPC.getField("receipts")).setFieldValue("receiptsTable", null);
            ((FormRPC)displayRPC.getField("shippingNote")).setFieldValue(OrderMeta.ORDER_SHIPPING_NOTE_META.getText(),null);
            
            //set the load flags correctly
            ((FormRPC)displayRPC.getField("items")).load = false;
            ((FormRPC)displayRPC.getField("shippingNote")).load = true;
            ((FormRPC)displayRPC.getField("receipts")).load = true;
            
            DataSet tempKey = key;
            
            add();
            
            key = tempKey;

            rpc = displayRPC;
            forms.put("display", displayRPC);
                        
            load();
            
            fillItemsModel(true);
        }
    }
    
    private void fillItemsModel(final boolean forDuplicate) {
        if(key == null)
            return;
        
        window.setStatus("","spinnerIcon");
        
        // prepare the argument list for the getObject function
        DataObject[] args = new DataObject[] {key, new BooleanObject(forDuplicate), new StringObject(orderType), rpc.getField("items")};

        screenService.getObject("loadItems", args, new AsyncCallback() {
            public void onSuccess(Object result) {
                load((FormRPC)result);
                rpc.setField("items", (FormRPC)result);
                
                if(forDuplicate)
                    key = null;
                
                window.setStatus("","");
            }

            public void onFailure(Throwable caught) {
                Window.alert(caught.getMessage());
                window.setStatus("","");
            }
        });
    }
    
    private void fillReceipts() {
        if(key == null)
            return;
        
        window.setStatus("","spinnerIcon");

        // prepare the argument list for the getObject function
        DataObject[] args = new DataObject[] {key, rpc.getField("receipts")};

        screenService.getObject("loadReceipts", args, new AsyncCallback() {
            public void onSuccess(Object result) {
                load((FormRPC)result);
                rpc.setField("receipts", (FormRPC)result);
                window.setStatus("","");
            }

            public void onFailure(Throwable caught) {
                Window.alert(caught.getMessage());
                window.setStatus("","");
            }
        });
    }
    
    private void fillCustomerNotes() {
        if(key == null)
            return;
        
        window.setStatus("","spinnerIcon");

        // prepare the argument list for the getObject function
        DataObject[] args = new DataObject[] {key, rpc.getField("custNote")};

        screenService.getObject("loadCustomerNotes", args, new AsyncCallback() {
            public void onSuccess(Object result) {
                load((FormRPC)result);
                rpc.setField("custNote",(FormRPC)result);
                window.setStatus("","");
            }

            public void onFailure(Throwable caught) {
                Window.alert(caught.getMessage());
                window.setStatus("","");
            }
        });
    }
    
    private void fillShippingNotes() {
        if(key == null)
            return;
        
        window.setStatus("","spinnerIcon");

        // prepare the argument list for the getObject function
        DataObject[] args = new DataObject[] {key, rpc.getField("shippingNote")};

        screenService.getObject("loadOrderShippingNotes", args, new AsyncCallback() {
            public void onSuccess(Object result) {
                load((FormRPC)result);
                rpc.setField("shippingNote",(FormRPC)result);
                window.setStatus("","");
            }

            public void onFailure(Throwable caught) {
                Window.alert(caught.getMessage());
                window.setStatus("","");
            }
        });
    }
    
    private void fillReportToBillTo() {
        if(key == null)
            return;
        
        window.setStatus("","spinnerIcon");

        // prepare the argument list for the getObject function
        DataObject[] args = new DataObject[] {key, rpc.getField("reportToBillTo")};

        screenService.getObject("loadReportToBillTo", args, new AsyncCallback() {
            public void onSuccess(Object result) {
                if(result != null){
                    /*DataModel model = (DataModel)((ModelField)result).getValue();
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
                    reportToZipCode.setText((String)((StringObject)set.getObject(13)).getValue());*/
                    load((FormRPC)result);
                    rpc.setField("reportToBillTo",(FormRPC)result);

                }
                window.setStatus("","");
            }

            public void onFailure(Throwable caught) {
                Window.alert(caught.getMessage());
                window.setStatus("","");
            }
        });
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
