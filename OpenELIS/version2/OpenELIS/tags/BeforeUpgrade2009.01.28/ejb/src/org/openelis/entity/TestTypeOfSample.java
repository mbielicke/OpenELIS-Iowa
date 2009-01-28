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
  * TestTypeOfSample Entity POJO for database 
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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import org.openelis.utils.AuditUtil;
import org.openelis.utils.Auditable;


@NamedQueries({@NamedQuery(name = "TestTypeOfSample.TestTypeOfSample", query = "select distinct new org.openelis.domain.TestTypeOfSampleDO(ts.id, ts.testId, ts.typeOfSampleId,ts.unitOfMeasureId) " 
                                            + "  from TestTypeOfSample ts where ts.testId = :id"),
               @NamedQuery(name = "TestTypeOfSample.TestTypeOfSampleByTestId", query = "from TestTypeOfSample ts where ts.testId = :testId"),
               @NamedQuery(name = "TestTypeOfSample.DictEntriesForUnitsByTestId", query = "select distinct new org.openelis.domain.IdNameDO(ts.unitOfMeasureId, d.entry) " 
                               + "  from TestTypeOfSample ts, Dictionary d where ts.unitOfMeasureId = d.id and ts.testId = :id " )})
            
@Entity
@Table(name="test_type_of_sample")
@EntityListeners({AuditUtil.class})
public class TestTypeOfSample implements Auditable, Cloneable {
  
  @Id
  @GeneratedValue
  @Column(name="id")
  private Integer id;             

  @Column(name="test_id")
  private Integer testId;             

  @Column(name="type_of_sample_id")
  private Integer typeOfSampleId;             

  @Column(name="unit_of_measure_id")
  private Integer unitOfMeasureId;             

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "type_of_sample_id",insertable = false, updatable = false)
  private Dictionary dictionary;   
  
  @Transient
  private TestTypeOfSample original;

  
  public Integer getId() {
    return id;
  }
  protected void setId(Integer id) {
    if((id == null && this.id != null) || 
       (id != null && !id.equals(this.id)))
      this.id = id;
  }

  public Integer getTestId() {
    return testId;
  }
  public void setTestId(Integer testId) {
    if((testId == null && this.testId != null) || 
       (testId != null && !testId.equals(this.testId)))
      this.testId = testId;
  }

  public Integer getTypeOfSampleId() {
    return typeOfSampleId;
  }
  public void setTypeOfSampleId(Integer typeOfSampleId) {
    if((typeOfSampleId == null && this.typeOfSampleId != null) || 
       (typeOfSampleId != null && !typeOfSampleId.equals(this.typeOfSampleId)))
      this.typeOfSampleId = typeOfSampleId;
  }

  public Integer getUnitOfMeasureId() {
    return unitOfMeasureId;
  }
  public void setUnitOfMeasureId(Integer unitOfMeasureId) {
    if((unitOfMeasureId == null && this.unitOfMeasureId != null) || 
       (unitOfMeasureId != null && !unitOfMeasureId.equals(this.unitOfMeasureId)))
      this.unitOfMeasureId = unitOfMeasureId;
  }

  
  public void setClone() {
    try {
      original = (TestTypeOfSample)this.clone();
    }catch(Exception e){}
  }
  
  public String getChangeXML() {
    try {
      Document doc = XMLUtil.createNew("change");
      Element root = doc.getDocumentElement();
      
      AuditUtil.getChangeXML(id,original.id,doc,"id");

      AuditUtil.getChangeXML(testId,original.testId,doc,"test_id");

      AuditUtil.getChangeXML(typeOfSampleId,original.typeOfSampleId,doc,"type_of_sample_id");

      AuditUtil.getChangeXML(unitOfMeasureId,original.unitOfMeasureId,doc,"unit_of_measure_id");

      if(root.hasChildNodes())
        return XMLUtil.toString(doc);
    }catch(Exception e){
      e.printStackTrace();
    }
    return null;
  }
   
  public String getTableName() {
    return "test_type_of_sample";
  }
public Dictionary getDictionary() {
    return dictionary;
}
public void setDictionary(Dictionary dictionary) {
    this.dictionary = dictionary;
}
  
}   
