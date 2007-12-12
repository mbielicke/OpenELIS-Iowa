
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

  @Column(name="last_name")
  private String lastName;             

  @Column(name="first_name")
  private String firstName;             

  @Column(name="middle_name")
  private String middleName;             

  @Column(name="address")
  private Integer address;             

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

  public String getLastName() {
    return lastName;
  }
  public void setLastName(String lastName) {
    if((lastName == null && this.lastName != null) || 
       (lastName != null && !lastName.equals(this.lastName)))
      this.lastName = lastName;
  }

  public String getFirstName() {
    return firstName;
  }
  public void setFirstName(String firstName) {
    if((firstName == null && this.firstName != null) || 
       (firstName != null && !firstName.equals(this.firstName)))
      this.firstName = firstName;
  }

  public String getMiddleName() {
    return middleName;
  }
  public void setMiddleName(String middleName) {
    if((middleName == null && this.middleName != null) || 
       (middleName != null && !middleName.equals(this.middleName)))
      this.middleName = middleName;
  }

  public Integer getAddress() {
    return address;
  }
  public void setAddress(Integer address) {
    if((address == null && this.address != null) || 
       (address != null && !address.equals(this.address)))
      this.address = address;
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

      if((lastName == null && original.lastName != null) || 
         (lastName != null && !lastName.equals(original.lastName))){
        Element elem = doc.createElement("last_name");
        elem.appendChild(doc.createTextNode(original.lastName.toString()));
        root.appendChild(elem);
      }      

      if((firstName == null && original.firstName != null) || 
         (firstName != null && !firstName.equals(original.firstName))){
        Element elem = doc.createElement("first_name");
        elem.appendChild(doc.createTextNode(original.firstName.toString()));
        root.appendChild(elem);
      }      

      if((middleName == null && original.middleName != null) || 
         (middleName != null && !middleName.equals(original.middleName))){
        Element elem = doc.createElement("middle_name");
        elem.appendChild(doc.createTextNode(original.middleName.toString()));
        root.appendChild(elem);
      }      

      if((address == null && original.address != null) || 
         (address != null && !address.equals(original.address))){
        Element elem = doc.createElement("address");
        elem.appendChild(doc.createTextNode(original.address.toString()));
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
