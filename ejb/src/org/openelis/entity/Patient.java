
package org.openelis.entity;

/**
  * Patient Entity POJO for database 
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
@Table(name="patient")
@EntityListeners({AuditUtil.class})
public class Patient implements Auditable, Cloneable {
  
  @Id
  @GeneratedValue
  @Column(name="id")
  private Integer id;             

  @Column(name="person")
  private Integer person;             

  @Column(name="birth_date")
  private Date birthDate;             

  @Column(name="birth_time")
  private Date birthTime;             

  @Column(name="gender")
  private Integer gender;             

  @Column(name="race")
  private String race;             

  @Column(name="ethnicity")
  private Integer ethnicity;             


  @Transient
  private Patient original;

  
  public Integer getId() {
    return id;
  }
  protected void setId(Integer id) {
    if((id == null && this.id != null) || 
       (id != null && !id.equals(this.id)))
      this.id = id;
  }

  public Integer getPerson() {
    return person;
  }
  public void setPerson(Integer person) {
    if((person == null && this.person != null) || 
       (person != null && !person.equals(this.person)))
      this.person = person;
  }

  public Datetime getBirthDate() {
    if(birthDate == null)
      return null;
    return new Datetime(Datetime.YEAR ,Datetime. DAY,birthDate);
  }
  public void setBirthDate (Datetime birth_date){
    if((birthDate == null && this.birthDate != null) || 
       (birthDate != null && !birthDate.equals(this.birthDate)))
      this.birthDate = birth_date.getDate();
  }

  public Datetime getBirthTime() {
    if(birthTime == null)
      return null;
    return new Datetime(Datetime.HOUR,Datetime.MINUTE,birthTime);
  }
  public void setBirthTime (Datetime birth_time){
    if((birthTime == null && this.birthTime != null) || 
       (birthTime != null && !birthTime.equals(this.birthTime)))
      this.birthTime = birth_time.getDate();
  }

  public Integer getGender() {
    return gender;
  }
  public void setGender(Integer gender) {
    if((gender == null && this.gender != null) || 
       (gender != null && !gender.equals(this.gender)))
      this.gender = gender;
  }

  public String getRace() {
    return race;
  }
  public void setRace(String race) {
    if((race == null && this.race != null) || 
       (race != null && !race.equals(this.race)))
      this.race = race;
  }

  public Integer getEthnicity() {
    return ethnicity;
  }
  public void setEthnicity(Integer ethnicity) {
    if((ethnicity == null && this.ethnicity != null) || 
       (ethnicity != null && !ethnicity.equals(this.ethnicity)))
      this.ethnicity = ethnicity;
  }

  
  public void setClone() {
    try {
      original = (Patient)this.clone();
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

      if((person == null && original.person != null) || 
         (person != null && !person.equals(original.person))){
        Element elem = doc.createElement("person");
        elem.appendChild(doc.createTextNode(original.person.toString()));
        root.appendChild(elem);
      }      

      if((birthDate == null && original.birthDate != null) || 
         (birthDate != null && !birthDate.equals(original.birthDate))){
        Element elem = doc.createElement("birth_date");
        elem.appendChild(doc.createTextNode(original.birthDate.toString()));
        root.appendChild(elem);
      }      

      if((birthTime == null && original.birthTime != null) || 
         (birthTime != null && !birthTime.equals(original.birthTime))){
        Element elem = doc.createElement("birth_time");
        elem.appendChild(doc.createTextNode(original.birthTime.toString()));
        root.appendChild(elem);
      }      

      if((gender == null && original.gender != null) || 
         (gender != null && !gender.equals(original.gender))){
        Element elem = doc.createElement("gender");
        elem.appendChild(doc.createTextNode(original.gender.toString()));
        root.appendChild(elem);
      }      

      if((race == null && original.race != null) || 
         (race != null && !race.equals(original.race))){
        Element elem = doc.createElement("race");
        elem.appendChild(doc.createTextNode(original.race.toString()));
        root.appendChild(elem);
      }      

      if((ethnicity == null && original.ethnicity != null) || 
         (ethnicity != null && !ethnicity.equals(original.ethnicity))){
        Element elem = doc.createElement("ethnicity");
        elem.appendChild(doc.createTextNode(original.ethnicity.toString()));
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
    return "patient";
  }
  
}   
