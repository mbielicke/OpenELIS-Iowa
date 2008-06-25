
package org.openelis.meta;

/**
  * Category META Data
  */

import java.util.HashSet;
import org.openelis.gwt.common.Meta;

public class CategoryMeta implements Meta {
  	private String path = "";
	private static final String entityName = "Category";
	
	private static final String
              ID					="id",
              SYSTEM_NAME					="systemName",
              NAME					="name",
              DESCRIPTION					="description",
              SECTION_ID					="sectionId";

  	private static final String[] columnNames = {
  	  ID,SYSTEM_NAME,NAME,DESCRIPTION,SECTION_ID};
  	  
	private HashSet<String> columnHashList;
    
    private void init() {
        columnHashList = new HashSet<String>(columnNames.length);
        for(int i = 0; i < columnNames.length; i++){
            columnHashList.add(path+columnNames[i]);
        }
    }
    
    public CategoryMeta() {
		init();        
    }
    
    public CategoryMeta(String path) {
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

    public String getSystemName() {
        return path + SYSTEM_NAME;
    } 

    public String getName() {
        return path + NAME;
    } 

    public String getDescription() {
        return path + DESCRIPTION;
    } 

    public String getSectionId() {
        return path + SECTION_ID;
    } 

  
}   
