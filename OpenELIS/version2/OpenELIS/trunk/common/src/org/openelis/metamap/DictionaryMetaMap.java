package org.openelis.metamap;

import org.openelis.gwt.common.MetaMap;
import org.openelis.meta.DictionaryMeta;

public class DictionaryMetaMap extends DictionaryMeta implements MetaMap {

    public DictionaryMetaMap(){
        super();
    }
    
    public DictionaryMetaMap(String path){
        super(path);        
        RELATED_ENTRY = new DictionaryMeta(path+"relatedEntry.");
    }
    
    private DictionaryMeta RELATED_ENTRY;
    
    public String buildFrom(String where) {        
        return "Dictionary ";
    }
    
    public boolean hasColumn(String name){
        if(name.startsWith(path+"relatedEntry."))
          return RELATED_ENTRY.hasColumn(name); 
      return super.hasColumn(name);
    }
    
    public DictionaryMeta getRelatedEntry(){
        return RELATED_ENTRY;
    }

}
