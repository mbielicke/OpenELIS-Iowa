
package org.openelis.meta;

/**
  * PwsAddress META Data
  */

import java.util.HashSet;
import org.openelis.gwt.common.Meta;

public class PwsAddressMeta implements Meta {
  	private String path = "";
	private static final String entityName = "PwsAddress";
	
	private static final String
              ID					="id",
              TINWSYS_IS_NUMBER					="tinwsysIsNumber",
              TYPE_CODE					="typeCode",
              ACTIVE_IND_CD					="activeIndCd",
              NAME					="name",
              ADDRESS_LINE_ONE_TEXT					="addressLineOneText",
              ADDRESS_LINE_TWO_TEXT					="addressLineTwoText",
              ADDRESS_CITY_NAME					="addressCityName",
              ADDRESS_STATE_CODE					="addressStateCode",
              ADDRESS_ZIP_CODE					="addressZipCode",
              STATE_FIPS_CODE					="stateFipsCode",
              PHONE_NUMBER					="phoneNumber";

  	private static final String[] columnNames = {
  	  ID,TINWSYS_IS_NUMBER,TYPE_CODE,ACTIVE_IND_CD,NAME,ADDRESS_LINE_ONE_TEXT,ADDRESS_LINE_TWO_TEXT,ADDRESS_CITY_NAME,ADDRESS_STATE_CODE,ADDRESS_ZIP_CODE,STATE_FIPS_CODE,PHONE_NUMBER};
  	  
	private HashSet<String> columnHashList;
    
    private void init() {
        columnHashList = new HashSet<String>(columnNames.length);
        for(int i = 0; i < columnNames.length; i++){
            columnHashList.add(path+columnNames[i]);
        }
    }
    
    public PwsAddressMeta() {
		init();        
    }
    
    public PwsAddressMeta(String path) {
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

    public String getTinwsysIsNumber() {
        return path + TINWSYS_IS_NUMBER;
    } 

    public String getTypeCode() {
        return path + TYPE_CODE;
    } 

    public String getActiveIndCd() {
        return path + ACTIVE_IND_CD;
    } 

    public String getName() {
        return path + NAME;
    } 

    public String getAddressLineOneText() {
        return path + ADDRESS_LINE_ONE_TEXT;
    } 

    public String getAddressLineTwoText() {
        return path + ADDRESS_LINE_TWO_TEXT;
    } 

    public String getAddressCityName() {
        return path + ADDRESS_CITY_NAME;
    } 

    public String getAddressStateCode() {
        return path + ADDRESS_STATE_CODE;
    } 

    public String getAddressZipCode() {
        return path + ADDRESS_ZIP_CODE;
    } 

    public String getStateFipsCode() {
        return path + STATE_FIPS_CODE;
    } 

    public String getPhoneNumber() {
        return path + PHONE_NUMBER;
    } 

  
}   
