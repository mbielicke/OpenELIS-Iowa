package org.openelis.domain;

import java.io.Serializable;


public class TestSectionDO implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -7903491961646659739L;
    
    protected Integer id;             

    protected Integer testId;             

    protected Integer sectionId;             
    
    protected Integer flagId;
    
    private Boolean delete = false;   
    
    public TestSectionDO() {
        
    }
    
    public TestSectionDO(Integer id,Integer testId,
                         Integer sectionId,Integer flagId) {
        this.id = id;
        this.sectionId = sectionId;
        this.testId = testId;
        this.flagId = flagId;        
    }

    public Integer getFlagId() {
        return flagId;
    }

    public void setFlagId(Integer flagId) {
        this.flagId = flagId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getSectionId() {
        return sectionId;
    }

    public void setSectionId(Integer sectionId) {
        this.sectionId = sectionId;
    }

    public Integer getTestId() {
        return testId;
    }

    public void setTestId(Integer testId) {
        this.testId = testId;
    } 
    
    public Boolean getDelete() {
        return delete;
    }

    public void setDelete(Boolean delete) {
        this.delete = delete;
    }
}
