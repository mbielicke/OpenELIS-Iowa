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
  * MethodAnalyte Entity POJO for database 
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

@NamedQueries({@NamedQuery(name = "MethodAnalyte.MethodAnalyteByAnalyteId", query = "select m.id from MethodAnalyte m where m.analyteId = :id"),
               @NamedQuery(name = "MethodAnalyte.GetMaxAnalyteGroup", query = "select max(m.analyteGroup) " +                                                          
                                                          " from MethodAnalyte m where m.methodId = :id")})
    
@Entity
@Table(name="method_analyte")
@EntityListeners({AuditUtil.class})
public class MethodAnalyte implements Auditable, Cloneable {
  
  @Id
  @GeneratedValue
  @Column(name="id")
  private Integer id;             

  @Column(name="method_id")
  private Integer methodId;             

  @Column(name="analyte_group")
  private Integer analyteGroup;             

  @Column(name="result_group")
  private Integer resultGroup;             

  @Column(name="sort_order")
  private Integer sortOrder;             

  @Column(name="type")
  private String type;             

  @Column(name="analyte_id")
  private Integer analyteId;             


  @Transient
  private MethodAnalyte original;

  
  public Integer getId() {
    return id;
  }
  protected void setId(Integer id) {
    if((id == null && this.id != null) || 
       (id != null && !id.equals(this.id)))
      this.id = id;
  }

  public Integer getMethodId() {
    return methodId;
  }
  public void setMethodId(Integer methodId) {
    if((methodId == null && this.methodId != null) || 
       (methodId != null && !methodId.equals(this.methodId)))
      this.methodId = methodId;
  }

  public Integer getAnalyteGroup() {
    return analyteGroup;
  }
  public void setAnalyteGroup(Integer analyteGroup) {
    if((analyteGroup == null && this.analyteGroup != null) || 
       (analyteGroup != null && !analyteGroup.equals(this.analyteGroup)))
      this.analyteGroup = analyteGroup;
  }

  public Integer getResultGroup() {
    return resultGroup;
  }
  public void setResultGroup(Integer resultGroup) {
    if((resultGroup == null && this.resultGroup != null) || 
       (resultGroup != null && !resultGroup.equals(this.resultGroup)))
      this.resultGroup = resultGroup;
  }

  public Integer getSortOrder() {
    return sortOrder;
  }
  public void setSortOrder(Integer sortOrder) {
    if((sortOrder == null && this.sortOrder != null) || 
       (sortOrder != null && !sortOrder.equals(this.sortOrder)))
      this.sortOrder = sortOrder;
  }

  public String getType() {
    return type;
  }
  public void setType(String type) {
    if((type == null && this.type != null) || 
       (type != null && !type.equals(this.type)))
      this.type = type;
  }

  public Integer getAnalyteId() {
    return analyteId;
  }
  public void setAnalyteId(Integer analyteId) {
    if((analyteId == null && this.analyteId != null) || 
       (analyteId != null && !analyteId.equals(this.analyteId)))
      this.analyteId = analyteId;
  }

  
  public void setClone() {
    try {
      original = (MethodAnalyte)this.clone();
    }catch(Exception e){}
  }
  
  public String getChangeXML() {
    try {
      Document doc = XMLUtil.createNew("change");
      Element root = doc.getDocumentElement();
      
      AuditUtil.getChangeXML(id,original.id,doc,"id");

      AuditUtil.getChangeXML(methodId,original.methodId,doc,"method_id");

      AuditUtil.getChangeXML(analyteGroup,original.analyteGroup,doc,"analyte_group");

      AuditUtil.getChangeXML(resultGroup,original.resultGroup,doc,"result_group");

      AuditUtil.getChangeXML(sortOrder,original.sortOrder,doc,"sort_order");

      AuditUtil.getChangeXML(type,original.type,doc,"type");

      AuditUtil.getChangeXML(analyteId,original.analyteId,doc,"analyte_id");

      if(root.hasChildNodes())
        return XMLUtil.toString(doc);
    }catch(Exception e){
      e.printStackTrace();
    }
    return null;
  }
   
  public String getTableName() {
    return "method_analyte";
  }
  
}   
