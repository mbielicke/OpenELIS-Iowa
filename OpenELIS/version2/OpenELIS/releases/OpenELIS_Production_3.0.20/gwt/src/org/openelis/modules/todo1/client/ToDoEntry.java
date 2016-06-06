package org.openelis.modules.todo1.client;

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

public class ToDoEntry implements EntryPoint, ShowScreenHandler {

    @Override
    public void onModuleLoad() {
        ScreenBus.get().addHandler(ScreenBus.TO_DO, this);
    }

    @Override
    public void showScreen() {
        GWT.runAsync(new RunAsyncCallback() {
            public void onSuccess() {
                try {
                    addScreen();
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

    public ToDoScreenUI addScreen() throws Exception {
        org.openelis.ui.widget.Window window;
        ToDoScreenUI screen;

        window = new org.openelis.ui.widget.Window();
        screen = new ToDoScreenUI(window);
        window.setName(Messages.get().toDo());
        window.setSize("650px", "700px");
        window.setContent(screen);
        OpenELIS.getBrowser().addWindow(window, "toDo");
        
        return screen;
    }
}
