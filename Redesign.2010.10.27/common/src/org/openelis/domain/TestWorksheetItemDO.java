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
 * Class represents the fields in database table test_worksheet_item.
 */

public class TestWorksheetItemDO extends DataObject {

    private static final long serialVersionUID = 1L;

    protected Integer         id, testWorksheetId, position, typeId;
    protected String          qcName;

    public TestWorksheetItemDO() {
    }

    public TestWorksheetItemDO(Integer id, Integer testWorksheetId, Integer position,
                               Integer typeId, String qcName) {
        setId(id);
        setTestWorksheetId(testWorksheetId);
        setPosition(position);
        setTypeId(typeId);
        setQcName(qcName);
        _changed = false;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
        _changed = true;
    }

    public Integer getTestWorksheetId() {
        return testWorksheetId;
    }

    public void setTestWorksheetId(Integer testWorksheetId) {        
        this.testWorksheetId = testWorksheetId;
        _changed = true;
        
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
        _changed = true;
    }

    public Integer getTypeId() {
        return typeId;
    }

    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
        _changed = true;
    }

    public String getQcName() {
        return qcName;
    }

    public void setQcName(String qcName) {
        this.qcName = DataBaseUtil.trim(qcName);
        _changed = true;
    }
}
