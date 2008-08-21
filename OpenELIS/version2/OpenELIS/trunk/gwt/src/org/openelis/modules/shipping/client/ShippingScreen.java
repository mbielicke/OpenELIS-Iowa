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
package org.openelis.modules.shipping.client;

import org.openelis.gwt.common.data.DataModel;
import org.openelis.gwt.common.data.TableRow;
import org.openelis.gwt.widget.ButtonPanel;
import org.openelis.gwt.widget.FormInt;
import org.openelis.gwt.widget.table.EditTable;
import org.openelis.gwt.widget.table.TableController;
import org.openelis.gwt.widget.table.TableManager;
import org.openelis.gwt.widget.table.TableWidget;
import org.openelis.modules.main.client.OpenELISScreenForm;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Widget;

public class ShippingScreen extends OpenELISScreenForm implements ClickListener, TableManager{

    //private static boolean loaded = false;
    
    //private OrganizationMetaMap OrgMeta = new OrganizationMetaMap();
    private EditTable itemsController, trackingNumbersController;
    
    public ShippingScreen() {
        super("org.openelis.modules.shipping.server.ShippingService", false);
    }
    
    public void onClick(Widget sender) {
    
    }
    
    public void afterDraw(boolean success) {
     //   loaded = true;

        itemsController = ((TableWidget)getWidget("itemsTable")).controller;
        itemsController.setAutoAdd(false);

        trackingNumbersController = ((TableWidget)getWidget("trackingNumbersTable")).controller;
        trackingNumbersController.setAutoAdd(false);

        addCommandListener((ButtonPanel)getWidget("buttons"));
        ((ButtonPanel)getWidget("buttons")).addCommandListener(this);        
        super.afterDraw(success);
    }
    
    public void add() {
        itemsController.setAutoAdd(true);
        trackingNumbersController.setAutoAdd(true);
        super.add();
    }
    
    public void update() {
        itemsController.setAutoAdd(true);
        trackingNumbersController.setAutoAdd(true);
        super.update();
    }
    
    public void abort() {
        itemsController.setAutoAdd(false);
        trackingNumbersController.setAutoAdd(true);
        super.abort();  
    }

    protected AsyncCallback afterCommitAdd = new AsyncCallback() {
        public void onSuccess(Object result){
            itemsController.setAutoAdd(false);
            trackingNumbersController.setAutoAdd(true);
            
            //we need to do this reset to get rid of the last row
            itemsController.reset();
            trackingNumbersController.reset();
        }
        
        public void onFailure(Throwable caught){
            
        }
    };
    
    protected AsyncCallback afterCommitUpdate = new AsyncCallback() {
        public void onSuccess(Object result){
            itemsController.setAutoAdd(false);
            trackingNumbersController.setAutoAdd(true);
            
            //we need to do this reset to get rid of the last row
            itemsController.reset();
            trackingNumbersController.reset();
        }
        
        public void onFailure(Throwable caught){
            
        }
    };
    
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

    public boolean doAutoAdd(TableRow autoAddRow, TableController controller) {
        return autoAddRow.getColumn(0).getValue() != null && !autoAddRow.getColumn(0).getValue().equals(0);
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
    
}
