package org.openelis.meta;

import java.util.HashMap;

import org.openelis.util.Meta;

public class InventoryLocationStorageLocationMeta implements Meta{
    private String tableName = "storageLocation";
    private String entityName = "inventory_location.storageLocation";
    private boolean includeInFrom = true;
    
    public static final String
     ID                         = "storageLocation.id",
     SORT_ORDER_ID              = "storageLocation.sortOrder",
     NAME                       = "storageLocation.name",
     LOCATION                   = "storageLocation.location",
     PARENT_STORAGE_LOCATION_ID = "storageLocation.parentStorageLocationId",
     STORAGE_UNIT_ID            = "storageLocation.storageUnit",
     IS_AVAILABLE               = "storageLocation.isAvailable";

    //
    // Array of column names used for building select/insert/update strings
    //
    private static final String[] columnNames = {
         ID, SORT_ORDER_ID, NAME, LOCATION, PARENT_STORAGE_LOCATION_ID, STORAGE_UNIT_ID, IS_AVAILABLE};
    
    private static HashMap<String,String> columnHashList;
    
    private static final InventoryLocationStorageLocationMeta inventoryLocationStorageLocationMeta = new InventoryLocationStorageLocationMeta();
    
    static {
     columnHashList = new HashMap<String, String>(columnNames.length);
     for (int i = 0; i < columnNames.length; i++)
         columnHashList.put(columnNames[i].substring(16), "");
    }

    private InventoryLocationStorageLocationMeta() {

    }
    
    public static InventoryLocationStorageLocationMeta getInstance(){
        return inventoryLocationStorageLocationMeta;
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
    
    public static String getId(){
        return columnNames[0];
    }
    
    public static String getSortOrderId(){
        return columnNames[1];
    }
    
    public static String getName(){
        return columnNames[2];
    }
    
    public static String getLocation(){
        return columnNames[3];
    }
    
    public static String getParentStorageLocationId(){
        return columnNames[4];
    }
    
    public static String getStorageUnitId(){
        return columnNames[5];
    }
    
    public static String getIsAvailable(){
        return columnNames[6];
    }
}