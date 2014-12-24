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

import org.openelis.domain.AnalysisQaEventViewDO;
import org.openelis.domain.AnalysisViewDO;
import org.openelis.domain.Constants;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.QaEventDO;
import org.openelis.domain.ResultViewDO;
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
 * The scriptlet for performing operations for "nbs bt (Biotinidase)" test
 */
public class NbsBtScriptlet1 implements ScriptletInt<SampleSO> {

    private NBSScriptlet1Proxy proxy;

    private Integer            analysisId;

    private static final String BIOTINIDASE = "nbs_biotinidase", INTERPRETATION = "nbs_bt_inter",
                    OVERRIDE_INTER = "nbs_override_inter", NO = "no";

    public static Integer       INTER_N, INTER_PP_NR, INTER_TRAN, INTER_TRANU, INTER_PQ;

    private NBSCache1           nbsCache1;

    public NbsBtScriptlet1(NBSScriptlet1Proxy proxy, Integer analysisId) throws Exception {
        this.proxy = proxy;
        this.analysisId = analysisId;

        proxy.log(Level.FINE, "Initializing NbsBtScriptlet1", null);

        if (INTER_N == null) {
            INTER_N = proxy.getDictionaryBySystemName("newborn_inter_n").getId();
            INTER_PP_NR = proxy.getDictionaryBySystemName("newborn_inter_pp_nr").getId();
            INTER_TRAN = proxy.getDictionaryBySystemName("newborn_inter_tran").getId();
            INTER_TRANU = proxy.getDictionaryBySystemName("newborn_inter_tranu").getId();
            INTER_PQ = proxy.getDictionaryBySystemName("newborn_inter_pq").getId();
        }

        if (nbsCache1 == null)
            nbsCache1 = NBSCache1.getInstance(proxy);

        proxy.log(Level.FINE, "Initialized NbsBtScriptlet1", null);
    }

    @Override
    public SampleSO run(SampleSO data) {
        AnalysisViewDO ana;
        TestManager tm;

        proxy.log(Level.FINE, "In NbsBtScriptlet1.run", null);

        if (data.getActionBefore().contains(Action_Before.RESULT)) {
            /*
             * find out if the changed result belongs to the analysis managed by
             * this scriptlet; don't do anything if it doesn't
             */
            if ( !ScriptletUtility.isManagedResult(data, analysisId))
                return data;
            ana = (AnalysisViewDO)data.getManager().getObject(Constants.uid()
                                                                       .getAnalysis(analysisId));
        } else if (data.getActionBefore().contains(Action_Before.QA) ||
                   SampleMeta.getNeonatalIsTransfused().equals(data.getChanged()) ||
                   SampleMeta.getNeonatalTransfusionDate().equals(data.getChanged())) {
            /*
             * a sample qa event was added or removed or a field related to
             * transfusion changed; find the analysis managed by this scriptlet
             */
            ana = (AnalysisViewDO)data.getManager().getObject(Constants.uid()
                                                                       .getAnalysis(analysisId));
        } else {
            /*
             * nothing concerning this scriptlet happened
             */
            return data;
        }

        /*
         * don't do anything if the analysis is not in the manager anymore or if
         * it is released or cancelled
         */
        if (ana == null || Constants.dictionary().ANALYSIS_RELEASED.equals(ana.getStatusId()) ||
            Constants.dictionary().ANALYSIS_CANCELLED.equals(ana.getStatusId()))
            return data;

        tm = (TestManager)data.getCache().get(Constants.uid().getTest(ana.getTestId()));
        /*
         * set the value of interpretation based on the value of this result
         */
        setInterpretion(data, ana, tm);

        return data;
    }

    /**
     * Sets the value of the analyte for interpretation based on the value of
     * the passed result, if the value to be set is different from the current
     * value
     */
    private void setInterpretion(SampleSO data, AnalysisViewDO ana, TestManager tm) {
        int i, j;
        Integer interp;
        String bioVal, sysName;
        SampleManager1 sm;
        ResultViewDO res, resOver, resInter;
        DictionaryDO dict;
        QaEventDO qa;
        AnalysisQaEventViewDO aqa;
        FormattedValue fv;
        ResultFormatter rf;

        /*
         * find the values for the various analytes
         */
        sm = data.getManager();
        bioVal = null;
        resOver = null;
        resInter = null;
        proxy.log(Level.FINE,
                  "Going through the SO to find the result that trigerred the scriptlet",
                  null);
        for (i = 0; i < sm.result.count(ana); i++ ) {
            for (j = 0; j < sm.result.count(ana, i); j++ ) {
                res = sm.result.get(ana, i, j);
                if (BIOTINIDASE.equals(res.getAnalyteExternalId()))
                    bioVal = res.getValue();
                else if (OVERRIDE_INTER.equals(res.getAnalyteExternalId()))
                    resOver = res;
                else if (INTERPRETATION.equals(res.getAnalyteExternalId()))
                    resInter = res;
            }
        }

        try {
            proxy.log(Level.FINE, "Finding the value of override interretation", null);
            /*
             * proceed only if the value for override interpretation has been
             * validated and is "No"
             */
            if (resOver.getTypeId() == null)
                return;

            dict = getDictionaryByValue(resOver.getValue());
            if (dict == null || !NO.equals(dict.getSystemName()))
                return;

            proxy.log(Level.FINE, "Finding the value of biotinidase", null);
            /*
             * get the value for biotinidase
             */
            dict = getDictionaryByValue(bioVal);
            if (dict == null)
                return;

            /*
             * determine the initial interpretation based on weight and the
             * value for biotinidase
             */
            proxy.log(Level.FINE,
                      "Getting the value for interretation based on the value of biotinidase",
                      null);
            sysName = dict.getSystemName();
            interp = null;
            if (sysName.endsWith("1+") || sysName.endsWith("0"))
                interp = INTER_PP_NR;
            else if (sysName.endsWith("2+") || sysName.endsWith("3+"))
                interp = INTER_N;

            proxy.log(Level.FINE, "Finding the qa event to be added to the analysis", null);
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
                    qa = nbsCache1.getQaEvent(NBSCache1.QA_TRAN, ana.getTestId());
                else
                    qa = nbsCache1.getQaEvent(NBSCache1.QA_TRANU, ana.getTestId());
            }

            /*
             * add the qa event if it's not already added
             */
            if (qa != null && !ScriptletUtility.analysisHasQA(sm, ana, qa.getName())) {
                proxy.log(Level.FINE, "Adding the qa event: " + qa.getName(), null);
                aqa = sm.qaEvent.add(ana, qa);
                data.getChangedUids().add(Constants.uid().getAnalysisQAEvent(aqa.getId()));
            }

            /*
             * the original interpretation overrides qa events if it's
             * "presumptive positive"; otherwise it's overridden by other data
             */
            if ( !INTER_PP_NR.equals(interp)) {
                proxy.log(Level.FINE, "Setting the interpretation based on qa events", null);
                if (ScriptletUtility.sampleHasRejectQA(sm, true)) {
                    /*
                     * the sample has reject qas so set the interpretation as
                     * "poor quality"
                     */
                    interp = INTER_PQ;
                } else if (qa != null) {
                    /*
                     * the sample is transfused so if a transfusion date is
                     * specified then set "transfused" as the interpretation,
                     * otherwise set "transfused unknown" as the interpretation
                     */
                    if (NBSCache1.QA_TRAN.equals(qa.getName()))
                        interp = INTER_TRAN;
                    else if (NBSCache1.QA_TRANU.equals(qa.getName()))
                        interp = INTER_TRANU;
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
                                          dict.getEntry(), null);
                    ResultHelper.formatValue(resInter,
                                             dict.getEntry(),
                                             ana.getUnitOfMeasureId(),
                                             rf);
                    data.addRerun(resInter.getAnalyteExternalId());
                    data.getChangedUids().add(Constants.uid().getResult(resInter.getId()));
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
}