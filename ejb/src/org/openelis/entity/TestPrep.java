
package org.openelis.entity;

/**
  * TestPrep Entity POJO for database 
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
@Table(name="test_prep")
@EntityListeners({AuditUtil.class})
public class TestPrep implements Auditable, Cloneable {
  
  @Id
  @GeneratedValue
  @Column(name="id")
  private Integer id;             

  @Column(name="test_id")
  private Integer testId;             

  @Column(name="prep_test_id")
  private Integer prepTestId;             

  @Column(name="is_optional")
  private String isOptional;             


  @Transient
  private TestPrep original;

  
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

  public Integer getPrepTestId() {
    return prepTestId;
  }
  public void setPrepTestId(Integer prepTestId) {
    if((prepTestId == null && this.prepTestId != null) || 
       (prepTestId != null && !prepTestId.equals(this.prepTestId)))
      this.prepTestId = prepTestId;
  }

  public String getIsOptional() {
    return isOptional;
  }
  public void setIsOptional(String isOptional) {
    if((isOptional == null && this.isOptional != null) || 
       (isOptional != null && !isOptional.equals(this.isOptional)))
      this.isOptional = isOptional;
  }

  
  public void setClone() {
    try {
      original = (TestPrep)this.clone();
    }catch(Exception e){}
  }
  
  public String getChangeXML() {
    try {
      Document doc = XMLUtil.createNew("change");
      Element root = doc.getDocumentElement();
      
      AuditUtil.getChangeXML(id,original.id,doc,"id");

      AuditUtil.getChangeXML(testId,original.testId,doc,"test_id");

      AuditUtil.getChangeXML(prepTestId,original.prepTestId,doc,"prep_test_id");

      AuditUtil.getChangeXML(isOptional,original.isOptional,doc,"is_optional");

      if(root.hasChildNodes())
        return XMLUtil.toString(doc);
    }catch(Exception e){
      e.printStackTrace();
    }
    return null;
  }
   
  public String getTableName() {
    return "test_prep";
  }
  
}   
