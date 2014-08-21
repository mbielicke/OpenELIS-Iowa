package org.openelis.utils;

import java.util.*;

/**
 * A class to manage counting of "keys" for various reports.
 */
public class Counter {

    private Hashtable<String, Integer> count = new Hashtable<String, Integer>();

    /**
     * This methods manages a counter for the specified string "key". The
     * counter for "key" is incremented every time flag is true. This method
     * returns false in order to satisfy Jasper's "print when".
     */
    public Boolean set(String key, boolean flag) {
        int n = 0;
        Integer oldCount;

        if (flag) {
            oldCount = (Integer)count.get(key);
            if (oldCount != null)
                n = oldCount.intValue();
            count.put(key, new Integer(n + 1));
        }
        return Boolean.FALSE;
    }

    /**
     * This methods manages a counter for the specified string "key". The
     * counter for "key" is incremented every time flag is true. This method
     * returns false in order to satisfy Jasper's "print when".
     */
    public Boolean setIfAbsent(String key, boolean flag) {
        int n = 0;
        Integer oldCount;

        if (flag) {
            oldCount = (Integer)count.get(key);
            if (oldCount == null)
                count.put(key, new Integer(n + 1));
        }
        return Boolean.FALSE;
    }

    /**
     * Returns the counter value for "key".
     */
    public Integer get(String key) {
        return (Integer)count.get(key);
    }

    /**
     * Returns True if any counter is being maintained
     */
    public Boolean hasKey() {
        return count.isEmpty() ? Boolean.FALSE : Boolean.TRUE;
    }

    /**
     * Returns the total of all counter values.
     */
    public Integer getTotal() {
        int total = 0;
        String key;
        Enumeration<String> e;

        e = count.keys();
        while (e.hasMoreElements()) {
            key = (String)e.nextElement();
            total += ((Integer)count.get(key)).intValue();
        }
        return new Integer(total);
    }

    /**
     * Prints the keys and their counters
     */
    public String toString() {
        String key;
        StringBuffer buff;
        Enumeration<String> e;

        e = count.keys();
        buff = new StringBuffer();
        while (e.hasMoreElements()) {
            key = (String)e.nextElement();
            if (buff.length() > 0)
                buff.append('\n');
            buff.append(count.get(key)).append("  ").append(key);
        }
        return buff.toString();
    }

    /**
     * Returns all the keys
     */
    public ArrayList<String> getKeys() {
        String key;
        Enumeration<String> e;
        ArrayList<String> tokens = new ArrayList<String>();

        e = count.keys();
        while (e.hasMoreElements()) {
            key = (String)e.nextElement();
            tokens.add(key);
        }
        return tokens;
    }

    /**
     * Adds all the keys and their counters to this class. Used for totals.
     */
    public Boolean add(Counter from) {
        int n;
        String key;
        Integer oldCount;
        Enumeration<String> e;

        e = from.count.keys();
        while (e.hasMoreElements()) {
            key = (String)e.nextElement();
            n = from.get(key).intValue();
            oldCount = (Integer)count.get(key);
            if (oldCount != null)
                n += ((Integer)oldCount).intValue();
            count.put(key, new Integer(n));
        }
        return Boolean.FALSE;
    }
}
