package org.openelis.domain;

import java.io.Serializable;

public class StorageUnitAutoDO implements Serializable{

    private static final long serialVersionUID = 1L;
    
    protected Integer id;
    protected String description;
    protected String category;
    
    public StorageUnitAutoDO(Integer id, String description, String category){
        this.id = id;
        this.description = description;
        this.category = category;        
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

}
