
package org.openelis.meta;

/**
  * TransReceiptLocation META Data
  */

import java.util.HashSet;
import org.openelis.gwt.common.Meta;

public class TransReceiptLocationMeta implements Meta {
  	protected String path = "";
	private static final String entityName = "TransReceiptLocation";
	
	private static final String
              ID					                ="id",
              INVENTORY_RECEIPT_ID					="inventoryReceiptId",
              INVENTORY_LOCATION_ID					="inventoryLocationId",
              QUANTITY					            ="quantity";

  	private static final String[] columnNames = {
  	  ID,INVENTORY_RECEIPT_ID,INVENTORY_LOCATION_ID,QUANTITY};
  	  
	private HashSet<String> columnHashList;
    
    private void init() {
        columnHashList = new HashSet<String>(columnNames.length);
        for(int i = 0; i < columnNames.length; i++){
            columnHashList.add(path+columnNames[i]);
        }
    }
    
    public TransReceiptLocationMeta() {
		init();        
    }
    
    public TransReceiptLocationMeta(String path) {
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

    public String getInventoryReceiptId() {
        return path + INVENTORY_RECEIPT_ID;
    } 

    public String getInventoryLocationId() {
        return path + INVENTORY_LOCATION_ID;
    } 

    public String getQuantity() {
        return path + QUANTITY;
    } 

  
}   
