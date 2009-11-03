package org.openelis.modules.dictionary.client;

import org.openelis.gwt.common.data.DataModel;
import org.openelis.gwt.common.data.DataSet;
import org.openelis.gwt.common.data.TableRow;
import org.openelis.gwt.widget.FormInt;
import org.openelis.gwt.widget.table.TableController;
import org.openelis.gwt.widget.table.TableManager;

public class CategorySystemNamesTable implements TableManager {
    
    private DictionaryScreen dictionaryForm = null;

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
        if(dictionaryForm.bpanel.state == FormInt.DISPLAY)                   
            dictionaryForm.modelWidget.select(row);       
        return false;
    }

    public boolean doAutoAdd(int row, int col, TableController controller) {
        // TODO Auto-generated method stub
        return false;
    }

    public void finishedEditing(int row, int col, TableController controller) {
        // TODO Auto-generated method stub

    }

    public void getNextPage(TableController controller) {
        dictionaryForm.modelWidget.getModel().selecttLast(false);
        dictionaryForm.modelWidget.setPage(dictionaryForm.modelWidget.getPage()+1);        

    }

    public void getPage(int page) {
        dictionaryForm.modelWidget.setPage(page);        
    }

    public void getPreviousPage(TableController controller) {
        dictionaryForm.modelWidget.getModel().selecttLast(false);
        dictionaryForm.modelWidget.setPage(dictionaryForm.modelWidget.getPage()-1);
        
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
            controller.model.addRow(tRow);
        }
        if(dictionaryForm.modelWidget.getPage() > 0)
            controller.model.pageIndex = 1;
        else
            controller.model.pageIndex = 0;
        controller.model.paged = true;
        controller.model.rowsPerPage = 1;
        controller.model.totalRows = 2;
        controller.model.totalPages = 3;
        
        controller.reset();
    }

    public DictionaryScreen getDictionaryForm() {
        return dictionaryForm;
    }

    public void setDictionaryForm(DictionaryScreen dictionaryForm) {
        this.dictionaryForm = dictionaryForm;
    }

    public void validateRow(int row, TableController controller) {
        // TODO Auto-generated method stub
        
    }

    public void setMultiple(int row, int col, TableController controller) {
        // TODO Auto-generated method stub
        
    }

}
