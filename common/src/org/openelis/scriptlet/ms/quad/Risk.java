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
package org.openelis.scriptlet.ms.quad;

/**
 * The class that stores data such as means and constant multipliers for a
 * specific disorder such as Trisomy 18; it also computes the disorder's risk
 */
public abstract class Risk {

    private double  meanUA[], meanDS[], constUA[], constDS[];
    private int risk;

    protected Risk() {
    }

    protected Risk(double meanUA[], double meanDS[], double constUA[], double constDS[]) {
        this.meanUA = meanUA;
        this.meanDS = meanDS;
        this.constUA = constUA;
        this.constDS = constDS;
    }

    /**
     * Returns the unaffected mean
     */
    public double[] getMeanUA() {
        return meanUA;
    }

    /**
     * Returns the Disease mean
     */
    public double[] getMeanDS() {
        return meanDS;
    }

    /**
     * Returns the unaffected constant multiplier
     */
    public double[] getConstUA() {
        return constUA;
    }

    /**
     * Returns the disease constant multiplier
     */
    public double[] getConstDS() {
        return constDS;
    }

    /**
     * Returns the computed risk
     */
    public int getRisk() {
        return risk;
    }

    /**
     * Sets the risk
     */
    public void setRisk(int risk) {
        this.risk = risk;
    }

    /**
     * Computes the risk based on passed MoMs and apriori risk
     */
    public abstract void computeRisk(double mom[], double apr);

    /**
     * Computes and returns an ordinal value using passed parameters
     */
    protected double getOrdinal(double mom[], int dimension, double discriminant, double logMean[],
                                double constant[]) {
        int i, j, n;
        double ordinal;

        ordinal = 0.0;
        for (i = 0, n = 0; i < dimension; i++ )
            for (j = 0; j < dimension; j++ , n++ )
                ordinal += (constant[n] * (Math.log10(mom[i]) - logMean[i]) * (Math.log10(mom[j]) - logMean[j]));

        return Math.pow(2.0 * Math.PI, -dimension / 2) * (1 / Math.sqrt(discriminant)) *
               Math.exp( -0.5 * ordinal);
    }
}