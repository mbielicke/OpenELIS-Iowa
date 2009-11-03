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
import org.openelis.gwt.screen.deprecated.AppScreen;
import org.openelis.gwt.screen.deprecated.ScreenMenuPanel;
import org.openelis.gwt.screen.deprecated.ScreenWidget;
import org.openelis.gwt.services.ScreenService;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.MenuItem;
import org.openelis.gwt.widget.WindowBrowser;
import org.openelis.modules.PTSampleLogin.client.PTSampleLoginScreen;
import org.openelis.modules.SDWISSampleLogin.client.SDWISSampleLoginScreen;
import org.openelis.modules.analyte.client.AnalyteScreen;
import org.openelis.modules.animalSampleLogin.client.AnimalSampleLoginScreen;
import org.openelis.modules.auxiliary.client.AuxiliaryScreen;
import org.openelis.modules.buildKits.client.BuildKitsScreen;
import org.openelis.modules.clinicalSampleLogin.client.ClinicalSampleLoginScreen;
import org.openelis.modules.dictionary.client.DictionaryScreen;
import org.openelis.modules.environmentalSampleLogin.client.EnvironmentalSampleLoginScreen;
import org.openelis.modules.favorites.client.FavoritesScreen;
import org.openelis.modules.fillOrder.client.FillOrderScreen;
import org.openelis.modules.instrument.client.InstrumentScreen;
import org.openelis.modules.inventoryAdjustment.client.InventoryAdjustmentScreen;
import org.openelis.modules.inventoryItem.client.InventoryItemScreen;
import org.openelis.modules.inventoryReceipt.client.InventoryReceiptScreen;
import org.openelis.modules.label.client.LabelScreen;
import org.openelis.modules.method.client.MethodScreen;
import org.openelis.modules.newbornScreeningSampleLogin.client.NewbornScreeningSampleLoginScreen;
import org.openelis.modules.order.client.OrderScreen;
import org.openelis.modules.organization.client.OrganizationScreen;
import org.openelis.modules.panel.client.PanelScreen;
import org.openelis.modules.privateWellWaterSampleLogin.client.PrivateWellWaterSampleLoginScreen;
import org.openelis.modules.project.client.ProjectScreen;
import org.openelis.modules.provider.client.ProviderScreen;
import org.openelis.modules.qaevent.client.QaEventScreen;
import org.openelis.modules.qc.client.QcScreen;
import org.openelis.modules.section.client.SectionScreen;
import org.openelis.modules.shipping.client.ShippingScreen;
import org.openelis.modules.standardnote.client.StandardNoteScreen;
import org.openelis.modules.storage.client.StorageLocationScreen;
import org.openelis.modules.storageunit.client.StorageUnitScreen;
import org.openelis.modules.systemvariable.client.SystemVariableScreen;
import org.openelis.modules.test.client.TestScreen;
import org.openelis.modules.testTrailer.client.TestTrailerScreen;
import org.openelis.modules.worksheetCreation.client.WorksheetCreationLookupScreen;
import org.openelis.modules.worksheetCreation.client.WorksheetCreationScreen;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class OpenELIS extends Screen implements ClickHandler {

    public static WindowBrowser             browser;
    public static SecurityUtil              security;
    private FavoritesScreen                 fv;
    private static HashMap<String, HashMap> cacheList;
    public static ArrayList<String>         modules = new ArrayList<String>();

    public OpenELIS() throws Exception {
        service = new ScreenService("OpenELISServlet?service=org.openelis.modules.main.server.OpenELISScreenService");
        OpenELISRPC rpc = service.call("initialData");
        AppScreen.consts = rpc.appConstants;
        Screen.consts = rpc.appConstants;
        security = rpc.security;
        drawScreen((ScreenDefInt)GWT.create(OpenELISDef.class));
        browser = (WindowBrowser)def.getWidget("browser");
        browser.setBrowserHeight();
        setHandlers();
    }

    public void setHandlers() {
        ((MenuItem)def.getWidget("preference")).addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                // browser.addScreen(new )
            }
        });
        ((MenuItem)def.getWidget("FavoritesMenu")).addClickHandler(new ClickHandler() {
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
        ((MenuItem)def.getWidget("Logout")).addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                try {
                    service.callVoid("logout");
                    Window.open("http://www.uhl.uiowa.edu", "_self", null);
                } catch (Throwable e) {
                    e.printStackTrace();
                    Window.alert(e.getMessage());
                }
            }
        });
        ((MenuItem)def.getWidget("quickEntry")).addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                // browser.addScreen(new )
            }
        });
        ((MenuItem)def.getWidget("tracking")).addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                // browser.addScreen(new )
            }
        });
        ((MenuItem)def.getWidget("environmentalSampleLogin")).addClickHandler(new ClickHandler() {
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
        ((MenuItem)def.getWidget("clinicalSampleLogin")).addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            browser.addScreen(new ClinicalSampleLoginScreen());
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
        ((MenuItem)def.getWidget("animalSampleLogin")).addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            browser.addScreen(new AnimalSampleLoginScreen());
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
        ((MenuItem)def.getWidget("newbornScreeningSampleLogin")).addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            browser.addScreen(new NewbornScreeningSampleLoginScreen());
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
        ((MenuItem)def.getWidget("ptSampleLogin")).addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            browser.addScreen(new PTSampleLoginScreen());
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
        ((MenuItem)def.getWidget("sdwisSampleLogin")).addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            browser.addScreen(new SDWISSampleLoginScreen());
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
        ((MenuItem)def.getWidget("privateWellWaterSampleLogin")).addClickHandler(new ClickHandler() {
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
        ((MenuItem)def.getWidget("project")).addClickHandler(new ClickHandler() {
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
        ((MenuItem)def.getWidget("provider")).addClickHandler(new ClickHandler() {
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

        ((MenuItem)def.getWidget("organization")).addClickHandler(new ClickHandler() {
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

        ((MenuItem)def.getWidget("worksheetCreation")).addClickHandler(new ClickHandler() {
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
        ((MenuItem)def.getWidget("worksheetCompletion")).addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                // browser.addScreen(new )
            }
        });
        ((MenuItem)def.getWidget("addOrCancel")).addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                // browser.addScreen(new )
            }
        });
        ((MenuItem)def.getWidget("reviewAndRelease")).addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                // browser.addScreen(new )
            }
        });
        ((MenuItem)def.getWidget("toDo")).addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                // browser.addScreen(new )
            }
        });
        ((MenuItem)def.getWidget("labelFor")).addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                // browser.addScreen(new )
            }
        });
        ((MenuItem)def.getWidget("storage")).addClickHandler(new ClickHandler() {
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
        ((MenuItem)def.getWidget("QC")).addClickHandler(new ClickHandler() {
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
        ((MenuItem)def.getWidget("internalOrder")).addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            browser.addScreen(new OrderScreen(new String[] {"internal"}));
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
        ((MenuItem)def.getWidget("vendorOrder")).addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            browser.addScreen(new OrderScreen(new String[] {"external"}));
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
        ((MenuItem)def.getWidget("kitOrder")).addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            browser.addScreen(new OrderScreen(new String[] {"kits"}));
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
        ((MenuItem)def.getWidget("fillOrder")).addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            browser.addScreen(new FillOrderScreen());
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
        ((MenuItem)def.getWidget("shipping")).addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            browser.addScreen(new ShippingScreen());
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
        ((MenuItem)def.getWidget("buildKits")).addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            browser.addScreen(new BuildKitsScreen());
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
        ((MenuItem)def.getWidget("inventoryTransfer")).addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            browser.addScreen(new InventoryReceiptScreen());
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
        ((MenuItem)def.getWidget("inventoryAdjustment")).addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            browser.addScreen(new InventoryAdjustmentScreen());
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
        ((MenuItem)def.getWidget("inventoryItem")).addClickHandler(new ClickHandler() {
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
        ((MenuItem)def.getWidget("instrument")).addClickHandler(new ClickHandler() {
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
        ((MenuItem)def.getWidget("test")).addClickHandler(new ClickHandler() {
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
        ((MenuItem)def.getWidget("method")).addClickHandler(new ClickHandler() {
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
        ((MenuItem)def.getWidget("panel")).addClickHandler(new ClickHandler() {
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
        ((MenuItem)def.getWidget("QAEvent")).addClickHandler(new ClickHandler() {
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
        ((MenuItem)def.getWidget("labSection")).addClickHandler(new ClickHandler() {
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
        ((MenuItem)def.getWidget("analyte")).addClickHandler(new ClickHandler() {
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
        ((MenuItem)def.getWidget("dictionary")).addClickHandler(new ClickHandler() {
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
        ((MenuItem)def.getWidget("auxiliaryPrompt")).addClickHandler(new ClickHandler() {
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
        ((MenuItem)def.getWidget("label")).addClickHandler(new ClickHandler() {
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
        ((MenuItem)def.getWidget("standardNote")).addClickHandler(new ClickHandler() {
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
        ((MenuItem)def.getWidget("trailerForTest")).addClickHandler(new ClickHandler() {
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
        ((MenuItem)def.getWidget("storageUnit")).addClickHandler(new ClickHandler() {
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
        ((MenuItem)def.getWidget("storageLocation")).addClickHandler(new ClickHandler() {
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
        ((MenuItem)def.getWidget("scriptlet")).addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                // browser.addScreen(new )
            }
        });
        ((MenuItem)def.getWidget("systemVariable")).addClickHandler(new ClickHandler() {
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
        ((MenuItem)def.getWidget("finalReport")).addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                // browser.addScreen(new )
            }
        });
        ((MenuItem)def.getWidget("sampleDataExport")).addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                // browser.addScreen(new )
            }
        });
        ((MenuItem)def.getWidget("loginLabel")).addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                // browser.addScreen(new )
            }
        });
    }

    public void onClick(ClickEvent item) {
        /*
         * if(item.getSource() == def.getWidget("EditFavorites")){
         * if(fv.editing) fv.saveFavorites(); else fv.getEditFavorites();
         * return; }
         */
    }

    public void setStyleToAllCellsInCol(FlexTable table, int col, String style) {
        for (int i = 0; i < table.getRowCount(); i++ ) {
            table.getCellFormatter().addStyleName(i, col, style);
        }
    }

    public void onDrop(Widget sender, Widget source) {
        VerticalPanel vp = (VerticalPanel) ((ScreenMenuPanel)sender).getWidget();
        for (int i = 0; i < vp.getWidgetCount(); i++ ) {
            if (vp.getWidget(i).getAbsoluteTop() > source.getAbsoluteTop()) {
                vp.insert((Widget) ((ScreenWidget)source).getUserObject(), i);
                break;
            }
        }
    }

    public static HashMap<String, HashMap> getCacheList() {
        if (cacheList == null)
            cacheList = new HashMap<String, HashMap>();
        return cacheList;
    }

    public static void setCacheList(HashMap<String, HashMap> hash) {
        cacheList = hash;
    }
}
