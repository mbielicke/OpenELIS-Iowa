/**
* The contents of this file are subject to the Mozilla Public License
* Version 1.1 (the "License"); you may not use this file except in
* compliance with the License. You may obtain a copy of the License at
* http://www.mozilla.org/MPL/
* 
* Software distributed under the License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
* License for the specific language governing rights and limitations under
* the License.
* 
* The Original Code is OpenELIS code.
* 
* Copyright (C) The University of Iowa.  All Rights Reserved.
*/

package org.openelis.meta;

/**
  * Result META Data
  */

import java.util.HashSet;
import org.openelis.gwt.common.Meta;

public class ResultMeta implements Meta {
  	private String path = "";
	private static final String entityName = "Result";
	
	private static final String
              ID					="id",
              ANALYSIS_ID					="analysisId",
              SORT_ORDER_ID					="sortOrderId",
              IS_REPORTABLE					="isReportable",
              ANALYTE_ID					="analyteId",
              TYPE_ID					="typeId",
              VALUE					="value",
              TEST_RESULT_ID					="testResultId",
              QUANT_LIMIT					="quantLimit";

  	private static final String[] columnNames = {
  	  ID,ANALYSIS_ID,SORT_ORDER_ID,IS_REPORTABLE,ANALYTE_ID,TYPE_ID,VALUE,TEST_RESULT_ID,QUANT_LIMIT};
  	  
	private HashSet<String> columnHashList;
    
    private void init() {
        columnHashList = new HashSet<String>(columnNames.length);
        for(int i = 0; i < columnNames.length; i++){
            columnHashList.add(path+columnNames[i]);
        }
    }
    
    public ResultMeta() {
		init();        
    }
    
    public ResultMeta(String path) {
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

    public String getAnalysisId() {
        return path + ANALYSIS_ID;
    } 

    public String getSortOrderId() {
        return path + SORT_ORDER_ID;
    } 

    public String getIsReportable() {
        return path + IS_REPORTABLE;
    } 

    public String getAnalyteId() {
        return path + ANALYTE_ID;
    } 

    public String getTypeId() {
        return path + TYPE_ID;
    } 

    public String getValue() {
        return path + VALUE;
    } 

    public String getTestResultId() {
        return path + TEST_RESULT_ID;
    } 

    public String getQuantLimit() {
        return path + QUANT_LIMIT;
    } 

  
}   
