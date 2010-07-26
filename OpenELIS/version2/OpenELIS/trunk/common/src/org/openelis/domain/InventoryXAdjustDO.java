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

/**
 * Class represents the fields in database table inventory_x_adjust.
 */
public class InventoryXAdjustDO extends DataObject {

    private static final long serialVersionUID = 1L;
    protected Integer         id, inventoryAdjustmentId, inventoryLocationId, 
                              quantity, physicalCount;

    public InventoryXAdjustDO() {
    }

    public InventoryXAdjustDO(Integer id, Integer inventoryAdjustmentId,
                                      Integer inventoryLocationId, Integer quantity,
                                      Integer physicalCount) {

        setId(id);
        setInventoryAdjustmentId(inventoryAdjustmentId);
        setInventoryLocationId(inventoryLocationId); 
        setQuantity(quantity);
        setPhysicalCount(physicalCount);
        _changed = false;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
        _changed = false;
    }

    public Integer getInventoryAdjustmentId() {
        return inventoryAdjustmentId;
    }

    public void setInventoryAdjustmentId(Integer inventoryAdjustmentId) {
        this.inventoryAdjustmentId = inventoryAdjustmentId;
        _changed = false;
    }

    public Integer getInventoryLocationId() {
        return inventoryLocationId;
    }

    public void setInventoryLocationId(Integer inventoryLocationId) {
        this.inventoryLocationId = inventoryLocationId;
        _changed = false;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
        _changed = false;
    }
    
    public Integer getPhysicalCount() {
        return physicalCount;
    }

    public void setPhysicalCount(Integer physicalCount) {
        this.physicalCount = physicalCount;
        _changed = false;
    }
}
