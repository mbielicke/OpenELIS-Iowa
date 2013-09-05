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
        Integer n;

        if (flag) {
            n = count.get(key);
            count.put(key, n != null ? n + 1 : 1);
        }
        return Boolean.FALSE;
    }

    /**
     * This methods manages a counter for the specified string "key". The
     * counter for "key" is incremented every time flag is true. This method
     * returns false in order to satisfy Jasper's "print when".
     */
    public Boolean setIfAbsent(String key, boolean flag) {
        if (flag && !count.containsKey(key))
            count.put(key, 1);

        return Boolean.FALSE;
    }

    /**
     * Returns the counter value for "key".
     */
    public Integer get(String key) {
        return count.get(key);
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

        for (Integer value : count.values())
            total += value;

        return total;
    }

    public Integer getTotal(String... keys) {
        int total = 0;

        for (String key : keys)
            total += count.get(key);

        return total;
    }

    /**
     * Prints the keys and their counters
     */
    public String toString() {
        StringBuffer buff;

        buff = new StringBuffer();
        for (String key : count.keySet()) {
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
        return new ArrayList<String>(count.keySet());
    }

    /**
     * Adds all the keys and their counters to this class. Used for totals.
     */
    public Boolean add(Counter from) {
        Integer n;

        for (String key : from.getKeys()) {
            n = count.get(key);
            count.put(key, n != null ? from.get(key) + n : from.get(key));
        }
        return Boolean.FALSE;
    }
}
