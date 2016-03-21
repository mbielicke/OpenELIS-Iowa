package org.openelis.portal.modules.desktop.client;

import org.openelis.portal.client.PortalEntry;
import org.openelis.portal.modules.example.client.ExampleScreen;
import org.openelis.ui.screen.Screen;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Widget;

public class DesktopScreen extends Screen implements PortalEntry{
	
	@UiTemplate("Desktop.ui.xml")
	interface DesktopUiBinder extends UiBinder<Widget, DesktopScreen>{};
	protected static final DesktopUiBinder uiBinder = GWT.create(DesktopUiBinder.class);
	
	
	@UiField
	LayoutPanel main;
	
	public DesktopScreen() {
		initWidget(uiBinder.createAndBindUi(this));
	}
	
	
}
