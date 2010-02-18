
package org.openelis.entity;

/**
  * PwsFacility Entity POJO for database 
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
@Table(name="pws_facility")
@EntityListeners({AuditUtil.class})
public class PwsFacility implements Auditable, Cloneable {
  
  @Id
  @GeneratedValue
  @Column(name="id")
  private Integer id;             

  @Column(name="tinwsys_is_number")
  private Integer tinwsysIsNumber;             

  @Column(name="name")
  private String name;             

  @Column(name="type_code")
  private String typeCode;             

  @Column(name="st_asgn_ident_cd")
  private String stAsgnIdentCd;             

  @Column(name="activity_status_cd")
  private String activityStatusCd;             

  @Column(name="water_type_code")
  private String waterTypeCode;             

  @Column(name="availability_code")
  private String availabilityCode;             

  @Column(name="identification_cd")
  private String identificationCd;             

  @Column(name="description_text")
  private String descriptionText;             

  @Column(name="source_type_code")
  private String sourceTypeCode;             


  @Transient
  private PwsFacility original;

  
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

  public String getName() {
    return name;
  }
  public void setName(String name) {
    if((name == null && this.name != null) || 
       (name != null && !name.equals(this.name)))
      this.name = name;
  }

  public String getTypeCode() {
    return typeCode;
  }
  public void setTypeCode(String typeCode) {
    if((typeCode == null && this.typeCode != null) || 
       (typeCode != null && !typeCode.equals(this.typeCode)))
      this.typeCode = typeCode;
  }

  public String getStAsgnIdentCd() {
    return stAsgnIdentCd;
  }
  public void setStAsgnIdentCd(String stAsgnIdentCd) {
    if((stAsgnIdentCd == null && this.stAsgnIdentCd != null) || 
       (stAsgnIdentCd != null && !stAsgnIdentCd.equals(this.stAsgnIdentCd)))
      this.stAsgnIdentCd = stAsgnIdentCd;
  }

  public String getActivityStatusCd() {
    return activityStatusCd;
  }
  public void setActivityStatusCd(String activityStatusCd) {
    if((activityStatusCd == null && this.activityStatusCd != null) || 
       (activityStatusCd != null && !activityStatusCd.equals(this.activityStatusCd)))
      this.activityStatusCd = activityStatusCd;
  }

  public String getWaterTypeCode() {
    return waterTypeCode;
  }
  public void setWaterTypeCode(String waterTypeCode) {
    if((waterTypeCode == null && this.waterTypeCode != null) || 
       (waterTypeCode != null && !waterTypeCode.equals(this.waterTypeCode)))
      this.waterTypeCode = waterTypeCode;
  }

  public String getAvailabilityCode() {
    return availabilityCode;
  }
  public void setAvailabilityCode(String availabilityCode) {
    if((availabilityCode == null && this.availabilityCode != null) || 
       (availabilityCode != null && !availabilityCode.equals(this.availabilityCode)))
      this.availabilityCode = availabilityCode;
  }

  public String getIdentificationCd() {
    return identificationCd;
  }
  public void setIdentificationCd(String identificationCd) {
    if((identificationCd == null && this.identificationCd != null) || 
       (identificationCd != null && !identificationCd.equals(this.identificationCd)))
      this.identificationCd = identificationCd;
  }

  public String getDescriptionText() {
    return descriptionText;
  }
  public void setDescriptionText(String descriptionText) {
    if((descriptionText == null && this.descriptionText != null) || 
       (descriptionText != null && !descriptionText.equals(this.descriptionText)))
      this.descriptionText = descriptionText;
  }

  public String getSourceTypeCode() {
    return sourceTypeCode;
  }
  public void setSourceTypeCode(String sourceTypeCode) {
    if((sourceTypeCode == null && this.sourceTypeCode != null) || 
       (sourceTypeCode != null && !sourceTypeCode.equals(this.sourceTypeCode)))
      this.sourceTypeCode = sourceTypeCode;
  }

  
  public void setClone() {
    try {
      original = (PwsFacility)this.clone();
    }catch(Exception e){}
  }
  
  public String getChangeXML() {
    try {
      Document doc = XMLUtil.createNew("change");
      Element root = doc.getDocumentElement();
      
      AuditUtil.getChangeXML(id,original.id,doc,"id");

      AuditUtil.getChangeXML(tinwsysIsNumber,original.tinwsysIsNumber,doc,"tinwsys_is_number");

      AuditUtil.getChangeXML(name,original.name,doc,"name");

      AuditUtil.getChangeXML(typeCode,original.typeCode,doc,"type_code");

      AuditUtil.getChangeXML(stAsgnIdentCd,original.stAsgnIdentCd,doc,"st_asgn_ident_cd");

      AuditUtil.getChangeXML(activityStatusCd,original.activityStatusCd,doc,"activity_status_cd");

      AuditUtil.getChangeXML(waterTypeCode,original.waterTypeCode,doc,"water_type_code");

      AuditUtil.getChangeXML(availabilityCode,original.availabilityCode,doc,"availability_code");

      AuditUtil.getChangeXML(identificationCd,original.identificationCd,doc,"identification_cd");

      AuditUtil.getChangeXML(descriptionText,original.descriptionText,doc,"description_text");

      AuditUtil.getChangeXML(sourceTypeCode,original.sourceTypeCode,doc,"source_type_code");

      if(root.hasChildNodes())
        return XMLUtil.toString(doc);
    }catch(Exception e){
      e.printStackTrace();
    }
    return null;
  }
   
  public String getTableName() {
    return "pws_facility";
  }
  
}   
