package org.openelis.utils;

import java.util.*;

/**
  * A class to manage a the count of positive tests.
  */
public class Counter {

    private Hashtable count = new Hashtable();

    /**
      * This methods manages a counter for the specified string "key". The
      * counter for "key" is incremented everytime "positive" is true.
      */
    public Boolean set(String key, boolean positive) {
        int     n = 0;
        Integer oldCount;

        if (positive) {
            oldCount = (Integer) count.get(key);
            if (oldCount != null)
                n = oldCount.intValue();
            count.put(key, new Integer(n+1));
        }
        return Boolean.FALSE;
    }

    /**
     * This methods manages a counter for the specified string "key". The
     * counter for "key" is incremented everytime "positive" is true.
     */
   public Boolean setIfAbsent(String key, boolean positive) {
       int     n = 0;
       Integer oldCount;

       if (positive) {
           oldCount = (Integer) count.get(key);
           if (oldCount == null)
               count.put(key, new Integer(n+1));
       }
       return Boolean.FALSE;
   }

    /**
      * Returns the counter value for "key".
      */
    public Integer get(String key) {
        return (Integer) count.get(key);
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
         int         total = 0;
         String      key;
         Enumeration e;
         
         e = count.keys();
         while (e.hasMoreElements()) {
             key = (String) e.nextElement();
             total += ((Integer)count.get(key)).intValue();
         }
         return new Integer(total);
     }

    /**
      * Prints the keys and their counters
      */
    public String toString() {
        String       key;
        Enumeration  e;
        StringBuffer buff;

        e = count.keys();
        buff = new StringBuffer();
        while (e.hasMoreElements()) {
            key = (String) e.nextElement();
            if (buff.length() > 0)
                buff.append('\n');
            buff.append(count.get(key))
                .append("  ")
                .append(key);
        }
        return buff.toString();
    }

    /**
      * Adds all the keys and their counters to this class. Used for
      * totals.
      */
    public Boolean add(Counter from) {
        int          n;
        String       key;
        Integer      oldCount;
        Enumeration  e;

        e = from.count.keys();
        while (e.hasMoreElements()) {
            key = (String) e.nextElement();
            n   = from.get(key).intValue();
            oldCount = (Integer) count.get(key);
            if (oldCount != null)
                n += ((Integer)oldCount).intValue();
            count.put(key, new Integer(n));
        }
        return Boolean.FALSE;
    }
}
