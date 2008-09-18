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
  * TestWorksheet META Data
  */

import java.util.HashSet;
import org.openelis.gwt.common.Meta;

public class TestWorksheetMeta implements Meta {
  	protected String path = "";
	private static final String entityName = "TestWorksheet";
	
	private static final String
              ID					="id",
              TEST_ID					="testId",
              BATCH_CAPACITY					="batchCapacity",
              TOTAL_CAPACITY					="totalCapacity",
              NUMBER_FORMAT_ID					="numberFormatId",
              SCRIPTLET_ID					="scriptletId";

  	private static final String[] columnNames = {
  	  ID,TEST_ID,BATCH_CAPACITY,TOTAL_CAPACITY,NUMBER_FORMAT_ID,SCRIPTLET_ID};
  	  
	private HashSet<String> columnHashList;
    
    private void init() {
        columnHashList = new HashSet<String>(columnNames.length);
        for(int i = 0; i < columnNames.length; i++){
            columnHashList.add(path+columnNames[i]);
        }
    }
    
    public TestWorksheetMeta() {
		init();        
    }
    
    public TestWorksheetMeta(String path) {
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

    public String getBatchCapacity() {
        return path + BATCH_CAPACITY;
    } 

    public String getTotalCapacity() {
        return path + TOTAL_CAPACITY;
    } 

    public String getNumberFormatId() {
        return path + NUMBER_FORMAT_ID;
    } 

    public String getScriptletId() {
        return path + SCRIPTLET_ID;
    } 

  
}   
