
package org.openelis.meta;

/**
  * Category META Data
  */

import java.util.HashMap;
import org.openelis.util.Meta;

public class CategoryMeta implements Meta {
  	private static final String tableName = "category";
	private static final String entityName = "Category";
	private boolean includeInFrom = true;
	
	public static final String
              ID					="category.id",
              SYSTEM_NAME					="category.system_name",
              NAME					="category.name",
              DESCRIPTION					="category.description",
              SECTION_ID					="category.section_id";


  	private static final String[] columnNames = {
  	  ID,SYSTEM_NAME,NAME,DESCRIPTION,SECTION_ID};
  	  
	private static HashMap<String,String> columnHashList;

	private static final CategoryMeta categoryMeta = new CategoryMeta();
    
    static {
        columnHashList = new HashMap<String,String>(columnNames.length);
        for(int i = 0; i < columnNames.length; i++){
            columnHashList.put(columnNames[i].substring(tableName.length()+1), "");
        }
    }
    
    private CategoryMeta() {
        
    }
    
    public static CategoryMeta getInstance() {
        return categoryMeta;
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

  public static String getSystemName() {
    return SYSTEM_NAME;
  } 

  public static String getName() {
    return NAME;
  } 

  public static String getDescription() {
    return DESCRIPTION;
  } 

  public static String getSectionId() {
    return SECTION_ID;
  } 

  
}   
