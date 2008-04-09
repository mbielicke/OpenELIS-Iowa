package org.openelis.meta;

import java.util.HashMap;

import org.openelis.util.Meta;

public class AnalyteParentAnalyteMeta implements Meta{
	private String tableName = "parentAnalyte";
	private String entityName = "analyte.parentAnalyte";
	private boolean includeInFrom = true;
	
	public static final String
     ID            		= "parentAnalyte.id",
     NAME				= "parentAnalyte.name",
     IS_ACTIVE   		= "parentAnalyte.isActive",
     ANALYTE_GROUP  	= "parentAnalyte.analyteGroup",
     PARENT_ANALYTE		= "parentAnalyte.parentAnalyte",
     EXTERNAL_ID		= "parentAnalyte.externalId";

	//
	// Array of column names used for building select/insert/update strings
	//
	private static final String[] columnNames = {
	     ID, NAME, IS_ACTIVE, ANALYTE_GROUP, PARENT_ANALYTE, EXTERNAL_ID};
	
	private static HashMap<String,String> columnHashList;
	
	private static final AnalyteParentAnalyteMeta analyteParentAnalyteMeta = new AnalyteParentAnalyteMeta();
	
	static {
	 columnHashList = new HashMap<String, String>(columnNames.length);
	 for (int i = 0; i < columnNames.length; i++)
	     columnHashList.put(columnNames[i].substring(14), "");
	}

	private AnalyteParentAnalyteMeta() {

	}
	
	public static AnalyteParentAnalyteMeta getInstance(){
		return analyteParentAnalyteMeta;
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
