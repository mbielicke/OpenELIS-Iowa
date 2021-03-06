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

import org.openelis.constants.Messages;
import org.openelis.exception.ParseException;

/**
 * This class is used to manage a number range. The range is specified using a
 * pair of floating point numbers separated using a comma (,) such as 12.0,18.0.
 * The lower limit is inclusive and the upper limit is exclusive.
 */
public class ResultRangeNumeric implements ResultRange {
    private static final long serialVersionUID = 1L;

    protected boolean valid = false;
    protected double min, max;

    public void setRange(String range) throws ParseException {
        String st[];

        valid = false;
        if (range == null)
            throw new ParseException(Messages.get().illegalNumericFormatException());

        st = range.split(",");
        if (st.length != 2)
            throw new ParseException(Messages.get().illegalNumericFormatException());

        try {
            min = Double.parseDouble(st[0]);
            max = Double.parseDouble(st[1]);
            if (min >= max)
                throw new ParseException(Messages.get().illegalNumericRangeException());
        } catch (NumberFormatException ex) {
            throw new ParseException(Messages.get().illegalNumericFormatException());
        }
        valid = true;
    }

    public void contains(String value) throws ParseException {
        double d;
        boolean contains;
        String sign;

        sign = null;
        if (value.startsWith(">") || value.startsWith("<")) {
            sign = value.substring(0, 1);
            value = value.substring(1);
        }

        try {
            d = Double.parseDouble(value);
            /*
             * If the user specifies a "<" in front of the result, we want to
             * try to match the upper bounds.
             */
            if ("<".equals(sign) && d == max)
                contains = true;
            else
                contains = d >= min && d < max;
        } catch (Exception e) {
            contains = false;
        }
        
        if(!contains || !valid)
            throw new ParseException(Messages.get().illegalNumericValueException(String.valueOf(min), String.valueOf(max)));
    }
    
    public boolean intersects(ResultRange value) {
        ResultRangeNumeric r;

        if (value instanceof ResultRangeNumeric && valid) {
            r = (ResultRangeNumeric)value;
            return (r.getMin() >= min && r.getMin() < max) ||
                   (r.getMax() > min && r.getMax() <= max) ||
                   (r.getMax() >= max && r.getMin() <= min);
        }
        return false;
    }

    public String toString() {
        if (valid)
            return min + "," + max;
        return "";
    }

    protected double getMin() {
        if (valid)
            return min;
        return 0;
    }

    protected double getMax() {
        if (valid)
            return max;
        return 0;
    }
}