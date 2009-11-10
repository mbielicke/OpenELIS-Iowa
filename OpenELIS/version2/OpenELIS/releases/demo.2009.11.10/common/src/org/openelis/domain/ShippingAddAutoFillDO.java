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

import java.util.Date;

import org.openelis.gwt.common.Datetime;
import org.openelis.gwt.common.RPC;
import org.openelis.utilcommon.DataBaseUtil;

public class ShippingAddAutoFillDO implements RPC {

    private static final long serialVersionUID = 1L;

    protected Integer         status;
    protected Datetime        processedDate;
    protected String          processedBy;
    protected Integer         systemUserId;

    public ShippingAddAutoFillDO() {

    }

    public ShippingAddAutoFillDO(Integer status,
                                 Date processedDate,
                                 String processedBy,
                                 Integer systemUserId) {
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
        this.processedDate = new Datetime(Datetime.YEAR,
                                          Datetime.DAY,
                                          processedDate);
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
