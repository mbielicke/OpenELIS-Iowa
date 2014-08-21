package org.openelis.modules.main.client;

import org.openelis.modules.main.client.event.ShowScreenHandler;

import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.event.shared.GwtEvent.Type;

public class ScreenBus extends SimpleEventBus {
    
    public static ScreenBus instance;
    
    public static final Type<ShowScreenHandler> ORGANIZATION = new Type<ShowScreenHandler>(),
                    PREFERENCE = new Type<ShowScreenHandler>(),
                    QUICK_ENTRY = new Type<ShowScreenHandler>(),
                    VERIFICATION = new Type<ShowScreenHandler>(),
                    SAMPLE_TRACKING = new Type<ShowScreenHandler>(),
                    SAMPLE_ENVIRONMENTAL = new Type<ShowScreenHandler>(),
                    SAMPLE_CLINICAL = new Type<ShowScreenHandler>(),
                    SAMPLE_NEONATAL = new Type<ShowScreenHandler>(),
                    SAMPLE_ANIMAL = new Type<ShowScreenHandler>(),
                    SAMPLE_PT = new Type<ShowScreenHandler>(),
                    SAMPLE_SDWIS = new Type<ShowScreenHandler>(),
                    SAMPLE_PRIVATE_WELL = new Type<ShowScreenHandler>(),
                    SAMPLE_COMPLETE_RELEASE = new Type<ShowScreenHandler>(),
                    PROJECT = new Type<ShowScreenHandler>(),
                    PROVIDER = new Type<ShowScreenHandler>(),
                    WORKSHEET_CREATION = new Type<ShowScreenHandler>(),
                    WORKSHEET_BUILDER = new Type<ShowScreenHandler>(),
                    WORKSHEET_COMPLETION = new Type<ShowScreenHandler>(),
                    STORAGE = new Type<ShowScreenHandler>(),
                    STORAGE_LOCATION = new Type<ShowScreenHandler>(),
                    TO_DO = new Type<ShowScreenHandler>(),
                    QUALITY_CONTROL = new Type<ShowScreenHandler>(),
                    ANALYTE_PARAMETER = new Type<ShowScreenHandler>(),
                    INTERNAL_ORDER = new Type<ShowScreenHandler>(),
                    VENDOR_ORDER = new Type<ShowScreenHandler>(),
                    SENDOUT_ORDER = new Type<ShowScreenHandler>(),
                    ORDER_FILL = new Type<ShowScreenHandler>(),
                    SHIPPING = new Type<ShowScreenHandler>(),
                    BUILD_KITS = new Type<ShowScreenHandler>(),
                    INVENTORY_RECEIPT = new Type<ShowScreenHandler>(),
                    INVENTORY_TRANSFER = new Type<ShowScreenHandler>(),
                    INVENTORY_ADJUSTMENT = new Type<ShowScreenHandler>(),
                    INVENTORY_ITEM = new Type<ShowScreenHandler>(),
                    INSTRUMENT = new Type<ShowScreenHandler>(),
                    TEST = new Type<ShowScreenHandler>(),
                    METHOD = new Type<ShowScreenHandler>(),
                    PANEL = new Type<ShowScreenHandler>(),
                    QA_EVENT = new Type<ShowScreenHandler>(),
                    SECTION = new Type<ShowScreenHandler>(),
                    ANALYTE = new Type<ShowScreenHandler>(),
                    DICTIONARY = new Type<ShowScreenHandler>(),
                    EXCHANGE_VOCABULARY = new Type<ShowScreenHandler>(),
                    EXCHANGE_DATA = new Type<ShowScreenHandler>(),
                    AUXILIARY = new Type<ShowScreenHandler>(),
                    LABEL = new Type<ShowScreenHandler>(),
                    STANDARD_NOTE = new Type<ShowScreenHandler>(),
                    TEST_TRAILER = new Type<ShowScreenHandler>(),
                    STORAGE_UNIT = new Type<ShowScreenHandler>(),
                    SCRIPTLET = new Type<ShowScreenHandler>(),
                    SYSTEM_VARIABLE = new Type<ShowScreenHandler>(),
                    PWS = new Type<ShowScreenHandler>(),
                    CRON = new Type<ShowScreenHandler>(),
                    TEST_REPORT = new Type<ShowScreenHandler>(),
                    SAMPLE_LOGIN_LABEL_REPORT = new Type<ShowScreenHandler>(),
                    SAMPLE_LOGIN_LABEL_ADD_REPORT = new Type<ShowScreenHandler>(),
                    DATA_VIEW_REPORT = new Type<ShowScreenHandler>(),
                    SINGLE_FINAL_REPORT_REPRINT = new Type<ShowScreenHandler>(),
                    BATCH_FINAL_REPORT = new Type<ShowScreenHandler>(),
                    BATCH_FINAL_REPORT_REPRINT = new Type<ShowScreenHandler>(),
                    VERIFICATION_REPORT = new Type<ShowScreenHandler>(),
                    REQUEST_FORM_REPORT = new Type<ShowScreenHandler>(),
                    SAMPLE_IN_HOUSE_REPORT = new Type<ShowScreenHandler>(),
                    VOLUME_REPORT = new Type<ShowScreenHandler>(),
                    TURNAROUND_REPORT = new Type<ShowScreenHandler>(),
                    QA_SUMMARY_REPORT = new Type<ShowScreenHandler>(),
                    SDWIS_UNLOAD_REPORT = new Type<ShowScreenHandler>(),
                    QC_CHART_REPORT = new Type<ShowScreenHandler>(),
                    TO_DO_ANALYTE_REPORT = new Type<ShowScreenHandler>(),
                    TURNAROUND_STATISTIC_REPORT = new Type<ShowScreenHandler>(),
                    KIT_TRACKING_REPORT = new Type<ShowScreenHandler>(),
                    HOLD_REFUSE_REPORT = new Type<ShowScreenHandler>();
    
    public static ScreenBus get() {
        if(instance == null)
            instance = new ScreenBus();
        
        return instance;
    }
    
    
    private ScreenBus() {
        
    }

}
