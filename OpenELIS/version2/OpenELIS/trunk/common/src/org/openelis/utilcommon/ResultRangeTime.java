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

import org.openelis.exception.ParseException;

public class ResultRangeTime implements ResultRange {
    private static final long serialVersionUID = 1L;
    
    protected String time;

    public void setRange(String format) throws ParseException {
    }

    public void contains(String time) throws ParseException {
        String st[];
        int hrs, min;

        if (time == null)
            return;

        try {
            st = time.split(":");
            if (st.length != 2)
            	throw new ParseException("illegalTimeValueException");
            
            if (st[0].length() > 2)
                st[0] = st[0].substring(st[0].length()-2, st[0].length());
            hrs = Integer.parseInt(st[0]);

            if (st[1].length() > 2)
                st[1] = st[1].substring(st[1].length()-2, st[1].length());
            min = Integer.parseInt(st[1]);            
            
            if (hrs < 0 || hrs > 23 || min < 0 || min > 59) 
            	throw new ParseException("illegalTimeValueException");

            this.time = d2(hrs) + ":" + d2(min); 
        } catch (IllegalArgumentException ex) {
            throw new ParseException("illegalTimeValueException");
        }
    }

    public boolean intersects(ResultRange range) {
        return false;
    }

    public String toString() {
        return time;
    }
    
    private String d2(int n) {
    	if (n < 10)
    		return "0"+n;
    	return String.valueOf(n);
    }
}
