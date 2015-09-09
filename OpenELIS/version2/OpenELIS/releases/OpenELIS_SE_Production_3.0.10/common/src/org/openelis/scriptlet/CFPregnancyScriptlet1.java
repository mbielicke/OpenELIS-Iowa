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

import java.util.ArrayList;
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
import org.openelis.utilcommon.ResultHelper;

/**
 * The scriptlet for "cf-pregnancy (Cystic Fibrosis Pregnancy Screen)" test. It
 * copies the values from a released cf-carrier analysis on the same
 * (individual) sample. It also copies the values from such an analysis on a
 * different (partner) sample if a partner accession number is specified. The
 * partner sample is fetched as soon as the user enters the accession number, on
 * a request for recomputation and on completing/releasing the cf-pregnancy
 * analysis. If the partner accession number is blanked, the partner values are
 * blanked. If the values for the partner,like ethnicity, are manually entered,
 * it computes the partner's risks and blanks the partner accession number. It
 * computes the pregnancy risks and result based on the individual and partner
 * risks, if the results are not overridden, otherwise it blanks the pregnancy
 * risks and result.
 */
public class CFPregnancyScriptlet1 implements ScriptletInt<SampleSO> {

    private CFScriptlet1Proxy proxy;

    private Integer           analysisId, carrierId;

    private static final String CARR_TEST_NAME = "cf-carrier", METHOD_NAME = "pcr",
                    PART_ACCESSION = "cf_part_accession";

    private CFRisk1             cfRisk1;

    public CFPregnancyScriptlet1(CFScriptlet1Proxy proxy, Integer analysisId) {
        proxy.log(Level.FINE, "Initializing CFPregnancyScriptlet1", null);

        this.proxy = proxy;
        this.analysisId = analysisId;
        cfRisk1 = CFRisk1.getInstance(proxy);

        proxy.log(Level.FINE, "Initialized CFPregnancyScriptlet1", null);
    }

    @Override
    public SampleSO run(SampleSO data) {
        String extId;
        boolean fetchPartner, partnerRemoved, computePartnerRisks;
        SampleManager1 sm;
        AnalysisViewDO ana;
        ResultViewDO res;

        proxy.log(Level.FINE, "In CFPregnancyScriptlet1.run", null);
        sm = data.getManager();
        ana = (AnalysisViewDO)sm.getObject(Constants.uid().getAnalysis(analysisId));
        if (ana == null || Constants.dictionary().ANALYSIS_RELEASED.equals(ana.getStatusId()) ||
            Constants.dictionary().ANALYSIS_CANCELLED.equals(ana.getStatusId()))
            return data;

        /*
         * manage result changed, the risks needing to be recomputed, analysis
         * getting completed or released and a qa event added/removed
         */
        fetchPartner = false;
        partnerRemoved = false;
        computePartnerRisks = false;
        if (data.getActionBefore().contains(Action_Before.RESULT)) {
            res = (ResultViewDO)data.getManager().getObject(data.getUid());
            if ( !analysisId.equals(res.getAnalysisId()))
                return data;
            extId = res.getAnalyteExternalId();
            /*
             * By convention, all partner analyte external ids start with
             * "cf_part". If any analyte for the partner was changed, if it was
             * partner accession number, the partner sample is fetched if an
             * accession number was entered, otherwise the partner values are
             * blanked if the accession number was blanked; if it was any other
             * analyte, partner risks need to be recomputed
             */
            if (extId != null && extId.startsWith("cf_part")) {
                if (PART_ACCESSION.equals(extId)) {
                    fetchPartner = res.getValue() != null;
                    partnerRemoved = res.getValue() == null;
                } else {
                    computePartnerRisks = true;
                }
            }
        } else if (data.getActionBefore().contains(Action_Before.RECOMPUTE)) {
            fetchPartner = true;
        } else if (data.getActionBefore().contains(Action_Before.COMPLETE) ||
                   data.getActionBefore().contains(Action_Before.RELEASE)) {
            fetchPartner = true;
            ana = (AnalysisViewDO)data.getManager().getObject(data.getUid());
            if ( !analysisId.equals(ana.getId()))
                return data;
        } else if ( !data.getActionBefore().contains(Action_Before.QA)) {
            return data;
        }

        /*
         * set the value of risks based on the values of certain results
         */
        setRisks(data, ana, fetchPartner, partnerRemoved, computePartnerRisks);

        return data;
    }

    /**
     * Sets the values for the individual and partner results and computes and
     * sets pregnancy risks and result based on individual and partner risks. If
     * the flag "fetchPartner" is true then a partner sample is fetched.
     */
    private void setRisks(SampleSO data, AnalysisViewDO ana, boolean fetchPartner,
                          boolean partnerRemoved, boolean computePartnerRisks) {
        Integer indAccession, partAccession, partCarrierId;
        SampleManager1 indSM, partSM;
        AnalysisViewDO indCarrierAna, partCarrierAna;
        TestManager tm;
        ResultFormatter rf;
        IndividualCarrier individual;
        PartnerCarrier partner;
        Pregnancy pregnancy;

        indSM = data.getManager();
        try {
            /*
             * initialize the carrier and pregnancy objects from the results of
             * the analysis
             */
            individual = new IndividualCarrier(indSM, ana);
            partner = new PartnerCarrier(indSM, ana);
            pregnancy = new Pregnancy(indSM, ana);
        } catch (Exception e) {
            data.setStatus(Status.FAILED);
            data.addException(e);
            return;
        }

        indAccession = indSM.getSample().getAccessionNumber();
        /*
         * for display
         */
        if (indAccession == null)
            indAccession = 0;

        proxy.log(Level.FINE, "Finding the analysis for the cf-carrier test", null);
        indCarrierAna = null;
        /*
         * find the carrier analysis on the individual sample
         */
        if (carrierId == null) {
            carrierId = getReleasedCarrierId(indSM);
        } else {
            indCarrierAna = (AnalysisViewDO)indSM.getObject(Constants.uid().getAnalysis(carrierId));
            if ( !isReleasedCarrier(indCarrierAna)) {
                carrierId = getReleasedCarrierId(indSM);
                indCarrierAna = null;
            }
        }
        if (indCarrierAna == null && carrierId != null)
            indCarrierAna = (AnalysisViewDO)indSM.getObject(Constants.uid().getAnalysis(carrierId));

        if (indCarrierAna == null) {
            data.setStatus(Status.FAILED);
            data.addException(new FormErrorException(Messages.get()
                                                             .analysis_validIndCarrierNotFoundException(indAccession,
                                                                                                        ana.getTestName(),
                                                                                                        ana.getMethodName(),
                                                                                                        CARR_TEST_NAME,
                                                                                                        METHOD_NAME)));
            individual.isValid = false;
        }

        try {
            partCarrierAna = null;
            partSM = null;
            if (fetchPartner) {
                /*
                 * a partner sample is fetched if the partner accession number
                 * is specified
                 */
                if (partner.accessionNumber.getValue() != null) {
                    partAccession = Integer.valueOf(partner.accessionNumber.getValue());
                    /*
                     * partner accession number can't be the same as the
                     * individual accession number
                     */
                    if (indAccession.equals(partAccession)) {
                        data.setStatus(Status.FAILED);
                        data.addException(new FormErrorException(Messages.get()
                                                                         .analysis_partAccCantBeSameAsIndException(partAccession,
                                                                                                                   ana.getTestName(),
                                                                                                                   ana.getMethodName())));
                        partner.isValid = false;
                    } else {
                        proxy.log(Level.FINE, "Fetching sample with accession number: " +
                                              partAccession, null);
                        try {
                            /*
                             * fetch the partner sample and find the cf-carrier
                             * analysis in it
                             */
                            partSM = proxy.fetchByAccession(partAccession,
                                                            SampleManager1.Load.RESULT);
                            partCarrierId = getReleasedCarrierId(partSM);
                            if (partCarrierId != null) {
                                partCarrierAna = (AnalysisViewDO)partSM.getObject(Constants.uid()
                                                                                           .getAnalysis(partCarrierId));
                            } else {
                                partner.isValid = false;
                                data.setStatus(Status.FAILED);
                                data.addException(new FormErrorException(Messages.get()
                                                                                 .analysis_validPartCarrierNotFoundException(indAccession,
                                                                                                                             ana.getTestName(),
                                                                                                                             ana.getMethodName(),
                                                                                                                             CARR_TEST_NAME,
                                                                                                                             METHOD_NAME)));
                            }
                        } catch (NotFoundException e) {
                            partner.isValid = false;
                            data.setStatus(Status.FAILED);
                            data.addException(new FormErrorException(Messages.get()
                                                                             .result_partSamNotFoundException(indAccession,
                                                                                                              ana.getTestName(),
                                                                                                              ana.getMethodName(),
                                                                                                              partAccession)));
                        }
                    }
                }
            } else if (partnerRemoved) {
                /*
                 * partner values are blanked if the partner accession number
                 * was blanked
                 */
                partner.isValid = false;
            }

            tm = (TestManager)data.getCache().get(Constants.uid().getTest(ana.getTestId()));
            rf = tm.getFormatter();
            if (individual.isValid) {
                /*
                 * copy the values from cf-carrier on the same sample
                 */
                proxy.log(Level.FINE,
                          "Copying results from cf-carrier on the same sample to cf-pregnancy",
                          null);
                copyValues(indSM, indCarrierAna, ana, individual, rf, data);
            } else {
                clearValues(ana, data, individual);
            }

            if (partner.isValid) {
                /*
                 * if a partner sample was fetched then copy the values from its
                 * cf-carrier; recompute the partner's risks if a result like
                 * ethnicity was changed and blank the partner accession number
                 * because the partner values don't belong to that sample
                 * anymore
                 */
                if (partCarrierAna != null) {
                    proxy.log(Level.FINE,
                              "Copying results from cf-carrier on the partner sample to cf-pregnancy",
                              null);
                    copyValues(partSM, partCarrierAna, ana, partner, rf, data);
                }

                if (computePartnerRisks) {
                    setPartnerRisks(ana, data, partner, rf);
                    setValue(partner.accessionNumber, null, rf, false, ana, data);
                }
            } else {
                clearValues(ana, data, partner);
            }

            /*
             * if the sample or analysis have any result override qa events then
             * blank the pregnancy risks and result; otherwise compute them
             */
            if (indSM.qaEvent.hasType(Constants.dictionary().QAEVENT_OVERRIDE) ||
                indSM.qaEvent.hasType(ana, Constants.dictionary().QAEVENT_OVERRIDE)) {
                clearValues(ana, data, pregnancy);
            } else {
                setPregnancyRisks(ana, data, individual, partner, pregnancy, rf);
                /*
                 * the analysis needs to have a pregnancy result on being
                 * completed or released because the results are not overridden
                 */
                if ( (data.getActionBefore().contains(Action_Before.COMPLETE) || data.getActionBefore()
                                                                                     .contains(Action_Before.RELEASE)) &&
                    pregnancy.result.getValue() == null) {
                    data.setStatus(Status.FAILED);
                    data.addException(new FormErrorException(Messages.get()
                                                                     .result_valueRequiredException(indAccession,
                                                                                                    ana.getTestName(),
                                                                                                    ana.getMethodName(),
                                                                                                    pregnancy.result.getAnalyte())));
                }
            }
        } catch (Exception e) {
            data.setStatus(Status.FAILED);
            data.addException(e);
        }
    }

    /**
     * Returns the id of the first released cf-carrier analysis; returns null if
     * no such analysis is found
     */
    private Integer getReleasedCarrierId(SampleManager1 sm) {
        AnalysisViewDO ana;

        for (int i = 0; i < sm.analysis.count(); i++ ) {
            ana = sm.analysis.get(i);
            if (isReleasedCarrier(ana))
                return ana.getId();
        }

        return null;
    }

    /**
     * Returns true if the passed analysis is for the cf-carrier test and is
     * released; returns false otherwise
     */
    private boolean isReleasedCarrier(AnalysisViewDO ana) {
        return CARR_TEST_NAME.equals(ana.getTestName()) &&
               METHOD_NAME.equals(ana.getMethodName()) &&
               (Constants.dictionary().ANALYSIS_RELEASED.equals(ana.getStatusId()));
    }

    /**
     * Computes and sets the partner's initial and final risks
     */
    private void setPartnerRisks(AnalysisViewDO ana, SampleSO data, PartnerCarrier partner,
                                 ResultFormatter rf) throws Exception {
        double initRisk, finalRisk;
        boolean isDict;
        Integer partEthnicityId, partFamHistId, accession;
        String value;

        accession = data.getManager().getSample().getAccessionNumber();
        /*
         * for display
         */
        if (accession == null)
            accession = 0;

        proxy.log(Level.FINE,
                  "Checking if the ethnicity and family history have been specified for the partner",
                  null);
        partEthnicityId = partner.getEthnicityId();
        /*
         * ethnicity and family history must be specified
         */
        if (partEthnicityId == null) {
            data.setStatus(Status.FAILED);
            data.addException(new FormErrorException(Messages.get()
                                                             .result_valueRequiredException(accession,
                                                                                            ana.getTestName(),
                                                                                            ana.getMethodName(),
                                                                                            partner.ethnicity.getAnalyte())));
            partner.isValid = false;
        }

        partFamHistId = partner.getFamilyHistoryId();
        if (partFamHistId == null) {
            data.setStatus(Status.FAILED);
            data.addException(new FormErrorException(Messages.get()
                                                             .result_valueRequiredException(accession,
                                                                                            ana.getTestName(),
                                                                                            ana.getMethodName(),
                                                                                            partner.familyHistory.getAnalyte())));
            partner.isValid = false;
        }
        if (partner.isValid) {
            /*
             * compute and set the initial and final risks; if the computed
             * final risk is 0.0, the risk is set as "Unknown"; otherwise it's
             * the computed value
             */
            proxy.log(Level.FINE, "Computing partner initial and final risks", null);
            initRisk = cfRisk1.computeCarrierInitialRisk(partEthnicityId,
                                                         partFamHistId,
                                                         getIntegerResult(partner.relation));
            setValue(partner.initialRisk, cfRisk1.format(initRisk), rf, false, ana, data);
            finalRisk = cfRisk1.computeCarrierFinalRisk(partEthnicityId,
                                                        getIntegerResult(partner.result),
                                                        initRisk);
            if (finalRisk == 0.0) {
                isDict = true;
                value = cfRisk1.UNKNOWN.toString();
            } else {
                isDict = false;
                value = cfRisk1.format(finalRisk);
            }
            setValue(partner.finalRisk, value, rf, isDict, ana, data);
        } else {
            setValue(partner.initialRisk, null, rf, false, ana, data);
            setValue(partner.finalRisk, null, rf, false, ana, data);
        }
    }

    /**
     * Computes and sets the pregnancy initial and final risks and result; the
     * pregnancy initial risk is computed from the individual and partner
     * initial risks; the pregnancy initial risk is computed from the individual
     * and partner final risks; the pregnancy result is computed from pregnancy
     * final risk and is only computed if both pregnancy risks are not null
     */
    private void setPregnancyRisks(AnalysisViewDO ana, SampleSO data, IndividualCarrier individual,
                                   PartnerCarrier partner, Pregnancy pregnancy, ResultFormatter rf) throws Exception {
        boolean isDict;
        Integer pregResultId;
        Double indRisk, partRisk, pregInitRisk, pregFinalRisk;
        String value, unknown;

        proxy.log(Level.FINE, "Computing pregnancy risks and result", null);
        pregInitRisk = cfRisk1.computePregnancyInitialRisk(getDoubleResult(individual.initialRisk),
                                                           getDoubleResult(partner.initialRisk));
        setValue(pregnancy.initialRisk, cfRisk1.format(pregInitRisk), rf, false, ana, data);

        unknown = cfRisk1.UNKNOWN.toString();
        /*
         * if either the individual or partner final risk is "Unknown",
         * pregnancy final risk will be "Unknown"; otherwise the pregnancy risk
         * will be calculated from the two risks
         */
        indRisk = unknown.equals(individual.finalRisk.getValue()) ? null
                                                                 : getDoubleResult(individual.finalRisk);
        partRisk = unknown.equals(partner.finalRisk.getValue()) ? null
                                                               : getDoubleResult(partner.finalRisk);
        pregFinalRisk = cfRisk1.computePregnancyFinalRisk(indRisk, partRisk);
        if (pregFinalRisk == null) {
            isDict = true;
            value = unknown;
        } else {
            isDict = false;
            value = cfRisk1.format(pregFinalRisk);
        }
        setValue(pregnancy.finalRisk, value, rf, isDict, ana, data);

        /*
         * the pregnancy result will be obtained only if pregnancy initial risk
         * is not null; if the initial risk is not null, the result will depend
         * on the final risk; 0.0 is passed because the value set in the result
         * for final risk is "Unknown" if the computed final risk is null
         */
        if (pregInitRisk != null && pregFinalRisk == null)
            pregFinalRisk = 0.0;
        pregResultId = cfRisk1.computePregnancyResult(pregFinalRisk);
        value = pregResultId != null ? pregResultId.toString() : null;
        setValue(pregnancy.result, value, rf, value != null, ana, data);
    }

    /**
     * Returns the double equivalent of the value of the passed result; returns
     * null if the value is null
     */
    private Double getDoubleResult(ResultViewDO res) {
        return res.getValue() != null ? Double.valueOf(res.getValue()) : null;
    }

    /**
     * Returns the integer equivalent of the value of the passed result; returns
     * null if the value is null
     */
    private Integer getIntegerResult(ResultViewDO res) {
        return res.getValue() != null ? Integer.valueOf(res.getValue()) : null;
    }

    /**
     * Sets the values of the results for the individual to null
     */
    private void clearValues(AnalysisViewDO ana, SampleSO data, IndividualCarrier individual) throws Exception {
        setValue(individual.ethnicity, null, null, false, ana, data);
        setValue(individual.familyHistory, null, null, false, ana, data);
        setValue(individual.relation, null, null, false, ana, data);
        setValue(individual.result, null, null, false, ana, data);
        setValue(individual.initialRisk, null, null, false, ana, data);
        setValue(individual.finalRisk, null, null, false, ana, data);
    }

    /**
     * Sets the values of the results for the partner to null
     */
    private void clearValues(AnalysisViewDO ana, SampleSO data, PartnerCarrier partner) throws Exception {
        setValue(partner.ethnicity, null, null, false, ana, data);
        setValue(partner.familyHistory, null, null, false, ana, data);
        setValue(partner.relation, null, null, false, ana, data);
        setValue(partner.result, null, null, false, ana, data);
        setValue(partner.initialRisk, null, null, false, ana, data);
        setValue(partner.finalRisk, null, null, false, ana, data);
    }

    /**
     * Sets the values of the results for pregnancy to null
     */
    private void clearValues(AnalysisViewDO ana, SampleSO data, Pregnancy pregnancy) throws Exception {
        setValue(pregnancy.initialRisk, null, null, false, ana, data);
        setValue(pregnancy.finalRisk, null, null, false, ana, data);
        setValue(pregnancy.result, null, null, false, ana, data);
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
     * Copies the values of the results of the carrier analysis to the results
     * of the pregnancy analysis
     */
    private void copyValues(SampleManager1 sm, AnalysisViewDO carrierAna,
                            AnalysisViewDO pregnancyAna, IndividualCarrier individual,
                            ResultFormatter rf, SampleSO data) throws Exception {
        int i, j;
        ResultViewDO res;

        for (i = 0; i < sm.result.count(carrierAna); i++ ) {
            for (j = 0; j < sm.result.count(carrierAna, i); j++ ) {
                res = sm.result.get(carrierAna, i, j);
                switch (res.getAnalyteExternalId()) {
                    case "cf_ethnicity":
                        copyValue(res, individual.ethnicity, rf, pregnancyAna, data);
                        break;
                    case "cf_family_history":
                        copyValue(res, individual.familyHistory, rf, pregnancyAna, data);
                        break;
                    case "cf_relation":
                        copyValue(res, individual.relation, rf, pregnancyAna, data);
                        break;
                    case "cf_cftr_gene":
                        copyValue(res, individual.result, rf, pregnancyAna, data);
                        break;
                    case "cf_initial_risk":
                        copyValue(res, individual.initialRisk, rf, pregnancyAna, data);
                        break;
                    case "cf_final_risk":
                        copyValue(res, individual.finalRisk, rf, pregnancyAna, data);
                        break;
                }
            }
        }
    }

    /**
     * Copies the values of the results of the carrier analysis to the results
     * of the pregnancy analysis
     */
    private void copyValues(SampleManager1 sm, AnalysisViewDO carrierAna,
                            AnalysisViewDO pregnancyAna, PartnerCarrier partner,
                            ResultFormatter rf, SampleSO data) throws Exception {
        int i, j;
        ResultViewDO res;

        for (i = 0; i < sm.result.count(carrierAna); i++ ) {
            for (j = 0; j < sm.result.count(carrierAna, i); j++ ) {
                res = sm.result.get(carrierAna, i, j);
                switch (res.getAnalyteExternalId()) {
                    case "cf_ethnicity":
                        copyValue(res, partner.ethnicity, rf, pregnancyAna, data);
                        break;
                    case "cf_family_history":
                        copyValue(res, partner.familyHistory, rf, pregnancyAna, data);
                        break;
                    case "cf_relation":
                        copyValue(res, partner.relation, rf, pregnancyAna, data);
                        break;
                    case "cf_cftr_gene":
                        copyValue(res, partner.result, rf, pregnancyAna, data);
                        break;
                    case "cf_initial_risk":
                        copyValue(res, partner.initialRisk, rf, pregnancyAna, data);
                        break;
                    case "cf_final_risk":
                        copyValue(res, partner.finalRisk, rf, pregnancyAna, data);
                        break;
                }
            }
        }
    }

    /**
     * Copies the value of the "from" result to the "to" result
     */
    private void copyValue(ResultViewDO fromResult, ResultViewDO toResult, ResultFormatter rf,
                           AnalysisViewDO ana, SampleSO data) throws Exception {
        boolean isDict;

        isDict = Constants.dictionary().TEST_RES_TYPE_DICTIONARY.equals(fromResult.getTypeId());
        setValue(toResult, fromResult.getValue(), rf, isDict, ana, data);
    }

    /**
     * This class groups the results of the cf-pregnancy analysis that hold the
     * values for the individual carrier
     */
    private class IndividualCarrier {
        boolean isValid;
        ResultViewDO ethnicity, familyHistory, relation, result, initialRisk, finalRisk;

        public IndividualCarrier(SampleManager1 sm, AnalysisViewDO ana) throws Exception {
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
                        case "cf_ind_ethnicity":
                            ethnicity = res;
                            break;
                        case "cf_ind_fam_history":
                            familyHistory = res;
                            break;
                        case "cf_ind_relation":
                            relation = res;
                            break;
                        case "cf_ind_result":
                            result = res;
                            break;
                        case "cf_ind_initial_risk":
                            initialRisk = res;
                            break;
                        case "cf_ind_final_risk":
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
                extIds.add("cf_ind_ethnicity");
            if (familyHistory == null)
                extIds.add("cf_ind_fam_history");
            if (relation == null)
                extIds.add("cf_ind_relation");
            if (result == null)
                extIds.add("cf_ind_result");
            if (initialRisk == null)
                extIds.add("cf_ind_initial_risk");
            if (finalRisk == null)
                extIds.add("cf_ind_final_risk");

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
    }

    /**
     * This class groups the results of the cf-pregnancy analysis that hold the
     * values for the partner carrier
     */
    private class PartnerCarrier {
        boolean isValid;
        ResultViewDO ethnicity, familyHistory, relation, result, initialRisk, finalRisk,
                        accessionNumber;

        public PartnerCarrier(SampleManager1 sm, AnalysisViewDO ana) throws Exception {
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
                        case PART_ACCESSION:
                            accessionNumber = res;
                            break;
                        case "cf_part_ethnicity":
                            ethnicity = res;
                            break;
                        case "cf_part_fam_history":
                            familyHistory = res;
                            break;
                        case "cf_part_relation":
                            relation = res;
                            break;
                        case "cf_part_result":
                            result = res;
                            break;
                        case "cf_part_initial_risk":
                            initialRisk = res;
                            break;
                        case "cf_part_final_risk":
                            finalRisk = res;
                            break;
                    }
                }
            }

            /*
             * show an error if any of the expected analytes was not found
             */
            extIds = new ArrayList<String>();
            if (accessionNumber == null)
                extIds.add(PART_ACCESSION);
            if (ethnicity == null)
                extIds.add("cf_part_ethnicity");
            if (familyHistory == null)
                extIds.add("cf_part_fam_history");
            if (relation == null)
                extIds.add("cf_part_relation");
            if (result == null)
                extIds.add("cf_part_result");
            if (initialRisk == null)
                extIds.add("cf_part_initial_risk");
            if (finalRisk == null)
                extIds.add("cf_part_final_risk");

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

    /**
     * This class groups the results of the cf-pregnancy analysis that hold the
     * values for the pregnancy risks and result
     */
    private class Pregnancy {
        ResultViewDO initialRisk, finalRisk, result;

        public Pregnancy(SampleManager1 sm, AnalysisViewDO ana) throws Exception {
            int i, j;
            Integer accession;
            ResultViewDO res;
            ArrayList<String> extIds;

            /*
             * initialize the object from the results of the analysis
             */
            for (i = 0; i < sm.result.count(ana); i++ ) {
                for (j = 0; j < sm.result.count(ana, i); j++ ) {
                    res = sm.result.get(ana, i, j);
                    if (res.getAnalyteExternalId() == null)
                        continue;
                    switch (res.getAnalyteExternalId()) {
                        case "cf_preg_initial_risk":
                            initialRisk = res;
                            break;
                        case "cf_preg_final_risk":
                            finalRisk = res;
                            break;
                        case "cf_preg_result":
                            result = res;
                            break;
                    }
                }
            }

            /*
             * show an error if any of the expected analytes was not found
             */
            extIds = new ArrayList<String>();
            if (initialRisk == null)
                extIds.add("cf_preg_initial_risk");
            if (finalRisk == null)
                extIds.add("cf_preg_final_risk");
            if (result == null)
                extIds.add("cf_preg_result");

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
    }
}