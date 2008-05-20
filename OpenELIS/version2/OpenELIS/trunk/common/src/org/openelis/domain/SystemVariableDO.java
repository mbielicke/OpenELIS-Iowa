package org.openelis.domain;

import java.io.Serializable;

import org.openelis.util.DataBaseUtil;

public class SystemVariableDO implements Serializable {

    private static final long serialVersionUID = 1L;
    
    protected Integer id;
    protected String name;
    protected String value;
    
    public SystemVariableDO(){
        
    }
    
    public SystemVariableDO(Integer id, String name, String value){       
       setId(id);
       setName(name);
       setValue(value);
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
        this.name = DataBaseUtil.trim(name);
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = DataBaseUtil.trim(value);
    }

    
     
}
