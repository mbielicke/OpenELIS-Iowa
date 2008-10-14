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
import org.openelis.gwt.common.FormRPC;
import org.openelis.gwt.common.data.DataModel;
import org.openelis.gwt.common.data.DataObject;
import org.openelis.gwt.common.data.DataSet;
import org.openelis.gwt.common.data.DateField;
import org.openelis.gwt.common.data.DropDownField;
import org.openelis.gwt.common.data.KeyListManager;
import org.openelis.gwt.common.data.ModelObject;
import org.openelis.gwt.common.data.NumberField;
import org.openelis.gwt.common.data.NumberObject;
import org.openelis.gwt.common.data.StringField;
import org.openelis.gwt.common.data.StringObject;
import org.openelis.gwt.screen.CommandChain;
import org.openelis.gwt.screen.ScreenCalendar;
import org.openelis.gwt.screen.ScreenDropDownWidget;
import org.openelis.gwt.screen.ScreenTextBox;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.ButtonPanel;
import org.openelis.gwt.widget.Dropdown;
import org.openelis.gwt.widget.FormInt;
import org.openelis.gwt.widget.table.TableLabel;
import org.openelis.gwt.widget.table.TableManager;
import org.openelis.gwt.widget.table.TableModel;
import org.openelis.gwt.widget.table.TableTextBox;
import org.openelis.gwt.widget.table.TableWidget;
import org.openelis.gwt.widget.table.event.SourcesTableWidgetEvents;
import org.openelis.gwt.widget.table.event.TableWidgetListener;
import org.openelis.metamap.InventoryAdjustmentMetaMap;
import org.openelis.modules.main.client.OpenELISScreenForm;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Widget;

public class InventoryAdjustmentScreen extends OpenELISScreenForm implements TableManager, TableWidgetListener, ClickListener {

    private TableWidget        adjustmentsTable;
    private boolean startedLoadingTable = false;
    private AppButton        removeRowButton;
    private static boolean loaded = false;
    
    private ScreenTextBox   idText, userText, descText;
    private static DataModel storesDropdownModel;
    private ScreenDropDownWidget storesDropdown;
    private ScreenCalendar adjustmentDateText;
    private KeyListManager   keyList = new KeyListManager();
    
    private InventoryAdjustmentMetaMap InventoryAdjustmentMeta = new InventoryAdjustmentMetaMap();
    
    public InventoryAdjustmentScreen() {
        super("org.openelis.modules.inventoryAdjustment.server.InventoryAdjustmentService",!loaded);
    }

    public void onClick(Widget sender) {
        if (sender == removeRowButton)
            onRemoveRowButtonClick();
    }
    
    public void afterDraw(boolean sucess) {
        Dropdown drop;
        loaded = true;
        
        removeRowButton = (AppButton)getWidget("removeRowButton");
        adjustmentsTable = (TableWidget)getWidget("adjustmentsTable");
        adjustmentsTable.addTableWidgetListener(this);
        adjustmentsTable.model.enableAutoAdd(false);
        
        idText = (ScreenTextBox) widgets.get(InventoryAdjustmentMeta.getId());
        adjustmentDateText = (ScreenCalendar) widgets.get(InventoryAdjustmentMeta.getAdjustmentDate());
        userText = (ScreenTextBox) widgets.get(InventoryAdjustmentMeta.getSystemUserId());
        storesDropdown = (ScreenDropDownWidget) widgets.get(InventoryAdjustmentMeta.TRANS_ADJUSTMENT_LOCATION_META.INVENTORY_LOCATION_META.INVENTORY_ITEM_META.getStoreId());
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
        
        super.afterDraw(sucess);
        
        rpc.setFieldValue("adjustmentsTable", adjustmentsTable.model.getData());
        
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
        
        DataObject[] args = new DataObject[0]; 
        
        screenService.getObject("getAddAutoFillValues", args, new AsyncCallback(){
            public void onSuccess(Object result){    
              // get the datamodel, load the fields in the form
                DataModel model = (DataModel) ((ModelObject)result).getValue();
                DataSet set = model.get(0);

                //load the values
                adjustmentDateText.load((DateField)set.get(2));
                userText.load((StringField)set.get(0));
                
                //set the values in the rpc
                rpc.setFieldValue(InventoryAdjustmentMeta.getSystemUserId(), (String)((StringField)set.get(0)).getValue());
                rpc.setFieldValue(InventoryAdjustmentMeta.getAdjustmentDate(), (DatetimeRPC)((DateField)set.get(2)).getValue());
                rpc.setFieldValue("systemUserId", (Integer)((NumberField)set.get(1)).getValue());
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
            adjustmentsTable.model.enableAutoAdd(true);
        }
        
        public void onFailure(Throwable caught){
            
        }
    };
    
    public void query() {
        super.query();
        userText.enable(false);
        idText.setFocus(true);
    }
    
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
         
        /*if(col == 5 && row > -1 && numRows > 0 && row < numRows){
            TableRow tableRow = controller.model.getRow(row);
            
            //if we dont have a location we dont want to allow the user to edit physical count yet
            if(((NumberField)tableRow.getColumn(0)).getValue() == null)
                return false;
               
        }*/
        
       return true;
    }

    public boolean canSelect(TableWidget widget, DataSet set, int row) {
        if(state == FormInt.State.ADD || state == FormInt.State.UPDATE)           
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
        /*if(col == 0 && !startedLoadingTable && row > -1 && row < adjustmentsTable.model.numRows()){
            if(rpc.getFieldValue(InventoryAdjustmentMeta.TRANS_ADJUSTMENT_LOCATION_META.INVENTORY_LOCATION_META.INVENTORY_ITEM_META.getStoreId()) == null){
                window.setStatus(consts.get("inventoryAdjLocAutoException"),"ErrorPanel");
                return;
            }
            
            NumberObject locIdObj = new NumberObject((Integer)adjustmentsTable.model.getCell(row, 0));
            NumberObject storeIdObj = new NumberObject((Integer)rpc.getFieldValue(
                                                        InventoryAdjustmentMeta.TRANS_ADJUSTMENT_LOCATION_META.INVENTORY_LOCATION_META.INVENTORY_ITEM_META.getStoreId()));
            final DataSet tableRow = adjustmentsTable.model.getRow(row);
            
            //we need to make sure the location id isnt already in the table
            if(locationIdAlreadyExists((Integer)locIdObj.getValue(), row)){
                tableRow.get(0).clearErrors();
                tableRow.get(0).addError(consts.get("fieldUniqueException"));
                return;
            }
            
            startedLoadingTable = true;
            window.setStatus("","spinnerIcon");
            
            // prepare the argument list for the getObject function
            DataObject[] args = new DataObject[] {locIdObj, storeIdObj}; 
            
            screenService.getObject("getInventoryItemInformation", args, new AsyncCallback(){
                public void onSuccess(Object result){    
                  DataModel model  = (DataModel)((ModelObject)result).getValue();
                  
                  if(model.size() > 0){
                      DataSet set = model.get(0);
                            
                      tableRow.setColumn(1,(DropDownField)set.getObject(1));
                      ((TableAutoDropdown)((EditTable)controller).view.table.getWidget(row, 1)).setField((DropDownField)set.getObject(1));
                      ((TableAutoDropdown)((EditTable)controller).view.table.getWidget(row, 1)).setDisplay();
                      
                      tableRow.setColumn(2,(StringField)set.getObject(2));
                      ((TableLabel)((EditTable)controller).view.table.getWidget(row, 2)).setField((StringField)set.getObject(2));
                      ((TableLabel)((EditTable)controller).view.table.getWidget(row, 2)).setDisplay();
                      
                      tableRow.setColumn(3,(NumberField)set.getObject(3));
                      ((TableLabel)((EditTable)controller).view.table.getWidget(row, 3)).setField((NumberField)set.getObject(3));
                      ((TableLabel)((EditTable)controller).view.table.getWidget(row, 3)).setDisplay();
                  }
                  startedLoadingTable = false;
                  window.setStatus("","");
                }
                
                public void onFailure(Throwable caught){
                    Window.alert(caught.getMessage());
                    window.setStatus("","");
                    startedLoadingTable = false;
                }
            });
            
        }else if(col == 1 && !startedLoadingTable && row > -1 && row < controller.model.numRows()){
            startedLoadingTable = true;
            
            TableRow tableRow = ((EditTable)controller).model.getRow(row);
            DropDownField invItemField = (DropDownField)tableRow.getColumn(1);
            ArrayList selections = invItemField.getSelections();
  
            if(selections.size() > 0){
                DataSet selectedRow = (DataSet)selections.get(0);
  
                if(selectedRow.size() > 1){
                    //we need to make sure this inventory item isnt already in the table
                    if(locationIdAlreadyExists((Integer)((NumberObject)selectedRow.getObject(6)).getValue(), row)){
                        tableRow.getColumn(1).clearErrors();
                        tableRow.getColumn(1).addError(consts.get("fieldUniqueException"));
                     
                    }else{
                        NumberField locNumField = new NumberField(NumberObject.Type.INTEGER);
                        StringField locationField = new StringField();
                        NumberField qtyOnHandField = new NumberField(NumberObject.Type.INTEGER);
                        
                        locNumField.setValue((Integer)((NumberObject)selectedRow.getObject(6)).getValue());
                        locationField.setValue((String)((StringObject)selectedRow.getObject(2)).getValue());
                        qtyOnHandField.setValue((Integer)((NumberObject)selectedRow.getObject(5)).getValue());
    
                        tableRow.setColumn(0, locNumField);                    
                        ((TableTextBox)((EditTable)controller).view.table.getWidget(row, 0)).setField(locNumField);
                        ((TableTextBox)((EditTable)controller).view.table.getWidget(row, 0)).setDisplay();
                        
                        tableRow.setColumn(2, locationField);                    
                        ((TableLabel)((EditTable)controller).view.table.getWidget(row, 2)).setField(locationField);
                        ((TableLabel)((EditTable)controller).view.table.getWidget(row, 2)).setDisplay();
                        
                        tableRow.setColumn(3, qtyOnHandField);                    
                        ((TableLabel)((EditTable)controller).view.table.getWidget(row, 3)).setField(qtyOnHandField);
                        ((TableLabel)((EditTable)controller).view.table.getWidget(row, 3)).setDisplay();
                    }
                }    
            }
            startedLoadingTable = false;
            
        }else if(col == 4 && !startedLoadingTable && row > -1 && row < controller.model.numRows()){
            TableRow tableRow = controller.model.getRow(row);
            Integer qtyOnHand = null;
            Integer physicalCount = null;
            Integer adjQty = null;
            
            qtyOnHand = (Integer)((NumberField)tableRow.getColumn(3)).getValue();
            physicalCount = (Integer)((NumberField)tableRow.getColumn(4)).getValue();
            
            if(qtyOnHand != null && physicalCount != null){
                adjQty = physicalCount - qtyOnHand;
                NumberField adjQtyField = new NumberField(NumberObject.Type.INTEGER);
                adjQtyField.setValue(adjQty);
                
                tableRow.setColumn(5, adjQtyField);
                ((TableLabel)((EditTable)controller).view.table.getWidget(row, 5)).setField(adjQtyField);
                ((TableLabel)((EditTable)controller).view.table.getWidget(row, 5)).setDisplay();
            }
        }*/
    }

    public void startedEditing(SourcesTableWidgetEvents sender, int row, int col) {
        // TODO Auto-generated method stub
        
    }
    //
    //end table listener methods
    //
    
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
    
    private void onRemoveRowButtonClick() {
        int selectedRow = adjustmentsTable.model.getSelectedIndex();
        if (selectedRow > -1 && adjustmentsTable.model.numRows() > 0) {
            adjustmentsTable.model.deleteRow(selectedRow);

        }
    }
    
    private boolean locationIdAlreadyExists(Integer locationId, int currentRow){
        boolean exists = false;
        
        TableModel model = (TableModel)adjustmentsTable.model;
        
        for(int i=0; i<model.numRows(); i++){
            if(i != currentRow && locationId.equals(model.getCell(i, 0))){
                exists = true;
                break;
            }            
        }
        
        return exists;
    }
}
