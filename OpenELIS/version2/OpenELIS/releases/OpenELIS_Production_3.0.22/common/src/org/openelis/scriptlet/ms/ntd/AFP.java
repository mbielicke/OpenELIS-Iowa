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
package org.openelis.scriptlet.ms.ntd;

import org.openelis.scriptlet.ms.Util;
import org.openelis.ui.common.Datetime;
import org.openelis.ui.common.InconsistencyException;

/**
 * The class that stores data for AFP for the ms ntd (Maternal NTD Screen) test;
 * its computations for MoM and median based on gestational age are different
 * than the default AFP computations
 */
public class AFP extends org.openelis.scriptlet.ms.quad.AFP {
    public AFP(Double p1, Double p2, Double p3, Double result, String analyteName) {
        super(p1, p2, p3, result, analyteName);
    }

    @Override
    public void computeMoms(int gestAgeInit, int gestAgeCurr, Datetime entered, Double weight,
                            boolean isRaceBlack, boolean isDiabetic) throws Exception {
        double mom;

        if (getResult() != null) {
            try {
                setMomInit(0.0);
                mom = getMoM(gestAgeCurr, entered, weight, isRaceBlack, isDiabetic);
                setMomCurr(Util.trunc(mom + 0.005, 2));
                setDidCmpMoM(true);
            } catch (Exception indE) {
                setMomInit(0.0);
                setMomCurr(0.0);
                throw indE;
            }
        }
    }
    
    @Override
    protected double getMedianforAge(int ga) throws Exception {
        if (getP1() == null || getP2() == null)
            throw new InconsistencyException(getPValueError("P1", "P2"));
        if (ga < 105 || ga > 146)
            return 0.0;

        return super.getMedianforAge(ga);
    }
}