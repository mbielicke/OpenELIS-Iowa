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
  * ProviderAddress Entity POJO for database 
  */

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.openelis.entity.Address;
import org.openelis.util.Datetime;
import org.openelis.util.XMLUtil;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
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

  @Column(name="provider_id")
  private Integer providerId;             

  @Column(name="address_id")
  private Integer addressId;             

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "address_id", insertable = false, updatable = false)
  private Address address;

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

  public Integer getProviderId() {
    return providerId;
  }
  public void setProviderId(Integer providerId) {
    if((providerId == null && this.providerId != null) || 
       (providerId != null && !providerId.equals(this.providerId)))
      this.providerId = providerId;
  }

  public Integer getAddressId() {
    return addressId;
  }
  public void setAddressId(Integer addressId) {
    if((addressId == null && this.addressId != null) || 
       (addressId != null && !addressId.equals(this.addressId)))
      this.addressId = addressId;
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
      
      AuditUtil.getChangeXML(id,original.id,doc,"id");

      AuditUtil.getChangeXML(location,original.location,doc,"location");

      AuditUtil.getChangeXML(externalId,original.externalId,doc,"external_id");

      AuditUtil.getChangeXML(providerId,original.providerId,doc,"provider_id");

      AuditUtil.getChangeXML(addressId,original.addressId,doc,"address_id");

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
  
  public Address getAddress() {
      return address;
  }
  public void setAddress(Address address) {
      this.address = address;
  }
  
}   
