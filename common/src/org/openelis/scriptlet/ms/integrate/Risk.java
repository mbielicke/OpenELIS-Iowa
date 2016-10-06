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
package org.openelis.scriptlet.ms.integrate;

import org.openelis.scriptlet.ms.Util;

/**
 * The class that stores data such as means and constant multipliers for a
 * specific disorder such as Trisomy 18; it also computes the disorder's risk
 */
public abstract class Risk {

    private double meanUA[];
    private int    risk;

    protected Risk(double meanUA[]) {
        this.meanUA = meanUA;
    }

    /**
     * Returns the log mean for disease
     */
    protected abstract double[] getMeanDS(Integer gestAge1T, Integer gestAge1TNT);
    
    /**
     * Returns the constant multipliers for affected/unaffected
     */
    protected abstract double[] getConstUA(Double ntMom, Integer gestAge1TNT);

    /**
     * Returns the constant multipliers for disease
     */
    protected abstract double[] getConstDS(Double ntMom);
    
    /**
     * Returns the discriminant for affected and unaffected
     */
    protected abstract double getDiscrUA(Double ntMom, Integer gestAge1TNT);

    /**
     * Returns the discriminant for disease
     */
    protected abstract double getDiscrDS(Double ntMom);
    
    /**
     * Computes the risk from the passed arguments and sets it 
     */
    protected abstract void setRisk(double apr, double ordinalUA, double ordinalDS);
    
    /**
     * Returns the log mean for affected/unaffected
     */
    protected double[] getMeanUA() {
        return meanUA;
    }

    /**
     * Returns the computed risk
     */
    protected int getRisk() {
        return risk;
    }
    
    /**
     * Sets the risk
     */
    protected void setRisk(int risk) {
        this.risk = risk; 
    }

    /**
     * Computes the risk based on passed MoMs, apriori risk and 1st trimester 
     * gestational ages
     */
    protected void computeRisk(double mom[], int markers, double apr, Double ntMom,
                               Integer gestAge1T, Integer gestAge1TNT) {
        double ordinalUA, ordinalDS;

        ordinalUA = Util.getOrdinal(mom,
                                    markers,
                                    getDiscrUA(ntMom, gestAge1TNT),
                                    getMeanUA(),
                                    getConstUA(ntMom, gestAge1TNT));
        ordinalDS = Util.getOrdinal(mom,
                                    markers,
                                    getDiscrDS(ntMom),
                                    getMeanDS(gestAge1T, gestAge1TNT),
                                    getConstDS(ntMom));
        setRisk(apr, ordinalUA, ordinalDS);
    }
}