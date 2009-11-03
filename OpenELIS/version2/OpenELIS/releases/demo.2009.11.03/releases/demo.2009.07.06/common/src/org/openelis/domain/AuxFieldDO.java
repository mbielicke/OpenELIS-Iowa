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
import java.util.List;

import org.openelis.utilcommon.DataBaseUtil;

public class AuxFieldDO implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    protected Integer id;  
    protected Integer auxFieldGroupId; 
    protected Integer sortOrder;             
    protected Integer analyteId;    
    protected String analyteName;
    protected String description;                         
    protected Integer methodId;     
    protected String methodName;
    protected Integer unitOfMeasureId;             
    protected String isRequired;             
    protected String isActive;             
    protected String isReportable;             
    protected Integer scriptletId;
    protected String scriptletName;
    private boolean delete = false;
    private List<AuxFieldValueDO> auxFieldValues;
    
    public AuxFieldDO() {
        
    }
    
    public AuxFieldDO( Integer id, Integer sortOrder,
                       Integer analyteId,String analyteName,String description,
                       Integer auxFieldGroupId,Integer methodId, String methodName,
                       Integer unitOfMeasureId,String isRequired,
                       String isActive,String isReportable,
                       Integer scriptletId,String scriptletName) {        
        setId(id);
        setSortOrder(sortOrder);
        setAnalyteId(analyteId);
        setAnalyteName(analyteName);
        setDescription(description);
        setAuxFieldGroupId(auxFieldGroupId); 
        setMethodId(methodId);
        setMethodName(methodName);
        setUnitOfMeasureId(unitOfMeasureId);
        setIsRequired(isRequired);
        setIsActive(isActive);
        setIsReportable(isReportable);
        setScriptletId(scriptletId);   
        setScriptletName(scriptletName);
    }
    
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public Integer getSortOrder() {
        return sortOrder;
    }
    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }
    public Integer getAnalyteId() {
        return analyteId;
    }
    public void setAnalyteId(Integer analyteId) {
        this.analyteId = analyteId;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public Integer getMethodId() {
        return methodId;
    }
    public void setMethodId(Integer methodId) {
        this.methodId = methodId;
    }
    public Integer getUnitOfMeasureId() {
        return unitOfMeasureId;
    }
    public void setUnitOfMeasureId(Integer unitOfMeasureId) {
        this.unitOfMeasureId = unitOfMeasureId;
    }
    public String getIsRequired() {
        return isRequired;
    }
    public void setIsRequired(String isRequired) {
        this.isRequired = isRequired;
    }
    public String getIsActive() {
        return isActive;
    }
    public void setIsActive(String isActive) {
        this.isActive = isActive;
    }
    public String getIsReportable() {
        return isReportable;
    }
    public void setIsReportable(String isReportable) {
        this.isReportable = isReportable;
    }
    public Integer getScriptletId() {
        return scriptletId;
    }
    public void setScriptletId(Integer scriptletId) {
        this.scriptletId = scriptletId;
    }

    public boolean getDelete() {
        return delete;
    }

    public void setDelete(boolean delete) {
        this.delete = delete;
    }

    public Integer getAuxFieldGroupId() {
        return auxFieldGroupId;
    }

    public void setAuxFieldGroupId(Integer auxFieldGroupId) {
        this.auxFieldGroupId = auxFieldGroupId;
    }

    public String getAnalyteName() {
        return analyteName;
    }

    public void setAnalyteName(String analyteName) {
        this.analyteName = DataBaseUtil.trim(analyteName);
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = DataBaseUtil.trim(methodName);
    }

    public List<AuxFieldValueDO> getAuxFieldValues() {
        return auxFieldValues;
    }

    public void setAuxFieldValues(List<AuxFieldValueDO> auxFieldValues) {
        this.auxFieldValues = auxFieldValues;
    }

    public String getScriptletName() {
        return scriptletName;
    }

    public void setScriptletName(String scriptletName) {
        this.scriptletName = DataBaseUtil.trim(scriptletName);
    } 

}
