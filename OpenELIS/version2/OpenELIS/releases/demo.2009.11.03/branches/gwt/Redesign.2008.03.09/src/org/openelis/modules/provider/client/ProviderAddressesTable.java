package org.openelis.modules.provider.client;

import org.openelis.gwt.common.data.DataModel;
import org.openelis.gwt.widget.table.TableController;
import org.openelis.gwt.widget.table.TableManager;

public class ProviderAddressesTable implements TableManager {

    public boolean disableRows = false;
    
    //private Provider providerForm;
    
    public boolean action(int row, int col, TableController controller) {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean canDelete(int row, TableController controller) {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean canEdit(int row, int col, TableController controller) {
       // if(disableRows){
            return true;
         // } 
        //return true;
    }

    public boolean canInsert(int row, TableController controller) {
        
        return false;
    }

    public boolean canSelect(int row, TableController controller) {
        //if(providerForm.bpanel.state == FormInt.ADD || providerForm.bpanel.state == FormInt.UPDATE)      
        if(disableRows){
            return false;
        } 
        return true;
    }

    public boolean doAutoAdd(int row, int col, TableController controller) {
       if (col == 0 && row == controller.model.numRows() - 1) {
           return true;
        }   
        
        return false;
    }

    public void finishedEditing(int row, int col, TableController controller) {
        // TODO Auto-generated method stub

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

    public void setProviderForm(ProviderScreen providerForm) {
        //this.providerForm = providerForm;
    }

    public void validateRow(int row, TableController controller) {
        // TODO Auto-generated method stub
        
    }

    public void setMultiple(int row, int col, TableController controller) {
        // TODO Auto-generated method stub
        
    }

}
