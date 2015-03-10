package org.openelis.modules.method.client;

import static org.openelis.client.Logger.remote;

import java.util.logging.Level;

import javax.inject.Inject;

import org.openelis.client.OpenELISEntry;
import org.openelis.client.ScreenBus;
import org.openelis.client.event.ShowScreenHandler;
import org.openelis.di.AppDI;
import org.openelis.messages.Messages;
import org.openelis.ui.widget.Browser;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.user.client.Window;

import dagger.Lazy;

public class MethodPoint implements ShowScreenHandler {
	    
		@Inject
		ScreenBus bus; 
		
		@Inject
		Lazy<Browser> lazyBrowser;
		
		@Inject
		public MethodPoint() {
			
		}
		
	    public void onModuleLoad() {
	        bus.addHandler(ScreenBus.METHOD, this);
	    }
	    
	    @Override
	    public void showScreen() {
	        GWT.runAsync(new RunAsyncCallback() {
	            public void onSuccess() {
	                try {
	                    org.openelis.ui.widget.Window window = new org.openelis.ui.widget.Window(true);
	                    window.setName(Messages.get().method());
	                    window.setSize("862px", "432px");
	                    MethodViewImpl view = new MethodViewImpl();
	                    window.setContent(view);
	                    OpenELISEntry.browser.addWindow(window, "method");
	                    new MethodPresenter(view,window);
	                } catch (Throwable e) {
	                    remote().log(Level.SEVERE, e.getMessage(), e);
	                    Window.alert(e.getMessage());
	                }
	            }

	            public void onFailure(Throwable caught) {
	                remote().log(Level.SEVERE, caught.getMessage(), caught);
	                Window.alert(caught.getMessage());
	            }
	        });
	        
	    }

}
