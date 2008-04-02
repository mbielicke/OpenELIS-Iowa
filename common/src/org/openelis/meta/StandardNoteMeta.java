package org.openelis.meta;

import java.util.HashMap;

import org.openelis.util.Meta;

public class StandardNoteMeta implements Meta{
	private String tableName = "standardNote";
	private String entityName = "StandardNote";
	private boolean includeInFrom = true;
	
	public static final String
     ID             = "standardNote.id",
     NAME			= "standardNote.name",
     DESCRIPTION   	= "standardNote.description",
     TYPE  			= "standardNote.type",
     TEXT			= "standardNote.text";

	//
	// Array of column names used for building select/insert/update strings
	//
	private static final String[] columnNames = {
	     ID, NAME, DESCRIPTION, TYPE, TEXT};
	
	private static HashMap<String,String> columnHashList;
	
	private static final StandardNoteMeta standardNoteMeta = new StandardNoteMeta();
	
	static {
	 columnHashList = new HashMap<String, String>(columnNames.length);
	 for (int i = 0; i < columnNames.length; i++)
	     columnHashList.put(columnNames[i].substring(13), "");
	}

	private StandardNoteMeta() {

	}
	
	public static StandardNoteMeta getInstance(){
		return standardNoteMeta;
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
