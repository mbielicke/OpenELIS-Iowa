
package org.openelis.meta;

/**
  * OrderTest META Data
  */

import java.util.HashSet;
import org.openelis.gwt.common.Meta;

public class OrderTestMeta implements Meta {
  	private String path = "";
	private static final String entityName = "OrderTest";
	
	private static final String
              ID					="id",
              ORDER_ID					="orderId",
              SEQUENCE					="sequence",
              REFERENCE_ID					="referenceId",
              REFERENCE_TABLE_ID					="referenceTableId";

  	private static final String[] columnNames = {
  	  ID,ORDER_ID,SEQUENCE,REFERENCE_ID,REFERENCE_TABLE_ID};
  	  
	private HashSet<String> columnHashList;
    
    private void init() {
        columnHashList = new HashSet<String>(columnNames.length);
        for(int i = 0; i < columnNames.length; i++){
            columnHashList.add(path+columnNames[i]);
        }
    }
    
    public OrderTestMeta() {
		init();        
    }
    
    public OrderTestMeta(String path) {
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

    public String getSequence() {
        return path + SEQUENCE;
    } 

    public String getReferenceId() {
        return path + REFERENCE_ID;
    } 

    public String getReferenceTableId() {
        return path + REFERENCE_TABLE_ID;
    } 

  
}   
