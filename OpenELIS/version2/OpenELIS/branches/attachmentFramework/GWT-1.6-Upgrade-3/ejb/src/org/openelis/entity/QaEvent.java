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
  * Qaevent Entity POJO for database 
  */

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.openelis.util.XMLUtil;

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
import org.openelis.utils.AuditUtil;
import org.openelis.utils.Auditable;

@Entity
@Table(name="qaevent")
@EntityListeners({AuditUtil.class})
@NamedQueries({@NamedQuery(name = "QaEvent.QaEvent", query = "select new org.openelis.domain.QaEventDO(q.id, q.name, q.description, q.testId,  q.typeId,  q.isBillable, q.reportingSequence, q.reportingText)" +                                                                                                  
                                                             "  from QaEvent q where q.id = :id"),
               @NamedQuery(name = "QaEvent.IdByTestId", query = "select q.id from QaEvent q where q.testId = :testId")})
               
public class QaEvent implements Auditable, Cloneable {
  
  @Id
  @GeneratedValue
  @Column(name="id")
  private Integer id;             

  @Column(name="name")
  private String name;             

  @Column(name="description")
  private String description;             

  @Column(name="test_id")
  private Integer testId;             

  @Column(name="type_id")
  private Integer typeId;             

  @Column(name="is_billable")
  private String isBillable;             

  @Column(name="reporting_sequence")
  private Integer reportingSequence;             

  @Column(name="reporting_text")
  private String reportingText;             

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "test_id",insertable = false, updatable = false)
  private Test test; 
  
  @Transient
  private QaEvent original;

  
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

  public Integer getTestId() {
    return testId;
  }
  public void setTestId(Integer testId) {
    if((testId == null && this.testId != null) || 
       (testId != null && !testId.equals(this.testId)))
      this.testId = testId;
  }

  public Integer getTypeId() {
    return typeId;
  }
  public void setTypeId(Integer typeId) {
    if((typeId == null && this.typeId != null) || 
       (typeId != null && !typeId.equals(this.typeId)))
      this.typeId = typeId;
  }

  public String getIsBillable() {
    return isBillable;
  }
  public void setIsBillable(String isBillable) {
    if((isBillable == null && this.isBillable != null) || 
       (isBillable != null && !isBillable.equals(this.isBillable)))
      this.isBillable = isBillable;
  }

  public Integer getReportingSequence() {
    return reportingSequence;
  }
  public void setReportingSequence(Integer reportingSequence) {
    if((reportingSequence == null && this.reportingSequence != null) || 
       (reportingSequence != null && !reportingSequence.equals(this.reportingSequence)))
      this.reportingSequence = reportingSequence;
  }

  public String getReportingText() {
    return reportingText;
  }
  public void setReportingText(String reportingText) {
    if((reportingText == null && this.reportingText != null) || 
       (reportingText != null && !reportingText.equals(this.reportingText)))
      this.reportingText = reportingText;
  }

  
  public void setClone() {
    try {
      original = (QaEvent)this.clone();
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

        if((testId == null && original.testId != null) || 
           (testId != null && !testId.equals(original.testId))){
          Element elem = doc.createElement("test");
          elem.appendChild(doc.createTextNode(original.testId.toString().trim()));
          root.appendChild(elem);
        }      

        if((typeId == null && original.typeId != null) || 
           (typeId != null && !typeId.equals(original.typeId))){
          Element elem = doc.createElement("type");
          elem.appendChild(doc.createTextNode(original.typeId.toString().trim()));
          root.appendChild(elem);
        }      

        if((isBillable == null && original.isBillable != null) || 
           (isBillable != null && !isBillable.equals(original.isBillable))){
          Element elem = doc.createElement("is_billable");
          elem.appendChild(doc.createTextNode(original.isBillable.toString().trim()));
          root.appendChild(elem);
        }      

        if((reportingSequence == null && original.reportingSequence != null) || 
           (reportingSequence != null && !reportingSequence.equals(original.reportingSequence))){
          Element elem = doc.createElement("reporting_sequence");
          elem.appendChild(doc.createTextNode(original.reportingSequence.toString().trim()));
          root.appendChild(elem);
        }      

        if((reportingText == null && original.reportingText != null) || 
           (reportingText != null && !reportingText.equals(original.reportingText))){
          Element elem = doc.createElement("reporting_text");
          elem.appendChild(doc.createTextNode(original.reportingText.toString().trim()));
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
    return "qaevent";
  }
  
  public Test getTest() {
       return test ;
  }

  public void setTest(Test test) {
    this.test = test;
   }
  
  

  
}   
