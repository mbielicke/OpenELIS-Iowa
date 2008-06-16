
package org.openelis.newmeta;

/**
  * SystemVariable META Data
  */

import java.util.HashSet;
import org.openelis.gwt.common.NewMeta;

public class SystemVariableMeta implements NewMeta {
  	private String path = "";
	private static final String entityName = "SystemVariable";
	
	private static final String
              ID					="id",
              NAME					="name",
              VALUE					="value";

  	private static final String[] columnNames = {
  	  ID,NAME,VALUE};
  	  
	private HashSet<String> columnHashList;
    
    private void init() {
        columnHashList = new HashSet<String>(columnNames.length);
        for(int i = 0; i < columnNames.length; i++){
            columnHashList.add(path+columnNames[i]);
        }
    }
    
    public SystemVariableMeta() {
		init();        
    }
    
    public SystemVariableMeta(String path) {
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

    public String getValue() {
        return path + VALUE;
    } 

  
}   
