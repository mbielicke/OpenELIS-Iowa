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
package org.openelis.scriptlet.nbs.tsh;

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
 * The scriptlet for "nbs galt (Galactosemia)" test. TODO add comments for the
 * algorithm used by the scriptlet after it's been fully implemented with the
 * use of analyte parameters for upper and lower limits.
 */
public class Scriptlet implements ScriptletInt<SampleSO> {

    private ScriptletProxy proxy;

    private Integer            analysisId;

    private static Integer     INTER_N, INTER_PP_NR, INTER_EC, INTER_ECU, INTER_PQ, INTER_BORD;

    private Cache          nbsCache1;

    public Scriptlet(ScriptletProxy proxy, Integer analysisId) throws Exception {
        this.proxy = proxy;
        this.analysisId = analysisId;

        proxy.log(Level.FINE, "Initializing NbsTshScriptlet1", null);

        if (INTER_N == null) {
            INTER_N = proxy.getDictionaryBySystemName("newborn_inter_n").getId();
            INTER_PP_NR = proxy.getDictionaryBySystemName("newborn_inter_pp_nr").getId();
            INTER_ECU = proxy.getDictionaryBySystemName("newborn_inter_ecu").getId();
            INTER_PQ = proxy.getDictionaryBySystemName("newborn_inter_pq").getId();
            INTER_BORD = proxy.getDictionaryBySystemName("newborn_inter_bord").getId();
        }

        if (nbsCache1 == null)
            nbsCache1 = Cache.getInstance(proxy);

        proxy.log(Level.FINE, "Initialized NbsTshScriptlet1", null);
    }

    @Override
    public SampleSO run(SampleSO data) {
        AnalysisViewDO ana;
        ResultViewDO res;

        proxy.log(Level.FINE, "In NbsTshScriptlet1.run", null);
        ana = (AnalysisViewDO)data.getManager().getObject(Constants.uid().getAnalysis(analysisId));
        if (ana == null || Constants.dictionary().ANALYSIS_RELEASED.equals(ana.getStatusId()) ||
            Constants.dictionary().ANALYSIS_CANCELLED.equals(ana.getStatusId()))
            return data;

        /*
         * manage result changed, a qa event added/removed, and the collection
         * age changing
         */
        if (data.getActionBefore().contains(Action_Before.RESULT)) {
            res = (ResultViewDO)data.getManager().getObject(data.getUid());
            if ( !analysisId.equals(res.getAnalysisId()))
                return data;
        } else if ( !data.getActionBefore().contains(Action_Before.QA) &&
                   !SampleMeta.getNeonatalCollectionAge().equals(data.getChanged())) {
            return data;
        }

        /*
         * set the value of interpretation based on the value of this result
         */
        setInterpretation(data, ana);

        return data;
    }

    /**
     * Sets the value of the analyte for interpretation based on the value of
     * Thyroid Stimulating Hormone and whether or not the sample is transfused
     */
    private void setInterpretation(SampleSO data, AnalysisViewDO ana) {
        int i, j;
        boolean lt;
        Integer interp, colAge;
        String sign;
        Double tshVal;
        SampleManager1 sm;
        SampleNeonatalViewDO sn;
        ResultViewDO res, resTsh, resOver, resInter;
        DictionaryDO dict;
        QaEventDO qa;
        AnalysisQaEventViewDO aqa;
        TestManager tm;
        ResultFormatter rf;

        sm = data.getManager();
        sn = sm.getSampleNeonatal();
        resTsh = null;
        resOver = null;
        resInter = null;

        proxy.log(Level.FINE,
                  "Going through the manager to find the result that triggered the scriptlet",
                  null);
        /*
         * find the values for the various analytes
         */
        for (i = 0; i < sm.result.count(ana); i++ ) {
            for (j = 0; j < sm.result.count(ana, i); j++ ) {
                res = sm.result.get(ana, i, j);
                if ("nbs_tsh".equals(res.getAnalyteExternalId()))
                    resTsh = res;
                else if ("nbs_override_inter".equals(res.getAnalyteExternalId()))
                    resOver = res;
                else if ("nbs_tsh_inter".equals(res.getAnalyteExternalId()))
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
            proxy.log(Level.FINE, "Finding the values of override interpretation", null);
            /*
             * proceed only if the value for override interpretation has been
             * validated and is "No"
             */
            if (resOver.getTypeId() == null)
                return;

            dict = resOver.getValue() == null ? null
                                             : proxy.getDictionaryById(Integer.valueOf(resOver.getValue()));
            if (dict == null || !"no".equals(dict.getSystemName()) || resTsh.getValue() == null)
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
            proxy.log(Level.FINE,
                      "Getting the value for interretation based on the value for tsh",
                      null);

            if ( (tshVal >= 0 && tshVal < 25.0) || (tshVal == 25.0 && lt))
                interp = INTER_N;
            else if (tshVal < 60.0 || (tshVal == 60.0 && lt))
                interp = INTER_BORD;
            else
                interp = INTER_PP_NR;

            proxy.log(Level.FINE, "Finding the qa event to be added to the analysis", null);
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
                    qa = nbsCache1.getQaEvent(Cache.QA_EC, ana.getTestId());
                else
                    qa = nbsCache1.getQaEvent(Cache.QA_ECU, ana.getTestId());
            }

            /*
             * add the qa event if it's not already added
             */
            if (qa != null && !sm.qaEvent.hasName(ana, qa.getName())) {
                proxy.log(Level.FINE, "Adding the qa event: " + qa.getName(), null);
                aqa = sm.qaEvent.add(ana, qa);
                data.addChangedUid(Constants.uid().getAnalysisQAEvent(aqa.getId()));
            }

            /*
             * the original interpretation overrides qa events if it's
             * "presumptive positive"; otherwise it's overridden by other data
             */
            if ( !INTER_PP_NR.equals(interp)) {
                proxy.log(Level.FINE, "Setting the interpretation based on qa events", null);
                if (sm.qaEvent.hasType(Constants.dictionary().QAEVENT_OVERRIDE) ||
                    sm.qaEvent.hasType(Constants.dictionary().QAEVENT_WARNING)) {
                    /*
                     * the sample has reject qas so set the interpretation as
                     * "poor quality"
                     */
                    interp = INTER_PQ;
                } else if ( !INTER_BORD.equals(interp)) {
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
                        if (Cache.QA_EC.equals(qa.getName()))
                            interp = INTER_EC;
                        else if (Cache.QA_ECU.equals(qa.getName()))
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
                tm = (TestManager)data.getCache().get(Constants.uid().getTest(ana.getTestId()));
                rf = tm.getFormatter();
                dict = proxy.getDictionaryById(interp);
                if (ResultHelper.formatValue(resInter,
                                             dict.getEntry(),
                                             ana.getUnitOfMeasureId(),
                                             rf)) {
                    proxy.log(Level.FINE, "Setting the value of interpretation as: " +
                                          dict.getEntry(), null);
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
}