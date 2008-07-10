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
  * Analyte META Data
  */

import java.util.HashSet;
import org.openelis.gwt.common.Meta;

public class AnalyteMeta implements Meta {
  	protected String path = "";
	private static final String entityName = "Analyte";
	
	private static final String
              ID					="id",
              NAME					="name",
              IS_ACTIVE				="isActive",
              ANALYTE_GROUP_ID		="analyteGroupId",
              PARENT_ANALYTE_ID		="parentAnalyteId",
              EXTERNAL_ID			="externalId";

  	private static final String[] columnNames = {
  	  ID,NAME,IS_ACTIVE,ANALYTE_GROUP_ID,PARENT_ANALYTE_ID,EXTERNAL_ID};
  	  
	private HashSet<String> columnHashList;
    
    private void init() {
        columnHashList = new HashSet<String>(columnNames.length);
        for(int i = 0; i < columnNames.length; i++){
            columnHashList.add(path+columnNames[i]);
        }
    }
    
    public AnalyteMeta() {
		init();        
    }
    
    public AnalyteMeta(String path) {
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

    public String getName() {
        return path + NAME;
    } 

    public String getIsActive() {
        return path + IS_ACTIVE;
    } 

    public String getAnalyteGroupId() {
        return path + ANALYTE_GROUP_ID;
    } 

    public String getParentAnalyteId() {
        return path + PARENT_ANALYTE_ID;
    } 

    public String getExternalId() {
        return path + EXTERNAL_ID;
    } 

  
}   
