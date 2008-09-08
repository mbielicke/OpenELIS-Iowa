package org.openelis.metamap;

import org.openelis.gwt.common.MetaMap;
import org.openelis.meta.OrderMeta;

public class FillOrderMetaMap extends OrderMeta implements MetaMap{
    private String parentPath = "";
    public FillOrderMetaMap() {
        super("ordr.");
        ORDER_ITEM_META = new OrderItemMetaMap("order_item.");
        ORDER_ORGANIZATION_META = new OrderOrganizationMetaMap("organization.");
    }
    
    public OrderItemMetaMap ORDER_ITEM_META;

    public OrderOrganizationMetaMap ORDER_ORGANIZATION_META;
    
    public OrderItemMetaMap getOrderItem(){
        return ORDER_ITEM_META;
    }
    
    public OrderOrganizationMetaMap getOrderOrganization(){
        return ORDER_ORGANIZATION_META;
    }
    
    public boolean hasColumn(String name){
        if(name.startsWith(parentPath+"order_item."))
            return ORDER_ITEM_META.hasColumn(name);
        if(name.startsWith(parentPath+"organization."))
            return ORDER_ORGANIZATION_META.hasColumn(name);
        return super.hasColumn(name);
    }
    
    public String buildFrom(String name){
        String from = "Order ordr ";
        if(name.indexOf("order_item") > -1 || name.indexOf("store.") > -1 || name.indexOf("inventoryTrans.") > -1)
            from += ", IN (ordr.orderItem) order_item";
        if(name.indexOf("organization.") > -1)
            from += ", IN (ordr.organization) organization";
        
        return from;
    }
}
