/**
 * Exhibit A - UIRF Open-source Based Public Software License.
 * 
 * The contents of this file are subject to the UIRF Open-source Based Public
 * Software License(the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * openelis.uhl.uiowa.edu
 * 
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 * 
 * The Original Code is OpenELIS code.
 * 
 * The Initial Developer of the Original Code is The University of Iowa.
 * Portions created by The University of Iowa are Copyright 2006-2008. All
 * Rights Reserved.
 * 
 * Contributor(s): ______________________________________.
 * 
 * Alternatively, the contents of this file marked "Separately-Licensed" may be
 * used under the terms of a UIRF Software license ("UIRF Software License"), in
 * which case the provisions of a UIRF Software License are applicable instead
 * of those above.
 */
package org.openelis.domain;

/**
 * Class represents the fields in database table storage_location.
 */

import org.openelis.utilcommon.DataBaseUtil;

public class StorageLocationDO extends DataObject {

    private static final long serialVersionUID = 1L;

    protected Integer         id, sortOrder, parentStorageLocationId, storageUnitId;
    protected String          name, location, isAvailable;

    public StorageLocationDO() {
    }

    public StorageLocationDO(Integer id, Integer sortOrder, String name, String location,
                             Integer parentStorageLocationId, Integer storageUnitId,
                             String isAvailable) {
        setId(id);
        setSortOrder(sortOrder);
        setName(name);
        setLocation(location);
        setParentStorageLocationId(parentStorageLocationId);
        setStorageUnitId(storageUnitId);
        setIsAvailable(isAvailable);
        _changed = false;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
        _changed = true;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        if(this.sortOrder == null || !(this.sortOrder.equals(sortOrder))) {
            this.sortOrder = sortOrder;
            _changed = true;
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if(this.name == null || !(this.name.equals(DataBaseUtil.trim(name)))) {
            this.name = DataBaseUtil.trim(name);
            _changed = true;
        }
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = DataBaseUtil.trim(location);
        _changed = true;
    }

    public Integer getParentStorageLocationId() {
        return parentStorageLocationId;
    }

    public void setParentStorageLocationId(Integer parentStorageLocationId) {
        this.parentStorageLocationId = parentStorageLocationId;
        _changed = true;
    }

    public Integer getStorageUnitId() {
        return storageUnitId;
    }

    public void setStorageUnitId(Integer storageUnitId) {
        this.storageUnitId = storageUnitId;
        _changed = true;
    }

    public String getIsAvailable() {
        return isAvailable;
    }

    public void setIsAvailable(String isAvailable) {
        this.isAvailable = DataBaseUtil.trim(isAvailable);
        _changed = true;
    }
}
