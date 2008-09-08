package org.openelis.metamap;

import org.openelis.gwt.common.MetaMap;
import org.openelis.meta.ShippingItemMeta;
import org.openelis.meta.ShippingMeta;
import org.openelis.meta.ShippingTrackingMeta;

public class ShippingMetaMap extends ShippingMeta implements MetaMap{

    private String parentPath = "";
    public ShippingMetaMap() {
        super("ship.");
        
        ORDER_META = new OrderMetaMap("ordr.");
        
        TRACKING_META = new ShippingTrackingMeta("shippingTracking.");
            
        SHIPPING_ITEM_META = new ShippingItemMeta("shippingItem.");
    }
    
    public OrderMetaMap ORDER_META;
    
    public ShippingTrackingMeta TRACKING_META;
        
    public ShippingItemMeta SHIPPING_ITEM_META;
    
    public OrderMetaMap getOrderMeta(){
        return ORDER_META;
    }
    
    public ShippingTrackingMeta getTrackingMeta(){
        return TRACKING_META;
    }
    
    public ShippingItemMeta getShippingItemMeta(){
        return SHIPPING_ITEM_META;
    }
    
    public String buildFrom(String where) {
        // TODO Auto-generated method stub
        return null;
    }

    public boolean hasColumn(String columnName) {
        // TODO Auto-generated method stub
        return false;
    }

}
