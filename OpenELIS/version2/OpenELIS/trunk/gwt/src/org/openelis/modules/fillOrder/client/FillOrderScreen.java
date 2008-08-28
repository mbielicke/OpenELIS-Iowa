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

import org.openelis.gwt.common.data.DataModel;
import org.openelis.gwt.common.data.KeyListManager;
import org.openelis.gwt.common.data.StringField;
import org.openelis.gwt.common.data.TableRow;
import org.openelis.gwt.screen.CommandChain;
import org.openelis.gwt.screen.ScreenTableWidget;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.AutoCompleteDropdown;
import org.openelis.gwt.widget.ButtonPanel;
import org.openelis.gwt.widget.FormInt;
import org.openelis.gwt.widget.FormInt.State;
import org.openelis.gwt.widget.table.EditTable;
import org.openelis.gwt.widget.table.QueryTable;
import org.openelis.gwt.widget.table.TableAutoDropdown;
import org.openelis.gwt.widget.table.TableController;
import org.openelis.gwt.widget.table.TableManager;
import org.openelis.gwt.widget.table.TableWidget;
import org.openelis.metamap.OrderMetaMap;
import org.openelis.modules.main.client.OpenELISScreenForm;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Widget;

public class FillOrderScreen extends OpenELISScreenForm implements ClickListener, TableManager{
    
    private static boolean loaded = false;
    private static DataModel costCenterDropdown, shipFromDropdown, statusDropdown;
    
    private OrderMetaMap OrderMeta = new OrderMetaMap();
    
    private AppButton removeRowButton;
    private EditTable fillItemsController;
    
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
        if(sender == removeRowButton)
            onRemoveRowButtonClick();        
    }
    
    public void afterDraw(boolean success) {
        TableWidget d;
        QueryTable q;
        ScreenTableWidget sw;
        AutoCompleteDropdown drop;
        
        loaded = true;
        
        fillItemsController = ((TableWidget)getWidget("fillItemsTable")).controller;
        addCommandListener(fillItemsController);
        
        removeRowButton = (AppButton)getWidget("removeRowButton");
        
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
    
    public void query() {
        super.query();
        
        removeRowButton.changeState(AppButton.ButtonState.DISABLED);
    }
    
    public void fetch() {
//      we dont need to call the super because we arent using the datamodel for lookups
        //we are using it to fill the table
        //super.fetch();
        
        DataModel model = keyList.getList();
        
        fillItemsController.model.reset();
        fillItemsController.reset();
        
        loadFillItemsTableFromModel(model);
               
        if(model.size() > 0)
            changeState(State.DISPLAY);
    }
   
    //
    //start table manager methods
    //
    public boolean canSelect(int row, TableController controller) {        
        if(state == FormInt.State.ADD || state == FormInt.State.UPDATE)           
            return true;
        return false;
    }

    public boolean canEdit(int row, int col, TableController controller) {
       return true;
    }

    public boolean canDelete(int row, TableController controller) {
        return true;
    }

    public boolean action(int row, int col, TableController controller) {  
        return false;
    }

    public boolean canInsert(int row, TableController controller) {
        return false;     
    }

    public void finishedEditing(int row, int col, TableController controller) {}

    public boolean doAutoAdd(TableRow row, TableController controller) {
        return !tableRowEmpty(row);
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
    
    private boolean tableRowEmpty(TableRow row){
        boolean empty = true;
        
        //we want to ignore the first column since it is a check field
        for(int i=1; i<row.numColumns(); i++){
            if(row.getColumn(i).getValue() != null && !"".equals(row.getColumn(i).getValue())){
                empty = false;
                break;
            }
        }
        
        return empty;
    }
    
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
        
    }
}
