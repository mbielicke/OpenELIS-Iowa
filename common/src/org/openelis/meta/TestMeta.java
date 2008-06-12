
package org.openelis.meta;

/**
  * Test META Data
  */

import java.util.HashMap;
import org.openelis.util.Meta;

public class TestMeta implements Meta {
  	private static final String tableName = "test";
	private static final String entityName = "Test";
	private boolean includeInFrom = true;
	
	public static final String
              ID					="test.id",
              NAME					="test.name",
              DESCRIPTION					="test.description",
              REPORTING_DESCRIPTION					="test.reporting_description",
              METHOD_ID					="test.method_id",
              IS_ACTIVE					="test.is_active",
              ACTIVE_BEGIN					="test.active_begin",
              ACTIVE_END					="test.active_end",
              IS_REPORTABLE					="test.is_reportable",
              TIME_TRANSIT					="test.time_transit",
              TIME_HOLDING					="test.time_holding",
              TIME_TA_AVERAGE					="test.time_ta_average",
              TIME_TA_WARNING					="test.time_ta_warning",
              TIME_TA_MAX					="test.time_ta_max",
              LABEL_ID					="test.label_id",
              LABEL_QTY					="test.label_qty",
              TEST_TRAILER_ID					="test.test_trailer_id",
              SECTION_ID					="test.section_id",
              SCRIPTLET_ID					="test.scriptlet_id",
              TEST_FORMAT_ID					="test.test_format_id",
              REVISION_METHOD_ID					="test.revision_method_id";


  	private static final String[] columnNames = {
  	  ID,NAME,DESCRIPTION,REPORTING_DESCRIPTION,METHOD_ID,IS_ACTIVE,ACTIVE_BEGIN,ACTIVE_END,IS_REPORTABLE,TIME_TRANSIT,TIME_HOLDING,TIME_TA_AVERAGE,TIME_TA_WARNING,TIME_TA_MAX,LABEL_ID,LABEL_QTY,TEST_TRAILER_ID,SECTION_ID,SCRIPTLET_ID,TEST_FORMAT_ID,REVISION_METHOD_ID};
  	  
	private static HashMap<String,String> columnHashList;

	private static final TestMeta testMeta = new TestMeta();
    
    static {
        columnHashList = new HashMap<String,String>(columnNames.length);
        for(int i = 0; i < columnNames.length; i++){
            columnHashList.put(columnNames[i].substring(tableName.length()+1), "");
        }
    }
    
    private TestMeta() {
        
    }
    
    public static TestMeta getInstance() {
        return testMeta;
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

  public static String getName() {
    return NAME;
  } 

  public static String getDescription() {
    return DESCRIPTION;
  } 

  public static String getReportingDescription() {
    return REPORTING_DESCRIPTION;
  } 

  public static String getMethodId() {
    return METHOD_ID;
  } 

  public static String getIsActive() {
    return IS_ACTIVE;
  } 

  public static String getActiveBegin() {
    return ACTIVE_BEGIN;
  } 

  public static String getActiveEnd() {
    return ACTIVE_END;
  } 

  public static String getIsReportable() {
    return IS_REPORTABLE;
  } 

  public static String getTimeTransit() {
    return TIME_TRANSIT;
  } 

  public static String getTimeHolding() {
    return TIME_HOLDING;
  } 

  public static String getTimeTaAverage() {
    return TIME_TA_AVERAGE;
  } 

  public static String getTimeTaWarning() {
    return TIME_TA_WARNING;
  } 

  public static String getTimeTaMax() {
    return TIME_TA_MAX;
  } 

  public static String getLabelId() {
    return LABEL_ID;
  } 

  public static String getLabelQty() {
    return LABEL_QTY;
  } 

  public static String getTestTrailerId() {
    return TEST_TRAILER_ID;
  } 

  public static String getSectionId() {
    return SECTION_ID;
  } 

  public static String getScriptletId() {
    return SCRIPTLET_ID;
  } 

  public static String getTestFormatId() {
    return TEST_FORMAT_ID;
  } 

  public static String getRevisionMethodId() {
    return REVISION_METHOD_ID;
  } 

  
}   
