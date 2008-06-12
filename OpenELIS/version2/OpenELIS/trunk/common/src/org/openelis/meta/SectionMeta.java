
package org.openelis.meta;

/**
  * Section META Data
  */

import java.util.HashMap;
import org.openelis.util.Meta;

public class SectionMeta implements Meta {
  	private static final String tableName = "section";
	private static final String entityName = "Section";
	private boolean includeInFrom = true;
	
	public static final String
              ID					="section.id",
              PARENT_SECTION_ID					="section.parent_section_id",
              NAME					="section.name",
              DESCRIPTION					="section.description",
              IS_EXTERNAL					="section.is_external",
              ORGANIZATION_ID					="section.organization_id";


  	private static final String[] columnNames = {
  	  ID,PARENT_SECTION_ID,NAME,DESCRIPTION,IS_EXTERNAL,ORGANIZATION_ID};
  	  
	private static HashMap<String,String> columnHashList;

	private static final SectionMeta sectionMeta = new SectionMeta();
    
    static {
        columnHashList = new HashMap<String,String>(columnNames.length);
        for(int i = 0; i < columnNames.length; i++){
            columnHashList.put(columnNames[i].substring(tableName.length()+1), "");
        }
    }
    
    private SectionMeta() {
        
    }
    
    public static SectionMeta getInstance() {
        return sectionMeta;
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

  public static String getParentSectionId() {
    return PARENT_SECTION_ID;
  } 

  public static String getName() {
    return NAME;
  } 

  public static String getDescription() {
    return DESCRIPTION;
  } 

  public static String getIsExternal() {
    return IS_EXTERNAL;
  } 

  public static String getOrganizationId() {
    return ORGANIZATION_ID;
  } 

  
}   
