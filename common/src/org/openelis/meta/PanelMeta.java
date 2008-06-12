
package org.openelis.meta;

/**
  * Panel META Data
  */

import java.util.HashMap;
import org.openelis.util.Meta;

public class PanelMeta implements Meta {
  	private static final String tableName = "panel";
	private static final String entityName = "Panel";
	private boolean includeInFrom = true;
	
	public static final String
              ID					="panel.id",
              NAME					="panel.name",
              DESCRIPTION					="panel.description";


  	private static final String[] columnNames = {
  	  ID,NAME,DESCRIPTION};
  	  
	private static HashMap<String,String> columnHashList;

	private static final PanelMeta panelMeta = new PanelMeta();
    
    static {
        columnHashList = new HashMap<String,String>(columnNames.length);
        for(int i = 0; i < columnNames.length; i++){
            columnHashList.put(columnNames[i].substring(tableName.length()+1), "");
        }
    }
    
    private PanelMeta() {
        
    }
    
    public static PanelMeta getInstance() {
        return panelMeta;
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

  
}   
