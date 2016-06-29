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
package org.openelis.scriptlet.ms.conf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;

import org.openelis.constants.Messages;
import org.openelis.domain.AnalysisViewDO;
import org.openelis.domain.AnalyteParameterViewDO;
import org.openelis.domain.Constants;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.ResultViewDO;
import org.openelis.domain.SampleItemViewDO;
import org.openelis.manager.SampleManager1;
import org.openelis.manager.TestManager;
import org.openelis.meta.SampleMeta;
import org.openelis.scriptlet.SampleSO;
import org.openelis.scriptlet.SampleSO.Action_Before;
import org.openelis.scriptlet.ms.ScriptletProxy;
import org.openelis.scriptlet.ms.Util;
import org.openelis.scriptlet.ms.conf.Constants.Sample_Type;
import org.openelis.scriptlet.ms.quad.Constants.Gest_Age_Method;
import org.openelis.scriptlet.ms.quad.Constants.Interpretation;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.Datetime;
import org.openelis.ui.common.FormErrorException;
import org.openelis.ui.scriptlet.ScriptletInt;
import org.openelis.ui.scriptlet.ScriptletObject.Status;
import org.openelis.utilcommon.ResultFormatter;
import org.openelis.utilcommon.ResultHelper;

import com.google.gwt.i18n.shared.DateTimeFormat;
import com.google.gwt.i18n.shared.DefaultDateTimeFormatInfo;

/**
 * The scriptlet for "ms ntd (Maternal NTD Screen)" test. It uses a compute
 * engine to compute the MoMs for AFP and risk and interpretation for NTD. The
 * computations are done based on patient information and results entered by the
 * analyst. If the results are overridden, the MoM, risk and interpretation are
 * blanked. The computations are done as soon as the user changes any of the
 * values or on completing/releasing the analysis.
 */
public class Scriptlet implements ScriptletInt<SampleSO> {

    private ScriptletProxy                                 proxy;

    private Integer                                        analysisId;

    private Compute                                        compute;

    private static DateTimeFormat                          dateFormatter;

    private ArrayList<String>                              changes;

    private HashMap<String, HashMap<String, ResultViewDO>> resultMap;

    private static boolean                                 initialized;

    private static Integer                                 YES, NO, GEST_AGE_LMP, GEST_AGE_US,
                    INT_POSITIVE, INT_NEGATIVE, ACTION_US_AMN, ACTION_SEE_ACHE, ACTION_NO_ACTION,
                    SAM_TYPE_SERUM, SAM_TYPE_AMNIOTIC_FLUID;

    private static String                                  YES_STR;

    public Scriptlet(ScriptletProxy proxy, Integer analysisId) {
        this.proxy = proxy;
        this.analysisId = analysisId;

        proxy.log(Level.FINE, "Initializing MSConfScriptlet1", null);
        try {
            if ( !initialized) {
                initialized = true;
                SAM_TYPE_SERUM = proxy.getDictionaryBySystemName("sample_type_serum").getId();
                SAM_TYPE_AMNIOTIC_FLUID = proxy.getDictionaryBySystemName("sample_type_amniotic_fluid")
                                               .getId();
                YES = proxy.getDictionaryBySystemName("yes").getId();
                NO = proxy.getDictionaryBySystemName("no").getId();
                GEST_AGE_LMP = proxy.getDictionaryBySystemName("gest_age_date_type_lmp").getId();
                GEST_AGE_US = proxy.getDictionaryBySystemName("gest_age_date_type_us").getId();
                INT_POSITIVE = proxy.getDictionaryBySystemName("positive").getId();
                INT_NEGATIVE = proxy.getDictionaryBySystemName("negative").getId();
                ACTION_US_AMN = proxy.getDictionaryBySystemName("ms_rec_action_us_amn").getId();
                ACTION_SEE_ACHE = proxy.getDictionaryBySystemName("ms_rec_action_see_ache_res")
                                       .getId();
                ACTION_NO_ACTION = proxy.getDictionaryBySystemName("ms_rec_action_no_action")
                                        .getId();
                YES_STR = YES.toString();

                /*
                 * the formatter that converts from string to date
                 */
                dateFormatter = new DateTimeFormat(Messages.get().datePattern(),
                                                   new DefaultDateTimeFormatInfo()) {
                };
            }
            compute = new Compute();
            proxy.log(Level.FINE, "Initialized MSConfScriptlet1", null);
        } catch (Exception e) {
            initialized = false;
            proxy.log(Level.SEVERE, "Failed to initialize dictionary constants", e);
        }
    }

    @Override
    public SampleSO run(SampleSO data) {
        SampleManager1 sm;
        AnalysisViewDO ana;
        SampleItemViewDO item;
        ResultViewDO res;

        proxy.log(Level.FINE, "In MSConfScriptlet1.run", null);

        changes = data.getChanges();
        sm = data.getManager();
        ana = (AnalysisViewDO)sm.getObject(Constants.uid().getAnalysis(analysisId));
        if (ana == null || Constants.dictionary().ANALYSIS_RELEASED.equals(ana.getStatusId()) ||
            Constants.dictionary().ANALYSIS_CANCELLED.equals(ana.getStatusId()) ||
            changes.size() == 0)
            return data;

        /*
         * manage result changed, the risks needing to be recomputed, the
         * analysis getting completed or released, sample type changed, a qa
         * event added/removed or collection date, patient's birth date or
         * patient's race changed
         */
        if (data.getActionBefore().contains(Action_Before.RESULT)) {
            res = (ResultViewDO)sm.getObject(data.getUid());
            if ( !analysisId.equals(res.getAnalysisId()))
                return data;
        } else if (data.getActionBefore().contains(Action_Before.COMPLETE) ||
                   data.getActionBefore().contains(Action_Before.RELEASE)) {
            ana = (AnalysisViewDO)sm.getObject(data.getUid());
            if ( !analysisId.equals(ana.getId()))
                return data;
        } else if (data.getActionBefore().contains(Action_Before.SAMPLE_ITEM)) {
            item = (SampleItemViewDO)sm.getObject(data.getUid());
            if ( !item.getId().equals(ana.getSampleItemId()))
                return data;
        } else if ( !data.getActionBefore().contains(Action_Before.QA) &&
                   !data.getActionBefore().contains(Action_Before.PATIENT) &&
                   !data.getActionBefore().contains(Action_Before.RECOMPUTE) &&
                   !data.getActionBefore().contains(Action_Before.ANALYSIS) &&
                   !changes.contains(SampleMeta.getCollectionDate()) &&
                   !changes.contains(SampleMeta.getClinicalPatientBirthDate()) &&
                   !changes.contains(SampleMeta.getClinicalPatientRaceId())) {
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

        try {
            initializeResultMap(data, ana);
            setBirthDate(data, ana);
            setMaternalWeight(data, ana);
            setRaceBlack(data, ana);
            setComputeValues(data, ana);
            compute.compute();
            setResultValues(data, ana);
            formatValues(data, ana);
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
     * row or column analyte and the value is the ResultVIewDO for the analyte;
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
    }

    /**
     * Validates the maternal weight entered and converts it to lbs from kgs if
     * it contains "k"
     */
    private void setMaternalWeight(SampleSO data, AnalysisViewDO ana) throws Exception {
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
            if ("weight".equals(res.getAnalyteExternalId()) && value != null) {
                proxy.log(Level.FINE, "Formatting and resetting maternal weight", null);
                /*
                 * convert from kgs to lbs if weight contains "k"
                 */
                tm = (TestManager)data.getCache().get(Constants.uid().getTest(ana.getTestId()));
                try {
                    if (value.endsWith("k")) {
                        value = value.substring(0, value.length() - 1);
                        weight = new Double(value) * 2.204;
                    } else {
                        weight = new Double(value);
                    }
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
        Integer value, raceId;
        DictionaryDO dict;
        ResultViewDO res;
        TestManager tm;
        SampleManager1 sm;

        sm = data.getManager();
        /*
         * don't do anything if either the patient's race wasn't changed or if
         * the analyte's value is already set
         */
        res = getResult("race_black");
        if (changes.contains(SampleMeta.getClinicalPatientRaceId()) && res.getValue() == null) {
            proxy.log(Level.FINE, "Setting 'maternal race black' from the sample", null);
            /*
             * race is black if the system name contains a "b" e.g. "race_b" or
             * "race_wb", otherwise not
             */
            raceId = sm.getSampleClinical().getPatient().getRaceId();
            value = NO;
            if (raceId != null) {
                dict = proxy.getDictionaryById(raceId);
                if (dict.getSystemName() != null && dict.getSystemName().contains("b"))
                    value = YES;
            }
            tm = (TestManager)data.getCache().get(Constants.uid().getTest(ana.getTestId()));
            setValue(res, value, tm.getFormatter(), true, data, ana);
        }
    }

    /**
     * Set values in the compute engine from the passed sample, analysis,
     * results of specific analytes and analyte parameters
     */
    private void setComputeValues(SampleSO data, AnalysisViewDO ana) throws Exception {
        Integer gestAgeMethodId;
        SampleManager1 sm;
        SampleItemViewDO item;
        AnalyteParameterViewDO ap;
        AFP t;
        Sample_Type sampleType;
        HashMap<Integer, AnalyteParameterViewDO> paramMap;

        proxy.log(Level.FINE, "Setting values in the compute engine", null);
        /*
         * set sample/analysis level info
         */
        sm = data.getManager();
        compute.setIsOverridden(sm.qaEvent.hasType(Constants.dictionary().QAEVENT_OVERRIDE) ||
                                sm.qaEvent.hasType(ana, Constants.dictionary().QAEVENT_OVERRIDE));
        compute.setEnteredDate(sm.getSample().getEnteredDate());
        compute.setCollectionDate(sm.getSample().getCollectionDate());
        compute.setIsReleased(Constants.dictionary().SAMPLE_RELEASED.equals(sm.getSample()
                                                                              .getStatusId()) ||
                              Constants.dictionary().ANALYSIS_RELEASED.equals(ana.getStatusId()));
        /*
         * set patient info
         */
        compute.setIsRaceBlack(YES_STR.equals(getValue("race_black")));
        compute.setIsDiabetic(YES_STR.equals(getValue("insulin")));
        compute.setHasHistoryOfNTD(YES_STR.equals(getValue("history_ntd")));
        compute.setBirthDate(getDateValue(getValue("birth")));
        compute.setLMPDate(getDateValue(getValue("lmp")));
        compute.setUltrasoundDate(getDateValue(getValue("ultrasound")));
        compute.setNumFetus(getIntegerValue(getResult("multiple_fetuses"), sm, ana));
        compute.setWeight(getDoubleValue(getResult("weight"), sm, ana, false));
        compute.setCRL(getIntegerValue(getResult("crl"), sm, ana));
        compute.setBPD(getIntegerValue(getResult("bpd"), sm, ana));
        compute.setWeeksDays(getDoubleValue(getResult("weeks_days"), sm, ana, false));
        /*
         * set the method by which gestational age was initially computed
         */
        gestAgeMethodId = getIntegerValue(getResult("gest_age_init", "determined_by"), sm, ana);
        if (GEST_AGE_LMP.equals(gestAgeMethodId))
            compute.setGestAgeInitMethod(Gest_Age_Method.LMP);
        else if (GEST_AGE_US.equals(gestAgeMethodId))
            compute.setGestAgeInitMethod(Gest_Age_Method.US);

        item = (SampleItemViewDO)sm.getObject(Constants.uid().getSampleItem(ana.getSampleItemId()));
        sampleType = getSampleType(item.getTypeOfSampleId());
        compute.setSampleType(sampleType);
        /*
         * get the analyte parameters for this analysis' test
         */
        paramMap = Util.getParameters(ana.getTestId(), Constants.table().TEST, proxy, data, ana);
        /*
         * set p-values and result from the intrument for AFP
         */
        ap = paramMap != null ? paramMap.get(getAnalyteId("afp")) : null;
        t = (AFP)compute.getAFP();
        t.setP1(getP1(ap));
        t.setP2(getP2(ap));
        t.setP3(getP3(ap));
        t.setResult(getDoubleValue(getResult("afp", "result"), sm, ana, true));
        t.setSampleType(sampleType);
    }

    /**
     * Sets values such as MoMs, risks, cutoffs, interpretations etc. in the
     * results from the compute engine
     */
    private void setResultValues(SampleSO data, AnalysisViewDO ana) throws Exception {
        Object value;
        TestManager tm;
        SampleItemViewDO item;
        ResultFormatter rf;

        proxy.log(Level.FINE, "Setting values in the results from the compute engine", null);
        tm = (TestManager)data.getCache().get(Constants.uid().getTest(ana.getTestId()));
        rf = tm.getFormatter();
        setValue(getResult("age_delivery"), compute.getMothersDueAge(), rf, false, data, ana);
        /*
         * MoM
         */
        setValue(getResult("afp"), compute.getAFP().getMomFinal(), rf, false, data, ana);
        /*
         * risks, screening cutoffs(limits), interpretations, recommended
         * actions; ntd
         */
        value = null;
        item = (SampleItemViewDO)data.getManager()
                                     .getObject(Constants.uid()
                                                         .getSampleItem(ana.getSampleItemId()));
        if (compute.getLimitNTD() != null)
            value = DataBaseUtil.concatWithSeparator("NTD >=",
                                                     " ",
                                                     Util.format(compute.getLimitNTD(), 1, proxy));
        setValue(getResult("afp", "screen_cutoff"), value, rf, false, data, ana);
        setInterpretation("afp", compute.getInterpretationNTD(), rf, data, ana);
        setRecommendedAction("afp",
                             compute.getInterpretationNTD(),
                             getSampleType(item.getTypeOfSampleId()),
                             rf,
                             data,
                             ana);
        /*
         * gestational ages, methods
         */
        setValue(getResult("init_gest_age"), compute.getGestAgeInit(), rf, false, data, ana);
        setGestAgeMethod("init_gest_age", compute.getGestAgeInitMethod(), rf, ana, data);
        setValue(getResult("gest_age"), compute.getGestAgeCurr(), rf, false, data, ana);
        setGestAgeMethod("gest_age", compute.getGestAgeCurrMethod(), rf, ana, data);
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
        formatValue(getResult("weight"), 1, tm.getFormatter(), false, data, ana);
        formatValue(getResult("weeks_days"), 1, tm.getFormatter(), true, data, ana);
        /*
         * test MoMs and instrument results have two fractional digit
         */
        formatValue(getResult("afp"), 2, tm.getFormatter(), false, data, ana);
        formatValue(getResult("afp", "result"), 2, tm.getFormatter(), true, data, ana);
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
     * Returns the id for the row analyte whose external id is the passed value
     */
    private Integer getAnalyteId(String rowExtId) {
        ResultViewDO res;

        res = getResult(rowExtId);
        return res != null ? res.getAnalyteId() : null;
    }

    /**
     * Returns an element of the enum Sample_Type based on the passed sample
     * type id
     */
    private Sample_Type getSampleType(Integer sampleTypeId) {
        if (sampleTypeId == null)
            return null;

        if (SAM_TYPE_SERUM.equals(sampleTypeId))
            return Sample_Type.SERUM;
        else if (SAM_TYPE_AMNIOTIC_FLUID.equals(sampleTypeId))
            return Sample_Type.AMNIOTIC_FLUID;

        return null;
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
            return Integer.valueOf(res.getValue());
        } catch (NumberFormatException e) {
            throw getValueInvalidException(res, sm, ana);
        }
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
        String value;

        if (res == null || res.getValue() == null)
            return null;
        value = allowLessThan ? res.getValue().replaceAll("<", "") : res.getValue();
        try {
            return Double.valueOf(value);
        } catch (NumberFormatException e) {
            throw getValueInvalidException(res, sm, ana);
        }
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
     * Sets the value of the analyte with the passed external id as the
     * interpretation e.g. "Positive", based on the passed computed
     * interpretation e.g. "Presumptive Positive"
     */
    private void setInterpretation(String externalId, Interpretation interp, ResultFormatter rf,
                                   SampleSO data, AnalysisViewDO ana) throws Exception {
        Integer value;

        value = null;
        if (interp != null) {
            switch (interp) {
                case NORMAL:
                    value = INT_NEGATIVE;
                    break;
                case PRES_POS:
                case PRES_POS_AS:
                    value = INT_POSITIVE;
                    break;
            }
        }
        setValue(getResult(externalId, "interpretation"), value, rf, true, data, ana);
    }

    /**
     * Sets the value of the analyte with the passed external id as a
     * recommended action e.g. "Offer amniocentesis"; the action depends on the
     * passed interpretation e.g. "Positive"
     */
    private void setRecommendedAction(String externalId, Interpretation interp,
                                      Sample_Type sampleType, ResultFormatter rf, SampleSO data,
                                      AnalysisViewDO ana) throws Exception {
        Integer value;

        value = null;
        if (interp != null) {
            switch (interp) {
                case NORMAL:
                    value = ACTION_NO_ACTION;
                    break;
                case PRES_POS:
                case PRES_POS_AS:
                    switch (sampleType) {
                        case SERUM:
                            value = ACTION_US_AMN;
                            break;
                        case AMNIOTIC_FLUID:
                            value = ACTION_SEE_ACHE;
                            break;
                    }
                    break;
            }
        }

        setValue(getResult(externalId, "rec_action"), value, rf, true, data, ana);
    }

    /**
     * Sets the value of the analyte as the method for determining gestational
     * age e.g. LMP, based on the passed computed method
     */
    private void setGestAgeMethod(String externalId, Gest_Age_Method method, ResultFormatter rf,
                                  AnalysisViewDO ana, SampleSO data) throws Exception {
        Integer value;

        value = null;
        if (method != null) {
            switch (method) {
                case LMP:
                    value = GEST_AGE_LMP;
                    break;
                case US:
                    value = GEST_AGE_US;
                    break;
            }
        }
        setValue(getResult(externalId, "determined_by"), value, rf, true, data, ana);
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