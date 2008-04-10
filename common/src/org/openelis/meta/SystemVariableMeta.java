package org.openelis.meta;

import java.util.HashMap;

import org.openelis.util.Meta;

public class SystemVariableMeta implements Meta {

    private String tableName = "systemVariable";
    private String entityName = "SystemVariable";
    private boolean includeInFrom = true;
    
    public static final String
    ID                      = "systemVariable.id",
    NAME                    = "systemVariable.name",
    VALUE                   = "systemVariable.value";     
        
    
    //
    // Array of column names used for building select/insert/update strings
    //
    private static final String[] columnNames = {ID, NAME, VALUE};
    
    private static HashMap<String,String> columnHashList;
    
    
    static {
        columnHashList = new HashMap<String, String>(columnNames.length);
        for (int i = 0; i < columnNames.length; i++)
            columnHashList.put(columnNames[i].substring(15), "");
       }
    
    private static SystemVariableMeta sysVarMeta = new SystemVariableMeta();      
    
    private SystemVariableMeta(){
        
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
        return includeInFrom;
    }
    
    public static String id(){
        return columnNames[0];
    }
    
    public static String name(){
        return columnNames[1];
    }
    
    public static String value(){
        return columnNames[2];
    }
    
    public static SystemVariableMeta getInstance(){
        return sysVarMeta;
    }
    

}
