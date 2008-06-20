package org.openelis.meta;

import java.util.HashMap;

import org.openelis.util.Meta;

public class OrderReportToAddressMeta implements Meta{
    private String tableName = "ordr.reportTo.address";
    private String entityName = "ordr.reportTo.address";
    private boolean includeInFrom = false;
    
    public static final String
    ID              = "ordr.reportTo.address.id",
    MULTIPLE_UNIT   = "ordr.reportTo.address.multipleUnit",
    STREET_ADDRESS  = "ordr.reportTo.address.streetAddress",
    CITY            = "ordr.reportTo.address.city",
    STATE           = "ordr.reportTo.address.state",
    ZIP_CODE        = "ordr.reportTo.address.zipCode",
    WORK_PHONE      = "ordr.reportTo.address.workPhone",
    HOME_PHONE      = "ordr.reportTo.address.homePhone",
    CELL_PHONE      = "ordr.reportTo.address.cellPhone",
    FAX_PHONE       = "ordr.reportTo.address.faxPhone",
    EMAIL           = "ordr.reportTo.address.email",
    COUNTRY         = "ordr.reportTo.address.country";

    //
    // Array of column names used for building select/insert/update strings
    //
    private static final String[] columnNames = {
         ID, MULTIPLE_UNIT, STREET_ADDRESS, CITY, STATE, ZIP_CODE, WORK_PHONE, HOME_PHONE,
         CELL_PHONE, FAX_PHONE, EMAIL, COUNTRY};
    
    private static HashMap<String, String> columnHashList;
    private static final OrderReportToAddressMeta orderReportToAddressMeta = new OrderReportToAddressMeta();
    
    static {
     columnHashList = new HashMap<String, String>(columnNames.length);
     for (int i = 0; i < columnNames.length; i++)
         columnHashList.put(columnNames[i].substring(22), "");
    }

    private OrderReportToAddressMeta() {

    }

    public static OrderReportToAddressMeta getInstance(){
        return orderReportToAddressMeta;
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
    
    public static String getMultipleUnit(){
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
    
    public static String getzipCode(){
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
