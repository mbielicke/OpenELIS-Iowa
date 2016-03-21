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
                            addInternalOrderScreen();
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
                            addVendorOrderScreen();
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
                            addSendoutOrderScreen();
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
    
    public InternalOrderScreenUI addInternalOrderScreen() throws Exception {
        org.openelis.ui.widget.Window window;
        InternalOrderScreenUI screen;
        
        window = new org.openelis.ui.widget.Window();
        window.setName(Messages.get().order_internalOrder());
        window.setSize("880px", "588px");
        screen = new InternalOrderScreenUI(window);
        window.setContent(screen);
        OpenELIS.getBrowser().addWindow(window, "internalOrder");
        
        return screen;
    }
    
    public VendorOrderScreenUI addVendorOrderScreen() throws Exception {
        org.openelis.ui.widget.Window window;
        VendorOrderScreenUI screen;
        
        window = new org.openelis.ui.widget.Window();
        window.setName(Messages.get().order_vendorOrder());
        window.setSize("880px", "588px");
        screen = new VendorOrderScreenUI(window);
        window.setContent(screen);
        OpenELIS.getBrowser().addWindow(window, "vendorOrder");
        
        return screen;
    }
    
    public SendoutOrderScreenUI addSendoutOrderScreen() throws Exception {
        org.openelis.ui.widget.Window window;
        SendoutOrderScreenUI screen;
        
        window = new org.openelis.ui.widget.Window();
        window.setName(Messages.get().order_sendoutOrder());
        window.setSize("960px", "588px");
        screen = new SendoutOrderScreenUI(window);
        window.setContent(screen);
        OpenELIS.getBrowser().addWindow(window, "sendoutOrder");
        
        return screen;
    }
}