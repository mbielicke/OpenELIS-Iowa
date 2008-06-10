package org.openelis.meta;

import java.util.HashMap;

import org.openelis.util.Meta;

public class OrderOrganizationAddressMeta implements Meta{
    private String tableName = "order.organization.address";
    private String entityName = "order.organization.address";
    private boolean includeInFrom = false;
    
    public static final String
    ID              = "order.organization.address.id",
    MULTIPLE_UNIT   = "order.organization.address.multipleUnit",
    STREET_ADDRESS  = "order.organization.address.streetAddress",
    CITY            = "order.organization.address.city",
    STATE           = "order.organization.address.state",
    ZIP_CODE        = "order.organization.address.zipCode",
    WORK_PHONE      = "order.organization.address.workPhone",
    HOME_PHONE      = "order.organization.address.homePhone",
    CELL_PHONE      = "order.organization.address.cellPhone",
    FAX_PHONE       = "order.organization.address.faxPhone",
    EMAIL           = "order.organization.address.email",
    COUNTRY         = "order.organization.address.country";

    //
    // Array of column names used for building select/insert/update strings
    //
    private static final String[] columnNames = {
         ID, MULTIPLE_UNIT, STREET_ADDRESS, CITY, STATE, ZIP_CODE, WORK_PHONE, HOME_PHONE,
         CELL_PHONE, FAX_PHONE, EMAIL, COUNTRY};
    
    private static HashMap<String, String> columnHashList;
    private static final OrderOrganizationAddressMeta orderOrganizationAddressMeta = new OrderOrganizationAddressMeta();
    
    static {
     columnHashList = new HashMap<String, String>(columnNames.length);
     for (int i = 0; i < columnNames.length; i++)
         columnHashList.put(columnNames[i].substring(27), "");
    }

    private OrderOrganizationAddressMeta() {

    }

    public static OrderOrganizationAddressMeta getInstance(){
        return orderOrganizationAddressMeta;
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
