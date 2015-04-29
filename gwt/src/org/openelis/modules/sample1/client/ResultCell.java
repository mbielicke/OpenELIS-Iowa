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
package org.openelis.modules.sample1.client;

import static org.openelis.modules.main.client.Logger.*;

import java.util.ArrayList;
import java.util.logging.Level;

import org.openelis.cache.DictionaryCache;
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

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.HTMLTable;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

/**
 * This class is used for displaying, editing and validating the data in the
 * table for the results of analyses.
 */
public class ResultCell extends EditableCell<ResultCell.Value> implements CellEditor, CellRenderer {

    /*
     * The objects responsible for handling display and editing based on the
     * type of the value in the cell
     */
    private CellDropdown<Integer> dropdownCell;

    private CellTextbox<String>  textboxCell;

    private Widget       editor;

    private Value        value;

    public ResultCell() {
        dropdownCell = new CellDropdown<Integer>();
        textboxCell = new CellTextbox<String>();
    }

    /**
     * Gets Formatted value from editor and sets it as the cell's display
     */
    public void render(HTMLTable table, int row, int col, Object value) {
    	if (editor instanceof Dropdown) {
    		dropdownCell.render(table.getCellFormatter().getElement(row,col), Integer.valueOf(((Value)value).getDictId()));
    	} else {
    		textboxCell.render(table.getCellFormatter().getElement(row,col), ((Value)value).display);
    	}
    }
    
    public SafeHtml bulkRender(Object value) {
    	return asHtml((Value)value);
    }

    public String display(Object val) {
        Integer dictId;
        if ( ! (val instanceof Value))
            return DataBaseUtil.toString(val);

        value = (Value)val;
        if (value.display != null)
            return DataBaseUtil.toString(value.display);

        if (value.dictId != null) {
            try {
                dictId = Integer.valueOf(value.dictId);
                value.display = DictionaryCache.getById(dictId).getEntry();
            } catch (Exception e) {
                logger.log(Level.SEVERE, e.getMessage(), e);
            }
        }

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
        Dropdown<Integer> dd;

        dictId = null;
        if (editor instanceof Dropdown) {
            dd = (Dropdown<Integer>)editor;
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
    	if(editor instanceof Dropdown) {
    		return dropdownCell.ignoreKey(keyCode);
    	} else {
    		return false;
    	}
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
    public void setModel(ArrayList<Item<Integer>> model) {
        Dropdown<Integer> dd;

        if (model != null) {
            dd = (Dropdown<Integer>)dropdownCell.getWidget();
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
        Integer dictId;

        value = value;
        if (editor instanceof Dropdown) {
            dictId = null;
            if (value.dictId != null)
                dictId = Integer.valueOf(value.dictId);
            dropdownCell.startEditing(element,dictId);
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
    	if(editor instanceof Dropdown) {
    		return dropdownCell.asHtml(Integer.valueOf(value.getDictId()));
    	} else {
    		return textboxCell.asHtml(value.getDisplay());
    	}
	}

	@Override
	public String asString(Value value) {
		return display(value);
	}
}