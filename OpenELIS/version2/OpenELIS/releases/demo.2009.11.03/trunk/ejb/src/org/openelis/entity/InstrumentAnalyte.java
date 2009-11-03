
package org.openelis.entity;

/**
  * InstrumentAnalyte Entity POJO for database 
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
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import org.openelis.utils.AuditUtil;
import org.openelis.utils.Auditable;


@Entity
@Table(name="instrument_analyte")
@EntityListeners({AuditUtil.class})
public class InstrumentAnalyte implements Auditable, Cloneable {
  
  @Id
  @GeneratedValue
  @Column(name="id")
  private Integer id;             

  @Column(name="instrument_id")
  private Integer instrumentId;             

  @Column(name="analyte_id")
  private Integer analyteId;             

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "analyte_id",insertable = false, updatable = false)
  private Analyte analyte;

  @Transient
  private InstrumentAnalyte original;

  
  public Integer getId() {
    return id;
  }
  protected void setId(Integer id) {
    if((id == null && this.id != null) || 
       (id != null && !id.equals(this.id)))
      this.id = id;
  }

  public Integer getInstrumentId() {
    return instrumentId;
  }
  public void setInstrumentId(Integer instrumentId) {
    if((instrumentId == null && this.instrumentId != null) || 
       (instrumentId != null && !instrumentId.equals(this.instrumentId)))
      this.instrumentId = instrumentId;
  }

  public Integer getAnalyteId() {
    return analyteId;
  }
  public void setAnalyteId(Integer analyteId) {
    if((analyteId == null && this.analyteId != null) || 
       (analyteId != null && !analyteId.equals(this.analyteId)))
      this.analyteId = analyteId;
  }
  
  public Analyte getAnalyte() {
      return analyte;
  }
  public void setAnalyte(Analyte analyte) {
      this.analyte = analyte;
  }

  
  public void setClone() {
    try {
      original = (InstrumentAnalyte)this.clone();
    }catch(Exception e){}
  }
  
  public String getChangeXML() {
    try {
      Document doc = XMLUtil.createNew("change");
      Element root = doc.getDocumentElement();
      
      AuditUtil.getChangeXML(id,original.id,doc,"id");

      AuditUtil.getChangeXML(instrumentId,original.instrumentId,doc,"instrument_id");

      AuditUtil.getChangeXML(analyteId,original.analyteId,doc,"analyte_id");

      if(root.hasChildNodes())
        return XMLUtil.toString(doc);
    }catch(Exception e){
      e.printStackTrace();
    }
    return null;
  }
   
  public String getTableName() {
    return "instrument_analyte";
  }
  
}   
