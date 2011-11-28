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
import org.openelis.gwt.common.ModulePermission;
import org.openelis.gwt.common.RPC;
import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.screen.ScreenDefInt;
import org.openelis.gwt.screen.ScreenSessionTimer;
import org.openelis.gwt.services.ScreenService;
import org.openelis.gwt.widget.Confirm;
import org.openelis.gwt.widget.Label;
import org.openelis.gwt.widget.web.WebWindow;
import org.openelis.web.modules.dataView.client.DataViewEnvironmentalScreen;
import org.openelis.web.modules.finalReport.client.FinalReportEnvironmentalScreen;
import org.openelis.web.modules.finalReport.client.FinalReportPrivateWellScreen;
import org.openelis.web.modules.finalReport.client.FinalReportSDWISScreen;
import org.openelis.web.modules.home.client.HomeScreen;
import org.openelis.web.modules.notificationPreference.client.NotificationPreferenceScreen;
import org.openelis.web.modules.sampleStatusReport.client.SampleStatusScreen;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.ClosingEvent;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.SyncCallback;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

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
        Section section;
        Label<String> link;

        content = (AbsolutePanel)def.getWidget("content");
        content.add(window);

        //
        // link the logo with home screen
        //
        link = (Label)def.getWidget("logo");
        if (link != null) {
            link.addClickHandler(new ClickHandler() {
                public void onClick(ClickEvent event) {
                    try {
                        setScreen(new HomeScreen(), "Home", "home");
                    } catch (Exception e) {
                        e.printStackTrace();
                        Window.alert(e.getMessage());
                    }
                }
            });
        }

        //
        // section: RESULT REPORT
        // final reports & result by analyte
        section = new Section("RESULT REPORTS");

        if (showMenu("w_final_environmental")) {
            link = section.addLink("ENVIRONMENTAL FINAL REPORT");
            link.addClickHandler(new ClickHandler() {
                public void onClick(ClickEvent event) {
                    try {
                        Screen screen = new FinalReportEnvironmentalScreen();
                        setScreen(screen, consts.get("environmentalFinalReport"),
                                  "finalReportEnvironmental");
                        window.setCrumbLink(null);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Window.alert(e.getMessage());
                    }
                }
            });
        }

        if (showMenu("w_final_privatewell")) {
            link = section.addLink("PRIVATE WELL FINAL REPORT");
            link.addClickHandler(new ClickHandler() {
                public void onClick(ClickEvent event) {
                    try {
                        Screen screen = new FinalReportPrivateWellScreen();
                        setScreen(screen,
                                  consts.get("privateWellFinalReport"),
                                  "finalReportPrivateWell");
                        window.setCrumbLink(null);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Window.alert(e.getMessage());
                    }
                }
            });
        }

        if (showMenu("w_final_sdwis")) {
            link = section.addLink("SDWIS FINAL REPORT");
            link.addClickHandler(new ClickHandler() {
                public void onClick(ClickEvent event) {
                    try {
                        Screen screen = new FinalReportSDWISScreen();
                        setScreen(screen, consts.get("sdwisFinalReport"), "finalReportSDWIS");
                        window.setCrumbLink(null);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Window.alert(e.getMessage());
                    }
                }
            });
        }

        if (showMenu("w_dataview_environmental")) {
            link = section.addLink("ENVIRONMENTAL RESULT BY ANALYTE");
            link.addClickHandler(new ClickHandler() {
                public void onClick(ClickEvent event) {
                    try {
                        Screen screen = new DataViewEnvironmentalScreen();
                        setScreen(screen, consts.get("environmentalSamplesByTest"),
                                  "environmentalSampleByTest");
                        window.setCrumbLink(null);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Window.alert(e.getMessage());
                    }
                }
            });
        }

        if (showMenu("w_dataview_privatewell")) {
            link = section.addLink("PRIVATE WELL RESULT BY ANALYTE");
            link.addClickHandler(new ClickHandler() {
                public void onClick(ClickEvent event) {
                    try {
                        window.setCrumbLink(null);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Window.alert(e.getMessage());
                    }
                }
            });
        }

        if (showMenu("w_dataview_sdwis")) {
            link = section.addLink("SDWIS RESULT BY ANALYTE");
            link.addClickHandler(new ClickHandler() {
                public void onClick(ClickEvent event) {
                    try {
                        window.setCrumbLink(null);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Window.alert(e.getMessage());
                    }
                }
            });
        }

        //
        // section: SAMPLE
        //
        section = new Section("STATUS REPORTS");

        if (showMenu("w_status")) {
            link = section.addLink("STATUS OF SAMPLES RECEIVED");
            link.addClickHandler(new ClickHandler() {
                public void onClick(ClickEvent event) {
                    try {
                        Screen screen = new SampleStatusScreen();
                        setScreen(screen, consts.get("sampleInhouseStatusReport"), "sampleStatus");
                        window.setCrumbLink(null);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Window.alert(e.getMessage());
                    }
                }
            });
        }

        // section: ACCOUNT
        // username, change password, logout is always enabled so add them first
        section = new Section("ACCOUNT");

        section.addLabel(UserCache.getPermission().getLoginName());

        if (showMenu("w_notify")) {
            link = section.addLink("SAMPLE EMAIL NOTIFICATION");
            link.addClickHandler(new ClickHandler() {
                public void onClick(ClickEvent event) {
                    try {
                        Screen screen = new NotificationPreferenceScreen();
                        setScreen(screen, consts.get("notificationPreference"), "notificationPref");
                        window.setCrumbLink(null);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Window.alert(e.getMessage());
                    }
                }
            });
        }

        link = section.addLink("CHANGE PASSWORD");
        link.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                try {
                    Window.open("https://www.shl.uiowa.edu/ldap/loginView.jsp", "password", null);
                    window.setCrumbLink(null);
                } catch (Throwable e) {
                    e.printStackTrace();
                    Window.alert(e.getMessage());
                }
            }
        });

        link = section.addLink("LOGOUT");
        link.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                try {
                    logout();
                } catch (Throwable e) {
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
     * This method is called to initialize the screen and set the main screen
     * content.
     */
    private void setScreen(Screen screen, String name, String key) {
        screen.getDefinition().setName(name);
        window.setContent(screen);
    }

    /**
     * Convenience method to check for a module select permission to enable it
     * on menu
     */
    private boolean showMenu(String module) {
        ModulePermission perm;

        perm = UserCache.getPermission().getModule(module);
        return perm != null && perm.hasSelectPermission();
    }

    /*
     * Section subclass manages section header and link items
     */

    private class Section {
        int             count;
        boolean         first;
        String          title;
        HorizontalPanel section;
        VerticalPanel   current;

        public Section(String sectionTitle) {
            title = sectionTitle;
            first = true;
            count = 0;
            current = null;
        }

        public void addLabel(String text) {
            add(text, "label");
        }

        public Label<String> addLink(String text) {
            final Label<String> l;

            l = add(text, "link");
            l.addMouseOverHandler(new MouseOverHandler() {
                public void onMouseOver(MouseOverEvent event) {
                    l.setStyleName("link-hover");
                }
            });
            l.addMouseOutHandler(new MouseOutHandler() {
                public void onMouseOut(MouseOutEvent event) {
                    l.setStyleName("link");
                }
            });
            
            return l;
        }

        public Label<String> add(String text, String style) {
            HorizontalPanel header;

            //
            // since the menus are based on user permission, we will delay adding a
            // menu section until the user has a menu item for that section.
            //
            if (first) {
                first = false;
                section = new HorizontalPanel();
                section.addStyleName("header-section");

                header = (HorizontalPanel)def.getWidget("header");
                header.add(section);
            }
            if (current == null || count % 4 == 0) {
                current = new VerticalPanel();
                current.addStyleName("header-subsection");
                section.add(current);
                addLabel( (count == 0) ? title : "", "section-label");
            }

            count++ ;
            return addLabel(text, style);
        }

        private Label<String> addLabel(String text, String style) {
            Label<String> l;

            l = new Label<String>();
            l.setText(text);
            l.setStyleName(style);
            current.add(l);

            return l;
        }
    }
}
