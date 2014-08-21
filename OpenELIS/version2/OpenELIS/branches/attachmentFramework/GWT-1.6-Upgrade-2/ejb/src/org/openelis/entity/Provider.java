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
  * Provider Entity POJO for database 
  */

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.openelis.entity.Note;
import org.openelis.entity.ProviderAddress;
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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import org.openelis.utils.AuditUtil;
import org.openelis.utils.Auditable;

@NamedQueries({@NamedQuery(name = "Provider.Provider", query = "select new org.openelis.domain.ProviderDO(p.id,p.lastName,p.firstName,p.middleName,d.id,p.npi)" +                                                                                                  
"  from Provider p, Dictionary d where d.id = p.typeId and p.id = :id"),
@NamedQuery(name = "Provider.Addresses", query = "select new org.openelis.domain.ProviderAddressDO(pa.id, pa.location, pa.externalId, pa.providerId, " +
            " a.id, a.multipleUnit,a.streetAddress, a.city, a.state, a.zipCode, a.workPhone, a.homePhone, "+
            " a.cellPhone, a.faxPhone, a.email, a.country)"+" from ProviderAddress pa left join pa.address a "+
            " where pa.providerId = :id order by pa.location"),
@NamedQuery(name = "Provider.Notes", query = "select new org.openelis.domain.NoteDO(n.id, n.systemUserId, n.text, n.timestamp, n.subject) " + 
"  from Note n where n.referenceTableId = (select id from ReferenceTable where name='provider') and n.referenceId = :id ORDER BY n.timestamp DESC")})               

@Entity
@Table(name="provider")
@EntityListeners({AuditUtil.class})
public class Provider implements Auditable, Cloneable {
  
  @Id
  @GeneratedValue
  @Column(name="id")
  private Integer id;             

  @Column(name="last_name")
  private String lastName;             

  @Column(name="first_name")
  private String firstName;             

  @Column(name="middle_name")
  private String middleName;             

  @Column(name="type_id")
  private Integer typeId;             

  @Column(name="npi")
  private String npi;             

  @OneToMany(fetch = FetchType.LAZY)
  @JoinColumn(name = "provider_id",insertable = false, updatable = false)
  private Collection<ProviderAddress> providerAddress;
  
  @OneToMany(fetch = FetchType.LAZY)
  @JoinColumn(name = "reference_id", insertable = false, updatable = false)
  private Collection<Note> provNote;

  @Transient
  private Provider original;

  
  public Integer getId() {
    return id;
  }
  protected void setId(Integer id) {
    if((id == null && this.id != null) || 
       (id != null && !id.equals(this.id)))
      this.id = id;
  }

  public String getLastName() {
    return lastName;
  }
  public void setLastName(String lastName) {
    if((lastName == null && this.lastName != null) || 
       (lastName != null && !lastName.equals(this.lastName)))
      this.lastName = lastName;
  }

  public String getFirstName() {
    return firstName;
  }
  public void setFirstName(String firstName) {
    if((firstName == null && this.firstName != null) || 
       (firstName != null && !firstName.equals(this.firstName)))
      this.firstName = firstName;
  }

  public String getMiddleName() {
    return middleName;
  }
  public void setMiddleName(String middleName) {
    if((middleName == null && this.middleName != null) || 
       (middleName != null && !middleName.equals(this.middleName)))
      this.middleName = middleName;
  }

  public Integer getTypeId() {
    return typeId;
  }
  public void setTypeId(Integer typeId) {
    if((typeId == null && this.typeId != null) || 
       (typeId != null && !typeId.equals(this.typeId)))
      this.typeId = typeId;
  }

  public String getNpi() {
    return npi;
  }
  public void setNpi(String npi) {
    if((npi == null && this.npi != null) || 
       (npi != null && !npi.equals(this.npi)))
      this.npi = npi;
  }

  
  public void setClone() {
    try {
      original = (Provider)this.clone();
    }catch(Exception e){}
  }
  
  public String getChangeXML() {
    try {
      Document doc = XMLUtil.createNew("change");
      Element root = doc.getDocumentElement();
      
      AuditUtil.getChangeXML(id,original.id,doc,"id");

      AuditUtil.getChangeXML(lastName,original.lastName,doc,"last_name");

      AuditUtil.getChangeXML(firstName,original.firstName,doc,"first_name");

      AuditUtil.getChangeXML(middleName,original.middleName,doc,"middle_name");

      AuditUtil.getChangeXML(typeId,original.typeId,doc,"type_id");

      AuditUtil.getChangeXML(npi,original.npi,doc,"npi");

      if(root.hasChildNodes())
        return XMLUtil.toString(doc);
    }catch(Exception e){
      e.printStackTrace();
    }
    return null;
  }
   
  public String getTableName() {
    return "provider";
  }
  
  public Collection<ProviderAddress> getProviderAddress() {
      return providerAddress;
   }
   
   public void setProviderAddress(Collection<ProviderAddress> providerAddress) {
     this.providerAddress = providerAddress;
   }
    public Collection<Note> getProvNote() {
     return provNote;
   }
    public void setProvNote(Collection<Note> provNote) {
     this.provNote = provNote;
   }
  
}   
