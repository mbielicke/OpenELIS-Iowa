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
  * AuxField Entity POJO for database 
  */

import java.util.Collection;

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
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import org.openelis.utils.AuditUtil;
import org.openelis.utils.Auditable;


@NamedQueries({@NamedQuery(name = "AuxField.AuxFieldDOList", query = "select distinct new org.openelis.domain.AuxFieldDO(af.id, af.sortOrder,"+
                       " af.analyteId,a.name,af.description,af.auxFieldGroupId,af.methodId,m.name,af.unitOfMeasureId,af.isRequired,"+
                       " af.isActive,af.isReportable,s.id,s.name) " +
                       " from AuxField af left join af.scriptlet s left join af.analyte a left join af.method m where af.auxFieldGroupId = :auxFieldGroupId order by af.sortOrder "),
                @NamedQuery(name = "AuxField.AuxFieldByAnalyteId", query = "select a.id from AuxField a where a.analyteId = :id ")       })
        
@Entity
@Table(name="aux_field")
@EntityListeners({AuditUtil.class})
public class AuxField implements Auditable, Cloneable {
  
  @Id
  @GeneratedValue
  @Column(name="id")
  private Integer id;       
  
  @Column(name="aux_field_group_id")
  private Integer auxFieldGroupId;  

  @Column(name="sort_order")
  private Integer sortOrder;             

  @Column(name="analyte_id")
  private Integer analyteId;             

  @Column(name="description")
  private String description;                         

  @Column(name="method_id")
  private Integer methodId;             

  @Column(name="unit_of_measure_id")
  private Integer unitOfMeasureId;             

  @Column(name="is_required")
  private String isRequired;             

  @Column(name="is_active")
  private String isActive;             

  @Column(name="is_reportable")
  private String isReportable;             

  @Column(name="scriptlet_id")
  private Integer scriptletId;             

  @OneToMany(fetch = FetchType.LAZY)
  @JoinColumn(name = "aux_field_id",insertable = false, updatable = false)
  private Collection<AuxFieldValue> auxFieldValue;
  
  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "analyte_id",insertable = false, updatable = false)
  private Analyte analyte;
  
  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "method_id",insertable = false, updatable = false)
  private Method method;
  
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "scriptlet_id",insertable = false, updatable = false)
  private Scriptlet scriptlet;

  @Transient
  private AuxField original;

  
  public Integer getId() {
    return id;
  }
  protected void setId(Integer id) {
    if((id == null && this.id != null) || 
       (id != null && !id.equals(this.id)))
      this.id = id;
  }

  public Integer getSortOrder() {
    return sortOrder;
  }
  public void setSortOrder(Integer sortOrder) {
    if((sortOrder == null && this.sortOrder != null) || 
       (sortOrder != null && !sortOrder.equals(this.sortOrder)))
      this.sortOrder = sortOrder;
  }

  public Integer getAnalyteId() {
    return analyteId;
  }
  public void setAnalyteId(Integer analyteId) {
    if((analyteId == null && this.analyteId != null) || 
       (analyteId != null && !analyteId.equals(this.analyteId)))
      this.analyteId = analyteId;
  }

  public String getDescription() {
    return description;
  }
  public void setDescription(String description) {
    if((description == null && this.description != null) || 
       (description != null && !description.equals(this.description)))
      this.description = description;
  }


  public Integer getMethodId() {
    return methodId;
  }
  public void setMethodId(Integer methodId) {
    if((methodId == null && this.methodId != null) || 
       (methodId != null && !methodId.equals(this.methodId)))
      this.methodId = methodId;
  }

  public Integer getUnitOfMeasureId() {
    return unitOfMeasureId;
  }
  public void setUnitOfMeasureId(Integer unitOfMeasureId) {
    if((unitOfMeasureId == null && this.unitOfMeasureId != null) || 
       (unitOfMeasureId != null && !unitOfMeasureId.equals(this.unitOfMeasureId)))
      this.unitOfMeasureId = unitOfMeasureId;
  }

  public String getIsRequired() {
    return isRequired;
  }
  public void setIsRequired(String isRequired) {
    if((isRequired == null && this.isRequired != null) || 
       (isRequired != null && !isRequired.equals(this.isRequired)))
      this.isRequired = isRequired;
  }

  public String getIsActive() {
    return isActive;
  }
  public void setIsActive(String isActive) {
    if((isActive == null && this.isActive != null) || 
       (isActive != null && !isActive.equals(this.isActive)))
      this.isActive = isActive;
  }

  public String getIsReportable() {
    return isReportable;
  }
  public void setIsReportable(String isReportable) {
    if((isReportable == null && this.isReportable != null) || 
       (isReportable != null && !isReportable.equals(this.isReportable)))
      this.isReportable = isReportable;
  }

  public Integer getScriptletId() {
    return scriptletId;
  }
  public void setScriptletId(Integer scriptletId) {
    if((scriptletId == null && this.scriptletId != null) || 
       (scriptletId != null && !scriptletId.equals(this.scriptletId)))
      this.scriptletId = scriptletId;
  }
  
  public Analyte getAnalyte() {
      return analyte;
  }
  public void setAnalyte(Analyte analyte) {
      this.analyte = analyte;
  }
  public void setMethod(Method method) {
      this.method = method;
  }
  public Method getMethod() {
      return method;
  }

  
  public void setClone() {
    try {
      original = (AuxField)this.clone();
    }catch(Exception e){}
  }
  
  public String getChangeXML() {
    try {
      Document doc = XMLUtil.createNew("change");
      Element root = doc.getDocumentElement();
      
      AuditUtil.getChangeXML(id,original.id,doc,"id");
      
      AuditUtil.getChangeXML(auxFieldGroupId,original.auxFieldGroupId,doc,"aux_field_group_id");

      AuditUtil.getChangeXML(sortOrder,original.sortOrder,doc,"sort_order");

      AuditUtil.getChangeXML(analyteId,original.analyteId,doc,"analyte_id");

      AuditUtil.getChangeXML(description,original.description,doc,"description");

      AuditUtil.getChangeXML(methodId,original.methodId,doc,"method_id");

      AuditUtil.getChangeXML(unitOfMeasureId,original.unitOfMeasureId,doc,"unit_of_measure_id");

      AuditUtil.getChangeXML(isRequired,original.isRequired,doc,"is_required");

      AuditUtil.getChangeXML(isActive,original.isActive,doc,"is_active");

      AuditUtil.getChangeXML(isReportable,original.isReportable,doc,"is_reportable");

      AuditUtil.getChangeXML(scriptletId,original.scriptletId,doc,"scriptlet_id");

      if(root.hasChildNodes())
        return XMLUtil.toString(doc);
    }catch(Exception e){
      e.printStackTrace();
    }
    return null;
  }
   
  public String getTableName() {
    return "aux_field";
  }
public Collection<AuxFieldValue> getAuxFieldValue() {
    return auxFieldValue;
}
public void setAuxFieldValue(Collection<AuxFieldValue> auxFieldValue) {
    this.auxFieldValue = auxFieldValue;
}
public Integer getAuxFieldGroupId() {
    return auxFieldGroupId;
}
public void setAuxFieldGroupId(Integer auxFieldGroupId) {
    if((auxFieldGroupId == null && this.auxFieldGroupId != null) || 
      (auxFieldGroupId != null && !auxFieldGroupId.equals(this.auxFieldGroupId)))
      this.auxFieldGroupId = auxFieldGroupId;
}
public Scriptlet getScriptlet() {
    return scriptlet;
}
public void setScriptlet(Scriptlet scriptlet) {
    this.scriptlet = scriptlet;
}
  
}   
