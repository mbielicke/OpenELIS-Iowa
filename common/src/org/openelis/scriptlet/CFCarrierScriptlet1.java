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

import org.openelis.constants.Messages;
import org.openelis.domain.AnalysisViewDO;
import org.openelis.domain.Constants;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.ResultViewDO;
import org.openelis.manager.SampleManager1;
import org.openelis.manager.TestManager;
import org.openelis.scriptlet.SampleSO.Action_Before;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.FormErrorException;
import org.openelis.ui.scriptlet.ScriptletInt;
import org.openelis.ui.scriptlet.ScriptletObject.Status;

/**
 * The scriptlet for performing operations for
 * "cf-carrier (Cystic Fibrosis Carrier Screen)" test
 */
public class CFCarrierScriptlet1 implements ScriptletInt<SampleSO> {

    private ScriptletUtility scriptletUtility;

    private Proxy            proxy;

    private static final String TEST_NAME = "cf-carrier", METHOD_NAME = "pcr",
                    ETHNICITY = "cf_ethnicity", FAMILY_HISTORY = "cf_family_history",
                    RELATION = "cf_relation", RESULT = "result", INITIAL_RISK = "cf_initial_risk",
                    FINAL_RISK = "cf_final_risk";

    public CFCarrierScriptlet1(ScriptletUtility scriptletUtility, Proxy proxy) {
        this.scriptletUtility = scriptletUtility;
        this.proxy = proxy;

        proxy.log(Level.FINE, "Initializing CFCarrierScriptlet1");
    }

    @Override
    public SampleSO run(SampleSO data) {
        ResultViewDO res;
        AnalysisViewDO ana;
        TestManager tm;

        proxy.log(Level.FINE, "In CFCarrierScriptlet1.run");

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
                   data.getActionBefore().contains(Action_Before.ANALYSIS_QA_ADDED) ||
                   data.getActionBefore().contains(Action_Before.ANALYSIS_QA_REMOVED)) {
            /*
             * a sample or analysis qa event was added or removed; find the
             * analysis that's linked to the active version of this test
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
         * set the value of risks based on the value of this result
         */
        setRisks(data, ana, tm);

        return data;
    }

    /**
     * Set the initial and final risks based on ethnicity, family history,
     * relation and whether the sample has any reject QA events
     */
    private void setRisks(SampleSO data, AnalysisViewDO ana, TestManager tm) {
        int i, j;
        double ir, ir1, fr, initRisk, finalRisk;
        Integer histId, resultId;
        String ethVal, histVal, relatVal, resultVal, initRiskVal, finalRiskVal;
        SampleManager1 sm;
        ResultViewDO res, resInit, resFinal;

        /*
         * if the sample or analysis has any result override qa events then
         * don't compute the risks
         */
        proxy.log(Level.FINE,
                  "Determining if the sample or analysis has any result override qa events");

        sm = data.getManager();
        if (scriptletUtility.sampleHasRejectQA(sm, false) ||
            scriptletUtility.analysisHasRejectQA(sm, ana, false))
            return;

        /*
         * find the values for the various analytes
         */
        ethVal = null;
        histVal = null;
        relatVal = null;
        resultVal = null;
        resFinal = null;
        resInit = null;

        proxy.log(Level.FINE, "Finding the various analytes to get their values");
        for (i = 0; i < sm.result.count(ana); i++ ) {
            for (j = 0; j < sm.result.count(ana, i); j++ ) {
                res = sm.result.get(ana, i, j);
                if (ETHNICITY.equals(res.getAnalyteExternalId()))
                    ethVal = res.getValue();
                else if (FAMILY_HISTORY.equals(res.getAnalyteExternalId()))
                    histVal = res.getValue();
                else if (RELATION.equals(res.getAnalyteExternalId()))
                    relatVal = res.getValue();
                else if (RESULT.equals(res.getAnalyteExternalId()))
                    resultVal = res.getValue();
                else if (INITIAL_RISK.equals(res.getAnalyteExternalId()))
                    resInit = res;
                else if (FINAL_RISK.equals(res.getAnalyteExternalId()))
                    resFinal = res;
            }
        }

        try {
            proxy.log(Level.FINE,
                      "Checking if the ethinicity and family history have been specified");
            /*
             * if ethnicity or family history is not specified then that's an
             * error
             */
            if (ethVal == null) {
                data.setStatus(Status.FAILED);
                data.addException(new FormErrorException(Messages.get()
                                                                 .result_missingEthnicityException()));
                return;
            }

            if (histVal == null) {
                data.setStatus(Status.FAILED);
                data.addException(new FormErrorException(Messages.get()
                                                                 .result_missingFamilyHistoryException()));
                return;
            }

            /*
             * compute initial risk based on ethnicity and family history and
             * set it only if it's different from the current value
             */
            proxy.log(Level.FINE, "Computing initial risk based on ethnicity and family history");
            ir = getInitialEthnicityRisk(ethVal);
            histId = histVal != null ? Integer.valueOf(histVal) : null;

            if (scriptletUtility.FAM_HIST_DISORDER.equals(histId))
                ir1 = getDisorderRelationRisk(relatVal);
            else if (scriptletUtility.FAM_HIST_CARRIER.equals(histId))
                ir1 = getCarrierRelationRisk(relatVal);
            else
                ir1 = 1000000;

            initRisk = Math.min(ir, ir1);
            if (initRisk == 1000000)
                initRisk = 0;
            initRiskVal = String.valueOf(initRisk);

            if ( !DataBaseUtil.isSame(initRiskVal, resInit.getValue())) {
                proxy.log(Level.FINE, "Setting the value of initial risk as: " + initRiskVal);
                resInit.setValue(initRiskVal);
                data.addRerun(resInit.getAnalyteExternalId());
                data.getChangedUids().add(Constants.uid().getResult(resInit.getId()));
            }

            /*
             * compute final risk based on ethnicity, family history, result and
             * set it only if it's different from the current value
             */
            proxy.log(Level.FINE, "Computing final risk based on ethnicity, family history, result");
            fr = 1000000;
            resultId = resultVal != null ? Integer.valueOf(resultVal) : null;

            if (scriptletUtility.NORMAL.equals(resultId) ||
                scriptletUtility.NEGATIVE.equals(resultId)) {
                fr = getFinalEthnicityRisk(ethVal);
            } else if (resultId == null || scriptletUtility.NOT_TESTED.equals(resultId)) {
                fr = initRisk;
            } else if (resultId != null) {
                fr = 1;
            }

            finalRisk = fr;
            if (finalRisk == 1000000)
                finalRisk = 0;
            finalRiskVal = String.valueOf(finalRisk);

            if ( !DataBaseUtil.isSame(finalRiskVal, resFinal.getValue())) {
                proxy.log(Level.FINE, "Setting the value of final risk as: " + finalRiskVal);
                resFinal.setValue(finalRiskVal);
                data.addRerun(resFinal.getAnalyteExternalId());
                data.getChangedUids().add(Constants.uid().getResult(resFinal.getId()));
            }
        } catch (Exception e) {
            data.setStatus(Status.FAILED);
            data.addException(e);
        }
    }

    /**
     * Computes initial risk based on ethnicity
     */
    private double getInitialEthnicityRisk(String ethVal) throws Exception {
        double risk;
        DictionaryDO dict;

        risk = 1000000;
        dict = getDictionaryByValue(ethVal);

        if (scriptletUtility.ETHN_CAUCASIAN.equals(dict.getId()) ||
            scriptletUtility.ETHN_JEWISH.equals(dict.getId()))
            risk = 29;
        else if (scriptletUtility.ETHN_HISPANIC.equals(dict.getId()))
            risk = 46;
        else if (scriptletUtility.ETHN_AFRICAN.equals(dict.getId()))
            risk = 65;
        else if (scriptletUtility.ETHN_ASIAN.equals(dict.getId()))
            risk = 90;

        return risk;
    }

    /**
     * Computes disorder risk based on relation
     */
    private double getDisorderRelationRisk(String relatVal) throws Exception {
        double risk;
        DictionaryDO dict;

        risk = 1000000;
        dict = getDictionaryByValue(relatVal);
        if (dict == null)
            return risk;

        if (scriptletUtility.RELAT_PARENT.equals(dict.getId()) ||
            scriptletUtility.RELAT_DAUGHTER.equals(dict.getId()) ||
            scriptletUtility.RELAT_INDIVIDUAL.equals(dict.getId()))
            risk = 1;
        else if (scriptletUtility.RELAT_SIBLING.equals(dict.getId()))
            risk = 1.5;
        else if (scriptletUtility.RELAT_AUNT.equals(dict.getId()))
            risk = 3;
        else if (scriptletUtility.RELAT_NIECE.equals(dict.getId()))
            risk = 2;
        else if (scriptletUtility.RELAT_COUSIN.equals(dict.getId()))
            risk = 4;
        else if (scriptletUtility.RELAT_8.equals(dict.getId()))
            risk = 8;
        else if (scriptletUtility.RELAT_16.equals(dict.getId()))
            risk = 16;

        return risk;
    }

    /**
     * Computes carrier risk based on relation
     */
    private double getCarrierRelationRisk(String relatVal) throws Exception {
        double risk;
        DictionaryDO dict;

        risk = 1000000;
        dict = getDictionaryByValue(relatVal);
        if (dict == null)
            return risk;

        if (scriptletUtility.RELAT_INDIVIDUAL.equals(dict.getId()))
            risk = 1;
        else if (scriptletUtility.RELAT_PARENT.equals(dict.getId()) ||
                 scriptletUtility.RELAT_SIBLING.equals(dict.getId()) ||
                 scriptletUtility.RELAT_DAUGHTER.equals(dict.getId()))
            risk = 2;
        else if (scriptletUtility.RELAT_AUNT.equals(dict.getId()) ||
                 scriptletUtility.RELAT_NIECE.equals(dict.getId()))
            risk = 4;
        else if (scriptletUtility.RELAT_COUSIN.equals(dict.getId()) ||
                 scriptletUtility.RELAT_8.equals(dict.getId()))
            risk = 8;
        else if (scriptletUtility.RELAT_16.equals(dict.getId()))
            risk = 16;

        return risk;
    }

    /**
     * Computes final risk based on ethnicity
     */
    private double getFinalEthnicityRisk(String ethVal) throws Exception {
        double risk;
        DictionaryDO dict;

        risk = 1000000;
        dict = getDictionaryByValue(ethVal);

        if (scriptletUtility.ETHN_CAUCASIAN.equals(dict.getId()))
            risk = 235;
        else if (scriptletUtility.ETHN_JEWISH.equals(dict.getId()))
            risk = 930;
        else if (scriptletUtility.ETHN_HISPANIC.equals(dict.getId()))
            risk = 105;
        else if (scriptletUtility.ETHN_AFRICAN.equals(dict.getId()))
            risk = 207;

        return risk;
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

        public void log(Level level, String message);
    }
}