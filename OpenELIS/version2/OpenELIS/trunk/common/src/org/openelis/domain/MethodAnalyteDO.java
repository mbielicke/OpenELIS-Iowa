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

import org.openelis.gwt.common.RPC;

public class MethodAnalyteDO implements RPC {

    private static final long serialVersionUID = 1L;

    protected Integer         id;
    protected Integer         methodId;
    protected Integer         analyteGroup;
    protected Integer         resultGroup;
    protected Integer         sortOrder;
    protected String          type;
    protected Integer         analyteId;
    private Boolean           delete           = false;
    private Boolean           grouped          = false;

    public MethodAnalyteDO() {

    }

    public MethodAnalyteDO(Integer id,
                           Integer methodId,
                           Integer analyteGroup,
                           Integer resultGroup,
                           Integer sortOrder,
                           String type,
                           Integer analyteId) {

        this.id = id;
        this.methodId = methodId;
        this.analyteGroup = analyteGroup;
        this.resultGroup = resultGroup;
        this.sortOrder = sortOrder;
        this.type = type;
    }

    public Boolean getDelete() {
        return delete;
    }

    public void setDelete(Boolean delete) {
        this.delete = delete;
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

    public Integer getMethodId() {
        return methodId;
    }

    public void setMethodId(Integer methodId) {
        this.methodId = methodId;
    }

    public Integer getResultGroup() {
        return resultGroup;
    }

    public void setResultGroup(Integer resultGroup) {
        this.resultGroup = resultGroup;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Boolean getGrouped() {
        return grouped;
    }

    public void setGrouped(Boolean grouped) {
        this.grouped = grouped;
    }

}
