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
import org.openelis.gwt.event.GetMatchesHandler;
import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.widget.AutoComplete;
import org.openelis.gwt.widget.CalendarLookUp;
import org.openelis.gwt.widget.DateField;
import org.openelis.gwt.widget.IntegerField;
import org.openelis.gwt.widget.Label;
import org.openelis.gwt.widget.StringField;
import org.openelis.gwt.widget.TextBox;
import org.openelis.gwt.widget.TextBox.Case;
import org.openelis.gwt.widget.table.TableColumn;
import org.openelis.gwt.widget.table.TableDataRow;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;

public class AuxTableColumn extends TableColumn {
    protected GetMatchesHandler     screen;
    protected TextBox               alphaTextBox;
    protected TextBox<Double>       numTextBox;
    protected CalendarLookUp        calendar;
    protected AutoComplete<Integer> autoComplete;
    protected Label                 label;

    protected Integer               alphaLowerId, alphaUpperId, alphaMixedId, timeId, numericId,
                    dateId, dateTimeId, dictionaryId;

    public Widget getDisplayWidget(TableDataRow row) {
        setColumnWidget(getCellWidget(row));
        return super.getDisplayWidget(row);
    }

    public void loadWidget(Widget widget, TableDataRow row) {
        setColumnWidget(getCellWidget(row));
        super.loadWidget(widget, row);
    }

    public Widget getWidgetEditor(TableDataRow row) {
        setColumnWidget(getCellWidget(row));
        return super.getWidgetEditor(row);
    }

    private AutoComplete<Integer> getAutoComplete(Case boxCase) {
        if (autoComplete == null) {
            autoComplete = new AutoComplete<Integer>();
            autoComplete.setWidth("270");
            autoComplete.setTableWidth("auto");
            autoComplete.setField(new IntegerField());

            ArrayList<TableColumn> cols = new ArrayList<TableColumn>();
            TableColumn col = new TableColumn();
            col.controller = autoComplete;
            col.setCurrentWidth(290);
            Label label = new Label();
            label.setField(new StringField());
            col.setColumnWidget(label);
            cols.add(col);
            autoComplete.setColumns(cols);

            autoComplete.init();
            autoComplete.addGetMatchesHandler(screen);
            autoComplete.enable(true);
        }

        autoComplete.setCase(boxCase);

        return autoComplete;
    }

    private TextBox getAlphaTextbox(Case boxCase) {
        if (alphaTextBox == null) {
            alphaTextBox = new TextBox();
            alphaTextBox.setStyleName("ScreenTextBox");
            alphaTextBox.setField(new StringField());
            alphaTextBox.setLength(80);
        }

        alphaTextBox.setCase(boxCase);

        return alphaTextBox;
    }

    private CalendarLookUp getCalendar(byte begin, byte end) {
        if (calendar == null) {
            calendar = new CalendarLookUp();
            calendar.setStyleName("ScreenCalendar");
        }

        DateField field = new DateField();
        field.setBegin(begin);
        field.setEnd(end);

        if (end == Datetime.DAY)
            field.setFormat( ((Screen)screen).consts.get("datePattern"));
        else
            field.setFormat( ((Screen)screen).consts.get("dateTimePattern"));

        calendar.setField(field);
        calendar.init(begin, end, false);

        return calendar;
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
        Integer typeId;
        typeId = getCellTypeId(row);
        
        if(alphaLowerId == null){
            try{
                alphaLowerId = DictionaryCache.getIdFromSystemName("aux_alpha_lower");
                alphaUpperId = DictionaryCache.getIdFromSystemName("aux_alpha_upper");
                alphaMixedId = DictionaryCache.getIdFromSystemName("aux_alpha_mixed");
                timeId = DictionaryCache.getIdFromSystemName("aux_time");
                numericId = DictionaryCache.getIdFromSystemName("aux_numeric");
                dateId = DictionaryCache.getIdFromSystemName("aux_date");
                dateTimeId = DictionaryCache.getIdFromSystemName("aux_date_time");
                dictionaryId = DictionaryCache.getIdFromSystemName("aux_dictionary");
            }catch(Exception e){
                Window.alert(e.getMessage());
            }
        }
        

        if (typeId == null)
            return getLabel();
        else if (alphaLowerId.equals(typeId))
            return getAlphaTextbox(Case.LOWER);
        else if (alphaUpperId.equals(typeId))
            return getAlphaTextbox(Case.UPPER);
        else if (alphaMixedId.equals(typeId) || timeId.equals(typeId))
            return getAlphaTextbox(Case.MIXED);
        else if (numericId.equals(typeId))
            return getAlphaTextbox(Case.MIXED);
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

    private Integer getCellTypeId(TableDataRow row) {
        AuxDataBundle data = (AuxDataBundle)row.data;
        
        if (data == null)
            return null;
        else
            return data.fieldDO.getTypeId();
    }

    public void setScreen(GetMatchesHandler screen) {
        this.screen = screen;
    }
}
