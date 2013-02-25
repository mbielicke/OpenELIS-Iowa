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

import org.openelis.gwt.common.DataBaseUtil;

/**
 * Class represents the fields in database table order_organization.
 */

public class OrderOrganizationDO extends DataObject {

    private static final long serialVersionUID = 1L;

    protected Integer         id, orderId, organizationId, typeId;
    protected String          organizationAttention;

    public OrderOrganizationDO() {
    }

    public OrderOrganizationDO(Integer id, Integer orderId, Integer organizationId, String organizationAttention, Integer typeId) {
        setId(id);
        setOrderId(orderId);
        setOrganizationId(organizationId);
        setOrganizationAttention(organizationAttention);
        setTypeId(typeId);
        _changed = false;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
        _changed = true;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
        _changed = true;
    }

    public Integer getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Integer organizationId) {
        this.organizationId = organizationId;
        _changed = true;
    }

    public Integer getTypeId() {
        return typeId;
    }

    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
        _changed = true;
    }

    public String getOrganizationAttention() {
        return organizationAttention;
    }

    public void setOrganizationAttention(String organizationAttention) {
        this.organizationAttention = DataBaseUtil.trim(organizationAttention);
        _changed = true;
    }
}
