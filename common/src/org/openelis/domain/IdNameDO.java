package org.openelis.domain;

import java.io.Serializable;

import org.openelis.util.DataBaseUtil;

public class IdNameDO implements Serializable{
    
    private static final long serialVersionUID = 1L;
    
    protected Integer id;
    protected String name;
    
    public IdNameDO(Integer id, String name){
        setId(id);
        setName(name);
    }
    
    public IdNameDO(Integer id){
        setId(id);
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

}
