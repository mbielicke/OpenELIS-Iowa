package org.openelis.web.modules.main.client;

import java.util.ArrayList;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;

public class CrumbLine extends Composite {
	
	protected HorizontalPanel bar; 
	protected ArrayList<Link> links;
	
	public CrumbLine() {
		bar = new HorizontalPanel();
		bar.setStyleName("CrumbLine");
		links = new ArrayList<Link>();
		initWidget(bar);
	}
	
	public void addLink(String display, String key) {
		Link link;
		link = new Link(display,key);
		links.add(link);
		createCrumb();
	}
	
	public void removeLink() {
		links.remove(links.size()-1);
		createCrumb();
	}
	
	private void createCrumb() {
		Label label;
		
		bar.clear();
		bar.add(new Label(""));
		bar.setCellWidth(bar.getWidget(0),"100%");
		for(int i = 0; i < links.size(); i++) {
			if(i > 0) {
				label = new Label(">");
				label.setStyleName("webLabel");
				bar.insert(label,bar.getWidgetCount()-1);
			}
			bar.insert(links.get(i),bar.getWidgetCount()-1);
			if(i < links.size()-1)
				links.get(i).setEnabled(true);
			else
				links.get(i).setEnabled(false);
		}
	}
	
	private class Link extends Label {
		String key;
		Link source;
		boolean enabled;
		
		public Link(String display, String key) {
			super(display);
			this.key = key;
			source = this;
			addHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					setEnabled(false);
					while(links.indexOf(source) < links.size() -1) 
						links.remove(links.size()-1);
					//links.remove(links.size()-1);
					OpenELISWebScreen.gotoScreen(source.key);
					createCrumb();
					
				}
			},ClickEvent.getType());
			setStyleName("webLabel");
			setWordWrap(false);
		}
		
		public void setEnabled(boolean enabled) {
			if(enabled == this.enabled)
				return;
			this.enabled = enabled;
			if(enabled){
				sinkEvents(Event.ONCLICK);
				addStyleName("clickable");
			}else {
				unsinkEvents(Event.ONCLICK);
				removeStyleName("clickable");
			}
		}
		
		
	}

}
