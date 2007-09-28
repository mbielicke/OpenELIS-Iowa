
package org.openelis.entity;

/**
  * Analysis Entity POJO for database 
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
import org.openelis.interfaces.Auditable;

@Entity
@Table(name="analysis")
@EntityListeners({AuditUtil.class})
public class Analysis implements Auditable, Cloneable {
  
  @Id
  @GeneratedValue
  @Column(name="id")
  private Integer id;             

  @Column(name="sample_item")
  private Integer sampleItem;             

  @Column(name="revision")
  private Integer revision;             

  @Column(name="test")
  private Integer test;             

  @Column(name="section")
  private Integer section;             

  @Column(name="pre_analysis")
  private Integer preAnalysis;             

  @Column(name="parent_analysis")
  private Integer parentAnalysis;             

  @Column(name="parent_result")
  private Integer parentResult;             

  @Column(name="is_reportable")
  private String isReportable;             

  @Column(name="unit_of_measure")
  private Integer unitOfMeasure;             

  @Column(name="status")
  private Integer status;             

  @Column(name="available_date")
  private Date availableDate;             

  @Column(name="started_date")
  private Date startedDate;             

  @Column(name="completed_date")
  private Date completedDate;             

  @Column(name="released_date")
  private Date releasedDate;             

  @Column(name="printed_date")
  private Date printedDate;             


  @Transient
  private Analysis original;

  
  public Integer getId() {
    return id;
  }
  protected void setId(Integer id) {
    this.id = id;
  }

  public Integer getSampleItem() {
    return sampleItem;
  }
  public void setSampleItem(Integer sampleItem) {
    this.sampleItem = sampleItem;
  }

  public Integer getRevision() {
    return revision;
  }
  public void setRevision(Integer revision) {
    this.revision = revision;
  }

  public Integer getTest() {
    return test;
  }
  public void setTest(Integer test) {
    this.test = test;
  }

  public Integer getSection() {
    return section;
  }
  public void setSection(Integer section) {
    this.section = section;
  }

  public Integer getPreAnalysis() {
    return preAnalysis;
  }
  public void setPreAnalysis(Integer preAnalysis) {
    this.preAnalysis = preAnalysis;
  }

  public Integer getParentAnalysis() {
    return parentAnalysis;
  }
  public void setParentAnalysis(Integer parentAnalysis) {
    this.parentAnalysis = parentAnalysis;
  }

  public Integer getParentResult() {
    return parentResult;
  }
  public void setParentResult(Integer parentResult) {
    this.parentResult = parentResult;
  }

  public String getIsReportable() {
    return isReportable;
  }
  public void setIsReportable(String isReportable) {
    this.isReportable = isReportable;
  }

  public Integer getUnitOfMeasure() {
    return unitOfMeasure;
  }
  public void setUnitOfMeasure(Integer unitOfMeasure) {
    this.unitOfMeasure = unitOfMeasure;
  }

  public Integer getStatus() {
    return status;
  }
  public void setStatus(Integer status) {
    this.status = status;
  }

  public Datetime getAvailableDate() {
    if(availableDate == null)
      return null;
    return new Datetime(Datetime.YEAR,Datetime.SECOND,availableDate);
  }
  public void setAvailableDate (Datetime available_date){
    this.availableDate = available_date.getDate();
  }

  public Datetime getStartedDate() {
    if(startedDate == null)
      return null;
    return new Datetime(Datetime.YEAR,Datetime.SECOND,startedDate);
  }
  public void setStartedDate (Datetime started_date){
    this.startedDate = started_date.getDate();
  }

  public Datetime getCompletedDate() {
    if(completedDate == null)
      return null;
    return new Datetime(Datetime.YEAR,Datetime.SECOND,completedDate);
  }
  public void setCompletedDate (Datetime completed_date){
    this.completedDate = completed_date.getDate();
  }

  public Datetime getReleasedDate() {
    if(releasedDate == null)
      return null;
    return new Datetime(Datetime.YEAR,Datetime.SECOND,releasedDate);
  }
  public void setReleasedDate (Datetime released_date){
    this.releasedDate = released_date.getDate();
  }

  public Datetime getPrintedDate() {
    if(printedDate == null)
      return null;
    return new Datetime(Datetime.YEAR,Datetime.SECOND,printedDate);
  }
  public void setPrintedDate (Datetime printed_date){
    this.printedDate = printed_date.getDate();
  }

  
  public void setClone() {
    try {
      original = (Analysis)this.clone();
    }catch(Exception e){}
  }
  
  public String getChangeXML() {
    try {
      Document doc = XMLUtil.createNew("change");
      Element root = doc.getDocumentElement();
      
      if((id == null && original.id != null) || 
         (id != null && !id.equals(original.id))){
        Element elem = doc.createElement("id");
        elem.appendChild(doc.createTextNode(original.id.toString()));
        root.appendChild(elem);
      }      

      if((sampleItem == null && original.sampleItem != null) || 
         (sampleItem != null && !sampleItem.equals(original.sampleItem))){
        Element elem = doc.createElement("sample_item");
        elem.appendChild(doc.createTextNode(original.sampleItem.toString()));
        root.appendChild(elem);
      }      

      if((revision == null && original.revision != null) || 
         (revision != null && !revision.equals(original.revision))){
        Element elem = doc.createElement("revision");
        elem.appendChild(doc.createTextNode(original.revision.toString()));
        root.appendChild(elem);
      }      

      if((test == null && original.test != null) || 
         (test != null && !test.equals(original.test))){
        Element elem = doc.createElement("test");
        elem.appendChild(doc.createTextNode(original.test.toString()));
        root.appendChild(elem);
      }      

      if((section == null && original.section != null) || 
         (section != null && !section.equals(original.section))){
        Element elem = doc.createElement("section");
        elem.appendChild(doc.createTextNode(original.section.toString()));
        root.appendChild(elem);
      }      

      if((preAnalysis == null && original.preAnalysis != null) || 
         (preAnalysis != null && !preAnalysis.equals(original.preAnalysis))){
        Element elem = doc.createElement("pre_analysis");
        elem.appendChild(doc.createTextNode(original.preAnalysis.toString()));
        root.appendChild(elem);
      }      

      if((parentAnalysis == null && original.parentAnalysis != null) || 
         (parentAnalysis != null && !parentAnalysis.equals(original.parentAnalysis))){
        Element elem = doc.createElement("parent_analysis");
        elem.appendChild(doc.createTextNode(original.parentAnalysis.toString()));
        root.appendChild(elem);
      }      

      if((parentResult == null && original.parentResult != null) || 
         (parentResult != null && !parentResult.equals(original.parentResult))){
        Element elem = doc.createElement("parent_result");
        elem.appendChild(doc.createTextNode(original.parentResult.toString()));
        root.appendChild(elem);
      }      

      if((isReportable == null && original.isReportable != null) || 
         (isReportable != null && !isReportable.equals(original.isReportable))){
        Element elem = doc.createElement("is_reportable");
        elem.appendChild(doc.createTextNode(original.isReportable.toString()));
        root.appendChild(elem);
      }      

      if((unitOfMeasure == null && original.unitOfMeasure != null) || 
         (unitOfMeasure != null && !unitOfMeasure.equals(original.unitOfMeasure))){
        Element elem = doc.createElement("unit_of_measure");
        elem.appendChild(doc.createTextNode(original.unitOfMeasure.toString()));
        root.appendChild(elem);
      }      

      if((status == null && original.status != null) || 
         (status != null && !status.equals(original.status))){
        Element elem = doc.createElement("status");
        elem.appendChild(doc.createTextNode(original.status.toString()));
        root.appendChild(elem);
      }      

      if((availableDate == null && original.availableDate != null) || 
         (availableDate != null && !availableDate.equals(original.availableDate))){
        Element elem = doc.createElement("available_date");
        elem.appendChild(doc.createTextNode(original.availableDate.toString()));
        root.appendChild(elem);
      }      

      if((startedDate == null && original.startedDate != null) || 
         (startedDate != null && !startedDate.equals(original.startedDate))){
        Element elem = doc.createElement("started_date");
        elem.appendChild(doc.createTextNode(original.startedDate.toString()));
        root.appendChild(elem);
      }      

      if((completedDate == null && original.completedDate != null) || 
         (completedDate != null && !completedDate.equals(original.completedDate))){
        Element elem = doc.createElement("completed_date");
        elem.appendChild(doc.createTextNode(original.completedDate.toString()));
        root.appendChild(elem);
      }      

      if((releasedDate == null && original.releasedDate != null) || 
         (releasedDate != null && !releasedDate.equals(original.releasedDate))){
        Element elem = doc.createElement("released_date");
        elem.appendChild(doc.createTextNode(original.releasedDate.toString()));
        root.appendChild(elem);
      }      

      if((printedDate == null && original.printedDate != null) || 
         (printedDate != null && !printedDate.equals(original.printedDate))){
        Element elem = doc.createElement("printed_date");
        elem.appendChild(doc.createTextNode(original.printedDate.toString()));
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
    return "analysis";
  }
  
}   
