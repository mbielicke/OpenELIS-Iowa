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

/**
  * Sample Entity POJO for database 
  */

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.openelis.util.Datetime;
import org.openelis.util.XMLUtil;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import org.openelis.utils.AuditUtil;
import org.openelis.utils.Auditable;

@Entity
@Table(name="sample")
@EntityListeners({AuditUtil.class})
public class Sample implements Auditable, Cloneable {
  
  @Id
  @GeneratedValue
  @Column(name="id")
  private Integer id;             

  @Column(name="next_item_sequence")
  private Integer nextItemSequence;             

  @Column(name="domain_id")
  private Integer domainId;             

  @Column(name="accession_number")
  private Integer accessionNumber;             

  @Column(name="revision")
  private Integer revision;             

  @Column(name="entered_date")
  private Date enteredDate;             

  @Column(name="received_date")
  private Date receivedDate;             

  @Column(name="received_by_id")
  private Integer receivedById;             

  @Column(name="collection_date")
  private Date collectionDate;             

  @Column(name="status_id")
  private Integer statusId;             

  @Column(name="package_id")
  private Integer packageId;             

  @Column(name="client_reference")
  private String clientReference;             

  @Column(name="released_date")
  private Date releasedDate;             

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "sample_id",insertable = false, updatable = false)
  private SampleHuman sampleHuman;

  @Transient
  private Sample original;

  
  public Integer getId() {
    return id;
  }
  protected void setId(Integer id) {
    if((id == null && this.id != null) || 
       (id != null && !id.equals(this.id)))
      this.id = id;
  }

  public Integer getNextItemSequence() {
    return nextItemSequence;
  }
  public void setNextItemSequence(Integer nextItemSequence) {
    if((nextItemSequence == null && this.nextItemSequence != null) || 
       (nextItemSequence != null && !nextItemSequence.equals(this.nextItemSequence)))
      this.nextItemSequence = nextItemSequence;
  }

  public Integer getDomainId() {
    return domainId;
  }
  public void setDomainId(Integer domainId) {
    if((domainId == null && this.domainId != null) || 
       (domainId != null && !domainId.equals(this.domainId)))
      this.domainId = domainId;
  }

  public Integer getAccessionNumber() {
    return accessionNumber;
  }
  public void setAccessionNumber(Integer accessionNumber) {
    if((accessionNumber == null && this.accessionNumber != null) || 
       (accessionNumber != null && !accessionNumber.equals(this.accessionNumber)))
      this.accessionNumber = accessionNumber;
  }

  public Integer getRevision() {
    return revision;
  }
  public void setRevision(Integer revision) {
    if((revision == null && this.revision != null) || 
       (revision != null && !revision.equals(this.revision)))
      this.revision = revision;
  }

  public Datetime getEnteredDate() {
    if(enteredDate == null)
      return null;
    return new Datetime(Datetime.YEAR,Datetime.SECOND,enteredDate);
  }
  public void setEnteredDate (Datetime entered_date){
    if((enteredDate == null && this.enteredDate != null) || 
       (enteredDate != null && !enteredDate.equals(this.enteredDate)))
      this.enteredDate = entered_date.getDate();
  }

  public Datetime getReceivedDate() {
    if(receivedDate == null)
      return null;
    return new Datetime(Datetime.YEAR,Datetime.SECOND,receivedDate);
  }
  public void setReceivedDate (Datetime received_date){
    if((receivedDate == null && this.receivedDate != null) || 
       (receivedDate != null && !receivedDate.equals(this.receivedDate)))
      this.receivedDate = received_date.getDate();
  }

  public Integer getReceivedById() {
    return receivedById;
  }
  public void setReceivedById(Integer receivedById) {
    if((receivedById == null && this.receivedById != null) || 
       (receivedById != null && !receivedById.equals(this.receivedById)))
      this.receivedById = receivedById;
  }

  public Datetime getCollectionDate() {
    if(collectionDate == null)
      return null;
    return new Datetime(Datetime.YEAR,Datetime.SECOND,collectionDate);
  }
  public void setCollectionDate (Datetime collection_date){
    if((collectionDate == null && this.collectionDate != null) || 
       (collectionDate != null && !collectionDate.equals(this.collectionDate)))
      this.collectionDate = collection_date.getDate();
  }

  public Integer getStatusId() {
    return statusId;
  }
  public void setStatusId(Integer statusId) {
    if((statusId == null && this.statusId != null) || 
       (statusId != null && !statusId.equals(this.statusId)))
      this.statusId = statusId;
  }

  public Integer getPackageId() {
    return packageId;
  }
  public void setPackageId(Integer packageId) {
    if((packageId == null && this.packageId != null) || 
       (packageId != null && !packageId.equals(this.packageId)))
      this.packageId = packageId;
  }

  public String getClientReference() {
    return clientReference;
  }
  public void setClientReference(String clientReference) {
    if((clientReference == null && this.clientReference != null) || 
       (clientReference != null && !clientReference.equals(this.clientReference)))
      this.clientReference = clientReference;
  }

  public Datetime getReleasedDate() {
    if(releasedDate == null)
      return null;
    return new Datetime(Datetime.YEAR,Datetime.SECOND,releasedDate);
  }
  public void setReleasedDate (Datetime released_date){
    if((releasedDate == null && this.releasedDate != null) || 
       (releasedDate != null && !releasedDate.equals(this.releasedDate)))
      this.releasedDate = released_date.getDate();
  }

  
  public void setClone() {
    try {
      original = (Sample)this.clone();
    }catch(Exception e){}
  }
  
  public String getChangeXML() {
    try {
      Document doc = XMLUtil.createNew("change");
      Element root = doc.getDocumentElement();
      
      AuditUtil.getChangeXML(id,original.id,doc,"id");

      AuditUtil.getChangeXML(nextItemSequence,original.nextItemSequence,doc,"next_item_sequence");

      AuditUtil.getChangeXML(domainId,original.domainId,doc,"domain_id");

      AuditUtil.getChangeXML(accessionNumber,original.accessionNumber,doc,"accession_number");

      AuditUtil.getChangeXML(revision,original.revision,doc,"revision");

      AuditUtil.getChangeXML(enteredDate,original.enteredDate,doc,"entered_date");

      AuditUtil.getChangeXML(receivedDate,original.receivedDate,doc,"received_date");

      AuditUtil.getChangeXML(receivedById,original.receivedById,doc,"received_by_id");

      AuditUtil.getChangeXML(collectionDate,original.collectionDate,doc,"collection_date");

      AuditUtil.getChangeXML(statusId,original.statusId,doc,"status_id");

      AuditUtil.getChangeXML(packageId,original.packageId,doc,"package_id");

      AuditUtil.getChangeXML(clientReference,original.clientReference,doc,"client_reference");

      AuditUtil.getChangeXML(releasedDate,original.releasedDate,doc,"released_date");

      if(root.hasChildNodes())
        return XMLUtil.toString(doc);
    }catch(Exception e){
      e.printStackTrace();
    }
    return null;
  }
   
  public String getTableName() {
    return "sample";
  }
public SampleHuman getSampleHuman() {
    return sampleHuman;
}
public void setSampleHuman(SampleHuman sampleHuman) {
    this.sampleHuman = sampleHuman;
}
  
}   
