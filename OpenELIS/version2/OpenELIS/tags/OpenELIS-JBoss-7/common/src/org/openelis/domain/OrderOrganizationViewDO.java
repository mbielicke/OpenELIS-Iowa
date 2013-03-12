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
 * The class extends order organization DO and carries several commonly used fields 
 * such as organization name. The additional fields are for read/display only and
 * do not get committed to the database. Note: isChanged will reflect any
 * changes to read/display fields.
 */

public class OrderOrganizationViewDO extends OrderOrganizationDO {

    private static final long serialVersionUID = 1L;

    protected String organizationName, organizationAddressMultipleUnit, organizationAddressStreetAddress,
                     organizationAddressCity, organizationAddressState, organizationAddressZipCode, organizationAddressWorkPhone,
                     organizationAddressFaxPhone, organizationAddressCountry;

    public OrderOrganizationViewDO() {
    }

    public OrderOrganizationViewDO(Integer id, Integer orderId, Integer organizationId,
                                    String organizationAttention, Integer typeId,
                                    String organizationName, String organizationAddressMultipleUnit,
                                    String organizationAddressStreetAddress, String organizationAddressCity,
                                    String organizationAddressState, String organizationAddressZipCode,
                                    String organizationAddressWorkPhone, String organizationAddressFaxPhone,
                                    String organizationAddressCountry) {
        super(id, orderId, organizationId, organizationAttention, typeId);
        setOrganizationName(organizationName);
        setOrganizationAddressMultipleUnit(organizationAddressMultipleUnit);
        setOrganizationAddressStreetAddress(organizationAddressStreetAddress);
        setOrganizationAddressCity(organizationAddressCity);
        setOrganizationAddressState(organizationAddressState);
        setOrganizationAddressZipCode(organizationAddressZipCode);
        setOrganizationAddressWorkPhone(organizationAddressWorkPhone);
        setOrganizationAddressFaxPhone(organizationAddressFaxPhone);
        setOrganizationAddressCountry(organizationAddressCountry);
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = DataBaseUtil.trim(organizationName);
    }

    public String getOrganizationAddressMultipleUnit() {
        return organizationAddressMultipleUnit;
    }

    public void setOrganizationAddressMultipleUnit(String organizationAddressMultipleUnit) {
        this.organizationAddressMultipleUnit = DataBaseUtil.trim(organizationAddressMultipleUnit);
    }

    public String getOrganizationAddressStreetAddress() {
        return organizationAddressStreetAddress;
    }

    public void setOrganizationAddressStreetAddress(String organizationAddressStreetAddress) {
        this.organizationAddressStreetAddress = DataBaseUtil.trim(organizationAddressStreetAddress);
    }

    public String getOrganizationAddressCity() {
        return organizationAddressCity;
    }

    public void setOrganizationAddressCity(String organizationAddressCity) {
        this.organizationAddressCity = DataBaseUtil.trim(organizationAddressCity);
    }

    public String getOrganizationAddressState() {
        return organizationAddressState;
    }

    public void setOrganizationAddressState(String organizationAddressState) {
        this.organizationAddressState = DataBaseUtil.trim(organizationAddressState);
    }

    public String getOrganizationAddressZipCode() {
        return organizationAddressZipCode;
    }

    public void setOrganizationAddressZipCode(String organizationAddressZipCode) {
        this.organizationAddressZipCode = DataBaseUtil.trim(organizationAddressZipCode);
    }

    public String getOrganizationAddressWorkPhone() {
        return organizationAddressWorkPhone;
    }

    public void setOrganizationAddressWorkPhone(String organizationAddressWorkPhone) {
        this.organizationAddressWorkPhone = DataBaseUtil.trim(organizationAddressWorkPhone);
    }

    public String getOrganizationAddressFaxPhone() {
        return organizationAddressFaxPhone;
    }

    public void setOrganizationAddressFaxPhone(String organizationAddressFaxPhone) {
        this.organizationAddressFaxPhone = DataBaseUtil.trim(organizationAddressFaxPhone);
    }
    
    public String getOrganizationAddressCountry() {
        return organizationAddressCountry;
    }

    public void setOrganizationAddressCountry(String organizationAddressCountry) {
        this.organizationAddressCountry = DataBaseUtil.trim(organizationAddressCountry);
    }
}
