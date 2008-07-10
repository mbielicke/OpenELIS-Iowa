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
  * StorageUnit META Data
  */

import java.util.HashSet;
import org.openelis.gwt.common.Meta;

public class StorageUnitMeta implements Meta {
  	private String path = "";
	private static final String entityName = "StorageUnit";
	
	private static final String
              ID					="id",
              CATEGORY					="category",
              DESCRIPTION					="description",
              IS_SINGULAR					="isSingular";

  	private static final String[] columnNames = {
  	  ID,CATEGORY,DESCRIPTION,IS_SINGULAR};
  	  
	private HashSet<String> columnHashList;
    
    private void init() {
        columnHashList = new HashSet<String>(columnNames.length);
        for(int i = 0; i < columnNames.length; i++){
            columnHashList.add(path+columnNames[i]);
        }
    }
    
    public StorageUnitMeta() {
		init();        
    }
    
    public StorageUnitMeta(String path) {
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

    public String getCategory() {
        return path + CATEGORY;
    } 

    public String getDescription() {
        return path + DESCRIPTION;
    } 

    public String getIsSingular() {
        return path + IS_SINGULAR;
    } 

  
}   
