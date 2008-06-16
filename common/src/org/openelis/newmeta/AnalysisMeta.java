
package org.openelis.newmeta;

/**
  * Analysis META Data
  */

import java.util.HashSet;
import org.openelis.gwt.common.NewMeta;

public class AnalysisMeta implements NewMeta {
  	private String path = "";
	private static final String entityName = "Analysis";
	
	private static final String
              ID					="id",
              SAMPLE_ITEM_ID					="sampleItemId",
              REVISION					="revision",
              TEST_ID					="testId",
              SECTION_ID					="sectionId",
              PRE_ANALYSIS_ID					="preAnalysisId",
              PARENT_ANALYSIS_ID					="parentAnalysisId",
              PARENT_RESULT_ID					="parentResultId",
              IS_REPORTABLE					="isReportable",
              UNIT_OF_MEASURE_ID					="unitOfMeasureId",
              STATUS_ID					="statusId",
              AVAILABLE_DATE					="availableDate",
              STARTED_DATE					="startedDate",
              COMPLETED_DATE					="completedDate",
              RELEASED_DATE					="releasedDate",
              PRINTED_DATE					="printedDate";

  	private static final String[] columnNames = {
  	  ID,SAMPLE_ITEM_ID,REVISION,TEST_ID,SECTION_ID,PRE_ANALYSIS_ID,PARENT_ANALYSIS_ID,PARENT_RESULT_ID,IS_REPORTABLE,UNIT_OF_MEASURE_ID,STATUS_ID,AVAILABLE_DATE,STARTED_DATE,COMPLETED_DATE,RELEASED_DATE,PRINTED_DATE};
  	  
	private HashSet<String> columnHashList;
    
    private void init() {
        columnHashList = new HashSet<String>(columnNames.length);
        for(int i = 0; i < columnNames.length; i++){
            columnHashList.add(path+columnNames[i]);
        }
    }
    
    public AnalysisMeta() {
		init();        
    }
    
    public AnalysisMeta(String path) {
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

    public String getSampleItemId() {
        return path + SAMPLE_ITEM_ID;
    } 

    public String getRevision() {
        return path + REVISION;
    } 

    public String getTestId() {
        return path + TEST_ID;
    } 

    public String getSectionId() {
        return path + SECTION_ID;
    } 

    public String getPreAnalysisId() {
        return path + PRE_ANALYSIS_ID;
    } 

    public String getParentAnalysisId() {
        return path + PARENT_ANALYSIS_ID;
    } 

    public String getParentResultId() {
        return path + PARENT_RESULT_ID;
    } 

    public String getIsReportable() {
        return path + IS_REPORTABLE;
    } 

    public String getUnitOfMeasureId() {
        return path + UNIT_OF_MEASURE_ID;
    } 

    public String getStatusId() {
        return path + STATUS_ID;
    } 

    public String getAvailableDate() {
        return path + AVAILABLE_DATE;
    } 

    public String getStartedDate() {
        return path + STARTED_DATE;
    } 

    public String getCompletedDate() {
        return path + COMPLETED_DATE;
    } 

    public String getReleasedDate() {
        return path + RELEASED_DATE;
    } 

    public String getPrintedDate() {
        return path + PRINTED_DATE;
    } 

  
}   
