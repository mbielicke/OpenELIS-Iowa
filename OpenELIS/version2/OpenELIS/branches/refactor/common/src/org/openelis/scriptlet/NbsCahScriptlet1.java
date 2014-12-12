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
 * "nbs cah (Congenital Adrenal Hyperplasia)" test
 */
public class NbsCahScriptlet1 implements ScriptletInt<SampleSO> {

    private NBSScriptlet1Proxy proxy;

    private Integer          analysisId;

    private static final String CAH = "nbs_cah", OVERRIDE_INTER = "nbs_override_inter",
                    INTERPRETATION = "nbs_cah_inter", NO = "no";

    private static Integer       INTER_N, INTER_PP_NR, INTER_EC, INTER_UE, INTER_U, INTER_ECU,
                    INTER_PQ, INTER_AA, INTER_BORD;
    
    private NBSCache1 nbsCache1;

    public NbsCahScriptlet1(NBSScriptlet1Proxy proxy, Integer analysisId) throws Exception {
        this.proxy = proxy;
        this.analysisId = analysisId;

        proxy.log(Level.FINE, "Initializing NbsCahScriptlet1", null);

        if (INTER_N == null) {
            INTER_N = proxy.getDictionaryBySystemName("newborn_inter_n").getId();
            INTER_PP_NR = proxy.getDictionaryBySystemName("newborn_inter_pp_nr").getId();
            INTER_EC = proxy.getDictionaryBySystemName("newborn_inter_ec").getId();
            INTER_UE = proxy.getDictionaryBySystemName("newborn_inter_ue").getId();
            INTER_U = proxy.getDictionaryBySystemName("newborn_inter_u").getId();
            INTER_ECU = proxy.getDictionaryBySystemName("newborn_inter_ecu").getId();
            INTER_PQ = proxy.getDictionaryBySystemName("newborn_inter_pq").getId();
            INTER_AA = proxy.getDictionaryBySystemName("newborn_inter_aa").getId();
            INTER_BORD = proxy.getDictionaryBySystemName("newborn_inter_bord").getId();
        }

        if (nbsCache1 == null)
            nbsCache1 = NBSCache1.getInstance(proxy);
        
        proxy.log(Level.FINE, "Initialized NbsCahScriptlet1", null);
    }

    @Override
    public SampleSO run(SampleSO data) {
        AnalysisViewDO ana;
        TestManager tm;

        proxy.log(Level.FINE, "In NbsCahScriptlet1.run", null);

        try {
            if (data.getActionBefore().contains(Action_Before.RESULT)) {
                /*
                 * find out if the changed result belongs to the analysis
                 * managed by this scriptlet; don't do anything if it doesn't
                 */
                if ( !ScriptletUtility.isManagedResult(data, analysisId))
                    return data;
                ana = (AnalysisViewDO)data.getManager()
                                          .getObject(Constants.uid().getAnalysis(analysisId));
            } else if (data.getActionBefore().contains(Action_Before.QA) ||
                       SampleMeta.getNeonatalWeight().equals(data.getChanged()) ||
                       SampleMeta.getNeonatalCollectionAge().equals(data.getChanged())) {
                /*
                 * a sample qa event was added or removed or a field related to
                 * transfusion changed; find the analysis managed by this
                 * scriptlet
                 */
                ana = (AnalysisViewDO)data.getManager()
                                          .getObject(Constants.uid().getAnalysis(analysisId));
            } else {
                /*
                 * nothing concerning this scriptlet happened
                 */
                return data;
            }
        } catch (Exception e) {
            data.setStatus(Status.FAILED);
            data.addException(e);
            return data;
        }

        /*
         * don't do anything if the analysis is released or cancelled
         */
        if (Constants.dictionary().ANALYSIS_RELEASED.equals(ana.getStatusId()) ||
            Constants.dictionary().ANALYSIS_CANCELLED.equals(ana.getStatusId()))
            return data;

        tm = (TestManager)data.getCache().get(Constants.uid().getTest(ana.getTestId()));
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
        Integer interp, colAge;
        Double cahVal;
        String val;
        SampleNeonatalViewDO sn;
        SampleManager1 sm;
        ResultViewDO res, resOver, resInter;
        DictionaryDO dict;
        QaEventDO qa;
        AnalysisQaEventViewDO aqa;
        FormattedValue fv;
        ResultFormatter rf;

        sm = data.getManager();
        sn = sm.getSampleNeonatal();
        cahVal = null;
        resOver = null;
        resInter = null;
        proxy.log(Level.FINE,
                  "Going through the scriptlet object to find the result that trigerred the scriptlet", null);
        /*
         * find the values for the various analytes
         */
        for (i = 0; i < sm.result.count(ana); i++ ) {
            for (j = 0; j < sm.result.count(ana, i); j++ ) {
                res = sm.result.get(ana, i, j);
                if (CAH.equals(res.getAnalyteExternalId())) {
                    val = res.getValue();
                    if (val != null) {
                        if (val.startsWith(">") || val.startsWith("<"))
                            val = val.substring(1);
                        cahVal = Double.valueOf(val);
                    }
                } else if (OVERRIDE_INTER.equals(res.getAnalyteExternalId())) {
                    resOver = res;
                } else if (INTERPRETATION.equals(res.getAnalyteExternalId())) {
                    resInter = res;
                }
            }
        }

        try {
            proxy.log(Level.FINE, "Finding the values of override interpretation", null);
            /*
             * proceed only if the value for override interpretation has been
             * validated and is "No"
             */
            if (resOver.getTypeId() == null)
                return;

            dict = getDictionaryByValue(resOver.getValue());
            if (dict == null || !NO.equals(dict.getSystemName()) || cahVal == null)
                return;

            proxy.log(Level.FINE,
                      "Getting the value for interpretation based on the value for 17-Hydroxyprogesterone and weight", null);
            /*
             * determine the initial interpretation based on weight and the
             * value for 17-Hydroxyprogesterone
             */
            if (sn.getWeight() == null) {
                /*
                 * the weight is unknown
                 */
                if (cahVal < 30.0)
                    interp = INTER_U;
                else
                    interp = INTER_UE;
            } else if (sn.getWeight() <= 1500) {
                if (cahVal < 90.0)
                    interp = INTER_N;
                else if (cahVal < 155.0)
                    interp = INTER_BORD;
                else
                    interp = INTER_PP_NR;
            } else if (sn.getWeight() <= 2500) {
                if (cahVal < 50.0)
                    interp = INTER_N;
                else if (cahVal < 75.0)
                    interp = INTER_BORD;
                else
                    interp = INTER_PP_NR;
            } else {
                if (cahVal < 30.0)
                    interp = INTER_N;
                else if (cahVal < 75.0)
                    interp = INTER_BORD;
                else
                    interp = INTER_PP_NR;
            }

            proxy.log(Level.FINE, "Finding the qa event to be added to the analysis", null);
            /*
             * find the qa event to be added to the analysis
             */
            qa = null;
            if (sn.getWeight() == null) {
                /*
                 * the sample is transfused; add "transfused" if the transfusion
                 * date has been specified, otherwise add "transfused unknown"
                 */
                if (cahVal < 30.0)
                    qa = nbsCache1.getQaEvent(NBSCache1.QA_U, ana.getTestId());
                else
                    qa = nbsCache1.getQaEvent(NBSCache1.QA_UE, ana.getTestId());
            } else {
                colAge = sn.getCollectionAge();
                if (colAge != null && (colAge / 60) < 24) {
                    /*
                     * the collection age is less than 24 hours so if either
                     * collection time or patient birth time is specified then
                     * add "early collection" otherwise add
                     * "early collection unknown"
                     */
                    if (sm.getSample().getCollectionTime() != null ||
                        sn.getPatient().getBirthTime() != null)
                        qa = nbsCache1.getQaEvent(NBSCache1.QA_EC, ana.getTestId());
                    else
                        qa = nbsCache1.getQaEvent(NBSCache1.QA_ECU, ana.getTestId());
                }
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
                } else if ( !INTER_U.equals(interp) && !INTER_UE.equals(interp) &&
                           !INTER_BORD.equals(interp)) {
                    /*
                     * if the interpretation is "unknown weight",
                     * "unknown weight elevated" or "borderline", then it has
                     * higher priority than "collection age"
                     */
                    if (qa != null) {
                        /*
                         * the collection age is less than 24 hours so if either
                         * collection time or patient birth time is specified
                         * then the interpretation is "early collection"
                         * otherwise it's "early collection unknown"
                         */
                        if (NBSCache1.QA_EC.equals(qa.getName()))
                            interp = INTER_EC;
                        else if (NBSCache1.QA_ECU.equals(qa.getName()))
                            interp = INTER_ECU;
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