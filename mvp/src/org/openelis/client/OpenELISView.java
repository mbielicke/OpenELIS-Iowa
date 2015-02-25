package org.openelis.client;

import org.openelis.ui.annotation.Enable;
import org.openelis.ui.mvp.Presenter;
import org.openelis.ui.mvp.View;
import org.openelis.ui.screen.State;
import org.openelis.ui.widget.Browser;
import org.openelis.ui.widget.MenuItem;

import com.google.gwt.uibinder.client.UiField;


@org.openelis.ui.annotation.View(template="OpenELIS.ui.xml",presenter=OpenELISPresenter.class)
public class OpenELISView extends View {
    
    @UiField
    Browser browser;
    
    @UiField 
    @Enable({State.DEFAULT,State.DISPLAY})
    MenuItem method;
    
    @UiField
    @Enable({State.DEFAULT,State.DISPLAY})
    MenuItem organization;
    
    OpenELISPresenter presenter;
    
    public OpenELISView() {
    	
    }
    
    public void setPresenter(Presenter  presenter) {
    	this.presenter = (OpenELISPresenter)presenter;
    }
}
