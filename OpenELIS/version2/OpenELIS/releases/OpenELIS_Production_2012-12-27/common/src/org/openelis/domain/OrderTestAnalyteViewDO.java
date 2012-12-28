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
 * The class extends order test analyte DO and adds analyte name etc. The
 * additional fields are for read/display only and do not get committed to the
 * database. Note: isChanged will reflect any changes to read/display fields.
 */

public class OrderTestAnalyteViewDO extends OrderTestAnalyteDO {

    private static final long serialVersionUID = 1L;
    
    protected Integer testAnalyteSortOrder, testAnalyteTypeId;
    
    protected String analyteName, testAnalyteIsReportable, testAnalyteIsPresent;
    
    public OrderTestAnalyteViewDO() {
    }
    
    public OrderTestAnalyteViewDO(Integer id, Integer orderTestId, Integer analyteId,
                                  String analyteName, Integer testAnalyteSortOrder,
                                  Integer testAnalyteTypeId, String testAnalyteIsReportable,
                                  String testAnalyteIsPresent) {
        super(id, orderTestId, analyteId);
        setAnalyteName(analyteName);
        setTestAnalyteSortOrder(testAnalyteSortOrder);
        setTestAnalyteTypeId(testAnalyteTypeId);
        setTestAnalyteIsReportable(testAnalyteIsReportable);
        setTestAnalyteIsPresent(testAnalyteIsPresent);
    }
    
    public Integer getTestAnalyteSortOrder() {
        return testAnalyteSortOrder;
    }

    public void setTestAnalyteSortOrder(Integer testAnalyteSortOrder) {
        this.testAnalyteSortOrder = testAnalyteSortOrder;
    }

    public Integer getTestAnalyteTypeId() {
        return testAnalyteTypeId;
    }

    public void setTestAnalyteTypeId(Integer testAnalyteTypeId) {
        this.testAnalyteTypeId = testAnalyteTypeId;
    }
    
    public String getAnalyteName() {
        return analyteName;
    }

    public void setAnalyteName(String analyteName) {
        this.analyteName = DataBaseUtil.trim(analyteName);
    }

    public String getTestAnalyteIsReportable() {
        return testAnalyteIsReportable;
    }

    public void setTestAnalyteIsReportable(String testAnalyteIsReportable) {
        this.testAnalyteIsReportable = DataBaseUtil.trim(testAnalyteIsReportable);
    }

    public String getTestAnalyteIsPresent() {
        return testAnalyteIsPresent;
    }

    public void setTestAnalyteIsPresent(String testAnalyteIsPresent) {
        this.testAnalyteIsPresent = DataBaseUtil.trim(testAnalyteIsPresent);
    }
}