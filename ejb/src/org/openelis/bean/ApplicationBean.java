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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import org.jboss.ejb3.annotation.SecurityDomain;
import org.openelis.domain.Constants;
import org.openelis.domain.DictionaryDO;
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
    void atStartup() {
        ArrayList<DictionaryDO> list;
        HashMap<String, Integer> map;
        String names[] = {"analysis_cancelled", "analysis_completed",
                        "analysis_error_completed", "analysis_error_initiated",
                        "analysis_error_inprep", "analysis_error_logged_in",
                        "analysis_initiated", "analysis_inprep", "analysis_logged_in",
                        "analysis_on_hold", "analysis_released", "analysis_requeue",
                        "an_user_ac_completed", "an_user_ac_released", "aux_alpha_lower",
                        "aux_alpha_mixed", "aux_alpha_upper", "aux_date",
                        "aux_date_time", "aux_default", "aux_dictionary", "aux_numeric",
                        "aux_time", "chart_type_dynamic", "chart_type_fixed",
                        "instrument_log_completed", "instrument_log_pending",
                        "log_level_error", "log_level_info",
                        "log_type_data_transmission", "order_recurrence_unit_days",
                        "order_recurrence_unit_months", "order_recurrence_unit_years",
                        "order_status_back_ordered", "order_status_pending",
                        "order_status_processed", "org_bill_to",
                        "org_finalrep_fax_number", "org_no_finalreport", "org_report_to",
                        "org_second_report_to", "pos_duplicate", "pos_fixed",
                        "pos_fixed_always", "pt_sample", "qaevent_internal",
                        "qaevent_override", "qaevent_warning", "qc_blank",
                        "qc_duplicate", "qc_spike", "receivable_reportto_email",
                        "released_reportto_email", "round_int", "round_int_sig_fig",
                        "round_int_sig_fig_noe", "round_sig_fig", "round_sig_fig_noe",
                        "sample_completed", "sample_error", "sample_logged_in",
                        "sample_not_verified", "sample_released", "test_analyte_suplmtl",
                        "test_res_type_alpha_lower", "test_res_type_alpha_mixed",
                        "test_res_type_alpha_upper", "test_res_type_date",
                        "test_res_type_date_time", "test_res_type_default",
                        "test_res_type_dictionary", "test_res_type_numeric",
                        "test_res_type_time", "test_res_type_titer",
                        "test_section_default", "test_section_match", "turnaround_daily",
                        "turnaround_monthly", "turnaround_weekly", "worksheet_complete",
                        "worksheet_failed", "worksheet_void", "worksheet_working"};

        try {
            list = dictionary.fetchBySystemNames(Arrays.asList(names));
        } catch (Exception e) {
            //log.log(Level.SEVERE, "Could not load dictionary constants", e);
            errors = true;
            return;
        }
        map = new HashMap<String, Integer>();
        
        for (DictionaryDO data: list)
            map.put(data.getSystemName(), data.getId());

        /*
         * load the dictionary constants
         */
        Constants.dictionary().ANALYSIS_CANCELLED = dictId(map, "analysis_cancelled");
        Constants.dictionary().ANALYSIS_COMPLETED = dictId(map, "analysis_completed");
        Constants.dictionary().ANALYSIS_ERROR_COMPLETED = dictId(map, "analysis_error_completed");
        Constants.dictionary().ANALYSIS_ERROR_INITIATED = dictId(map, "analysis_error_initiated");
        Constants.dictionary().ANALYSIS_ERROR_INPREP = dictId(map, "analysis_error_inprep");
        Constants.dictionary().ANALYSIS_ERROR_LOGGED_IN = dictId(map, "analysis_error_logged_in");
        Constants.dictionary().ANALYSIS_INITIATED = dictId(map, "analysis_initiated");
        Constants.dictionary().ANALYSIS_INPREP = dictId(map, "analysis_inprep");
        Constants.dictionary().ANALYSIS_LOGGED_IN = dictId(map, "analysis_logged_in");
        Constants.dictionary().ANALYSIS_ON_HOLD = dictId(map, "analysis_on_hold");
        Constants.dictionary().ANALYSIS_RELEASED = dictId(map, "analysis_released");
        Constants.dictionary().ANALYSIS_REQUEUE = dictId(map, "analysis_requeue");
        Constants.dictionary().AN_USER_AC_COMPLETED = dictId(map, "an_user_ac_completed");
        Constants.dictionary().AN_USER_AC_RELEASED = dictId(map, "an_user_ac_released");
        Constants.dictionary().AUX_ALPHA_LOWER = dictId(map, "aux_alpha_lower");
        Constants.dictionary().AUX_ALPHA_MIXED = dictId(map, "aux_alpha_mixed");
        Constants.dictionary().AUX_ALPHA_UPPER = dictId(map, "aux_alpha_upper");
        Constants.dictionary().AUX_DATE = dictId(map, "aux_date");
        Constants.dictionary().AUX_DATE_TIME = dictId(map, "aux_date_time");
        Constants.dictionary().AUX_DEFAULT = dictId(map, "aux_default");
        Constants.dictionary().AUX_DICTIONARY = dictId(map, "aux_dictionary");
        Constants.dictionary().AUX_NUMERIC = dictId(map, "aux_numeric");
        Constants.dictionary().AUX_TIME = dictId(map, "aux_time");
        Constants.dictionary().CHART_TYPE_DYNAMIC = dictId(map, "chart_type_dynamic");
        Constants.dictionary().CHART_TYPE_FIXED = dictId(map, "chart_type_fixed");
        Constants.dictionary().INSTRUMENT_LOG_COMPLETED = dictId(map, "instrument_log_completed");
        Constants.dictionary().INSTRUMENT_LOG_PENDING = dictId(map, "instrument_log_pending");
        Constants.dictionary().LOG_LEVEL_ERROR = dictId(map, "log_level_error");
        Constants.dictionary().LOG_LEVEL_INFO = dictId(map, "log_level_info");
        Constants.dictionary().LOG_TYPE_DATA_TRANSMISSION = dictId(map, "log_type_data_transmission");
        Constants.dictionary().ORDER_RECURRENCE_UNIT_DAYS = dictId(map, "order_recurrence_unit_days");
        Constants.dictionary().ORDER_RECURRENCE_UNIT_MONTHS = dictId(map, "order_recurrence_unit_months");
        Constants.dictionary().ORDER_RECURRENCE_UNIT_YEARS = dictId(map, "order_recurrence_unit_years");
        Constants.dictionary().ORDER_STATUS_BACK_ORDERED = dictId(map, "order_status_back_ordered");
        Constants.dictionary().ORDER_STATUS_PENDING = dictId(map, "order_status_pending");
        Constants.dictionary().ORDER_STATUS_PROCESSED = dictId(map, "order_status_processed");
        Constants.dictionary().ORG_BILL_TO = dictId(map, "org_bill_to");
        Constants.dictionary().ORG_FINALREP_FAX_NUMBER = dictId(map, "org_finalrep_fax_number");
        Constants.dictionary().ORG_NO_FINALREPORT = dictId(map, "org_no_finalreport");
        Constants.dictionary().ORG_REPORT_TO = dictId(map, "org_report_to");
        Constants.dictionary().ORG_SECOND_REPORT_TO = dictId(map, "org_second_report_to");
        Constants.dictionary().POS_DUPLICATE = dictId(map, "pos_duplicate");
        Constants.dictionary().POS_FIXED = dictId(map, "pos_fixed");
        Constants.dictionary().POS_FIXED_ALWAYS = dictId(map, "pos_fixed_always");
        Constants.dictionary().PT_SAMPLE = dictId(map, "pt_sample");
        Constants.dictionary().QAEVENT_INTERNAL = dictId(map, "qaevent_internal");
        Constants.dictionary().QAEVENT_OVERRIDE = dictId(map, "qaevent_override");
        Constants.dictionary().QAEVENT_WARNING = dictId(map, "qaevent_warning");
        Constants.dictionary().QC_BLANK = dictId(map, "qc_blank");
        Constants.dictionary().QC_DUPLICATE = dictId(map, "qc_duplicate");
        Constants.dictionary().QC_SPIKE = dictId(map, "qc_spike");
        Constants.dictionary().RECEIVABLE_REPORTTO_EMAIL = dictId(map, "receivable_reportto_email");
        Constants.dictionary().RELEASED_REPORTTO_EMAIL = dictId(map, "released_reportto_email");
        Constants.dictionary().ROUND_INT = dictId(map, "round_int");
        Constants.dictionary().ROUND_INT_SIG_FIG = dictId(map, "round_int_sig_fig");
        Constants.dictionary().ROUND_INT_SIG_FIG_NOE = dictId(map, "round_int_sig_fig_noe");
        Constants.dictionary().ROUND_SIG_FIG = dictId(map, "round_sig_fig");
        Constants.dictionary().ROUND_SIG_FIG_NOE = dictId(map, "round_sig_fig_noe");
        Constants.dictionary().SAMPLE_COMPLETED = dictId(map, "sample_completed");
        Constants.dictionary().SAMPLE_ERROR = dictId(map, "sample_error");
        Constants.dictionary().SAMPLE_LOGGED_IN = dictId(map, "sample_logged_in");
        Constants.dictionary().SAMPLE_NOT_VERIFIED = dictId(map, "sample_not_verified");
        Constants.dictionary().SAMPLE_RELEASED = dictId(map, "sample_released");
        Constants.dictionary().TEST_ANALYTE_SUPLMTL = dictId(map, "test_analyte_suplmtl");
        Constants.dictionary().TEST_RES_TYPE_ALPHA_LOWER = dictId(map, "test_res_type_alpha_lower");
        Constants.dictionary().TEST_RES_TYPE_ALPHA_MIXED = dictId(map, "test_res_type_alpha_mixed");
        Constants.dictionary().TEST_RES_TYPE_ALPHA_UPPER = dictId(map, "test_res_type_alpha_upper");
        Constants.dictionary().TEST_RES_TYPE_DATE = dictId(map, "test_res_type_date");
        Constants.dictionary().TEST_RES_TYPE_DATE_TIME = dictId(map, "test_res_type_date_time");
        Constants.dictionary().TEST_RES_TYPE_DEFAULT = dictId(map, "test_res_type_default");
        Constants.dictionary().TEST_RES_TYPE_DICTIONARY = dictId(map, "test_res_type_dictionary");
        Constants.dictionary().TEST_RES_TYPE_NUMERIC = dictId(map, "test_res_type_numeric");
        Constants.dictionary().TEST_RES_TYPE_TIME = dictId(map, "test_res_type_time");
        Constants.dictionary().TEST_RES_TYPE_TITER = dictId(map, "test_res_type_titer");
        Constants.dictionary().TEST_SECTION_DEFAULT = dictId(map, "test_section_default");
        Constants.dictionary().TEST_SECTION_MATCH = dictId(map, "test_section_match");
        Constants.dictionary().TURNAROUND_DAILY = dictId(map, "turnaround_daily");
        Constants.dictionary().TURNAROUND_MONTHLY = dictId(map, "turnaround_monthly");
        Constants.dictionary().TURNAROUND_WEEKLY = dictId(map, "turnaround_weekly");
        Constants.dictionary().WORKSHEET_COMPLETE = dictId(map, "worksheet_complete");
        Constants.dictionary().WORKSHEET_FAILED = dictId(map, "worksheet_failed");
        Constants.dictionary().WORKSHEET_VOID = dictId(map, "worksheet_void");
        Constants.dictionary().WORKSHEET_WORKING = dictId(map, "worksheet_working");
        loaded = true;
    }

    /**
     * Returns the list of constants class
     */
    public Constants getConstants() throws Exception {
        if ( !loaded || errors)
            throw new InconsistencyException("Could not initialize constants in ApplicationBean");
        return constants;
    }

    /*
     * fetch by system name
     */
    private Integer dictId(HashMap<String,Integer> map, String systemName) {
        Integer id;
        
        id = map.get(systemName);
        if (id == null)
            errors = true;

        return id;
    }
}
