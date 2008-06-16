
package org.openelis.newmeta;

/**
  * PatientRelation META Data
  */

import java.util.HashSet;
import org.openelis.gwt.common.NewMeta;

public class PatientRelationMeta implements NewMeta {
  	private String path = "";
	private static final String entityName = "PatientRelation";
	
	private static final String
              ID					="id",
              RELATION_ID					="relationId",
              FROM_PATIENT_ID					="fromPatientId",
              TO_PATIENT_ID					="toPatientId";

  	private static final String[] columnNames = {
  	  ID,RELATION_ID,FROM_PATIENT_ID,TO_PATIENT_ID};
  	  
	private HashSet<String> columnHashList;
    
    private void init() {
        columnHashList = new HashSet<String>(columnNames.length);
        for(int i = 0; i < columnNames.length; i++){
            columnHashList.add(path+columnNames[i]);
        }
    }
    
    public PatientRelationMeta() {
		init();        
    }
    
    public PatientRelationMeta(String path) {
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

    public String getRelationId() {
        return path + RELATION_ID;
    } 

    public String getFromPatientId() {
        return path + FROM_PATIENT_ID;
    } 

    public String getToPatientId() {
        return path + TO_PATIENT_ID;
    } 

  
}   
