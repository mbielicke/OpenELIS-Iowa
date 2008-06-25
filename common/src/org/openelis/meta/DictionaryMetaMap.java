package org.openelis.meta;

import org.openelis.gwt.common.MetaMap;

public class DictionaryMetaMap extends DictionaryMeta implements MetaMap {

    public DictionaryMetaMap(){
        super();
    }
    
    public DictionaryMetaMap(String path){
        super(path);        
        RELATED_ENTRY = new DictionaryMeta(path+"relentry.");
    }
    
    private DictionaryMeta RELATED_ENTRY;
    
    public String buildFrom(String where) {        
        return "Dictionary ";
    }
    
    public boolean hasColumn(String name){
        if(name.startsWith(path+"relentry."))
          return RELATED_ENTRY.hasColumn(name); 
      return super.hasColumn(name);
    }
    
    public DictionaryMeta getRelatedEntry(){
        return RELATED_ENTRY;
    }

}
