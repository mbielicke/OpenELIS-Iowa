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
  * OrganizationContactReporting Entity POJO for database 
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
@Table(name="organization_contact_reporting")
@EntityListeners({AuditUtil.class})
public class OrganizationContactReporting implements Auditable, Cloneable {
  
  @Id
  @GeneratedValue
  @Column(name="id")
  private Integer id;             

  @Column(name="organization_contact")
  private Integer organizationContact;             

  @Column(name="format")
  private Integer format;             

  @Column(name="route")
  private String route;             


  @Transient
  private OrganizationContactReporting original;

  
  public Integer getId() {
    return id;
  }
  protected void setId(Integer id) {
    this.id = id;
  }

  public Integer getOrganizationContact() {
    return organizationContact;
  }
  public void setOrganizationContact(Integer organizationContact) {
    this.organizationContact = organizationContact;
  }

  public Integer getFormat() {
    return format;
  }
  public void setFormat(Integer format) {
    this.format = format;
  }

  public String getRoute() {
    return route;
  }
  public void setRoute(String route) {
    this.route = route;
  }

  
  public void setClone() {
    try {
      original = (OrganizationContactReporting)this.clone();
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

      if((organizationContact == null && original.organizationContact != null) || 
         (organizationContact != null && !organizationContact.equals(original.organizationContact))){
        Element elem = doc.createElement("organization_contact");
        elem.appendChild(doc.createTextNode(original.organizationContact.toString()));
        root.appendChild(elem);
      }      

      if((format == null && original.format != null) || 
         (format != null && !format.equals(original.format))){
        Element elem = doc.createElement("format");
        elem.appendChild(doc.createTextNode(original.format.toString()));
        root.appendChild(elem);
      }      

      if((route == null && original.route != null) || 
         (route != null && !route.equals(original.route))){
        Element elem = doc.createElement("route");
        elem.appendChild(doc.createTextNode(original.route.toString()));
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
    return "organization_contact_reporting";
  }
  
}   
