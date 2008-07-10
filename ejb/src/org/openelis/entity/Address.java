/**
* The contents of this file are subject to the Mozilla Public License
* Version 1.1 (the "License"); you may not use this file except in
* compliance with the License. You may obtain a copy of the License at
* http://www.mozilla.org/MPL/
* 
* Software distributed under the License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
* License for the specific language governing rights and limitations under
* the License.
* 
* The Original Code is OpenELIS code.
* 
* Copyright (C) The University of Iowa.  All Rights Reserved.
*/

package org.openelis.entity;

/**
  * Address Entity POJO for database 
  */

import org.openelis.util.XMLUtil;
import org.openelis.utils.AuditUtil;
import org.openelis.utils.Auditable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name="address")
@EntityListeners({AuditUtil.class})
public class Address implements Auditable, Cloneable {
  
    @Id
    @GeneratedValue
    @Column(name="id")
    private Integer id;             

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
      if((id == null && this.id != null) || 
         (id != null && !id.equals(this.id)))
        this.id = id;
    }

    public String getMultipleUnit() {
      return multipleUnit;
    }
    public void setMultipleUnit(String multipleUnit) {
      if((multipleUnit == null && this.multipleUnit != null) || 
         (multipleUnit != null && !multipleUnit.equals(this.multipleUnit)))
        this.multipleUnit = multipleUnit;
    }

    public String getStreetAddress() {
      return streetAddress;
    }
    public void setStreetAddress(String streetAddress) {
      if((streetAddress == null && this.streetAddress != null) || 
         (streetAddress != null && !streetAddress.equals(this.streetAddress)))
        this.streetAddress = streetAddress;
    }

    public String getCity() {
      return city;
    }
    public void setCity(String city) {
      if((city == null && this.city != null) || 
         (city != null && !city.equals(this.city)))
        this.city = city;
    }

    public String getState() {
      return state;
    }
    public void setState(String state) {
      if((state == null && this.state != null) || 
         (state != null && !state.equals(this.state)))
        this.state = state;
    }

    public String getZipCode() {
      return zipCode;
    }
    public void setZipCode(String zipCode) {
      if((zipCode == null && this.zipCode != null) || 
         (zipCode != null && !zipCode.equals(this.zipCode)))
        this.zipCode = zipCode;
    }

    public String getWorkPhone() {
      return workPhone;
    }
    public void setWorkPhone(String workPhone) {
      if((workPhone == null && this.workPhone != null) || 
         (workPhone != null && !workPhone.equals(this.workPhone)))
        this.workPhone = workPhone;
    }

    public String getHomePhone() {
      return homePhone;
    }
    public void setHomePhone(String homePhone) {
      if((homePhone == null && this.homePhone != null) || 
         (homePhone != null && !homePhone.equals(this.homePhone)))
        this.homePhone = homePhone;
    }

    public String getCellPhone() {
      return cellPhone;
    }
    public void setCellPhone(String cellPhone) {
      if((cellPhone == null && this.cellPhone != null) || 
         (cellPhone != null && !cellPhone.equals(this.cellPhone)))
        this.cellPhone = cellPhone;
    }

    public String getFaxPhone() {
      return faxPhone;
    }
    public void setFaxPhone(String faxPhone) {
      if((faxPhone == null && this.faxPhone != null) || 
         (faxPhone != null && !faxPhone.equals(this.faxPhone)))
        this.faxPhone = faxPhone;
    }

    public String getEmail() {
      return email;
    }
    public void setEmail(String email) {
      if((email == null && this.email != null) || 
         (email != null && !email.equals(this.email)))
        this.email = email;
    }

    public String getCountry() {
      return country;
    }
    public void setCountry(String country) {
      if((country == null && this.country != null) || 
         (country != null && !country.equals(this.country)))
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
        
        AuditUtil.getChangeXML(id,original.id,doc,"id");

        AuditUtil.getChangeXML(multipleUnit,original.multipleUnit,doc,"multiple_unit");

        AuditUtil.getChangeXML(streetAddress,original.streetAddress,doc,"street_address");

        AuditUtil.getChangeXML(city,original.city,doc,"city");

        AuditUtil.getChangeXML(state,original.state,doc,"state");

        AuditUtil.getChangeXML(zipCode,original.zipCode,doc,"zip_code");

        AuditUtil.getChangeXML(workPhone,original.workPhone,doc,"work_phone");

        AuditUtil.getChangeXML(homePhone,original.homePhone,doc,"home_phone");

        AuditUtil.getChangeXML(cellPhone,original.cellPhone,doc,"cell_phone");

        AuditUtil.getChangeXML(faxPhone,original.faxPhone,doc,"fax_phone");

        AuditUtil.getChangeXML(email,original.email,doc,"email");

        AuditUtil.getChangeXML(country,original.country,doc,"country");

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
