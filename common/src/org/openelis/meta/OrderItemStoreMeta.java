package org.openelis.meta;

import java.util.HashMap;

import org.openelis.util.Meta;

public class OrderItemStoreMeta implements Meta{
    private String tableName = "store";
    private String entityName = "Dictionary";
    private boolean includeInFrom = false;
    
    public static final String
     ID             = "store.id",
     CATEGORY       = "store.category",
     RELATED_ENTRY  = "store.relatedEntry",
     SYSTEM_NAME    = "store.systemName",
     IS_ACTIVE      = "store.isActive",
     LOCAL_ABBREV   = "store.localAbbrev",
     ENTRY          = "store.entry";

    //
    // Array of column names used for building select/insert/update strings
    //
    private static final String[] columnNames = {
         ID, CATEGORY, RELATED_ENTRY, SYSTEM_NAME, IS_ACTIVE, LOCAL_ABBREV, ENTRY};
    
    private static HashMap<String,String> columnHashList;
    
    private static final OrderItemStoreMeta orderItemStoreMeta = new OrderItemStoreMeta();
    
    static {
     columnHashList = new HashMap<String, String>(columnNames.length);
     for (int i = 0; i < columnNames.length; i++)
         columnHashList.put(columnNames[i].substring(6), "");
    }

    private OrderItemStoreMeta() {

    }
    
    public static OrderItemStoreMeta getInstance(){
        return orderItemStoreMeta;
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
        System.out.println("if("+columnName == null+" || "+!columnName.startsWith(tableName)+")");
        if(columnName == null || !columnName.startsWith(tableName))
            return false;
            
        String column = columnName.substring(tableName.length()+1);
        
        return columnHashList.containsKey(column);
    }
    
    public static String id(){
        return columnNames[0];
    }
    
    public static String category(){
        return columnNames[1];
    }
    
    public static String relatedEntry(){
        return columnNames[2];
    }

    public static String systemName(){
        return columnNames[3];
    }
    
    public static String isActive(){
        return columnNames[4];
    }
    
    public static String localAbbrev(){
        return columnNames[5];
    }
    
    public static String entry(){
        return columnNames[6];
    }
}
