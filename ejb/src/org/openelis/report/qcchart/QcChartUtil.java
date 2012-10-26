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
package org.openelis.report.qcchart;

public class QcChartUtil {

    public static Double getMeanRecovery(Double mean, String expected) {
        try {
            return getMeanRecovery(mean, Double.parseDouble(expected));
        } catch (Exception e) {
            return null;
        }
    }

    public static Double getMeanRecovery(Double mean, Double expected) {
        Double meanRecovery;

        meanRecovery = null;
        if (mean != null && expected != null)
            meanRecovery = (mean / expected) * 100;

        return meanRecovery;
    }

    public static Double getConcentrationBias(Double mean, String expected) {
        try {
            return getConcentrationBias(mean, Double.parseDouble(expected));
        } catch (Exception e) {
            return null;
        }
    }

    public static Double getConcentrationBias(Double mean, Double expected) {
        Double concBias;

        concBias = null;
        if (mean != null && expected != null)
            concBias = mean - expected;

        return concBias;
    }

    public static Double getPercentBias(Double mean, String expected) {
        try {
            return getPercentBias(mean, Double.parseDouble(expected));
        } catch (Exception e) {
            return null;
        }
    }

    public static Double getPercentBias(Double mean, Double expected) {
        Double percentBias, meanRecovery;

        percentBias = null;
        meanRecovery = getMeanRecovery(mean, expected);
        if (meanRecovery != null)
            percentBias = meanRecovery - 100;

        return percentBias;
    }

    public static Double getSD(Double mean, Double ucl, Double lcl) {
        Double sD;

        sD = null;
        if (mean != null) {
            if (ucl != null)
                sD = (ucl - mean) / 3;
            else if (lcl != null)
                sD = (mean - lcl) / 3;
        }
        return sD;
    }

    public static Double getRelativeSD(Double sD, Double mean) {
        Double relSD;
        
        relSD = null;
        if(sD != null && mean != null)
            relSD = (sD/mean) * 100;
            
        return relSD;
    }

}
