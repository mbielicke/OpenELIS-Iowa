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
  * Method Entity POJO for database 
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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import org.openelis.utils.AuditUtil;
import org.openelis.utils.Auditable;

@NamedQueries({@NamedQuery(name = "Method.MethodIdName",query = "select distinct new org.openelis.domain.IdNameDO(m.id, m.name) " + "  from Method m order by m.name"),
               @NamedQuery(name = "Method.MethodById",query = "select distinct new org.openelis.domain.MethodDO(m.id,m.name,m.description," +
                    "m.reportingDescription,m.isActive,m.activeBegin, m.activeEnd) " + "  from Method m where m.id = :id"),
               @NamedQuery(name = "Method.MethodByName", query = "from Method m where m.name = :name order by m.name"),
               @NamedQuery(name = "Method.AutoCompleteByName", query = "select distinct new org.openelis.domain.IdNameDO(m.id, m.name) from Method m where m.name like :name and m.isActive = :isActive order by m.name ")})
               
@Entity
@Table(name="method")
@EntityListeners({AuditUtil.class})
public class Method implements Auditable, Cloneable {
  
  @Id
  @GeneratedValue
  @Column(name="id")
  private Integer id;             

  @Column(name="name")
  private String name;             

  @Column(name="description")
  private String description;             

  @Column(name="reporting_description")
  private String reportingDescription;             

  @Column(name="is_active")
  private String isActive;             

  @Column(name="active_begin")
  private Date activeBegin;             

  @Column(name="active_end")
  private Date activeEnd;             

  @OneToMany(fetch = FetchType.LAZY)
  @JoinColumn(name = "method_id",insertable = false, updatable = false)
  private Collection<MethodAnalyte> methodAnalyte;
  
  @Transient
  private Method original;

  
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

  public String getDescription() {
    return description;
  }
  public void setDescription(String description) {
    if((description == null && this.description != null) || 
       (description != null && !description.equals(this.description)))
      this.description = description;
  }

  public String getReportingDescription() {
    return reportingDescription;
  }
  public void setReportingDescription(String reportingDescription) {
    if((reportingDescription == null && this.reportingDescription != null) || 
       (reportingDescription != null && !reportingDescription.equals(this.reportingDescription)))
      this.reportingDescription = reportingDescription;
  }

  public String getIsActive() {
    return isActive;
  }
  public void setIsActive(String isActive) {
    if((isActive == null && this.isActive != null) || 
       (isActive != null && !isActive.equals(this.isActive)))
      this.isActive = isActive;
  }

  public Datetime getActiveBegin() {
    if(activeBegin == null)
      return null;
    return new Datetime(Datetime.YEAR ,Datetime. DAY,activeBegin);
  }
  public void setActiveBegin (Datetime active_begin){
      if((active_begin == null && this.activeBegin != null) || (active_begin != null && this.activeBegin == null) ||
        (active_begin != null && !active_begin.equals(new Datetime(Datetime.YEAR, Datetime.DAY, this.activeBegin))))
      this.activeBegin = active_begin.getDate();
  }

  public Datetime getActiveEnd() {
    if(activeEnd == null)
      return null;
    return new Datetime(Datetime.YEAR ,Datetime. DAY,activeEnd);
  }
  public void setActiveEnd (Datetime active_end){
      if((active_end == null && this.activeEnd != null) || (active_end != null && this.activeEnd == null) ||
        (active_end != null && !active_end.equals(new Datetime(Datetime.YEAR, Datetime.DAY, this.activeEnd))))
      this.activeEnd = active_end.getDate();
  }

  
  public void setClone() {
    try {
      original = (Method)this.clone();
    }catch(Exception e){}
  }
  
  public String getChangeXML() {
    try {
      Document doc = XMLUtil.createNew("change");
      Element root = doc.getDocumentElement();
      
      AuditUtil.getChangeXML(id,original.id,doc,"id");

      AuditUtil.getChangeXML(name,original.name,doc,"name");

      AuditUtil.getChangeXML(description,original.description,doc,"description");

      AuditUtil.getChangeXML(reportingDescription,original.reportingDescription,doc,"reporting_description");

      AuditUtil.getChangeXML(isActive,original.isActive,doc,"is_active");

      AuditUtil.getChangeXML(activeBegin,original.activeBegin,doc,"active_begin");

      AuditUtil.getChangeXML(activeEnd,original.activeEnd,doc,"active_end");

      if(root.hasChildNodes())
        return XMLUtil.toString(doc);
    }catch(Exception e){
      e.printStackTrace();
    }
    return null;
  }
   
  public String getTableName() {
    return "method";
  }
public Collection<MethodAnalyte> getMethodAnalyte() {
    return methodAnalyte;
}
public void setMethodAnalyte(Collection<MethodAnalyte> methodAnalyte) {
    this.methodAnalyte = methodAnalyte;
}
  
}   
