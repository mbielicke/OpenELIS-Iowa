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
package org.openelis.scriptlet.ms.quad;

/**
 * The class that stores data such as means and constant multipliers for Downs
 * determined by ultrasound; it also computes the disorder's risk
 */
public class DownsUS extends Risk {
    public DownsUS() {
        super(new double[]{0.0,0.0,0.0,0.0}, 
              new double[]{-0.1308,-0.1549,0.3118,0.3384}, 
              new double[]{56.3832,-15.0500,-2.8510,-7.1003,-15.0500,
                           81.2878,0.9529,5.5208,-2.8510,0.9529,
                           23.8106,-10.7598,-7.1003,5.5208,-10.7598,
                           29.4551},
              new double[]{53.7735,1.2244,-4.2979,-3.1688,1.2244,
                           79.5173,12.0368,7.4086,-4.2979,12.0368,
                           23.3571,-6.5446,-3.1688,7.4086,-6.5446,
                           17.8032});
    }

    @Override
    public void computeRisk(double[] mom, double apr) {
        double ordinalUA, ordinalDS;
        
        ordinalUA = getOrdinal(mom, 4, 0.000000418797,
                               getMeanUA(), getConstUA());
        ordinalDS = getOrdinal(mom, 4, 0.000000793465,
                               getMeanDS(), getConstDS());
        setRisk((int) (apr / (ordinalDS / ordinalUA)));
    }
}
