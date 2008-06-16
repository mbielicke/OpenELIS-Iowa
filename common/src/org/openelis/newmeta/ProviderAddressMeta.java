
package org.openelis.newmeta;

/**
  * ProviderAddress META Data
  */

import java.util.HashSet;
import org.openelis.gwt.common.NewMeta;

public class ProviderAddressMeta implements NewMeta {
  	private String path = "";
	private static final String entityName = "ProviderAddress";
	
	private static final String
              ID					="id",
              LOCATION					="location",
              EXTERNAL_ID					="externalId",
              PROVIDER_ID					="providerId",
              ADDRESS_ID					="addressId";

  	private static final String[] columnNames = {
  	  ID,LOCATION,EXTERNAL_ID,PROVIDER_ID,ADDRESS_ID};
  	  
	private HashSet<String> columnHashList;
    
    private void init() {
        columnHashList = new HashSet<String>(columnNames.length);
        for(int i = 0; i < columnNames.length; i++){
            columnHashList.add(path+columnNames[i]);
        }
    }
    
    public ProviderAddressMeta() {
		init();        
    }
    
    public ProviderAddressMeta(String path) {
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

    public String getLocation() {
        return path + LOCATION;
    } 

    public String getExternalId() {
        return path + EXTERNAL_ID;
    } 

    public String getProviderId() {
        return path + PROVIDER_ID;
    } 

    public String getAddressId() {
        return path + ADDRESS_ID;
    } 

  
}   
