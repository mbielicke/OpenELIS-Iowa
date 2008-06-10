package org.openelis.meta;

import java.util.HashMap;

import org.openelis.util.Meta;

public class OrderCustomerNoteMeta implements Meta{
    private String tableName = "customerNote";
    private String entityName = "order.note";
    private boolean includeInFrom = false;
    
    public static final String
    ID                  = "customerNote.id",
    REFERENCE_ID        = "customerNote.referenceId",
    REFERENCE_TABLE     = "customerNote.referenceTable",
    TIMESTAMP           = "customerNote.timestamp",
    IS_EXTERNAL         = "customerNote.isExternal",
    SYSTEM_USER         = "customerNote.systemUser",
    SUBJECT             = "customerNote.subject",
    TEXT                = "customerNote.text";

    //
    // Array of column names used for building select/insert/update strings
    //
    private static final String[] columnNames = {
         ID, REFERENCE_ID, REFERENCE_TABLE, TIMESTAMP, IS_EXTERNAL, SYSTEM_USER, SUBJECT, TEXT};
    
    private static HashMap<String, String> columnHashList;
    private static final OrderCustomerNoteMeta orderCustomerNoteMeta = new OrderCustomerNoteMeta();
    
    static {
     columnHashList = new HashMap<String, String>(columnNames.length);
     for (int i = 0; i < columnNames.length; i++)
         columnHashList.put(columnNames[i].substring(13), "");
    }

    private OrderCustomerNoteMeta() {

    }

    public static OrderCustomerNoteMeta getInstance(){
        return orderCustomerNoteMeta;
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

    public boolean includeInFrom(){
        return includeInFrom;
    }
    
    public boolean hasColumn(String columnName) {
        if(columnName == null || !columnName.startsWith(tableName))
            return false;
        String column = columnName.substring(tableName.length()+1);
        
        return columnHashList.containsKey(column);
    }
    
    public static String id(){
        return columnNames[0];
    }
    
    public static String referenceId(){
        return columnNames[1];
    }
    
    public static String referenceTable(){
        return columnNames[2];
    }
    
    public static String timestamp(){
        return columnNames[3];
    }
    
    public static String isExternal(){
        return columnNames[4];
    }
    
    public static String systemUser(){
        return columnNames[5];
    }
    
    public static String subject(){
        return columnNames[6];
    }
    
    public static String text(){
        return columnNames[7];
    }
}
