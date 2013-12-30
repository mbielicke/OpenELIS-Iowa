package org.openelis.modules.organization.client;

import static org.openelis.modules.main.client.Logger.remote;

import java.util.logging.Level;

import org.openelis.modules.main.client.OpenELIS;
import org.openelis.modules.main.client.OpenELISEntry;
import org.openelis.modules.main.client.event.ShowScreenHandler;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.user.client.Window;

public class OrganizationEntry implements EntryPoint {

    @Override
    public void onModuleLoad() {
        
        OpenELISEntry.mainBus.addHandler(OpenELIS.ORGANIZATION,new ShowScreenHandler() {
            
            @Override
            public void showScreen() {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            org.openelis.ui.widget.Window window = new org.openelis.ui.widget.Window(false);
                            window.setName(OpenELIS.getMessages().organization());
                            window.setSize("20px", "20px");
                            window.setContent(new OrganizationScreen(window));
                            OpenELIS.getBrowser().addWindow(window, "organization");
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
        });
        
    }

}
