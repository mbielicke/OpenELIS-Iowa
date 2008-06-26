package org.openelis.metamap;

import org.openelis.gwt.common.MetaMap;
import org.openelis.meta.InventoryLocationMeta;
import org.openelis.meta.InventoryReceiptMeta;

public class InventoryReceiptMetaMap extends InventoryReceiptMeta implements MetaMap{

    public InventoryReceiptMetaMap() {
        super("inventoryReceipt.");
    }
    
    public InventoryLocationMeta INVENTORY_LOCATION_META = new InventoryLocationMeta("locations.");
    
    public OrderMetaMap ORDER_META = new OrderMetaMap();
    
    public InventoryLocationMeta getInventoryLocation() {
        return INVENTORY_LOCATION_META;
    }
    
    public OrderMetaMap getOrder() {
        return ORDER_META;
    }
    
    public static InventoryReceiptMetaMap getInstance() {
        return new InventoryReceiptMetaMap();
    }
    
    public boolean hasColumn(String name){
       /* if(name.startsWith("o.address."))
            return ADDRESS.hasColumn(name);
        if(name.startsWith("o.parentOrganization."))
            return PARENT_ORGANIZATION.hasColumn(name);
        if(name.startsWith("notes."))
            return NOTE.hasColumn(name);
        if(name.startsWith("contacts."))
            return ORGANIZATION_CONTACT.hasColumn(name);*/
        return super.hasColumn(name);
    }
    
    public String buildFrom(String name){
        String from = "InventoryReceipt inventoryReceipt ";
        /*if(name.indexOf("notes.") > -1)
            from += ", IN (o.note) notes ";
        if(name.indexOf("contacts.") > -1)
            from += ", IN (o.organizationContact) contacts ";*/ 
        return from;
    }
}
