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

import org.openelis.utilcommon.DataBaseUtil;

/**
 * Class represents the fields in database table order_item.
 */

public class OrderItemDO extends DataObject {

    private static final long serialVersionUID = 1L;

    protected Integer         id, orderId, inventoryItemId, quantity;
    protected String          catalogNumber;
    protected Double          unitCost;

    public OrderItemDO() {
    }

    public OrderItemDO(Integer id, Integer orderId, Integer inventoryItemId,
                       Integer quantity, String catalogNumber, Double unitCost) {
        setId(id);
        setOrderId(orderId);
        setInventoryItemId(inventoryItemId);
        setQuantity(quantity);
        setCatalogNumber(catalogNumber);
        setUnitCost(unitCost);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public Integer getInventoryItemId() {
        return inventoryItemId;
    }

    public void setInventoryItemId(Integer inventoryItemId) {
        this.inventoryItemId = inventoryItemId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getCatalogNumber() {
        return catalogNumber;
    }

    public void setCatalogNumber(String catalogNumber) {
        this.catalogNumber = DataBaseUtil.trim(catalogNumber);
    }

    public Double getUnitCost() {
        return unitCost;
    }

    public void setUnitCost(Double unitCost) {
        this.unitCost = unitCost;
    }
}
