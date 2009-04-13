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
  * Qc Entity POJO for database 
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
@Table(name="qc")
@EntityListeners({AuditUtil.class})
public class Qc implements Auditable, Cloneable {
  
  @Id
  @GeneratedValue
  @Column(name="id")
  private Integer id;             

  @Column(name="name")
  private String name;             

  @Column(name="type_id")
  private Integer typeId;             

  @Column(name="source")
  private String source;             

  @Column(name="lot_number")
  private String lotNumber;             

  @Column(name="prepared_date")
  private Date preparedDate;             

  @Column(name="prepared_volume")
  private Double preparedVolume;             

  @Column(name="prepared_unit_id")
  private Integer preparedUnitId;             

  @Column(name="prepared_by_id")
  private Integer preparedById;             

  @Column(name="usable_date")
  private Date usableDate;             

  @Column(name="expire_date")
  private Date expireDate;             

  @Column(name="is_single_use")
  private String isSingleUse;             


  @Transient
  private Qc original;

  
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

  public Integer getTypeId() {
    return typeId;
  }
  public void setTypeId(Integer typeId) {
    if((typeId == null && this.typeId != null) || 
       (typeId != null && !typeId.equals(this.typeId)))
      this.typeId = typeId;
  }

  public String getSource() {
    return source;
  }
  public void setSource(String source) {
    if((source == null && this.source != null) || 
       (source != null && !source.equals(this.source)))
      this.source = source;
  }

  public String getLotNumber() {
    return lotNumber;
  }
  public void setLotNumber(String lotNumber) {
    if((lotNumber == null && this.lotNumber != null) || 
       (lotNumber != null && !lotNumber.equals(this.lotNumber)))
      this.lotNumber = lotNumber;
  }

  public Datetime getPreparedDate() {
    if(preparedDate == null)
      return null;
    return new Datetime(Datetime.YEAR,Datetime.SECOND,preparedDate);
  }
  public void setPreparedDate (Datetime prepared_date){
    if((preparedDate == null && this.preparedDate != null) || 
       (preparedDate != null && !preparedDate.equals(this.preparedDate)))
      this.preparedDate = prepared_date.getDate();
  }

  public Double getPreparedVolume() {
    return preparedVolume;
  }
  public void setPreparedVolume(Double preparedVolume) {
    if((preparedVolume == null && this.preparedVolume != null) || 
       (preparedVolume != null && !preparedVolume.equals(this.preparedVolume)))
      this.preparedVolume = preparedVolume;
  }

  public Integer getPreparedUnitId() {
    return preparedUnitId;
  }
  public void setPreparedUnitId(Integer preparedUnitId) {
    if((preparedUnitId == null && this.preparedUnitId != null) || 
       (preparedUnitId != null && !preparedUnitId.equals(this.preparedUnitId)))
      this.preparedUnitId = preparedUnitId;
  }

  public Integer getPreparedById() {
    return preparedById;
  }
  public void setPreparedById(Integer preparedById) {
    if((preparedById == null && this.preparedById != null) || 
       (preparedById != null && !preparedById.equals(this.preparedById)))
      this.preparedById = preparedById;
  }

  public Datetime getUsableDate() {
    if(usableDate == null)
      return null;
    return new Datetime(Datetime.YEAR,Datetime.SECOND,usableDate);
  }
  public void setUsableDate (Datetime usable_date){
    if((usableDate == null && this.usableDate != null) || 
       (usableDate != null && !usableDate.equals(this.usableDate)))
      this.usableDate = usable_date.getDate();
  }

  public Datetime getExpireDate() {
    if(expireDate == null)
      return null;
    return new Datetime(Datetime.YEAR,Datetime.SECOND,expireDate);
  }
  public void setExpireDate (Datetime expire_date){
    if((expireDate == null && this.expireDate != null) || 
       (expireDate != null && !expireDate.equals(this.expireDate)))
      this.expireDate = expire_date.getDate();
  }

  public String getIsSingleUse() {
    return isSingleUse;
  }
  public void setIsSingleUse(String isSingleUse) {
    if((isSingleUse == null && this.isSingleUse != null) || 
       (isSingleUse != null && !isSingleUse.equals(this.isSingleUse)))
      this.isSingleUse = isSingleUse;
  }

  
  public void setClone() {
    try {
      original = (Qc)this.clone();
    }catch(Exception e){}
  }
  
  public String getChangeXML() {
    try {
      Document doc = XMLUtil.createNew("change");
      Element root = doc.getDocumentElement();
      
      AuditUtil.getChangeXML(id,original.id,doc,"id");

      AuditUtil.getChangeXML(name,original.name,doc,"name");

      AuditUtil.getChangeXML(typeId,original.typeId,doc,"type_id");

      AuditUtil.getChangeXML(source,original.source,doc,"source");

      AuditUtil.getChangeXML(lotNumber,original.lotNumber,doc,"lot_number");

      AuditUtil.getChangeXML(preparedDate,original.preparedDate,doc,"prepared_date");

      AuditUtil.getChangeXML(preparedVolume,original.preparedVolume,doc,"prepared_volume");

      AuditUtil.getChangeXML(preparedUnitId,original.preparedUnitId,doc,"prepared_unit_id");

      AuditUtil.getChangeXML(preparedById,original.preparedById,doc,"prepared_by_id");

      AuditUtil.getChangeXML(usableDate,original.usableDate,doc,"usable_date");

      AuditUtil.getChangeXML(expireDate,original.expireDate,doc,"expire_date");

      AuditUtil.getChangeXML(isSingleUse,original.isSingleUse,doc,"is_single_use");

      if(root.hasChildNodes())
        return XMLUtil.toString(doc);
    }catch(Exception e){
      e.printStackTrace();
    }
    return null;
  }
   
  public String getTableName() {
    return "qc";
  }
  
}   
