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
  * TestWorksheet Entity POJO for database 
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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import org.openelis.utils.AuditUtil;
import org.openelis.utils.Auditable;

@NamedQueries({@NamedQuery(name = "TestWorksheet.TestWorksheetByTestId",query = "from TestWorksheet tw where tw.testId = :testId"),
               @NamedQuery(name = "TestWorksheet.TestWorksheetDOByTestId",query = "select distinct new org.openelis.domain.TestWorksheetDO(tw.id,tw.testId,tw.batchCapacity," +
                "tw.totalCapacity, tw.formatId, tw.scriptletId) from TestWorksheet tw where tw.testId = :testId"),
               @NamedQuery(name = "TestWorksheet.TestWorksheetItemsByTestId",query = "select distinct new org.openelis.domain.TestWorksheetItemDO(twi.id,twi.testWorksheetId," +
                "twi.position, twi.typeId,twi.qcName) from TestWorksheet tw left join tw.testWorksheetItem twi where tw.testId = :testId order by twi.position")})


@Entity
@Table(name="test_worksheet")
@EntityListeners({AuditUtil.class})
public class TestWorksheet implements Auditable, Cloneable {
  
  @Id
  @GeneratedValue
  @Column(name="id")
  private Integer id;             

  @Column(name="test_id")
  private Integer testId;             

  @Column(name="batch_capacity")
  private Integer batchCapacity;             

  @Column(name="total_capacity")
  private Integer totalCapacity;             

  @Column(name="format_id")
  private Integer formatId;             

  @Column(name="scriptlet_id")
  private Integer scriptletId; 
  
  @OneToMany(fetch = FetchType.LAZY)
  @JoinColumn(name = "test_worksheet_id",insertable = false, updatable = false)
  private Collection<TestWorksheetItem> testWorksheetItem;


  @Transient
  private TestWorksheet original;

  
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

  public Integer getBatchCapacity() {
    return batchCapacity;
  }
  public void setBatchCapacity(Integer batchCapacity) {
    if((batchCapacity == null && this.batchCapacity != null) || 
       (batchCapacity != null && !batchCapacity.equals(this.batchCapacity)))
      this.batchCapacity = batchCapacity;
  }

  public Integer getTotalCapacity() {
    return totalCapacity;
  }
  public void setTotalCapacity(Integer totalCapacity) {
    if((totalCapacity == null && this.totalCapacity != null) || 
       (totalCapacity != null && !totalCapacity.equals(this.totalCapacity)))
      this.totalCapacity = totalCapacity;
  }

  public Integer getFormatId() {
    return formatId;
  }
  public void setNumberFormatId(Integer formatId) {
    if((formatId == null && this.formatId != null) || 
       (formatId != null && !formatId.equals(this.formatId)))
      this.formatId = formatId;
  }

  public Integer getScriptletId() {
    return scriptletId;
  }
  public void setScriptletId(Integer scriptletId) {
    if((scriptletId == null && this.scriptletId != null) || 
       (scriptletId != null && !scriptletId.equals(this.scriptletId)))
      this.scriptletId = scriptletId;
  }

  
  public void setClone() {
    try {
      original = (TestWorksheet)this.clone();
    }catch(Exception e){}
  }
  
  public String getChangeXML() {
    try {
      Document doc = XMLUtil.createNew("change");
      Element root = doc.getDocumentElement();
      
      AuditUtil.getChangeXML(id,original.id,doc,"id");

      AuditUtil.getChangeXML(testId,original.testId,doc,"test_id");

      AuditUtil.getChangeXML(batchCapacity,original.batchCapacity,doc,"batch_capacity");

      AuditUtil.getChangeXML(totalCapacity,original.totalCapacity,doc,"total_capacity");

      AuditUtil.getChangeXML(formatId,original.formatId,doc,"format_id");

      AuditUtil.getChangeXML(scriptletId,original.scriptletId,doc,"scriptlet_id");

      if(root.hasChildNodes())
        return XMLUtil.toString(doc);
    }catch(Exception e){
      e.printStackTrace();
    }
    return null;
  }
   
  public String getTableName() {
    return "test_worksheet";
  }
public Collection<TestWorksheetItem> getTestWorksheetItem() {
    return testWorksheetItem;
}
public void setTestWorksheetItem(Collection<TestWorksheetItem> testWorksheetItem) {
    this.testWorksheetItem = testWorksheetItem;
}
  
}   
