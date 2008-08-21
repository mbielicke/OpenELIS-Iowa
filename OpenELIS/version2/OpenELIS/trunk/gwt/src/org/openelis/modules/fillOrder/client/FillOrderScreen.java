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

public class FillOrderScreen extends OpenELISScreenForm implements ClickListener, TableManager{
    
    //private OrganizationMetaMap OrgMeta = new OrganizationMetaMap();
    private EditTable fillItemsController;

    public FillOrderScreen() {
        super("org.openelis.modules.fillOrder.server.FillOrderService", false);
    }
    
    public void onClick(Widget sender) {
    
    }
    
    public void afterDraw(boolean success) {
        fillItemsController = ((TableWidget)getWidget("fillItemsTable")).controller;
        fillItemsController.setAutoAdd(false);
        
        addCommandListener((ButtonPanel)getWidget("buttons"));
        ((ButtonPanel)getWidget("buttons")).addCommandListener(this);        
        super.afterDraw(success);
        
    }
    
    public void add() {
        fillItemsController.setAutoAdd(true);
        super.add();
    }
    
    public void update() {
        fillItemsController.setAutoAdd(true);
        super.update();
    }
    
    public void abort() {
        fillItemsController.setAutoAdd(false);
        super.abort();  
    }

    protected AsyncCallback afterCommitAdd = new AsyncCallback() {
        public void onSuccess(Object result){
            fillItemsController.setAutoAdd(false);
            
            //we need to do this reset to get rid of the last row
            fillItemsController.reset();
        }
        
        public void onFailure(Throwable caught){
            
        }
  };
  
  protected AsyncCallback afterCommitUpdate = new AsyncCallback() {
      public void onSuccess(Object result){
          fillItemsController.setAutoAdd(false);
          
          //we need to do this reset to get rid of the last row
          fillItemsController.reset();
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

}
