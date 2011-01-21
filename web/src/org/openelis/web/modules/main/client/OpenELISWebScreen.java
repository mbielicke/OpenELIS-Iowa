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
import org.openelis.web.modules.main.client.OpenELISRPC;
import org.openelis.web.modules.menu.client.MenuScreen;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * 
 * This class draws the initial screen for the OpenELIS Web interface.  All screen navigations will
 * be handled by this class.
 *
 */
public class OpenELISWebScreen extends Screen implements ValueChangeHandler<String>{
	
	/**
	 * This panel is where the screen content is displayed
	 */
	protected static AbsolutePanel 							content;
	/**
	 * HashMap of Screens navigated 
	 */
	protected static HashMap<String,Screen> 				screens;
	/**
	 * Crumbline navigation widget
	 */
	protected static CrumbLine 								crumb;
	/**
	 * Permisions assigned to the currently signed in user
	 */
	protected static SystemUserPermission                   systemUserPermission;
	
	/**
	 * No-arg Constructor
	 * @throws Exception
	 */
	public OpenELISWebScreen() throws Exception {
		super((ScreenDefInt)GWT.create(OpenELISWebDef.class));
        OpenELISRPC rpc;

        service = new ScreenService("OpenELISServlet?service=org.openelis.web.modules.main.server.OpenELISWebService");
        rpc = service.call("initialData");
        consts = rpc.appConstants;
        systemUserPermission = rpc.systemUserPermission;
        
		initialize();
	}
	
	/**
	 * This method will set up initial widgets for the main screen and load the Menu screen into the content panel.
	 */
	private void initialize() {
		content = (AbsolutePanel)def.getWidget("content");
		screens = new HashMap<String,Screen>();
		crumb = new CrumbLine();
		
		((VerticalPanel)def.getWidget("container")).insert(crumb, 0);
		
		History.addValueChangeHandler(this);
		
		setScreen(new MenuScreen(),"Menu","menu");
	}
	
	/**
	 * This method is called statically by other modules to display a Screen for the first time.  The Screen will be 
	 * added to the Screen map and a new History entry will be added to the browser.
	 * @param screen
	 * @param name
	 * @param key
	 */
	public static void setScreen(Screen screen, String name, String key) {
		content.clear();
		content.add(screen);
		History.newItem(key,false);
		screens.put(key, screen);
		screen.getDefinition().setName(name);
		crumb.addLink(name, key);
	}
	
	/**
	 * This method is called statically when a user navigates by using the crumbline or the browser back or forward buttons
	 * @param key
	 */
	public static void gotoScreen(String key) {
		content.clear();
		content.add(screens.get(key));
		History.newItem(key,false);
	}

	/**
	 * This handler receives events from the History listener of GWT when the user presses the back or forward button
	 */
	public void onValueChange(ValueChangeEvent<String> event) {
		Screen screen;
		
		screen = screens.get(event.getValue());
		
		content.clear();
		content.add(screen);
		if(event.getValue().equals("menu"))
			crumb.removeLink();
		else
			crumb.addLink(screen.getDefinition().getName(),event.getValue());
	}
	
	@Override
	public void onBrowserEvent(Event event) {
		super.onBrowserEvent(event);
	}

}
