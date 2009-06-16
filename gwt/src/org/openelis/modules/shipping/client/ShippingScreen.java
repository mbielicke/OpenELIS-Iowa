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
package org.openelis.modules.shipping.client;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.SourcesTabEvents;
import com.google.gwt.user.client.ui.TabListener;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

import org.openelis.gwt.common.Query;
import org.openelis.gwt.common.data.DataObject;
import org.openelis.gwt.common.data.DropDownField;
import org.openelis.gwt.common.data.FieldType;
import org.openelis.gwt.common.data.IntegerObject;
import org.openelis.gwt.common.data.KeyListManager;
import org.openelis.gwt.common.data.StringObject;
import org.openelis.gwt.common.data.TableDataModel;
import org.openelis.gwt.common.data.TableDataRow;
import org.openelis.gwt.event.CommandListener;
import org.openelis.gwt.screen.CommandChain;
import org.openelis.gwt.screen.ScreenDropDownWidget;
import org.openelis.gwt.screen.ScreenTabPanel;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.AutoComplete;
import org.openelis.gwt.widget.ButtonPanel;
import org.openelis.gwt.widget.Dropdown;
import org.openelis.gwt.widget.table.TableManager;
import org.openelis.gwt.widget.table.TableWidget;
import org.openelis.metamap.ShippingMetaMap;
import org.openelis.modules.fillOrder.client.FillOrderScreen;
import org.openelis.modules.main.client.OpenELISScreenForm;

public class ShippingScreen extends OpenELISScreenForm<ShippingForm, Query<TableDataRow<Integer>>> implements ClickListener, TableManager, ChangeListener, TabListener{

    public enum Action {Commited, Aborted}
    private CommandListener target;
    private Integer shipFromId, shipToId;
    private String shipToText, multUnitText, streetAddressText, cityText, stateText, zipCodeText;
    private AppButton removeRowButton;
    private TextBox shippedToAptSuite, shippedToAddress, shippedToCity, shippedToState, shippedToZipCode;
    private AutoComplete shippedToDropdown;
    private TableWidget itemsTable, trackingNumbersTable;
    private Dropdown status, shippedFrom, shippedMethod;
    private TableDataModel<TableDataRow<Integer>> itemsShippedModel, checkedOrderIds;
    private ShippingDataService data;
    private ScreenTabPanel tabPanel;
    private boolean closeOnCommitAbort = false;
    
    private ShippingMetaMap ShippingMeta = new ShippingMetaMap();
    private CommandListener listener;
    private KeyListManager keyList = new KeyListManager();
    
    AsyncCallback<ShippingForm> checkModels = new AsyncCallback<ShippingForm>() {
        public void onSuccess(ShippingForm rpc) {
            if(rpc.shippedStatus != null) {
                setStatusIdModel(rpc.shippedStatus);
                rpc.shippedStatus = null;
            }
            if(rpc.shippedFrom != null) {
                setShippedFromModel(rpc.shippedFrom);
                rpc.shippedFrom = null;
            }
            if(rpc.shippedMethod != null) {
                setShippedMethodModel(rpc.shippedMethod);
                rpc.shippedMethod = null;
            }
        }
        
        public void onFailure(Throwable caught) {
            
        }
    };
    
    public ShippingScreen() {                
        super("org.openelis.modules.shipping.server.ShippingService");
        query = new Query<TableDataRow<Integer>>();
        getScreen(new ShippingForm());
    }
    
    public void setShippingData(ShippingDataService data){
        this.data = data;
        setCloseOnCommitAbort(true);
    }
    
    public void setTarget(CommandListener target){
        this.target = target;
    }
    
    private void setCloseOnCommitAbort(boolean close){
        closeOnCommitAbort = close;
    }
    
    public void loadShippingScreenFromData(){
        this.shipFromId = data.getShipFromId();
        this.shipToId = data.getShipToId();
        this.shipToText = data.getShipToText();   
        this.multUnitText = data.getMultUnitText();
        this.streetAddressText = data.getStreetAddressText();
        this.cityText = data.getCityText();
        this.stateText = data.getStateText();
        this.zipCodeText = data.getZipCodeText();
        this.itemsShippedModel = data.getItemsShippedModel();

    }
    
    /*
    public void clearShippingData(){
        this.shipFromId = null;
        this.shipToId = null;
        this.shipToText = null;   
        this.multUnitText = null;
        this.streetAddressText = null;
        this.cityText = null;
        this.stateText = null;
        this.zipCodeText = null;
        this.itemsShippedModel = null;
    }
    */
    
    public void onClick(Widget sender) {
        if (sender == removeRowButton)
            onRemoveRowButtonClick();
    }
    
    public void onChange(Widget sender) {
        super.onChange(sender);
        
        if(sender == shippedToDropdown){
            if(shippedToDropdown.getSelections().size() > 0){
                TableDataRow selectedRow = shippedToDropdown.getSelections().get(0);
                
                //load address
                shippedToAddress.setText((String)((StringObject)selectedRow.getCells().get(1)).getValue());
                //load city
                shippedToCity.setText((String)((StringObject)selectedRow.getCells().get(2)).getValue());
                //load state
                shippedToState.setText((String)((StringObject)selectedRow.getCells().get(3)).getValue());
                
                ShippingShipToKey hiddenData = (ShippingShipToKey)selectedRow.getData();
                //load apt/suite
                shippedToAptSuite.setText(hiddenData.aptSuite);
                //load zipcode
                shippedToZipCode.setText(hiddenData.zipCode);
            }else{
                shippedToAddress.setText("");
                shippedToCity.setText("");
                shippedToState.setText("");
                shippedToAptSuite.setText("");
                shippedToZipCode.setText("");
            }
        }   
    }
    
    public void afterDraw(boolean success) {
        //shipped to address fields
        shippedToAptSuite  = (TextBox)getWidget(ShippingMeta.ORGANIZATION_META.ADDRESS.getMultipleUnit());
        shippedToAddress = (TextBox)getWidget(ShippingMeta.ORGANIZATION_META.ADDRESS.getStreetAddress());
        shippedToCity = (TextBox)getWidget(ShippingMeta.ORGANIZATION_META.ADDRESS.getCity());
        shippedToState = (TextBox)getWidget(ShippingMeta.ORGANIZATION_META.ADDRESS.getState());
        shippedToZipCode = (TextBox)getWidget(ShippingMeta.ORGANIZATION_META.ADDRESS.getZipCode());
        
        shippedToDropdown = (AutoComplete)getWidget(ShippingMeta.ORGANIZATION_META.getName());

        removeRowButton = (AppButton)getWidget("removeRowButton");
        
        itemsTable = (TableWidget)getWidget("itemsTable");
        itemsTable.model.enableAutoAdd(false);
        
        trackingNumbersTable = (TableWidget)getWidget("trackingNumbersTable");
        trackingNumbersTable.model.enableAutoAdd(false);

        tabPanel = (ScreenTabPanel)widgets.get("shippingTabPanel");
        
        ButtonPanel bpanel = (ButtonPanel)getWidget("buttons");
        
        CommandChain chain = new CommandChain();
        chain.addCommand(this);
        chain.addCommand(keyList);
        chain.addCommand(bpanel);
        
        status = (Dropdown)getWidget(ShippingMeta.getStatusId()); 
        shippedFrom = (Dropdown)getWidget(ShippingMeta.getShippedFromId());
        shippedMethod = (Dropdown)getWidget(ShippingMeta.getShippedMethodId());
        
        setStatusIdModel(form.shippedStatus);
        setShippedFromModel(form.shippedFrom);
        setShippedMethodModel(form.shippedMethod);
        
        /*
         * Null out the rpc models so they are not sent with future rpc calls
         */
        form.shippedStatus = null;
        form.shippedFrom = null;
        form.shippedMethod = null;
        
        updateChain.add(0,checkModels);
        fetchChain.add(0,checkModels);
        abortChain.add(0,checkModels);
        deleteChain.add(0,checkModels);
        commitUpdateChain.add(0,checkModels);
        commitAddChain.add(0,checkModels);
        commitAddChain.add(afterCommit);
        
        super.afterDraw(success);
        
        if(data != null)
            add();
    }
    
    protected AsyncCallback afterCommit = new AsyncCallback() {
        public void onFailure(Throwable caught) {   
        }
        public void onSuccess(Object result) {
            trackingNumbersTable.model.enableAutoAdd(false);
            
            if(target != null)
                target.performCommand(Action.Commited, this);
            
            if(closeOnCommitAbort)
                window.close();
        }
    };
    
    public void add() {
       super.add();
       
       window.setBusy();
       
       FieldType[] args = new FieldType[0]; 
         
       //ShippingRPC srpc = new ShippingRPC();
       //srpc.key = form.key;
       //srpc.form = form.form;
       
       
       screenService.call("getAddAutoFillValues", form, new AsyncCallback<ShippingForm>(){
           public void onSuccess(ShippingForm result){    
               //set the values in the rpc
               form.statusId.setValue(result.statusId.getValue());
               form.processedDate.setValue(result.processedDate.getValue());
               form.processedBy.setValue(result.processedBy.getValue());
               form.systemUserId = result.systemUserId;
               //rpc.form.setFieldValue(ShippingMeta.getProcessedDate(), (DatetimeRPC)((DateField)set.get(1)).getValue());
               //rpc.form.setFieldValue(ShippingMeta.getProcessedById(), (String)((StringField)set.get(2)).getValue());
               //rpc.form.setFieldValue("systemUserId", ((NumberField)set.get(3)).getValue());
                      
               if(data != null)
                   initScreen();
               
               loadScreen();
               
               trackingNumbersTable.model.enableAutoAdd(true);
               
               window.clearStatus();
           }
           
           public void onFailure(Throwable caught){
               Window.alert(caught.getMessage());
           }
       }); 
       
       ((ScreenDropDownWidget)widgets.get(ShippingMeta.getStatusId())).setFocus(true);
    }
    
    
    
    public void initScreen(){
        //add();
        
        loadShippingScreenFromData();
        
        //set the values after the screen is in add mode
        form.shippedFromId.setValue(new TableDataRow<Integer>(shipFromId));
        
        if(shipToId != null){
            TableDataModel<TableDataRow<Integer>> shipToModel = new TableDataModel<TableDataRow<Integer>>();
            shipToModel.add(new TableDataRow<Integer>(shipToId,new StringObject(shipToText)));
            form.organization.setModel(shipToModel);
            form.organization.setValue(shipToModel.get(0));
        }
        
        form.multipleUnit.setValue(multUnitText);
        form.streetAddress.setValue(streetAddressText);
        form.city.setValue(cityText);
        form.state.setValue(stateText);
        form.zipcode.setValue(zipCodeText);
        loadItemsShippedTableFromModel(itemsShippedModel);
        form.numberOfPackages.setValue(new Integer(1));
        data = null;
    }
    
    public void query() {
        super.query();
        removeRowButton.changeState(AppButton.ButtonState.DISABLED);
        status.setFocus(true);
    }
    
    public void update() {
        super.update();
        ((ScreenDropDownWidget)widgets.get(ShippingMeta.getStatusId())).setFocus(true);
        trackingNumbersTable.model.enableAutoAdd(true);
    }
    
    
    public void abort() {
        trackingNumbersTable.model.enableAutoAdd(false);
        super.abort();
        
        if(target != null)
            target.performCommand(Action.Aborted, this);
        
        if(closeOnCommitAbort)
            window.close();
    }
    
    
    //
    //start table manager methods
    //
    public boolean canAdd(TableWidget widget, TableDataRow set, int row) {
        return false;
    }

    public boolean canAutoAdd(TableWidget widget, TableDataRow addRow) {
        return ((DataObject)addRow.getCells().get(0)).getValue() != null && !((DataObject)addRow.getCells().get(0)).getValue().equals(0);
    }

    public boolean canDelete(TableWidget widget, TableDataRow set, int row) {
        return false;
    }

    public boolean canEdit(TableWidget widget, TableDataRow set, int row, int col) {
        return true;
    }

    public boolean canSelect(TableWidget widget, TableDataRow set, int row) {
        if(state == State.ADD || state == State.UPDATE)           
            return true;
        return false;
    }
    
    //
    //end table manager methods
    //
    
    private void loadItemsShippedTableFromModel(TableDataModel<TableDataRow<Integer>> model){
        itemsTable.model.clear();
        for(int i=0; i<model.size(); i++){
            TableDataRow<Integer> set = model.get(i);
            
            TableDataRow<Integer> tableRow = itemsTable.model.createRow();
            
            tableRow.getCells().get(0).setValue(set.getCells().get(0).getValue());
            tableRow.getCells().get(1).setValue(((DropDownField)set.getCells().get(1)).getTextValue());
            
            tableRow.setData(set.getData());
            
            itemsTable.model.addRow(tableRow);
        }
        
        if(model.size() > 0)
            itemsTable.model.refresh();
    }
 
    private void onRemoveRowButtonClick() {
        int selectedRow = trackingNumbersTable.model.getSelectedIndex();
        
        if (selectedRow > -1 && trackingNumbersTable.model.numRows() > 0) 
            trackingNumbersTable.model.deleteRow(selectedRow);
        
    }

    public boolean onBeforeTabSelected(SourcesTabEvents sender, int tabIndex) {
        if(state != State.QUERY){
            if (tabIndex == 0 && !form.shippingItemsForm.load)
                fillShippingItems();
                
            else if (tabIndex == 1 && !form.shippingNotesForm.load) 
                fillOrderShippingNotes();
              
        }
        return true;
    }

    public void onTabSelected(SourcesTabEvents sender, int tabIndex) { 
        form.shippingTabPanel = tabPanel.getSelectedTabKey();
    }
    
    private void fillShippingItems() {
        if(form.entityKey == null)
            return;
        
        window.setBusy();
        
        //ShippingItemsForm sirpc = new ShippingItemsRPC();
        form.shippingItemsForm.entityKey = form.entityKey;
        //sirpc.form =form.form.shippingItemsForm;

        screenService.call("loadShippingItems", form.shippingItemsForm, new AsyncCallback<ShippingItemsForm>() {
            public void onSuccess(ShippingItemsForm result) {
                form.shippingItemsForm = result;
                load(form.shippingItemsForm);

                window.clearStatus();
            }

            public void onFailure(Throwable caught) {
                Window.alert(caught.getMessage());
                window.clearStatus();
            }
        });
    }
    
    private void fillOrderShippingNotes() {
        if(form.entityKey == null)
            return;
        
        window.setBusy();

        // prepare the argument list for the getObject function
        //ShippingNotesRPC snrpc = new ShippingNotesRPC();
        form.shippingNotesForm.entityKey = form.entityKey;
        //snrpc.form = form.form.shippingNotesForm;
        
        screenService.call("loadOrderShippingNotes", form.shippingNotesForm, new AsyncCallback<ShippingNotesForm>() {
            public void onSuccess(ShippingNotesForm result) {
                form.shippingNotesForm = result;
                load(form.shippingNotesForm);
                
                window.clearStatus();
            }

            public void onFailure(Throwable caught) {
                Window.alert(caught.getMessage());
                window.clearStatus();
            }
        });
    }

    public void setStatusIdModel(TableDataModel<TableDataRow<Integer>> statusIdsModel) {
        status.setModel(statusIdsModel);
    }
    
    public void setShippedFromModel(TableDataModel<TableDataRow<Integer>> shippedFromsModel) {
        shippedFrom.setModel(shippedFromsModel);
    }
    
    public void setShippedMethodModel(TableDataModel<TableDataRow<Integer>> shippedMethodsModel) {
        shippedMethod.setModel(shippedMethodsModel);
    }
}