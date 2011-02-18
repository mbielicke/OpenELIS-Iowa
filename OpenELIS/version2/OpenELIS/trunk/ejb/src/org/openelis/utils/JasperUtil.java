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

//import java.sql.Date;
import java.sql.Timestamp;
import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class JasperUtil {
	
	public enum Type{DAY,HOUR};
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
     * Returns a new String representation of a Timestamp object with the specified Timestamp field incremented
     * (+amount) or decremented (-amount) by "amount" which is of the type Type. This implementation uses
     * Calendar.add() method to provide this functionality.
     * E.g.: changeDate("2009-02-12 08:20", 2, JasperUtil.TYPE.DAY) would yield "2009-02-14 08:20"
     *       changeDate("2009-02-12 08:20", 2, JasperUtil.TYPE.HOUR) would yield "2009-02-12 10:20"
     **/  
     public static String changeDate(Timestamp date, int amount, Type type ) {
        Calendar c;   
        long time;
        String retDate;
        DateFormat formatter;  
        
        if(date == null)
        	return null;
        
        c = Calendar.getInstance();
        time = date.getTime();
        retDate = null;
    	formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    	c.setTimeInMillis(time); 
    	
        if(amount == 0){
        	formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        	return formatter.format(c.getTime());
        }     
    	
        switch(type){
        case HOUR:         	    	       
        	c.add(Calendar.HOUR, amount);
        	retDate = formatter.format(c.getTime());
        	break;
        case DAY: 
        	c.add(Calendar.DAY_OF_MONTH, amount);
        	retDate = formatter.format(c.getTime());
        	break;        
        }     	
    	return retDate;
    } 
    
    /**
     * Concats date with time to call the overloaded changeDate method: changeDate(Timestamp date, int amount, Type type )   
     */
    public static String changeDate(Timestamp date, Timestamp time, int amount, Type type) {             
    	String tempDate, tempTime, retDate;
    	Timestamp t;
    	DateFormat formatter;
    	Date dt;
    	long ms;
    	
    	dt = null;
    	
    	if(date == null)
        	return null;
    	
    	tempDate = date.toString().trim().substring(0, 10);
    	
    	if(time == null){
    		retDate =  tempDate + " 00:00";    		
    	}
    	else{
    		formatter = new SimpleDateFormat("HH:mm");    		
    		tempTime = time.toString().substring(11, 16);    		
    		retDate = concatWithSeparator(tempDate, " ",tempTime); 
    	}
    	
    	formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");    	
    	try {
			dt = (Date)formatter.parse(retDate);
		} catch (ParseException e) {			
			e.printStackTrace();
		}		
		ms = dt.getTime();
		t = new Timestamp(ms);		
		retDate=changeDate(t, amount, type);	
		
		return retDate;    	
    }
}