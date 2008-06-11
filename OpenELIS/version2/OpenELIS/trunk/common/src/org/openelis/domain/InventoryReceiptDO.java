package org.openelis.domain;

import java.io.Serializable;
import java.util.Date;

import org.openelis.util.DataBaseUtil;
import org.openelis.util.Datetime;

public class InventoryReceiptDO implements Serializable{

    private static final long serialVersionUID = 1L;
    
    protected Integer id;
    protected Integer inventoryItem;
    protected Integer organization;
    protected Datetime receivedDate;
    protected Integer quantityReceived;
    protected Double unitCost;
    protected String qcReference;
    protected String externalReference;
    protected String upc;

    public InventoryReceiptDO(){
        
    }
    
    public InventoryReceiptDO(Integer id, Integer inventoryItem, Integer organization, Date receivedDate,
                              Integer quantityReceived, Double unitCost, String qcReference, String externalReference,
                              String upc){
        setId(id);
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
    public Integer getInventoryItem() {
        return inventoryItem;
    }
    public void setInventoryItem(Integer inventoryItem) {
        this.inventoryItem = inventoryItem;
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
        upc = DataBaseUtil.trim(upc);
    }

}
