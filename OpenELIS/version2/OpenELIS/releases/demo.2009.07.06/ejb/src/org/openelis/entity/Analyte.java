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
  * Analyte Entity POJO for database 
  */

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.openelis.util.XMLUtil;

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
import javax.persistence.Table;
import javax.persistence.Transient;
import org.openelis.utils.AuditUtil;
import org.openelis.utils.Auditable;

@NamedQueries({@NamedQuery(name = "Analyte.Analyte", query = "select new org.openelis.domain.AnalyteDO(a.id,a.name,a.isActive,a.parentAnalyteId,p.name,a.externalId) from " + 
" Analyte a left join a.parentAnalyte p where a.id = :id"),
@NamedQuery(name = "Analyte.AnalyteByParentId", query = "select a.id from Analyte a where a.parentAnalyteId = :id"),
@NamedQuery(name = "Analyte.UpdateNameCompare", query = "select a.id from Analyte a where a.name = :name and a.id != :id"),
@NamedQuery(name = "Analyte.AddNameCompare", query = "select a.id from Analyte a where a.name = :name"),
@NamedQuery(name = "Analyte.AutoCompleteByName", query = "select new org.openelis.domain.IdNameDO(a.id, a.name) " +
     " from Analyte a where a.name like :name and a.isActive = 'Y' order by a.name")})
     
@Entity
@Table(name="analyte")
@EntityListeners({AuditUtil.class})
public class Analyte implements Auditable, Cloneable {
  
  @Id
  @GeneratedValue
  @Column(name="id")
  private Integer id;             

  @Column(name="name")
  private String name;             

  @Column(name="is_active")
  private String isActive;             

  @Column(name="parent_analyte_id")
  private Integer parentAnalyteId;             

  @Column(name="external_id")
  private String externalId;  
  
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "parent_analyte_id", insertable = false, updatable = false)
  private Analyte parentAnalyte;


  @Transient
  private Analyte original;

  
  public Integer getId() {
    return id;
  }
  protected void setId(Integer id) {
    if((id == null && this.id != null) || 
       (id != null && !id.equals(this.id)))
      this.id = id;
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

  public Integer getParentAnalyteId() {
    return parentAnalyteId;
  }
  public void setParentAnalyteId(Integer parentAnalyteId) {
    if((parentAnalyteId == null && this.parentAnalyteId != null) || 
       (parentAnalyteId != null && !parentAnalyteId.equals(this.parentAnalyteId)))
      this.parentAnalyteId = parentAnalyteId;
  }

  public String getExternalId() {
    return externalId;
  }
  public void setExternalId(String externalId) {
    if((externalId == null && this.externalId != null) || 
       (externalId != null && !externalId.equals(this.externalId)))
      this.externalId = externalId;
  }

  
  public void setClone() {
    try {
      original = (Analyte)this.clone();
    }catch(Exception e){}
  }
  
  public String getChangeXML() {
    try {
      Document doc = XMLUtil.createNew("change");
      Element root = doc.getDocumentElement();
      
      AuditUtil.getChangeXML(id,original.id,doc,"id");

      AuditUtil.getChangeXML(name,original.name,doc,"name");

      AuditUtil.getChangeXML(isActive,original.isActive,doc,"is_active");

      AuditUtil.getChangeXML(parentAnalyteId,original.parentAnalyteId,doc,"parent_analyte_id");

      AuditUtil.getChangeXML(externalId,original.externalId,doc,"external_id");

      if(root.hasChildNodes())
        return XMLUtil.toString(doc);
    }catch(Exception e){
      e.printStackTrace();
    }
    return null;
  }
   
  public String getTableName() {
    return "analyte";
  }
  
  public Analyte getParentAnalyte() {
    return parentAnalyte;
  }
  public void setParentAnalyte(Analyte parentAnalyte) {
    this.parentAnalyte = parentAnalyte;
  }
  
}   
