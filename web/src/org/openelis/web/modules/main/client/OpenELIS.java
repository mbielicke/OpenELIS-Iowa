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


import org.openelis.constants.Messages;
import org.openelis.domain.Constants;
import org.openelis.ui.common.ModulePermission;
import org.openelis.ui.screen.Screen;
import org.openelis.ui.widget.Label;
import org.openelis.ui.widget.web.WebWindow;
import org.openelis.web.cache.UserCache;
import org.openelis.web.modules.dataView.client.DataViewEnvironmentalScreen;
import org.openelis.web.modules.finalReport.client.FinalReportEnvironmentalScreen;
import org.openelis.web.modules.finalReport.client.FinalReportPrivateWellScreen;
import org.openelis.web.modules.finalReport.client.FinalReportSDWISScreen;
import org.openelis.web.modules.home.client.HomeScreen;
import org.openelis.web.modules.notificationPreference.client.NotificationPreferenceScreen;
import org.openelis.web.modules.sampleStatusReport.client.SampleStatusScreen;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.SyncCallback;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * 
 * This class draws the initial screen for the OpenELIS Web interface. All
 * screen navigations will be handled by this class.
 * 
 */
public class OpenELIS extends ResizeComposite {
    
    @UiTemplate("OpenELIS.ui.xml")
    interface OpenELISUiBinder extends UiBinder<Widget, OpenELIS>{};
    private static final OpenELISUiBinder uiBinder = GWT.create(OpenELISUiBinder.class);

    /**
     * This panel is where the screen content is displayed
     */    
    protected AbsolutePanel           linksPanel;
    
    @UiField
    protected Label<String>           logo;
    
    @UiField
    protected HorizontalPanel         header;
    

    /**
     * Static window used to display status messages
     */
    @UiField
    protected WebWindow               window;

    /**
     * No-arg Constructor
     * 
     * @throws Exception
     */
    public OpenELIS() throws Exception {
        OpenELISRPC rpc;

        rpc = OpenELISWebService.get().initialData();

        Constants.setConstants(rpc.constants);

        initWidget(uiBinder.createAndBindUi(this));

        initialize();
        
        setScreen(new HomeScreen(window), "Home", "home");

    }

    /**
     * This method will set up initial widgets for the main screen and load the
     * Menu screen into the content panel.
     */
    protected void initialize() {
        Section section;
        Label<String> link;

        //
        // link the logo with home screen
        //
        if (logo != null) {
            logo.addClickHandler(new ClickHandler() {
                public void onClick(ClickEvent event) {
                    try {
                        setScreen(new HomeScreen(window), "Home", "home");
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
                        setScreen(new FinalReportEnvironmentalScreen(window), Messages.get().environmentalFinalReport(),
                                  "finalReportEnvironmental");
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
                        setScreen(new FinalReportPrivateWellScreen(window),
                                  Messages.get().privateWellFinalReport(),
                                  "finalReportPrivateWell");
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
                        setScreen(new FinalReportSDWISScreen(window), Messages.get().sdwisFinalReport(),"finalReportSDWIS");
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
                        setScreen(new DataViewEnvironmentalScreen(window), Messages.get().environmentalResultByAnalyte(),
                                  "environmentalResultByAnalyte");
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
                        setScreen(new SampleStatusScreen(window), Messages.get().sampleInhouseStatusReport(), "sampleStatus");
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
                        setScreen(new NotificationPreferenceScreen(window), Messages.get().notificationPreference(), "notificationPref");
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

    private void logout() {

        OpenELISWebService.get().logout(new SyncCallback<Void>() {
            public void onSuccess(Void result) {
            }

            public void onFailure(Throwable caught) {
            }
        });
        Window.open("/openelisweb/OpenELIS.html", "_self", null);
    }

    /**
     * This method is called to initialize the screen and set the main screen
     * content.
     */
    private void setScreen(Widget screen, String name, String key) {
        //screen.getDefinition().setName(name);
        window.setContent(screen);//setContent(screen);
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
            //
            // since the menus are based on user permission, we will delay adding a
            // menu section until the user has a menu item for that section.
            //
            if (first) {
                first = false;
                section = new HorizontalPanel();
                section.addStyleName("header-section");
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
