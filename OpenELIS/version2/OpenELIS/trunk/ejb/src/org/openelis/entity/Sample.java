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
import javax.persistence.ColumnResult;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.SqlResultSetMappings;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.openelis.domain.ReferenceTable;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.Datetime;
import org.openelis.utils.Audit;
import org.openelis.utils.AuditUtil;
import org.openelis.utils.Auditable;

@NamedQueries( {
    @NamedQuery( name = "Sample.FetchById",
                query = "select new org.openelis.domain.SampleDO(id, nextItemSequence, domain, " +
                        "accessionNumber, revision, orderId, enteredDate, receivedDate, receivedById, collectionDate, collectionTime," +
                        "statusId, packageId, clientReference, releasedDate)"
                      + " from Sample where id = :id"),
    @NamedQuery( name = "Sample.FetchByAccessionNumber",
                query = "select new org.openelis.domain.SampleDO(id, nextItemSequence, domain," +
                        "accessionNumber, revision, orderId, enteredDate, receivedDate," +
                        "receivedById, collectionDate, collectionTime, statusId, packageId," +
                        "clientReference, releasedDate)"
                      + " from Sample where accessionNumber = :accession"),                     
    @NamedQuery( name = "Sample.FetchProjectsForOrganizations",
                query = "select new org.openelis.domain.IdNameVO(p.id, p.description)" 
                     + " from Sample s, SampleOrganization so, SampleProject sp, Project p, Organization o"
                     + " where s.id = so.sampleId and o.id in (:organizationIds) and so.typeId in(select id from Dictionary where systemName in ('org_report_to')) and" +
                       " so.organizationId = o.id and sp.sampleId = s.id and sp.projectId = p.id"),
   @NamedQuery( name = "Sample.FetchProjectsForPvtOrganizations",
               query = "select new org.openelis.domain.IdNameVO(p.id, p.description)" 
                     + " from Sample s, SamplePrivateWell so, SampleProject sp, Project p, Organization o"
                     + " where s.id = so.sampleId and o.id in (:organizationIds) and so.organizationId = o.id and sp.sampleId = s.id and sp.projectId = p.id"),
   @NamedQuery( name = "Sample.FetchSDWISByReleasedAndLocation",
               query = "select distinct new org.openelis.domain.SampleDO(s.id, s.nextItemSequence, s.domain," +
                       "s.accessionNumber, s.revision, s.orderId, s.enteredDate, s.receivedDate," +
                       "s.receivedById, s.collectionDate, s.collectionTime, s.statusId, s.packageId," +
                       "s.clientReference, s.releasedDate)"
                     + " from Sample s, SampleItem si, Analysis a, Section se" +
                       " where s.id = si.sampleId and si.id = a.sampleItemId and a.sectionId = se.id and s.domain = 'S' and s.releasedDate between :startDate and :endDate and se.name like :location"),
   @NamedQuery( name = "Sample.FetchForCachingByStatusId",
               query = "select distinct new org.openelis.domain.SampleCacheVO(s.id, s.statusId, s.domain, s.accessionNumber," +
                       "s.receivedDate, s.collectionDate, s.collectionTime, '', '')"
                     + " from Sample s where s.statusId = :statusId order by s.accessionNumber ")})
                      
@NamedNativeQueries({
    @NamedNativeQuery(name = "Sample.FetchSamplesForFinalReportBatch",     
                query = "select s.id s_id, so.organization_id o_id, a.id a_id"
                      + " from sample s, sample_item si, analysis a, sample_organization so"
                      + " where s.domain != 'W' and s.status_id in (select id from dictionary where system_name = 'sample_released') and si.sample_id = s.id and a.sample_item_id = si.id and"
                      + " a.printed_date is null and a.status_id in (select id from dictionary where system_name = 'analysis_released') and a.is_reportable = 'Y' and"
                      + " so.sample_id = s.id and so.type_id in (select id from dictionary where system_name in ('org_report_to', 'org_second_report_to'))"
                      + " union "
                      + "select s.id s_id, so.organization_id o_id, a.id a_id"
                      + " from sample s, sample_item si, analysis a, sample_organization so, test t"
                      + " where s.domain != 'W' and s.status_id not in (select id from dictionary where system_name in ('sample_released', 'sample_error')) and si.sample_id = s.id and a.sample_item_id = si.id and"  
                      + " a.printed_date is null and a.status_id in (select id from dictionary where system_name = 'analysis_released') and a.is_reportable = 'Y' and"
                      + " a.test_id = t.id and t.reporting_method_id in (select id from dictionary where system_name = 'analyses_released') and"
                      + " so.sample_id = s.id and so.type_id in (select id from dictionary where system_name in ('org_report_to', 'org_second_report_to'))"
                      + " union "
                      + "select s.id s_id, spw.organization_id o_id, a.id a_id"
                      + " from sample s, sample_private_well spw, sample_item si, analysis a"
                      + " where s.domain = 'W' and s.status_id in (select id from dictionary where system_name = 'sample_released') and spw.sample_id = s.id and si.sample_id = s.id and a.sample_item_id = si.id and"
                      + " a.printed_date is null and a.status_id in (select id from dictionary where system_name = 'analysis_released') and a.is_reportable = 'Y'"
                      + " union "
                      + "select s.id s_id, spw.organization_id o_id, a.id a_id"
                      + " from sample s, sample_private_well spw, sample_item si, analysis a, test t"
                      + " where s.domain = 'W' and s.status_id not in (select id from dictionary where system_name in ('sample_released', 'sample_error')) and spw.sample_id = s.id and si.sample_id = s.id and a.sample_item_id = si.id and"
                      + " a.printed_date is null and a.status_id in (select id from dictionary where system_name = 'analysis_released') and a.is_reportable = 'Y' and"
                      + " a.test_id = t.id and t.reporting_method_id in (select id from dictionary where system_name = 'analyses_released')"
                      + " union "
                      + "select s.id s_id, so.organization_id o_id, a.id a_id"
                      + " from sample s, sample_private_well spw, sample_item si, analysis a, sample_organization so"
                      + " where s.domain = 'W' and s.status_id in (select id from dictionary where system_name = 'sample_released') and spw.sample_id = s.id and si.sample_id = s.id and a.sample_item_id = si.id and"
                      + " a.printed_date is null and a.status_id in (select id from dictionary where system_name = 'analysis_released') and a.is_reportable = 'Y' and"
                      + " so.sample_id = s.id and so.type_id in (select id from dictionary where system_name = 'org_second_report_to')"
                      + " union "
                      + "select s.id s_id, so.organization_id o_id, a.id a_id"
                      + " from sample s, sample_private_well spw, sample_item si, analysis a, sample_organization so, test t"
                      + " where s.domain = 'W' and s.status_id not in (select id from dictionary where system_name in ('sample_released', 'sample_error')) and spw.sample_id = s.id and si.sample_id = s.id and a.sample_item_id = si.id and"
                      + " a.printed_date is null and a.status_id in (select id from dictionary where system_name = 'analysis_released') and a.is_reportable = 'Y' and"
                      + " a.test_id = t.id and t.reporting_method_id in (select id from dictionary where system_name = 'analyses_released') and"
                      + " so.sample_id = s.id and so.type_id in (select id from dictionary where system_name = 'org_second_report_to')"
                      + " order by s_id , o_id",
                resultSetMapping="Sample.FetchSamplesForFinalReportBatchMapping"),
    @NamedNativeQuery(name = "Sample.FetchSamplesForFinalReportSingle",     
                query = "select s.id s_id, so.organization_id o_id"
                      + " from sample s, sample_item si, analysis a, sample_organization so"
                      + " where s.id = :sampleId and s.domain != 'W' and s.status_id not in (select id from dictionary where system_name = 'sample_error') and si.sample_id = s.id and a.sample_item_id = si.id and"  
                      + " a.status_id in (select id from dictionary where system_name = 'analysis_released') and a.is_reportable = 'Y' and"
                      + " so.sample_id = s.id and so.type_id in (select id from dictionary where system_name in ('org_report_to', 'org_second_report_to'))"
                      + " union "
                      + "select s.id s_id, spw.organization_id o_id"
                      + " from sample s, sample_private_well spw, sample_item si, analysis a"
                      + " where s.id = :sampleId and s.domain = 'W' and s.status_id not in (select id from dictionary where system_name = 'sample_error') and spw.sample_id = s.id and"
                      + " si.sample_id = s.id and a.sample_item_id = si.id and a.status_id in (select id from dictionary where system_name = 'analysis_released') and a.is_reportable = 'Y'"
                      + " union "
                      + "select s.id s_id, so.organization_id o_id"
                      + " from sample s, sample_private_well spw, sample_item si, analysis a, sample_organization so"
                      + " where s.id = :sampleId and s.domain = 'W' and s.status_id not in (select id from dictionary where system_name = 'sample_error') and spw.sample_id = s.id and"
                      + " si.sample_id = s.id and a.sample_item_id = si.id and a.status_id in (select id from dictionary where system_name = 'analysis_released') and a.is_reportable = 'Y' and"
                      + " so.sample_id = s.id and so.type_id in (select id from dictionary where system_name = 'org_second_report_to')"
                      + " order by s_id , o_id",
                  resultSetMapping="Sample.FetchSamplesForFinalReportSingleMapping"),              
    @NamedNativeQuery(name = "Sample.FetchSamplesForFinalReportPreview",     
                query = "select s.id s_id, so.organization_id o_id"
                      + " from sample s, sample_item si, analysis a, sample_organization so"
                      + " where s.id = :sampleId and s.domain != 'W' and si.sample_id = s.id and a.sample_item_id = si.id and"  
                      + " a.status_id in (select id from dictionary where system_name in ('analysis_released', 'analysis_completed')) and a.is_reportable = 'Y' and"
                      + " so.sample_id = s.id and so.type_id in (select id from dictionary where system_name = 'org_report_to')"
                      + " union "
                      + "select s.id s_id, spw.organization_id o_id"
                      + " from sample s, sample_private_well spw, sample_item si, analysis a"
                      + " where s.id = :sampleId and s.domain = 'W' and spw.sample_id = s.id and si.sample_id = s.id and a.sample_item_id = si.id and"
                      + " a.status_id in (select id from dictionary where system_name in ('analysis_released', 'analysis_completed')) and a.is_reportable = 'Y'"
                      + " order by s_id , o_id",
                  resultSetMapping="Sample.FetchSamplesForFinalReportPreviewMapping")})              

@SqlResultSetMappings({
    @SqlResultSetMapping(name="Sample.FetchSamplesForFinalReportBatchMapping",
                         columns={@ColumnResult(name="s_id"), @ColumnResult(name="o_id"),  @ColumnResult(name="a_id")}),
    @SqlResultSetMapping(name="Sample.FetchSamplesForFinalReportSingleMapping",
                         columns={@ColumnResult(name="s_id"), @ColumnResult(name="o_id")}),
    @SqlResultSetMapping(name="Sample.FetchSamplesForFinalReportPreviewMapping",
                         columns={@ColumnResult(name="s_id"), @ColumnResult(name="o_id")})})
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
        return DataBaseUtil.toYM(enteredDate);
    }

    public void setEnteredDate(Datetime enteredDate) {
        if (DataBaseUtil.isDifferentYM(enteredDate, this.enteredDate))
            this.enteredDate = DataBaseUtil.toDate(enteredDate);
    }

    public Datetime getReceivedDate() {
        return DataBaseUtil.toYD(receivedDate);
    }

    public void setReceivedDate(Datetime receivedDate) {
        if (DataBaseUtil.isDifferentYM(receivedDate, this.receivedDate))
            this.receivedDate = DataBaseUtil.toDate(receivedDate);
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