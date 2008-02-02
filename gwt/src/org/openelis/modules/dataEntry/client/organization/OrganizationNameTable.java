package org.openelis.modules.dataEntry.client.organization;

import org.openelis.gwt.client.widget.FormInt;
import org.openelis.gwt.client.widget.table.TableController;
import org.openelis.gwt.client.widget.table.TableManager;
import org.openelis.gwt.common.data.TableRow;
import org.openelis.gwt.common.data.DataModel;
import org.openelis.gwt.common.data.DataSet;

public class OrganizationNameTable implements TableManager {
    private OrganizationScreen userForm;
    public boolean disableRows = false;

    public void setOrganizationForm(OrganizationScreen form) {
        userForm = form;
    }  

    public boolean canSelect(int row, TableController controller) {        
        if(userForm.bpanel.state == FormInt.DISPLAY)           
        	userForm.modelWidget.select(row);
        return false;
    }

    public boolean canEdit(int row, int col, TableController controller) {
       
        return false;
    }

    public boolean canDelete(int row, TableController controller) {
        
        return false;
    }

    public boolean action(int row, int col, TableController controller) {
        
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

	public void getNextPage(TableController controller) {
		userForm.modelWidget.getModel().selecttLast(false);
		userForm.modelWidget.setPage(userForm.modelWidget.getPage()+1);
	}

	public void getPage(int page) {
		userForm.modelWidget.setPage(page);
		
	}

	public void getPreviousPage(TableController controller) {
		userForm.modelWidget.getModel().selecttLast(false);
		userForm.modelWidget.setPage(userForm.modelWidget.getPage()-1);
	}

	public void setModel(TableController controller, DataModel model) {
		controller.model.reset();
		//controller.reset();
		for (int i = 0; i < model.size(); i++) {
			DataSet row = (DataSet)model.get(i);
			TableRow tRow = controller.model.createRow();
			
			tRow.getColumn(0).setDataObject(row.getObject(1));
			controller.model.addRow(tRow);
		}
		if(userForm.modelWidget.getPage() > 0)
			controller.model.pageIndex = 1;
		else
			controller.model.pageIndex = 0;
		controller.model.paged = true;
		controller.model.rowsPerPage = 1;
		controller.model.totalRows = 2;
		controller.model.totalPages = 3;
		
		controller.loadModel(controller.model);
	}  
}
