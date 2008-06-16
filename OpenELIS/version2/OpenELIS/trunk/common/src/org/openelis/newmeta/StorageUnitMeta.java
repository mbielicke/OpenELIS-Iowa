
package org.openelis.newmeta;

/**
  * StorageUnit META Data
  */

import java.util.HashSet;
import org.openelis.gwt.common.NewMeta;

public class StorageUnitMeta implements NewMeta {
  	private String path = "";
	private static final String entityName = "StorageUnit";
	
	private static final String
              ID					="id",
              CATEGORY					="category",
              DESCRIPTION					="description",
              IS_SINGULAR					="isSingular";

  	private static final String[] columnNames = {
  	  ID,CATEGORY,DESCRIPTION,IS_SINGULAR};
  	  
	private HashSet<String> columnHashList;
    
    private void init() {
        columnHashList = new HashSet<String>(columnNames.length);
        for(int i = 0; i < columnNames.length; i++){
            columnHashList.add(path+columnNames[i]);
        }
    }
    
    public StorageUnitMeta() {
		init();        
    }
    
    public StorageUnitMeta(String path) {
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

    public String getCategory() {
        return path + CATEGORY;
    } 

    public String getDescription() {
        return path + DESCRIPTION;
    } 

    public String getIsSingular() {
        return path + IS_SINGULAR;
    } 

  
}   
