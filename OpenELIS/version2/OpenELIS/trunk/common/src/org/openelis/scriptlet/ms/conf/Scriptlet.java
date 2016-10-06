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
import org.openelis.domain.ResultViewDO;
import org.openelis.manager.AnalyteParameterManager1;
import org.openelis.manager.SampleManager1;
import org.openelis.manager.TestManager;
import org.openelis.meta.SampleMeta;
import org.openelis.scriptlet.SampleSO;
import org.openelis.scriptlet.SampleSO.Action_Before;
import org.openelis.scriptlet.ms.Constants.Gest_Age_Method;
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
 * The scriptlet for "ms conf (Amniotic Fluid AFP)" test. It uses a compute
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

    private ArrayList<String>                              changes;

    private HashMap<String, HashMap<String, ResultViewDO>> resultMap;

    private AnalyteParameterManager1                       paramManager;

    private static DateTimeFormat                          dateFormatter;

    private static boolean                                 initialized;

    private static Integer                                 YES, UNIT_MM, UNIT_YEARS, GEST_AGE_LMP,
                    GEST_AGE_US, INT_POSITIVE, INT_NEGATIVE, ACTION_SEE_ACHE, ACTION_NO_ACTION;

    private static String                                  YES_STR;

    public Scriptlet(ScriptletProxy proxy, Integer analysisId) {
        this.proxy = proxy;
        this.analysisId = analysisId;

        proxy.log(Level.FINE, "Initializing MSConfScriptlet1", null);
        try {
            if ( !initialized) {
                initialized = true;
                YES = proxy.getDictionaryBySystemName("yes").getId();
                UNIT_MM = proxy.getDictionaryBySystemName("millimeters").getId();
                UNIT_YEARS = proxy.getDictionaryBySystemName("years_local").getId();
                GEST_AGE_LMP = proxy.getDictionaryBySystemName("gest_age_date_type_lmp").getId();
                GEST_AGE_US = proxy.getDictionaryBySystemName("gest_age_date_type_us").getId();
                INT_POSITIVE = proxy.getDictionaryBySystemName("positive").getId();
                INT_NEGATIVE = proxy.getDictionaryBySystemName("negative").getId();
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
            proxy.log(Level.FINE, "Initialized MSConfScriptlet1", null);
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

        proxy.log(Level.FINE, "In MSConfScriptlet1.setRisks", null);
        try {
            initializeResultMap(data, ana);
            setBirthDate(data, ana);
            setComputeValues(data, ana);
            compute.compute();
            setResultValues(data, ana);
            formatValues(data, ana);
            setUnits(data, ana);
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
     * Set values in the compute engine from the passed sample, analysis,
     * results of specific analytes and analyte parameters
     */
    private void setComputeValues(SampleSO data, AnalysisViewDO ana) throws Exception {
        SampleManager1 sm;
        AnalyteParameterViewDO ap;
        AFP afp;
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
        compute.setBeenReleased(ana.getRevision() > 0);
        /*
         * set patient info
         */
        compute.setIsDiabetic(YES_STR.equals(getValue("insulin_dep_diabetic")));
        compute.setBirthDate(getDateValue(getValue("birth")));
        compute.setLMPDate(getDateValue(getValue("lmp")));
        compute.setUltrasoundDate(getDateValue(getValue("ultrasound")));
        compute.setCRL(getIntegerValue(getResult("crl"), sm, ana));
        compute.setBPD(getIntegerValue(getResult("bpd"), sm, ana));
        compute.setWeeksDays(getDoubleValue(getResult("weeks_days"), sm, ana, false));

        /*
         * get the analyte parameters for this analysis' test
         */
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
         * set p-values and result from the intrument for AFP
         */
        ap = paramMap != null ? paramMap.get(getAnalyteId("afp_mom")) : null;
        afp = (AFP)compute.getAFP();
        afp.setP1(getP1(ap));
        afp.setP2(getP2(ap));
        afp.setP3(getP3(ap));
        afp.setResult(getDoubleValue(getResult("afp_mom", "result"), sm, ana, true));
    }

    /**
     * Sets values such as MoMs, risks, cutoffs, interpretations etc. in the
     * results from the compute engine
     */
    private void setResultValues(SampleSO data, AnalysisViewDO ana) throws Exception {
        String gaCurr;
        Object value;
        TestManager tm;
        ResultFormatter rf;

        proxy.log(Level.FINE, "Setting values in the results from the compute engine", null);
        tm = (TestManager)data.getCache().get(Constants.uid().getTest(ana.getTestId()));
        rf = tm.getFormatter();
        setValue(getResult("mat_age_at_delivery"), compute.getMothersDueAge(), rf, false, data, ana);
        /*
         * MoM
         */
        setValue(getResult("afp_mom"), compute.getAFP().getMomFinal(), rf, false, data, ana);
        /*
         * risks, screening cutoffs(limits), interpretations, recommended
         * actions; ntd
         */
        value = null;
        if (compute.getLimitNTD() != null)
            value = DataBaseUtil.concatWithSeparator("NTD >=",
                                                     " ",
                                                     Util.format(compute.getLimitNTD(), 1, proxy));
        setValue(getResult("afp_mom", "screen_cutoff"), value, rf, false, data, ana);
        setInterpretation("afp_mom", compute.getInterpretationNTD(), rf, data, ana);
        setRecommendedAction("afp_mom", compute.getInterpretationNTD(), rf, data, ana);
        /*
         * gestational ages, methods
         */
        gaCurr = Util.getWeeksAndDays(compute.getGestAgeCurr());
        setValue(getResult("gest_age"), gaCurr, rf, false, data, ana);
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
         * age at delivery, egg's age weeks & days have one fractional digit
         */
        formatValue(getResult("mat_age_at_delivery"), 1, tm.getFormatter(), false, data, ana);
        formatValue(getResult("eggs_age"), 1, tm.getFormatter(), false, data, ana);
        formatValue(getResult("weeks_days"), 1, tm.getFormatter(), true, data, ana);
        /*
         * test MoMs and instrument results have two fractional digit
         */
        formatValue(getResult("afp_mom"), 2, tm.getFormatter(), false, data, ana);
        formatValue(getResult("afp_mom", "result"), 2, tm.getFormatter(), true, data, ana);
    }

    /**
     * Sets the values in the "Unit" column for various row analytes such as
     * "Maternal Age At Delivery", "CRL" etc; sets the unit as null if the row
     * analyte's value is null
     */
    private void setUnits(SampleSO data, AnalysisViewDO ana) throws Exception {
        ResultFormatter rf;
        TestManager tm;

        tm = (TestManager)data.getCache().get(Constants.uid().getTest(ana.getTestId()));
        rf = tm.getFormatter();
        setUnit("eggs_age", UNIT_YEARS, rf, data, ana);
        setUnit("mat_age_at_delivery", UNIT_YEARS, rf, data, ana);
        setUnit("crl", UNIT_MM, rf, data, ana);
        setUnit("bpd", UNIT_MM, rf, data, ana);
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
            return Integer.valueOf(res.getValue());
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
     * Sets the value in the "Unit" column for a row analyte such as
     * "Maternal Age At Delivery", "CRL" etc; sets the unit as null if the row
     * analyte's value is null
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
                case PRES_POS_AS:
                    value = ACTION_SEE_ACHE;
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