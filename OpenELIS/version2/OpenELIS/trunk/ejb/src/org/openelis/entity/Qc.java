
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

  @Column(name="type")
  private Integer type;             

  @Column(name="source")
  private String source;             

  @Column(name="lot_number")
  private String lotNumber;             

  @Column(name="prepared_date")
  private Date preparedDate;             

  @Column(name="prepared_volume")
  private Double preparedVolume;             

  @Column(name="prepared_unit")
  private Integer preparedUnit;             

  @Column(name="prepared_by")
  private Integer preparedBy;             

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

  public Integer getType() {
    return type;
  }
  public void setType(Integer type) {
    if((type == null && this.type != null) || 
       (type != null && !type.equals(this.type)))
      this.type = type;
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

  public Integer getPreparedUnit() {
    return preparedUnit;
  }
  public void setPreparedUnit(Integer preparedUnit) {
    if((preparedUnit == null && this.preparedUnit != null) || 
       (preparedUnit != null && !preparedUnit.equals(this.preparedUnit)))
      this.preparedUnit = preparedUnit;
  }

  public Integer getPreparedBy() {
    return preparedBy;
  }
  public void setPreparedBy(Integer preparedBy) {
    if((preparedBy == null && this.preparedBy != null) || 
       (preparedBy != null && !preparedBy.equals(this.preparedBy)))
      this.preparedBy = preparedBy;
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

      if((type == null && original.type != null) || 
         (type != null && !type.equals(original.type))){
        Element elem = doc.createElement("type");
        elem.appendChild(doc.createTextNode(original.type.toString().trim()));
        root.appendChild(elem);
      }      

      if((source == null && original.source != null) || 
         (source != null && !source.equals(original.source))){
        Element elem = doc.createElement("source");
        elem.appendChild(doc.createTextNode(original.source.toString().trim()));
        root.appendChild(elem);
      }      

      if((lotNumber == null && original.lotNumber != null) || 
         (lotNumber != null && !lotNumber.equals(original.lotNumber))){
        Element elem = doc.createElement("lot_number");
        elem.appendChild(doc.createTextNode(original.lotNumber.toString().trim()));
        root.appendChild(elem);
      }      

      if((preparedDate == null && original.preparedDate != null) || 
         (preparedDate != null && !preparedDate.equals(original.preparedDate))){
        Element elem = doc.createElement("prepared_date");
        elem.appendChild(doc.createTextNode(original.preparedDate.toString().trim()));
        root.appendChild(elem);
      }      

      if((preparedVolume == null && original.preparedVolume != null) || 
         (preparedVolume != null && !preparedVolume.equals(original.preparedVolume))){
        Element elem = doc.createElement("prepared_volume");
        elem.appendChild(doc.createTextNode(original.preparedVolume.toString().trim()));
        root.appendChild(elem);
      }      

      if((preparedUnit == null && original.preparedUnit != null) || 
         (preparedUnit != null && !preparedUnit.equals(original.preparedUnit))){
        Element elem = doc.createElement("prepared_unit");
        elem.appendChild(doc.createTextNode(original.preparedUnit.toString().trim()));
        root.appendChild(elem);
      }      

      if((preparedBy == null && original.preparedBy != null) || 
         (preparedBy != null && !preparedBy.equals(original.preparedBy))){
        Element elem = doc.createElement("prepared_by");
        elem.appendChild(doc.createTextNode(original.preparedBy.toString().trim()));
        root.appendChild(elem);
      }      

      if((usableDate == null && original.usableDate != null) || 
         (usableDate != null && !usableDate.equals(original.usableDate))){
        Element elem = doc.createElement("usable_date");
        elem.appendChild(doc.createTextNode(original.usableDate.toString().trim()));
        root.appendChild(elem);
      }      

      if((expireDate == null && original.expireDate != null) || 
         (expireDate != null && !expireDate.equals(original.expireDate))){
        Element elem = doc.createElement("expire_date");
        elem.appendChild(doc.createTextNode(original.expireDate.toString().trim()));
        root.appendChild(elem);
      }      

      if((isSingleUse == null && original.isSingleUse != null) || 
         (isSingleUse != null && !isSingleUse.equals(original.isSingleUse))){
        Element elem = doc.createElement("is_single_use");
        elem.appendChild(doc.createTextNode(original.isSingleUse.toString().trim()));
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
    return "qc";
  }
  
}   
