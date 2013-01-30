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

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.openelis.domain.Constants;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.Datetime;
import org.openelis.utils.Audit;
import org.openelis.utils.AuditUtil;
import org.openelis.utils.Auditable;

@NamedQueries( {
    @NamedQuery( name = "InstrumentLog.FetchByInstrumentId",
                query = "select new org.openelis.domain.InstrumentLogDO(il.id,il.instrumentId,il.typeId," + 
                        "il.worksheetId,il.eventBegin,il.eventEnd,il.text)"
                      + " from InstrumentLog il where il.instrumentId = :id order by il.eventBegin desc"),
    @NamedQuery( name = "InstrumentLog.FetchByInstrumentIdWorksheetId",
                query = "select new org.openelis.domain.InstrumentLogDO(il.id,il.instrumentId,il.typeId," + 
                        "il.worksheetId,il.eventBegin,il.eventEnd,il.text)"
                      + " from InstrumentLog il where il.instrumentId = :id and il.worksheetId = :wId")})
@Entity
@Table(name = "instrument_log")
@EntityListeners({AuditUtil.class})
public class InstrumentLog implements Auditable, Cloneable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
        if (DataBaseUtil.isDifferent(id, this.id))
            this.id = id;
    }

    public Integer getInstrumentId() {
        return instrumentId;
    }

    public void setInstrumentId(Integer instrumentId) {
        if (DataBaseUtil.isDifferent(instrumentId, this.instrumentId))
            this.instrumentId = instrumentId;
    }

    public Integer getTypeId() {
        return typeId;
    }

    public void setTypeId(Integer typeId) {
        if (DataBaseUtil.isDifferent(typeId, this.typeId))
            this.typeId = typeId;
    }

    public Integer getWorksheetId() {
        return worksheetId;
    }

    public void setWorksheetId(Integer worksheetId) {
        if (DataBaseUtil.isDifferent(worksheetId, this.worksheetId))
            this.worksheetId = worksheetId;
    }

    public Datetime getEventBegin() {
        return DataBaseUtil.toYS(eventBegin);
    }

    public void setEventBegin(Datetime event_begin) {
        if (DataBaseUtil.isDifferentYM(event_begin, this.eventBegin))
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
        if (DataBaseUtil.isDifferent(text, this.text))
            this.text = text;
    }

    public void setClone() {
        try {
            original = (InstrumentLog)this.clone();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Audit getAudit(Integer activity) {
        Audit audit;

        audit = new Audit(activity);
        audit.setReferenceTableId(Constants.table().INSTRUMENT_LOG);
        audit.setReferenceId(getId());
        if (original != null)
            audit.setField("id", id, original.id)
                 .setField("instrument_id", instrumentId, original.instrumentId)
                 .setField("type_id", typeId, original.typeId)
                 .setField("worksheet_id", worksheetId, original.worksheetId)
                 .setField("event_begin", eventBegin, original.eventBegin)
                 .setField("event_end", eventEnd, original.eventEnd)
                 .setField("text", text, original.text);

        return audit;
    }
}
