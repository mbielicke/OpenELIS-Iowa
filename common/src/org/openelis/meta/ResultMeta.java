
package org.openelis.meta;

/**
  * Result META Data
  */

import java.util.HashMap;
import org.openelis.util.Meta;

public class ResultMeta implements Meta {
  	private static final String tableName = "result";
	private static final String entityName = "Result";
	private boolean includeInFrom = true;
	
	public static final String
              ID					="result.id",
              ANALYSIS_ID					="result.analysisId",
              SORT_ORDER_ID					="result.sortOrderId",
              IS_REPORTABLE					="result.isReportable",
              ANALYTE_ID					="result.analyteId",
              TYPE_ID					="result.typeId",
              VALUE					="result.value",
              TEST_RESULT_ID					="result.testResultId",
              QUANT_LIMIT					="result.quantLimit";


  	private static final String[] columnNames = {
  	  ID,ANALYSIS_ID,SORT_ORDER_ID,IS_REPORTABLE,ANALYTE_ID,TYPE_ID,VALUE,TEST_RESULT_ID,QUANT_LIMIT};
  	  
	private static HashMap<String,String> columnHashList;

	private static final ResultMeta resultMeta = new ResultMeta();
    
    static {
        columnHashList = new HashMap<String,String>(columnNames.length);
        for(int i = 0; i < columnNames.length; i++){
            columnHashList.put(columnNames[i].substring(tableName.length()+1), "");
        }
    }
    
    private ResultMeta() {
        
    }
    
    public static ResultMeta getInstance() {
        return resultMeta;
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

  public static String getAnalysisId() {
    return ANALYSIS_ID;
  } 

  public static String getSortOrderId() {
    return SORT_ORDER_ID;
  } 

  public static String getIsReportable() {
    return IS_REPORTABLE;
  } 

  public static String getAnalyteId() {
    return ANALYTE_ID;
  } 

  public static String getTypeId() {
    return TYPE_ID;
  } 

  public static String getValue() {
    return VALUE;
  } 

  public static String getTestResultId() {
    return TEST_RESULT_ID;
  } 

  public static String getQuantLimit() {
    return QUANT_LIMIT;
  } 

  
}   
