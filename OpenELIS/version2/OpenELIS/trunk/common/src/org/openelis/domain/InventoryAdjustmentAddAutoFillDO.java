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

import org.openelis.util.Datetime;
import org.openelis.utilcommon.DataBaseUtil;

public class InventoryAdjustmentAddAutoFillDO implements Serializable{

    private static final long serialVersionUID = 1L;
    protected Integer systemUserId;
    protected String systemUser;
    protected Datetime adjustmentDate;
    
    public InventoryAdjustmentAddAutoFillDO(){
        
    }
    
    public InventoryAdjustmentAddAutoFillDO(Integer systemUserId, String systemUser, Date adjustmentDate){
        setSystemUserId(systemUserId);
        setSystemUser(systemUser);
        setAdjustmentDate(adjustmentDate);
    }

    public Datetime getAdjustmentDate() {
        return adjustmentDate;
    }

    public void setAdjustmentDate(Date adjustmentDate) {
        this.adjustmentDate = new Datetime(Datetime.YEAR,Datetime.DAY,adjustmentDate);
    }

    public String getSystemUser() {
        return systemUser;
    }

    public void setSystemUser(String systemUser) {
        this.systemUser = DataBaseUtil.trim(systemUser);
    }

    public Integer getSystemUserId() {
        return systemUserId;
    }

    public void setSystemUserId(Integer systemUserId) {
        this.systemUserId = systemUserId;
    }    
}
