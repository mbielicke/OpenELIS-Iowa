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
  * Section Entity POJO for database 
  */

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.openelis.util.XMLUtil;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;
import org.openelis.utils.AuditUtil;
import org.openelis.utils.Auditable;

@NamedQuery(name = "Section.IdName", query = "select distinct new org.openelis.domain.IdNameDO(s.id, s.name) " + "  from Section s order by s.name")

@Entity
@Table(name="section")
@EntityListeners({AuditUtil.class})
public class Section implements Auditable, Cloneable {
  
  @Id
  @GeneratedValue
  @Column(name="id")
  private Integer id;             

  @Column(name="parent_section_id")
  private Integer parentSectionId;             

  @Column(name="name")
  private String name;             

  @Column(name="description")
  private String description;             

  @Column(name="is_external")
  private String isExternal;             

  @Column(name="organization_id")
  private Integer organizationId;             


  @Transient
  private Section original;

  
  public Integer getId() {
    return id;
  }
  protected void setId(Integer id) {
    if((id == null && this.id != null) || 
       (id != null && !id.equals(this.id)))
      this.id = id;
  }

  public Integer getParentSectionId() {
    return parentSectionId;
  }
  public void setParentSectionId(Integer parentSectionId) {
    if((parentSectionId == null && this.parentSectionId != null) || 
       (parentSectionId != null && !parentSectionId.equals(this.parentSectionId)))
      this.parentSectionId = parentSectionId;
  }

  public String getName() {
    return name;
  }
  public void setName(String name) {
    if((name == null && this.name != null) || 
       (name != null && !name.equals(this.name)))
      this.name = name;
  }

  public String getDescription() {
    return description;
  }
  public void setDescription(String description) {
    if((description == null && this.description != null) || 
       (description != null && !description.equals(this.description)))
      this.description = description;
  }

  public String getIsExternal() {
    return isExternal;
  }
  public void setIsExternal(String isExternal) {
    if((isExternal == null && this.isExternal != null) || 
       (isExternal != null && !isExternal.equals(this.isExternal)))
      this.isExternal = isExternal;
  }

  public Integer getOrganizationId() {
    return organizationId;
  }
  public void setOrganizationId(Integer organizationId) {
    if((organizationId == null && this.organizationId != null) || 
       (organizationId != null && !organizationId.equals(this.organizationId)))
      this.organizationId = organizationId;
  }

  
  public void setClone() {
    try {
      original = (Section)this.clone();
    }catch(Exception e){}
  }
  
  public String getChangeXML() {
    try {
      Document doc = XMLUtil.createNew("change");
      Element root = doc.getDocumentElement();
      
      AuditUtil.getChangeXML(id,original.id,doc,"id");

      AuditUtil.getChangeXML(parentSectionId,original.parentSectionId,doc,"parent_section_id");

      AuditUtil.getChangeXML(name,original.name,doc,"name");

      AuditUtil.getChangeXML(description,original.description,doc,"description");

      AuditUtil.getChangeXML(isExternal,original.isExternal,doc,"is_external");

      AuditUtil.getChangeXML(organizationId,original.organizationId,doc,"organization_id");

      if(root.hasChildNodes())
        return XMLUtil.toString(doc);
    }catch(Exception e){
      e.printStackTrace();
    }
    return null;
  }
   
  public String getTableName() {
    return "section";
  }
  
}   
