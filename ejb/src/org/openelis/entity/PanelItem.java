
package org.openelis.entity;

/**
  * PanelItem Entity POJO for database 
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
@Table(name="panel_item")
@EntityListeners({AuditUtil.class})
public class PanelItem implements Auditable, Cloneable {
  
  @Id
  @GeneratedValue
  @Column(name="id")
  private Integer id;             

  @Column(name="panel_id")
  private Integer panelId;             

  @Column(name="sort_order_id")
  private Integer sortOrderId;             

  @Column(name="test_name")
  private String testName;             

  @Column(name="method_name")
  private String methodName;             


  @Transient
  private PanelItem original;

  
  public Integer getId() {
    return id;
  }
  protected void setId(Integer id) {
    if((id == null && this.id != null) || 
       (id != null && !id.equals(this.id)))
      this.id = id;
  }

  public Integer getPanelId() {
    return panelId;
  }
  public void setPanelId(Integer panelId) {
    if((panelId == null && this.panelId != null) || 
       (panelId != null && !panelId.equals(this.panelId)))
      this.panelId = panelId;
  }

  public Integer getSortOrderId() {
    return sortOrderId;
  }
  public void setSortOrderId(Integer sortOrderId) {
    if((sortOrderId == null && this.sortOrderId != null) || 
       (sortOrderId != null && !sortOrderId.equals(this.sortOrderId)))
      this.sortOrderId = sortOrderId;
  }

  public String getTestName() {
    return testName;
  }
  public void setTestName(String testName) {
    if((testName == null && this.testName != null) || 
       (testName != null && !testName.equals(this.testName)))
      this.testName = testName;
  }

  public String getMethodName() {
    return methodName;
  }
  public void setMethodName(String methodName) {
    if((methodName == null && this.methodName != null) || 
       (methodName != null && !methodName.equals(this.methodName)))
      this.methodName = methodName;
  }

  
  public void setClone() {
    try {
      original = (PanelItem)this.clone();
    }catch(Exception e){}
  }
  
  public String getChangeXML() {
    try {
      Document doc = XMLUtil.createNew("change");
      Element root = doc.getDocumentElement();
      
      AuditUtil.getChangeXML(id,original.id,doc,"id");

      AuditUtil.getChangeXML(panelId,original.panelId,doc,"panel_id");

      AuditUtil.getChangeXML(sortOrderId,original.sortOrderId,doc,"sort_order_id");

      AuditUtil.getChangeXML(testName,original.testName,doc,"test_name");

      AuditUtil.getChangeXML(methodName,original.methodName,doc,"method_name");

      if(root.hasChildNodes())
        return XMLUtil.toString(doc);
    }catch(Exception e){
      e.printStackTrace();
    }
    return null;
  }
   
  public String getTableName() {
    return "panel_item";
  }
  
}   
