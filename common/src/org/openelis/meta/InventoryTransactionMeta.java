
package org.openelis.meta;

/**
  * InventoryTransaction META Data
  */

import java.util.HashMap;
import org.openelis.util.Meta;

public class InventoryTransactionMeta implements Meta {
  	private static final String tableName = "inventory_transaction";
	private static final String entityName = "InventoryTransaction";
	private boolean includeInFrom = true;
	
	public static final String
              ID					="inventory_transaction.id",
              FROM_LOCATION_ID					="inventory_transaction.fromLocationId",
              FROM_RECEIPT_ID					="inventory_transaction.fromReceiptId",
              FROM_ADJUSTMENT_ID					="inventory_transaction.fromAdjustmentId",
              TO_ORDER_ID					="inventory_transaction.toOrderId",
              TO_LOCATION_ID					="inventory_transaction.toLocationId",
              TYPE_ID					="inventory_transaction.typeId",
              QUANTITY					="inventory_transaction.quantity";


  	private static final String[] columnNames = {
  	  ID,FROM_LOCATION_ID,FROM_RECEIPT_ID,FROM_ADJUSTMENT_ID,TO_ORDER_ID,TO_LOCATION_ID,TYPE_ID,QUANTITY};
  	  
	private static HashMap<String,String> columnHashList;

	private static final InventoryTransactionMeta inventory_transactionMeta = new InventoryTransactionMeta();
    
    static {
        columnHashList = new HashMap<String,String>(columnNames.length);
        for(int i = 0; i < columnNames.length; i++){
            columnHashList.put(columnNames[i].substring(tableName.length()+1), "");
        }
    }
    
    private InventoryTransactionMeta() {
        
    }
    
    public static InventoryTransactionMeta getInstance() {
        return inventory_transactionMeta;
    }

    public String[] getColumnList() {
        return columnNames;
    }

    public String getEntity() {
        return entityName;
    }

    public String getTable() {
        return tableName;
    }

    public boolean hasColumn(String columnName) {
        if(columnName == null || !columnName.startsWith(tableName))
            return false;
        String column = columnName.substring(tableName.length()+1);
        
        return columnHashList.containsKey(column);
    }

    public boolean includeInFrom() {
        // TODO Auto-generated method stub
        return includeInFrom;
    }
    
    
  public static String getId() {
    return ID;
  } 

  public static String getFromLocationId() {
    return FROM_LOCATION_ID;
  } 

  public static String getFromReceiptId() {
    return FROM_RECEIPT_ID;
  } 

  public static String getFromAdjustmentId() {
    return FROM_ADJUSTMENT_ID;
  } 

  public static String getToOrderId() {
    return TO_ORDER_ID;
  } 

  public static String getToLocationId() {
    return TO_LOCATION_ID;
  } 

  public static String getTypeId() {
    return TYPE_ID;
  } 

  public static String getQuantity() {
    return QUANTITY;
  } 

  
}   
