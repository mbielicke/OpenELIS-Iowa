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
import org.openelis.domain.ResultViewDO;
import org.openelis.manager.SampleManager1;
import org.openelis.manager.TestManager;
import org.openelis.scriptlet.SampleSO.Action_Before;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.FormErrorException;
import org.openelis.ui.scriptlet.ScriptletInt;
import org.openelis.ui.scriptlet.ScriptletObject.Status;
import org.openelis.utilcommon.ResultFormatter;
import org.openelis.utilcommon.ResultHelper;
import org.openelis.utilcommon.ResultFormatter.FormattedValue;

/**
 * The scriptlet for performing operations for
 * "cf-carrier (Cystic Fibrosis Carrier Screen)" test
 */
public class CFCarrierScriptlet1 implements ScriptletInt<SampleSO> {

    private CFScriptlet1Proxy proxy;

    private Integer           analysisId;

    private static final String PREG_TEST_NAME = "cf-pregnancy", METHOD_NAME = "pcr",
                    ETHNICITY = "cf_ethnicity", FAMILY_HISTORY = "cf_family_history",
                    RELATION = "cf_relation", RESULT = "result", INITIAL_RISK = "cf_initial_risk",
                    FINAL_RISK = "cf_final_risk";

    private CFRisk1             cfRisk1;

    public CFCarrierScriptlet1(CFScriptlet1Proxy proxy, Integer analysisId) {
        this.proxy = proxy;
        this.analysisId = analysisId;

        proxy.log(Level.FINE, "Initializing CFCarrierScriptlet1", null);

        if (cfRisk1 == null)
            cfRisk1 = CFRisk1.getInstance(proxy);

        proxy.log(Level.FINE, "Initialized CFCarrierScriptlet1", null);
    }

    @Override
    public SampleSO run(SampleSO data) {
        int i;
        Integer accNum;
        AnalysisViewDO ana, prAna;

        proxy.log(Level.FINE, "In CFCarrierScriptlet1.run", null);
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
            } else if (data.getActionBefore().contains(Action_Before.UNRELEASE)) {
                /*
                 * an analysis is being unreleased; find out if it's the one
                 * managed by this scriptlet
                 */
                ana = (AnalysisViewDO)data.getManager().getObject(data.getUid());
                if ( !analysisId.equals(ana.getId()))
                    return data;

                /*
                 * don't let the analysis be unreleased if there's a released
                 * cf-pregnancy analysis on the sample
                 */
                for (i = 0; i < data.getManager().analysis.count(); i++ ) {
                    prAna = data.getManager().analysis.get(i);
                    if (PREG_TEST_NAME.equals(prAna.getTestName()) &&
                        METHOD_NAME.equals(prAna.getMethodName()) &&
                        Constants.dictionary().ANALYSIS_RELEASED.equals(prAna.getStatusId())) {
                        accNum = data.getManager().getSample().getAccessionNumber();
                        /*
                         * for display
                         */
                        if (accNum == null)
                            accNum = 0;
                        data.setStatus(Status.FAILED);
                        data.addException(new FormErrorException(Messages.get()
                                                                         .analysis_cantUnreleaseCarrierException(accNum,
                                                                                                                 ana.getTestName(),
                                                                                                                 ana.getMethodName(),
                                                                                                                 PREG_TEST_NAME,
                                                                                                                 METHOD_NAME)));
                        return data;
                    }
                }
            } else if (data.getActionBefore().contains(Action_Before.QA)) {
                /*
                 * a sample or analysis qa event was added or removed; find the
                 * analysis managed by this scriptlet
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
         * don't do anything if the analysis is not in the manager anymore or if
         * it is released or cancelled
         */
        if (ana == null || Constants.dictionary().ANALYSIS_RELEASED.equals(ana.getStatusId()) ||
            Constants.dictionary().ANALYSIS_CANCELLED.equals(ana.getStatusId()))
            return data;

        /*
         * set the value of risks based on the value of this result
         */
        setRisks(data, ana);

        return data;
    }

    /**
     * Set the initial and final risks based on ethnicity, family history,
     * relation and whether the sample has any reject QA events
     */
    private void setRisks(SampleSO data, AnalysisViewDO ana) {
        int i, j;
        double initRisk, finalRisk;
        Integer ethnicityId, famHistId, relationId, resultId;
        String value;
        SampleManager1 sm;
        ResultViewDO res, resInit, resFinal;
        TestManager tm;
        ResultFormatter rf;

        sm = data.getManager();
        /*
         * find the values for the various analytes
         */
        ethnicityId = null;
        famHistId = null;
        relationId = null;
        resultId = null;
        resInit = null;
        resFinal = null;

        proxy.log(Level.FINE, "Finding the various analytes and getting their values", null);
        for (i = 0; i < sm.result.count(ana); i++ ) {
            for (j = 0; j < sm.result.count(ana, i); j++ ) {
                res = sm.result.get(ana, i, j);
                if (ETHNICITY.equals(res.getAnalyteExternalId()))
                    ethnicityId = res.getValue() != null ? Integer.valueOf(res.getValue()) : null;
                else if (FAMILY_HISTORY.equals(res.getAnalyteExternalId()))
                    famHistId = res.getValue() != null ? Integer.valueOf(res.getValue()) : null;
                else if (RELATION.equals(res.getAnalyteExternalId()))
                    relationId = res.getValue() != null ? Integer.valueOf(res.getValue()) : null;
                else if (RESULT.equals(res.getAnalyteExternalId()))
                    resultId = res.getValue() != null ? Integer.valueOf(res.getValue()) : null;
                else if (INITIAL_RISK.equals(res.getAnalyteExternalId()))
                    resInit = res;
                else if (FINAL_RISK.equals(res.getAnalyteExternalId()))
                    resFinal = res;
            }
        }

        /*
         * if the sample or analysis has any result override qa events then
         * don't compute the risks
         */
        proxy.log(Level.FINE,
                  "Determining if the sample or analysis has any result override qa events",
                  null);
        try {
            tm = (TestManager)data.getCache().get(Constants.uid().getTest(ana.getTestId()));
            rf = tm.getFormatter();
            if (ScriptletUtility.sampleHasRejectQA(sm, false) ||
                ScriptletUtility.analysisHasRejectQA(sm, ana, false)) {
                /*
                 * blank the risks
                 */
                setValue(resInit, null, rf, ana, data);
                setValue(resFinal, null, rf, ana, data);
                return;
            }

            proxy.log(Level.FINE,
                      "Checking if the ethnicity and family history have been specified",
                      null);
            /*
             * if ethnicity or family history is not specified then that's an
             * error
             */
            if (ethnicityId == null) {
                data.setStatus(Status.FAILED);
                data.addException(new FormErrorException(Messages.get()
                                                                 .result_missingEthnicityException()));
                /*
                 * blank the risks
                 */
                setValue(resInit, null, rf, ana, data);
                setValue(resFinal, null, rf, ana, data);
                return;
            }

            if (famHistId == null) {
                data.setStatus(Status.FAILED);
                data.addException(new FormErrorException(Messages.get()
                                                                 .result_missingFamilyHistoryException()));
                /*
                 * blank the risks
                 */
                setValue(resInit, null, rf, ana, data);
                setValue(resFinal, null, rf, ana, data);
                return;
            }

            /*
             * compute initial risk based on ethnicity and family history and
             * set it only if it's different from the current value
             */
            proxy.log(Level.FINE,
                      "Computing initial risk based on ethnicity, family history and relation",
                      null);

            initRisk = cfRisk1.computeInitialRisk(ethnicityId, famHistId, relationId);
            value = String.valueOf(initRisk);
            setValue(resInit, value, rf, ana, data);

            /*
             * compute final risk based on ethnicity, family history, result and
             * set it only if it's different from the current value
             */
            proxy.log(Level.FINE,
                      "Computing final risk based on ethnicity, family history and result",
                      null);

            finalRisk = cfRisk1.computeFinalRisk(ethnicityId, resultId, initRisk);
            value = String.valueOf(finalRisk);
            setValue(resFinal, value, rf, ana, data);
        } catch (Exception e) {
            data.setStatus(Status.FAILED);
            data.addException(e);
        }
    }

    /**
     * Sets the passed value in the passed result; if isDict is true, it means
     * that the value is the id of a dictionary entry
     */
    private void setValue(ResultViewDO result, String value, ResultFormatter rf,
                          AnalysisViewDO ana, SampleSO data) throws Exception {
        boolean isChanged;
        FormattedValue fv;

        isChanged = false;
        if (value != null) {
            fv = rf.format(result.getResultGroup(), ana.getUnitOfMeasureId(), value);
            if ( !DataBaseUtil.isSame(result.getValue(), fv.getDisplay())) {
                proxy.log(Level.FINE, "Setting the value of " + result.getAnalyte() + " as: " +
                                      value, null);
                ResultHelper.formatValue(result, value, ana.getUnitOfMeasureId(), rf);
                isChanged = true;
            }
        } else if (result.getValue() != null) {
            /*
             * ResultHelper is used to format values entered by the user, so it
             * doesn't handle null values
             */
            proxy.log(Level.FINE,
                      "Setting the value of " + result.getAnalyte() + " as: " + value,
                      null);
            result.setValue(null);
            result.setTypeId(null);
            result.setTestResultId(null);
            isChanged = true;
        }

        if (isChanged) {
            data.addRerun(result.getAnalyteExternalId());
            data.getChangedUids().add(Constants.uid().getResult(result.getId()));
        }
    }
}