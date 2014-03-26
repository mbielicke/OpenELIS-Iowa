package org.openelis.report.turnaround;

import java.util.*;

/**
  * A class to manage statistics such as min, max, average, ... for an
  * interval.
  */
public class CounterTurnaround {
    private boolean   changed;
    private ArrayList<Integer> intervals = new ArrayList<Integer>();

    /**
      * This methods appends the new interval to the list.
      */
    public Object set(String interval) {
        Integer i;

        if (interval != null) {
            try {
                i = Integer.valueOf(interval.trim());
                intervals.add(i);
                changed = true;
            } catch (NumberFormatException ignE) {}
        }
        return this;
    }
    
    /**
     * This methods appends the new interval in the Integer format to the list.
     */
    public Object set(Integer interval) {
        if (interval != null) {
            intervals.add(interval);
            changed = true;
        }
        return this;
    }

    /**
     * Returns the number of elements in list
     */
    public Integer getCount() {
        return new Integer(intervals.size());
    }

    /**
     * Returns the min of list
     */
     public Integer getMin() {
         Integer min = null;

         sortList();
         if (intervals.size() > 0)
             min = (Integer) intervals.get(0);

         return min;
     }
     
     /**
      * Returns the max of list
      */
     public Integer getMax() {
         int     n;
         Integer max = null;

         sortList();
         n = intervals.size();
         if (n > 0)
             max = (Integer) intervals.get(n-1);

         return max;
     }
     
     /**
      * Returns the average in hours
      */
     public Integer getAverage() {
         int i, n, sum;
         Integer average = null;
         
         n = intervals.size();
         sum = 0;

         for (i = 0; i < n; i++)
             sum += ((Integer)intervals.get(i)).intValue();
         if (n > 0)
             average = new Integer(sum / n);
         return average; 
     }
     
     /**
      * Returns the average in days and hours. 7 days 12 hours will be represented as 7.5
      * 
      */
     public double getAverageInDays() {
        double avgHrs;       
        avgHrs = getAverage().intValue()/24.0; 
        return(Math.round(avgHrs*100.0) / 100.0);        
     }
     
     /**
      * Returns the median in days and hours
      */
     public Integer getMedian() {
         int n;
         Integer median = null;
         
         sortList();
         n = intervals.size();
         if (n > 0)
             median = (Integer) intervals.get(n/2);
         
         return median;
     }
     
     /**
      * Returns the standard deviation
      * @return
      */
     public Integer getSD() {
         int i, n;
         double average, sum, v;
         Integer sd = null;
         
         n = intervals.size();
         if (n > 1) {
             average = 0;
             for (i = 0; i < n; i++)
                 average += ((Integer)intervals.get(i)).intValue();
             average /= n;
         
             sum = 0;
             for (i = 0; i < n; i++) {
                 v = ((Integer)intervals.get(i)).intValue() - average;
                 sum += v * v;
             }
             
             sd = new Integer((int)Math.sqrt(sum/(n-1)));
         }
         return sd;
     }
     
     /*
      * Sorts the array list
      */
     private void sortList() {
         if (changed) {
             Collections.sort(intervals);
             changed = false;
         }
     }
}
