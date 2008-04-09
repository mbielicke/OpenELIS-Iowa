package org.openelis.meta;

import java.util.HashMap;

import org.openelis.util.Meta;

public class TestTrailerMeta implements Meta{
	private String tableName = "testTrailer";
	private String entityName = "TestTrailer";
	private boolean includeInFrom = true;
	
	public static final String
     ID            	= "testTrailer.id",
     NAME			= "testTrailer.name",
     DESCRIPTION   	= "testTrailer.description",
     TEXT  			= "testTrailer.text";

	//
	// Array of column names used for building select/insert/update strings
	//
	private static final String[] columnNames = {
	     ID, NAME, DESCRIPTION, TEXT};
	
	private static HashMap<String,String> columnHashList;
	
	private static final TestTrailerMeta testTrailerMeta = new TestTrailerMeta();
	
	static {
	 columnHashList = new HashMap<String, String>(columnNames.length);
	 for (int i = 0; i < columnNames.length; i++)
	     columnHashList.put(columnNames[i].substring(12), "");
	}

	private TestTrailerMeta() {

	}
	
	public static TestTrailerMeta getInstance(){
		return testTrailerMeta;
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
    
    public static String name(){
        return columnNames[1];
    }
    
    public static String description(){
        return columnNames[2];
    }
    
    public static String text(){
        return columnNames[3];
    }
}
