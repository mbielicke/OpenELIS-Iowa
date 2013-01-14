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
 * Class represents the fields in database table note.
 */

public class NoteDO extends DataObject {

    private static final long serialVersionUID = 1L;

    protected Integer         id, referenceId, referenceTableId, systemUserId;
    protected Datetime        timestamp;
    protected String          isExternal, subject, text;

    public NoteDO() {
    }

    public NoteDO(Integer id, Integer referenceId, Integer referenceTable, Date timestamp,
                  String isExternal, Integer systemUserId, String subject, String text) {
        setId(id);
        setReferenceId(referenceId);
        setReferenceTableId(referenceTable);
        setTimestamp(DataBaseUtil.toYM(timestamp));
        setIsExternal(isExternal);
        setSystemUserId(systemUserId);
        setSubject(subject);
        setText(text);
        _changed = false;
    }

    public NoteDO(Integer id, Integer systemUser, String text, Date timestamp, String subject) {
        setId(id);
        setTimestamp(DataBaseUtil.toYM(timestamp));
        setSystemUserId(systemUser);
        setSubject(subject);
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

    public Integer getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(Integer referenceId) {
        this.referenceId = referenceId;
        _changed = true;
    }

    public Integer getReferenceTableId() {
        return referenceTableId;
    }

    public void setReferenceTableId(Integer referenceTableId) {
        this.referenceTableId = referenceTableId;
        _changed = true;
    }

    public Datetime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Datetime timestamp) {
        this.timestamp = timestamp;
    }

    public String getIsExternal() {
        return isExternal;
    }

    public void setIsExternal(String isExternal) {
        this.isExternal = DataBaseUtil.trim(isExternal);
        _changed = true;
    }

    public Integer getSystemUserId() {
        return systemUserId;
    }

    public void setSystemUserId(Integer systemUser) {
        this.systemUserId = systemUser;
        _changed = true;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = DataBaseUtil.trim(subject);
        _changed = true;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = DataBaseUtil.trim(text);
        _changed = true;
    }

    public void copy(NoteDO noteDO) {
        id = noteDO.getId();
        referenceId = noteDO.getReferenceId();
        referenceTableId = noteDO.getReferenceTableId();
        isExternal = noteDO.getIsExternal();
        subject = noteDO.getSubject();
        systemUserId = noteDO.getSystemUserId();
        text = noteDO.getText();
        timestamp = noteDO.getTimestamp();
        _changed = noteDO.isChanged();
    }
}
