
package org.openelis.meta;

/**
  * AuxFieldValue META Data
  */

import java.util.HashSet;
import org.openelis.gwt.common.Meta;

public class AuxFieldValueMeta implements Meta {
  	private String path = "";
	private static final String entityName = "AuxFieldValue";
	
	private static final String
              ID					="id",
              AUX_FIELD_ID					="auxFieldId",
              TYPE_ID					="typeId",
              VALUE					="value";

  	private static final String[] columnNames = {
  	  ID,AUX_FIELD_ID,TYPE_ID,VALUE};
  	  
	private HashSet<String> columnHashList;
    
    private void init() {
        columnHashList = new HashSet<String>(columnNames.length);
        for(int i = 0; i < columnNames.length; i++){
            columnHashList.add(path+columnNames[i]);
        }
    }
    
    public AuxFieldValueMeta() {
		init();        
    }
    
    public AuxFieldValueMeta(String path) {
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

    public String getAuxFieldId() {
        return path + AUX_FIELD_ID;
    } 

    public String getTypeId() {
        return path + TYPE_ID;
    } 

    public String getValue() {
        return path + VALUE;
    } 

  
}   
