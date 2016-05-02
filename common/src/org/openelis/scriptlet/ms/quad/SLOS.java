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
 * The class that stores the data for SLOS risk 
 */
public class SLOS extends Risk {
    public SLOS() {
        super(new double[]{0.0,0.0,0.0}, 
              new double[]{-0.1427,-0.6778,-0.1192}, 
              new double[]{49.085,-19.453,-5.651,-19.453,65.153,
                           9.622,-5.651,9.622,21.178},
              new double[]{54.68,-7.807,-2.229,-7.807,10.791,
                           -3.928,-2.229,-3.928,11.975});
    }

    @Override
    public void computeRisk(double[] mom, double apr) {
        double ordinalUA, ordinalDS;
        
        ordinalUA = getOrdinal(mom, 3, 0.00001811,
                               getMeanUA(), getConstUA());
        ordinalDS = getOrdinal(mom, 3, 0.0001886,
                               getMeanDS(), getConstDS());
        setRisk((int) Math.round(20000/(ordinalDS / ordinalUA)));
        setDidCmpRisk(true);
    }
}
