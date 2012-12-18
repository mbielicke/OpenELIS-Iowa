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
package org.openelis.domain;

import org.openelis.gwt.common.RPC;

/**
 * The class provides application wide constants that are used in the EJB and
 * GWT context. Parts of the class is loaded from the database at server
 * start-up which makes some of these variables not immutable.
 */
public class Constants implements RPC {
    private static final long serialVersionUID = 1L;

    /**
     * The class is used for table reference id. Table reference ids are used
     * for common tables such as notes to link to other tables-records.
     */
    public class Table {
        public final Integer PERSON = 1, PATIENT = 2, PATIENT_RELATION = 3, PROVIDER = 4,
                        ORGANIZATION = 5, ORGANIZATION_CONTACT = 6, SAMPLE = 8,
                        SAMPLE_ENVIRONMENTAL = 9, SAMPLE_ANIMAL = 10, SAMPLE_HUMAN = 11,
                        SAMPLE_PROJECT = 12, SAMPLE_ORGANIZATION = 13, SAMPLE_ITEM = 14,
                        ANALYSIS = 15, ANALYSIS_QAEVENT = 16, ANALYSIS_USER = 17,
                        RESULT = 18, QAEVENT = 19, NOTE = 20, STANDARD_NOTE = 21,
                        TEST = 22, TEST_ANALYTE = 23, TEST_RESULT = 24, TEST_REFLEX = 25,
                        TEST_WORKSHEET = 26, TEST_WORKSHEET_ITEM = 27, SECTION = 28,
                        TEST_TRAILER = 29, METHOD = 30, SAMPLE_PRIVATE_WELL = 31,
                        PROJECT = 33, PROJECT_PARAMETER = 34, INVENTORY_ITEM = 35,
                        INVENTORY_COMPONENT = 36, INVENTORY_LOCATION = 37,
                        INVENTORY_RECEIPT = 38, ORDER = 39, ORDER_ITEM = 40,
                        INSTRUMENT = 41, INSTRUMENT_LOG = 42, INSTRUMENT_METHOD = 43,
                        HISTORY = 44, REFERENCE_TABLE = 45, PANEL = 46, PANEL_ITEM = 47,
                        ANALYTE = 48, CATEGORY = 49, DICTIONARY = 50, QC = 51,
                        QC_ANALYTE = 52, STORAGE_UNIT = 53, STORAGE_LOCATION = 54,
                        STORAGE = 55, SCRIPTLET = 56, LABEL = 57, ATTACHMENT = 58,
                        ATTACHMENT_ITEM = 59, SYSTEM_VARIABLE = 60, ADDRESS = 61,
                        AUX_FIELD = 62, AUX_FIELD_VALUE = 63, AUX_DATA = 64,
                        ORDER_CUSTOMER_NOTE = 65, ORDER_SHIPPING_NOTE = 66,
                        INVENTORY_X_USE = 68, INVENTORY_X_PUT = 70,
                        INVENTORY_X_ADJUST = 71, INVENTORY_ADJUSTMENT = 72,
                        SHIPPING = 73, SHIPPING_TRACKING = 74, SHIPPING_ITEM = 75,
                        LOCK = 76, PROVIDER_LOCATION = 78, PREFERENCES = 79,
                        AUX_FIELD_GROUP = 80, INVENTORY_ITEM_MANUFACTURING = 82,
                        SAMPLE_SDWIS = 83, SAMPLE_QAEVENT = 85, WORKSHEET = 86,
                        INVENTORY_RECEIPT_ORDER_ITEM = 87, ORGANIZATION_PARAMETER = 88,
                        TEST_PREP = 89, TEST_SECTION = 90, TEST_TYPE_OF_SAMPLE = 91,
                        TEST_WORKSHEET_ANALYTE = 92, WORKSHEET_ANALYSIS = 93,
                        WORKSHEET_ITEM = 94, WORKSHEET_QC_RESULT = 96,
                        WORKSHEET_RESULT = 97, INVENTORY_TRANSACTION = 98, PWS = 99,
                        PWS_FACILITY = 100, PWS_ADDRESS = 101, PWS_MONITOR = 102,
                        ORDER_CONTAINER = 103, ORDER_TEST = 104, ANALYTE_PARAMETER = 105,
                        SECTION_PARAMETER = 106, ANALYSIS_REPORT_FLAGS = 107,
                        ORDER_RECURRENCE = 108, CRON = 109, ORDER_ORGANIZATION = 110,
                        ORDER_TEST_ANALYTE = 111, ORDER_SAMPLE_NOTE = 112,
                        EXCHANGE_LOCAL_TERM = 113, EXCHANGE_EXTERNAL_TERM = 114,
                        EXCHANGE_CRITERIA = 115, EXCHANGE_PROFILE = 116, EVENT_LOG = 117,
                        QC_LOT = 118;
    }

    /**
     * The class represents all the sample domains that can be used in sample
     * record
     */
    public static class Domain {
        public final static String QUICKENTRY = "Q", ENVIRONMENTAL = "E", SDWIS = "S",
                        PRIVATEWELL = "W", NEWBORN = "N", HUMAN = "H", ANIMAL = "A",
                        PT = "P";
    }

    /**
     * The class represents all the operations that can be performed for
     * auditing.
     */
    public static class Audit {
        public final static Integer ADD = 1, UPDATE = 2, DELETE = 3, VIEW = 4;
    }

    /**
     * The class represents some of dictionary values that are used throughout
     * the program. The constants are not final; they are loaded at server load
     * time because the value is the dictionary entry id.
     */
    public static class Dictionary {
        public static Integer ANALYSIS_CANCELLED, ANALYSIS_COMPLETED,
                        ANALYSIS_ERROR_COMPLETED, ANALYSIS_ERROR_INITIATED,
                        ANALYSIS_ERROR_INPREP, ANALYSIS_ERROR_LOGGED_IN,
                        ANALYSIS_INITIATED, ANALYSIS_INPREP, ANALYSIS_LOGGED_IN,
                        ANALYSIS_ON_HOLD, ANALYSIS_RELEASED, ANALYSIS_REQUEUE,
                        AN_USER_AC_COMPLETED, AN_USER_AC_RELEASED, AUX_ALPHA_LOWER,
                        AUX_ALPHA_MIXED, AUX_ALPHA_UPPER, AUX_DATE, AUX_DATE_TIME,
                        AUX_DEFAULT, AUX_DICTIONARY, AUX_NUMERIC, AUX_TIME,
                        CHART_TYPE_DYNAMIC, CHART_TYPE_FIXED, INSTRUMENT_LOG_COMPLETED,
                        INSTRUMENT_LOG_PENDING, LOG_LEVEL_ERROR, LOG_LEVEL_INFO,
                        LOG_TYPE_DATA_TRANSMISSION, ORDER_RECURRENCE_UNIT_DAYS,
                        ORDER_RECURRENCE_UNIT_MONTHS, ORDER_RECURRENCE_UNIT_YEARS,
                        ORDER_STATUS_BACK_ORDERED, ORDER_STATUS_PENDING,
                        ORDER_STATUS_PROCESSED, ORG_BILL_TO, ORG_FINALREP_FAX_NUMBER,
                        ORG_NO_FINALREPORT, ORG_REPORT_TO, ORG_SECOND_REPORT_TO,
                        POS_DUPLICATE, POS_FIXED, POS_FIXED_ALWAYS, PT_SAMPLE,
                        QAEVENT_INTERNAL, QAEVENT_OVERRIDE, QAEVENT_WARNING, QC_BLANK,
                        QC_DUPLICATE, QC_SPIKE, RECEIVABLE_REPORTTO_EMAIL,
                        RELEASED_REPORTTO_EMAIL, ROUND_INT, ROUND_INT_SIG_FIG,
                        ROUND_INT_SIG_FIG_NOE, ROUND_SIG_FIG, ROUND_SIG_FIG_NOE,
                        SAMPLE_COMPLETED, SAMPLE_ERROR, SAMPLE_LOGGED_IN,
                        SAMPLE_NOT_VERIFIED, SAMPLE_RELEASED, TEST_ANALYTE_SUPLMTL,
                        TEST_RES_TYPE_ALPHA_LOWER, TEST_RES_TYPE_ALPHA_MIXED,
                        TEST_RES_TYPE_ALPHA_UPPER, TEST_RES_TYPE_DATE,
                        TEST_RES_TYPE_DATE_TIME, TEST_RES_TYPE_DEFAULT,
                        TEST_RES_TYPE_DICTIONARY, TEST_RES_TYPE_NUMERIC,
                        TEST_RES_TYPE_TIME, TEST_RES_TYPE_TITER, TEST_SECTION_DEFAULT,
                        TEST_SECTION_MATCH, TURNAROUND_DAILY, TURNAROUND_MONTHLY,
                        TURNAROUND_WEEKLY, WORKSHEET_COMPLETE, WORKSHEET_FAILED,
                        WORKSHEET_VOID, WORKSHEET_WORKING;
    }
}
