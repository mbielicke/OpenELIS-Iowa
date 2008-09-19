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

public class ShippingAddAutoFillDO implements Serializable{
    
    private static final long serialVersionUID = 1L;
    
    protected Integer status;
    protected Datetime processedDate;
    protected String processedBy;
    protected Integer systemUserId;
    
    public ShippingAddAutoFillDO(){
        
    }
    
    public ShippingAddAutoFillDO(Integer status, Date processedDate, String processedBy, Integer systemUserId){
        setStatus(status);
        setProcessedDate(processedDate);
        setProcessedBy(processedBy);
        setSystemUserId(systemUserId);
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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getSystemUserId() {
        return systemUserId;
    }

    public void setSystemUserId(Integer systemUserId) {
        this.systemUserId = systemUserId;
    }
}
