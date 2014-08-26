package org.openelis.portal.client;

import static org.openelis.portal.client.Logger.remote;

import java.util.logging.Level;

import org.openelis.portal.client.resources.Resources;
import org.openelis.portal.modules.desktop.client.DesktopScreen;
import org.openelis.ui.screen.Screen;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class OpenELISPortalEntry implements EntryPoint, NativePreviewHandler {

    /**
     * This is invoked by the GWT JavaScript to start the Application
     */
    public void onModuleLoad() {
        // All Events will flow this this handler first before any other
        // handlers.
        Event.addNativePreviewHandler(this);
        Resources.INSTANCE.style().ensureInjected();
        Resources.INSTANCE.general().ensureInjected();
        Resources.INSTANCE.icon().ensureInjected();
        try {
            RootPanel.get("main").clear();
            RootLayoutPanel.get().add((Screen)GWT.create(DesktopScreen.class));

            SessionTimer.start();
        } catch (Throwable e) {
            remote.log(Level.SEVERE,e.getMessage(),e);
            Window.alert("Unable to start app : " + e.getMessage());
        }
    }

    /**
     * All events created by the application will flow through here. The event
     * can be inspected for type and other user input then certain actions can
     * be taken such as preventing default browser before or even cancelling
     * events
     */
    public void onPreviewNativeEvent(NativePreviewEvent event) {
        // This check is to prevent FireFox from highlighting HTML Elements when
        // mouseDown is combined with the ctrl key
        if (event.getTypeInt() == Event.ONMOUSEDOWN && event.getNativeEvent().getCtrlKey())
            event.getNativeEvent().preventDefault();
        if (event.getTypeInt() == Event.ONMOUSEWHEEL && event.getNativeEvent().getShiftKey())
            event.getNativeEvent().preventDefault();

    }
}
