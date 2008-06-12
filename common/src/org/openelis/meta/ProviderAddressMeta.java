
package org.openelis.meta;

/**
  * ProviderAddress META Data
  */

import java.util.HashMap;
import org.openelis.util.Meta;

public class ProviderAddressMeta implements Meta {
  	private static final String tableName = "provider_address";
	private static final String entityName = "ProviderAddress";
	private boolean includeInFrom = true;
	
	public static final String
              ID					="provider_address.id",
              LOCATION					="provider_address.location",
              EXTERNAL_ID					="provider_address.externalId",
              PROVIDER_ID					="provider_address.providerId",
              ADDRESS_ID					="provider_address.addressId";


  	private static final String[] columnNames = {
  	  ID,LOCATION,EXTERNAL_ID,PROVIDER_ID,ADDRESS_ID};
  	  
	private static HashMap<String,String> columnHashList;

	private static final ProviderAddressMeta provider_addressMeta = new ProviderAddressMeta();
    
    static {
        columnHashList = new HashMap<String,String>(columnNames.length);
        for(int i = 0; i < columnNames.length; i++){
            columnHashList.put(columnNames[i].substring(tableName.length()+1), "");
        }
    }
    
    private ProviderAddressMeta() {
        
    }
    
    public static ProviderAddressMeta getInstance() {
        return provider_addressMeta;
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

  public static String getLocation() {
    return LOCATION;
  } 

  public static String getExternalId() {
    return EXTERNAL_ID;
  } 

  public static String getProviderId() {
    return PROVIDER_ID;
  } 

  public static String getAddressId() {
    return ADDRESS_ID;
  } 

  
}   
