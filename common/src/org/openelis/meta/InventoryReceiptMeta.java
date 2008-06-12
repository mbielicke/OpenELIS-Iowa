
package org.openelis.meta;

/**
  * InventoryReceipt META Data
  */

import java.util.HashMap;
import org.openelis.util.Meta;

public class InventoryReceiptMeta implements Meta {
  	private static final String tableName = "inventory_receipt";
	private static final String entityName = "InventoryReceipt";
	private boolean includeInFrom = true;
	
	public static final String
              ID					="inventory_receipt.id",
              INVENTORY_ITEM_ID					="inventory_receipt.inventoryItemId",
              ORGANIZATION_ID					="inventory_receipt.organizationId",
              RECEIVED_DATE					="inventory_receipt.receivedDate",
              QUANTITY_RECEIVED					="inventory_receipt.quantityReceived",
              UNIT_COST					="inventory_receipt.unitCost",
              QC_REFERENCE					="inventory_receipt.qcReference",
              EXTERNAL_REFERENCE					="inventory_receipt.externalReference",
              UPC					="inventory_receipt.upc";


  	private static final String[] columnNames = {
  	  ID,INVENTORY_ITEM_ID,ORGANIZATION_ID,RECEIVED_DATE,QUANTITY_RECEIVED,UNIT_COST,QC_REFERENCE,EXTERNAL_REFERENCE,UPC};
  	  
	private static HashMap<String,String> columnHashList;

	private static final InventoryReceiptMeta inventory_receiptMeta = new InventoryReceiptMeta();
    
    static {
        columnHashList = new HashMap<String,String>(columnNames.length);
        for(int i = 0; i < columnNames.length; i++){
            columnHashList.put(columnNames[i].substring(tableName.length()+1), "");
        }
    }
    
    private InventoryReceiptMeta() {
        
    }
    
    public static InventoryReceiptMeta getInstance() {
        return inventory_receiptMeta;
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

  public static String getInventoryItemId() {
    return INVENTORY_ITEM_ID;
  } 

  public static String getOrganizationId() {
    return ORGANIZATION_ID;
  } 

  public static String getReceivedDate() {
    return RECEIVED_DATE;
  } 

  public static String getQuantityReceived() {
    return QUANTITY_RECEIVED;
  } 

  public static String getUnitCost() {
    return UNIT_COST;
  } 

  public static String getQcReference() {
    return QC_REFERENCE;
  } 

  public static String getExternalReference() {
    return EXTERNAL_REFERENCE;
  } 

  public static String getUpc() {
    return UPC;
  } 

  
}   
