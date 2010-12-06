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
package org.openelis.modules.report.client;

import java.util.ArrayList;

import org.openelis.domain.OptionListItem;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.Datetime;
import org.openelis.gwt.common.Util;
import org.openelis.gwt.common.data.Query;
import org.openelis.gwt.common.data.QueryData;
import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.screen.ScreenEventHandler;
import org.openelis.gwt.widget.AppButton;
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
import org.openelis.report.ReportStatus;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class ReportScreen extends Screen {   
    
    protected ArrayList<Prompt>  reportParameters;
    
    protected AppButton          runReportButton, resetButton;
    
    protected String             title, reportURL, runReportInterface, getPromptsInterface;   
    
    protected static String      defaultPrinter, defaultBarcodePrinter;       
    
    public ReportScreen() throws Exception {         
        title            = null;
        reportURL        = null;
        runReportInterface = "runReport";
        getPromptsInterface = "getPrompts";
        reportParameters = new ArrayList<Prompt>();     
    }   
    
    protected void initialize() {
        getReportParameters();    
        window.setName(title);
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }   

    private void createReportWindow() {   
        int i;
        VerticalPanel main;
        FlexTable tp;
        HorizontalPanel hp;
        Prompt p;
        Field f;
        Widget w;

                                        
        main = new VerticalPanel();
        main.setStyleName("WhiteContentPanel");  
        def.getPanel().add(main);
        tp = new FlexTable();
        tp.setStyleName("Form");
        main.add(tp);
     
        //for (Prompt p : reportParameters) {    
        for (i = 0; i < reportParameters.size(); i++) {     
            p = reportParameters.get(i);
            //
            // decode and create component objects
            //
            if (p.isHidden())
                continue;
                        
            switch(p.getType()) {
                case ARRAY:
                case ARRAYMULTI:                                        
                    w = createDropdown(p);
                    ((Dropdown<String>)w).enable(true);                    
                    break;   
                case CHECK:
                    w = createCheckBox(p);
                    ((CheckBox)w).enable(true);                        
                    break;   
                case STRING:                                        
                    f = new StringField();                    
                    w = createTextBox(f, p);
                    ((TextBox)w).enable(true);    
                case SHORT:
                case INTEGER:                    
                    f = new IntegerField();                    
                    w = createTextBox(f, p);
                    ((TextBox)w).enable(true);    
                    break;
                case FLOAT:
                case DOUBLE:                
                    f = new DoubleField();                    
                    w = createTextBox(f, p);
                    ((TextBox)w).enable(true);    
                    break;                                   
                case DATETIME:                    
                    f = new DateField();
                    w = createTextBox(f, p);
                    ((DateField)f).setBegin(getDatetimeCode(p.getDatetimeStartCode()));
                    ((DateField)f).setEnd(getDatetimeCode(p.getDatetimeEndCode()));
                    ((TextBox)w).enable(true);    
                    break;
                default:
                    w = null;
                    Window.alert("Error: Type "+ p.getType()+ " not supported; Please notify IT");                                                        
            }            
            
            if (w != null) {
                def.setWidget(w, p.getName());            
                addLabelAndWidget(p, tp, w);
            }
        }
        
        hp = new HorizontalPanel();
        hp.setHeight("10px");
        main.add(hp);        
        
        hp = new HorizontalPanel();
        runReportButton = createAppButton(consts.get("runReport"));
        runReportButton.enable(true);
        hp.add(runReportButton);        
        def.setWidget(runReportButton, "run");
        
        addScreenHandler(runReportButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                runReport();
            }           
        });       
        
        resetButton = createAppButton(consts.get("reset"));        
        resetButton.enable(true);
        hp.add(resetButton);                
        def.setWidget(resetButton, "reset");
        
        addScreenHandler(resetButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                reset();
            }           
        });
        
        main.add(hp);
        
        // this is done to adjust the width of the window so that it can display the
        // message associated with the key "correctErrors" without being expanded when 
        // the message shows at its bottom
        if (tp.getOffsetWidth() < 335)
            main.setWidth("335px");
        
        main.setCellHorizontalAlignment(hp,HasAlignment.ALIGN_CENTER);
    }
    
    protected void runReport() {    
        Query query;
        ReportStatus st;
        Dropdown<String> d;
        TableDataRow r;
        String key;
        
        if ( !validate()) {
            window.setError(consts.get("correctErrors"));
            return;
        }
                
        query = new Query();
        query.setFields(getQueryFields());
        window.setBusy(consts.get("genReportMessage"));
        try {
            st = service.call(runReportInterface, query);
            if (ReportStatus.Status.SAVED == st.getStatus()) {
                window.setDone("Generated file "+ st.getMessage());
                d = (Dropdown<String>)def.getWidget("PRINTER");
                if (d != null) {
                    r = d.getSelection();
                    if (r != null && r.key != null) {
                        key = (String)r.key;
                        if (key.startsWith("-") && key.endsWith("-")) {
                            Window.open("report?tempfile="+st.getMessage()+"&contentType="+"application/pdf", title, null);                            
                        }
                    }
                } else {                
                    Window.open("report?tempfile="+st.getMessage()+"&contentType="+"application/pdf", title, null);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Window.alert(e.getMessage());
            window.clearStatus();
        }                  
    }
    
    public ArrayList<QueryData> getQueryFields() {
        ArrayList<QueryData> list;
        QueryData field;

        list = new ArrayList<QueryData>(); 
        for (String key : def.getWidgets().keySet()) {
            if (def.getWidget(key) instanceof Dropdown)                 
                field = getQuery((Dropdown<String>)def.getWidget(key), key);
            else if (def.getWidget(key) instanceof TextBox) 
                field = getQuery((TextBox)def.getWidget(key), key);  
            else
                continue;
            if (field != null)
                list.add(field);
        }
        return list;
    }
    
    private void getReportParameters() {   
        window.setBusy(consts.get("gettingReportParam"));
        service.callList(getPromptsInterface, new AsyncCallback<ArrayList<Prompt>>() {
            public void onSuccess(ArrayList<Prompt> result) {
                reportParameters =  result;  
                createReportWindow();
                window.setDone(consts.get("loadCompleteMessage"));
            }
            
            public void onFailure(Throwable caught) {
                window.close();
                Window.alert("Failed to get parameters for "+ title);                
            }                    
        });
    }    
    
    private void reset() {
        Dropdown<String> dd;
        TextBox tb;
        ArrayList<TableDataRow> data;
        
        for (String key : def.getWidgets().keySet()) {            
            if (def.getWidget(key) instanceof Dropdown) {                 
                dd = ((Dropdown<String>)def.getWidget(key));                
                dd.clearExceptions();                
                data = dd.getData();
                for (Prompt p : reportParameters) {
                    if (key.equals(p.getName())) {
                        resetDropdown(p, data, dd);
                        break;
                    }
                }
            } else if (def.getWidget(key) instanceof TextBox) { 
                tb = ((TextBox)def.getWidget(key)); 
                tb.setFieldValue("");
                tb.clearExceptions();
            }
        }
        
        window.clearStatus();
    }  
    
    protected QueryData getQuery(Dropdown<String> dd, String key) {
        ArrayList<TableDataRow> sel;
        QueryData qd;
        boolean needComma;

        sel = dd.getSelections();
        if (sel.size() == 0)
            return null;

        qd = new QueryData();
        qd.key = key;
        qd.type = QueryData.Type.STRING;

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
        
        if ("PRINTER".equals(key)) 
            defaultPrinter = qd.query;
        else if ("BARCODE".equals(key)) 
            defaultBarcodePrinter = qd.query;

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
    
    private Dropdown<String> createDropdown(Prompt p) {
        Dropdown<String> d;
        TableColumn c;    
        Label<String> dl;
        StringField f;
        ArrayList<TableDataRow> l;
        int w;
        
        w = (p.getWidth() != null && p.getWidth() > 0) ? p.getWidth() : 100;
 
        //
        // create a new dropdown
        //                                        
        d = new Dropdown<String>();  
        f = new StringField();
        f.required = p.isRequired();
        d.setField(f);
        d.setTableWidth("auto");
        d.dropwidth = w + "px";
        d.setMultiSelect(p.getMultiSelect());  
        
        dl = new Label<String>();   
        dl.setField(f);                
        dl.setWidth(d.dropwidth);
        
        d.setColumns(new ArrayList<TableColumn>());
        c = new TableColumn();
        c.controller = d;
        c.setCurrentWidth(w);
        c.setColumnWidget(dl);
        d.getColumns().add(c);
        d.setup();
                          
        l = new ArrayList<TableDataRow>();
        for (OptionListItem o : p.getOptionList())  
            l.add(new TableDataRow(o.getKey(), o.getLabel()));    
        d.load(l);
        
        resetDropdown(p, l, d);
        return d;
    }
    
    private CheckBox createCheckBox(Prompt p) {
        CheckBox cb;
        CheckField f; 
        
        cb = new CheckBox();
        f = new CheckField();
        f.required = p.isRequired();
        cb.setField(f);
        if (p.getWidth() != null && p.getWidth() > 0)
            cb.setWidth(p.getWidth()+ "px");                                                                                           
        
        return cb;
    }        
    
    
    private AppButton createAppButton(String label) {
        AppButton b;
        Label txt;
        
        b = new AppButton();
        txt = new Label(label);
        txt.setStyleName("ScreenLabel");
        b.setWidget(txt);
        
        return b;
    }
    
    private TextBox createTextBox(Field f, Prompt p) {
        TextBox t;
        
        t = new TextBox();
        t.setStyleName("ScreenTextBox");
        t.addFocusHandler(Util.focusHandler);
        t.addBlurHandler(Util.focusHandler);
        f.required = p.isRequired();
        t.setField(f);
        
        if (p.getMask() != null) 
            t.setMask(p.getMask());
        if (p.getLength() != null)
            t.setMaxLength(p.getLength());
        if (p.getCase() == Prompt.Case.LOWER)
            t.setCase(Case.LOWER);
        else if (p.getCase() == Prompt.Case.UPPER)
            t.setCase(Case.UPPER);
        if (p.getWidth() != null && p.getWidth() > 0)
            t.setWidth(p.getWidth()+ "px");                    
        else
            t.setWidth("100px");
        
        return t;
    }
    
    private void addLabelAndWidget(Prompt p, FlexTable tp, Widget w) {
        int row;
        Label pr;
        HorizontalPanel hp;        
        
        row = tp.getRowCount();        
        //
        // add a label and widget if both are present
        //
        if (!DataBaseUtil.isEmpty(p.getPrompt())) {            
            pr = new Label(p.getPrompt());
            pr.setStyleName("Prompt");
            tp.setWidget(row, 0, pr); 
            hp = new HorizontalPanel();
            hp.add(w);
            tp.setWidget(row, 1, hp);                              
        } else if (row > 0) {         
            // 
            // add the widget to the previous row's list of widgets 
            //
            hp = (HorizontalPanel)tp.getWidget(row-1, 1);
            hp.insert(w,hp.getWidgetCount());
        } else {
            //
            // special case; if first row doesn't have a label
            //
            tp.setWidget(row, 1, w);       
        }
    }
    
    private byte getDatetimeCode(Prompt.Datetime code) {
        switch (code) {
            case YEAR: 
                return Datetime.YEAR; 
            case MONTH: 
                return Datetime.MONTH;
            case DAY: 
                return Datetime.DAY;
            case HOUR: 
                return Datetime.HOUR;
            case MINUTE: 
                return Datetime.MINUTE;
            case SECOND: 
                return Datetime.SECOND;            
        }
        
        return 0;
    }
    
    private void resetDropdown(Prompt p, ArrayList<TableDataRow> l, Dropdown<String> d) {
        String key;
        
        if (p.getDefaultValue() != null)
            d.setValue(p.getDefaultValue());
        else if ("PRINTER".equals(p.getName()) && defaultPrinter != null) 
            d.setValue(defaultPrinter);
        else if ("BARCODE".equals(p.getName()) && defaultBarcodePrinter != null) 
            d.setValue(defaultBarcodePrinter);
        else {             
            //
            // set the value to the first entry that doesn't have a key like 
            // "-view-" or "-xsl-" etc.
            //
            for (TableDataRow r : l ) { 
                key = (String)r.key;
                if (!key.startsWith("-") && !key.endsWith("-")) {
                    d.setValue(key);
                    break;
                }
            }            
        }
    }
}

