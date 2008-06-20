package org.openelis.meta;

import java.util.HashMap;

import org.openelis.util.Meta;

public class OrderShippingNoteMeta implements Meta{
    private String tableName = "shippingNote";
    private String entityName = "ordr.note";
    private boolean includeInFrom = false;
    
    public static final String
    ID                  = "shippingNote.id",
    REFERENCE_ID        = "shippingNote.referenceId",
    REFERENCE_TABLE     = "shippingNote.referenceTable",
    TIMESTAMP           = "shippingNote.timestamp",
    IS_EXTERNAL         = "shippingNote.isExternal",
    SYSTEM_USER         = "shippingNote.systemUser",
    SUBJECT             = "shippingNote.subject",
    TEXT                = "shippingNote.text";

    //
    // Array of column names used for building select/insert/update strings
    //
    private static final String[] columnNames = {
         ID, REFERENCE_ID, REFERENCE_TABLE, TIMESTAMP, IS_EXTERNAL, SYSTEM_USER, SUBJECT, TEXT};
    
    private static HashMap<String, String> columnHashList;
    private static final OrderShippingNoteMeta orderShippingNoteMeta = new OrderShippingNoteMeta();
    
    static {
     columnHashList = new HashMap<String, String>(columnNames.length);
     for (int i = 0; i < columnNames.length; i++)
         columnHashList.put(columnNames[i].substring(13), "");
    }

    private OrderShippingNoteMeta() {

    }

    public static OrderShippingNoteMeta getInstance(){
        return orderShippingNoteMeta;
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
    
    public static String getId(){
        return columnNames[0];
    }
    
    public static String getReferenceId(){
        return columnNames[1];
    }
    
    public static String getReferenceTable(){
        return columnNames[2];
    }
    
    public static String getTimestamp(){
        return columnNames[3];
    }
    
    public static String getIsExternal(){
        return columnNames[4];
    }
    
    public static String getSystemUser(){
        return columnNames[5];
    }
    
    public static String getSubject(){
        return columnNames[6];
    }
    
    public static String getText(){
        return columnNames[7];
    }
}
