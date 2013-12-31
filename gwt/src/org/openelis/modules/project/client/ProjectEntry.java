package org.openelis.modules.project.client;

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

public class ProjectEntry implements EntryPoint {
    
    @Override
    public void onModuleLoad() {
        ScreenBus.get().addHandler(ScreenBus.PROJECT, new ShowScreenHandler() {
            
            @Override
            public void showScreen() {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            org.openelis.ui.widget.Window window = new org.openelis.ui.widget.Window(false);
                            window.setName(Messages.get().project());
                            window.setSize("20px", "20px");
                            window.setContent(new ProjectScreen(window));
                            OpenELIS.getBrowser().addWindow(window, "project");
                        } catch (Throwable caught) {
                            remote().log(Level.SEVERE, caught.getMessage(), caught);
                            Window.alert(caught.getMessage());
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
