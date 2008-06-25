
package org.openelis.meta;

/**
  * ReferenceTable META Data
  */

import java.util.HashSet;
import org.openelis.gwt.common.Meta;

public class ReferenceTableMeta implements Meta {
  	private String path = "";
	private static final String entityName = "ReferenceTable";
	
	private static final String
              ID					="id",
              NAME					="name";

  	private static final String[] columnNames = {
  	  ID,NAME};
  	  
	private HashSet<String> columnHashList;
    
    private void init() {
        columnHashList = new HashSet<String>(columnNames.length);
        for(int i = 0; i < columnNames.length; i++){
            columnHashList.add(path+columnNames[i]);
        }
    }
    
    public ReferenceTableMeta() {
		init();        
    }
    
    public ReferenceTableMeta(String path) {
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

  
}   
