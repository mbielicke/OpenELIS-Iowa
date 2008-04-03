package org.openelis.meta;

import java.util.HashMap;

import org.openelis.util.Meta;

public class QaEventMethodMeta implements Meta{
    
    private String tableName = "method";
    private String entityName = "test.method";
    private boolean includeInFrom = true;
    
    public static final String 
    ID        =   "method.id",
    NAME      =   "method.name";
    
    //
    // Array of column names used for building select/insert/update strings
    //
    private static final String[] columnNames = {ID, NAME};
    
    private static HashMap<String,String> columnHashList;

    private static QaEventMethodMeta qaEventMethodMeta =  new QaEventMethodMeta();
    
    private QaEventMethodMeta(){
        
    }
    
    static {
        columnHashList = new HashMap<String, String>(columnNames.length);
        for (int i = 0; i < columnNames.length; i++)
            columnHashList.put(columnNames[i].substring(7), "");
       }
    
    public String[] getColumnList() {
        return null;
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
        return includeInFrom;
    }

    public static QaEventMethodMeta getInstance(){
        return qaEventMethodMeta;
    }
    
    public static String id(){
        return columnNames[0];
    }
    
    public static String name(){
        return columnNames[1];
    }
    
    
}
