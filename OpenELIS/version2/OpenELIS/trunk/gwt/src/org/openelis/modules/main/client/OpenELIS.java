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
package org.openelis.modules.main.client;

import org.openelis.gwt.common.RPC;
import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.screen.ScreenDefInt;
import org.openelis.gwt.screen.ScreenSessionTimer;
import org.openelis.gwt.services.ScreenService;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.CollapsePanel;
import org.openelis.gwt.widget.Confirm;
import org.openelis.gwt.widget.MenuItem;
import org.openelis.gwt.widget.WindowBrowser;
import org.openelis.manager.Application;
import org.openelis.modules.SDWISSampleLogin.client.SDWISSampleLoginScreen;
import org.openelis.modules.analyte.client.AnalyteScreen;
import org.openelis.modules.analyteParameter.client.AnalyteParameterScreen;
import org.openelis.modules.auxiliary.client.AuxiliaryScreen;
import org.openelis.modules.buildKits.client.BuildKitsScreen;
import org.openelis.modules.completeRelease.client.CompleteReleaseScreen;
import org.openelis.modules.dictionary.client.DictionaryScreen;
import org.openelis.modules.environmentalSampleLogin.client.EnvironmentalSampleLoginScreen;
import org.openelis.modules.favorites.client.FavoritesScreen;
import org.openelis.modules.instrument.client.InstrumentScreen;
import org.openelis.modules.inventoryAdjustment.client.InventoryAdjustmentScreen;
import org.openelis.modules.inventoryItem.client.InventoryItemScreen;
import org.openelis.modules.inventoryReceipt.client.InventoryReceiptScreen;
import org.openelis.modules.inventoryTransfer.client.InventoryTransferScreen;
import org.openelis.modules.label.client.LabelScreen;
import org.openelis.modules.method.client.MethodScreen;
import org.openelis.modules.order.client.InternalOrderScreen;
import org.openelis.modules.order.client.SendoutOrderScreen;
import org.openelis.modules.order.client.VendorOrderScreen;
import org.openelis.modules.orderFill.client.OrderFillScreen;
import org.openelis.modules.organization.client.OrganizationScreen;
import org.openelis.modules.panel.client.PanelScreen;
import org.openelis.modules.privateWellWaterSampleLogin.client.PrivateWellWaterSampleLoginScreen;
import org.openelis.modules.project.client.ProjectScreen;
import org.openelis.modules.provider.client.ProviderScreen;
import org.openelis.modules.pws.client.PWSScreen;
import org.openelis.modules.qaevent.client.QaEventScreen;
import org.openelis.modules.qc.client.QcScreen;
import org.openelis.modules.quickEntry.client.QuickEntryScreen;
import org.openelis.modules.report.client.ClientNotificationReleasedReportScreen;
import org.openelis.modules.report.client.ClientNotificationReportScreen;
import org.openelis.modules.report.client.OrderRecurrenceReportScreen;
import org.openelis.modules.report.client.FinalReportScreen;
import org.openelis.modules.report.client.QASummaryReportScreen;
import org.openelis.modules.report.client.RequestformReportScreen;
import org.openelis.modules.report.client.SDWISUnloadReportScreen;
import org.openelis.modules.report.client.SampleInhouseReportScreen;
import org.openelis.modules.report.client.SampleLoginLabelAdditionalReportScreen;
import org.openelis.modules.report.client.SampleLoginLabelReportScreen;
import org.openelis.modules.report.client.TestReportScreen;
import org.openelis.modules.report.client.TurnaroundReportScreen;
import org.openelis.modules.report.client.VerificationReportScreen;
import org.openelis.modules.report.client.VolumeReportScreen;
import org.openelis.modules.sampleTracking.client.SampleTrackingScreen;
import org.openelis.modules.section.client.SectionScreen;
import org.openelis.modules.shipping.client.ShippingScreen;
import org.openelis.modules.standardnote.client.StandardNoteScreen;
import org.openelis.modules.storage.client.StorageScreen;
import org.openelis.modules.storageLocation.client.StorageLocationScreen;
import org.openelis.modules.storageunit.client.StorageUnitScreen;
import org.openelis.modules.systemvariable.client.SystemVariableScreen;
import org.openelis.modules.test.client.TestScreen;
import org.openelis.modules.testTrailer.client.TestTrailerScreen;
import org.openelis.modules.todo.client.ToDoScreen;
import org.openelis.modules.verification.client.VerificationScreen;
import org.openelis.modules.worksheetCompletion.client.WorksheetCompletionScreen;
import org.openelis.modules.worksheetCreation.client.WorksheetCreationScreen;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.ClosingEvent;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.SyncCallback;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.visualization.client.VisualizationUtils;
import com.google.gwt.visualization.client.visualizations.corechart.PieChart;

public class OpenELIS extends Screen implements ScreenSessionTimer {

    protected static WindowBrowser browser;
    protected CollapsePanel        favoritesCollapse;
    private FavoritesScreen        fv;
    private Confirm                timeoutPopup;
    private static Timer           timeoutTimer, forceTimer;
    private static int             SESSION_TIMEOUT = 1000 * 60 * 30, FORCE_TIMEOUT = 1000 * 60;
    private HandlerRegistration    closeHandler;

    public OpenELIS() throws Exception {
        OpenELISRPC rpc;
        VerticalPanel vp;

        service = new ScreenService("controller?service=org.openelis.modules.main.server.OpenELISScreenService");
        rpc = service.call("initialData");
       
        consts = rpc.appConstants;

        drawScreen((ScreenDefInt)GWT.create(OpenELISDef.class));
        browser = (WindowBrowser)def.getWidget("browser");
        favoritesCollapse = (CollapsePanel)def.getWidget("favoritesCollapse");
        
        
        favoritesCollapse.addResizeHandler(new ResizeHandler() {
        	public void onResize(ResizeEvent event) {
        		browser.setBrowserHeight();
			}
		});
        
        DeferredCommand.addCommand(new Command() {
			public void execute() {
				 favoritesCollapse.setHeight(Window.getClientHeight()+"px");	
				 browser.setBrowserHeight();
			}
		});
        	        
        Window.addResizeHandler(new ResizeHandler() {
			
			@Override
			public void onResize(ResizeEvent event) {
				favoritesCollapse.setHeight(Window.getClientHeight()+"px");
				
			}
		});
        

       
        // load the favorite's panel
        vp = (VerticalPanel)def.getWidget("favoritesPanel");
        try {
            vp.add(new FavoritesScreen(def));
        } catch (Throwable t) {
            Window.alert("Can't initalize the favorite panel; "+t.getMessage());
        }
        
        // load the google chart api
        VisualizationUtils.loadVisualizationApi(new Runnable() {
            public void run() {
            }
        }, PieChart.PACKAGE, PieChart.PACKAGE);
       
        browser.setBrowserHeight();        
        initializeWindowClose();
        initializeTimeout();
        initialize();
    }

    protected void initialize() {
        addClickHandler("preference", new ClickHandler() {
            public void onClick(ClickEvent event) {
                // browser.addScreen(new )
            }
        });

        addClickHandler("FavoritesMenu", new ClickHandler() {
            public void onClick(ClickEvent event) {
                CollapsePanel cp = (CollapsePanel)def.getWidget("favoritesCollapse");
                if (cp.isOpen)
                    cp.close();
                else
                    cp.open();
            }
        });
        addClickHandler("Logout", new ClickHandler() {
            public void onClick(ClickEvent event) {
                try {
                    logout();
                } catch (Throwable e) {
                    e.printStackTrace();
                    Window.alert(e.getMessage());
                }
            }
        });

        addClickHandler("quickEntry", new ClickHandler() {
            public void onClick(ClickEvent event) {
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
        addClickHandler("verification", new ClickHandler() {
            public void onClick(ClickEvent event) {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            browser.addScreen(new VerificationScreen());
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
                        } catch (Throwable caught) {
                            caught.printStackTrace();
                            Window.alert(caught.getMessage());
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

        addClickHandler("storage", new ClickHandler() {
            public void onClick(ClickEvent event) {

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

        addClickHandler("toDo", new ClickHandler() {
            public void onClick(ClickEvent event) {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            browser.addScreen(new ToDoScreen());
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

        addClickHandler("analyteParameter", new ClickHandler() {
            public void onClick(ClickEvent event) {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            browser.addScreen(new AnalyteParameterScreen());
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

        addClickHandler("kitOrder", new ClickHandler() {
            public void onClick(ClickEvent event) {
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

        addClickHandler("fillOrder", new ClickHandler() {
            public void onClick(ClickEvent event) {
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
        addClickHandler("shipping", new ClickHandler() {
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

        addClickHandler("buildKits", new ClickHandler() {
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
        addClickHandler("inventoryReceipt", new ClickHandler() {
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
        addClickHandler("inventoryTransfer", new ClickHandler() {
            public void onClick(ClickEvent event) {
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
        addClickHandler("inventoryAdjustment", new ClickHandler() {
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

        addClickHandler("pws", new ClickHandler() {
            public void onClick(ClickEvent event) {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            browser.addScreen(new PWSScreen());
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

        addClickHandler("testReport", new ClickHandler() {
            public void onClick(ClickEvent event) {
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

        addClickHandler("sampleLoginLabelReport", new ClickHandler() {
            public void onClick(ClickEvent event) {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            browser.addScreen(new SampleLoginLabelReportScreen());
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

        addClickHandler("sampleLoginLabelAdditionalReport", new ClickHandler() {
            public void onClick(ClickEvent event) {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            browser.addScreen(new SampleLoginLabelAdditionalReportScreen());
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

        addClickHandler("verificationReport", new ClickHandler() {
            public void onClick(ClickEvent event) {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            browser.addScreen(new VerificationReportScreen());
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

        addClickHandler("orderRequestForm", new ClickHandler() {
            public void onClick(ClickEvent event) {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            browser.addScreen(new RequestformReportScreen());
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

        addClickHandler("sampleInhouseReport", new ClickHandler() {
            public void onClick(ClickEvent event) {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            browser.addScreen(new SampleInhouseReportScreen());
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

        addClickHandler("volumeReport", new ClickHandler() {
            public void onClick(ClickEvent event) {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            browser.addScreen(new VolumeReportScreen());
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

        addClickHandler("turnaround", new ClickHandler() {
            public void onClick(ClickEvent event) {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            browser.addScreen(new TurnaroundReportScreen());
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

        addClickHandler("QAByOrganization", new ClickHandler() {
            public void onClick(ClickEvent event) {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            browser.addScreen(new QASummaryReportScreen());
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

        addClickHandler("sdwisUnloadReport", new ClickHandler() {
            public void onClick(ClickEvent event) {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            browser.addScreen(new SDWISUnloadReportScreen());
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

        addClickHandler("organizationRef", new ClickHandler() {
            public void onClick(ClickEvent event) {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            browser.addScreen(new ClientNotificationReportScreen());
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

        addClickHandler("organizationRelRef", new ClickHandler() {
            public void onClick(ClickEvent event) {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            browser.addScreen(new ClientNotificationReleasedReportScreen());
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
        
        addClickHandler("orderRecurrence", new ClickHandler() {
            public void onClick(ClickEvent event) {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            browser.addScreen(new OrderRecurrenceReportScreen());
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
    }

    /**
     * Returns the browser associated with this application.
     */
    public static WindowBrowser getBrowser() {
        return browser;
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
     * register a click handler
     */
    private void addClickHandler(String screenName, ClickHandler handler) {
        MenuItem item;

        item = ((MenuItem)def.getWidget(screenName));
        if (item != null)
            item.addClickHandler(handler);
    }
}
