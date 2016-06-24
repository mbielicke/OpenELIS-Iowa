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
package org.openelis.modules.report.client;

import java.io.Serializable;
import java.util.ArrayList;

import org.openelis.constants.Messages;
import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.screen.ScreenEventHandler;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.CalendarLookUp;
import org.openelis.gwt.widget.CheckBox;
import org.openelis.gwt.widget.CheckField;
import org.openelis.gwt.widget.DateField;
import org.openelis.gwt.widget.DoubleField;
import org.openelis.gwt.widget.Dropdown;
import org.openelis.gwt.widget.Field;
import org.openelis.gwt.widget.HasField;
import org.openelis.gwt.widget.IntegerField;
import org.openelis.gwt.widget.Label;
import org.openelis.gwt.widget.StringField;
import org.openelis.gwt.widget.TextBox;
import org.openelis.gwt.widget.TextBox.Case;
import org.openelis.gwt.widget.table.TableColumn;
import org.openelis.gwt.widget.table.TableDataRow;
import org.openelis.manager.Preferences1;
import org.openelis.modules.preferences1.client.PreferencesService1Impl;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.Datetime;
import org.openelis.ui.common.OptionListItem;
import org.openelis.ui.common.Prompt;
import org.openelis.ui.common.ReportStatus;
import org.openelis.ui.common.data.Query;
import org.openelis.ui.common.data.QueryData;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
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

/**
 * This class provides the basic framework for calling a report. Each report
 * should extend this class and specify the report servlet service to get report
 * prompts and run the report.
 */
public abstract class ReportScreen<T extends Serializable> extends Screen {

    protected ArrayList<Prompt> reportParameters;

    protected AppButton         runReportButton, resetButton;

    protected String            name, attachmentName;

    protected Preferences1      preferences;
    
    protected boolean           isScreenInitialized;
    

	protected ReportScreen() throws Exception {
		name = null;
		attachmentName = null;

		reportParameters = new ArrayList<Prompt>();
		
		DeferredCommand.addCommand(new Command() {
            public void execute() {
                initialize();
            }
        });
	}

	protected void initialize() {
		getReportParameters();
		/*
		 * this check is here to make sure that if the name is not specified, because
		 * the class implementing ReportScreen doesn't make a screen to show up 
		 * but acts as a proxy for other screens like Sample Tracking that run reports,
		 * the name of that screen doesn't get blanked out 
		 */
		if (!DataBaseUtil.isEmpty(name))
		    window.setName(name);
	}

	/**
	 * Gets/sets the name (window title) for this report window
	 */
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Gets/sets the attachment filename for files that are returned to the
	 * browser
	 */
	public String getAttachmentName() {
		return attachmentName;
	}

	public void setAttachmentName(String name) {
		this.attachmentName = name;
	}
	
	public void setFieldValue(String key, Object value) {
	    Widget w;
	    
	     w = def.getWidget(key);
	     if (w != null && w instanceof HasField)
	         ((HasField)w).setFieldValue(value);	     
	}

    public Object getFieldValue(String key) {
        Widget w;

        w = def.getWidget(key);
        if (w != null && w instanceof HasField)
            return ((HasField)w).getFieldValue();

        return null;
    }
    
    public boolean isScreenInitialized() {
        return isScreenInitialized;
    }
        	
	/**
	 * Gets the prompts from the report
	 */
	protected void getReportParameters() {
		window.setBusy(Messages.get().gettingReportParam());

		try {
		    reportParameters = getPrompts();
		    createReportWindow();
            window.setDone(Messages.get().loadCompleteMessage());
		} catch (Exception e) {
		    window.close();
            Window.alert("Failed to get parameters for " + name);
        }		
	}
	
	
	protected abstract ArrayList<Prompt> getPrompts() throws Exception;

	/**
	 * Draws the prompts and fields in the report window
	 */
	protected void createReportWindow() throws Exception {
		int i;
		VerticalPanel main;
		FlexTable tp;
		HorizontalPanel hp;
		Prompt p;
		Field f;
		Widget w;

		main = new VerticalPanel();
		main.setStyleName("WhiteContentPanel");
		main.setWidth("100%");
		main.setHeight("100%");
		def.getPanel().add(main);
		tp = new FlexTable();
		tp.setStyleName("Form");
		main.add(tp);

		// for (Prompt p : reportParameters) {
		for (i = 0; i < reportParameters.size(); i++) {
			p = reportParameters.get(i);
			//
			// decode and create component objects
			//

            switch (p.getType()) {
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
                    break;
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
                    w = createCalendar(p);
                    ((CalendarLookUp)w).enable(true);
                    break;
                default:
                    w = null;
                    Window.alert("Error: Type " + p.getType() + " not supported; Please notify IT");
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
		runReportButton = createAppButton(Messages.get().runReport());
		runReportButton.enable(true);
		hp.add(runReportButton);
		def.setWidget(runReportButton, "run");

		addScreenHandler(runReportButton, new ScreenEventHandler<Object>() {
			public void onClick(ClickEvent event) {
				runReport();
			}
		});

		resetButton = createAppButton(Messages.get().reset());
		resetButton.enable(true);
		hp.add(resetButton);
		def.setWidget(resetButton, "reset");

		addScreenHandler(resetButton, new ScreenEventHandler<Object>() {
			public void onClick(ClickEvent event) {
			    try {
			        reset();
			    } catch (Exception e) {
                    Window.alert(e.getMessage());
                    e.printStackTrace();
                }
			}
		});

		main.add(hp);

		// this is done to adjust the width of the window so that it can display
		// the
		// message associated with the key "correctErrors" without being
		// expanded when
		// the message shows at its bottom
		if (tp.getOffsetWidth() < 335)
			main.setWidth("335px");

		main.setCellHorizontalAlignment(hp, HasAlignment.ALIGN_CENTER);
		isScreenInitialized = true;
	}

    /**
	 * Builds a query array from the specified parameters and calls the report's
	 * run method.
	 */
	protected void runReport() {
		Query query;

		if (!validate()) {
			window.setError(Messages.get().correctErrors());
			return;
		}

		query = new Query();
		query.setFields(getQueryFields());
		runReport((T)query);		
	}
	
	/**
     * Provides a more generic interface to run reports so that screens not 
     * implementing ReportScreen can utilize this functionality too
     */
    public abstract void runReport(T rpc, AsyncCallback<ReportStatus> callback);
    
    public void runReport(T query) {
        window.setBusy(Messages.get().genReportMessage());

        runReport(query, new AsyncCallback<ReportStatus>() {
            public void onSuccess(ReportStatus status) {
                String url;

                if (status.getStatus() == ReportStatus.Status.SAVED) {
                    url = "/openelis/openelis/report?file=" + status.getMessage();
                    if (attachmentName != null)
                        url += "&attachment=" + attachmentName;

                    Window.open(URL.encode(url), name, null);
                    window.setDone("Generated file " + status.getMessage());
                } else {
                    window.setDone(status.getMessage());
                }
            }

            public void onFailure(Throwable caught) {
                window.setError("Failed");
                Window.alert(caught.getMessage());
            }
        });
    }

	/**
	 * Resets all the fields to their original report specified values
	 */
    public void reset() throws Exception {
        byte s, e;
        Dropdown<String> dd;
        TextBox tb;
        CalendarLookUp cl;
        CheckBox cb;
        ArrayList<TableDataRow> data;
        DateTimeFormat format;

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
                tb.clearExceptions();
                for (Prompt p : reportParameters) {
                    if (key.equals(p.getName())) {
                        tb.setValue(p.getDefaultValue());
                        break;
                    }
                }
            } else if (def.getWidget(key) instanceof CalendarLookUp) {
                cl = ((CalendarLookUp)def.getWidget(key));
                cl.clearExceptions();
                for (Prompt p : reportParameters) {
                    if (key.equals(p.getName())) {
                        if (p.getDefaultValue() != null) {
                            s = getDatetimeCode(p.getDatetimeStartCode());
                            e = getDatetimeCode(p.getDatetimeEndCode());
                            if (e > Datetime.DAY)
                                format = DateTimeFormat.getFormat("yyyy-MM-dd HH:mm");
                            else
                                format = DateTimeFormat.getFormat("yyyy-MM-dd");

                            try {
                                cl.setValue(Datetime.getInstance(s, e,
                                                                 format.parse(p.getDefaultValue())));
                            } catch (IllegalArgumentException iargE) {
                                // we don't set a default if we cannot parse it
                            }
                        } else {
                            cl.setValue(null);
                        }
                        break;
                    }
                }
            } else if (def.getWidget(key) instanceof CheckBox) {
                cb = ((CheckBox)def.getWidget(key));
                cb.clearExceptions();
                for (Prompt p : reportParameters) {
                    if (key.equals(p.getName())) {
                        cb.setValue(p.getDefaultValue());
                        break;
                    }
                }
            }

        }

        window.clearStatus();
    }

	/**
	 * Resets the dropdown to prompt specified value
	 */
	protected void resetDropdown(Prompt p, ArrayList<TableDataRow> l, Dropdown<String> d) throws Exception {
		String defaultPrinter,defaultBarcodePrinter,location;

		defaultPrinter = null;
		defaultBarcodePrinter = null;
		location = null;
		
		preferences =  PreferencesService1Impl.INSTANCE.userRoot();
		if (preferences != null) {
		    defaultPrinter = preferences.get("default_printer", null);
		    defaultBarcodePrinter = preferences.get("default_bar_code_printer", null);
		    location = preferences.get("location", null);
		}
		if (p.getDefaultValue() != null) {
			d.setValue(p.getDefaultValue());
		} else if ("PRINTER".equals(p.getName()) && defaultPrinter != null) {
			d.setValue(defaultPrinter);
		} else if ("BARCODE".equals(p.getName()) && defaultBarcodePrinter != null) {
			d.setValue(defaultBarcodePrinter);
        } else if ("LOCATION".equals(p.getName()) && location != null) {
            d.setValue(location);
		} else {
			if (l.size() > 0)
				d.setValue((String) l.get(0).key);
		}
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
				field = getQuery((Dropdown<String>) def.getWidget(key), key);
			else if (def.getWidget(key) instanceof TextBox)
				field = getQuery((TextBox) def.getWidget(key), key);
			else if (def.getWidget(key) instanceof CalendarLookUp)
				field = getQuery((CalendarLookUp) def.getWidget(key), key);
	        else if (def.getWidget(key) instanceof CheckBox)
	            field = getQuery((CheckBox) def.getWidget(key), key);
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

		sel = dd.getSelections();
		if (sel.size() == 0)
			return null;

		qd = new QueryData();
		qd.setKey(key);
		qd.setType(QueryData.Type.STRING);

		qd.setQuery("");
		needComma = false;
		for (TableDataRow row : sel) {
			if (needComma)
				qd.setQuery(qd.getQuery() + ",");
			if (row.key != null) {
				qd.setQuery(qd.getQuery() + row.key.toString());
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
		qd.setQuery(field.getValue().toString());
		qd.setKey(key);
		if (field instanceof StringField)
			qd.setType(QueryData.Type.STRING);
		else if (field instanceof IntegerField)
			qd.setType(QueryData.Type.INTEGER);
		else if (field instanceof DoubleField)
			qd.setType(QueryData.Type.DOUBLE);
		else if (field instanceof DateField)
			qd.setType(QueryData.Type.DATE);
		return qd;
	}

	protected QueryData getQuery(CalendarLookUp c, String key) {
		QueryData qd;
		DateField field;

		field = (DateField) c.getField();

		if (field.getValue() == null)
			return null;

		qd = new QueryData();
		qd.setQuery(field.formatQuery());
		qd.setKey(key);
		qd.setType(QueryData.Type.DATE);

		return qd;
	}
	
	private QueryData getQuery(CheckBox c, String key) {
	    QueryData qd;
        CheckField field;

        field = (CheckField) c.getField();

        if (field.getValue() == null)
            return null;

        qd = new QueryData();
        qd.setQuery(field.formatQuery());
        qd.setKey(key);
        qd.setType(QueryData.Type.STRING);

        return qd;
	}

	protected Dropdown<String> createDropdown(Prompt p) throws Exception {
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

	protected CheckBox createCheckBox(Prompt p) {
		CheckBox cb;
		CheckField f;

		cb = new CheckBox();
		f = new CheckField();
		f.required = p.isRequired();
		cb.setField(f);
		if (p.getDefaultValue() != null)
		    cb.setValue(p.getDefaultValue());
		if (p.getWidth() != null && p.getWidth() > 0)
			cb.setWidth(p.getWidth() + "px");

		return cb;
	}

	protected AppButton createAppButton(String label) {
		AppButton b;
		Label txt;

		b = new AppButton();
		txt = new Label(label);
		txt.setStyleName("ScreenLabel");
		b.setWidget(txt);

		return b;
	}

	protected TextBox createTextBox(Field f, Prompt p) {
		final TextBox t;

		t = new TextBox();
		t.setStyleName("ScreenTextBox");
		t.addFocusHandler(new FocusHandler() {  
            @Override
            public void onFocus(FocusEvent event) {
                t.addStyleName("Focus");
            }
        });
		t.addBlurHandler(new BlurHandler() { 
            @Override
            public void onBlur(BlurEvent event) {
                t.removeStyleName("Focus");
            }
        });
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
			t.setWidth(p.getWidth() + "px");
		else
			t.setWidth("100px");
		t.setValue(p.getDefaultValue());

		return t;
	}

	protected CalendarLookUp createCalendar(Prompt p) {
		byte s, e;
		DateField f;
		CalendarLookUp c;
		DateTimeFormat format;

		s = getDatetimeCode(p.getDatetimeStartCode());
		e = getDatetimeCode(p.getDatetimeEndCode());

		f = new DateField();
		f.setBegin(s);
		f.setEnd(e);

		c = new CalendarLookUp();
		c.init(s, e, false);

		c.setStyleName("ScreenCalendar");
		f.required = p.isRequired();
		c.setField(f);

		if (p.getWidth() != null && p.getWidth() > 0)
			c.setWidth(p.getWidth() + "px");
		else
			c.setWidth("100px");
        if (p.getDefaultValue() != null) {
            if (e > Datetime.DAY)
                format = DateTimeFormat.getFormat("yyyy-MM-dd HH:mm");
            else
                format = DateTimeFormat.getFormat("yyyy-MM-dd");
            
            try {
                c.setValue(Datetime.getInstance(s, e, format.parse(p.getDefaultValue())));
            } catch (IllegalArgumentException iargE) {
                // we don't set a default if we cannot parse it
            }
        }
		return c;
	}

	/**
	 * Places the specified widget after the label.
	 */
	protected void addLabelAndWidget(Prompt p, FlexTable tp, Widget w) {
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
			pr.setWordWrap(false);
			hp = new HorizontalPanel();
			hp.add(w);
			if (!p.isHidden()) {
			    tp.setWidget(row, 0, pr);
			    tp.setWidget(row, 1, hp);
			}
		} else if (row > 0) {
			//
			// add the widget to the previous row's list of widgets
			//
			hp = (HorizontalPanel) tp.getWidget(row - 1, 1);
			hp.insert(w, hp.getWidgetCount());
		} else {
			//
			// special case; if first row doesn't have a label
			//
			tp.setWidget(row, 1, w);
		}
	}

	/**
	 * Converts the Prompt's date-time code to Datetime class's values
	 */
	protected byte getDatetimeCode(Prompt.Datetime code) {
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
	
}