package org.openelis.domain;

import java.io.Serializable;

public class CategoryTableRowDO implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    protected Integer id;
    protected String systemName;
    
    public CategoryTableRowDO(){
        
    }
    
    public CategoryTableRowDO(Integer id,String systemName){
        this.id = id;
        this.systemName = systemName;
    }
    
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public String getSystemName() {
        return systemName;
    }
    public void setSystemName(String systemName) {
        this.systemName = systemName;
    }
    
}
