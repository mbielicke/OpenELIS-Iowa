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
* Copyright (C) The University of Iowa.  All Rights Reserved.
*/
package org.openelis.domain;

import java.io.Serializable;
import java.util.Date;

import org.openelis.util.DataBaseUtil;
import org.openelis.util.Datetime;

public class ShippingDO implements Serializable{

    private static final long serialVersionUID = 1L;
    
    protected Integer id;
    protected Integer statusId;
    protected Integer shippedFromId;
    protected Integer shippedToId;
    protected String shippedTo;
    protected Integer processedById;
    protected String processedBy;
    protected Datetime processedDate;
    protected Integer shippedMethodId;
    protected Datetime shippedDate;
    protected Integer numberOfPackages;
    protected double cost;
    
    public AddressDO addressDO = new AddressDO();
    
    public ShippingDO(){
        
    }
    
    //without address info
    public ShippingDO(Integer id, Integer statusId, Integer shippedFromId, Integer shippedToId, String shippedTo,
                      Integer processedById, Date processedDate, Integer shippedMethodId,
                      Date shippedDate, Integer numberOfPackages, double cost){
        setId(id);
        setStatusId(statusId);
        setShippedFromId(shippedFromId);
        setShippedToId(shippedToId);
        setShippedTo(shippedTo);
        setProcessedById(processedById);
        setProcessedDate(processedDate);
        setShippedMethodId(shippedMethodId);
        setShippedDate(shippedDate);
        setNumberOfPackages(numberOfPackages);
        setCost(cost);
    }
    
    //with address info
    public ShippingDO(Integer id, Integer statusId, Integer shippedFromId, Integer shippedToId, String shippedTo,
                      Integer processedById, Date processedDate, Integer shippedMethodId,
                      Date shippedDate, Integer numberOfPackages, double cost,
                      String multUnit, String streetAddress, String city, String state, String zipCode){
        setId(id);
        setStatusId(statusId);
        setShippedFromId(shippedFromId);
        setShippedToId(shippedToId);
        setShippedTo(shippedTo);
        setProcessedById(processedById);
        setProcessedDate(processedDate);
        setShippedMethodId(shippedMethodId);
        setShippedDate(shippedDate);
        setNumberOfPackages(numberOfPackages);
        setCost(cost);
        
        //address info
        addressDO.setMultipleUnit(multUnit);
        addressDO.setStreetAddress(streetAddress);
        addressDO.setCity(city);
        addressDO.setState(state);
        addressDO.setZipCode(zipCode);
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getNumberOfPackages() {
        return numberOfPackages;
    }

    public void setNumberOfPackages(Integer numberOfPackages) {
        this.numberOfPackages = numberOfPackages;
    }

    public Integer getProcessedById() {
        return processedById;
    }

    public void setProcessedById(Integer processedById) {
        this.processedById = processedById;
    }

    public Datetime getProcessedDate() {
        return processedDate;
    }

    public void setProcessedDate(Date processedDate) {
        this.processedDate = new Datetime(Datetime.YEAR, Datetime.DAY, processedDate);
    }

    public Datetime getShippedDate() {
        return shippedDate;
    }

    public void setShippedDate(Date shippedDate) {
        this.shippedDate = new Datetime(Datetime.YEAR, Datetime.DAY, shippedDate);
    }

    public Integer getShippedFromId() {
        return shippedFromId;
    }

    public void setShippedFromId(Integer shippedFromId) {
        this.shippedFromId = shippedFromId;
    }

    public Integer getShippedMethodId() {
        return shippedMethodId;
    }

    public void setShippedMethodId(Integer shippedMethodId) {
        this.shippedMethodId = shippedMethodId;
    }

    public Integer getShippedToId() {
        return shippedToId;
    }

    public void setShippedToId(Integer shippedToId) {
        this.shippedToId = shippedToId;
    }
    
    public String getShippedTo() {
        return shippedTo;
    }

    public void setShippedTo(String shippedTo) {
        this.shippedTo = shippedTo;
    }

    public Integer getStatusId() {
        return statusId;
    }

    public void setStatusId(Integer statusId) {
        this.statusId = statusId;
    }

    public String getProcessedBy() {
        return processedBy;
    }

    public void setProcessedBy(String processedBy) {
        this.processedBy = DataBaseUtil.trim(processedBy);
    }
}
