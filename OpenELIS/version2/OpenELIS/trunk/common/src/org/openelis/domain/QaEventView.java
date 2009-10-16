package org.openelis.domain;

import org.openelis.gwt.common.RPC;

public class QaEventView implements RPC {
    private static final long serialVersionUID = 1L;
    
    protected Integer id;
    protected String name;
    protected String description;
    protected Integer typeId;
    protected String type;
    protected String isBillable;
    
    public QaEventView(){
        
    }
    
    public QaEventView(Integer id, String name, String description,
                       Integer typeId, String type, String isBillable){
        setId(id);
        setName(name);
        setDescription(description);
        setTypeId(typeId);
        setType(type);
        setIsBillable(isBillable);
        
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
    public Integer getTypeId() {
        return typeId;
    }
    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String getIsBillable() {
        return isBillable;
    }
    public void setIsBillable(String isBillable) {
        this.isBillable = isBillable;
    }
    
}
