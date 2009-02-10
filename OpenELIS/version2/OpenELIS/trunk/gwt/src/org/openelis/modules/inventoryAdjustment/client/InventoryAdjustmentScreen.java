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
package org.openelis.modules.inventoryAdjustment.client;

import java.util.ArrayList;

import org.openelis.gwt.common.DatetimeRPC;
import org.openelis.gwt.common.Form;
import org.openelis.gwt.common.FormErrorException;
import org.openelis.gwt.common.RPC;
import org.openelis.gwt.common.data.Data;
import org.openelis.gwt.common.data.DataMap;
import org.openelis.gwt.common.data.DataModel;
import org.openelis.gwt.common.data.DataSet;
import org.openelis.gwt.common.data.DateField;
import org.openelis.gwt.common.data.DropDownField;
import org.openelis.gwt.common.data.KeyListManager;
import org.openelis.gwt.common.data.NumberField;
import org.openelis.gwt.common.data.NumberObject;
import org.openelis.gwt.common.data.StringField;
import org.openelis.gwt.common.data.StringObject;
import org.openelis.gwt.screen.CommandChain;
import org.openelis.gwt.screen.ScreenCalendar;
import org.openelis.gwt.screen.ScreenDropDownWidget;
import org.openelis.gwt.screen.ScreenTextBox;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.AutoComplete;
import org.openelis.gwt.widget.AutoCompleteCallInt;
import org.openelis.gwt.widget.ButtonPanel;
import org.openelis.gwt.widget.Dropdown;
import org.openelis.gwt.widget.DropdownWidget;
import org.openelis.gwt.widget.FormInt;
import org.openelis.gwt.widget.table.TableManager;
import org.openelis.gwt.widget.table.TableModel;
import org.openelis.gwt.widget.table.TableWidget;
import org.openelis.gwt.widget.table.event.SourcesTableWidgetEvents;
import org.openelis.gwt.widget.table.event.TableWidgetListener;
import org.openelis.metamap.InventoryAdjustmentMetaMap;
import org.openelis.modules.main.client.OpenELISScreenForm;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Widget;

public class InventoryAdjustmentScreen extends OpenELISScreenForm<RPC<Form,Data>,Form> implements TableManager, TableWidgetListener, ClickListener, AutoCompleteCallInt {

    private TableWidget        adjustmentsTable;
    private AppButton        removeRowButton;
    private static boolean loaded = false;
    
    private ScreenTextBox   idText, userText, descText;
    private static DataModel storesDropdownModel;
    private ScreenDropDownWidget storesDropdown;
    private ScreenCalendar adjustmentDateText;
    private KeyListManager   keyList = new KeyListManager();
    
    private InventoryAdjustmentMetaMap InventoryAdjustmentMeta = new InventoryAdjustmentMetaMap();
    private static String storeIdKey;
    
    public InventoryAdjustmentScreen() {
        super("org.openelis.modules.inventoryAdjustment.server.InventoryAdjustmentService",!loaded, new RPC<Form,Data>());
    }

    public void onClick(Widget sender) {
        if (sender == removeRowButton)
            onRemoveRowButtonClick();
    }
    
/*    public void onChange(Widget sender) {
        if(sender == storesDropdown.getWidget()){
            Dropdown storeD = (Dropdown)storesDropdown.getWidget();
            form.setFieldValue(storeIdKey, storeD.getSelections());
           
        }else
            super.onChange(sender);
    }
    */
    
    public void afterDraw(boolean sucess) {
        Dropdown drop;
        loaded = true;
        
        removeRowButton = (AppButton)getWidget("removeRowButton");
        adjustmentsTable = (TableWidget)getWidget("adjustmentsTable");
        adjustmentsTable.addTableWidgetListener(this);
        adjustmentsTable.model.enableAutoAdd(false);
        
        storeIdKey = InventoryAdjustmentMeta.TRANS_ADJUSTMENT_LOCATION_META.INVENTORY_LOCATION_META.INVENTORY_ITEM_META.getStoreId();
        
        idText = (ScreenTextBox) widgets.get(InventoryAdjustmentMeta.getId());
        adjustmentDateText = (ScreenCalendar) widgets.get(InventoryAdjustmentMeta.getAdjustmentDate());
        userText = (ScreenTextBox) widgets.get(InventoryAdjustmentMeta.getSystemUserId());
        storesDropdown = (ScreenDropDownWidget) widgets.get(storeIdKey);
        descText = (ScreenTextBox) widgets.get(InventoryAdjustmentMeta.getDescription());
        
        if (storesDropdownModel == null) 
            storesDropdownModel = (DataModel)initData.get("stores");
         
        drop = (Dropdown)storesDropdown.getWidget();
        drop.setModel(storesDropdownModel);
        
        ButtonPanel bpanel = (ButtonPanel)getWidget("buttons");
        
        CommandChain chain = new CommandChain();
        chain.addCommand(this);
        chain.addCommand(keyList);
        chain.addCommand(bpanel);
        
        updateChain.add(afterUpdate);
        commitAddChain.add(afterCommitAdd);
        commitUpdateChain.add(afterCommitUpdate);
        
        super.afterDraw(sucess);
        
        form.setFieldValue("adjustmentsTable", adjustmentsTable.model.getData());
        
    }
    
    public void add() {
        //
        // make sure the contact table gets set before the main add
        //
        adjustmentsTable.model.enableAutoAdd(true);
        super.add();
        
        idText.enable(false);
        adjustmentDateText.enable(false);
        userText.enable(false);
        
        Data[] args = new Data[0]; 
        
        screenService.getObject("getAddAutoFillValues", args, new AsyncCallback<DataModel<DataSet>>(){
            public void onSuccess(DataModel<DataSet> model){    
              // get the datamodel, load the fields in the form
                DataSet set = model.get(0);

                //load the values
                adjustmentDateText.load((DateField)set.get(2));
                userText.load((StringField)set.get(0));
                
                //set the values in the rpc
                form.setFieldValue(InventoryAdjustmentMeta.getSystemUserId(), (String)((StringField)set.get(0)).getValue());
                form.setFieldValue(InventoryAdjustmentMeta.getAdjustmentDate(), (DatetimeRPC)((DateField)set.get(2)).getValue());
                form.setFieldValue("systemUserId", (Integer)((NumberField)set.get(1)).getValue());
                descText.setFocus(true);
                window.setStatus("","");
            }
            
            public void onFailure(Throwable caught){
                Window.alert(caught.getMessage());
            }
        }); 
    }
    
    protected AsyncCallback afterUpdate = new AsyncCallback() {
        public void onSuccess(Object result){
            idText.enable(false);
            adjustmentDateText.enable(false);
            userText.enable(false);
            storesDropdown.enable(false);
            descText.setFocus(true);
            removeRowButton.changeState(AppButton.ButtonState.DISABLED);
        }
        
        public void onFailure(Throwable caught){
            
        }
    };
    
    public void abort() {
        adjustmentsTable.model.enableAutoAdd(false);
        super.abort();
    }
    
    public void query() {
        super.query();
        userText.enable(false);
        idText.setFocus(true);
    }
    
    protected AsyncCallback afterCommitUpdate = new AsyncCallback() {
        public void onSuccess(Object result){
            adjustmentsTable.model.enableAutoAdd(false);
        }
        
        public void onFailure(Throwable caught){
            
        }
  };
    
    protected AsyncCallback afterCommitAdd = new AsyncCallback() {
      public void onFailure(Throwable caught) {
          
      }
      public void onSuccess(Object result) {
          adjustmentsTable.model.enableAutoAdd(false);
      }   
  };
    
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
        int numRows = adjustmentsTable.model.numRows();
        if(state == FormInt.State.UPDATE && (col == 0 || col == 1) && row > -1 && numRows > 0 && row < numRows)
            return false;
        
       return true;
    }

    public boolean canSelect(TableWidget widget, DataSet set, int row) {
        if(state == FormInt.State.ADD || state == FormInt.State.UPDATE)           
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
    
    //
    //start table listener methods
    //
    public void finishedEditing(SourcesTableWidgetEvents sender, final int row, int col) {
        if(row >= adjustmentsTable.model.numRows())
            return;
        final DataSet tableRow = adjustmentsTable.model.getRow(row);
        final DropDownField invItemField = (DropDownField)tableRow.get(1);
        switch (col){
            case 0:
                form.setFieldValue(storeIdKey, ((Dropdown)storesDropdown.getWidget()).getSelections());
                if(form.getFieldValue(storeIdKey) == null){
                    window.setStatus(consts.get("inventoryAdjLocAutoException"),"ErrorPanel");
                    return;
                }
                
                final NumberObject locIdObj = new NumberObject((Integer)adjustmentsTable.model.getCell(row, 0));
                NumberObject storeIdObj = new NumberObject((Integer)form.getFieldValue(storeIdKey));
                
                
                //we need to make sure the location id isnt already in the table
                if(locationIdAlreadyExists((Integer)locIdObj.getValue(), row)){
                    ((NumberField)tableRow.get(0)).clearErrors();
                    adjustmentsTable.model.setCellError(row, 0, consts.get("fieldUniqueException"));
                    return;
                }
                
                window.setStatus("","spinnerIcon");
                
                // prepare the argument list for the getObject function
                Data[] args = new Data[] {locIdObj, storeIdObj}; 
                
                screenService.getObject("getInventoryItemInformation", args, new AsyncCallback<DataModel<DataSet>>(){
                    public void onSuccess(DataModel<DataSet> model){    
                        if(row < adjustmentsTable.model.numRows()){
                        Integer currentId = (Integer)adjustmentsTable.model.getCell(row, 0);
                        Integer oldId = (Integer)locIdObj.getValue();
                        
                        //make sure the row hasnt been deleted and it still has the same values
                        if(currentId.equals(oldId)){
                          if(model.size() > 0){
                              DataSet set = model.get(0);
                                    
                              invItemField.setModel(((DropDownField)set.get(1)).getModel());
                              adjustmentsTable.model.setCell(row, 1, ((DropDownField)set.get(1)).getModel().get(0));
                              adjustmentsTable.model.setCell(row, 2, ((StringField)set.get(2)).getValue());
                              adjustmentsTable.model.setCell(row, 3, ((NumberField)set.get(3)).getValue());
                          }
                        }
                        }
                      window.setStatus("","");
                    }
                    
                    public void onFailure(Throwable caught){
                        Window.alert(caught.getMessage());
                        window.setStatus("","");
                    }
                });
                break;
            case 1:
                ArrayList selections = invItemField.getSelections();
      
                if(selections.size() > 0){
                    DataSet selectedRow = (DataSet)selections.get(0);
      
                    if(selectedRow.size() > 1){
                        //we need to make sure this inventory item isnt already in the table
                        if(locationIdAlreadyExists((Integer)((NumberObject)selectedRow.getData()).getValue(), row)){
                            ((DropDownField)tableRow.get(1)).clearErrors();
                            adjustmentsTable.model.setCellError(row, 1, consts.get("fieldUniqueException"));
                         
                        }else{
                            adjustmentsTable.model.setCell(row, 0, ((NumberObject)selectedRow.getData()).getValue());
                            adjustmentsTable.model.setCell(row, 2, ((StringObject)selectedRow.get(2)).getValue());
                            adjustmentsTable.model.setCell(row, 3, ((NumberObject)selectedRow.get(5)).getValue());
                            
                        }
                    }    
                }

                break;
            case 4:
                Integer qtyOnHand = null;
                Integer physicalCount = null;
                Integer adjQty = null;
                
                qtyOnHand = (Integer)((NumberField)tableRow.get(3)).getValue();
                physicalCount = (Integer)((NumberField)tableRow.get(4)).getValue();
                
                if(qtyOnHand != null && physicalCount != null){
                    adjQty = physicalCount - qtyOnHand;
                    
                    adjustmentsTable.model.setCell(row, 5, adjQty);
                }
                break;
        }
    }

    public void startEditing(SourcesTableWidgetEvents sender, int row, int col) {}

    public void stopEditing(SourcesTableWidgetEvents sender, int row, int col) {}
    //
    //end table listener methods
    //
    
    private boolean tableRowEmpty(DataSet<Data> row){
        boolean empty = true;
        
        for(int i=0; i<row.size(); i++){
            if(row.get(i).getValue() != null && !"".equals(row.get(i).getValue())){
                empty = false;
                break;
            }
        }
        
        return empty;
    }
    
    private void onRemoveRowButtonClick() {
        int selectedRow = adjustmentsTable.model.getSelectedIndex();
        if (selectedRow > -1 && adjustmentsTable.model.numRows() > 0) {
            adjustmentsTable.model.deleteRow(selectedRow);

        }
    }
    
    private boolean locationIdAlreadyExists(Integer locationId, int currentRow){
        boolean exists = false;
        
        if(locationId != null){        
            TableModel model = (TableModel)adjustmentsTable.model;
            
            for(int i=0; i<model.numRows(); i++){
                if(i != currentRow && locationId.equals(model.getCell(i, 0))){
                    exists = true;
                    break;
                }            
            }
        }
        
        return exists;
    }

    public void callForMatches(final AutoComplete widget, DataModel model, String text) {
        StringObject catObj = new StringObject(widget.cat);
        StringObject matchObj = new StringObject(text);
        DataMap paramsObj = new DataMap();
        form.setFieldValue(storeIdKey, ((Dropdown)storesDropdown.getWidget()).getSelections());

        paramsObj.put("storeId", form.getField(storeIdKey));
        
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
}
