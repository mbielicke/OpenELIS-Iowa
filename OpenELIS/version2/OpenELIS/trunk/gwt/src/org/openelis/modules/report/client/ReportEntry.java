package org.openelis.modules.report.client;

import static org.openelis.modules.main.client.Logger.remote;

import java.util.logging.Level;

import org.openelis.constants.Messages;
import org.openelis.modules.main.client.OpenELIS;
import org.openelis.modules.main.client.ScreenBus;
import org.openelis.modules.main.client.event.ShowScreenHandler;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.user.client.Window;

public class ReportEntry implements EntryPoint {
    
    @Override
    public void onModuleLoad() {
        ScreenBus.get().addHandler(ScreenBus.TEST_REPORT, new ShowScreenHandler() {
            
            @Override
            public void showScreen() {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            org.openelis.ui.widget.Window window = new org.openelis.ui.widget.Window(false);
                            window.setName(Messages.get().testReport());
                            window.setSize("20px", "20px");
                            window.setContent(new TestReportScreen(window));
                            OpenELIS.getBrowser().addWindow(window, "testReport");
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
        
        ScreenBus.get().addHandler(ScreenBus.SAMPLE_LOGIN_LABEL_REPORT, new ShowScreenHandler() {
            
            @Override
            public void showScreen() {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            org.openelis.ui.widget.Window window = new org.openelis.ui.widget.Window(false);
                            window.setName(Messages.get().sampleLogin());
                            window.setSize("20px", "20px");
                            window.setContent(new SampleLoginLabelReportScreen(window));
                            OpenELIS.getBrowser().addWindow(window, "sampleLoginLabelReport");
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
        
        ScreenBus.get().addHandler(ScreenBus.SAMPLE_LOGIN_LABEL_ADD_REPORT, new ShowScreenHandler() {
            
            @Override
            public void showScreen() {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            org.openelis.ui.widget.Window window = new org.openelis.ui.widget.Window(false);
                            window.setName(Messages.get().sampleLogin());
                            window.setSize("20px", "20px");
                            window.setContent(new SampleLoginLabelAdditionalReportScreen(window));
                            OpenELIS.getBrowser().addWindow(window, "sampleLoginLabelAdditionalReport");
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
        
        ScreenBus.get().addHandler(ScreenBus.BATCH_FINAL_REPORT, new ShowScreenHandler() {
            
            @Override
            public void showScreen() {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            org.openelis.ui.widget.Window window = new org.openelis.ui.widget.Window(false);
                            window.setName(Messages.get().finalReport());
                            window.setSize("20px", "20px");
                            window.setContent(new FinalReportBatchScreen(window));
                            OpenELIS.getBrowser().addWindow(window, "finalReportBatch");
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
        
        ScreenBus.get().addHandler(ScreenBus.BATCH_FINAL_REPORT_REPRINT, new ShowScreenHandler() {
            
            @Override
            public void showScreen() {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            org.openelis.ui.widget.Window window = new org.openelis.ui.widget.Window(false);
                            window.setName(Messages.get().finalReport());
                            window.setSize("20px", "20px");
                            window.setContent(new FinalReportBatchReprintScreen(window));
                            OpenELIS.getBrowser().addWindow(window, "finalReportBatchReprint");
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
        
        ScreenBus.get().addHandler(ScreenBus.VERIFICATION_REPORT, new ShowScreenHandler() {
            
            @Override
            public void showScreen() {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            org.openelis.ui.widget.Window window = new org.openelis.ui.widget.Window(false);
                            window.setName(Messages.get().verificationReport());
                            window.setSize("20px", "20px");
                            window.setContent(new VerificationReportScreen(window));
                            OpenELIS.getBrowser().addWindow(window, "verificationReport");
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
        
        ScreenBus.get().addHandler(ScreenBus.REQUEST_FORM_REPORT, new ShowScreenHandler() {
            
            @Override
            public void showScreen() {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            org.openelis.ui.widget.Window window = new org.openelis.ui.widget.Window(false);
                            window.setName(Messages.get().orderRequestForm());
                            window.setSize("20px", "20px");
                            window.setContent(new RequestformReportScreen(window));
                            OpenELIS.getBrowser().addWindow(window, "orderRequestForm");
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
        
        ScreenBus.get().addHandler(ScreenBus.SAMPLE_IN_HOUSE_REPORT, new ShowScreenHandler() {
            
            @Override
            public void showScreen() {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            org.openelis.ui.widget.Window window = new org.openelis.ui.widget.Window(false);
                            window.setName(Messages.get().sampleInhouseReport());
                            window.setSize("20px", "20px");
                            window.setContent(new SampleInhouseReportScreen(window));
                            OpenELIS.getBrowser().addWindow(window, "sampleInhouseReport");
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
        
        ScreenBus.get().addHandler(ScreenBus.VOLUME_REPORT, new ShowScreenHandler() {
            
            @Override
            public void showScreen() {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            org.openelis.ui.widget.Window window = new org.openelis.ui.widget.Window(false);
                            window.setName(Messages.get().volumeReport());
                            window.setSize("20px", "20px");
                            window.setContent(new VolumeReportScreen(window));
                            OpenELIS.getBrowser().addWindow(window, "volumeReport");
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
        
        ScreenBus.get().addHandler(ScreenBus.TURNAROUND_REPORT, new ShowScreenHandler() {
            
            @Override
            public void showScreen() {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            org.openelis.ui.widget.Window window = new org.openelis.ui.widget.Window(false);
                            window.setName(Messages.get().volumeReport());
                            window.setSize("20px", "20px");
                            window.setContent(new TurnaroundReportScreen(window));
                            OpenELIS.getBrowser().addWindow(window, "turnaround");
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
        
        ScreenBus.get().addHandler(ScreenBus.QA_SUMMARY_REPORT, new ShowScreenHandler() {
            
            @Override
            public void showScreen() {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            org.openelis.ui.widget.Window window = new org.openelis.ui.widget.Window(false);
                            window.setName(Messages.get().QASummaryReport());
                            window.setSize("20px", "20px");
                            window.setContent(new QASummaryReportScreen(window));
                            OpenELIS.getBrowser().addWindow(window, "QASummaryReport");
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
        
        ScreenBus.get().addHandler(ScreenBus.SDWIS_UNLOAD_REPORT, new ShowScreenHandler() {
            
            @Override
            public void showScreen() {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            org.openelis.ui.widget.Window window = new org.openelis.ui.widget.Window(false);
                            window.setName(Messages.get().sdwisUnloadReport());
                            window.setSize("20px", "20px");
                            window.setContent(new SDWISUnloadReportScreen(window));
                            OpenELIS.getBrowser().addWindow(window, "sdwisUnloadReport");
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
        
        ScreenBus.get().addHandler(ScreenBus.TO_DO_ANALYTE_REPORT, new ShowScreenHandler() {
            
            @Override
            public void showScreen() {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            org.openelis.ui.widget.Window window = new org.openelis.ui.widget.Window(false);
                            window.setName(Messages.get().toDoAnalyteReport());
                            window.setSize("20px", "20px");
                            window.setContent(new ToDoAnalyteReportScreen(window));
                            OpenELIS.getBrowser().addWindow(window, "toDoAnalyteReport");
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
        ScreenBus.get().addHandler(ScreenBus.KIT_TRACKING_REPORT, new ShowScreenHandler() {
            
            @Override
            public void showScreen() {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            org.openelis.ui.widget.Window window = new org.openelis.ui.widget.Window(false);
                            window.setName(Messages.get().kitTracking_kitTrackingReport());
                            window.setSize("20px", "20px");
                            window.setContent(new KitTrackingReportScreen(window));
                            OpenELIS.getBrowser().addWindow(window, "kitTrackingReport");
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
        
        ScreenBus.get().addHandler(ScreenBus.HOLD_REFUSE_REPORT, new ShowScreenHandler() {
            
            @Override
            public void showScreen() {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            org.openelis.ui.widget.Window window = new org.openelis.ui.widget.Window(false);
                            window.setName(Messages.get().holdRefuseOrganization());
                            window.setSize("20px", "20px");
                            window.setContent(new HoldRefuseOrganizationReportScreen(window));
                            OpenELIS.getBrowser().addWindow(window, "holdRefuseOrganization");
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
        
        ScreenBus.get().addHandler(ScreenBus.INSTRUMENT_BARCODE_REPORT, new ShowScreenHandler() {

            @Override
            public void showScreen() {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            org.openelis.ui.widget.Window window = new org.openelis.ui.widget.Window(false);
                            window.setName(Messages.get().instrumentBarcode_instrumentBarcodeReport());
                            window.setSize("20px", "20px");
                            window.setContent(new InstrumentBarcodeReportScreen(window));
                            OpenELIS.getBrowser().addWindow(window, "instrumentBarcodeReport");
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
        
        ScreenBus.get().addHandler(ScreenBus.AIR_QUALITY_EXPORT, new ShowScreenHandler() {

            @Override
            public void showScreen() {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            org.openelis.ui.widget.Window window = new org.openelis.ui.widget.Window(false);
                            window.setName(Messages.get().airQuality_airQualityExport());
                            window.setSize("20px", "20px");
                            window.setContent(new AirQualityExportScreen(window));
                            OpenELIS.getBrowser().addWindow(window, "airQualityExport");
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
        
        ScreenBus.get().addHandler(ScreenBus.TUBE_LABEL_REPORT, new ShowScreenHandler() {
            
            @Override
            public void showScreen() {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            org.openelis.ui.widget.Window window = new org.openelis.ui.widget.Window(false);
                            window.setName(Messages.get().report_tubeLabels());
                            window.setSize("20px", "20px");
                            window.setContent(new TubeLabelReportScreen(window));
                            OpenELIS.getBrowser().addWindow(window, "tubeLabelReport");
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

        ScreenBus.get().addHandler(ScreenBus.CHL_GC_TO_CDC_EXPORT, new ShowScreenHandler() {

            @Override
            public void showScreen() {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            org.openelis.ui.widget.Window window = new org.openelis.ui.widget.Window(false);
                            window.setName("Chl-Gc to CDC Export");
                            window.setSize("20px", "20px");
                            window.setContent(new ChlGcToCDCExportScreen(window));
                            OpenELIS.getBrowser().addWindow(window, "chlGcToCDCExport");
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

        ScreenBus.get().addHandler(ScreenBus.ABNORMALS_REPORT, new ShowScreenHandler() {

            @Override
            public void showScreen() {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            org.openelis.ui.widget.Window window = new org.openelis.ui.widget.Window(false);
                            window.setName("Abnormals Report");
                            window.setSize("20px", "20px");
                            window.setContent(new AbnormalsReportScreen(window));
                            OpenELIS.getBrowser().addWindow(window, "abnormalsReport");
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

        ScreenBus.get().addHandler(ScreenBus.ABNORMALS_CALL_LIST_REPORT, new ShowScreenHandler() {

            @Override
            public void showScreen() {
                GWT.runAsync(new RunAsyncCallback() {
                    public void onSuccess() {
                        try {
                            org.openelis.ui.widget.Window window = new org.openelis.ui.widget.Window(false);
                            window.setName("Abnormals Call List Report");
                            window.setSize("20px", "20px");
                            window.setContent(new AbnormalsCallListReportScreen(window));
                            OpenELIS.getBrowser().addWindow(window, "abnormalsCallListReport");
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
}