
package org.openelis.entity;

/**
  * Test Entity POJO for database 
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
@Table(name="test")
@EntityListeners({AuditUtil.class})
public class Test implements Auditable, Cloneable {
  
  @Id
  @GeneratedValue
  @Column(name="id")
  private Integer id;             

  @Column(name="name")
  private String name;             

  @Column(name="description")
  private String description;             

  @Column(name="reporting_description")
  private String reportingDescription;             

  @Column(name="method")
  private Integer method;             

  @Column(name="is_active")
  private String isActive;             

  @Column(name="active_begin")
  private Date activeBegin;             

  @Column(name="active_end")
  private Date activeEnd;             

  @Column(name="is_reportable")
  private String isReportable;             

  @Column(name="time_transit")
  private Integer timeTransit;             

  @Column(name="time_holding")
  private Integer timeHolding;             

  @Column(name="time_ta_average")
  private Integer timeTaAverage;             

  @Column(name="time_ta_warning")
  private Integer timeTaWarning;             

  @Column(name="time_ta_max")
  private Integer timeTaMax;             

  @Column(name="label")
  private Integer label;             

  @Column(name="label_qty")
  private Integer labelQty;             

  @Column(name="test_trailer")
  private Integer testTrailer;             

  @Column(name="section")
  private Integer section;             

  @Column(name="scriptlet")
  private Integer scriptlet;             

  @Column(name="test_format")
  private Integer testFormat;             

  @Column(name="revision_method")
  private Integer revisionMethod;             


  @Transient
  private Test original;

  
  public Integer getId() {
    return id;
  }
  protected void setId(Integer id) {
    if((id == null && this.id != null) || 
       (id != null && !id.equals(this.id)))
      this.id = id;
  }

  public String getName() {
    return name;
  }
  public void setName(String name) {
    if((name == null && this.name != null) || 
       (name != null && !name.equals(this.name)))
      this.name = name;
  }

  public String getDescription() {
    return description;
  }
  public void setDescription(String description) {
    if((description == null && this.description != null) || 
       (description != null && !description.equals(this.description)))
      this.description = description;
  }

  public String getReportingDescription() {
    return reportingDescription;
  }
  public void setReportingDescription(String reportingDescription) {
    if((reportingDescription == null && this.reportingDescription != null) || 
       (reportingDescription != null && !reportingDescription.equals(this.reportingDescription)))
      this.reportingDescription = reportingDescription;
  }

  public Integer getMethod() {
    return method;
  }
  public void setMethod(Integer method) {
    if((method == null && this.method != null) || 
       (method != null && !method.equals(this.method)))
      this.method = method;
  }

  public String getIsActive() {
    return isActive;
  }
  public void setIsActive(String isActive) {
    if((isActive == null && this.isActive != null) || 
       (isActive != null && !isActive.equals(this.isActive)))
      this.isActive = isActive;
  }

  public Datetime getActiveBegin() {
    if(activeBegin == null)
      return null;
    return new Datetime(Datetime.YEAR ,Datetime. DAY,activeBegin);
  }
  public void setActiveBegin (Datetime active_begin){
    if((activeBegin == null && this.activeBegin != null) || 
       (activeBegin != null && !activeBegin.equals(this.activeBegin)))
      this.activeBegin = active_begin.getDate();
  }

  public Datetime getActiveEnd() {
    if(activeEnd == null)
      return null;
    return new Datetime(Datetime.YEAR ,Datetime. DAY,activeEnd);
  }
  public void setActiveEnd (Datetime active_end){
    if((activeEnd == null && this.activeEnd != null) || 
       (activeEnd != null && !activeEnd.equals(this.activeEnd)))
      this.activeEnd = active_end.getDate();
  }

  public String getIsReportable() {
    return isReportable;
  }
  public void setIsReportable(String isReportable) {
    if((isReportable == null && this.isReportable != null) || 
       (isReportable != null && !isReportable.equals(this.isReportable)))
      this.isReportable = isReportable;
  }

  public Integer getTimeTransit() {
    return timeTransit;
  }
  public void setTimeTransit(Integer timeTransit) {
    if((timeTransit == null && this.timeTransit != null) || 
       (timeTransit != null && !timeTransit.equals(this.timeTransit)))
      this.timeTransit = timeTransit;
  }

  public Integer getTimeHolding() {
    return timeHolding;
  }
  public void setTimeHolding(Integer timeHolding) {
    if((timeHolding == null && this.timeHolding != null) || 
       (timeHolding != null && !timeHolding.equals(this.timeHolding)))
      this.timeHolding = timeHolding;
  }

  public Integer getTimeTaAverage() {
    return timeTaAverage;
  }
  public void setTimeTaAverage(Integer timeTaAverage) {
    if((timeTaAverage == null && this.timeTaAverage != null) || 
       (timeTaAverage != null && !timeTaAverage.equals(this.timeTaAverage)))
      this.timeTaAverage = timeTaAverage;
  }

  public Integer getTimeTaWarning() {
    return timeTaWarning;
  }
  public void setTimeTaWarning(Integer timeTaWarning) {
    if((timeTaWarning == null && this.timeTaWarning != null) || 
       (timeTaWarning != null && !timeTaWarning.equals(this.timeTaWarning)))
      this.timeTaWarning = timeTaWarning;
  }

  public Integer getTimeTaMax() {
    return timeTaMax;
  }
  public void setTimeTaMax(Integer timeTaMax) {
    if((timeTaMax == null && this.timeTaMax != null) || 
       (timeTaMax != null && !timeTaMax.equals(this.timeTaMax)))
      this.timeTaMax = timeTaMax;
  }

  public Integer getLabel() {
    return label;
  }
  public void setLabel(Integer label) {
    if((label == null && this.label != null) || 
       (label != null && !label.equals(this.label)))
      this.label = label;
  }

  public Integer getLabelQty() {
    return labelQty;
  }
  public void setLabelQty(Integer labelQty) {
    if((labelQty == null && this.labelQty != null) || 
       (labelQty != null && !labelQty.equals(this.labelQty)))
      this.labelQty = labelQty;
  }

  public Integer getTestTrailer() {
    return testTrailer;
  }
  public void setTestTrailer(Integer testTrailer) {
    if((testTrailer == null && this.testTrailer != null) || 
       (testTrailer != null && !testTrailer.equals(this.testTrailer)))
      this.testTrailer = testTrailer;
  }

  public Integer getSection() {
    return section;
  }
  public void setSection(Integer section) {
    if((section == null && this.section != null) || 
       (section != null && !section.equals(this.section)))
      this.section = section;
  }

  public Integer getScriptlet() {
    return scriptlet;
  }
  public void setScriptlet(Integer scriptlet) {
    if((scriptlet == null && this.scriptlet != null) || 
       (scriptlet != null && !scriptlet.equals(this.scriptlet)))
      this.scriptlet = scriptlet;
  }

  public Integer getTestFormat() {
    return testFormat;
  }
  public void setTestFormat(Integer testFormat) {
    if((testFormat == null && this.testFormat != null) || 
       (testFormat != null && !testFormat.equals(this.testFormat)))
      this.testFormat = testFormat;
  }

  public Integer getRevisionMethod() {
    return revisionMethod;
  }
  public void setRevisionMethod(Integer revisionMethod) {
    if((revisionMethod == null && this.revisionMethod != null) || 
       (revisionMethod != null && !revisionMethod.equals(this.revisionMethod)))
      this.revisionMethod = revisionMethod;
  }

  
  public void setClone() {
    try {
      original = (Test)this.clone();
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

      if((name == null && original.name != null) || 
         (name != null && !name.equals(original.name))){
        Element elem = doc.createElement("name");
        elem.appendChild(doc.createTextNode(original.name.toString().trim()));
        root.appendChild(elem);
      }      

      if((description == null && original.description != null) || 
         (description != null && !description.equals(original.description))){
        Element elem = doc.createElement("description");
        elem.appendChild(doc.createTextNode(original.description.toString().trim()));
        root.appendChild(elem);
      }      

      if((reportingDescription == null && original.reportingDescription != null) || 
         (reportingDescription != null && !reportingDescription.equals(original.reportingDescription))){
        Element elem = doc.createElement("reporting_description");
        elem.appendChild(doc.createTextNode(original.reportingDescription.toString().trim()));
        root.appendChild(elem);
      }      

      if((method == null && original.method != null) || 
         (method != null && !method.equals(original.method))){
        Element elem = doc.createElement("method");
        elem.appendChild(doc.createTextNode(original.method.toString().trim()));
        root.appendChild(elem);
      }      

      if((isActive == null && original.isActive != null) || 
         (isActive != null && !isActive.equals(original.isActive))){
        Element elem = doc.createElement("is_active");
        elem.appendChild(doc.createTextNode(original.isActive.toString().trim()));
        root.appendChild(elem);
      }      

      if((activeBegin == null && original.activeBegin != null) || 
         (activeBegin != null && !activeBegin.equals(original.activeBegin))){
        Element elem = doc.createElement("active_begin");
        elem.appendChild(doc.createTextNode(original.activeBegin.toString().trim()));
        root.appendChild(elem);
      }      

      if((activeEnd == null && original.activeEnd != null) || 
         (activeEnd != null && !activeEnd.equals(original.activeEnd))){
        Element elem = doc.createElement("active_end");
        elem.appendChild(doc.createTextNode(original.activeEnd.toString().trim()));
        root.appendChild(elem);
      }      

      if((isReportable == null && original.isReportable != null) || 
         (isReportable != null && !isReportable.equals(original.isReportable))){
        Element elem = doc.createElement("is_reportable");
        elem.appendChild(doc.createTextNode(original.isReportable.toString().trim()));
        root.appendChild(elem);
      }      

      if((timeTransit == null && original.timeTransit != null) || 
         (timeTransit != null && !timeTransit.equals(original.timeTransit))){
        Element elem = doc.createElement("time_transit");
        elem.appendChild(doc.createTextNode(original.timeTransit.toString().trim()));
        root.appendChild(elem);
      }      

      if((timeHolding == null && original.timeHolding != null) || 
         (timeHolding != null && !timeHolding.equals(original.timeHolding))){
        Element elem = doc.createElement("time_holding");
        elem.appendChild(doc.createTextNode(original.timeHolding.toString().trim()));
        root.appendChild(elem);
      }      

      if((timeTaAverage == null && original.timeTaAverage != null) || 
         (timeTaAverage != null && !timeTaAverage.equals(original.timeTaAverage))){
        Element elem = doc.createElement("time_ta_average");
        elem.appendChild(doc.createTextNode(original.timeTaAverage.toString().trim()));
        root.appendChild(elem);
      }      

      if((timeTaWarning == null && original.timeTaWarning != null) || 
         (timeTaWarning != null && !timeTaWarning.equals(original.timeTaWarning))){
        Element elem = doc.createElement("time_ta_warning");
        elem.appendChild(doc.createTextNode(original.timeTaWarning.toString().trim()));
        root.appendChild(elem);
      }      

      if((timeTaMax == null && original.timeTaMax != null) || 
         (timeTaMax != null && !timeTaMax.equals(original.timeTaMax))){
        Element elem = doc.createElement("time_ta_max");
        elem.appendChild(doc.createTextNode(original.timeTaMax.toString().trim()));
        root.appendChild(elem);
      }      

      if((label == null && original.label != null) || 
         (label != null && !label.equals(original.label))){
        Element elem = doc.createElement("label");
        elem.appendChild(doc.createTextNode(original.label.toString().trim()));
        root.appendChild(elem);
      }      

      if((labelQty == null && original.labelQty != null) || 
         (labelQty != null && !labelQty.equals(original.labelQty))){
        Element elem = doc.createElement("label_qty");
        elem.appendChild(doc.createTextNode(original.labelQty.toString().trim()));
        root.appendChild(elem);
      }      

      if((testTrailer == null && original.testTrailer != null) || 
         (testTrailer != null && !testTrailer.equals(original.testTrailer))){
        Element elem = doc.createElement("test_trailer");
        elem.appendChild(doc.createTextNode(original.testTrailer.toString().trim()));
        root.appendChild(elem);
      }      

      if((section == null && original.section != null) || 
         (section != null && !section.equals(original.section))){
        Element elem = doc.createElement("section");
        elem.appendChild(doc.createTextNode(original.section.toString().trim()));
        root.appendChild(elem);
      }      

      if((scriptlet == null && original.scriptlet != null) || 
         (scriptlet != null && !scriptlet.equals(original.scriptlet))){
        Element elem = doc.createElement("scriptlet");
        elem.appendChild(doc.createTextNode(original.scriptlet.toString().trim()));
        root.appendChild(elem);
      }      

      if((testFormat == null && original.testFormat != null) || 
         (testFormat != null && !testFormat.equals(original.testFormat))){
        Element elem = doc.createElement("test_format");
        elem.appendChild(doc.createTextNode(original.testFormat.toString().trim()));
        root.appendChild(elem);
      }      

      if((revisionMethod == null && original.revisionMethod != null) || 
         (revisionMethod != null && !revisionMethod.equals(original.revisionMethod))){
        Element elem = doc.createElement("revision_method");
        elem.appendChild(doc.createTextNode(original.revisionMethod.toString().trim()));
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
    return "test";
  }
  
}   
