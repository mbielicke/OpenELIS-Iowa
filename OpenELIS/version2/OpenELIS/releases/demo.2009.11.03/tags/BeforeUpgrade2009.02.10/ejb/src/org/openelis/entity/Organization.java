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
  * Organization Entity POJO for database 
  */

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.openelis.util.Datetime;
import org.openelis.util.XMLUtil;

import java.util.Collection;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import org.openelis.utils.AuditUtil;
import org.openelis.utils.Auditable;

@NamedQueries( {
    @NamedQuery(name = "Organization.OrganizationAndAddress", query = "select new org.openelis.domain.OrganizationAddressDO(orgz.id,orgz.parentOrganizationId,parentOrg.name,orgz.name,orgz.isActive,orgz.address.id,"
            + "orgz.address.multipleUnit,orgz.address.streetAddress,orgz.address.city,orgz.address.state,orgz.address.zipCode,orgz.address.country)"
            + "  from Organization orgz left join orgz.parentOrganization parentOrg where orgz.id = :id"),
    @NamedQuery(name = "Organization.Contacts", query = "select new org.openelis.domain.OrganizationContactDO(contact.id,contact.organizationId,contact.contactTypeId,contact.name,addr.id,"
            + "addr.multipleUnit,addr.streetAddress,addr.city,addr.state,addr.zipCode,addr.workPhone,addr.homePhone,addr.cellPhone,addr.faxPhone,addr.email,addr.country)"
            + "  from OrganizationContact contact, Organization orgz, Address addr where addr.id = contact.addressId and "
            + " orgz.id = contact.organizationId and orgz.id = :id"),
    @NamedQuery(name = "Organization.Notes", query = "select new org.openelis.domain.NoteDO(n.id, n.systemUserId, n.text, n.timestamp, n.subject) "
            + "  from Note n where n.referenceTableId = (select id from ReferenceTable where name='organization') and n.referenceId = :id ORDER BY n.timestamp DESC"),
    @NamedQuery(name = "Organization.AutoCompleteById", query = "select new org.openelis.domain.OrganizationAutoDO(o.id, o.name, o.address.multipleUnit, o.address.streetAddress, o.address.city, o.address.state, " +
              " o.address.zipCode) from Organization o where o.id = :id and o.isActive = 'Y'"),
    @NamedQuery(name = "Organization.AutoCompleteByName", query = "select new org.openelis.domain.OrganizationAutoDO(o.id, o.name, o.address.multipleUnit, o.address.streetAddress, o.address.city, o.address.state, " +
              " o.address.zipCode) from Organization o where o.name like :name and o.isActive='Y' order by o.name") })
              
@Entity
@Table(name="organization")
@EntityListeners({AuditUtil.class})
public class Organization implements Auditable, Cloneable {
  
  @Id
  @GeneratedValue
  @Column(name="id")
  private Integer id;             

  @Column(name="parent_organization_id")
  private Integer parentOrganizationId;             

  @Column(name="name")
  private String name;             

  @Column(name="is_active")
  private String isActive;             

  @Column(name="address_id")
  private Integer addressId;             

  // address table is mapped in the organizationContact entity
  @OneToMany(fetch = FetchType.LAZY)
  @JoinColumn(name = "organization_id")
  private Collection<OrganizationContact> organizationContact;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "address_id", insertable = false, updatable = false)
  private Address                         address;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "parent_organization_id", insertable = false, updatable = false)
  private Organization                    parentOrganization;

  @OneToMany(fetch = FetchType.LAZY)
  @JoinColumn(name = "reference_id", insertable = false, updatable = false)
  private Collection<Note>                note;

  public Collection<OrganizationContact> getOrganizationContact() {
      return organizationContact;
  }

  public void setOrganizationContact(
          Collection<OrganizationContact> organizationContact) {
      this.organizationContact = organizationContact;
  }
  
  @Transient
  private Organization original;

  
  public Integer getId() {
    return id;
  }
  protected void setId(Integer id) {
    if((id == null && this.id != null) || 
       (id != null && !id.equals(this.id)))
      this.id = id;
  }

  public Integer getParentOrganizationId() {
    return parentOrganizationId;
  }
  public void setParentOrganizationId(Integer parentOrganizationId) {
    if((parentOrganizationId == null && this.parentOrganizationId != null) || 
       (parentOrganizationId != null && !parentOrganizationId.equals(this.parentOrganizationId)))
      this.parentOrganizationId = parentOrganizationId;
  }

  public String getName() {
    return name;
  }
  public void setName(String name) {
    if((name == null && this.name != null) || 
       (name != null && !name.equals(this.name)))
      this.name = name;
  }

  public String getIsActive() {
    return isActive;
  }
  public void setIsActive(String isActive) {
    if((isActive == null && this.isActive != null) || 
       (isActive != null && !isActive.equals(this.isActive)))
      this.isActive = isActive;
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
      original = (Organization)this.clone();
    }catch(Exception e){}
  }
  
  public String getChangeXML() {
    try {
      Document doc = XMLUtil.createNew("change");
      Element root = doc.getDocumentElement();
      
      AuditUtil.getChangeXML(id,original.id,doc,"id");

      AuditUtil.getChangeXML(parentOrganizationId,original.parentOrganizationId,doc,"parent_organization_id");

      AuditUtil.getChangeXML(name,original.name,doc,"name");

      AuditUtil.getChangeXML(isActive,original.isActive,doc,"is_active");

      AuditUtil.getChangeXML(addressId,original.addressId,doc,"address_id");

      if(root.hasChildNodes())
        return XMLUtil.toString(doc);
    }catch(Exception e){
      e.printStackTrace();
    }
    return null;
  }
   
  public String getTableName() {
    return "organization";
  }
  
  public Collection<Note> getNote() {
      return note;
  }

  public void setNote(Collection<Note> note) {
      this.note = note;
  }

  public Organization getParentOrganization() {
      return parentOrganization;
  }

  public void setParentOrganization(Organization parentOrg) {
      this.parentOrganization = parentOrg;
  }

  public Address getAddress() {
      return address;
  }

  public void setAddress(Address address) {
      this.address = address;
  }
  
}   
