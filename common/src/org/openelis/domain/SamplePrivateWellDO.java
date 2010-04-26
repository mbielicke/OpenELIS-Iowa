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

public class SamplePrivateWellDO extends DataObject {

    private static final long serialVersionUID = 1L;
    
    protected Integer id, sampleId, organizationId, reportToAddressId, 
                     locationAddressId, wellNumber;
    protected String reportToName, reportToAttention, location, owner, collector;
    
    protected AddressDO locationAddressDO;
    
    public SamplePrivateWellDO(){
        locationAddressDO = new AddressDO();
    }
    
    public SamplePrivateWellDO(Integer id, Integer sampleId, Integer organizationId, String reportToName, String reportToAttention, Integer reportToAddressId, 
                               String location, Integer locationAddressId, String locationMultipleUnit, String locationStreetAddress, 
                               String locationCity, String locationState, String locationZipCode, String owner, String collector, Integer wellNumber){
        setId(id);
        setSampleId(sampleId);
        setOrganizationId(organizationId);
        setReportToName(reportToName);
        setReportToAttention(reportToAttention);
        setReportToAddressId(reportToAddressId);
        setLocation(location);
        setLocationAddressId(locationAddressId);
        setOwner(owner);
        setCollector(collector);
        setWellNumber(wellNumber);
        
        locationAddressDO = new AddressDO();
        locationAddressDO.setId(locationAddressId);
        locationAddressDO.setMultipleUnit(locationMultipleUnit);
        locationAddressDO.setStreetAddress(locationStreetAddress);
        locationAddressDO.setCity(locationCity);
        locationAddressDO.setState(locationState);
        locationAddressDO.setZipCode(locationZipCode);
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
    public Integer getOrganizationId() {
        return organizationId;
    }
    public void setOrganizationId(Integer organizationId) {
        this.organizationId = organizationId;
        _changed = true;
    }
    public Integer getReportToAddressId() {
        return reportToAddressId;
    }
    public void setReportToAddressId(Integer reportToAddressId) {
        this.reportToAddressId = reportToAddressId;
        _changed = true;
    }
    public Integer getLocationAddressId() {
        return locationAddressId;
    }
    public void setLocationAddressId(Integer locationAddressId) {
        this.locationAddressId = locationAddressId;
        _changed = true;
    }
    public Integer getWellNumber() {
        return wellNumber;
    }
    public void setWellNumber(Integer wellNumber) {
        this.wellNumber = wellNumber;
        _changed = true;
    }
    public String getReportToName() {
        return reportToName;
    }
    public void setReportToName(String reportToName) {
        this.reportToName = DataBaseUtil.trim(reportToName);
        _changed = true;
    }
    public String getReportToAttention() {
        return reportToAttention;
    }

    public void setReportToAttention(String reportToAttention) {
        this.reportToAttention = DataBaseUtil.trim(reportToAttention);
    }
    public String getLocation() {
        return location;
    }
    public void setLocation(String location) {
        this.location = DataBaseUtil.trim(location);
        _changed = true;
    }
    public String getOwner() {
        return owner;
    }
    public void setOwner(String owner) {
        this.owner = DataBaseUtil.trim(owner);
        _changed = true;
    }
    public String getCollector() {
        return collector;
    }
    public void setCollector(String collector) {
        this.collector = DataBaseUtil.trim(collector);
        _changed = true;
    }
    public AddressDO getLocationAddressDO() {
        return locationAddressDO;
    }
}
