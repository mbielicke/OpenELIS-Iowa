
package org.openelis.entity;

/**
  * AnalysisUser Entity POJO for database 
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
@Table(name="analysis_user")
@EntityListeners({AuditUtil.class})
public class AnalysisUser implements Auditable, Cloneable {
  
  @Id
  @GeneratedValue
  @Column(name="id")
  private Integer id;             

  @Column(name="analysis")
  private Integer analysis;             

  @Column(name="system_user")
  private Integer systemUser;             

  @Column(name="action")
  private Integer action;             


  @Transient
  private AnalysisUser original;

  
  public Integer getId() {
    return id;
  }
  protected void setId(Integer id) {
    if((id == null && this.id != null) || 
       (id != null && !id.equals(this.id)))
      this.id = id;
  }

  public Integer getAnalysis() {
    return analysis;
  }
  public void setAnalysis(Integer analysis) {
    if((analysis == null && this.analysis != null) || 
       (analysis != null && !analysis.equals(this.analysis)))
      this.analysis = analysis;
  }

  public Integer getSystemUser() {
    return systemUser;
  }
  public void setSystemUser(Integer systemUser) {
    if((systemUser == null && this.systemUser != null) || 
       (systemUser != null && !systemUser.equals(this.systemUser)))
      this.systemUser = systemUser;
  }

  public Integer getAction() {
    return action;
  }
  public void setAction(Integer action) {
    if((action == null && this.action != null) || 
       (action != null && !action.equals(this.action)))
      this.action = action;
  }

  
  public void setClone() {
    try {
      original = (AnalysisUser)this.clone();
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

      if((analysis == null && original.analysis != null) || 
         (analysis != null && !analysis.equals(original.analysis))){
        Element elem = doc.createElement("analysis");
        elem.appendChild(doc.createTextNode(original.analysis.toString()));
        root.appendChild(elem);
      }      

      if((systemUser == null && original.systemUser != null) || 
         (systemUser != null && !systemUser.equals(original.systemUser))){
        Element elem = doc.createElement("system_user");
        elem.appendChild(doc.createTextNode(original.systemUser.toString()));
        root.appendChild(elem);
      }      

      if((action == null && original.action != null) || 
         (action != null && !action.equals(original.action))){
        Element elem = doc.createElement("action");
        elem.appendChild(doc.createTextNode(original.action.toString()));
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
    return "analysis_user";
  }
  
}   
