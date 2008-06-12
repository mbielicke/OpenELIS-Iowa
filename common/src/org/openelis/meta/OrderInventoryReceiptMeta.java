package org.openelis.meta;

import java.util.HashMap;

import org.openelis.util.Meta;

public class OrderInventoryReceiptMeta  implements Meta{
    private String tableName = "inventoryReceipt";
    private String entityName = "InventoryReceipt";
    private boolean includeInFrom = true;
    
    public static final String
     ID                 = "inventoryReceipt.id",
     INVENTORY_ITEM     = "inventoryReceipt.inventoryItem",
     ORGANIZATION       = "inventoryReceipt.organization",
     RECIEVED_DATE      = "inventoryReceipt.recievedDate",
     QUANTITY_RECIEVED  = "inventoryReceipt.quantityRecieved",
     UNIT_COST          = "inventoryReceipt.unitCost",
     QC_REFERENCE       = "inventoryReceipt.qcReference",
     EXTERNAL_REFERENCE = "inventoryReceipt.externalReference",
     UPC                = "inventoryReceipt.upc";
    
    //
    // Array of column names used for building select/insert/update strings
    //
    private static final String[] columnNames = {
         ID, INVENTORY_ITEM, ORGANIZATION, RECIEVED_DATE, QUANTITY_RECIEVED, UNIT_COST,
         QC_REFERENCE, EXTERNAL_REFERENCE, UPC};
    
    private static HashMap<String,String> columnHashList;
    
    private static final OrderInventoryReceiptMeta orderInventoryReceiptMeta = new OrderInventoryReceiptMeta();
    
    static {
     columnHashList = new HashMap<String, String>(columnNames.length);
     for (int i = 0; i < columnNames.length; i++)
         columnHashList.put(columnNames[i].substring(17), "");
    }

    private OrderInventoryReceiptMeta() {

    }
    
    public static OrderInventoryReceiptMeta getInstance(){
        return orderInventoryReceiptMeta;
    }
    
    public String[] getColumnList() {
        return columnNames;
    }

    public String getTable() {
        return tableName;
    }
    
    public String getEntity(){
        return entityName;
    }
    
    public boolean includeInFrom(){
        return includeInFrom;
    }
    
    public boolean hasColumn(String columnName){
        if(columnName == null || !columnName.startsWith(tableName))
            return false;
        String column = columnName.substring(tableName.length()+1);
        
        return columnHashList.containsKey(column);
    }
    
    public static String id(){
        return columnNames[0];
    }
    
    public static String inventoryItem(){
        return columnNames[1];
    }
    
    public static String organization(){
        return columnNames[2];
    }
    
    public static String recievedDate(){
        return columnNames[3];
    }
    
    public static String quantityRecieved(){
        return columnNames[4];
    }
    
    public static String unitCost(){
        return columnNames[5];
    }
    
    public static String qcReference(){
        return columnNames[6];
    }
    
    public static String externalReference(){
        return columnNames[7];
    }
    
    public static String upc(){
        return columnNames[8];
    }
}