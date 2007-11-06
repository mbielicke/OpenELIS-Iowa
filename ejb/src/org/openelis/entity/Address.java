
package org.openelis.entity;

/**
  * Address Entity POJO for database 
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
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import org.openelis.utils.AuditUtil;
import org.openelis.util.Auditable;

@Entity
@Table(name="address")
@EntityListeners({AuditUtil.class})
public class Address implements Auditable, Cloneable {
  
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name="id")
  private Integer id;             

  @Column(name="reference_id")
  private Integer referenceId;             

  @Column(name="reference_table")
  private Integer referenceTable;             

  @Column(name="type")
  private Integer type;             

  @Column(name="multiple_unit")
  private String multipleUnit;             

  @Column(name="street_address")
  private String streetAddress;             

  @Column(name="city")
  private String city;             

  @Column(name="state")
  private String state;             

  @Column(name="zip_code")
  private String zipCode;             

  @Column(name="work_phone")
  private String workPhone;             

  @Column(name="home_phone")
  private String homePhone;             

  @Column(name="cell_phone")
  private String cellPhone;             

  @Column(name="fax_phone")
  private String faxPhone;             

  @Column(name="email")
  private String email;             

  @Column(name="country")
  private String country;             


  @Transient
  private Address original;

  
  public Integer getId() {
    return id;
  }
  protected void setId(Integer id) {
    this.id = id;
  }

  public Integer getReferenceId() {
    return referenceId;
  }
  public void setReferenceId(Integer referenceId) {
    this.referenceId = referenceId;
  }

  public Integer getReferenceTable() {
    return referenceTable;
  }
  public void setReferenceTable(Integer referenceTable) {
    this.referenceTable = referenceTable;
  }

  public Integer getType() {
    return type;
  }
  public void setType(Integer type) {
    this.type = type;
  }

  public String getMultipleUnit() {
    return multipleUnit;
  }
  public void setMultipleUnit(String multipleUnit) {
    this.multipleUnit = multipleUnit;
  }

  public String getStreetAddress() {
    return streetAddress;
  }
  public void setStreetAddress(String streetAddress) {
    this.streetAddress = streetAddress;
  }

  public String getCity() {
    return city;
  }
  public void setCity(String city) {
    this.city = city;
  }

  public String getState() {
    return state;
  }
  public void setState(String state) {
    this.state = state;
  }

  public String getZipCode() {
    return zipCode;
  }
  public void setZipCode(String zipCode) {
    this.zipCode = zipCode;
  }

  public String getWorkPhone() {
    return workPhone;
  }
  public void setWorkPhone(String workPhone) {
    this.workPhone = workPhone;
  }

  public String getHomePhone() {
    return homePhone;
  }
  public void setHomePhone(String homePhone) {
    this.homePhone = homePhone;
  }

  public String getCellPhone() {
    return cellPhone;
  }
  public void setCellPhone(String cellPhone) {
    this.cellPhone = cellPhone;
  }

  public String getFaxPhone() {
    return faxPhone;
  }
  public void setFaxPhone(String faxPhone) {
    this.faxPhone = faxPhone;
  }

  public String getEmail() {
    return email;
  }
  public void setEmail(String email) {
    this.email = email;
  }

  public String getCountry() {
    return country;
  }
  public void setCountry(String country) {
    this.country = country;
  }

  
  public void setClone() {
    try {
      original = (Address)this.clone();
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

      if((referenceId == null && original.referenceId != null) || 
         (referenceId != null && !referenceId.equals(original.referenceId))){
        Element elem = doc.createElement("reference_id");
        elem.appendChild(doc.createTextNode(original.referenceId.toString()));
        root.appendChild(elem);
      }      

      if((referenceTable == null && original.referenceTable != null) || 
         (referenceTable != null && !referenceTable.equals(original.referenceTable))){
        Element elem = doc.createElement("reference_table");
        elem.appendChild(doc.createTextNode(original.referenceTable.toString()));
        root.appendChild(elem);
      }      

      if((type == null && original.type != null) || 
         (type != null && !type.equals(original.type))){
        Element elem = doc.createElement("type");
        elem.appendChild(doc.createTextNode(original.type.toString()));
        root.appendChild(elem);
      }      

      if((multipleUnit == null && original.multipleUnit != null) || 
         (multipleUnit != null && !multipleUnit.equals(original.multipleUnit))){
        Element elem = doc.createElement("multiple_unit");
        elem.appendChild(doc.createTextNode(original.multipleUnit.toString()));
        root.appendChild(elem);
      }      

      if((streetAddress == null && original.streetAddress != null) || 
         (streetAddress != null && !streetAddress.equals(original.streetAddress))){
        Element elem = doc.createElement("street_address");
        elem.appendChild(doc.createTextNode(original.streetAddress.toString()));
        root.appendChild(elem);
      }      

      if((city == null && original.city != null) || 
         (city != null && !city.equals(original.city))){
        Element elem = doc.createElement("city");
        elem.appendChild(doc.createTextNode(original.city.toString()));
        root.appendChild(elem);
      }      

      if((state == null && original.state != null) || 
         (state != null && !state.equals(original.state))){
        Element elem = doc.createElement("state");
        elem.appendChild(doc.createTextNode(original.state.toString()));
        root.appendChild(elem);
      }      

      if((zipCode == null && original.zipCode != null) || 
         (zipCode != null && !zipCode.equals(original.zipCode))){
        Element elem = doc.createElement("zip_code");
        elem.appendChild(doc.createTextNode(original.zipCode.toString()));
        root.appendChild(elem);
      }      

      if((workPhone == null && original.workPhone != null) || 
         (workPhone != null && !workPhone.equals(original.workPhone))){
        Element elem = doc.createElement("work_phone");
        elem.appendChild(doc.createTextNode(original.workPhone.toString()));
        root.appendChild(elem);
      }      

      if((homePhone == null && original.homePhone != null) || 
         (homePhone != null && !homePhone.equals(original.homePhone))){
        Element elem = doc.createElement("home_phone");
        elem.appendChild(doc.createTextNode(original.homePhone.toString()));
        root.appendChild(elem);
      }      

      if((cellPhone == null && original.cellPhone != null) || 
         (cellPhone != null && !cellPhone.equals(original.cellPhone))){
        Element elem = doc.createElement("cell_phone");
        elem.appendChild(doc.createTextNode(original.cellPhone.toString()));
        root.appendChild(elem);
      }      

      if((faxPhone == null && original.faxPhone != null) || 
         (faxPhone != null && !faxPhone.equals(original.faxPhone))){
        Element elem = doc.createElement("fax_phone");
        elem.appendChild(doc.createTextNode(original.faxPhone.toString()));
        root.appendChild(elem);
      }      

      if((email == null && original.email != null) || 
         (email != null && !email.equals(original.email))){
        Element elem = doc.createElement("email");
        elem.appendChild(doc.createTextNode(original.email.toString()));
        root.appendChild(elem);
      }      

      if((country == null && original.country != null) || 
         (country != null && !country.equals(original.country))){
        Element elem = doc.createElement("country");
        elem.appendChild(doc.createTextNode(original.country.toString()));
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
    return "address";
  }
  
}   
