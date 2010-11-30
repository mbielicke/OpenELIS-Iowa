/** Exhibit A - UIRF Open-source Based Public Software License.
* 
* The contents of this file are subject to the UIRF Open-source Based
* Public Software License(the "License"); you may not use this file except
* in compliance with the License. You may obtain a copy of the License at
* openelis.uhl.uiowa.edu
* 
* Software distributed under the License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
* License for the specific language governing rights and limitations
* under the License.
* 
* The Original Code is OpenELIS code.
* 
* The Initial Developer of the Original Code is The University of Iowa.
* Portions created by The University of Iowa are Copyright 2006-2008. All
* Rights Reserved.
* 
* Contributor(s): ______________________________________.
* 
* Alternatively, the contents of this file marked
* "Separately-Licensed" may be used under the terms of a UIRF Software
* license ("UIRF Software License"), in which case the provisions of a
* UIRF Software License are applicable instead of those above. 
*/
package org.openelis.modules.sample.client;

import org.openelis.gwt.widget.CheckBox;
import org.openelis.gwt.widget.Label;
import org.openelis.gwt.widget.table.CellEditor;
import org.openelis.gwt.widget.table.CellRenderer;
import org.openelis.gwt.widget.table.CheckBoxCell;
import org.openelis.gwt.widget.table.Column;
import org.openelis.gwt.widget.table.LabelCell;
import org.openelis.gwt.widget.table.Row;

public class SampleResultTableColumn extends Column {
    protected LabelCell<String>         label;
    protected CheckBoxCell              check;
    
    @Override
    public CellEditor getCellEditor(int r) {
    	Row row;
    	
    	row = table.getRowAt(r);
    	
    	return (CellEditor)getCellWidget(row);
    }
    
    @Override
    public CellRenderer getCellRenderer(int r) {
    	Row row;
    	
    	row = table.getRowAt(r);
    	
    	return (CellRenderer)getCellWidget(row);
    }

    /*
    public Widget getDisplayWidget(TableDataRow row) {
        setColumnWidget(getCellWidget(row));
        return super.getDisplayWidget(row);
    }

    public void loadWidget(Widget widget, TableDataRow row, int modelIndex) {
        Widget wid = setCellDisplay(modelIndex);
        super.loadWidget(wid,row,modelIndex);
        resetAlign(modelIndex);
    }

    public Widget getWidgetEditor(TableDataRow row) {
        setColumnWidget(getCellWidget(row));
        return super.getWidgetEditor(row);
    }
    */
   
    private CheckBoxCell getCheckBox(){
        CheckBox cb;
        if(check == null){
            cb = new CheckBox();
            
            check = new CheckBoxCell(cb);
        }
        //setAlign(HasAlignment.ALIGN_CENTER);
        return check;
    }
    
    private LabelCell<String> getLabelCell() {
    	Label<String> lb;
    	
        if (label == null) {
            lb = new Label<String>();
            lb.setStyleName("ScreenLabel");
            
            label = new LabelCell<String>(lb);
        }
        //setAlign(HasAlignment.ALIGN_LEFT);
        return label;
    }

    private Object getCellWidget(Row row) {
        Boolean isHeader;
        
        isHeader = (Boolean)row.getData();
        
        if(isHeader == null || isHeader.booleanValue())
            return getLabel();
        else
            return getCheckBox();
    }
}
