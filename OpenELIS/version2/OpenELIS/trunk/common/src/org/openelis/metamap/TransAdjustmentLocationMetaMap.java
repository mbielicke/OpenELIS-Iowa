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
import org.openelis.meta.InventoryLocationMeta;
import org.openelis.meta.TransAdjustmentLocationMeta;

public class TransAdjustmentLocationMetaMap extends TransAdjustmentLocationMeta implements MetaMap{
    
    public TransAdjustmentLocationMetaMap(String path) {
        super(path);
    }
    
    public TransAdjustmentLocationMetaMap() {
        super();
    }
    
    public InventoryLocationMetaMap INVENTORY_LOCATION_META = new InventoryLocationMetaMap(path+"inventoryLocation.");
    
    public InventoryLocationMetaMap getInventoryLocation(){
        return INVENTORY_LOCATION_META;
    }
    
    public static TransAdjustmentLocationMetaMap getInstance() {
        return new TransAdjustmentLocationMetaMap();
    }
    
    public boolean hasColumn(String name){
        if(name.startsWith(path+"inventoryLocation."))
            return INVENTORY_LOCATION_META.hasColumn(name);
        return super.hasColumn(name);
    }
  
    public String buildFrom(String name){
        String from = "TransAdjustmentLocationMeta transAdj ";
        
        return from;
    }
}
