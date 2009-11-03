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
  * StorageUnit Entity POJO for database 
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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;
import org.openelis.utils.AuditUtil;
import org.openelis.utils.Auditable;

@NamedQueries({@NamedQuery(name = "StorageUnit.StorageUnit", query = "select new org.openelis.domain.StorageUnitDO(s.id,s.category,s.description,s.isSingular) from StorageUnit s where s.id = :id"),
       @NamedQuery(name = "StorageUnit.AutoCompleteByDesc", query = "select new org.openelis.domain.StorageUnitAutoDO(s.id, s.description, s.category) " +
                                                         " from StorageUnit s where s.description like :desc order by s.description")})
                                                         
@Entity
@Table(name="storage_unit")
@EntityListeners({AuditUtil.class})
public class StorageUnit implements Auditable, Cloneable {
  
  @Id
  @GeneratedValue
  @Column(name="id")
  private Integer id;             

  @Column(name="category")
  private String category;             

  @Column(name="description")
  private String description;             

  @Column(name="is_singular")
  private String isSingular;             


  @Transient
  private StorageUnit original;

  
  public Integer getId() {
    return id;
  }
  protected void setId(Integer id) {
    if((id == null && this.id != null) || 
       (id != null && !id.equals(this.id)))
      this.id = id;
  }

  public String getCategory() {
    return category;
  }
  public void setCategory(String category) {
    if((category == null && this.category != null) || 
       (category != null && !category.equals(this.category)))
      this.category = category;
  }

  public String getDescription() {
    return description;
  }
  public void setDescription(String description) {
    if((description == null && this.description != null) || 
       (description != null && !description.equals(this.description)))
      this.description = description;
  }

  public String getIsSingular() {
    return isSingular;
  }
  public void setIsSingular(String isSingular) {
    if((isSingular == null && this.isSingular != null) || 
       (isSingular != null && !isSingular.equals(this.isSingular)))
      this.isSingular = isSingular;
  }

  
  public void setClone() {
    try {
      original = (StorageUnit)this.clone();
    }catch(Exception e){}
  }
  
  public String getChangeXML() {
    try {
      Document doc = XMLUtil.createNew("change");
      Element root = doc.getDocumentElement();
      
      AuditUtil.getChangeXML(id,original.id,doc,"id");

      AuditUtil.getChangeXML(category,original.category,doc,"category");

      AuditUtil.getChangeXML(description,original.description,doc,"description");

      AuditUtil.getChangeXML(isSingular,original.isSingular,doc,"is_singular");

      if(root.hasChildNodes())
        return XMLUtil.toString(doc);
    }catch(Exception e){
      e.printStackTrace();
    }
    return null;
  }
   
  public String getTableName() {
    return "storage_unit";
  }
  
}   
