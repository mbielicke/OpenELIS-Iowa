
package org.openelis.entity;

/**
  * PatientRelation Entity POJO for database 
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
@Table(name="patient_relation")
@EntityListeners({AuditUtil.class})
public class PatientRelation implements Auditable, Cloneable {
  
  @Id
  @GeneratedValue
  @Column(name="id")
  private Integer id;             

  @Column(name="relation")
  private Integer relation;             

  @Column(name="from_patient")
  private Integer fromPatient;             

  @Column(name="to_patient")
  private Integer toPatient;             


  @Transient
  private PatientRelation original;

  
  public Integer getId() {
    return id;
  }
  protected void setId(Integer id) {
    this.id = id;
  }

  public Integer getRelation() {
    return relation;
  }
  public void setRelation(Integer relation) {
    this.relation = relation;
  }

  public Integer getFromPatient() {
    return fromPatient;
  }
  public void setFromPatient(Integer fromPatient) {
    this.fromPatient = fromPatient;
  }

  public Integer getToPatient() {
    return toPatient;
  }
  public void setToPatient(Integer toPatient) {
    this.toPatient = toPatient;
  }

  
  public void setClone() {
    try {
      original = (PatientRelation)this.clone();
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

      if((relation == null && original.relation != null) || 
         (relation != null && !relation.equals(original.relation))){
        Element elem = doc.createElement("relation");
        elem.appendChild(doc.createTextNode(original.relation.toString()));
        root.appendChild(elem);
      }      

      if((fromPatient == null && original.fromPatient != null) || 
         (fromPatient != null && !fromPatient.equals(original.fromPatient))){
        Element elem = doc.createElement("from_patient");
        elem.appendChild(doc.createTextNode(original.fromPatient.toString()));
        root.appendChild(elem);
      }      

      if((toPatient == null && original.toPatient != null) || 
         (toPatient != null && !toPatient.equals(original.toPatient))){
        Element elem = doc.createElement("to_patient");
        elem.appendChild(doc.createTextNode(original.toPatient.toString()));
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
    return "patient_relation";
  }
  
}   
