
package org.openelis.meta;

/**
  * StandardNote META Data
  */

import java.util.HashMap;
import org.openelis.util.Meta;

public class StandardNoteMeta implements Meta {
  	private static final String tableName = "standard_note";
	private static final String entityName = "StandardNote";
	private boolean includeInFrom = true;
	
	public static final String
              ID			= "standard_note.id",
              NAME			= "standard_note.name",
              DESCRIPTION   = "standard_note.description",
              TYPE_ID		= "standard_note.typeId",
              TEXT			= "standard_note.text";


  	private static final String[] columnNames = {
  	  ID,NAME,DESCRIPTION,TYPE_ID,TEXT};
  	  
	private static HashMap<String,String> columnHashList;

	private static final StandardNoteMeta standard_noteMeta = new StandardNoteMeta();
    
    static {
        columnHashList = new HashMap<String,String>(columnNames.length);
        for(int i = 0; i < columnNames.length; i++){
            columnHashList.put(columnNames[i].substring(tableName.length()+1), "");
        }
    }
    
    private StandardNoteMeta() {
        
    }
    
    public static StandardNoteMeta getInstance() {
        return standard_noteMeta;
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

  public static String getTypeId() {
    return TYPE_ID;
  } 

  public static String getText() {
    return TEXT;
  } 

  
}   
