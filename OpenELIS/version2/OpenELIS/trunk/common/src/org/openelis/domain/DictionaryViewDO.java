package org.openelis.domain;

import org.openelis.utilcommon.DataBaseUtil;

public class DictionaryViewDO extends DictionaryDO {

    private static final long serialVersionUID = 1L;
    protected String relatedEntryName;
    
    public DictionaryViewDO(){
        
    }
    
    public DictionaryViewDO(Integer id,Integer sortOrder, Integer categoryId, Integer relatedEntryId, String systemName,
                            String isActive, String localAbbrev, String entry, String relatedEntryName){
        super(id,sortOrder,categoryId, relatedEntryId, systemName, isActive, localAbbrev, entry);
        setRelatedEntryName(relatedEntryName);
        
    }
    
    public String getRelatedEntryName() {
        return relatedEntryName;
    }
    
    public void setRelatedEntryName(String relatedEntryName) {
        this.relatedEntryName = DataBaseUtil.trim(relatedEntryName);
    }

}
