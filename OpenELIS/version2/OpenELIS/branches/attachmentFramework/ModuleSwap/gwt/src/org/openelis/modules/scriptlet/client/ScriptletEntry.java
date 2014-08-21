package org.openelis.modules.scriptlet.client;

import static org.openelis.modules.main.client.Logger.remote;

import java.util.logging.Level;

import org.openelis.constants.Messages;
import org.openelis.modules.main.client.OpenELIS;
import org.openelis.modules.main.client.ScreenBus;
import org.openelis.modules.main.client.event.ShowScreenHandler;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.user.client.Window;

public class ScriptletEntry implements EntryPoint {
    
    @Override
    public void onModuleLoad() {
        ScreenBus.get().addHandler(ScreenBus.SCRIPTLET, new ShowScreenHandler() {
            
            @Override
            public void showScreen() {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                       try {
                           org.openelis.ui.widget.Window window = new org.openelis.ui.widget.Window();
                           window.setName(Messages.get().scriptlet());
                           window.setSize("862px", "432px");
                           window.setContent(new ScriptletScreen(window));
                           OpenELIS.getBrowser().addWindow(window,"scriptlet");
                       }catch(Exception e) {
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
        });
        
    }

}
