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

import java.io.Serializable;


public class TestAnalyteDO implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    protected Integer id;             

    protected Integer testId;   
    
    protected Integer analyteGroup;

    protected Integer resultGroup;             

    protected Integer sortOrder;             

    protected Integer typeId;             

    protected Integer analyteId; 
    
    protected String analyteName;

    protected String isReportable;             

    protected Integer scriptletId;
    
    private Boolean delete = false;
    
    private Boolean grouped = false;
    
    public TestAnalyteDO() {
        
    }
    
    public TestAnalyteDO(Integer id,Integer testId,Integer analyteGroup,
                         Integer resultGroup,Integer sortOrder,Integer typeId,
                         Integer analyteId,String analyteName, String isReportable,
                         Integer scriptletId) {
              
        this.id = id;
        this.testId = testId;
        this.analyteGroup = analyteGroup;
        this.resultGroup = resultGroup;
        this.sortOrder = sortOrder;
        this.typeId = typeId;
        this.analyteId = analyteId;
        this.isReportable = isReportable;
        this.scriptletId = scriptletId;        
        this.analyteName = analyteName;
    }

    public Integer getAnalyteGroup() {
        return analyteGroup;
    }

    public void setAnalyteGroup(Integer analyteGroup) {
        this.analyteGroup = analyteGroup;
    }

    public Integer getAnalyteId() {
        return analyteId;
    }

    public void setAnalyteId(Integer analyteId) {
        this.analyteId = analyteId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getIsReportable() {
        return isReportable;
    }

    public void setIsReportable(String isReportable) {
        this.isReportable = isReportable;
    }

    public Integer getResultGroup() {
        return resultGroup;
    }

    public void setResultGroup(Integer resultGroup) {
        this.resultGroup = resultGroup;
    }

    public Integer getScriptletId() {
        return scriptletId;
    }

    public void setScriptletId(Integer scriptletId) {
        this.scriptletId = scriptletId;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    public Integer getTestId() {
        return testId;
    }

    public void setTestId(Integer testId) {
        this.testId = testId;
    }

    public Integer getTypeId() {
        return typeId;
    }

    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
    }

    public Boolean getDelete() {
        return delete;
    }

    public void setDelete(Boolean delete) {
        this.delete = delete;
    }

    public Boolean getGrouped() {
        return grouped;
    }

    public void setGrouped(Boolean grouped) {
        this.grouped = grouped;
    }

    public String getAnalyteName() {
        return analyteName;
    }

    public void setAnalyteName(String analyteName) {
        this.analyteName = analyteName;
    }

}
