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
 * This class is used to manage a titer range. The range is specified using a
 * pair of integers separated using a colon (:) such as 1:256. Both the lower
 * and upper limit are inclusive.
 */
public class ResultRangeTiter implements ResultRange {
    private static final long serialVersionUID = 1L;

    protected boolean         valid            = false;
    protected int             min, max;

    public void setRange(String range) throws ParseException {
        String st[];

        valid = false;
        if (range == null)
            throw new ParseException(Messages.get().illegalTiterFormatException());

        st = range.split(":");
        if (st.length != 2)
            throw new ParseException(Messages.get().illegalTiterFormatException());

        try {
            min = Integer.parseInt(st[0]);
            max = Integer.parseInt(st[1]);
            if (min <= 0 || max <= 0)
                throw new ParseException(Messages.get().illegalTiterFormatException());
            if (min > max)
                throw new ParseException(Messages.get().illegalTiterRangeException());
        } catch (NumberFormatException ex) {
            throw new ParseException(Messages.get().illegalTiterFormatException());
        }
        valid = true;
    }

    public void contains(String value) throws ParseException {
        int d;
        boolean contains;

        if (value.startsWith(">") || value.startsWith("<"))
            value = value.substring(1);

        if (value.startsWith("="))
            value = value.substring(1);

        if (value.startsWith("1:"))
            value = value.substring(2);
        else
            throw new ParseException(Messages.get().illegalTiterFormatException());

        try {
            d = Integer.parseInt(value);
            contains = d >= min && d <= max;
        } catch (Exception e) {
            contains = false;
        }

        if ( !contains || !valid)
            throw new ParseException(Messages.get().illegalTiterFormatException());
    }

    public boolean intersects(ResultRange value) {
        ResultRangeTiter r;

        if (value instanceof ResultRangeTiter && valid) {
            r = (ResultRangeTiter)value;
            return (r.getMin() >= min && r.getMin() < max) ||
                   (r.getMax() > min && r.getMax() <= max) ||
                   (r.getMax() >= max && r.getMin() <= min);
        }
        return false;
    }

    public String toString() {
        if (valid)
            return min + ":" + max;
        return "";
    }

    protected int getMin() {
        if (valid)
            return min;
        return 0;
    }

    protected int getMax() {
        if (valid)
            return max;
        return 0;
    }
}