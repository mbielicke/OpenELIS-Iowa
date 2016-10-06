/** Exhibit A - UIRF Open-source Based Public Software License.
* 
* The contents of this file are subject to the UIRF Open-source Based
* Public Software License(the "License"); you may not use this file except
* in compliance with the License. You may obtain a copy of the License at
* openelis.uhl.uiowa.edu
* 
* Software distributed under the License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
* License for the specific language governing rights and limitations
* under the License.
* 
* The Original Code is OpenELIS code.
* 
* The Initial Developer of the Original Code is The University of Iowa.
* Portions created by The University of Iowa are Copyright 2006-2008. All
* Rights Reserved.
* 
* Contributor(s): ______________________________________.
* 
* Alternatively, the contents of this file marked
* "Separately-Licensed" may be used under the terms of a UIRF Software
* license ("UIRF Software License"), in which case the provisions of a
* UIRF Software License are applicable instead of those above. 
*/
package org.openelis.scriptlet.ms.firstTri;

import org.openelis.scriptlet.ms.Util;

/**
 * The class that stores data such as means and constant multipliers for Trisomy
 * 18 for 1st trimester tests; it also computes the disorder's risk
 */
public class Trisomy18 extends Risk {
    
    private double constUA[], meanDS[], discrUA;
    
    protected Trisomy18() {
        super(new double[] {0.0, 0.0, 0.0},
               new double[] {11.6713, -1.7272, 0.5495,
                             -1.7272, 23.7483, -7.5547,
                             0.5495, -7.5547, 11.8185},
               0.000387362);
        meanDS = new double[] {0.2977, -0.5541, -0.4290};
        constUA = new double[] {56.9598, 0.0000, 0.0000,
                                 0.0000, 10.2272,-0.0752,
                                 0.0000, -0.0752, 22.1043};
        discrUA = 0.0000776625;
    }

    @Override
    protected void computeRisk(double[] mom, double apr, int gestAgeNT, int gestAge) {
        double ordinalUA, ordinalDS;

        ordinalUA = Util.getOrdinal(mom, 3, discrUA, getMeanUA(), constUA);
        ordinalDS = Util.getOrdinal(mom, 3, getDiscriminantDS(), meanDS, getConstDS());
        setRisk((int)Math.ceil(apr / (ordinalDS / ordinalUA)));
    }
}