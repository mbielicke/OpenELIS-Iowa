
package org.openelis.entity;

/**
  * Organization Entity POJO for database 
  */


import java.util.Collection;

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

import org.openelis.util.XMLUtil;
import org.openelis.utils.AuditUtil;
import org.openelis.utils.Auditable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;



@NamedQueries({@NamedQuery(name = "getOrganizationNameRowsByLetter", query = "select	new org.openelis.domain.OrganizationTableRowDO(o.id,o.name) " + "from Organization o where o.name like :letter order by name"),
	           @NamedQuery(name = "getOrganizationAndAddress", query = "select new org.openelis.domain.OrganizationAddressDO(orgz.id,orgz.parentOrganization,orgz.name,orgz.isActive,addr.id," +
			   		              "addr.multipleUnit,addr.streetAddress,addr.city,addr.state,addr.zipCode,addr.country)" +                                                                              
			                      "  from Organization orgz, Address addr where addr.id = orgz.address and orgz.id = :id"),
			   @NamedQuery(name = "getOrganizationContacts", query = "select new org.openelis.domain.OrganizationContactDO(contact.id,contact.organization,contact.contactType,contact.name,addr.id," +
	           		              "addr.multipleUnit,addr.streetAddress,addr.city,addr.state,addr.zipCode,addr.workPhone,addr.homePhone,addr.cellPhone,addr.faxPhone,addr.email,addr.country)" +
						           "  from OrganizationContact contact, Organization orgz, Address addr where addr.id = contact.address and " +
						           " orgz.id = contact.organization and orgz.id = :id"),
			   @NamedQuery(name = "getOrganizationNotesTopLevel", query = "select n.id, n.systemUser, n.text, n.timestamp, n.subject " + 
					   "  from Note n where n.referenceTable = (select id from ReferenceTable where name='organization') and n.referenceId = :id"),
			   @NamedQuery(name = "getOrganizationAutoCompleteById", query = "select o.id, o.name, o.orgAddress.streetAddress, o.orgAddress.city, o.orgAddress.state " +
					   "  from Organization o where o.id = :id"),
			   @NamedQuery(name = "getOrganizationAutoCompleteByName", query = "select o.id, o.name, o.orgAddress.streetAddress, o.orgAddress.city, o.orgAddress.state " +
					   "  from Organization o where o.name like :name")})
                                                                  
@Entity
@Table(name="organization")
@EntityListeners({AuditUtil.class})
public class Organization implements Auditable, Cloneable {
  
  @Id
  @GeneratedValue
  @Column(name="id")
  private Integer id;             

  @Column(name="parent_organization")
  private Integer parentOrganization;             

  @Column(name="name")
  private String name;             

  @Column(name="is_active")
  private String isActive;             

  @Column(name="address")
  private Integer address;             

  //address table is mapped in the organizationContact entity
  @OneToMany(fetch = FetchType.LAZY)
  @JoinColumn(name = "organization")
  private Collection<OrganizationContact> organizationContact;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "address", insertable = false, updatable = false)
  private Address orgAddress;
  
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "parent_organization", insertable = false, updatable = false)
  private Organization parentOrg;
  
  @OneToMany(fetch = FetchType.LAZY)
  @JoinColumn(name = "reference_id", insertable = false, updatable = false)
  private Collection<Note> orgNote;
  
  public Collection<OrganizationContact> getOrganizationContact() {
	return organizationContact;
  }

  public void setOrganizationContact(Collection<OrganizationContact> organizationContact) {
	this.organizationContact = organizationContact;
  }
  
//  @OneToOne(fetch = FetchType.LAZY)
//  @JoinColumn(name = "reference_table", insertable = false, updatable = false)
//  private OrganizationContact referenceTable;

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

  public Integer getParentOrganization() {
    return parentOrganization;
  }
  public void setParentOrganization(Integer parentOrganization) {
    if((parentOrganization == null && this.parentOrganization != null) || 
       (parentOrganization != null && !parentOrganization.equals(this.parentOrganization)))
      this.parentOrganization = parentOrganization;
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
      original = (Organization)this.clone();
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

      if((parentOrganization == null && original.parentOrganization != null) || 
         (parentOrganization != null && !parentOrganization.equals(original.parentOrganization))){
        Element elem = doc.createElement("parent_organization");
        elem.appendChild(doc.createTextNode(original.parentOrganization.toString()));
        root.appendChild(elem);
      }      

      if((name == null && original.name != null) || 
         (name != null && !name.equals(original.name.trim()))){
        Element elem = doc.createElement("name");
        elem.appendChild(doc.createTextNode(original.name.toString().trim()));
        root.appendChild(elem);
      }      

      if((isActive == null && original.isActive != null) || 
         (isActive != null && !isActive.equals(original.isActive))){
        Element elem = doc.createElement("is_active");
        elem.appendChild(doc.createTextNode(original.isActive.toString()));
        root.appendChild(elem);
      }
      
      if((address == null && original.address != null) || 
    		  (address != null && !address.equals(original.address))){
    	  Element elem = doc.createElement("address");
          elem.appendChild(doc.createTextNode(original.address.toString()));
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
    return "organization";
  }

  public Collection<Note> getNote() {
	return orgNote;
	}

  public void setNote(Collection<Note> note) {
	this.orgNote = note;
  }

  public Organization getParentOrg() {
	return parentOrg;
  }

  public void setParentOrg(Organization parentOrg) {
	this.parentOrg = parentOrg;
  }

public Address getOrgAddress() {
	return orgAddress;
}

public void setOrgAddress(Address orgAddress) {
	this.orgAddress = orgAddress;
}

public Collection<Note> getOrgNote() {
	return orgNote;
}

public void setOrgNote(Collection<Note> orgNote) {
	this.orgNote = orgNote;
}

}   
