package org.openelis.meta;

import java.util.HashMap;

import org.openelis.util.Meta;

public class OrganizationMeta implements Meta{
	private String tableName = "organization";
	private String entityName = "Organization";
	private boolean includeInFrom = true;
	
	public static final String
     ID               		= "organization.id",
     PARENT_ORGANIZATION_ID	= "organization.parentOrganizationId",
     NAME   				= "organization.name",
     IS_ACTIVE  			= "organization.isActive",
     ADDRESS_ID			    = "organization.addressId";

	//
	// Array of column names used for building select/insert/update strings
	//
	private static final String[] columnNames = {
	     ID, PARENT_ORGANIZATION_ID, NAME, IS_ACTIVE, ADDRESS_ID};
	
	private static HashMap<String,String> columnHashList;
	
	private static final OrganizationMeta organizationMeta = new OrganizationMeta();
	
	static {
	 columnHashList = new HashMap<String, String>(columnNames.length);
	 for (int i = 0; i < columnNames.length; i++)
	     columnHashList.put(columnNames[i].substring(13), "");
	}

	private OrganizationMeta() {

	}
	
	public static OrganizationMeta getInstance(){
		return organizationMeta;
	}
	
	public String[] getColumnList() {
		return columnNames;
	}

	public String getTable() {
		return tableName;
	}
	
	public String getEntity(){
		return entityName;
	}
	
	public boolean includeInFrom(){
		return includeInFrom;
	}
	
	public boolean hasColumn(String columnName){
		if(columnName == null || !columnName.startsWith(tableName))
			return false;
		String column = columnName.substring(tableName.length()+1);
		
		return columnHashList.containsKey(column);
	}
}
