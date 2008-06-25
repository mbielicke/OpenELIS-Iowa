
package org.openelis.meta;

/**
  * TestTrailer META Data
  */

import java.util.HashSet;
import org.openelis.gwt.common.Meta;

public class TestTrailerMeta implements Meta {
  	private String path = "";
	private static final String entityName = "TestTrailer";
	
	private static final String
              ID					="id",
              NAME					="name",
              DESCRIPTION					="description",
              TEXT					="text";

  	private static final String[] columnNames = {
  	  ID,NAME,DESCRIPTION,TEXT};
  	  
	private HashSet<String> columnHashList;
    
    private void init() {
        columnHashList = new HashSet<String>(columnNames.length);
        for(int i = 0; i < columnNames.length; i++){
            columnHashList.add(path+columnNames[i]);
        }
    }
    
    public TestTrailerMeta() {
		init();        
    }
    
    public TestTrailerMeta(String path) {
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

    public String getName() {
        return path + NAME;
    } 

    public String getDescription() {
        return path + DESCRIPTION;
    } 

    public String getText() {
        return path + TEXT;
    } 

  
}   
