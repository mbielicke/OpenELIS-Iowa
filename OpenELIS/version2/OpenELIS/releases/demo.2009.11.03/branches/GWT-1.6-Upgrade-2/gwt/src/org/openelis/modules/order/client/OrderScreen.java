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

import org.openelis.gwt.common.Form;
import org.openelis.gwt.common.data.DataModel;
import org.openelis.gwt.common.data.DataSet;
import org.openelis.gwt.common.data.DropDownField;
import org.openelis.gwt.common.data.KeyListManager;
import org.openelis.gwt.common.data.StringField;
import org.openelis.gwt.common.data.StringObject;
import org.openelis.gwt.screen.CommandChain;
import org.openelis.gwt.screen.ScreenDropDownWidget;
import org.openelis.gwt.screen.ScreenMenuPanel;
import org.openelis.gwt.screen.ScreenTextArea;
import org.openelis.gwt.screen.ScreenTextBox;
import org.openelis.gwt.screen.ScreenWindow;
import org.openelis.gwt.widget.AToZTable;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.AutoComplete;
import org.openelis.gwt.widget.ButtonPanel;
import org.openelis.gwt.widget.Dropdown;
import org.openelis.gwt.widget.FormInt;
import org.openelis.gwt.widget.MenuItem;
import org.openelis.gwt.widget.table.TableManager;
import org.openelis.gwt.widget.table.TableWidget;
import org.openelis.gwt.widget.table.event.SourcesTableWidgetEvents;
import org.openelis.gwt.widget.table.event.TableWidgetListener;
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

public class OrderScreen extends OpenELISScreenForm<OrderRPC, OrderForm, Integer> implements TableManager, TableWidgetListener, ClickListener, TabListener, ChangeListener {
    
    private AutoComplete orgDropdown, billToDropdown, reportToDropdown;
    
    private AppButton        removeItemButton, standardNoteCustomerButton, standardNoteShippingButton;
    
    private ScreenMenuPanel duplicateMenuPanel;
    
    private ScreenTextBox          orderNum, neededInDays, orderDate, requestedBy;
    private TextBox orgAptSuite, orgAddress, orgCity, orgState, orgZipCode,
                    reportToAptSuite, reportToAddress, reportToCity, reportToState, reportToZipCode,
                    billToAptSuite, billToAddress, billToCity, billToState, billToZipCode;
    
    private KeyListManager keyList = new KeyListManager();
    
    private ScreenTextArea   shippingNoteText, customerNoteText;
    
    private Dropdown status, shipFrom, costCenter;
    
    private TableWidget itemsTable, receiptsTable;
    
    private String orderType;
    
    private OrderMetaMap OrderMeta = new OrderMetaMap();
    
    AsyncCallback<OrderRPC> checkModels = new AsyncCallback<OrderRPC>() {
        public void onSuccess(OrderRPC rpc) {
            if(rpc.status != null) {
                setStatusIdModel(rpc.status);
                rpc.status = null;
            }
            if(rpc.costCenters != null) {
                setCostCentersModel(rpc.costCenters);
                rpc.costCenters = null;
            }
            if(rpc.shipFrom != null) {
                setShipFromModel(rpc.shipFrom);
                rpc.shipFrom = null;
            }
        }
        
        public void onFailure(Throwable caught) {
            
        }
    };
    
    public OrderScreen(Object[] args) {                
        super("org.openelis.modules.order.server.OrderService");
        
        orderType = (String)((StringObject)args[0]).getValue();
      
        forms.put("display",new OrderForm());
        OrderRPC orderRPC = new OrderRPC();
        orderRPC.orderType = orderType;
        
        getScreen(orderRPC);
    }

    public void onClick(Widget sender) {
        if(sender instanceof MenuItem){
            if("duplicateRecord".equals(((String)((MenuItem)sender).objClass))){
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
            if(orgDropdown.getSelections().size() > 0){
                DataSet selectedRow = (DataSet)orgDropdown.getSelections().get(0);
                OrderOrgKey selectedKey = (OrderOrgKey)selectedRow.getData();
                
                //load address
                orgAddress.setText((String)((StringObject)selectedRow.get(1)).getValue());
                //load city
                orgCity.setText((String)((StringObject)selectedRow.get(2)).getValue());
                //load state
                orgState.setText((String)((StringObject)selectedRow.get(3)).getValue());               
                //load apt/suite
                orgAptSuite.setText(selectedKey.aptSuite);
                //load zipcode
                orgZipCode.setText(selectedKey.zipCode);
            }else{
                orgAddress.setText("");
                orgCity.setText("");
                orgState.setText("");               
                orgAptSuite.setText("");
                orgZipCode.setText("");
            }
                
        }else if(sender == reportToDropdown){
            if(reportToDropdown.getSelections().size() > 0){
                DataSet selectedRow = (DataSet)reportToDropdown.getSelections().get(0);
                OrderOrgKey selectedKey = (OrderOrgKey)selectedRow.getData();
                
                //load address
                reportToAddress.setText((String)((StringObject)selectedRow.get(1)).getValue());
                //load city
                reportToCity.setText((String)((StringObject)selectedRow.get(2)).getValue());
                //load state
                reportToState.setText((String)((StringObject)selectedRow.get(3)).getValue());               
                //load apt/suite
                reportToAptSuite.setText(selectedKey.aptSuite);
                //load zipcode
                reportToZipCode.setText(selectedKey.zipCode);
            }else{
                reportToAddress.setText("");
                reportToCity.setText("");
                reportToState.setText("");               
                reportToAptSuite.setText("");
                reportToZipCode.setText("");
            }
        }else if(sender == billToDropdown){
            if(billToDropdown.getSelections().size() > 0){
                DataSet selectedRow = (DataSet)billToDropdown.getSelections().get(0);
                OrderOrgKey selectedKey = (OrderOrgKey)selectedRow.getData();
                
                //load address
                billToAddress.setText((String)((StringObject)selectedRow.get(1)).getValue());
                //load city
                billToCity.setText((String)((StringObject)selectedRow.get(2)).getValue());
                //load state
                billToState.setText((String)((StringObject)selectedRow.get(3)).getValue());               
                //load apt/suite
                billToAptSuite.setText(selectedKey.aptSuite);
                //load zipcode
                billToZipCode.setText(selectedKey.zipCode);
            }else{
                billToAddress.setText("");
                billToCity.setText("");
                billToState.setText("");               
                billToAptSuite.setText("");
                billToZipCode.setText("");                
            }
        }
    }
    
    public void afterDraw(boolean sucess) {
        orderNum = (ScreenTextBox)widgets.get(OrderMeta.getId());
        neededInDays = (ScreenTextBox)widgets.get(OrderMeta.getNeededInDays());
        //status = (ScreenDropDownWidget)widgets.get(OrderMeta.getStatusId());
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
        
        itemsTable = (TableWidget)getWidget("itemsTable");
        itemsTable.addTableWidgetListener(this);
        itemsTable.model.enableAutoAdd(false);
        
        receiptsTable = (TableWidget)getWidget("receiptsTable");
        receiptsTable.model.enableAutoAdd(false);
        
        status = (Dropdown)getWidget(OrderMeta.getStatusId());
        costCenter = (Dropdown)getWidget(OrderMeta.getCostCenterId());
        shipFrom = (Dropdown)getWidget(OrderMeta.getShipFromId());
        
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
        
        orgDropdown = (AutoComplete)getWidget(OrderMeta.ORDER_ORGANIZATION_META.getName()); 
        billToDropdown = (AutoComplete)getWidget(OrderMeta.ORDER_BILL_TO_META.getName());
        reportToDropdown = (AutoComplete)getWidget(OrderMeta.ORDER_REPORT_TO_META.getName());
               
        setStatusIdModel(rpc.status);
        setCostCentersModel(rpc.costCenters);
        setShipFromModel(rpc.shipFrom);
        
        /*
         * Null out the rpc models so they are not sent with future rpc calls
         */
        rpc.status = null;
        rpc.costCenters = null;
        rpc.shipFrom = null;
        
        
        //set order type in display and query rpcs
        ((Form)forms.get("display")).setFieldValue("orderType", orderType);
        ((Form)forms.get("query")).setFieldValue("orderType", orderType);
        
        updateChain.add(afterUpdate);
        //commitQueryChain.add(afterCommitQuery);
        
        updateChain.add(0,checkModels);
        fetchChain.add(0,checkModels);
        abortChain.add(0,checkModels);
        deleteChain.add(0,checkModels);
        commitUpdateChain.add(0,checkModels);
        commitAddChain.add(0,checkModels);
        
        super.afterDraw(sucess);
        
        rpc.form.items.itemsTable.setValue(itemsTable.model.getData());
        rpc.form.receipts.receiptsTable.setValue(receiptsTable.model.getData());
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
        rpc.originalStatus = (Integer)((DataSet)status.getSelections().get(0)).getKey();
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
        super.add();
        itemsTable.model.enableAutoAdd(true);
        //rpc.setFieldValue("orderType", orderType);
        
        
        window.setStatus("","spinnerIcon");

        OrderRPC orpc = new OrderRPC();
        orpc.key = rpc.key;
        orpc.form = rpc.form;
          
        screenService.call("getAddAutoFillValues", orpc, new AsyncCallback<OrderRPC>(){
            public void onSuccess(OrderRPC returnRpc){    
                //load the values
                ((ScreenDropDownWidget)widgets.get(OrderMeta.getStatusId())).load(returnRpc.form.statusId);
                orderDate.load(returnRpc.form.orderedDate);
                requestedBy.load(returnRpc.form.requestedBy);
                
                //set the values in the rpc
                rpc.form.statusId.setValue(returnRpc.form.statusId.getValue());
                rpc.form.orderedDate.setValue(returnRpc.form.orderedDate.getValue());
                rpc.form.requestedBy.setValue(returnRpc.form.requestedBy.getValue());
                
                window.setStatus("","");
            }
            
            public void onFailure(Throwable caught){
                Window.alert(caught.getMessage());
            }
        }); 
        
        orderNum.enable(false);
        ((ScreenDropDownWidget)widgets.get(OrderMeta.getStatusId())).enable(false);
        orderDate.enable(false);
        
        neededInDays.setFocus(true);
    }
        
    protected AsyncCallback afterUpdate = new AsyncCallback() {
        public void onFailure(Throwable caught) {   
        }
        
        public void onSuccess(Object result) {
    //        rpc.setFieldValue("orderType", orderType);

            itemsTable.model.enableAutoAdd(true);
            orderNum.enable(false);
            orderDate.enable(false);
            
            neededInDays.setFocus(true);
        }
    };
    
    public void abort() {
        itemsTable.model.enableAutoAdd(false);
        super.abort();
    }
    
    /*protected AsyncCallback afterCommitUpdate = new AsyncCallback() {
        public void onFailure(Throwable caught) {   
        }
        public void onSuccess(Object result) {
            itemsController.setAutoAdd(false);
            itemsController.reset();
        }
    };*/
    
    public void commit() {
        itemsTable.model.enableAutoAdd(false);
        super.commit();
    }
    
    public boolean onBeforeTabSelected(SourcesTabEvents sender, int index) {
        if(state != FormInt.State.QUERY){
            if (index == 0 && !rpc.form.items.load) {
                fillItemsModel(false);
                
            }else if( index == 1 && !rpc.form.receipts.load) {
                fillReceipts();
                
            } else if (index == 2 && "kits".equals(orderType) && !rpc.form.customerNotes.load) {
                fillCustomerNotes();
                
            } else if ((index == 2 && !"kits".equals(orderType) && !rpc.form.shippingNotes.load)) {
                fillShippingNotes();
            
            } else if ((index == 3 && !rpc.form.shippingNotes.load)) {
                fillShippingNotes();
                
            } else if (index == 4 && !rpc.form.reportToBillTo.load) {
                fillReportToBillTo();
                
            }
        }
        return true;
    }

    public void onTabSelected(SourcesTabEvents sender, int tabIndex) { }

    //
    //start table manager methods
    //
    public boolean canAdd(TableWidget widget, DataSet set, int row) {
        return true;
    }

    public boolean canAutoAdd(TableWidget widget, DataSet addRow) {
        return !tableRowEmpty(addRow);
    }

    public boolean canDelete(TableWidget widget, DataSet set, int row) {
        return true;
    }

    public boolean canEdit(TableWidget widget, DataSet set, int row, int col) {
        return true;
    }

    public boolean canSelect(TableWidget widget, DataSet set, int row) {
        if(state == FormInt.State.ADD || state == FormInt.State.UPDATE)           
            return true;
        return false;
    }
    
    public boolean canDrag(TableWidget widget, DataSet item, int row) {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean canDrop(TableWidget widget, Widget dragWidget, DataSet dropTarget, int targetRow) {
        // TODO Auto-generated method stub
        return false;
    }

    public void drop(TableWidget widget, Widget dragWidget, DataSet dropTarget, int targetRow) {
        // TODO Auto-generated method stub
        
    }
    
    public void drop(TableWidget widget, Widget dragWidget) {
        // TODO Auto-generated method stub
        
    }
    //
    //end table manager methods
    //
    
    //
    //start table listener methods
    //
    public void finishedEditing(SourcesTableWidgetEvents sender, int row, int col) {
        if(col == 1 && row < itemsTable.model.numRows()){
            DataSet tableRow = itemsTable.model.getRow(row);
            DropDownField<Integer> invItemField = (DropDownField)tableRow.get(1);
            ArrayList selections = invItemField.getValue();
  
            if(selections.size() > 0){
                DataSet selectedRow = (DataSet)selections.get(0);
  
                if(selectedRow.size() > 1){
                    StringField storeLabel = new StringField();
                    //StringField locationLabel = new StringField();
                    //NumberField locationId = new NumberField(NumberObject.Type.INTEGER);
                    //NumberField qtyOnHand = new NumberField(NumberObject.Type.INTEGER);
                    //storeLabel.setValue((String)((StringObject)selectedRow.get(1)).getValue());

                    tableRow.get(2).setValue((String)((StringObject)selectedRow.get(1)).getValue());
                    
                    /*if(tableRow.size() == 4){
                        locationLabel.setValue((String)((StringObject)selectedRow.get(2)).getValue());
                        tableRow.set(3, locationLabel);
                        
                        DataMap selectedMap = (DataMap)selectedRow.getData();
                        locationId.setValue((Integer)((NumberObject)selectedMap.get("locId")).getValue());
                        qtyOnHand.setValue((Integer)((NumberObject)selectedRow.get(5)).getValue());
                        DataMap map = new DataMap();
                        map.put("locationId", locationId);
                        map.put("qtyOnHand", qtyOnHand);
                        tableRow.setData(map);
                    }*/
                    itemsTable.model.refresh();
                }
            }
        }
    }

    public void startEditing(SourcesTableWidgetEvents sender, int row, int col) {
        // TODO Auto-generated method stub
        
    }

    public void stopEditing(SourcesTableWidgetEvents sender, int row, int col) {
        // TODO Auto-generated method stub
        
    }
    //
    //end table listener methods
    //
    
    public void changeState(FormInt.State state) {
        if(duplicateMenuPanel != null){ 
            if(state == FormInt.State.DISPLAY){
                ((MenuItem)((MenuItem)duplicateMenuPanel.panel.menuItems.get(0)).menuItemsPanel.menuItems.get(0)).enable(true);

            }else{  
                ((MenuItem)((MenuItem)duplicateMenuPanel.panel.menuItems.get(0)).menuItemsPanel.menuItems.get(0)).enable(false);
            }
        }
        
        super.changeState(state);
    }
    
    private void onRemoveItemButtonClick() {
        int selectedRow = itemsTable.model.getSelectedIndex();
        if (selectedRow > -1 && itemsTable.model.numRows() > 0) {
            itemsTable.model.deleteRow(selectedRow);

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
            OrderForm display = (OrderForm)rpc.form.clone();
            display.setFieldValue(OrderMeta.getId(), null);
            display.setFieldValue(OrderMeta.getExternalOrderNumber(), null);
            display.setFieldValue("orderType", orderType);
            if(display.getField("receipts") != null){
                ((Form)display.getField("receipts")).setFieldValue("receiptsTable", null);
                ((Form)display.getField("receipts")).load = true;
            }
            if(display.getField("shippingNote") != null){
                ((Form)display.getField("shippingNote")).setFieldValue(OrderMeta.ORDER_SHIPPING_NOTE_META.getText(),null);
                ((Form)display.getField("shippingNote")).load = true;
            }
            if(display.getField("custNote") != null){
                ((Form)display.getField("custNote")).setFieldValue(OrderMeta.ORDER_CUSTOMER_NOTE_META.getText(),null);
                ((Form)display.getField("custNote")).load = true;
            }
            
            //set the load flags correctly
            ((Form)display.getField("items")).load = false;
            
            Integer tempKey = key;
            
            add();
            
            key = tempKey;

            rpc.form = display;
            forms.put("display", display);
                        
            load();
            
            fillItemsModel(true);
        }
    }
    
    private void fillItemsModel(final boolean forDuplicate) {
        if(key == null)
            return;
        
        window.setStatus("","spinnerIcon");
        
        //prepare the argument list
        OrderItemRPC oirpc = new OrderItemRPC();
        oirpc.key = key;
        oirpc.forDuplicate = forDuplicate;
        oirpc.form = rpc.form.items;
        
        
        screenService.call("loadItems", oirpc, new AsyncCallback<OrderItemRPC>() {
            public void onSuccess(OrderItemRPC result) {
                load(result.form);
                /*
                 * This call has been modified to use the specific sub rpc in the form.  To ensure everything 
                 * stays in sync it needs to be assigned back into the hash and to its member field in the form
                 */
                rpc.form.fields.put("items", rpc.form.items = result.form);
                
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

      //prepare the argument list
        OrderReceiptRPC orrpc = new OrderReceiptRPC();
        orrpc.key = key;
        orrpc.orderType = rpc.orderType;
        orrpc.form = rpc.form.receipts;
        
        screenService.call("loadReceipts", orrpc, new AsyncCallback<OrderReceiptRPC>() {
            public void onSuccess(OrderReceiptRPC result) {
                load(result.form);
                /*
                 * This call has been modified to use the specific sub rpc in the form.  To ensure everything 
                 * stays in sync it needs to be assigned back into the hash and to its member field in the form
                 */
                rpc.form.fields.put("receipts", rpc.form.receipts = result.form);
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

      //prepare the argument list
        OrderNoteRPC onrpc = new OrderNoteRPC();
        onrpc.key = key;
        onrpc.form = rpc.form.customerNotes;
        
        screenService.call("loadCustomerNotes", onrpc, new AsyncCallback<OrderNoteRPC>() {
            public void onSuccess(OrderNoteRPC result) {
                load(result.form);
                rpc.form.fields.put("custNote", rpc.form.customerNotes = result.form);
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

     // prepare the argument list
        OrderShippingNoteRPC osnrpc = new OrderShippingNoteRPC();
        osnrpc.key = key;
        osnrpc.form = rpc.form.shippingNotes;

        screenService.call("loadOrderShippingNotes", osnrpc, new AsyncCallback<OrderShippingNoteRPC>() {
            public void onSuccess(OrderShippingNoteRPC result) {
                load(result.form);
                rpc.form.fields.put("shippingNote", rpc.form.shippingNotes = result.form);

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

        ReportToBillToRpc rtbtrpc = new ReportToBillToRpc();
        rtbtrpc.key = key;
        rtbtrpc.form = rpc.form.reportToBillTo;
        
        screenService.call("loadReportToBillTo", rtbtrpc, new AsyncCallback<ReportToBillToRpc>() {
            public void onSuccess(ReportToBillToRpc result) {
                if(result != null){
                    load(result.form);
                    rpc.form.fields.put("reportToBillTo", rpc.form.reportToBillTo = result.form);

                }
                window.setStatus("","");
            }

            public void onFailure(Throwable caught) {
                Window.alert(caught.getMessage());
                window.setStatus("","");
            }
        });
    }
    
    private boolean tableRowEmpty(DataSet<Integer> row){
        boolean empty = true;
        
        for(int i=0; i<row.size(); i++){
            if(row.get(i).getValue() != null && !"".equals(row.get(i).getValue())){
                empty = false;
                break;
            }
        }
        
        return empty;
    }

    public void setStatusIdModel(DataModel<Integer> statusIdsModel) {
        status.setModel(statusIdsModel);
    }
    
    public void setCostCentersModel(DataModel<Integer> costCentersModel) {
        costCenter.setModel(costCentersModel);
    }
    
    public void setShipFromModel(DataModel<Integer> shipFromsModel) {
        if(shipFrom != null)
            shipFrom.setModel(shipFromsModel);
    }
}