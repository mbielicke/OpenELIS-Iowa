
package org.openelis.meta;

/**
  * Shipping META Data
  */

import java.util.HashSet;
import org.openelis.gwt.common.Meta;

public class ShippingMeta implements Meta {
  	protected String path = "";
	private static final String entityName = "Shipping";
	
	private static final String
              ID					="id",
              STATUS_ID					="statusId",
              SHIPPED_FROM_ID					="shippedFromId",
              PROCESSED_BY_ID					="processedById",
              PROCESSED_DATE					="processedDate",
              SHIPPED_METHOD_ID					="shippedMethodId",
              SHIPPED_DATE					="shippedDate",
              NUMBER_OF_PACKAGES					="numberOfPackages";

  	private static final String[] columnNames = {
  	  ID,STATUS_ID,SHIPPED_FROM_ID,PROCESSED_BY_ID,PROCESSED_DATE,SHIPPED_METHOD_ID,SHIPPED_DATE,NUMBER_OF_PACKAGES};
  	  
	private HashSet<String> columnHashList;
    
    private void init() {
        columnHashList = new HashSet<String>(columnNames.length);
        for(int i = 0; i < columnNames.length; i++){
            columnHashList.add(path+columnNames[i]);
        }
    }
    
    public ShippingMeta() {
		init();        
    }
    
    public ShippingMeta(String path) {
        this.path = path;
		init();        
    }

    public String[] getColumnList() {
        return columnNames;
    }

    public String getEntity() {
        return entityName;
    }

    public boolean hasColumn(String columnName) {
        return columnHashList.contains(columnName);
    }
    
    
    public String getId() {
        return path + ID;
    } 

    public String getStatusId() {
        return path + STATUS_ID;
    } 

    public String getShippedFromId() {
        return path + SHIPPED_FROM_ID;
    } 

    public String getProcessedById() {
        return path + PROCESSED_BY_ID;
    } 

    public String getProcessedDate() {
        return path + PROCESSED_DATE;
    } 

    public String getShippedMethodId() {
        return path + SHIPPED_METHOD_ID;
    } 

    public String getShippedDate() {
        return path + SHIPPED_DATE;
    } 

    public String getNumberOfPackages() {
        return path + NUMBER_OF_PACKAGES;
    } 

  
}   
