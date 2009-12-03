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
  * TestAnalyte META Data
  */

import java.util.HashSet;

import org.openelis.gwt.common.Meta;

public class TestAnalyteMeta implements Meta {
  	protected String path = "";
	private static final String entityName = "TestAnalyte";
	
	private static final String
              ID					="id",
              TEST_ID					="testId",
              ANALYTE_GROUP             = "analyteGroup",
              RESULT_GROUP					="resultGroup",
              SORT_ORDER					="sortOrder",
              TYPE_ID					="typeId",
              ANALYTE_ID					="analyteId",
              IS_REPORTABLE					="isReportable",
              SCRIPTLET_ID					="scriptletId";

  	private static final String[] columnNames = {
  	  ID,TEST_ID,ANALYTE_GROUP,RESULT_GROUP,SORT_ORDER,TYPE_ID,ANALYTE_ID,IS_REPORTABLE,SCRIPTLET_ID};
  	  
	private HashSet<String> columnHashList;
    
    private void init() {
        columnHashList = new HashSet<String>(columnNames.length);
        for(int i = 0; i < columnNames.length; i++){
            columnHashList.add(path+columnNames[i]);
        }
    }
    
    public TestAnalyteMeta() {
		init();        
    }
    
    public TestAnalyteMeta(String path) {
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
    
    public String getAnalyteGroup() {
        return path + ANALYTE_GROUP;
    }

    public String getResultGroup() {
        return path + RESULT_GROUP;
    } 

    public String getSortOrder() {
        return path + SORT_ORDER;
    } 

    public String getTypeId() {
        return path + TYPE_ID;
    } 

    public String getAnalyteId() {
        return path + ANALYTE_ID;
    } 

    public String getIsReportable() {
        return path + IS_REPORTABLE;
    } 

    public String getScriptletId() {
        return path + SCRIPTLET_ID;
    } 

  
}   
