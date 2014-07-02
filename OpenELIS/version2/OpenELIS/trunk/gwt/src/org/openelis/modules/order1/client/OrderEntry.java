package org.openelis.modules.order1.client;

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

public class OrderEntry implements EntryPoint {
    
    @Override
    public void onModuleLoad() {
        ScreenBus.get().addHandler(ScreenBus.INTERNAL_ORDER, new ShowScreenHandler() {
            
            @Override
            public void showScreen() {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            org.openelis.ui.widget.Window window = new org.openelis.ui.widget.Window();
                            window.setName(Messages.get().internalOrder());
                            window.setSize("880px", "588px");
                            window.setContent(new InternalOrderScreenUI(window));
                            OpenELIS.getBrowser().addWindow(window, "internalOrder");
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
        
        ScreenBus.get().addHandler(ScreenBus.VENDOR_ORDER, new ShowScreenHandler() {
            
            @Override
            public void showScreen() {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            org.openelis.ui.widget.Window window = new org.openelis.ui.widget.Window();
                            window.setName(Messages.get().vendorOrder());
                            window.setSize("880px", "588px");
                            window.setContent(new VendorOrderScreenUI(window));
                            OpenELIS.getBrowser().addWindow(window, "vendorOrder");
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
        
        ScreenBus.get().addHandler(ScreenBus.SENDOUT_ORDER, new ShowScreenHandler() {
            
            @Override
            public void showScreen() {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            org.openelis.ui.widget.Window window = new org.openelis.ui.widget.Window();
                            window.setName(Messages.get().sendoutOrder());
                            window.setSize("1020px", "588px");
                            window.setContent(new SendoutOrderScreenUI(window));
                            OpenELIS.getBrowser().addWindow(window, "sendoutOrder");
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
