package org.openelis.portal.modules.example.client;

import org.openelis.ui.screen.Screen;

import com.google.gwt.core.client.GWT;

public class ExampleScreen extends Screen {
	
	ExampleUI ui = GWT.create(ExampleUIImpl.class);
	
	public ExampleScreen() {
		initWidget(ui.asWidget());
		
	}

}
