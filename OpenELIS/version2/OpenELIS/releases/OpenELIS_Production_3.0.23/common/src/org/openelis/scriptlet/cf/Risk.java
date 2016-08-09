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
package org.openelis.scriptlet.cf;

import java.util.logging.Level;

/**
 * The class for calculating various risks for the scriptlets for
 * "cf (Cystic Fibrosis)" tests
 */
public class Risk {

    private static Risk    cfRisk1;

    private ScriptletProxy proxy;

    public Integer            ETHN_JEWISH, ETHN_WHITE, ETHN_HISPANIC, ETHN_BLACK, ETHN_ASIAN,
                    FAM_HIST_CARRIER, FAM_HIST_DISORDER, RELAT_PARENT, RELAT_SIBLING,
                    RELAT_DAUGHTER, RELAT_AUNT, RELAT_NIECE, RELAT_COUSIN, RELAT_8, RELAT_16,
                    RELAT_32, RELAT_64, RELAT_INDIVIDUAL, NEGATIVE, NOT_TESTED, AT_RISK, UNKNOWN;

    private Risk(ScriptletProxy proxy) {
        this.proxy = proxy;
        proxy.log(Level.FINE, "Initializing CFRisk1", null);
        try {
            if (ETHN_JEWISH == null) {
                ETHN_JEWISH = proxy.getDictionaryBySystemName("cf_ethnicity_jewish").getId();
                ETHN_WHITE = proxy.getDictionaryBySystemName("cf_ethnicity_white").getId();
                ETHN_HISPANIC = proxy.getDictionaryBySystemName("cf_ethnicity_hispanic").getId();
                ETHN_BLACK = proxy.getDictionaryBySystemName("cf_ethnicity_black").getId();
                ETHN_ASIAN = proxy.getDictionaryBySystemName("cf_ethnicity_asian").getId();
                FAM_HIST_CARRIER = proxy.getDictionaryBySystemName("cf_fam_hist_carrier").getId();
                FAM_HIST_DISORDER = proxy.getDictionaryBySystemName("cf_fam_hist_disorder").getId();
                RELAT_PARENT = proxy.getDictionaryBySystemName("cf_relation_parent").getId();
                RELAT_SIBLING = proxy.getDictionaryBySystemName("cf_relation_sibling").getId();
                RELAT_DAUGHTER = proxy.getDictionaryBySystemName("cf_relation_daughter").getId();
                RELAT_AUNT = proxy.getDictionaryBySystemName("cf_relation_aunt").getId();
                RELAT_NIECE = proxy.getDictionaryBySystemName("cf_relation_niece").getId();
                RELAT_COUSIN = proxy.getDictionaryBySystemName("cf_relation_cousin").getId();
                RELAT_8 = proxy.getDictionaryBySystemName("cf_relation_8").getId();
                RELAT_16 = proxy.getDictionaryBySystemName("cf_relation_16").getId();
                RELAT_32 = proxy.getDictionaryBySystemName("cf_relation_32").getId();
                RELAT_64 = proxy.getDictionaryBySystemName("cf_relation_64").getId();
                RELAT_INDIVIDUAL = proxy.getDictionaryBySystemName("cf_relation_individual")
                                        .getId();
                NEGATIVE = proxy.getDictionaryBySystemName("negative").getId();
                NOT_TESTED = proxy.getDictionaryBySystemName("not_tested").getId();
                AT_RISK = proxy.getDictionaryBySystemName("at_risk").getId();
                UNKNOWN = proxy.getDictionaryBySystemName("unknown").getId();
            }
            proxy.log(Level.FINE, "Initialized CFRisk1", null);
        } catch (Exception e) {
            proxy.log(Level.SEVERE, "Failed to initialize dictionary constants", e);
        }
    }

    public static Risk getInstance(ScriptletProxy proxy) {
        if (cfRisk1 == null)
            cfRisk1 = new Risk(proxy);

        return cfRisk1;
    }

    /**
     * Computes initial risk based on ethnicity, family history and relation
     */
    public double computeCarrierInitialRisk(Integer ethnicityId, Integer famHistId,
                                            Integer relationId) {
        double ir, ir1, initRisk;

        ir = getInitialEthnicityRisk(ethnicityId);

        if (FAM_HIST_DISORDER.equals(famHistId))
            ir1 = getDisorderRelationRisk(relationId);
        else if (FAM_HIST_CARRIER.equals(famHistId))
            ir1 = getCarrierRelationRisk(relationId);
        else
            ir1 = 1000000;

        initRisk = Math.min(ir, ir1);
        if (initRisk == 1000000.0)
            initRisk = 0;

        return initRisk;
    }

    /**
     * Computes carrier final risk based on ethnicity, family history, result
     * and initial risk
     */
    public double computeCarrierFinalRisk(Integer ethnicityId, Integer resultId, double initRisk) {
        double fr, finalRisk;

        fr = 1000000;

        if (NEGATIVE.equals(resultId))
            fr = getFinalEthnicityRisk(ethnicityId);
        else if (resultId == null || NOT_TESTED.equals(resultId))
            fr = initRisk;
        else if (resultId != null)
            fr = 1;

        finalRisk = fr;
        if (finalRisk == 1000000.0)
            finalRisk = 0;

        return finalRisk;
    }

    /**
     * Computes pregnancy initial risk based on individual and partner initial
     * risks; returns null if either risk is null
     */
    public Double computePregnancyInitialRisk(Double indInitRisk, Double partInitRisk) {
        if (indInitRisk != null && partInitRisk != null)
            return indInitRisk * partInitRisk * 4.0;

        return null;
    }

    /**
     * Computes pregnancy final risk based on individual and partner final
     * risks; returns null if either risk is null
     */
    public Double computePregnancyFinalRisk(Double indFinalRisk, Double partFinalRisk) {
        if (indFinalRisk != null && partFinalRisk != null)
            return indFinalRisk * partFinalRisk * 4.0;

        return null;
    }

    /**
     * Computes pregnancy result based on pregnancy final risk; returns null if
     * the risk is null
     */
    public Integer computePregnancyResult(Double pregFinalRisk) {
        if (pregFinalRisk != null) {
            if (pregFinalRisk > 0.0 && pregFinalRisk < 200.0)
                return AT_RISK;
            else
                return NEGATIVE;
        }

        return null;
    }

    /**
     * Converts the passed double to format it with one decimal point for both
     * back and front-end
     */
    public String format(Double risk) {
        return proxy.format(risk, "#0.0");
    }

    /**
     * Computes initial risk based on ethnicity
     */
    private double getInitialEthnicityRisk(Integer ethnicityId) {
        double risk;

        risk = 1000000;

        if (ETHN_WHITE.equals(ethnicityId) || ETHN_JEWISH.equals(ethnicityId))
            risk = 29;
        else if (ETHN_HISPANIC.equals(ethnicityId))
            risk = 46;
        else if (ETHN_BLACK.equals(ethnicityId))
            risk = 65;
        else if (ETHN_ASIAN.equals(ethnicityId))
            risk = 90;

        return risk;
    }

    /**
     * Computes disorder risk based on relation
     */
    private double getDisorderRelationRisk(Integer relationId) {
        double risk;

        risk = 1000000;
        if (relationId == null)
            return risk;

        if (RELAT_PARENT.equals(relationId) || RELAT_DAUGHTER.equals(relationId) ||
            RELAT_INDIVIDUAL.equals(relationId))
            risk = 1;
        else if (RELAT_SIBLING.equals(relationId))
            risk = 1.5;
        else if (RELAT_AUNT.equals(relationId))
            risk = 3;
        else if (RELAT_NIECE.equals(relationId))
            risk = 2;
        else if (RELAT_COUSIN.equals(relationId))
            risk = 4;
        else if (RELAT_8.equals(relationId))
            risk = 8;
        else if (RELAT_16.equals(relationId))
            risk = 16;
        else if (RELAT_32.equals(relationId))
            risk = 32;
        else if (RELAT_64.equals(relationId))
            risk = 64;

        return risk;
    }

    /**
     * Computes carrier risk based on relation
     */
    private double getCarrierRelationRisk(Integer relationId) {
        double risk;

        risk = 1000000;
        if (relationId == null)
            return risk;

        if (RELAT_INDIVIDUAL.equals(relationId))
            risk = 1;
        else if (RELAT_PARENT.equals(relationId) || RELAT_SIBLING.equals(relationId) ||
                 RELAT_DAUGHTER.equals(relationId))
            risk = 2;
        else if (RELAT_AUNT.equals(relationId) || RELAT_NIECE.equals(relationId))
            risk = 4;
        else if (RELAT_COUSIN.equals(relationId) || RELAT_8.equals(relationId))
            risk = 8;
        else if (RELAT_16.equals(relationId))
            risk = 16;
        else if (RELAT_32.equals(relationId))
            risk = 32;
        else if (RELAT_64.equals(relationId))
            risk = 64;

        return risk;
    }

    /**
     * Computes final risk based on ethnicity
     */
    private double getFinalEthnicityRisk(Integer ethnicityId) {
        double risk;

        risk = 1000000;

        if (ETHN_WHITE.equals(ethnicityId))
            risk = 235;
        else if (ETHN_JEWISH.equals(ethnicityId))
            risk = 930;
        else if (ETHN_HISPANIC.equals(ethnicityId))
            risk = 105;
        else if (ETHN_BLACK.equals(ethnicityId))
            risk = 207;

        return risk;
    }
}