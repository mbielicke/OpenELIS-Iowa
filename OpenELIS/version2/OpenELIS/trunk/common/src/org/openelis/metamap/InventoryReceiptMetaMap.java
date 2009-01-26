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
import org.openelis.meta.InventoryItemMeta;
import org.openelis.meta.InventoryReceiptMeta;

public class InventoryReceiptMetaMap extends InventoryReceiptMeta implements MetaMap{

    public InventoryReceiptMetaMap() {
        super("receipt.");
    }
    
    //public TransReceiptOrderMetaMap TRANS_RECEIPT_ORDER_META = new TransReceiptOrderMetaMap("orderTrans.");
    public InventoryXUseMetaMap TRANS_LOC_ORDER_META = new InventoryXUseMetaMap("locOrderTrans.");
    public InventoryXPutMetaMap TRANS_RECEIPT_LOCATION_META = new InventoryXPutMetaMap("locTrans.");
    public InventoryItemMeta INVENTORY_ITEM_META = new InventoryItemMeta("ii.");
    public OrderItemMetaMap ORDER_ITEM_META = new OrderItemMetaMap("oi.");
    public DictionaryMeta DICTIONARY_STORE_META = new DictionaryMeta("dictStore.");
    public DictionaryMeta DICTIONARY_DISPENSED_UNITS_META = new DictionaryMeta("dictDis.");
    public OrderOrganizationMetaMap ORGANIZATION_META = new OrderOrganizationMetaMap("orgz.", false);

    //public TransReceiptOrderMetaMap getTransReceiptOrder(){
    //    return TRANS_RECEIPT_ORDER_META;
    //}
    
    public OrderItemMetaMap getOrderItem(){
        return ORDER_ITEM_META;
    }
    
    public DictionaryMeta getDispensedUnitsDict(){
        return DICTIONARY_DISPENSED_UNITS_META;
    }
    
    public DictionaryMeta getStoreDict(){
        return DICTIONARY_STORE_META;
    }
    
    public InventoryXPutMetaMap getTransReceiptLocation(){
        return TRANS_RECEIPT_LOCATION_META;
    }
    
    public OrderOrganizationMetaMap getOrganization(){
        return ORGANIZATION_META;
    }
    
    public InventoryItemMeta getInventoryitem(){
        return INVENTORY_ITEM_META;
    }
    
    public InventoryXUseMetaMap getTransLocationOrder(){
        return TRANS_LOC_ORDER_META;
    }
    
    public static InventoryReceiptMetaMap getInstance() {
        return new InventoryReceiptMetaMap();
    }
    
    public boolean hasColumn(String name){
        //if(name.startsWith("orderTrans."))
        //    return TRANS_RECEIPT_ORDER_META.hasColumn(name);
        if(name.startsWith("oi."))
            return ORDER_ITEM_META.hasColumn(name);
        if(name.startsWith("dictStore."))
            return DICTIONARY_STORE_META.hasColumn(name);
        if(name.startsWith("dictDis."))
            return DICTIONARY_DISPENSED_UNITS_META.hasColumn(name);
        if(name.startsWith("locTrans."))
            return TRANS_RECEIPT_LOCATION_META.hasColumn(name);
        if(name.startsWith("orgz."))
            return ORGANIZATION_META.hasColumn(name);
        if(name.startsWith("ii."))
            return INVENTORY_ITEM_META.hasColumn(name);
        if(name.startsWith("locOrderTrans."))
            return TRANS_LOC_ORDER_META.hasColumn(name);
        
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
        from += " LEFT JOIN receipt.orderItem oi ";
        
        //if(name.indexOf("oi.") > -1)
        //from += " LEFT JOIN receipt.transReceiptOrders orderTrans ";
        from += " LEFT JOIN receipt.transReceiptLocations locTrans ";
        //from += ", IN (trans.fromReceipt) receipt ";
        //if(name.indexOf("ii.") > -1)
        
        
        //from += ", IN (trans.toOrder) oi ";
        //from += ", IN (locTrans.toLocation) loc ";
        from += ", InventoryItem ii , Organization orgz ";
        from += ", Dictionary dictStore, Dictionary dictDis ";
        
        return from;
    }
}
