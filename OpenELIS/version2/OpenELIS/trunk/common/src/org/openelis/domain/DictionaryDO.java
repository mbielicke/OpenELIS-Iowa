package org.openelis.domain;

import java.io.Serializable;

public class DictionaryDO implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    protected Integer id;             

    protected Integer category;             

    protected Integer relatedEntry;             

    protected String systemName;             

    protected String isActive;             

    protected String localAbbrev;             

    protected String entry;
    
    public DictionaryDO(){
        
    }

    public DictionaryDO(Integer id, Integer category, Integer relatedEntry,
          String systemName,String isActive,  String localAbbrev, String entry){
        this.id = id;
        this.relatedEntry = relatedEntry;
        this.systemName = systemName;
        this.isActive = isActive;
        this.localAbbrev = localAbbrev;
        this.entry = entry;
    }
    
    public Integer getCategory() {
        return category;
    }

    public void setCategory(Integer category) {
        this.category = category;
    }

    public String getEntry() {
        return entry;
    }

    public void setEntry(String entry) {
        this.entry = entry;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getIsActive() {
        return isActive;
    }

    public void setIsActive(String isActive) {
        this.isActive = isActive;
    }

    public String getLocalAbbrev() {
        return localAbbrev;
    }

    public void setLocalAbbrev(String localAbbrev) {
        this.localAbbrev = localAbbrev;
    }

    public Integer getRelatedEntry() {
        return relatedEntry;
    }

    public void setRelatedEntry(Integer relatedEntry) {
        this.relatedEntry = relatedEntry;
    }

    public String getSystemName() {
        return systemName;
    }

    public void setSystemName(String systemName) {
        this.systemName = systemName;
    }

}
