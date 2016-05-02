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
package org.openelis.scriptlet.cf.carrier;

import java.util.ArrayList;
import java.util.logging.Level;

import org.openelis.constants.Messages;
import org.openelis.domain.AnalysisViewDO;
import org.openelis.domain.Constants;
import org.openelis.domain.ResultViewDO;
import org.openelis.manager.SampleManager1;
import org.openelis.manager.TestManager;
import org.openelis.scriptlet.SampleSO;
import org.openelis.scriptlet.SampleSO.Action_Before;
import org.openelis.scriptlet.cf.Risk;
import org.openelis.scriptlet.cf.ScriptletProxy;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.FormErrorException;
import org.openelis.ui.scriptlet.ScriptletInt;
import org.openelis.ui.scriptlet.ScriptletObject.Status;
import org.openelis.utilcommon.ResultFormatter;
import org.openelis.utilcommon.ResultHelper;

/**
 * The scriptlet for "cf-carrier (Cystic Fibrosis Carrier Screen)" test. It
 * computes the initial and final risks based on various values like ethnicity
 * and family history, if the results are not overriden, otherwise it blanks the
 * risks and result. The risks are computed as soon as the user changes any of
 * the values or on completing/releasing the cf-carrier analysis. It doesn't
 * allow the cf-carrier analysis to be unreleased if there's a released
 * cf-pregnancy analysis on the sample.
 */
public class Scriptlet implements ScriptletInt<SampleSO> {

    private ScriptletProxy proxy;

    private Integer           analysisId, pregnancyId;

    private static final String PREG_TEST_NAME = "cf-pregnancy", METHOD_NAME = "pcr";

    private Risk             risk1;

    public Scriptlet(ScriptletProxy proxy, Integer analysisId) {
        this.proxy = proxy;
        this.analysisId = analysisId;

        proxy.log(Level.FINE, "Initializing CFCarrierScriptlet1", null);

        if (risk1 == null)
            risk1 = Risk.getInstance(proxy);

        proxy.log(Level.FINE, "Initialized CFCarrierScriptlet1", null);
    }

    @Override
    public SampleSO run(SampleSO data) {
        Integer accession;
        SampleManager1 sm;
        AnalysisViewDO ana, prAna;
        ResultViewDO res;

        proxy.log(Level.FINE, "In CFCarrierScriptlet1.run", null);

        sm = data.getManager();
        ana = (AnalysisViewDO)sm.getObject(Constants.uid().getAnalysis(analysisId));
        if (ana == null || Constants.dictionary().ANALYSIS_CANCELLED.equals(ana.getStatusId()))
            return data;

        /*
         * manage result changed, the analysis getting completed, released or
         * unreleased and a qa event added/removed; the analysis can't be
         * allowed to be completed or released if the results for the risks,
         * which are read-only, are removed by the user
         */
        if (data.getActionBefore().contains(Action_Before.RESULT)) {
            res = (ResultViewDO)sm.getObject(data.getUid());
            if ( !analysisId.equals(res.getAnalysisId()))
                return data;
        } else if (data.getActionBefore().contains(Action_Before.UNRELEASE)) {
            ana = (AnalysisViewDO)sm.getObject(data.getUid());
            if ( !analysisId.equals(ana.getId()))
                return data;

            /*
             * don't let the analysis be unreleased if there's a released
             * cf-pregnancy analysis on the sample
             */
            if (pregnancyId == null) {
                pregnancyId = getReleasedPregnancyId(sm);
            } else {
                prAna = (AnalysisViewDO)sm.getObject(Constants.uid().getAnalysis(pregnancyId));
                if ( !isReleasedPregnancy(prAna))
                    pregnancyId = getReleasedPregnancyId(sm);
            }
            accession = sm.getSample().getAccessionNumber();
            if (pregnancyId != null) {
                data.setStatus(Status.FAILED);
                data.addException(new FormErrorException(Messages.get()
                                                                 .analysis_cantUnreleaseCarrierException(accession != null ? accession
                                                                                                                          : 0,
                                                                                                         ana.getTestName(),
                                                                                                         ana.getMethodName(),
                                                                                                         PREG_TEST_NAME,
                                                                                                         METHOD_NAME)));
                return data;
            }
        } else if (data.getActionBefore().contains(Action_Before.COMPLETE) ||
                   data.getActionBefore().contains(Action_Before.RELEASE)) {
            ana = (AnalysisViewDO)sm.getObject(data.getUid());
            if ( !analysisId.equals(ana.getId()))
                return data;
        } else if ( !data.getActionBefore().contains(Action_Before.QA)) {
            return data;
        }

        /*
         * check for released here because the analysis would be released even
         * on getting unreleased (see the block for UNRELEASE)
         */
        if (Constants.dictionary().ANALYSIS_RELEASED.equals(ana.getStatusId()))
            return data;

        /*
         * set the value of risks based on the values of certain results
         */
        setRisks(data, ana);

        return data;
    }

    /**
     * Set the initial and final risks based on ethnicity, family history,
     * relation and result and whether the sample has any reject QA events
     */
    private void setRisks(SampleSO data, AnalysisViewDO ana) {
        double initRisk, finalRisk;
        boolean isDict;
        Integer accession, ethnicityId, famHistId;
        String value;
        SampleManager1 sm;
        TestManager tm;
        ResultFormatter rf;
        Carrier carrier;

        sm = data.getManager();
        try {
            /*
             * initialize the carrier object from the results of the analysis
             */
            carrier = new Carrier(sm, ana);
        } catch (Exception e) {
            data.setStatus(Status.FAILED);
            data.addException(e);
            return;
        }

        ethnicityId = null;
        famHistId = null;
        /*
         * if the sample or analysis has any result override qa events then
         * don't compute the risks
         */
        if (sm.qaEvent.hasType(Constants.dictionary().QAEVENT_OVERRIDE) ||
            sm.qaEvent.hasType(ana, Constants.dictionary().QAEVENT_OVERRIDE)) {
            carrier.isValid = false;
        } else {
            proxy.log(Level.FINE,
                      "Checking if the ethnicity and family history have been specified",
                      null);
            accession = sm.getSample().getAccessionNumber();
            /*
             * for display
             */
            if (accession == null)
                accession = 0;
            /*
             * ethnicity and family history must be specified
             */
            ethnicityId = carrier.getEthnicityId();
            if (ethnicityId == null) {
                data.setStatus(Status.FAILED);
                data.addException(new FormErrorException(Messages.get()
                                                                 .result_valueRequiredException(accession,
                                                                                                ana.getTestName(),
                                                                                                ana.getMethodName(),
                                                                                                carrier.ethnicity.getAnalyte())));
                carrier.isValid = false;
            }

            famHistId = carrier.getFamilyHistoryId();
            if (famHistId == null) {
                data.setStatus(Status.FAILED);
                data.addException(new FormErrorException(Messages.get()
                                                                 .result_valueRequiredException(accession,
                                                                                                ana.getTestName(),
                                                                                                ana.getMethodName(),
                                                                                                carrier.familyHistory.getAnalyte())));
                carrier.isValid = false;
            }
        }

        try {
            if (carrier.isValid) {
                /*
                 * compute and set the initial and final risks; if the computed
                 * final risk is 0.0, the risk is set as "Unknown"; otherwise
                 * it's the computed value
                 */
                proxy.log(Level.FINE, "Computing initial and final risks", null);
                tm = (TestManager)data.getCache().get(Constants.uid().getTest(ana.getTestId()));
                rf = tm.getFormatter();

                initRisk = risk1.computeCarrierInitialRisk(ethnicityId,
                                                           famHistId,
                                                           getIntegerResult(carrier.relation));
                setValue(carrier.initialRisk, risk1.format(initRisk), rf, false, ana, data);
                finalRisk = risk1.computeCarrierFinalRisk(ethnicityId,
                                                         getIntegerResult(carrier.cftrGene),
                                                         initRisk);
                if (finalRisk == 0.0) {
                    isDict = true;
                    value = risk1.UNKNOWN.toString();
                } else {
                    isDict = false;
                    value = risk1.format(finalRisk);
                }
                setValue(carrier.finalRisk, value, rf, isDict, ana, data);
            } else {
                setValue(carrier.initialRisk, null, null, false, ana, data);
                setValue(carrier.finalRisk, null, null, false, ana, data);
            }
        } catch (Exception e) {
            data.setStatus(Status.FAILED);
            data.addException(e);
        }
    }

    /**
     * Returns the id of the first released cf-pregnancy analysis; returns null
     * if no such analysis is found
     */
    private Integer getReleasedPregnancyId(SampleManager1 sm) {
        AnalysisViewDO ana;

        for (int i = 0; i < sm.analysis.count(); i++ ) {
            ana = sm.analysis.get(i);
            if (isReleasedPregnancy(ana))
                return ana.getId();
        }

        return null;
    }

    /**
     * Returns true if the passed analysis is for the cf-pregnancy test and is
     * released; returns false otherwise
     */
    private boolean isReleasedPregnancy(AnalysisViewDO ana) {
        return PREG_TEST_NAME.equals(ana.getTestName()) &&
               METHOD_NAME.equals(ana.getMethodName()) &&
               (Constants.dictionary().ANALYSIS_RELEASED.equals(ana.getStatusId()));
    }

    /**
     * Returns the integer equivalent of the value of the passed result; returns
     * null if the value is null
     */
    private Integer getIntegerResult(ResultViewDO res) {
        return res.getValue() != null ? Integer.valueOf(res.getValue()) : null;
    }

    /**
     * Sets the passed value in the passed result. The flag isDict means that
     * the value is a dictionary id and a dictionary record needs to be
     * obtained, because ResultHelper validates dictionary entries and not ids.
     */
    private void setValue(ResultViewDO result, String value, ResultFormatter rf, boolean isDict,
                          AnalysisViewDO ana, SampleSO data) throws Exception {
        String val;

        if (isDict)
            val = proxy.getDictionaryById(Integer.valueOf(value)).getEntry();
        else
            val = value;

        if (ResultHelper.formatValue(result, val, ana.getUnitOfMeasureId(), rf)) {
            data.addRerun(result.getAnalyteExternalId());
            data.addChangedUid(Constants.uid().getResult(result.getId()));
            proxy.log(Level.FINE,
                      "Setting the value of " + result.getAnalyte() + " as: " + value,
                      null);
        }
    }

    /**
     * This class groups the results of a cf-carrier analysis
     */
    private class Carrier {
        boolean isValid;
        ResultViewDO ethnicity, familyHistory, relation, cftrGene, initialRisk, finalRisk;

        public Carrier(SampleManager1 sm, AnalysisViewDO ana) throws Exception {
            int i, j;
            Integer accession;
            ResultViewDO res;
            ArrayList<String> extIds;

            isValid = true;
            for (i = 0; i < sm.result.count(ana); i++ ) {
                for (j = 0; j < sm.result.count(ana, i); j++ ) {
                    res = sm.result.get(ana, i, j);
                    if (res.getAnalyteExternalId() == null)
                        continue;
                    switch (res.getAnalyteExternalId()) {
                        case "cf_ethnicity":
                            ethnicity = res;
                            break;
                        case "cf_family_history":
                            familyHistory = res;
                            break;
                        case "cf_relation":
                            relation = res;
                            break;
                        case "cf_cftr_gene":
                            cftrGene = res;
                            break;
                        case "cf_initial_risk":
                            initialRisk = res;
                            break;
                        case "cf_final_risk":
                            finalRisk = res;
                            break;
                    }
                }
            }

            /*
             * show an error if any of the expected analytes was not found
             */
            extIds = new ArrayList<String>();
            if (ethnicity == null)
                extIds.add("cf_ethnicity");
            if (familyHistory == null)
                extIds.add("cf_family_history");
            if (relation == null)
                extIds.add("cf_relation");
            if (cftrGene == null)
                extIds.add("cf_cftr_gene");
            if (initialRisk == null)
                extIds.add("cf_initial_risk");
            if (finalRisk == null)
                extIds.add("cf_final_risk");

            if (extIds.size() > 0) {
                accession = sm.getSample().getAccessionNumber();
                throw new FormErrorException(Messages.get()
                                                     .result_analytesNotFoundException(accession != null ? accession
                                                                                                        : 0,
                                                                                       ana.getTestName(),
                                                                                       ana.getMethodName(),
                                                                                       DataBaseUtil.concatWithSeparator(extIds,
                                                                                                                        ",")));
            }
        }

        Integer getEthnicityId() {
            return getIntegerResult(ethnicity);
        }

        Integer getFamilyHistoryId() {
            return getIntegerResult(familyHistory);
        }
    }
}