
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
import org.openelis.utils.Auditable;

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
    if((id == null && this.id != null) || 
       (id != null && !id.equals(this.id)))
      this.id = id;
  }

  public Integer getSampleItem() {
    return sampleItem;
  }
  public void setSampleItem(Integer sampleItem) {
    if((sampleItem == null && this.sampleItem != null) || 
       (sampleItem != null && !sampleItem.equals(this.sampleItem)))
      this.sampleItem = sampleItem;
  }

  public Integer getRevision() {
    return revision;
  }
  public void setRevision(Integer revision) {
    if((revision == null && this.revision != null) || 
       (revision != null && !revision.equals(this.revision)))
      this.revision = revision;
  }

  public Integer getTest() {
    return test;
  }
  public void setTest(Integer test) {
    if((test == null && this.test != null) || 
       (test != null && !test.equals(this.test)))
      this.test = test;
  }

  public Integer getSection() {
    return section;
  }
  public void setSection(Integer section) {
    if((section == null && this.section != null) || 
       (section != null && !section.equals(this.section)))
      this.section = section;
  }

  public Integer getPreAnalysis() {
    return preAnalysis;
  }
  public void setPreAnalysis(Integer preAnalysis) {
    if((preAnalysis == null && this.preAnalysis != null) || 
       (preAnalysis != null && !preAnalysis.equals(this.preAnalysis)))
      this.preAnalysis = preAnalysis;
  }

  public Integer getParentAnalysis() {
    return parentAnalysis;
  }
  public void setParentAnalysis(Integer parentAnalysis) {
    if((parentAnalysis == null && this.parentAnalysis != null) || 
       (parentAnalysis != null && !parentAnalysis.equals(this.parentAnalysis)))
      this.parentAnalysis = parentAnalysis;
  }

  public Integer getParentResult() {
    return parentResult;
  }
  public void setParentResult(Integer parentResult) {
    if((parentResult == null && this.parentResult != null) || 
       (parentResult != null && !parentResult.equals(this.parentResult)))
      this.parentResult = parentResult;
  }

  public String getIsReportable() {
    return isReportable;
  }
  public void setIsReportable(String isReportable) {
    if((isReportable == null && this.isReportable != null) || 
       (isReportable != null && !isReportable.equals(this.isReportable)))
      this.isReportable = isReportable;
  }

  public Integer getUnitOfMeasure() {
    return unitOfMeasure;
  }
  public void setUnitOfMeasure(Integer unitOfMeasure) {
    if((unitOfMeasure == null && this.unitOfMeasure != null) || 
       (unitOfMeasure != null && !unitOfMeasure.equals(this.unitOfMeasure)))
      this.unitOfMeasure = unitOfMeasure;
  }

  public Integer getStatus() {
    return status;
  }
  public void setStatus(Integer status) {
    if((status == null && this.status != null) || 
       (status != null && !status.equals(this.status)))
      this.status = status;
  }

  public Datetime getAvailableDate() {
    if(availableDate == null)
      return null;
    return new Datetime(Datetime.YEAR,Datetime.SECOND,availableDate);
  }
  public void setAvailableDate (Datetime available_date){
    if((availableDate == null && this.availableDate != null) || 
       (availableDate != null && !availableDate.equals(this.availableDate)))
      this.availableDate = available_date.getDate();
  }

  public Datetime getStartedDate() {
    if(startedDate == null)
      return null;
    return new Datetime(Datetime.YEAR,Datetime.SECOND,startedDate);
  }
  public void setStartedDate (Datetime started_date){
    if((startedDate == null && this.startedDate != null) || 
       (startedDate != null && !startedDate.equals(this.startedDate)))
      this.startedDate = started_date.getDate();
  }

  public Datetime getCompletedDate() {
    if(completedDate == null)
      return null;
    return new Datetime(Datetime.YEAR,Datetime.SECOND,completedDate);
  }
  public void setCompletedDate (Datetime completed_date){
    if((completedDate == null && this.completedDate != null) || 
       (completedDate != null && !completedDate.equals(this.completedDate)))
      this.completedDate = completed_date.getDate();
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

  public Datetime getPrintedDate() {
    if(printedDate == null)
      return null;
    return new Datetime(Datetime.YEAR,Datetime.SECOND,printedDate);
  }
  public void setPrintedDate (Datetime printed_date){
    if((printedDate == null && this.printedDate != null) || 
       (printedDate != null && !printedDate.equals(this.printedDate)))
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
