
package org.openelis.meta;

/**
  * ShippingItem META Data
  */

import java.util.HashSet;
import org.openelis.gwt.common.Meta;

public class ShippingItemMeta implements Meta {
  	protected String path = "";
	private static final String entityName = "ShippingItem";
	
	private static final String
              ID					="id",
              SHIPPING_ID					="shippingId",
              REFERENCE_TABLE_ID					="referenceTableId",
              REFERENCE_ID					="referenceId";

  	private static final String[] columnNames = {
  	  ID,SHIPPING_ID,REFERENCE_TABLE_ID,REFERENCE_ID};
  	  
	private HashSet<String> columnHashList;
    
    private void init() {
        columnHashList = new HashSet<String>(columnNames.length);
        for(int i = 0; i < columnNames.length; i++){
            columnHashList.add(path+columnNames[i]);
        }
    }
    
    public ShippingItemMeta() {
		init();        
    }
    
    public ShippingItemMeta(String path) {
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

    public String getReferenceTableId() {
        return path + REFERENCE_TABLE_ID;
    } 

    public String getReferenceId() {
        return path + REFERENCE_ID;
    } 

  
}   
