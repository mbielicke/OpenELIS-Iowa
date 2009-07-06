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

import org.openelis.cache.DictionaryCache;
import org.openelis.domain.DictionaryDO;
import org.openelis.gwt.common.data.DropDownField;
import org.openelis.gwt.common.data.KeyListManager;
import org.openelis.gwt.common.data.StringField;
import org.openelis.gwt.common.data.StringObject;
import org.openelis.gwt.common.data.TableDataModel;
import org.openelis.gwt.common.data.TableDataRow;
import org.openelis.gwt.screen.CommandChain;
import org.openelis.gwt.screen.ScreenDropDownWidget;
import org.openelis.gwt.screen.ScreenTabPanel;
import org.openelis.gwt.screen.ScreenTextArea;
import org.openelis.gwt.screen.ScreenTextBox;
import org.openelis.gwt.screen.ScreenWindow;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.AutoComplete;
import org.openelis.gwt.widget.ButtonPanel;
import org.openelis.gwt.widget.Dropdown;
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
import com.google.gwt.user.client.rpc.SyncCallback;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SourcesTabEvents;
import com.google.gwt.user.client.ui.TabListener;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class OrderScreen extends OpenELISScreenForm<OrderForm,OrderQuery> implements TableManager, TableWidgetListener, ClickListener, TabListener, ChangeListener {
    
    private AutoComplete orgDropdown, billToDropdown, reportToDropdown;
    
    private AppButton        removeItemButton, standardNoteCustomerButton, standardNoteShippingButton;
    
    private ScreenTextBox          orderNum, neededInDays, orderDate, requestedBy;
    private TextBox orgAptSuite, orgAddress, orgCity, orgState, orgZipCode,
                    reportToAptSuite, reportToAddress, reportToCity, reportToState, reportToZipCode,
                    billToAptSuite, billToAddress, billToCity, billToState, billToZipCode;
    
    private KeyListManager keyList = new KeyListManager();
    
    private ScreenTextArea   shippingNoteText, customerNoteText;
    
    private Dropdown status, shipFrom, costCenter;
    
    private TableWidget itemsTable, receiptsTable;
    private ScreenTabPanel tabPanel;
    private String orderType;
    
    private OrderMetaMap OrderMeta = new OrderMetaMap();
    
    public OrderScreen(Object[] args) {                
        super("org.openelis.modules.order.server.OrderService");
        
        orderType = (String)((StringObject)args[0]).getValue();
      
        OrderForm orderRPC = new OrderForm();
        orderRPC.orderType = orderType;
        query = new OrderQuery();
        query.type = orderType;
        
        
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
                TableDataRow<Integer> selectedRow = orgDropdown.getSelections().get(0);
                OrderOrgKey selectedKey = (OrderOrgKey)selectedRow.getData();
                
                //load address
                orgAddress.setText((String)((StringObject)selectedRow.cells[1]).getValue());
                //load city
                orgCity.setText((String)((StringObject)selectedRow.cells[2]).getValue());
                //load state
                orgState.setText((String)((StringObject)selectedRow.cells[3]).getValue());               
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
                TableDataRow<Integer> selectedRow = (TableDataRow<Integer>)reportToDropdown.getSelections().get(0);
                OrderOrgKey selectedKey = (OrderOrgKey)selectedRow.getData();
                
                //load address
                reportToAddress.setText((String)((StringObject)selectedRow.cells[1]).getValue());
                //load city
                reportToCity.setText((String)((StringObject)selectedRow.cells[2]).getValue());
                //load state
                reportToState.setText((String)((StringObject)selectedRow.cells[3]).getValue());               
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
                TableDataRow<Integer> selectedRow = (TableDataRow<Integer>)billToDropdown.getSelections().get(0);
                OrderOrgKey selectedKey = (OrderOrgKey)selectedRow.getData();
                
                //load address
                billToAddress.setText((String)((StringObject)selectedRow.cells[1]).getValue());
                //load city
                billToCity.setText((String)((StringObject)selectedRow.cells[2]).getValue());
                //load state
                billToState.setText((String)((StringObject)selectedRow.cells[3]).getValue());               
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
        
        //AToZTable atozTable = (AToZTable) getWidget("azTable");
        //ButtonPanel atozButtons = (ButtonPanel)getWidget("atozButtons");
        ButtonPanel bpanel = (ButtonPanel)getWidget("buttons");
        
        CommandChain chain = new CommandChain();
        chain.addCommand(this);
        chain.addCommand(keyList);
        chain.addCommand(bpanel);
        //chain.addCommand(atozTable);
        //chain.addCommand(atozButtons);
        
        itemsTable = (TableWidget)getWidget("itemsTable");
        itemsTable.addTableWidgetListener(this);
        itemsTable.model.enableAutoAdd(false);
        
        receiptsTable = (TableWidget)getWidget("receiptsTable");
        receiptsTable.model.enableAutoAdd(false);
        
        status = (Dropdown)getWidget(OrderMeta.getStatusId());
        costCenter = (Dropdown)getWidget(OrderMeta.getCostCenterId());
        shipFrom = (Dropdown)getWidget(OrderMeta.getShipFromId());
        
        tabPanel = (ScreenTabPanel)widgets.get("orderTabPanel");
        
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
        
        //set order type in display and query rpcs
        form.type = orderType;
        
        updateChain.add(afterUpdate);
        
        super.afterDraw(sucess);
        
        ArrayList cache;
        TableDataModel<TableDataRow> model;
        cache = DictionaryCache.getListByCategorySystemName("cost_centers");
        model = getDictionaryIdEntryList(cache);
        costCenter.setModel(model);
        
        cache = DictionaryCache.getListByCategorySystemName("order_status");
        model = getDictionaryIdEntryList(cache);
        status.setModel(model);
        
        if(shipFrom != null){
            cache = DictionaryCache.getListByCategorySystemName("shipFrom");
            model = getDictionaryIdEntryList(cache);
            shipFrom.setModel(model);
        }
        
    }
    
    public void update() {
        form.originalStatus = (Integer)((TableDataRow<Integer>)status.getSelections().get(0)).key;
        super.update();
        
    }
    
    public void query() {
        super.query();
        
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

        //Order orpc = new OrderRPC();
        //orpc.key = form.key;
        //orpc.form = form.form;
          
        screenService.call("getAddAutoFillValues", form, new AsyncCallback<OrderForm>(){
            public void onSuccess(OrderForm returnRpc){    
                //load the values
                ((ScreenDropDownWidget)widgets.get(OrderMeta.getStatusId())).load(returnRpc.statusId);
                orderDate.load(returnRpc.orderedDate);
                requestedBy.load(returnRpc.requestedBy);
                
                //set the values in the rpc
                form.statusId.setValue(returnRpc.statusId.getValue());
                form.orderedDate.setValue(returnRpc.orderedDate.getValue());
                form.requestedBy.setValue(returnRpc.requestedBy.getValue());
                
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
        
    protected SyncCallback afterUpdate = new SyncCallback() {
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
        if(state != State.QUERY){
            if (index == 0 && !form.items.load) {
                fillItemsModel(false);
                
            }else if( index == 1 && !form.receipts.load) {
                fillReceipts();
                
            } else if (index == 2 && "kits".equals(orderType) && !form.customerNotes.load) {
                fillCustomerNotes();
                
            } else if ((index == 2 && !"kits".equals(orderType) && !form.shippingNotes.load)) {
                fillShippingNotes();
            
            } else if ((index == 3 && !form.shippingNotes.load)) {
                fillShippingNotes();
                
            } else if (index == 4 && !form.reportToBillTo.load) {
                fillReportToBillTo();
                
            }
        }
        return true;
    }

    public void onTabSelected(SourcesTabEvents sender, int tabIndex) {
        form.orderTabPanel = tabPanel.getSelectedTabKey();
    }

    //
    //start table manager methods
    //
    public boolean canAdd(TableWidget widget, TableDataRow set, int row) {
        return true;
    }

    public boolean canAutoAdd(TableWidget widget, TableDataRow addRow) {
        return !tableRowEmpty(addRow);
    }

    public boolean canDelete(TableWidget widget, TableDataRow set, int row) {
        return true;
    }

    public boolean canEdit(TableWidget widget, TableDataRow set, int row, int col) {
        return true;
    }

    public boolean canSelect(TableWidget widget, TableDataRow set, int row) {
        if(state == State.ADD || state == State.UPDATE || state == State.QUERY)           
            return true;
        return false;
    }
    
    //
    //end table manager methods
    //
    
    //
    //start table listener methods
    //
    public void finishedEditing(SourcesTableWidgetEvents sender, int row, int col) {
        if(col == 1 && row < itemsTable.model.numRows()){
            TableDataRow<Integer> tableRow = itemsTable.model.getRow(row);
            DropDownField<Integer> invItemField = (DropDownField)tableRow.cells[1];
            ArrayList selections = invItemField.getValue();
  
            if(selections.size() > 0){
                TableDataRow<Integer> selectedRow = (TableDataRow<Integer>)selections.get(0);
  
                if(selectedRow.size() > 1){
                    StringField storeLabel = new StringField();
                    //StringField locationLabel = new StringField();
                    //NumberField locationId = new NumberField(NumberObject.Type.INTEGER);
                    //NumberField qtyOnHand = new NumberField(NumberObject.Type.INTEGER);
                    //storeLabel.setValue((String)((StringObject)selectedRow.get(1)).getValue());

              
                    tableRow.cells[2].setValue((String)((StringObject)selectedRow.cells[1]).getValue());
                    
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
        screenService.call("getDuplicateRPC", form, new AsyncCallback<OrderForm>(){
            public void onSuccess(OrderForm result) {
                form = result;
                loadScreen();
                enable(true);
                setState(State.ADD);
                window.setDone(consts.get("enterInformationPressCommit"));
            }

            public void onFailure(Throwable caught) {
                handleError(caught);
                window.setDone("Load Failed");
                setState(State.DEFAULT);
                form.entityKey = null;
            }
        });
    }
    
    private void fillItemsModel(final boolean forDuplicate) {
        if(form.entityKey == null)
            return;
        
        window.setBusy();
        
        //prepare the argument list
        //OrderItemRPC oirpc = new OrderItemRPC();
        form.items.entityKey = form.entityKey;
        form.items.forDuplicate = forDuplicate;
        //oirpc.form = form.form.items;
        
        
        screenService.call("loadItems", form.items, new AsyncCallback<ItemsForm>() {
            public void onSuccess(ItemsForm result) {
                form.items = result;
                load(form.items);
                
                if(forDuplicate)
                    form.entityKey = null;
                
                window.clearStatus();
            }

            public void onFailure(Throwable caught) {
                Window.alert(caught.getMessage());
                window.clearStatus();
            }
        });
    }
    
    private void fillReceipts() {
        if(form.entityKey == null)
            return;
        
        window.setBusy();

      //prepare the argument list
        //OrderReceiptRPC orrpc = new OrderReceiptRPC();
        form.receipts.entityKey = form.entityKey;
        form.receipts.orderType = form.orderType;
        //orrpc.form = form.form.receipts;
        
        screenService.call("loadReceipts", form.receipts, new AsyncCallback<ReceiptForm>() {
            public void onSuccess(ReceiptForm result) {
                form.receipts = result;
                load(form.receipts);
                window.clearStatus();
            }

            public void onFailure(Throwable caught) {
                Window.alert(caught.getMessage());
                window.clearStatus();
            }
        });
    }
    
    private void fillCustomerNotes() {
        if(form.entityKey == null)
            return;
        
        window.setBusy();

      //prepare the argument list
        //OrderNoteRPC onrpc = new OrderNoteRPC();
        form.customerNotes.entityKey = form.entityKey;
        //onrpc.form = form.form.customerNotes;
        
        screenService.call("loadCustomerNotes", form.customerNotes, new AsyncCallback<OrderNoteForm>() {
            public void onSuccess(OrderNoteForm result) {
                form.customerNotes = result;
                load(form.customerNotes);
                window.clearStatus();
            }

            public void onFailure(Throwable caught) {
                Window.alert(caught.getMessage());
                window.clearStatus();
            }
        });
    }
    
    private void fillShippingNotes() {
        if(form.entityKey == null)
            return;
        
        window.setBusy();

     // prepare the argument list
        //OrderShippingNoteRPC osnrpc = new OrderShippingNoteRPC();
        form.shippingNotes.entityKey = form.entityKey;
        //osnrpc.form = form.form.shippingNotes;

        screenService.call("loadOrderShippingNotes", form.shippingNotes, new AsyncCallback<OrderShippingNoteForm>() {
            public void onSuccess(OrderShippingNoteForm result) {
                form.shippingNotes = result;
                load(form.shippingNotes);
                window.clearStatus();
            }

            public void onFailure(Throwable caught) {
                Window.alert(caught.getMessage());
                window.clearStatus();
            }
        });
    }
    
    private void fillReportToBillTo() {
        if(form.entityKey == null)
            return;
        
        window.setBusy();

        //ReportToBillToRpc rtbtrpc = new ReportToBillToRpc();
        form.reportToBillTo.entityKey = form.entityKey;
        //rtbtrpc.form = form.form.reportToBillTo;
        
        screenService.call("loadReportToBillTo", form.reportToBillTo, new AsyncCallback<ReportToBillToForm>() {
            public void onSuccess(ReportToBillToForm result) {
                if(result != null){
                    form.reportToBillTo = result;
                    load(result);
                }
                window.clearStatus();
            }

            public void onFailure(Throwable caught) {
                Window.alert(caught.getMessage());
                window.clearStatus();
            }
        });
    }
    
    private boolean tableRowEmpty(TableDataRow<Integer> row){
        boolean empty = true;
        
        for(int i=0; i<row.size(); i++){
            if(row.cells[i].getValue() != null && !"".equals(row.cells[i].getValue())){
                empty = false;
                break;
            }
        }
        
        return empty;
    }

    private TableDataModel<TableDataRow> getDictionaryIdEntryList(ArrayList list){
        if(list == null)
            return null;
        
        TableDataModel<TableDataRow> m = new TableDataModel<TableDataRow>();
        
        for(int i=0; i<list.size(); i++){
            TableDataRow<Integer> row = new TableDataRow<Integer>(1);
            DictionaryDO dictDO = (DictionaryDO)list.get(i);
            row.key = dictDO.getId();
            row.cells[0] = new StringObject(dictDO.getEntry());
            m.add(row);
        }
        
        return m;
    }
}