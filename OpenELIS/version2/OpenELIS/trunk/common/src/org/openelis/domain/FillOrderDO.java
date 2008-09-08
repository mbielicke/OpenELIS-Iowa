package org.openelis.domain;

import java.io.Serializable;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;

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
    
    public AddressDO addressDO = new AddressDO();

    public FillOrderDO(){
        
    }
    
    public FillOrderDO(Integer orderId, Integer statusId, Date orderedDate, Integer shipFromId,
                       Integer shipToId, String shipTo, String description, Integer numberOfDays, /*Integer daysLeft,*/ String requestedBy,
                       Integer costCenterId, String multUnit, String streetAddress, String city,
                       String state, String zipCode){
        setOrderId(orderId);
        setStatusId(statusId);
        setOrderedDate(orderedDate);
        setShipFromId(shipFromId);
        setShipToId(shipToId);
        setShipTo(shipTo);
        setDescription(description);
        setNumberOfDays(numberOfDays);
        //setDaysLeft(daysLeft);
        setRequestedBy(requestedBy);
        setCostCenterId(costCenterId);
        
        //address values
        addressDO.setMultipleUnit(multUnit);
        addressDO.setStreetAddress(streetAddress);
        addressDO.setCity(city);
        addressDO.setState(state);
        addressDO.setZipCode(zipCode);
    }
    
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
}
