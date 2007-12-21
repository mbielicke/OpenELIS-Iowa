
package org.openelis.entity;

/**
  * Provider Entity POJO for database 
  */

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.openelis.util.XMLUtil;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;
import org.openelis.utils.AuditUtil;
import org.openelis.utils.Auditable;
@NamedQueries({@NamedQuery(name = "getProviderNameRowsByLetter", query = "select new org.openelis.domain.ProviderTableRowDO(p.id,p.lastName,p.firstName) " + "from Provider p where p.lastName like :letter order by lastName,firstName"),
               @NamedQuery(name = "getProvider", query = "select new org.openelis.domain.ProviderDO(p.id,p.lastName,p.firstName,p.middleName,d.id,p.npi)" +                                                                                                  
                               "  from Provider p, Dictionary d where d.id = p.type and p.id = :id"),
               @NamedQuery(name = "getProviderAddresses", query = "select new org.openelis.domain.ProviderAddressDO(pa.id, pa.location, pa.externalId, pa.provider, " +
                                           " a.id, a.multipleUnit,a.streetAddress, a.city, a.state, a.zipCode, a.workPhone, a.homePhone, "+
                                           " a.cellPhone, a.faxPhone, a.email, a.country)"+" from Provider p, ProviderAddress pa, Address a "+
                                           " where pa.provider = p.id and pa.address = a.id and p.id = :id order by pa.location"),
               @NamedQuery(name = "getProviderTypes", query = "select distinct d.id, d.entry from Provider p, Dictionary d, Category c where p.type = d.id and d.category = c.id")})          
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

  @Column(name="type")
  private Integer type;             

  @Column(name="npi")
  private String npi;             


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

  public Integer getType() {
    return type;
  }
  public void setType(Integer type) {
    if((type == null && this.type != null) || 
       (type != null && !type.equals(this.type)))
      this.type = type;
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
      
      if((id == null && original.id != null) || 
         (id != null && !id.equals(original.id))){
        Element elem = doc.createElement("id");
        elem.appendChild(doc.createTextNode(original.id.toString()));
        root.appendChild(elem);
      }      

      if((lastName == null && original.lastName != null) || 
         (lastName != null && !lastName.equals(original.lastName))){
        Element elem = doc.createElement("last_name");
        elem.appendChild(doc.createTextNode(original.lastName.toString()));
        root.appendChild(elem);
      }      

      if((firstName == null && original.firstName != null) || 
         (firstName != null && !firstName.equals(original.firstName))){
        Element elem = doc.createElement("first_name");
        elem.appendChild(doc.createTextNode(original.firstName.toString()));
        root.appendChild(elem);
      }      

      if((middleName == null && original.middleName != null) || 
         (middleName != null && !middleName.equals(original.middleName))){
        Element elem = doc.createElement("middle_name");
        elem.appendChild(doc.createTextNode(original.middleName.toString()));
        root.appendChild(elem);
      }      

      if((type == null && original.type != null) || 
         (type != null && !type.equals(original.type))){
        Element elem = doc.createElement("type");
        elem.appendChild(doc.createTextNode(original.type.toString()));
        root.appendChild(elem);
      }      

      if((npi == null && original.npi != null) || 
         (npi != null && !npi.equals(original.npi))){
        Element elem = doc.createElement("npi");
        elem.appendChild(doc.createTextNode(original.npi.toString()));
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
    return "provider";
  }
  
}   
