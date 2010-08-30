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
package org.openelis.domain;

import java.util.Date;

import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.Datetime;

/**
 * Class represents the fields in database table InsturmentLog.
 */

public class InstrumentLogDO extends DataObject {

    private static final long serialVersionUID = 1L;

    protected Integer         id, instrumentId, typeId, worksheetId;
    protected Datetime        eventBegin, eventEnd;
    protected String          text;

    public InstrumentLogDO() {
    }

    public InstrumentLogDO(Integer id, Integer instrumentId, Integer typeId, Integer worksheetId,
                           Date eventBegin, Date eventEnd, String text) {
        setId(id);
        setInstrumentId(instrumentId);
        setTypeId(typeId);
        setWorksheetId(worksheetId);
        setEventBegin(DataBaseUtil.toYM(eventBegin));
        setEventEnd(DataBaseUtil.toYM(eventEnd));
        setText(text);
        _changed = false;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
        _changed = true;
    }

    public Integer getInstrumentId() {
        return instrumentId;
    }

    public void setInstrumentId(Integer instrumentId) {
        this.instrumentId = instrumentId;
        _changed = true;
    }

    public Integer getTypeId() {
        return typeId;
    }

    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
        _changed = true;
    }

    public Integer getWorksheetId() {
        return worksheetId;
    }

    public void setWorksheetId(Integer worksheetId) {
        this.worksheetId = worksheetId;
        _changed = true;
    }

    public Datetime getEventBegin() {
        return eventBegin;
    }

    public void setEventBegin(Datetime eventBegin) {
        this.eventBegin = DataBaseUtil.toYM(eventBegin);
        _changed = true;
    }

    public Datetime getEventEnd() {
        return eventEnd;
    }

    public void setEventEnd(Datetime eventEnd) {
        this.eventEnd = DataBaseUtil.toYM(eventEnd);
        _changed = true;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = DataBaseUtil.trim(text);
        _changed = true;
    }
}
