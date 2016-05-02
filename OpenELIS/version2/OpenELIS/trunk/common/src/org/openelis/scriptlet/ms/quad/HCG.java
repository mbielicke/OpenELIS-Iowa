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

import org.openelis.ui.common.InconsistencyException;

/**
 * The class that stores data for HCG; it also computes weight adjustment
 * factor for MoM and median based on gestational age
 */
public class HCG extends Test {
    public HCG(Double p1, Double p2, Double p3, Double result, String analyteName) {
        super(1.12, 0.93, new double[]{0, 0}, new double[]{.4, 5}, new double[]{.2, 2.5},
              new double[]{.3, 1.5}, 105, 146, p1, p2, p3, result, analyteName);
    }

    @Override
    protected double getAdjWeight(double w) {
        return (101.0 / w) + 0.3521;
    }

    @Override
    protected double getMedianforAge(int ga) throws Exception {
        if (getP1() == null || getP2() == null || getP3() == null)
            throw new InconsistencyException(getPValueError("P1", "P2", "P3"));
        return (getP1() * Math.exp(getP2() * (ga - 98)) + getP3()) * 1000.0;
    }
}