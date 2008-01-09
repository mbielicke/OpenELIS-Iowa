package org.openelis.domain;

import java.io.Serializable;

public class DictionaryEntryTableRowDO implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    protected String relatedEntryString;
    protected DictionaryDO dictionaryDO = new DictionaryDO();
        
    
    public DictionaryEntryTableRowDO(){
        
    }

    public DictionaryEntryTableRowDO(Integer id, Integer category, Integer relatedEntryId,
                         String systemName,String isActive,  String localAbbrev, String entry,
                         String relEntryString){
        dictionaryDO.setId(id);
        dictionaryDO.setRelatedEntry(relatedEntryId);
        dictionaryDO.setSystemName(systemName);
        dictionaryDO.setIsActive(isActive);
        dictionaryDO.setLocalAbbrev(localAbbrev);
        dictionaryDO.setEntry(entry);
        this.relatedEntryString = relEntryString;        
    }
    
    public DictionaryDO getDictionaryDO() {
        return dictionaryDO;
    }

    public void setDictionaryDO(DictionaryDO dictionaryDO) {
        this.dictionaryDO = dictionaryDO;
    }

    public String getRelatedEntryString() {
        return relatedEntryString;
    }

    public void setRelatedEntryString(String relatedEntryString) {
        this.relatedEntryString = relatedEntryString;
    }

    
    
    
}
