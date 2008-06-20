
package org.openelis.newmeta;

/**
  * Qaevent META Data
  */

import java.util.HashSet;
import org.openelis.gwt.common.NewMeta;

public class QaeventMeta implements NewMeta {
  	private String path = "";
	private static final String entityName = "QaEvent";
	
	private static final String
              ID					="id",
              NAME					="name",
              DESCRIPTION					="description",
              TEST_ID					="testId",
              TYPE_ID					="typeId",
              IS_BILLABLE					="isBillable",
              REPORTING_SEQUENCE					="reportingSequence",
              REPORTING_TEXT					="reportingText";

  	private static final String[] columnNames = {
  	  ID,NAME,DESCRIPTION,TEST_ID,TYPE_ID,IS_BILLABLE,REPORTING_SEQUENCE,REPORTING_TEXT};
  	  
	private HashSet<String> columnHashList;
    
    private void init() {
        columnHashList = new HashSet<String>(columnNames.length);
        for(int i = 0; i < columnNames.length; i++){
            columnHashList.add(path+columnNames[i]);
        }
    }
    
    public QaeventMeta() {
		init();        
    }
    
    public QaeventMeta(String path) {
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

    public String getName() {
        return path + NAME;
    } 

    public String getDescription() {
        return path + DESCRIPTION;
    } 

    public String getTestId() {
        return path + TEST_ID;
    } 

    public String getTypeId() {
        return path + TYPE_ID;
    } 

    public String getIsBillable() {
        return path + IS_BILLABLE;
    } 

    public String getReportingSequence() {
        return path + REPORTING_SEQUENCE;
    } 

    public String getReportingText() {
        return path + REPORTING_TEXT;
    } 

  
}   
