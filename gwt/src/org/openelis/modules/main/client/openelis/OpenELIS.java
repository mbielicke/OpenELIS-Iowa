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
package org.openelis.modules.main.client.openelis;

import java.util.ArrayList;
import java.util.HashMap;

import org.openelis.gwt.common.SecurityUtil;
import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.screen.ScreenDefInt;
import org.openelis.gwt.services.ScreenService;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.MenuItem;
import org.openelis.gwt.widget.WindowBrowser;
import org.openelis.modules.analyte.client.AnalyteScreen;
import org.openelis.modules.auxiliary.client.AuxiliaryScreen;
import org.openelis.modules.dictionary.client.DictionaryScreen;
import org.openelis.modules.environmentalSampleLogin.client.EnvironmentalSampleLoginScreen;
import org.openelis.modules.favorites.client.FavoritesScreen;
import org.openelis.modules.instrument.client.InstrumentScreen;
import org.openelis.modules.inventoryItem.client.InventoryItemScreen;
import org.openelis.modules.label.client.LabelScreen;
import org.openelis.modules.method.client.MethodScreen;
import org.openelis.modules.order.client.InternalOrderScreen;
import org.openelis.modules.organization.client.OrganizationScreen;
import org.openelis.modules.panel.client.PanelScreen;
import org.openelis.modules.privateWellWaterSampleLogin.client.PrivateWellWaterSampleLoginScreen;
import org.openelis.modules.project.client.ProjectScreen;
import org.openelis.modules.provider.client.ProviderScreen;
import org.openelis.modules.qaevent.client.QaEventScreen;
import org.openelis.modules.qc.client.QcScreen;
import org.openelis.modules.reviewRelease.client.ReviewReleaseScreen;
import org.openelis.modules.sampleTracking.client.SampleTrackingScreen;
import org.openelis.modules.section.client.SectionScreen;
import org.openelis.modules.standardnote.client.StandardNoteScreen;
import org.openelis.modules.storage.client.StorageLocationScreen;
import org.openelis.modules.storageunit.client.StorageUnitScreen;
import org.openelis.modules.systemvariable.client.SystemVariableScreen;
import org.openelis.modules.test.client.TestScreen;
import org.openelis.modules.testTrailer.client.TestTrailerScreen;
import org.openelis.modules.worksheetCreation.client.WorksheetCreationScreen;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.VerticalPanel;

public class OpenELIS extends Screen {

    public static WindowBrowser             browser;
    public static SecurityUtil              security;
    private FavoritesScreen                 fv;
    public static ArrayList<String>         modules = new ArrayList<String>();
    private static HashMap<String, HashMap> cacheList;

    public OpenELIS() throws Exception {
        service = new ScreenService("OpenELISServlet?service=org.openelis.modules.main.server.OpenELISScreenService");
        OpenELISRPC rpc = service.call("initialData");
        Screen.consts = rpc.appConstants;
        security = rpc.security;
        drawScreen((ScreenDefInt)GWT.create(OpenELISDef.class));
        browser = (WindowBrowser)def.getWidget("browser");
        browser.setBrowserHeight();
        setHandlers();
    }

    public void setHandlers() {
        addClickHandler("preference", new ClickHandler() {
            public void onClick(ClickEvent event) {
                // browser.addScreen(new )
            }
        });
        addClickHandler("FavoritesMenu", new ClickHandler() {
            public void onClick(ClickEvent event) {
                VerticalPanel fmp = (VerticalPanel)def.getWidget("favoritesPanel");
                if (fmp.getWidgetCount() == 1) {
                    try {
                        fv = new FavoritesScreen(def);
                        fmp.add(fv);
                    } catch (Throwable e) {
                        e.printStackTrace();
                        Window.alert(e.getMessage());
                    }
                }
                fmp.setVisible( !fmp.isVisible());
                browser.setBrowserHeight();
            }
        });
        ((AppButton)def.getWidget("EditFavorites")).addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                if (fv.editing)
                    fv.stopEditing();
                else
                    fv.edit();
            }
        });
        addClickHandler("Logout", new ClickHandler() {
            public void onClick(ClickEvent event) {
                try {
                    service.call("logout");
                    Window.open("http://www.uhl.uiowa.edu", "_self", null);
                } catch (Throwable e) {
                    e.printStackTrace();
                    Window.alert(e.getMessage());
                }
            }
        });
        addClickHandler("quickEntry", new ClickHandler() {
            public void onClick(ClickEvent event) {
                // browser.addScreen(new )
            }
        });
        addClickHandler("tracking", new ClickHandler() {
            public void onClick(ClickEvent event) {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            browser.addScreen(new SampleTrackingScreen());
                        } catch (Throwable e) {
                            e.printStackTrace();
                            Window.alert(e.getMessage());
                        }
                    }

                    public void onFailure(Throwable caught) {
                        caught.printStackTrace();
                        Window.alert(caught.getMessage());
                    }
                });
            }
        });
        addClickHandler("environmentalSampleLogin", new ClickHandler() {
            public void onClick(ClickEvent event) {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            browser.addScreen(new EnvironmentalSampleLoginScreen());
                        } catch (Throwable e) {
                            e.printStackTrace();
                            Window.alert(e.getMessage());
                        }
                    }

                    public void onFailure(Throwable caught) {
                        caught.printStackTrace();
                        Window.alert(caught.getMessage());
                    }
                });
            }
        });
        
        addClickHandler("clinicalSampleLogin", new ClickHandler() {
            public void onClick(ClickEvent event) {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            //browser.addScreen(new ClinicalSampleLoginScreen());
                        } catch (Throwable e) {
                            e.printStackTrace();
                            Window.alert(e.getMessage());
                        }
                    }

                    public void onFailure(Throwable caught) {
                        caught.printStackTrace();
                        Window.alert(caught.getMessage());
                    }
                });
            }
        });
        
        addClickHandler("animalSampleLogin", new ClickHandler() {
            public void onClick(ClickEvent event) {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                           // browser.addScreen(new AnimalSampleLoginScreen());
                        } catch (Throwable e) {
                            e.printStackTrace();
                            Window.alert(e.getMessage());
                        }
                    }

                    public void onFailure(Throwable caught) {
                        caught.printStackTrace();
                        Window.alert(caught.getMessage());
                    }
                });
            }
        });
        
        addClickHandler("newbornScreeningSampleLogin", new ClickHandler() {
            public void onClick(ClickEvent event) {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                           // browser.addScreen(new NewbornScreeningSampleLoginScreen());
                        } catch (Throwable e) {
                            e.printStackTrace();
                            Window.alert(e.getMessage());
                        }
                    }

                    public void onFailure(Throwable caught) {
                        caught.printStackTrace();
                        Window.alert(caught.getMessage());
                    }
                });
            }
        });
        
        addClickHandler("ptSampleLogin", new ClickHandler() {
            public void onClick(ClickEvent event) {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                           // browser.addScreen(new PTSampleLoginScreen());
                        } catch (Throwable e) {
                            e.printStackTrace();
                            Window.alert(e.getMessage());
                        }
                    }

                    public void onFailure(Throwable caught) {
                        caught.printStackTrace();
                        Window.alert(caught.getMessage());
                    }
                });
            }
        });
        addClickHandler("sdwisSampleLogin", new ClickHandler() {
            public void onClick(ClickEvent event) {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                          //  browser.addScreen(new SDWISSampleLoginScreen());
                        } catch (Throwable e) {
                            e.printStackTrace();
                            Window.alert(e.getMessage());
                        }
                    }

                    public void onFailure(Throwable caught) {
                        caught.printStackTrace();
                        Window.alert(caught.getMessage());
                    }
                });
            }
        });
        addClickHandler("privateWellWaterSampleLogin", new ClickHandler() {
            public void onClick(ClickEvent event) {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                           browser.addScreen(new PrivateWellWaterSampleLoginScreen());
                        } catch (Throwable e) {
                            e.printStackTrace();
                            Window.alert(e.getMessage());
                        }
                    }

                    public void onFailure(Throwable caught) {
                        caught.printStackTrace();
                        Window.alert(caught.getMessage());
                    }
                });
            }
        });
        
        addClickHandler("project", new ClickHandler() {
            public void onClick(ClickEvent event) {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            browser.addScreen(new ProjectScreen());
                        } catch (Throwable e) {
                            e.printStackTrace();
                        }
                    }

                    public void onFailure(Throwable caught) {
                        caught.printStackTrace();
                        Window.alert(caught.getMessage());
                    }
                });
            }
        });
        addClickHandler("provider", new ClickHandler() {
            public void onClick(ClickEvent event) {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            browser.addScreen(new ProviderScreen());
                        } catch (Throwable e) {
                            e.printStackTrace();
                            Window.alert(e.getMessage());
                        }
                    }

                    public void onFailure(Throwable caught) {
                        caught.printStackTrace();
                        Window.alert(caught.getMessage());
                    }
                });
            }
        });
        addClickHandler("organization", new ClickHandler() {
            public void onClick(ClickEvent event) {

                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            browser.addScreen(new OrganizationScreen());
                        } catch (Throwable e) {
                            e.printStackTrace();
                            Window.alert(e.getMessage());
                        }
                    }

                    public void onFailure(Throwable caught) {
                        caught.printStackTrace();
                        Window.alert(caught.getMessage());
                    }
                });
            }
        });
        addClickHandler("worksheetCreation", new ClickHandler() {
            public void onClick(ClickEvent event) {

                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            browser.addScreen(new WorksheetCreationScreen());
                        } catch (Throwable e) {
                            e.printStackTrace();
                            Window.alert(e.getMessage());
                        }
                    }

                    public void onFailure(Throwable caught) {
                        caught.printStackTrace();
                        Window.alert(caught.getMessage());
                    }
                });
            }
        });
        addClickHandler("worksheetCompletion", new ClickHandler() {
            public void onClick(ClickEvent event) {
                // browser.addScreen(new )
            }
        });
        addClickHandler("addOrCancel", new ClickHandler() {
            public void onClick(ClickEvent event) {
                // browser.addScreen(new )
            }
        });
        addClickHandler("reviewAndRelease", new ClickHandler() {
            public void onClick(ClickEvent event) {

                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            browser.addScreen(new ReviewReleaseScreen());
                        } catch (Throwable e) {
                            e.printStackTrace();
                            Window.alert(e.getMessage());
                        }
                    }

                    public void onFailure(Throwable caught) {
                        caught.printStackTrace();
                        Window.alert(caught.getMessage());
                    }
                });
            }
        });
        addClickHandler("toDo", new ClickHandler() {
            public void onClick(ClickEvent event) {
                // browser.addScreen(new )
            }
        });
        addClickHandler("labelFor", new ClickHandler() {
            public void onClick(ClickEvent event) {
                // browser.addScreen(new )
            }
        });
        
        addClickHandler("storageLocation", new ClickHandler() {
            public void onClick(ClickEvent event) {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            browser.addScreen(new StorageLocationScreen());
                        } catch (Throwable e) {
                            e.printStackTrace();
                            Window.alert(e.getMessage());
                        }
                    }

                    public void onFailure(Throwable caught) {
                        caught.printStackTrace();
                        Window.alert(caught.getMessage());
                    }
                });
            }
        });
        
        addClickHandler("QC", new ClickHandler() {
            public void onClick(ClickEvent event) {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            browser.addScreen(new QcScreen());
                        } catch (Throwable e) {
                            e.printStackTrace();
                            Window.alert(e.getMessage());
                        }
                    }

                    public void onFailure(Throwable caught) {
                        caught.printStackTrace();
                        Window.alert(caught.getMessage());
                    }
                });
            }
        });
        
        addClickHandler("internalOrder", new ClickHandler() {
            public void onClick(ClickEvent event) {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            browser.addScreen(new InternalOrderScreen());
                        } catch (Throwable e) {
                            e.printStackTrace();
                            Window.alert(e.getMessage());
                        }
                    }

                    public void onFailure(Throwable caught) {
                        caught.printStackTrace();
                        Window.alert(caught.getMessage());
                    }
                });
            }
        });
        
        addClickHandler("vendorOrder", new ClickHandler() {
            public void onClick(ClickEvent event) {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                         //   browser.addScreen(new OrderScreen(new String[] {"external"}));
                        } catch (Throwable e) {
                            e.printStackTrace();
                            Window.alert(e.getMessage());
                        }
                    }

                    public void onFailure(Throwable caught) {
                        caught.printStackTrace();
                        Window.alert(caught.getMessage());
                    }
                });
            }
        });
        
        addClickHandler("kitOrder", new ClickHandler() {
            public void onClick(ClickEvent event) {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                         //   browser.addScreen(new OrderScreen(new String[] {"kits"}));
                        } catch (Throwable e) {
                            e.printStackTrace();
                            Window.alert(e.getMessage());
                        }
                    }

                    public void onFailure(Throwable caught) {
                        caught.printStackTrace();
                        Window.alert(caught.getMessage());
                    }
                });
            }
        });
        
        addClickHandler("fillOrder", new ClickHandler() {
            public void onClick(ClickEvent event) {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                         //   browser.addScreen(new FillOrderScreen());
                        } catch (Throwable e) {
                            e.printStackTrace();
                            Window.alert(e.getMessage());
                        }
                    }

                    public void onFailure(Throwable caught) {
                        caught.printStackTrace();
                        Window.alert(caught.getMessage());
                    }
                });
            }
        });
        addClickHandler("shipping", new ClickHandler() {
            public void onClick(ClickEvent event) {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                         //   browser.addScreen(new ShippingScreen());
                        } catch (Throwable e) {
                            e.printStackTrace();
                            Window.alert(e.getMessage());
                        }
                    }

                    public void onFailure(Throwable caught) {
                        caught.printStackTrace();
                        Window.alert(caught.getMessage());
                    }
                });
            }
        });
        
        addClickHandler("buildKits", new ClickHandler() {
            public void onClick(ClickEvent event) {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                         //   browser.addScreen(new BuildKitsScreen());
                        } catch (Throwable e) {
                            e.printStackTrace();
                            Window.alert(e.getMessage());
                        }
                    }

                    public void onFailure(Throwable caught) {
                        caught.printStackTrace();
                        Window.alert(caught.getMessage());
                    }
                });
            }
        });
        addClickHandler("inventoryTransfer", new ClickHandler() {
            public void onClick(ClickEvent event) {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                          //  browser.addScreen(new InventoryReceiptScreen());
                        } catch (Throwable e) {
                            e.printStackTrace();
                            Window.alert(e.getMessage());
                        }
                    }

                    public void onFailure(Throwable caught) {
                        caught.printStackTrace();
                        Window.alert(caught.getMessage());
                    }
                });
            }
        });
        addClickHandler("inventoryAdjustment", new ClickHandler() {
            public void onClick(ClickEvent event) {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                       //     browser.addScreen(new InventoryAdjustmentScreen());
                        } catch (Throwable e) {
                            e.printStackTrace();
                            Window.alert(e.getMessage());
                        }
                    }

                    public void onFailure(Throwable caught) {
                        caught.printStackTrace();
                        Window.alert(caught.getMessage());
                    }
                });
            }
        });
        addClickHandler("inventoryItem", new ClickHandler() {
            public void onClick(ClickEvent event) {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            browser.addScreen(new InventoryItemScreen());
                        } catch (Throwable e) {
                            e.printStackTrace();
                            Window.alert(e.getMessage());
                        }
                    }

                    public void onFailure(Throwable caught) {
                        caught.printStackTrace();
                        Window.alert(caught.getMessage());
                    }
                });
            }
        });
        
        addClickHandler("instrument", new ClickHandler() {
            public void onClick(ClickEvent event) {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            browser.addScreen(new InstrumentScreen());
                        } catch (Throwable e) {
                            e.printStackTrace();
                            Window.alert(e.getMessage());
                        }
                    }

                    public void onFailure(Throwable caught) {
                        caught.printStackTrace();
                        Window.alert(caught.getMessage());
                    }
                });
            }
        });
        
        addClickHandler("test", new ClickHandler() {
            public void onClick(ClickEvent event) {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            browser.addScreen(new TestScreen());
                        } catch (Throwable e) {
                            e.printStackTrace();
                            Window.alert(e.getMessage());
                        }
                    }

                    public void onFailure(Throwable caught) {
                        caught.printStackTrace();
                        Window.alert(caught.getMessage());
                    }
                });
            }
        });
        addClickHandler("method", new ClickHandler() {
            public void onClick(ClickEvent event) {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            browser.addScreen(new MethodScreen());
                        } catch (Throwable e) {
                            e.printStackTrace();
                            Window.alert(e.getMessage());
                        }
                    }

                    public void onFailure(Throwable caught) {
                        caught.printStackTrace();
                        Window.alert(caught.getMessage());
                    }
                });
            }
        });
        
        addClickHandler("panel", new ClickHandler() {
            public void onClick(ClickEvent event) {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            browser.addScreen(new PanelScreen());
                        } catch (Throwable e) {
                            e.printStackTrace();
                            Window.alert(e.getMessage());
                        }
                    }

                    public void onFailure(Throwable caught) {
                        caught.printStackTrace();
                        Window.alert(caught.getMessage());
                    }
                });
            }
        });
        
        addClickHandler("QAEvent", new ClickHandler() {
            public void onClick(ClickEvent event) {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            browser.addScreen(new QaEventScreen());
                        } catch (Throwable e) {
                            e.printStackTrace();
                            Window.alert(e.getMessage());
                        }
                    }

                    public void onFailure(Throwable caught) {
                        caught.printStackTrace();
                        Window.alert(caught.getMessage());
                    }
                });
            }
        });
        addClickHandler("labSection", new ClickHandler() {
            public void onClick(ClickEvent event) {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            browser.addScreen(new SectionScreen());
                        } catch (Throwable e) {
                            e.printStackTrace();
                            Window.alert(e.getMessage());
                        }
                    }

                    public void onFailure(Throwable caught) {
                        caught.printStackTrace();
                        Window.alert(caught.getMessage());
                    }
                });
            }
        });
        addClickHandler("analyte", new ClickHandler() {
            public void onClick(ClickEvent event) {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            browser.addScreen(new AnalyteScreen());
                        } catch (Throwable e) {
                            e.printStackTrace();
                            Window.alert(e.getMessage());
                        }
                    }

                    public void onFailure(Throwable caught) {
                        caught.printStackTrace();
                        Window.alert(caught.getMessage());
                    }
                });
            }
        });
        addClickHandler("dictionary", new ClickHandler() {
            public void onClick(ClickEvent event) {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            browser.addScreen(new DictionaryScreen());
                        } catch (Throwable e) {
                            e.printStackTrace();
                            Window.alert(e.getMessage());
                        }
                    }

                    public void onFailure(Throwable caught) {
                        caught.printStackTrace();
                        Window.alert(caught.getMessage());
                    }
                });
            }
        });
        
        addClickHandler("auxiliaryPrompt", new ClickHandler() {
            public void onClick(ClickEvent event) {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            browser.addScreen(new AuxiliaryScreen());
                        } catch (Throwable e) {
                            e.printStackTrace();
                            Window.alert(e.getMessage());
                        }
                    }

                    public void onFailure(Throwable caught) {
                        caught.printStackTrace();
                        Window.alert(caught.getMessage());
                    }
                });
            }
        });
        
        addClickHandler("label", new ClickHandler() {
            public void onClick(ClickEvent event) {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            browser.addScreen(new LabelScreen());
                        } catch (Throwable e) {
                            e.printStackTrace();
                            Window.alert(e.getMessage());
                        }
                    }

                    public void onFailure(Throwable caught) {
                        caught.printStackTrace();
                        Window.alert(caught.getMessage());
                    }
                });
            }
        });
        
        addClickHandler("standardNote", new ClickHandler() {
            public void onClick(ClickEvent event) {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            browser.addScreen(new StandardNoteScreen());
                        } catch (Throwable e) {
                            e.printStackTrace();
                            Window.alert(e.getMessage());
                        }
                    }

                    public void onFailure(Throwable caught) {
                        caught.printStackTrace();
                        Window.alert(caught.getMessage());
                    }
                });
            }
        });
        addClickHandler("trailerForTest", new ClickHandler() {
            public void onClick(ClickEvent event) {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            browser.addScreen(new TestTrailerScreen());
                        } catch (Throwable e) {
                            e.printStackTrace();
                            Window.alert(e.getMessage());
                        }
                    }

                    public void onFailure(Throwable caught) {
                        caught.printStackTrace();
                        Window.alert(caught.getMessage());
                    }
                });
            }
        });
        
        addClickHandler("storageUnit", new ClickHandler() {
            public void onClick(ClickEvent event) {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            browser.addScreen(new StorageUnitScreen());
                        } catch (Throwable e) {
                            e.printStackTrace();
                            Window.alert(e.getMessage());
                        }
                    }

                    public void onFailure(Throwable caught) {
                        caught.printStackTrace();
                        Window.alert(caught.getMessage());
                    }
                });
            }
        });
        addClickHandler("storageLocation", new ClickHandler() {
            public void onClick(ClickEvent event) {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                          //  browser.addScreen(new StorageLocationScreen());
                        } catch (Throwable e) {
                            e.printStackTrace();
                            Window.alert(e.getMessage());
                        }
                    }

                    public void onFailure(Throwable caught) {
                        caught.printStackTrace();
                        Window.alert(caught.getMessage());
                    }
                });
            }
        });
        
        addClickHandler("scriptlet", new ClickHandler() {
            public void onClick(ClickEvent event) {
                // browser.addScreen(new )
            }
        });
        addClickHandler("systemVariable", new ClickHandler() {
            public void onClick(ClickEvent event) {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            browser.addScreen(new SystemVariableScreen());
                        } catch (Throwable e) {
                            e.printStackTrace();
                            Window.alert(e.getMessage());
                        }
                    }

                    public void onFailure(Throwable caught) {
                        caught.printStackTrace();
                        Window.alert(caught.getMessage());
                    }
                });
            }
        });
        addClickHandler("finalReport", new ClickHandler() {
            public void onClick(ClickEvent event) {
                // browser.addScreen(new )
            }
        });
        addClickHandler("sampleDataExport", new ClickHandler() {
            public void onClick(ClickEvent event) {
                // browser.addScreen(new )
            }
        });
        addClickHandler("loginLabel", new ClickHandler() {
            public void onClick(ClickEvent event) {
                // browser.addScreen(new )
            }
        });
    }

    public void setStyleToAllCellsInCol(FlexTable table, int col, String style) {
        for (int i = 0; i < table.getRowCount(); i++ ) {
            table.getCellFormatter().addStyleName(i, col, style);
        }
    }

    private void addClickHandler(String screen, ClickHandler handler) {
        MenuItem item;

        item = ((MenuItem)def.getWidget(screen));
        if (item != null)
            item.addClickHandler(handler);
    }

    /**
     * Gobal in browser cache list. Used for ditionary and other objects that
     * are cached in the browser space.
     */
    public static HashMap<String, HashMap> getCacheList() {
        if (cacheList == null)
            cacheList = new HashMap<String, HashMap>();
        return cacheList;
    }

    public static void setCacheList(HashMap<String, HashMap> hash) {
        cacheList = hash;
    }
}
