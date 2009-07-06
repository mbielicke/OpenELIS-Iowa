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

import org.openelis.utilcommon.DataBaseUtil;
import org.openelis.util.Datetime;


public class QcDO implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    protected Integer id;             
    protected String name;             
    protected Integer typeId;            
    protected Integer inventoryItemId;
    protected String inventoryItemName;
    protected String source;             
    protected String lotNumber;             
    protected Datetime preparedDate;             
    protected Double preparedVolume;             
    protected Integer preparedUnitId;             
    protected Integer preparedById; 
    protected String preparedByName;
    protected Datetime usableDate;             
    protected Datetime expireDate;             
    protected String isSingleUse;   
    
    public QcDO() {
        
    }
    
    public QcDO(Integer id,String name,Integer typeId,Integer inventoryItemId,String inventoryItemName,
                String source,String lotNumber,Date preparedDate,Double preparedVolume,
                Integer preparedUnitId,Integer preparedById,Date usableDate,
                Date expireDate,String isSingleUse) {
        setId(id);             
        setName(name);             
        setTypeId(typeId);            
        setInventoryItemId(inventoryItemId);
        setInventoryItemName(inventoryItemName);
        setSource(source);             
        setLotNumber(lotNumber);             
        setPreparedDate(preparedDate);             
        setPreparedVolume(preparedVolume);             
        setPreparedUnitId(preparedUnitId);             
        setPreparedById(preparedById);             
        setUsableDate(usableDate);             
        setExpireDate(expireDate);             
        setIsSingleUse(isSingleUse);        
    }
    
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = DataBaseUtil.trim(name);
    }
    public Integer getTypeId() {
        return typeId;
    }
    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
    }
    public Integer getInventoryItemId() {
        return inventoryItemId;
    }
    public void setInventoryItemId(Integer inventoryItemId) {
        this.inventoryItemId = inventoryItemId;
    }
    
    public String getInventoryItemName() {
        return inventoryItemName;
    }

    public void setInventoryItemName(String inventoryItemName) {
        this.inventoryItemName = DataBaseUtil.trim(inventoryItemName);
    } 
    
    public String getSource() {
        return source;
    }
    public void setSource(String source) {
        this.source = DataBaseUtil.trim(source);
    }
    public String getLotNumber() {
        return lotNumber;
    }
    public void setLotNumber(String lotNumber) {
        this.lotNumber = DataBaseUtil.trim(lotNumber);
    }
    public Double getPreparedVolume() {
        return preparedVolume;
    }
    public void setPreparedVolume(Double preparedVolume) {
        this.preparedVolume = preparedVolume;
    }
    public Integer getPreparedUnitId() {
        return preparedUnitId;
    }
    public void setPreparedUnitId(Integer preparedUnitId) {
        this.preparedUnitId = preparedUnitId;
    }
    public Integer getPreparedById() {
        return preparedById;
    }
    public void setPreparedById(Integer preparedById) {
        this.preparedById = preparedById;
    }
    public String getIsSingleUse() {
        return isSingleUse;
    }
    public void setIsSingleUse(String isSingleUse) {
        this.isSingleUse = DataBaseUtil.trim(isSingleUse);
    }

    public Datetime getPreparedDate() {
        return preparedDate;
    }

    public void setPreparedDate(Date preparedDate) {
        this.preparedDate = new Datetime(Datetime.YEAR, Datetime.SECOND, preparedDate);
    }

    public Datetime getUsableDate() {
        return usableDate;
    }

    public void setUsableDate(Date usableDate) {
        this.usableDate = new Datetime(Datetime.YEAR, Datetime.SECOND, usableDate);
    }

    public Datetime getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(Date expireDate) {
        this.expireDate = new Datetime(Datetime.YEAR, Datetime.SECOND, expireDate);
    }

    public String getPreparedByName() {
        return preparedByName;
    }

    public void setPreparedByName(String preparedByName) {
        this.preparedByName = preparedByName;
    }
   
}
