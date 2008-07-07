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
* Copyright (C) OpenELIS.  All Rights Reserved.
*/
package org.openelis.metamap;

import org.openelis.gwt.common.MetaMap;
import org.openelis.meta.InventoryReceiptMeta;
import org.openelis.meta.InventoryTransactionMeta;

public class InventoryTransactionMetaMap extends InventoryTransactionMeta implements MetaMap{
    
    public InventoryTransactionMetaMap() {
        super();
    }
    
    public InventoryTransactionMetaMap(String path){
        super(path);
        INVENTORY_RECEIPT_META = new InventoryReceiptMeta(path + "fromReceipt.");
    }
    
    public InventoryReceiptMeta INVENTORY_RECEIPT_META; 
    
    public InventoryReceiptMeta getInventoryReceipt() {
        return INVENTORY_RECEIPT_META;
    }

    public String buildFrom(String where) {
        return "InventoryTransaction ";
    }
    
    public boolean hasColumn(String name){
        if(name.startsWith(path+"fromReceipt."))
            return INVENTORY_RECEIPT_META.hasColumn(name);
        return super.hasColumn(name);
    }
}
