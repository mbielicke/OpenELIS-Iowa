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
package org.openelis.modules.sample.client;

import java.util.ArrayList;

import org.openelis.gwt.widget.Dropdown;
import org.openelis.gwt.widget.Label;
import org.openelis.gwt.widget.StringField;
import org.openelis.gwt.widget.TextBox;
import org.openelis.gwt.widget.table.TableColumn;
import org.openelis.gwt.widget.table.TableDataRow;
import org.openelis.utilcommon.ResultValidator;
import org.openelis.utilcommon.ResultValidator.OptionItem;

import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.Widget;

public class AuxTableColumn extends TableColumn {
    protected Label           label;
    protected TextBox<String> textBox;

    public Widget getDisplayWidget(TableDataRow row) {
        setColumnWidget(getCellWidget(row));
        return super.getDisplayWidget(row);
    }

    public void loadWidget(Widget widget, TableDataRow row, int modelIndex) {
        setColumnWidget(getCellWidget(row));
        super.loadWidget(widget, row, modelIndex);
    }

    public Widget getWidgetEditor(TableDataRow row) {
        setColumnWidget(getCellWidget(row));
        return super.getWidgetEditor(row);
    }

    private TextBox<String> getTextbox() {
        if (textBox == null) {
            textBox = new TextBox<String>();
            textBox.setStyleName("ScreenTextBox");
            textBox.setField(new StringField());
            textBox.setLength(80);
        }
        textBox.enable(true);
        return textBox;
    }

    private Label getLabel() {
        if (label == null) {
            label = new Label();
            label.setStyleName("ScreenLabel");
            label.setField(new StringField());
        }
        setAlign(HasAlignment.ALIGN_LEFT);
        return label;
    }

    private Dropdown<String> createDropdown(ArrayList<OptionItem> options) {
        int i;
        ArrayList<TableDataRow> model;
        Dropdown<String> d;
        TableColumn c;
        TableDataRow row;
        Label<String> dl;
        StringField f;
        OptionItem item;

        //
        // create a new dropdown
        //
        d = new Dropdown<String>();
        f = new StringField();
        f.required = false;
        d.setField(f);
        d.setTableWidth("auto");
        d.dropwidth = currentWidth + "px";
        d.setMultiSelect(false);        
        
        dl = new Label<String>();
        dl.setField(f);
        dl.setWidth(d.dropwidth);

        d.setColumns(new ArrayList<TableColumn>());
        c = new TableColumn();
        c.controller = d;
        c.setCurrentWidth(currentWidth);
        c.setColumnWidget(dl);
        d.getColumns().add(c);
        d.setup();

        model = new ArrayList<TableDataRow>();
        model.add(new TableDataRow(null, ""));
        for (i = 0; i < options.size(); i++) {
            item = options.get(i);
            model.add(new TableDataRow(item.getValue(), item.getValue()));
        }
        d.load(model);
        d.enable(true);
        
        return d;
    }

    private Widget getCellWidget(TableDataRow row) {
        ResultValidator validator;
        
        validator = (ResultValidator) row.data;
        if (validator == null) {
            return getLabel();
        } else if (validator.hasOnlyDictionary()) {
            return createDropdown(validator.getDictionaryRanges());
        } else {
            return getTextbox();
        }
    }
}