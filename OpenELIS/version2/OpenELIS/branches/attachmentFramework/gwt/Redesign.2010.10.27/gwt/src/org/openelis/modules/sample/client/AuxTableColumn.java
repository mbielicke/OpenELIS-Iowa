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

import org.openelis.cache.DictionaryCache;
import org.openelis.gwt.common.Datetime;
import org.openelis.gwt.common.data.QueryData;
import org.openelis.gwt.event.GetMatchesHandler;
import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.widget.AutoComplete;
import org.openelis.gwt.widget.DateHelper;
import org.openelis.gwt.widget.Label;
import org.openelis.gwt.widget.TextBox;
import org.openelis.gwt.widget.TextBox.Case;
import org.openelis.gwt.widget.calendar.Calendar;
import org.openelis.gwt.widget.table.AutoCompleteCell;
import org.openelis.gwt.widget.table.CalendarCell;
import org.openelis.gwt.widget.table.CellEditor;
import org.openelis.gwt.widget.table.CellRenderer;
import org.openelis.gwt.widget.table.Column;
import org.openelis.gwt.widget.table.Container;
import org.openelis.gwt.widget.table.LabelCell;
import org.openelis.gwt.widget.table.Row;
import org.openelis.gwt.widget.table.Table;
import org.openelis.gwt.widget.table.TextBoxCell;

import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTMLTable;
import com.google.gwt.user.client.ui.Widget;

public class AuxTableColumn extends Column {
    protected GetMatchesHandler     screen;
    protected TextBoxCell<String>   alphaTextBox;
    protected TextBoxCell<Integer>  numericTextBox;
    protected TextBoxCell<Double>   numTextBox;
    protected CalendarCell          calendar;
    protected AutoCompleteCell      autoComplete;
    protected LabelCell<String>     label;

    protected Integer               alphaLowerId, alphaUpperId, alphaMixedId, timeId, numericId,
                    dateId, dateTimeId, dictionaryId;
    
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
    
    private AutoCompleteCell getAutoComplete(Case boxCase) {
    	AutoComplete auto;
    	Table table;
    	Column column;
    	
    	if(autoComplete == null) {
    	
    		auto = new AutoComplete();
    		auto.setWidth("270");
    		auto.setMaxLength(80);
        
    		table = new Table();
    		table.setVisibleRows(10);
    		column = table.addColumn();
    		column.setWidth(290);
        
    		auto.setPopupContext(table);
    		auto.addGetMatchesHandler(screen);
    		auto.setEnabled(true);
    		
    		autoComplete = new AutoCompleteCell(auto);
    	}
        
    	((AutoComplete)autoComplete.getWidget()).setCase(boxCase);
        return autoComplete;
    }

    private TextBoxCell<String> getAlphaTextbox(Case boxCase) {
    	TextBox<String> alpha;
    	
        if (alphaTextBox == null) {
            alpha = new TextBox<String>();
            alpha.setStyleName("ScreenTextBox");
            alpha.setMaxLength(80);
            
            alphaTextBox = new TextBoxCell<String>(alpha);
        }

        ((TextBox)alphaTextBox.getWidget()).setCase(boxCase);
        
        //alphaTextBox.setQueryMode( ((Screen)screen).state == State.QUERY);

        return alphaTextBox;
    }

    private TextBoxCell<Integer> getNumericTextbox() {
    	TextBox<Integer> numeric;
        //if(((Screen)screen).state == State.QUERY)
        //    return getAlphaTextbox(Case.MIXED);
        
        if (numericTextBox == null) {
            numeric = new TextBox<Integer>();
            numeric.setStyleName("ScreenTextBox");
            numeric.setMaxLength(80);
            
            numericTextBox = new TextBoxCell<Integer>(numeric);
        }
                
        return numericTextBox;
    }

    private CalendarCell getCalendar(byte begin, byte end) {
    	Calendar cal;
    	
        if (calendar == null) {
            cal = new Calendar();
            cal.setStyleName("ScreenCalendar");
            
            calendar = new CalendarCell(cal);
        }

        DateHelper helper = new DateHelper();
        helper.setBegin(begin);
        helper.setEnd(end);

        if (end == Datetime.DAY)
            helper.setPattern( ((Screen)screen).consts.get("datePattern"));
        else
            helper.setPattern( ((Screen)screen).consts.get("dateTimePattern"));

        ((Calendar)calendar.getWidget()).setHelper(helper);
        //calendar.init(begin, end, false);
        //calendar.setQueryMode( ((Screen)screen).state == State.QUERY);

        return calendar;
    }

    private LabelCell<String> getLabelCell() {
    	Label lb;
        
    	if (label == null) {
            lb = new Label();
            lb.setStyleName("ScreenLabel");
             label = new LabelCell<String>(lb);
        }

        return label;
    }

    private Object getCellWidget(Row row) {
        Integer typeId;
        typeId = getCellTypeId(row);

        if (alphaLowerId == null) {
            try {
                alphaLowerId = DictionaryCache.getIdFromSystemName("aux_alpha_lower");
                alphaUpperId = DictionaryCache.getIdFromSystemName("aux_alpha_upper");
                alphaMixedId = DictionaryCache.getIdFromSystemName("aux_alpha_mixed");
                timeId = DictionaryCache.getIdFromSystemName("aux_time");
                numericId = DictionaryCache.getIdFromSystemName("aux_numeric");
                dateId = DictionaryCache.getIdFromSystemName("aux_date");
                dateTimeId = DictionaryCache.getIdFromSystemName("aux_date_time");
                dictionaryId = DictionaryCache.getIdFromSystemName("aux_dictionary");
            } catch (Exception e) {
                Window.alert(e.getMessage());
            }
        }

        if (typeId == null)
            return getLabelCell();
        else if (alphaLowerId.equals(typeId))
            return getAlphaTextbox(Case.LOWER);
        else if (alphaUpperId.equals(typeId))
            return getAlphaTextbox(Case.UPPER);
        else if (alphaMixedId.equals(typeId) || timeId.equals(typeId))
            return getAlphaTextbox(Case.MIXED);
        else if (numericId.equals(typeId))
            return getNumericTextbox();
        else if (dateId.equals(typeId))
            return getCalendar(Datetime.YEAR, Datetime.DAY);
        else if (dateTimeId.equals(typeId))
            return getCalendar(Datetime.YEAR, Datetime.MINUTE);
        else if (timeId.equals(typeId))
            return getAlphaTextbox(Case.MIXED);
        else if (dictionaryId.equals(typeId))
            return getAutoComplete(Case.MIXED);

        return null;
    }



    public void setScreen(GetMatchesHandler screen) {
        this.screen = screen;
    }
    
    
    private Integer getCellTypeId(Row row) {
        AuxDataBundle data = (AuxDataBundle)row.getData();

        if (data == null)
            return null;
        else
            return data.fieldDO.getTypeId();
    }
}
