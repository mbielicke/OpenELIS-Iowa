/** Exhibit A - UIRF Open-source Based Public Software License.
* 
* The contents of this file are subject to the UIRF Open-source Based
* Public Software License(the "License"); you may not use this file except
* in compliance with the License. You may obtain a copy of the License at
* openelis.uhl.uiowa.edu
* 
* Software distributed under the License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
* License for the specific language governing rights and limitations
* under the License.
* 
* The Original Code is OpenELIS code.
* 
* The Initial Developer of the Original Code is The University of Iowa.
* Portions created by The University of Iowa are Copyright 2006-2008. All
* Rights Reserved.
* 
* Contributor(s): ______________________________________.
* 
* Alternatively, the contents of this file marked
* "Separately-Licensed" may be used under the terms of a UIRF Software
* license ("UIRF Software License"), in which case the provisions of a
* UIRF Software License are applicable instead of those above. 
*/
package org.openelis.utilcommon;

import org.openelis.exception.ParseException;

/**
 * This class is used to manage a titer range. Titer ranges have a minimum and
 * a maximum arbitrary value in the format 1:32, 1:64, and 2:4 etc.
 *
 */
public class TiterRange {
    protected int min, max;
    protected String separator = ":";

    public TiterRange(String value) throws ParseException {
        parse(value);
    }

    public void parse(String value) throws ParseException {
        String st[];
        
        if (value == null)
            throw new ParseException("illegalTiterFormatException");
        
        st = value.split(separator);
        if (st.length != 2)
            throw new ParseException("illegalTiterFormatException");
        
        try {
            min = Integer.parseInt(st[0]);
            max = Integer.parseInt(st[1]);
            if(min <= 0 || max <= 0)
                throw new ParseException("illegalTiterFormatException");
            if(min > max)
                throw new ParseException("illegalTiterRangeException");
        } catch (NumberFormatException ex) {
            throw new ParseException("illegalTiterFormatException");
        }
    }
    
    public int getMin() {
        return min;
    }
    
    public int getMax() {
        return max;
    }
    
    public boolean isInRange(String value) {
        TiterRange t;
        
        try {
            t = new TiterRange(value);
            return t.getMax() >= min && t.getMax() <= max;
        } catch (ParseException ignE) {}
        
        return false;
    }
    
    public boolean isInRange(int value) {
        return value >= min && value <= max;
    }
    
    public boolean isOverlapping(TiterRange value) {
        return (value.getMin() >= min && value.getMin() <= max) ||
               (value.getMax() >= min && value.getMax() <= max) ||
               (value.getMax() >= max && value.getMin() <= min);
    }

    public String toString() {
        return min+separator+max;
    }
}