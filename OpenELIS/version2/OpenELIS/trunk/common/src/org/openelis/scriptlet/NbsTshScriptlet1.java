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
package org.openelis.scriptlet;

import java.util.logging.Level;

import org.openelis.domain.AnalysisViewDO;
import org.openelis.domain.Constants;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.QaEventDO;
import org.openelis.domain.ResultViewDO;
import org.openelis.domain.SampleNeonatalViewDO;
import org.openelis.manager.SampleManager1;
import org.openelis.manager.TestManager;
import org.openelis.meta.SampleMeta;
import org.openelis.scriptlet.SampleSO.Action_Before;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.scriptlet.ScriptletInt;
import org.openelis.ui.scriptlet.ScriptletObject.Status;
import org.openelis.utilcommon.ResultFormatter;
import org.openelis.utilcommon.ResultFormatter.FormattedValue;
import org.openelis.utilcommon.ResultHelper;

/**
 * The scriptlet for performing operations for
 * "nbs tsh (Thyroid Stimulating Hormone)" test
 */
public class NbsTshScriptlet1 implements ScriptletInt<SampleSO> {

    private ScriptletUtility scriptletUtility;

    private Proxy            proxy;

    private static final String TEST_NAME = "nbs tsh", METHOD_NAME = "immunoassay",
                    TSH = "nbs_tsh", OVERRIDE_INTER = "nbs_override_inter",
                    INTERPRETATION = "nbs_tsh_inter", NO = "no", LOWER_LIMIT = "nbs_lower_limit",
                    UPPER_LIMIT = "nbs_upper_limit";

    public NbsTshScriptlet1(ScriptletUtility scriptletUtility, Proxy proxy) {
        this.scriptletUtility = scriptletUtility;
        this.proxy = proxy;

        proxy.log(Level.FINE, "Initializing NbsTshScriptlet1");
    }

    @Override
    public SampleSO run(SampleSO data) {
        ResultViewDO res;
        AnalysisViewDO ana;
        TestManager tm;

        proxy.log(Level.FINE, "In NbsTshScriptlet1.run");

        if (data.getActionBefore().contains(Action_Before.RESULT_CHANGED)) {
            /*
             * find the result that made this scriptlet get executed
             */
            res = scriptletUtility.getChangedResult(data, TEST_NAME, METHOD_NAME);

            if (res == null)
                /*
                 * the result doesn't belong to this test
                 */
                return data;
            ana = (AnalysisViewDO)data.getManager()
                                      .getObject(Constants.uid().getAnalysis(res.getAnalysisId()));
            tm = data.getResults().get(res.getId());
        } else if (data.getActionBefore().contains(Action_Before.SAMPLE_QA_ADDED) ||
                   data.getActionBefore().contains(Action_Before.SAMPLE_QA_REMOVED) ||
                   SampleMeta.getNeonatalCollectionAge().equals(data.getChanged())) {
            /*
             * a sample qa event was added or removed or the collection age
             * changed
             */
            ana = scriptletUtility.getAnalysis(data, TEST_NAME, METHOD_NAME);
            if (ana == null)
                /*
                 * the sample doesn't have an active version of this test
                 */
                return data;
            tm = data.getAnalyses().get(ana.getId());
        } else {
            /*
             * nothing concerning this scriptlet happened
             */
            return data;
        }

        /*
         * set the value of interpretation based on the value of this result
         */
        setInterpretation(data, ana, tm);

        return data;
    }

    /**
     * Sets the value of the analyte for interpretation based on the value of
     * the passed result, if the value to be set is different from the current
     * value
     */
    private void setInterpretation(SampleSO data, AnalysisViewDO ana, TestManager tm) {
        int i, j;
        boolean lt;
        Integer interp, colAge;
        String sign;
        Double tshVal, lower, upper;
        SampleManager1 sm;
        SampleNeonatalViewDO sn;
        ResultViewDO res, resTsh, resOver, resInter;
        DictionaryDO dict;
        QaEventDO qa;
        FormattedValue fv;
        ResultFormatter rf;

        sm = data.getManager();
        sn = sm.getSampleNeonatal();
        resTsh = null;
        resOver = null;
        resInter = null;
        lower = null;
        upper = null;

        proxy.log(Level.FINE,
                  "Going through the manager to find the result that triggered the scriptlet");
        /*
         * find the values for the various analytes
         */
        for (i = 0; i < sm.result.count(ana); i++ ) {
            for (j = 0; j < sm.result.count(ana, i); j++ ) {
                res = sm.result.get(ana, i, j);
                if (TSH.equals(res.getAnalyteExternalId()))
                    resTsh = res;
                else if (OVERRIDE_INTER.equals(res.getAnalyteExternalId()))
                    resOver = res;
                else if (INTERPRETATION.equals(res.getAnalyteExternalId()))
                    resInter = res;
                /*
                 * else if (LOWER_LIMIT.equals(res.getAnalyteExternalId()))
                 * lower = getDoubleValue(res.getValue()); else if
                 * (UPPER_LIMIT.equals(res.getAnalyteExternalId())) upper =
                 * getDoubleValue(res.getValue());
                 */
            }
        }

        try {
            proxy.log(Level.FINE, "Finding the values of override interpretation");
            /*
             * proceed only if the value for override interpretation has been
             * validated and is "No"
             */
            if (resOver.getTypeId() == null)
                return;

            dict = getDictionaryByValue(resOver.getValue());
            if (dict == null || !NO.equals(dict.getSystemName()) || resTsh.getValue() == null)
                return;

            tshVal = getDoubleValue(resTsh.getValue());
            sign = null;
            /*
             * reset the value of the "tsh" analyte if the current value is
             * outside the range of lower and upper limits
             */
            /*
             * if (tshVal < lower) { tshVal = lower; sign = "<"; } else if
             * (tshVal > upper) { tshVal = upper; sign = ">"; }
             * 
             * rf = tm.getFormatter(); if (sign != null) {
             * ResultHelper.formatValue(resTsh, DataBaseUtil.concat(sign,
             * tshVal), ana.getUnitOfMeasureId(), rf); }
             */

            lt = "<".equals(sign);
            /*
             * the value of interpretation is "presumptive positive" if the
             * value of tsh is between 60 and 9998, the value of interpretation
             * is "borderline" if the value of tsh is between 25 and 59, the
             * value of interpretation is "within normal limits" if the value of
             * tsh is between 0 and 24
             */
            proxy.log(Level.FINE, "Getting the value for interretation based on the value for tsh");

            if ( (tshVal >= 0 && tshVal < 25.0) || (tshVal == 25.0 && lt))
                interp = scriptletUtility.INTER_N;
            else if (tshVal < 60.0 || (tshVal == 60.0 && lt))
                interp = scriptletUtility.INTER_BORD;
            else
                interp = scriptletUtility.INTER_PP_NR;

            proxy.log(Level.FINE, "Finding the qa event to be added to the analysis");
            /*
             * find the qa event to be added to the analysis
             */
            qa = null;
            colAge = sn.getCollectionAge();
            if (colAge != null && (colAge / 60) < 24) {
                /*
                 * the collection age is less than 24 hours so if either
                 * collection time or patient birth time is specified then add
                 * "early collection" otherwise add "early collection unknown"
                 */
                if (sm.getSample().getCollectionTime() != null ||
                    sn.getPatient().getBirthTime() != null)
                    qa = scriptletUtility.getQaEvent(scriptletUtility.QA_EC, ana.getTestId());
                else
                    qa = scriptletUtility.getQaEvent(scriptletUtility.QA_ECU, ana.getTestId());
            }

            /*
             * add the qa event if it's not already added
             */
            if (qa != null && !scriptletUtility.analysisHasQA(sm, ana, qa.getName())) {
                proxy.log(Level.FINE, "Adding the qa event: " + qa.getName());
                sm.qaEvent.add(ana, qa);
            }

            /*
             * the original interpretation overrides qa events if it's
             * "presumptive positive"; otherwise it's overridden by other data
             */
            if ( !scriptletUtility.INTER_PP_NR.equals(interp)) {
                proxy.log(Level.FINE, "Setting the interpretation based on qa events");
                if (scriptletUtility.sampleHasRejectQA(sm, true)) {
                    /*
                     * the sample has reject qas so set the interpretation as
                     * "poor quality"
                     */
                    interp = scriptletUtility.INTER_PQ;
                } else if ( !scriptletUtility.INTER_BORD.equals(interp)) {
                    /*
                     * the interpretation is "borderline - resubmit sample",
                     * then it has higher priority than "collection age"
                     */
                    if (qa != null) {
                        /*
                         * the collection age is less than 24 hours so if either
                         * collection time or patient birth time is specified
                         * then the interpretation is "early collection"
                         * otherwise it's "early collection unknown"
                         */
                        if (scriptletUtility.QA_EC.equals(qa.getName()))
                            interp = scriptletUtility.INTER_EC;
                        else if (scriptletUtility.QA_ECU.equals(qa.getName()))
                            interp = scriptletUtility.INTER_ECU;
                    }
                }
            }

            if (interp != null) {
                /*
                 * get the value to be set in the interpretation from this test
                 * manager's result formatter and set it only if it's different
                 * from the current value
                 */
                rf = tm.getFormatter();
                dict = proxy.getDictionaryById(interp);
                fv = rf.format(resInter.getResultGroup(), ana.getUnitOfMeasureId(), dict.getEntry());
                if ( !DataBaseUtil.isSame(resInter.getTestResultId(), fv.getId())) {
                    proxy.log(Level.FINE, "Setting the value of interpretation as: " +
                                          dict.getEntry());
                    ResultHelper.formatValue(resInter,
                                             dict.getEntry(),
                                             ana.getUnitOfMeasureId(),
                                             rf);
                    data.addRerun(resInter.getAnalyteExternalId());
                }
            }
        } catch (Exception e) {
            data.setStatus(Status.FAILED);
            data.addException(e);
        }
    }

    /**
     * Returns the dictionary entry whose id's string equivalent is the passed
     * value
     */
    private DictionaryDO getDictionaryByValue(String value) throws Exception {
        if (value == null)
            return null;

        return proxy.getDictionaryById(Integer.valueOf(value));
    }

    /**
     * If the passed value is not null, removes any "<" or ">" from it, converts
     * the remaining string to a double and returns it; otherwise returns null
     */
    private Double getDoubleValue(String value) {
        if (value != null) {
            if (value.startsWith(">") || value.startsWith("<"))
                value = value.substring(1);
            return Double.valueOf(value);
        }

        return null;
    }

    public static interface Proxy {
        public DictionaryDO getDictionaryById(Integer id) throws Exception;

        public DictionaryDO getDictionaryBySystemName(String systemName) throws Exception;

        public void log(Level level, String message);
    }
}