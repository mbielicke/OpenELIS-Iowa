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

import java.util.Arrays;
import java.util.HashSet;

import org.openelis.gwt.common.Meta;
import org.openelis.gwt.common.MetaMap;

public class StorageLocationMeta implements Meta, MetaMap {
	
	private static final String ID = "_storageLocation.id",
                                SORT_ORDER = "_storageLocation.sortOrder",
                                NAME = "_storageLocation.name",
                                LOCATION = "_storageLocation.location",
                                PARENT_STORAGE_LOCATION_ID	= "_storageLocation.parentStorageLocationId",
                                STORAGE_UNIT_ID	= "_storageLocation.storageUnitId",
                                IS_AVAILABLE = "_storageLocation.isAvailable",
	
                                CHILD_ID = "_childStorageLocation.id",
                                CHILD_SORT_ORDER = "_childStorageLocation.sortOrder",
                                CHILD_NAME = "_childStorageLocation.name",
                                CHILD_LOCATION = "_childStorageLocation.location",
                                CHILD_PARENT_STORAGE_LOCATION_ID  = "_childStorageLocation.parentStorageLocationId",
                                CHILD_STORAGE_UNIT_ID = "_childStorageLocation.storageUnitId",
                                CHILD_IS_AVAILABLE = "_childStorageLocation.isAvailable", 
                                
                                STORAGE_UNIT_DESCRIPTION = "_storageLocation.storageUnit.description",
                                CHILD_STORAGE_UNIT_DESCRIPTION = "_childStorageLocation.storageUnit.description";	                            		                          

	private static HashSet<String> names;
    
    static {
        names = new HashSet<String>(Arrays.asList(ID,SORT_ORDER,NAME,LOCATION,
                                                  PARENT_STORAGE_LOCATION_ID,
                                                  STORAGE_UNIT_ID,IS_AVAILABLE,CHILD_ID,
                                                  CHILD_SORT_ORDER,CHILD_NAME,CHILD_LOCATION,
                                                  CHILD_PARENT_STORAGE_LOCATION_ID,
                                                  CHILD_STORAGE_UNIT_ID,CHILD_IS_AVAILABLE,
                                                  STORAGE_UNIT_DESCRIPTION,
                                                  CHILD_STORAGE_UNIT_DESCRIPTION));
    }
        
    public static String getId() {
        return ID;
    } 

    public static String getSortOrder() {
        return SORT_ORDER;
    } 

    public static String getName() {
        return NAME;
    } 

    public static String getLocation() {
        return LOCATION;
    } 

    public static String getParentStorageLocationId() {
        return PARENT_STORAGE_LOCATION_ID;
    } 

    public static String getStorageUnitId() {
        return STORAGE_UNIT_ID;
    } 

    public static String getIsAvailable() {
        return IS_AVAILABLE;
    }
    
    public static String getStorageUnitDescription() {
        return STORAGE_UNIT_DESCRIPTION;
    } 
    
    public static String getChildId() {
        return CHILD_ID;
    } 

    public static String getChildSortOrder() {
        return CHILD_SORT_ORDER;
    } 

    public static String getChildName() {
        return CHILD_NAME;
    } 

    public static String getChildLocation() {
        return CHILD_LOCATION;
    } 

    public static String getChildParentStorageLocationId() {
        return CHILD_PARENT_STORAGE_LOCATION_ID;
    } 

    public static String getChildStorageUnitId() {
        return CHILD_STORAGE_UNIT_ID;
    } 

    public static String getChildIsAvailable() {
        return CHILD_IS_AVAILABLE;
    }
    
    public static String getChildStorageUnitDescription() {
        return CHILD_STORAGE_UNIT_DESCRIPTION;
    } 

    public boolean hasColumn(String columnName) {
        return names.contains(columnName);
    }
    
    public String buildFrom(String where) {
        String from = "StorageLocation _storageLocation ";
        if(where.indexOf("childStorageLocation.") > -1)
            from += ", IN (_storageLocation.childStorageLocation) _childStorageLocation ";

        return from;
    } 

  
}   
