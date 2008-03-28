package org.openelis.meta;

import java.util.HashMap;

import org.openelis.util.Meta;

public class OrganizationContactMeta implements Meta{
	private String tableName = "organizationContact";
	private String entityName = "organization.organizationContact";
	private boolean includeInFrom = true;
	private boolean collection = true;
	
	public static final String
    ID             	= "organizationContact.id",
    ORGANIZATION	= "organizationContact.organization",
    CONTACT_TYPE	= "organizationContact.contactType",
    NAME  			= "organizationContact.name",
    ADDRESS			= "organizationContact.address";

	//
	// Array of column names used for building select/insert/update strings
	//
	private static final String[] columnNames = {
	     ID, ORGANIZATION, CONTACT_TYPE, NAME, ADDRESS};
	
	private static HashMap<String, String> columnHashList;
	private static final OrganizationContactMeta organizationContactMeta = new OrganizationContactMeta();
	
	static {
	 columnHashList = new HashMap<String, String>(columnNames.length);
	 for (int i = 0; i < columnNames.length; i++)
	     columnHashList.put(columnNames[i].substring(20), "");
	}

	private OrganizationContactMeta() {
		
	}

	public static OrganizationContactMeta getInstance(){
		return organizationContactMeta;
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
	
	public boolean includeInFrom(){
		return includeInFrom;
	}
	
	public boolean isCollection(){
		return collection;
	}

	public boolean hasColumn(String columnName) {
		if(columnName == null || !columnName.startsWith(tableName))
			return false;
		String column = columnName.substring(tableName.length()+1);
		
		return columnHashList.containsKey(column);
	}
}
