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
import org.openelis.meta.ShippingItemMeta;
import org.openelis.meta.ShippingMeta;
import org.openelis.meta.ShippingTrackingMeta;

public class ShippingMetaMap extends ShippingMeta implements MetaMap{

    public ShippingMetaMap() {
        super("ship.");
        
        ORGANIZATION_META = new OrganizationMetaMap("orgz.");
        
        TRACKING_META = new ShippingTrackingMeta("shippingTracking.");
            
        SHIPPING_ITEM_META = new ShippingItemMeta("shippingItem.");
    }    

    public OrganizationMetaMap ORGANIZATION_META;
    
    public ShippingTrackingMeta TRACKING_META;
        
    public ShippingItemMeta SHIPPING_ITEM_META;
    
    public OrganizationMetaMap getOrganizationMeta(){
        return ORGANIZATION_META;
    }
    
    public ShippingTrackingMeta getTrackingMeta(){
        return TRACKING_META;
    }
    
    public ShippingItemMeta getShippingItemMeta(){
        return SHIPPING_ITEM_META;
    }
    
    public String buildFrom(String where) {
        String from = "Shipping ship ";
        if(where.indexOf("shippingTracking.") > -1)
            from += ", IN (ship.shippingTracking) shippingTracking ";
        if(where.indexOf("orgz.") > -1)
            from += ", IN (ship.shipTo) orgz ";

        return from;
    }

    public boolean hasColumn(String columnName) {
        if(columnName.startsWith("orgz."))
            return ORGANIZATION_META.hasColumn(columnName);
        if(columnName.startsWith("shippingTracking."))
            return TRACKING_META.hasColumn(columnName);
        if(columnName.startsWith("shippingItem."))
            return SHIPPING_ITEM_META.hasColumn(columnName);
        
        return super.hasColumn(columnName);
    }

}
