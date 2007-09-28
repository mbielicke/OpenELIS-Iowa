
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
import org.openelis.interfaces.Auditable;

@Entity
@Table(name="panel_item")
@EntityListeners({AuditUtil.class})
public class PanelItem implements Auditable, Cloneable {
  
  @Id
  @GeneratedValue
  @Column(name="id")
  private Integer id;             

  @Column(name="panel")
  private Integer panel;             

  @Column(name="sort_order")
  private Integer sortOrder;             

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
    this.id = id;
  }

  public Integer getPanel() {
    return panel;
  }
  public void setPanel(Integer panel) {
    this.panel = panel;
  }

  public Integer getSortOrder() {
    return sortOrder;
  }
  public void setSortOrder(Integer sortOrder) {
    this.sortOrder = sortOrder;
  }

  public String getTestName() {
    return testName;
  }
  public void setTestName(String testName) {
    this.testName = testName;
  }

  public String getMethodName() {
    return methodName;
  }
  public void setMethodName(String methodName) {
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
      
      if((id == null && original.id != null) || 
         (id != null && !id.equals(original.id))){
        Element elem = doc.createElement("id");
        elem.appendChild(doc.createTextNode(original.id.toString()));
        root.appendChild(elem);
      }      

      if((panel == null && original.panel != null) || 
         (panel != null && !panel.equals(original.panel))){
        Element elem = doc.createElement("panel");
        elem.appendChild(doc.createTextNode(original.panel.toString()));
        root.appendChild(elem);
      }      

      if((sortOrder == null && original.sortOrder != null) || 
         (sortOrder != null && !sortOrder.equals(original.sortOrder))){
        Element elem = doc.createElement("sort_order");
        elem.appendChild(doc.createTextNode(original.sortOrder.toString()));
        root.appendChild(elem);
      }      

      if((testName == null && original.testName != null) || 
         (testName != null && !testName.equals(original.testName))){
        Element elem = doc.createElement("test_name");
        elem.appendChild(doc.createTextNode(original.testName.toString()));
        root.appendChild(elem);
      }      

      if((methodName == null && original.methodName != null) || 
         (methodName != null && !methodName.equals(original.methodName))){
        Element elem = doc.createElement("method_name");
        elem.appendChild(doc.createTextNode(original.methodName.toString()));
        root.appendChild(elem);
      }      

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
