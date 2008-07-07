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
  * Dictionary META Data
  */

import java.util.HashSet;
import org.openelis.gwt.common.Meta;

public class DictionaryMeta implements Meta {
  	protected String path = "";
	private static final String entityName = "Dictionary";
	
	private static final String
              ID					="id",
              CATEGORY_ID					="categoryId",
              RELATED_ENTRY_ID					="relatedEntryId",
              SYSTEM_NAME					="systemName",
              IS_ACTIVE					="isActive",
              LOCAL_ABBREV					="localAbbrev",
              ENTRY					="entry";

  	private static final String[] columnNames = {
  	  ID,CATEGORY_ID,RELATED_ENTRY_ID,SYSTEM_NAME,IS_ACTIVE,LOCAL_ABBREV,ENTRY};
  	  
	private HashSet<String> columnHashList;
    
    private void init() {
        columnHashList = new HashSet<String>(columnNames.length);
        for(int i = 0; i < columnNames.length; i++){
            columnHashList.add(path+columnNames[i]);
        }
    }
    
    public DictionaryMeta() {
		init();        
    }
    
    public DictionaryMeta(String path) {
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

    public String getCategoryId() {
        return path + CATEGORY_ID;
    } 

    public String getRelatedEntryId() {
        return path + RELATED_ENTRY_ID;
    } 

    public String getSystemName() {
        return path + SYSTEM_NAME;
    } 

    public String getIsActive() {
        return path + IS_ACTIVE;
    } 

    public String getLocalAbbrev() {
        return path + LOCAL_ABBREV;
    } 

    public String getEntry() {
        return path + ENTRY;
    } 

  
}   
