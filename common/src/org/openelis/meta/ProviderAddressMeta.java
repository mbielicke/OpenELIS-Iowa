package org.openelis.meta;

import java.util.HashMap;

import org.openelis.util.Meta;

public class ProviderAddressMeta implements Meta {

    private String tableName = "providerAddress";
    private String entityName = "provider.providerAddress";
    private boolean includeInFrom = true;
    
    public static final String 
     ID             =  "providerAddress.id",
     LOCATION       =  "providerAddress.location", 
     EXTERNAL_ID    =  "providerAddress.externalId", 
     PROVIDER       =  "providerAddress.provider", 
     ADDRESS        =  "providerAddress.addressId";   
    
    //
    // Array of column names used for building select/insert/update strings
    //
    private static final String[] columnNames = {
         ID, LOCATION, EXTERNAL_ID, PROVIDER, ADDRESS};
    
    private static HashMap<String,String> columnHashList;
    
    private static final ProviderAddressMeta providerAddressMeta = new ProviderAddressMeta();
    
    static {
        columnHashList = new HashMap<String, String>(columnNames.length);
        for (int i = 0; i < columnNames.length; i++)
            columnHashList.put(columnNames[i].substring(16), "");
       }
    
    private ProviderAddressMeta(){
        
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

    
    public static ProviderAddressMeta getInstance(){
        return providerAddressMeta;
    }
    
    public static String id(){
        return columnNames[0];
    }
    
    public static String location(){
        return columnNames[1];
    }       
    
    public static String externalId(){
        return columnNames[2];
    }
    
    public static String provider(){
        return columnNames[3];
    }
    
    public static String addressId(){
        return columnNames[4];
    }

}
