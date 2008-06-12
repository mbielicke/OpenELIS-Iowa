
package org.openelis.meta;

/**
  * Provider META Data
  */

import java.util.HashMap;
import org.openelis.util.Meta;

public class ProviderMeta implements Meta {
  	private static final String tableName = "provider";
	private static final String entityName = "Provider";
	private boolean includeInFrom = true;
	
	public static final String
              ID					="provider.id",
              LAST_NAME					="provider.lastName",
              FIRST_NAME					="provider.firstName",
              MIDDLE_NAME					="provider.middleName",
              TYPE_ID					="provider.typeId",
              NPI					="provider.npi";


  	private static final String[] columnNames = {
  	  ID,LAST_NAME,FIRST_NAME,MIDDLE_NAME,TYPE_ID,NPI};
  	  
	private static HashMap<String,String> columnHashList;

	private static final ProviderMeta providerMeta = new ProviderMeta();
    
    static {
        columnHashList = new HashMap<String,String>(columnNames.length);
        for(int i = 0; i < columnNames.length; i++){
            columnHashList.put(columnNames[i].substring(tableName.length()+1), "");
        }
    }
    
    private ProviderMeta() {
        
    }
    
    public static ProviderMeta getInstance() {
        return providerMeta;
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

  public static String getLastName() {
    return LAST_NAME;
  } 

  public static String getFirstName() {
    return FIRST_NAME;
  } 

  public static String getMiddleName() {
    return MIDDLE_NAME;
  } 

  public static String getTypeId() {
    return TYPE_ID;
  } 

  public static String getNpi() {
    return NPI;
  } 

  
}   
