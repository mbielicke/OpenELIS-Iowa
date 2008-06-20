package org.openelis.newmeta;

import org.openelis.gwt.common.MetaMap;

public class OrderMetaMap extends OrderMeta implements MetaMap{

    public OrderMetaMap() {
        super("ordr.");
    }
    
    public OrderItemMetaMap ORDER_ITEM_META = new OrderItemMetaMap("order_item.");

    public OrderOrganizationMetaMap ORDER_ORGANIZATION_META = new OrderOrganizationMetaMap("organization.");
    
    public OrderOrganizationMetaMap ORDER_REPORT_TO_META = new OrderOrganizationMetaMap("reportTo.");
    
    public OrderOrganizationMetaMap ORDER_BILL_TO_META = new OrderOrganizationMetaMap("billTo.");
    
    public NoteMeta ORDER_SHIPPING_NOTE_META = new NoteMeta("shippingNote.");
    
    public NoteMeta ORDER_CUSTOMER_NOTE_META = new NoteMeta("customerNote.");
        
    public  DictionaryMeta ORDER_ITEM_STORE_META = new DictionaryMeta("store."); 
    
    public InventoryTransactionMetaMap ORDER_INV_TRANS_META = new InventoryTransactionMetaMap("inventoryTrans.");
    
    public OrderItemMetaMap getOrderItem(){
        return ORDER_ITEM_META;
    }
    
    public OrderOrganizationMetaMap getOrderOrganization(){
        return ORDER_ORGANIZATION_META;
    }
    
    public OrderOrganizationMetaMap getReportToOrganization(){
        return ORDER_REPORT_TO_META;
    }
    
    public OrderOrganizationMetaMap getBillToOrganization(){
        return ORDER_BILL_TO_META;
    }
    
    public NoteMeta getShippingNote(){
        return ORDER_SHIPPING_NOTE_META;
    }
    
    public NoteMeta getCustomerNote(){
        return ORDER_CUSTOMER_NOTE_META;
    }
    
    public DictionaryMeta getStore(){
        return ORDER_ITEM_STORE_META;
    }
    
    public InventoryTransactionMetaMap getInventoryTransaction(){
        return ORDER_INV_TRANS_META;
    }
    
    public boolean hasColumn(String name){
        if(name.startsWith("order_item."))
            return ORDER_ITEM_META.hasColumn(name);
        if(name.startsWith("organization."))
            return ORDER_ORGANIZATION_META.hasColumn(name);
        if(name.startsWith("reportTo."))
            return ORDER_REPORT_TO_META.hasColumn(name);
        if(name.startsWith("billTo."))
            return ORDER_BILL_TO_META.hasColumn(name);
        if(name.startsWith("shippingNote."))
            return ORDER_SHIPPING_NOTE_META.hasColumn(name);
        if(name.startsWith("customerNote."))
            return ORDER_CUSTOMER_NOTE_META.hasColumn(name);
        if(name.startsWith("store."))
            return ORDER_ITEM_STORE_META.hasColumn(name);
        if(name.startsWith("inventoryTrans."))
            return ORDER_INV_TRANS_META.hasColumn(name);
        return super.hasColumn(name);
    }
    
    public String buildFrom(String name){
        String from = "Order ordr ";
        if(name.indexOf("order_item") > -1 || name.indexOf("store.") > -1 || name.indexOf("inventoryTrans.") > -1)
            from += ", IN (ordr.orderItem) order_item";
        if(name.indexOf("organization.") > -1)
            from += ", IN (ordr.organization) organization";
        if(name.indexOf("reportTo.") > -1)
            from += ", IN (ordr.reportTo) reportTo";
        if(name.indexOf("billTo.") > -1)
            from += ", IN (ordr.billTo) billTo";
        if(name.indexOf("shippingNote.") > -1)
            from += ", IN(ordr.note) shippingNote";
        if(name.indexOf("customerNote.") > -1)
            from += ", IN(ordr.note) customerNote";
        if(name.indexOf("store.") > -1)
            from += ", Dictionary store";
        if(name.indexOf("inventoryTrans.") > -1)
            from += ", InventoryTransaction inventoryTrans";
        
        return from;
    }

}
