/**
 * Exhibit A - UIRF Open-source Based Public Software License.
 * 
 * The contents of this file are subject to the UIRF Open-source Based Public
 * Software License(the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * openelis.uhl.uiowa.edu
 * 
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 * 
 * The Original Code is OpenELIS code.
 * 
 * The Initial Developer of the Original Code is The University of Iowa.
 * Portions created by The University of Iowa are Copyright 2006-2008. All
 * Rights Reserved.
 * 
 * Contributor(s): ______________________________________.
 * 
 * Alternatively, the contents of this file marked "Separately-Licensed" may be
 * used under the terms of a UIRF Software license ("UIRF Software License"), in
 * which case the provisions of a UIRF Software License are applicable instead
 * of those above.
 */
package org.openelis.entity;

/**
 * InstrumentLog Entity POJO for database
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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.openelis.utilcommon.DataBaseUtil;
import org.openelis.utils.AuditUtil;
import org.openelis.utils.Auditable;

@NamedQueries( {
    @NamedQuery(name = "InstrumentLog.FetchByInstrumentId",
               query = "select new org.openelis.domain.InstrumentLogDO(il.id,il.instrumentId,il.typeId, " + 
                       "il.worksheetId,il.eventBegin,il.eventEnd,il.text)"
                     + " from InstrumentLog il where il.instrumentId = :id order by il.eventBegin desc")})
@Entity 
@Table(name = "instrument_log")
@EntityListeners( {AuditUtil.class})
public class InstrumentLog implements Auditable, Cloneable {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Integer       id;

    @Column(name = "instrument_id")
    private Integer       instrumentId;

    @Column(name = "type_id")
    private Integer       typeId;

    @Column(name = "worksheet_id")
    private Integer       worksheetId;

    @Column(name = "event_begin")
    private Date          eventBegin;

    @Column(name = "event_end")
    private Date          eventEnd;

    @Column(name = "text")
    private String        text;

    @Transient
    private InstrumentLog original;

    public Integer getId() {
        return id;
    }

    protected void setId(Integer id) {
        if (DataBaseUtil.isDifferent(id,this.id))
            this.id = id;
    }

    public Integer getInstrumentId() {
        return instrumentId;
    }

    public void setInstrumentId(Integer instrumentId) {
        if (DataBaseUtil.isDifferent(instrumentId,this.instrumentId))
            this.instrumentId = instrumentId;
    }

    public Integer getTypeId() {
        return typeId;
    }

    public void setTypeId(Integer typeId) {
        if (DataBaseUtil.isDifferent(typeId,this.typeId))
            this.typeId = typeId;
    }

    public Integer getWorksheetId() {
        return worksheetId;
    }

    public void setWorksheetId(Integer worksheetId) {
        if (DataBaseUtil.isDifferent(worksheetId,this.worksheetId))
            this.worksheetId = worksheetId;
    }

    public Datetime getEventBegin() {
        return DataBaseUtil.toYS(eventBegin);
    }

    public void setEventBegin(Datetime event_begin) {
        if (DataBaseUtil.isDifferentYM(event_begin,this.eventBegin)) 
            this.eventBegin = DataBaseUtil.toDate(event_begin);        
    }

    public Datetime getEventEnd() {
        return DataBaseUtil.toYS(eventEnd);
    }

    public void setEventEnd(Datetime event_end) {
        if (DataBaseUtil.isDifferentYM(event_end, this.eventEnd))             
            this.eventEnd = DataBaseUtil.toDate(event_end);        
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        if (DataBaseUtil.isDifferent(text,this.text))
            this.text = text;
    }

    public void setClone() {
        try {
            original = (InstrumentLog)this.clone();
        } catch (Exception e) {
        }
    }

    public String getChangeXML() {
        try {
            Document doc = XMLUtil.createNew("change");
            Element root = doc.getDocumentElement();

            AuditUtil.getChangeXML(id, original.id, doc, "id");
            AuditUtil.getChangeXML(instrumentId, original.instrumentId, doc, "instrument_id");
            AuditUtil.getChangeXML(typeId, original.typeId, doc, "type_id");
            AuditUtil.getChangeXML(worksheetId, original.worksheetId, doc, "worksheet_id");
            AuditUtil.getChangeXML(eventBegin, original.eventBegin, doc, "event_begin");
            AuditUtil.getChangeXML(eventEnd, original.eventEnd, doc, "event_end");
            AuditUtil.getChangeXML(text, original.text, doc, "text");

            if (root.hasChildNodes())
                return XMLUtil.toString(doc);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getTableName() {
        return "instrument_log";
    }

}
