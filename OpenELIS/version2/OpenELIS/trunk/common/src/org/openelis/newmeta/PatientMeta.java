
package org.openelis.newmeta;

/**
  * Patient META Data
  */

import java.util.HashSet;
import org.openelis.gwt.common.NewMeta;

public class PatientMeta implements NewMeta {
  	private String path = "";
	private static final String entityName = "Patient";
	
	private static final String
              ID					="id",
              LAST_NAME					="lastName",
              FIRST_NAME					="firstName",
              MIDDLE_NAME					="middleName",
              ADDRESS_ID					="addressId",
              BIRTH_DATE					="birthDate",
              BIRTH_TIME					="birthTime",
              GENDER_ID					="genderId",
              RACE					="race",
              ETHNICITY_ID					="ethnicityId";

  	private static final String[] columnNames = {
  	  ID,LAST_NAME,FIRST_NAME,MIDDLE_NAME,ADDRESS_ID,BIRTH_DATE,BIRTH_TIME,GENDER_ID,RACE,ETHNICITY_ID};
  	  
	private HashSet<String> columnHashList;
    
    private void init() {
        columnHashList = new HashSet<String>(columnNames.length);
        for(int i = 0; i < columnNames.length; i++){
            columnHashList.add(path+columnNames[i]);
        }
    }
    
    public PatientMeta() {
		init();        
    }
    
    public PatientMeta(String path) {
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

    public String getLastName() {
        return path + LAST_NAME;
    } 

    public String getFirstName() {
        return path + FIRST_NAME;
    } 

    public String getMiddleName() {
        return path + MIDDLE_NAME;
    } 

    public String getAddressId() {
        return path + ADDRESS_ID;
    } 

    public String getBirthDate() {
        return path + BIRTH_DATE;
    } 

    public String getBirthTime() {
        return path + BIRTH_TIME;
    } 

    public String getGenderId() {
        return path + GENDER_ID;
    } 

    public String getRace() {
        return path + RACE;
    } 

    public String getEthnicityId() {
        return path + ETHNICITY_ID;
    } 

  
}   
