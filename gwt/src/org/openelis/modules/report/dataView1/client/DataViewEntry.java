package org.openelis.modules.report.dataView1.client;

import static org.openelis.modules.main.client.Logger.*;

import java.util.logging.Level;

import org.openelis.constants.Messages;
import org.openelis.modules.main.client.OpenELIS;
import org.openelis.modules.main.client.ScreenBus;
import org.openelis.modules.main.client.event.ShowScreenHandler;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.user.client.Window;

public class DataViewEntry implements EntryPoint, ShowScreenHandler {

    @Override
    public void onModuleLoad() {
        ScreenBus.get().addHandler(ScreenBus.DATA_VIEW_REPORT, this);
    }

    @Override
    public void showScreen() {
        GWT.runAsync(new RunAsyncCallback() {
            public void onSuccess() {
                try {
                    org.openelis.ui.widget.Window window = new org.openelis.ui.widget.Window(true);
                    window.setName(Messages.get().dataView_dataView());
                    window.setSize("660px", "605px");
                    window.setContent(new DataViewScreenUI(window));
                    OpenELIS.getBrowser().addWindow(window, "dataView");
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
