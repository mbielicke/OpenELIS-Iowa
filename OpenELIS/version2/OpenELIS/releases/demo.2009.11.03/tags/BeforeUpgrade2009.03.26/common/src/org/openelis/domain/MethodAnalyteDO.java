package org.openelis.domain;

import java.io.Serializable;


public class MethodAnalyteDO implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -2709654963250649961L;
    
    protected Integer id;             

    protected Integer methodId;             

    protected Integer analyteGroup;             

    protected Integer resultGroup;             

    protected Integer sortOrder;             

    protected String type;             

    protected Integer analyteId;
    
    private Boolean delete = false;
    
    private Boolean grouped = false;
    
    public MethodAnalyteDO() {
        
    }
    
    public MethodAnalyteDO(Integer id,Integer methodId, Integer analyteGroup,
                           Integer resultGroup,Integer sortOrder,String type,
                           Integer analyteId) {
        
        this.id= id;
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
