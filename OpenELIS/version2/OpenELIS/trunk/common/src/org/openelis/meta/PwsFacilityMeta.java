
package org.openelis.meta;

/**
  * PwsFacility META Data
  */

import java.util.HashSet;
import org.openelis.gwt.common.Meta;

public class PwsFacilityMeta implements Meta {
  	private String path = "";
	private static final String entityName = "PwsFacility";
	
	private static final String
              ID					="id",
              TINWSYS_IS_NUMBER					="tinwsysIsNumber",
              NAME					="name",
              TYPE_CODE					="typeCode",
              ST_ASGN_IDENT_CD					="stAsgnIdentCd",
              ACTIVITY_STATUS_CD					="activityStatusCd",
              WATER_TYPE_CODE					="waterTypeCode",
              AVAILABILITY_CODE					="availabilityCode",
              IDENTIFICATION_CD					="identificationCd",
              DESCRIPTION_TEXT					="descriptionText",
              SOURCE_TYPE_CODE					="sourceTypeCode";

  	private static final String[] columnNames = {
  	  ID,TINWSYS_IS_NUMBER,NAME,TYPE_CODE,ST_ASGN_IDENT_CD,ACTIVITY_STATUS_CD,WATER_TYPE_CODE,AVAILABILITY_CODE,IDENTIFICATION_CD,DESCRIPTION_TEXT,SOURCE_TYPE_CODE};
  	  
	private HashSet<String> columnHashList;
    
    private void init() {
        columnHashList = new HashSet<String>(columnNames.length);
        for(int i = 0; i < columnNames.length; i++){
            columnHashList.add(path+columnNames[i]);
        }
    }
    
    public PwsFacilityMeta() {
		init();        
    }
    
    public PwsFacilityMeta(String path) {
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

    public String getName() {
        return path + NAME;
    } 

    public String getTypeCode() {
        return path + TYPE_CODE;
    } 

    public String getStAsgnIdentCd() {
        return path + ST_ASGN_IDENT_CD;
    } 

    public String getActivityStatusCd() {
        return path + ACTIVITY_STATUS_CD;
    } 

    public String getWaterTypeCode() {
        return path + WATER_TYPE_CODE;
    } 

    public String getAvailabilityCode() {
        return path + AVAILABILITY_CODE;
    } 

    public String getIdentificationCd() {
        return path + IDENTIFICATION_CD;
    } 

    public String getDescriptionText() {
        return path + DESCRIPTION_TEXT;
    } 

    public String getSourceTypeCode() {
        return path + SOURCE_TYPE_CODE;
    } 

  
}   
