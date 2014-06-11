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

import static org.openelis.modules.main.client.Logger.*;

import java.util.logging.Level;

import org.openelis.cache.UserCache;
import org.openelis.constants.OpenELISConstants;
import org.openelis.domain.Constants;
import org.openelis.modules.SDWISSampleLogin.client.SDWISSampleLoginScreen;
import org.openelis.modules.analyte.client.AnalyteScreen;
import org.openelis.modules.analyteParameter.client.AnalyteParameterScreen;
import org.openelis.modules.auxiliary.client.AuxiliaryScreen;
import org.openelis.modules.buildKits.client.BuildKitsScreen;
import org.openelis.modules.clinicalSampleLogin1.client.ClinicalSampleLoginScreenUI;
import org.openelis.modules.completeRelease1.client.CompleteReleaseScreenUI;
import org.openelis.modules.cron.client.CronScreen;
import org.openelis.modules.dictionary.client.DictionaryScreen;
import org.openelis.modules.environmentalSampleLogin.client.EnvironmentalSampleLoginScreen;
import org.openelis.modules.exchangeDataSelection.client.ExchangeDataSelectionScreen;
import org.openelis.modules.exchangeVocabularyMap.client.ExchangeVocabularyMapScreen;
import org.openelis.modules.instrument.client.InstrumentScreen;
import org.openelis.modules.inventoryAdjustment.client.InventoryAdjustmentScreen;
import org.openelis.modules.inventoryItem.client.InventoryItemScreen;
import org.openelis.modules.inventoryReceipt.client.InventoryReceiptScreen;
import org.openelis.modules.inventoryTransfer.client.InventoryTransferScreen;
import org.openelis.modules.label.client.LabelScreen;
import org.openelis.modules.logging.client.LoggingScreen;
import org.openelis.modules.method.client.MethodScreenUI;
import org.openelis.modules.neonatalScreeningSampleLogin.client.NeonatalScreeningSampleLoginScreenUI;
import org.openelis.modules.order1.client.InternalOrderScreenUI;
import org.openelis.modules.order1.client.SendoutOrderScreenUI;
import org.openelis.modules.order1.client.VendorOrderScreenUI;
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
import org.openelis.modules.quickEntry.client.QuickEntryScreenUI;
import org.openelis.modules.report.client.AirQualityExportScreen;
import org.openelis.modules.report.client.FinalReportBatchReprintScreen;
import org.openelis.modules.report.client.FinalReportBatchScreen;
import org.openelis.modules.report.client.HoldRefuseOrganizationReportScreen;
import org.openelis.modules.report.client.KitTrackingReportScreen;
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
import org.openelis.modules.report.dataView.client.DataViewScreen;
import org.openelis.modules.report.finalReportSingleReprint.client.FinalReportSingleReprintScreen;
import org.openelis.modules.report.qcChart.client.QcChartScreen;
import org.openelis.modules.report.turnaroundStatistic.client.TurnaroundStatisticScreen;
import org.openelis.modules.sampleQC.client.SampleQCScreenUI;
import org.openelis.modules.sampleTracking1.client.SampleTrackingScreenUI;
import org.openelis.modules.scriptlet.client.ScriptletScreen;
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
import org.openelis.modules.worksheetBuilder.client.WorksheetBuilderScreenUI;
import org.openelis.modules.worksheetCompletion.client.WorksheetCompletionScreen;
import org.openelis.modules.worksheetCompletion.client.WorksheetCompletionScreenUI;
// import org.openelis.modules.worksheetCreation.client.WorksheetCreationScreen;
import org.openelis.ui.common.ModulePermission;
import org.openelis.ui.screen.Screen;
import org.openelis.ui.widget.Browser;
import org.openelis.ui.widget.Menu;
import org.openelis.ui.widget.MenuItem;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.SyncCallback;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.visualization.client.VisualizationUtils;
import com.google.gwt.visualization.client.visualizations.corechart.PieChart;

public class OpenELIS extends Screen {

    @UiTemplate("OpenELIS.ui.xml")
    interface OpenELISUiBinder extends UiBinder<Widget, OpenELIS> {
    };

    private static OpenELISUiBinder uiBinder = GWT.create(OpenELISUiBinder.class);

    @UiField
    protected static Browser        browser;
    // protected CollapsePanel favoritesCollapse;
    OpenELISConstants               msg      = GWT.create(OpenELISConstants.class);

    @UiField
    protected MenuItem              preference, logout, sampleLoginLabelReport,
                    sampleLoginLabelAdditionalReport, quickEntry, verification, tracking,
                    environmentalSampleLogin, privateWellWaterSampleLogin, sdwisSampleLogin,
                    clinicalSampleLogin, neonatalScreeningSampleLogin, animalSampleLogin,
                    ptSampleLogin, testSampleManager, project, provider, organization,
                    worksheetBuilder,/* worksheetCreation, */worksheetCompletion,
                    worksheetCompletionUI, addOrCancel, reviewAndRelease, toDo, labelFor, storage,
                    QC, sampleQc, analyteParameter, internalOrder, vendorOrder, sendoutOrder,
                    fillOrder, shipping, buildKits, inventoryTransfer, inventoryReceipt,
                    inventoryAdjustment, inventoryItem, verificationReport, testRequestFormReport,
                    orderRequestForm, holdRefuseOrganization, testReport, billingReport,
                    sampleInhouseReport, volumeReport, toDoAnalyteReport, sampleDataExport,
                    QASummaryReport, testCountByFacility, turnaround, turnAroundStatisticReport,
                    kitTrackingReport, airQualityReport, sdwisUnloadReport, dataView, qcChart,
                    finalReport, finalReportBatch, finalReportBatchReprint, test, method, panel,
                    QAEvent, labSection, analyte, dictionary, auxiliaryPrompt,
                    exchangeVocabularyMap, exchangeDataSelection, label, standardNote,
                    trailerForTest, storageUnit, storageLocation, instrument, scriptlet,
                    systemVariable, pws, cron, logs;

    @UiField
    protected Menu                  maintenanceMenu;

    public OpenELIS() throws Exception {
        Exception loadError;

        try {
            loadError = null;
            Constants.setConstants(OpenELISService.get().getConstants());
        } catch (Exception anyE) {
            loadError = anyE;
        }

        initWidget(uiBinder.createAndBindUi(this));

        maintenanceMenu.ensureDebugId("openelis.maintenanceMenu");
        method.ensureDebugId("openelis.method");

        // load the google chart api
        VisualizationUtils.loadVisualizationApi(new Runnable() {
            public void run() {
            }
        }, PieChart.PACKAGE, PieChart.PACKAGE);

        initialize();

        if (loadError != null)
            Window.alert("FATAL ERROR: " + loadError.getMessage() + "; Please contact IT support");
    }

    protected void initialize() {

        addCommand(preference, "openelis", new Command() {
            public void execute() {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            org.openelis.ui.widget.Window window = new org.openelis.ui.widget.Window(false);
                            window.setName(msg.preference());
                            window.setSize("20px", "20px");
                            window.setContent(new PreferencesScreen(window));
                            browser.addWindow(window, "preference");
                        } catch (Throwable e) {
                            remote().log(Level.SEVERE, e.getMessage(), e);
                            Window.alert(e.getMessage());
                        }
                    }

                    public void onFailure(Throwable caught) {
                        remote().log(Level.SEVERE, caught.getMessage(), caught);
                        Window.alert(caught.getMessage());
                    }
                });
            }
        });

        addCommand(logout, "openelis", new Command() {
            public void execute() {
                try {
                    logout();
                } catch (Throwable e) {
                    remote().log(Level.SEVERE, e.getMessage(), e);
                    Window.alert(e.getMessage());
                }
            }
        });

        addCommand(logs, "openelis", new Command() {
            public void execute() {
                org.openelis.ui.widget.Window window = new org.openelis.ui.widget.Window();
                window.setName(msg.logs());
                window.setSize("1000px", "600px");
                window.setContent(new LoggingScreen(window));
                browser.addWindow(window, "logs");
            }
        });

        addCommand(quickEntry, "quickentry", new Command() {
            public void execute() {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        QuickEntryScreenUI screen;

                        try {
                            org.openelis.ui.widget.Window window = new org.openelis.ui.widget.Window();
                            window.setName(msg.quickEntry());
                            window.setSize("830px", "577px");
                            screen = new QuickEntryScreenUI(window);
                            window.setContent(screen);
                            screen.initialize();
                            browser.addWindow(window, "quickEntry");
                        } catch (Throwable e) {
                            remote().log(Level.SEVERE, e.getMessage(), e);
                            Window.alert(e.getMessage());
                        }
                    }

                    public void onFailure(Throwable caught) {
                        remote().log(Level.SEVERE, caught.getMessage(), caught);
                        Window.alert(caught.getMessage());
                    }
                });
            }
        });

        addCommand(verification, "verification", new Command() {
            public void execute() {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            org.openelis.ui.widget.Window window = new org.openelis.ui.widget.Window(false);
                            window.setName(msg.verification());
                            window.setSize("20px", "20px");
                            window.setContent(new VerificationScreen(window));
                            browser.addWindow(window, "verification");
                        } catch (Throwable e) {
                            remote().log(Level.SEVERE, e.getMessage(), e);
                            Window.alert(e.getMessage());
                        }
                    }

                    public void onFailure(Throwable caught) {
                        remote().log(Level.SEVERE, caught.getMessage(), caught);
                        Window.alert(caught.getMessage());
                    }
                });
            }
        });

        addCommand(tracking, "sampletracking", new Command() {
            public void execute() {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            org.openelis.ui.widget.Window window = new org.openelis.ui.widget.Window();
                            window.setName(msg.sampleTracking_tracking());
                            window.setSize("1074px", "435px");
                            window.setContent(new SampleTrackingScreenUI(window));
                            browser.addWindow(window, "tracking");
                        } catch (Throwable e) {
                            remote().log(Level.SEVERE, e.getMessage(), e);
                            Window.alert(e.getMessage());
                        }
                    }

                    public void onFailure(Throwable caught) {
                        remote().log(Level.SEVERE, caught.getMessage(), caught);
                        Window.alert(caught.getMessage());
                    }
                });
            }
        });

        addCommand(environmentalSampleLogin, "sampleenvironmental", new Command() {
            public void execute() {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            org.openelis.ui.widget.Window window = new org.openelis.ui.widget.Window(false);
                            window.setName(msg.environmentalSampleLogin());
                            window.setSize("20px", "20px");
                            window.setContent(new EnvironmentalSampleLoginScreen(window));
                            browser.addWindow(window, "environmentalSampleLogin");
                        } catch (Throwable e) {
                            remote().log(Level.SEVERE, e.getMessage(), e);
                            Window.alert(e.getMessage());
                        }
                    }

                    public void onFailure(Throwable caught) {
                        remote().log(Level.SEVERE, caught.getMessage(), caught);
                        Window.alert(caught.getMessage());
                    }
                });
            }
        });

        addCommand(clinicalSampleLogin, "sampleclinical", new Command() {
            public void execute() {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            org.openelis.ui.widget.Window window = new org.openelis.ui.widget.Window();
                            window.setName(msg.clinicalSampleLogin());
                            window.setSize("770px", "740px");
                            window.setContent(new ClinicalSampleLoginScreenUI(window));
                            browser.addWindow(window, "clinicalSampleLogin");
                        } catch (Throwable e) {
                            remote().log(Level.SEVERE, e.getMessage(), e);
                            Window.alert(e.getMessage());
                        }
                    }

                    public void onFailure(Throwable caught) {
                        remote().log(Level.SEVERE, caught.getMessage(), caught);
                        Window.alert(caught.getMessage());
                    }
                });
            }
        });

        addCommand(neonatalScreeningSampleLogin, "sampleneonatal", new Command() {
            public void execute() {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            org.openelis.ui.widget.Window window = new org.openelis.ui.widget.Window();
                            window.setName(msg.neonatalScreeningSampleLogin());
                            window.setSize("900px", "850px");
                            window.setContent(new NeonatalScreeningSampleLoginScreenUI(window));
                            browser.addWindow(window, "neonatalScreeningSampleLogin");
                        } catch (Throwable e) {
                            remote().log(Level.SEVERE, e.getMessage(), e);
                            Window.alert(e.getMessage());
                        }
                    }

                    public void onFailure(Throwable caught) {
                        remote().log(Level.SEVERE, caught.getMessage(), caught);
                        Window.alert(caught.getMessage());
                    }
                });
            }
        });

        addCommand(animalSampleLogin, "sampleanimal", new Command() {
            public void execute() {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            // browser.addScreen(new AnimalSampleLoginScreen());
                        } catch (Throwable e) {
                            remote().log(Level.SEVERE, e.getMessage(), e);
                            Window.alert(e.getMessage());
                        }
                    }

                    public void onFailure(Throwable caught) {
                        remote().log(Level.SEVERE, caught.getMessage(), caught);
                        Window.alert(caught.getMessage());
                    }
                });
            }
        });

        addCommand(ptSampleLogin, "samplept", new Command() {
            public void execute() {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            // browser.addScreen(new PTSampleLoginScreen());
                        } catch (Throwable e) {
                            remote().log(Level.SEVERE, e.getMessage(), e);
                            Window.alert(e.getMessage());
                        }
                    }

                    public void onFailure(Throwable caught) {
                        remote().log(Level.SEVERE, caught.getMessage(), caught);
                        Window.alert(caught.getMessage());
                    }
                });
            }
        });

        addCommand(sdwisSampleLogin, "samplesdwis", new Command() {
            public void execute() {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            org.openelis.ui.widget.Window window = new org.openelis.ui.widget.Window(false);
                            window.setName(msg.sdwisSampleLogin());
                            window.setSize("20px", "20px");
                            window.setContent(new SDWISSampleLoginScreen(window));
                            browser.addWindow(window, "sdwisSampleLogin");
                        } catch (Throwable e) {
                            remote().log(Level.SEVERE, e.getMessage(), e);
                            Window.alert(e.getMessage());
                        }
                    }

                    public void onFailure(Throwable caught) {
                        remote().log(Level.SEVERE, caught.getMessage(), caught);
                        Window.alert(caught.getMessage());
                    }
                });
            }
        });

        addCommand(privateWellWaterSampleLogin, "sampleprivatewell", new Command() {
            public void execute() {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            org.openelis.ui.widget.Window window = new org.openelis.ui.widget.Window(false);
                            window.setName(msg.privateWellWaterSampleLogin());
                            window.setSize("20px", "20px");
                            window.setContent(new PrivateWellWaterSampleLoginScreen(window));
                            browser.addWindow(window, "privateWellWaterSampleLogin");
                        } catch (Throwable e) {
                            remote().log(Level.SEVERE, e.getMessage(), e);
                            Window.alert(e.getMessage());
                        }
                    }

                    public void onFailure(Throwable caught) {
                        remote().log(Level.SEVERE, caught.getMessage(), caught);
                        Window.alert(caught.getMessage());
                    }
                });
            }
        });

        addCommand(project, "project", new Command() {
            public void execute() {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            org.openelis.ui.widget.Window window = new org.openelis.ui.widget.Window(false);
                            window.setName(msg.project());
                            window.setSize("20px", "20px");
                            window.setContent(new ProjectScreen(window));
                            browser.addWindow(window, "project");
                        } catch (Throwable caught) {
                            remote().log(Level.SEVERE, caught.getMessage(), caught);
                            Window.alert(caught.getMessage());
                        }
                    }

                    public void onFailure(Throwable caught) {
                        remote().log(Level.SEVERE, caught.getMessage(), caught);
                        Window.alert(caught.getMessage());
                    }
                });
            }
        });

        addCommand(provider, "provider", new Command() {
            public void execute() {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            org.openelis.ui.widget.Window window = new org.openelis.ui.widget.Window(false);
                            window.setName(msg.provider());
                            window.setSize("20px", "20px");
                            window.setContent(new ProviderScreen(window));
                            browser.addWindow(window, "provider");
                        } catch (Throwable e) {
                            remote().log(Level.SEVERE, e.getMessage(), e);
                            Window.alert(e.getMessage());
                        }
                    }

                    public void onFailure(Throwable caught) {
                        remote().log(Level.SEVERE, caught.getMessage(), caught);
                        Window.alert(caught.getMessage());
                    }
                });
            }
        });

        addCommand(organization, "organization", new Command() {
            public void execute() {

                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            org.openelis.ui.widget.Window window = new org.openelis.ui.widget.Window(false);
                            // org.openelis.ui.widget.Window window = new
                            // org.openelis.ui.widget.Window();
                            window.setName(msg.organization());
                            // window.setSize("877px", "631px");
                            window.setSize("20px", "20px");
                            window.setContent(new OrganizationScreen(window));
                            // window.setContent(new
                            // OrganizationScreenUI(window));
                            browser.addWindow(window, "organization");
                        } catch (Throwable e) {
                            e.printStackTrace();
                            Window.alert(e.getMessage());
                            remote().log(Level.SEVERE, e.getMessage(), e);
                        }
                    }

                    public void onFailure(Throwable caught) {
                        remote().log(Level.SEVERE, caught.getMessage(), caught);
                        Window.alert(caught.getMessage());
                    }
                });
            }
        });

        // addCommand(worksheetCreation, "worksheet", new Command() {
        // public void execute() {
        //
        // GWT.runAsync(new RunAsyncCallback() {
        // public void onSuccess() {
        // try {
        // org.openelis.ui.widget.Window window = new
        // org.openelis.ui.widget.Window(false);
        // window.setName(msg.worksheetCreation());
        // window.setSize("20px", "20px");
        // window.setContent(new WorksheetCreationScreen(window));
        // browser.addWindow(window, "worksheetCreation");
        // } catch (Throwable e) {
        // remote().log(Level.SEVERE, e.getMessage(), e);
        // Window.alert(e.getMessage());
        // }
        // }
        //
        // public void onFailure(Throwable caught) {
        // remote().log(Level.SEVERE, caught.getMessage(), caught);
        // Window.alert(caught.getMessage());
        // }
        // });
        // }
        // });

        addCommand(worksheetBuilder, "worksheetbuilder", new Command() {
            public void execute() {

                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        WorksheetBuilderScreenUI screen;

                        try {
                            org.openelis.ui.widget.Window window = new org.openelis.ui.widget.Window();
                            window.setName(msg.worksheetBuilder());
                            window.setSize("1061px", "527px");
                            screen = new WorksheetBuilderScreenUI(window);
                            window.setContent(screen);
                            screen.initialize();
                            browser.addWindow(window, "worksheetBuilder");
                        } catch (Throwable e) {
                            remote().log(Level.SEVERE, e.getMessage(), e);
                            Window.alert(e.getMessage());
                        }
                    }

                    public void onFailure(Throwable caught) {
                        remote().log(Level.SEVERE, caught.getMessage(), caught);
                        Window.alert(caught.getMessage());
                    }
                });
            }
        });

        addCommand(worksheetCompletion, "worksheet", new Command() {
            public void execute() {

                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            org.openelis.ui.widget.Window window = new org.openelis.ui.widget.Window(false);
                            window.setName(msg.worksheetCompletion());
                            window.setSize("20px", "20px");
                            window.setContent(new WorksheetCompletionScreen(window));
                            browser.addWindow(window, "worksheetCompletion");
                        } catch (Throwable e) {
                            remote().log(Level.SEVERE, e.getMessage(), e);
                            Window.alert(e.getMessage());
                        }
                    }

                    public void onFailure(Throwable caught) {
                        remote().log(Level.SEVERE, caught.getMessage(), caught);
                        Window.alert(caught.getMessage());
                    }
                });
            }
        });

        addCommand(worksheetCompletionUI, "worksheetcompletion", new Command() {
            public void execute() {

                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        WorksheetCompletionScreenUI screen;

                        try {
                            org.openelis.ui.widget.Window window = new org.openelis.ui.widget.Window();
                            window.setName(msg.worksheetCompletion() + " 2");
                            window.setSize("1061px", "511px");
                            screen = new WorksheetCompletionScreenUI(window);
                            window.setContent(screen);
                            screen.initialize();
                            browser.addWindow(window, "worksheetCompletionUI");
                        } catch (Throwable e) {
                            remote().log(Level.SEVERE, e.getMessage(), e);
                            Window.alert(e.getMessage());
                        }
                    }

                    public void onFailure(Throwable caught) {
                        remote().log(Level.SEVERE, caught.getMessage(), caught);
                        Window.alert(caught.getMessage());
                    }
                });
            }
        });

        addCommand(addOrCancel, null, new Command() {
            public void execute() {
                // browser.addScreen(new )
            }
        });

        addCommand(reviewAndRelease, "samplecompleterelease", new Command() {
            public void execute() {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            org.openelis.ui.widget.Window window = new org.openelis.ui.widget.Window();
                            window.setName(msg.reviewAndRelease());
                            window.setSize("750px", "618px");
                            window.setContent(new CompleteReleaseScreenUI(window));
                            browser.addWindow(window, "reviewAndRelease");
                        } catch (Throwable e) {
                            remote().log(Level.SEVERE, e.getMessage(), e);
                            Window.alert(e.getMessage());
                        }
                    }

                    public void onFailure(Throwable caught) {
                        remote().log(Level.SEVERE, caught.getMessage(), caught);
                        Window.alert(caught.getMessage());
                    }
                });
            }
        });

        addCommand(storage, "storage", new Command() {
            public void execute() {

                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            org.openelis.ui.widget.Window window = new org.openelis.ui.widget.Window(false);
                            window.setName(msg.storage());
                            window.setSize("20px", "20px");
                            window.setContent(new StorageScreen(window));
                            browser.addWindow(window, "storage");
                        } catch (Throwable e) {
                            remote().log(Level.SEVERE, e.getMessage(), e);
                            Window.alert(e.getMessage());
                        }
                    }

                    public void onFailure(Throwable caught) {
                        remote().log(Level.SEVERE, caught.getMessage(), caught);
                        Window.alert(caught.getMessage());
                    }
                });
            }
        });

        addCommand(toDo, "sampletracking", new Command() {
            public void execute() {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        ToDoScreen screen;

                        try {
                            org.openelis.ui.widget.Window window = new org.openelis.ui.widget.Window(false);
                            window.setName(msg.toDo());
                            window.setSize("20px", "20px");
                            screen = new ToDoScreen(window);
                            window.setContent(screen);
                            screen.initialize();
                            browser.addWindow(window, "toDo");
                        } catch (Throwable e) {
                            e.printStackTrace();
                            remote().log(Level.SEVERE, e.getMessage(), e);
                            Window.alert(e.getMessage());
                        }
                    }

                    public void onFailure(Throwable caught) {
                        remote().log(Level.SEVERE, caught.getMessage(), caught);
                        Window.alert(caught.getMessage());
                    }
                });
            }
        });

        addCommand(labelFor, null, new Command() {
            public void execute() {
                // browser.addScreen(new )
            }
        });

        addCommand(storageLocation, "storagelocation", new Command() {
            public void execute() {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            org.openelis.ui.widget.Window window = new org.openelis.ui.widget.Window(false);
                            window.setName(msg.storageLocation());
                            window.setSize("20px", "20px");
                            window.setContent(new StorageLocationScreen(window));
                            browser.addWindow(window, "storageLocation");
                        } catch (Throwable e) {
                            remote().log(Level.SEVERE, e.getMessage(), e);
                            Window.alert(e.getMessage());
                        }
                    }

                    public void onFailure(Throwable caught) {
                        remote().log(Level.SEVERE, caught.getMessage(), caught);
                        Window.alert(caught.getMessage());
                    }
                });
            }
        });

        addCommand(QC, "qc", new Command() {
            public void execute() {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            org.openelis.ui.widget.Window window = new org.openelis.ui.widget.Window(false);
                            window.setName(msg.QC());
                            window.setSize("20px", "20px");
                            window.setContent(new QcScreen(window));
                            browser.addWindow(window, "QC");
                        } catch (Throwable e) {
                            remote().log(Level.SEVERE, e.getMessage(), e);
                            Window.alert(e.getMessage());
                        }
                    }

                    public void onFailure(Throwable caught) {
                        remote().log(Level.SEVERE, caught.getMessage(), caught);
                        Window.alert(caught.getMessage());
                    }
                });
            }
        });

        addCommand(sampleQc, "sampletracking", new Command() {
            public void execute() {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            org.openelis.ui.widget.Window window = new org.openelis.ui.widget.Window();
                            window.setName(msg.sample_sampleQc());
                            window.setSize("785px", "350px");
                            window.setContent(new SampleQCScreenUI(window));
                            browser.addWindow(window, "sampleQc");
                        } catch (Throwable e) {
                            remote().log(Level.SEVERE, e.getMessage(), e);
                            Window.alert(e.getMessage());
                        }
                    }

                    public void onFailure(Throwable caught) {
                        remote().log(Level.SEVERE, caught.getMessage(), caught);
                        Window.alert(caught.getMessage());
                    }
                });
            }
        });

        addCommand(analyteParameter, "analyteparameter", new Command() {
            public void execute() {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            org.openelis.ui.widget.Window window = new org.openelis.ui.widget.Window(false);
                            window.setName(msg.analyteParameter());
                            window.setSize("20px", "20px");
                            window.setContent(new AnalyteParameterScreen(window));
                            browser.addWindow(window, "analyteParameter");
                        } catch (Throwable e) {
                            remote().log(Level.SEVERE, e.getMessage(), e);
                            Window.alert(e.getMessage());
                        }
                    }

                    public void onFailure(Throwable caught) {
                        remote().log(Level.SEVERE, caught.getMessage(), caught);
                        Window.alert(caught.getMessage());
                    }
                });
            }
        });

        addCommand(internalOrder, "internalorder", new Command() {
            public void execute() {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            org.openelis.ui.widget.Window window = new org.openelis.ui.widget.Window();
                            window.setName(msg.internalOrder());
                            window.setSize("880px", "588px");
                            window.setContent(new InternalOrderScreenUI(window));
                            browser.addWindow(window, "internalOrder");
                        } catch (Throwable e) {
                            remote().log(Level.SEVERE, e.getMessage(), e);
                            Window.alert(e.getMessage());
                        }
                    }

                    public void onFailure(Throwable caught) {
                        remote().log(Level.SEVERE, caught.getMessage(), caught);
                        Window.alert(caught.getMessage());
                    }
                });
            }
        });

        addCommand(vendorOrder, "vendororder", new Command() {
            public void execute() {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            org.openelis.ui.widget.Window window = new org.openelis.ui.widget.Window();
                            window.setName(msg.vendorOrder());
                            window.setSize("880px", "588px");
                            window.setContent(new VendorOrderScreenUI(window));
                            browser.addWindow(window, "vendorOrder");
                        } catch (Throwable e) {
                            remote().log(Level.SEVERE, e.getMessage(), e);
                            Window.alert(e.getMessage());
                        }
                    }

                    public void onFailure(Throwable caught) {
                        remote().log(Level.SEVERE, caught.getMessage(), caught);
                        Window.alert(caught.getMessage());
                    }
                });
            }
        });

        addCommand(sendoutOrder, "sendoutorder", new Command() {
            public void execute() {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            org.openelis.ui.widget.Window window = new org.openelis.ui.widget.Window();
                            window.setName(msg.sendoutOrder());
                            window.setSize("1020px", "588px");
                            window.setContent(new SendoutOrderScreenUI(window));
                            browser.addWindow(window, "sendoutOrder");
                        } catch (Throwable e) {
                            remote().log(Level.SEVERE, e.getMessage(), e);
                            Window.alert(e.getMessage());
                        }
                    }

                    public void onFailure(Throwable caught) {
                        remote().log(Level.SEVERE, caught.getMessage(), caught);
                        Window.alert(caught.getMessage());
                    }
                });
            }
        });

        addCommand(fillOrder, "fillorder", new Command() {
            public void execute() {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            org.openelis.ui.widget.Window window = new org.openelis.ui.widget.Window(false);
                            window.setName(msg.fillOrder());
                            window.setSize("20px", "20px");
                            window.setContent(new OrderFillScreen(window));
                            browser.addWindow(window, "fillOrder");
                        } catch (Throwable e) {
                            remote().log(Level.SEVERE, e.getMessage(), e);
                            Window.alert(e.getMessage());
                        }
                    }

                    public void onFailure(Throwable caught) {
                        remote().log(Level.SEVERE, caught.getMessage(), caught);
                        Window.alert(caught.getMessage());
                    }
                });
            }
        });

        addCommand(shipping, "shipping", new Command() {
            public void execute() {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            org.openelis.ui.widget.Window window = new org.openelis.ui.widget.Window(false);
                            window.setName(msg.shipping());
                            window.setSize("20px", "20px");
                            window.setContent(new ShippingScreen(window));
                            browser.addWindow(window, "shipping");
                        } catch (Throwable e) {
                            remote().log(Level.SEVERE, e.getMessage(), e);
                            Window.alert(e.getMessage());
                        }
                    }

                    public void onFailure(Throwable caught) {
                        remote().log(Level.SEVERE, caught.getMessage(), caught);
                        Window.alert(caught.getMessage());
                    }
                });
            }
        });

        addCommand(buildKits, "buildkits", new Command() {
            public void execute() {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            org.openelis.ui.widget.Window window = new org.openelis.ui.widget.Window(false);
                            window.setName(msg.buildKits());
                            window.setSize("20px", "20px");
                            window.setContent(new BuildKitsScreen(window));
                            browser.addWindow(window, "buildKits");
                        } catch (Throwable e) {
                            remote().log(Level.SEVERE, e.getMessage(), e);
                            Window.alert(e.getMessage());
                        }
                    }

                    public void onFailure(Throwable caught) {
                        remote().log(Level.SEVERE, caught.getMessage(), caught);
                        Window.alert(caught.getMessage());
                    }
                });
            }
        });

        addCommand(inventoryReceipt, "inventoryreceipt", new Command() {
            public void execute() {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            org.openelis.ui.widget.Window window = new org.openelis.ui.widget.Window(false);
                            window.setName(msg.inventoryReceipt());
                            window.setSize("20px", "20px");
                            window.setContent(new InventoryReceiptScreen(window));
                            browser.addWindow(window, "inventoryReceipt");
                        } catch (Throwable e) {
                            remote().log(Level.SEVERE, e.getMessage(), e);
                            Window.alert(e.getMessage());
                        }
                    }

                    public void onFailure(Throwable caught) {
                        remote().log(Level.SEVERE, caught.getMessage(), caught);
                        Window.alert(caught.getMessage());
                    }
                });
            }
        });

        addCommand(inventoryTransfer, "inventorytransfer", new Command() {
            public void execute() {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            org.openelis.ui.widget.Window window = new org.openelis.ui.widget.Window(false);
                            window.setName(msg.inventoryTransfer());
                            window.setSize("20px", "20px");
                            window.setContent(new InventoryTransferScreen(window));
                            browser.addWindow(window, "inventoryTransfer");
                        } catch (Throwable e) {
                            remote().log(Level.SEVERE, e.getMessage(), e);
                            Window.alert(e.getMessage());
                        }
                    }

                    public void onFailure(Throwable caught) {
                        remote().log(Level.SEVERE, caught.getMessage(), caught);
                        Window.alert(caught.getMessage());
                    }
                });
            }
        });

        addCommand(inventoryAdjustment, "inventoryadjustment", new Command() {
            public void execute() {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            org.openelis.ui.widget.Window window = new org.openelis.ui.widget.Window(false);
                            window.setName(msg.inventoryAdjustment());
                            window.setSize("20px", "20px");
                            window.setContent(new InventoryAdjustmentScreen(window));
                            browser.addWindow(window, "inventoryAdjustment");
                        } catch (Throwable e) {
                            remote().log(Level.SEVERE, e.getMessage(), e);
                            Window.alert(e.getMessage());
                        }
                    }

                    public void onFailure(Throwable caught) {
                        remote().log(Level.SEVERE, caught.getMessage(), caught);
                        Window.alert(caught.getMessage());
                    }
                });
            }
        });

        addCommand(inventoryItem, "inventoryitem", new Command() {
            public void execute() {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            org.openelis.ui.widget.Window window = new org.openelis.ui.widget.Window(false);
                            window.setName(msg.inventoryItem());
                            window.setSize("20px", "20px");
                            window.setContent(new InventoryItemScreen(window));
                            browser.addWindow(window, "inventoryItem");
                        } catch (Throwable e) {
                            remote().log(Level.SEVERE, e.getMessage(), e);
                            Window.alert(e.getMessage());
                        }
                    }

                    public void onFailure(Throwable caught) {
                        remote().log(Level.SEVERE, caught.getMessage(), caught);
                        Window.alert(caught.getMessage());
                    }
                });
            }
        });

        addCommand(instrument, "instrument", new Command() {
            public void execute() {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            org.openelis.ui.widget.Window window = new org.openelis.ui.widget.Window(false);
                            window.setName(msg.instrument());
                            window.setSize("20px", "20px");
                            window.setContent(new InstrumentScreen(window));
                            browser.addWindow(window, "instrument");
                        } catch (Throwable e) {
                            remote().log(Level.SEVERE, e.getMessage(), e);
                            Window.alert(e.getMessage());
                        }
                    }

                    public void onFailure(Throwable caught) {
                        remote().log(Level.SEVERE, caught.getMessage(), caught);
                        Window.alert(caught.getMessage());
                    }
                });
            }
        });

        addCommand(test, "test", new Command() {
            public void execute() {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            org.openelis.ui.widget.Window window = new org.openelis.ui.widget.Window(false);
                            window.setName(msg.test());
                            window.setSize("20px", "20px");
                            window.setContent(new TestScreen(window));
                            browser.addWindow(window, "test");
                        } catch (Throwable e) {
                            remote().log(Level.SEVERE, e.getMessage(), e);
                            Window.alert(e.getMessage());
                        }
                    }

                    public void onFailure(Throwable caught) {
                        remote().log(Level.SEVERE, caught.getMessage(), caught);
                        Window.alert(caught.getMessage());
                    }
                });
            }
        });

        addCommand(method, "method", new Command() {
            public void execute() {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            org.openelis.ui.widget.Window window = new org.openelis.ui.widget.Window(true);
                            window.setName(msg.method());
                            window.setSize("862px", "432px");
                            window.setContent(new MethodScreenUI(window));
                            browser.addWindow(window, "method");
                        } catch (Throwable e) {
                            remote().log(Level.SEVERE, e.getMessage(), e);
                            Window.alert(e.getMessage());
                        }
                    }

                    public void onFailure(Throwable caught) {
                        remote().log(Level.SEVERE, caught.getMessage(), caught);
                        Window.alert(caught.getMessage());
                    }
                });
            }
        });

        addCommand(panel, "panel", new Command() {
            public void execute() {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            org.openelis.ui.widget.Window window = new org.openelis.ui.widget.Window(false);
                            window.setName(msg.panel());
                            window.setSize("20px", "20px");
                            window.setContent(new PanelScreen(window));
                            browser.addWindow(window, "panel");
                        } catch (Throwable e) {
                            remote().log(Level.SEVERE, e.getMessage(), e);
                            Window.alert(e.getMessage());
                        }
                    }

                    public void onFailure(Throwable caught) {
                        remote().log(Level.SEVERE, caught.getMessage(), caught);
                        Window.alert(caught.getMessage());
                    }
                });
            }
        });

        addCommand(QAEvent, "qaevent", new Command() {
            public void execute() {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            org.openelis.ui.widget.Window window = new org.openelis.ui.widget.Window(false);
                            window.setName(msg.QAEvent());
                            window.setSize("20px", "20px");
                            window.setContent(new QaEventScreen(window));
                            browser.addWindow(window, "QAEvent");
                        } catch (Throwable e) {
                            remote().log(Level.SEVERE, e.getMessage(), e);
                            Window.alert(e.getMessage());
                        }
                    }

                    public void onFailure(Throwable caught) {
                        remote().log(Level.SEVERE, caught.getMessage(), caught);
                        Window.alert(caught.getMessage());
                    }
                });
            }
        });

        addCommand(labSection, "section", new Command() {
            public void execute() {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            org.openelis.ui.widget.Window window = new org.openelis.ui.widget.Window(false);
                            window.setName(msg.labSection());
                            window.setSize("20px", "20px");
                            window.setContent(new SectionScreen(window));
                            browser.addWindow(window, "labSection");
                        } catch (Throwable e) {
                            remote().log(Level.SEVERE, e.getMessage(), e);
                            Window.alert(e.getMessage());
                        }
                    }

                    public void onFailure(Throwable caught) {
                        remote().log(Level.SEVERE, caught.getMessage(), caught);
                        Window.alert(caught.getMessage());
                    }
                });
            }
        });

        addCommand(analyte, "analyte", new Command() {
            public void execute() {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            org.openelis.ui.widget.Window window = new org.openelis.ui.widget.Window(false);
                            window.setName(msg.analyte());
                            window.setSize("20px", "20px");
                            window.setContent(new AnalyteScreen(window));
                            browser.addWindow(window, "analyte");
                        } catch (Throwable e) {
                            remote().log(Level.SEVERE, e.getMessage(), e);
                            Window.alert(e.getMessage());
                        }
                    }

                    public void onFailure(Throwable caught) {
                        remote().log(Level.SEVERE, caught.getMessage(), caught);
                        Window.alert(caught.getMessage());
                    }
                });
            }
        });

        addCommand(dictionary, "dictionary", new Command() {
            public void execute() {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            org.openelis.ui.widget.Window window = new org.openelis.ui.widget.Window(false);
                            window.setName(msg.dictionary());
                            window.setSize("20px", "20px");
                            window.setContent(new DictionaryScreen(window));
                            browser.addWindow(window, "dictionary");
                        } catch (Throwable e) {
                            remote().log(Level.SEVERE, e.getMessage(), e);
                            Window.alert(e.getMessage());
                        }
                    }

                    public void onFailure(Throwable caught) {
                        remote().log(Level.SEVERE, caught.getMessage(), caught);
                        Window.alert(caught.getMessage());
                    }
                });
            }
        });

        addCommand(exchangeVocabularyMap, "exchangevocabularymap", new Command() {
            public void execute() {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            org.openelis.ui.widget.Window window = new org.openelis.ui.widget.Window(false);
                            window.setName(msg.exchangeVocabularyMap());
                            window.setSize("20px", "20px");
                            window.setContent(new ExchangeVocabularyMapScreen(window));
                            browser.addWindow(window, "exchangeVocabularyMap");
                        } catch (Throwable e) {
                            remote().log(Level.SEVERE, e.getMessage(), e);
                            Window.alert(e.getMessage());
                        }
                    }

                    public void onFailure(Throwable caught) {
                        remote().log(Level.SEVERE, caught.getMessage(), caught);
                        Window.alert(caught.getMessage());
                    }
                });
            }
        });

        addCommand(exchangeDataSelection, "exchangedataselection", new Command() {
            public void execute() {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            org.openelis.ui.widget.Window window = new org.openelis.ui.widget.Window(false);
                            window.setName(msg.exchangeDataSelection());
                            window.setSize("20px", "20px");
                            window.setContent(new ExchangeDataSelectionScreen(window));
                            browser.addWindow(window, "exchangeDataSelection");
                        } catch (Throwable e) {
                            remote().log(Level.SEVERE, e.getMessage(), e);
                            Window.alert(e.getMessage());
                        }
                    }

                    public void onFailure(Throwable caught) {
                        remote().log(Level.SEVERE, caught.getMessage(), caught);
                        Window.alert(caught.getMessage());
                    }
                });
            }
        });

        addCommand(auxiliaryPrompt, "auxiliary", new Command() {
            public void execute() {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            org.openelis.ui.widget.Window window = new org.openelis.ui.widget.Window(false);
                            window.setName(msg.auxiliaryPrompt());
                            window.setSize("20px", "20px");
                            window.setContent(new AuxiliaryScreen(window));
                            browser.addWindow(window, "auxiliaryPrompt");
                        } catch (Throwable e) {
                            remote().log(Level.SEVERE, e.getMessage(), e);
                            Window.alert(e.getMessage());
                        }
                    }

                    public void onFailure(Throwable caught) {
                        remote().log(Level.SEVERE, caught.getMessage(), caught);
                        Window.alert(caught.getMessage());
                    }
                });
            }
        });

        addCommand(label, "label", new Command() {
            public void execute() {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            org.openelis.ui.widget.Window window = new org.openelis.ui.widget.Window(false);
                            window.setName(msg.label());
                            window.setSize("20px", "20px");
                            window.setContent(new LabelScreen(window));
                            browser.addWindow(window, "label");
                        } catch (Throwable e) {
                            remote().log(Level.SEVERE, e.getMessage(), e);
                            Window.alert(e.getMessage());
                        }
                    }

                    public void onFailure(Throwable caught) {
                        remote().log(Level.SEVERE, caught.getMessage(), caught);
                        Window.alert(caught.getMessage());
                    }
                });
            }
        });

        addCommand(standardNote, "standardnote", new Command() {
            public void execute() {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            org.openelis.ui.widget.Window window = new org.openelis.ui.widget.Window(false);
                            window.setName(msg.standardNote());
                            window.setSize("20px", "20px");
                            window.setContent(new StandardNoteScreen(window));
                            browser.addWindow(window, "standardNote");
                        } catch (Throwable e) {
                            remote().log(Level.SEVERE, e.getMessage(), e);
                            Window.alert(e.getMessage());
                        }
                    }

                    public void onFailure(Throwable caught) {
                        remote().log(Level.SEVERE, caught.getMessage(), caught);
                        Window.alert(caught.getMessage());
                    }
                });
            }
        });

        addCommand(trailerForTest, "testtrailer", new Command() {
            public void execute() {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            org.openelis.ui.widget.Window window = new org.openelis.ui.widget.Window(false);
                            window.setName(msg.trailerForTest());
                            window.setSize("20px", "20px");
                            window.setContent(new TestTrailerScreen(window));
                            browser.addWindow(window, "trailerForTest");
                        } catch (Throwable e) {
                            remote().log(Level.SEVERE, e.getMessage(), e);
                            Window.alert(e.getMessage());
                        }
                    }

                    public void onFailure(Throwable caught) {
                        remote().log(Level.SEVERE, caught.getMessage(), caught);
                        Window.alert(caught.getMessage());
                    }
                });
            }
        });

        addCommand(storageUnit, "storageunit", new Command() {
            public void execute() {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            org.openelis.ui.widget.Window window = new org.openelis.ui.widget.Window(false);
                            window.setName(msg.storageUnit());
                            window.setSize("20px", "20px");
                            window.setContent(new StorageUnitScreen(window));
                            browser.addWindow(window, "storageUnit");
                        } catch (Throwable e) {
                            remote().log(Level.SEVERE, e.getMessage(), e);
                            Window.alert(e.getMessage());
                        }
                    }

                    public void onFailure(Throwable caught) {
                        remote().log(Level.SEVERE, caught.getMessage(), caught);
                        Window.alert(caught.getMessage());
                    }
                });
            }
        });

        addCommand(scriptlet, "scriptlet", new Command() {
            public void execute() {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            org.openelis.ui.widget.Window window = new org.openelis.ui.widget.Window();
                            window.setName(msg.scriptlet());
                            window.setSize("862px", "432px");
                            window.setContent(new ScriptletScreen(window));
                            browser.addWindow(window, "scriptlet");
                        } catch (Exception e) {
                            remote().log(Level.SEVERE, e.getMessage(), e);
                            Window.alert(e.getMessage());
                        }
                    }

                    public void onFailure(Throwable caught) {
                        remote().log(Level.SEVERE, caught.getMessage(), caught);
                        Window.alert(caught.getMessage());
                    }
                });
            }
        });

        addCommand(systemVariable, "systemvariable", new Command() {
            public void execute() {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            org.openelis.ui.widget.Window window = new org.openelis.ui.widget.Window(false);
                            window.setName(msg.systemVariable());
                            window.setSize("20px", "20px");
                            window.setContent(new SystemVariableScreen(window));
                            browser.addWindow(window, "systemVariable");
                        } catch (Throwable e) {
                            remote().log(Level.SEVERE, e.getMessage(), e);
                            Window.alert(e.getMessage());
                        }
                    }

                    public void onFailure(Throwable caught) {
                        remote().log(Level.SEVERE, caught.getMessage(), caught);
                        Window.alert(caught.getMessage());
                    }
                });
            }
        });

        addCommand(pws, "pws", new Command() {
            public void execute() {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        PWSScreen screen;
                        try {
                            org.openelis.ui.widget.Window window = new org.openelis.ui.widget.Window(false);
                            window.setName(msg.pwsInformation());
                            screen = new PWSScreen(window);
                            window.setSize("20px", "20px");
                            window.setContent(screen);
                            screen.initialize();
                            browser.addWindow(window, "pws");
                        } catch (Throwable e) {
                            remote().log(Level.SEVERE, e.getMessage(), e);
                            Window.alert(e.getMessage());
                        }
                    }

                    public void onFailure(Throwable caught) {
                        remote().log(Level.SEVERE, caught.getMessage(), caught);
                        Window.alert(caught.getMessage());
                    }
                });
            }
        });

        addCommand(cron, "cron", new Command() {
            public void execute() {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            org.openelis.ui.widget.Window window = new org.openelis.ui.widget.Window(false);
                            window.setName(msg.cron());
                            window.setSize("20px", "20px");
                            window.setContent(new CronScreen(window));
                            browser.addWindow(window, "cron");
                        } catch (Exception e) {
                            remote().log(Level.SEVERE, e.getMessage(), e);
                            Window.alert(e.getMessage());
                        }
                    }

                    public void onFailure(Throwable caught) {
                        remote().log(Level.SEVERE, caught.getMessage(), caught);
                        Window.alert(caught.getMessage());
                    }
                });
            }
        });

        addCommand(testReport, "test", new Command() {
            public void execute() {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            org.openelis.ui.widget.Window window = new org.openelis.ui.widget.Window(false);
                            window.setName(msg.testReport());
                            window.setSize("20px", "20px");
                            window.setContent(new TestReportScreen(window));
                            browser.addWindow(window, "testReport");
                        } catch (Throwable e) {
                            remote().log(Level.SEVERE, e.getMessage(), e);
                            Window.alert(e.getMessage());
                        }
                    }

                    public void onFailure(Throwable caught) {
                        remote().log(Level.SEVERE, caught.getMessage(), caught);
                        Window.alert(caught.getMessage());
                    }
                });
            }
        });

        addCommand(sampleLoginLabelReport, "r_loginlabel", new Command() {
            public void execute() {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            org.openelis.ui.widget.Window window = new org.openelis.ui.widget.Window(false);
                            window.setName(msg.sampleLogin());
                            window.setSize("20px", "20px");
                            window.setContent(new SampleLoginLabelReportScreen(window));
                            browser.addWindow(window, "sampleLoginLabelReport");
                        } catch (Throwable e) {
                            remote().log(Level.SEVERE, e.getMessage(), e);
                            Window.alert(e.getMessage());
                        }
                    }

                    public void onFailure(Throwable caught) {
                        remote().log(Level.SEVERE, caught.getMessage(), caught);
                        Window.alert(caught.getMessage());
                    }
                });
            }
        });

        addCommand(sampleLoginLabelAdditionalReport, "r_loginlabel", new Command() {
            public void execute() {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            org.openelis.ui.widget.Window window = new org.openelis.ui.widget.Window(false);
                            window.setName(msg.sampleLogin());
                            window.setSize("20px", "20px");
                            window.setContent(new SampleLoginLabelAdditionalReportScreen(window));
                            browser.addWindow(window, "sampleLoginLabelAdditionalReport");
                        } catch (Throwable e) {
                            remote().log(Level.SEVERE, e.getMessage(), e);
                            Window.alert(e.getMessage());
                        }
                    }

                    public void onFailure(Throwable caught) {
                        remote().log(Level.SEVERE, caught.getMessage(), caught);
                        Window.alert(caught.getMessage());
                    }
                });
            }
        });

        addCommand(dataView, "sampletracking", new Command() {
            public void execute() {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            org.openelis.ui.widget.Window window = new org.openelis.ui.widget.Window(false);
                            window.setName(msg.dataView());
                            window.setSize("20px", "20px");
                            window.setContent(new DataViewScreen(window));
                            browser.addWindow(window, "dataView");
                        } catch (Throwable e) {
                            remote().log(Level.SEVERE, e.getMessage(), e);
                            Window.alert(e.getMessage());
                        }
                    }

                    public void onFailure(Throwable caught) {
                        remote().log(Level.SEVERE, caught.getMessage(), caught);
                        Window.alert(caught.getMessage());
                    }
                });
            }
        });

        addCommand(finalReport, "r_final", new Command() {
            public void execute() {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            org.openelis.ui.widget.Window window = new org.openelis.ui.widget.Window(false);
                            window.setName(msg.finalReport());
                            window.setSize("20px", "20px");
                            window.setContent(new FinalReportSingleReprintScreen(window));
                            browser.addWindow(window, "finalReport");
                        } catch (Throwable e) {
                            remote().log(Level.SEVERE, e.getMessage(), e);
                            Window.alert(e.getMessage());
                        }
                    }

                    public void onFailure(Throwable caught) {
                        remote().log(Level.SEVERE, caught.getMessage(), caught);
                        Window.alert(caught.getMessage());
                    }
                });
            }
        });

        addCommand(finalReportBatch, "r_finalbatch", new Command() {
            public void execute() {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            org.openelis.ui.widget.Window window = new org.openelis.ui.widget.Window(false);
                            window.setName(msg.finalReport());
                            window.setSize("20px", "20px");
                            window.setContent(new FinalReportBatchScreen(window));
                            browser.addWindow(window, "finalReportBatch");
                        } catch (Throwable e) {
                            remote().log(Level.SEVERE, e.getMessage(), e);
                            Window.alert(e.getMessage());
                        }
                    }

                    public void onFailure(Throwable caught) {
                        remote().log(Level.SEVERE, caught.getMessage(), caught);
                        Window.alert(caught.getMessage());
                    }
                });
            }
        });

        addCommand(finalReportBatchReprint, "r_finalbatch", new Command() {
            public void execute() {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            org.openelis.ui.widget.Window window = new org.openelis.ui.widget.Window(false);
                            window.setName(msg.finalReport());
                            window.setSize("20px", "20px");
                            window.setContent(new FinalReportBatchReprintScreen(window));
                            browser.addWindow(window, "finalReportBatchReprint");
                        } catch (Throwable e) {
                            remote().log(Level.SEVERE, e.getMessage(), e);
                            Window.alert(e.getMessage());
                        }
                    }

                    public void onFailure(Throwable caught) {
                        remote().log(Level.SEVERE, caught.getMessage(), caught);
                        Window.alert(caught.getMessage());
                    }
                });
            }
        });

        addCommand(verificationReport, "verification", new Command() {
            public void execute() {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            org.openelis.ui.widget.Window window = new org.openelis.ui.widget.Window(false);
                            window.setName(msg.verificationReport());
                            window.setSize("20px", "20px");
                            window.setContent(new VerificationReportScreen(window));
                            browser.addWindow(window, "verificationReport");
                        } catch (Throwable e) {
                            remote().log(Level.SEVERE, e.getMessage(), e);
                            Window.alert(e.getMessage());
                        }
                    }

                    public void onFailure(Throwable caught) {
                        remote().log(Level.SEVERE, caught.getMessage(), caught);
                        Window.alert(caught.getMessage());
                    }
                });
            }
        });

        addCommand(orderRequestForm, "sendoutorder", new Command() {
            public void execute() {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            org.openelis.ui.widget.Window window = new org.openelis.ui.widget.Window(false);
                            window.setName(msg.orderRequestForm());
                            window.setSize("20px", "20px");
                            window.setContent(new RequestformReportScreen(window));
                            browser.addWindow(window, "orderRequestForm");
                        } catch (Throwable e) {
                            remote().log(Level.SEVERE, e.getMessage(), e);
                            Window.alert(e.getMessage());
                        }
                    }

                    public void onFailure(Throwable caught) {
                        remote().log(Level.SEVERE, caught.getMessage(), caught);
                        Window.alert(caught.getMessage());
                    }
                });
            }
        });

        addCommand(sampleInhouseReport, "sampletracking", new Command() {
            public void execute() {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            org.openelis.ui.widget.Window window = new org.openelis.ui.widget.Window(false);
                            window.setName(msg.sampleInhouseReport());
                            window.setSize("20px", "20px");
                            window.setContent(new SampleInhouseReportScreen(window));
                            browser.addWindow(window, "sampleInhouseReport");
                        } catch (Throwable e) {
                            remote().log(Level.SEVERE, e.getMessage(), e);
                            Window.alert(e.getMessage());
                        }
                    }

                    public void onFailure(Throwable caught) {
                        remote().log(Level.SEVERE, caught.getMessage(), caught);
                        Window.alert(caught.getMessage());
                    }
                });
            }
        });

        addCommand(volumeReport, "sampletracking", new Command() {
            public void execute() {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            org.openelis.ui.widget.Window window = new org.openelis.ui.widget.Window(false);
                            window.setName(msg.volumeReport());
                            window.setSize("20px", "20px");
                            window.setContent(new VolumeReportScreen(window));
                            browser.addWindow(window, "volumeReport");
                        } catch (Throwable e) {
                            remote().log(Level.SEVERE, e.getMessage(), e);
                            Window.alert(e.getMessage());
                        }
                    }

                    public void onFailure(Throwable caught) {
                        remote().log(Level.SEVERE, caught.getMessage(), caught);
                        Window.alert(caught.getMessage());
                    }
                });
            }
        });

        addCommand(turnaround, "sampletracking", new Command() {
            public void execute() {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            org.openelis.ui.widget.Window window = new org.openelis.ui.widget.Window(false);
                            window.setName(msg.volumeReport());
                            window.setSize("20px", "20px");
                            window.setContent(new TurnaroundReportScreen(window));
                            browser.addWindow(window, "turnaround");
                        } catch (Throwable e) {
                            remote().log(Level.SEVERE, e.getMessage(), e);
                            Window.alert(e.getMessage());
                        }
                    }

                    public void onFailure(Throwable caught) {
                        remote().log(Level.SEVERE, caught.getMessage(), caught);
                        Window.alert(caught.getMessage());
                    }
                });
            }
        });

        addCommand(QASummaryReport, "sampletracking", new Command() {
            public void execute() {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            org.openelis.ui.widget.Window window = new org.openelis.ui.widget.Window(false);
                            window.setName(msg.QASummaryReport());
                            window.setSize("20px", "20px");
                            window.setContent(new QASummaryReportScreen(window));
                            browser.addWindow(window, "QASummaryReport");
                        } catch (Throwable e) {
                            remote().log(Level.SEVERE, e.getMessage(), e);
                            Window.alert(e.getMessage());
                        }
                    }

                    public void onFailure(Throwable caught) {
                        remote().log(Level.SEVERE, caught.getMessage(), caught);
                        Window.alert(caught.getMessage());
                    }
                });
            }
        });

        addCommand(sdwisUnloadReport, "samplesdwis", new Command() {
            public void execute() {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            org.openelis.ui.widget.Window window = new org.openelis.ui.widget.Window(false);
                            window.setName(msg.sdwisUnloadReport());
                            window.setSize("20px", "20px");
                            window.setContent(new SDWISUnloadReportScreen(window));
                            browser.addWindow(window, "sdwisUnloadReport");
                        } catch (Throwable e) {
                            remote().log(Level.SEVERE, e.getMessage(), e);
                            Window.alert(e.getMessage());
                        }
                    }

                    public void onFailure(Throwable caught) {
                        remote().log(Level.SEVERE, caught.getMessage(), caught);
                        Window.alert(caught.getMessage());
                    }
                });
            }
        });

        addCommand(qcChart, "sampletracking", new Command() {
            public void execute() {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            org.openelis.ui.widget.Window window = new org.openelis.ui.widget.Window(false);
                            window.setName(msg.qcChart());
                            window.setSize("20px", "20px");
                            window.setContent(new QcChartScreen(window));
                            browser.addWindow(window, "qcChart");
                        } catch (Throwable e) {
                            remote().log(Level.SEVERE, e.getMessage(), e);
                            Window.alert(e.getMessage());
                        }
                    }

                    public void onFailure(Throwable caught) {
                        remote().log(Level.SEVERE, caught.getMessage(), caught);
                        Window.alert(caught.getMessage());
                    }
                });
            }
        });

        addCommand(toDoAnalyteReport, "sampletracking", new Command() {
            public void execute() {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            org.openelis.ui.widget.Window window = new org.openelis.ui.widget.Window(false);
                            window.setName(msg.toDoAnalyteReport());
                            window.setSize("20px", "20px");
                            window.setContent(new ToDoAnalyteReportScreen(window));
                            browser.addWindow(window, "toDoAnalyteReport");
                        } catch (Throwable e) {
                            remote().log(Level.SEVERE, e.getMessage(), e);
                            Window.alert(e.getMessage());
                        }
                    }

                    public void onFailure(Throwable caught) {
                        remote().log(Level.SEVERE, caught.getMessage(), caught);
                        Window.alert(caught.getMessage());
                    }
                });
            }
        });

        addCommand(turnAroundStatisticReport, "sampletracking", new Command() {
            public void execute() {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            org.openelis.ui.widget.Window window = new org.openelis.ui.widget.Window(false);
                            window.setName(msg.turnAroundStatisticReport());
                            window.setSize("20px", "20px");
                            window.setContent(new TurnaroundStatisticScreen(window));
                            browser.addWindow(window, "turnAroundStatisticReport");
                        } catch (Throwable e) {
                            remote().log(Level.SEVERE, e.getMessage(), e);
                            Window.alert(e.getMessage());
                        }
                    }

                    public void onFailure(Throwable caught) {
                        remote().log(Level.SEVERE, caught.getMessage(), caught);
                        Window.alert(caught.getMessage());
                    }
                });
            }
        });

        addCommand(kitTrackingReport, "sendoutorder", new Command() {
            public void execute() {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            org.openelis.ui.widget.Window window = new org.openelis.ui.widget.Window(false);
                            window.setName(msg.kitTracking_kitTrackingReport());
                            window.setSize("20px", "20px");
                            window.setContent(new KitTrackingReportScreen(window));
                            browser.addWindow(window, "kitTrackingReport");
                        } catch (Throwable e) {
                            remote().log(Level.SEVERE, e.getMessage(), e);
                            Window.alert(e.getMessage());
                        }
                    }

                    public void onFailure(Throwable caught) {
                        remote().log(Level.SEVERE, caught.getMessage(), caught);
                        Window.alert(caught.getMessage());
                    }
                });
            }
        });

        addCommand(airQualityReport, "sampletracking", new Command() {
            public void execute() {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            org.openelis.ui.widget.Window window = new org.openelis.ui.widget.Window(false);
                            window.setName(msg.airQuality_airQualityReport());
                            window.setSize("20px", "20px");
                            window.setContent(new AirQualityExportScreen(window));
                            browser.addWindow(window, "airQualityReport");
                        } catch (Throwable e) {
                            remote().log(Level.SEVERE, e.getMessage(), e);
                            Window.alert(e.getMessage());
                        }
                    }

                    public void onFailure(Throwable caught) {
                        remote().log(Level.SEVERE, caught.getMessage(), caught);
                        Window.alert(caught.getMessage());
                    }
                });
            }
        });

        addCommand(holdRefuseOrganization, "sampletracking", new Command() {
            public void execute() {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            org.openelis.ui.widget.Window window = new org.openelis.ui.widget.Window(false);
                            window.setName(msg.holdRefuseOrganization());
                            window.setSize("20px", "20px");
                            window.setContent(new HoldRefuseOrganizationReportScreen(window));
                            browser.addWindow(window, "holdRefuseOrganization");
                        } catch (Throwable e) {
                            remote().log(Level.SEVERE, e.getMessage(), e);
                            Window.alert(e.getMessage());
                        }
                    }

                    public void onFailure(Throwable caught) {
                        remote().log(Level.SEVERE, caught.getMessage(), caught);
                        Window.alert(caught.getMessage());
                    }
                });
            }
        });
    }

    /**
     * Returns the browser associated with this application.
     */
    public static Browser getBrowser() {
        return browser;
    }

    /**
     * logout the user
     */
    private void logout() {

        OpenELISService.get().logout(new SyncCallback<Void>() {
            public void onSuccess(Void result) {
            }

            public void onFailure(Throwable caught) {
            }
        });
        Window.open("/openelis/OpenELIS.html", "_self", null);
    }

    /**
     * register a click handler
     */
    private void addCommand(MenuItem item, String modulePermission, Command handler) {
        ModulePermission perm;

        if (item != null && modulePermission != null) {
            perm = UserCache.getPermission().getModule(modulePermission);
            if (perm != null && perm.hasSelectPermission()) {
                item.setEnabled(true);
                item.addCommand(handler);
            }
        }
    }
}
