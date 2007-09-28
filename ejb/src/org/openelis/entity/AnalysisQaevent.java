
package org.openelis.entity;

/**
  * AnalysisQaevent Entity POJO for database 
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
@Table(name="analysis_qaevent")
@EntityListeners({AuditUtil.class})
public class AnalysisQaevent implements Auditable, Cloneable {
  
  @Id
  @GeneratedValue
  @Column(name="id")
  private Integer id;             

  @Column(name="analysis")
  private Integer analysis;             

  @Column(name="qaevent")
  private Integer qaevent;             


  @Transient
  private AnalysisQaevent original;

  
  public Integer getId() {
    return id;
  }
  protected void setId(Integer id) {
    this.id = id;
  }

  public Integer getAnalysis() {
    return analysis;
  }
  public void setAnalysis(Integer analysis) {
    this.analysis = analysis;
  }

  public Integer getQaevent() {
    return qaevent;
  }
  public void setQaevent(Integer qaevent) {
    this.qaevent = qaevent;
  }

  
  public void setClone() {
    try {
      original = (AnalysisQaevent)this.clone();
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

      if((qaevent == null && original.qaevent != null) || 
         (qaevent != null && !qaevent.equals(original.qaevent))){
        Element elem = doc.createElement("qaevent");
        elem.appendChild(doc.createTextNode(original.qaevent.toString()));
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
    return "analysis_qaevent";
  }
  
}   
