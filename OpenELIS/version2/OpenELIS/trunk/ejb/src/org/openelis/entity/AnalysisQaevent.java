
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
import org.openelis.utils.Auditable;

@Entity
@Table(name="analysis_qaevent")
@EntityListeners({AuditUtil.class})
public class AnalysisQaevent implements Auditable, Cloneable {
  
  @Id
  @GeneratedValue
  @Column(name="id")
  private Integer id;             

  @Column(name="analysis_id")
  private Integer analysisId;             

  @Column(name="qaevent_id")
  private Integer qaeventId;             


  @Transient
  private AnalysisQaevent original;

  
  public Integer getId() {
    return id;
  }
  protected void setId(Integer id) {
    if((id == null && this.id != null) || 
       (id != null && !id.equals(this.id)))
      this.id = id;
  }

  public Integer getAnalysisId() {
    return analysisId;
  }
  public void setAnalysisId(Integer analysisId) {
    if((analysisId == null && this.analysisId != null) || 
       (analysisId != null && !analysisId.equals(this.analysisId)))
      this.analysisId = analysisId;
  }

  public Integer getQaeventId() {
    return qaeventId;
  }
  public void setQaeventId(Integer qaeventId) {
    if((qaeventId == null && this.qaeventId != null) || 
       (qaeventId != null && !qaeventId.equals(this.qaeventId)))
      this.qaeventId = qaeventId;
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
      
      AuditUtil.getChangeXML(id,original.id,doc,"id");

      AuditUtil.getChangeXML(analysisId,original.analysisId,doc,"analysis_id");

      AuditUtil.getChangeXML(qaeventId,original.qaeventId,doc,"qaevent_id");

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
