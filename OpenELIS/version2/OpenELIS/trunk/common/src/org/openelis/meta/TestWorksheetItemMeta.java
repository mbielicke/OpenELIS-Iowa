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
  * TestWorksheetItem META Data
  */

import java.util.HashSet;
import org.openelis.gwt.common.Meta;

public class TestWorksheetItemMeta implements Meta {
  	private String path = "";
	private static final String entityName = "TestWorksheetItem";
	
	private static final String
              ID					="id",
              TEST_WORKSHEET_ID					="testWorksheetId",
              POSITION					="position",
              TYPE_ID					="typeId",
              QC_NAME					="qcName";

  	private static final String[] columnNames = {
  	  ID,TEST_WORKSHEET_ID,POSITION,TYPE_ID,QC_NAME};
  	  
	private HashSet<String> columnHashList;
    
    private void init() {
        columnHashList = new HashSet<String>(columnNames.length);
        for(int i = 0; i < columnNames.length; i++){
            columnHashList.add(path+columnNames[i]);
        }
    }
    
    public TestWorksheetItemMeta() {
		init();        
    }
    
    public TestWorksheetItemMeta(String path) {
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

    public String getTestWorksheetId() {
        return path + TEST_WORKSHEET_ID;
    } 

    public String getPosition() {
        return path + POSITION;
    } 

    public String getTypeId() {
        return path + TYPE_ID;
    } 

    public String getQcName() {
        return path + QC_NAME;
    } 

  
}   
