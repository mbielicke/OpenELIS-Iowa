package org.openelis.modules.inventoryReceipt.client;

import org.openelis.gwt.common.data.DataModel;
import org.openelis.gwt.screen.ScreenVertical;
import org.openelis.gwt.widget.ButtonPanel;
import org.openelis.gwt.widget.FormInt;
import org.openelis.gwt.widget.table.EditTable;
import org.openelis.gwt.widget.table.TableController;
import org.openelis.gwt.widget.table.TableManager;
import org.openelis.gwt.widget.table.TableWidget;
import org.openelis.modules.main.client.OpenELISScreenForm;
import org.openelis.newmeta.InventoryReceiptMetaMap;

import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Widget;

public class InventoryReceiptScreen extends OpenELISScreenForm implements ClickListener, TableManager {
    
    private EditTable        receiptsController;
    
    private InventoryReceiptMetaMap InventoryReceiptMeta = new InventoryReceiptMetaMap();
    
    public InventoryReceiptScreen() {
        super("org.openelis.modules.inventoryReceipt.server.InventoryReceiptService",false);
    }

    public void onClick(Widget sender) {
        // TODO Auto-generated method stub
        
    }
    
    public void afterDraw(boolean sucess) {
        //
        // disable auto add and make sure there are no rows in the table
        //
        receiptsController = ((TableWidget)getWidget("receiptsTable")).controller;
        receiptsController.setAutoAdd(false);
        
        setBpanel((ButtonPanel)getWidget("buttons"));
        super.afterDraw(sucess);
    }
    
    public void add() {
        //
        // make sure the contact table gets set before the main add
        //
        receiptsController.setAutoAdd(true);
        super.add();
    }
    
    public void update() {
        receiptsController.setAutoAdd(true);
        super.update();
    }
    
    public void abort() {
        receiptsController.setAutoAdd(false);
        
        super.abort();
    }
    
    public void afterCommitAdd(boolean success) {
        receiptsController.setAutoAdd(false);
        super.afterCommitAdd(success);
    }
    
    public void afterCommitUpdate(boolean success) {
        receiptsController.setAutoAdd(false);
        //we need to do this reset to get rid of the last row
        receiptsController.reset();
        
        super.afterCommitUpdate(success);
    }

    //start table manager methods
    public boolean action(int row, int col, TableController controller) {
        return false;
    }

    public boolean canDelete(int row, TableController controller) {
        return true;
    }

    public boolean canEdit(int row, int col, TableController controller) {
        return true;
    }

    public boolean canInsert(int row, TableController controller) {
        return false;
    }

    public boolean canSelect(int row, TableController controller) {
        if(state == FormInt.State.ADD || state == FormInt.State.UPDATE)           
            return true;
        return false;
    }

    public boolean doAutoAdd(int row, int col, TableController controller) {
        if(col == 0)
            return true;
        else
            return false;
    }

    public void finishedEditing(int row, int col, TableController controller) {
        //we need to try and lookup the order using the order number that they have entered
        if(col == 0){
            
        }
    }

    public void getNextPage(TableController controller) {
        // TODO Auto-generated method stub
        
    }

    public void getPage(int page) {
        // TODO Auto-generated method stub
        
    }

    public void getPreviousPage(TableController controller) {
        // TODO Auto-generated method stub
        
    }

    public void rowAdded(int row, TableController controller) {
        // TODO Auto-generated method stub
        
    }

    public void setModel(TableController controller, DataModel model) {
        // TODO Auto-generated method stub
        
    }

    public void setMultiple(int row, int col, TableController controller) {
        // TODO Auto-generated method stub
        
    }
    //end table manager methods

}
