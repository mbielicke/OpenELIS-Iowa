
package org.openelis.meta;

/**
  * OrderTest META Data
  */

import java.util.HashSet;
import org.openelis.ui.common.Meta;

public class IOrderTestMeta implements Meta {
  	private String path = "";
	private static final String entityName = "OrderTest";
	
	private static final String
              ID					="id",
              IORDER_ID             ="iorderId",
              SEQUENCE              ="sequence",
              REFERENCE_ID          ="referenceId",
              REFERENCE_TABLE_ID    ="referenceTableId";

  	private static final String[] columnNames = {
  	  ID,IORDER_ID,SEQUENCE,REFERENCE_ID,REFERENCE_TABLE_ID};
  	  
	private HashSet<String> columnHashList;
    
    private void init() {
        columnHashList = new HashSet<String>(columnNames.length);
        for(int i = 0; i < columnNames.length; i++){
            columnHashList.add(path+columnNames[i]);
        }
    }
    
    public IOrderTestMeta() {
		init();        
    }
    
    public IOrderTestMeta(String path) {
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

    public String getIorderId() {
        return path + IORDER_ID;
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
