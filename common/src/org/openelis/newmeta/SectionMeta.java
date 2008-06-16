
package org.openelis.newmeta;

/**
  * Section META Data
  */

import java.util.HashSet;
import org.openelis.gwt.common.NewMeta;

public class SectionMeta implements NewMeta {
  	private String path = "";
	private static final String entityName = "Section";
	
	private static final String
              ID					="id",
              PARENT_SECTION_ID					="parentSectionId",
              NAME					="name",
              DESCRIPTION					="description",
              IS_EXTERNAL					="isExternal",
              ORGANIZATION_ID					="organizationId";

  	private static final String[] columnNames = {
  	  ID,PARENT_SECTION_ID,NAME,DESCRIPTION,IS_EXTERNAL,ORGANIZATION_ID};
  	  
	private HashSet<String> columnHashList;
    
    private void init() {
        columnHashList = new HashSet<String>(columnNames.length);
        for(int i = 0; i < columnNames.length; i++){
            columnHashList.add(path+columnNames[i]);
        }
    }
    
    public SectionMeta() {
		init();        
    }
    
    public SectionMeta(String path) {
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

    public String getParentSectionId() {
        return path + PARENT_SECTION_ID;
    } 

    public String getName() {
        return path + NAME;
    } 

    public String getDescription() {
        return path + DESCRIPTION;
    } 

    public String getIsExternal() {
        return path + IS_EXTERNAL;
    } 

    public String getOrganizationId() {
        return path + ORGANIZATION_ID;
    } 

  
}   
