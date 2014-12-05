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

import java.util.HashMap;
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
import org.openelis.ui.common.NotFoundException;
import org.openelis.ui.scriptlet.ScriptletInt;
import org.openelis.ui.scriptlet.ScriptletObject.Status;
import org.openelis.utilcommon.ResultFormatter;
import org.openelis.utilcommon.ResultFormatter.FormattedValue;
import org.openelis.utilcommon.ResultHelper;

/**
 * The scriptlet for performing operations for
 * "cf-pregnancy (Cystic Fibrosis Pregnancy Screen)" test
 */
public class CFPregnancyScriptlet1 implements ScriptletInt<SampleSO> {

    private CFScriptlet1Proxy proxy;

    private Integer           analysisId, carrierIndex;

    private static final String CARRIER_TEST_NAME = "cf-carrier", METHOD_NAME = "pcr",
                    IND_ETHNICITY = "cf_ind_ethnicity", IND_FAMILY_HISTORY = "cf_ind_fam_history",
                    IND_RELATION = "cf_ind_relation", IND_RESULT = "cf_ind_result",
                    IND_INITIAL_RISK = "cf_ind_initial_risk", IND_FINAL_RISK = "cf_ind_final_risk",
                    PART_ACCESSION = "cf_part_accession", PART_ETHNICITY = "cf_part_ethnicity",
                    PART_FAM_HISTORY = "cf_part_fam_history", PART_RELATION = "cf_part_relation",
                    PART_RESULT = "cf_part_result", PART_INITIAL_RISK = "cf_part_initial_risk",
                    PART_FINAL_RISK = "cf_part_final_risk",
                    PREG_INITIAL_RISK = "cf_preg_initial_risk",
                    PREG_FINAL_RISK = "cf_preg_final_risk", PREG_RESULT = "cf_preg_result",
                    CAR_ETHNICITY = "cf_ethnicity", CAR_FAMILY_HISTORY = "cf_family_history",
                    CAR_RELATION = "cf_relation", CAR_RESULT = "result",
                    CAR_INITIAL_RISK = "cf_initial_risk", CAR_FINAL_RISK = "cf_final_risk";

    private static CFRisk1      cfRisk1;

    public CFPregnancyScriptlet1(CFScriptlet1Proxy proxy, Integer analysisId) {
        this.proxy = proxy;
        this.analysisId = analysisId;

        proxy.log(Level.FINE, "Initializing CFPregnancyScriptlet1", null);

        if (cfRisk1 == null)
            cfRisk1 = CFRisk1.getInstance(proxy);

        proxy.log(Level.FINE, "Initialized CFPregnancyScriptlet1", null);
    }

    @Override
    public SampleSO run(SampleSO data) {
        Integer accNum;
        AnalysisViewDO ana, crAna;
        ResultViewDO res;
        SampleManager1 psm;

        proxy.log(Level.FINE, "In CFPregnancyScriptlet1.run", null);
        try {
            psm = null;
            if (data.getActionBefore().contains(Action_Before.RESULT)) {
                /*
                 * find out if the changed result belongs to the analysis
                 * managed by this scriptlet; don't do anything if it doesn't
                 */
                if ( !ScriptletUtility.isManagedResult(data, analysisId))
                    return data;
                ana = (AnalysisViewDO)data.getManager()
                                          .getObject(Constants.uid().getAnalysis(analysisId));
                /*
                 * find out if a new partner accession number was specified and
                 * if it was then try to fetch that sample
                 */
                res = (ResultViewDO)data.getManager().getObject(data.getUid());

                if (PART_ACCESSION.equals(res.getAnalyteExternalId()) && res.getValue() != null) {
                    accNum = Integer.valueOf(res.getValue());
                    /*
                     * partner accession number can't be the same as the current
                     * sample's accession number
                     */
                    if (data.getManager().getSample().getAccessionNumber().equals(accNum)) {
                        data.setStatus(Status.FAILED);
                        data.addException(new FormErrorException(Messages.get()
                                                                         .sample_partAccCantBeSameAsIndException()));
                        return data;
                    }
                    try {
                        proxy.log(Level.FINE,
                                  "Fetching sample with accession number: " + accNum,
                                  null);
                        psm = proxy.fetchByAccession(accNum, SampleManager1.Load.RESULT);
                    } catch (NotFoundException e) {
                        data.setStatus(Status.FAILED);
                        data.addException(new FormErrorException(Messages.get()
                                                                         .gen_noRecordsFound()));
                        return data;
                    }
                }
            } else if (data.getActionBefore().contains(Action_Before.QA) ||
                       data.getActionBefore().contains(Action_Before.RECOMPUTE)) {
                /*
                 * a sample or analysis qa event was added or removed or the
                 * risks need to be recomputed e.g. on updating a sample; find
                 * the analysis managed by this scriptlet
                 */
                ana = (AnalysisViewDO)data.getManager()
                                          .getObject(Constants.uid().getAnalysis(analysisId));
            } else if (data.getActionBefore().contains(Action_Before.COMPLETE) ||
                       data.getActionBefore().contains(Action_Before.RELEASE)) {

                /*
                 * an analysis got completed or released; don't anything if it's
                 * not a cf-carrier analysis or the one managed by this
                 * scriptlet
                 */
                crAna = (AnalysisViewDO)data.getManager().getObject(data.getUid());
                if ( !analysisId.equals(crAna.getId()) && !isValidCarrier(crAna))
                    return data;
                /*
                 * find the analysis managed by this scriptlet
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

        /*
         * set the value of risks based on the value of this result
         */
        setRisks(data, ana, psm);

        return data;
    }

    /**
     * Set the pregnancy initial and final risks and result based on individual
     * and partner risks
     */
    private void setRisks(SampleSO data, AnalysisViewDO ana, SampleManager1 psm) {
        Integer index, pregResult;
        String value;
        Double indRisk, partRisk, pregRisk;
        SampleManager1 sm;
        AnalysisViewDO crAna;
        ResultViewDO resInit, resFinal, resResult;
        TestManager tm;
        ResultFormatter rf;
        HashMap<String, ResultViewDO> prs, crs;

        /*
         * find the carrier analysis; don't do anything if it's not found
         */
        proxy.log(Level.FINE, "Finding the analysis for the cf-carrier test", null);
        crAna = getCarrierAnalysis(data);
        if (crAna == null)
            return;

        sm = data.getManager();
        /*
         * find the values for the various analytes for the pregnancy and
         * carrier analysis
         */
        proxy.log(Level.FINE,
                  "Finding the various analytes for cf-pregnancy and cf-carrier to get their values",
                  null);
        prs = getResults(sm, ana);
        crs = getResults(sm, crAna);

        tm = (TestManager)data.getCache().get(Constants.uid().getTest(ana.getTestId()));
        try {
            /*
             * copy the results from cf-carrier on the same sample
             */
            rf = tm.getFormatter();
            proxy.log(Level.FINE,
                      "Copying results from cf-carrier on the same sample to cf-pregnancy",
                      null);
            copyValue(crs.get(CAR_ETHNICITY), prs.get(IND_ETHNICITY), rf, ana, data);
            copyValue(crs.get(CAR_FAMILY_HISTORY), prs.get(IND_FAMILY_HISTORY), rf, ana, data);
            copyValue(crs.get(CAR_RELATION), prs.get(IND_RELATION), rf, ana, data);
            copyValue(crs.get(CAR_RESULT), prs.get(IND_RESULT), rf, ana, data);
            copyValue(crs.get(CAR_INITIAL_RISK), prs.get(IND_INITIAL_RISK), rf, ana, data);
            copyValue(crs.get(CAR_FINAL_RISK), prs.get(IND_FINAL_RISK), rf, ana, data);

            if (psm != null) {
                /*
                 * a partner sample has been specified
                 */
                index = getCarrierIndex(psm);
                /*
                 * find the carrier analysis in the partner sample; don't do
                 * anything if it's not found
                 */
                if (index == null)                   
                    return;                

                crAna = psm.analysis.get(index);
                /*
                 * find the values for the various analytes for the carrier
                 * analysis
                 */
                crs = getResults(psm, crAna);
                /*
                 * copy the results from cf-carrier on the partner sample
                 */
                proxy.log(Level.FINE,
                          "Copying results from cf-carrier on the partner sample to cf-pregnancy",
                          null);
                copyValue(crs.get(CAR_ETHNICITY), prs.get(PART_ETHNICITY), rf, ana, data);
                copyValue(crs.get(CAR_FAMILY_HISTORY), prs.get(PART_FAM_HISTORY), rf, ana, data);
                copyValue(crs.get(CAR_RELATION), prs.get(PART_RELATION), rf, ana, data);
                copyValue(crs.get(CAR_RESULT), prs.get(PART_RESULT), rf, ana, data);
                copyValue(crs.get(CAR_INITIAL_RISK), prs.get(PART_INITIAL_RISK), rf, ana, data);
                copyValue(crs.get(CAR_FINAL_RISK), prs.get(PART_FINAL_RISK), rf, ana, data);
            }

            /*
             * result DOs for the pregnancy initial and final risks and result
             */
            resInit = prs.get(PREG_INITIAL_RISK);
            resFinal = prs.get(PREG_FINAL_RISK);
            resResult = prs.get(PREG_RESULT);

            /*
             * if the sample or analysis has any result override qa events then
             * don't compute the risks
             */
            proxy.log(Level.FINE,
                      "Determining if the sample or analysis has any result override qa events",
                      null);

            if (ScriptletUtility.sampleHasRejectQA(sm, false) ||
                ScriptletUtility.analysisHasRejectQA(sm, ana, false)) {
                /*
                 * blank the risks and result
                 */
                setValue(resInit, null, false, rf, ana, data);
                setValue(resFinal, null, false, rf, ana, data);
                setValue(resResult, null, false, rf, ana, data);

                return;
            }

            /*
             * find the individual and partner initial risks
             */
            value = prs.get(IND_INITIAL_RISK).getValue();
            indRisk = value != null ? Double.valueOf(value) : null;

            value = prs.get(PART_INITIAL_RISK).getValue();
            partRisk = value != null ? Double.valueOf(value) : null;

            /*
             * compute and set the pregnancy initial risk
             */
            pregRisk = cfRisk1.computePregnancyInitialRisk(indRisk, partRisk);
            value = pregRisk != null ? pregRisk.toString() : null;
            setValue(resInit, value, false, rf, ana, data);

            /*
             * find the individual and partner final risks
             */
            value = prs.get(IND_FINAL_RISK).getValue();
            indRisk = value != null ? Double.valueOf(value) : null;

            value = prs.get(PART_FINAL_RISK).getValue();
            partRisk = value != null ? Double.valueOf(value) : null;

            /*
             * compute and set the pregnancy final risk
             */
            pregRisk = cfRisk1.computePregnancyFinalRisk(indRisk, partRisk);
            value = pregRisk != null ? pregRisk.toString() : null;
            setValue(resFinal, value, false, rf, ana, data);

            /*
             * compute and set the pregnancy result
             */
            pregRisk = resFinal.getValue() != null ? Double.valueOf(resFinal.getValue()) : null;
            pregResult = cfRisk1.computePregnancyResult(pregRisk);
            value = pregResult != null ? pregResult.toString() : null;
            setValue(resResult, value, value != null, rf, ana, data);
        } catch (Exception e) {
            data.setStatus(Status.FAILED);
            data.addException(e);
        }
    }

    /**
     * Returns a mapping between analyte external id and the result DO for that
     * analyte
     */
    private HashMap<String, ResultViewDO> getResults(SampleManager1 sm, AnalysisViewDO ana) {
        int i, j;
        HashMap<String, ResultViewDO> results;
        ResultViewDO res;

        results = new HashMap<String, ResultViewDO>();
        for (i = 0; i < sm.result.count(ana); i++ ) {
            for (j = 0; j < sm.result.count(ana, i); j++ ) {
                res = sm.result.get(ana, i, j);
                results.put(res.getAnalyteExternalId(), res);
            }
        }

        return results;
    }

    /**
     * Returns the analysis for the cf-carrier test from the manager in the SO;
     * return null if no such analysis is found or if it's not completed or
     * released
     */
    private AnalysisViewDO getCarrierAnalysis(SampleSO data) {
        AnalysisViewDO ana;
        SampleManager1 sm;

        sm = data.getManager();

        if (carrierIndex == null || carrierIndex >= sm.analysis.count()) {
            /*
             * either the index of the carrier analysis was not obtained before
             * or it's not valid anymore, so try to find the index
             */
            carrierIndex = getCarrierIndex(sm);
        } else {
            /*
             * the index is valid, get the analysis at the index and see if it's
             * a valid carrier analysis; if it's not then try to find the index
             * of the first valid one
             */
            ana = sm.analysis.get(carrierIndex);
            if (isValidCarrier(ana))
                return ana;
            else
                carrierIndex = getCarrierIndex(sm);
        }

        /*
         * no valid carrier analysis was found
         */
        if (carrierIndex == null)
            return null;

        return sm.analysis.get(carrierIndex);
    }

    /**
     * Returns the index of the first completed or released analysis for the
     * cf-carrier test; returns null if no such analysis is found
     */
    private Integer getCarrierIndex(SampleManager1 sm) {
        AnalysisViewDO ana;

        for (int i = 0; i < sm.analysis.count(); i++ ) {
            ana = sm.analysis.get(i);
            if (isValidCarrier(ana))
                return i;
        }

        return null;
    }

    /**
     * Returns true if the passed analysis is for the cf-carrier test and is
     * completed or released; returns false otherwise
     */
    private boolean isValidCarrier(AnalysisViewDO ana) {
        return CARRIER_TEST_NAME.equals(ana.getTestName()) &&
               METHOD_NAME.equals(ana.getMethodName()) &&
               (Constants.dictionary().ANALYSIS_COMPLETED.equals(ana.getStatusId()) || Constants.dictionary().ANALYSIS_RELEASED.equals(ana.getStatusId()));
    }

    /**
     * Sets the value of the "from" result as the value of the "to" result if
     * the two values are different
     */
    private void copyValue(ResultViewDO fromResult, ResultViewDO toResult, ResultFormatter rf,
                           AnalysisViewDO ana, SampleSO data) throws Exception {
        boolean isDict;

        if (DataBaseUtil.isSame(fromResult.getValue(), toResult.getValue()) &&
            DataBaseUtil.isSame(fromResult.getTypeId(), toResult.getTypeId()))
            return;

        isDict = Constants.dictionary().TEST_RES_TYPE_DICTIONARY.equals(fromResult.getTypeId());

        setValue(toResult, fromResult.getValue(), isDict, rf, ana, data);
    }

    /**
     * Sets the passed value in the passed result; if isDict is true, it means
     * that the value is the id of a dictionary entry
     */
    private void setValue(ResultViewDO result, String value, boolean isDict, ResultFormatter rf,
                          AnalysisViewDO ana, SampleSO data) throws Exception {
        boolean isChanged;
        String val;
        FormattedValue fv;

        isChanged = false;
        if (value != null) {
            if (isDict)
                val = proxy.getDictionaryById(Integer.valueOf(value)).getEntry();
            else
                val = value;
            fv = rf.format(result.getResultGroup(), ana.getUnitOfMeasureId(), val);
            if ( !DataBaseUtil.isSame(result.getValue(), fv.getDisplay())) {
                proxy.log(Level.FINE,
                          "Setting the value of " + result.getAnalyte() + " as: " + val,
                          null);
                ResultHelper.formatValue(result, val, ana.getUnitOfMeasureId(), rf);
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
            if (result.getDictionary() != null)
                result.setDictionary(null);
            isChanged = true;
        }

        if (isChanged) {
            data.addRerun(result.getAnalyteExternalId());
            data.getChangedUids().add(Constants.uid().getResult(result.getId()));
        }
    }
}