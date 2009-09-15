package org.openelis.domain;

import org.openelis.gwt.common.RPC;
import org.openelis.utilcommon.DataBaseUtil;

public class QaEventAutoDO implements RPC{

    private static final long serialVersionUID = 1L;
    
    protected Integer id;
    protected String name;
    protected String description;
    protected Integer typeId;
    protected String isBillable;
    
    public QaEventAutoDO(){
        
    }
    
    public QaEventAutoDO(Integer id, String name, String description, Integer typeId,
                         String isBillable){
        
        setId(id);
        setName(name);
        setDescription(description);
        setTypeId(typeId);
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
        this.name = DataBaseUtil.trim(name);
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = DataBaseUtil.trim(description);
    }
    public Integer getTypeId() {
        return typeId;
    }
    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
    }
    public String getIsBillable() {
        return isBillable;
    }
    public void setIsBillable(String isBillable) {
        this.isBillable = DataBaseUtil.trim(isBillable);
    }
}
