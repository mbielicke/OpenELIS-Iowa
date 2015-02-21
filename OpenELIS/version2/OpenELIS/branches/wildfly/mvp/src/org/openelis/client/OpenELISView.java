package org.openelis.client;

import org.openelis.ui.mvp.View;
import org.openelis.ui.widget.Browser;
import org.openelis.ui.widget.MenuItem;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Widget;

@org.openelis.ui.annotation.View(presenter="org.openelis.client.OpenELIS")
public class OpenELISView extends View {
	
    @UiTemplate("OpenELIS.ui.xml")
    interface OpenELISUiBinder extends UiBinder<Widget, OpenELISView> {};
    protected static OpenELISUiBinder uiBinder = GWT.create(OpenELISUiBinder.class);
    
    @UiField
    Browser browser;
    
    @UiField 
    MenuItem method;
    
    

}
