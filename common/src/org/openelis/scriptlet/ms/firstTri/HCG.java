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

import org.openelis.constants.Messages;
import org.openelis.ui.common.InconsistencyException;

/**
 * The class that stores data for PAPP-A for the first trimester tests; it also
 * computes weight adjustment factor for MoM and median based on gestational age
 */
public class HCG extends Test {
    
    protected HCG() {
        super(new double[] {.3, 3.0}, new double[] {.3, 1.0}, 70, 97, Messages.get()
                                                                              .scriptlet_hcg());
    }
    
    @Override
    protected void validateResult(boolean isIntegrated) throws Exception {
    }

    @Override
    protected double getAdjWeight(double w) {
        return (101.6335 / w) + 0.35675;
    }

    @Override
    protected double getMedian(int measurement) throws Exception {
        if (getP1() == null || getP2() == null)
            throw new InconsistencyException(getPValueError("P1", "P2"));
        return getP1() * measurement + getP2();
    }

    @Override
    protected int getMeasurement(Integer crl, int gestAge) {
        return gestAge;
    }
}