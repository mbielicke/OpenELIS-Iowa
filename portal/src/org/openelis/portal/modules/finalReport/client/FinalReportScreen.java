package org.openelis.portal.modules.finalReport.client;

import org.openelis.ui.screen.Screen;

import com.google.gwt.core.client.GWT;

public class FinalReportScreen extends Screen {
	
	FinalReportUI ui = GWT.create(FinalReportUIImpl.class);
	
	public FinalReportScreen() {
		initWidget(ui.asWidget());
		
	}

}
