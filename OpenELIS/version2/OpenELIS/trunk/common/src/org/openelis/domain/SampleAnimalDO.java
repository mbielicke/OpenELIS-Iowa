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

import org.openelis.ui.common.DataBaseUtil;

/**
 * Class represents the fields in database table sample_animal.
 */

public class SampleAnimalDO extends DataObject {

    private static final long serialVersionUID = 1L;

    protected Integer         id, sampleId, animalCommonNameId, animalScientificNameId,
                    locationAddressId, providerId;

    protected String          location, providerPhone;

    protected AddressDO       locationAddress;

    protected ProviderDO      provider;

    public SampleAnimalDO() {
        locationAddress = new AddressDO();
    }

    public SampleAnimalDO(Integer id, Integer sampleId, Integer animalCommonNameId,
                          Integer animalScientificNameId, String location,
                          Integer locationAddressId, Integer providerId, String providerPhone,
                          String multipleUnit, String streetAddress, String city, String state,
                          String zipCode, String workPhone, String homePhone, String cellPhone,
                          String faxPhone, String email, String country) {
        setId(id);
        setSampleId(sampleId);
        setAnimalCommonNameId(animalCommonNameId);
        setAnimalScientificNameId(animalScientificNameId);
        setLocation(location);
        setProviderId(providerId);
        setProviderPhone(providerPhone);

        locationAddress = new AddressDO(locationAddressId,
                                        multipleUnit,
                                        streetAddress,
                                        city,
                                        state,
                                        zipCode,
                                        workPhone,
                                        homePhone,
                                        cellPhone,
                                        faxPhone,
                                        email,
                                        country);

        _changed = false;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
        _changed = true;
    }

    public Integer getSampleId() {
        return sampleId;
    }

    public void setSampleId(Integer sampleId) {
        this.sampleId = sampleId;
        _changed = true;
    }

    public Integer getAnimalCommonNameId() {
        return animalCommonNameId;
    }

    public void setAnimalCommonNameId(Integer animalCommonNameId) {
        this.animalCommonNameId = animalCommonNameId;
        _changed = true;
    }

    public Integer getAnimalScientificNameId() {
        return animalScientificNameId;
    }

    public void setAnimalScientificNameId(Integer animalScientificNameId) {
        this.animalScientificNameId = animalScientificNameId;
        _changed = true;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = DataBaseUtil.trim(location);
        _changed = true;
    }

    public Integer getLocationAddressId() {
        return locationAddressId;
    }

    public void setLocationAddressId(Integer locationAddressId) {
        this.locationAddressId = locationAddressId;
        _changed = true;
    }

    public Integer getProviderId() {
        return providerId;
    }

    public void setProviderId(Integer providerId) {
        this.providerId = providerId;
        _changed = true;
    }

    public String getProviderPhone() {
        return providerPhone;
    }

    public void setProviderPhone(String providerPhone) {
        this.providerPhone = DataBaseUtil.trim(providerPhone);
        _changed = true;
    }

    public ProviderDO getProvider() {
        return provider;
    }

    public void setProvider(ProviderDO provider) {
        this.provider = provider;
    }

    public AddressDO getLocationAddress() {
        return locationAddress;
    }
}