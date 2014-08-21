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

import com.google.gwt.core.client.GWT;
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
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import org.openelis.constants.Messages;
import org.openelis.domain.Constants;
import org.openelis.ui.common.ModulePermission;
import org.openelis.ui.widget.Label;
import org.openelis.ui.widget.web.WebWindow;
import org.openelis.web.cache.UserCache;
import org.openelis.web.modules.dataView.client.DataViewEnvironmentalScreen;
import org.openelis.web.modules.finalReport.client.FinalReportEnvironmentalScreen;
import org.openelis.web.modules.finalReport.client.FinalReportPrivateWellScreen;
import org.openelis.web.modules.finalReport.client.FinalReportSDWISScreen;
import org.openelis.web.modules.home.client.HomeScreen;
import org.openelis.web.modules.main.client.resources.Resources;
import org.openelis.web.modules.main.client.resources.Style;
import org.openelis.web.modules.notificationPreference.client.NotificationPreferenceScreen;
import org.openelis.web.modules.sampleStatusReport.client.SampleStatusScreen;
import org.openelis.web.modules.followup.client.CasesScreen;

/**
 * 
 * This class draws the initial screen for the OpenELIS Web interface. All
 * screen navigations will be handled by this class.
 * 
 */
public class OpenELIS extends ResizeComposite {

    @UiTemplate("OpenELIS.ui.xml")
    interface OpenELISUiBinder extends UiBinder<Widget, OpenELIS> {
    };

    private static final OpenELISUiBinder uiBinder = GWT.create(OpenELISUiBinder.class);

    /**
     * This panel is where the screen content is displayed
     */
    protected AbsolutePanel               linksPanel;

    @UiField
    protected Label<String>               logo;

    @UiField
    protected LayoutPanel             header;
    
    @UiField
    protected FlowPanel                   resultSection,statusSection,followupSection,accountSection;

    /**
     * Static window used to display status messages
     */
    @UiField
    protected WebWindow                   window;

    protected Style                       css;
    /**
     * No-arg Constructor
     * 
     * @throws Exception
     */
    public OpenELIS() throws Exception {
        OpenELISRPC rpc;
        
        css = Resources.INSTANCE.style();
        css.ensureInjected();

        try {
            rpc = OpenELISWebService.get().initialData();
            Constants.setConstants(rpc.constants);
        } catch (Exception anyE) {
            Window.alert("APPLICATION UNAVAILABLE; Please contact Laboratory for support");
            logout();
            return;
        }

        initWidget(uiBinder.createAndBindUi(this));

        initialize();

        setScreen(new HomeScreen(window), "Home", "home");
    }

    /**
     * This method will set up initial widgets for the main screen and load the
     * Menu screen into the content panel.
     */
    protected void initialize() {
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

       
        if (showMenu("w_final_environmental")) {
            link = createLink("ENVIRONMENTAL FINAL REPORT");
            resultSection.add(link);
            link.addClickHandler(new ClickHandler() {
                public void onClick(ClickEvent event) {
                    try {
                        setScreen(new FinalReportEnvironmentalScreen(window),
                                  Messages.get().environmentalFinalReport(),
                                  "finalReportEnvironmental");
                    } catch (Exception e) {
                        e.printStackTrace();
                        Window.alert(e.getMessage());
                    }
                }
            });
        }

        if (showMenu("w_final_privatewell")) {
            link = createLink("PRIVATE WELL FINAL REPORT");
            resultSection.add(link);
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
            link = createLink("SDWIS FINAL REPORT");
            resultSection.add(link);
            link.addClickHandler(new ClickHandler() {
                public void onClick(ClickEvent event) {
                    try {
                        setScreen(new FinalReportSDWISScreen(window),
                                  Messages.get().sdwisFinalReport(),
                                  "finalReportSDWIS");
                    } catch (Exception e) {
                        e.printStackTrace();
                        Window.alert(e.getMessage());
                    }
                }
            });
        }

        if (showMenu("w_dataview_environmental")) {
            link = createLink("ENVIRONMENTAL RESULT BY ANALYTE");
            resultSection.add(link);
            link.addClickHandler(new ClickHandler() {
                public void onClick(ClickEvent event) {
                    try {
                        setScreen(new DataViewEnvironmentalScreen(window),
                                  Messages.get().environmentalResultByAnalyte(),
                                  "environmentalResultByAnalyte");
                    } catch (Exception e) {
                        e.printStackTrace();
                        Window.alert(e.getMessage());
                    }
                }
            });
        }


        if (showMenu("w_status")) {
            link = createLink("STATUS OF SAMPLES RECEIVED");
            statusSection.add(link);
            link.addClickHandler(new ClickHandler() {
                public void onClick(ClickEvent event) {
                    try {
                        setScreen(new SampleStatusScreen(window),
                                  Messages.get().sampleInhouseStatusReport(),
                                  "sampleStatus");
                    } catch (Exception e) {
                        e.printStackTrace();
                        Window.alert(e.getMessage());
                    }
                }
            });
        }


        accountSection.add(new Label<String>(UserCache.getPermission().getLoginName()));

        if (showMenu("w_notify")) {
            link = createLink("SAMPLE EMAIL NOTIFICATION");
            accountSection.add(link);
            link.addClickHandler(new ClickHandler() {
                public void onClick(ClickEvent event) {
                    try {
                        setScreen(new NotificationPreferenceScreen(window),
                                  Messages.get().notificationPreference(),
                                  "notificationPref");
                    } catch (Exception e) {
                        e.printStackTrace();
                        Window.alert(e.getMessage());
                    }
                }
            });
        }
        
        link = createLink("Cases");
        followupSection.add(link);
        link.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                try {
                    setScreen(new CasesScreen(window), "Cases", "cases");
                }catch(Exception e) {
                    e.printStackTrace();
                    Window.alert(e.getMessage());
                }
            }
        });

        link = createLink("CHANGE PASSWORD");
        accountSection.add(link);
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

        link = createLink("LOGOUT");
        accountSection.add(link);
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
        window.setName(name);
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
    
    private Label<String> createLink(String text) {
        final Label<String> l;

        l = new Label<String>();
        l.setText(text);
        l.setStyleName(css.link());
        
        l.addMouseOverHandler(new MouseOverHandler() {
            public void onMouseOver(MouseOverEvent event) {
                l.setStyleName(css.link_hover());
            }
        });
        l.addMouseOutHandler(new MouseOutHandler() {
            public void onMouseOut(MouseOutEvent event) {
                l.setStyleName(css.link());
            }
        });

        return l;
    }
}
