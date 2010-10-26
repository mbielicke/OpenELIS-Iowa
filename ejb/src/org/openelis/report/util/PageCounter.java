package org.openelis.report.util;

/**
  * Set of conveniance methods for jasper report paginations
  */
public class PageCounter {
    int    count = 0;
    String reportType;

    public PageCounter(String reportType) {
        this.reportType = reportType;
    }
    public Boolean incrementCount() {
        count++;
        return Boolean.FALSE;
    }

    public Integer getCount() {
        return new Integer(count);
    }
    
    public Boolean isFirstPage() {
        if (count == 1  && ("B".equals(reportType) || "R".equals(reportType))) 
            return Boolean.TRUE;
        return Boolean.FALSE;
    }
    public Boolean isNotFirstPage() {
        if (count != 1 && ("B".equals(reportType) || "R".equals(reportType))) 
            return Boolean.TRUE;
        return Boolean.FALSE;
    }   
}
