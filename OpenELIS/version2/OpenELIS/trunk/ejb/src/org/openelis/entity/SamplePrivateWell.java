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
    @NamedQuery( name = "SamplePrivateWell.FetchBySampleId",
                query = "select new org.openelis.domain.SamplePrivateWellViewDO(s.id, s.sampleId, s.organizationId, s.reportToName, s.reportToAttention," +
                		"s.reportToAddressId, s.location, s.locationAddressId, s.owner, s.collector, s.wellNumber, r.multipleUnit, r.streetAddress, r.city," +
                		"r.state, r.zipCode, r.workPhone, r.homePhone, r.cellPhone, r.faxPhone, r.email, r.country,l.multipleUnit, l.streetAddress, l.city," +
                		"l.state, l.zipCode, l.workPhone,l.homePhone, l.cellPhone, l.faxPhone, l.email, l.country) "
                      + " from SamplePrivateWell s left join s.locationAddress l left join s.reportToAddress r where s.sampleId = :id")})

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
    
    @Column(name = "report_to_attention")
    private String              reportToAttention;

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
    @JoinColumn(name = "location_address_id", insertable = false, updatable = false)
    private Address             locationAddress;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_to_address_id", insertable = false, updatable = false)
    private Address             reportToAddress;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", insertable = false, updatable = false)
    private Organization             organization;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sample_id", insertable = false, updatable = false)
    private Sample              sample;

    @Transient
    private SamplePrivateWell original;
    
    @Transient
    private boolean           auditLocationAddressId, auditReportToAddressId;

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
    
    public String getReportToAttention() {
        return reportToAttention;
    }

    public void setReportToAttention(String reportToAttention) {
        if (DataBaseUtil.isDifferent(reportToAttention, this.reportToAttention))
            this.reportToAttention = reportToAttention;
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

    /*
     * support lookup entities
     */
    public Address getLocationAddress() {
        return locationAddress;
    }

    public Sample getSample() {
        return sample;
    }

    public Organization getOrganization() {
        return organization;
    }

    public Address getReportToAddress() {
        return reportToAddress;
    }

    /*
     * Audit support
     */
    public void setAuditLocationAddressId(boolean changed) {
        auditLocationAddressId = changed;
    }
    
    public void setAuditReportToAddressId(boolean changed) {
        auditReportToAddressId = changed;
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
                 .setField("sample_id", sampleId, original.sampleId, ReferenceTable.SAMPLE)
                 .setField("organization_id", organizationId, original.organizationId, ReferenceTable.ORGANIZATION)
                 .setField("report_to_name", reportToName, original.reportToName)
                 .setField("report_to_attention", reportToAttention, original.reportToAttention)
                 .setField("report_to_address_id", (auditReportToAddressId ? null : reportToAddressId), original.reportToAddressId,
                           ReferenceTable.ADDRESS)
                 .setField("location", location, original.location)
                 .setField("location_address_id", (auditLocationAddressId ? null : locationAddressId), original.locationAddressId,
                           ReferenceTable.ADDRESS)
                 .setField("owner", owner, original.owner)
                 .setField("collector", collector, original.collector)
                 .setField("well_number", wellNumber, original.wellNumber);



        return audit;
    }
}
