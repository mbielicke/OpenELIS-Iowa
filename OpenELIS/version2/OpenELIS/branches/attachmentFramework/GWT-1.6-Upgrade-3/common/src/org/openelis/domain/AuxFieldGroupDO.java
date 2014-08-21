package org.openelis.domain;

import java.io.Serializable;
import java.util.Date;

import org.openelis.util.Datetime;


public class AuxFieldGroupDO implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    protected Integer id;             
    protected String name;             
    protected String description;             
    protected String isActive;             
    protected Datetime activeBegin;             
    protected Datetime activeEnd;
    private boolean delete = false;
    
    public AuxFieldGroupDO() {
        
    }
    
    public AuxFieldGroupDO(Integer id,String name,String description,                            
                           String isActive,Date activeBegin,Date activeEnd) {
        this.id = id;
        this.name = name;
        this.description= description;
        this.isActive = isActive;
        setActiveBegin(activeBegin);
        setActiveEnd(activeEnd);
    }
    
    
    public Datetime getActiveBegin() {
        return activeBegin;
    }

    public void setActiveBegin(Date activeBegin) {
        this.activeBegin = new Datetime(Datetime.YEAR,Datetime.DAY,activeBegin);
    }

    public Datetime getActiveEnd() {
        return activeEnd;
    }

    public void setActiveEnd(Date activeEnd) {
        this.activeEnd = new Datetime(Datetime.YEAR,Datetime.DAY,activeEnd);
    }
    
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getIsActive() {
        return isActive;
    }
    public void setIsActive(String isActive) {
        this.isActive = isActive;
    }

    public boolean isDelete() {
        return delete;
    }

    public void setDelete(boolean delete) {
        this.delete = delete;
    } 
}
