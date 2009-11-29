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

import java.util.Date;

import org.openelis.gwt.common.Datetime;
import org.openelis.utilcommon.DataBaseUtil;

/**
 * Class represents the fields in database table inventory_location.
 */
public class InventoryLocationDO extends DataObject {

    private static final long serialVersionUID = 1L;

    protected Integer         id, inventoryItemId, storageLocationId, quantityOnhand;
    protected String          lotNumber;
    protected Datetime        expirationDate;

    public InventoryLocationDO() {
    }

    public InventoryLocationDO(Integer id, Integer inventoryItemId, String lotNumber,
                               Integer storageLocationId, Integer quantityOnhand,
                               Date expirationDate) {
        setId(id);
        setInventoryItemId(inventoryItemId);
        setLotNumber(lotNumber);
        setStorageLocationId(storageLocationId);
        setQuantityOnhand(quantityOnhand);
        setExpirationDate(DataBaseUtil.toYD(expirationDate));
        _changed = false;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
        _changed = true;
    }

    public Integer getInventoryItemId() {
        return inventoryItemId;
    }

    public void setInventoryItemId(Integer inventoryItemId) {
        this.inventoryItemId = inventoryItemId;
        _changed = true;
    }

    public Integer getStorageLocationId() {
        return storageLocationId;
    }

    public void setStorageLocationId(Integer storageLocationId) {
        this.storageLocationId = storageLocationId;
        _changed = true;
    }

    public Integer getQuantityOnhand() {
        return quantityOnhand;
    }

    public void setQuantityOnhand(Integer quantityOnhand) {
        this.quantityOnhand = quantityOnhand;
        _changed = true;
    }

    public String getLotNumber() {
        return lotNumber;
    }

    public void setLotNumber(String lotNumber) {
        this.lotNumber = DataBaseUtil.trim(lotNumber);
        _changed = true;
    }

    public Datetime getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Datetime expirationDate) {
        this.expirationDate = DataBaseUtil.toYD(expirationDate);
        _changed = true;
    }
}
