package org.openelis.modules.organization.client;

import org.openelis.gwt.common.data.DataModel;
import org.openelis.gwt.widget.FormInt;
import org.openelis.gwt.widget.table.TableController;
import org.openelis.gwt.widget.table.TableManager;

public class OrganizationContactsTable implements TableManager {
	private OrganizationScreen userForm;
    public boolean disableRows = false;
    
    public void setOrganizationForm(OrganizationScreen form) {
        userForm = form;
    }
    
    public boolean canSelect(int row, TableController controller) {        
    	if(userForm.bpanel.state == FormInt.ADD || userForm.bpanel.state == FormInt.UPDATE)           
            return true;
        return false;
    }

    public boolean canEdit(int row, int col, TableController controller) {
    	if(disableRows){
            return false;
        }
        
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

    public boolean doAutoAdd(int row, int col, TableController controller) {
    	if (col == 0 && row == controller.model.numRows() - 1) {
            return true;
        }   
        
        return false;
    }

    public void rowAdded(int row, TableController controller) {}

	public void getNextPage(TableController controller) {}

	public void getPage(int page) {}

	public void getPreviousPage(TableController controller) {}

	public void setModel(TableController controller, DataModel model) {}

	public void validateRow(int row, TableController controller) {
		// TODO Auto-generated method stub
		
	}

	public void setMultiple(int row, int col, TableController controller) {
		// TODO Auto-generated method stub
		
	}
}
