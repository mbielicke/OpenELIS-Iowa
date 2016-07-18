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
package org.openelis.scriptlet.nbs.cah;

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
import org.openelis.scriptlet.SampleSO;
import org.openelis.scriptlet.SampleSO.Action_Before;
import org.openelis.scriptlet.nbs.Cache;
import org.openelis.scriptlet.nbs.ScriptletProxy;
import org.openelis.ui.scriptlet.ScriptletInt;
import org.openelis.ui.scriptlet.ScriptletObject.Status;
import org.openelis.utilcommon.ResultFormatter;
import org.openelis.utilcommon.ResultHelper;

/**
 * The scriptlet for "nbs cah (Congenital Adrenal Hyperplasia)" test.The
 * scriptlet for "nbs bt (Biotinidase)" test. It determines the interpretation
 * if it isn't overridden by setting override interpretation to "Yes". The
 * initial interpretation is based on weight and the value for
 * 17-Hydroxyprogesterone. It gets overridden if the sample has any qa events of
 * type result-override or warning or if the interpretation is not
 * "unknown weight", "unknown weight elevated" or "borderline". The qa event
 * "unknown weight" or "unknown weight elevated" is added to the analysis if
 * weight is not present. Otherwise "early collection" or
 * "early collection unknown" is added based on collection age, collection time
 * and patient birth time and in this case, the interpretation is the same as
 * the qa event.
 */
public class Scriptlet implements ScriptletInt<SampleSO> {

    private ScriptletProxy proxy;

    private Integer            analysisId;

    private static Integer     INTER_N, INTER_PP_NR, INTER_EC, INTER_UE, INTER_U, INTER_ECU,
                    INTER_PQ, INTER_BORD;

    private Cache          nbsCache1;

    public Scriptlet(ScriptletProxy proxy, Integer analysisId) throws Exception {
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
            INTER_BORD = proxy.getDictionaryBySystemName("newborn_inter_bord").getId();
        }

        if (nbsCache1 == null)
            nbsCache1 = Cache.getInstance(proxy);

        proxy.log(Level.FINE, "Initialized NbsCahScriptlet1", null);
    }

    @Override
    public SampleSO run(SampleSO data) {
        AnalysisViewDO ana;
        ResultViewDO res;

        proxy.log(Level.FINE, "In NbsCahScriptlet1.run", null);
        ana = (AnalysisViewDO)data.getManager().getObject(Constants.uid().getAnalysis(analysisId));
        if (ana == null || Constants.dictionary().ANALYSIS_RELEASED.equals(ana.getStatusId()) ||
            Constants.dictionary().ANALYSIS_CANCELLED.equals(ana.getStatusId()))
            return data;

        /*
         * manage result changed, a qa event added/removed and the weight or
         * collection age changing
         */
        if (data.getActionBefore().contains(Action_Before.RESULT)) {
            res = (ResultViewDO)data.getManager().getObject(data.getUid());
            if ( !analysisId.equals(res.getAnalysisId()))
                return data;
        } else if ( !data.getActionBefore().contains(Action_Before.QA) &&
                   !SampleMeta.getNeonatalWeight().equals(data.getChanged()) &&
                   !SampleMeta.getNeonatalCollectionAge().equals(data.getChanged())) {
            return data;
        }

        /*
         * set the value of interpretation based on weight, collection age etc.
         */
        setInterpretation(data, ana);

        return data;
    }

    /**
     * Sets the value of the analyte for interpretation based on the value of
     * the passed result, if the value to be set is different from the current
     * value
     */
    private void setInterpretation(SampleSO data, AnalysisViewDO ana) {
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
        TestManager tm;
        ResultFormatter rf;

        sm = data.getManager();
        sn = sm.getSampleNeonatal();
        cahVal = null;
        resOver = null;
        resInter = null;
        /*
         * find the values for the various analytes
         */
        for (i = 0; i < sm.result.count(ana); i++ ) {
            for (j = 0; j < sm.result.count(ana, i); j++ ) {
                res = sm.result.get(ana, i, j);
                if ("nbs_cah".equals(res.getAnalyteExternalId())) {
                    val = res.getValue();
                    if (val != null) {
                        if (val.startsWith(">") || val.startsWith("<"))
                            val = val.substring(1);
                        cahVal = Double.valueOf(val);
                    }
                } else if ("nbs_override_inter".equals(res.getAnalyteExternalId())) {
                    resOver = res;
                } else if ("nbs_cah_inter".equals(res.getAnalyteExternalId())) {
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

            dict = resOver.getValue() == null ? null
                                             : proxy.getDictionaryById(Integer.valueOf(resOver.getValue()));
            if (dict == null || !"no".equals(dict.getSystemName()) || cahVal == null)
                return;

            proxy.log(Level.FINE,
                      "Getting the value for interpretation based on the value for 17-Hydroxyprogesterone and weight",
                      null);
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

            /*
             * find the qa event to be added to the analysis and add it if it's
             * not already added
             */
            qa = null;
            if (sn.getWeight() == null) {
                /*
                 * add "unknown weight" or "unknown weight elevated" based on
                 * the value of 17-Hydroxyprogesterone
                 */
                if (cahVal < 30.0)
                    qa = nbsCache1.getQaEvent(Cache.QA_U, ana.getTestId());
                else
                    qa = nbsCache1.getQaEvent(Cache.QA_UE, ana.getTestId());
            } else {
                colAge = sn.getCollectionAge();
                if (colAge != null && (colAge / 60) < 24) {
                    /*
                     * the collection age is less than 24 hours, if either
                     * collection time or patient birth time is specified then
                     * add "early collection" otherwise add
                     * "early collection unknown"
                     */
                    if (sm.getSample().getCollectionTime() != null ||
                        sn.getPatient().getBirthTime() != null)
                        qa = nbsCache1.getQaEvent(Cache.QA_EC, ana.getTestId());
                    else
                        qa = nbsCache1.getQaEvent(Cache.QA_ECU, ana.getTestId());
                }
            }

            if (qa != null && !sm.qaEvent.hasName(ana, qa.getName())) {
                proxy.log(Level.FINE, "Adding the qa event: " + qa.getName(), null);
                aqa = sm.qaEvent.add(ana, qa);
                data.addChangedUid(Constants.uid().getAnalysisQAEvent(aqa.getId()));
            }

            /*
             * if the initial interpretation is not "presumptive positive" then
             * it's overridden by other data
             */
            if ( !INTER_PP_NR.equals(interp)) {
                proxy.log(Level.FINE, "Setting the interpretation based on qa events", null);
                /*
                 * if the sample has reject qa events, the interpretation is
                 * "poor quality"; if the interpretation is "unknown weight",
                 * "unknown weight elevated" or "borderline", then it's not
                 * overridden by any qa event; if it's not one of these then it
                 * it is the same as the qa event added if the qa event is
                 * either "early collection" or "early collection unknown"
                 */
                if (sm.qaEvent.hasType(Constants.dictionary().QAEVENT_OVERRIDE) ||
                    sm.qaEvent.hasType(Constants.dictionary().QAEVENT_WARNING)) {
                    interp = INTER_PQ;
                } else if ( !INTER_U.equals(interp) && !INTER_UE.equals(interp) &&
                           !INTER_BORD.equals(interp) && qa != null) {
                    if (Cache.QA_EC.equals(qa.getName()))
                        interp = INTER_EC;
                    else if (Cache.QA_ECU.equals(qa.getName()))
                        interp = INTER_ECU;
                }
            }

            /*
             * set the interpretation
             */
            if (interp != null) {
                tm = (TestManager)data.getCache().get(Constants.uid().getTest(ana.getTestId()));
                rf = tm.getFormatter();
                dict = proxy.getDictionaryById(interp);
                if (ResultHelper.formatValue(resInter,
                                             dict.getEntry(),
                                             ana.getUnitOfMeasureId(),
                                             rf)) {
                    proxy.log(Level.FINE, "Setting the value of interpretation as: " +
                                          dict.getEntry(), null);
                    data.addRerun(resInter.getAnalyteExternalId());
                    data.addChangedUid(Constants.uid().getResult(resInter.getId()));
                }
            }
        } catch (Exception e) {
            data.setStatus(Status.FAILED);
            data.addException(e);
        }
    }
}