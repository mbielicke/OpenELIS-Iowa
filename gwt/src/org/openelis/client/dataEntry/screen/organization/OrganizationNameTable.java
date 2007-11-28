package org.openelis.client.dataEntry.screen.organization;

import org.openelis.gwt.client.widget.FormInt;
import org.openelis.gwt.client.widget.table.TableController;
import org.openelis.gwt.client.widget.table.TableManager;

public class OrganizationNameTable implements TableManager {
    private Organization userForm;
    public boolean disableRows = false;

    public void setOrganizationForm(Organization form) {
        userForm = form;
    }  

    public boolean canSelect(int row, TableController controller) {        
        if(userForm.bpanel.state == FormInt.DISPLAY)           
            return true;
        return false;
    }

    public boolean canEdit(int row, int col, TableController controller) {
       
        return false;
    }

    public boolean canDelete(int row, TableController controller) {
        
        return false;
    }

    public boolean action(int row, int col, TableController controller) {
        if(userForm != null && userForm.bpanel.state == FormInt.DISPLAY){ 
        //	userForm.fetch(key);
            userForm.fetchData(controller.model.getRow(row)
                                            .getHidden("id"));                     
        }  
        
        return true;
    }

    public boolean canInsert(int row, TableController controller) {
        
        return false;
    }

    public void finishedEditing(int row, int col, TableController controller) {
      

    }



    public boolean doAutoAdd(int row, int col, TableController controller) {

        return false;
    }



    public void rowAdded(int row, TableController controller) {

        
    }
    
    
}
