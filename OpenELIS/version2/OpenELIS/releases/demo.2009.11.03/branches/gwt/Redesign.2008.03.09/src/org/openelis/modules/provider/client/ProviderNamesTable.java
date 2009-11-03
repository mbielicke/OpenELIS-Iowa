package org.openelis.modules.provider.client;

import org.openelis.gwt.common.data.TableRow;
import org.openelis.gwt.common.data.DataModel;
import org.openelis.gwt.common.data.DataSet;
import org.openelis.gwt.widget.FormInt;
import org.openelis.gwt.widget.table.TableController;
import org.openelis.gwt.widget.table.TableManager;



public class ProviderNamesTable implements TableManager {
    private ProviderScreen providerForm;
        
     
    public boolean action(int row, int col, TableController controller) {
        
        return true;
    }

    public boolean canDelete(int row, TableController controller) {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean canEdit(int row, int col, TableController controller) {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean canInsert(int row, TableController controller) {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean canSelect(int row, TableController controller) {                
        if(providerForm.bpanel.state == FormInt.DISPLAY)                   
            providerForm.modelWidget.select(row);       
        return false;
    }

    public boolean doAutoAdd(int row, int col, TableController controller) {
        // TODO Auto-generated method stub
        return false;
    }

    public void finishedEditing(int row, int col, TableController controller) {
        // TODO Auto-generated method stub

    }

    public void rowAdded(int row, TableController controller) {
        // TODO Auto-generated method stub

    }

    public void setModel(TableController controller, DataModel model) {
        
        controller.model.reset();
        for (int i = 0; i < model.size(); i++) {
            DataSet row = (DataSet)model.get(i);
            TableRow tRow = controller.model.createRow();
            
            tRow.getColumn(0).setDataObject(row.getObject(1));
            tRow.getColumn(1).setDataObject(row.getObject(2));
            controller.model.addRow(tRow);
        }
        if(providerForm.modelWidget.getPage() > 0)
            controller.model.pageIndex = 1;
        else
            controller.model.pageIndex = 0;
        controller.model.paged = true;
        controller.model.rowsPerPage = 1;
        controller.model.totalRows = 2;
        controller.model.totalPages = 3;
        
        controller.reset();

    }
    
    public void getNextPage(TableController controller) {
        providerForm.modelWidget.getModel().selecttLast(false);
        providerForm.modelWidget.setPage(providerForm.modelWidget.getPage()+1);
        
    }

    public void getPreviousPage(TableController controller) {
        providerForm.modelWidget.getModel().selecttLast(false);
        providerForm.modelWidget.setPage(providerForm.modelWidget.getPage()-1);
        
    }

    public void setProviderForm(ProviderScreen providerForm) {
        this.providerForm = providerForm;
    }

    public void getPage(int page) {
        providerForm.modelWidget.setPage(page);
        
    }

    public void validateRow(int row, TableController controller) {
        // TODO Auto-generated method stub
        
    }

    public void setMultiple(int row, int col, TableController controller) {
        // TODO Auto-generated method stub
        
    }

}
