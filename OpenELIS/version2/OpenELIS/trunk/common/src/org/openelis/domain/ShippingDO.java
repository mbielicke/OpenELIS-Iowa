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
import org.openelis.utilcommon.DataBaseUtil;

/**
 * Class represents the fields in database table shipping.
 */
public class ShippingDO extends DataObject {

    private static final long serialVersionUID = 1L;

    protected Integer         id, statusId, shippedFromId, shippedToId,
                              shippedMethodId, numberOfPackages;
    protected String          processedBy;
    protected Datetime        processedDate, shippedDate;
    protected Double          cost;

    public ShippingDO() {
    }

    public ShippingDO(Integer id, Integer statusId, Integer shippedFromId,
                      Integer shippedToId,  String processedBy,
                      Date processedDate, Integer shippedMethodId, Date shippedDate,
                      Integer numberOfPackages, Double cost) {
        setId(id);
        setStatusId(statusId);
        setShippedFromId(shippedFromId);
        setShippedToId(shippedToId);
        setProcessedBy(processedBy);
        setProcessedDate(DataBaseUtil.toYD(processedDate));
        setShippedMethodId(shippedMethodId);
        setShippedDate(DataBaseUtil.toYD(shippedDate));
        setNumberOfPackages(numberOfPackages);
        setCost(cost);
        _changed = false;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
        _changed = true;
    }
    
    public Integer getStatusId() {
        return statusId;
    }

    public void setStatusId(Integer statusId) {
        this.statusId = statusId;
        _changed = true;
    }
    
    public Integer getShippedFromId() {
        return shippedFromId;
    }

    public void setShippedFromId(Integer shippedFromId) {
        this.shippedFromId = shippedFromId;
        _changed = true;
    }
    
    public Integer getShippedToId() {
        return shippedToId;
    }

    public void setShippedToId(Integer shippedToId) {
        this.shippedToId = shippedToId;
        _changed = true;
    }  
    
    public String getProcessedBy() {
        return processedBy;
    }

    public void setProcessedBy(String processedBy) {
        this.processedBy = DataBaseUtil.trim(processedBy);
        _changed = true;
    }
    
    public Datetime getProcessedDate() {
        return processedDate;
    }

    public void setProcessedDate(Datetime processedDate) {
        this.processedDate = DataBaseUtil.toYD(processedDate);
        _changed = true;
    }
    
    public Integer getShippedMethodId() {
        return shippedMethodId;
    }

    public void setShippedMethodId(Integer shippedMethodId) {
        this.shippedMethodId = shippedMethodId;
        _changed = true;
    }
    
    public Datetime getShippedDate() {
        return shippedDate;
    }

    public void setShippedDate(Datetime shippedDate) {
        this.shippedDate = DataBaseUtil.toYD(shippedDate);
        _changed = true;
    }
    
    public Integer getNumberOfPackages() {
        return numberOfPackages;
    }

    public void setNumberOfPackages(Integer numberOfPackages) {
        this.numberOfPackages = numberOfPackages;
        _changed = true;
    }
    
    public Double getCost() {
        return cost;
    }

    public void setCost(Double cost) {
        this.cost = cost;
        _changed = true;
    }
}
