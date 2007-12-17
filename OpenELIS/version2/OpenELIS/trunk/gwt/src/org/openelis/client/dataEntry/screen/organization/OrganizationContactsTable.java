package org.openelis.client.dataEntry.screen.organization;

import org.openelis.gwt.client.widget.FormInt;
import org.openelis.gwt.client.widget.table.small.TableController;
import org.openelis.gwt.client.widget.table.small.TableManager;
import org.openelis.gwt.common.data.DataModel;

public class OrganizationContactsTable implements TableManager {
	private Organization userForm;
    public boolean disableRows = false;
    
    public void setOrganizationForm(Organization form) {
        userForm = form;
    }
    
   

    public boolean canSelect(int row, TableController controller) {        
    	if(userForm.bpanel.state == FormInt.ADD || userForm.bpanel.state == FormInt.UPDATE)           
            return true;
        return false;
    }

    public boolean canEdit(int row, int col, TableController controller) {
    	//if(userForm.bpanel.state == FormInt.ADD || userForm.bpanel.state == FormInt.UPDATE)           
        //    return true;
        //return false;
    	if(disableRows){
            return false;
        }
        
       return true;
    }

    public boolean canDelete(int row, TableController controller) {
    	//if(userForm.bpanel.state == FormInt.ADD || userForm.bpanel.state == FormInt.UPDATE)           
            return true;
      //  return false;
    }

    public boolean action(int row, int col, TableController controller) {
        //if(userForm != null){ 
        //    userForm.fetchData(controller.model.getRow(row)
        //                                    .getHidden("id"));                     
        
         
        //}  
       
        
        return false;
    }

    public boolean canInsert(int row, TableController controller) {
    	//if(userForm.bpanel.state == FormInt.ADD || userForm.bpanel.state == FormInt.UPDATE)           
            return false;
      //  return false;        
    }

    public void finishedEditing(int row, int col, TableController controller) {
      

    }



    public boolean doAutoAdd(int row, int col, TableController controller) {
    	if (col == 0 && row == controller.model.numRows() - 1) {
            return true;
        }   
        
        return false;
    }



    public void rowAdded(int row, TableController controller) {

        
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



	public void setModel(TableController controller, DataModel model) {
		// TODO Auto-generated method stub
		
	}
}
