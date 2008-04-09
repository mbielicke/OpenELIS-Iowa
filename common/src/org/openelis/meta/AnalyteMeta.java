package org.openelis.meta;

import java.util.HashMap;

import org.openelis.util.Meta;

public class AnalyteMeta implements Meta{
	private String tableName = "analyte";
	private String entityName = "Analyte";
	private boolean includeInFrom = true;
	
	public static final String
     ID            		= "analyte.id",
     NAME				= "analyte.name",
     IS_ACTIVE   		= "analyte.isActive",
     ANALYTE_GROUP  	= "analyte.analyteGroup",
     PARENT_ANALYTE		= "analyte.parentAnalyte",
     EXTERNAL_ID		= "analyte.externalId";

	//
	// Array of column names used for building select/insert/update strings
	//
	private static final String[] columnNames = {
	     ID, NAME, IS_ACTIVE, ANALYTE_GROUP, PARENT_ANALYTE, EXTERNAL_ID};
	
	private static HashMap<String,String> columnHashList;
	
	private static final AnalyteMeta analyteMeta = new AnalyteMeta();
	
	static {
	 columnHashList = new HashMap<String, String>(columnNames.length);
	 for (int i = 0; i < columnNames.length; i++)
	     columnHashList.put(columnNames[i].substring(8), "");
	}

	private AnalyteMeta() {

	}
	
	public static AnalyteMeta getInstance(){
		return analyteMeta;
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
    
    public static String isActive(){
        return columnNames[2];
    }
    
    public static String analyteGroup(){
        return columnNames[3];
    }
    
    public static String parentAnalyte(){
        return columnNames[4];
    }
    
    public static String externalId(){
    	return columnNames[5];
    }
}
