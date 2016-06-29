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
 * The class that stores data such as means and constant multipliers for Downs
 * determined by LMP; it also computes the disorder's risk
 */
public class DownsLMP extends Risk {
    public DownsLMP() {
        super(new double[] {0.0, 0.0, 0.0, 0.0}, 
              new double[] { -0.1308, -0.1549, 0.3118, 0.3384},
              new double[] {56.2277, -17.1057, -2.9223, -7.0602, -17.1057, 
                            66.8092, 3.2146, 5.1928, -2.9223, 3.2146, 23.6769,
                            -10.3782, -7.0602, 5.1928, -10.3782, 29.2276},
              new double[] {52.2051, -3.8360, -4.5708, -3.5110, -3.8360,
                             65.9394, 12.5197, 6.4201, -4.5708, 12.5197, 23.6827,
                             -6.3510, -3.5110, 6.4201, -6.3510, 17.6433});
    }

    @Override
    public void computeRisk(double mom[], double apr) {
        double ordinalUA, ordinalDS;

        ordinalUA = getOrdinal(mom, 4, 0.000000531883, getMeanUA(), getConstUA());
        ordinalDS = getOrdinal(mom, 4, 0.000000996054, getMeanDS(), getConstDS());
        setRisk((int) (apr / (ordinalDS / ordinalUA)));
    }
}