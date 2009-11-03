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

/**
 * This class is used to manage a number range. Number ranges have a minimum and
 * a maximum value such as 10,20.
 */
public class NumericRange {
    protected double min, max;
    protected String separator = ",";

    public NumericRange(String value) throws ParseException {
        parse(value);
    }

    public void parse(String value) throws ParseException {
        String st[];
        
        if (value == null)
            throw new ParseException("illegalNumericFormatException");

        st = value.split(separator);
        if (st.length != 2)
            throw new ParseException("illegalNumericFormatException");

        try {
            min = Double.parseDouble(st[0]);
            max = Double.parseDouble(st[1]);
            if(min > max)
                throw new ParseException("illegalNumericRangeException");
        } catch (NumberFormatException ex) {
            throw new ParseException("illegalNumericFormatException");
        }
    }
    
    public double getMin() {
        return min;
    }
    
    public double getMax() {
        return max;
    }
    
    public boolean isInRange(double value) {
        return value >= min && value <= max;
    }

    public boolean isOverlapping(NumericRange value) {
        return (value.getMin() >= min && value.getMin() <= max) ||
               (value.getMax() >= min && value.getMax() <= max) ||
               (value.getMax() >= max && value.getMin() <= min);
    }

    public String toString() {
        return min+separator+max;
    }
}