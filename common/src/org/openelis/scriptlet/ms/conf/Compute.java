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

import org.openelis.scriptlet.ms.conf.Constants.Sample_Type;
import org.openelis.scriptlet.ms.quad.NTD;

/**
 * The class for computing various risks and MoMs for the ms conf (Amniotic
 * Fluid AFP) test
 */
public class Compute extends org.openelis.scriptlet.ms.quad.Compute {
    private Sample_Type sampleType;

    public void setSampleType(Sample_Type sampleType) {
        this.sampleType = sampleType;
    }

    /**
     * Computes various values such as gestational and due ages, MoMs and risks
     */
    public void compute() {
        cmpGestationalAges();
        cmpMotherDueAge();
        /*
         * this object is created here because the scriptlet gets MoMs, risks
         * etc. from this compute engine even if results are overridden; the
         * engine gets those values from this object, so it can't be null in any
         * case; objects for tests e.g. AFP are set by the scriptlet
         */
        ntd = new NTD();
        if ( !isOverridden) {
            cmpMoMs();
            cmpNTD();
        }
    }

    /**
     * Returns the limit (cutoff) for NTD
     */
    public Double getLimitNTD() {
        double limit;

        if ( !ntd.getDidCmpRisk())
            return null;

        limit = 0.0d;
        if (numFetus.intValue() == 1) {
            if (Sample_Type.SERUM.equals(sampleType))
                limit = (hasNTDHistory) ? 2.0d : 2.2d;
            else
                limit = 2.0d;
        }
        if (numFetus.intValue() == 2) {
            if (Sample_Type.SERUM.equals(sampleType))
                limit = (hasNTDHistory) ? 4.0d : 4.4d;
            else
                limit = 2.0d;
        }
        return new Double(limit);
    }

    /**
     * Computes Multiple of Medians for AFP
     */
    protected void cmpMoMs() {
        if ( !didCmpGa)
            return;

        try {
            afp.computeMoms(gestAgeInit, gestAgeCurr, enteredDate, weight, isRaceBlack, isDiabetic);
        } catch (Exception indE) {
            lastException = indE;
        }
    }
}