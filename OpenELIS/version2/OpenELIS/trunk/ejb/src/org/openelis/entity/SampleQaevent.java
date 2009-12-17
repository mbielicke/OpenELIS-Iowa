
package org.openelis.entity;

/**
  * SampleQaevent Entity POJO for database 
  */

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.openelis.util.XMLUtil;
import org.openelis.utilcommon.DataBaseUtil;
import org.openelis.utils.AuditUtil;
import org.openelis.utils.Auditable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

@NamedQueries( {
    @NamedQuery(name = "SampleQaevent.SampleQaeventBySampleId", query = "select new org.openelis.domain.SampleQaEventViewDO(q.id, q.sampleId, q.qaeventId, " + 
                " q.typeId, q.isBillable, q.qaEvent.name) from SampleQaevent q where q.sampleId = :id order by q.id")})
                
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
  
  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "qaevent_id", insertable = false, updatable = false)
  private QaEvent qaEvent;

  @Transient
  private SampleQaevent original;

  
  public Integer getId() {
    return id;
  }
  protected void setId(Integer id) {
    if(DataBaseUtil.isDifferent(id, this.id))
      this.id = id;
  }

  public Integer getSampleId() {
    return sampleId;
  }
  public void setSampleId(Integer sampleId) {
    if(DataBaseUtil.isDifferent(sampleId, this.sampleId))
      this.sampleId = sampleId;
  }

  public Integer getQaeventId() {
    return qaeventId;
  }
  public void setQaeventId(Integer qaeventId) {
    if(DataBaseUtil.isDifferent(qaeventId, this.qaeventId))
      this.qaeventId = qaeventId;
  }

  public Integer getTypeId() {
    return typeId;
  }
  public void setTypeId(Integer typeId) {
    if(DataBaseUtil.isDifferent(typeId, this.typeId))
      this.typeId = typeId;
  }

  public String getIsBillable() {
    return isBillable;
  }
  public void setIsBillable(String isBillable) {
    if(DataBaseUtil.isDifferent(isBillable, this.isBillable))
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
public QaEvent getQaEvent() {
    return qaEvent;
}
public void setQaEvent(QaEvent qaEvent) {
    this.qaEvent = qaEvent;
}
}   
