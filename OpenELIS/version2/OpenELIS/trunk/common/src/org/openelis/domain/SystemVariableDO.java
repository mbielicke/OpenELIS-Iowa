package org.openelis.domain;

import java.io.Serializable;

public class SystemVariableDO implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    protected Integer id;
    
    protected String name;
    
    protected String value;
    
    public SystemVariableDO(){
        
    }
    
    public SystemVariableDO(Integer id, String name, String value){       
       this.id = id;
       this.name = name;
       this.value = value;
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

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    
     
}
