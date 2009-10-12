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

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.openelis.gwt.common.Datetime;

public class DataBaseUtil {

    /*
     * Removed blanks from both sides of the string and nullifies empty strings
     */
    public static String trim(String result) {
        if (result != null) {
            result = result.trim();
            if (result.length() == 0)
                result = null;
        }
        return result;
    }

    /*
     * Convenience methods to covert a date or a datetime to datetime with date
     * precision.
     */
    public static Datetime toYD(Datetime yearToDay) {
        if (yearToDay != null) {
            yearToDay.startCode = Datetime.YEAR;
            yearToDay.endCode = Datetime.DAY;
        }
        return yearToDay;
    }

    public static Datetime toYD(Date yearToDay) {
        Datetime dt;

        dt = null;
        if (yearToDay != null)
            dt = new Datetime(Datetime.YEAR, Datetime.DAY, yearToDay);

        return dt;
    }

    public static Datetime toYM(Datetime yearToMinute) {
        if (yearToMinute != null) {
            yearToMinute.startCode = Datetime.YEAR;
            yearToMinute.endCode = Datetime.MINUTE;
        }
        return yearToMinute;
    }

    public static Datetime toYM(Date yearToMinute) {
        Datetime dt;

        dt = null;
        if (yearToMinute != null)
            dt = new Datetime(Datetime.YEAR, Datetime.MINUTE, yearToMinute);

        return dt;
    }

    public static Datetime toHM(Datetime hourToMinute) {
        if (hourToMinute != null) {
            hourToMinute.startCode = Datetime.HOUR;
            hourToMinute.endCode = Datetime.MINUTE;
        }
        return hourToMinute;
    }

    public static Datetime toHM(Date hourToMinute) {
        Datetime dt;

        dt = null;
        if (hourToMinute != null)
            dt = new Datetime(Datetime.HOUR, Datetime.MINUTE, hourToMinute);

        return dt;
    }

    /**
     * Compares the two parameters to see if they are different
     * @return true if object is the same; otherwise false
     */
    public static boolean isDifferent(Object a, Object b) {
        return (a == null && b != null) || (a != null && !a.equals(b));
    }
    
    /**
     * Checks the parameter to see if its null or its length is 0.
     * @return true if object is empty; otherwise false
     */
    public static boolean isEmpty(Object a) {
        if (a instanceof String)
            return ((String)a).length() == 0;
        return a == null;
    }
    
    
    /*
     * For paged result list, this method returns a subList of the query list
     * starting at first for max number of results.
     */
    public static ArrayList subList(List query, int first, int max) {
        int to;
        Iterator e;
        ArrayList list;

        if (query == null || query.isEmpty() || first >= query.size())
            return null;

        to = Math.min(first + max, query.size());
        list = new ArrayList(to - first);
        e = query.listIterator(first);
        for (; first < to; first++ )
            list.add(e.next());

        return list;
    }

    /**
     * Convert a List to ArrayList 
     */
    public static ArrayList toArrayList(List from) {
        if (from instanceof ArrayList)
            return (ArrayList) from;
        return new ArrayList(from);
    }
    
    //
    // move this to somewhere else
    //
    public static String formatStorageLocation(String storageName,
                                               String StorageLocation,
                                               String storageUnitDesc) {
        return formatStorageLocation(storageName, StorageLocation, storageUnitDesc, null);
    }

    public static String formatStorageLocation(String storageName,
                                               String StorageLocation,
                                               String storageUnitDesc,
                                               String parentStorageLocationName) {
        String returnString = "";

        if (parentStorageLocationName != null) {
            returnString += parentStorageLocationName.trim() + ", " + storageUnitDesc.trim() +
                            ", " + StorageLocation.trim();
        } else {
            returnString += storageName.trim() + ", " + storageUnitDesc.trim() + ", " +
                            StorageLocation.trim();
        }

        return returnString;
    }
}
