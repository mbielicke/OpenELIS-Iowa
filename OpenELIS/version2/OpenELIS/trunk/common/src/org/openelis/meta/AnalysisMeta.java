
package org.openelis.meta;

/**
  * Analysis META Data
  */

import java.util.HashMap;
import org.openelis.util.Meta;

public class AnalysisMeta implements Meta {
  	private static final String tableName = "analysis";
	private static final String entityName = "Analysis";
	private boolean includeInFrom = true;
	
	public static final String
              ID					="analysis.id",
              SAMPLE_ITEM_ID					="analysis.sampleItemId",
              REVISION					="analysis.revision",
              TEST_ID					="analysis.testId",
              SECTION_ID					="analysis.sectionId",
              PRE_ANALYSIS_ID					="analysis.preAnalysisId",
              PARENT_ANALYSIS_ID					="analysis.parentAnalysisId",
              PARENT_RESULT_ID					="analysis.parentResultId",
              IS_REPORTABLE					="analysis.isReportable",
              UNIT_OF_MEASURE_ID					="analysis.unitOfMeasureId",
              STATUS_ID					="analysis.statusId",
              AVAILABLE_DATE					="analysis.availableDate",
              STARTED_DATE					="analysis.startedDate",
              COMPLETED_DATE					="analysis.completedDate",
              RELEASED_DATE					="analysis.releasedDate",
              PRINTED_DATE					="analysis.printedDate";


  	private static final String[] columnNames = {
  	  ID,SAMPLE_ITEM_ID,REVISION,TEST_ID,SECTION_ID,PRE_ANALYSIS_ID,PARENT_ANALYSIS_ID,PARENT_RESULT_ID,IS_REPORTABLE,UNIT_OF_MEASURE_ID,STATUS_ID,AVAILABLE_DATE,STARTED_DATE,COMPLETED_DATE,RELEASED_DATE,PRINTED_DATE};
  	  
	private static HashMap<String,String> columnHashList;

	private static final AnalysisMeta analysisMeta = new AnalysisMeta();
    
    static {
        columnHashList = new HashMap<String,String>(columnNames.length);
        for(int i = 0; i < columnNames.length; i++){
            columnHashList.put(columnNames[i].substring(tableName.length()+1), "");
        }
    }
    
    private AnalysisMeta() {
        
    }
    
    public static AnalysisMeta getInstance() {
        return analysisMeta;
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

  public static String getSampleItemId() {
    return SAMPLE_ITEM_ID;
  } 

  public static String getRevision() {
    return REVISION;
  } 

  public static String getTestId() {
    return TEST_ID;
  } 

  public static String getSectionId() {
    return SECTION_ID;
  } 

  public static String getPreAnalysisId() {
    return PRE_ANALYSIS_ID;
  } 

  public static String getParentAnalysisId() {
    return PARENT_ANALYSIS_ID;
  } 

  public static String getParentResultId() {
    return PARENT_RESULT_ID;
  } 

  public static String getIsReportable() {
    return IS_REPORTABLE;
  } 

  public static String getUnitOfMeasureId() {
    return UNIT_OF_MEASURE_ID;
  } 

  public static String getStatusId() {
    return STATUS_ID;
  } 

  public static String getAvailableDate() {
    return AVAILABLE_DATE;
  } 

  public static String getStartedDate() {
    return STARTED_DATE;
  } 

  public static String getCompletedDate() {
    return COMPLETED_DATE;
  } 

  public static String getReleasedDate() {
    return RELEASED_DATE;
  } 

  public static String getPrintedDate() {
    return PRINTED_DATE;
  } 

  
}   
