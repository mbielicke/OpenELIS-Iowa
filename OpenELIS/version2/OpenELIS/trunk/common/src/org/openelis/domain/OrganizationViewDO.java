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
 * The class extends organization DO and adds a parent organization name. The
 * additional field is for read/display only and do not get committed to the
 * database. Note: isChanged will reflect any changes to read/display fields.
 */

public class OrganizationViewDO extends OrganizationDO {

    private static final long serialVersionUID = 1L;

    protected String          parentOrganizationName;

    public OrganizationViewDO() {
    }

    public OrganizationViewDO(Integer id, Integer parentOrganizationId, String name,
                              String isActive, Integer addressId, String multipleUnit,
                              String streetAddress, String city, String state, String zipCode,
                              String workPhone, String homePhone, String cellPhone,
                              String faxPhone, String email, String country,
                              String parentOrganizationName) {
        super(id, parentOrganizationId, name, isActive, addressId, multipleUnit, streetAddress,
              city, state, zipCode, workPhone, homePhone, cellPhone, faxPhone, email, country);
        setParentOrganizationName(parentOrganizationName);
    }

    public String getParentOrganizationName() {
        return parentOrganizationName;
    }

    public void setParentOrganizationName(String parentOrganizationName) {
        this.parentOrganizationName = DataBaseUtil.trim(parentOrganizationName);
    }
}
