
package org.openelis.meta;

/**
  * PanelItem META Data
  */

import java.util.HashMap;
import org.openelis.util.Meta;

public class PanelItemMeta implements Meta {
  	private static final String tableName = "panel_item";
	private static final String entityName = "PanelItem";
	private boolean includeInFrom = true;
	
	public static final String
              ID					="panel_item.id",
              PANEL_ID					="panel_item.panel_id",
              SORT_ORDER_ID					="panel_item.sort_order_id",
              TEST_NAME					="panel_item.test_name",
              METHOD_NAME					="panel_item.method_name";


  	private static final String[] columnNames = {
  	  ID,PANEL_ID,SORT_ORDER_ID,TEST_NAME,METHOD_NAME};
  	  
	private static HashMap<String,String> columnHashList;

	private static final PanelItemMeta panel_itemMeta = new PanelItemMeta();
    
    static {
        columnHashList = new HashMap<String,String>(columnNames.length);
        for(int i = 0; i < columnNames.length; i++){
            columnHashList.put(columnNames[i].substring(tableName.length()+1), "");
        }
    }
    
    private PanelItemMeta() {
        
    }
    
    public static PanelItemMeta getInstance() {
        return panel_itemMeta;
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

  public static String getPanelId() {
    return PANEL_ID;
  } 

  public static String getSortOrderId() {
    return SORT_ORDER_ID;
  } 

  public static String getTestName() {
    return TEST_NAME;
  } 

  public static String getMethodName() {
    return METHOD_NAME;
  } 

  
}   
