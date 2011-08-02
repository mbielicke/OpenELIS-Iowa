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

/**
 * The class extends order DO and carries the additional organization information.
 * The additional field is for read/display only and does not get committed to
 * the database. Note: isChanged will not reflect any changes to read/display
 * fields.
 */

public class OrderViewDO extends OrderDO {

    private static final long serialVersionUID = 1L;

    protected OrganizationDO organization, reportTo, billTo;

    public OrderViewDO() {
    }

    public OrderViewDO(Integer id,Integer parentOrderId, String description,
                       Integer statusId, Date orderedDate, Integer neededInDays,
                       String requestedBy, Integer costCenterId, Integer organizationId,
                       String organizationAttention, String type, String externalOrderNumber,
                       Integer reportToId, String reportToAttention, Integer billToId,
                       String billToAttention, Integer shipFromId) {
        super(id, parentOrderId, description, statusId, orderedDate,neededInDays, requestedBy,
              costCenterId, organizationId, organizationAttention, type, externalOrderNumber,
              reportToId, reportToAttention, billToId, billToAttention, shipFromId);
    }

    public OrganizationDO getOrganization() {
        return organization;
    }

    public void setOrganization(OrganizationDO organization) {
        this.organization = organization;
    }

    public OrganizationDO getReportTo() {
        return reportTo;
    }

    public void setReportTo(OrganizationDO reportTo) {
        this.reportTo = reportTo;
    }

    public OrganizationDO getBillTo() {
        return billTo;
    }

    public void setBillTo(OrganizationDO billTo) {
        this.billTo = billTo;
    }
}
