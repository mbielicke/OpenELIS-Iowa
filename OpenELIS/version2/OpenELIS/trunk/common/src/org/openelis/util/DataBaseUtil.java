package org.openelis.util;

public class DataBaseUtil {

    public static String trim(String result) {
        if (result == null || result.length() == 0)
            return null;
        else
            return result.trim();
    }
}
