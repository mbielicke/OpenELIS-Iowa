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
package org.openelis.scriptlet.ms.conf;

import org.openelis.scriptlet.ms.quad.NTD;
import org.openelis.scriptlet.ms.quad.Test;

/**
 * The class for computing various risks and MoMs for the ms conf (Amniotic
 * Fluid AFP) test
 */
public class Compute extends org.openelis.scriptlet.ms.quad.Compute {    
    public Compute() {
        /*
         * instantiate tests and risks
         */
        tests = new Test[1];
        tests[0] = new AFP();
        ntd = new NTD();
    }

    /**
     * Computes various values such as gestational and due ages, MoMs and risks
     */
    public void compute() {
        cmpGestationalAges();
        cmpMotherDueAge();
        if ( !isOverridden) {
            cmpMoMs();
            cmpNTD();
        }
    }

    /**
     * Returns the limit (cutoff) for NTD
     */
    public Double getLimitNTD() {
        if ( !didCmpRisks)
            return null;

        return 2.0d;
    }
    
    /**
     * Computes NTD -- NA
     */
    protected void cmpNTD() {
        if ( !super.didCmpMoMs())
            return;
        
        ((NTD)ntd).computeRisk(null, 0.0);
        didCmpRisks = true;
    }
}