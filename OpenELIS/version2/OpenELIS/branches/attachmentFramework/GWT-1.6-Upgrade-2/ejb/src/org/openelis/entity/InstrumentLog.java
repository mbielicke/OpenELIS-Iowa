/** Exhibit A - UIRF Open-source Based Public Software License.
* 
* The contents of this file are subject to the UIRF Open-source Based
* Public Software License(the "License"); you may not use this file except
* in compliance with the License. You may obtain a copy of the License at
* openelis.uhl.uiowa.edu
* 
* Software distributed under the License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
* License for the specific language governing rights and limitations
* under the License.
* 
* The Original Code is OpenELIS code.
* 
* The Initial Developer of the Original Code is The University of Iowa.
* Portions created by The University of Iowa are Copyright 2006-2008. All
* Rights Reserved.
* 
* Contributor(s): ______________________________________.
* 
* Alternatively, the contents of this file marked
* "Separately-Licensed" may be used under the terms of a UIRF Software
* license ("UIRF Software License"), in which case the provisions of a
* UIRF Software License are applicable instead of those above. 
*/
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

  @Column(name="instrument_id")
  private Integer instrumentId;             

  @Column(name="type_id")
  private Integer typeId;             

  @Column(name="worksheet_id")
  private Integer worksheetId;             

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

  public Integer getInstrumentId() {
    return instrumentId;
  }
  public void setInstrumentId(Integer instrumentId) {
    if((instrumentId == null && this.instrumentId != null) || 
       (instrumentId != null && !instrumentId.equals(this.instrumentId)))
      this.instrumentId = instrumentId;
  }

  public Integer getTypeId() {
    return typeId;
  }
  public void setTypeId(Integer typeId) {
    if((typeId == null && this.typeId != null) || 
       (typeId != null && !typeId.equals(this.typeId)))
      this.typeId = typeId;
  }

  public Integer getWorksheetId() {
    return worksheetId;
  }
  public void setWorksheetId(Integer worksheetId) {
    if((worksheetId == null && this.worksheetId != null) || 
       (worksheetId != null && !worksheetId.equals(this.worksheetId)))
      this.worksheetId = worksheetId;
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
      
      AuditUtil.getChangeXML(id,original.id,doc,"id");

      AuditUtil.getChangeXML(instrumentId,original.instrumentId,doc,"instrument_id");

      AuditUtil.getChangeXML(typeId,original.typeId,doc,"type_id");

      AuditUtil.getChangeXML(worksheetId,original.worksheetId,doc,"worksheet_id");

      AuditUtil.getChangeXML(eventBegin,original.eventBegin,doc,"event_begin");

      AuditUtil.getChangeXML(eventEnd,original.eventEnd,doc,"event_end");

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
