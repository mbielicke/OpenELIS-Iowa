package org.openelis.web.modules.menu.client;

import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.screen.ScreenDefInt;
import org.openelis.gwt.widget.AppButton;
import org.openelis.web.modules.main.client.OpenELISWebScreen;
import org.openelis.web.modules.report.client.ReportScreen;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

public class MenuScreen extends Screen {
	
	public MenuScreen() {
		super((ScreenDefInt)GWT.create(MenuScreenDef.class));
		initialize();
	}
	
	private void initialize() {

	}
}
