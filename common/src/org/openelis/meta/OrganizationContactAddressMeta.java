package org.openelis.meta;

import java.util.HashMap;

import org.openelis.util.Meta;

public class OrganizationContactAddressMeta implements Meta{
	private static final String tableName = "organization_contact.address";
	private static final String entityName = "organization_contact.address";
	private boolean includeInFrom = false;
	
	public static final String
    ID             	= "organization_contact.address.id",
    MULTIPLE_UNIT	= "organization_contact.address.multipleUnit",
    STREET_ADDRESS	= "organization_contact.address.streetAddress",
    CITY  			= "organization_contact.address.city",
    STATE			= "organization_contact.address.state",
    ZIP_CODE 		= "organization_contact.address.zipCode",
    WORK_PHONE 		= "organization_contact.address.workPhone",
    HOME_PHONE 		= "organization_contact.address.homePhone",
    CELL_PHONE 		= "organization_contact.address.cellPhone",
    FAX_PHONE 		= "organization_contact.address.faxPhone",
    EMAIL 			= "organization_contact.address.email",
    COUNTRY 		= "organization_contact.address.country";

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
	     columnHashList.put(columnNames[i].substring(tableName.length()+1), "");
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
	
	public static String id(){
		return columnNames[0];
	}
	
	public static String multipleUnit(){
		return columnNames[1];
	}
	
	public static String streetAddress(){
		return columnNames[2];
	}
	
	public static String city(){
		return columnNames[3];
	}
	
	public static String state(){
		return columnNames[4];
	}
	
	public static String zipCode(){
		return columnNames[5];
	}
	
	public static String workPhone(){
		return columnNames[6];
	}
	
	public static String homePhone(){
		return columnNames[7];
	}
	
	public static String cellPhone(){
		return columnNames[8];
	}
	
	public static String faxPhone(){
		return columnNames[9];
	}
	
	public static String email(){
		return columnNames[10];
	}
	
	public static String country(){
		return columnNames[11];
	}
}
