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
  * AnalysisQaevent META Data
  */

import java.util.HashSet;
import org.openelis.gwt.common.Meta;

public class AnalysisQaeventMeta implements Meta {
  	private String path = "";
	private static final String entityName = "AnalysisQaevent";
	
	private static final String
              ID					="id",
              ANALYSIS_ID					="analysisId",
              QAEVENT_ID					="qaeventId";

  	private static final String[] columnNames = {
  	  ID,ANALYSIS_ID,QAEVENT_ID};
  	  
	private HashSet<String> columnHashList;
    
    private void init() {
        columnHashList = new HashSet<String>(columnNames.length);
        for(int i = 0; i < columnNames.length; i++){
            columnHashList.add(path+columnNames[i]);
        }
    }
    
    public AnalysisQaeventMeta() {
		init();        
    }
    
    public AnalysisQaeventMeta(String path) {
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

    public String getQaeventId() {
        return path + QAEVENT_ID;
    } 

  
}   
