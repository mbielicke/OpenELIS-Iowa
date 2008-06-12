
package org.openelis.meta;

/**
  * Organization META Data
  */

import java.util.HashMap;
import org.openelis.util.Meta;

public class OrganizationMeta implements Meta {
  	private static final String tableName = "organization";
	private static final String entityName = "Organization";
	private boolean includeInFrom = true;
	
	public static final String
              ID					="organization.id",
              PARENT_ORGANIZATION_ID					="organization.parentOrganizationId",
              NAME					="organization.name",
              IS_ACTIVE					="organization.isActive",
              ADDRESS_ID					="organization.addressId";


  	private static final String[] columnNames = {
  	  ID,PARENT_ORGANIZATION_ID,NAME,IS_ACTIVE,ADDRESS_ID};
  	  
	private static HashMap<String,String> columnHashList;

	private static final OrganizationMeta organizationMeta = new OrganizationMeta();
    
    static {
        columnHashList = new HashMap<String,String>(columnNames.length);
        for(int i = 0; i < columnNames.length; i++){
            columnHashList.put(columnNames[i].substring(tableName.length()+1), "");
        }
    }
    
    private OrganizationMeta() {
        
    }
    
    public static OrganizationMeta getInstance() {
        return organizationMeta;
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

  public static String getParentOrganizationId() {
    return PARENT_ORGANIZATION_ID;
  } 

  public static String getName() {
    return NAME;
  } 

  public static String getIsActive() {
    return IS_ACTIVE;
  } 

  public static String getAddressId() {
    return ADDRESS_ID;
  } 

  
}   
