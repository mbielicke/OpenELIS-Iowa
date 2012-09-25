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

import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.Datetime;

/**
 * Class represents the fields in database table event_log.
 */

public class EventLogDO extends DataObject {

    private static final long serialVersionUID = 1L;

    private Integer           id, typeId, referenceTableId, referenceId, levelId,
                              systemUserId;
    private String            source, text;
    private Datetime          timeStamp;
    
    public EventLogDO() {
    }
    
    public EventLogDO(Integer id, Integer typeId, String source, Integer referenceTableId,
                      Integer referenceId, Integer levelId, Integer systemUserId, 
                      Date timeStamp, String text) {
        setId(id);
        setTypeId(typeId); 
        setSource(source);
        setReferenceTableId(referenceTableId);
        setReferenceId(referenceId);
        setLevelId(levelId);
        setSystemUserId(systemUserId); 
        setTimeStamp(DataBaseUtil.toYM(timeStamp));
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

    public Integer getTypeId() {
        return typeId;
    }

    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
        _changed = true;
    }

    public Integer getReferenceTableId() {
        return referenceTableId;
    }

    public void setReferenceTableId(Integer referenceTableId) {
        this.referenceTableId = referenceTableId;
        _changed = true;
    }

    public Integer getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(Integer referenceId) {
        this.referenceId = referenceId;
        _changed = true;
    }

    public Integer getLevelId() {
        return levelId;
    }

    public void setLevelId(Integer levelId) {
        this.levelId = levelId;
        _changed = true;
    }

    public Integer getSystemUserId() {
        return systemUserId;
    }

    public void setSystemUserId(Integer systemUserId) {
        this.systemUserId = systemUserId;
        _changed = true;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = DataBaseUtil.trim(source);
        _changed = true;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = DataBaseUtil.trim(text);
        _changed = true;
    }

    public Datetime getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Datetime timeStamp) {
        this.timeStamp = DataBaseUtil.toYM(timeStamp);
        _changed = true;
    }
    
}
