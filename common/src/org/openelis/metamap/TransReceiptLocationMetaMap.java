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
import org.openelis.meta.InventoryReceiptMeta;
import org.openelis.meta.TransReceiptLocationMeta;

public class TransReceiptLocationMetaMap extends TransReceiptLocationMeta implements MetaMap{
    public TransReceiptLocationMetaMap() {
        super();
    }
    
    public TransReceiptLocationMetaMap(String path){
        super(path);
        INVENTORY_RECEIPT_META = new InventoryReceiptMeta(path + "inventoryReceipt.");
        INVENTORY_LOCATION_META = new InventoryLocationMetaMap(path + "inventoryLocation.");
    }
    
    public InventoryReceiptMeta INVENTORY_RECEIPT_META; 
    public InventoryLocationMetaMap INVENTORY_LOCATION_META;
    
    public InventoryReceiptMeta getInventoryReceipt() {
        return INVENTORY_RECEIPT_META;
    }
    
    public InventoryLocationMetaMap getInventoryLocation(){
        return INVENTORY_LOCATION_META;
    }

    public String buildFrom(String where) {
        return "TransReceiptLocation ";
    }
    
    public boolean hasColumn(String name){
        if(name.startsWith(path+"inventoryReceipt."))
            return INVENTORY_RECEIPT_META.hasColumn(name);
        if(name.startsWith(path+"toLocation."))
            return INVENTORY_LOCATION_META.hasColumn(name);
        return super.hasColumn(name);
    }
}
