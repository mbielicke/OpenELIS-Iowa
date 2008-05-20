package org.openelis.domain;

import java.io.Serializable;

import org.openelis.util.DataBaseUtil;


public class CategoryDO implements Serializable {

    private static final long serialVersionUID = 1L;
    
    protected Integer id;                 
    protected String systemName;             
    protected String name;             
    protected String description;             
    protected Integer section;
    
    public CategoryDO(){
        
    } 

    public CategoryDO(Integer id,String systemName,String name,String description,Integer section){
        setId(id);
        setSystemName(systemName);
        setName(name);
        setDescription(description);
        setSection(section);
    } 
    
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = DataBaseUtil.trim(description);
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

    public Integer getSection() {
        return section;
    }

    public void setSection(Integer section) {
        this.section = section;
    }

    public String getSystemName() {
        return systemName;
    }

    public void setSystemName(String systemName) {
        this.systemName = DataBaseUtil.trim(systemName);
    } 

}
