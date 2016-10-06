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

import org.openelis.constants.Messages;
import org.openelis.ui.common.Datetime;
import org.openelis.ui.common.InconsistencyException;

/**
 * The class that stores data for AFP for the ms conf (Amniotic Fluid AFP) test;
 * its computations for MoM, NTD limit and weight adjustment factor for MoM are
 * different than the default AFP computations
 */
public class AFP extends org.openelis.scriptlet.ms.quad.AFP {
    /**
     * Overridden because for this test, min gestational age is different from
     * the one used for quad test
     */
    protected int getMinDay() {
        return 96;
    }

    /**
     * Overridden because for this test, max gestational age is different from
     * the one used for quad test
     */
    protected int getMaxDay() {
        return 168;
    }

    /**
     * Returns the computed MoM value after applying corrections
     */
    protected double getMoM(int ga, Datetime entered, Double weight, boolean isRaceBlack,
                            boolean isDiabetic) throws Exception {
        double mom, median, adjWeight;

        if (entered == null)
            throw new InconsistencyException(Messages.get().result_missingEnteredDateException());

        /*
         * the weight adjustment factor
         */
        adjWeight = 1.0;
        /*
         * the median for gestational age
         */
        if (ga < getMinDay() || ga > getMaxDay())
            median = 0.0;
        else
            median = getMedianforAge(ga);

        if (getResult() == null || getResult() == 0.0)
            throw new InconsistencyException(Messages.get().result_missingInvalidResultException(getAnalyteName()));
        if (median == 0.0)
            throw new InconsistencyException(Messages.get().result_noMedianForGestAgeException(ga));

        mom = getResult() / median / adjWeight;
        if (isDiabetic)
            mom /= getInsuline();

        return mom;
    }
}