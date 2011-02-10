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

import java.util.ArrayList;
import java.util.HashMap;

import org.openelis.gwt.event.GetMatchesHandler;
import org.openelis.gwt.widget.Dropdown;
import org.openelis.gwt.widget.Label;
import org.openelis.gwt.widget.StringField;
import org.openelis.gwt.widget.TextBox;
import org.openelis.gwt.widget.table.TableColumn;
import org.openelis.gwt.widget.table.TableDataRow;

import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.Widget;

public class SampleResultValueTableColumn extends TableColumn {
    protected GetMatchesHandler                                 screen;
    protected Label                                             label;
    protected TextBox                                           textBox;
    protected HashMap<Integer, Dropdown<String>>                resultGroupMap; 
    protected Integer                                           resultGroup;      

    public Widget getDisplayWidget(TableDataRow row) {
        setColumnWidget(getLabel());
        return super.getDisplayWidget(row);
    }

    public void loadWidget(Widget widget, TableDataRow row, int modelIndex) {
        setColumnWidget(getLabel());
        super.loadWidget(widget,row,modelIndex);
        resetAlign(modelIndex);
    }

    public Widget getWidgetEditor(TableDataRow row) {
        setColumnWidget(getCellWidget(row));
        return super.getWidgetEditor(row);
    }   
    
    public void setResultGroup(Integer resultGroup) {
        this.resultGroup = resultGroup;
    }     
    
    public void setResultGroupModel(Integer resultGroup, ArrayList<TableDataRow> model) {
        Dropdown<String> d;
        
        if (resultGroup == null)
            return;
        
        if (resultGroupMap == null)
            resultGroupMap = new HashMap<Integer, Dropdown<String>>();        
        
        d = resultGroupMap.get(resultGroup);
        if (d == null) {                
            d = createDropdown(model);
            resultGroupMap.put(resultGroup, d);
        }        
    }
    
    public void setResultGroupMap(HashMap<Integer, Dropdown<String>> resultGroupMap) {
        this.resultGroupMap = resultGroupMap;
    }
    
    public void clear() {
        resultGroupMap = null;
    }
    
    private TextBox<String> getTextbox() {
        if (textBox == null) {
            textBox = new TextBox<String>();
            textBox.setStyleName("ScreenTextBox");
            textBox.setField(new StringField());
            textBox.setLength(200);
            textBox.enable(true);            
        }
        
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
    
    private Dropdown<String> createDropdown(ArrayList<TableDataRow> model) {
        Dropdown<String> d;
        TableColumn c;
        Label<String> dl;
        StringField f;

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

        d.load(model);
        d.enable(true);
        
        return d;
    }

    private Widget getCellWidget(TableDataRow row) {
        Boolean isHeader;
        Dropdown<String> d;
        TextBox t;
        ArrayList<String> model;
        Object val;
        
        isHeader = (Boolean)row.data;        
        
        if(isHeader == null || isHeader) {
            return getLabel();
        } else if (resultGroupMap == null || resultGroup == null) {                
            t = getTextbox();
            t.setValue((String)row.cells.get(columnIndex).getValue());
            return t;
        } else {
            d = resultGroupMap.get(resultGroup);
            if (d == null)
                return getTextbox();
                
            d.renderer.dataChanged(true);   
            val = row.cells.get(columnIndex).getValue();
            if (val instanceof ArrayList) {
                model = (ArrayList<String>)val;
                if (model != null && model.size() > 0)
                    d.setValue(model.get(0));
                else
                    d.setValue(null);
            } else if (val instanceof String) {
                d.setValue((String)val);
            }
            return d;
        }
    }
}
