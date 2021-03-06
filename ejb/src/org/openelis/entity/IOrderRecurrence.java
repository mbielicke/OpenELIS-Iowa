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
 * Order Recurrence Entity POJO for database
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
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.Datetime;
import org.openelis.utils.Audit;
import org.openelis.utils.AuditUtil;
import org.openelis.utils.Auditable;

@NamedQueries({
    @NamedQuery( name = "IOrderRecurrence.FetchByIorderId",
                query = "select new org.openelis.domain.IOrderRecurrenceDO(r.id, r.iorderId,r.isActive," +
                		"r.activeBegin, r.activeEnd, r.frequency, r.unitId)"
                      + " from IOrderRecurrence r where r.iorderId = :orderId"),
    @NamedQuery( name = "IOrderRecurrence.FetchByIorderIds",
                query = "select new org.openelis.domain.IOrderRecurrenceDO(r.id, r.iorderId,r.isActive," +
                        "r.activeBegin, r.activeEnd, r.frequency, r.unitId)"
                      + " from IOrderRecurrence r where r.iorderId in ( :orderIds )"),
    @NamedQuery( name = "IOrderRecurrence.FetchActiveList",
                query = "select new org.openelis.domain.IOrderRecurrenceDO(r.id, r.iorderId,r.isActive," +
                        "r.activeBegin, r.activeEnd, r.frequency, r.unitId)"
                      + " from IOrderRecurrence r where r.isActive = 'Y' and r.activeEnd >= :today")})
        
@Entity
@Table(name = "iorder_recurrence")
@EntityListeners( {AuditUtil.class})
public class IOrderRecurrence implements Auditable, Cloneable {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer               id;
    
    @Column(name = "iorder_id")
    private Integer               iorderId;
    
    @Column(name = "is_active")
    private String                isActive;
    
    @Column(name = "active_begin")
    private Date                  activeBegin;
    
    @Column(name = "active_end")
    private Date                  activeEnd;
    
    @Column(name = "frequency")
    private Integer               frequency;
    
    @Column(name = "unit_id")
    private Integer               unitId;
    
    @Transient
    private IOrderRecurrence      original;
    
    public Integer getId() {
        return id;
    }

    protected void setId(Integer id) {
        if (DataBaseUtil.isDifferent(id, this.id))
            this.id = id;
    }
    
    public Integer getIorderId() {
        return iorderId;
    }

    public void setIorderId(Integer iorderId) {
        if (DataBaseUtil.isDifferent(iorderId, this.iorderId))
            this.iorderId = iorderId;
    }

    public String getIsActive() {
        return isActive;
    }

    public void setIsActive(String isActive) {
        if (DataBaseUtil.isDifferent(isActive, this.isActive))
            this.isActive = isActive;
    }
    
    public Datetime getActiveBegin() {
        return DataBaseUtil.toYD(activeBegin);
    }

    public void setActiveBegin(Datetime activeBegin) {
        if (DataBaseUtil.isDifferentYD(activeBegin, this.activeBegin))
            this.activeBegin = DataBaseUtil.toDate(activeBegin);
    }
    
    public Datetime getActiveEnd() {
        return DataBaseUtil.toYD(activeEnd);
    }

    public void setActiveEnd(Datetime activeEnd) {
        if (DataBaseUtil.isDifferentYD(activeEnd, this.activeEnd))
            this.activeEnd = DataBaseUtil.toDate(activeEnd);
    }

    public Integer getFrequency() {
        return frequency;
    }

    public void setFrequency(Integer frequency) {
        if (DataBaseUtil.isDifferent(frequency, this.frequency))
            this.frequency = frequency;
    }

    public Integer getUnitId() {
        return unitId;
    }

    public void setUnitId(Integer unitId) {
        if (DataBaseUtil.isDifferent(unitId, this.unitId))
            this.unitId = unitId;
    }

    public void setClone() {
        try {
            original = (IOrderRecurrence)this.clone();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Audit getAudit(Integer activity) {
        Audit audit;

        audit = new Audit(activity);
        audit.setReferenceTableId(Constants.table().IORDER_RECURRENCE);
        audit.setReferenceId(getId());
        if (original != null)
            audit.setField("id", id, original.id)
                 .setField("iorderId", iorderId, original.iorderId)
                 .setField("isActive", isActive, original.isActive)
                 .setField("activeBegin", activeBegin, original.activeBegin)
                 .setField("activeEnd", activeEnd, original.activeEnd)
                 .setField("frequency", frequency, original.frequency)
                 .setField("unitId", unitId, original.unitId);

        return audit;
    }
}
