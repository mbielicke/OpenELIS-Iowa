
package org.openelis.meta;

/**
  * ReferenceTable META Data
  */

import java.util.HashMap;
import org.openelis.util.Meta;

public class ReferenceTableMeta implements Meta {
  	private static final String tableName = "reference_table";
	private static final String entityName = "ReferenceTable";
	private boolean includeInFrom = true;
	
	public static final String
              ID					="reference_table.id",
              NAME					="reference_table.name";


  	private static final String[] columnNames = {
  	  ID,NAME};
  	  
	private static HashMap<String,String> columnHashList;

	private static final ReferenceTableMeta reference_tableMeta = new ReferenceTableMeta();
    
    static {
        columnHashList = new HashMap<String,String>(columnNames.length);
        for(int i = 0; i < columnNames.length; i++){
            columnHashList.put(columnNames[i].substring(tableName.length()+1), "");
        }
    }
    
    private ReferenceTableMeta() {
        
    }
    
    public static ReferenceTableMeta getInstance() {
        return reference_tableMeta;
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

  
}   
