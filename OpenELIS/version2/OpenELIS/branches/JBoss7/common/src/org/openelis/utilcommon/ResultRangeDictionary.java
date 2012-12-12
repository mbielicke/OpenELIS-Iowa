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
import org.openelis.gwt.common.DataBaseUtil;

/**
 * This class is used to manage a dictionary entry having a string representation and
 * an entry id.
 */
public class ResultRangeDictionary implements ResultRange {
    private static final long serialVersionUID = 1L;

    protected Integer id;
    protected String entry;

    public void setId(Integer dictId) throws ParseException {
        id = dictId;
    }

    public Integer getId() {
        return id;
    }
    
    public void setRange(String entry) throws ParseException {
        this.entry = entry;
    }

    public void contains(String value) throws ParseException {
        if(DataBaseUtil.isDifferent(value, entry) && (id != null && DataBaseUtil.isDifferent(value, id.toString())))
            throw new ParseException("illegalDictionaryValueException");
    }
    
    public boolean intersects(ResultRange value) {
        if (value instanceof ResultRangeDictionary && entry != null && id != null)
            return DataBaseUtil.isSame(entry, ((ResultRangeDictionary)value).entry) &&
                   DataBaseUtil.isSame(id, ((ResultRangeDictionary)value).id);

        return false;
    }

    public String toString() {
        if (entry != null)
            return entry;
        return "";
    }
}