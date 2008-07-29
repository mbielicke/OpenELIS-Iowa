
package org.openelis.meta;

/**
  * TransAdjustmentLocation META Data
  */

import java.util.HashSet;
import org.openelis.gwt.common.Meta;

public class TransAdjustmentLocationMeta implements Meta {
  	protected String path = "";
	private static final String entityName = "TransAdjustmentLocation";
	
	private static final String
              ID					="id",
              INVENTORY_ADJUSTMENT_ID					="inventoryAdjustmentId",
              INVENTORY_LOCATION_ID					="inventoryLocationId",
              QUANTITY					="quantity",
              PHYSICAL_COUNT					="physicalCount";

  	private static final String[] columnNames = {
  	  ID,INVENTORY_ADJUSTMENT_ID,INVENTORY_LOCATION_ID,QUANTITY,PHYSICAL_COUNT};
  	  
	private HashSet<String> columnHashList;
    
    private void init() {
        columnHashList = new HashSet<String>(columnNames.length);
        for(int i = 0; i < columnNames.length; i++){
            columnHashList.add(path+columnNames[i]);
        }
    }
    
    public TransAdjustmentLocationMeta() {
		init();        
    }
    
    public TransAdjustmentLocationMeta(String path) {
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

    public String getInventoryAdjustmentId() {
        return path + INVENTORY_ADJUSTMENT_ID;
    } 

    public String getInventoryLocationId() {
        return path + INVENTORY_LOCATION_ID;
    } 

    public String getQuantity() {
        return path + QUANTITY;
    } 

    public String getPhysicalCount() {
        return path + PHYSICAL_COUNT;
    } 

  
}   
