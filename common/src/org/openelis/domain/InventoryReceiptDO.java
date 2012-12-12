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

import java.util.Date;

import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.Datetime;

/**
 * Class represents the fields in database table shipping.
 */
public class InventoryReceiptDO extends DataObject {

    private static final long serialVersionUID = 1L;

    protected Integer         id, inventoryItemId, orderItemId, organizationId,
                              quantityReceived;
    protected Datetime        receivedDate;
    protected Double          unitCost;
    protected String          qcReference, externalReference, upc;   

    public InventoryReceiptDO() {
    }     
    
    public InventoryReceiptDO(Integer id, Integer inventoryItemId, Integer orderItemId,
                              Integer organizationId, Date receivedDate,
                              Integer quantityReceived, Double unitCost,
                              String qcReference, String externalReference, String upc) {
        setId(id);
        setInventoryItemId(inventoryItemId);
        setOrderItemId(orderItemId);
        setOrganizationId(organizationId);
        setReceivedDate(DataBaseUtil.toYD(receivedDate));
        setQuantityReceived(quantityReceived);
        setUnitCost(unitCost);
        setQcReference(qcReference);
        setExternalReference(externalReference);
        setUpc(upc);
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

    public void setInventoryItemId(Integer inventoryItem) {
        this.inventoryItemId = inventoryItem;
        _changed = true;
    }
    
    public Integer getOrderItemId() {
        return orderItemId;
    }

    public void setOrderItemId(Integer orderItemId) {
        this.orderItemId = orderItemId;
        _changed = true;
    }
    
    public Integer getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Integer organization) {
        this.organizationId = organization;
        _changed = true;
    }
    
    public Integer getQuantityReceived() {
        return quantityReceived;
    }

    public void setQuantityReceived(Integer quantityReceived) {
        this.quantityReceived = quantityReceived;
        _changed = true;
    }
    
    public String getExternalReference() {
        return externalReference;
    }

    public void setExternalReference(String externalReference) {
        this.externalReference = DataBaseUtil.trim(externalReference);
        _changed = true;
    }

    public String getQcReference() {
        return qcReference;
    }

    public void setQcReference(String qcReference) {
        this.qcReference = DataBaseUtil.trim(qcReference);
        _changed = true;
    }

    public Datetime getReceivedDate() {
        return receivedDate;
    }

    public void setReceivedDate(Datetime receivedDate) {
        this.receivedDate = DataBaseUtil.toYD(receivedDate);
        _changed = true;
    }

    public Double getUnitCost() {
        return unitCost;
    }

    public void setUnitCost(Double unitCost) {
        this.unitCost = unitCost;
        _changed = true;
    }

    public String getUpc() {
        return upc;
    }

    public void setUpc(String upc) {
        this.upc = DataBaseUtil.trim(upc);
        _changed = true;
    }
}
