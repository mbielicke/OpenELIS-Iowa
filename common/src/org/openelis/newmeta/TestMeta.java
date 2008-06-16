
package org.openelis.newmeta;

/**
  * Test META Data
  */

import java.util.HashSet;
import org.openelis.gwt.common.NewMeta;

public class TestMeta implements NewMeta {
  	private String path = "";
	private static final String entityName = "Test";
	
	private static final String
              ID					="id",
              NAME					="name",
              DESCRIPTION					="description",
              REPORTING_DESCRIPTION					="reportingDescription",
              METHOD_ID					="methodId",
              IS_ACTIVE					="isActive",
              ACTIVE_BEGIN					="activeBegin",
              ACTIVE_END					="activeEnd",
              IS_REPORTABLE					="isReportable",
              TIME_TRANSIT					="timeTransit",
              TIME_HOLDING					="timeHolding",
              TIME_TA_AVERAGE					="timeTaAverage",
              TIME_TA_WARNING					="timeTaWarning",
              TIME_TA_MAX					="timeTaMax",
              LABEL_ID					="labelId",
              LABEL_QTY					="labelQty",
              TEST_TRAILER_ID					="testTrailerId",
              SECTION_ID					="sectionId",
              SCRIPTLET_ID					="scriptletId",
              TEST_FORMAT_ID					="testFormatId",
              REVISION_METHOD_ID					="revisionMethodId";

  	private static final String[] columnNames = {
  	  ID,NAME,DESCRIPTION,REPORTING_DESCRIPTION,METHOD_ID,IS_ACTIVE,ACTIVE_BEGIN,ACTIVE_END,IS_REPORTABLE,TIME_TRANSIT,TIME_HOLDING,TIME_TA_AVERAGE,TIME_TA_WARNING,TIME_TA_MAX,LABEL_ID,LABEL_QTY,TEST_TRAILER_ID,SECTION_ID,SCRIPTLET_ID,TEST_FORMAT_ID,REVISION_METHOD_ID};
  	  
	private HashSet<String> columnHashList;
    
    private void init() {
        columnHashList = new HashSet<String>(columnNames.length);
        for(int i = 0; i < columnNames.length; i++){
            columnHashList.add(path+columnNames[i]);
        }
    }
    
    public TestMeta() {
		init();        
    }
    
    public TestMeta(String path) {
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

    public String getReportingDescription() {
        return path + REPORTING_DESCRIPTION;
    } 

    public String getMethodId() {
        return path + METHOD_ID;
    } 

    public String getIsActive() {
        return path + IS_ACTIVE;
    } 

    public String getActiveBegin() {
        return path + ACTIVE_BEGIN;
    } 

    public String getActiveEnd() {
        return path + ACTIVE_END;
    } 

    public String getIsReportable() {
        return path + IS_REPORTABLE;
    } 

    public String getTimeTransit() {
        return path + TIME_TRANSIT;
    } 

    public String getTimeHolding() {
        return path + TIME_HOLDING;
    } 

    public String getTimeTaAverage() {
        return path + TIME_TA_AVERAGE;
    } 

    public String getTimeTaWarning() {
        return path + TIME_TA_WARNING;
    } 

    public String getTimeTaMax() {
        return path + TIME_TA_MAX;
    } 

    public String getLabelId() {
        return path + LABEL_ID;
    } 

    public String getLabelQty() {
        return path + LABEL_QTY;
    } 

    public String getTestTrailerId() {
        return path + TEST_TRAILER_ID;
    } 

    public String getSectionId() {
        return path + SECTION_ID;
    } 

    public String getScriptletId() {
        return path + SCRIPTLET_ID;
    } 

    public String getTestFormatId() {
        return path + TEST_FORMAT_ID;
    } 

    public String getRevisionMethodId() {
        return path + REVISION_METHOD_ID;
    } 

  
}   
