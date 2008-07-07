/**
* The contents of this file are subject to the Mozilla Public License
* Version 1.1 (the "License"); you may not use this file except in
* compliance with the License. You may obtain a copy of the License at
* http://www.mozilla.org/MPL/
* 
* Software distributed under the License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
* License for the specific language governing rights and limitations under
* the License.
* 
* The Original Code is OpenELIS code.
* 
* Copyright (C) OpenELIS.  All Rights Reserved.
*/

package org.openelis.meta;

/**
  * Patient META Data
  */

import java.util.HashSet;
import org.openelis.gwt.common.Meta;

public class PatientMeta implements Meta {
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
