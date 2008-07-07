/**
* The contents of this file are subject to the Mozilla Public License
* Version 1.1 (the "License"); you may not use this file except in
* compliance with the License. You may obtain a copy of the License at
* http://www.mozilla.org/MPL/
* 
* Software distributed under the License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
* License for the specific language governing rights and limitations under
* the License.
* 
* The Original Code is OpenELIS code.
* 
* Copyright (C) OpenELIS.  All Rights Reserved.
*/
package org.openelis.domain;

import java.io.Serializable;
import java.util.Date;

import org.openelis.util.DataBaseUtil;
import org.openelis.util.Datetime;

public class InventoryReceiptDO implements Serializable{

    private static final long serialVersionUID = 1L;
    
    protected Integer id;
    protected Integer inventoryItemId;
    protected String inventoryItem;
    protected Integer organization;
    protected Datetime receivedDate;
    protected Integer quantityReceived;
    protected Double unitCost;
    protected String qcReference;
    protected String externalReference;
    protected String upc;

    public InventoryReceiptDO(){
        
    }
    
    public InventoryReceiptDO(Integer id, Integer inventoryItemId, String inventoryItem, Integer organization, Date receivedDate,
                              Integer quantityReceived, Double unitCost, String qcReference, String externalReference,
                              String upc){
        setId(id);
        setInventoryItemId(inventoryItemId);
        setInventoryItem(inventoryItem);
        setOrganization(organization);
        setReceivedDate(receivedDate);
        setQuantityReceived(quantityReceived);
        setUnitCost(unitCost);
        setQcReference(qcReference);
        setExternalReference(externalReference);   
        setUpc(upc);
    }
    
    public String getExternalReference() {
        return externalReference;
    }
    public void setExternalReference(String externalReference) {
        this.externalReference = DataBaseUtil.trim(externalReference);
    }
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public Integer getInventoryItemId() {
        return inventoryItemId;
    }
    public void setInventoryItemId(Integer inventoryItem) {
        this.inventoryItemId = inventoryItem;
    }
    public Integer getOrganization() {
        return organization;
    }
    public void setOrganization(Integer organization) {
        this.organization = organization;
    }
    public String getQcReference() {
        return qcReference;
    }
    public void setQcReference(String qcReference) {
        this.qcReference = DataBaseUtil.trim(qcReference);
    }
    public Integer getQuantityReceived() {
        return quantityReceived;
    }
    public void setQuantityReceived(Integer quantityReceived) {
        this.quantityReceived = quantityReceived;
    }
    public Datetime getReceivedDate() {
        return receivedDate;
    }
    public void setReceivedDate(Date receivedDate) {
        this.receivedDate = new Datetime(Datetime.YEAR,Datetime.DAY,receivedDate);
    }
    public Double getUnitCost() {
        return unitCost;
    }
    public void setUnitCost(Double unitCost) {
        this.unitCost = unitCost;
    }
    public String getUpc() {
        return upc;
    }
    public void setUpc(String upc) {
        this.upc = DataBaseUtil.trim(upc);
    }

    public String getInventoryItem() {
        return inventoryItem;
    }

    public void setInventoryItem(String inventoryItem) {
        this.inventoryItem = DataBaseUtil.trim(inventoryItem);
    }

}
