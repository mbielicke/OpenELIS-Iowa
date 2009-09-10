
package org.openelis.entity;

/**
  * SampleQaevent Entity POJO for database 
  */

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.openelis.gwt.common.Datetime;
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
@Table(name="sample_qaevent")
@EntityListeners({AuditUtil.class})
public class SampleQaevent implements Auditable, Cloneable {
  
  @Id
  @GeneratedValue
  @Column(name="id")
  private Integer id;             

  @Column(name="sample_id")
  private Integer sampleId;             

  @Column(name="qaevent_id")
  private Integer qaeventId;             

  @Column(name="type_id")
  private Integer typeId;             

  @Column(name="is_billable")
  private String isBillable;             


  @Transient
  private SampleQaevent original;

  
  public Integer getId() {
    return id;
  }
  protected void setId(Integer id) {
    if((id == null && this.id != null) || 
       (id != null && !id.equals(this.id)))
      this.id = id;
  }

  public Integer getSampleId() {
    return sampleId;
  }
  public void setSampleId(Integer sampleId) {
    if((sampleId == null && this.sampleId != null) || 
       (sampleId != null && !sampleId.equals(this.sampleId)))
      this.sampleId = sampleId;
  }

  public Integer getQaeventId() {
    return qaeventId;
  }
  public void setQaeventId(Integer qaeventId) {
    if((qaeventId == null && this.qaeventId != null) || 
       (qaeventId != null && !qaeventId.equals(this.qaeventId)))
      this.qaeventId = qaeventId;
  }

  public Integer getTypeId() {
    return typeId;
  }
  public void setTypeId(Integer typeId) {
    if((typeId == null && this.typeId != null) || 
       (typeId != null && !typeId.equals(this.typeId)))
      this.typeId = typeId;
  }

  public String getIsBillable() {
    return isBillable;
  }
  public void setIsBillable(String isBillable) {
    if((isBillable == null && this.isBillable != null) || 
       (isBillable != null && !isBillable.equals(this.isBillable)))
      this.isBillable = isBillable;
  }

  
  public void setClone() {
    try {
      original = (SampleQaevent)this.clone();
    }catch(Exception e){}
  }
  
  public String getChangeXML() {
    try {
      Document doc = XMLUtil.createNew("change");
      Element root = doc.getDocumentElement();
      
      AuditUtil.getChangeXML(id,original.id,doc,"id");

      AuditUtil.getChangeXML(sampleId,original.sampleId,doc,"sample_id");

      AuditUtil.getChangeXML(qaeventId,original.qaeventId,doc,"qaevent_id");

      AuditUtil.getChangeXML(typeId,original.typeId,doc,"type_id");

      AuditUtil.getChangeXML(isBillable,original.isBillable,doc,"is_billable");

      if(root.hasChildNodes())
        return XMLUtil.toString(doc);
    }catch(Exception e){
      e.printStackTrace();
    }
    return null;
  }
   
  public String getTableName() {
    return "sample_qaevent";
  }
  
}   
