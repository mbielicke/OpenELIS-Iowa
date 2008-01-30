
package org.openelis.entity;

/**
  * InstrumentLog Entity POJO for database 
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
@Table(name="instrument_log")
@EntityListeners({AuditUtil.class})
public class InstrumentLog implements Auditable, Cloneable {
  
  @Id
  @GeneratedValue
  @Column(name="id")
  private Integer id;             

  @Column(name="instrument")
  private Integer instrument;             

  @Column(name="type")
  private Integer type;             

  @Column(name="worksheet")
  private Integer worksheet;             

  @Column(name="event_begin")
  private Date eventBegin;             

  @Column(name="event_end")
  private Date eventEnd;             


  @Transient
  private InstrumentLog original;

  
  public Integer getId() {
    return id;
  }
  protected void setId(Integer id) {
    if((id == null && this.id != null) || 
       (id != null && !id.equals(this.id)))
      this.id = id;
  }

  public Integer getInstrument() {
    return instrument;
  }
  public void setInstrument(Integer instrument) {
    if((instrument == null && this.instrument != null) || 
       (instrument != null && !instrument.equals(this.instrument)))
      this.instrument = instrument;
  }

  public Integer getType() {
    return type;
  }
  public void setType(Integer type) {
    if((type == null && this.type != null) || 
       (type != null && !type.equals(this.type)))
      this.type = type;
  }

  public Integer getWorksheet() {
    return worksheet;
  }
  public void setWorksheet(Integer worksheet) {
    if((worksheet == null && this.worksheet != null) || 
       (worksheet != null && !worksheet.equals(this.worksheet)))
      this.worksheet = worksheet;
  }

  public Datetime getEventBegin() {
    if(eventBegin == null)
      return null;
    return new Datetime(Datetime.YEAR,Datetime.SECOND,eventBegin);
  }
  public void setEventBegin (Datetime event_begin){
    if((eventBegin == null && this.eventBegin != null) || 
       (eventBegin != null && !eventBegin.equals(this.eventBegin)))
      this.eventBegin = event_begin.getDate();
  }

  public Datetime getEventEnd() {
    if(eventEnd == null)
      return null;
    return new Datetime(Datetime.YEAR,Datetime.SECOND,eventEnd);
  }
  public void setEventEnd (Datetime event_end){
    if((eventEnd == null && this.eventEnd != null) || 
       (eventEnd != null && !eventEnd.equals(this.eventEnd)))
      this.eventEnd = event_end.getDate();
  }

  
  public void setClone() {
    try {
      original = (InstrumentLog)this.clone();
    }catch(Exception e){}
  }
  
  public String getChangeXML() {
    try {
      Document doc = XMLUtil.createNew("change");
      Element root = doc.getDocumentElement();
      
      if((id == null && original.id != null) || 
         (id != null && !id.equals(original.id))){
        Element elem = doc.createElement("id");
        elem.appendChild(doc.createTextNode(original.id.toString().trim()));
        root.appendChild(elem);
      }      

      if((instrument == null && original.instrument != null) || 
         (instrument != null && !instrument.equals(original.instrument))){
        Element elem = doc.createElement("instrument");
        elem.appendChild(doc.createTextNode(original.instrument.toString().trim()));
        root.appendChild(elem);
      }      

      if((type == null && original.type != null) || 
         (type != null && !type.equals(original.type))){
        Element elem = doc.createElement("type");
        elem.appendChild(doc.createTextNode(original.type.toString().trim()));
        root.appendChild(elem);
      }      

      if((worksheet == null && original.worksheet != null) || 
         (worksheet != null && !worksheet.equals(original.worksheet))){
        Element elem = doc.createElement("worksheet");
        elem.appendChild(doc.createTextNode(original.worksheet.toString().trim()));
        root.appendChild(elem);
      }      

      if((eventBegin == null && original.eventBegin != null) || 
         (eventBegin != null && !eventBegin.equals(original.eventBegin))){
        Element elem = doc.createElement("event_begin");
        elem.appendChild(doc.createTextNode(original.eventBegin.toString().trim()));
        root.appendChild(elem);
      }      

      if((eventEnd == null && original.eventEnd != null) || 
         (eventEnd != null && !eventEnd.equals(original.eventEnd))){
        Element elem = doc.createElement("event_end");
        elem.appendChild(doc.createTextNode(original.eventEnd.toString().trim()));
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
    return "instrument_log";
  }
  
}   
