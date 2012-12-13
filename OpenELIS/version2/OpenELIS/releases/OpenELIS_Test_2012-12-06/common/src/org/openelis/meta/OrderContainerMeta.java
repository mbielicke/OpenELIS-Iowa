
package org.openelis.meta;

/**
  * OrderContainer META Data
  */

import java.util.HashSet;

import org.openelis.gwt.common.Meta;


public class OrderContainerMeta implements Meta {
  	private String path = "";
	private static final String entityName = "OrderContainer";
	
	private static final String
              ID					="id",
              ORDER_ID					="orderId",
              CONTAINER_ID					="containerId",
              NUMBER_OF_CONTAINERS					="numberOfContainers",
              TYPE_OF_SAMPLE_ID					="typeOfSampleId";

  	private static final String[] columnNames = {
  	  ID,ORDER_ID,CONTAINER_ID,NUMBER_OF_CONTAINERS,TYPE_OF_SAMPLE_ID};
  	  
	private HashSet<String> columnHashList;
    
    private void init() {
        columnHashList = new HashSet<String>(columnNames.length);
        for(int i = 0; i < columnNames.length; i++){
            columnHashList.add(path+columnNames[i]);
        }
    }
    
    public OrderContainerMeta() {
		init();        
    }
    
    public OrderContainerMeta(String path) {
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

    public String getOrderId() {
        return path + ORDER_ID;
    } 

    public String getContainerId() {
        return path + CONTAINER_ID;
    } 

    public String getNumberOfContainers() {
        return path + NUMBER_OF_CONTAINERS;
    } 

    public String getTypeOfSampleId() {
        return path + TYPE_OF_SAMPLE_ID;
    } 

  
}   
