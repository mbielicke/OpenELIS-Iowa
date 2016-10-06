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

import org.openelis.scriptlet.ms.Util;

/**
 * The class that stores data such as means and constant multipliers for Trisomy
 * 18 for the ms quad test; it also computes the disorder's risk
 */
public class Trisomy18 extends Risk {
    public Trisomy18() {
        super(new double[] {0.0, 0.0, 0.0},
              new double[] { -0.1871, -0.3665, -0.4437},
              new double[] {38.1655, -12.8098, -3.8796, -12.8098, 58.4191, 
                            7.9554, -3.8796, 7.9554, 18.5590},
              new double[] {27.2012, -4.4544, -0.5463, -4.4544, 12.4186,
                              -0.7700, -0.5463, -0.7700, 7.1026});
    }

    @Override
    public void computeRisk(double[] mom, double apr) {
        double ordinalUA, ordinalDS;

        ordinalUA = Util.getOrdinal(mom, 3, 0.0000279099, getMeanUA(), getConstUA());
        ordinalDS = Util.getOrdinal(mom, 3, 0.000447477, getMeanDS(), getConstDS());
        setRisk((int)Math.round(apr / (ordinalDS / ordinalUA)));
    }
}