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
package org.openelis.web.modules.links.client;

import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.screen.ScreenDefInt;
import org.openelis.gwt.widget.web.LinkButton;
import org.openelis.modules.report.client.FinalReportScreen;
import org.openelis.modules.report.client.TestReportScreen;
import org.openelis.web.modules.main.client.OpenELISWebScreen;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;

/**
 * This screen dynamically loads buttons into the Links column based on permissions of the user. *
 */
public class LinksScreen extends Screen {
	
	/**
	 * Buttons used as Links
	 */
	private LinkButton finalReport,testReport;
	
	/**
	 * Panel to add Links to
	 */
	private AbsolutePanel linksPanel;
	
	/**
	 * No-arg constructor 
	 */
	public LinksScreen() {
		super((ScreenDefInt)GWT.create(LinksScreenDef.class));
		DeferredCommand.addCommand(new Command() {
			public void execute() {
				initialize();
			}
		});
	}
	
	/**
	 * Create and add links to the screen that the user has permission to.
	 */
	private void initialize() {
		
		linksPanel = (AbsolutePanel)def.getWidget("linksPanel");
			    
	    finalReport = new LinkButton("","Final Report");
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
		
		testReport = new LinkButton("","Test Report");
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
}
