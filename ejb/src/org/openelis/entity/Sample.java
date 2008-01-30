
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
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
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

  @Column(name="domain")
  private Integer domain;             

  @Column(name="accession_number")
  private Integer accessionNumber;             

  @Column(name="revision")
  private Integer revision;             

  @Column(name="entered_date")
  private Date enteredDate;             

  @Column(name="received_date")
  private Date receivedDate;             

  @Column(name="received_by")
  private Integer receivedBy;             

  @Column(name="collection_date")
  private Date collectionDate;             

  @Column(name="status")
  private Integer status;             

  @Column(name="package")
  private Integer _package;             

  @Column(name="client_reference")
  private String clientReference;             

  @Column(name="released_date")
  private Date releasedDate;             


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

  public Integer getDomain() {
    return domain;
  }
  public void setDomain(Integer domain) {
    if((domain == null && this.domain != null) || 
       (domain != null && !domain.equals(this.domain)))
      this.domain = domain;
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

  public Integer getReceivedBy() {
    return receivedBy;
  }
  public void setReceivedBy(Integer receivedBy) {
    if((receivedBy == null && this.receivedBy != null) || 
       (receivedBy != null && !receivedBy.equals(this.receivedBy)))
      this.receivedBy = receivedBy;
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

  public Integer getStatus() {
    return status;
  }
  public void setStatus(Integer status) {
    if((status == null && this.status != null) || 
       (status != null && !status.equals(this.status)))
      this.status = status;
  }

  public Integer getPackage() {
    return _package;
  }
  public void setPackage(Integer _package) {
    if((_package == null && this._package != null) || 
       (_package != null && !_package.equals(this._package)))
      this._package = _package;
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
      
      if((id == null && original.id != null) || 
         (id != null && !id.equals(original.id))){
        Element elem = doc.createElement("id");
        elem.appendChild(doc.createTextNode(original.id.toString().trim()));
        root.appendChild(elem);
      }      

      if((nextItemSequence == null && original.nextItemSequence != null) || 
         (nextItemSequence != null && !nextItemSequence.equals(original.nextItemSequence))){
        Element elem = doc.createElement("next_item_sequence");
        elem.appendChild(doc.createTextNode(original.nextItemSequence.toString().trim()));
        root.appendChild(elem);
      }      

      if((domain == null && original.domain != null) || 
         (domain != null && !domain.equals(original.domain))){
        Element elem = doc.createElement("domain");
        elem.appendChild(doc.createTextNode(original.domain.toString().trim()));
        root.appendChild(elem);
      }      

      if((accessionNumber == null && original.accessionNumber != null) || 
         (accessionNumber != null && !accessionNumber.equals(original.accessionNumber))){
        Element elem = doc.createElement("accession_number");
        elem.appendChild(doc.createTextNode(original.accessionNumber.toString().trim()));
        root.appendChild(elem);
      }      

      if((revision == null && original.revision != null) || 
         (revision != null && !revision.equals(original.revision))){
        Element elem = doc.createElement("revision");
        elem.appendChild(doc.createTextNode(original.revision.toString().trim()));
        root.appendChild(elem);
      }      

      if((enteredDate == null && original.enteredDate != null) || 
         (enteredDate != null && !enteredDate.equals(original.enteredDate))){
        Element elem = doc.createElement("entered_date");
        elem.appendChild(doc.createTextNode(original.enteredDate.toString().trim()));
        root.appendChild(elem);
      }      

      if((receivedDate == null && original.receivedDate != null) || 
         (receivedDate != null && !receivedDate.equals(original.receivedDate))){
        Element elem = doc.createElement("received_date");
        elem.appendChild(doc.createTextNode(original.receivedDate.toString().trim()));
        root.appendChild(elem);
      }      

      if((receivedBy == null && original.receivedBy != null) || 
         (receivedBy != null && !receivedBy.equals(original.receivedBy))){
        Element elem = doc.createElement("received_by");
        elem.appendChild(doc.createTextNode(original.receivedBy.toString().trim()));
        root.appendChild(elem);
      }      

      if((collectionDate == null && original.collectionDate != null) || 
         (collectionDate != null && !collectionDate.equals(original.collectionDate))){
        Element elem = doc.createElement("collection_date");
        elem.appendChild(doc.createTextNode(original.collectionDate.toString().trim()));
        root.appendChild(elem);
      }      

      if((status == null && original.status != null) || 
         (status != null && !status.equals(original.status))){
        Element elem = doc.createElement("status");
        elem.appendChild(doc.createTextNode(original.status.toString().trim()));
        root.appendChild(elem);
      }      

      if((_package == null && original._package != null) || 
         (_package != null && !_package.equals(original._package))){
        Element elem = doc.createElement("package");
        elem.appendChild(doc.createTextNode(original._package.toString().trim()));
        root.appendChild(elem);
      }      

      if((clientReference == null && original.clientReference != null) || 
         (clientReference != null && !clientReference.equals(original.clientReference))){
        Element elem = doc.createElement("client_reference");
        elem.appendChild(doc.createTextNode(original.clientReference.toString().trim()));
        root.appendChild(elem);
      }      

      if((releasedDate == null && original.releasedDate != null) || 
         (releasedDate != null && !releasedDate.equals(original.releasedDate))){
        Element elem = doc.createElement("released_date");
        elem.appendChild(doc.createTextNode(original.releasedDate.toString().trim()));
        root.appendChild(elem);
      }      

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
  
}   
