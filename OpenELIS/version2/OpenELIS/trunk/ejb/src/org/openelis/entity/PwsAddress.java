
package org.openelis.entity;

/**
  * PwsAddress Entity POJO for database 
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
@Table(name="pws_address")
@EntityListeners({AuditUtil.class})
public class PwsAddress implements Auditable, Cloneable {
  
  @Id
  @GeneratedValue
  @Column(name="id")
  private Integer id;             

  @Column(name="tinwsys_is_number")
  private Integer tinwsysIsNumber;             

  @Column(name="type_code")
  private String typeCode;             

  @Column(name="active_ind_cd")
  private String activeIndCd;             

  @Column(name="name")
  private String name;             

  @Column(name="address_line_one_text")
  private String addressLineOneText;             

  @Column(name="address_line_two_text")
  private String addressLineTwoText;             

  @Column(name="address_city_name")
  private String addressCityName;             

  @Column(name="address_state_code")
  private String addressStateCode;             

  @Column(name="address_zip_code")
  private String addressZipCode;             

  @Column(name="state_fips_code")
  private String stateFipsCode;             

  @Column(name="phone_number")
  private String phoneNumber;             


  @Transient
  private PwsAddress original;

  
  public Integer getId() {
    return id;
  }
  protected void setId(Integer id) {
    if((id == null && this.id != null) || 
       (id != null && !id.equals(this.id)))
      this.id = id;
  }

  public Integer getTinwsysIsNumber() {
    return tinwsysIsNumber;
  }
  public void setTinwsysIsNumber(Integer tinwsysIsNumber) {
    if((tinwsysIsNumber == null && this.tinwsysIsNumber != null) || 
       (tinwsysIsNumber != null && !tinwsysIsNumber.equals(this.tinwsysIsNumber)))
      this.tinwsysIsNumber = tinwsysIsNumber;
  }

  public String getTypeCode() {
    return typeCode;
  }
  public void setTypeCode(String typeCode) {
    if((typeCode == null && this.typeCode != null) || 
       (typeCode != null && !typeCode.equals(this.typeCode)))
      this.typeCode = typeCode;
  }

  public String getActiveIndCd() {
    return activeIndCd;
  }
  public void setActiveIndCd(String activeIndCd) {
    if((activeIndCd == null && this.activeIndCd != null) || 
       (activeIndCd != null && !activeIndCd.equals(this.activeIndCd)))
      this.activeIndCd = activeIndCd;
  }

  public String getName() {
    return name;
  }
  public void setName(String name) {
    if((name == null && this.name != null) || 
       (name != null && !name.equals(this.name)))
      this.name = name;
  }

  public String getAddressLineOneText() {
    return addressLineOneText;
  }
  public void setAddressLineOneText(String addressLineOneText) {
    if((addressLineOneText == null && this.addressLineOneText != null) || 
       (addressLineOneText != null && !addressLineOneText.equals(this.addressLineOneText)))
      this.addressLineOneText = addressLineOneText;
  }

  public String getAddressLineTwoText() {
    return addressLineTwoText;
  }
  public void setAddressLineTwoText(String addressLineTwoText) {
    if((addressLineTwoText == null && this.addressLineTwoText != null) || 
       (addressLineTwoText != null && !addressLineTwoText.equals(this.addressLineTwoText)))
      this.addressLineTwoText = addressLineTwoText;
  }

  public String getAddressCityName() {
    return addressCityName;
  }
  public void setAddressCityName(String addressCityName) {
    if((addressCityName == null && this.addressCityName != null) || 
       (addressCityName != null && !addressCityName.equals(this.addressCityName)))
      this.addressCityName = addressCityName;
  }

  public String getAddressStateCode() {
    return addressStateCode;
  }
  public void setAddressStateCode(String addressStateCode) {
    if((addressStateCode == null && this.addressStateCode != null) || 
       (addressStateCode != null && !addressStateCode.equals(this.addressStateCode)))
      this.addressStateCode = addressStateCode;
  }

  public String getAddressZipCode() {
    return addressZipCode;
  }
  public void setAddressZipCode(String addressZipCode) {
    if((addressZipCode == null && this.addressZipCode != null) || 
       (addressZipCode != null && !addressZipCode.equals(this.addressZipCode)))
      this.addressZipCode = addressZipCode;
  }

  public String getStateFipsCode() {
    return stateFipsCode;
  }
  public void setStateFipsCode(String stateFipsCode) {
    if((stateFipsCode == null && this.stateFipsCode != null) || 
       (stateFipsCode != null && !stateFipsCode.equals(this.stateFipsCode)))
      this.stateFipsCode = stateFipsCode;
  }

  public String getPhoneNumber() {
    return phoneNumber;
  }
  public void setPhoneNumber(String phoneNumber) {
    if((phoneNumber == null && this.phoneNumber != null) || 
       (phoneNumber != null && !phoneNumber.equals(this.phoneNumber)))
      this.phoneNumber = phoneNumber;
  }

  
  public void setClone() {
    try {
      original = (PwsAddress)this.clone();
    }catch(Exception e){}
  }
  
  public String getChangeXML() {
    try {
      Document doc = XMLUtil.createNew("change");
      Element root = doc.getDocumentElement();
      
      AuditUtil.getChangeXML(id,original.id,doc,"id");

      AuditUtil.getChangeXML(tinwsysIsNumber,original.tinwsysIsNumber,doc,"tinwsys_is_number");

      AuditUtil.getChangeXML(typeCode,original.typeCode,doc,"type_code");

      AuditUtil.getChangeXML(activeIndCd,original.activeIndCd,doc,"active_ind_cd");

      AuditUtil.getChangeXML(name,original.name,doc,"name");

      AuditUtil.getChangeXML(addressLineOneText,original.addressLineOneText,doc,"address_line_one_text");

      AuditUtil.getChangeXML(addressLineTwoText,original.addressLineTwoText,doc,"address_line_two_text");

      AuditUtil.getChangeXML(addressCityName,original.addressCityName,doc,"address_city_name");

      AuditUtil.getChangeXML(addressStateCode,original.addressStateCode,doc,"address_state_code");

      AuditUtil.getChangeXML(addressZipCode,original.addressZipCode,doc,"address_zip_code");

      AuditUtil.getChangeXML(stateFipsCode,original.stateFipsCode,doc,"state_fips_code");

      AuditUtil.getChangeXML(phoneNumber,original.phoneNumber,doc,"phone_number");

      if(root.hasChildNodes())
        return XMLUtil.toString(doc);
    }catch(Exception e){
      e.printStackTrace();
    }
    return null;
  }
   
  public String getTableName() {
    return "pws_address";
  }
  
}   
