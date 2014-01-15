package org.openelis.web.modules.followup.client;

import java.util.ArrayList;
import java.util.Iterator;

import org.openelis.domain.EncounterDO;
import org.openelis.ui.widget.table.CellRenderer;
import org.openelis.ui.widget.table.ColumnInt;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTMLTable;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class EncounterCell implements CellRenderer, IsWidget, HasWidgets.ForIsWidget {


    public EncounterCell() {
        
    }
    
    @Override
    public void render(HTMLTable table, int row, int col, Object value) {
        assert value instanceof EncounterDO;
        
        EncounterDO enc;
        
        enc = (EncounterDO)value;
        
        
        switch(enc.getType().charAt(0)) {
            case 'C' :
                renderContact(table, row, col, enc);
                break;
            case 'R' :
                renderRecommendation(table,row,col,enc);
                break;
            case 'N' :
                renderNotification(table,row,col,enc);
                break;
            case 'O' :
                renderOutcome(table,row,col,enc);
                break;
            case 'F' :
                renderConfirmatory(table,row,col,enc);
                break;      
        }
        
        
    }
    
    protected void renderContact(HTMLTable table, int row, int col, EncounterDO enc) {
        
        FlexTable grid;
        grid = new FlexTable();
        
        grid.setWidget(0, 0, new Label("Contact"));
        grid.setText(1, 0, "By:"+enc.getEnteredBy());
        grid.setText(2, 0, "Initiator:"+enc.getContact1());
        grid.setText(3, 0, "Organiztion:"+enc.getContact2());
        grid.setText(4, 0, "Occurred:"+enc.getOccurred());
        grid.setText(5, 0, "Method:"+enc.getContactMethod());

        table.setWidget(row, col, grid);
        
    }
    
    protected void renderRecommendation(HTMLTable table, int row, int col, Object enc) {
        
    }
    
    protected void renderNotification(HTMLTable table, int row, int col, Object enc) {
        
    }
    
    protected void renderOutcome(HTMLTable table, int row, int col, Object enc) {
        
    }
    
    protected void renderConfirmatory(HTMLTable table, int row, int col, Object enc) {
        
    }

    @Override
    public void renderQuery(HTMLTable table, int row, int col, org.openelis.ui.common.data.QueryData qd) {
        
    }

    @Override
    public ArrayList<Exception> validate(Object value) {
        return null;
    }

    @Override
    public Widget asWidget() {
        return new Label();
    }

    @Override
    public String display(Object value) {
        return null;
    }

    @Override
    public void add(Widget w) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void clear() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public Iterator<Widget> iterator() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean remove(Widget w) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void add(IsWidget w) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public boolean remove(IsWidget w) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void setColumn(ColumnInt col) {
        // TODO Auto-generated method stub
        
    }

}
