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
package org.openelis.domain;

import org.openelis.gwt.common.DataBaseUtil;

/**
 * The class extends test result DO and carries the dictionary entry
 * text,"dictionary". The additional field is for read/display only and does not
 * get committed to the database. The field is for the purpose of carrying the
 * entry text that belongs to the id that's stored as the value for a test
 * result that has its type set as dictionary, because in the case of the values
 * of this type the text entered by the users on the table on the screen is not
 * stored in the database, but the id of the dictionary record that contains the
 * text is. Note: isChanged will not reflect any changes to read/display fields.
 */

public class TestResultViewDO extends TestResultDO {

    private static final long serialVersionUID = 1L;

    protected String          dictionary;

    public TestResultViewDO() {
    }

    public TestResultViewDO(Integer id, Integer testId, Integer resultGroup, Integer sortOrder,
                            Integer unitOfMeasureId, Integer typeId, String value,
                            Integer significantDigits, Integer roundingMethodId, Integer flagsId,
                            String dictionary) {
        super(id, testId, resultGroup, sortOrder, unitOfMeasureId, typeId, value,
              significantDigits, roundingMethodId, flagsId);
        setDictionary(dictionary);
    }

    public String getDictionary() {
        return dictionary;
    }

    public void setDictionary(String dictionary) {
        this.dictionary = DataBaseUtil.trim(dictionary);
    }
}
