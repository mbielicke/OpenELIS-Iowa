package org.openelis.meta;

import java.util.HashMap;

import org.openelis.util.Meta;

public class OrganizationParentOrganizationMeta implements Meta{
	private String tableName = "parentOrganization";
	private String entityName = "organization.parentOrganization";
	private boolean includeInFrom = true;
	
	public static final String
     ID               		= "parentOrganization.id",
     PARENT_ORGANIZATION_ID	= "parentOrganization.parentOrganizationId",
     NAME   				= "parentOrganization.name",
     IS_ACTIVE  			= "parentOrganization.isActive",
     ADDRESS_ID			    = "parentOrganization.addressId";

	//
	// Array of column names used for building select/insert/update strings
	//
	private static final String[] columnNames = {
	     ID, PARENT_ORGANIZATION_ID, NAME, IS_ACTIVE, ADDRESS_ID};
	
	private static HashMap<String,String> columnHashList;
	
	private static final OrganizationParentOrganizationMeta organizationParentOrganizationMeta = new OrganizationParentOrganizationMeta();
	
	static {
	 columnHashList = new HashMap<String, String>(columnNames.length);
	 for (int i = 0; i < columnNames.length; i++)
	     columnHashList.put(columnNames[i].substring(19), "");
	}

	private OrganizationParentOrganizationMeta() {

	}
	
	public static OrganizationParentOrganizationMeta getInstance(){
		return organizationParentOrganizationMeta;
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
	
	public static String id(){
		return columnNames[0];
	}
	
	public static String idparentOrganizationId(){
		return columnNames[1];
	}
	
	public static String name(){
		return columnNames[2];
	}
	
	public static String isActive(){
		return columnNames[3];
	}
	
	public static String addressId(){
		return columnNames[4];
	}
}
