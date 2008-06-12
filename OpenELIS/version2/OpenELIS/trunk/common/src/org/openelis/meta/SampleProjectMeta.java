
package org.openelis.meta;

/**
  * SampleProject META Data
  */

import java.util.HashMap;
import org.openelis.util.Meta;

public class SampleProjectMeta implements Meta {
  	private static final String tableName = "sample_project";
	private static final String entityName = "SampleProject";
	private boolean includeInFrom = true;
	
	public static final String
              ID					="sample_project.id",
              SAMPLE_ID					="sample_project.sampleId",
              PROJECT_ID					="sample_project.projectId",
              IS_PERMANENT					="sample_project.isPermanent";


  	private static final String[] columnNames = {
  	  ID,SAMPLE_ID,PROJECT_ID,IS_PERMANENT};
  	  
	private static HashMap<String,String> columnHashList;

	private static final SampleProjectMeta sample_projectMeta = new SampleProjectMeta();
    
    static {
        columnHashList = new HashMap<String,String>(columnNames.length);
        for(int i = 0; i < columnNames.length; i++){
            columnHashList.put(columnNames[i].substring(tableName.length()+1), "");
        }
    }
    
    private SampleProjectMeta() {
        
    }
    
    public static SampleProjectMeta getInstance() {
        return sample_projectMeta;
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

  public static String getSampleId() {
    return SAMPLE_ID;
  } 

  public static String getProjectId() {
    return PROJECT_ID;
  } 

  public static String getIsPermanent() {
    return IS_PERMANENT;
  } 

  
}   
