package org.openelis.meta;

import org.openelis.gwt.common.MetaMap;

public class CategoryMetaMap extends CategoryMeta implements MetaMap {

    public CategoryMetaMap (){
        super("cat.");
    }
    
    private DictionaryMetaMap DICTIONARY = new DictionaryMetaMap("dict.");  
    
    public String buildFrom(String name) {
        String from = "Category cat ";        
        if(name.indexOf("dict.") > -1)
            from += ", IN (cat.dictionary) dict "; 
        return from;
    }
    
    public boolean hasColumn(String name){
        if(name.startsWith("dict."))
            return DICTIONARY.hasColumn(name);
        return super.hasColumn(name);
    }
    
    public CategoryMetaMap getInstance(){
        return new CategoryMetaMap();
    }
    
    public DictionaryMetaMap getDictionary(){
        return DICTIONARY;
    }

}
