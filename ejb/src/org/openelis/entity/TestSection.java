
package org.openelis.entity;

/**
  * TestSection Entity POJO for database 
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
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import org.openelis.utils.AuditUtil;
import org.openelis.utils.Auditable;

@NamedQuery(name = "TestSection.TestSectionsByTestId", query = "select distinct new org.openelis.domain.TestSectionDO(ts.id,ts.testId,ts.sectionId,ts.flagId) " +
                                                                                 " from TestSection ts where ts.testId = :testId")
                     

@Entity
@Table(name="test_section")
@EntityListeners({AuditUtil.class})
public class TestSection implements Auditable, Cloneable {
  
  @Id
  @GeneratedValue
  @Column(name="id")
  private Integer id;             

  @Column(name="test_id")
  private Integer testId;             

  @Column(name="section_id")
  private Integer sectionId;             

  @Column(name="flag_id")
  private Integer flagId;  
  
  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "section_id",insertable = false, updatable = false)
  private Section section;
  
  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "test_id",insertable = false, updatable = false)
  private Test test;


  @Transient
  private TestSection original;

  
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

  public Integer getSectionId() {
    return sectionId;
  }
  public void setSectionId(Integer sectionId) {
    if((sectionId == null && this.sectionId != null) || 
       (sectionId != null && !sectionId.equals(this.sectionId)))
      this.sectionId = sectionId;
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
      original = (TestSection)this.clone();
    }catch(Exception e){}
  }
  
  public String getChangeXML() {
    try {
      Document doc = XMLUtil.createNew("change");
      Element root = doc.getDocumentElement();
      
      AuditUtil.getChangeXML(id,original.id,doc,"id");

      AuditUtil.getChangeXML(testId,original.testId,doc,"test_id");

      AuditUtil.getChangeXML(sectionId,original.sectionId,doc,"section_id");

      AuditUtil.getChangeXML(flagId,original.flagId,doc,"flag_id");

      if(root.hasChildNodes())
        return XMLUtil.toString(doc);
    }catch(Exception e){
      e.printStackTrace();
    }
    return null;
  }
   
  public String getTableName() {
    return "test_section";
  }
public Section getSection() {
    return section;
}
public void setSection(Section section) {
    this.section = section;
}
  
}   
