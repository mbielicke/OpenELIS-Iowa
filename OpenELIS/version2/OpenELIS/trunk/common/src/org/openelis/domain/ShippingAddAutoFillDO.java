package org.openelis.domain;

import java.io.Serializable;
import java.util.Date;

import org.openelis.util.DataBaseUtil;
import org.openelis.util.Datetime;

public class ShippingAddAutoFillDO implements Serializable{
    
    private static final long serialVersionUID = 1L;
    
    protected Integer status;
    protected Datetime processedDate;
    protected Datetime shippedDate;
    protected String processedBy;
    
    public ShippingAddAutoFillDO(){
        
    }
    
    public ShippingAddAutoFillDO(Integer status, Date processedDate, Date shippedDate, String processedBy){
        setStatus(status);
        setProcessedDate(processedDate);
        setShippedDate(shippedDate);
        setProcessedBy(processedBy);
    }

    public String getProcessedBy() {
        return processedBy;
    }

    public void setProcessedBy(String processedBy) {
        this.processedBy = DataBaseUtil.trim(processedBy);
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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
