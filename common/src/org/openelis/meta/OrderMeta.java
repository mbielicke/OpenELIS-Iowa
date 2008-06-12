
package org.openelis.meta;

/**
  * Order META Data
  */

import java.util.HashMap;
import org.openelis.util.Meta;

public class OrderMeta implements Meta {
  	private static final String tableName = "order";
	private static final String entityName = "Order";
	private boolean includeInFrom = true;
	
	public static final String
              ID					="order.id",
              STATUS_ID					="order.statusId",
              ORDERED_DATE					="order.orderedDate",
              NEEDED_IN_DAYS					="order.neededInDays",
              REQUESTED_BY					="order.requestedBy",
              COST_CENTER_ID					="order.costCenterId",
              ORGANIZATION_ID					="order.organizationId",
              IS_EXTERNAL					="order.isExternal",
              EXTERNAL_ORDER_NUMBER					="order.externalOrderNumber",
              REPORT_TO_ID					="order.reportToId",
              BILL_TO_ID					="order.billToId";


  	private static final String[] columnNames = {
  	  ID,STATUS_ID,ORDERED_DATE,NEEDED_IN_DAYS,REQUESTED_BY,COST_CENTER_ID,ORGANIZATION_ID,IS_EXTERNAL,EXTERNAL_ORDER_NUMBER,REPORT_TO_ID,BILL_TO_ID};
  	  
	private static HashMap<String,String> columnHashList;

	private static final OrderMeta orderMeta = new OrderMeta();
    
    static {
        columnHashList = new HashMap<String,String>(columnNames.length);
        for(int i = 0; i < columnNames.length; i++){
            columnHashList.put(columnNames[i].substring(tableName.length()+1), "");
        }
    }
    
    private OrderMeta() {
        
    }
    
    public static OrderMeta getInstance() {
        return orderMeta;
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
        // TODO Auto-generated method stub
        return includeInFrom;
    }
    
    
  public static String getId() {
    return ID;
  } 

  public static String getStatusId() {
    return STATUS_ID;
  } 

  public static String getOrderedDate() {
    return ORDERED_DATE;
  } 

  public static String getNeededInDays() {
    return NEEDED_IN_DAYS;
  } 

  public static String getRequestedBy() {
    return REQUESTED_BY;
  } 

  public static String getCostCenterId() {
    return COST_CENTER_ID;
  } 

  public static String getOrganizationId() {
    return ORGANIZATION_ID;
  } 

  public static String getIsExternal() {
    return IS_EXTERNAL;
  } 

  public static String getExternalOrderNumber() {
    return EXTERNAL_ORDER_NUMBER;
  } 

  public static String getReportToId() {
    return REPORT_TO_ID;
  } 

  public static String getBillToId() {
    return BILL_TO_ID;
  } 

  
}   
