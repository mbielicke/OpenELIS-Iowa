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

import java.util.ArrayList;
import java.util.Date;

import org.openelis.domain.OptionListItem;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.Datetime;
import org.openelis.gwt.common.ReportStatus;
import org.openelis.gwt.common.Util;
import org.openelis.gwt.common.data.Query;
import org.openelis.gwt.common.data.QueryData;
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
public class ReportScreen extends Screen {

	protected ArrayList<Prompt> reportParameters;

	protected AppButton runReportButton, resetButton;

	protected String name, attachmentName, runReportInterface,
			promptsInterface;

	protected static String defaultPrinter, defaultBarcodePrinter;

	protected ReportScreen() throws Exception {
		name = null;
		attachmentName = null;
		runReportInterface = "runReport";
		promptsInterface = "getPrompts";
		reportParameters = new ArrayList<Prompt>();

		DeferredCommand.addCommand(new Command() {
			public void execute() {
				initialize();
			}
		});
	}

	protected void initialize() {
		getReportParameters();
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

	/*
	 * Gets/sets the report's getPrompt interface method name
	 */
	public String getPromptsInterface() {
		return promptsInterface;
	}

	public void setPromptsInterface(String promptsInterface) {
		this.promptsInterface = promptsInterface;
	}

	/*
	 * Gets/sets the run report interface method name
	 */
	public String getRunReportInterface() {
		return runReportInterface;
	}

	public void setRunReportInterface(String runReportInterface) {
		this.runReportInterface = runReportInterface;
	}

	/**
	 * Gets the prompts from the report
	 */
	protected void getReportParameters() {
		window.setBusy(consts.get("gettingReportParam"));

		service.callList(promptsInterface,
				new AsyncCallback<ArrayList<Prompt>>() {
					public void onSuccess(ArrayList<Prompt> result) {
						reportParameters = result;
						createReportWindow();
						window.setDone(consts.get("loadCompleteMessage"));
					}

					public void onFailure(Throwable caught) {
						window.close();
						Window.alert("Failed to get parameters for " + name);
					}
				});
	}

	/**
	 * Draws the prompts and fields in the report window
	 */
	protected void createReportWindow() {
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

		// for (Prompt p : reportParameters) {
		for (i = 0; i < reportParameters.size(); i++) {
			p = reportParameters.get(i);
			//
			// decode and create component objects
			//
			if (p.isHidden())
				continue;

			switch (p.getType()) {
			case ARRAY:
			case ARRAYMULTI:
				w = createDropdown(p);
				((Dropdown<String>) w).enable(true);
				break;
			case CHECK:
				w = createCheckBox(p);
				((CheckBox) w).enable(true);
				break;
			case STRING:
				f = new StringField();
				w = createTextBox(f, p);
				((TextBox) w).enable(true);
			case SHORT:
			case INTEGER:
				f = new IntegerField();
				w = createTextBox(f, p);
				((TextBox) w).enable(true);
				break;
			case FLOAT:
			case DOUBLE:
				f = new DoubleField();
				w = createTextBox(f, p);
				((TextBox) w).enable(true);
				break;
			case DATETIME:
				w = createCalendar(p);
				((CalendarLookUp) w).enable(true);
				break;
			default:
				w = null;
				Window.alert("Error: Type " + p.getType()
						+ " not supported; Please notify IT");
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

		// this is done to adjust the width of the window so that it can display
		// the
		// message associated with the key "correctErrors" without being
		// expanded when
		// the message shows at its bottom
		if (tp.getOffsetWidth() < 335)
			main.setWidth("335px");

		main.setCellHorizontalAlignment(hp, HasAlignment.ALIGN_CENTER);
	}

	/**
	 * Builds a query array from the specified parameters and calls the report's
	 * run method.
	 */
	protected void runReport() {
		Query query;
		ReportStatus st;
		Dropdown<String> d;
		TableDataRow r;
		String key;

		if (!validate()) {
			window.setError(consts.get("correctErrors"));
			return;
		}

		query = new Query();
		query.setFields(getQueryFields());
		window.setBusy(consts.get("genReportMessage"));

		service.call(runReportInterface, query,
				new AsyncCallback<ReportStatus>() {
					public void onSuccess(ReportStatus status) {
						String url;

						if (status.getStatus() == ReportStatus.Status.SAVED) {
							url = "report?file=" + status.getMessage();
							if (attachmentName != null)
								url += "&attachment=" + attachmentName;

							Window.open(URL.encode(url), name, null);
							window.setDone("Generated file "
									+ status.getMessage());
						} else {
							window.setDone(status.getMessage());
						}
					}

					public void onFailure(Throwable caught) {
						window.setError("Failed");
						caught.printStackTrace();
						Window.alert(caught.getMessage());
					}
				});
	}

	/**
	 * Resets all the fields to their original report specified values
	 */
	protected void reset() {
		Dropdown<String> dd;
		TextBox tb;
		ArrayList<TableDataRow> data;

		for (String key : def.getWidgets().keySet()) {
			if (def.getWidget(key) instanceof Dropdown) {
				dd = ((Dropdown<String>) def.getWidget(key));
				dd.clearExceptions();
				data = dd.getData();
				for (Prompt p : reportParameters) {
					if (key.equals(p.getName())) {
						resetDropdown(p, data, dd);
						break;
					}
				}
			} else if (def.getWidget(key) instanceof TextBox) {
				tb = ((TextBox) def.getWidget(key));
				tb.setFieldValue("");
				tb.clearExceptions();
			}
		}

		window.clearStatus();
	}

	/**
	 * Resets the dropdown to prompt specified value
	 */
	protected void resetDropdown(Prompt p, ArrayList<TableDataRow> l,
			Dropdown<String> d) {
		String key;

		if (p.getDefaultValue() != null)
			d.setValue(p.getDefaultValue());
		else if ("PRINTER".equals(p.getName()) && defaultPrinter != null)
			d.setValue(defaultPrinter);
		else if ("BARCODE".equals(p.getName()) && defaultBarcodePrinter != null)
			d.setValue(defaultBarcodePrinter);
		else {
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

		/*
		 * remember the last printer & barcode printer they selected
		 */
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

	protected QueryData getQuery(CalendarLookUp c, String key) {
		QueryData qd;
		DateField field;

		field = (DateField) c.getField();

		if (field.getValue() == null)
			return null;

		qd = new QueryData();
		qd.query = field.formatQuery();
		qd.key = key;
		qd.type = QueryData.Type.DATE;

		return qd;
	}

	protected Dropdown<String> createDropdown(Prompt p) {
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
		if (p.getDefaultValue() != null)
			c.setFieldValue(Datetime.getInstance(s, e,
					new Date(p.getDefaultValue())));

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
			tp.setWidget(row, 0, pr);
			hp = new HorizontalPanel();
			hp.add(w);
			tp.setWidget(row, 1, hp);
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