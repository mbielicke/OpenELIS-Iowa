/**
 * Exhibit A - UIRF Open-source Based Public Software License.
 * 
 * The contents of this file are subject to the UIRF Open-source Based Public
 * Software License(the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * openelis.uhl.uiowa.edu
 * 
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 * 
 * The Original Code is OpenELIS code.
 * 
 * The Initial Developer of the Original Code is The University of Iowa.
 * Portions created by The University of Iowa are Copyright 2006-2008. All
 * Rights Reserved.
 * 
 * Contributor(s): ______________________________________.
 * 
 * Alternatively, the contents of this file marked "Separately-Licensed" may be
 * used under the terms of a UIRF Software license ("UIRF Software License"), in
 * which case the provisions of a UIRF Software License are applicable instead
 * of those above.
 */
package org.openelis.web.modules.main.client;


import org.openelis.ui.resources.UIResources;
import org.openelis.web.modules.main.client.resources.Resources;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry Point to the OpenELISWeb GWT Module.
 */
public class OpenELISEntry implements EntryPoint, NativePreviewHandler {

    /**
     * This is invoked by the GWT JavaScript to start the Application
     */
    public void onModuleLoad() {
        // All Events will flow this this handler first before any other
        // handlers.
        Event.addNativePreviewHandler(this);
        Resources.INSTANCE.style().ensureInjected();
        UIResources.INSTANCE.buttonPanel().ensureInjected();
        UIResources.INSTANCE.calendar().ensureInjected();
        UIResources.INSTANCE.collapse().ensureInjected();
        UIResources.INSTANCE.text().ensureInjected();
        UIResources.INSTANCE.dragDrop().ensureInjected();
        UIResources.INSTANCE.general().ensureInjected();
        try {
            RootPanel.get("main").clear();
            RootLayoutPanel.get().add(new OpenELIS());

            SessionTimer.start();
        } catch (Throwable e) {
            e.printStackTrace();
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
