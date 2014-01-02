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

import static org.openelis.modules.main.client.Logger.remote;
import static org.openelis.modules.main.client.ScreenBus.*;

import java.util.logging.Level;

import org.openelis.cache.UserCache;
import org.openelis.constants.OpenELISConstants;
import org.openelis.domain.Constants;
import org.openelis.modules.logging.client.LoggingScreen;
import org.openelis.modules.main.client.event.ShowScreenEvent;
import org.openelis.modules.main.client.event.ShowScreenHandler;
import org.openelis.ui.common.ModulePermission;
import org.openelis.ui.screen.Screen;
import org.openelis.ui.widget.Browser;
import org.openelis.ui.widget.Menu;
import org.openelis.ui.widget.MenuItem;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.GwtEvent.Type;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.SyncCallback;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.visualization.client.VisualizationUtils;
import com.google.gwt.visualization.client.visualizations.corechart.PieChart;
//import org.openelis.modules.organization.client.OrganizationScreenUI;

public class OpenELIS extends Screen {

    @UiTemplate("OpenELIS.ui.xml")
    interface OpenELISUiBinder extends UiBinder<Widget, OpenELIS> {
    };

    private static OpenELISUiBinder uiBinder = GWT.create(OpenELISUiBinder.class);

    @UiField
    protected static Browser        browser;
    // protected CollapsePanel favoritesCollapse;
    protected static OpenELISConstants               msg      = GWT.create(OpenELISConstants.class);
    
    @UiField
    protected Menu maintenance;

    @UiField
    protected MenuItem              preference, logout, sampleLoginLabelReport,
                    sampleLoginLabelAdditionalReport, quickEntry, verification, tracking,
                    environmentalSampleLogin, privateWellWaterSampleLogin, sdwisSampleLogin,
                    clinicalSampleLogin, neonatalScreeningSampleLogin, animalSampleLogin,
                    ptSampleLogin, testSampleManager, project, provider, organization,
                    worksheetBuilder, worksheetCreation, worksheetCompletion, addOrCancel,
                    reviewAndRelease, toDo, labelFor, storage, QC, analyteParameter, internalOrder,
                    vendorOrder, sendoutOrder, fillOrder, shipping, buildKits, inventoryTransfer,
                    inventoryReceipt, inventoryAdjustment, inventoryItem, verificationReport,
                    testRequestFormReport, orderRequestForm, holdRefuseOrganization, testReport,
                    billingReport, sampleInhouseReport, volumeReport, toDoAnalyteReport,
                    sampleDataExport, QASummaryReport, testCountByFacility, turnaround,
                    turnAroundStatisticReport, kitTrackingReport, sdwisUnloadReport, dataView,
                    qcChart, finalReport, finalReportBatch, finalReportBatchReprint, test, method,
                    panel, QAEvent, labSection, analyte, dictionary, auxiliaryPrompt,
                    exchangeVocabularyMap, exchangeDataSelection, label, standardNote,
                    trailerForTest, storageUnit, storageLocation, instrument, scriptlet,
                    systemVariable, pws, cron, logs;                 
    
    public OpenELIS() throws Exception {
        Exception loadError;
        
        try {
            loadError = null;
            Constants.setConstants(OpenELISService.get().getConstants());
        } catch (Exception anyE) {
            loadError = anyE;
        }

        initWidget(uiBinder.createAndBindUi(this));

        // load the google chart api
        VisualizationUtils.loadVisualizationApi(new Runnable() {
            public void run() {
            }
        }, PieChart.PACKAGE, PieChart.PACKAGE);

        initialize();
        
        if (loadError != null)
            Window.alert("FATAL ERROR: "+loadError.getMessage()+"; Please contact IT support");
    }

    protected void initialize() {
        ensureDebugId("main");
        
        addCommand(preference, "openelis", new Command() {
            public void execute() {
                showScreen(PREFERENCE);
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
                showScreen(QUICK_ENTRY);
            }
        });

        addCommand(verification, "verification", new Command() {
            public void execute() {
                showScreen(VERIFICATION);
            }
        });

        addCommand(tracking, "sampletracking", new Command() {
            public void execute() {
                showScreen(SAMPLE_TRACKING);
            }
        });

        addCommand(environmentalSampleLogin, "sampleenvironmental", new Command() {
            public void execute() {
                showScreen(SAMPLE_ENVIRONMENTAL);
            }
        });

        addCommand(clinicalSampleLogin, "sampleclinical", new Command() {
            public void execute() {
                showScreen(SAMPLE_CLINICAL);
            }
        });

        addCommand(neonatalScreeningSampleLogin, "sampleneonatal", new Command() {
            public void execute() {
                showScreen(SAMPLE_NEONATAL);
            }
        });

        addCommand(animalSampleLogin, "sampleanimal", new Command() {
            public void execute() {
                showScreen(SAMPLE_ANIMAL);
            }
        });

        addCommand(ptSampleLogin, "samplept", new Command() {
            public void execute() {
                showScreen(SAMPLE_PT);
            }
        });

        addCommand(sdwisSampleLogin, "samplesdwis", new Command() {
            public void execute() {
                showScreen(SAMPLE_SDWIS);
            }
        });

        addCommand(privateWellWaterSampleLogin, "sampleprivatewell", new Command() {
            public void execute() {
                showScreen(SAMPLE_PRIVATE_WELL);
            }
        });

        addCommand(project, "project", new Command() {
            public void execute() {
                showScreen(PROJECT);
            }
        });

        addCommand(provider, "provider", new Command() {
            public void execute() {
                showScreen(PROVIDER);
            }
        });

        addCommand(organization, "organization", new Command() {
            public void execute() {
                showScreen(ORGANIZATION);
            }
        });

        addCommand(worksheetCreation, "worksheet", new Command() {
            public void execute() {
                showScreen(WORKSHEET_CREATION);
            }
        });

        addCommand(worksheetBuilder, "worksheetbuilder", new Command() {
            public void execute() {
                showScreen(WORKSHEET_BUILDER);
            }
        });

        addCommand(worksheetCompletion, "worksheet", new Command() {
            public void execute() {
                showScreen(WORKSHEET_COMPLETION);
            }
        });

        addCommand(addOrCancel, null, new Command() {
            public void execute() {
                // browser.addScreen(new )
            }
        });

        addCommand(reviewAndRelease, "samplecompleterelease", new Command() {
            public void execute() {
                showScreen(SAMPLE_COMPLETE_RELEASE);
            }
        });

        addCommand(storage, "storage", new Command() {
            public void execute() {
                showScreen(STORAGE);
            }
        });

        addCommand(toDo, "sampletracking", new Command() {
            public void execute() {
                showScreen(TO_DO);
            }
        });

        addCommand(labelFor, null, new Command() {
            public void execute() {
                // browser.addScreen(new )
            }
        });

        addCommand(storageLocation, "storagelocation", new Command() {
            public void execute() {
                showScreen(STORAGE_LOCATION);
            }
        });

        addCommand(QC, "qc", new Command() {
            public void execute() {
                showScreen(QUALITY_CONTROL);
            }
        });

        addCommand(analyteParameter, "analyteparameter", new Command() {
            public void execute() {
                showScreen(ANALYTE_PARAMETER);
            }
        });

        addCommand(internalOrder, "internalorder", new Command() {
            public void execute() {
                showScreen(INTERNAL_ORDER);
            }
        });

        addCommand(vendorOrder, "vendororder", new Command() {
            public void execute() {
                showScreen(VENDOR_ORDER);
            }
        });

        addCommand(sendoutOrder, "sendoutorder", new Command() {
            public void execute() {

            }
        });

        addCommand(fillOrder, "fillorder", new Command() {
            public void execute() {
                showScreen(ORDER_FILL);
            }
        });

        addCommand(shipping, "shipping", new Command() {
            public void execute() {
                showScreen(SHIPPING);
            }
        });

        addCommand(buildKits, "buildkits", new Command() {
            public void execute() {
                showScreen(BUILD_KITS);
            }
        });

        addCommand(inventoryReceipt, "inventoryreceipt", new Command() {
            public void execute() {
                showScreen(INVENTORY_RECEIPT);
            }
        });

        addCommand(inventoryTransfer, "inventorytransfer", new Command() {
            public void execute() {
                showScreen(INVENTORY_TRANSFER);
            }
        });

        addCommand(inventoryAdjustment, "inventoryadjustment", new Command() {
            public void execute() {
                showScreen(INVENTORY_ADJUSTMENT);
            }
        });

        addCommand(inventoryItem, "inventoryitem", new Command() {
            public void execute() {
                showScreen(INVENTORY_ITEM);
            }
        });

        addCommand(instrument, "instrument", new Command() {
            public void execute() {
                showScreen(INSTRUMENT);
            }
        });

        addCommand(test, "test", new Command() {
            public void execute() {
                showScreen(TEST);
            }
        });

        addCommand(method, "method", new Command() {
            public void execute() {
                showScreen(METHOD);
            }
        });

        addCommand(panel, "panel", new Command() {
            public void execute() {
                showScreen(PANEL);
            }
        });

        addCommand(QAEvent, "qaevent", new Command() {
            public void execute() {
                showScreen(QA_EVENT);
            }
        });

        addCommand(labSection, "section", new Command() {
            public void execute() {
                showScreen(SECTION);
            }
        });

        addCommand(analyte, "analyte", new Command() {
            public void execute() {
                showScreen(ANALYTE);
            }
        });

        addCommand(dictionary, "dictionary", new Command() {
            public void execute() {
                showScreen(DICTIONARY);
            }
        });

        addCommand(exchangeVocabularyMap, "exchangevocabularymap", new Command() {
            public void execute() {
                showScreen(EXCHANGE_VOCABULARY);
            }
        });

        addCommand(exchangeDataSelection, "exchangedataselection", new Command() {
            public void execute() {
                showScreen(EXCHANGE_DATA);
            }
        });

        addCommand(auxiliaryPrompt, "auxiliary", new Command() {
            public void execute() {
                showScreen(AUXILIARY);
            }
        });

        addCommand(label, "label", new Command() {
            public void execute() {
                showScreen(LABEL);
            }
        });

        addCommand(standardNote, "standardnote", new Command() {
            public void execute() {
                showScreen(STANDARD_NOTE);
            }
        });

        addCommand(trailerForTest, "testtrailer", new Command() {
            public void execute() {
                showScreen(TEST_TRAILER);
            }
        });

        addCommand(storageUnit, "storageunit", new Command() {
            public void execute() {
                showScreen(STORAGE_UNIT);
            }
        });

        addCommand(scriptlet, "scriptlet", new Command() {
            public void execute() {
                showScreen(SCRIPTLET);
            }
        });

        addCommand(systemVariable, "systemvariable", new Command() {
            public void execute() {
                showScreen(SYSTEM_VARIABLE);
            }
        });

        addCommand(pws, "pws", new Command() {
            public void execute() {
                showScreen(PWS);
            }
        });

        addCommand(cron, "cron", new Command() {
            public void execute() {
                showScreen(CRON);
            }
        });

        addCommand(testReport, "test", new Command() {
            public void execute() {
                showScreen(TEST_REPORT);
            }
        });

        addCommand(sampleLoginLabelReport, "r_loginlabel", new Command() {
            public void execute() {
                showScreen(SAMPLE_LOGIN_LABEL_REPORT);
            }
        });

        addCommand(sampleLoginLabelAdditionalReport, "r_loginlabel", new Command() {
            public void execute() {
                showScreen(SAMPLE_LOGIN_LABEL_ADD_REPORT);
            }
        });

        addCommand(dataView, "sampletracking", new Command() {
            public void execute() {
                showScreen(DATA_VIEW_REPORT);
            }
        });

        addCommand(finalReport, "r_final", new Command() {
            public void execute() {
                showScreen(SINGLE_FINAL_REPORT_REPRINT);
            }
        });

        addCommand(finalReportBatch, "r_finalbatch", new Command() {
            public void execute() {
                showScreen(BATCH_FINAL_REPORT);
            }
        });

        addCommand(finalReportBatchReprint, "r_finalbatch", new Command() {
            public void execute() {
                showScreen(BATCH_FINAL_REPORT_REPRINT);
            }
        });

        addCommand(verificationReport, "verification", new Command() {
            public void execute() {
                showScreen(VERIFICATION_REPORT);
            }
        });

        addCommand(orderRequestForm, "sendoutorder", new Command() {
            public void execute() {
                showScreen(REQUEST_FORM_REPORT);
            }
        });

        addCommand(sampleInhouseReport, "sampletracking", new Command() {
            public void execute() {
                showScreen(SAMPLE_IN_HOUSE_REPORT);
            }
        });

        addCommand(volumeReport, "sampletracking", new Command() {
            public void execute() {
                showScreen(VOLUME_REPORT);
            }
        });

        addCommand(turnaround, "sampletracking", new Command() {
            public void execute() {
                showScreen(TURNAROUND_REPORT);
            }
        });

        addCommand(QASummaryReport, "sampletracking", new Command() {
            public void execute() {
                showScreen(QA_SUMMARY_REPORT);
            }
        });

        addCommand(sdwisUnloadReport, "samplesdwis", new Command() {
            public void execute() {
                showScreen(SDWIS_UNLOAD_REPORT);
            }
        });

        addCommand(qcChart, "sampletracking", new Command() {
            public void execute() {
                showScreen(QC_CHART_REPORT);
            }
        });

        addCommand(toDoAnalyteReport, "sampletracking", new Command() {
            public void execute() {
                showScreen(TO_DO_ANALYTE_REPORT);
            }
        });

        addCommand(turnAroundStatisticReport, "sampletracking", new Command() {
            public void execute() {
                showScreen(TURNAROUND_STATISTIC_REPORT);
            }
        });

        addCommand(kitTrackingReport, "sendoutorder", new Command() {
            public void execute() {
                showScreen(KIT_TRACKING_REPORT);
            }
        });

        addCommand(holdRefuseOrganization, "sampletracking", new Command() {
            public void execute() {
                showScreen(HOLD_REFUSE_REPORT);
            }
        });
    }

    /**
     * Returns the browser associated with this application.
     */
    public static Browser getBrowser() {
        return browser;
    }

    private void showScreen(Type<ShowScreenHandler> type) {
        ScreenBus.get().fireEvent(new ShowScreenEvent(type));
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
    
    @Override
    protected void onEnsureDebugId(String baseID) {
        super.onEnsureDebugId(baseID);
        
        preference.ensureDebugId(baseID + Ids.PREFRENCE); 
        logout.ensureDebugId(baseID + Ids.LOGOUT); 
        sampleLoginLabelReport.ensureDebugId(baseID + Ids.SAMPLE_LOGIN_LABEL_REPORT);
        sampleLoginLabelAdditionalReport.ensureDebugId(baseID + Ids.SAMPLE_LOGIN_ADDITIONAL_REPORT);
        quickEntry.ensureDebugId(baseID + Ids.QUICK_ENTRY);
        verification.ensureDebugId(baseID + Ids.VERIFICATION); 
        tracking.ensureDebugId(baseID + Ids.TRACKING);
        environmentalSampleLogin.ensureDebugId(baseID + Ids.ENVIRONMENTAL_SAMPLE_LOGIN);
        privateWellWaterSampleLogin.ensureDebugId(baseID + Ids.PRIVATE_WELL_WATER_SAMPLE_LOGIN);
        sdwisSampleLogin.ensureDebugId(baseID + Ids.SDWIS_SAMPLE_LOGIN);
        clinicalSampleLogin.ensureDebugId(baseID + Ids.CLINICAL_SAMPLE_LOGIN);
        neonatalScreeningSampleLogin.ensureDebugId(baseID + Ids.NEONATAL_SCREENING_SAMPLE_LOGIN);
        animalSampleLogin.ensureDebugId(baseID + Ids.ANIMAL_SAMPLE_LOGIN);
        ptSampleLogin.ensureDebugId(baseID + Ids.PT_SAMPLE_LOGIN);
        testSampleManager.ensureDebugId(baseID + Ids.TEST_SAMPLE_MANAGER);
        project.ensureDebugId(baseID + Ids.PROJECT); 
        provider.ensureDebugId(baseID + Ids.PROVIDER);
        organization.ensureDebugId(baseID + Ids.ORGANIZATION);
        worksheetBuilder.ensureDebugId(baseID + Ids.WORKSHEET_BUILDER); 
        worksheetCreation.ensureDebugId(baseID + Ids.WORKSHEET_CREATION); 
        worksheetCompletion.ensureDebugId(baseID + Ids.WORKSHEET_COMPLETION);
        addOrCancel.ensureDebugId(baseID + Ids.ADD_OR_CANCEL);
        reviewAndRelease.ensureDebugId(baseID + Ids.REVIEW_AND_RELEASE);
        toDo.ensureDebugId(baseID + Ids.TO_DO);
        labelFor.ensureDebugId(baseID + Ids.LABEL_FOR); 
        storage.ensureDebugId(baseID + Ids.STORAGE);
        QC.ensureDebugId(baseID + Ids.QC); 
        analyteParameter.ensureDebugId(baseID + Ids.ANALYTE_PARAMETER);
        internalOrder.ensureDebugId(baseID + Ids.INTERNAL_ORDER);
        vendorOrder.ensureDebugId(baseID + Ids.VENDOR_ORDER);
        sendoutOrder.ensureDebugId(baseID + Ids.SENDOUT_ORDER);
        fillOrder.ensureDebugId(baseID + Ids.FILL_ORDER);
        shipping.ensureDebugId(baseID + Ids.SHIPPING);
        buildKits.ensureDebugId(baseID + Ids.BUILD_KITS);
        inventoryTransfer.ensureDebugId(baseID + Ids.INVENTORY_TRANSFER);
        inventoryReceipt.ensureDebugId(baseID + Ids.INVENTORY_RECEIPT);
        inventoryAdjustment.ensureDebugId(baseID + Ids.INVENTORY_ADJUSTMENT);
        inventoryItem.ensureDebugId(baseID + Ids.INVENTORY_ITEM);
        verificationReport.ensureDebugId(baseID + Ids.VERIFICAITON_REPORT);
        testRequestFormReport.ensureDebugId(baseID + Ids.TEST_REQUEST_FORM_REPORT);
        orderRequestForm.ensureDebugId(baseID + Ids.ORDER_REQUEST_FORM); 
        holdRefuseOrganization.ensureDebugId(baseID + Ids.HOLD_REFUSE_ORGANIZATION);
        testReport.ensureDebugId(baseID + Ids.TEST_REPORT);
        billingReport.ensureDebugId(baseID + Ids.BILLING_REPORT);
        sampleInhouseReport.ensureDebugId(baseID + Ids.SAMPLE_IN_HOUSE_REPORT); 
        volumeReport.ensureDebugId(baseID + Ids.VOLUME_REPORT);
        toDoAnalyteReport.ensureDebugId(baseID + Ids.TO_DO_ANALYTE_REPORT);
        sampleDataExport.ensureDebugId(baseID + Ids.SAMPLE_DATA_EXPORT);
        QASummaryReport.ensureDebugId(baseID + Ids.QA_SUMMARY_REPORT);
        testCountByFacility.ensureDebugId(baseID + Ids.TEST_COUNTY_BY_FACILITY);
        turnaround.ensureDebugId(baseID + Ids.TURNAROUND);
        turnAroundStatisticReport.ensureDebugId(baseID + Ids.TURNAROUND_STATISTIC_REPORT);
        kitTrackingReport.ensureDebugId(baseID + Ids.KIT_TRACKING_REPORT);
        sdwisUnloadReport.ensureDebugId(baseID + Ids.SDWIS_UNLOAD_REPORT);
        dataView.ensureDebugId(baseID + Ids.DATA_VIEW);
        qcChart.ensureDebugId(baseID + Ids.QC_CHART);
        finalReport.ensureDebugId(baseID + Ids.FINAL_REPORT);
        finalReportBatch.ensureDebugId(baseID + Ids.FINAL_REPORT_BATCH);
        finalReportBatchReprint.ensureDebugId(baseID + Ids.FINAL_REPORT_BATCH_REPRINT);
        test.ensureDebugId(baseID + Ids.TEST);
        method.ensureDebugId(baseID + Ids.METHOD);
        panel.ensureDebugId(baseID + Ids.PANEL);
        QAEvent.ensureDebugId(baseID + Ids.QA_EVENT);
        labSection.ensureDebugId(baseID + Ids.LAB_SECTION); 
        analyte.ensureDebugId(baseID + Ids.ANALYTE);
        dictionary.ensureDebugId(baseID + Ids.DICTIONARY);
        auxiliaryPrompt.ensureDebugId(baseID + Ids.AUXILIARY_PROMPT);
        exchangeVocabularyMap.ensureDebugId(baseID + Ids.EXCHANGE_VOCABULARY_MAP);
        exchangeDataSelection.ensureDebugId(baseID + Ids.EXCHANGE_DATA_SELECTION); 
        label.ensureDebugId(baseID + Ids.LABEL);
        standardNote.ensureDebugId(baseID + Ids.STANDARD_NOTE);
        trailerForTest.ensureDebugId(baseID + Ids.TRAILER_FOR_TEST);
        storageUnit.ensureDebugId(baseID + Ids.STORAGE_UNIT);
        storageLocation.ensureDebugId(baseID + Ids.STORAGE_LOCATION);
        instrument.ensureDebugId(baseID + Ids.INSTRUMENT);
        scriptlet.ensureDebugId(baseID + Ids.SCRIPTLET);
        systemVariable.ensureDebugId(baseID + Ids.SYSTEM_VARIABLE);
        pws.ensureDebugId(baseID + Ids.PWS);
        cron.ensureDebugId(baseID + Ids.CRON); 
        logs.ensureDebugId(baseID + Ids.LOGS);   
        maintenance.ensureDebugId(baseID + Ids.MAINTENANCE);
    }
    
    public static class Ids {
        public static final String PREFRENCE = ".preference", 
        		                   LOGOUT = ".logout", 
        		                   SAMPLE_LOGIN_LABEL_REPORT = ".sampleLoginLabelReport",
        		                   SAMPLE_LOGIN_ADDITIONAL_REPORT = ".sampleLoginLabelAdditionalReport",
        		                   QUICK_ENTRY = ".quickEntry",
        		                   VERIFICATION = ".verification", 
        		                   TRACKING = ".tracking",
        		                   ENVIRONMENTAL_SAMPLE_LOGIN = ".environmentalSampleLogin",
        		                   PRIVATE_WELL_WATER_SAMPLE_LOGIN = ".privateWellWaterSampleLogin",
        		                   SDWIS_SAMPLE_LOGIN = ".sdwisSampleLogin",
        		                   CLINICAL_SAMPLE_LOGIN = ".clinicalSampleLogin",
        		                   NEONATAL_SCREENING_SAMPLE_LOGIN = ".neonatalScreeningSampleLogin",
        		                   ANIMAL_SAMPLE_LOGIN = ".animalSampleLogin",
        		                   PT_SAMPLE_LOGIN = ".ptSampleLogin",
        		                   TEST_SAMPLE_MANAGER = ".testSampleManager",
        		                   PROJECT = ".project", 
        		                   PROVIDER = ".provider",
        		                   ORGANIZATION = ".organization",
        		                   WORKSHEET_BUILDER = ".worksheetBuilder", 
        		                   WORKSHEET_CREATION = ".worksheetCreation", 
        		                   WORKSHEET_COMPLETION = ".worksheetCompletion",
        		                   ADD_OR_CANCEL = ".addOrCancel",
        		                   REVIEW_AND_RELEASE = ".reviewAndRelease",
        		                   TO_DO = ".toDo",
        		                   LABEL_FOR = ".labelFor", 
        		                   STORAGE = ".storage",
        		                   QC = ".QC", 
        		                   ANALYTE_PARAMETER = ".analyteParameter",
        		                   INTERNAL_ORDER = ".internalOrder",
        		                   VENDOR_ORDER = ".vendorOrder",
        		                   SENDOUT_ORDER = ".sendoutOrder",
        		                   FILL_ORDER = ".fillOrder",
        		                   SHIPPING = ".shipping",
        		                   BUILD_KITS = ".buildKits",
        		                   INVENTORY_TRANSFER = ".inventoryTransfer",
        		                   INVENTORY_RECEIPT = ".inventoryReceipt",
        		                   INVENTORY_ADJUSTMENT = ".inventoryAdjustment",
        		                   INVENTORY_ITEM = ".inventoryItem",
        		                   VERIFICAITON_REPORT = ".verificationReport",
        		                   TEST_REQUEST_FORM_REPORT = ".testRequestFormReport",
        		                   ORDER_REQUEST_FORM = ".orderRequestForm", 
        		                   HOLD_REFUSE_ORGANIZATION = ".holdRefuseOrganization",
        		                   TEST_REPORT = ".testReport",
        		                   BILLING_REPORT = ".billingReport",
        		                   SAMPLE_IN_HOUSE_REPORT = ".sampleInhouseReport", 
        		                   VOLUME_REPORT = ".volumeReport",
        		                   TO_DO_ANALYTE_REPORT = ".toDoAnalyteReport",
        		                   SAMPLE_DATA_EXPORT = ".sampleDataExport",
        		                   QA_SUMMARY_REPORT = ".QASummaryReport",
        		                   TEST_COUNTY_BY_FACILITY = ".testCountByFacility",
        		                   TURNAROUND = ".turnaround",
        		                   TURNAROUND_STATISTIC_REPORT = ".turnAroundStatisticReport",
        		                   KIT_TRACKING_REPORT = ".kitTrackingReport",
        		                   SDWIS_UNLOAD_REPORT = ".sdwisUnloadReport",
        		                   DATA_VIEW = ".dataView",
        		                   QC_CHART = ".qcChart",
        		                   FINAL_REPORT = ".finalReport",
        		                   FINAL_REPORT_BATCH = ".finalReportBatch",
        		                   FINAL_REPORT_BATCH_REPRINT = ".finalReportBatchReprint",
        		                   TEST = ".test",
        		                   METHOD = ".method",
        		                   PANEL = ".panel",
        		                   QA_EVENT = ".QAEvent",
        		                   LAB_SECTION = ".labSection", 
        		                   ANALYTE = ".analyte",
        		                   DICTIONARY = ".dictionary",
        		                   AUXILIARY_PROMPT = ".auxiliaryPrompt",
        		                   EXCHANGE_VOCABULARY_MAP = ".exchangeVocabularyMap",
        		                   EXCHANGE_DATA_SELECTION = ".exchangeDataSelection", 
        		                   LABEL = ".label",
        		                   STANDARD_NOTE = ".standardNote",
        		                   TRAILER_FOR_TEST = ".trailerForTest",
        		                   STORAGE_UNIT = ".storageUnit",
        		                   STORAGE_LOCATION = ".storageLocation",
        		                   INSTRUMENT = ".instrument",
        		                   SCRIPTLET = ".scriptlet",
        		                   SYSTEM_VARIABLE = ".systemVariable",
        		                   PWS = ".pws",
        		                   CRON = ".cron", 
        		                   LOGS = ".logs",  
                                   MAINTENANCE = ".maintenence";
    }
}
