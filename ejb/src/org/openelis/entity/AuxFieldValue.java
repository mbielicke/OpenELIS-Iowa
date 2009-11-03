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
  * AuxFieldValue Entity POJO for database 
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

@NamedQueries({@NamedQuery(name = "AuxFieldValue.FetchById", query = "select distinct new org.openelis.domain.AuxFieldValueViewDO(afv.id,afv.auxFieldId,"+
                          " afv.typeId,afv.value,'') from AuxFieldValue afv where afv.auxFieldId = :auxFieldId"),
                          @NamedQuery(name = "AuxFieldValue.FetchByGroupId", query = "select distinct new org.openelis.domain.AuxFieldValueViewDO(afv.id,afv.auxFieldId,"+
                                      " afv.typeId,afv.value,'') from AuxField af left join af.auxFieldValue afv where " + 
                                                  " af.auxFieldGroupId = :groupId order by afv.auxFieldId "),
                          @NamedQuery(name = "AuxFieldValue.FetchByDataRefId", query = "select distinct new org.openelis.domain.AuxFieldValueViewDO(afv.id,afv.auxFieldId,"+
                          " afv.typeId,afv.value,'') from AuxData ad, IN (ad.auxField) af left join af.auxFieldValue afv where " + 
                                      " ad.referenceId = :id and ad.referenceTableId = :tableId order by afv.auxFieldId")})

@Entity
@Table(name="aux_field_value")
@EntityListeners({AuditUtil.class})
public class AuxFieldValue implements Auditable, Cloneable {
  
  @Id
  @GeneratedValue
  @Column(name="id")
  private Integer id;             

  @Column(name="aux_field_id")
  private Integer auxFieldId;             

  @Column(name="type_id")
  private Integer typeId;             

  @Column(name="value")
  private String value;             


  @Transient
  private AuxFieldValue original;

  
  public Integer getId() {
    return id;
  }
  protected void setId(Integer id) {
    if((id == null && this.id != null) || 
       (id != null && !id.equals(this.id)))
      this.id = id;
  }

  public Integer getAuxFieldId() {
    return auxFieldId;
  }
  
  public void setAuxFieldId(Integer auxFieldId) {
    if((auxFieldId == null && this.auxFieldId != null) || 
       (auxFieldId != null && !auxFieldId.equals(this.auxFieldId)))
      this.auxFieldId = auxFieldId;
  }

  public Integer getTypeId() {
    return typeId;
  }
  public void setTypeId(Integer typeId) {
    if((typeId == null && this.typeId != null) || 
       (typeId != null && !typeId.equals(this.typeId)))
      this.typeId = typeId;
  }

  public String getValue() {
    return value;
  }
  public void setValue(String value) {
    if((value == null && this.value != null) || 
       (value != null && !value.equals(this.value)))
      this.value = value;
  }

  
  public void setClone() {
    try {
      original = (AuxFieldValue)this.clone();
    }catch(Exception e){}
  }
  
  public String getChangeXML() {
    try {
      Document doc = XMLUtil.createNew("change");
      Element root = doc.getDocumentElement();
      
      AuditUtil.getChangeXML(id,original.id,doc,"id");

      AuditUtil.getChangeXML(auxFieldId,original.auxFieldId,doc,"aux_field_id");

      AuditUtil.getChangeXML(typeId,original.typeId,doc,"type_id");

      AuditUtil.getChangeXML(value,original.value,doc,"value");

      if(root.hasChildNodes())
        return XMLUtil.toString(doc);
    }catch(Exception e){
      e.printStackTrace();
    }
    return null;
  }
   
  public String getTableName() {
    return "aux_field_value";
  }
  
}   
