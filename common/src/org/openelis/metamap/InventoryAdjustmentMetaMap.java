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
import org.openelis.meta.InventoryAdjustmentMeta;

public class InventoryAdjustmentMetaMap extends InventoryAdjustmentMeta implements MetaMap{

    public InventoryAdjustmentMetaMap() {
        super("adj.");
    }

    public TransAdjustmentLocationMetaMap TRANS_ADJUSTMENT_LOCATION_META = new TransAdjustmentLocationMetaMap("transAdj.");
    public  DictionaryMeta DICTIONARY_META = new DictionaryMeta("store."); 
    
    public TransAdjustmentLocationMetaMap getTransAdjustmentLocation(){
        return TRANS_ADJUSTMENT_LOCATION_META;
    }
    
    public DictionaryMeta getDictionary(){
        return DICTIONARY_META;
    }
    
    public static InventoryAdjustmentMetaMap getInstance() {
        return new InventoryAdjustmentMetaMap();
    }
    
    public boolean hasColumn(String name){
        if(name.startsWith("transAdj."))
            return TRANS_ADJUSTMENT_LOCATION_META.hasColumn(name);
        if(name.startsWith("store."))
            return DICTIONARY_META.hasColumn(name);

        return super.hasColumn(name);
    }
    
    public String buildFrom(String name){
        String from = "InventoryAdjustment adj ";
        
        if(name.indexOf("transAdj.") > -1)
            from += " , IN (adj.transAdjustmentLocation) transAdj ";
        if(name.indexOf("store.") > -1)
            from += ", Dictionary store ";
        
        return from;
    }
}
