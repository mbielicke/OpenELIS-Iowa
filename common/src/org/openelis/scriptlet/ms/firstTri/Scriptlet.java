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
package org.openelis.scriptlet.ms.firstTri;

import static org.openelis.scriptlet.ms.Constants.*;

import java.util.ArrayList;
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
import org.openelis.ui.scriptlet.ScriptletInt;
import org.openelis.ui.scriptlet.ScriptletObject.Status;
import org.openelis.utilcommon.ResultFormatter;
import org.openelis.utilcommon.ResultHelper;

import com.google.gwt.i18n.shared.DateTimeFormat;
import com.google.gwt.i18n.shared.DefaultDateTimeFormatInfo;

/**
 * The scriptlet for "ms 1st tri (Maternal First Trimester Screen)" test. It
 * uses a compute engine to calculate the MoMs for various tests such as NT and
 * risks and interpretations for various disorders such as Downs. The
 * computations are done based on patient information and results entered by the
 * analyst. If the results are overridden, the MoMs, risks and interpretations
 * are blanked. The computations are done as soon as the user changes any of the
 * values or on completing/releasing the analysis.
 */
public class Scriptlet implements ScriptletInt<SampleSO> {

    private ScriptletProxy                                 proxy;

    private Integer                                        analysisId;

    private Compute                                        compute;

    private ArrayList<String>                              changes;

    private HashMap<String, HashMap<String, ResultViewDO>> resultMap;

    private AnalyteParameterManager1                       testParamManager, provParamManager;

    private static DateTimeFormat                          dateFormatter;

    private static boolean                                 initialized;

    private static Integer                                 YES, NO, UNIT_MM, UNIT_POUNDS,
                    UNIT_YEARS, INT_POSITIVE, INT_NEGATIVE, INT_UNKNOWN, ACTION_US_DIAG,
                    ACTION_NO_ACTION, ACTION_RISK_NOT_CALC;

    private static String                                  YES_STR;

    private enum Risk {
        NT, DOWNS, T18
    }

    public Scriptlet(ScriptletProxy proxy, Integer analysisId) {
        this.proxy = proxy;
        this.analysisId = analysisId;

        proxy.log(Level.FINE, "Initializing MS1stTriScriptlet1", null);
        try {
            if ( !initialized) {
                initialized = true;
                YES = proxy.getDictionaryBySystemName("yes").getId();
                UNIT_MM = proxy.getDictionaryBySystemName("millimeters").getId();
                UNIT_POUNDS = proxy.getDictionaryBySystemName("pounds_local").getId();
                UNIT_YEARS = proxy.getDictionaryBySystemName("years_local").getId();
                INT_POSITIVE = proxy.getDictionaryBySystemName("positive").getId();
                INT_NEGATIVE = proxy.getDictionaryBySystemName("negative").getId();
                INT_UNKNOWN = proxy.getDictionaryBySystemName("unknown").getId();
                ACTION_US_DIAG = proxy.getDictionaryBySystemName("ms_rec_action_us_diag").getId();
                ACTION_NO_ACTION = proxy.getDictionaryBySystemName("ms_rec_action_no_action")
                                        .getId();
                ACTION_RISK_NOT_CALC = proxy.getDictionaryBySystemName("ms_rec_action_risk_not_calc")
                                            .getId();
                YES_STR = YES.toString();

                /*
                 * the formatter that converts from string to date
                 */
                dateFormatter = new DateTimeFormat(Messages.get().datePattern(),
                                                   new DefaultDateTimeFormatInfo()) {
                };
            }
            proxy.log(Level.FINE, "Initialized MS1stTriScriptlet1", null);
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

        proxy.log(Level.FINE, "In MS1stTriScriptlet1.run", null);

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
         * collection date, patient's birth date or patient's race changed
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
                   !changes.contains(SampleMeta.getClinicalPatientRaceId())) {
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

        proxy.log(Level.FINE, "In MS1stTriScriptlet1.setRisks", null);
        try {
            initializeResultMap(data, ana);
            setBirthDate(data, ana);
            setMaternalWeight(data, ana);
            setRaceBlack(data, ana);
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
     * Initialize a hash map where the key is the external id of a row analyte
     * and the value is another hash map containing the ResultViewDOs for all
     * analytes in the row; the key in the second map is the external id of a
     * row or column analyte and the value is the ResultVIewDO for that analyte;
     * these maps are used to quickly lookup ResultViewDOs for getting or
     * setting their value
     */
    private void initializeResultMap(SampleSO data, AnalysisViewDO ana) throws Exception {
        int i, j;
        ResultViewDO res;
        SampleManager1 sm;
        HashMap<String, ResultViewDO> map;

        proxy.log(Level.FINE, "Creating hashmap of results", null);
        sm = data.getManager();
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
            if ("mat_weight".equals(res.getAnalyteExternalId()) && value != null) {
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
     * Set values in the compute engine from the passed sample, analysis,
     * results of specific analytes and analyte parameters
     */
    private void setComputeValues(SampleSO data, AnalysisViewDO ana) throws Exception {
        SampleManager1 sm;
        ResultViewDO res;
        HashMap<Integer, AnalyteParameterViewDO> provParamMap, testParamMap;

        proxy.log(Level.FINE, "Setting values in the compute engine", null);
        /*
         * set sample/analysis level info
         */
        sm = data.getManager();
        compute = new Compute();
        compute.setIsOverridden(sm.qaEvent.hasType(Constants.dictionary().QAEVENT_OVERRIDE) ||
                                sm.qaEvent.hasType(ana, Constants.dictionary().QAEVENT_OVERRIDE));
        compute.setIsIntegrated(false);
        compute.setEnteredDate(sm.getSample().getEnteredDate());
        compute.setCollectionDate(sm.getSample().getCollectionDate());
        /*
         * set patient info
         */
        compute.setBirthDate(getDateValue(getValue("birth")));
        compute.setUltrasoundDate(getDateValue(getValue("ultrasound")));
        compute.setWeight(getDoubleValue(getResult("mat_weight"), sm, ana, false));
        compute.setCRL(getIntegerValue(getResult("crl"), sm, ana));
        /*
         * if the sonographer was changed or if this analysis is getting
         * completed, released or risks need to be recomputed, get the
         * sonographer's analyte parameters; they provide p-values for "NT"
         */
        try {
            if (data.getActionBefore().contains(Action_Before.RESULT)) {
                res = (ResultViewDO)sm.getObject(data.getUid());
                if ("sonographer".equals(res.getAnalyteExternalId()))
                    provParamManager = getSonographerParams(sm, ana);
            }
            if (provParamManager == null ||
                data.getActionBefore().contains(Action_Before.COMPLETE) ||
                data.getActionBefore().contains(Action_Before.RELEASE)) {
                provParamManager = getSonographerParams(sm, ana);
            }
        } catch (NotFoundException e) {
            /*
             * analyte parameters were not found; if this exception is not
             * handled here, an error with the message "null" is shown, which is
             * not very helpful; a more descriptive error will be added later if
             * some computation(s) can't be done because of missing p-values
             */
        }
        provParamMap = Util.getParameters(provParamManager, data, ana, null);
        /*
         * get this test's analyte parameters; they provide p-values for
         * analytes other than "NT" e.g. "HCG"
         */
        if (testParamManager == null) {
            try {
                testParamManager = proxy.fetchParameters(ana.getTestId(), Constants.table().TEST);
            } catch (NotFoundException e) {
                /*
                 * see the comment in the previous catch block
                 */
            }
        }
        testParamMap = Util.getParameters(testParamManager, data, ana);
        /*
         * set p-values and results from the instrument for "NT" etc.
         */
        setTestValues(compute.getNT(), "nt_mom", provParamMap, sm, ana);
        setTestValues(compute.getPAPPA(), "papp_a_mom", testParamMap, sm, ana);
        setTestValues(compute.getHCG(), "hcg_mom", testParamMap, sm, ana);
    }

    /**
     * Sets values such as MoMs, risks, cutoffs, interpretations etc. in the
     * results from the compute engine
     */
    private void setResultValues(SampleSO data, AnalysisViewDO ana) throws Exception {
        Double ntResult;
        TestManager tm;
        ResultFormatter rf;

        proxy.log(Level.FINE, "Setting values in the results from the compute engine", null);
        tm = (TestManager)data.getCache().get(Constants.uid().getTest(ana.getTestId()));
        rf = tm.getFormatter();
        setValue(getResult("mat_age_at_delivery"), compute.getMothersDueAge(), rf, false, data, ana);
        /*
         * copy the NT intrument result to the reportable analyte; set MoMs
         */
        ntResult = getDoubleValue(getResult("nt_mom", "result"), data.getManager(), ana, false);
        setValue(getResult("nt_measurement"), ntResult, rf, false, data, ana);
        setValue(getResult("nt_mom"), compute.getNT().getMom(), rf, false, data, ana);
        setValue(getResult("papp_a_mom"), compute.getPAPPA().getMom(), rf, false, data, ana);
        setValue(getResult("hcg_mom"), compute.getHCG().getMom(), rf, false, data, ana);
        /*
         * risks, screening cutoffs(limits), interpretations, recommended
         * actions; nt
         */
        setInterpretation("nt_mom", compute.getInterpretationNT(), Risk.NT, rf, data, ana);
        setRecommendedAction("nt_mom", compute.getInterpretationNT(), rf, data, ana);
        /*
         * downs
         */
        setRisk("downs_risk", compute.getRiskSignDowns(), compute.getRiskDowns(), rf, data, ana);
        setScreeningCutoff("downs_risk", compute.getLimitDowns(), rf, data, ana);
        setInterpretation("downs_risk", compute.getInterpretationDowns(), Risk.DOWNS, rf, data, ana);
        setRecommendedAction("downs_risk", compute.getInterpretationDowns(), rf, data, ana);
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
        setInterpretation("trisomy_18_risk",
                          compute.getInterpretationTrisomy18(),
                          Risk.T18,
                          rf,
                          data,
                          ana);
        setRecommendedAction("trisomy_18_risk", compute.getInterpretationTrisomy18(), rf, data, ana);
        /*
         * downs by age
         */
        setValue(getResult("downs_age_risk"), compute.getRiskDownsByAge(), rf, false, data, ana);
        /*
         * gestational age in weeks and days
         */
        setValue(getResult("gest_age"),
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
         * egg's age, weight and weeks & days have one fractional digit
         */
        formatValue(getResult("eggs_age"), 1, tm.getFormatter(), false, data, ana);
        formatValue(getResult("mat_weight"), 1, tm.getFormatter(), false, data, ana);
        /*
         * test MoMs and instrument results have two fractional digit
         */
        formatValue(getResult("nt_measurement"), 2, tm.getFormatter(), false, data, ana);
        formatValue(getResult("nt_mom"), 2, tm.getFormatter(), false, data, ana);
        formatValue(getResult("nt_mom", "result"), 2, tm.getFormatter(), true, data, ana);
        formatValue(getResult("papp_a_mom"), 2, tm.getFormatter(), false, data, ana);
        formatValue(getResult("papp_a_mom", "result"), 2, tm.getFormatter(), true, data, ana);
        formatValue(getResult("hcg_mom"), 2, tm.getFormatter(), false, data, ana);
        formatValue(getResult("hcg_mom", "result"), 2, tm.getFormatter(), true, data, ana);
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
        setUnit("mat_weight", UNIT_POUNDS, rf, data, ana);
        setUnit("crl", UNIT_MM, rf, data, ana);
        setUnit("nt_measurement", UNIT_MM, rf, data, ana);
    }

    /**
     * Adds a QA event to the analysis if age at delivery < 15 years or if there
     * are multiple fetuses; removes a QA event if the condition for which it
     * was added is no longer true
     */
    private void addRemoveQaEvents(SampleSO data, AnalysisViewDO ana) throws Exception {
        Double age, mom;

        mom = getDoubleValue(getValue("papp_a_mom"), false);
        if (mom != null && mom < PAPP_A_LIMIT)
            addQAEvent(QA_PAPP_A_MOM, data, ana);
        else
            removeQAEvent(QA_PAPP_A_MOM, data, ana);

        age = getDoubleValue(getValue("mat_age_at_delivery"), false);
        if (age != null && age < 15.0)
            addQAEvent(QA_LESS_THAN_15, data, ana);
        else
            removeQAEvent(QA_LESS_THAN_15, data, ana);
    }

    /**
     * Returns the ResultViewDO for the row analyte with the passed external id
     */
    private ResultViewDO getResult(String rowExtId) {
        return getResult(rowExtId, rowExtId);
    }

    /**
     * Returns the ResultViewDO for a column analyte whose row analyte's
     * external id is the first argument and whose external id is the second
     * argument
     */
    private ResultViewDO getResult(String rowExtId, String colExtId) {
        HashMap<String, ResultViewDO> map;

        map = resultMap.get(rowExtId);
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
     * Returns the analyte parameters for the currently entered sonographer;
     * returns null if no sonographer has been entered
     */
    private AnalyteParameterManager1 getSonographerParams(SampleManager1 sm, AnalysisViewDO ana) throws Exception {
        Integer dictId, provId;
        String sysName;
        DictionaryDO dict;

        dictId = getIntegerValue(getResult("sonographer"), sm, ana);
        if (dictId != null) {
            /*
             * the sonographer's dictionary entry's system name contains the id
             * of a provider e.g. "son_1234", where "1234" is the id; fetch the
             * AnalyteParameterManager1 using that id
             */
            dict = proxy.getDictionaryById(dictId);
            sysName = dict.getSystemName();
            if (sysName != null && sysName.startsWith("son_")) {
                provId = Integer.valueOf(sysName.substring(4));
                return proxy.fetchParameters(provId, Constants.table().PROVIDER);
            }
        }
        return null;
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
        if (res == null || res.getValue() == null)
            return null;
        try {
            return new Integer(res.getValue());
        } catch (NumberFormatException e) {
            throw getValueInvalidException(res, sm, ana);
        }
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
    private void setInterpretation(String externalId, Interpretation interp, Risk risk,
                                   ResultFormatter rf, SampleSO data, AnalysisViewDO ana) throws Exception {
        boolean isDict;
        Object value;

        value = null;
        isDict = false;
        if (interp != null) {
            switch (interp) {
                case NORMAL:
                    switch (risk) {
                        case NT:
                            value = Messages.get().result_ntInterpretation("<");
                            break;
                        case DOWNS:
                        case T18:
                            value = INT_NEGATIVE;
                            isDict = true;
                            break;
                    }
                    break;
                case PRES_POS:
                    switch (risk) {
                        case NT:
                            value = Messages.get().result_ntInterpretation(">=");
                            break;
                        case DOWNS:
                        case T18:
                            value = INT_POSITIVE;
                            isDict = true;
                            break;
                    }
                    break;
                case UNKNOWN:
                    value = INT_UNKNOWN;
                    break;
            }
        }
        setValue(getResult(externalId, "interpretation"), value, rf, isDict, data, ana);
    }

    /**
     * Sets the value of the analyte with the passed external id as a
     * recommended action e.g. "Offer amniocentesis"; the action depends on the
     * passed interpretation e.g. "Positive" and a risk e.g. "Downs"
     */
    private void setRecommendedAction(String externalId, Interpretation interp, ResultFormatter rf,
                                      SampleSO data, AnalysisViewDO ana) throws Exception {
        Integer value;

        value = null;
        if (interp != null) {
            switch (interp) {
                case NORMAL:
                    value = ACTION_NO_ACTION;
                    break;
                case PRES_POS:
                    value = ACTION_US_DIAG;
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
}