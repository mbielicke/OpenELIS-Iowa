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

import java.util.HashMap;

import org.openelis.gwt.common.SystemUserPermission;
import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.screen.ScreenDefInt;
import org.openelis.gwt.services.ScreenService;
import org.openelis.gwt.widget.Browser;
import org.openelis.gwt.widget.Button;
import org.openelis.gwt.widget.MenuItem;
import org.openelis.modules.dictionary.client.DictionaryScreen;
import org.openelis.modules.favorites.client.FavoritesScreen;
import org.openelis.modules.organization.client.OrganizationScreen;
import org.openelis.modules.provider.client.ProviderScreen;
import org.openelis.modules.project.client.ProjectScreen;
import org.openelis.modules.pws.client.PwsScreen;
import org.openelis.modules.method.client.MethodScreen;
import org.openelis.modules.panel.client.PanelScreen;
import org.openelis.modules.qaevent.client.QaEventScreen;
import org.openelis.modules.section.client.SectionScreen;
import org.openelis.modules.analyte.client.AnalyteScreen;
import org.openelis.modules.auxiliary.client.AuxiliaryScreen;
import org.openelis.modules.label.client.LabelScreen;
import org.openelis.modules.standardnote.client.StandardNoteScreen;
import org.openelis.modules.testTrailer.client.TestTrailerScreen;
import org.openelis.modules.systemvariable.client.SystemVariableScreen;
import org.openelis.modules.instrument.client.InstrumentScreen;
import org.openelis.modules.storageLocation.client.StorageLocationScreen;
import org.openelis.modules.storageunit.client.StorageUnitScreen;
import org.openelis.modules.test.client.TestScreen;
import org.openelis.modules.order.client.InternalOrderScreen;
import org.openelis.modules.qc.client.QcScreen;
import org.openelis.modules.storage.client.StorageScreen;
import org.openelis.modules.inventoryReceipt.client.InventoryReceiptScreen;
import org.openelis.modules.inventoryAdjustment.client.InventoryAdjustmentScreen;
import org.openelis.modules.inventoryItem.client.InventoryItemScreen;
import org.openelis.modules.inventoryTransfer.client.InventoryTransferScreen;
import org.openelis.modules.buildKits.client.BuildKitsScreen;
import org.openelis.modules.shipping.client.ShippingScreen;
import org.openelis.modules.order.client.VendorOrderScreen;
import org.openelis.modules.order.client.SendoutOrderScreen;
import org.openelis.modules.orderFill.client.OrderFillScreen;
import org.openelis.modules.environmentalSampleLogin.client.EnvironmentalSampleLoginScreen;
import org.openelis.modules.privateWellWaterSampleLogin.client.PrivateWellWaterSampleLoginScreen;
import org.openelis.modules.SDWISSampleLogin.client.SDWISSampleLoginScreen;
import org.openelis.modules.quickEntry.client.QuickEntryScreen;
import org.openelis.modules.sampleTracking.client.SampleTrackingScreen;
import org.openelis.modules.completeRelease.client.CompleteReleaseScreen;
import org.openelis.modules.worksheetCreation.client.WorksheetCreationScreen;
import org.openelis.modules.worksheetCompletion.client.WorksheetCompletionScreen;
import org.openelis.modules.report.client.TestReportScreen;
import org.openelis.modules.report.client.FinalReportScreen;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.VerticalPanel;

public class OpenELIS extends Screen {

    protected static Browser                          browser;
    protected static SystemUserPermission             systemUserPermission;

    private FavoritesScreen                           fv;
    private static HashMap<String, HashMap>           cacheList;

    public OpenELIS() throws Exception {
        OpenELISRPC rpc;

        service = new ScreenService("controller?service=org.openelis.modules.main.server.OpenELISScreenService");
        rpc = service.call("initialData");
        consts = rpc.appConstants;
        systemUserPermission = rpc.systemUserPermission;

        drawScreen((ScreenDefInt)GWT.create(OpenELISDef.class));
        browser = (Browser)def.getWidget("browser");
        //browser.setBrowserHeight();
        initialize();
    }

    protected void initialize() {
    	
        addCommand("preference", new Command() {
            public void execute() {
                // browser.addScreen(new )
            }
        });

        addCommand("FavoritesMenu", new Command() {
            public void execute() {
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
                //browser.setBrowserHeight();
            }
        });
        ((Button)def.getWidget("EditFavorites")).addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                if (fv.editing)
                    fv.stopEditing();
                else
                    fv.edit();
            }
        });
        addCommand("Logout", new Command() {
            public void execute() {
                try {
                    service.call("logout");
                    Window.open("OpenELIS.html", "_self", null);
                } catch (Throwable e) {
                    e.printStackTrace();
                    Window.alert(e.getMessage());
                }
            }
        });
	    
        addCommand("quickEntry", new Command() {
            public void execute() {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            browser.addScreen(new QuickEntryScreen());
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
        addCommand("tracking", new Command() {
            public void execute() {
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
        
        addCommand("environmentalSampleLogin", new Command() {
            public void execute() {
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
        
        addCommand("clinicalSampleLogin", new Command() {
            public void execute() {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            // browser.addScreen(new
                            // ClinicalSampleLoginScreen());
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

        addCommand("animalSampleLogin", new Command() {
            public void execute() {
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

        addCommand("newbornScreeningSampleLogin", new Command() {
            public void execute() {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            // browser.addScreen(new
                            // NewbornScreeningSampleLoginScreen());
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

        addCommand("ptSampleLogin", new Command() {
            public void execute() {
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
        
        addCommand("sdwisSampleLogin", new Command() {
            public void execute() {
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
        addCommand("privateWellWaterSampleLogin", new Command() {
            public void execute() {
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
		
        addCommand("project", new Command() {
            public void execute() {
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
        
        addCommand("provider", new Command() {
            public void execute() {
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
        
        addCommand("organization", new Command() {
            public void execute() {

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
        
        addCommand("worksheetCreation", new Command() {
            public void execute() {

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
        
        addCommand("worksheetCompletion", new Command() {
            public void execute() {

                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            browser.addScreen(new WorksheetCompletionScreen());
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
        
        addCommand("addOrCancel", new Command() {
            public void execute() {
                // browser.addScreen(new )
            }
        });
        
        addCommand("reviewAndRelease", new Command() {
            public void execute() {

                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            browser.addScreen(new CompleteReleaseScreen());
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
		
        addCommand("storage", new Command() {
            public void execute() {

                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            browser.addScreen(new StorageScreen());
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
        
        addCommand("toDo", new Command() {
            public void execute() {
                // browser.addScreen(new )
            }
        });
        addCommand("labelFor", new Command() {
            public void execute() {
                // browser.addScreen(new )
            }
        });
		
        addCommand("storageLocation", new Command() {
            public void execute() {
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
        
        addCommand("QC", new Command() {
            public void execute() {
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
		
        addCommand("internalOrder", new Command() {
            public void execute() {
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
        
        addCommand("vendorOrder", new Command() {
            public void execute() {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            browser.addScreen(new VendorOrderScreen());
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
        
        addCommand("kitOrder", new Command() {
            public void execute() {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            browser.addScreen(new SendoutOrderScreen());
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
        
        addCommand("fillOrder", new Command() {
            public void execute() {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            browser.addScreen(new OrderFillScreen());
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
        
        addCommand("shipping", new Command() {
            public void execute() {
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
		
        addCommand("buildKits", new Command() {
            public void execute() {
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
        
        addCommand("inventoryReceipt", new Command() {
            public void execute() {
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
        
        addCommand("inventoryTransfer", new Command() {
            public void execute() {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            browser.addScreen(new InventoryTransferScreen());
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
        
        addCommand("inventoryAdjustment", new Command() {
            public void execute() {
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
        
        addCommand("inventoryItem", new Command() {
            public void execute() {
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
		
        addCommand("instrument", new Command() {
            public void execute() {
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
        
        addCommand("test", new Command() {
            public void execute() {
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
        
        addCommand("method", new Command() {
            public void execute() {
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
        
        addCommand("panel", new Command() {
            public void execute() {
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
        
        addCommand("QAEvent", new Command() {
            public void execute() {
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
        
        addCommand("labSection", new Command() {
            public void execute() {
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
        
        addCommand("analyte", new Command() {
            public void execute() {
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
        
        addCommand("dictionary", new Command() {
            public void execute() {
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
        
        addCommand("auxiliaryPrompt", new Command() {
            public void execute() {
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
        
        addCommand("label", new Command() {
            public void execute() {
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
        
        addCommand("standardNote", new Command() {
            public void execute() {
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
        
        addCommand("trailerForTest", new Command() {
            public void execute() {
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
        
        addCommand("storageUnit", new Command() {
            public void execute() {
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
        
        addCommand("storageLocation", new Command() {
            public void execute() {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            // browser.addScreen(new StorageLocationScreen());
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

        addCommand("scriptlet", new Command() {
            public void execute() {
                // browser.addScreen(new )
            }
        });
        
        addCommand("systemVariable", new Command() {
            public void execute() {
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
        
        addCommand("pws", new Command() {
            public void execute() {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            browser.addScreen(new PwsScreen());
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

        addCommand("testReport", new Command() {
            public void execute() {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            browser.addScreen(new TestReportScreen());
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
        
        addCommand("finalReport", new Command() {
            public void execute() {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            browser.addScreen(new FinalReportScreen());
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
        addCommand("sampleDataExport", new Command() {
            public void execute() {
                // browser.addScreen(new )
            }
        });
        addCommand("loginLabel", new Command() {
            public void execute() {
                // browser.addScreen(new )
            }
        });
        
    }

    /**
     * Global in browser cache list. Used for dictionary and other objects that
     * are cached in the browser space.
     */
    public static HashMap<String, HashMap> getCacheList() {
        if (cacheList == null)
            cacheList = new HashMap<String, HashMap>();
        return cacheList;
    }

    /**
     * Returns the current user's basic information including module and
     * section permission.
     */
    public static SystemUserPermission getSystemUserPermission() {
        return systemUserPermission;
    }

    /**
     * Returns the browser associated with this application. 
     */
    public static Browser getBrowser() {
        return browser;
    }
    
    private void addCommand(String screenName, Command handler) {
        MenuItem item;

        item = ((MenuItem)def.getWidget(screenName));
        if (item != null)
            item.addCommand(handler);
    }
}
