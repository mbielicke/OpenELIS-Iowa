
package org.openelis.entity;

/**
  * Instrument Entity POJO for database 
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
@Table(name="instrument")
@EntityListeners({AuditUtil.class})
public class Instrument implements Auditable, Cloneable {
  
  @Id
  @GeneratedValue
  @Column(name="id")
  private Integer id;             

  @Column(name="name")
  private String name;             

  @Column(name="description")
  private String description;             

  @Column(name="model_number")
  private String modelNumber;             

  @Column(name="serial_number")
  private String serialNumber;             

  @Column(name="type")
  private Integer type;             

  @Column(name="location")
  private String location;             

  @Column(name="is_active")
  private String isActive;             

  @Column(name="active_begin")
  private Date activeBegin;             

  @Column(name="active_end")
  private Date activeEnd;             

  @Column(name="scriptlet")
  private Integer scriptlet;             


  @Transient
  private Instrument original;

  
  public Integer getId() {
    return id;
  }
  protected void setId(Integer id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }
  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }
  public void setDescription(String description) {
    this.description = description;
  }

  public String getModelNumber() {
    return modelNumber;
  }
  public void setModelNumber(String modelNumber) {
    this.modelNumber = modelNumber;
  }

  public String getSerialNumber() {
    return serialNumber;
  }
  public void setSerialNumber(String serialNumber) {
    this.serialNumber = serialNumber;
  }

  public Integer getType() {
    return type;
  }
  public void setType(Integer type) {
    this.type = type;
  }

  public String getLocation() {
    return location;
  }
  public void setLocation(String location) {
    this.location = location;
  }

  public String getIsActive() {
    return isActive;
  }
  public void setIsActive(String isActive) {
    this.isActive = isActive;
  }

  public Datetime getActiveBegin() {
    if(activeBegin == null)
      return null;
    return new Datetime(Datetime.YEAR ,Datetime. DAY,activeBegin);
  }
  public void setActiveBegin (Datetime active_begin){
    this.activeBegin = active_begin.getDate();
  }

  public Datetime getActiveEnd() {
    if(activeEnd == null)
      return null;
    return new Datetime(Datetime.YEAR ,Datetime. DAY,activeEnd);
  }
  public void setActiveEnd (Datetime active_end){
    this.activeEnd = active_end.getDate();
  }

  public Integer getScriptlet() {
    return scriptlet;
  }
  public void setScriptlet(Integer scriptlet) {
    this.scriptlet = scriptlet;
  }

  
  public void setClone() {
    try {
      original = (Instrument)this.clone();
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

      if((name == null && original.name != null) || 
         (name != null && !name.equals(original.name))){
        Element elem = doc.createElement("name");
        elem.appendChild(doc.createTextNode(original.name.toString()));
        root.appendChild(elem);
      }      

      if((description == null && original.description != null) || 
         (description != null && !description.equals(original.description))){
        Element elem = doc.createElement("description");
        elem.appendChild(doc.createTextNode(original.description.toString()));
        root.appendChild(elem);
      }      

      if((modelNumber == null && original.modelNumber != null) || 
         (modelNumber != null && !modelNumber.equals(original.modelNumber))){
        Element elem = doc.createElement("model_number");
        elem.appendChild(doc.createTextNode(original.modelNumber.toString()));
        root.appendChild(elem);
      }      

      if((serialNumber == null && original.serialNumber != null) || 
         (serialNumber != null && !serialNumber.equals(original.serialNumber))){
        Element elem = doc.createElement("serial_number");
        elem.appendChild(doc.createTextNode(original.serialNumber.toString()));
        root.appendChild(elem);
      }      

      if((type == null && original.type != null) || 
         (type != null && !type.equals(original.type))){
        Element elem = doc.createElement("type");
        elem.appendChild(doc.createTextNode(original.type.toString()));
        root.appendChild(elem);
      }      

      if((location == null && original.location != null) || 
         (location != null && !location.equals(original.location))){
        Element elem = doc.createElement("location");
        elem.appendChild(doc.createTextNode(original.location.toString()));
        root.appendChild(elem);
      }      

      if((isActive == null && original.isActive != null) || 
         (isActive != null && !isActive.equals(original.isActive))){
        Element elem = doc.createElement("is_active");
        elem.appendChild(doc.createTextNode(original.isActive.toString()));
        root.appendChild(elem);
      }      

      if((activeBegin == null && original.activeBegin != null) || 
         (activeBegin != null && !activeBegin.equals(original.activeBegin))){
        Element elem = doc.createElement("active_begin");
        elem.appendChild(doc.createTextNode(original.activeBegin.toString()));
        root.appendChild(elem);
      }      

      if((activeEnd == null && original.activeEnd != null) || 
         (activeEnd != null && !activeEnd.equals(original.activeEnd))){
        Element elem = doc.createElement("active_end");
        elem.appendChild(doc.createTextNode(original.activeEnd.toString()));
        root.appendChild(elem);
      }      

      if((scriptlet == null && original.scriptlet != null) || 
         (scriptlet != null && !scriptlet.equals(original.scriptlet))){
        Element elem = doc.createElement("scriptlet");
        elem.appendChild(doc.createTextNode(original.scriptlet.toString()));
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
    return "instrument";
  }
  
}   
