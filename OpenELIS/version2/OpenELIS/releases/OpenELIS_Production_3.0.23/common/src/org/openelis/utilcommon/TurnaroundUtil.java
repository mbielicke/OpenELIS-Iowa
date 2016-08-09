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

package org.openelis.utilcommon;

import java.util.Date;

import org.openelis.ui.common.Datetime;

public class TurnaroundUtil {

    /**
     * Returns percent of holding time used by this analysis.
     */
    public static Double getPercentHoldingUsed(Datetime startedDate, Datetime collectionDate,
                                               Datetime collectionTime, Integer timeHolding) {
        double hrs;
        Datetime cd, sd;

        if (collectionDate == null)
            return 0.0;

        cd = getCombinedYM(collectionDate, collectionTime);
        sd = startedDate != null ? startedDate : Datetime.getInstance();
        hrs = diffDT(sd, cd) / 3600000D;

        return hrs / ((double)timeHolding) * 100.0;
    }

    /**
     * Returns percent of time left to expected complete the analysis.
     */
    public static Double getPercentExpectedCompletion(Datetime collectionDate,
                                                      Datetime collectionTime,
                                                      Datetime receivedDate, Integer priority,
                                                      Integer timeTaAverage) {
        double days;
        Datetime cd;

        if (collectionDate == null && receivedDate == null)
            return 0.0;

        cd = getCombinedYM(collectionDate, collectionTime);
        if (cd == null)
            cd = receivedDate;
        if (priority != null)
            timeTaAverage = priority;

        days = diffDT(new Date(), cd) / 86400000D;
        return (days / (double)timeTaAverage) * 100.0;
    }

    /**
     * Returns number of days remaining to complete analysis.
     */
    public static Integer getDueDays(Datetime received, Integer expectedDays) {
        if (received == null || expectedDays == null)
            return null;

        return expectedDays - (int) (diffDT(new Date(), received) / 86400000L);
    }

    /**
     * Returns Datetime the analysis expiration.
     */
    public static Datetime getExpireDate(Datetime collectionDate, Datetime collectionTime,
                                         Integer holdingHours) {
        Date d;
        Datetime cd;

        if (collectionDate == null)
            return null;

        cd = getCombinedYM(collectionDate, collectionTime);
        return new Datetime(Datetime.YEAR, Datetime.MINUTE, new Date(cd.getDate().getTime() +
                                                                     holdingHours * 3600000L));
    }

    /*
     * Returns the combined date and time in a datetime object
     */
    public static Datetime getCombinedYM(Datetime d, Datetime t) {
        Datetime dt;

        dt = null;
        if (d != null) {
            dt = new Datetime(Datetime.YEAR, Datetime.MINUTE, d.getDate());
            if (t != null) {
                dt.getDate().setHours(t.getDate().getHours());
                dt.getDate().setMinutes(t.getDate().getMinutes());
            }
        }
        return dt;
    }
    
    /*
     * Returns the number of days between starting and ending dates
     */
    public static Integer diffDays(Datetime starting, Datetime ending) {
        if (starting == null || ending == null)
            return null;
        return (int)Math.ceil((double)diffDT(ending, starting) / 86400000D);
    }

    /**
     * Returns the millisecond difference between two dates
     */
    private static long diffDT(Datetime a, Datetime b) {
        return a.getDate().getTime() - b.getDate().getTime();
    }

    private static long diffDT(Date a, Datetime b) {
        return a.getTime() - b.getDate().getTime();
    }
}
