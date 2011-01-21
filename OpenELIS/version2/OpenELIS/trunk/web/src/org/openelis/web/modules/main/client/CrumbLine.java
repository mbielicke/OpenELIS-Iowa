package org.openelis.web.modules.main.client;

import java.util.ArrayList;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;

/**
 * This class provides a list of navigated links at the top of the screen 
 * for the OpenELIS Web Application. 
 *
 */
public class CrumbLine extends Composite {
	
	/**
	 * The panel to display the links
	 */
	protected HorizontalPanel bar; 
	/**
	 * List of links to display
	 */
	protected ArrayList<Link> links;
	
	/**
	 * No-arg constructor
	 */
	public CrumbLine() {
		bar = new HorizontalPanel();
		bar.setStyleName("CrumbLine");
		/**
		 * Insert a blank label at the end of the bar at 100% to align right
		 */
		bar.add(new Label(""));
		bar.setCellWidth(bar.getWidget(0),"100%");
		
		links = new ArrayList<Link>();
		
		initWidget(bar);
	}
	
	/**
	 * Adds a link to the end of this Crumbline.  
	 * @param display 
	 * 		- The text to use when displaying the link
	 * @param key
	 * 		- This key should correspond the Screen key used in the
	 * 	      OpenELISWebScreen
	 */
	public void addLink(String display, String key) {
		Link link;
		
		link = new Link(display,key);
		links.add(link);
		
		createCrumb();
	}
	
	/**
	 * Removes the last link in the Crumbline
	 */
	public void removeLink() {
		links.remove(links.size()-1);
		createCrumb();
	}
	
	/**
	 * This method will draw the display for the Crumbline based on the list of links
	 */
	private void createCrumb() {
		Label label;
		
		while(bar.getWidgetCount() > 1)
			bar.remove(0);
		
		for(int i = 0; i < links.size(); i++) {
			if(i > 0) {
				/* Insert Separator */
				label = new Label(">");
				label.setStyleName("webLabel");
				bar.insert(label,bar.getWidgetCount()-1);
			}
			
			bar.insert(links.get(i),bar.getWidgetCount()-1);
			
			/* Last link is not lit up since it is current screen */
			if(i < links.size()-1)
				links.get(i).setEnabled(true);
			else
				links.get(i).setEnabled(false);
		}
	}
	
	/**
	 * This class implements a Link in the Crumbline as a Label with 
	 * ClickEvents 
	 */
	private class Link extends Label {
		/**
		 * Key of the Screen this link represents
		 */
		private String key;
		/**
		 * Reference to this Link used in Anon Handler
		 */
		private Link source;
		/**
		 * Flag if the link is lit up
		 */
		boolean enabled;
		
		/**
		 * Constructor
		 * @param display
		 * @param key
		 */
		public Link(String display, String key) {
			super(display);
			this.key = key;
			
			source = this;
			
			addHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					setEnabled(false);
					/*
					 * Remove all links after the link that was clicked
					 */
					while(links.indexOf(source) < links.size() -1) 
						links.remove(links.size()-1);
					
					OpenELISWebScreen.gotoScreen(source.key);
					
					createCrumb();
					
				}
			},ClickEvent.getType());
			
			setStyleName("webLabel");
			setWordWrap(false);
		}
		
		/**
		 * Method will enable the link to be clicked 
		 * @param enabled
		 */
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
