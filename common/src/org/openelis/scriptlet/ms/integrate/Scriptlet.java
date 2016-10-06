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
package org.openelis.scriptlet.ms.integrate;

import static org.openelis.scriptlet.ms.Constants.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.logging.Level;

import org.openelis.constants.Messages;
import org.openelis.domain.AnalysisQaEventViewDO;
import org.openelis.domain.AnalysisViewDO;
import org.openelis.domain.AnalyteParameterViewDO;
import org.openelis.domain.Constants;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.QaEventDO;
import org.openelis.domain.ResultViewDO;
import org.openelis.manager.AnalyteParameterManager1;
import org.openelis.manager.SampleManager1;
import org.openelis.manager.TestManager;
import org.openelis.meta.SampleMeta;
import org.openelis.scriptlet.SampleSO;
import org.openelis.scriptlet.SampleSO.Action_Before;
import org.openelis.scriptlet.ms.Constants.Interpretation;
import org.openelis.scriptlet.ms.ScriptletProxy;
import org.openelis.scriptlet.ms.Util;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.Datetime;
import org.openelis.ui.common.FormErrorCaution;
import org.openelis.ui.common.FormErrorException;
import org.openelis.ui.common.NotFoundException;
import org.openelis.ui.common.data.QueryData;
import org.openelis.ui.scriptlet.ScriptletInt;
import org.openelis.ui.scriptlet.ScriptletObject.Status;
import org.openelis.utilcommon.ResultFormatter;
import org.openelis.utilcommon.ResultHelper;

import com.google.gwt.i18n.shared.DateTimeFormat;
import com.google.gwt.i18n.shared.DefaultDateTimeFormatInfo;

/**
 * The scriptlet for "ms integrate (Maternal Integrated Screen)" test. It uses a
 * compute engine to calculate the MoMs for various tests such as AFP and risks
 * and interpretations for various disorders such as Downs. The computations are
 * done based on patient information and results entered by the analyst. If the
 * results are overridden, the MoMs, risks and interpretations are blanked. The
 * computations are done as soon as the user changes any of the values or on
 * completing/releasing the analysis.
 */
public class Scriptlet implements ScriptletInt<SampleSO> {

    private ScriptletProxy                                 proxy;

    private Integer                                        analysisId;

    private Compute                                        compute;

    private ArrayList<String>                              changes;

    private HashMap<String, HashMap<String, ResultViewDO>> resultMap;

    private AnalyteParameterManager1                       paramManager;

    private SampleManagerComparator                        comparator;

    private static DateTimeFormat                          dateFormatter, dateTimeFormatter;

    private static boolean                                 initialized;

    private static Integer                                 YES, NO, UNIT_MM, UNIT_POUNDS,
                    UNIT_YEARS, INT_POSITIVE, INT_NEGATIVE, INT_UNKNOWN, ACTION_US_AMN,
                    ACTION_US_DIAG, ACTION_NO_ACTION, ACTION_RISK_NOT_CALC;

    private static String                                  YES_STR, RACE_BLACK_STR;

    private static final String                            INTEG_TEST_NAME = "ms 1st integ",
                    METHOD_NAME = "eia";

    private enum Risk {
        NTD, DOWNS, T18
    }

    public Scriptlet(ScriptletProxy proxy, Integer analysisId) {
        this.proxy = proxy;
        this.analysisId = analysisId;

        proxy.log(Level.FINE, "Initializing MSIntegrateScriptlet1", null);
        try {
            if ( !initialized) {
                initialized = true;
                YES = proxy.getDictionaryBySystemName("yes").getId();
                NO = proxy.getDictionaryBySystemName("no").getId();
                UNIT_MM = proxy.getDictionaryBySystemName("millimeters").getId();
                UNIT_POUNDS = proxy.getDictionaryBySystemName("pounds_local").getId();
                UNIT_YEARS = proxy.getDictionaryBySystemName("years_local").getId();
                INT_POSITIVE = proxy.getDictionaryBySystemName("positive").getId();
                INT_NEGATIVE = proxy.getDictionaryBySystemName("negative").getId();
                INT_UNKNOWN = proxy.getDictionaryBySystemName("unknown").getId();
                ACTION_US_AMN = proxy.getDictionaryBySystemName("ms_rec_action_us_amn").getId();
                ACTION_US_DIAG = proxy.getDictionaryBySystemName("ms_rec_action_us_diag").getId();
                ACTION_NO_ACTION = proxy.getDictionaryBySystemName("ms_rec_action_no_action")
                                        .getId();
                ACTION_RISK_NOT_CALC = proxy.getDictionaryBySystemName("ms_rec_action_risk_not_calc")
                                            .getId();
                YES_STR = YES.toString();
                RACE_BLACK_STR = proxy.getDictionaryBySystemName("race_b").getId().toString();

                /*
                 * the formatters that convert from string to date or vice-versa
                 */
                dateFormatter = new DateTimeFormat(Messages.get().datePattern(),
                                                   new DefaultDateTimeFormatInfo()) {
                };
                dateTimeFormatter = new DateTimeFormat(Messages.get().dateTimePattern(),
                                                       new DefaultDateTimeFormatInfo()) {
                };
            }
            proxy.log(Level.FINE, "Initialized MSIntegrateScriptlet1", null);
        } catch (Exception e) {
            initialized = false;
            proxy.log(Level.SEVERE, "Failed to initialize dictionary constants", e);
        }
    }

    @Override
    public SampleSO run(SampleSO data) {
        Integer accession;
        SampleManager1 sm;
        AnalysisViewDO ana;
        ResultViewDO res;

        proxy.log(Level.FINE, "In MSIntegrateScriptlet1.run", null);

        changes = data.getChanges();
        sm = data.getManager();
        ana = (AnalysisViewDO)sm.getObject(Constants.uid().getAnalysis(analysisId));
        if (ana == null || Constants.dictionary().ANALYSIS_RELEASED.equals(ana.getStatusId()) ||
            Constants.dictionary().ANALYSIS_CANCELLED.equals(ana.getStatusId()) ||
            changes.size() == 0)
            return data;

        /*
         * manage result changed, the risks needing to be recomputed, the
         * analysis getting completed or released, a qa event added/removed or
         * collection date, patient's birth date or patient's race changed or
         * provider changed
         */
        if (data.getActionBefore().contains(Action_Before.RESULT)) {
            res = (ResultViewDO)sm.getObject(data.getUid());
            if ( !analysisId.equals(res.getAnalysisId()))
                return data;
        } else if (data.getActionBefore().contains(Action_Before.COMPLETE) ||
                   data.getActionBefore().contains(Action_Before.RELEASE) ||
                   changes.contains(SampleMeta.getAnalysisStartedDate())) {
            ana = (AnalysisViewDO)sm.getObject(data.getUid());
            if ( !analysisId.equals(ana.getId()))
                return data;
        } else if ( !data.getActionBefore().contains(Action_Before.QA) &&
                   !data.getActionBefore().contains(Action_Before.PATIENT) &&
                   !data.getActionBefore().contains(Action_Before.RECOMPUTE) &&
                   !data.getActionBefore().contains(Action_Before.ANALYSIS) &&
                   !changes.contains(SampleMeta.getCollectionDate()) &&
                   !changes.contains(SampleMeta.getReceivedDate()) &&
                   !changes.contains(SampleMeta.getClinicalPatientBirthDate()) &&
                   !changes.contains(SampleMeta.getClinicalPatientRaceId()) &&
                   !changes.contains(SampleMeta.getClinicalProviderLastName())) {
            return data;
        }

        /*
         * patient must be present for doing computations; it may not be present
         * if, for example, this test is added to a PT sample
         */
        if (sm.getSampleClinical() == null || sm.getSampleClinical().getPatientId() == null) {
            accession = sm.getSample().getAccessionNumber();
            /*
             * for display
             */
            if (accession == null)
                accession = 0;
            data.setStatus(Status.FAILED);
            data.addException(new FormErrorException(Messages.get()
                                                             .analysis_patientRequiredForComputeException(accession,
                                                                                                          ana.getTestName(),
                                                                                                          ana.getMethodName())));
            return data;
        }

        setRisks(data, ana);

        return data;
    }

    /**
     * Initializes the map containing the ResultViewDOs for various analytes;
     * calculates/copies result values based on sample and patient fields such
     * as birth date or race; sets values in the compute engine from results;
     * sets the values computed by the engine in results and formats them
     */
    private void setRisks(SampleSO data, AnalysisViewDO ana) {
        Integer accession;
        Exception lastEx;

        proxy.log(Level.FINE, "In MSIntegrateScriptlet1.setRisks", null);
        try {
            initializeResultMap(data, ana);
            setBirthDate(data, ana);
            setMaternalWeight(data, ana);
            setRaceBlack(data, ana);
            setFirstTrimesterValues(data, ana);
            setComputeValues(data, ana);
            compute.compute();
            setResultValues(data, ana);
            formatValues(data, ana);
            setUnits(data, ana);
            addRemoveQaEvents(data, ana);
            /*
             * if the compute engine encountered an error, make it available to
             * be shown to the user; add the accession number to the error
             * message because the engine doesn't know that information
             */
            lastEx = compute.getLastException();
            if (lastEx != null) {
                accession = data.getManager().getSample().getAccessionNumber();
                /*
                 * for display
                 */
                if (accession == null)
                    accession = 0;
                data.setStatus(Status.FAILED);
                data.addException(new FormErrorException(DataBaseUtil.concatWithSeparator(Messages.get()
                                                                                                  .sample_accessionAnalysisPrefix(accession,
                                                                                                                                  ana.getTestName(),
                                                                                                                                  ana.getMethodName()),
                                                                                          " ",
                                                                                          lastEx.getMessage())));
            }
        } catch (Exception e) {
            data.setStatus(Status.FAILED);
            data.addException(e);
        }
    }

    /**
     * Initializes a hash map used to quickly lookup ResultViewDOs for getting
     * or setting their value
     */
    private void initializeResultMap(SampleSO data, AnalysisViewDO ana) {
        proxy.log(Level.FINE, "Creating hashmap of results", null);
        resultMap = createResultMap(data.getManager(), ana);
    }

    /**
     * Sets the birth date value from the egg donor's age or from patient's
     * birth date
     */
    private void setBirthDate(SampleSO data, AnalysisViewDO ana) throws Exception {
        ResultViewDO res, resAge, resBirth;
        ResultFormatter rf;
        TestManager tm;
        SampleManager1 sm;
        Datetime bd;

        proxy.log(Level.FINE,
                  "Setting birth date from either the egg's age or from patient's birth date",
                  null);
        sm = data.getManager();
        resBirth = getResult("birth");
        tm = (TestManager)data.getCache().get(Constants.uid().getTest(ana.getTestId()));
        rf = tm.getFormatter();
        /*
         * find out if egg's age was changed or egg donor was changed from "Yes"
         * to "No" or vice-versa
         */
        if (data.getActionBefore().contains(Action_Before.RESULT)) {
            res = (ResultViewDO)sm.getObject(data.getUid());
            if ("eggs_age".equals(res.getAnalyteExternalId()) ||
                "egg_donor".equals(res.getAnalyteExternalId())) {
                /*
                 * if it's an egg donor, compute and set birth date from egg's
                 * age; otherwise wipe egg's age; set birth date value as either
                 * the computed value or the patient's birth date
                 */
                resAge = getResult("eggs_age");
                if (YES_STR.equals(getValue("egg_donor"))) {
                    bd = Util.getBirthDate(getDoubleValue(resAge, sm, ana, false), proxy, data, ana);
                } else {
                    bd = null;
                    setValue(resAge, null, rf, false, data, ana);
                }
                setValue(resBirth, bd, rf, false, data, ana);
            }
        }

        if (resBirth.getValue() == null &&
            sm.getSampleClinical().getPatient().getBirthDate() != null) {
            bd = sm.getSampleClinical().getPatient().getBirthDate();
            setValue(resBirth, bd, rf, false, data, ana);
        }
        /*
         * if the result value for birth date doesn't match the patient's birth
         * date, add a caution to inform the user
         */
        if (DataBaseUtil.isDifferentYD(getDateValue(resBirth), sm.getSampleClinical()
                                                                 .getPatient()
                                                                 .getBirthDate()))
            addResultNotMatchCaution(resBirth.getAnalyte(), data, ana);
    }

    /**
     * Validates the maternal weight entered and converts it to lbs from kgs if
     * it contains "k"
     */
    private void setMaternalWeight(SampleSO data, AnalysisViewDO ana) throws Exception {
        double multiplier;
        String value;
        Double weight;
        ResultViewDO res;
        TestManager tm;

        /*
         * do something only if a result was changed, that result was maternal
         * weight and its value is not null
         */
        if (data.getActionBefore().contains(Action_Before.RESULT)) {
            res = (ResultViewDO)data.getManager().getObject(data.getUid());
            value = res.getValue();
            if ("mat_weight_2nd_tri".equals(res.getAnalyteExternalId()) && value != null) {
                proxy.log(Level.FINE, "Formatting and resetting maternal weight", null);
                /*
                 * convert from kgs to lbs if weight contains "k" or "kg"
                 */
                tm = (TestManager)data.getCache().get(Constants.uid().getTest(ana.getTestId()));
                try {
                    multiplier = 1.0;
                    if (value.endsWith("k")) {
                        value = value.substring(0, value.length() - 1);
                        multiplier = 2.204;
                    } else if (value.endsWith("kg")) {
                        value = value.substring(0, value.length() - 2);
                        multiplier = 2.204;
                    }
                    weight = new Double(value) * multiplier;
                    setValue(res, weight, tm.getFormatter(), false, data, ana);
                } catch (NumberFormatException e) {
                    throw getValueInvalidException(res, data.getManager(), ana);
                }
            }
        }
    }

    /**
     * Sets the value of "Maternal Race Black" to "Yes" if the patient's race is
     * black even if it's mixed race; otherwise sets the value to "No"
     */
    private void setRaceBlack(SampleSO data, AnalysisViewDO ana) throws Exception {
        boolean raceblack;
        Integer value, raceId;
        DictionaryDO dict;
        ResultViewDO res;
        TestManager tm;

        /*
         * race is black if the system name contains a "b" e.g. "race_b" or
         * "race_wb", otherwise not; change 'maternal race black' if either the
         * patient's race was changed or if the analyte's value is not set
         */
        raceId = data.getManager().getSampleClinical().getPatient().getRaceId();
        raceblack = false;
        res = getResult("mat_race_black");
        if (raceId != null) {
            dict = proxy.getDictionaryById(raceId);
            raceblack = dict.getSystemName() != null && dict.getSystemName().contains("b");
        }
        if (changes.contains(SampleMeta.getClinicalPatientRaceId()) && res.getValue() == null) {
            proxy.log(Level.FINE, "Setting 'maternal race black' from the sample", null);
            value = raceblack ? YES : NO;
            tm = (TestManager)data.getCache().get(Constants.uid().getTest(ana.getTestId()));
            setValue(res, value, tm.getFormatter(), true, data, ana);
        }
        /*
         * if the result value for 'maternal race black' isn't consistent with
         * the patient, add a caution to inform the user
         */
        value = getIntegerValue(res, data.getManager(), ana);
        if ( ( !raceblack && YES.equals(value)) || (raceblack && !YES.equals(value)))
            addResultNotMatchCaution(res.getAnalyte(), data, ana);
    }

    /**
     * Finds the most recent sample which has the same patient as the passed
     * sample and a released 1st trimester integrated analysis; uses the 1st
     * trimester accession number, if present, or the passed sample's patient;
     * copies values such as NT result and MoM from that sample to the passed
     * analysis' results
     */
    private void setFirstTrimesterValues(SampleSO data, AnalysisViewDO ana) throws Exception {
        int i, j;
        Integer accession, ftAccession;
        AnalysisViewDO tmpAna, ftAna;
        ResultViewDO res;
        SampleManager1 sm, ftSm;
        ArrayList<SampleManager1> ftSms;

        /*
         * fetch the 1st trimester sample(s) using the 1st trimester accession
         * number and/or the patient if either has been entered; do this only if
         * the 1st trimester accession number, patient or the received date was
         * changed or if the passed analysis was just added, is getting
         * completed or released or the risks need to be recomputed
         */
        sm = data.getManager();
        ftSms = null;
        try {
            if (data.getActionBefore().contains(Action_Before.RESULT)) {
                res = (ResultViewDO)sm.getObject(data.getUid());
                if ("accession_1st_tri".equals(res.getAnalyteExternalId())) {
                    ftAccession = getIntegerValue(res, sm, ana);
                    if (ftAccession != null &&
                        ftAccession.equals(data.getManager().getSample().getAccessionNumber())) {
                        data.setStatus(Status.FAILED);
                        data.addException(new FormErrorException(Messages.get()
                                                                         .analysis_firstSecTriNotOnSameSampleException(ftAccession,
                                                                                                                       ana.getTestName(),
                                                                                                                       ana.getMethodName())));
                    }
                    ftSms = fetchFirstTriSamples(ftAccession, sm);
                } else {
                    return;
                }
            } else if (data.getActionBefore().contains(Action_Before.PATIENT) ||
                       data.getActionBefore().contains(Action_Before.ANALYSIS) ||
                       data.getActionBefore().contains(Action_Before.COMPLETE) ||
                       data.getActionBefore().contains(Action_Before.RELEASE) ||
                       data.getActionBefore().contains(Action_Before.RECOMPUTE) ||
                       changes.contains(SampleMeta.getReceivedDate())) {
                ftAccession = getIntegerValue(getResult("accession_1st_tri"), sm, ana);
                ftSms = fetchFirstTriSamples(ftAccession, sm);
            } else {
                return;
            }
        } catch (NotFoundException e) {
            /*
             * a 1st trimester sample was not found; if this exception is not
             * handled here, an error with the message "null" is shown, which is
             * not very helpful; a more descriptive error for missing 1st
             * trimester values will be added later
             */
        }

        /*
         * find the released and not overridden 1st trimester integrated
         * analysis on the most recent of the fetched samples; the samples are
         * in ascending order of received date, so to find the most recent,
         * start from the end of the list; add an error if such an analysis was
         * not found; set the 1st trimester values in the passed analysis from
         * the 1st trimester sample and analysis
         */
        ftSm = null;
        ftAna = null;
        if (ftSms != null) {
            for (i = ftSms.size() - 1; i >= 0; i-- ) {
                ftSm = ftSms.get(i);
                if ( !ftSm.qaEvent.hasType(Constants.dictionary().QAEVENT_OVERRIDE)) {
                    for (j = 0; j < ftSm.analysis.count(); j++ ) {
                        tmpAna = ftSm.analysis.get(j);
                        if (INTEG_TEST_NAME.equals(tmpAna.getTestName()) &&
                            METHOD_NAME.equals(tmpAna.getMethodName()) &&
                            Constants.dictionary().ANALYSIS_RELEASED.equals(tmpAna.getStatusId()) &&
                            !ftSm.qaEvent.hasType(tmpAna, Constants.dictionary().QAEVENT_OVERRIDE)) {
                            ftAna = tmpAna;
                            break;
                        }
                    }
                    if (ftAna != null)
                        break;
                }
            }
        }
        if (ftAna == null) {
            /*
             * for display
             */
            accession = sm.getSample().getAccessionNumber();
            if (accession == null)
                accession = 0;
            data.setStatus(Status.FAILED);
            data.addException(new FormErrorException(Messages.get()
                                                             .analysis_relatedFirstTriNotFoundException(accession,
                                                                                                        ana.getTestName(),
                                                                                                        ana.getMethodName(),
                                                                                                        INTEG_TEST_NAME,
                                                                                                        METHOD_NAME)));
        }
        setFirstTrimesterValues(ftSm, ftAna, data, ana);
    }

    /**
     * Set values in the compute engine from the passed sample, analysis,
     * results of specific analytes and analyte parameters
     */
    private void setComputeValues(SampleSO data, AnalysisViewDO ana) throws Exception {
        SampleManager1 sm;
        HashMap<Integer, AnalyteParameterViewDO> paramMap;

        proxy.log(Level.FINE, "Setting values in the compute engine", null);
        /*
         * set sample/analysis level info
         */
        sm = data.getManager();
        compute = new Compute();
        compute.setIsOverridden(sm.qaEvent.hasType(Constants.dictionary().QAEVENT_OVERRIDE) ||
                                sm.qaEvent.hasType(ana, Constants.dictionary().QAEVENT_OVERRIDE));
        compute.setEnteredDate(sm.getSample().getEnteredDate());
        compute.setCollectionDate(sm.getSample().getCollectionDate());
        /*
         * set patient info
         */
        compute.setBirthDate(getDateValue(getValue("birth")));
        compute.setWeight(getDoubleValue(getResult("mat_weight_2nd_tri"), sm, ana, false));
        compute.setIsRaceBlack(RACE_BLACK_STR.equals(getValue("mat_race_black")));
        compute.setIsDiabetic(YES_STR.equals(getValue("insulin_dep_diabetic")));
        compute.setHasHistoryOfNTD(YES_STR.equals(getValue("history_ntd")));
        compute.setNumFetus(getIntegerValue(getResult("number_of_fetuses"), sm, ana));
        /*
         * set 1st trimester values
         */
        compute.setFirstTriGestAge(getFirstTriGestAge(sm, ana));
        compute.setFirstTriUltrasoundDate(getDateValue(getValue("ultrasound")));
        compute.setFirstTriCRL(getIntegerValue(getResult("crl"), sm, ana));
        compute.getNT().setResult(getDoubleValue(getResult("nt_measurement"), sm, ana, false));
        compute.getNT().setMom(getDoubleValue(getResult("nt_mom"), sm, ana, false));
        compute.getPAPPA().setMom(getDoubleValue(getResult("papp_a_mom"), sm, ana, false));

        if (paramManager == null) {
            try {
                paramManager = proxy.fetchParameters(ana.getTestId(), Constants.table().TEST);
            } catch (NotFoundException e) {
                /*
                 * the analyte parameters have not been defined; if this
                 * exception is not handled here, an error with the message
                 * "null" is shown, which is not very helpful; a more
                 * descriptive error for missing p-values will be added later if
                 * some computation can't be done without them
                 */
            }
        }
        paramMap = Util.getParameters(paramManager, data, ana);
        /*
         * set p-values and result from the instrument for tests e.g. NT
         */
        setTestValues(compute.getAFP(), "afp_mom", paramMap, sm, ana);
        setTestValues(compute.getEST(), "estriol_mom", paramMap, sm, ana);
        setTestValues(compute.getHCG(), "hcg_mom", paramMap, sm, ana);
        setTestValues(compute.getInhibin(), "inhibin_mom", paramMap, sm, ana);
    }

    /**
     * Sets values such as MoMs, risks, cutoffs, interpretations etc. in the
     * results from the compute engine
     */
    private void setResultValues(SampleSO data, AnalysisViewDO ana) throws Exception {
        Object value;
        TestManager tm;
        ResultFormatter rf;

        proxy.log(Level.FINE, "Setting values in the results from the compute engine", null);
        tm = (TestManager)data.getCache().get(Constants.uid().getTest(ana.getTestId()));
        rf = tm.getFormatter();
        setValue(getResult("mat_age_at_delivery"), compute.getMothersDueAge(), rf, false, data, ana);
        /*
         * MoMs
         */
        setValue(getResult("afp_mom"), compute.getAFP().getMom(), rf, false, data, ana);
        setValue(getResult("estriol_mom"), compute.getEST().getMom(), rf, false, data, ana);
        setValue(getResult("hcg_mom"), compute.getHCG().getMom(), rf, false, data, ana);
        setValue(getResult("inhibin_mom"), compute.getInhibin().getMom(), rf, false, data, ana);
        /*
         * risks, screening cutoffs(limits), interpretations, recommended
         * actions; ntd
         */
        value = null;
        if (compute.getLimitNTD() != null)
            value = DataBaseUtil.concatWithSeparator("NTD >=", " ", compute.getLimitNTD());
        setValue(getResult("afp_mom", "screen_cutoff"), value, rf, false, data, ana);
        setInterpretation("afp_mom", compute.getInterpretationNTD(), rf, data, ana);
        setRecommendedAction("afp_mom", compute.getInterpretationNTD(), Risk.NTD, rf, data, ana);
        /*
         * downs
         */
        setRisk("downs_risk", compute.getRiskSignDowns(), compute.getRiskDowns(), rf, data, ana);
        setScreeningCutoff("downs_risk", compute.getLimitDowns(), rf, data, ana);
        setInterpretation("downs_risk", compute.getInterpretationDowns(), rf, data, ana);
        setRecommendedAction("downs_risk",
                             compute.getInterpretationDowns(),
                             Risk.DOWNS,
                             rf,
                             data,
                             ana);
        /*
         * t18
         */
        setRisk("trisomy_18_risk",
                compute.getRiskSignTrisomy18(),
                compute.getRiskTrisomy18(),
                rf,
                data,
                ana);
        setScreeningCutoff("trisomy_18_risk", compute.getLimitTrisomy18(), rf, data, ana);
        setInterpretation("trisomy_18_risk", compute.getInterpretationTrisomy18(), rf, data, ana);
        setRecommendedAction("trisomy_18_risk",
                             compute.getInterpretationTrisomy18(),
                             Risk.T18,
                             rf,
                             data,
                             ana);
        /*
         * downs by age
         */
        setValue(getResult("downs_age_risk"), compute.getRiskDownsByAge(), rf, false, data, ana);
        /*
         * gestational age in days and weeks and days
         */
        setValue(getResult("gest_age_2nd_tri"),
                 Util.getWeeksAndDays(compute.getGestAge()),
                 rf,
                 false,
                 data,
                 ana);
    }

    /**
     * Formats "double" values such as egg's age, MoMs and intrument results
     * etc. to have one or two fractional digits
     */
    private void formatValues(SampleSO data, AnalysisViewDO ana) throws Exception {
        TestManager tm;

        proxy.log(Level.FINE, "Formatting result values", null);
        tm = (TestManager)data.getCache().get(Constants.uid().getTest(ana.getTestId()));
        /*
         * egg's age and maternal weight have one fractional digit
         */
        formatValue(getResult("eggs_age"), 1, tm.getFormatter(), false, data, ana);
        formatValue(getResult("mat_weight_1st_tri"), 1, tm.getFormatter(), false, data, ana);
        formatValue(getResult("mat_weight_2nd_tri"), 1, tm.getFormatter(), false, data, ana);
        /*
         * test MoMs and instrument results have two fractional digit
         */
        formatValue(getResult("afp_mom"), 2, tm.getFormatter(), false, data, ana);
        formatValue(getResult("afp_mom", "result"), 2, tm.getFormatter(), true, data, ana);
        formatValue(getResult("estriol_mom"), 2, tm.getFormatter(), false, data, ana);
        formatValue(getResult("estriol_mom", "result"), 2, tm.getFormatter(), true, data, ana);
        formatValue(getResult("hcg_mom"), 2, tm.getFormatter(), false, data, ana);
        formatValue(getResult("hcg_mom", "result"), 2, tm.getFormatter(), true, data, ana);
        formatValue(getResult("inhibin_mom"), 2, tm.getFormatter(), false, data, ana);
        formatValue(getResult("inhibin_mom", "result"), 2, tm.getFormatter(), true, data, ana);
        formatValue(getResult("nt_mom"), 2, tm.getFormatter(), false, data, ana);
        formatValue(getResult("nt_mom"), 2, tm.getFormatter(), true, data, ana);
        formatValue(getResult("papp_a_mom"), 2, tm.getFormatter(), false, data, ana);
    }

    /**
     * Sets the values in the "Unit" column for various row analytes such as
     * "Maternal Weight", "CRL" etc; sets the unit as null if the row analyte's
     * value is null
     */
    private void setUnits(SampleSO data, AnalysisViewDO ana) throws Exception {
        ResultFormatter rf;
        TestManager tm;

        tm = (TestManager)data.getCache().get(Constants.uid().getTest(ana.getTestId()));
        rf = tm.getFormatter();
        setUnit("eggs_age", UNIT_YEARS, rf, data, ana);
        setUnit("mat_age_at_delivery", UNIT_YEARS, rf, data, ana);
        setUnit("mat_weight_2nd_tri", UNIT_POUNDS, rf, data, ana);
        setUnit("mat_weight_1st_tri", UNIT_POUNDS, rf, data, ana);
        setUnit("crl", UNIT_MM, rf, data, ana);
        setUnit("nt_measurement", UNIT_MM, rf, data, ana);
    }

    /**
     * Adds a QA event to the analysis if the MoM for EST is below a certain
     * limit or if age at delivery < 15 years or if there are multiple fetuses;
     * removes a QA event if the condition for which it was added is no longer
     * true
     */
    private void addRemoveQaEvents(SampleSO data, AnalysisViewDO ana) throws Exception {
        Integer numFetus;
        Double age, mom;

        mom = getDoubleValue(getValue("estriol_mom"), false);
        if (mom != null && mom < EST_LIMIT)
            addQAEvent(QA_EST_MOM, data, ana);
        else
            removeQAEvent(QA_EST_MOM, data, ana);

        age = getDoubleValue(getValue("mat_age_at_delivery"), false);
        if (age != null && age < 15.0)
            addQAEvent(QA_LESS_THAN_15, data, ana);
        else
            removeQAEvent(QA_LESS_THAN_15, data, ana);

        numFetus = getIntegerValue(getValue("number_of_fetuses"));
        if (numFetus != null && numFetus > 1)
            addQAEvent(QA_TWINS, data, ana);
        else
            removeQAEvent(QA_TWINS, data, ana);
    }

    /**
     * Creates a hash map where the key is the external id of a row analyte and
     * the value is another hash map containing the ResultViewDOs for all
     * analytes in the row; the key in the second map is the external id of a
     * row or column analyte and the value is the ResultVIewDO for that analyte
     */
    private HashMap<String, HashMap<String, ResultViewDO>> createResultMap(SampleManager1 sm,
                                                                           AnalysisViewDO ana) {
        int i, j;
        ResultViewDO res;
        HashMap<String, ResultViewDO> map;
        HashMap<String, HashMap<String, ResultViewDO>> resultMap;

        if (sm == null || ana == null)
            return null;

        resultMap = new HashMap<String, HashMap<String, ResultViewDO>>();
        map = null;
        for (i = 0; i < sm.result.count(ana); i++ ) {
            for (j = 0; j < sm.result.count(ana, i); j++ ) {
                res = sm.result.get(ana, i, j);
                if ("N".equals(res.getIsColumn())) {
                    map = resultMap.get(res.getAnalyteExternalId());
                    if (map == null) {
                        map = new HashMap<String, ResultViewDO>();
                        resultMap.put(res.getAnalyteExternalId(), map);
                    }
                }
                map.put(res.getAnalyteExternalId(), res);
            }
        }

        return resultMap;
    }

    /**
     * Returns the ResultViewDO for the row analyte with the passed external id
     */
    private ResultViewDO getResult(String rowExtId) {
        return getResult(rowExtId, rowExtId);
    }

    /**
     * Returns from the passed map, the ResultViewDO for the row analyte with
     * the passed external id
     */
    private ResultViewDO getResult(String rowExtId,
                                   HashMap<String, HashMap<String, ResultViewDO>> resultMap) {
        return getResult(rowExtId, rowExtId, resultMap);
    }

    /**
     * Returns the ResultViewDO for a column analyte whose row analyte's
     * external id is the first argument and whose external id is the second
     * argument
     */
    private ResultViewDO getResult(String rowExtId, String colExtId) {
        return getResult(rowExtId, colExtId, resultMap);
    }

    /**
     * Returns from the passed map, the ResultViewDO for a column analyte whose
     * row analyte's external id is the first argument and whose external id is
     * the second argument
     */
    private ResultViewDO getResult(String rowExtId, String colExtId,
                                   HashMap<String, HashMap<String, ResultViewDO>> resultMap) {
        HashMap<String, ResultViewDO> map;

        map = resultMap != null ? resultMap.get(rowExtId) : null;
        return map != null ? map.get(colExtId) : null;
    }

    /**
     * Sets the passed value in the passed result; the flag isDict means that
     * the value is a dictionary id and a dictionary record needs to be
     * obtained, because ResultHelper validates dictionary entries and not ids
     */
    private void setValue(ResultViewDO result, Object value, ResultFormatter rf, boolean isDict,
                          SampleSO data, AnalysisViewDO ana) throws Exception {
        String val;

        val = null;
        if (value != null)
            val = isDict ? proxy.getDictionaryById((Integer)value).getEntry() : value.toString();

        if (ResultHelper.formatValue(result, val, ana.getUnitOfMeasureId(), rf)) {
            data.setChanges(result.getAnalyteExternalId());
            data.addChangedUid(Constants.uid().getResult(result.getId()));
            proxy.log(Level.FINE,
                      "Setting the value of " + result.getAnalyte() + " as: " + val,
                      null);
        }
    }

    /**
     * Returns the value for the row analyte whose external id is the passed
     * value
     */
    private String getValue(String rowExtId) {
        ResultViewDO res;

        res = getResult(rowExtId);
        return res != null ? res.getValue() : null;
    }

    /**
     * Adds a caution to inform the user that the value for passed analyte e.g.
     * "Race" doesn't match the patient's value; a caution is used here because
     * it's just a warning to the user; the sample doesn't need to go to error
     * status and update doesn't need to fail
     */
    private void addResultNotMatchCaution(String analyteName, SampleSO data, AnalysisViewDO ana) throws Exception {
        Integer accession;

        /*
         * for display
         */
        accession = data.getManager().getSample().getAccessionNumber();
        if (accession == null)
            accession = 0;
        data.setStatus(Status.FAILED);
        data.addException(new FormErrorCaution(Messages.get()
                                                       .result_resultNotMatchPatientCaution(accession,
                                                                                            ana.getTestName(),
                                                                                            ana.getMethodName(),
                                                                                            analyteName)));
    }

    /**
     * Fetches the 1st trimester sample(s) for the passed sample's patient; the
     * fetched samples must have a released 1st trimester integrated analysis
     * and must have been received before the passed sample; returns the most
     * recent of those samples; returns null if the passed sample doesn't have a
     * patient linked yet
     */
    private ArrayList<SampleManager1> fetchFirstTriSamples(Integer ftAccession, SampleManager1 sm) throws Exception {
        Datetime startDT, endDT;
        QueryData field;
        ArrayList<QueryData> fields;
        ArrayList<SampleManager1> sms;

        if (sm.getSampleClinical().getPatientId() == null)
            return null;

        fields = new ArrayList<QueryData>();

        if (ftAccession != null) {
            field = new QueryData();
            field.setKey(SampleMeta.getAccessionNumber());
            field.setType(QueryData.Type.INTEGER);
            field.setQuery(ftAccession.toString());
            fields.add(field);
        }

        /*
         * make sure that the samples to be fetched were received at the most 90
         * days before the current(passed) sample
         */
        endDT = sm.getSample().getReceivedDate() != null ? sm.getSample().getReceivedDate()
                                                        : sm.getSample().getEnteredDate();
        startDT = endDT.add( -90);

        field = new QueryData();
        field.setKey(SampleMeta.getReceivedDate());
        field.setType(QueryData.Type.DATE);
        field.setQuery(format(dateTimeFormatter, startDT) + ".." + format(dateTimeFormatter, endDT));
        fields.add(field);

        sms = Util.fetchRelatedSamples(INTEG_TEST_NAME,
                                       METHOD_NAME,
                                       sm,
                                       fields,
                                       proxy,
                                       SampleManager1.Load.QA,
                                       SampleManager1.Load.RESULT);
        if (sms != null) {
            /*
             * sort the fetched managers by received date because in the
             * back-end they were sorted by sample id
             */
            if (comparator == null)
                comparator = new SampleManagerComparator();
            Collections.sort(sms, comparator);
        }
        return sms;
    }

    /**
     * Returns the id for the row analyte whose external id is the passed value
     */
    private Integer getAnalyteId(String rowExtId) {
        ResultViewDO res;

        res = getResult(rowExtId);
        return res != null ? res.getAnalyteId() : null;
    }

    /**
     * Returns the passed result's value converted to an Integer; returns null
     * if the value is null; throws an exception if the value contains any
     * non-numeric characters such as "<" or ">"
     */
    private Integer getIntegerValue(ResultViewDO res, SampleManager1 sm, AnalysisViewDO ana) throws Exception {
        if (res != null) {
            try {
                return getIntegerValue(res.getValue());
            } catch (NumberFormatException e) {
                throw getValueInvalidException(res, sm, ana);
            }
        }
        return null;
    }

    /**
     * Returns the passed value converted to an Integer; returns null if the
     * value is null; throws an exception if the value contains any non-numeric
     * characters such as "<" or ">"
     */
    private Integer getIntegerValue(String value) throws Exception {
        return value != null ? new Integer(value) : null;
    }

    /**
     * Returns the passed results's value converted to a Datetime; returns null
     * if the result or value is null
     */
    private Datetime getDateValue(ResultViewDO res) throws Exception {
        return res != null ? getDateValue(res.getValue()) : null;
    }

    /**
     * Returns the passed value converted to a Datetime; returns null if the
     * value is null
     */
    private Datetime getDateValue(String value) throws Exception {
        if (DataBaseUtil.isEmpty(value))
            return null;
        return Datetime.getInstance(Datetime.YEAR, Datetime.DAY, dateFormatter.parseStrict(value));
    }

    /**
     * Returns the passed Datetime's date formatted using the passed formatter
     */
    private String format(DateTimeFormat formatter, Datetime dt) throws Exception {
        return dt != null ? formatter.format(dt.getDate()) : null;
    }

/**
     * Returns the passed result's value converted to a Double; returns null if
     * the value is null; if the boolean flag is true, treats a value with a "<"
     * as valid; otherwise throws an exception if the value contains any non-numeric
     * characters such as "<" or ">"
     */
    private Double getDoubleValue(ResultViewDO res, SampleManager1 sm, AnalysisViewDO ana,
                                  boolean allowLessThan) throws Exception {
        if (res != null) {
            try {
                return getDoubleValue(res.getValue(), allowLessThan);
            } catch (NumberFormatException e) {
                throw getValueInvalidException(res, sm, ana);
            }
        }
        return null;
    }

/**
     * Returns the passed value converted to a Double; returns null if
     * the value is null; if the boolean flag is true, treats a value with a "<"
     * as valid; otherwise throws an exception if the value contains any non-numeric
     * characters such as "<" or ">"
     */
    private Double getDoubleValue(String value, boolean allowLessThan) throws Exception {
        if (value != null) {
            value = allowLessThan ? value.replaceAll("<", "") : value;
            return new Double(value);
        }
        return null;
    }

    /**
     * Returns an exception whose message states that the passed result's value
     * is invalid
     */
    private Exception getValueInvalidException(ResultViewDO res, SampleManager1 sm,
                                               AnalysisViewDO ana) {
        Integer accession;

        accession = sm.getSample().getAccessionNumber();
        /*
         * for display
         */
        if (accession == null)
            accession = 0;
        return new FormErrorException(Messages.get()
                                              .result_valueInvalidException(accession,
                                                                            ana.getTestName(),
                                                                            ana.getMethodName(),
                                                                            res.getAnalyte(),
                                                                            res.getValue()));
    }

    /**
     * Sets the 1st trimester values from the passed sample and analysis; blanks
     * the values if the analysis is null, because a 1st trimester integrated
     * analysis was not found on the passed sample
     */
    private void setFirstTrimesterValues(SampleManager1 ftSm, AnalysisViewDO ftAna, SampleSO data,
                                         AnalysisViewDO ana) throws Exception {
        Integer accession;
        String gestAge;
        Datetime cd, rd;
        SampleManager1 sm;
        ResultViewDO resUltra, resCrl, resNTMom, resNTResult, resSono, resPappaMom, resMatWeight, resGestAge;
        TestManager tm;
        ResultFormatter rf;
        HashMap<String, HashMap<String, ResultViewDO>> ftResultMap;

        /*
         * get the data from the 1st trimester sample
         */
        ftResultMap = createResultMap(ftSm, ftAna);
        accession = ftSm != null ? ftSm.getSample().getAccessionNumber() : null;
        cd = ftSm != null ? ftSm.getSample().getCollectionDate() : null;
        rd = ftSm != null ? ftSm.getSample().getReceivedDate() : null;
        resUltra = getResult("ultrasound", ftResultMap);
        resCrl = getResult("crl", ftResultMap);
        resNTMom = getResult("nt_mom", ftResultMap);
        resNTResult = getResult("nt_measurement", ftResultMap);
        resSono = getResult("sonographer", ftResultMap);
        resPappaMom = getResult("papp_a_mom", ftResultMap);
        resMatWeight = getResult("mat_weight", ftResultMap);
        resGestAge = getResult("gest_age", ftResultMap);

        /*
         * set the values in the current(passed) analysis; if a 1st trimester
         * sample was not found, wipe all the 1si trimester fields accept the
         * accession number; this will allow spotting and correcting any typos
         */
        tm = (TestManager)data.getCache().get(Constants.uid().getTest(ana.getTestId()));
        rf = tm.getFormatter();
        sm = data.getManager();
        if (accession != null)
            setValue(getResult("accession_1st_tri"), accession, rf, false, data, ana);
        setValue(getResult("collected_1st_tri"), format(dateFormatter, cd), rf, false, data, ana);
        setValue(getResult("received_1st_tri"), format(dateFormatter, rd), rf, false, data, ana);
        setValue(getResult("ultrasound"), getDateValue(resUltra), rf, false, data, ana);
        setValue(getResult("crl"), getDoubleValue(resCrl, sm, ana, false), rf, false, data, ana);
        setValue(getResult("nt_measurement"),
                 getDoubleValue(resNTResult, sm, ana, true),
                 rf,
                 false,
                 data,
                 ana);
        setValue(getResult("sonographer"), getIntegerValue(resSono, sm, ana), rf, true, data, ana);
        setValue(getResult("nt_mom"),
                 getDoubleValue(resNTMom, sm, ana, false),
                 rf,
                 false,
                 data,
                 ana);
        setValue(getResult("papp_a_mom"),
                 getDoubleValue(resPappaMom, sm, ana, false),
                 rf,
                 false,
                 data,
                 ana);
        setValue(getResult("mat_weight_1st_tri"),
                 getDoubleValue(resMatWeight, sm, ana, false),
                 rf,
                 false,
                 data,
                 ana);
        gestAge = resGestAge != null ? resGestAge.getValue() : null;
        setValue(getResult("gest_age_1st_tri"), gestAge, rf, false, data, ana);
    }

    /**
     * Returns the total number of days from the first trimester gestational age
     * which is in the format "X days (YwZd)"; here X, Y and Z represent the
     * gestational age in total number of days and weeks and days, e.g. 145, 20
     * and 5 respectively
     */
    private Integer getFirstTriGestAge(SampleManager1 sm, AnalysisViewDO ana) throws Exception {
        ResultViewDO res;
        String value[];

        res = getResult("gest_age_1st_tri");
        if (res.getValue() != null) {
            value = res.getValue().split(" ");
            if (value.length > 0) {
                try {
                    return Integer.valueOf(value[0]);
                } catch (NumberFormatException e) {
                    throw getValueInvalidException(res, sm, ana);
                }
            } else {
                throw getValueInvalidException(res, sm, ana);
            }
        }
        return null;
    }

    /**
     * Sets p-values and result from the intrument for the passed test
     */
    private void setTestValues(Test t, String analyteName,
                               HashMap<Integer, AnalyteParameterViewDO> paramMap,
                               SampleManager1 sm, AnalysisViewDO ana) throws Exception {
        AnalyteParameterViewDO ap;

        ap = paramMap != null ? paramMap.get(getAnalyteId(analyteName)) : null;
        t.setP1(getP1(ap));
        t.setP2(getP2(ap));
        t.setP3(getP3(ap));
        t.setResult(getDoubleValue(getResult(analyteName, "result"), sm, ana, true));
    }

    /**
     * Sets the value in the "Unit" column for a row analyte such as
     * "Maternal Weight", "CRL" etc; sets the unit as null if the row analyte's
     * value is null
     */
    private void setUnit(String externalId, Integer unitId, ResultFormatter rf, SampleSO data,
                         AnalysisViewDO ana) throws Exception {
        Object value;
        ResultViewDO res;

        res = getResult(externalId);
        value = (res != null && res.getValue() != null) ? unitId : null;
        setValue(getResult(externalId, "unit"), value, rf, true, data, ana);
    }

    /**
     * Returns the passed parameter's p1 value; returns null if either the
     * parameter or the value is null
     */
    private Double getP1(AnalyteParameterViewDO ap) {
        return ap == null || ap.getP1() == null ? null : ap.getP1();
    }

    /**
     * Returns the passed parameter's p2 value; returns null if either the
     * parameter or the value is null
     */
    private Double getP2(AnalyteParameterViewDO ap) {
        return ap == null || ap.getP2() == null ? null : ap.getP2();
    }

    /**
     * Returns the passed parameter's p3 value; returns null if either the
     * parameter or the value is null
     */
    private Double getP3(AnalyteParameterViewDO ap) {
        return ap == null || ap.getP3() == null ? null : ap.getP3();
    }

    /**
     * Sets the value of the analyte with the passed external id as the risk
     * created from the passed risk sign and computed risk
     */
    private void setRisk(String externalId, String riskSign, Integer risk, ResultFormatter rf,
                         SampleSO data, AnalysisViewDO ana) throws Exception {
        String value;

        value = risk == null ? null : DataBaseUtil.concatWithSeparator(riskSign, "1:", risk);
        setValue(getResult(externalId), value, rf, false, data, ana);
    }

    /**
     * Sets the value of the analyte with the passed external id as the
     * screening cutoff created from the passed computed limit
     */
    private void setScreeningCutoff(String externalId, Integer limit, ResultFormatter rf,
                                    SampleSO data, AnalysisViewDO ana) throws Exception {
        String value;

        value = limit == null ? null : DataBaseUtil.concat("1:", limit);
        setValue(getResult(externalId, "screen_cutoff"), value, rf, false, data, ana);
    }

    /**
     * Sets the value of the analyte with the passed external id as the
     * interpretation e.g. "Positive", based on the passed computed
     * interpretation e.g. "Presumptive Positive"
     */
    private void setInterpretation(String externalId, Interpretation interp, ResultFormatter rf,
                                   SampleSO data, AnalysisViewDO ana) throws Exception {
        Object value;

        value = null;
        if (interp != null) {
            switch (interp) {
                case NORMAL:
                    value = INT_NEGATIVE;
                    break;
                case PRES_POS:
                    value = INT_POSITIVE;
                    break;
                case UNKNOWN:
                    value = INT_UNKNOWN;
                    break;
            }
        }
        setValue(getResult(externalId, "interpretation"), value, rf, true, data, ana);
    }

    /**
     * Sets the value of the analyte with the passed external id as a
     * recommended action e.g. "Offer amniocentesis"; the action depends on the
     * passed interpretation e.g. "Positive" and a risk e.g. "Downs"
     */
    private void setRecommendedAction(String externalId, Interpretation interp, Risk risk,
                                      ResultFormatter rf, SampleSO data, AnalysisViewDO ana) throws Exception {
        Integer value;

        value = null;
        if (interp != null) {
            switch (interp) {
                case NORMAL:
                    value = ACTION_NO_ACTION;
                    break;
                case PRES_POS:
                    switch (risk) {
                        case NTD:
                            value = ACTION_US_AMN;
                            break;
                        case DOWNS:
                        case T18:
                            value = ACTION_US_DIAG;
                            break;
                    }
                    break;
                case UNKNOWN:
                    value = ACTION_RISK_NOT_CALC;
                    break;
            }
        }

        setValue(getResult(externalId, "rec_action"), value, rf, true, data, ana);
    }

/**
     * Formats the passed result's value to have one or two fractional digits
     * based on the passed precision; the boolean flag specifies whether the 
     * value can have a "<"; if the value has a "<" before formatting, 
     * concatenates "<" to the formatted value 
     */
    private void formatValue(ResultViewDO res, int precision, ResultFormatter rf,
                             boolean allowLessThan, SampleSO data, AnalysisViewDO ana) throws Exception {
        boolean hasLessThan;
        Double val;
        String value;

        value = res.getValue();
        if (value == null)
            return;
        hasLessThan = value.startsWith("<");
        val = getDoubleValue(res, data.getManager(), ana, allowLessThan);
        value = Util.format(val, precision, proxy);
        if (hasLessThan)
            value = DataBaseUtil.concat("<", value);
        setValue(res, value, rf, false, data, ana);
    }

    /**
     * Adds a QA event with the passed name to the passed analysis if it's not
     * already added; throws an exception if the QA event was not found in the
     * system
     */
    private void addQAEvent(String name, SampleSO data, AnalysisViewDO ana) throws Exception {
        Integer accession;
        QaEventDO qa;
        AnalysisQaEventViewDO aqa;
        SampleManager1 sm;

        qa = Util.getQAEvent(name, ana.getTestId(), proxy);
        sm = data.getManager();
        accession = sm.getSample().getAccessionNumber();
        if (qa == null) {
            /*
             * for display
             */
            if (accession == null)
                accession = 0;
            throw new FormErrorException(Messages.get()
                                                 .analysis_qaEventNotFoundException(accession,
                                                                                    ana.getTestName(),
                                                                                    ana.getMethodName(),
                                                                                    name));
        }
        if ( !sm.qaEvent.hasName(ana, name)) {
            proxy.log(Level.FINE, "Adding the qa event: " + name, null);
            aqa = sm.qaEvent.add(ana, qa);
            data.addChangedUid(Constants.uid().getAnalysisQAEvent(aqa.getId()));
        }
    }

    /**
     * Removes the QA event with the passed name from the passed analysis if it
     * was added earlier
     */
    private void removeQAEvent(String name, SampleSO data, AnalysisViewDO ana) {
        int i;
        AnalysisQaEventViewDO aqa;
        SampleManager1 sm;

        proxy.log(Level.FINE, "Removing the qa event: " + name, null);
        sm = data.getManager();
        for (i = 0; i < sm.qaEvent.count(ana); i++ ) {
            aqa = sm.qaEvent.get(ana, i);
            if (aqa.getQaEventName().equals(name)) {
                sm.qaEvent.remove(ana, aqa);
                break;
            }
        }
        data.addChangedUid(Constants.uid().getAnalysis(ana.getId()));
        data.addActionAfter(SampleSO.Action_After.QA_REMOVED);
    }

    /**
     * This class is used for sorting the 1st trimester sample managers fetched
     * by this scriptlet; the managers are sorted by received date and are here
     * because the "query" method in SampleBean sorts by the default field which
     * is sample id
     */
    private class SampleManagerComparator implements Comparator<SampleManager1> {
        public int compare(SampleManager1 sm1, SampleManager1 sm2) {
            Datetime rd1, rd2;

            rd1 = sm1.getSample().getReceivedDate();
            rd2 = sm2.getSample().getReceivedDate();
            if (DataBaseUtil.isAfter(rd2, rd1))
                return -1;
            else if (DataBaseUtil.isAfter(rd1, rd2))
                return 1;
            else
                return 0;
        }
    }
}