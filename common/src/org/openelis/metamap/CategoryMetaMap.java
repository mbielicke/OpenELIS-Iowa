/**
* The contents of this file are subject to the Mozilla Public License
* Version 1.1 (the "License"); you may not use this file except in
* compliance with the License. You may obtain a copy of the License at
* http://www.mozilla.org/MPL/
* 
* Software distributed under the License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
* License for the specific language governing rights and limitations under
* the License.
* 
* The Original Code is OpenELIS code.
* 
* Copyright (C) The University of Iowa.  All Rights Reserved.
*/
package org.openelis.metamap;

import org.openelis.gwt.common.MetaMap;
import org.openelis.meta.CategoryMeta;

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
