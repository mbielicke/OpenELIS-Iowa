
package org.openelis.meta;

/**
  * ShippingTracking META Data
  */

import java.util.HashSet;
import org.openelis.gwt.common.Meta;

public class ShippingTrackingMeta implements Meta {
  	protected String path = "";
	private static final String entityName = "ShippingTracking";
	
	private static final String
              ID					="id",
              SHIPPING_ID					="shippingId",
              TRACKING_NUMBER					="trackingNumber";

  	private static final String[] columnNames = {
  	  ID,SHIPPING_ID,TRACKING_NUMBER};
  	  
	private HashSet<String> columnHashList;
    
    private void init() {
        columnHashList = new HashSet<String>(columnNames.length);
        for(int i = 0; i < columnNames.length; i++){
            columnHashList.add(path+columnNames[i]);
        }
    }
    
    public ShippingTrackingMeta() {
		init();        
    }
    
    public ShippingTrackingMeta(String path) {
        this.path = path;
		init();        
    }

    public String[] getColumnList() {
        return columnNames;
    }

    public String getEntity() {
        return entityName;
    }

    public boolean hasColumn(String columnName) {
        return columnHashList.contains(columnName);
    }
    
    
    public String getId() {
        return path + ID;
    } 

    public String getShippingId() {
        return path + SHIPPING_ID;
    } 

    public String getTrackingNumber() {
        return path + TRACKING_NUMBER;
    } 

  
}   
