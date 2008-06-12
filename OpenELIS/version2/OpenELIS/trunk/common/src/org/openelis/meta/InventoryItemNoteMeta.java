package org.openelis.meta;

import java.util.HashMap;

import org.openelis.util.Meta;

public class InventoryItemNoteMeta implements Meta{
	private String tableName = "note";
	private String entityName = "inventory_item.note";
	private boolean includeInFrom = true;
	
	public static final String
    ID             		= "note.id",
    REFERENCE_ID		= "note.referenceId",
    REFERENCE_TABLE_ID	= "note.referenceTableId",
    TIMESTAMP  			= "note.timestamp",
    IS_EXTERNAL			= "note.isExternal",
    SYSTEM_USER_ID		= "note.systemUserId",
    SUBJECT 			= "note.subject",
    TEXT				= "note.text";

	//
	// Array of column names used for building select/insert/update strings
	//
	private static final String[] columnNames = {
	     ID, REFERENCE_ID, REFERENCE_TABLE_ID, TIMESTAMP, IS_EXTERNAL, SYSTEM_USER_ID, SUBJECT, TEXT};
	
	private static HashMap<String, String> columnHashList;
	private static final InventoryItemNoteMeta inventoryItemNoteMeta = new InventoryItemNoteMeta();
	
	static {
	 columnHashList = new HashMap<String, String>(columnNames.length);
	 for (int i = 0; i < columnNames.length; i++)
	     columnHashList.put(columnNames[i].substring(5), "");
	}

	private InventoryItemNoteMeta() {

	}

	public static InventoryItemNoteMeta getInstance(){
		return inventoryItemNoteMeta;
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
	
	public static String getId(){
		return columnNames[0];
	}
	
	public static String getReferenceId(){
		return columnNames[1];
	}
	
	public static String getReferenceTableId(){
		return columnNames[2];
	}
	
	public static String getTimestamp(){
		return columnNames[3];
	}
	
	public static String getIsExternal(){
		return columnNames[4];
	}
	
	public static String getSystemUserId(){
		return columnNames[5];
	}
	
	public static String getSubject(){
		return columnNames[6];
	}
	
	public static String getText(){
		return columnNames[7];
	}

}
