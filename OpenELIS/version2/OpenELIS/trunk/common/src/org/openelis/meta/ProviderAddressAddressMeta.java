package org.openelis.meta;

import java.util.HashMap;

import org.openelis.util.Meta;

public class ProviderAddressAddressMeta implements Meta {
    
    private String tableName = "providerAddress.address";
    private String entityName = "providerAddress.address";
    private boolean includeInFrom = false;
    
    
    public static final String 
    ID              = "providerAddress.address.id",
    MULTIPLE_UNIT   = "providerAddress.address.multipleUnit",
    STREET_ADDRESS  = "providerAddress.address.streetAddress",
    CITY            = "providerAddress.address.city",
    STATE           = "providerAddress.address.state",
    ZIP_CODE        = "providerAddress.address.zipCode",
    WORK_PHONE      = "providerAddress.address.workPhone",
    HOME_PHONE      = "providerAddress.address.homePhone",                       
    CELL_PHONE      = "providerAddress.address.cellPhone",
    FAX_PHONE       = "providerAddress.address.faxPhone",
    EMAIL           = "providerAddress.address.email",
    COUNTRY         = "providerAddress.address.country";
    
    //
    // Array of column names used for building select/insert/update strings
    //
    private static final String[] columnNames = {
         ID, MULTIPLE_UNIT, STREET_ADDRESS, CITY, STATE, ZIP_CODE, WORK_PHONE, HOME_PHONE,
         CELL_PHONE, FAX_PHONE, EMAIL, COUNTRY};
        
    private static HashMap<String, String> columnHashList;
    
    private static final ProviderAddressAddressMeta providerAddressAddressMeta = new ProviderAddressAddressMeta();
    
    static {
        columnHashList = new HashMap<String, String>(columnNames.length);
        for (int i = 0; i < columnNames.length; i++)
            columnHashList.put(columnNames[i].substring(24), "");
       }
          
    private ProviderAddressAddressMeta(){
        
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

    
    public static ProviderAddressAddressMeta getInstance(){
        return providerAddressAddressMeta;
    }
    
    public static String id(){
        return columnNames[0];
    }
    
    public static String multipleUnit(){
        return columnNames[1];
    }
    
    public static String streetAddress(){
        return columnNames[2];
    }
    
    public static String city(){
        return columnNames[3];
    }
    
    public static String state(){
        return columnNames[4];
    }
    
    public static String zipCode(){
        return columnNames[5];
    }
    
    public static String workPhone(){
        return columnNames[6];
    }
    
    public static String homePhone(){
        return columnNames[7];
    }
    
    public static String cellPhone(){
        return columnNames[8];
    }
    
    public static String faxPhone(){
        return columnNames[9];
    }
    
    public static String email(){
        return columnNames[10];
    }
    
    public static String country(){
        return columnNames[11];
    }

}
