package org.openelis.meta;

import java.util.HashMap;

import org.openelis.util.Meta;

public class OrderBillToAddressMeta implements Meta{
    private String tableName = "ordr.billTo.address";
    private String entityName = "ordr.billTo.address";
    private boolean includeInFrom = false;
    
    public static final String
    ID              = "ordr.billTo.address.id",
    MULTIPLE_UNIT   = "ordr.billTo.address.multipleUnit",
    STREET_ADDRESS  = "ordr.billTo.address.streetAddress",
    CITY            = "ordr.billTo.address.city",
    STATE           = "ordr.billTo.address.state",
    ZIP_CODE        = "ordr.billTo.address.zipCode",
    WORK_PHONE      = "ordr.billTo.address.workPhone",
    HOME_PHONE      = "ordr.billTo.address.homePhone",
    CELL_PHONE      = "ordr.billTo.address.cellPhone",
    FAX_PHONE       = "ordr.billTo.address.faxPhone",
    EMAIL           = "ordr.billTo.address.email",
    COUNTRY         = "ordr.billTo.address.country";

    //
    // Array of column names used for building select/insert/update strings
    //
    private static final String[] columnNames = {
         ID, MULTIPLE_UNIT, STREET_ADDRESS, CITY, STATE, ZIP_CODE, WORK_PHONE, HOME_PHONE,
         CELL_PHONE, FAX_PHONE, EMAIL, COUNTRY};
    
    private static HashMap<String, String> columnHashList;
    private static final OrderBillToAddressMeta orderBillToAddressMeta = new OrderBillToAddressMeta();
    
    static {
     columnHashList = new HashMap<String, String>(columnNames.length);
     for (int i = 0; i < columnNames.length; i++)
         columnHashList.put(columnNames[i].substring(20), "");
    }

    private OrderBillToAddressMeta() {

    }

    public static OrderBillToAddressMeta getInstance(){
        return orderBillToAddressMeta;
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
    
    public static String getMmultipleUnit(){
        return columnNames[1];
    }
    
    public static String getStreetAddress(){
        return columnNames[2];
    }
    
    public static String getCity(){
        return columnNames[3];
    }
    
    public static String getState(){
        return columnNames[4];
    }
    
    public static String getZipCode(){
        return columnNames[5];
    }
    
    public static String getWorkPhone(){
        return columnNames[6];
    }
    
    public static String getHomePhone(){
        return columnNames[7];
    }
    
    public static String getCellPhone(){
        return columnNames[8];
    }
    
    public static String getFaxPhone(){
        return columnNames[9];
    }
    
    public static String getEmail(){
        return columnNames[10];
    }
    
    public static String getCountry(){
        return columnNames[11];
    }
}
