
package org.openelis.meta;

/**
  * PwsMonitor META Data
  */

import java.util.HashSet;
import org.openelis.gwt.common.Meta;

public class PwsMonitorMeta implements Meta {
  	private String path = "";
	private static final String entityName = "PwsMonitor";
	
	private static final String
              ID					="id",
              TINWSYS_IS_NUMBER					="tinwsysIsNumber",
              ST_ASGN_IDENT_CD					="stAsgnIdentCd",
              NAME					="name",
              TIAANLGP_TIAANLYT_NAME					="tiaanlgpTiaanlytName",
              NUMBER_SAMPLES					="numberSamples",
              COMP_BEGIN_DATE					="compBeginDate",
              COMP_END_DATE					="compEndDate",
              FREQUENCY_NAME					="frequencyName",
              PERIOD_NAME					="periodName";

  	private static final String[] columnNames = {
  	  ID,TINWSYS_IS_NUMBER,ST_ASGN_IDENT_CD,NAME,TIAANLGP_TIAANLYT_NAME,NUMBER_SAMPLES,COMP_BEGIN_DATE,COMP_END_DATE,FREQUENCY_NAME,PERIOD_NAME};
  	  
	private HashSet<String> columnHashList;
    
    private void init() {
        columnHashList = new HashSet<String>(columnNames.length);
        for(int i = 0; i < columnNames.length; i++){
            columnHashList.add(path+columnNames[i]);
        }
    }
    
    public PwsMonitorMeta() {
		init();        
    }
    
    public PwsMonitorMeta(String path) {
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

    public String getStAsgnIdentCd() {
        return path + ST_ASGN_IDENT_CD;
    } 

    public String getName() {
        return path + NAME;
    } 

    public String getTiaanlgpTiaanlytName() {
        return path + TIAANLGP_TIAANLYT_NAME;
    } 

    public String getNumberSamples() {
        return path + NUMBER_SAMPLES;
    } 

    public String getCompBeginDate() {
        return path + COMP_BEGIN_DATE;
    } 

    public String getCompEndDate() {
        return path + COMP_END_DATE;
    } 

    public String getFrequencyName() {
        return path + FREQUENCY_NAME;
    } 

    public String getPeriodName() {
        return path + PERIOD_NAME;
    } 

  
}   
