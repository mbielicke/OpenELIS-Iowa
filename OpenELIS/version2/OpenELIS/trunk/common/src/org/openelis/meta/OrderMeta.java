package org.openelis.meta;

import java.util.HashMap;

import org.openelis.util.Meta;

public class OrderMeta implements Meta{
    private String tableName = "ordr";
    private String entityName = "Order";
    private boolean includeInFrom = true;
    
    public static final String
     ID                     = "ordr.id",
     STATUS                 = "ordr.status",
     ORDERED_DATE           = "ordr.orderedDate",
     NEEDED_IN_DAYS         = "ordr.neededInDays",
     REQUESTED_BY           = "ordr.requestedBy",
     COST_CENTER            = "ordr.costCenter",
     ORGANIZATION           = "ordr.organization",
     IS_EXTERNAL            = "ordr.isExternal",
     EXTERNAL_ORDER_NUMBER  = "ordr.externalOrderNumber",
     REPORT_TO              = "ordr.reportTo",
     BILL_TO                = "ordr.billTo";

    //
    // Array of column names used for building select/insert/update strings
    //
    private static final String[] columnNames = {
         ID, STATUS, ORDERED_DATE, NEEDED_IN_DAYS, REQUESTED_BY, COST_CENTER, ORGANIZATION,
         IS_EXTERNAL, EXTERNAL_ORDER_NUMBER, REPORT_TO, BILL_TO};
    
    private static HashMap<String,String> columnHashList;
    
    private static final OrderMeta orderMeta = new OrderMeta();
    
    static {
     columnHashList = new HashMap<String, String>(columnNames.length);
     for (int i = 0; i < columnNames.length; i++)
         columnHashList.put(columnNames[i].substring(5), "");
    }

    private OrderMeta() {

    }
    
    public static OrderMeta getInstance(){
        return orderMeta;
    }
    
    public String[] getColumnList() {
        return columnNames;
    }

    public String getTable() {
        return tableName;
    }
    
    public String getEntity(){
        return entityName;
    }
    
    public boolean includeInFrom(){
        return includeInFrom;
    }
    
    public boolean hasColumn(String columnName){
        if(columnName == null || !columnName.startsWith(tableName))
            return false;
        String column = columnName.substring(tableName.length()+1);
        
        return columnHashList.containsKey(column);
    }
    
    public static String id(){
        return columnNames[0];
    }
    
    public static String status(){
        return columnNames[1];
    }
    
    public static String orderedDate(){
        return columnNames[2];
    }
    
    public static String neededInDays(){
        return columnNames[3];
    }
    
    public static String requestedBy(){
        return columnNames[4];
    }
        
    public static String costCenter(){
        return columnNames[5];
    }
    
    public static String organization(){
        return columnNames[6];
    }
    
    public static String isExternal(){
        return columnNames[7];
    }
    
    public static String externalOrderNumber(){
        return columnNames[8];
    }
    
    public static String reportTo(){
        return columnNames[9];
    }
    
    public static String billTo(){
        return columnNames[10];
    }
}
