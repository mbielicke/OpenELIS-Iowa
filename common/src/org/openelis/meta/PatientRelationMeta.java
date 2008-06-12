
package org.openelis.meta;

/**
  * PatientRelation META Data
  */

import java.util.HashMap;
import org.openelis.util.Meta;

public class PatientRelationMeta implements Meta {
  	private static final String tableName = "patient_relation";
	private static final String entityName = "PatientRelation";
	private boolean includeInFrom = true;
	
	public static final String
              ID					="patient_relation.id",
              RELATION_ID					="patient_relation.relationId",
              FROM_PATIENT_ID					="patient_relation.fromPatientId",
              TO_PATIENT_ID					="patient_relation.toPatientId";


  	private static final String[] columnNames = {
  	  ID,RELATION_ID,FROM_PATIENT_ID,TO_PATIENT_ID};
  	  
	private static HashMap<String,String> columnHashList;

	private static final PatientRelationMeta patient_relationMeta = new PatientRelationMeta();
    
    static {
        columnHashList = new HashMap<String,String>(columnNames.length);
        for(int i = 0; i < columnNames.length; i++){
            columnHashList.put(columnNames[i].substring(tableName.length()+1), "");
        }
    }
    
    private PatientRelationMeta() {
        
    }
    
    public static PatientRelationMeta getInstance() {
        return patient_relationMeta;
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

  public static String getRelationId() {
    return RELATION_ID;
  } 

  public static String getFromPatientId() {
    return FROM_PATIENT_ID;
  } 

  public static String getToPatientId() {
    return TO_PATIENT_ID;
  } 

  
}   
