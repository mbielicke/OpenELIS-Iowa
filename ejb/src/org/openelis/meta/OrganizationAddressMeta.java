package org.openelis.meta;

import java.util.HashMap;

import org.openelis.util.Meta;

public class OrganizationAddressMeta implements Meta{
	private String tableName = "organization.address";
	private String entityName = "Organization.address";
	private boolean includeInFrom = false;
	private boolean collection = false;
	
	public static final String
    ID             	= "organization.address.id",
    MULTIPLE_UNIT	= "organization.address.multipleUnit",
    STREET_ADDRESS	= "organization.address.streetAddress",
    CITY  			= "organization.address.city",
    STATE			= "organization.address.state",
    ZIP_CODE 		= "organization.address.zipCode",
    WORK_PHONE 		= "organization.address.workPhone",
    HOME_PHONE 		= "organization.address.homePhone",
    CELL_PHONE 		= "organization.address.cellPhone",
    FAX_PHONE 		= "organization.address.faxPhone",
    EMAIL 			= "organization.address.email",
    COUNTRY 		= "organization.address.country";

	//
	// Array of column names used for building select/insert/update strings
	//
	private static final String[] columnNames = {
	     ID, MULTIPLE_UNIT, STREET_ADDRESS, CITY, STATE, ZIP_CODE, WORK_PHONE, HOME_PHONE,
	     CELL_PHONE, FAX_PHONE, EMAIL, COUNTRY};
	
	private static HashMap<String, String> columnHashList;
	private static final OrganizationAddressMeta organizationAddressMeta = new OrganizationAddressMeta();
	
	static {
	 columnHashList = new HashMap<String, String>(columnNames.length);
	 for (int i = 0; i < columnNames.length; i++)
	     columnHashList.put(columnNames[i].substring(21), "");
	}

	private OrganizationAddressMeta() {

	}

	public static OrganizationAddressMeta getInstance(){
		return organizationAddressMeta;
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
