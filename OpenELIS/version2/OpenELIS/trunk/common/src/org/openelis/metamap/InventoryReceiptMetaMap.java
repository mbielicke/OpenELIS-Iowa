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
