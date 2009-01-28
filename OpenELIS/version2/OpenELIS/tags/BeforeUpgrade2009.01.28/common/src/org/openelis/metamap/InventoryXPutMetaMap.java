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
import org.openelis.meta.InventoryReceiptMeta;
import org.openelis.meta.InventoryXPutMeta;

public class InventoryXPutMetaMap extends InventoryXPutMeta implements MetaMap{
    public InventoryXPutMetaMap() {
        super();
    }
    
    public InventoryXPutMetaMap(String path){
        super(path);
        INVENTORY_RECEIPT_META = new InventoryReceiptMeta(path + "inventoryReceipt.");
        INVENTORY_LOCATION_META = new InventoryLocationMetaMap(path + "inventoryLocation.");
    }
    
    public InventoryReceiptMeta INVENTORY_RECEIPT_META; 
    public InventoryLocationMetaMap INVENTORY_LOCATION_META;
    
    public InventoryReceiptMeta getInventoryReceipt() {
        return INVENTORY_RECEIPT_META;
    }
    
    public InventoryLocationMetaMap getInventoryLocation(){
        return INVENTORY_LOCATION_META;
    }

    public String buildFrom(String where) {
        return "TransReceiptLocation ";
    }
    
    public boolean hasColumn(String name){
        if(name.startsWith(path+"inventoryReceipt."))
            return INVENTORY_RECEIPT_META.hasColumn(name);
        if(name.startsWith(path+"toLocation."))
            return INVENTORY_LOCATION_META.hasColumn(name);
        return super.hasColumn(name);
    }
}
