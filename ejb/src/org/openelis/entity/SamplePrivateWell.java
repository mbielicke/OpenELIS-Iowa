/** Exhibit A - UIRF Open-source Based Public Software License.
* 
* The contents of this file are subject to the UIRF Open-source Based
* Public Software License(the "License"); you may not use this file except
* in compliance with the License. You may obtain a copy of the License at
* openelis.uhl.uiowa.edu
* 
* Software distributed under the License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
* License for the specific language governing rights and limitations
* under the License.
* 
* The Original Code is OpenELIS code.
* 
* The Initial Developer of the Original Code is The University of Iowa.
* Portions created by The University of Iowa are Copyright 2006-2008. All
* Rights Reserved.
* 
* Contributor(s): ______________________________________.
* 
* Alternatively, the contents of this file marked
* "Separately-Licensed" may be used under the terms of a UIRF Software
* license ("UIRF Software License"), in which case the provisions of a
* UIRF Software License are applicable instead of those above. 
*/
package org.openelis.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.openelis.domain.ReferenceTable;
import org.openelis.utilcommon.DataBaseUtil;
import org.openelis.utils.Audit;
import org.openelis.utils.AuditUtil;
import org.openelis.utils.Auditable;

/*
@NamedQueries({
    @NamedQuery( name = "SampleEnvironmental.FetchBySampleId",
                query = "select new org.openelis.domain.SampleEnvironmentalDO(s.id,s.sampleId,s.isHazardous, s.priority, "+
                        "s.description,s.collector,s.collectorPhone,s.location,s.locationAddressId,a.multipleUnit," +
                        "a.streetAddress,a.city,a.state,a.zipCode,a.country)"
                      + " from SampleEnvironmental s LEFT JOIN s.address a where s.sampleId = :id")})
                      */
@Entity
@Table(name = "sample_private_well")
@EntityListeners( {AuditUtil.class})
public class SamplePrivateWell implements Auditable, Cloneable {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Integer             id;

    @Column(name = "sample_id")
    private Integer             sampleId;

    @Column(name = "organization_id")
    private Integer              organizationId;
    
    @Column(name = "report_to_name")
    private String              reportToName;

    @Column(name = "report_to_address_id")
    private Integer             reportToAddressId;

    @Column(name = "location")
    private String              location;

    @Column(name = "location_address_id")
    private Integer              locationAddressId;

    @Column(name = "owner")
    private String              owner;

    @Column(name = "collector")
    private String             collector;

    @Column(name = "well_number")
    private Integer             wellNumber;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "address_id", insertable = false, updatable = false)
    private Address             address;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sample_id", insertable = false, updatable = false)
    private Sample              sample;

    @Transient
    private SamplePrivateWell original;

    public Integer getId() {
        return id;
    }

    protected void setId(Integer id) {
        if (DataBaseUtil.isDifferent(id, this.id))
            this.id = id;
    }

    public Integer getSampleId() {
        return sampleId;
    }

    public void setSampleId(Integer sampleId) {
        if (DataBaseUtil.isDifferent(sampleId, this.sampleId))
            this.sampleId = sampleId;
    }
    
    public Integer getOrganizationId() {
        return organizationId;
    }
    
    public void setOrganizationId(Integer organizationId) {
        if (DataBaseUtil.isDifferent(organizationId, this.organizationId))
            this.organizationId = organizationId;
    }
    
    public String getReportToName() {
        return reportToName;
    }

    public void setReportToName(String reportToName) {
        if (DataBaseUtil.isDifferent(reportToName, this.reportToName))
            this.reportToName = reportToName;
    }

    public Integer getReportToAddressId() {
        return reportToAddressId;
    }

    public void setReportToAddressId(Integer reportToAddressId) {
        if (DataBaseUtil.isDifferent(reportToAddressId, this.reportToAddressId))
            this.reportToAddressId = reportToAddressId;
    }
    
    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        if (DataBaseUtil.isDifferent(location, this.location))
            this.location = location;
    }

    public Integer getLocationAddressId() {
        return locationAddressId;
    }

    public void setLocationAddressId(Integer locationAddressId) {
        if (DataBaseUtil.isDifferent(locationAddressId, this.locationAddressId))
            this.locationAddressId = locationAddressId;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        if (DataBaseUtil.isDifferent(owner, this.owner))
            this.owner = owner;
    }

    public String getCollector() {
        return collector;
    }

    public void setCollector(String collector) {
        if (DataBaseUtil.isDifferent(collector, this.collector))
            this.collector = collector;
    }
    
    public Integer getWellNumber() {
        return wellNumber;
    }

    public void setWellNumber(Integer wellNumber) {
        if (DataBaseUtil.isDifferent(wellNumber, this.wellNumber))
            this.wellNumber = wellNumber;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public Sample getSample() {
        return sample;
    }

    public void setSample(Sample sample) {
        this.sample = sample;
    }

    public void setClone() {
        try {
            original = (SamplePrivateWell)this.clone();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Audit getAudit() {
        Audit audit;

        audit = new Audit();
        audit.setReferenceTableId(ReferenceTable.SAMPLE_PRIVATE_WELL);
        audit.setReferenceId(getId());
        if (original != null)
            audit.setField("id", id, original.id)
                 .setField("sample_id", sampleId, original.sampleId)
                 .setField("organization_id", organizationId, original.organizationId)
                 .setField("report_to_name", reportToName, original.reportToName)
                 .setField("report_to_address_id", reportToAddressId, original.reportToAddressId)
                 .setField("location", location, original.location)
                 .setField("location_address_id", locationAddressId, original.locationAddressId)
                 .setField("owner", owner, original.owner)
                 .setField("collector", collector, original.collector)
                 .setField("well_number", wellNumber, original.wellNumber);

        return audit;
    }    
}
