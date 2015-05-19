package org.openelis.portal.modules.tablet.client;

import org.openelis.portal.client.PortalEntry;
import org.openelis.portal.modules.example.client.ExampleScreen;
import org.openelis.ui.screen.Screen;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Widget;

public class TabletScreen extends Screen implements PortalEntry{
	
	@UiTemplate("Tablet.ui.xml")
	interface DesktopUiBinder extends UiBinder<Widget, TabletScreen>{};
	protected static final DesktopUiBinder uiBinder = GWT.create(DesktopUiBinder.class);
	
	@UiField
	Anchor exampleLink;
	
	@UiField
	LayoutPanel main;
	
	public TabletScreen() {
		initWidget(uiBinder.createAndBindUi(this));
	}
	
	@UiHandler("exampleLink")
	public void exampleClick(ClickEvent event) {
		main.add(new ExampleScreen());
	}
	
}
