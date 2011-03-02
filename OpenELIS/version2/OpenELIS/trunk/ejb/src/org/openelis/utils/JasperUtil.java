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
package org.openelis.utils;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;

public class JasperUtil {

    /**
     * Concats two strings together. Null parameters are ignored.
     */
    public static String concat(Object a, Object b) {
        StringBuffer buf;

        buf = new StringBuffer();
        if (a != null)
            buf.append(a.toString().trim());
        if (b != null)
            buf.append(b.toString().trim());

        return buf.toString();
    }

    /**
     * Concats two strings together with the specified delimiter. Null
     * parameters are ignored and the delimiter is not used.
     */
    public static String concatWithSeparator(Object a, Object delimiter, Object b) {
        StringBuffer buf;

        buf = new StringBuffer();
        if (a != null)
            buf.append(a.toString().trim());
        if (b != null) {
            if (a != null)
                buf.append(delimiter);
            buf.append(b.toString().trim());
        }
        return buf.toString();
    }

    /**
     * Concats a list of objects together using delimiter.
     */
    public static String concatWithSeparator(List list, Object delimiter) {
        StringBuffer buf;

        buf = new StringBuffer();
        for (Object i : list) {
            if (buf.length() > 0)
                buf.append(delimiter);
            buf.append(i.toString().trim());
        }
        return buf.toString();
    }

    /**
     * Increments/decrements the specified timestamp by the amount and unit. The
     * unit is a Calendar.HOUR, Calendar.DAY or any other Calendar field.
     */
    public static Timestamp changeDate(Timestamp date, Integer amount, Integer unit) {
        Calendar c;

        if (date == null || amount == 0)
            return date;

        c = Calendar.getInstance();
        c.setTimeInMillis(date.getTime());
        c.add(unit, amount);

        return new Timestamp(c.getTimeInMillis());
    }

    /**
     * Concats date with time to call the overloaded changeDate method:
     */
    public static Timestamp concatDateAndTime(Timestamp date, Timestamp time) {
        Calendar c;

        if (date == null || time == null)
            return date;

        c = Calendar.getInstance();
        c.setTimeInMillis(date.getTime());
        c.set(Calendar.HOUR_OF_DAY, time.getHours());
        c.set(Calendar.MINUTE, time.getMinutes());
        c.set(Calendar.SECOND, time.getSeconds());

        return new Timestamp(c.getTimeInMillis());
    }

    /**
     * Returns the difference between two dates in hours
     */
    public static Integer delta_hours(Timestamp startdate, Timestamp enddate) {
        Long span;
        if (startdate == null || enddate == null)
            return null;
        span = (enddate.getTime() - startdate.getTime()) / 1000 / 60 / 60;
        return span.intValue();
    }

    /**
     * Converts hours to days and hours. 4.09 is 4 days and 9 hours
     */
    public static Float daysAndHours(int hours) {
        int days, hrs;
        String s = null;
        days = Math.abs(hours) / 24;
        hrs = Math.abs(hours) % 24;
        if (hours < 0) {
            if (hrs < 10)
                s = "-" + days + ".0" + hrs;
            else
                s = "-" + days + "." + hrs;
        } else {
            if (hrs < 10)
                s = days + ".0" + hrs;
            else
                s = days + "." + hrs;
        }
        float f = Float.parseFloat(s);
        return f;
    }
}