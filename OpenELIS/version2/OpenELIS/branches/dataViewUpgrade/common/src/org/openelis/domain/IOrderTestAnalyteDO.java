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
package org.openelis.domain;

import org.openelis.ui.common.DataBaseUtil;

/**
 * Class represents the fields in database table iorder_test_analyte.
 */

public class IOrderTestAnalyteDO extends DataObject {

    private static final long serialVersionUID = 1L;

    protected Integer         id, iorderTestId, analyteId;
    
    public IOrderTestAnalyteDO() {
    }

    public IOrderTestAnalyteDO(Integer id, Integer orderTestId, Integer analyteId) {
        setId(id);
        setIorderTestId(orderTestId);
        setAnalyteId(analyteId);
        _changed = false;
    }
    
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        /*
         * The named query can't set null as a value for this column. The practice
         * is to set the field to 0 in the named query so it can be set to null.
         */ 
        this.id = DataBaseUtil.isSame(id, 0) ? null : id;
        _changed = true;
    }

    public Integer getIorderTestId() {
        return iorderTestId;        
    }

    public void setIorderTestId(Integer iorderTestId) {
        /*
         * The named query can't set null as a value for this column. The practice
         * is to set the field to 0 in the named query so it can be set to null.
         */ 
        this.iorderTestId = DataBaseUtil.isSame(iorderTestId, 0) ? null : iorderTestId;
        _changed = true;
    }

    public Integer getAnalyteId() {
        return analyteId;
    }

    public void setAnalyteId(Integer analyteId) {
        this.analyteId = analyteId;
        _changed = true;
    }
}