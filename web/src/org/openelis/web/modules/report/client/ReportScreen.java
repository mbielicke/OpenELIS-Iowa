package org.openelis.web.modules.report.client;

import java.util.ArrayList;

import org.openelis.gwt.common.ReportProgress;
import org.openelis.gwt.common.data.Query;
import org.openelis.gwt.common.data.QueryData;
import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.screen.ScreenDefInt;
import org.openelis.gwt.services.ScreenService;
import org.openelis.gwt.widget.Button;
import org.openelis.gwt.widget.DateHelper;
import org.openelis.gwt.widget.Dropdown;
import org.openelis.gwt.widget.Item;
import org.openelis.gwt.widget.Queryable;
import org.openelis.gwt.widget.StringHelper;
import org.openelis.gwt.widget.TextBox;
import org.openelis.gwt.widget.calendar.Calendar;
import org.openelis.gwt.widget.table.Table;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;
import com.google.gwt.xml.client.XMLParser;

public class ReportScreen extends Screen {
	
	protected Button run;
	protected String report;
	
	public ReportScreen(String report) {
		super((ScreenDefInt)GWT.create(ReportScreenDef.class));
		service = new ScreenService("controller?service=org.openelis.web.modules.report.server.ReportService");
		this.report = report;
		try {
			generateScreen(service.callString("getReport","timetracker-report/"+report));
			initialize();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private void initialize() {
		run.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				run();
			}
		});
	}
	
	private void generateScreen(String xml) {
		Document doc;
		NodeList params;
		Node param;
		String type;
		VerticalPanel vp = new VerticalPanel();
		FlexTable table = new FlexTable();
		table.setStyleName("Form");
		vp.add(table);
		((AbsolutePanel)def.getWidget("content")).add(vp);
		String key,prompt;
		
		doc = XMLParser.parse(xml);
		
		params = doc.getElementsByTagName("param");
		
		for(int i = 0; i < params.getLength(); i++) {
			param = params.item(i);
			prompt = param.getAttributes().getNamedItem("prompt").getNodeValue();
			key = param.getAttributes().getNamedItem("name").getNodeValue();
			
			type = param.getAttributes().getNamedItem("type").getNodeValue();
			if(type.equals("datetime")) {
				Calendar calendar = new Calendar();
				calendar.setStyleName("ScreenCalendar");
				DateHelper helper = new DateHelper();
				helper.setBegin((byte)0);
				helper.setEnd((byte)2);
				helper.setPattern("yyyy-MM-dd");
				calendar.setHelper(helper);
				calendar.setEnabled(true);
				def.setWidget(calendar, key);
				table.setText(i, 0, prompt);
				table.getCellFormatter().setStyleName(i, 0, "Prompt");
				table.setWidget(i,1,calendar);
			}else if(type.equals("string")) {
				TextBox<String> tb = new TextBox<String>();
				tb.setWidth("100%");
				tb.setStyleName("ScreenTextBox");
				StringHelper helper = new StringHelper();
				tb.setHelper(helper);
				tb.setEnabled(true);
				def.setWidget(tb, key);
				table.setText(i, 0, prompt);
				table.getCellFormatter().setStyleName(i, 0, "Prompt");
				table.setWidget(i, 1, tb);
			}else if(type.equals("array") || type.equals("arraymulti")) {
				Dropdown<String> drop = new Dropdown<String>();
				drop.setWidth("200px");
				NodeList options = ((Element)param).getElementsByTagName("option");
				ArrayList<Item<String>> model = new ArrayList<Item<String>>();
				for(int j = 0; j < options.getLength(); j++) {
					Node option = options.item(j);
					model.add(new Item<String>(option.getAttributes().getNamedItem("value").getNodeValue(),
							                   option.getAttributes().getNamedItem("display").getNodeValue()));
				}
				if(type.equals("arraymulti"))
					drop.setQueryMode(true);
				def.setWidget(drop, key);
				Table popContext = new Table();
				popContext.addColumn().setWidth(200);
				popContext.setWidth(200);
				popContext.setHorizontalScroll(Table.Scrolling.AS_NEEDED);
				popContext.setVerticalScroll(Table.Scrolling.AS_NEEDED);
				drop.setPopupContext(popContext);
				drop.setModel(model);
				drop.setEnabled(true);	
				if(key.equals("printer") && model.size() == 1) 
					drop.setValue(model.get(0).getKey());
				else {
					table.setText(i, 0, prompt);
					table.getCellFormatter().setStyleName(i, 0, "Prompt");
					table.setWidget(i,1,drop);
				}
			}
		}
		run = new Button("","Run");
		run.setStyleName("Button");
		run.setEnabled(true);
		vp.add(run);
		vp.setCellHorizontalAlignment(run,HasAlignment.ALIGN_CENTER);
	}

	private void run() {
		Query query = new Query();
		query.setFields(getQueryFields());
		QueryData qd = new QueryData();
		qd.key = "report";
		qd.query = report;
		query.setFields(qd);
		
		service.call("run", query, new AsyncCallback<ReportProgress>() {
			public void onSuccess(final ReportProgress fp) {
				DeferredCommand.addCommand(new Command() {
					public void execute() {
						Window.open("Report.html?id="+fp.name, "Report", "toolbar=no,location=no,menubar=no,status=no,titlebar=no");
					}
				});
			    
			}
			
			public void onFailure(Throwable caught) {
				Window.alert(caught.getMessage());
			}
		});
	}
}
