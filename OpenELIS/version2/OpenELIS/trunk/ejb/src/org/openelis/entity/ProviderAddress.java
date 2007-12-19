
package org.openelis.entity;

/**
  * ProviderAddress Entity POJO for database 
  */

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.openelis.util.XMLUtil;

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
@Table(name="provider_address")
@EntityListeners({AuditUtil.class})
public class ProviderAddress implements Auditable, Cloneable {
  
  @Id
  @GeneratedValue
  @Column(name="id")
  private Integer id;             

  @Column(name="location")
  private String location;             

  @Column(name="external_id")
  private String externalId;             

  @Column(name="provider")
  private Integer provider;             

  @Column(name="address")
  private Integer address;             


  @Transient
  private ProviderAddress original;

  
  public Integer getId() {
    return id;
  }
  protected void setId(Integer id) {
    if((id == null && this.id != null) || 
       (id != null && !id.equals(this.id)))
      this.id = id;
  }

  public String getLocation() {
    return location;
  }
  public void setLocation(String location) {
    if((location == null && this.location != null) || 
       (location != null && !location.equals(this.location)))
      this.location = location;
  }

  public String getExternalId() {
    return externalId;
  }
  public void setExternalId(String externalId) {
    if((externalId == null && this.externalId != null) || 
       (externalId != null && !externalId.equals(this.externalId)))
      this.externalId = externalId;
  }

  public Integer getProvider() {
    return provider;
  }
  public void setProvider(Integer provider) {
    if((provider == null && this.provider != null) || 
       (provider != null && !provider.equals(this.provider)))
      this.provider = provider;
  }

  public Integer getAddress() {
    return address;
  }
  public void setAddress(Integer address) {
    if((address == null && this.address != null) || 
       (address != null && !address.equals(this.address)))
      this.address = address;
  }

  
  public void setClone() {
    try {
      original = (ProviderAddress)this.clone();
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

      if((location == null && original.location != null) || 
         (location != null && !location.equals(original.location))){
        Element elem = doc.createElement("location");
        elem.appendChild(doc.createTextNode(original.location.toString()));
        root.appendChild(elem);
      }      

      if((externalId == null && original.externalId != null) || 
         (externalId != null && !externalId.equals(original.externalId))){
        Element elem = doc.createElement("external_id");
        elem.appendChild(doc.createTextNode(original.externalId.toString()));
        root.appendChild(elem);
      }      

      if((provider == null && original.provider != null) || 
         (provider != null && !provider.equals(original.provider))){
        Element elem = doc.createElement("provider");
        elem.appendChild(doc.createTextNode(original.provider.toString()));
        root.appendChild(elem);
      }      

      if((address == null && original.address != null) || 
         (address != null && !address.equals(original.address))){
        Element elem = doc.createElement("address");
        elem.appendChild(doc.createTextNode(original.address.toString()));
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
    return "provider_address";
  }
  
}   
