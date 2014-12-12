package org.openelis.portal.modules.cases.client;

import org.openelis.ui.screen.Screen;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Widget;

public class PatientTab extends Screen {
	
	@UiTemplate("PatientTab.ui.xml")
	interface PatientTabUiBinder extends UiBinder<Widget,PatientTab>{};
	private static final PatientTabUiBinder uiBinder = GWT.create(PatientTabUiBinder.class);
	
	public PatientTab() {
		initWidget(uiBinder.createAndBindUi(this));
	}
	
	

}
