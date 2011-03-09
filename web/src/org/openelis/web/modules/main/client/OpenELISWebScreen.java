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
package org.openelis.web.modules.main.client;

import java.util.HashMap;

import org.openelis.gwt.common.SystemUserPermission;
import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.screen.ScreenDefInt;
import org.openelis.gwt.services.ScreenService;
import org.openelis.gwt.widget.IconContainer;
import org.openelis.gwt.widget.Label;
import org.openelis.gwt.widget.web.LinkButton;
import org.openelis.gwt.widget.web.WebWindow;
import org.openelis.modules.report.client.FinalReportScreen;
import org.openelis.modules.report.client.TestReportScreen;
import org.openelis.web.modules.home.client.HomeScreen;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;

/**
 * 
 * This class draws the initial screen for the OpenELIS Web interface.  All screen navigations will
 * be handled by this class.
 *
 */
public class OpenELISWebScreen extends Screen {
	
	/**
	 * This panel is where the screen content is displayed
	 */
	protected static AbsolutePanel 							content;
	
	/**
	 * Ever Present link back to the Home Page
	 */
	protected static IconContainer							homeLink;
	
	/**
	 * Labels on the main screen
	 */
	protected static Label<String>                          welcome,logout,title;
	
	/**
	 * HashMap of Screens navigated 
	 */
	protected static HashMap<String,Screen> 				screens;
	
	/**
	 * Permissions assigned to the currently signed in user
	 */
	protected static SystemUserPermission                   systemUserPermission;
	
	/**
	 * Static window used to display status messages
	 */
	public static WebWindow window;
	
	/**
	 * No-arg Constructor
	 * @throws Exception
	 */
	public OpenELISWebScreen() throws Exception {
		super((ScreenDefInt)GWT.create(OpenELISWebDef.class));
		 OpenELISRPC rpc;

        service = new ScreenService("controller?service=org.openelis.web.modules.main.server.OpenELISWebService");
        rpc = service.call("initialData");
        consts = rpc.appConstants;
        systemUserPermission = rpc.systemUserPermission;
        window = new WebWindow();
        
		initialize();
	}
	
	/**
	 * This method will set up initial widgets for the main screen and load the Menu screen into the content panel.
	 */
	@SuppressWarnings("unchecked")
	private void initialize() {		
		content = (AbsolutePanel)def.getWidget("content");
		screens = new HashMap<String,Screen>();
		welcome = (Label<String>)def.getWidget("welcome");
		logout = (Label<String>)def.getWidget("logout");
		//title = (Label<String>)def.getWidget("title");
		homeLink = (IconContainer)def.getWidget("home");
		
		content.add(window);
		
		homeLink.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				gotoScreen("home");
			}
		});
	
		welcome.setText("Welcome "+systemUserPermission.getFirstName());
		logout.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
                try {
                    service.call("logout");
                    Window.open("OpenELIS.html", "_self", null);
                } catch (Throwable e) {
                    e.printStackTrace();
                    Window.alert(e.getMessage());
                }
			}
		});
				
		History.addValueChangeHandler(new ValueChangeHandler<String>() {
			public void onValueChange(ValueChangeEvent<String> event) {
				gotoScreen(event.getValue());
			}
		});
		
		setLinks();
		
		setScreen(new HomeScreen(),"Home","home");
		
	}
	
	/**
	 * This method will set the links available to the user in the right column 
	 * based on their permissions.
	 */
	private void setLinks() {
		LinkButton finalReport,testReport;
		AbsolutePanel linksPanel;
		
		linksPanel = (AbsolutePanel)def.getWidget("links");

	    finalReport = new LinkButton("finalReportIcon","Final Report");
	    finalReport.setSize("60px", "60px");
		finalReport.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {

				try {
					FinalReportScreen screen =  new FinalReportScreen();
					screen.setStyleName("WhiteContentPanel");
					OpenELISWebScreen.setScreen(screen, "Final Report", "finalReport");
				}catch(Exception e) {
					e.printStackTrace();
					Window.alert(e.getMessage());
				}
			}
		});
		finalReport.addStyleName("webButton");
		linksPanel.add(finalReport);
		
		testReport = new LinkButton("testReportIcon","Test Report");
		testReport.setSize("60px", "60px");
		testReport.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				try {
					TestReportScreen screen = new TestReportScreen();
					screen.setStyleName("WhiteContentPanel");
					OpenELISWebScreen.setScreen(screen, "Test Report", "testReport");
				}catch(Exception e) {
					e.printStackTrace();
					Window.alert(e.getMessage());
				}
			}
		});
		testReport.addStyleName("webButton");
		linksPanel.add(testReport);		
	}
	
	/**
	 * This method is called statically by other modules to display a Screen for the first time.  The Screen will be 
	 * added to the Screen map and a new History entry will be added to the browser.
	 * @param screen
	 * @param name
	 * @param key
	 */
	public static void setScreen(Screen screen, String name, String key) {
		window.setContent(screen);
		screen.setWidth("100%");
		screen.setHeight("100%");
		History.newItem(key,false);
		screens.put(key, screen);
		screen.getDefinition().setName(name);
	}
	
	/**
	 * This method is called statically when a user navigates by using the back or forward buttons
	 * @param key
	 */
	public static void gotoScreen(String key) {
		Screen screen;
		
		screen = screens.get(key);
		
		window.setContent(screen);
		History.newItem(key,false);
	}

}
