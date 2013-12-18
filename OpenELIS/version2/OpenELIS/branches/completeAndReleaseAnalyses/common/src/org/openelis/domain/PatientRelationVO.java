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

public class PatientRelationVO extends PatientDO {

    private static final long serialVersionUID = 1L;

    protected Integer         relationId;

    public PatientRelationVO() {
        address = new AddressDO();
    }

    public PatientRelationVO(Integer id, String lastName, String firstName, String middleName,
                             Integer addressId, Date birthDate, Date birthTime,
                             Integer genderId, Integer raceId, Integer ethnicityId,
                             String nationalId, String multipleUnit, String streetAddress,
                             String city, String state, String zipCode, String workPhone,
                             String homePhone, String cellPhone, String faxPhone,
                             String email, String country, Integer relationId) {
        super(id, lastName, firstName, middleName, addressId, birthDate, birthTime,
              genderId, raceId, ethnicityId, nationalId, multipleUnit, streetAddress, city,
              state, zipCode, workPhone, homePhone, cellPhone, faxPhone, email,
              country);
        setRelationId(relationId);
    }

    public Integer getRelationId() {
        return relationId;
    }

    public void setRelationId(Integer relationId) {
        this.relationId = relationId;
    }
}