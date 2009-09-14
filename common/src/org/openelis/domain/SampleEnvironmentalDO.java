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

import org.openelis.utilcommon.DataBaseUtil;

/**
 * Class represents the fields in database table sample_environmental. The
 * address information in this class is maintained in AddressDO object.
 */

public class SampleEnvironmentalDO extends DataObject {

    private static final long serialVersionUID = 1L;

    protected Integer         id, sampleId, addressId;
    protected String          isHazardous, description, collector, collectorPhone,
                              samplingLocation;
    protected AddressDO       addressDO        = new AddressDO();

    public SampleEnvironmentalDO() {
    }

    public SampleEnvironmentalDO(Integer id, Integer sampleId, String isHazardous,
                                 String description, String collector, String collectorPhone,
                                 String samplingLocation, Integer addressId, String multipleUnit,
                                 String streetAddress, String city, String state, String zipCode,
                                 String country) {
        setId(id);
        setSampleId(sampleId);
        setIsHazardous(isHazardous);
        setDescription(description);
        setCollector(collector);
        setCollectorPhone(collectorPhone);
        setSamplingLocation(samplingLocation);
        setAddressId(addressId);

        addressDO.setId(addressId);
        addressDO.setMultipleUnit(multipleUnit);
        addressDO.setStreetAddress(streetAddress);
        addressDO.setCity(city);
        addressDO.setState(state);
        addressDO.setZipCode(zipCode);
        addressDO.setCountry(country);
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

    public String getIsHazardous() {
        return isHazardous;
    }

    public void setIsHazardous(String isHazardous) {
        this.isHazardous = DataBaseUtil.trim(isHazardous);
        _changed = true;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = DataBaseUtil.trim(description);
        _changed = true;
    }

    public String getCollector() {
        return collector;
    }

    public void setCollector(String collector) {
        this.collector = DataBaseUtil.trim(collector);
        _changed = true;
    }

    public String getCollectorPhone() {
        return collectorPhone;
    }

    public void setCollectorPhone(String collectorPhone) {
        this.collectorPhone = DataBaseUtil.trim(collectorPhone);
        _changed = true;
    }

    public String getSamplingLocation() {
        return samplingLocation;
    }

    public void setSamplingLocation(String samplingLocation) {
        this.samplingLocation = DataBaseUtil.trim(samplingLocation);
        _changed = true;
    }

    public Integer getAddressId() {
        return addressId;
    }

    public void setAddressId(Integer addressId) {
        this.addressId = addressId;
        _changed = true;
    }

    public AddressDO getAddressDO() {
        return addressDO;
    }
}
