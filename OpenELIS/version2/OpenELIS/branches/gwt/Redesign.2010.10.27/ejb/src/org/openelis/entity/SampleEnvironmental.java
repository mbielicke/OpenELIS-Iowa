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
package org.openelis.entity;

/**
 * SampleEnvironmental Entity POJO for database
 */

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.openelis.domain.ReferenceTable;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.utils.Audit;
import org.openelis.utils.AuditUtil;
import org.openelis.utils.Auditable;

@NamedQueries({
    @NamedQuery( name = "SampleEnvironmental.FetchBySampleId",
                query = "select new org.openelis.domain.SampleEnvironmentalDO(s.id,s.sampleId,s.isHazardous, s.priority, "+
                        "s.description,s.collector,s.collectorPhone,s.location,s.locationAddressId,a.multipleUnit," +
                        "a.streetAddress,a.city,a.state,a.zipCode,a.workPhone,a.homePhone,a.cellPhone, a.faxPhone, a.email,a.country)"
                      + " from SampleEnvironmental s LEFT JOIN s.locationAddress a where s.sampleId = :id")})
@Entity
@Table(name = "sample_environmental")
@EntityListeners( {AuditUtil.class})
public class SampleEnvironmental implements Auditable, Cloneable {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Integer             id;

    @Column(name = "sample_id")
    private Integer             sampleId;

    @Column(name = "is_hazardous")
    private String              isHazardous;
    
    @Column(name = "priority")
    private Integer              priority;

    @Column(name = "description")
    private String              description;

    @Column(name = "collector")
    private String              collector;

    @Column(name = "collector_phone")
    private String              collectorPhone;

    @Column(name = "location")
    private String              location;

    @Column(name = "location_address_id")
    private Integer             locationAddressId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_address_id", insertable = false, updatable = false)
    private Address             locationAddress;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sample_id", insertable = false, updatable = false)
    private Sample              sample;

    @Transient
    private SampleEnvironmental original;
    
    @Transient
    private boolean             auditLocationAddressId;

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

    public String getIsHazardous() {
        return isHazardous;
    }

    public void setIsHazardous(String isHazardous) {
        if (DataBaseUtil.isDifferent(isHazardous, this.isHazardous))
            this.isHazardous = isHazardous;
    }
    
    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        if (DataBaseUtil.isDifferent(priority, this.priority))
            this.priority = priority;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        if (DataBaseUtil.isDifferent(description, this.description))
            this.description = description;
    }

    public String getCollector() {
        return collector;
    }

    public void setCollector(String collector) {
        if (DataBaseUtil.isDifferent(collector, this.collector))
            this.collector = collector;
    }

    public String getCollectorPhone() {
        return collectorPhone;
    }

    public void setCollectorPhone(String collectorPhone) {
        if (DataBaseUtil.isDifferent(collectorPhone, this.collectorPhone))
            this.collectorPhone = collectorPhone;
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

    public Address getLocationAddress() {
        return locationAddress;
    }

    public void setLocationAddress(Address locationAddress) {
        this.locationAddress = locationAddress;
    }        

    public Sample getSample() {
        return sample;
    }

    public void setSample(Sample sample) {
        this.sample = sample;
    }

    /*
     * Audit support
     */
    public void setAuditLocationAddressId(boolean changed) {
        auditLocationAddressId = changed;
    }
    
    public void setClone() {
        try {
            original = (SampleEnvironmental)this.clone();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Audit getAudit() {
        Audit audit;

        audit = new Audit();
        audit.setReferenceTableId(ReferenceTable.SAMPLE_ENVIRONMENTAL);
        audit.setReferenceId(getId());
        if (original != null)
            audit.setField("id", id, original.id)
                 .setField("sample_id", sampleId, original.sampleId, ReferenceTable.SAMPLE)
                 .setField("is_hazardous", isHazardous, original.isHazardous)
                 .setField("priority", priority, original.priority)
                 .setField("description", description, original.description)
                 .setField("collector", collector, original.collector)
                 .setField("collector_phone", collectorPhone, original.collectorPhone)
                 .setField("location", location, original.location)
                 .setField("address_id", (auditLocationAddressId ? null : locationAddressId), original.locationAddressId,
                           ReferenceTable.ADDRESS);

        return audit;
    }
}
