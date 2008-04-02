package org.openelis.meta;

import java.util.HashMap;

import org.openelis.util.Meta;

public class OrganizationNoteMeta implements Meta{
	private String tableName = "note";
	private String entityName = "organization.note";
	private boolean includeInFrom = true;
	
	public static final String
    ID             		= "note.id",
    REFERENCE_ID		= "note.referenceId",
    REFERENCE_TABLE		= "note.referenceTable",
    TIMESTAMP  			= "note.timestamp",
    IS_EXTERNAL			= "note.isExternal",
    SYSTEM_USER			= "note.systemUser",
    SUBJECT 			= "note.subject",
    TEXT				= "note.text";

	//
	// Array of column names used for building select/insert/update strings
	//
	private static final String[] columnNames = {
	     ID, REFERENCE_ID, REFERENCE_TABLE, TIMESTAMP, IS_EXTERNAL, SYSTEM_USER, SUBJECT, TEXT};
	
	private static HashMap<String, String> columnHashList;
	private static final OrganizationNoteMeta organizationNoteMeta = new OrganizationNoteMeta();
	
	static {
	 columnHashList = new HashMap<String, String>(columnNames.length);
	 for (int i = 0; i < columnNames.length; i++)
	     columnHashList.put(columnNames[i].substring(5), "");
	}

	private OrganizationNoteMeta() {

	}

	public static OrganizationNoteMeta getInstance(){
		return organizationNoteMeta;
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
