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
import org.openelis.gwt.widget.CheckField;
import org.openelis.gwt.widget.Label;
import org.openelis.gwt.widget.StringField;
import org.openelis.gwt.widget.table.TableColumn;
import org.openelis.gwt.widget.table.TableDataRow;

import com.google.gwt.user.client.ui.Widget;

public class SampleResultTableColumn extends TableColumn {
    protected Label                 label;
    protected CheckBox              check;

    public Widget getDisplayWidget(TableDataRow row) {
        setColumnWidget(getCellWidget(row));
        return super.getDisplayWidget(row);
    }

    public void loadWidget(Widget widget, TableDataRow row, int modelIndex) {
        controller.renderer.setCellDisplay(modelIndex, columnIndex);
        super.loadWidget(controller.view.table.getWidget(controller.tableIndex(modelIndex), columnIndex), row, modelIndex);
    }

    public Widget getWidgetEditor(TableDataRow row) {
        setColumnWidget(getCellWidget(row));
        return super.getWidgetEditor(row);
    }

    private CheckBox getCheckBox(){
        CheckField field;
        if(check == null){
            check = new CheckBox();
            
            field = new CheckField();
            field.required = false;
            check.setField(field);
        }
        
        return check;
    }
    
    private Label getLabel() {
        if (label == null) {
            label = new Label();
            label.setStyleName("ScreenLabel");
            label.setField(new StringField());
        }

        return label;
    }

    private Widget getCellWidget(TableDataRow row) {
        Boolean isHeader;
        
        isHeader = (Boolean)row.data;
        
        if(isHeader == null || isHeader.booleanValue())
            return getLabel();
        else
            return getCheckBox();
    }
}
