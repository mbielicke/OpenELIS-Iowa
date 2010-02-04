/**
 * Exhibit A - UIRF Open-source Based Public Software License.
 * 
 * The contents of this file are subject to the UIRF Open-source Based Public
 * Software License(the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * openelis.uhl.uiowa.edu
 * 
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 * 
 * The Original Code is OpenELIS code.
 * 
 * The Initial Developer of the Original Code is The University of Iowa.
 * Portions created by The University of Iowa are Copyright 2006-2008. All
 * Rights Reserved.
 * 
 * Contributor(s): ______________________________________.
 * 
 * Alternatively, the contents of this file marked "Separately-Licensed" may be
 * used under the terms of a UIRF Software license ("UIRF Software License"), in
 * which case the provisions of a UIRF Software License are applicable instead
 * of those above.
 */
package org.openelis.domain;

import java.util.Date;

import org.openelis.gwt.common.Datetime;
import org.openelis.utilcommon.DataBaseUtil;

/**
 * Class represents the fields in database table order.
 */

public class OrderDO extends DataObject {

    private static final long serialVersionUID = 1L;

    protected Integer         id, statusId, neededInDays, costCenterId, organizationId, reportToId,
                              billToId, shipFromId;
    protected String          description, requestedBy, type, externalOrderNumber;
    protected Datetime        orderedDate;

    public OrderDO() {
    }

    public OrderDO(Integer id, String description, Integer statusId, Date orderedDate, 
                   Integer neededInDays, String requestedBy, Integer costCenterId, 
                   Integer organizationId, String type, String externalOrderNumber,
                   Integer reportToId, Integer billToId, Integer shipFromId) {
        setId(id);
        setDescription(description);
        setStatusId(statusId);
        setOrderedDate(DataBaseUtil.toYM(orderedDate));
        setNeededInDays(neededInDays);
        setRequestedBy(requestedBy);
        setCostCenterId(costCenterId);
        setOrganizationId(organizationId);
        setType(type);
        setExternalOrderNumber(externalOrderNumber);
        setReportToId(reportToId);
        setBillToId(billToId);
        setShipFromId(shipFromId);
        _changed = false;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
        _changed = true;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = DataBaseUtil.trim(description);
        _changed = true;
    }

    public Integer getStatusId() {
        return statusId;
    }

    public void setStatusId(Integer statusId) {
        this.statusId = statusId;
        _changed = true;
    }

    public Datetime getOrderedDate() {
        return orderedDate;
    }

    public void setOrderedDate(Datetime orderedDate) {
        this.orderedDate = DataBaseUtil.toYM(orderedDate);
        _changed = true;
    }

    public Integer getNeededInDays() {
        return neededInDays;
    }

    public void setNeededInDays(Integer neededInDays) {
        this.neededInDays = neededInDays;
        _changed = true;
    }

    public String getRequestedBy() {
        return requestedBy;
    }

    public void setRequestedBy(String requestedBy) {
        this.requestedBy = DataBaseUtil.trim(requestedBy);
        _changed = true;
    }

    public Integer getCostCenterId() {
        return costCenterId;
    }

    public void setCostCenterId(Integer costCenterId) {
        this.costCenterId = costCenterId;
        _changed = true;
    }

    public Integer getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Integer organizationId) {
        this.organizationId = organizationId;
        _changed = true;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = DataBaseUtil.trim(type);
        _changed = true;
    }

    public String getExternalOrderNumber() {
        return externalOrderNumber;
    }

    public void setExternalOrderNumber(String externalOrderNumber) {
        this.externalOrderNumber = DataBaseUtil.trim(externalOrderNumber);
        _changed = true;
    }

    public Integer getReportToId() {
        return reportToId;
    }

    public void setReportToId(Integer reportToId) {
        this.reportToId = reportToId;
        _changed = true;
    }

    public Integer getBillToId() {
        return billToId;
    }

    public void setBillToId(Integer billToId) {
        this.billToId = billToId;
        _changed = true;
    }

    public Integer getShipFromId() {
        return shipFromId;
    }

    public void setShipFromId(Integer shipFromId) {
        this.shipFromId = shipFromId;
        _changed = true;
    }
}
