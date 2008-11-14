/** Exhibit A - UIRF Open-source Based Public Software License.
* 
* The contents of this file are subject to the UIRF Open-source Based
* Public Software License(the "License"); you may not use this file except
* in compliance with the License. You may obtain a copy of the License at
* openelis.uhl.uiowa.edu
* 
* Software distributed under the License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
* License for the specific language governing rights and limitations
* under the License.
* 
* The Original Code is OpenELIS code.
* 
* The Initial Developer of the Original Code is The University of Iowa.
* Portions created by The University of Iowa are Copyright 2006-2008. All
* Rights Reserved.
* 
* Contributor(s): ______________________________________.
* 
* Alternatively, the contents of this file marked
* "Separately-Licensed" may be used under the terms of a UIRF Software
* license ("UIRF Software License"), in which case the provisions of a
* UIRF Software License are applicable instead of those above. 
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
              ID					    ="id",
              TEST_ID					="testId",
              RESULT_GROUP			    ="resultGroup",
              SORT_ORDER                ="sortOrder",       
              FLAGS_ID					="flagsId",
              TYPE_ID					="typeId",
              VALUE					    ="value",
              SIGNIFICANT_DIGITS		="significantDigits",
              ROUNDING_METHOD_ID        ="roundingMethodId", 
              QUANT_LIMIT				="quantLimit",
              CONT_LEVEL				="contLevel",
              HAZARD_LEVEL              ="hazardLevel";

  	private static final String[] columnNames = {
  	  ID,TEST_ID,RESULT_GROUP,SORT_ORDER,FLAGS_ID,TYPE_ID,VALUE,SIGNIFICANT_DIGITS,ROUNDING_METHOD_ID,QUANT_LIMIT,CONT_LEVEL,HAZARD_LEVEL};
  	  
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

    public String getResultGroup() {
        return path + RESULT_GROUP;
    } 

    public String getFlagsId() {
        return path + FLAGS_ID;
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
    
    public String getRoundingMethodId() {
        return path + SIGNIFICANT_DIGITS;
    }

    public String getQuantLimit() {
        return path + QUANT_LIMIT;
    } 

    public String getContLevel() {
        return path + CONT_LEVEL;
    } 
    
    public String getHazardLevel() {
        return path + HAZARD_LEVEL;
    }

  
}   
