package org.openelis.web.modules.tests.client;

import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.screen.ScreenDefInt;

import com.google.gwt.core.client.GWT;

public class TestScreen extends Screen {
	
	public TestScreen() {
		super((ScreenDefInt)GWT.create(TestDef.class));
	}

}
