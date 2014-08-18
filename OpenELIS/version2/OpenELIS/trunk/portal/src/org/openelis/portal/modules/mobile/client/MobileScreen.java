package org.openelis.portal.modules.mobile.client;

import org.openelis.portal.client.PortalEntry;
import org.openelis.ui.screen.Screen;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Widget;

public class MobileScreen extends Screen implements PortalEntry{
	
	@UiTemplate("Mobile.ui.xml")
	interface MobileUiBinder extends UiBinder<Widget,MobileScreen>{};
	protected static final MobileUiBinder uiBinder = GWT.create(MobileUiBinder.class);
	
	public MobileScreen() {
		initWidget(uiBinder.createAndBindUi(this));
	}
	

}
