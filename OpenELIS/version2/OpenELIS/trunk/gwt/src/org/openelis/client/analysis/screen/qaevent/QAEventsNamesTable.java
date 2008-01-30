package org.openelis.client.analysis.screen.qaevent;

import org.openelis.gwt.client.widget.FormInt;
import org.openelis.gwt.client.widget.table.TableController;
import org.openelis.gwt.client.widget.table.TableManager;
import org.openelis.gwt.common.data.DataModel;
import org.openelis.gwt.common.data.DataSet;
import org.openelis.gwt.common.data.TableRow;

public class QAEventsNamesTable implements TableManager {

    private QAEvent qaEventForm = null;
    
    public void setQaEventForm(QAEvent qaEventForm) {
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

}
