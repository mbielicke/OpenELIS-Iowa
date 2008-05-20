package org.openelis.domain;

import java.io.Serializable;

import org.openelis.util.DataBaseUtil;


public class LabelDO implements Serializable {

    private static final long serialVersionUID = 1L;

    protected Integer id;
    protected String name; 
    protected String description;  
    protected Integer printerType; 
    protected Integer scriptlet;
    
    public LabelDO(){
        
    }
    
    public LabelDO( Integer id,String name,String description,Integer printerType,Integer scriptlet){
     setId(id);
     setName(name);
     setDescription(description);
     setPrinterType(printerType);
     setScriptlet(scriptlet);     
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

    public Integer getPrinterType() {
        return printerType;
    }

    public void setPrinterType(Integer printerType) {
        this.printerType = printerType;
    }

    public Integer getScriptlet() {
        return scriptlet;
    }

    public void setScriptlet(Integer scriptlet) {
        this.scriptlet = scriptlet;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = DataBaseUtil.trim(description);
    }    
}
