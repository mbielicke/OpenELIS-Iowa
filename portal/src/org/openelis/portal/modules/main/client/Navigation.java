package org.openelis.portal.modules.main.client;

import org.openelis.ui.widget.Button;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.Widget;

public class Navigation extends ResizeComposite {
	
	@UiTemplate("Navigation.ui.xml")
	interface NavigationUiBinder extends UiBinder<Widget,Navigation>{};
	protected static final NavigationUiBinder uiBinder = GWT.create(NavigationUiBinder.class);
	
	@UiField
	Button finalReport,dataView,sampleStatus,testRequest,
	       emailNotification, cases, logout; 
	
	public Navigation() {
		initWidget(uiBinder.createAndBindUi(this));
	}
	
	public Button finalReport() {
		return finalReport;
	}
	
	public Button dataView() {
		return dataView;
	}
	
	public Button sampleStatus() {
		return sampleStatus;
	}
	
	public Button testRequest() {
		return testRequest;
	}
	
	public Button emailNotification() {
		return emailNotification;
	}
	
	public Button logout() {
		return logout;
	}
	
	public Button cases() {
		return cases;
	}
}
