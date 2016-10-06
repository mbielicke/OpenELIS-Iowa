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

import org.openelis.constants.Messages;
import org.openelis.ui.common.InconsistencyException;

/**
 * The class that stores data for NT; it also computes weight adjustment
 * factor for MoM and median based on crl
 */
public class NT extends Test {
    
    protected NT() {
        super(new double[]{.65, 2.5}, new double[]{.8, 2.2}, 32, 80, Messages.get()
                                                                               .scriptlet_nt());
    }
    
    @Override
    protected void validateResult(boolean isIntegrated) throws Exception {
        if (getResult() >= 3.0 && isIntegrated)
            throw new InconsistencyException(Messages.get()
                                                     .result_invalidIntegNTResultException(Messages.get()
                                                                                                   .scriptlet_nt()));
    }

    @Override
    protected double getAdjWeight(double w) {
        return 1.0;
    }

    @Override
    protected double getMedian(int measurement) throws Exception {
        if (getP1() == null || getP2() == null)
            throw new InconsistencyException(getPValueError("P1", "P2"));
        return Math.pow(10.0, getP1() * measurement + getP2());
    }

    @Override
    protected int getMeasurement(Integer crl, int gestAge) {
        return crl;
    }
}