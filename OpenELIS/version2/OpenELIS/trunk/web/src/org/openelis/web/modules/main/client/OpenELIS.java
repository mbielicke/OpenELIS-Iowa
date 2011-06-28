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

import java.util.HashMap;

import org.openelis.cache.UserCache;
import org.openelis.gwt.common.RPC;
import org.openelis.gwt.common.SystemUserPermission;
import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.screen.ScreenDefInt;
import org.openelis.gwt.screen.ScreenSessionTimer;
import org.openelis.gwt.services.ScreenService;
import org.openelis.gwt.widget.Confirm;
import org.openelis.gwt.widget.IconContainer;
import org.openelis.gwt.widget.Label;
import org.openelis.gwt.widget.MenuItem;
import org.openelis.gwt.widget.web.LinkButton;
import org.openelis.gwt.widget.web.WebWindow;
import org.openelis.modules.main.client.OpenELISDef;
import org.openelis.web.modules.finalReport.client.FinalReportEnvScreen;
import org.openelis.web.modules.finalReport.client.FinalReportPvtScreen;
import org.openelis.web.modules.home.client.HomeScreen;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.ClosingEvent;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.SyncCallback;
import com.google.gwt.user.client.ui.AbsolutePanel;

/**
 * 
 * This class draws the initial screen for the OpenELIS Web interface. All
 * screen navigations will be handled by this class.
 * 
 */
public class OpenELIS extends Screen implements ScreenSessionTimer {

    /**
     * This panel is where the screen content is displayed
     */
    protected AbsolutePanel           content, linksPanel;

    /**
     * HashMap of Screens navigated
     */
    protected HashMap<String, Screen> screens;

    /**
     * Static window used to display status messages
     */
    protected WebWindow               window;

    private Confirm                   timeoutPopup;
    private static Timer              timeoutTimer, forceTimer;
    private static int                SESSION_TIMEOUT = 1000 * 60 * 30, FORCE_TIMEOUT = 1000 * 60;
    private HandlerRegistration       closeHandler;

    /**
     * No-arg Constructor
     * 
     * @throws Exception
     */
    public OpenELIS() throws Exception {
        OpenELISRPC rpc;

        service = new ScreenService("controller?service=org.openelis.web.modules.main.server.OpenELISWebService");
        rpc = service.call("initialData");

        consts = rpc.appConstants;

        drawScreen((ScreenDefInt)GWT.create(OpenELISDef.class));
        window = new WebWindow();

        initializeWindowClose();
        initializeTimeout();
        initialize();

        setScreen(new HomeScreen(), "Home", "home");
    }

    /**
     * This method will set up initial widgets for the main screen and load the
     * Menu screen into the content panel.
     */
    protected void initialize() {
        String name;
        Label<String> userName;

        screens = new HashMap<String, Screen>();
        content = (AbsolutePanel)def.getWidget("content");
        linksPanel = (AbsolutePanel)def.getWidget("links");
        userName = (Label<String>)def.getWidget("userName");

        content.add(window);

        name = null;
        try {
            name = UserCache.getPermission().getFirstName();
            if (name != null)
                name += " " + UserCache.getPermission().getLastName();
            else
                name = UserCache.getName();
        } catch (Exception e) {
            name = "who are you?";
        } finally {
            userName.setText(name);
        }

        History.addValueChangeHandler(new ValueChangeHandler<String>() {
            public void onValueChange(ValueChangeEvent<String> event) {
                gotoScreen(event.getValue());
            }
        });

        //
        // add all the buttons/links
        //
        addClickHandler("logout", new ClickHandler() {
            public void onClick(ClickEvent event) {
                try {
                    logout();
                } catch (Throwable e) {
                    e.printStackTrace();
                    Window.alert(e.getMessage());
                }
            }
        });

        addLinkHandler("homeIcon", "Home", null, new ClickHandler() {
            public void onClick(ClickEvent event) {
                gotoScreen("home");
            }
        });

        addLinkHandler("finalReportIcon", "Environmental Final Report", "w_finalrep_e", new ClickHandler() {
            public void onClick(ClickEvent event) {
                try {
                    Screen screen = new FinalReportEnvScreen();
                    setScreen(screen, "Final Report", "finalReport");
                } catch (Exception e) {
                    e.printStackTrace();
                    Window.alert(e.getMessage());
                }
            }
        });

        addLinkHandler("finalReportIcon", "Private Well Final Report", "w_finalrep_p", new ClickHandler() {
            public void onClick(ClickEvent event) {
                try {
                    Screen screen = new FinalReportPvtScreen();
                    setScreen(screen, "Private Well Final Report", "finalReportPvt");
                } catch (Exception e) {
                    e.printStackTrace();
                    Window.alert(e.getMessage());
                }
            }
        });

        addLinkHandler("requestFormIcon", "Request Forms", "w_requestform", new ClickHandler() {
            public void onClick(ClickEvent event) {
                try {
                } catch (Exception e) {
                    e.printStackTrace();
                    Window.alert(e.getMessage());
                }
            }
        });

        addLinkHandler("statusReportIcon", "Sample Status Report", "w_samplestat", new ClickHandler() {
            public void onClick(ClickEvent event) {
                try {
                } catch (Exception e) {
                    e.printStackTrace();
                    Window.alert(e.getMessage());
                }
            }
        });

        addLinkHandler("turnaroundIcon", "Turnaround Report", "w_turnaround", new ClickHandler() {
            public void onClick(ClickEvent event) {
                try {
                } catch (Exception e) {
                    e.printStackTrace();
                    Window.alert(e.getMessage());
                }
            }
        });

        addLinkHandler("dataMineIcon", "Data Mine Report", "w_datamine", new ClickHandler() {
            public void onClick(ClickEvent event) {
                try {
                } catch (Exception e) {
                    e.printStackTrace();
                    Window.alert(e.getMessage());
                }
            }
        });

        addLinkHandler("reportableIcon", "Reportable Disease", "w_rptdisease", new ClickHandler() {
            public void onClick(ClickEvent event) {
                try {
                } catch (Exception e) {
                    e.printStackTrace();
                    Window.alert(e.getMessage());
                }
            }
        });

        addLinkHandler("onholdReportIcon", "Onhold Report", "w_onhold", new ClickHandler() {
            public void onClick(ClickEvent event) {
                try {
                } catch (Exception e) {
                    e.printStackTrace();
                    Window.alert(e.getMessage());
                }
            }
        });
    }

    /**
     * resets the timeout timer to allow
     */
    public void resetTimeout() {
        timeoutTimer.schedule(SESSION_TIMEOUT);
    }

    protected void initializeTimeout() {
        /*
         * add session timeout dialog box and timers
         */
        timeoutPopup = new Confirm(Confirm.Type.WARN,
                                   consts.get("timeoutHeader"),
                                   consts.get("timeoutWarning"),
                                   consts.get("timeoutExtendTime"),
                                   consts.get("timeoutLogout"));
        timeoutPopup.addSelectionHandler(new SelectionHandler<Integer>() {
            public void onSelection(SelectionEvent<Integer> event) {
                if (event.getSelectedItem() == 0) {
                    forceTimer.cancel();
                    restServerTimeout();
                } else {
                    logout();
                }

            }
        });

        /*
         * if they don't answer the dialog box, we are going to log them out
         * automatically
         */
        forceTimer = new Timer() {
            public void run() {
                logout();
            }
        };

        timeoutTimer = new Timer() {
            public void run() {
                forceTimer.schedule(FORCE_TIMEOUT);
                timeoutPopup.show();
            }
        };
        resetTimeout();
        /*
         * Register the reset timer call
         */
        ScreenService.setScreenSessionTimer(this);
    }

    /**
     * ping the server so we session does not expire
     */
    private void restServerTimeout() {
        service.call("keepAlive", new AsyncCallback<RPC>() {
            public void onSuccess(RPC result) {
            }

            public void onFailure(Throwable caught) {
                Window.alert("Couldn't call the application server; please call your sysadmin");
            }
        });
    }

    /**
     * Sets up the notification for browser close button or navigating away from
     * the application
     */

    private void initializeWindowClose() {
        closeHandler = Window.addWindowClosingHandler(new Window.ClosingHandler() {
            public void onWindowClosing(ClosingEvent event) {
                logout();
            }
        });
    }

    /**
     * logout the user
     */
    private void logout() {
        //
        // close the handler so we don't get called again
        //
        closeHandler.removeHandler();

        service.call("logout", new SyncCallback<RPC>() {
            public void onSuccess(RPC result) {
            }

            public void onFailure(Throwable caught) {
            }
        });
        Window.open("OpenELIS.html", "_self", null);
    }

    /**
     * This method is called statically by other modules to display a Screen for
     * the first time. The Screen will be added to the Screen map and a new
     * History entry will be added to the browser.
     */
    private void setScreen(Screen screen, String name, String key) {
        screens.put(key, screen);
        screen.getDefinition().setName(name);
        History.newItem(key, true);
    }

    /**
     * This method is called statically when a user navigates by using the back
     * or forward buttons
     * 
     * @param key
     */
    private void gotoScreen(String key) {
        Screen screen;

        screen = screens.get(key);
        window.setContent(screen);
    }

    /**
     * register a click handler
     */
    private void addClickHandler(String screenName, ClickHandler handler) {
        Label l;

        l = ((Label)def.getWidget(screenName));
        if (l != null)
            l.addClickHandler(handler);
    }

    private void addLinkHandler(String icon, String balloonHelp, String secModule, ClickHandler handler) {
        LinkButton b;

//        if (secModule == null || UserCache.getPermission().getModule(secModule).hasSelectPermission()) {
            b = new LinkButton(icon, null, balloonHelp, 56, 40);
            b.addClickHandler(handler);
            b.addStyleName("webButton");
            linksPanel.add(b);
//        }
    }
}
