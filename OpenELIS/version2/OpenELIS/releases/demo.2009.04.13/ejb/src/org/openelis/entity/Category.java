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
  * Category Entity POJO for database 
  */

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.openelis.entity.Category;
import org.openelis.entity.Dictionary;
import org.openelis.util.XMLUtil;

import java.util.Collection;
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

@NamedQueries({@NamedQuery(name = "Category.Category", query = "select new org.openelis.domain.CategoryDO(c.id,c.systemName,c.name,c.description,c.sectionId)" +                                                                                                  
"  from Category c where c.id = :id"),
@NamedQuery(name = "Category.IdBySystemName", query = "select c.id from Category c where c.systemName = :systemName"),
@NamedQuery(name = "Category.IdName", query = "select distinct new org.openelis.domain.IdNameDO(c.id, c.name) " + "  from Category c order by c.name")})

@Entity
@Table(name="category")
@EntityListeners({AuditUtil.class})
public class Category implements Auditable, Cloneable {
  
  @Id
  @GeneratedValue
  @Column(name="id")
  private Integer id;             

  @Column(name="system_name")
  private String systemName;             

  @Column(name="name")
  private String name;             

  @Column(name="description")
  private String description;             

  @Column(name="section_id")
  private Integer sectionId;   
  
  @OneToMany(fetch = FetchType.LAZY)
  @JoinColumn(name = "category_id")
  private Collection<Dictionary> dictionary;


  @Transient
  private Category original;

  
  public Integer getId() {
    return id;
  }
  protected void setId(Integer id) {
    if((id == null && this.id != null) || 
       (id != null && !id.equals(this.id)))
      this.id = id;
  }

  public String getSystemName() {
    return systemName;
  }
  public void setSystemName(String systemName) {
    if((systemName == null && this.systemName != null) || 
       (systemName != null && !systemName.equals(this.systemName)))
      this.systemName = systemName;
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

  public Integer getSectionId() {
    return sectionId;
  }
  public void setSectionId(Integer sectionId) {
    if((sectionId == null && this.sectionId != null) || 
       (sectionId != null && !sectionId.equals(this.sectionId)))
      this.sectionId = sectionId;
  }

  
  public void setClone() {
    try {
      original = (Category)this.clone();
    }catch(Exception e){}
  }
  
  public String getChangeXML() {
    try {
      Document doc = XMLUtil.createNew("change");
      Element root = doc.getDocumentElement();
      
      AuditUtil.getChangeXML(id,original.id,doc,"id");

      AuditUtil.getChangeXML(systemName,original.systemName,doc,"system_name");

      AuditUtil.getChangeXML(name,original.name,doc,"name");

      AuditUtil.getChangeXML(description,original.description,doc,"description");

      AuditUtil.getChangeXML(sectionId,original.sectionId,doc,"section_id");

      if(root.hasChildNodes())
        return XMLUtil.toString(doc);
    }catch(Exception e){
      e.printStackTrace();
    }
    return null;
  }
   
  public String getTableName() {
    return "category";
  }
  
  public Collection<Dictionary> getDictionary() {
      return dictionary;
  }
  public void setDictionary(Collection<Dictionary> dictionary) {
      this.dictionary = dictionary;
  }
  public Category getOriginal() {
      return original;
  }
  public void setOriginal(Category original) {
      this.original = original;
  }
  
}   
