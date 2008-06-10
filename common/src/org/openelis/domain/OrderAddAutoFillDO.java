package org.openelis.domain;

import java.io.Serializable;
import java.util.Date;

import org.openelis.util.DataBaseUtil;
import org.openelis.util.Datetime;

public class OrderAddAutoFillDO implements Serializable{

    private static final long serialVersionUID = 1L;
    
    protected Integer status;
    protected Datetime orderedDate;
    protected String requestedBy;
    
    public OrderAddAutoFillDO(){
        
    }
    
    public OrderAddAutoFillDO(Integer status, Date orderedDate, String requestedBy){
        setStatus(status);
        setOrderedDate(orderedDate);
        setRequestedBy(requestedBy);
    }

    public Datetime getOrderedDate() {
        return orderedDate;
    }

    public void setOrderedDate(Date orderedDate) {
        this.orderedDate = new Datetime(Datetime.YEAR,Datetime.DAY, orderedDate);
    }

    public String getRequestedBy() {
        return requestedBy;
    }

    public void setRequestedBy(String requestedBy) {
        this.requestedBy = DataBaseUtil.trim(requestedBy);
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

}
