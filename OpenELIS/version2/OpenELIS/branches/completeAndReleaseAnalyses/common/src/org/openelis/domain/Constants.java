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

import java.io.Serializable;

/**
 * The class provides application wide constants that are used in the EJB and
 * GWT context. Because some of the constants are loaded from the database and
 * the entire class has to be serialized to the GWT context, every constant is
 * an instance variable rather than static final. Additionally, the dictionary
 * constants are not immutable.
 */
public class Constants implements Serializable {
    private static final long serialVersionUID = 1L;

    private static Constants  constants;
    protected Table           table            = new Table();
    protected Domain          domain           = new Domain();
    protected Audit           audit            = new Audit();
    protected Dictionary      dictionary       = new Dictionary();
    protected Order           order            = new Order();
    protected SystemProperty  systemProperty   = new SystemProperty();
    protected UID             uid              = new UID();

    /**
     * This method is used to set a serialized instance of this class for the
     * GWT application. ApplicationBean will create and set an instance of this
     * class at application startup.
     */
    public static void setConstants(Constants instance) {
        assert constants == null : "Cannot create duplicate constants";
        constants = instance;
    }

    public static Constants getConstants() {
        return constants;
    }

    /**
     * Table reference
     */
    public static Table table() {
        return constants.table;
    }

    /**
     * Domain reference
     */
    public static Domain domain() {
        return constants.domain;
    }

    /**
     * Audit reference
     */
    public static Audit audit() {
        return constants.audit;
    }

    /**
     * Dictionary reference
     */
    public static Dictionary dictionary() {
        return constants.dictionary;
    }

    /**
     * OrderType reference
     */
    public static Order order() {
        return constants.order;
    }

    /**
     * Locale reference
     */
    public static SystemProperty systemProperty() {
        return constants.systemProperty;
    }

    /**
     * Uid reference
     */
    public static UID uid() {
        return constants.uid;
    }

    /**
     * The class is used for table reference id. Table reference ids are used
     * for common tables such as notes to link to other tables-records.
     */
    public static class Table implements Serializable {
        private static final long serialVersionUID = 1L;

        public final Integer      PATIENT          = 2, PATIENT_RELATION = 3, PROVIDER = 4,
                        ORGANIZATION = 5, ORGANIZATION_CONTACT = 6, SAMPLE = 8,
                        SAMPLE_ENVIRONMENTAL = 9, SAMPLE_ANIMAL = 10, SAMPLE_HUMAN = 11,
                        SAMPLE_PROJECT = 12, SAMPLE_ORGANIZATION = 13, SAMPLE_ITEM = 14,
                        ANALYSIS = 15, ANALYSIS_QAEVENT = 16, ANALYSIS_USER = 17, RESULT = 18,
                        QAEVENT = 19, NOTE = 20, STANDARD_NOTE = 21, TEST = 22, TEST_ANALYTE = 23,
                        TEST_RESULT = 24, TEST_REFLEX = 25, TEST_WORKSHEET = 26,
                        TEST_WORKSHEET_ITEM = 27, SECTION = 28, TEST_TRAILER = 29, METHOD = 30,
                        SAMPLE_PRIVATE_WELL = 31, PROJECT = 33, PROJECT_PARAMETER = 34,
                        INVENTORY_ITEM = 35, INVENTORY_COMPONENT = 36, INVENTORY_LOCATION = 37,
                        INVENTORY_RECEIPT = 38, ORDER = 39, ORDER_ITEM = 40, INSTRUMENT = 41,
                        INSTRUMENT_LOG = 42, INSTRUMENT_METHOD = 43, HISTORY = 44,
                        REFERENCE_TABLE = 45, PANEL = 46, PANEL_ITEM = 47, ANALYTE = 48,
                        CATEGORY = 49, DICTIONARY = 50, QC = 51, QC_ANALYTE = 52,
                        STORAGE_UNIT = 53, STORAGE_LOCATION = 54, STORAGE = 55, SCRIPTLET = 56,
                        LABEL = 57, ATTACHMENT = 58, ATTACHMENT_ITEM = 59, SYSTEM_VARIABLE = 60,
                        ADDRESS = 61, AUX_FIELD = 62, AUX_FIELD_VALUE = 63, AUX_DATA = 64,
                        ORDER_CUSTOMER_NOTE = 65, ORDER_SHIPPING_NOTE = 66, INVENTORY_X_USE = 68,
                        INVENTORY_X_PUT = 70, INVENTORY_X_ADJUST = 71, INVENTORY_ADJUSTMENT = 72,
                        SHIPPING = 73, SHIPPING_TRACKING = 74, SHIPPING_ITEM = 75, LOCK = 76,
                        PROVIDER_LOCATION = 78, PREFERENCES = 79, AUX_FIELD_GROUP = 80,
                        INVENTORY_ITEM_MANUFACTURING = 82, SAMPLE_SDWIS = 83, SAMPLE_QAEVENT = 85,
                        WORKSHEET = 86, INVENTORY_RECEIPT_ORDER_ITEM = 87,
                        ORGANIZATION_PARAMETER = 88, TEST_PREP = 89, TEST_SECTION = 90,
                        TEST_TYPE_OF_SAMPLE = 91, TEST_WORKSHEET_ANALYTE = 92,
                        WORKSHEET_ANALYSIS = 93, WORKSHEET_ITEM = 94, WORKSHEET_QC_RESULT = 96,
                        WORKSHEET_RESULT = 97, INVENTORY_TRANSACTION = 98, PWS = 99,
                        PWS_FACILITY = 100, PWS_ADDRESS = 101, PWS_MONITOR = 102,
                        ORDER_CONTAINER = 103, ORDER_TEST = 104, ANALYTE_PARAMETER = 105,
                        SECTION_PARAMETER = 106, ANALYSIS_REPORT_FLAGS = 107,
                        ORDER_RECURRENCE = 108, CRON = 109, ORDER_ORGANIZATION = 110,
                        ORDER_TEST_ANALYTE = 111, ORDER_SAMPLE_NOTE = 112,
                        EXCHANGE_LOCAL_TERM = 113, EXCHANGE_EXTERNAL_TERM = 114,
                        EXCHANGE_CRITERIA = 115, EXCHANGE_PROFILE = 116, EVENT_LOG = 117,
                        QC_LOT = 118, SAMPLE_NEONATAL = 119;
    }

    /**
     * The class represents all the sample domains that can be used in sample
     * record
     */
    public static class Domain implements Serializable {
        private static final long serialVersionUID = 1L;

        public final String       QUICKENTRY       = "Q", ENVIRONMENTAL = "E", SDWIS = "S",
                        PRIVATEWELL = "W", NEONATAL = "N", HUMAN = "H", ANIMAL = "A", PT = "P";
    }

    /**
     * The class represents all the operations that can be performed for
     * auditing.
     */
    public static class Audit implements Serializable {
        private static final long serialVersionUID = 1L;

        public final Integer      ADD              = 1, UPDATE = 2, DELETE = 3, VIEW = 4;
    }

    /**
     * The class represents some of dictionary values that are used throughout
     * the program. The constants are not final; they are loaded at server load
     * time because the value is the dictionary entry id.
     */
    public static class Dictionary implements Serializable {
        private static final long serialVersionUID = 1L;

        public Integer            ANALYSIS_CANCELLED, ANALYSIS_COMPLETED, ANALYSIS_ERROR_COMPLETED,
                        ANALYSIS_ERROR_INITIATED, ANALYSIS_ERROR_INPREP, ANALYSIS_ERROR_LOGGED_IN,
                        ANALYSIS_INITIATED, ANALYSIS_INPREP, ANALYSIS_LOGGED_IN, ANALYSIS_ON_HOLD,
                        ANALYSIS_RELEASED, ANALYSIS_REQUEUE, AN_USER_AC_COMPLETED,
                        AN_USER_AC_RELEASED, AUX_ALPHA_LOWER, AUX_ALPHA_MIXED, AUX_ALPHA_UPPER,
                        AUX_DATE, AUX_DATE_TIME, AUX_DEFAULT, AUX_DICTIONARY, AUX_NUMERIC,
                        AUX_TIME, CHART_TYPE_DYNAMIC, CHART_TYPE_FIXED, COST_CENTER_UNKNOWN,
                        INSTRUMENT_LOG_COMPLETED, INSTRUMENT_LOG_PENDING, LABORATORY_LOCATION_IC,
                        LOCAL_TYPE_ANALYTE, LOCAL_TYPE_DICTIONARY, LOCAL_TYPE_METHOD,
                        LOCAL_TYPE_ORGANIZATION, LOCAL_TYPE_TEST, LOCAL_TYPE_TEST_ANALYTE,
                        LOG_LEVEL_ERROR, LOG_LEVEL_INFO, LOG_TYPE_DATA_TRANSMISSION,
                        MICROGRAMS_PER_LITER, NANOGRAMS_PER_LITER, NANOGRAMS_PER_MILLILITER,
                        ORDER_RECURRENCE_UNIT_DAYS, ORDER_RECURRENCE_UNIT_MONTHS,
                        ORDER_RECURRENCE_UNIT_YEARS, ORDER_STATUS_BACK_ORDERED,
                        ORDER_STATUS_PENDING, ORDER_STATUS_PROCESSED, ORDER_STATUS_RECURRING,
                        ORDER_STATUS_ON_HOLD, ORDER_STATUS_CANCELLED, ORG_BILL_TO,
                        ORG_BIRTH_HOSPITAL, ORG_FINALREP_FAX_NUMBER, ORG_NO_FINALREPORT,
                        ORG_REPORT_TO, ORG_SECOND_REPORT_TO, POS_DUPLICATE, POS_FIXED,
                        POS_FIXED_ALWAYS, POS_RANDOM, POS_LAST_OF_SUBSET, POS_LAST_OF_RUN,
                        POS_LAST_OF_SUBSET_AND_RUN, PT_SAMPLE, QAEVENT_INTERNAL, QAEVENT_OVERRIDE,
                        QAEVENT_WARNING, QC_BLANK, QC_DUPLICATE, QC_SPIKE,
                        RECEIVABLE_REPORTTO_EMAIL, RELEASED_REPORTTO_EMAIL, ORG_HOLD_SAMPLE,
                        ORG_PROD_EPARTNER_URL, ORG_TEST_EPARTNER_URL, ORG_EPARTNER_AGGR,
                        REFLEX_AUTO, REFLEX_PROMPT, REFLEX_AUTO_NDUP, REFLEX_PROMPT_NDUP,
                        ROUND_INT, ROUND_INT_SIG_FIG, ROUND_INT_SIG_FIG_NOE, ROUND_SIG_FIG,
                        ROUND_SIG_FIG_NOE, SAMPLE_COMPLETED, SAMPLE_ERROR, SAMPLE_LOGGED_IN,
                        SAMPLE_NOT_VERIFIED, SAMPLE_RELEASED, SDWIS_CATEGORY_BACTERIAL,
                        SDWIS_CATEGORY_CHEMICAL, SDWIS_CATEGORY_LEAD, SDWIS_CATEGORY_RADCHEM,
                        SECTION_MCL_VIOLATION_EMAIL, SHIPPING_STATUS_PROCESSED,
                        SHIPPING_STATUS_SHIPPED, SMPL_TYPE_RT, SMPL_TYPE_RP, SMPL_TYPE_SP,
                        TEST_ANALYTE_REQ, TEST_ANALYTE_SUPLMTL, TEST_ANALYTE_READ_ONLY,
                        TEST_RES_TYPE_ALPHA_LOWER, TEST_RES_TYPE_ALPHA_MIXED,
                        TEST_RES_TYPE_ALPHA_UPPER, TEST_RES_TYPE_DATE, TEST_RES_TYPE_DATE_TIME,
                        TEST_RES_TYPE_DEFAULT, TEST_RES_TYPE_DICTIONARY, TEST_RES_TYPE_NUMERIC,
                        TEST_RES_TYPE_TIME, TEST_RES_TYPE_TITER, TEST_RESULT_NEGATIVE,
                        TEST_RESULT_POS_REPORTABLE, TEST_RESULT_POSITIVE,
                        TEST_RESULT_NEG_REPORTABLE, TEST_SECTION_DEFAULT, TEST_SECTION_MATCH,
                        TURNAROUND_DAILY, TURNAROUND_MONTHLY, TURNAROUND_WEEKLY, WF_TOTAL,
                        WORKSHEET_COMPLETE, WORKSHEET_FAILED, WORKSHEET_VOID, WORKSHEET_WORKING;
    }

    /**
     * The class represents all the order types that can be used in order record
     */
    public static class Order implements Serializable {
        private static final long serialVersionUID = 1L;

        public final String       INTERNAL         = "I", VENDOR = "V", SEND_OUT = "S";
    }

    /**
     * Class represent the language locale for all the messages
     */
    public static class SystemProperty implements Serializable {
        private static final long serialVersionUID = 1L;

        public String             LOCALE, SECURITY_APPLICATION;
    }

    /**
     * Class that translates table specific ids to system wide UIDs
     */
    public static class UID implements Serializable {
        private static final long serialVersionUID = 1L;

        /**
         * Returns a unique id representing the data object's type and key. This
         * id can be used to directly find the object this manager rather than
         * serially traversing the lists.
         */

        public String get(SampleQaEventDO data) {
            return getSampleQAEvent(data.getId());
        }

        public String get(AnalysisQaEventDO data) {
            return getAnalysisQAEvent(data.getId());
        }

        public String get(NoteDO data) {
            return getNote(data.getId());
        }

        public String get(SampleItemDO data) {
            return getSampleItem(data.getId());
        }

        public String get(AnalysisDO data) {
            return getAnalysis(data.getId());
        }

        public String get(TestDO data) {
            return getTest(data.getId());
        }

        public String get(StorageDO data) {
            return getStorage(data.getId());
        }

        public String get(AnalysisUserDO data) {
            return getAnalysisUser(data.getId());
        }

        public String get(ResultDO data) {
            return getResult(data.getId());
        }

        public String get(AuxFieldGroupDO data) {
            return getAuxFieldGroup(data.getId());
        }

        public String get(OrderTestDO data) {
            return getOrderTest(data.getId());
        }

        public String get(OrderTestAnalyteDO data) {
            return getOrderTestAnalyte(data.getId());
        }

        /**
         * Returns the UID for a given key.
         */
        public String getSampleQAEvent(Integer id) {
            return Constants.table().SAMPLE_QAEVENT + ":" + id;
        }

        public String getAnalysisQAEvent(Integer id) {
            return Constants.table().ANALYSIS_QAEVENT + ":" + id;
        }

        public String getNote(Integer id) {
            return Constants.table().NOTE + ":" + id;
        }

        public String getSampleItem(Integer id) {
            return Constants.table().SAMPLE_ITEM + ":" + id;
        }

        public String getStorage(Integer id) {
            return Constants.table().STORAGE + ":" + id;
        }

        public String getAnalysis(Integer id) {
            return Constants.table().ANALYSIS + ":" + id;
        }

        public String getTest(Integer id) {
            return Constants.table().TEST + ":" + id;
        }

        public String getAnalysisUser(Integer id) {
            return Constants.table().ANALYSIS_USER + ":" + id;
        }

        public String getResult(Integer id) {
            return Constants.table().RESULT + ":" + id;
        }

        public String getAuxFieldGroup(Integer id) {
            return Constants.table().AUX_FIELD_GROUP + ":" + id;
        }

        public String getOrderTest(Integer id) {
            return Constants.table().ORDER_TEST + ":" + id;
        }

        public String getOrderTestAnalyte(Integer id) {
            return Constants.table().ORDER_TEST_ANALYTE + ":" + id;
        }
    }
}