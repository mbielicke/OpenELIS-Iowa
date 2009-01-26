/** Exhibit A - UIRF Open-source Based Public Software License.
* 
* The contents of this file are subject to the UIRF Open-source Based
* Public Software License(the "License"); you may not use this file except
* in compliance with the License. You may obtain a copy of the License at
* openelis.uhl.uiowa.edu
* 
* Software distributed under the License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
* License for the specific language governing rights and limitations
* under the License.
* 
* The Original Code is OpenELIS code.
* 
* The Initial Developer of the Original Code is The University of Iowa.
* Portions created by The University of Iowa are Copyright 2006-2008. All
* Rights Reserved.
* 
* Contributor(s): ______________________________________.
* 
* Alternatively, the contents of this file marked
* "Separately-Licensed" may be used under the terms of a UIRF Software
* license ("UIRF Software License"), in which case the provisions of a
* UIRF Software License are applicable instead of those above. 
*/
package org.openelis.metamap;

import org.openelis.gwt.common.MetaMap;
import org.openelis.meta.DictionaryMeta;
import org.openelis.meta.InventoryReceiptMeta;
import org.openelis.meta.NoteMeta;
import org.openelis.meta.OrderMeta;

public class OrderMetaMap extends OrderMeta implements MetaMap{
    private String parentPath = "";
    public OrderMetaMap() {
        super("ordr.");
        ORDER_ITEM_META = new OrderItemMetaMap("order_item.");
        ORDER_ORGANIZATION_META = new OrderOrganizationMetaMap("organization.", true);
        ORDER_REPORT_TO_META = new OrderOrganizationMetaMap("reportTo.", false);
        ORDER_BILL_TO_META = new OrderOrganizationMetaMap("billTo.", false);
        ORDER_SHIPPING_NOTE_META = new NoteMeta("shippingNote.");
        ORDER_CUSTOMER_NOTE_META = new NoteMeta("customerNote.");
        ORDER_ITEM_STORE_META = new DictionaryMeta("store."); 
       // ORDER_INV_TRANS_META = new TransReceiptOrderMetaMap("inventoryTrans.");
        INVENTORY_RECEIPT_META = new InventoryReceiptMeta("invReceipt.");
    }
    
    public OrderMetaMap(String path) {
        super(path);
        
        parentPath=path;
        
        ORDER_ITEM_META = new OrderItemMetaMap(path+"order_item.");
        ORDER_ORGANIZATION_META = new OrderOrganizationMetaMap(path+"organization.", true);
        ORDER_REPORT_TO_META = new OrderOrganizationMetaMap(path+"reportTo.", false);
        ORDER_BILL_TO_META = new OrderOrganizationMetaMap(path+"billTo.", false);
        ORDER_SHIPPING_NOTE_META = new NoteMeta(path+"shippingNote.");
        ORDER_CUSTOMER_NOTE_META = new NoteMeta(path+"customerNote.");
        ORDER_ITEM_STORE_META = new DictionaryMeta(path+"store."); 
        //ORDER_INV_TRANS_META = new TransReceiptOrderMetaMap(path+"inventoryTrans.");
        INVENTORY_RECEIPT_META = new InventoryReceiptMeta("invReceipt.");
    }
    
    public OrderMetaMap(String path, boolean initializeOrderItem) {
        super(path);
        parentPath=path;
        
        if(initializeOrderItem)
            ORDER_ITEM_META = new OrderItemMetaMap(path+"order_item.");
        
        ORDER_ORGANIZATION_META = new OrderOrganizationMetaMap(path+"organization.", true);
        ORDER_REPORT_TO_META = new OrderOrganizationMetaMap(path+"reportTo.", false);
        ORDER_BILL_TO_META = new OrderOrganizationMetaMap(path+"billTo.", false);
        ORDER_SHIPPING_NOTE_META = new NoteMeta(path+"shippingNote.");
        ORDER_CUSTOMER_NOTE_META = new NoteMeta(path+"customerNote.");
        ORDER_ITEM_STORE_META = new DictionaryMeta(path+"store."); 
        //ORDER_INV_TRANS_META = new TransReceiptOrderMetaMap(path+"inventoryTrans.");
        INVENTORY_RECEIPT_META = new InventoryReceiptMeta("invReceipt.");
    }
    
    public OrderItemMetaMap ORDER_ITEM_META;

    public OrderOrganizationMetaMap ORDER_ORGANIZATION_META;
    
    public OrderOrganizationMetaMap ORDER_REPORT_TO_META;
    
    public OrderOrganizationMetaMap ORDER_BILL_TO_META;
    
    public NoteMeta ORDER_SHIPPING_NOTE_META;
    
    public NoteMeta ORDER_CUSTOMER_NOTE_META;
        
    public  DictionaryMeta ORDER_ITEM_STORE_META; 
    
    public InventoryReceiptMeta INVENTORY_RECEIPT_META;
    
    //public TransReceiptOrderMetaMap ORDER_INV_TRANS_META;
    
    public OrderItemMetaMap getOrderItem(){
        return ORDER_ITEM_META;
    }
    
    public InventoryReceiptMeta getInventoryReceipt(){
        return INVENTORY_RECEIPT_META;
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
    
    //public TransReceiptOrderMetaMap getInventoryTransaction(){
    //    return ORDER_INV_TRANS_META;
    //}
    
    public boolean hasColumn(String name){
        if(name.startsWith(parentPath+"order_item."))
            return ORDER_ITEM_META.hasColumn(name);
        if(name.startsWith(parentPath+"organization."))
            return ORDER_ORGANIZATION_META.hasColumn(name);
        if(name.startsWith(parentPath+"reportTo."))
            return ORDER_REPORT_TO_META.hasColumn(name);
        if(name.startsWith(parentPath+"billTo."))
            return ORDER_BILL_TO_META.hasColumn(name);
        if(name.startsWith(parentPath+"shippingNote."))
            return ORDER_SHIPPING_NOTE_META.hasColumn(name);
        if(name.startsWith(parentPath+"customerNote."))
            return ORDER_CUSTOMER_NOTE_META.hasColumn(name);
        if(name.startsWith(parentPath+"store."))
            return ORDER_ITEM_STORE_META.hasColumn(name);
        if(name.startsWith(parentPath+"invReceipt."))
            return INVENTORY_RECEIPT_META.hasColumn(name);
        //if(name.startsWith(parentPath+"inventoryTrans."))
         //   return ORDER_INV_TRANS_META.hasColumn(name);
        return super.hasColumn(name);
    }
    
    public String buildFrom(String name){
        String from = "Order ordr ";
        if(name.indexOf("order_item") > -1 || name.indexOf("store.") > -1 || name.indexOf("invReceipt.") > -1)
            from += ", IN (ordr.orderItem) order_item";
        
        
        if(name.indexOf("invReceipt.") > -1)
            from += ", IN (ordr.orderItem) order_item";
        
        
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
        //if(name.indexOf("organization.") > -1)
            from += " LEFT JOIN ordr.organization organization LEFT JOIN organization.address organizationAddress";
        //if(name.indexOf("inventoryTrans.") > -1)
        //    from += ", TransLocationOrder inventoryTrans";
        
        return from;
    }

}
