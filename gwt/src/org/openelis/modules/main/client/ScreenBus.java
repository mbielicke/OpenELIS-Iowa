package org.openelis.modules.main.client;

import org.openelis.modules.main.client.event.ShowScreenHandler;

import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.event.shared.GwtEvent.Type;

public class ScreenBus extends SimpleEventBus {

    public static ScreenBus instance;

    public static final ShowScreenType ORGANIZATION = new ShowScreenType("Organization"),
                    PREFERENCES = new ShowScreenType("Preference"),
                    QUICK_ENTRY = new ShowScreenType("Quick Entry"),
                    VERIFICATION = new ShowScreenType("Verification"),
                    SAMPLE_TRACKING = new ShowScreenType("Sample Tracking"),
                    SAMPLE_ENVIRONMENTAL = new ShowScreenType("Sample Environmental"),
                    SAMPLE_CLINICAL = new ShowScreenType("Sample Clinical"),
                    SAMPLE_NEONATAL = new ShowScreenType("Sample Neonatal"),
                    SAMPLE_ANIMAL = new ShowScreenType("Sample Animal"),
                    SAMPLE_PT = new ShowScreenType("Sample PT"),
                    SAMPLE_SDWIS = new ShowScreenType("Sample SDWIS"),
                    SAMPLE_PRIVATE_WELL = new ShowScreenType("Sample Private Well"),
                    SAMPLE_COMPLETE_RELEASE = new ShowScreenType("Sample Complete Release"),
                    PROJECT = new ShowScreenType("Project"),
                    PROVIDER = new ShowScreenType("Provider"),
                    WORKSHEET_BUILDER = new ShowScreenType("Worksheet Builder"),
                    WORKSHEET_COMPLETION = new ShowScreenType("Worksheet Completion"),
                    STORAGE = new ShowScreenType("Storage"),
                    STORAGE_LOCATION = new ShowScreenType("Storage Location"),
                    TO_DO = new ShowScreenType("To Do"),
                    QUALITY_CONTROL = new ShowScreenType("Quality Control"),
                    ANALYTE_PARAMETER = new ShowScreenType("Analyte Parameter"),
                    INTERNAL_ORDER = new ShowScreenType("Internal Order"),
                    VENDOR_ORDER = new ShowScreenType("Vendor Order"),
                    SENDOUT_ORDER = new ShowScreenType("Sendout Order"),
                    ORDER_FILL = new ShowScreenType("Order Fill"),
                    SHIPPING = new ShowScreenType("Shipping"),
                    BUILD_KITS = new ShowScreenType("Build Kits"),
                    INVENTORY_RECEIPT = new ShowScreenType("Inventory Receipt"),
                    INVENTORY_TRANSFER = new ShowScreenType("Inventory Transfer"),
                    INVENTORY_ADJUSTMENT = new ShowScreenType("Inventory Adjustment"),
                    INVENTORY_ITEM = new ShowScreenType("Inventory Item"),
                    INSTRUMENT = new ShowScreenType("Instrument"),
                    TEST = new ShowScreenType("Test"),
                    METHOD = new ShowScreenType("Method"),
                    PANEL = new ShowScreenType("Panel"),
                    QA_EVENT = new ShowScreenType("QA Event"),
                    SECTION = new ShowScreenType("Section"),
                    ANALYTE = new ShowScreenType("Analyte"),
                    DICTIONARY = new ShowScreenType("Dictionary"),
                    EXCHANGE_VOCABULARY = new ShowScreenType("Exchange Vocabulary"),
                    EXCHANGE_DATA = new ShowScreenType("Exchange Data"),
                    AUXILIARY = new ShowScreenType("Auxiliary"),
                    LABEL = new ShowScreenType("Label"),
                    STANDARD_NOTE = new ShowScreenType("Standard Note"),
                    TEST_TRAILER = new ShowScreenType("Test Trailer"),
                    STORAGE_UNIT = new ShowScreenType("Storage Unit"),
                    SCRIPTLET = new ShowScreenType("Scriptlet"),
                    SYSTEM_VARIABLE = new ShowScreenType("System Variable"),
                    PWS = new ShowScreenType("PWS"),
                    CRON = new ShowScreenType("Cron"),
                    TEST_REPORT = new ShowScreenType("Test Report"),
                    SAMPLE_LOGIN_LABEL_REPORT = new ShowScreenType("Sample Login Label Report"),
                    SAMPLE_LOGIN_LABEL_ADD_REPORT = new ShowScreenType("Sample Login Label Add Report"),
                    DATA_VIEW_REPORT = new ShowScreenType("Data View Report"),
                    SINGLE_FINAL_REPORT_REPRINT = new ShowScreenType("Single Final Report Reprint"),
                    BATCH_FINAL_REPORT = new ShowScreenType("Batch Final Report"),
                    BATCH_FINAL_REPORT_REPRINT = new ShowScreenType("Batch Final Report Reprint"),
                    SAMPLE_QC = new ShowScreenType("Sample QC Export"),
                    VERIFICATION_REPORT = new ShowScreenType("Verification Report"),
                    REQUEST_FORM_REPORT = new ShowScreenType("Request Form Report"),
                    SAMPLE_IN_HOUSE_REPORT = new ShowScreenType("Sample In House Report"),
                    VOLUME_REPORT = new ShowScreenType("Volume Report"),
                    TURNAROUND_REPORT = new ShowScreenType("Turnaround Report"),
                    QA_SUMMARY_REPORT = new ShowScreenType("QA Summary Report"),
                    SDWIS_UNLOAD_REPORT = new ShowScreenType("SDWIS Unload Report"),
                    QC_CHART_REPORT = new ShowScreenType("QC Chart Report"),
                    TO_DO_ANALYTE_REPORT = new ShowScreenType("To Do Analyte Report"),
                    TURNAROUND_STATISTIC_REPORT = new ShowScreenType("Turnaround Statistic Report"),
                    KIT_TRACKING_REPORT = new ShowScreenType("Kit Tracking Report"),
                    HOLD_REFUSE_REPORT = new ShowScreenType("Hold Refuse Report"),
                    INSTRUMENT_BARCODE_REPORT = new ShowScreenType("Instrument Barcode Report"),
                    AIR_QUALITY_EXPORT = new ShowScreenType("Air Quality Export"),
                    ATTACHMENT = new ShowScreenType("Attachment"),
                    SECONDARY_LABEL_REPORT = new ShowScreenType("Secondary Labels"),
                    PRIVATE_WELL_ATTACHMENT = new ShowScreenType("PrivateWellAttachment"),
                    TUBE_LABEL_REPORT = new ShowScreenType("Tube Labels"),
                    CHL_GC_TO_CDC_EXPORT = new ShowScreenType("Chl-Gc to CDC Export"),
                    ABNORMALS_REPORT = new ShowScreenType("Abnormals Report");

    public static ScreenBus get() {
        if (instance == null)
            instance = new ScreenBus();

        return instance;
    }

    private ScreenBus() {

    }

    public static class ShowScreenType extends Type<ShowScreenHandler> {
        String screen;

        public ShowScreenType(String screen) {
            this.screen = screen;
        }

        public String getScreen() {
            return screen;
        }
    }

}
