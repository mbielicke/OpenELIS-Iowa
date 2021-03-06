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

import org.openelis.constants.Messages;
import org.openelis.ui.common.Datetime;

/**
 * The class that stores data for NT; it also computes weight adjustment factor
 * for MoM and median based on crl
 */
public class NT extends Test {

    protected NT() {
        super(null, null, new Double[] {0.0, 0.0}, new Double[] {.65, 2.5}, new Double[] {0.0, 0.0},
              32, 80, Messages.get().scriptlet_nt());
    }
    
    @Override
    protected void computeMoms(int gestAge, Datetime entered, Double weight, boolean isRaceBlack,
                               boolean isDiabetic) throws Exception {
        /*
         * the MoM isn't calculated here, it's set from the first trimester test
         */
    }

    @Override
    protected double getAdjWeight(double w) {
        return w;
    }

    @Override
    protected double getMedian(int ga) throws Exception {
        return 0.0;
    }
}