
package org.openelis.meta;

/**
  * Patient META Data
  */

import java.util.HashMap;
import org.openelis.util.Meta;

public class PatientMeta implements Meta {
  	private static final String tableName = "patient";
	private static final String entityName = "Patient";
	private boolean includeInFrom = true;
	
	public static final String
              ID					="patient.id",
              LAST_NAME					="patient.lastName",
              FIRST_NAME					="patient.firstName",
              MIDDLE_NAME					="patient.middleName",
              ADDRESS_ID					="patient.addressId",
              BIRTH_DATE					="patient.birthDate",
              BIRTH_TIME					="patient.birthTime",
              GENDER_ID					="patient.genderId",
              RACE					="patient.race",
              ETHNICITY_ID					="patient.ethnicityId";


  	private static final String[] columnNames = {
  	  ID,LAST_NAME,FIRST_NAME,MIDDLE_NAME,ADDRESS_ID,BIRTH_DATE,BIRTH_TIME,GENDER_ID,RACE,ETHNICITY_ID};
  	  
	private static HashMap<String,String> columnHashList;

	private static final PatientMeta patientMeta = new PatientMeta();
    
    static {
        columnHashList = new HashMap<String,String>(columnNames.length);
        for(int i = 0; i < columnNames.length; i++){
            columnHashList.put(columnNames[i].substring(tableName.length()+1), "");
        }
    }
    
    private PatientMeta() {
        
    }
    
    public static PatientMeta getInstance() {
        return patientMeta;
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

  public static String getLastName() {
    return LAST_NAME;
  } 

  public static String getFirstName() {
    return FIRST_NAME;
  } 

  public static String getMiddleName() {
    return MIDDLE_NAME;
  } 

  public static String getAddressId() {
    return ADDRESS_ID;
  } 

  public static String getBirthDate() {
    return BIRTH_DATE;
  } 

  public static String getBirthTime() {
    return BIRTH_TIME;
  } 

  public static String getGenderId() {
    return GENDER_ID;
  } 

  public static String getRace() {
    return RACE;
  } 

  public static String getEthnicityId() {
    return ETHNICITY_ID;
  } 

  
}   
