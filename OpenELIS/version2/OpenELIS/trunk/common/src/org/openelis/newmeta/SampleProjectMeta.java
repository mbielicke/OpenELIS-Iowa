
package org.openelis.newmeta;

/**
  * SampleProject META Data
  */

import java.util.HashSet;
import org.openelis.gwt.common.NewMeta;

public class SampleProjectMeta implements NewMeta {
  	private String path = "";
	private static final String entityName = "SampleProject";
	
	private static final String
              ID					="id",
              SAMPLE_ID					="sampleId",
              PROJECT_ID					="projectId",
              IS_PERMANENT					="isPermanent";

  	private static final String[] columnNames = {
  	  ID,SAMPLE_ID,PROJECT_ID,IS_PERMANENT};
  	  
	private HashSet<String> columnHashList;
    
    private void init() {
        columnHashList = new HashSet<String>(columnNames.length);
        for(int i = 0; i < columnNames.length; i++){
            columnHashList.add(path+columnNames[i]);
        }
    }
    
    public SampleProjectMeta() {
		init();        
    }
    
    public SampleProjectMeta(String path) {
        this.path = path;
		init();        
    }

    public String[] getColumnList() {
        return columnNames;
    }

    public String getEntity() {
        return entityName;
    }

    public boolean hasColumn(String columnName) {
        return columnHashList.contains(columnName);
    }
    
    
    public String getId() {
        return path + ID;
    } 

    public String getSampleId() {
        return path + SAMPLE_ID;
    } 

    public String getProjectId() {
        return path + PROJECT_ID;
    } 

    public String getIsPermanent() {
        return path + IS_PERMANENT;
    } 

  
}   
