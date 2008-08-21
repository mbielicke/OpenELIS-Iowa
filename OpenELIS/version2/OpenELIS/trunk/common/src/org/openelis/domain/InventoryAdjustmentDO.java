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



public class InventoryAdjustmentDO implements Serializable{

    private static final long serialVersionUID = 1L;
    protected Integer id;
    protected String description;
    protected Integer systemUserId;
    protected String systemUser;
    protected Datetime adjustmentDate;
    protected Integer storeId;
    
    public Integer getStoreId() {
        return storeId;
    }

    public void setStoreId(Integer storeId) {
        this.storeId = storeId;
    }

    public InventoryAdjustmentDO(){
        
    }
    
    public InventoryAdjustmentDO(Integer id, String description, Integer systemUserId, String systemUser, 
                                 Date adjustmentDate){
        setId(id);
        setDescription(description);
        setSystemUserId(systemUserId);
        setSystemUser(systemUser);
        setAdjustmentDate(adjustmentDate);
    }
    
    public InventoryAdjustmentDO(Integer id, String description, Integer systemUserId, Date adjustmentDate, Integer storeId){
        setId(id);
        setDescription(description);
        setSystemUserId(systemUserId);
        setAdjustmentDate(adjustmentDate);
        setStoreId(storeId);
    }

    public Datetime getAdjustmentDate() {
        return adjustmentDate;
    }

    public void setAdjustmentDate(Date adjustmentDate) {
        this.adjustmentDate = new Datetime(Datetime.YEAR,Datetime.DAY,adjustmentDate);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = DataBaseUtil.trim(description);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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
