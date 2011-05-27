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
package org.openelis.web.util;

import java.util.ArrayList;
import java.util.HashMap;

import org.openelis.domain.OptionListItem;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.Datetime;
import org.openelis.gwt.common.ReportStatus;
import org.openelis.gwt.common.Util;
import org.openelis.gwt.common.data.Query;
import org.openelis.gwt.common.data.QueryData;
import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.screen.ScreenDefInt;
import org.openelis.gwt.screen.ScreenEventHandler;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.CalendarLookUp;
import org.openelis.gwt.widget.CheckBox;
import org.openelis.gwt.widget.CheckField;
import org.openelis.gwt.widget.DateField;
import org.openelis.gwt.widget.DoubleField;
import org.openelis.gwt.widget.Dropdown;
import org.openelis.gwt.widget.Field;
import org.openelis.gwt.widget.IntegerField;
import org.openelis.gwt.widget.Label;
import org.openelis.gwt.widget.StringField;
import org.openelis.gwt.widget.TextBox;
import org.openelis.gwt.widget.TextBox.Case;
import org.openelis.gwt.widget.table.TableColumn;
import org.openelis.gwt.widget.table.TableDataRow;
import org.openelis.report.Prompt;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.http.client.URL;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class ReportScreenUtility {
    protected ArrayList<Prompt> reportParameters;

    protected AppButton         runReportButton, resetButton;

    protected String            name, attachmentName, runReportInterface, promptsInterface;
    protected ScreenDefInt      def;

    public ReportScreenUtility(ScreenDefInt def) {
        this.def = def;
    }

    /**
     * Returns the value of all the prompts in query format
     */
    public ArrayList<QueryData> getQueryFields() {
        ArrayList<QueryData> list;
        QueryData field;

        list = new ArrayList<QueryData>();
        for (String key : def.getWidgets().keySet()) {
            if (def.getWidget(key) instanceof Dropdown)
                field = getQuery((Dropdown<String>)def.getWidget(key), key);
            else if (def.getWidget(key) instanceof TextBox)
                field = getQuery((TextBox)def.getWidget(key), key);
            else if (def.getWidget(key) instanceof CalendarLookUp)
                field = getQuery((CalendarLookUp)def.getWidget(key), key);
            else
                continue;
            if (field != null)
                list.add(field);
        }
        return list;
    }

    /*
     * Returns the field specific query object
     */
    protected QueryData getQuery(Dropdown<String> dd, String key) {
        ArrayList<TableDataRow> sel;
        QueryData qd;
        boolean needComma;
        Field f;

        sel = dd.getSelections();
        if (sel.size() == 0)
            return null;
        f = dd.getField();

        qd = new QueryData();
        qd.key = key;
        if (f instanceof StringField)
            qd.type = QueryData.Type.STRING;
        if (f instanceof IntegerField)
            qd.type = QueryData.Type.INTEGER;

        qd.query = "";
        needComma = false;
        for (TableDataRow row : sel) {
            if (needComma)
                qd.query += ",";
            if (row.key != null) {
                qd.query += row.key.toString();
                needComma = true;
            }
        }
        return qd;
    }

    protected QueryData getQuery(TextBox tb, String key) {
        QueryData qd;
        Field field;

        field = tb.getField();

        if (field.getValue() == null)
            return null;

        qd = new QueryData();
        qd.query = field.getValue().toString();
        qd.key = key;
        if (field instanceof StringField)
            qd.type = QueryData.Type.STRING;
        else if (field instanceof IntegerField)
            qd.type = QueryData.Type.INTEGER;
        else if (field instanceof DoubleField)
            qd.type = QueryData.Type.DOUBLE;
        else if (field instanceof DateField)
            qd.type = QueryData.Type.DATE;
        return qd;
    }

    protected QueryData getQuery(CalendarLookUp c, String key) {
        QueryData qd;
        DateField field;

        field = (DateField)c.getField();

        if (field.getValue() == null)
            return null;

        qd = new QueryData();
        qd.query = field.formatQuery();
        qd.key = key;
        qd.type = QueryData.Type.DATE;

        return qd;
    }

    /**
     * Parses the clause in userPermission and returns the values in a format
     * which can be understood by the Query Builder.
     */
    public HashMap<String, String> parseClause(String clause) {
        HashMap<String, String> map;
        String[] str, str1;
        String key, value;

        map = new HashMap<String, String>();
        str = clause.split(";");
        for (int i = 0; i < str.length; i++ ) {
            str1 = str[i].split(":");
            key = str1[0];
            value = str1[1];
            if (value.contains(","))
                value = value.replace(",", "|");
            map.put(key, value);
        }

        return map;
    }

}
