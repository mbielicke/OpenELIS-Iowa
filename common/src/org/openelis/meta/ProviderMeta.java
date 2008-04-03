package org.openelis.meta;

import java.util.HashMap;

import org.openelis.util.Meta;

public class ProviderMeta implements Meta {

    private String tableName = "provider";
    private String entityName = "Provider";
    private boolean includeInFrom = true;
    
    public static final String
    ID                      = "provider.id",
    LAST_NAME               = "provider.lastName",
    FIRST_NAME              = "provider.firstName",  
    MIDDLE_NAME             = "provider.middleName",
    TYPE                    = "provider.type", 
    NPI                     = "provider.npi";    
    
    //
    // Array of column names used for building select/insert/update strings
    //
    private static final String[] columnNames = {
         ID, LAST_NAME, FIRST_NAME, MIDDLE_NAME, TYPE, NPI};
    
    private static HashMap<String,String> columnHashList;
    
    private static final ProviderMeta providerMeta = new ProviderMeta();
        
    private ProviderMeta (){
        
    }
    
    static {
     columnHashList = new HashMap<String, String>(columnNames.length);
     for (int i = 0; i < columnNames.length; i++)
         columnHashList.put(columnNames[i].substring(9), "");
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

    public boolean hasColumn(String columnName){
        if(columnName == null || !columnName.startsWith(tableName))
            return false;
        String column = columnName.substring(tableName.length()+1);
        
        return columnHashList.containsKey(column);
    }

    public boolean includeInFrom() {        
        return includeInFrom;
    }

    public static ProviderMeta getInstance(){
        return providerMeta;
    }
    
    public static String id(){
        return columnNames[0];
    }
    
    public static String lastName(){
        return columnNames[1];
    }
    
    public static String firstName(){
        return columnNames[2];
    }
    
    public static String middleName(){
        return columnNames[3];
    }
    
    public static String type(){
        return columnNames[4];
    }
    
    public static String npi(){
        return columnNames[5];
    }
}
