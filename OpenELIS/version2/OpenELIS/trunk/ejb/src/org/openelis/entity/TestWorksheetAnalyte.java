
package org.openelis.entity;

/**
  * TestWorksheetAnalyte Entity POJO for database 
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
import javax.persistence.Table;
import javax.persistence.Transient;
import org.openelis.utils.AuditUtil;
import org.openelis.utils.Auditable;

@Entity
@Table(name="test_worksheet_analyte")
@EntityListeners({AuditUtil.class})
public class TestWorksheetAnalyte implements Auditable, Cloneable {
  
  @Id
  @GeneratedValue
  @Column(name="id")
  private Integer id;             

  @Column(name="test_id")
  private Integer testId;             

  @Column(name="analyte_id")
  private Integer analyteId;             

  @Column(name="repeat")
  private Integer repeat;             

  @Column(name="flag_id")
  private Integer flagId;             


  @Transient
  private TestWorksheetAnalyte original;

  
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

  public Integer getAnalyteId() {
    return analyteId;
  }
  public void setAnalyteId(Integer analyteId) {
    if((analyteId == null && this.analyteId != null) || 
       (analyteId != null && !analyteId.equals(this.analyteId)))
      this.analyteId = analyteId;
  }

  public Integer getRepeat() {
    return repeat;
  }
  public void setRepeat(Integer repeat) {
    if((repeat == null && this.repeat != null) || 
       (repeat != null && !repeat.equals(this.repeat)))
      this.repeat = repeat;
  }

  public Integer getFlagId() {
    return flagId;
  }
  public void setFlagId(Integer flagId) {
    if((flagId == null && this.flagId != null) || 
       (flagId != null && !flagId.equals(this.flagId)))
      this.flagId = flagId;
  }

  
  public void setClone() {
    try {
      original = (TestWorksheetAnalyte)this.clone();
    }catch(Exception e){}
  }
  
  public String getChangeXML() {
    try {
      Document doc = XMLUtil.createNew("change");
      Element root = doc.getDocumentElement();
      
      AuditUtil.getChangeXML(id,original.id,doc,"id");

      AuditUtil.getChangeXML(testId,original.testId,doc,"test_id");

      AuditUtil.getChangeXML(analyteId,original.analyteId,doc,"analyte_id");

      AuditUtil.getChangeXML(repeat,original.repeat,doc,"repeat");

      AuditUtil.getChangeXML(flagId,original.flagId,doc,"flag_id");

      if(root.hasChildNodes())
        return XMLUtil.toString(doc);
    }catch(Exception e){
      e.printStackTrace();
    }
    return null;
  }
   
  public String getTableName() {
    return "test_worksheet_analyte";
  }
  
}   
