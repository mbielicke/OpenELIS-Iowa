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

import java.util.ArrayList;
import java.util.Date;

import org.openelis.gwt.common.DataBaseUtil;

public class InventoryReceiptViewDO extends InventoryReceiptDO {

    private static final long serialVersionUID = 1L;

    protected Integer                            orderItemOrderId, orderItemQuantity;

    protected String                             orderItemOrderExternalOrderNumber, addToExistingLocation;

    protected Double                             orderItemUnitCost;

    protected OrganizationDO                     organization;

    protected ArrayList<InventoryLocationViewDO> inventoryLocations;
    
    public InventoryReceiptViewDO() {
    }

    public InventoryReceiptViewDO(Integer id, Integer inventoryItemId, Integer orderItemId,
                              Integer organizationId, Date receivedDate,
                              Integer quantityReceived, Double unitCost,
                              String qcReference, String externalReference, String upc,
                              Integer orderItemQuantity, Integer orderItemOrderId,
                              String orderExternalOrderNumber, Double orderItemUnitCost) {
        super(id, inventoryItemId, orderItemId, organizationId, receivedDate,
              quantityReceived, unitCost, qcReference, externalReference, upc);
        setOrderItemQuantity(orderItemQuantity);
        setOrderItemOrderId(orderItemOrderId);
        setOrderItemOrderExternalOrderNumber(orderExternalOrderNumber);
        setOrderItemUnitCost(orderItemUnitCost);
    }
    
    public OrganizationDO getOrganization() {
        return organization;
    }

    public void setOrganization(OrganizationDO organization) {
        this.organization = organization;
    }

    public Integer getOrderItemOrderId() {
        return orderItemOrderId;
    }

    public void setOrderItemOrderId(Integer orderItemOrderId) {
        this.orderItemOrderId = orderItemOrderId;
    }

    public String getOrderItemOrderExternalOrderNumber() {
        return orderItemOrderExternalOrderNumber;
    }

    public void setOrderItemOrderExternalOrderNumber(String orderItemOrderExternalOrderNumber) {
        this.orderItemOrderExternalOrderNumber = orderItemOrderExternalOrderNumber;
    }

    public Integer getOrderItemQuantity() {
        return orderItemQuantity;
    }

    public void setOrderItemQuantity(Integer orderItemQuantity) {
        this.orderItemQuantity = orderItemQuantity;
    }
    
    public Double getOrderItemUnitCost() {
        return orderItemUnitCost;
    }

    public void setOrderItemUnitCost(Double orderItemUnitCost) {
        this.orderItemUnitCost = orderItemUnitCost;
    }    
    
    public ArrayList<InventoryLocationViewDO> getInventoryLocations() {
        return inventoryLocations;
    }

    public void setInventoryLocations(ArrayList<InventoryLocationViewDO> inventoryLocations) {
        this.inventoryLocations = inventoryLocations;
    }   

    public String getAddToExistingLocation() {
        return addToExistingLocation;
    }

    public void setAddToExistingLocation(String addToExistingLocation) {
        this.addToExistingLocation = DataBaseUtil.trim(addToExistingLocation);
    }
    
}
