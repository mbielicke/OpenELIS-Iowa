package org.openelis.web.modules.main.client;

import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.screen.ScreenDefInt;
import org.openelis.gwt.widget.MenuItem;
import org.openelis.web.modules.samples.client.SamplesScreen;
import org.openelis.web.modules.tests.client.TestScreen;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.AbsolutePanel;

public class OpenELISWebScreen extends Screen implements ValueChangeHandler<String>{
	
	AbsolutePanel content;
	Screen samples;
	Screen test;
	
	public OpenELISWebScreen() {
		super((ScreenDefInt)GWT.create(OpenELISWebDef.class));
		setHandlers();
		History.addValueChangeHandler(this);
		History.newItem("clear",false);
	}
	
	private void setHandlers() {
		content = (AbsolutePanel)def.getWidget("content");
		((MenuItem)def.getWidget("samples")).addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				content.clear();
				content.add(samples = new SamplesScreen());
				History.newItem("samples", false);
			}
		});
		((MenuItem)def.getWidget("tests")).addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event){
				content.clear();
				content.add(test = new TestScreen());
				History.newItem("tests",false);
			}
		});
	}

	public void onValueChange(ValueChangeEvent<String> event) {
		if(event.getValue().equals("samples")) {
			content.clear();
			if(samples != null)
				content.add(samples);
			else
				content.add(samples = new SamplesScreen());
		}
		if(event.getValue().equals("tests")) {
			content.clear();
			if(test != null)
				content.add(test);
			else
				content.add(test = new TestScreen());
		}
		if(event.getValue().equals("clear")){
			content.clear();
		}
	}

}
