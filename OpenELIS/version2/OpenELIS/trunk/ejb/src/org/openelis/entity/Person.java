
package org.openelis.entity;

/**
  * Person Entity POJO for database 
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
@Table(name="person")
@EntityListeners({AuditUtil.class})
public class Person implements Auditable, Cloneable {
  
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


  @Transient
  private Person original;

  
  public Integer getId() {
    return id;
  }
  protected void setId(Integer id) {
    this.id = id;
  }

  public String getLastName() {
    return lastName;
  }
  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public String getFirstName() {
    return firstName;
  }
  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getMiddleName() {
    return middleName;
  }
  public void setMiddleName(String middleName) {
    this.middleName = middleName;
  }

  
  public void setClone() {
    try {
      original = (Person)this.clone();
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

      if(root.hasChildNodes())
        return XMLUtil.toString(doc);
    }catch(Exception e){
      e.printStackTrace();
    }
    return null;
  }
   
  public String getTableName() {
    return "person";
  }
  
}   
