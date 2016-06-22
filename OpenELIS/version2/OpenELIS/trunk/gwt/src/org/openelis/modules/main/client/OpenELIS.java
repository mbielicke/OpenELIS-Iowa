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
import org.openelis.ui.common.ModulePermission;
import org.openelis.ui.screen.Screen;
import org.openelis.ui.widget.Browser;
import org.openelis.ui.widget.Menu;
import org.openelis.ui.widget.MenuItem;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.SyncCallback;
import com.google.gwt.user.client.ui.Widget;

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
                    environmentalSampleLogin, sdwisSampleLogin,
                    clinicalSampleLogin, neonatalScreeningSampleLogin, animalSampleLogin,
                    ptSampleLogin, secondDataEntry, project, provider, organization, worksheetBuilder,
                    worksheetCompletion, addOrCancel, reviewAndRelease, toDo, secondaryLabelReport,
                    storage, QC, analyteParameter, internalOrder, vendorOrder, sendoutOrder,
                    fillOrder, shipping, buildKits, inventoryTransfer, inventoryReceipt,
                    inventoryAdjustment, inventoryItem, verificationReport, orderRequestForm,
                    holdRefuseOrganization, testReport, sampleInhouseReport, volumeReport,
                    toDoAnalyteReport, sampleDataExport, QASummaryReport, testCountByFacility,
                    turnaround, turnAroundStatisticReport, kitTrackingReport, airQualityExport,
                    sdwisUnloadReport, dataView, qcChart, finalReport, finalReportBatch,
                    finalReportBatchReprint, sampleQc, test, method, panel, QAEvent, labSection,
                    analyte, dictionary, auxiliaryPrompt, exchangeVocabularyMap,
                    exchangeDataSelection, label, standardNote, trailerForTest, storageUnit,
                    storageLocation, instrument, scriptlet, systemVariable, pws, cron, logs,
                    instrumentBarcodeReport, attachment, tubeLabelReport,
                    chlGcToCDCExport, abnormalsReport, abnormalsCallListReport, patientMerge;

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

        initialize();

        if (loadError != null)
            Window.alert("FATAL ERROR: " + loadError.getMessage() + "; Please contact IT support");
    }

    protected void initialize() {
        String loginName;

        addCommand(preference, "openelis", new Command() {
            public void execute() {
                showScreen(PREFERENCES);
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

        addCommand(secondDataEntry, "verification", new Command() {
            public void execute() {
                showScreen(SECOND_DATA_ENTRY);
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

        addCommand(secondaryLabelReport, "sampletracking", new Command() {
            public void execute() {
                showScreen(SECONDARY_LABEL_REPORT);
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
                showScreen(SENDOUT_ORDER);
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

        addCommand(airQualityExport, "sampletracking", new Command() {
            public void execute() {
                showScreen(AIR_QUALITY_EXPORT);
            }
        });

        addCommand(holdRefuseOrganization, "sampletracking", new Command() {
            public void execute() {
                showScreen(HOLD_REFUSE_REPORT);
            }
        });

        addCommand(instrumentBarcodeReport, "worksheet", new Command() {
            public void execute() {
                showScreen(INSTRUMENT_BARCODE_REPORT);
            }
        });

        addCommand(sampleQc, "sampletracking", new Command() {
            public void execute() {
                showScreen(SAMPLE_QC);
            }
        });

        addCommand(attachment, "openelis", new Command() {
            public void execute() {
                showScreen(ATTACHMENT);
            }
        });
        
        addCommand(tubeLabelReport, "r_tubelabel", new Command() {
            public void execute() {
                showScreen(TUBE_LABEL_REPORT);
            }
        });

        /*
         * this screen is restricted to a few people from IT because it's used
         * for creating and attaching final reports for private well samples and
         * that will be a one-time process
         */
        loginName = UserCache.getPermission().getLoginName();
        if ("mbielick".equals(loginName) || "dshirazi".equals(loginName) ||
            "akampoow".equals(loginName)) {
            addCommand(chlGcToCDCExport, "sampletracking", new Command() {
                public void execute() {
                    showScreen(CHL_GC_TO_CDC_EXPORT);
                }
            });
        }

        addCommand(abnormalsReport, "sampletracking", new Command() {
            public void execute() {
                showScreen(ABNORMALS_REPORT);
            }
        });

        addCommand(abnormalsCallListReport, "sampletracking", new Command() {
            public void execute() {
                showScreen(ABNORMALS_CALL_LIST_REPORT);
            }
        });

        addCommand(patientMerge, "patientmerge", new Command() {
            public void execute() {
                showScreen(PATIENT_MERGE);
            }
        });
    }

    /**
     * Returns the browser associated with this application.
     */
    public static Browser getBrowser() {
        return browser;
    }

    private void showScreen(ShowScreenType type) {
        ShowScreenEvent event = new ShowScreenEvent(type);
        ScreenBus.get().fireEvent(event);
        if ( !event.wasHandled())
            Window.alert("Screen Handler for " + type.getScreen() +
                         " was not found.  Make sure the module for this screen was included in the compile");
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
