
package org.openelis.meta;

/**
  * Label META Data
  */

import java.util.HashMap;
import org.openelis.util.Meta;

public class LabelMeta implements Meta {
  	private static final String tableName = "label";
	private static final String entityName = "Label";
	private boolean includeInFrom = true;
	
	public static final String
              ID					="label.id",
              NAME					="label.name",
              DESCRIPTION					="label.description",
              PRINTER_TYPE_ID					="label.printer_type_id",
              SCRIPTLET_ID					="label.scriptlet_id";


  	private static final String[] columnNames = {
  	  ID,NAME,DESCRIPTION,PRINTER_TYPE_ID,SCRIPTLET_ID};
  	  
	private static HashMap<String,String> columnHashList;

	private static final LabelMeta labelMeta = new LabelMeta();
    
    static {
        columnHashList = new HashMap<String,String>(columnNames.length);
        for(int i = 0; i < columnNames.length; i++){
            columnHashList.put(columnNames[i].substring(tableName.length()+1), "");
        }
    }
    
    private LabelMeta() {
        
    }
    
    public static LabelMeta getInstance() {
        return labelMeta;
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

  public static String getName() {
    return NAME;
  } 

  public static String getDescription() {
    return DESCRIPTION;
  } 

  public static String getPrinterTypeId() {
    return PRINTER_TYPE_ID;
  } 

  public static String getScriptletId() {
    return SCRIPTLET_ID;
  } 

  
}   
