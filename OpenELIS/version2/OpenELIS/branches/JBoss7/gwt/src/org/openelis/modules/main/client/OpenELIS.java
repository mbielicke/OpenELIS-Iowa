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

import org.openelis.cache.UserCache;
import org.openelis.gwt.common.ModulePermission;
import org.openelis.gwt.common.RPC;
import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.screen.ScreenDefInt;
import org.openelis.gwt.screen.ScreenSessionTimer;
import org.openelis.gwt.services.ScreenService;
import org.openelis.gwt.widget.CollapsePanel;
import org.openelis.gwt.widget.Confirm;
import org.openelis.gwt.widget.MenuItem;
import org.openelis.gwt.widget.WindowBrowser;
import org.openelis.modules.SDWISSampleLogin.client.SDWISSampleLoginScreen;
import org.openelis.modules.analyte.client.AnalyteScreen;
import org.openelis.modules.analyteParameter.client.AnalyteParameterScreen;
import org.openelis.modules.auxiliary.client.AuxiliaryScreen;
import org.openelis.modules.buildKits.client.BuildKitsScreen;
import org.openelis.modules.completeRelease.client.CompleteReleaseScreen;
import org.openelis.modules.cron.client.CronScreen;
import org.openelis.modules.report.dataView.client.DataViewScreen;
import org.openelis.modules.dictionary.client.DictionaryScreen;
import org.openelis.modules.environmentalSampleLogin.client.EnvironmentalSampleLoginScreen;
import org.openelis.modules.newbornScreeningSampleLogin.client.NewbornScreeningSampleLoginScreen;
import org.openelis.modules.exchangeVocabularyMap.client.ExchangeVocabularyMapScreen;
import org.openelis.modules.exchangeDataSelection.client.ExchangeDataSelectionScreen;
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
import org.openelis.modules.preferences.client.PreferencesScreen;
import org.openelis.modules.privateWellWaterSampleLogin.client.PrivateWellWaterSampleLoginScreen;
import org.openelis.modules.project.client.ProjectScreen;
import org.openelis.modules.provider.client.ProviderScreen;
import org.openelis.modules.pws.client.PWSScreen;
import org.openelis.modules.qaevent.client.QaEventScreen;
import org.openelis.modules.qc.client.QcScreen;
import org.openelis.modules.report.qcChart.client.QcChartScreen;
import org.openelis.modules.quickEntry.client.QuickEntryScreen;
import org.openelis.modules.report.client.FinalReportBatchReprintScreen;
import org.openelis.modules.report.client.FinalReportBatchScreen;
import org.openelis.modules.report.finalReportSingleReprint.client.FinalReportSingleReprintScreen;
import org.openelis.modules.report.client.HoldRefuseOrganizationReportScreen;
import org.openelis.modules.report.client.OrderRecurrenceReportScreen;
import org.openelis.modules.report.client.QASummaryReportScreen;
import org.openelis.modules.report.client.RequestformReportScreen;
import org.openelis.modules.report.client.SDWISUnloadReportScreen;
import org.openelis.modules.report.client.SampleInhouseReportScreen;
import org.openelis.modules.report.client.SampleLoginLabelAdditionalReportScreen;
import org.openelis.modules.report.client.SampleLoginLabelReportScreen;
import org.openelis.modules.report.client.TestReportScreen;
import org.openelis.modules.report.client.ToDoAnalyteReportScreen;
import org.openelis.modules.report.client.TurnaroundReportScreen;
import org.openelis.modules.report.client.VerificationReportScreen;
import org.openelis.modules.report.client.VolumeReportScreen;
import org.openelis.modules.report.turnaroundStatistic.client.TurnaroundStatisticScreen;
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
import org.openelis.modules.worksheetCreation.client.WorksheetCreationScreen1;
import org.openelis.modules.sampleManagerTest.client.SampleManagerTestScreen;

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
    private Confirm                timeoutPopup;
    private static Timer           timeoutTimer, forceTimer;
    private static int             SESSION_TIMEOUT = 1000 * 60 * 30, FORCE_TIMEOUT = 1000 * 60;
    private HandlerRegistration    closeHandler;

    public OpenELIS() throws Exception {
        OpenELISRPC rpc;
        VerticalPanel vp;

        service = new ScreenService(
                                    "controller?service=org.openelis.modules.main.server.OpenELISScreenService");
        rpc = service.call("initialData");

        consts = rpc.appConstants;

        drawScreen((ScreenDefInt)GWT.create(OpenELISDef.class));

        // resize browser will move the collapse handle to the middle
        browser = (WindowBrowser)def.getWidget("browser");
        favoritesCollapse = (CollapsePanel)def.getWidget("favoritesCollapse");
        Window.addResizeHandler(new ResizeHandler() {
            public void onResize(ResizeEvent event) {
                favoritesCollapse.setHeight(Window.getClientHeight() + "px");
            }
        });
        // open/close favorites will adjust browser width
        favoritesCollapse.addResizeHandler(new ResizeHandler() {
            public void onResize(ResizeEvent event) {
                browser.resize();
            }
        });

        // load the google chart api
        VisualizationUtils.loadVisualizationApi(new Runnable() {
            public void run() {
            }
        }, PieChart.PACKAGE, PieChart.PACKAGE);

        DeferredCommand.addCommand(new Command() {
            public void execute() {
                favoritesCollapse.setHeight(Window.getClientHeight() + "px");
                browser.resize();
            }
        });

        initializeWindowClose();
        initializeTimeout();
        initialize();

        // load the favorite's panel
        vp = (VerticalPanel)def.getWidget("favoritesPanel");
        try {
            vp.add(new FavoritesScreen(def));
        } catch (Throwable t) {
            Window.alert("Can't initalize the favorite panel; " + t.getMessage());
        }
    }

    protected void initialize() {
        addClickHandler("preference", "openelis", new ClickHandler() {
            public void onClick(ClickEvent event) {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            browser.addScreen(new PreferencesScreen(), "preference");
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

        addClickHandler("logout", "openelis", new ClickHandler() {
            public void onClick(ClickEvent event) {
                try {
                    logout();
                } catch (Throwable e) {
                    e.printStackTrace();
                    Window.alert(e.getMessage());
                }
            }
        });

        addClickHandler("quickEntry", "quickentry", new ClickHandler() {
            public void onClick(ClickEvent event) {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            browser.addScreen(new QuickEntryScreen(), "quickEntry");
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

        addClickHandler("verification", "verification", new ClickHandler() {
            public void onClick(ClickEvent event) {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            browser.addScreen(new VerificationScreen(), "verification");
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

        addClickHandler("tracking", "sampletracking", new ClickHandler() {
            public void onClick(ClickEvent event) {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            browser.addScreen(new SampleTrackingScreen(), "tracking");
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

        addClickHandler("environmentalSampleLogin", "sampleenvironmental", new ClickHandler() {
            public void onClick(ClickEvent event) {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            browser.addScreen(new EnvironmentalSampleLoginScreen(),
                                              "environmentalSampleLogin");
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

        addClickHandler("clinicalSampleLogin", "sampleclinical", new ClickHandler() {
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
        
        addClickHandler("newbornScreeningSampleLogin", "sampletracking", new ClickHandler() {
            public void onClick(ClickEvent event) {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            browser.addScreen(new NewbornScreeningSampleLoginScreen(), "newbornScreeningSampleLogin");
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

        addClickHandler("animalSampleLogin", "sampleanimal", new ClickHandler() {
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

        addClickHandler("ptSampleLogin", "samplept", new ClickHandler() {
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

        addClickHandler("sdwisSampleLogin", "samplesdwis", new ClickHandler() {
            public void onClick(ClickEvent event) {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            browser.addScreen(new SDWISSampleLoginScreen(), "sdwisSampleLogin");
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

        addClickHandler("privateWellWaterSampleLogin", "sampleprivatewell", new ClickHandler() {
            public void onClick(ClickEvent event) {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            browser.addScreen(new PrivateWellWaterSampleLoginScreen(),
                                              "privateWellWaterSampleLogin");
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
        
        addClickHandler("testSampleManager", "sampleprivatewell", new ClickHandler() {
            public void onClick(ClickEvent event) {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            browser.addScreen(new SampleManagerTestScreen(),
                                              "Test Sample Manager");
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

        addClickHandler("project", "project", new ClickHandler() {
            public void onClick(ClickEvent event) {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            browser.addScreen(new ProjectScreen(), "project");
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

        addClickHandler("provider", "provider", new ClickHandler() {
            public void onClick(ClickEvent event) {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            browser.addScreen(new ProviderScreen(), "provider");
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

        addClickHandler("organization", "organization", new ClickHandler() {
            public void onClick(ClickEvent event) {

                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            browser.addScreen(new OrganizationScreen(), "organization");
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

        addClickHandler("worksheetCreation", "worksheet", new ClickHandler() {
            public void onClick(ClickEvent event) {

                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            browser.addScreen(new WorksheetCreationScreen(), "worksheetCreation");
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

        addClickHandler("worksheetCreation1", "worksheet", new ClickHandler() {
            public void onClick(ClickEvent event) {

                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            browser.addScreen(new WorksheetCreationScreen1(), "worksheetCreation1");
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

        addClickHandler("worksheetCompletion", "worksheet", new ClickHandler() {
            public void onClick(ClickEvent event) {

                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            browser.addScreen(new WorksheetCompletionScreen(),
                                              "worksheetCompletion");
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

        addClickHandler("addOrCancel", null, new ClickHandler() {
            public void onClick(ClickEvent event) {
                // browser.addScreen(new )
            }
        });

        addClickHandler("reviewAndRelease", "samplecompleterelease", new ClickHandler() {
            public void onClick(ClickEvent event) {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            browser.addScreen(new CompleteReleaseScreen(), "reviewAndRelease");
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

        addClickHandler("storage", "storage", new ClickHandler() {
            public void onClick(ClickEvent event) {

                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            browser.addScreen(new StorageScreen(), "storage");
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

        addClickHandler("toDo", "sampletracking", new ClickHandler() {
            public void onClick(ClickEvent event) {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            browser.addScreen(new ToDoScreen(), "toDo");
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

        addClickHandler("labelFor", null, new ClickHandler() {
            public void onClick(ClickEvent event) {
                // browser.addScreen(new )
            }
        });

        addClickHandler("storageLocation", "storagelocation", new ClickHandler() {
            public void onClick(ClickEvent event) {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            browser.addScreen(new StorageLocationScreen(), "storageLocation");
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

        addClickHandler("QC", "qc", new ClickHandler() {
            public void onClick(ClickEvent event) {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            browser.addScreen(new QcScreen(), "QC");
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

        addClickHandler("analyteParameter", "analyteparameter", new ClickHandler() {
            public void onClick(ClickEvent event) {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            browser.addScreen(new AnalyteParameterScreen(), "analyteParameter");
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

        addClickHandler("internalOrder", "internalorder", new ClickHandler() {
            public void onClick(ClickEvent event) {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            browser.addScreen(new InternalOrderScreen(), "internalOrder");
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

        addClickHandler("vendorOrder", "vendororder", new ClickHandler() {
            public void onClick(ClickEvent event) {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            browser.addScreen(new VendorOrderScreen(), "vendorOrder");
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

        addClickHandler("sendoutOrder", "sendoutorder", new ClickHandler() {
            public void onClick(ClickEvent event) {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            browser.addScreen(new SendoutOrderScreen(), "sendoutOrder");
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

        addClickHandler("fillOrder", "fillorder", new ClickHandler() {
            public void onClick(ClickEvent event) {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            browser.addScreen(new OrderFillScreen(), "fillOrder");
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

        addClickHandler("shipping", "shipping", new ClickHandler() {
            public void onClick(ClickEvent event) {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            browser.addScreen(new ShippingScreen(), "shipping");
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

        addClickHandler("buildKits", "buildkits", new ClickHandler() {
            public void onClick(ClickEvent event) {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            browser.addScreen(new BuildKitsScreen(), "buildKits");
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

        addClickHandler("inventoryReceipt", "inventoryreceipt", new ClickHandler() {
            public void onClick(ClickEvent event) {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            browser.addScreen(new InventoryReceiptScreen(), "inventoryReceipt");
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

        addClickHandler("inventoryTransfer", "inventorytransfer", new ClickHandler() {
            public void onClick(ClickEvent event) {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            browser.addScreen(new InventoryTransferScreen(), "inventoryTransfer");
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

        addClickHandler("inventoryAdjustment", "inventoryadjustment", new ClickHandler() {
            public void onClick(ClickEvent event) {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            browser.addScreen(new InventoryAdjustmentScreen(),
                                              "inventoryAdjustment");
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

        addClickHandler("inventoryItem", "inventoryitem", new ClickHandler() {
            public void onClick(ClickEvent event) {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            browser.addScreen(new InventoryItemScreen(), "inventoryItem");
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

        addClickHandler("instrument", "instrument", new ClickHandler() {
            public void onClick(ClickEvent event) {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            browser.addScreen(new InstrumentScreen(), "instrument");
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

        addClickHandler("test", "test", new ClickHandler() {
            public void onClick(ClickEvent event) {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            browser.addScreen(new TestScreen(), "test");
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

        addClickHandler("method", "method", new ClickHandler() {
            public void onClick(ClickEvent event) {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            browser.addScreen(new MethodScreen(), "method");
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

        addClickHandler("panel", "panel", new ClickHandler() {
            public void onClick(ClickEvent event) {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            browser.addScreen(new PanelScreen(), "panel");
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

        addClickHandler("QAEvent", "qaevent", new ClickHandler() {
            public void onClick(ClickEvent event) {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            browser.addScreen(new QaEventScreen(), "QAEvent");
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

        addClickHandler("labSection", "section", new ClickHandler() {
            public void onClick(ClickEvent event) {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            browser.addScreen(new SectionScreen(), "labSection");
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

        addClickHandler("analyte", "analyte", new ClickHandler() {
            public void onClick(ClickEvent event) {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            browser.addScreen(new AnalyteScreen(), "analyte");
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

        addClickHandler("dictionary", "dictionary", new ClickHandler() {
            public void onClick(ClickEvent event) {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            browser.addScreen(new DictionaryScreen(), "dictionary");
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
        
        addClickHandler("exchangeVocabularyMap", "exchangevocabularymap", new ClickHandler() {
            public void onClick(ClickEvent event) {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            browser.addScreen(new ExchangeVocabularyMapScreen(), "exchangeVocabularyMap");
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
        
        addClickHandler("exchangeDataSelection", "exchangedataselection", new ClickHandler() {
            public void onClick(ClickEvent event) {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            browser.addScreen(new ExchangeDataSelectionScreen(), "exchangeDataSelection");
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

        addClickHandler("auxiliaryPrompt", "auxiliary", new ClickHandler() {
            public void onClick(ClickEvent event) {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            browser.addScreen(new AuxiliaryScreen(), "auxiliaryPrompt");
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

        addClickHandler("label", "label", new ClickHandler() {
            public void onClick(ClickEvent event) {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            browser.addScreen(new LabelScreen(), "label");
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

        addClickHandler("standardNote", "standardnote", new ClickHandler() {
            public void onClick(ClickEvent event) {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            browser.addScreen(new StandardNoteScreen(), "standardNote");
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

        addClickHandler("trailerForTest", "testtrailer", new ClickHandler() {
            public void onClick(ClickEvent event) {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            browser.addScreen(new TestTrailerScreen(), "trailerForTest");
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

        addClickHandler("storageUnit", "storageunit", new ClickHandler() {
            public void onClick(ClickEvent event) {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            browser.addScreen(new StorageUnitScreen(), "storageUnit");
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

        addClickHandler("scriptlet", "scriptlet", new ClickHandler() {
            public void onClick(ClickEvent event) {
                // browser.addScreen(new )
            }
        });

        addClickHandler("systemVariable", "systemvariable", new ClickHandler() {
            public void onClick(ClickEvent event) {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            browser.addScreen(new SystemVariableScreen(), "systemVariable");
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

        addClickHandler("pws", "pws", new ClickHandler() {
            public void onClick(ClickEvent event) {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            browser.addScreen(new PWSScreen(), "pws");
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

        addClickHandler("cron", "cron", new ClickHandler() {
            public void onClick(ClickEvent event) {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            browser.addScreen(new CronScreen(), "cron");
                        } catch (Exception e) {
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

        addClickHandler("testReport", "test", new ClickHandler() {
            public void onClick(ClickEvent event) {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            browser.addScreen(new TestReportScreen(), "testReport");
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

        addClickHandler("sampleLoginLabelReport", "r_loginlabel", new ClickHandler() {
            public void onClick(ClickEvent event) {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            browser.addScreen(new SampleLoginLabelReportScreen(),
                                              "sampleLoginLabelReport");
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

        addClickHandler("sampleLoginLabelAdditionalReport", "r_loginlabel", new ClickHandler() {
            public void onClick(ClickEvent event) {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            browser.addScreen(new SampleLoginLabelAdditionalReportScreen(),
                                              "sampleLoginLabelAdditionalReport");
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

        addClickHandler("dataView", "sampletracking", new ClickHandler() {
            public void onClick(ClickEvent event) {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            browser.addScreen(new DataViewScreen(), "dataView");
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

        addClickHandler("finalReport", "r_final", new ClickHandler() {
            public void onClick(ClickEvent event) {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            browser.addScreen(new FinalReportSingleReprintScreen(), "finalReport");
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

        addClickHandler("finalReportBatch", "r_finalbatch", new ClickHandler() {
            public void onClick(ClickEvent event) {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            browser.addScreen(new FinalReportBatchScreen(), "finalReportBatch");
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

        addClickHandler("finalReportBatchReprint", "r_finalbatch", new ClickHandler() {
            public void onClick(ClickEvent event) {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            browser.addScreen(new FinalReportBatchReprintScreen(),
                                              "finalReportBatchReprint");
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

        addClickHandler("verificationReport", "verification", new ClickHandler() {
            public void onClick(ClickEvent event) {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            browser.addScreen(new VerificationReportScreen(), "verificationReport");
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

        addClickHandler("orderRequestForm", "sendoutorder", new ClickHandler() {
            public void onClick(ClickEvent event) {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            browser.addScreen(new RequestformReportScreen(), "orderRequestForm");
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

        addClickHandler("sampleInhouseReport", "sampletracking", new ClickHandler() {
            public void onClick(ClickEvent event) {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            browser.addScreen(new SampleInhouseReportScreen(),
                                              "sampleInhouseReport");
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

        addClickHandler("volumeReport", "sampletracking", new ClickHandler() {
            public void onClick(ClickEvent event) {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            browser.addScreen(new VolumeReportScreen(), "volumeReport");
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

        addClickHandler("turnaround", "sampletracking", new ClickHandler() {
            public void onClick(ClickEvent event) {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            browser.addScreen(new TurnaroundReportScreen(), "turnaround");
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

        addClickHandler("QAByOrganization", "sampletracking", new ClickHandler() {
            public void onClick(ClickEvent event) {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            browser.addScreen(new QASummaryReportScreen(), "QAByOrganization");
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

        addClickHandler("sdwisUnloadReport", "samplesdwis", new ClickHandler() {
            public void onClick(ClickEvent event) {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            browser.addScreen(new SDWISUnloadReportScreen(), "sdwisUnloadReport");
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

        addClickHandler("orderRecurrence", "system", new ClickHandler() {
            public void onClick(ClickEvent event) {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            browser.addScreen(new OrderRecurrenceReportScreen(), "orderRecurrence");
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
        
        addClickHandler("qcChart", "sampletracking", new ClickHandler() {
            public void onClick(ClickEvent event) {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            browser.addScreen(new QcChartScreen(), "qcChart");
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
        
        addClickHandler("toDoAnalyteReport", "sampletracking", new ClickHandler() {
            public void onClick(ClickEvent event) {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            browser.addScreen(new ToDoAnalyteReportScreen(), "toDoAnalyteReport");
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
        
        addClickHandler("turnAroundStatisticReport", "sampletracking", new ClickHandler() {
            public void onClick(ClickEvent event) {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            browser.addScreen(new TurnaroundStatisticScreen(), "turnAroundStatisticReport");
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
        
        addClickHandler("holdRefuseOrganization", "sampletracking", new ClickHandler() {
            public void onClick(ClickEvent event) {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            browser.addScreen(new HoldRefuseOrganizationReportScreen(), "holdRefuseOrganization");
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
        timeoutPopup = new Confirm(Confirm.Type.WARN, consts.get("timeoutHeader"),
                                   consts.get("timeoutWarning"), consts.get("timeoutExtendTime"),
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
    private void addClickHandler(String screenName, String modulePermission, ClickHandler handler) {
        MenuItem item;
        ModulePermission perm;

        item = ((MenuItem)def.getWidget(screenName));
        if (item != null && modulePermission != null) {
            perm = UserCache.getPermission().getModule(modulePermission);
            if (perm != null && perm.hasSelectPermission()) {
                item.enable(true);
                item.addClickHandler(handler);
            }
        }
    }
}
