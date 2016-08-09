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
package org.openelis.scriptlet.ms;

import java.util.HashMap;

import org.openelis.constants.Messages;
import org.openelis.domain.AnalysisViewDO;
import org.openelis.domain.AnalyteParameterViewDO;
import org.openelis.domain.Constants;
import org.openelis.domain.SampleItemViewDO;
import org.openelis.manager.AnalyteParameterManager1;
import org.openelis.manager.AnalyteParameterManager1.AnalyteCombo;
import org.openelis.scriptlet.SampleSO;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.Datetime;
import org.openelis.ui.common.FormErrorException;
import org.openelis.ui.scriptlet.ScriptletObject.Status;

/**
 * This class is used to provide general functionality to the scriptlet for
 * maternal screening tests
 */
public class Util {
    /**
     * Returns the birth date of the egg donor; it's calculated by subtracting
     * the passed egg's age from the passed sample's collection date
     */
    public static Datetime getBirthDate(Double eggAge, ScriptletProxy proxy, SampleSO data,
                                        AnalysisViewDO ana) {
        int months;
        Integer accession;
        Datetime cd;

        if (eggAge == null)
            return null;

        cd = data.getManager().getSample().getCollectionDate();
        if (cd == null) {
            accession = data.getManager().getSample().getAccessionNumber();
            /*
             * for display
             */
            if (accession == null)
                accession = 0;
            data.setStatus(Status.FAILED);
            data.addException(new FormErrorException(DataBaseUtil.concatWithSeparator(Messages.get()
                                                                                              .sample_accessionAnalysisPrefix(accession,
                                                                                                                              ana.getTestName(),
                                                                                                                              ana.getMethodName()),
                                                                                      " ",
                                                                                      Messages.get()
                                                                                              .result_missingCollectedDateException())));
            return null;
        }
        /*
         * find the total number of months specified by the egg's age and
         * subtract them from the collected date to get the birth date
         */
        months = (int) (Math.ceil(eggAge * 12));
        return proxy.addMonthsToDate(cd, -months);
    }

    /**
     * Returns a map created from the analyte parameters for the record
     * specified by the passed reference id and reference table id; the map's
     * key is analyte id and value is the most recent analyte parameter for that
     * analyte that's active for the passed analysis' started date
     */
    public static HashMap<Integer, AnalyteParameterViewDO> getParameters(Integer referenceId,
                                                                         Integer referenceTableId,
                                                                         ScriptletProxy proxy,
                                                                         SampleSO data,
                                                                         AnalysisViewDO ana) throws Exception {
        int i, j;
        Datetime sd;
        SampleItemViewDO item;
        AnalyteCombo ac;
        AnalyteParameterViewDO ap;
        AnalyteParameterManager1 apm;
        HashMap<Integer, AnalyteParameterViewDO> paramMap;

        sd = ana.getStartedDate();
        if (sd == null)
            return null;

        apm = proxy.fetchParameters(referenceId, referenceTableId);

        paramMap = new HashMap<Integer, AnalyteParameterViewDO>();
        /*
         * find the most recent analyte parameters defined for the analysis'
         * sample type and whose active range contains the passed analysis'
         * started date
         */
        item = (SampleItemViewDO)data.getManager()
                                     .getObject(Constants.uid()
                                                         .getSampleItem(ana.getSampleItemId()));
        for (i = 0; i < apm.analyte.count(); i++ ) {
            ac = apm.analyte.get(i);
            for (j = 0; j < apm.analyteParameter.count(ac.getId()); j++ ) {
                ap = apm.analyteParameter.get(ac.getId(), j);
                if (DataBaseUtil.isSame(item.getTypeOfSampleId(), ap.getTypeOfSampleId()) &&
                    (ap.getActiveBegin().compareTo(sd) <= 0) &&
                    ap.getActiveEnd().compareTo(sd) >= 0) {
                    paramMap.put(ap.getAnalyteId(), ap);
                    break;
                }
            }
        }
        return paramMap;
    }

    /**
     * Formats the passed value to have one or two fractional digits based on
     * "precision"; if "precision" is not one or two, returns the value as is
     */
    public static String format(Double value, int precision, ScriptletProxy proxy) throws Exception {
        if (value == null)
            return null;

        switch(precision) {
            case 1:
                return proxy.format(value, "#0.0");
            case 2:
                return proxy.format(value, "#0.00");
            default:
                return value.toString();
        }
    }

    /**
     * Truncates a double to given decimal digits
     */
    public static double trunc(double value, int dp) {
        double factor;

        factor = Math.pow(10, dp);
        return Math.floor(value * factor) / factor;
    }
}