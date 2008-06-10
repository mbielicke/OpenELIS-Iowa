package org.openelis.domain;

import java.io.Serializable;

import org.openelis.util.DataBaseUtil;

public class BillToReportToDO implements Serializable{

    private static final long serialVersionUID = 1L;
    protected Integer billToId;
    protected String billTo;
    public AddressDO billToAddress = new AddressDO();
    
    protected Integer reportToId;
    protected String reportTo;
    public AddressDO reportToAddress = new AddressDO();
    
    public BillToReportToDO(){
        
    }
    
    public BillToReportToDO(Integer billToId, String billTo, String billToMultUnit, String billToStreetAddress, String billToCity, String billToState, String billToZipcode, 
                            Integer reportToId, String reportTo, String reportToMultUnit, String reportToStreetAddress, String reportToCity, String reportToState, String reportToZipcode){
        setBillToId(billToId);
        setBillTo(billTo);
        billToAddress.setMultipleUnit(billToMultUnit);
        billToAddress.setStreetAddress(billToStreetAddress);
        billToAddress.setCity(billToCity);
        billToAddress.setState(billToState);
        billToAddress.setZipCode(billToZipcode);
        
        setReportToId(reportToId);
        setReportTo(reportTo);
        reportToAddress.setMultipleUnit(reportToMultUnit);
        reportToAddress.setStreetAddress(reportToStreetAddress);
        reportToAddress.setCity(reportToCity);
        reportToAddress.setState(reportToState);
        reportToAddress.setZipCode(reportToZipcode);
    }
    
    public Integer getBillToId() {
        return billToId;
    }
    
    public void setBillToId(Integer billToId) {
        this.billToId = billToId;
    }
    
    public Integer getReportToId() {
        return reportToId;
    }
    
    public void setReportToId(Integer reportToId) {
        this.reportToId = reportToId;
    }

    public String getBillTo() {
        return billTo;
    }

    public void setBillTo(String billTo) {
        this.billTo = DataBaseUtil.trim(billTo);
    }

    public String getReportTo() {
        return reportTo;
    }

    public void setReportTo(String reportTo) {
        this.reportTo = DataBaseUtil.trim(reportTo);
    }
}
