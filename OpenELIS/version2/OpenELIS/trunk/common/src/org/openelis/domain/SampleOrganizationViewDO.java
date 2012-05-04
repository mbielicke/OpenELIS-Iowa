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
 * The class extends sample organization DO and carries several commonly used fields
 * such as organization name. The additional fields are for read/display only and
 * do not get committed to the database. Note: isChanged will reflect any
 * changes to read/display fields.
 */

public class SampleOrganizationViewDO extends SampleOrganizationDO {

    private static final long serialVersionUID = 1L;

    protected String organizationName, organizationMultipleUnit, organizationStreetAddress,
                     organizationCity, organizationState, organizationZipCode, organizationWorkPhone,
                     organizationFaxPhone;

    public SampleOrganizationViewDO() {
    }

    public SampleOrganizationViewDO(Integer id, Integer sampleId, Integer organizationId,
                                    String organizationAttention, Integer typeId,
                                    String organizationName, String organizationMultipleUnit,
                                    String organizationStreetAddress, String organizationCity,
                                    String organizationState, String organizationZipCode,
                                    String organizationWorkPhone, String organizationFaxPhone) {
        super(id, sampleId, organizationId, organizationAttention, typeId);
        setOrganizationName(organizationName);
        setOrganizationMultipleUnit(organizationMultipleUnit);
        setOrganizationStreetAddress(organizationStreetAddress);
        setOrganizationCity(organizationCity);
        setOrganizationState(organizationState);
        setOrganizationZipCode(organizationZipCode);
        setOrganizationWorkPhone(organizationWorkPhone);
        setOrganizationFaxPhone(organizationFaxPhone);
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = DataBaseUtil.trim(organizationName);
    }

    public String getOrganizationMultipleUnit() {
        return organizationMultipleUnit;
    }

    public void setOrganizationMultipleUnit(String organizationMultipleUnit) {
        this.organizationMultipleUnit = DataBaseUtil.trim(organizationMultipleUnit);
    }

    public String getOrganizationStreetAddress() {
        return organizationStreetAddress;
    }

    public void setOrganizationStreetAddress(String organizationStreetAddress) {
        this.organizationStreetAddress = DataBaseUtil.trim(organizationStreetAddress);
    }

    public String getOrganizationCity() {
        return organizationCity;
    }

    public void setOrganizationCity(String organizationCity) {
        this.organizationCity = DataBaseUtil.trim(organizationCity);
    }

    public String getOrganizationState() {
        return organizationState;
    }

    public void setOrganizationState(String organizationState) {
        this.organizationState = DataBaseUtil.trim(organizationState);
    }

    public String getOrganizationZipCode() {
        return organizationZipCode;
    }

    public void setOrganizationZipCode(String organizationZipCode) {
        this.organizationZipCode = DataBaseUtil.trim(organizationZipCode);
    }

    public String getOrganizationWorkPhone() {
        return organizationWorkPhone;
    }

    public void setOrganizationWorkPhone(String organizationWorkPhone) {
        this.organizationWorkPhone = DataBaseUtil.trim(organizationWorkPhone);
    }

    public String getOrganizationFaxPhone() {
        return organizationFaxPhone;
    }

    public void setOrganizationFaxPhone(String organizationFaxPhone) {
        this.organizationFaxPhone = DataBaseUtil.trim(organizationFaxPhone);
    }
}
