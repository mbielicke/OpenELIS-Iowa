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
package org.openelis.scriptlet.nbs.galt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;

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
import org.openelis.scriptlet.ms.Util;
import org.openelis.scriptlet.nbs.Cache;
import org.openelis.scriptlet.nbs.ScriptletProxy;
import org.openelis.ui.common.NotFoundException;
import org.openelis.ui.scriptlet.ScriptletInt;
import org.openelis.ui.scriptlet.ScriptletObject.Status;
import org.openelis.utilcommon.ResultFormatter;
import org.openelis.utilcommon.ResultHelper;

/**
 * The scriptlet for "nbs galt (Galactosemia)" test. TODO add comments for the
 * algorithm used by the scriptlet after it's been fully implemented with the
 * use of analyte parameters for upper and lower limits.
 */
public class Scriptlet implements ScriptletInt<SampleSO> {

    private ScriptletProxy           proxy;

    private Integer                  analysisId;

    private AnalyteParameterManager1 paramManager;

    private static Integer           INTER_N, INTER_PP_NR, INTER_TRAN, INTER_TRANU, INTER_PQ,
                    INTER_BORD;

    private Cache                    nbsCache1;

    public Scriptlet(ScriptletProxy proxy, Integer analysisId) throws Exception {
        this.proxy = proxy;
        this.analysisId = analysisId;

        proxy.log(Level.FINE, "Initializing NbsGaltScriptlet1", null);

        if (INTER_N == null) {
            INTER_N = proxy.getDictionaryBySystemName("newborn_inter_n").getId();
            INTER_PP_NR = proxy.getDictionaryBySystemName("newborn_inter_pp_nr").getId();
            INTER_TRAN = proxy.getDictionaryBySystemName("newborn_inter_tran").getId();
            INTER_TRANU = proxy.getDictionaryBySystemName("newborn_inter_tranu").getId();
            INTER_PQ = proxy.getDictionaryBySystemName("newborn_inter_pq").getId();
            INTER_BORD = proxy.getDictionaryBySystemName("newborn_inter_bord_nr").getId();
        }

        if (nbsCache1 == null)
            nbsCache1 = Cache.getInstance(proxy);

        proxy.log(Level.FINE, "Initialized NbsGaltScriptlet1", null);
    }

    @Override
    public SampleSO run(SampleSO data) {
        AnalysisViewDO ana;
        ResultViewDO res;
        ArrayList<String> changes;

        proxy.log(Level.FINE, "In NbsGaltScriptlet1.run", null);

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
        } else if (data.getActionBefore().contains(Action_Before.COMPLETE) ||
                   data.getActionBefore().contains(Action_Before.RELEASE) ||
                   changes.contains(SampleMeta.getAnalysisStartedDate())) {
            ana = (AnalysisViewDO)data.getManager().getObject(data.getUid());
            if ( !analysisId.equals(ana.getId()))
                return data;
        } else if ( !data.getActionBefore().contains(Action_Before.QA) &&
                   !changes.contains(SampleMeta.getNeonatalIsTransfused()) &&
                   !changes.contains(SampleMeta.getNeonatalTransfusionDate())) {
            return data;
        }

        /*
         * set the value of interpretation based on whether or not the sample is
         * transfused etc.
         */
        setInterpretion(data, ana);

        return data;
    }

    /**
     * Sets the value of the analyte for interpretation based on the value of
     * Galactose 1 phosphate uridyl transferase and whether or not the sample is
     * transfused
     */
    private void setInterpretion(SampleSO data, AnalysisViewDO ana) {
        int i, j;
        Integer interp;
        String resVal, overVal;
        Double galtVal;
        SampleManager1 sm;
        ResultViewDO res, resInter;
        DictionaryDO dict;
        QaEventDO qa;
        TestManager tm;
        ResultFormatter rf;
        HashMap<Integer, AnalyteParameterViewDO> paramMap;

        sm = data.getManager();
        galtVal = null;
        overVal = null;
        resInter = null;
        /*
         * find the values for the various analytes
         */
        for (i = 0; i < sm.result.count(ana); i++ ) {
            for (j = 0; j < sm.result.count(ana, i); j++ ) {
                res = sm.result.get(ana, i, j);
                if ("nbs_galt".equals(res.getAnalyteExternalId())) {
                    resVal = res.getValue();
                    if (resVal != null) {
                        if (resVal.startsWith(">") || resVal.startsWith("<"))
                            resVal = resVal.substring(1);
                        galtVal = Double.valueOf(resVal);
                    }
                } else if ("nbs_override_inter".equals(res.getAnalyteExternalId())) {
                    overVal = res.getValue();
                } else if ("nbs_galt_inter".equals(res.getAnalyteExternalId())) {
                    resInter = res;
                }
            }
        }

        try {
            proxy.log(Level.FINE, "Finding the values of override interretation", null);
            /*
             * proceed only if the value for override interpretation is "No"
             */
            dict = getDictionaryByValue(overVal);
            if (dict == null || !"no".equals(dict.getSystemName()) || galtVal == null)
                return;
            
            /*
             * get this test's analyte parameters; they provide p-values for
             * "Galactose 1 phosphate uridyl transferase"
             */
            if (paramManager == null) {
                try {
                    paramManager = proxy.fetchParameters(ana.getTestId(), Constants.table().TEST);
                } catch (NotFoundException e) {
                    /*
                     * analyte parameters were not found; if this exception is
                     * not handled here, an error with the message "null" is
                     * shown, which is not very helpful; a more descriptive
                     * error will be added later if some computation(s) can't be
                     * done because of missing p-values
                     */
                }
            }
            paramMap = Util.getParameters(paramManager, data, ana);

            /*
             * the value of interpretation is "presumptive positive" if the
             * value of Galactose is between 3.8 and 99, the value of
             * interpretation is "borderline" if the value of Galactose is
             * between 3.2 and 3.7, the value of interpretation is
             * "within normal limits" if the value of Galactose is between 0 and
             * 3.1
             */
            proxy.log(Level.FINE,
                      "Getting the value for interpretation based on the value for Galactose 1 phosphate uridyl transferase",
                      null);
            if (galtVal >= 0 && galtVal < 3.2)
                interp = INTER_PP_NR;
            else if (galtVal < 3.8)
                interp = INTER_BORD;
            else
                interp = INTER_N;

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
                    qa = nbsCache1.getQaEvent(Cache.QA_TRAN, ana.getTestId());
                else
                    qa = nbsCache1.getQaEvent(Cache.QA_TRANU, ana.getTestId());
            }

            /*
             * if the initial interpretation is not "presumptive positive" then
             * it's overridden by other data
             */
            if ( !INTER_PP_NR.equals(interp)) {
                proxy.log(Level.FINE, "Setting the interpretation based on qa events", null);
                /*
                 * if the sample has reject qa events, the interpretation is
                 * "poor quality"; otherwise if the sample is transfused, the
                 * interpretation is "transfused" if transfusion date is
                 * specified or "transfused unknown" if it's not specified
                 */
                if (sm.qaEvent.hasType(Constants.dictionary().QAEVENT_OVERRIDE) ||
                    sm.qaEvent.hasType(Constants.dictionary().QAEVENT_WARNING)) {
                    interp = INTER_PQ;
                } else if (qa != null) {
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
}