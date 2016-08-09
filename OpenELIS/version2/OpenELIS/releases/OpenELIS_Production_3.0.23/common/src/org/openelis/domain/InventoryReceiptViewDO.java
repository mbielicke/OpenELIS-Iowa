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

import org.openelis.ui.common.DataBaseUtil;

public class InventoryReceiptViewDO extends InventoryReceiptDO {

    private static final long serialVersionUID = 1L;

    protected Integer                            iorderItemOrderId, iorderItemQuantity;

    protected String                             iorderItemIorderExternalOrderNumber, addToExistingLocation;

    protected Double                             iorderItemUnitCost;

    protected OrganizationDO                     organization;

    protected ArrayList<InventoryLocationViewDO> inventoryLocations;
    
    public InventoryReceiptViewDO() {
    }

    public InventoryReceiptViewDO(Integer id, Integer inventoryItemId, Integer iorderItemId,
                              Integer organizationId, Date receivedDate,
                              Integer quantityReceived, Double unitCost,
                              String qcReference, String externalReference, String upc,
                              Integer iorderItemQuantity, Integer iorderItemIorderId,
                              String iorderExternalOrderNumber, Double iorderItemUnitCost) {
        super(id, inventoryItemId, iorderItemId, organizationId, receivedDate,
              quantityReceived, unitCost, qcReference, externalReference, upc);
        setIorderItemQuantity(iorderItemQuantity);
        setIorderItemIorderId(iorderItemIorderId);
        setIorderItemIorderExternalOrderNumber(iorderExternalOrderNumber);
        setIorderItemUnitCost(iorderItemUnitCost);
    }
    
    public OrganizationDO getOrganization() {
        return organization;
    }

    public void setOrganization(OrganizationDO organization) {
        this.organization = organization;
    }

    public Integer getIorderItemIorderId() {
        return iorderItemOrderId;
    }

    public void setIorderItemIorderId(Integer iorderItemOrderId) {
        this.iorderItemOrderId = iorderItemOrderId;
    }

    public String getIorderItemIorderExternalOrderNumber() {
        return iorderItemIorderExternalOrderNumber;
    }

    public void setIorderItemIorderExternalOrderNumber(String iorderItemIorderExternalOrderNumber) {
        this.iorderItemIorderExternalOrderNumber = iorderItemIorderExternalOrderNumber;
    }

    public Integer getIorderItemQuantity() {
        return iorderItemQuantity;
    }

    public void setIorderItemQuantity(Integer iorderItemQuantity) {
        this.iorderItemQuantity = iorderItemQuantity;
    }
    
    public Double getIorderItemUnitCost() {
        return iorderItemUnitCost;
    }

    public void setIorderItemUnitCost(Double iorderItemUnitCost) {
        this.iorderItemUnitCost = iorderItemUnitCost;
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
