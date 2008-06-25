package org.openelis.domain;

import java.io.Serializable;
import java.util.Date;

import org.openelis.util.DataBaseUtil;
import org.openelis.util.Datetime;

import com.sun.org.apache.regexp.internal.REProgram;

public class OrderDO implements Serializable{

    private static final long serialVersionUID = 1L;
    
    protected Integer id;
    protected Integer statusId;
    protected Datetime orderedDate;
    protected Integer neededInDays;
    protected String requestedBy;
    protected Integer costCenter;
    protected Integer organizationId;
    protected String organization;
    public AddressDO organizationAddressDO = new AddressDO();
    protected String isExternal;
    protected String externalOrderNumber;
    protected Integer reportToId;
    protected String reportTo;
    public AddressDO reportToAddressDO = new AddressDO();
    protected Integer billToId;
    protected String billTo;
    public AddressDO billToAddressDO = new AddressDO();
    
    public OrderDO(){
        
    }

    //constructor without address values
    public OrderDO(Integer id, Integer status, Date orderedDate, Integer neededInDays, String requestedBy,
                   Integer costCenter, Integer organizationId, String isExternal, String externalOrderNumber, 
                   Integer reportToId, Integer billToId){
        setId(id);
        setStatusId(status);
        setOrderedDate(orderedDate);
        setNeededInDays(neededInDays);
        setRequestedBy(requestedBy);
        setCostCenter(costCenter);
        setOrganizationId(organizationId);
        setOrganization(organization);
        setIsExternal(isExternal);
        setExternalOrderNumber(externalOrderNumber);
        setReportToId(reportToId);
        setBillToId(billToId);

    }

    //constructor with org address values
    public OrderDO(Integer id, Integer status, Date orderedDate, Integer neededInDays, String requestedBy,
                   Integer costCenter, Integer organizationId, String organization, String orgMultUnit, String orgStreetAddress, String orgCity, 
                   String orgState, String orgZipCode, String isExternal, String externalOrderNumber, 
                   Integer reportToId, Integer billToId){
        setId(id);
        setStatusId(status);
        setOrderedDate(orderedDate);
        setNeededInDays(neededInDays);
        setRequestedBy(requestedBy);
        setCostCenter(costCenter);
        setOrganizationId(organizationId);
        setOrganization(organization);
        setIsExternal(isExternal);
        setExternalOrderNumber(externalOrderNumber);
        setReportToId(reportToId);
        setBillToId(billToId);
        
        //set org address values
        organizationAddressDO.setMultipleUnit(orgMultUnit);
        organizationAddressDO.setStreetAddress(orgStreetAddress);
        organizationAddressDO.setCity(orgCity);
        organizationAddressDO.setState(orgState);
        organizationAddressDO.setZipCode(orgZipCode);
    }
    
    //constructor with address values
    public OrderDO(Integer id, Integer status, Date orderedDate, Integer neededInDays, String requestedBy,
                   Integer costCenter, Integer organizationId, String organization, String orgMultUnit, String orgStreetAddress, String orgCity, 
                   String orgState, String orgZipCode, String isExternal, String externalOrderNumber, Integer reportToId, String reportTo, 
                   String reportToMultUnit, String reportToStreetAddress, String reportToCity, String reportToState, String reportToZipCode,
                   Integer billToId, String billTo, String billToMultUnit, String billToStreetAddress, String billToCity, String billToState,
                   String billToZipCode){
        setId(id);
        setStatusId(status);
        setOrderedDate(orderedDate);
        setNeededInDays(neededInDays);
        setRequestedBy(requestedBy);
        setCostCenter(costCenter);
        setOrganizationId(organizationId);
        setOrganization(organization);
        setIsExternal(isExternal);
        setExternalOrderNumber(externalOrderNumber);
        setReportToId(reportToId);
        setReportTo(reportTo);
        setBillToId(billToId);
        setBillTo(billTo);   
        
        //set org address values
        organizationAddressDO.setMultipleUnit(orgMultUnit);
        organizationAddressDO.setStreetAddress(orgStreetAddress);
        organizationAddressDO.setCity(orgCity);
        organizationAddressDO.setState(orgState);
        organizationAddressDO.setZipCode(orgZipCode);
        
        //set report to address values
        reportToAddressDO.setMultipleUnit(reportToMultUnit);
        reportToAddressDO.setStreetAddress(reportToStreetAddress);
        reportToAddressDO.setCity(reportToCity);
        reportToAddressDO.setState(reportToState);
        reportToAddressDO.setZipCode(reportToZipCode);
        
        //set bill to address values
        billToAddressDO.setMultipleUnit(billToMultUnit);
        billToAddressDO.setStreetAddress(billToStreetAddress);
        billToAddressDO.setCity(billToCity);
        billToAddressDO.setState(billToState);
        billToAddressDO.setZipCode(billToZipCode);
    }

    public Integer getBillToId() {
        return billToId;
    }

    public void setBillToId(Integer billToId) {
        this.billToId = billToId;
    }
    
    public String getBillTo() {
        return billTo;
    }

    public void setBillTo(String billTo) {
        this.billTo = DataBaseUtil.trim(billTo);
    }

    public Integer getCostCenter() {
        return costCenter;
    }

    public void setCostCenter(Integer costCenter) {
        this.costCenter = costCenter;
    }

    public String getExternalOrderNumber() {
        return externalOrderNumber;
    }

    public void setExternalOrderNumber(String externalOrderNumber) {
        this.externalOrderNumber = DataBaseUtil.trim(externalOrderNumber);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getIsExternal() {
        return isExternal;
    }

    public void setIsExternal(String isExternal) {
        this.isExternal = DataBaseUtil.trim(isExternal);
    }

    public Integer getNeededInDays() {
        return neededInDays;
    }

    public void setNeededInDays(Integer neededInDays) {
        this.neededInDays = neededInDays;
    }

    public Datetime getOrderedDate() {
        return orderedDate;
    }

    public void setOrderedDate(Date orderedDate) {
        this.orderedDate = new Datetime(Datetime.YEAR,Datetime.DAY,orderedDate);
    }

    public Integer getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Integer organizationId) {
        this.organizationId = organizationId;
    }
    
    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = DataBaseUtil.trim(organization);
    }

    public Integer getReportToId() {
        return reportToId;
    }

    public void setReportToId(Integer reportToId) {
        this.reportToId = reportToId;
    }
    
    public String getReportTo() {
        return reportTo;
    }

    public void setReportTo(String reportTo) {
        this.reportTo = DataBaseUtil.trim(reportTo);
    }

    public String getRequestedBy() {
        return requestedBy;
    }

    public void setRequestedBy(String requestedBy) {
        this.requestedBy = DataBaseUtil.trim(requestedBy);
    }

    public Integer getStatusId() {
        return statusId;
    }

    public void setStatusId(Integer status) {
        this.statusId = status;
    }
    

}
