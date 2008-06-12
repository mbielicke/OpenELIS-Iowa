
package org.openelis.meta;

/**
  * StorageUnit META Data
  */

import java.util.HashMap;
import org.openelis.util.Meta;

public class StorageUnitMeta implements Meta {
  	private static final String tableName = "storage_unit";
	private static final String entityName = "StorageUnit";
	private boolean includeInFrom = true;
	
	public static final String
              ID			= "storage_unit.id",
              CATEGORY		= "storage_unit.category",
              DESCRIPTION   = "storage_unit.description",
              IS_SINGULAR	= "storage_unit.is_singular";


  	private static final String[] columnNames = {
  	  ID,CATEGORY,DESCRIPTION,IS_SINGULAR};
  	  
	private static HashMap<String,String> columnHashList;

	private static final StorageUnitMeta storage_unitMeta = new StorageUnitMeta();
    
    static {
        columnHashList = new HashMap<String,String>(columnNames.length);
        for(int i = 0; i < columnNames.length; i++){
            columnHashList.put(columnNames[i].substring(tableName.length()+1), "");
        }
    }
    
    private StorageUnitMeta() {
        
    }
    
    public static StorageUnitMeta getInstance() {
        return storage_unitMeta;
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

  public static String getCategory() {
    return CATEGORY;
  } 

  public static String getDescription() {
    return DESCRIPTION;
  } 

  public static String getIsSingular() {
    return IS_SINGULAR;
  } 

  
}   
