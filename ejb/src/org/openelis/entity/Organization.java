
package org.openelis.entity;

/**
  * Organization Entity POJO for database 
  */

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.openelis.interfaces.Auditable;
import org.openelis.util.XMLUtil;
import org.openelis.utils.AuditUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

@NamedQueries({@NamedQuery(name = "getOrganizationNameRows", query = "select	new org.openelis.domain.OrganizationTableRowDO(id,name) " + "from Organization order by name"),
	           @NamedQuery(name = "getOrganizationNameRowsByLetter", query = "select	new org.openelis.domain.OrganizationTableRowDO(o.id,o.name) " + "from Organization o where o.name like :letter order by name"),
	           @NamedQuery(name = "getOrganizationAndAddress", query = "select new org.openelis.domain.OrganizationAddressDO(orgz.id,orgz.parentOrganization,orgz.name,orgz.isActive,addr.id," +
	           		              "addr.referenceId,addr.referenceTable," +
			   		              "addr.type,addr.multipleUnit,addr.streetAddress,addr.city,addr.state,addr.zipCode,addr.workPhone,addr.homePhone,addr.cellPhone,addr.faxPhone,addr.email,addr.country)" +                                                                              
			                      "  from Organization orgz, Address addr where addr.id = orgz.address and orgz.id = :id"),
			   @NamedQuery(name = "getOrganizationContacts", query = "select new org.openelis.domain.OrganizationContactDO(contact.id,contact.organization,contact.contactType,contact.name,addr.id," +
	           		              "addr.referenceId,addr.referenceTable," +
			   		              "addr.type,addr.multipleUnit,addr.streetAddress,addr.city,addr.state,addr.zipCode,addr.workPhone,addr.homePhone,addr.cellPhone,addr.faxPhone,addr.email,addr.country)" +
						           "  from OrganizationContact contact, Organization orgz, Address addr where addr.id = contact.address and " +
						           " orgz.id = contact.organization and orgz.id = :id"),
			   @NamedQuery(name = "getOrganizationNotes", query = "select new org.openelis.domain.NoteDO(n.id,n.referenceId,n.referenceTable,n.timestamp,n.isExternal,n.systemUser,n.subject,n.text)" +
									           "  from Note n where n.referenceTable = (select id from ReferenceTable where name='organization') and n.referenceId = :id")} )
                                                                  
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

  @OneToMany(fetch = FetchType.LAZY)
  @JoinColumn(name = "organization")
  private Collection<OrganizationContact> organizationContact;
//  @OneToOne(fetch = FetchType.LAZY)
//@JoinColumn(name = "reference_id", insertable = true, updatable = true)
//  private Address address;
  
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
    this.id = id;
  }

  public Integer getParentOrganization() {
    return parentOrganization;
  }
  public void setParentOrganization(Integer parentOrganization) {
    this.parentOrganization = parentOrganization;
  }

  public String getName() {
    return name;
  }
  public void setName(String name) {
    this.name = name;
  }

  public String getIsActive() {
    return isActive;
  }
  public void setIsActive(String isActive) {
    this.isActive = isActive;
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
         (name != null && !name.equals(original.name))){
        Element elem = doc.createElement("name");
        elem.appendChild(doc.createTextNode(original.name.toString()));
        root.appendChild(elem);
      }      

      if((isActive == null && original.isActive != null) || 
         (isActive != null && !isActive.equals(original.isActive))){
        Element elem = doc.createElement("is_active");
        elem.appendChild(doc.createTextNode(original.isActive.toString()));
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

public Integer getAddress() {
	return address;
}

public void setAddress(Integer address) {
	this.address = address;
}  
}   
