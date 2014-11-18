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

import java.util.Map;
import java.util.logging.Level;

import org.openelis.domain.AnalysisViewDO;
import org.openelis.domain.Constants;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.QaEventDO;
import org.openelis.domain.ResultViewDO;
import org.openelis.domain.TestViewDO;
import org.openelis.manager.SampleManager1;
import org.openelis.manager.TestManager;
import org.openelis.meta.SampleMeta;
import org.openelis.scriptlet.NbsTshScriptlet1.Proxy;
import org.openelis.scriptlet.SampleSO.Action_Before;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.scriptlet.ScriptletInt;
import org.openelis.ui.scriptlet.ScriptletObject.Status;
import org.openelis.utilcommon.ResultFormatter;
import org.openelis.utilcommon.ResultFormatter.FormattedValue;
import org.openelis.utilcommon.ResultHelper;

/**
 * The scriptlet for performing operations for "nbs galt (Galactosemia)" test
 */
public class NbsGaltScriptlet1 implements ScriptletInt<SampleSO> {

    private ScriptletUtility scriptletUtility;
    
    private Proxy proxy;

    private static final String TEST_NAME = "nbs galt", METHOD_NAME = "enzymatic assay",
                    GALT = "nbs_galt", OVERRIDE_INTER = "nbs_override_inter",
                    INTERPRETATION = "nbs_galt_inter", NO = "no", LOWER_LIMIT = "nbs_lower_limit",
                                    UPPER_LIMIT = "nbs_upper_limit";

    public NbsGaltScriptlet1(ScriptletUtility scriptletUtility, Proxy proxy) {
        this.scriptletUtility = scriptletUtility;
        this.proxy = proxy;

        proxy.log(Level.FINE, "Initializing NbsGaltScriptlet1");
    }

    @Override
    public SampleSO run(SampleSO data) {
        ResultViewDO res;
        AnalysisViewDO ana;
        TestManager tm;

        proxy.log(Level.FINE, "In NbsGaltScriptlet1.run");
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
                   SampleMeta.getNeonatalIsTransfused().equals(data.getChanged()) ||
                   SampleMeta.getNeonatalTransfusionDate().equals(data.getChanged())) {
            /*
             * a sample qa event was added or removed or a field related to
             * transfusion changed; find the analysis that's linked to the
             * active vesrion of this test
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
        setInterpretion(data, ana, tm);

        return data;
    }

    /**
     * Returns the result whose value was changed to make this scriptlet get
     * executed, but only if belongs to the active version of this test;
     * otherwise returns null
     */
    private ResultViewDO getChangedResult(SampleSO data) {
        ResultViewDO res;
        TestViewDO test;
        TestManager tm;

        res = null;
        proxy.log(Level.FINE,
                  "Going through the scriptlet object to find the result that trigerred the scriptlet");
        for (Map.Entry<Integer, TestManager> entry : data.getResults().entrySet()) {
            tm = entry.getValue();
            test = tm.getTest();
            if (TEST_NAME.equals(test.getName()) && METHOD_NAME.equals(test.getMethodName()) &&
                "Y".equals(test.getIsActive())) {
                res = (ResultViewDO)data.getManager()
                                        .getObject(Constants.uid().getResult(entry.getKey()));
                break;
            }
        }

        return res;
    }

    /**
     * Sets the value of the analyte for interpretation based on the value of
     * the passed result, if the value to be set is different from the current
     * value
     */
    private void setInterpretion(SampleSO data, AnalysisViewDO ana, TestManager tm) {
        int i, j;
        Integer interp;
        String resVal, overVal;
        Double galtVal;
        SampleManager1 sm;
        ResultViewDO res, resInter;
        DictionaryDO dict;
        QaEventDO qa;
        FormattedValue fv;
        ResultFormatter rf;

        /*
         * find the analysis for the result
         */
        sm = data.getManager();
        galtVal = null;
        overVal = null;
        resInter = null;
        proxy.log(Level.FINE,
                  "Going through the scriptlet object to find the result that trigerred the scriptlet");
        /*
         * find the values for the various analytes
         */
        for (i = 0; i < sm.result.count(ana); i++ ) {
            for (j = 0; j < sm.result.count(ana, i); j++ ) {
                res = sm.result.get(ana, i, j);
                if (GALT.equals(res.getAnalyteExternalId())) {
                    resVal = res.getValue();
                    if (resVal != null) {
                        if (resVal.startsWith(">") || resVal.startsWith("<"))
                            resVal = resVal.substring(1);
                        galtVal = Double.valueOf(resVal);
                    }
                } else if (OVERRIDE_INTER.equals(res.getAnalyteExternalId())) {
                    overVal = res.getValue();
                } else if (INTERPRETATION.equals(res.getAnalyteExternalId())) {                    
                    resInter = res;
                }
            }
        }

        try {
            proxy.log(Level.FINE, "Finding the values of override interretation");
            /*
             * proceed only if the value for override interpretation is "No"
             */
            dict = getDictionaryByValue(overVal);
            if (dict == null || !NO.equals(dict.getSystemName()) || galtVal == null)
                return;

            /*
             * the value of interpretation is "presumptive positive" if the
             * value of tsh is between 3.8 and 99, the value of interpretation
             * is "borderline" if the value of tsh is between 3.2 and 3.7, the
             * value of interpretation is "within normal limits" if the value of
             * tsh is between 0 and 3.1
             */
            proxy.log(Level.FINE, "Getting the value for interpretation based on the value for biotinidase");
            if (galtVal >= 0 && galtVal < 3.2)
                interp = scriptletUtility.INTER_PP_NR;
            else if (galtVal < 3.8)
                interp = scriptletUtility.INTER_BORD;
            else
                interp = scriptletUtility.INTER_N;
            
            proxy.log(Level.FINE, "Finding the qa event to be added to the analysis");
            /*
             * find the qa event to be added to the analysis
             */
            qa = null;
            if ("Y".equals(sm.getSampleNeonatal().getIsTransfused())) {
                /*
                 * the sample is transfused; add "transfused" if the transfusion
                 * date has been specified, otherwise add "transfused unknown"
                 */
                if (sm.getSampleNeonatal().getTransfusionDate() != null)
                    qa = scriptletUtility.getQaEvent(scriptletUtility.QA_TRAN, ana.getTestId());
                else
                    qa = scriptletUtility.getQaEvent(scriptletUtility.QA_TRANU, ana.getTestId());
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
                } else if (qa != null) {
                    /*
                     * the sample is transfused so if a transfusion date is
                     * specified then set "transfused" as the interpretation,
                     * otherwise set "transfused unknown" as the interpretation
                     */
                    if (scriptletUtility.QA_TRAN.equals(qa.getName()))
                        interp = scriptletUtility.INTER_TRAN;
                    else if (scriptletUtility.QA_TRANU.equals(qa.getName()))
                        interp = scriptletUtility.INTER_TRANU;
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

    public static interface Proxy {
        public DictionaryDO getDictionaryById(Integer id) throws Exception;

        public DictionaryDO getDictionaryBySystemName(String systemName) throws Exception;

        public void log(Level level, String message);
    }
}