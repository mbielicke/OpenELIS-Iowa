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

/**
 * This class is used to manage a titer range. The range is specified using a
 * pair of integers separated using a colon (:) such as 1:256. Both the lower
 * and upper limit are inclusive.
 */
public class ResultRangeTiter implements ResultRange {
    protected int min, max;

    public void setRange(String range) throws ParseException {
        String st[];

        if (range != null) {
            st = range.split(":");
            if (st.length != 2)
                throw new ParseException("illegalTiterFormatException");

            try {
                min = Integer.parseInt(st[0]);
                max = Integer.parseInt(st[1]);
                if (min <= 0 || max <= 0)
                    throw new ParseException("illegalTiterFormatException");
                if (min > max)
                    throw new ParseException("illegalTiterRangeException");
            } catch (NumberFormatException ex) {
                throw new ParseException("illegalTiterFormatException");
            }
        }
    }

    public boolean intersects(ResultRange value) {
        ResultRangeTiter r;

        if (value instanceof ResultRangeTiter) {
            r = (ResultRangeTiter)value;
            return (r.getMin() >= min && r.getMin() <= max) ||
                   (r.getMax() >= min && r.getMax() <= max) ||
                   (r.getMax() >= max && r.getMin() <= min);
        }
        return false;
    }

    public boolean contains(String value) {
        int d;
        boolean contains;

        try {
            d = Integer.parseInt(value);
            contains = d >= min && d <= max;
        } catch (Exception e) {
            contains = false;
        }
        return contains;
    }

    public String toString() {
        return min + ":" + max;
    }

    protected int getMin() {
        return min;
    }

    protected int getMax() {
        return max;
    }
}