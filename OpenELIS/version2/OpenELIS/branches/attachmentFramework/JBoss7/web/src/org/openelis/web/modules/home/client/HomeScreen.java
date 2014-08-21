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
package org.openelis.web.modules.home.client;

import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.screen.ScreenDefInt;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;

/**
 * This class is used to draw the initial screen seen by users who log in the OpenELIS Web Portal.
 * It is meant to display messages of the day or any pertinent information that needs to be 
 * conveyed to users.
 * @author tschmidt
 *
 */
public class HomeScreen extends Screen {

	/**
	 * No-Arg constructor
	 */
	public HomeScreen() {
		super((ScreenDefInt)GWT.create(HomeScreenDef.class));
		DeferredCommand.addCommand(new Command() {
			public void execute() {
				initialize();
			}
		});
	}
	
	/**
	 * Initialize widgets
	 */
	private void initialize() {
	}
}
