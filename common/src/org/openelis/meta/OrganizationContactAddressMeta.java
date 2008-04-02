package org.openelis.meta;

import java.util.HashMap;

import org.openelis.util.Meta;

public class OrganizationContactAddressMeta implements Meta{
	private String tableName = "organizationContact.address";
	private String entityName = "organizationContact.address";
	private boolean includeInFrom = false;
	
	public static final String
    ID             	= "organizationContact.address.id",
    MULTIPLE_UNIT	= "organizationContact.address.multipleUnit",
    STREET_ADDRESS	= "organizationContact.address.streetAddress",
    CITY  			= "organizationContact.address.city",
    STATE			= "organizationContact.address.state",
    ZIP_CODE 		= "organizationContact.address.zipCode",
    WORK_PHONE 		= "organizationContact.address.workPhone",
    HOME_PHONE 		= "organizationContact.address.homePhone",
    CELL_PHONE 		= "organizationContact.address.cellPhone",
    FAX_PHONE 		= "organizationContact.address.faxPhone",
    EMAIL 			= "organizationContact.address.email",
    COUNTRY 		= "organizationContact.address.country";

	//
	// Array of column names used for building select/insert/update strings
	//
	private static final String[] columnNames = {
	     ID, MULTIPLE_UNIT, STREET_ADDRESS, CITY, STATE, ZIP_CODE, WORK_PHONE, HOME_PHONE,
	     CELL_PHONE, FAX_PHONE, EMAIL, COUNTRY};
		
	private static HashMap<String, String> columnHashList;
	private static final OrganizationContactAddressMeta organizationContactAddressMeta = new OrganizationContactAddressMeta();
	
	static {
	 columnHashList = new HashMap<String, String>(columnNames.length);
	 for (int i = 0; i < columnNames.length; i++)
	     columnHashList.put(columnNames[i].substring(28), "");
	}
	
	private OrganizationContactAddressMeta() {
	
	}

	public static OrganizationContactAddressMeta getInstance(){
		return organizationContactAddressMeta;
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
	
	public boolean hasColumn(String columnName) {
		if(columnName == null || !columnName.startsWith(tableName))
			return false;
		String column = columnName.substring(tableName.length()+1);
		
		return columnHashList.containsKey(column);
	}
}
