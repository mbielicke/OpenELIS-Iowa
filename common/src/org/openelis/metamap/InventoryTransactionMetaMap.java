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
