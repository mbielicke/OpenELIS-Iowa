
package org.openelis.meta;

/**
  * TestTrailer META Data
  */

import java.util.HashMap;
import org.openelis.util.Meta;

public class TestTrailerMeta implements Meta {
  	private static final String tableName = "test_trailer";
	private static final String entityName = "TestTrailer";
	private boolean includeInFrom = true;
	
	public static final String
              ID			= "test_trailer.id",
              NAME			= "test_trailer.name",
              DESCRIPTION   = "test_trailer.description",
              TEXT			= "test_trailer.text";


  	private static final String[] columnNames = {
  	  ID,NAME,DESCRIPTION,TEXT};
  	  
	private static HashMap<String,String> columnHashList;

	private static final TestTrailerMeta test_trailerMeta = new TestTrailerMeta();
    
    static {
        columnHashList = new HashMap<String,String>(columnNames.length);
        for(int i = 0; i < columnNames.length; i++){
            columnHashList.put(columnNames[i].substring(tableName.length()+1), "");
        }
    }
    
    private TestTrailerMeta() {
        
    }
    
    public static TestTrailerMeta getInstance() {
        return test_trailerMeta;
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

    public boolean hasColumn(String columnName) {
        if(columnName == null || !columnName.startsWith(tableName))
            return false;
        String column = columnName.substring(tableName.length()+1);
        
        return columnHashList.containsKey(column);
    }

    public boolean includeInFrom() {
        // TODO Auto-generated method stub
        return includeInFrom;
    }
    
    
  public static String getId() {
    return ID;
  } 

  public static String getName() {
    return NAME;
  } 

  public static String getDescription() {
    return DESCRIPTION;
  } 

  public static String getText() {
    return TEXT;
  } 

  
}   
