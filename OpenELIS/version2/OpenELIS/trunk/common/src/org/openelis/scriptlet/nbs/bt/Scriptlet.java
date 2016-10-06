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
package org.openelis.scriptlet.nbs.bt;

import java.util.ArrayList;
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
import org.openelis.scriptlet.SampleSO;
import org.openelis.scriptlet.SampleSO.Action_Before;
import org.openelis.scriptlet.nbs.Cache;
import org.openelis.scriptlet.nbs.ScriptletProxy;
import org.openelis.ui.scriptlet.ScriptletInt;
import org.openelis.ui.scriptlet.ScriptletObject.Status;
import org.openelis.utilcommon.ResultFormatter;
import org.openelis.utilcommon.ResultHelper;

/**
 * The scriptlet for "nbs bt (Biotinidase)" test. It determines the
 * interpretation if it isn't overridden by setting override interpretation to
 * "Yes". The interpretation is initially based on the value of biotinidase. It
 * gets overridden if the sample has any qa events of type result-override or
 * warning or if it's transfused. The qa event "poor quality" is added to the
 * analysis in the first case and in the second, "transfused" or
 * "transfused unknown" is added depending upon whether the transfusion date is
 * specified.
 */
public class Scriptlet implements ScriptletInt<SampleSO> {

    private ScriptletProxy proxy;

    private Integer        analysisId;

    public static Integer  INTER_N, INTER_PP_NR, INTER_TRAN, INTER_TRANU, INTER_PQ;

    private Cache          nbsCache1;

    public Scriptlet(ScriptletProxy proxy, Integer analysisId) throws Exception {
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
            nbsCache1 = Cache.getInstance(proxy);

        proxy.log(Level.FINE, "Initialized NbsBtScriptlet1", null);
    }

    @Override
    public SampleSO run(SampleSO data) {
        AnalysisViewDO ana;
        ResultViewDO res;
        ArrayList<String> changes;

        proxy.log(Level.FINE, "In NbsBtScriptlet1.run", null);

        changes = data.getChanges();
        ana = (AnalysisViewDO)data.getManager().getObject(Constants.uid().getAnalysis(analysisId));
        if (ana == null || Constants.dictionary().ANALYSIS_RELEASED.equals(ana.getStatusId()) ||
            Constants.dictionary().ANALYSIS_CANCELLED.equals(ana.getStatusId()) ||
            changes.size() == 0)
            return data;

        /*
         * manage result changed, a qa event added/removed, the sample being
         * marked as transfused or not and the transfusion date changing
         */
        if (data.getActionBefore().contains(Action_Before.RESULT)) {
            res = (ResultViewDO)data.getManager().getObject(data.getUid());
            if ( !analysisId.equals(res.getAnalysisId()))
                return data;
        } else if ( !data.getActionBefore().contains(Action_Before.QA) &&
                   !changes.contains(SampleMeta.getNeonatalIsTransfused()) &&
                   !changes.contains(SampleMeta.getNeonatalTransfusionDate())) {
            return data;
        }

        /*
         * set the value of interpretation based on biotinidase, weight etc.
         */
        setInterpretion(data, ana);

        return data;
    }

    /**
     * Sets the value of interpretation based on the value of Biotinidase, the
     * weight of the patient and whether or not the sample is transfused
     */
    private void setInterpretion(SampleSO data, AnalysisViewDO ana) {
        int i, j;
        boolean qaAdded;
        Integer interp;
        String bioVal, sysName;
        SampleManager1 sm;
        ResultViewDO res, resOver, resInter;
        DictionaryDO dict;
        QaEventDO qa, transQA, transUQA;
        TestManager tm;
        ResultFormatter rf;

        /*
         * find the values for the various analytes
         */
        sm = data.getManager();
        bioVal = null;
        resOver = null;
        resInter = null;
        for (i = 0; i < sm.result.count(ana); i++ ) {
            for (j = 0; j < sm.result.count(ana, i); j++ ) {
                res = sm.result.get(ana, i, j);
                if ("nbs_biotinidase".equals(res.getAnalyteExternalId()))
                    bioVal = res.getValue();
                else if ("nbs_override_inter".equals(res.getAnalyteExternalId()))
                    resOver = res;
                else if ("nbs_bt_inter".equals(res.getAnalyteExternalId()))
                    resInter = res;
            }
        }

        try {
            /*
             * proceed only if the value for override interpretation has been
             * validated and is "No"
             */
            if (resOver.getTypeId() == null)
                return;

            dict = getDictionaryByValue(resOver.getValue());
            if (dict == null || !"no".equals(dict.getSystemName()))
                return;

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
                      "Getting the value for interpretation based on the value of biotinidase",
                      null);
            sysName = dict.getSystemName();
            interp = null;
            if (sysName.endsWith("1+") || sysName.endsWith("0"))
                interp = INTER_PP_NR;
            else if (sysName.endsWith("2+") || sysName.endsWith("3+"))
                interp = INTER_N;

            /*
             * find the qa event to be added to the analysis and add it if it's
             * not already added; otherwise, if condition for which a qa event
             * was added is no longer true, make it internal; if
             */
            qaAdded = false;
            qa = null;
            transQA = nbsCache1.getQaEvent(Cache.QA_TRAN, ana.getTestId());
            transUQA = nbsCache1.getQaEvent(Cache.QA_TRANU, ana.getTestId());
            if ("Y".equals(sm.getSampleNeonatal().getIsTransfused())) {
                /*
                 * add "transfused" if the transfusion date has been specified,
                 * otherwise add "transfused unknown"
                 */
                if (sm.getSampleNeonatal().getTransfusionDate() != null) {
                    setQAWarning(transQA, data, ana);
                    setQAInternal(transUQA, data, ana);
                    qa = transQA;
                } else {
                    setQAWarning(transUQA, data, ana);
                    setQAInternal(transQA, data, ana);
                    qa = transUQA;
                }
                qaAdded = true;
            } else {
                /*
                 * make "transfused" internal if the transfusion date has not
                 * been specified, otherwise make "transfused unknown" internal
                 */
                setQAInternal(transQA, data, ana);
                setQAInternal(transUQA, data, ana);
            }

            /*
             * if the initial interpretation is not "presumptive positive" then
             * it's overridden by other data
             */
            if ( !INTER_PP_NR.equals(interp)) {
                proxy.log(Level.FINE, "Setting the interpretation based on qa events", null);
                /*
                 * if the sample has reject qas, the interpretation is
                 * "poor quality"; otherwise if the sample is transfused, the
                 * interpretation is "transfused" if transfusion date is
                 * specified or "transfused unknown" if it's not specified
                 */
                if (sm.qaEvent.hasType(Constants.dictionary().QAEVENT_OVERRIDE) ||
                    sm.qaEvent.hasType(Constants.dictionary().QAEVENT_WARNING)) {
                    interp = INTER_PQ;
                } else if (qaAdded) {
                    if (Cache.QA_TRAN.equals(qa.getName()))
                        interp = INTER_TRAN;
                    else if (Cache.QA_TRANU.equals(qa.getName()))
                        interp = INTER_TRANU;
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
                    data.setChanges(resInter.getAnalyteExternalId());
                    data.addChangedUid(Constants.uid().getResult(resInter.getId()));
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
     * 
     */
    private void setQAWarning(QaEventDO qa, SampleSO data, AnalysisViewDO ana) {
        AnalysisQaEventViewDO aqa;

        aqa = data.getManager().qaEvent.getByName(ana, qa.getName());
        if (aqa == null) {
            proxy.log(Level.FINE, "Adding the qa event: " + qa.getName(), null);
            aqa = data.getManager().qaEvent.add(ana, qa);
            data.addChangedUid(Constants.uid().getAnalysisQAEvent(aqa.getId()));
        } else if (Constants.dictionary().QAEVENT_INTERNAL.equals(aqa.getTypeId())) {
            proxy.log(Level.FINE, "Changing the qa event: " + qa.getName() + " to warning", null);
            setQAType(aqa, Constants.dictionary().QAEVENT_WARNING, data);
        }
    }

    private void setQAInternal(QaEventDO qa, SampleSO data, AnalysisViewDO ana) {
        AnalysisQaEventViewDO aqa;

        aqa = data.getManager().qaEvent.getByName(ana, qa.getName());
        if (aqa != null && Constants.dictionary().QAEVENT_WARNING.equals(aqa.getTypeId())) {
            proxy.log(Level.FINE, "Changing the qa event: " + qa.getName() + " to internal", null);
            setQAType(aqa, Constants.dictionary().QAEVENT_INTERNAL, data);
        }
    }

    private void setQAType(AnalysisQaEventViewDO aqa, Integer typeId, SampleSO data) {
        aqa.setTypeId(typeId);
        data.addChangedUid(Constants.uid().getAnalysisQAEvent(aqa.getId()));
    }
}