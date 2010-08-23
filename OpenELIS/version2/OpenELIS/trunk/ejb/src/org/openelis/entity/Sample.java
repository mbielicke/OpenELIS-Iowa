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
 * Sample Entity POJO for database
 */

import java.util.Collection;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.openelis.domain.ReferenceTable;
import org.openelis.gwt.common.Datetime;
import org.openelis.utilcommon.DataBaseUtil;
import org.openelis.utils.Audit;
import org.openelis.utils.AuditUtil;
import org.openelis.utils.Auditable;

@NamedQueries( {
    @NamedQuery( name = "Sample.FetchById",
                query = "select new org.openelis.domain.SampleDO(smpl.id, smpl.nextItemSequence, smpl.domain, " +
                        "smpl.accessionNumber, smpl.revision, smpl.orderId, smpl.enteredDate, smpl.receivedDate, smpl.receivedById, smpl.collectionDate, smpl.collectionTime," +
                        "smpl.statusId, smpl.packageId, smpl.clientReference, smpl.releasedDate)"
                      + " from Sample smpl where smpl.id = :id"),
    @NamedQuery( name = "Sample.FetchByAccessionNumber",
                query = "select new org.openelis.domain.SampleDO(smpl.id, smpl.nextItemSequence, smpl.domain," +
                        "smpl.accessionNumber, smpl.revision, smpl.orderId, smpl.enteredDate, smpl.receivedDate," +
                        "smpl.receivedById, smpl.collectionDate, smpl.collectionTime, smpl.statusId, smpl.packageId," +
                        "smpl.clientReference, smpl.releasedDate)"
                      + " from Sample smpl where smpl.accessionNumber = :id")})
@Entity
@Table(name = "sample")
@EntityListeners( {AuditUtil.class})
public class Sample implements Auditable, Cloneable {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Integer                         id;

    @Column(name = "next_item_sequence")
    private Integer                         nextItemSequence;

    @Column(name = "domain")
    private String                          domain;

    @Column(name = "accession_number")
    private Integer                         accessionNumber;

    @Column(name = "revision")
    private Integer                         revision;

    @Column(name = "order_id")
    private Integer                         orderId;

    @Column(name = "entered_date")
    private Date                            enteredDate;

    @Column(name = "received_date")
    private Date                            receivedDate;

    @Column(name = "received_by_id")
    private Integer                         receivedById;

    @Column(name = "collection_date")
    private Date                            collectionDate;

    @Column(name = "collection_time")
    private Date                            collectionTime;

    @Column(name = "status_id")
    private Integer                         statusId;

    @Column(name = "package_id")
    private Integer                         packageId;

    @Column(name = "client_reference")
    private String                          clientReference;

    @Column(name = "released_date")
    private Date                            releasedDate;

    // sample environmental
    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "sample_id")
    private Collection<SampleEnvironmental> sampleEnvironmental;

    // sample private well
    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "sample_id")
    private Collection<SamplePrivateWell>   samplePrivateWell;

    // sample sdwis
    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "sample_id")
    private Collection<SampleSDWIS>         sampleSDWIS;

    // sample organizations
    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "sample_id")
    private Collection<SampleOrganization>  sampleOrganization;

    // sample projects
    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "sample_id")
    private Collection<SampleProject>       sampleProject;

    // projects
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "sample_project", joinColumns = {@JoinColumn(name = "sample_id")}, inverseJoinColumns = {@JoinColumn(name = "project_id")})
    private Collection<Project>             project;

    // sample items
    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "sample_id")
    private Collection<SampleItem>          sampleItem;

    // sample qa events
    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "sample_id")
    private Collection<SampleQaevent>       sampleQAEvent;

    // aux data records
    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "reference_id")
    private Collection<AuxData>             auxData;

    @Transient
    private Sample                          original;

    public Integer getId() {
        return id;
    }

    protected void setId(Integer id) {
        if (DataBaseUtil.isDifferent(id, this.id))
            this.id = id;
    }

    public Integer getNextItemSequence() {
        return nextItemSequence;
    }

    public void setNextItemSequence(Integer nextItemSequence) {
        if (DataBaseUtil.isDifferent(nextItemSequence, this.nextItemSequence))
            this.nextItemSequence = nextItemSequence;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        if (DataBaseUtil.isDifferent(domain, this.domain))
            this.domain = domain;
    }

    public Integer getAccessionNumber() {
        return accessionNumber;
    }

    public void setAccessionNumber(Integer accessionNumber) {
        if (DataBaseUtil.isDifferent(accessionNumber, this.accessionNumber))
            this.accessionNumber = accessionNumber;
    }

    public Integer getRevision() {
        return revision;
    }

    public void setRevision(Integer revision) {
        if (DataBaseUtil.isDifferent(revision, this.revision))
            this.revision = revision;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        if (DataBaseUtil.isDifferent(orderId, this.orderId))
            this.orderId = orderId;
    }

    public Datetime getEnteredDate() {
        if (enteredDate == null)
            return null;
        return new Datetime(Datetime.YEAR, Datetime.MINUTE, enteredDate);
    }

    public void setEnteredDate(Datetime enteredDate) {
        if (DataBaseUtil.isDifferentYM(enteredDate, this.enteredDate))
            this.enteredDate = enteredDate.getDate();
    }

    public Datetime getReceivedDate() {
        if (receivedDate == null)
            return null;
        return new Datetime(Datetime.YEAR, Datetime.MINUTE, receivedDate);
    }

    public void setReceivedDate(Datetime receivedDate) {
        if (DataBaseUtil.isDifferentYM(receivedDate, this.receivedDate))
            this.receivedDate = receivedDate.getDate();
    }

    public Integer getReceivedById() {
        return receivedById;
    }

    public void setReceivedById(Integer receivedById) {
        if (DataBaseUtil.isDifferent(receivedById, this.receivedById))
            this.receivedById = receivedById;
    }

    public Datetime getCollectionDate() {
        return DataBaseUtil.toYD(collectionDate);
    }

    public void setCollectionDate(Datetime collectionDate) {
        if (DataBaseUtil.isDifferentYD(collectionDate, this.collectionDate))
            this.collectionDate = DataBaseUtil.toDate(collectionDate);
    }

    public Datetime getCollectionTime() {
        return DataBaseUtil.toHM(collectionTime);
    }

    public void setCollectionTime(Datetime collectionTime) {
        if (DataBaseUtil.isDifferentHM(collectionTime, this.collectionTime))
            this.collectionTime = DataBaseUtil.toDate(collectionTime);
    }

    public Integer getStatusId() {
        return statusId;
    }

    public void setStatusId(Integer statusId) {
        if (DataBaseUtil.isDifferent(statusId, this.statusId))
            this.statusId = statusId;
    }

    public Integer getPackageId() {
        return packageId;
    }

    public void setPackageId(Integer packageId) {
        if (DataBaseUtil.isDifferent(packageId, this.packageId))
            this.packageId = packageId;
    }

    public String getClientReference() {
        return clientReference;
    }

    public void setClientReference(String clientReference) {
        if (DataBaseUtil.isDifferent(clientReference, this.clientReference))
            this.clientReference = clientReference;
    }

    public Datetime getReleasedDate() {
        return DataBaseUtil.toYM(releasedDate);
    }

    public void setReleasedDate(Datetime releasedDate) {
        if (DataBaseUtil.isDifferentYM(releasedDate, this.releasedDate))
            this.releasedDate = DataBaseUtil.toDate(releasedDate);
    }

    public Collection<SampleItem> getSampleItem() {
        return sampleItem;
    }

    public void setSampleItem(Collection<SampleItem> sampleItem) {
        this.sampleItem = sampleItem;
    }

    public Collection<SampleEnvironmental> getSampleEnvironmental() {
        return sampleEnvironmental;
    }

    public void setSampleEnvironmental(Collection<SampleEnvironmental> sampleEnvironmental) {
        this.sampleEnvironmental = sampleEnvironmental;
    }

    public Collection<SampleOrganization> getSampleOrganization() {
        return sampleOrganization;
    }

    public void setSampleOrganization(Collection<SampleOrganization> sampleOrganization) {
        this.sampleOrganization = sampleOrganization;
    }

    public Collection<SampleProject> getSampleProject() {
        return sampleProject;
    }

    public void setSampleProject(Collection<SampleProject> sampleProject) {
        this.sampleProject = sampleProject;
    }

    public Collection<SampleQaevent> getSampleQAEvent() {
        return sampleQAEvent;
    }

    public void setSampleQAEvent(Collection<SampleQaevent> sampleQAEvent) {
        this.sampleQAEvent = sampleQAEvent;
    }

    public Collection<Project> getProject() {
        return project;
    }

    public void setProject(Collection<Project> project) {
        this.project = project;
    }
    
    public Collection<SamplePrivateWell> getSamplePrivateWell() {
        return samplePrivateWell;
    }

    public void setSamplePrivateWell(Collection<SamplePrivateWell> samplePrivateWell) {
        this.samplePrivateWell = samplePrivateWell;
    }

    public Collection<AuxData> getAuxData() {
        return auxData;
    }

    public void setAuxData(Collection<AuxData> auxData) {
        this.auxData = auxData;
    }

    public Collection<SampleSDWIS> getSampleSDWIS() {
        return sampleSDWIS;
    }

    public void setSampleSDWIS(Collection<SampleSDWIS> sampleSDWIS) {
        this.sampleSDWIS = sampleSDWIS;
    }

    public void setClone() {
        try {
            original = (Sample)this.clone();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Audit getAudit() {
        Audit audit;

        audit = new Audit();
        audit.setReferenceTableId(ReferenceTable.SAMPLE);
        audit.setReferenceId(getId());
        if (original != null)
            audit.setField("id", id, original.id)
                 .setField("next_item_sequence", nextItemSequence, original.nextItemSequence)
                 .setField("domain", domain, original.domain)
                 .setField("accession_number", accessionNumber, original.accessionNumber)
                 .setField("revision", revision, original.revision)
                 .setField("order_id", orderId, original.orderId, ReferenceTable.ORDER)
                 .setField("entered_date", enteredDate, original.enteredDate)
                 .setField("received_date", receivedDate, original.receivedDate)
                 .setField("received_by_id", receivedById, original.receivedById)
                 .setField("collection_date", collectionDate, original.collectionDate)
                 .setField("collection_time", collectionTime, original.collectionTime)
                 .setField("status_id", statusId, original.statusId, ReferenceTable.DICTIONARY)
                 .setField("package_id", packageId, original.packageId)
                 .setField("client_reference", clientReference, original.clientReference)
                 .setField("released_date", releasedDate, original.releasedDate);

        return audit;
    }
}