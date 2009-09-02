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
package org.openelis.domain;

import java.util.Date;

import org.openelis.gwt.common.Datetime;
import org.openelis.gwt.common.RPC;
import org.openelis.utilcommon.DataBaseUtil;

public class InstrumentLogDO implements RPC {

    private static final long serialVersionUID = 1L;

    protected Integer id;             
    protected Integer instrumentId;             
    protected Integer typeId;             
    protected Integer worksheetId;  
    protected Datetime eventBegin;             
    protected Datetime eventEnd;
    protected String text;
    private boolean delete;
    
    public InstrumentLogDO() {
     
    }
    
    public InstrumentLogDO(Integer id,Integer instrumentId,Integer typeId,
                           Integer worksheetId,Date eventBegin,Date eventEnd,
                           String text) {
        setId(id);
        setInstrumentId(instrumentId);
        setTypeId(typeId);
        setWorksheetId(worksheetId);
        setEventBegin(eventBegin);
        setEventEnd(eventEnd);      
        setText(text);
    }
    
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public Integer getInstrumentId() {
        return instrumentId;
    }
    public void setInstrumentId(Integer instrumentId) {
        this.instrumentId = instrumentId;
    }
    public Integer getTypeId() {
        return typeId;
    }
    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
    }
    public Integer getWorksheetId() {
        return worksheetId;
    }
    public void setWorksheetId(Integer worksheetId) {
        this.worksheetId = worksheetId;
    }
    public Datetime getEventBegin() {
        return eventBegin;
    }
    public void setEventBegin(Date eventBegin) {
        this.eventBegin = new Datetime(Datetime.YEAR, Datetime.MINUTE,eventBegin);
    }
    public Datetime getEventEnd() {
        return eventEnd;
    }
    public void setEventEnd(Date eventEnd) {
        this.eventEnd = new Datetime(Datetime.YEAR, Datetime.MINUTE,eventEnd);
    }
    public boolean getDelete() {
        return delete;
    }
    public void setDelete(boolean delete) {
        this.delete = delete;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = DataBaseUtil.trim(text);
    }
    
    
}
