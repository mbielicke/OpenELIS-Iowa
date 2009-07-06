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
  * StorageLocation META Data
  */

import java.util.HashSet;
import org.openelis.gwt.common.Meta;

public class StorageLocationMeta implements Meta {
  	protected String path = "";
	private static final String entityName = "StorageLocation";
	
	private static final String
              ID					="id",
              SORT_ORDER					="sortOrder",
              NAME					="name",
              LOCATION					="location",
              PARENT_STORAGE_LOCATION_ID					="parentStorageLocationId",
              STORAGE_UNIT_ID					="storageUnitId",
              IS_AVAILABLE					="isAvailable";

  	private static final String[] columnNames = {
  	  ID,SORT_ORDER,NAME,LOCATION,PARENT_STORAGE_LOCATION_ID,STORAGE_UNIT_ID,IS_AVAILABLE};
  	  
	private HashSet<String> columnHashList;
    
    private void init() {
        columnHashList = new HashSet<String>(columnNames.length);
        for(int i = 0; i < columnNames.length; i++){
            columnHashList.add(path+columnNames[i]);
        }
    }
    
    public StorageLocationMeta() {
		init();        
    }
    
    public StorageLocationMeta(String path) {
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

    public String getSortOrder() {
        return path + SORT_ORDER;
    } 

    public String getName() {
        return path + NAME;
    } 

    public String getLocation() {
        return path + LOCATION;
    } 

    public String getParentStorageLocationId() {
        return path + PARENT_STORAGE_LOCATION_ID;
    } 

    public String getStorageUnitId() {
        return path + STORAGE_UNIT_ID;
    } 

    public String getIsAvailable() {
        return path + IS_AVAILABLE;
    } 

  
}   
