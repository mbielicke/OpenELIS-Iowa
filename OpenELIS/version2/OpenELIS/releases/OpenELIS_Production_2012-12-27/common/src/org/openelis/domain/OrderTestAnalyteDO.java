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

import org.openelis.gwt.common.DataBaseUtil;

/**
 * Class represents the fields in database table order_test_analyte.
 */

public class OrderTestAnalyteDO extends DataObject {

    private static final long serialVersionUID = 1L;

    protected Integer         id, orderTestId, analyteId;
    
    public OrderTestAnalyteDO() {
    }

    public OrderTestAnalyteDO(Integer id, Integer orderTestId, Integer analyteId) {
        setId(id);
        setOrderTestId(orderTestId);
        setAnalyteId(analyteId);
        _changed = false;
    }
    
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        /*
         * we need to set this field to null like this because the query that 
         * is run to fetch this record may set the id to zero and not null in 
         * order to make the query to get deployed
         */ 
        this.id = DataBaseUtil.isSame(id, 0) ? null : id;
        _changed = true;
    }

    public Integer getOrderTestId() {
        return orderTestId;        
    }

    public void setOrderTestId(Integer orderTestId) {
        /*
         * we need to set this field to null like this because the query that
         * is run to fetch this record may set the orderTestId to zero and not null
         * in order to make the query to get deployed
         */ 
        this.orderTestId = DataBaseUtil.isSame(orderTestId, 0) ? null : orderTestId;
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