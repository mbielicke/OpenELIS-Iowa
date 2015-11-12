package org.openelis.portal.modules.example.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.Widget;

public class ExampleUITabletImpl extends ResizeComposite implements ExampleUI {
	
	@UiTemplate("ExampleTablet.ui.xml")
	interface ExampleUiBinder extends UiBinder<Widget, ExampleUITabletImpl>{};
	protected static final ExampleUiBinder uiBinder = GWT.create(ExampleUiBinder.class);
	
	public ExampleUITabletImpl() {
		initWidget(uiBinder.createAndBindUi(this));
	}
	
	@Override
	public Widget asWidget() {
		return this;
	}
	
}
