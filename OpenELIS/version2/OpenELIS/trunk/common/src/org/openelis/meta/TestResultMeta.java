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
* Copyright (C) OpenELIS.  All Rights Reserved.
*/

package org.openelis.meta;

/**
  * TestResult META Data
  */

import java.util.HashSet;
import org.openelis.gwt.common.Meta;

public class TestResultMeta implements Meta {
  	private String path = "";
	private static final String entityName = "TestResult";
	
	private static final String
              ID					="id",
              TEST_ID					="testId",
              RESULT_GROUP_ID					="resultGroupId",
              FLAG_ID					="flagId",
              TYPE_ID					="typeId",
              VALUE					="value",
              SIGNIFICANT_DIGITS					="significantDigits",
              QUANT_LIMIT					="quantLimit",
              CONT_LEVEL					="contLevel";

  	private static final String[] columnNames = {
  	  ID,TEST_ID,RESULT_GROUP_ID,FLAG_ID,TYPE_ID,VALUE,SIGNIFICANT_DIGITS,QUANT_LIMIT,CONT_LEVEL};
  	  
	private HashSet<String> columnHashList;
    
    private void init() {
        columnHashList = new HashSet<String>(columnNames.length);
        for(int i = 0; i < columnNames.length; i++){
            columnHashList.add(path+columnNames[i]);
        }
    }
    
    public TestResultMeta() {
		init();        
    }
    
    public TestResultMeta(String path) {
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

    public String getTestId() {
        return path + TEST_ID;
    } 

    public String getResultGroupId() {
        return path + RESULT_GROUP_ID;
    } 

    public String getFlagId() {
        return path + FLAG_ID;
    } 

    public String getTypeId() {
        return path + TYPE_ID;
    } 

    public String getValue() {
        return path + VALUE;
    } 

    public String getSignificantDigits() {
        return path + SIGNIFICANT_DIGITS;
    } 

    public String getQuantLimit() {
        return path + QUANT_LIMIT;
    } 

    public String getContLevel() {
        return path + CONT_LEVEL;
    } 

  
}   
