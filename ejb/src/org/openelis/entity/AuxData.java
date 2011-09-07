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
  * AuxData Entity POJO for database 
  */

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.openelis.domain.ReferenceTable;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.utils.Audit;
import org.openelis.utils.AuditUtil;
import org.openelis.utils.Auditable;

@NamedQueries({
    @NamedQuery( name = "AuxData.FetchById",
                query = "select distinct new org.openelis.domain.AuxDataViewDO(a.id, a.sortOrder, " + 
                        "a.auxFieldId, a.referenceId, a.referenceTableId, a.isReportable, a.typeId, " +
                        "a.value, '',afg.id, an.id, an.name, an.externalId) "
                      + " from AuxData a LEFT JOIN a.auxField af LEFT JOIN af.analyte an LEFT JOIN af.auxFieldGroup afg"
                      +	" where a.referenceId = :id and a.referenceTableId = :tableId order by a.sortOrder "),
    @NamedQuery( name = "AuxData.FetchForDataDump",
                query = "select distinct new org.openelis.domain.AuxDataViewDO(a.id, a.sortOrder, " + 
                        "a.auxFieldId, a.referenceId, a.referenceTableId, a.isReportable, a.typeId, " +
                        "a.value, '',afg.id, an.id, an.name, an.externalId) "
                      + " from AuxData a LEFT JOIN a.auxField af LEFT JOIN af.analyte an LEFT JOIN af.auxFieldGroup afg"
                      +	" where a.referenceId in (:ids) and a.referenceTableId = :tableId and a.isReportable = 'Y' and a.value != null order by an.name")})
                    
@Entity
@Table(name="aux_data")
@EntityListeners({AuditUtil.class})
public class AuxData implements Auditable, Cloneable {
  
  @Id
  @GeneratedValue
  @Column(name="id")
  private Integer id;             

  @Column(name="sort_order")
  private Integer sortOrder;             

  @Column(name="aux_field_id")
  private Integer auxFieldId;             

  @Column(name="reference_id")
  private Integer referenceId;             

  @Column(name="reference_table_id")
  private Integer referenceTableId;             

  @Column(name="is_reportable")
  private String isReportable;             

  @Column(name="type_id")
  private Integer typeId;             

  @Column(name="value")
  private String value; 
 
  @Transient
  private AuxData original;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "aux_field_id", insertable = false, updatable = false)
  private AuxField auxField;
  
  public Integer getId() {
    return id;
  }
  
  protected void setId(Integer id) {
    if(DataBaseUtil.isDifferent(id, this.id))
      this.id = id;
  }

  public Integer getSortOrder() {
    return sortOrder;
  }
  
  public void setSortOrder(Integer sortOrder) {
    if(DataBaseUtil.isDifferent(sortOrder, this.sortOrder))
      this.sortOrder = sortOrder;
  }

  public Integer getAuxFieldId() {
    return auxFieldId;
  }
  
  public void setAuxFieldId(Integer auxFieldId) {
    if(DataBaseUtil.isDifferent(auxFieldId, this.auxFieldId))
      this.auxFieldId = auxFieldId;
  }

  public Integer getReferenceId() {
    return referenceId;
  }
  
  public void setReferenceId(Integer referenceId) {
    if(DataBaseUtil.isDifferent(referenceId, this.referenceId))
      this.referenceId = referenceId;
  }

  public Integer getReferenceTableId() {
    return referenceTableId;
  }
  
  public void setReferenceTableId(Integer referenceTableId) {
    if(DataBaseUtil.isDifferent(referenceTableId, this.referenceTableId))
      this.referenceTableId = referenceTableId;
  }

  public String getIsReportable() {
    return isReportable;
  }
  
  public void setIsReportable(String isReportable) {
    if(DataBaseUtil.isDifferent(isReportable, this.isReportable))
      this.isReportable = isReportable;
  }

  public Integer getTypeId() {
    return typeId;
  }
  
  public void setTypeId(Integer typeId) {
    if(DataBaseUtil.isDifferent(typeId, this.typeId))
      this.typeId = typeId;
  }

  public String getValue() {
    return value;
  }
  
  public void setValue(String value) {
    if(DataBaseUtil.isDifferent(value, this.value))
      this.value = value;
  }
  
  public AuxField getAuxField() {
      return auxField;
  }

  public void setAuxField(AuxField auxField) {
      this.auxField = auxField;
  }
  
  public void setClone() {
    try {
        original = (AuxData)this.clone();
    }catch(Exception e){
        e.printStackTrace();
    }
  }
  
  public Audit getAudit() {
        Audit audit;

        audit = new Audit();
        audit.setReferenceTableId(ReferenceTable.AUX_DATA);
        audit.setReferenceId(getId());
        if (original != null)
            audit.setField("id", id, original.id)
                 .setField("sort_order", sortOrder, original.sortOrder)
                 .setField("aux_field_id", auxFieldId, original.auxFieldId)
                 .setField("reference_id", referenceId, original.referenceId)
                 .setField("reference_table_id", referenceTableId, original.referenceTableId)
                 .setField("is_reportable", isReportable, original.isReportable)
                 .setField("type_id", typeId, original.typeId, ReferenceTable.DICTIONARY)
                 .setField("value", value, original.value);

        return audit;
  }
  
}   
