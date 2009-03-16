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

import java.io.Serializable;
import java.util.Date;

import org.openelis.gwt.common.data.DropDownField;
import org.openelis.gwt.common.data.NumberObject;
import org.openelis.util.DataBaseUtil;
import org.openelis.util.Datetime;

public class FillOrderDO implements Serializable{

    private static final long serialVersionUID = 1L;
    
    protected Integer orderId;
    protected Integer statusId;
    protected Datetime orderedDate;
    protected Integer shipFromId;
    protected Integer shipToId;
    protected String shipTo;
    protected String description;
    protected Integer numberOfDays;
    protected Integer daysLeft;
    protected String requestedBy;
    protected Integer costCenterId;
    protected String orderNote;
    //protected String isExternal;

    protected Integer inventoryLocationId;
    protected Integer orderItemId;
    protected Integer quantity;
    protected Integer inventoryXUseId;
    
    public AddressDO addressDO = new AddressDO();

    public FillOrderDO(){
        
    }
    
    public FillOrderDO(Integer orderId, Integer statusId, Date orderedDate, Integer shipFromId, Integer shipToId, 
                       String shipTo, String description, Integer numberOfDays) {
        setOrderId(orderId);
        setStatusId(statusId);
        setOrderedDate(orderedDate);
        setShipFromId(shipFromId);
        setShipToId(shipToId);
        setShipTo(shipTo);
        setDescription(description);
        setNumberOfDays(numberOfDays);
    }
    
    public FillOrderDO(String requestedBy, Integer costCenterId, String multUnit, String streetAddress, String city, 
                       String state, String zipCode) {
        setRequestedBy(requestedBy);
        setCostCenterId(costCenterId);
        addressDO.setMultipleUnit(multUnit);
        addressDO.setStreetAddress(streetAddress);
        addressDO.setCity(city);
        addressDO.setState(state);
        addressDO.setZipCode(zipCode);
        
    }

    /*
    public FillOrderDO(Integer orderId, Integer statusId, Date orderedDate, Integer shipFromId,
                       Integer shipToId, String shipTo, String description, Integer numberOfDays, String requestedBy,
                       Integer costCenterId, String isExternal, String multUnit, String streetAddress, String city,
                       String state, String zipCode){
        setOrderId(orderId);
        setStatusId(statusId);
        setOrderedDate(orderedDate);
        setShipFromId(shipFromId);
        setShipToId(shipToId);
        setShipTo(shipTo);
        setDescription(description);
        setNumberOfDays(numberOfDays);
        setRequestedBy(requestedBy);
        setCostCenterId(costCenterId);

        
        //address values
        addressDO.setMultipleUnit(multUnit);
        addressDO.setStreetAddress(streetAddress);
        addressDO.setCity(city);
        addressDO.setState(state);
        addressDO.setZipCode(zipCode);
    }*/
    
    public Integer getDaysLeft() {
        return daysLeft;
    }

    public void setDaysLeft(Integer daysLeft) {
        this.daysLeft = daysLeft;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = DataBaseUtil.trim(description);
    }

    public Integer getNumberOfDays() {
        return numberOfDays;
    }

    public void setNumberOfDays(Integer numberOfDays) {
        this.numberOfDays = numberOfDays;
    }

    public Datetime getOrderedDate() {
        return orderedDate;
    }

    public void setOrderedDate(Date orderedDate) {
        this.orderedDate = new Datetime(Datetime.YEAR, Datetime.DAY, orderedDate);
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public Integer getShipFromId() {
        return shipFromId;
    }

    public void setShipFromId(Integer shipFromId) {
        this.shipFromId = shipFromId;
    }

    public String getShipTo() {
        return shipTo;
    }

    public void setShipTo(String shipTo) {
        this.shipTo = DataBaseUtil.trim(shipTo);
    }

    public Integer getStatusId() {
        return statusId;
    }

    public void setStatusId(Integer statusId) {
        this.statusId = statusId;
    }

    public Integer getCostCenterId() {
        return costCenterId;
    }

    public void setCostCenterId(Integer costCenterId) {
        this.costCenterId = costCenterId;
    }

    public String getRequestedBy() {
        return requestedBy;
    }

    public void setRequestedBy(String requestedBy) {
        this.requestedBy = DataBaseUtil.trim(requestedBy);
    }

    public Integer getShipToId() {
        return shipToId;
    }

    public void setShipToId(Integer shipToId) {
        this.shipToId = shipToId;
    }

    public Integer getOrderItemId() {
        return orderItemId;
    }

    public void setOrderItemId(Integer orderItemId) {
        this.orderItemId = orderItemId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getOrderNote() {
        return DataBaseUtil.trim(orderNote);
    }

    public void setOrderNote(String orderNote) {
        this.orderNote = orderNote;
    }

    public Integer getInventoryXUseId() {
        return inventoryXUseId;
    }

    public void setInventoryXUseId(Integer inventoryXUseId) {
        this.inventoryXUseId = inventoryXUseId;
    }

    public Integer getInventoryLocationId() {
        return inventoryLocationId;
    }

    public void setInventoryLocationId(Integer inventoryLocationId) {
        this.inventoryLocationId = inventoryLocationId;
    }
}
