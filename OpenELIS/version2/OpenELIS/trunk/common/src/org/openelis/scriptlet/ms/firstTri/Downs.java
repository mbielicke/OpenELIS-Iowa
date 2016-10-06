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
package org.openelis.scriptlet.ms.firstTri;

import org.openelis.scriptlet.ms.Util;

/**
 * The class that stores data such as means and constant multipliers for Downs
 * for 1st trimester tests; it also computes the disorder's risk
 */
public class Downs extends Risk {

    private double constUA12[], constUA11[];
    
    protected Downs() {
        super(new double[] {0.0, 0.0, 0.0},
               new double[] {19.2037, 2.2579, 1.3656,
                            2.2579, 13.2159, -2.0914,
                            1.3656, -2.0914, 23.8490},
               0.000172129);
        constUA12 = new double[] {57.0882, 1.2095, 2.8542,
                                   1.2095,16.9053,-4.6866,
                                   2.8542,-4.6866,27.7762};
        constUA11 = new double[] {48.6342, 1.0298, 2.4308,
                                   1.0298,16.9015,-4.6956,
                                   2.4308,-4.6956,27.7550};
    }
    
    @Override
    protected void computeRisk(double[] mom, double apr, int gestAgeNT, int gestAge) {
        double ordinalUA, ordinalDS;

        ordinalUA = Util.getOrdinal(mom, 3, getDiscriminantUA(gestAge), getMeanUA(), getConstUA(gestAge));
        ordinalDS = Util.getOrdinal(mom, 3, getDiscriminantDS(), getMeanDS(gestAgeNT, gestAge), getConstDS());
        setRisk((int) (apr / (ordinalDS / ordinalUA)));
    }

    /**
     * Returns log means for disease
     */
    private double[] getMeanDS(int gestAgeNT, int gestAge) {
        double means[] = {0.8554679 - 0.0064686 * gestAgeNT,
                          -1.1444 + 0.00965 * gestAge,
                          -0.8662 + 0.01213 * gestAge};
       return means;
    }

    /**
     * Returns the unaffected constant multipliers
     */
    private double[] getConstUA(int gestAge) {
        if (gestAge > 84)          // 12 weeks
            return constUA12;
        else                     // 11 weeks
            return constUA11;
    }

    /**
     * Returns the unaffected discriminant
     */
    private double getDiscriminantUA(int gestAge) {
        if (gestAge > 84)            // 12 weeks
            return 0.0000394602;
        else
            return 0.0000463194;
    }
}
