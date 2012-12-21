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
package org.openelis.bean;

import javax.annotation.PostConstruct;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import org.jboss.ejb3.annotation.SecurityDomain;
import org.openelis.domain.Constants;
import org.openelis.gwt.common.InconsistencyException;

/**
 * This class provides application level initialization and services.
 */
@Startup
@Singleton
@SecurityDomain("openelis")
public class ApplicationBean {

    private Constants      constants;
    private boolean        loaded, errors;

    @EJB
    private DictionaryBean dictionary;

    public ApplicationBean() {
        constants = new Constants();
    }

    @PostConstruct
    @Asynchronous
    void atStartup() {
        /*
         * load the dictionary constants
         */
        Constants.dictionary().ANALYSIS_CANCELLED = dictId("analysis_cancelled");
        Constants.dictionary().ANALYSIS_COMPLETED = dictId("analysis_completed");
        Constants.dictionary().ANALYSIS_ERROR_COMPLETED = dictId("analysis_error_completed");
        Constants.dictionary().ANALYSIS_ERROR_INITIATED = dictId("analysis_error_initiated");
        Constants.dictionary().ANALYSIS_ERROR_INPREP = dictId("analysis_error_inprep");
        Constants.dictionary().ANALYSIS_ERROR_LOGGED_IN = dictId("analysis_error_logged_in");
        Constants.dictionary().ANALYSIS_INITIATED = dictId("analysis_initiated");
        Constants.dictionary().ANALYSIS_INPREP = dictId("analysis_inprep");
        Constants.dictionary().ANALYSIS_LOGGED_IN = dictId("analysis_logged_in");
        Constants.dictionary().ANALYSIS_ON_HOLD = dictId("analysis_on_hold");
        Constants.dictionary().ANALYSIS_RELEASED = dictId("analysis_released");
        Constants.dictionary().ANALYSIS_REQUEUE = dictId("analysis_requeue");
        Constants.dictionary().AN_USER_AC_COMPLETED = dictId("an_user_ac_completed");
        Constants.dictionary().AN_USER_AC_RELEASED = dictId("an_user_ac_released");
        Constants.dictionary().AUX_ALPHA_LOWER = dictId("aux_alpha_lower");
        Constants.dictionary().AUX_ALPHA_MIXED = dictId("aux_alpha_mixed");
        Constants.dictionary().AUX_ALPHA_UPPER = dictId("aux_alpha_upper");
        Constants.dictionary().AUX_DATE = dictId("aux_date");
        Constants.dictionary().AUX_DATE_TIME = dictId("aux_date_time");
        Constants.dictionary().AUX_DEFAULT = dictId("aux_default");
        Constants.dictionary().AUX_DICTIONARY = dictId("aux_dictionary");
        Constants.dictionary().AUX_NUMERIC = dictId("aux_numeric");
        Constants.dictionary().AUX_TIME = dictId("aux_time");
        Constants.dictionary().CHART_TYPE_DYNAMIC = dictId("chart_type_dynamic");
        Constants.dictionary().CHART_TYPE_FIXED = dictId("chart_type_fixed");
        Constants.dictionary().INSTRUMENT_LOG_COMPLETED = dictId("instrument_log_completed");
        Constants.dictionary().INSTRUMENT_LOG_PENDING = dictId("instrument_log_pending");
        Constants.dictionary().LOG_LEVEL_ERROR = dictId("log_level_error");
        Constants.dictionary().LOG_LEVEL_INFO = dictId("log_level_info");
        Constants.dictionary().LOG_TYPE_DATA_TRANSMISSION = dictId("log_type_data_transmission");
        Constants.dictionary().ORDER_RECURRENCE_UNIT_DAYS = dictId("order_recurrence_unit_days");
        Constants.dictionary().ORDER_RECURRENCE_UNIT_MONTHS = dictId("order_recurrence_unit_months");
        Constants.dictionary().ORDER_RECURRENCE_UNIT_YEARS = dictId("order_recurrence_unit_years");
        Constants.dictionary().ORDER_STATUS_BACK_ORDERED = dictId("order_status_back_ordered");
        Constants.dictionary().ORDER_STATUS_PENDING = dictId("order_status_pending");
        Constants.dictionary().ORDER_STATUS_PROCESSED = dictId("order_status_processed");
        Constants.dictionary().ORG_BILL_TO = dictId("org_bill_to");
        Constants.dictionary().ORG_FINALREP_FAX_NUMBER = dictId("org_finalrep_fax_number");
        Constants.dictionary().ORG_NO_FINALREPORT = dictId("org_no_finalreport");
        Constants.dictionary().ORG_REPORT_TO = dictId("org_report_to");
        Constants.dictionary().ORG_SECOND_REPORT_TO = dictId("org_second_report_to");
        Constants.dictionary().POS_DUPLICATE = dictId("pos_duplicate");
        Constants.dictionary().POS_FIXED = dictId("pos_fixed");
        Constants.dictionary().POS_FIXED_ALWAYS = dictId("pos_fixed_always");
        Constants.dictionary().PT_SAMPLE = dictId("pt_sample");
        Constants.dictionary().QAEVENT_INTERNAL = dictId("qaevent_internal");
        Constants.dictionary().QAEVENT_OVERRIDE = dictId("qaevent_override");
        Constants.dictionary().QAEVENT_WARNING = dictId("qaevent_warning");
        Constants.dictionary().QC_BLANK = dictId("qc_blank");
        Constants.dictionary().QC_DUPLICATE = dictId("qc_duplicate");
        Constants.dictionary().QC_SPIKE = dictId("qc_spike");
        Constants.dictionary().RECEIVABLE_REPORTTO_EMAIL = dictId("receivable_reportto_email");
        Constants.dictionary().RELEASED_REPORTTO_EMAIL = dictId("released_reportto_email");
        Constants.dictionary().ROUND_INT = dictId("round_int");
        Constants.dictionary().ROUND_INT_SIG_FIG = dictId("round_int_sig_fig");
        Constants.dictionary().ROUND_INT_SIG_FIG_NOE = dictId("round_int_sig_fig_noe");
        Constants.dictionary().ROUND_SIG_FIG = dictId("round_sig_fig");
        Constants.dictionary().ROUND_SIG_FIG_NOE = dictId("round_sig_fig_noe");
        Constants.dictionary().SAMPLE_COMPLETED = dictId("sample_completed");
        Constants.dictionary().SAMPLE_ERROR = dictId("sample_error");
        Constants.dictionary().SAMPLE_LOGGED_IN = dictId("sample_logged_in");
        Constants.dictionary().SAMPLE_NOT_VERIFIED = dictId("sample_not_verified");
        Constants.dictionary().SAMPLE_RELEASED = dictId("sample_released");
        Constants.dictionary().TEST_ANALYTE_SUPLMTL = dictId("test_analyte_suplmtl");
        Constants.dictionary().TEST_RES_TYPE_ALPHA_LOWER = dictId("test_res_type_alpha_lower");
        Constants.dictionary().TEST_RES_TYPE_ALPHA_MIXED = dictId("test_res_type_alpha_mixed");
        Constants.dictionary().TEST_RES_TYPE_ALPHA_UPPER = dictId("test_res_type_alpha_upper");
        Constants.dictionary().TEST_RES_TYPE_DATE = dictId("test_res_type_date");
        Constants.dictionary().TEST_RES_TYPE_DATE_TIME = dictId("test_res_type_date_time");
        Constants.dictionary().TEST_RES_TYPE_DEFAULT = dictId("test_res_type_default");
        Constants.dictionary().TEST_RES_TYPE_DICTIONARY = dictId("test_res_type_dictionary");
        Constants.dictionary().TEST_RES_TYPE_NUMERIC = dictId("test_res_type_numeric");
        Constants.dictionary().TEST_RES_TYPE_TIME = dictId("test_res_type_time");
        Constants.dictionary().TEST_RES_TYPE_TITER = dictId("test_res_type_titer");
        Constants.dictionary().TEST_SECTION_DEFAULT = dictId("test_section_default");
        Constants.dictionary().TEST_SECTION_MATCH = dictId("test_section_match");
        Constants.dictionary().TURNAROUND_DAILY = dictId("turnaround_daily");
        Constants.dictionary().TURNAROUND_MONTHLY = dictId("turnaround_monthly");
        Constants.dictionary().TURNAROUND_WEEKLY = dictId("turnaround_weekly");
        Constants.dictionary().WORKSHEET_COMPLETE = dictId("worksheet_complete");
        Constants.dictionary().WORKSHEET_FAILED = dictId("worksheet_failed");
        Constants.dictionary().WORKSHEET_VOID = dictId("worksheet_void");
        Constants.dictionary().WORKSHEET_WORKING = dictId("worksheet_working");
        loaded = true;
    }

    /**
     * Returns the list of constants class
     */
    public Constants getConstants() throws Exception {
        if (! loaded || errors)
            throw new InconsistencyException("Could not initialize constants in ApplicationBean");
        return constants;
    }

    /*
     * fetch by system name
     */
    private Integer dictId(String systemName) {
        try {
            return dictionary.fetchBySystemName(systemName).getId();
        } catch (Exception e) {
            errors = true;
        }
        return null;
    }
}
