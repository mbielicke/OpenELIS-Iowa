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

import org.openelis.gwt.common.SecurityException;
import org.openelis.gwt.common.SecurityModule;
import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.screen.ScreenDefInt;
import org.openelis.gwt.services.ScreenService;
import org.openelis.modules.main.client.openelis.OpenELIS;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;


public class InventoryAdjustmentScreen extends Screen {
    
    private SecurityModule  security;
    
    public InventoryAdjustmentScreen() throws Exception {
        super((ScreenDefInt)GWT.create(InventoryAdjustmentDef.class));
        service = new ScreenService("controller?service=org.openelis.modules.inventoryAdjustment.server.InventoryAdjustmentService");        

        security = OpenELIS.security.getModule("buildkits");
        if (security == null)
            throw new SecurityException("screenPermException", "Build Kits Screen");

        DeferredCommand.addCommand(new Command() {
            public void execute() {
                postConstructor();
            }
        });
    }
    
    private void postConstructor() {
        
    }
}
/*    

    private TableWidget        adjustmentsTable;
    private AppButton        removeRowButton;
    
    private ScreenTextBox   idText, userText, descText;
    private Dropdown store;
    private ScreenCalendar adjustmentDateText;
    private KeyListManager   keyList = new KeyListManager();
    private Integer lastLocValue = null;
    
    private InventoryAdjustmentMetaMap InventoryAdjustmentMeta = new InventoryAdjustmentMetaMap();
    private static String storeIdKey;
    
    public InventoryAdjustmentScreen() {
        super("org.openelis.modules.inventoryAdjustment.server.InventoryAdjustmentService");
        query = new Query<TableDataRow<Integer>>();
        getScreen(new InventoryAdjustmentForm());
    }
    
    public void onClick(Widget sender) {
        if (sender == removeRowButton)
            onRemoveRowButtonClick();
    }
    
    public void afterDraw(boolean sucess) {
        removeRowButton = (AppButton)getWidget("removeRowButton");
        adjustmentsTable = (TableWidget)getWidget("adjustmentsTable");
        adjustmentsTable.addTableWidgetListener(this);
        adjustmentsTable.model.enableAutoAdd(false);
        
        storeIdKey = InventoryAdjustmentMeta.TRANS_ADJUSTMENT_LOCATION_META.INVENTORY_LOCATION_META.INVENTORY_ITEM_META.getStoreId();
        
        idText = (ScreenTextBox) widgets.get(InventoryAdjustmentMeta.getId());
        adjustmentDateText = (ScreenCalendar) widgets.get(InventoryAdjustmentMeta.getAdjustmentDate());
        userText = (ScreenTextBox) widgets.get(InventoryAdjustmentMeta.getSystemUserId());
        store = (Dropdown) getWidget(storeIdKey);
        descText = (ScreenTextBox) widgets.get(InventoryAdjustmentMeta.getDescription());
        
        ButtonPanel bpanel = (ButtonPanel)getWidget("buttons");
        
        CommandChain chain = new CommandChain();
        chain.addCommand(this);
        chain.addCommand(keyList);
        chain.addCommand(bpanel);
        
        updateChain.add(afterUpdate);
        commitAddChain.add(afterCommitAdd);
        commitUpdateChain.add(afterCommitUpdate);
        
        super.afterDraw(sucess);
        
        ArrayList cache;
        TableDataModel<TableDataRow> model;
        cache = DictionaryCache.getListByCategorySystemName("inventory_item_stores");
        model = getDictionaryIdEntryList(cache);
        store.setModel(model);
    }
    
    public void add() {
        super.add();
        adjustmentsTable.model.enableAutoAdd(true);
        
        idText.enable(false);
        adjustmentDateText.enable(false);
        userText.enable(false);
        
        //InventoryAdjustmentForm iarpc = new InventoryAdjustmentForm();
        //iarpc.screenKey = form.screenKey;
        //iarpc.form = form.form;
        
        screenService.call("getAddAutoFillValues", form, new AsyncCallback<InventoryAdjustmentForm>(){
            public void onSuccess(InventoryAdjustmentForm result){    

                //load the values
                adjustmentDateText.load(result.adjustmentDate);
                userText.load(result.systemUser);
                
                //set the values in the rpc
                form.systemUser.setValue(result.systemUser.getValue());
                form.adjustmentDate.setValue(result.adjustmentDate.getValue());
                form.systemUserId = result.systemUserId;
                descText.setFocus(true);
                window.setStatus("","");
            }
            
            public void onFailure(Throwable caught){
                Window.alert(caught.getMessage());
            }
        }); 
    }
    
    protected SyncCallback afterUpdate = new SyncCallback() {
        public void onSuccess(Object result){
            idText.enable(false);
            adjustmentDateText.enable(false);
            userText.enable(false);
            ((ScreenDropDownWidget)widgets.get(storeIdKey)).enable(false);
            descText.setFocus(true);
            removeRowButton.changeState(AppButton.ButtonState.DISABLED);
            
        }
        
        public void onFailure(Throwable caught){
            
        }
    };
    
    public void abort() {
        adjustmentsTable.model.enableAutoAdd(false);
        
        if(state == State.ADD){
            //we need to unlock all the locs
          //unlock the record
            InventoryAdjustmentForm iarpc = new InventoryAdjustmentForm();
            iarpc.lockedIds = getLockedSetsFromTable();

            screenService.call("unlockLocations", iarpc, new AsyncCallback<FillOrderItemInfoForm>() {
                public void onSuccess(FillOrderItemInfoForm result) {
                    superAbort();
                    
               }

               public void onFailure(Throwable caught) {
                   Window.alert(caught.getMessage());
               }
                });
        }else
            super.abort();
    }
    
    private void superAbort(){
        super.abort();
    }
    
    public void query() {
        super.query();
        userText.enable(false);
        idText.setFocus(true);
    }
    
    protected SyncCallback afterCommitUpdate = new SyncCallback() {
        public void onSuccess(Object result){
            adjustmentsTable.model.enableAutoAdd(false);
        }
        
        public void onFailure(Throwable caught){
            
        }
  };
    
    protected SyncCallback afterCommitAdd = new SyncCallback() {
      public void onFailure(Throwable caught) {
          
      }
      public void onSuccess(Object result) {
          adjustmentsTable.model.enableAutoAdd(false);
      }   
  };
    
    //
    //start table manager methods
    //
    public boolean canAdd(TableWidget widget, TableDataRow set, int row) {
        return false;
    }

    public boolean canAutoAdd(TableWidget widget, TableDataRow addRow) {
        return !tableRowEmpty(addRow);
    }

    public boolean canDelete(TableWidget widget, TableDataRow set, int row) {
        return false;
    }

    public boolean canEdit(TableWidget widget, TableDataRow set, int row, int col) {
        if(state == State.UPDATE && (col == 0 || col == 1))
            return false;
        
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
    public void finishedEditing(SourcesTableWidgetEvents sender, final int row, int col) {
        if(state == State.QUERY || row >= adjustmentsTable.model.numRows())
            return;
        final TableDataRow<Integer> tableRow = adjustmentsTable.model.getRow(row);
        final DropDownField<Integer> invItemField = (DropDownField<Integer>)tableRow.cells[1];
        switch (col){
            case 0:
                if(store.getSelections().size() == 0){
                    window.setError(consts.get("inventoryAdjLocAutoException"));
                    return;
                }
                
                Integer currentLocValue = (Integer)adjustmentsTable.model.getCell(row, col);
                if(store.getSelections().size() > 0)
                    form.storeIdKey = (Integer)store.getSelections().get(0).key;
                form.locId = currentLocValue;
                
                //we need to make sure the location id isnt already in the table
                if(locationIdAlreadyExists(form.locId, row)){
                    ((IntegerField)tableRow.cells[0]).clearErrors();
                    adjustmentsTable.model.setCellError(row, 0, consts.get("fieldUniqueException"));
                    return;
                }
                
                if((lastLocValue == null && currentLocValue != null) || 
                                (lastLocValue != null && !lastLocValue.equals(currentLocValue))){
                    //we need to send lock/fetch call back
                    form.oldLocId = lastLocValue;
                    
                    window.setBusy();
                    
                    screenService.call("getInventoryItemInformation", form, new AsyncCallback<InventoryAdjustmentForm>(){
                        public void onSuccess(InventoryAdjustmentForm result){  
                            if(result.invItemModel != null && result.invItemModel.size() > 0){
                                invItemField.setModel(result.invItemModel);
                                adjustmentsTable.model.setCell(row, 1, result.invItemModel.get(0));
                                adjustmentsTable.model.clearCellError(row, 1);
                                adjustmentsTable.model.setCell(row, 2, result.storageLocation);
                                adjustmentsTable.model.setCell(row, 3, result.qtyOnHand);
                            }
                            window.clearStatus();
                        }
                        
                        public void onFailure(Throwable caught){
                            //clear out the columns
                            adjustmentsTable.model.setCell(row, 0, null);
                            adjustmentsTable.model.setCell(row, 1, null);
                            adjustmentsTable.model.setCell(row, 2, null);
                            adjustmentsTable.model.setCell(row, 3, null);
                            adjustmentsTable.model.setCell(row, 4, null);
                            adjustmentsTable.model.setCell(row, 5, null);
                            //adjustmentsTable.select(row, 0);
                            
                            Window.alert(caught.getMessage());
                            window.clearStatus();
                        }
                    });
                }
                break;
            case 1:
                    ArrayList selections = invItemField.getValue();
          
                    if(selections.size() > 0){
                        TableDataRow selectedRow = (TableDataRow)selections.get(0);
          
                        if(selectedRow.size() > 1){
                            Integer curLocValue = ((IntegerObject)selectedRow.getData()).getValue();
                            if((lastLocValue == null && curLocValue != null) || 
                                (lastLocValue != null && !lastLocValue.equals(curLocValue))){
                                //we need to make sure this inventory item isnt already in the table
                                if(locationIdAlreadyExists(((IntegerObject)selectedRow.getData()).getValue(), row)){
                                    ((DropDownField)tableRow.cells[1]).clearErrors();
                                    adjustmentsTable.model.setCellError(row, 1, consts.get("fieldUniqueException"));
                                 
                                }else{
                                    adjustmentsTable.model.setCell(row, 0, ((IntegerObject)selectedRow.getData()).getValue());
                                    adjustmentsTable.model.clearCellError(row, 0);
                                    adjustmentsTable.model.setCell(row, 2, ((StringObject)selectedRow.cells[2]).getValue());
                                    adjustmentsTable.model.setCell(row, 3, ((IntegerObject)selectedRow.cells[5]).getValue());
                                    
                                    window.setBusy();
                                    
                                    form.locId = curLocValue;
                                    form.oldLocId = lastLocValue;
                                    
                                    screenService.call("fetchLocationAndLock", form, new AsyncCallback<InventoryAdjustmentForm>(){
                                        public void onSuccess(InventoryAdjustmentForm result){    
                                            adjustmentsTable.model.setCell(row, 3, result.qtyOnHand);

                                            window.clearStatus();
                                        }
                                        
                                        public void onFailure(Throwable caught){
                                            //clear out the columns
                                            adjustmentsTable.model.setCell(row, 0, null);
                                            adjustmentsTable.model.setCell(row, 1, null);
                                            adjustmentsTable.model.setCell(row, 2, null);
                                            adjustmentsTable.model.setCell(row, 3, null);
                                            adjustmentsTable.model.setCell(row, 4, null);
                                            adjustmentsTable.model.setCell(row, 5, null);
                                            adjustmentsTable.select(row, 1);
                                            
                                            Window.alert(caught.getMessage());
                                            window.clearStatus();
                                            form.locId = null;
                                            form.oldLocId = null;
                                        }
                                    });
                                
                                }
                            }
                        }    
                    }

                break;
            case 4:
                Integer qtyOnHand = null;
                Integer physicalCount = null;
                Integer adjQty = null;
                
                qtyOnHand = (Integer)tableRow.cells[3].getValue();
                physicalCount = (Integer)tableRow.cells[4].getValue();
                
                if(qtyOnHand != null && physicalCount != null){
                    adjQty = physicalCount - qtyOnHand;
                    
                    adjustmentsTable.model.setCell(row, 5, adjQty);
                }
                break;
        }
    }

    public void startEditing(SourcesTableWidgetEvents sender, int row, int col) {
        if(state == State.QUERY)
            return;
        
        if(col == 0 && row < adjustmentsTable.model.numRows()){
            lastLocValue = (Integer)adjustmentsTable.model.getCell(row, col);

        }else if(col == 1 && row < adjustmentsTable.model.numRows()){
            ArrayList<TableDataRow<Integer>> selections = ((DropDownField<Integer>)adjustmentsTable.model.getObject(row, col)).getValue();
            
            if(selections != null && selections.size() == 1)
                lastLocValue = (Integer)selections.get(0).getData().getValue();
        }
    }

    public void stopEditing(SourcesTableWidgetEvents sender, int row, int col) {}
    //
    //end table listener methods
    //
    
    private boolean tableRowEmpty(TableDataRow<Integer> row){
        boolean empty = true;
        
        for(int i=0; i<row.cells.length; i++){
            if(row.cells[i].getValue() != null && !"".equals(row.cells[i].getValue())){
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

    public void callForMatches(final AutoComplete widget, TableDataModel model, String text) {
        // prepare the arguments
        InventoryAdjustmentItemAutoRPC iaarpc = new InventoryAdjustmentItemAutoRPC();
        iaarpc.key = form.entityKey;
        iaarpc.cat = widget.cat;
        iaarpc.text = text;
        if(store.getSelections().size() > 0)
            iaarpc.storeId = (Integer)store.getSelections().get(0).key;
        
        screenService.call("getMatchesObj", iaarpc, new AsyncCallback<InventoryAdjustmentItemAutoRPC>() {
            public void onSuccess(InventoryAdjustmentItemAutoRPC result) {
                widget.showAutoMatches(result.autoMatches);
            }
            
            public void onFailure(Throwable caught) {
                if(caught instanceof FormErrorException){
                    window.setError(caught.getMessage());
                }else
                    Window.alert(caught.getMessage());
            }
        });        
    }
    
    private TableDataModel<TableDataRow<Integer>> getLockedSetsFromTable(){
        TableDataModel<TableDataRow<Integer>> returnModel = new TableDataModel<TableDataRow<Integer>>();

        for(int i=0; i<adjustmentsTable.model.numRows(); i++)
            returnModel.add(new TableDataRow<Integer>((Integer)adjustmentsTable.model.getCell(i, 0)));
            
        return returnModel;
    }
    
    private TableDataModel<TableDataRow> getDictionaryIdEntryList(ArrayList list){
        TableDataModel<TableDataRow> m = new TableDataModel<TableDataRow>();
        TableDataRow<Integer> row;
        
        if(list == null)
            return m;
        
        m = new TableDataModel<TableDataRow>();
        m.add(new TableDataRow<Integer>(null,new StringObject("")));
        
        for(int i=0; i<list.size(); i++){
            row = new TableDataRow<Integer>(1);
            DictionaryDO dictDO = (DictionaryDO)list.get(i);
            row.key = dictDO.getId();
            row.cells[0] = new StringObject(dictDO.getEntry());
            m.add(row);
        }
        
        return m;
    }
*/
