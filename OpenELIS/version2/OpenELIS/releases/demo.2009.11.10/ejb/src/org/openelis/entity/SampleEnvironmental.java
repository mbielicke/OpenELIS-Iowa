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

import org.openelis.util.XMLUtil;
import org.openelis.utilcommon.DataBaseUtil;
import org.openelis.utils.AuditUtil;
import org.openelis.utils.Auditable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

@NamedQueries({
    @NamedQuery( name = "SampleEnvironmental.FetchBySampleId",
                query = "select new org.openelis.domain.SampleEnvironmentalDO(s.id,s.sampleId,s.isHazardous, s.priority, "+
                        "s.description,s.collector,s.collectorPhone,s.samplingLocation,s.addressId,a.multipleUnit," +
                        "a.streetAddress,a.city,a.state,a.zipCode,a.country)"
                      + " from SampleEnvironmental s LEFT JOIN s.address a where s.sampleId = :id")})
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

    @Column(name = "sampling_location")
    private String              samplingLocation;

    @Column(name = "address_id")
    private Integer             addressId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "address_id", insertable = false, updatable = false)
    private Address             address;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sample_id", insertable = false, updatable = false)
    private Sample              sample;

    @Transient
    private SampleEnvironmental original;

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

    public String getSamplingLocation() {
        return samplingLocation;
    }

    public void setSamplingLocation(String samplingLocation) {
        if (DataBaseUtil.isDifferent(samplingLocation, this.samplingLocation))
            this.samplingLocation = samplingLocation;
    }

    public Integer getAddressId() {
        return addressId;
    }

    public void setAddressId(Integer addressId) {
        if (DataBaseUtil.isDifferent(addressId, this.addressId))
            this.addressId = addressId;
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
            original = (SampleEnvironmental)this.clone();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getChangeXML() {
        try {
            Document doc = XMLUtil.createNew("change");
            Element root = doc.getDocumentElement();

            AuditUtil.getChangeXML(id, original.id, doc, "id");
            AuditUtil.getChangeXML(sampleId, original.sampleId, doc, "sample_id");
            AuditUtil.getChangeXML(isHazardous, original.isHazardous, doc, "is_hazardous");
            AuditUtil.getChangeXML(priority, original.priority, doc, "priority");
            AuditUtil.getChangeXML(description, original.description, doc, "description");
            AuditUtil.getChangeXML(collector, original.collector, doc, "collector");
            AuditUtil.getChangeXML(collectorPhone, original.collectorPhone, doc, "collector_phone");
            AuditUtil.getChangeXML(samplingLocation, original.samplingLocation, doc, "sampling_location");
            AuditUtil.getChangeXML(addressId, original.addressId, doc, "address_id");

            if (root.hasChildNodes())
                return XMLUtil.toString(doc);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getTableName() {
        return "sample_environmental";
    }
}
