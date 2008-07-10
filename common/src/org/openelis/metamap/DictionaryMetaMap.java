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
