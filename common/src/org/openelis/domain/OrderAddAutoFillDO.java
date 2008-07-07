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
