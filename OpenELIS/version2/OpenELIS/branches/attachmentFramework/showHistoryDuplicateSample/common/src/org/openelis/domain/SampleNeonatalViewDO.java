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

import org.openelis.gwt.common.DataBaseUtil;

/**
 * The class extends sample neonatal DO and adds provider last name and first name.
 * The additional fields are for read/display only and do not get committed to the
 * database. Note: isChanged will reflect any changes to read/display fields.
 */

public class SampleNeonatalViewDO extends SampleNeonatalDO {

    private static final long serialVersionUID = 1L;

    protected String            providerlastName, providerFirstName;

    public SampleNeonatalViewDO() {
    }

    public SampleNeonatalViewDO(Integer id, Integer sampleId, Integer patientId,
                                Integer birthOrder, Integer gestationalAge, Integer nextOfKinId,
                                Integer nextOfKinRelationId, String isRepeat, String isNicu,
                                Integer feedingId, String weightSign, Integer weight,
                                String isTransfused, Date transfusionDate,
                                String isCollectionValid, Integer collectionAge,
                                Integer providerId, String formNumber, String patientLastName,
                                String patientFirstName, String patientMiddleName,
                                Integer patientAddressId, Date patientBirthDate,
                                Date patientBirthTime, Integer patientGenderId,
                                Integer patientRaceId, Integer patientEthnicityId,
                                String patientMultipleUnit, String patientStreetAddress,
                                String patientCity, String patientState, String patientZipCode,
                                String patientWorkPhone, String patientHomePhone,
                                String patientCellPhone, String patientFaxPhone,
                                String patientEmail, String patientCountry,
                                String nextOfKinLastName, String nextOfKinFirstName,
                                String nextOfKinMiddleName, Integer nextOfKinAddressId,
                                Date nextOfKinBirthDate, Date nextOfKinBirthTime,
                                Integer nextOfKinGenderId, Integer nextOfKinRaceId,
                                Integer nextOfKinEthnicityId, String nextOfKinMultipleUnit,
                                String nextOfKinStreetAddress, String nextOfKinCity,
                                String nextOfKinState, String nextOfKinZipCode,
                                String nextOfKinWorkPhone, String nextOfKinHomePhone,
                                String nextOfKinCellPhone, String nextOfKinFaxPhone,
                                String nextOfKinEmail, String nextOfKinCountry,
                                String providerlastName, String providerFirstName) {
        super(id, sampleId, patientId, birthOrder, gestationalAge, nextOfKinId,
              nextOfKinRelationId, isRepeat, isNicu, feedingId, weightSign, weight, isTransfused,
              transfusionDate, isCollectionValid, collectionAge, providerId, formNumber,
              patientLastName, patientFirstName, patientMiddleName, patientAddressId,
              patientBirthDate, patientBirthTime, patientGenderId, patientRaceId,
              patientEthnicityId, patientMultipleUnit, patientStreetAddress, patientCity,
              patientState, patientZipCode, patientWorkPhone, patientHomePhone, patientCellPhone,
              patientFaxPhone, patientEmail, patientCountry, nextOfKinLastName, nextOfKinFirstName,
              nextOfKinMiddleName, nextOfKinAddressId, nextOfKinBirthDate, nextOfKinBirthTime,
              nextOfKinGenderId, nextOfKinRaceId, nextOfKinEthnicityId, nextOfKinMultipleUnit,
              nextOfKinStreetAddress, nextOfKinCity, nextOfKinState, nextOfKinZipCode,
              nextOfKinWorkPhone, nextOfKinHomePhone, nextOfKinCellPhone, nextOfKinFaxPhone,
              nextOfKinEmail, nextOfKinCountry);
        setProviderlastName(providerlastName);        
        setProviderFirstName(providerFirstName);
    }

    public String getProviderLastName() {
        return providerlastName;
    }

    public void setProviderlastName(String providerlastName) {
        this.providerlastName = DataBaseUtil.trim(providerlastName);
    }

    public String getProviderFirstName() {
        return providerFirstName;
    }

    public void setProviderFirstName(String providerFirstName) {
        this.providerFirstName = DataBaseUtil.trim(providerFirstName);
    }
}