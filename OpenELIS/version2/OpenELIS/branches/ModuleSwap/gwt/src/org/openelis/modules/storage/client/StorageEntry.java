package org.openelis.modules.storage.client;

import static org.openelis.modules.main.client.Logger.remote;

import java.util.logging.Level;

import org.openelis.modules.main.client.OpenELIS;
import org.openelis.modules.main.client.OpenELISEntry;
import org.openelis.modules.main.client.event.ShowScreenHandler;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.user.client.Window;

public class StorageEntry implements EntryPoint {
    
    @Override
    public void onModuleLoad() {
        OpenELISEntry.mainBus.addHandler(OpenELIS.STORAGE, new ShowScreenHandler() {
            
            @Override
            public void showScreen() {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            org.openelis.ui.widget.Window window = new org.openelis.ui.widget.Window(false);
                            window.setName(OpenELIS.getMessages().storage());
                            window.setSize("20px", "20px");
                            window.setContent(new StorageScreen(window));
                            OpenELIS.getBrowser().addWindow(window, "storage");
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
        });
    }
    

}
