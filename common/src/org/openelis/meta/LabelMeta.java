
package org.openelis.meta;

/**
  * Label META Data
  */

import java.util.HashSet;
import org.openelis.gwt.common.Meta;

public class LabelMeta implements Meta {
  	private String path = "";
	private static final String entityName = "Label";
	
	private static final String
              ID					="id",
              NAME					="name",
              DESCRIPTION					="description",
              PRINTER_TYPE_ID					="printerTypeId",
              SCRIPTLET_ID					="scriptletId";

  	private static final String[] columnNames = {
  	  ID,NAME,DESCRIPTION,PRINTER_TYPE_ID,SCRIPTLET_ID};
  	  
	private HashSet<String> columnHashList;
    
    private void init() {
        columnHashList = new HashSet<String>(columnNames.length);
        for(int i = 0; i < columnNames.length; i++){
            columnHashList.add(path+columnNames[i]);
        }
    }
    
    public LabelMeta() {
		init();        
    }
    
    public LabelMeta(String path) {
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

    public String getPrinterTypeId() {
        return path + PRINTER_TYPE_ID;
    } 

    public String getScriptletId() {
        return path + SCRIPTLET_ID;
    } 

  
}   
