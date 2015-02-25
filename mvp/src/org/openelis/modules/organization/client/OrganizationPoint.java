package org.openelis.modules.organization.client;

import static org.openelis.client.Logger.remote;

import java.util.logging.Level;

import javax.inject.Inject;

import org.openelis.client.OpenELISEntry;
import org.openelis.client.ScreenBus;
import org.openelis.client.event.ShowScreenHandler;
import org.openelis.di.AppDI;
import org.openelis.messages.Messages;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.user.client.Window;

public class OrganizationPoint implements ShowScreenHandler {
	
	@Inject
	ScreenBus screenBus;

	@Inject
	public OrganizationPoint() {
		
	}
	
    public void onModuleLoad() {   
        screenBus.addHandler(ScreenBus.ORGANIZATION, this);        
    }
    
    @Override
    public void showScreen() {
        GWT.runAsync(new RunAsyncCallback() {
            public void onSuccess() {
                try {
                    org.openelis.ui.widget.Window window = new org.openelis.ui.widget.Window(true);
                    window.setName(Messages.get().organization());
                    window.setSize("600px", "450px");
                    window.setContent(AppDI.INSTANCE.organization().view);
                    OpenELISEntry.browser.addWindow(window, "organization");
                } catch (Throwable e) {
                    e.printStackTrace();
                    Window.alert(e.getMessage());
                    remote().log(Level.SEVERE, e.getMessage(), e);
                }
            }

            public void onFailure(Throwable caught) {
                remote().log(Level.SEVERE, caught.getMessage(), caught);
                Window.alert(caught.getMessage());
            }
        });
    }

}
