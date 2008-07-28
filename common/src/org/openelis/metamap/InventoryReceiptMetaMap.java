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
import org.openelis.meta.InventoryItemMeta;
import org.openelis.meta.InventoryReceiptMeta;

public class InventoryReceiptMetaMap extends InventoryReceiptMeta implements MetaMap{

    public InventoryReceiptMetaMap() {
        super("receipt.");
    }
    
    public TransReceiptOrderMetaMap TRANS_RECEIPT_ORDER_META = new TransReceiptOrderMetaMap("orderTrans.");
    public TransReceiptLocationMetaMap TRANS_RECEIPT_LOCATION_META = new TransReceiptLocationMetaMap("locTrans.");
    public InventoryItemMeta INVENTORY_ITEM_META = new InventoryItemMeta("ii.");
    public OrderItemMetaMap ORDER_ITEM_META = new OrderItemMetaMap("oi.");
    public DictionaryMeta DICTIONARY_STORE_META = new DictionaryMeta("dictStore.");
    public DictionaryMeta DICTIONARY_PURCHASED_UNITS_META = new DictionaryMeta("dictPurch.");
    public OrderOrganizationMetaMap ORGANIZATION_META = new OrderOrganizationMetaMap("orgz.");

    public TransReceiptOrderMetaMap getTransReceiptOrder(){
        return TRANS_RECEIPT_ORDER_META;
    }
    
    public OrderItemMetaMap getOrderItem(){
        return ORDER_ITEM_META;
    }
    
    public DictionaryMeta getPurchasedUnitsDict(){
        return DICTIONARY_PURCHASED_UNITS_META;
    }
    
    public DictionaryMeta getStoreDict(){
        return DICTIONARY_STORE_META;
    }
    
    public TransReceiptLocationMetaMap getTransReceiptLocation(){
        return TRANS_RECEIPT_LOCATION_META;
    }
    
    public OrderOrganizationMetaMap getOrganization(){
        return ORGANIZATION_META;
    }
    
    public InventoryItemMeta getInventoryitem(){
        return INVENTORY_ITEM_META;
    }
    
    public static InventoryReceiptMetaMap getInstance() {
        return new InventoryReceiptMetaMap();
    }
    
    public boolean hasColumn(String name){
        if(name.startsWith("orderTrans."))
            return TRANS_RECEIPT_ORDER_META.hasColumn(name);
        if(name.startsWith("oi."))
            return ORDER_ITEM_META.hasColumn(name);
        if(name.startsWith("dictStore."))
            return DICTIONARY_STORE_META.hasColumn(name);
        if(name.startsWith("dictPurch."))
            return DICTIONARY_PURCHASED_UNITS_META.hasColumn(name);
        if(name.startsWith("locTrans."))
            return TRANS_RECEIPT_LOCATION_META.hasColumn(name);
        if(name.startsWith("orgz."))
            return ORGANIZATION_META.hasColumn(name);
        if(name.startsWith("ii."))
            return INVENTORY_ITEM_META.hasColumn(name);
        return super.hasColumn(name);
    }
    
    /*
    inventory_receipt ir
    left outer join inventory_transaction trans on ir.id=trans.from_receipt_id
    left outer join order_item oi on trans.to_order_id=oi.id 
    inner join organization orgz on ir.organization_id=orgz.id 
*/
    
    public String buildFrom(String name){
        //we always want to bring back the whole DO so the from wont change
        String from = "InventoryReceipt receipt ";
        //if(name.indexOf("oi.") > -1)
        from += " LEFT JOIN receipt.transReceiptOrders orderTrans ";
        from += " LEFT JOIN receipt.transReceiptLocations locTrans ";
        from += " LEFT JOIN orderTrans.orderItem oi ";
        //from += ", IN (trans.fromReceipt) receipt ";
        //if(name.indexOf("ii.") > -1)
        
        //if(name.indexOf("orgz.") > -1)
        //from += ", IN (receipt.organization) orgz ";
        //from += ", IN (trans.toOrder) oi ";
        //from += ", IN (locTrans.toLocation) loc ";
        from += ", InventoryItem ii, Organization orgz ";
        from += ", Dictionary dictStore, Dictionary dictPurch ";
        
        
        return from;
    }
}
