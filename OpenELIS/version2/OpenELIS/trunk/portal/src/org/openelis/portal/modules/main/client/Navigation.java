package org.openelis.portal.modules.main.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.Widget;

public class Navigation extends ResizeComposite {
	
	@UiTemplate("Navigation.ui.xml")
	interface NavigationUiBinder extends UiBinder<Widget,Navigation>{};
	protected static final NavigationUiBinder uiBinder = GWT.create(NavigationUiBinder.class);
	
	@UiField
	Anchor finalReport,dataView,sampleStatus,testRequest,
	       emailNotification, logout; 
	
	public Navigation() {
		initWidget(uiBinder.createAndBindUi(this));
	}
	
	public Anchor finalReport() {
		return finalReport;
	}
	
	public Anchor dataView() {
		return dataView;
	}
	
	public Anchor sampleStatus() {
		return sampleStatus;
	}
	
	public Anchor testRequest() {
		return testRequest;
	}
	
	public Anchor emailNotification() {
		return emailNotification;
	}
	
	public Anchor logout() {
		return logout;
	}
	

}
