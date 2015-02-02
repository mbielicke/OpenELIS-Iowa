package org.openelis.portal.modules.example.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.Widget;

public class ExampleUIMobileImpl extends ResizeComposite implements ExampleUI {
	
	@UiTemplate("ExampleMobile.ui.xml")
	interface ExampleUiBinder extends UiBinder<Widget, ExampleUIMobileImpl>{};
	protected static final ExampleUiBinder uiBinder = GWT.create(ExampleUiBinder.class);
	
	public ExampleUIMobileImpl() {
		initWidget(uiBinder.createAndBindUi(this));
	}
	
	@Override
	public Widget asWidget() {
		return this;
	}
	
}
