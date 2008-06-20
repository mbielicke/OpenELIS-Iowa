package org.openelis.newmeta;

import org.openelis.gwt.common.MetaMap;

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
