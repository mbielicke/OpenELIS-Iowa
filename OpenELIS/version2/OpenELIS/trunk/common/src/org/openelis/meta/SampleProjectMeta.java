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
  * SampleProject META Data
  */

import java.util.HashSet;
import org.openelis.gwt.common.Meta;

public class SampleProjectMeta implements Meta {
  	private String path = "";
	private static final String entityName = "SampleProject";
	
	private static final String
              ID					="id",
              SAMPLE_ID					="sampleId",
              PROJECT_ID					="projectId",
              IS_PERMANENT					="isPermanent";

  	private static final String[] columnNames = {
  	  ID,SAMPLE_ID,PROJECT_ID,IS_PERMANENT};
  	  
	private HashSet<String> columnHashList;
    
    private void init() {
        columnHashList = new HashSet<String>(columnNames.length);
        for(int i = 0; i < columnNames.length; i++){
            columnHashList.add(path+columnNames[i]);
        }
    }
    
    public SampleProjectMeta() {
		init();        
    }
    
    public SampleProjectMeta(String path) {
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

    public String getSampleId() {
        return path + SAMPLE_ID;
    } 

    public String getProjectId() {
        return path + PROJECT_ID;
    } 

    public String getIsPermanent() {
        return path + IS_PERMANENT;
    } 

  
}   
