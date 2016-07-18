package org.openelis.utils;

import java.util.Date;

import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.Datetime;
import org.openelis.ui.common.InconsistencyException;

public class ParseUtil {

    /*
     * Checks a string for max length and if it can be null. Throws an exception
     * using the fieldName parameter to identify the field in error. Returns the
     * trimmed string.
     */
    public static String parseStrField(String s, int maxlen, boolean canBeNull, String fieldName) throws Exception {
        s = DataBaseUtil.trim(s);
        if (s == null) {
            if ( !canBeNull)
                throw new InconsistencyException(fieldName + " is empty");
        } else if (maxlen < 0 && s.length() > -maxlen) {
            s = DataBaseUtil.trim(s.substring(0, -maxlen));
        } else if (maxlen > 0 && s.length() > maxlen) {
            throw new InconsistencyException(fieldName + " length > " + maxlen);
        }
        return s;
    }

    /*
     * Converts a field into Integer and checks if it can be null. Throws an
     * exception using the fieldName parameter to identify the field in error.
     * Returns Integer.
     */
    public static Integer parseIntField(String s, boolean canBeNull, String fieldName) throws Exception {
        s = DataBaseUtil.trim(s);
        if (s == null) {
            if ( !canBeNull)
                throw new InconsistencyException(fieldName + " is empty");
            return null;
        } else {
            try {
                return Integer.valueOf(s);
            } catch (NumberFormatException e) {
                throw new InconsistencyException(fieldName + " has invalid number '" + s + "'");
            }
        }
    }

    /*
     * Converts a field into Date and checks if it can be null. Throws an
     * exception using the fieldName parameter to identify the field in error.
     * Returns Datetime(YEAR, DAY)
     */
    public static Datetime parseDateField(String s, boolean canBeNull, String fieldName) throws Exception {
        s = DataBaseUtil.trim(s);
        if (s == null) {
            if ( !canBeNull)
                throw new InconsistencyException(fieldName + " is empty");
            return null;
        } else {
            try {
                return Datetime.getInstance(Datetime.YEAR, Datetime.DAY, new Date(s));
            } catch (Exception e) {
                throw new InconsistencyException(fieldName + " has invalid date '" + s + "'");
            }
        }
    }
}
