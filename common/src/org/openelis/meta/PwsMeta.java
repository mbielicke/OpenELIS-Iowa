
package org.openelis.meta;

/**
  * Pws META Data
  */

import java.util.HashSet;
import org.openelis.gwt.common.Meta;

public class PwsMeta implements Meta {
  	private String path = "";
	private static final String entityName = "Pws";
	
	private static final String
              ID					="id",
              TINWSYS_IS_NUMBER					="tinwsysIsNumber",
              NUMBER0					="number0",
              ALTERNATE_ST_NUM					="alternateStNum",
              NAME					="name",
              ACTIVITY_STATUS_CD					="activityStatusCd",
              D_PRIN_CITY_SVD_NM					="dPrinCitySvdNm",
              D_PRIN_CNTY_SVD_NM					="dPrinCntySvdNm",
              D_POPULATION_COUNT					="dPopulationCount",
              D_PWS_ST_TYPE_CD					="dPwsStTypeCd",
              ACTIVITY_RSN_TXT					="activityRsnTxt",
              START_DAY					="startDay",
              START_MONTH					="startMonth",
              END_DAY					="endDay",
              END_MONTH					="endMonth",
              EFF_BEGIN_DT					="effBeginDt",
              EFF_END_DT					="effEndDt";

  	private static final String[] columnNames = {
  	  ID,TINWSYS_IS_NUMBER,NUMBER0,ALTERNATE_ST_NUM,NAME,ACTIVITY_STATUS_CD,D_PRIN_CITY_SVD_NM,D_PRIN_CNTY_SVD_NM,D_POPULATION_COUNT,D_PWS_ST_TYPE_CD,ACTIVITY_RSN_TXT,START_DAY,START_MONTH,END_DAY,END_MONTH,EFF_BEGIN_DT,EFF_END_DT};
  	  
	private HashSet<String> columnHashList;
    
    private void init() {
        columnHashList = new HashSet<String>(columnNames.length);
        for(int i = 0; i < columnNames.length; i++){
            columnHashList.add(path+columnNames[i]);
        }
    }
    
    public PwsMeta() {
		init();        
    }
    
    public PwsMeta(String path) {
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

    public String getNumber0() {
        return path + NUMBER0;
    } 

    public String getAlternateStNum() {
        return path + ALTERNATE_ST_NUM;
    } 

    public String getName() {
        return path + NAME;
    } 

    public String getActivityStatusCd() {
        return path + ACTIVITY_STATUS_CD;
    } 

    public String getDPrinCitySvdNm() {
        return path + D_PRIN_CITY_SVD_NM;
    } 

    public String getDPrinCntySvdNm() {
        return path + D_PRIN_CNTY_SVD_NM;
    } 

    public String getDPopulationCount() {
        return path + D_POPULATION_COUNT;
    } 

    public String getDPwsStTypeCd() {
        return path + D_PWS_ST_TYPE_CD;
    } 

    public String getActivityRsnTxt() {
        return path + ACTIVITY_RSN_TXT;
    } 

    public String getStartDay() {
        return path + START_DAY;
    } 

    public String getStartMonth() {
        return path + START_MONTH;
    } 

    public String getEndDay() {
        return path + END_DAY;
    } 

    public String getEndMonth() {
        return path + END_MONTH;
    } 

    public String getEffBeginDt() {
        return path + EFF_BEGIN_DT;
    } 

    public String getEffEndDt() {
        return path + EFF_END_DT;
    } 

  
}   
