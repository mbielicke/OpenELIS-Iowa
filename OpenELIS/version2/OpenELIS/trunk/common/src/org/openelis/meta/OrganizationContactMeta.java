
package org.openelis.meta;

/**
  * OrganizationContact META Data
  */

import java.util.HashMap;
import org.openelis.util.Meta;

public class OrganizationContactMeta implements Meta {
  	private static final String tableName = "organization_contact";
	private static final String entityName = "OrganizationContact";
	private boolean includeInFrom = true;
	
	public static final String
              ID					="organization_contact.id",
              ORGANIZATION_ID					="organization_contact.organizationId",
              CONTACT_TYPE_ID					="organization_contact.contactTypeId",
              NAME					="organization_contact.name",
              ADDRESS_ID					="organization_contact.addressId";


  	private static final String[] columnNames = {
  	  ID,ORGANIZATION_ID,CONTACT_TYPE_ID,NAME,ADDRESS_ID};
  	  
	private static HashMap<String,String> columnHashList;

	private static final OrganizationContactMeta organization_contactMeta = new OrganizationContactMeta();
    
    static {
        columnHashList = new HashMap<String,String>(columnNames.length);
        for(int i = 0; i < columnNames.length; i++){
            columnHashList.put(columnNames[i].substring(tableName.length()+1), "");
        }
    }
    
    private OrganizationContactMeta() {
        
    }
    
    public static OrganizationContactMeta getInstance() {
        return organization_contactMeta;
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

  public static String getOrganizationId() {
    return ORGANIZATION_ID;
  } 

  public static String getContactTypeId() {
    return CONTACT_TYPE_ID;
  } 

  public static String getName() {
    return NAME;
  } 

  public static String getAddressId() {
    return ADDRESS_ID;
  } 

  
}   
