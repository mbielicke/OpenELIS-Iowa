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
  * Storage META Data
  */

import java.util.Arrays;
import java.util.HashSet;

import org.openelis.gwt.common.Meta;
import org.openelis.gwt.common.MetaMap;

public class StorageMeta implements Meta, MetaMap {
	
	private static final String ID ="_storage.id",
                                REFERENCE_ID ="_storage.referenceId",
                                REFERENCE_TABLE_ID ="_storage.referenceTableId",
                                STORAGE_LOCATION_ID ="_storage.storageLocationId",
                                CHECKIN ="_storage.checkin",
                                CHECKOUT ="_storage.checkout",
                                SYSTEM_USER_ID ="_storage.systemUserId",
                                
                                STRG_LOC_NAME = "_storageLocation.name",
                                STRG_LOC_LOCATION = "_storageLocation.location",
                                STRG_LOC_IS_AVAILABLE = "_storageLocation.isAvailable",
	                            STRG_LOC_STRG_UNIT_DESCRIPTION = "_storageLocation.storageUnit.description";
	

	private static HashSet<String> names;
    
    static {
        names = new HashSet<String>(Arrays.asList(ID,REFERENCE_ID,REFERENCE_TABLE_ID,
                                                  STORAGE_LOCATION_ID,CHECKIN,CHECKOUT,
                                                  SYSTEM_USER_ID,STRG_LOC_NAME,
                                                  STRG_LOC_LOCATION,STRG_LOC_IS_AVAILABLE,
                                                  STRG_LOC_STRG_UNIT_DESCRIPTION));
    }
    
    public static String getId() {
        return ID;
    } 

    public static String getReferenceId() {
        return REFERENCE_ID;
    } 

    public static String getReferenceTableId() {
        return REFERENCE_TABLE_ID;
    } 

    public static String getStorageLocationId() {
        return STORAGE_LOCATION_ID;
    } 
    
    public static String getCheckin() {
        return CHECKIN;
    } 

    public static String getCheckout() {
        return CHECKOUT;
    } 
    
    public static String getSystemUserId() {
        return SYSTEM_USER_ID;
    } 
    
    public static String getStorageLocationName() {
        return STRG_LOC_NAME;
    }
    
    public static String getStorageLocationLocation() {
        return STRG_LOC_LOCATION;
    }
    
    public static String getStorageLocationIsAvailable() {
        return STRG_LOC_IS_AVAILABLE;
    }
    
    public static String getStorageLocationStorageUnitDescription() {
        return STRG_LOC_STRG_UNIT_DESCRIPTION;
    }
    
    public boolean hasColumn(String columnName) {
        return names.contains(columnName);
    }
    
    public String buildFrom(String where) {        
        String from = "StorageLocation _storageLocation ";
        if(where.indexOf("storage.") > -1)
            from += ", IN (_storageLocation.storage) _storage ";

        return from;
    } 
    
}   
