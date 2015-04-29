/**
 * Exhibit A - UIRF Open-source Based Public Software License.
 * 
 * The contents of this file are subject to the UIRF Open-source Based Public
 * Software License(the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * openelis.uhl.uiowa.edu
 * 
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 * 
 * The Original Code is OpenELIS code.
 * 
 * The Initial Developer of the Original Code is The University of Iowa.
 * Portions created by The University of Iowa are Copyright 2006-2008. All
 * Rights Reserved.
 * 
 * Contributor(s): ______________________________________.
 * 
 * Alternatively, the contents of this file marked "Separately-Licensed" may be
 * used under the terms of a UIRF Software license ("UIRF Software License"), in
 * which case the provisions of a UIRF Software License are applicable instead
 * of those above.
 */
package org.openelis.modules.worksheetCompletion1.client;

import java.util.ArrayList;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.HTMLTable;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.data.QueryData;
import org.openelis.ui.widget.Dropdown;
import org.openelis.ui.widget.Item;
import org.openelis.ui.widget.TextBox;
import org.openelis.ui.widget.cell.CellDropdown;
import org.openelis.ui.widget.cell.CellTextbox;
import org.openelis.ui.widget.cell.EditableCell;
import org.openelis.ui.widget.table.CellEditor;
import org.openelis.ui.widget.table.CellRenderer;
import org.openelis.ui.widget.table.ColumnInt;
import org.openelis.ui.widget.table.Container;
import org.openelis.ui.widget.table.DropdownCell;
import org.openelis.ui.widget.table.TextBoxCell;

/**
 * This class is used for displaying, editing and validating the data in the
 * table for the results of analyses.
 */
public class WorksheetResultCell extends EditableCell<WorksheetResultCell.Value> implements CellEditor, CellRenderer {

    /*
     * The objects responsible for handling display and editing based on the
     * type of the value in the cell
     */
    private CellDropdown<String> dropdownCell;

    private CellTextbox<String> textboxCell;

    private Widget       editor;

    private Value        value;

    public WorksheetResultCell() {
        TextBox<String> tb;

        dropdownCell = new CellDropdown<String>();

        tb = new TextBox<String>();
        tb.setMaxLength(80);
        textboxCell = new CellTextbox<String>(tb);
    }

    /**
     * Gets Formatted value from editor and sets it as the cell's display
     */
    public void render(HTMLTable table, int row, int col, Object value) {
    	if(editor instanceof Dropdown) {
    		dropdownCell.render(table.getCellFormatter().getElement(row,col),((Value)value).getDictId());
    	} else {
    		textboxCell.render(table.getCellFormatter().getElement(row,col),((Value)value).getDisplay());
    	}
    }
    
    public SafeHtml bulkRender(Object value) {
        SafeHtmlBuilder builder = new SafeHtmlBuilder();
        
        builder.appendHtmlConstant("<td>");
        builder.appendEscaped(display(value));
        builder.appendHtmlConstant("</td>");
        
        return builder.toSafeHtml();
    }

    public String display(Object val) {
        if (!(val instanceof Value))
            return DataBaseUtil.toString(val);

        value = (Value)val;
        return DataBaseUtil.toString(value.display);
    }

    @Override
    public void renderQuery(HTMLTable table, int row, int col, QueryData qd) {
        // TODO Auto-generated method stub
    }

    @Override
    public ArrayList<Exception> validate(Object val) {
        return null;
    }

    @Override
    public void startEditing(Object val, Container container, NativeEvent event) {
    	startEditing(container.getElement(),(Value)val);
    }

    @Override
    public void startEditingQuery(QueryData qd, Container container, NativeEvent event) {
        // TODO Auto-generated method stub
    }

    @Override
    public Value finishEditing() {
        String display, dictId;
        TextBox<String> tb;
        Dropdown<String> dd;

        dictId = null;
        if (editor instanceof Dropdown) {
            dd = (Dropdown<String>)editor;
            dd.finishEditing();
            display = dd.getDisplay();
            /*
             * don't set dictId to an empty string as it may make the value
             * returned by this method be treated as not equal to the one that
             * was set in the cell
             */
            if (dd.getValue() != null)
                dictId = DataBaseUtil.toString(dd.getValue());
        } else {
            tb = (TextBox)editor;
            tb.finishEditing();
            display = tb.getValue();
        }

        /*
         * don't set display to an empty string as it may make the value
         * returned by this method be treated as not equal to the one that was
         * set in the cell
         */
        if (DataBaseUtil.isEmpty(display))
            display = null;

        return new Value(display, dictId);
    }

    @Override
    public boolean ignoreKey(int keyCode) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public Widget getWidget() {
        return editor;
    }

    @Override
    public void setColumn(ColumnInt col) {
        //dropdownCell.setColumn(col);
        //textboxCell.setColumn(col);
    }

    /**
     * If the model is not null then sets the editor to be a dropdown and sets
     * the model in the dropdown. Sets the editor to be a textbox otherwise.
     */
    public void setModel(ArrayList<Item<String>> model) {
        Dropdown<String> dd;

        if (model != null) {
            dd = (Dropdown<String>)dropdownCell.getWidget();
            dd.setModel(model);
            editor = dd;
        } else {
            editor = textboxCell.getWidget();
        }
    }

    public static class Value {
        private String display, dictId;

        public Value(String display, String dictId) {
            this.display = display;
            this.dictId = dictId;
        }

        public String getDisplay() {
            return display;
        }

        public void setDisplay(String display) {
            this.display = display;
        }
        
        public String getDictId() {
            return dictId;
        }

        /**
         * overridden to allow the table to determine if the value set in the
         * cell before finish editing is different from the one generated after
         * finish editing
         */
        public boolean equals(Object obj) {
            Value value;
            if ( ! (obj instanceof Value))
                return false;

            value = (Value)obj;
            return !DataBaseUtil.isDifferent(display, value.display) &&
                   !DataBaseUtil.isDifferent(dictId, value.dictId);
        }
    }

	@Override
	public void startEditing(Element element, Value val) {
        value = val;
        if (editor instanceof Dropdown) {
            dropdownCell.startEditing(element,value.dictId);
        } else {
            textboxCell.startEditing(element,DataBaseUtil.toString(value.display));
        }
		
	}

	@Override
	public void startEditing(Element element, QueryData qd) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public SafeHtml asHtml(Value value) {
        SafeHtmlBuilder builder = new SafeHtmlBuilder();
        
        builder.appendEscaped(display(value));
        
        return builder.toSafeHtml();
	}

	@Override
	public String asString(Value value) {
		return display(value);
	}
}