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
import org.openelis.gwt.common.data.Data;
import org.openelis.gwt.common.data.DataMap;
import org.openelis.gwt.common.data.DataModel;
import org.openelis.gwt.common.data.DataSet;
import org.openelis.gwt.common.data.DropDownField;
import org.openelis.gwt.common.data.KeyListManager;
import org.openelis.gwt.common.data.NumberField;
import org.openelis.gwt.common.data.NumberObject;
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

public class OrderScreen extends OpenELISScreenForm implements TableManager, TableWidgetListener, ClickListener, TabListener, ChangeListener {
    
    private static boolean loaded = false;
    
    private static DataModel statusDropdown, costCenterDropdown, shipFromDropdown;
    
    private AutoComplete orgDropdown, billToDropdown, reportToDropdown;
    
    private AppButton        removeItemButton, standardNoteCustomerButton, standardNoteShippingButton;
    
    private ScreenMenuPanel duplicateMenuPanel;
    
    private ScreenTextBox          orderNum, neededInDays, orderDate, requestedBy;
    private TextBox orgAptSuite, orgAddress, orgCity, orgState, orgZipCode,
                    reportToAptSuite, reportToAddress, reportToCity, reportToState, reportToZipCode,
                    billToAptSuite, billToAddress, billToCity, billToState, billToZipCode;
    
    private KeyListManager keyList = new KeyListManager();
    
    private ScreenTextArea   shippingNoteText, customerNoteText;
    
    private ScreenDropDownWidget status;
    
    private TableWidget itemsTable, receiptsTable;
    
    private String orderType;
    
    private OrderMetaMap OrderMeta = new OrderMetaMap();
    
    public OrderScreen(Object[] args) {                
        super("org.openelis.modules.order.server.OrderService");
        
        orderType = (String)((StringObject)args[0]).getValue();
        
        HashMap hash = new HashMap();
        hash.put("type", (StringObject)args[0]);
        
        BooleanObject loadedObj = new BooleanObject((loaded ? "Y" : "N"));
        hash.put("loaded", loadedObj);
        
        getXMLData(hash);
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
                DataMap map = (DataMap)selectedRow.getData();
                
                //load address
                orgAddress.setText((String)((StringObject)selectedRow.get(1)).getValue());
                //load city
                orgCity.setText((String)((StringObject)selectedRow.get(2)).getValue());
                //load state
                orgState.setText((String)((StringObject)selectedRow.get(3)).getValue());               
                //load apt/suite
                orgAptSuite.setText((String)((StringObject)map.get("aptSuite")).getValue());
                //load zipcode
                orgZipCode.setText((String)((StringObject)map.get("zipCode")).getValue());
            }            
        }else if(sender == reportToDropdown){
            if(reportToDropdown.getSelections().size() > 0){
                DataSet selectedRow = (DataSet)reportToDropdown.getSelections().get(0);
                DataMap map = (DataMap)selectedRow.getData();
                
                //load address
                reportToAddress.setText((String)((StringObject)selectedRow.get(1)).getValue());
                //load city
                reportToCity.setText((String)((StringObject)selectedRow.get(2)).getValue());
                //load state
                reportToState.setText((String)((StringObject)selectedRow.get(3)).getValue());               
                //load apt/suite
                reportToAptSuite.setText((String)((StringObject)map.get("aptSuite")).getValue());
                //load zipcode
                reportToZipCode.setText((String)((StringObject)map.get("zipCode")).getValue());
            }
        }else if(sender == billToDropdown){
            if(billToDropdown.getSelections().size() > 0){
                DataSet selectedRow = (DataSet)billToDropdown.getSelections().get(0);
                DataMap map = (DataMap)selectedRow.getData();
                
                //load address
                billToAddress.setText((String)((StringObject)selectedRow.get(1)).getValue());
                //load city
                billToCity.setText((String)((StringObject)selectedRow.get(2)).getValue());
                //load state
                billToState.setText((String)((StringObject)selectedRow.get(3)).getValue());               
                //load apt/suite
                billToAptSuite.setText((String)((StringObject)map.get("aptSuite")).getValue());
                //load zipcode
                billToZipCode.setText((String)((StringObject)map.get("zipCode")).getValue());
            }
        }
    }
    
    public void afterDraw(boolean sucess) {
        Dropdown drop;
        
        loaded = true;

        orderNum = (ScreenTextBox)widgets.get(OrderMeta.getId());
        neededInDays = (ScreenTextBox)widgets.get(OrderMeta.getNeededInDays());
        status = (ScreenDropDownWidget)widgets.get(OrderMeta.getStatusId());
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
        
        if (statusDropdown == null) {
            statusDropdown = (DataModel)initData.get("status");
            costCenterDropdown = (DataModel)initData.get("costCenter");
            shipFromDropdown = (DataModel)initData.get("shipFrom");
        }

        drop = (Dropdown)getWidget(OrderMeta.getStatusId());
        drop.setModel(statusDropdown);
        
       // drop = (AutoCompleteDropdown)getWidget("store");
       // if(drop != null)
       //     drop.setModel(storeDropdown);
        
        drop = (Dropdown)getWidget(OrderMeta.getCostCenterId());
        drop.setModel(costCenterDropdown);
        
        if("kits".equals(orderType)){
            drop = (Dropdown)getWidget(OrderMeta.getShipFromId());
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
        
        ((FormRPC)rpc.getField("items")).setFieldValue("itemsTable", itemsTable.model.getData());
        ((FormRPC)rpc.getField("receipts")).setFieldValue("receiptsTable", receiptsTable.model.getData());
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
            rpc.setFieldValue("originalStatus", (Integer)((NumberObject)((DataSet)((Dropdown)status.getWidget()).getSelections().get(0)).getKey()).getValue());
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
        itemsTable.model.enableAutoAdd(true);
        super.add();
        
        //rpc.setFieldValue("orderType", orderType);
        
        
        window.setStatus("","spinnerIcon");
          
        Data[] args = new Data[0]; 
          
        screenService.getObject("getAddAutoFillValues", args, new AsyncCallback<DataModel>(){
            public void onSuccess(DataModel model){    
              // get the datamodel, load the fields in the form
                DataSet set = model.get(0);

                //load the values
                status.load((DropDownField)set.get(0));
                orderDate.load((StringField)set.get(1));
                requestedBy.load((StringField)set.get(2));
                
                //set the values in the rpc
                rpc.setFieldValue(OrderMeta.getStatusId(), ((DropDownField)set.get(0)).getSelections());
                rpc.setFieldValue(OrderMeta.getOrderedDate(), (String)((StringField)set.get(1)).getValue());
                rpc.setFieldValue(OrderMeta.getRequestedBy(), (String)((StringField)set.get(2)).getValue());
                
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
            if (index == 0 && !((FormRPC)rpc.getField("items")).load) {
                fillItemsModel(false);
                
            }else if( index == 1 && !((FormRPC)rpc.getField("receipts")).load) {
                fillReceipts();
                
            } else if (index == 2 && "kits".equals(orderType) && !((FormRPC)rpc.getField("custNote")).load) {
                fillCustomerNotes();
                
            } else if ((index == 2 && !"kits".equals(orderType) && !((FormRPC)rpc.getField("shippingNote")).load)) {
                fillShippingNotes();
            
            } else if ((index == 3 && !((FormRPC)rpc.getField("shippingNote")).load)) {
                fillShippingNotes();
                
            } else if (index == 4 && !((FormRPC)rpc.getField("reportToBillTo")).load) {
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
            DropDownField invItemField = (DropDownField)tableRow.get(1);
            ArrayList selections = invItemField.getSelections();
  
            if(selections.size() > 0){
                DataSet selectedRow = (DataSet)selections.get(0);
  
                if(selectedRow.size() > 1){
                    StringField storeLabel = new StringField();
                    //StringField locationLabel = new StringField();
                    //NumberField locationId = new NumberField(NumberObject.Type.INTEGER);
                    //NumberField qtyOnHand = new NumberField(NumberObject.Type.INTEGER);
                    storeLabel.setValue((String)((StringObject)selectedRow.get(1)).getValue());

                    tableRow.set(2, storeLabel);
                    
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
            FormRPC displayRPC = (FormRPC)rpc.clone();
            displayRPC.setFieldValue(OrderMeta.getId(), null);
            displayRPC.setFieldValue(OrderMeta.getExternalOrderNumber(), null);
            displayRPC.setFieldValue("orderType", orderType);
            if(displayRPC.getField("receipts") != null){
                ((FormRPC)displayRPC.getField("receipts")).setFieldValue("receiptsTable", null);
                ((FormRPC)displayRPC.getField("receipts")).load = true;
            }
            if(displayRPC.getField("shippingNote") != null){
                ((FormRPC)displayRPC.getField("shippingNote")).setFieldValue(OrderMeta.ORDER_SHIPPING_NOTE_META.getText(),null);
                ((FormRPC)displayRPC.getField("shippingNote")).load = true;
            }
            if(displayRPC.getField("custNote") != null){
                ((FormRPC)displayRPC.getField("custNote")).setFieldValue(OrderMeta.ORDER_CUSTOMER_NOTE_META.getText(),null);
                ((FormRPC)displayRPC.getField("custNote")).load = true;
            }
            
            //set the load flags correctly
            ((FormRPC)displayRPC.getField("items")).load = false;
            
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
        Data[] args = new Data[] {key, new BooleanObject(forDuplicate), new StringObject(orderType), rpc.getField("items")};

        screenService.getObject("loadItems", args, new AsyncCallback<FormRPC>() {
            public void onSuccess(FormRPC result) {
                load(result);
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
        Data[] args = new Data[] {key, new StringObject(orderType), rpc.getField("receipts")};

        screenService.getObject("loadReceipts", args, new AsyncCallback<FormRPC>() {
            public void onSuccess(FormRPC result) {
                load(result);
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
        Data[] args = new Data[] {key, rpc.getField("custNote")};

        screenService.getObject("loadCustomerNotes", args, new AsyncCallback<FormRPC>() {
            public void onSuccess(FormRPC result) {
                load(result);
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
        Data[] args = new Data[] {key, rpc.getField("shippingNote")};

        screenService.getObject("loadOrderShippingNotes", args, new AsyncCallback<FormRPC>() {
            public void onSuccess(FormRPC result) {
                load(result);
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
        Data[] args = new Data[] {key, rpc.getField("reportToBillTo")};

        screenService.getObject("loadReportToBillTo", args, new AsyncCallback<FormRPC>() {
            public void onSuccess(FormRPC result) {
                if(result != null){
                    load(result);
                    rpc.setField("reportToBillTo",result);

                }
                window.setStatus("","");
            }

            public void onFailure(Throwable caught) {
                Window.alert(caught.getMessage());
                window.setStatus("","");
            }
        });
    }
    
    private boolean tableRowEmpty(DataSet row){
        boolean empty = true;
        
        for(int i=0; i<row.size(); i++){
            if(row.get(i).getValue() != null && !"".equals(row.get(i).getValue())){
                empty = false;
                break;
            }
        }
        
        return empty;
    }
}