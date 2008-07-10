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
  * AuxField META Data
  */

import java.util.HashSet;
import org.openelis.gwt.common.Meta;

public class AuxFieldMeta implements Meta {
  	private String path = "";
	private static final String entityName = "AuxField";
	
	private static final String
              ID					="id",
              SORT_ORDER_ID					="sortOrderId",
              ANALYTE_ID					="analyteId",
              REFERENCE_TABLE_ID					="referenceTableId",
              IS_REQUIRED					="isRequired",
              IS_ACTIVE					="isActive",
              IS_REPORTABLE					="isReportable",
              SCRIPTLET_ID					="scriptletId";

  	private static final String[] columnNames = {
  	  ID,SORT_ORDER_ID,ANALYTE_ID,REFERENCE_TABLE_ID,IS_REQUIRED,IS_ACTIVE,IS_REPORTABLE,SCRIPTLET_ID};
  	  
	private HashSet<String> columnHashList;
    
    private void init() {
        columnHashList = new HashSet<String>(columnNames.length);
        for(int i = 0; i < columnNames.length; i++){
            columnHashList.add(path+columnNames[i]);
        }
    }
    
    public AuxFieldMeta() {
		init();        
    }
    
    public AuxFieldMeta(String path) {
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

    public String getSortOrderId() {
        return path + SORT_ORDER_ID;
    } 

    public String getAnalyteId() {
        return path + ANALYTE_ID;
    } 

    public String getReferenceTableId() {
        return path + REFERENCE_TABLE_ID;
    } 

    public String getIsRequired() {
        return path + IS_REQUIRED;
    } 

    public String getIsActive() {
        return path + IS_ACTIVE;
    } 

    public String getIsReportable() {
        return path + IS_REPORTABLE;
    } 

    public String getScriptletId() {
        return path + SCRIPTLET_ID;
    } 

  
}   
