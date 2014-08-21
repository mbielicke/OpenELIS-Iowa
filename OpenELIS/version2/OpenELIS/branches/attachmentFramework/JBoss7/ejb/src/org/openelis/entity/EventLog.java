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
 * Event Log Entity POJO for database
 */

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.Datetime;

@NamedQueries({
    @NamedQuery( name = "EventLog.FetchById",
                query = "select new org.openelis.domain.EventLogDO(el.id, el.typeId, el.source, el.referenceTableId," +
                		"el.referenceId, el.levelId, el.systemUserId, el.timeStamp, el.text)"
                      + " from EventLog el where el.id = :id"),
    @NamedQuery( name = "EventLog.FetchByRefTableIdRefId",
                query = "select new org.openelis.domain.EventLogDO(el.id, el.typeId, el.source, el.referenceTableId," +
                        "el.referenceId, el.levelId, el.systemUserId, el.timeStamp, el.text)"
                      + " from EventLog el where el.referenceTableId = :refTableId and el.referenceId = :refId order by el.timeStamp DESC")})
                      
@Entity
@Table(name = "event_log")
public class EventLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer          id;
    
    @Column(name = "type_id")
    private Integer          typeId;
    
    @Column(name = "source")        
    private String           source;
    
    @Column(name = "reference_table_id")
    private Integer          referenceTableId;
    
    @Column(name = "reference_id")
    private Integer          referenceId;
    
    @Column(name = "level_id")
    private Integer          levelId;
    
    @Column(name = "system_user_id")
    private Integer          systemUserId;
    
    @Column(name = "timestamp")
    private Date             timeStamp;
    
    @Column(name = "text")
    private String           text;
    
    public Integer getId() {
        return id;
    }

    protected void setId(Integer id) {
        if (DataBaseUtil.isDifferent(id, this.id))
            this.id = id;
    }
    
    public Integer getTypeId() {
        return typeId;
    }

    public void setTypeId(Integer typeId) {
        if (DataBaseUtil.isDifferent(typeId, this.typeId))
            this.typeId = typeId;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        if (DataBaseUtil.isDifferent(source, this.source))
            this.source = source;
    }

    public Integer getReferenceTableId() {
        return referenceTableId;
    }

    public void setReferenceTableId(Integer referenceTableId) {
        if (DataBaseUtil.isDifferent(referenceTableId, this.referenceTableId))
            this.referenceTableId = referenceTableId;
    }

    public Integer getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(Integer referenceId) {
        if (DataBaseUtil.isDifferent(referenceId, this.referenceId))
            this.referenceId = referenceId;
    }

    public Integer getLevelId() {
        return levelId;
    }

    public void setLevelId(Integer levelId) {
        if (DataBaseUtil.isDifferent(levelId, this.levelId))
            this.levelId = levelId;
    }

    public Integer getSystemUserId() {
        return systemUserId;
    }

    public void setSystemUserId(Integer systemUserId) {
        if (DataBaseUtil.isDifferent(systemUserId, this.systemUserId))
            this.systemUserId = systemUserId;
    }

    public Datetime getTimeStamp() {
        return DataBaseUtil.toYM(timeStamp);
    }

    public void setTimeStamp(Datetime timeStamp) {
        if (DataBaseUtil.isDifferentYM(timeStamp, this.timeStamp))
            this.timeStamp = timeStamp.getDate();
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        if (DataBaseUtil.isDifferent(text, this.text))
            this.text = text;
    }
}