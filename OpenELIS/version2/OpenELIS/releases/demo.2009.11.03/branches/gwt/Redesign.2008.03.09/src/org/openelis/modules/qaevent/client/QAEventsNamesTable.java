package org.openelis.modules.qaevent.client;

import org.openelis.gwt.common.data.DataModel;
import org.openelis.gwt.common.data.DataSet;
import org.openelis.gwt.common.data.TableRow;
import org.openelis.gwt.widget.FormInt;
import org.openelis.gwt.widget.table.TableController;
import org.openelis.gwt.widget.table.TableManager;

public class QAEventsNamesTable implements TableManager {

    private QAEventScreen qaEventForm = null;
    
    public void setQaEventForm(QAEventScreen qaEventForm) {
        this.qaEventForm = qaEventForm;
    }

    public boolean action(int row, int col, TableController controller) {
        // TODO Auto-generated method stub
        return false;
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
        if(qaEventForm.bpanel.state == FormInt.DISPLAY)                   
            qaEventForm.modelWidget.select(row);       
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
        qaEventForm.modelWidget.getModel().selecttLast(false);
        qaEventForm.modelWidget.setPage(qaEventForm.modelWidget.getPage()+1);

    }

    public void getPage(int page) {
        // TODO Auto-generated method stub

    }

    public void getPreviousPage(TableController controller) {
        qaEventForm.modelWidget.getModel().selecttLast(false);
        qaEventForm.modelWidget.setPage(qaEventForm.modelWidget.getPage()-1);

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
            tRow.getColumn(2).setDataObject(row.getObject(3));
            controller.model.addRow(tRow);
        }
        if(qaEventForm.modelWidget.getPage() > 0)
            controller.model.pageIndex = 1;
        else
            controller.model.pageIndex = 0;
        controller.model.paged = true;
        controller.model.rowsPerPage = 1;
        controller.model.totalRows = 2;
        controller.model.totalPages = 3;
        
        controller.reset();

    }

    public void setMultiple(int row, int col, TableController controller) {
        // TODO Auto-generated method stub
        
    }

}
