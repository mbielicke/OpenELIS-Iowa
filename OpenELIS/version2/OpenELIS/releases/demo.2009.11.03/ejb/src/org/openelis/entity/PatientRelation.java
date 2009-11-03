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
  * PatientRelation Entity POJO for database 
  */

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.openelis.gwt.common.Datetime;
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
import org.openelis.utils.Auditable;

@Entity
@Table(name="patient_relation")
@EntityListeners({AuditUtil.class})
public class PatientRelation implements Auditable, Cloneable {
  
  @Id
  @GeneratedValue
  @Column(name="id")
  private Integer id;             

  @Column(name="relation_id")
  private Integer relationId;             

  @Column(name="from_patient_id")
  private Integer fromPatientId;             

  @Column(name="to_patient_id")
  private Integer toPatientId;             


  @Transient
  private PatientRelation original;

  
  public Integer getId() {
    return id;
  }
  protected void setId(Integer id) {
    if((id == null && this.id != null) || 
       (id != null && !id.equals(this.id)))
      this.id = id;
  }

  public Integer getRelationId() {
    return relationId;
  }
  public void setRelationId(Integer relationId) {
    if((relationId == null && this.relationId != null) || 
       (relationId != null && !relationId.equals(this.relationId)))
      this.relationId = relationId;
  }

  public Integer getFromPatientId() {
    return fromPatientId;
  }
  public void setFromPatientId(Integer fromPatientId) {
    if((fromPatientId == null && this.fromPatientId != null) || 
       (fromPatientId != null && !fromPatientId.equals(this.fromPatientId)))
      this.fromPatientId = fromPatientId;
  }

  public Integer getToPatientId() {
    return toPatientId;
  }
  public void setToPatientId(Integer toPatientId) {
    if((toPatientId == null && this.toPatientId != null) || 
       (toPatientId != null && !toPatientId.equals(this.toPatientId)))
      this.toPatientId = toPatientId;
  }

  
  public void setClone() {
    try {
      original = (PatientRelation)this.clone();
    }catch(Exception e){}
  }
  
  public String getChangeXML() {
    try {
      Document doc = XMLUtil.createNew("change");
      Element root = doc.getDocumentElement();
      
      AuditUtil.getChangeXML(id,original.id,doc,"id");

      AuditUtil.getChangeXML(relationId,original.relationId,doc,"relation_id");

      AuditUtil.getChangeXML(fromPatientId,original.fromPatientId,doc,"from_patient_id");

      AuditUtil.getChangeXML(toPatientId,original.toPatientId,doc,"to_patient_id");

      if(root.hasChildNodes())
        return XMLUtil.toString(doc);
    }catch(Exception e){
      e.printStackTrace();
    }
    return null;
  }
   
  public String getTableName() {
    return "patient_relation";
  }
  
}   
