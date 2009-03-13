package org.openelis.domain;

import java.io.Serializable;

public class AuxFieldDO implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    protected Integer id;             
    protected Integer sortOrder;             
    protected Integer analyteId;             
    protected String description;             
    protected Integer referenceTableId;             
    protected Integer methodId;             
    protected Integer unitOfMeasureId;             
    protected String isRequired;             
    protected String isActive;             
    protected String isReportable;             
    protected Integer scriptletId;
    private boolean delete = false;
    
    public AuxFieldDO() {
        
    }
    
    public AuxFieldDO( Integer id, Integer sortOrder,
                       Integer analyteId,String description,
                       Integer referenceTableId,Integer methodId,
                       Integer unitOfMeasureId,String isRequired,
                       String isActive,String isReportable,
                       Integer scriptletId) {        
        this.id = id;
        this.sortOrder = sortOrder;
        this.analyteId = analyteId;
        this.description = description;
        this.referenceTableId = referenceTableId;
        this.methodId = methodId;
        this.unitOfMeasureId = unitOfMeasureId;
        this.isRequired = isRequired;
        this.isActive = isActive;
        this.isReportable = isReportable;
        this.scriptletId = scriptletId;       
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
    public Integer getReferenceTableId() {
        return referenceTableId;
    }
    public void setReferenceTableId(Integer referenceTableId) {
        this.referenceTableId = referenceTableId;
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

}
