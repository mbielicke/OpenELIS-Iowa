package org.openelis.modules.worksheetCompletion1.client;

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

public class WorksheetCompletionEntry implements EntryPoint, ShowScreenHandler {
    
    @Override
    public void onModuleLoad() {
        ScreenBus.get().addHandler(ScreenBus.WORKSHEET_COMPLETION, this);        
    }
    
    @Override
    public void showScreen() {
        GWT.runAsync(new RunAsyncCallback() {
            public void onSuccess() {
                WorksheetCompletionScreenUI screen;
                
                try {
                    org.openelis.ui.widget.Window window = new org.openelis.ui.widget.Window();
                    window.setName(Messages.get().worksheetCompletion());
                    window.setSize("1061px", "511px");
                    screen = new WorksheetCompletionScreenUI(window);
                    window.setContent(screen);
                    screen.initialize();
                    OpenELIS.getBrowser().addWindow(window, "worksheetCompletion");
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