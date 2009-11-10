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
package org.openelis.domain;

import org.openelis.gwt.common.RPC;
import org.openelis.utilcommon.DataBaseUtil;

public class StorageLocationAutoDO implements RPC {

    private static final long serialVersionUID = 1L;
    protected Integer         id;
    protected String          name;
    protected Integer         locationId;
    protected String          location;
    protected Integer         qtyOnHand;
    protected String          lotNum;

    public StorageLocationAutoDO() {
    }

    public StorageLocationAutoDO(Integer id,
                                 String name,
                                 Integer locationId,
                                 String location,
                                 String parentStorageLocName,
                                 String storageUnitDescription) {
        setId(id);
        setName(name);
        setLocationId(locationId);
        setLocation(DataBaseUtil.formatStorageLocation(name, location, storageUnitDescription, parentStorageLocName));
    }

    public StorageLocationAutoDO(Integer id,
                                 String name,
                                 String location,
                                 String parentStorageLocName,
                                 String storageUnitDescription) {
        setId(id);
        setName(name);
        setLocation(DataBaseUtil.formatStorageLocation(name, location, storageUnitDescription, parentStorageLocName));
    }

    // with qty on hand
    public StorageLocationAutoDO(Integer id,
                                 String name,
                                 String location,
                                 String parentStorageLocName,
                                 String storageUnitDescription,
                                 Integer qtyOnHand) {
        setId(id);
        setName(name);
        setLocation(DataBaseUtil.formatStorageLocation(name, location, storageUnitDescription, parentStorageLocName));
        setQtyOnHand(qtyOnHand);
    }

    // with qty on hand and lot number
    public StorageLocationAutoDO(Integer id,
                                 String name,
                                 String location,
                                 String parentStorageLocName,
                                 String storageUnitDescription,
                                 Integer locationId,
                                 Integer qtyOnHand,
                                 String lotNum) {
        setId(id);
        setName(name);
        setLocationId(locationId);
        setLocation(DataBaseUtil.formatStorageLocation(name, location, storageUnitDescription, parentStorageLocName));
        setQtyOnHand(qtyOnHand);
        setLotNum(lotNum);
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = DataBaseUtil.trim(location);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = DataBaseUtil.trim(name);
    }

    public String getLotNum() {
        return lotNum;
    }

    public void setLotNum(String lotNum) {
        this.lotNum = DataBaseUtil.trim(lotNum);
    }

    public Integer getQtyOnHand() {
        return qtyOnHand;
    }

    public void setQtyOnHand(Integer qtyOnHand) {
        this.qtyOnHand = qtyOnHand;
    }

    public Integer getLocationId() {
        return locationId;
    }

    public void setLocationId(Integer locationId) {
        this.locationId = locationId;
    }
}
