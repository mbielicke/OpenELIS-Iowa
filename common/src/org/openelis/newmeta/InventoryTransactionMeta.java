
package org.openelis.newmeta;

/**
  * InventoryTransaction META Data
  */

import java.util.HashSet;
import org.openelis.gwt.common.NewMeta;

public class InventoryTransactionMeta implements NewMeta {
  	protected String path = "";
	private static final String entityName = "InventoryTransaction";
	
	private static final String
              ID					="id",
              FROM_LOCATION_ID					="fromLocationId",
              FROM_RECEIPT_ID					="fromReceiptId",
              FROM_ADJUSTMENT_ID					="fromAdjustmentId",
              TO_ORDER_ID					="toOrderId",
              TO_LOCATION_ID					="toLocationId",
              TYPE_ID					="typeId",
              QUANTITY					="quantity";

  	private static final String[] columnNames = {
  	  ID,FROM_LOCATION_ID,FROM_RECEIPT_ID,FROM_ADJUSTMENT_ID,TO_ORDER_ID,TO_LOCATION_ID,TYPE_ID,QUANTITY};
  	  
	private HashSet<String> columnHashList;
    
    private void init() {
        columnHashList = new HashSet<String>(columnNames.length);
        for(int i = 0; i < columnNames.length; i++){
            columnHashList.add(path+columnNames[i]);
        }
    }
    
    public InventoryTransactionMeta() {
		init();        
    }
    
    public InventoryTransactionMeta(String path) {
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

    public String getFromLocationId() {
        return path + FROM_LOCATION_ID;
    } 

    public String getFromReceiptId() {
        return path + FROM_RECEIPT_ID;
    } 

    public String getFromAdjustmentId() {
        return path + FROM_ADJUSTMENT_ID;
    } 

    public String getToOrderId() {
        return path + TO_ORDER_ID;
    } 

    public String getToLocationId() {
        return path + TO_LOCATION_ID;
    } 

    public String getTypeId() {
        return path + TYPE_ID;
    } 

    public String getQuantity() {
        return path + QUANTITY;
    } 

  
}   
