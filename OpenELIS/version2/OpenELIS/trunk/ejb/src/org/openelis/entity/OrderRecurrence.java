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

import org.openelis.domain.ReferenceTable;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.Datetime;
import org.openelis.utils.Audit;
import org.openelis.utils.AuditUtil;
import org.openelis.utils.Auditable;

@NamedQueries({
    @NamedQuery( name = "OrderRecurrence.FetchByOrderId",
                query = "select new org.openelis.domain.OrderRecurrenceDO(r.id, r.orderId,r.isActive," +
                		"r.activeBegin, r.activeEnd, r.frequency, r.unitId)"
                      + " from OrderRecurrence r where r.orderId = :orderId"),
    @NamedQuery( name = "OrderRecurrence.FetchActiveList",
                query = "select new org.openelis.domain.OrderRecurrenceDO(r.id, r.orderId,r.isActive," +
                        "r.activeBegin, r.activeEnd, r.frequency, r.unitId)"
                      + " from OrderRecurrence r where r.isActive = 'Y'")})
        
@Entity
@Table(name = "order_recurrence")
@EntityListeners( {AuditUtil.class})
public class OrderRecurrence implements Auditable, Cloneable {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer               id;
    
    @Column(name = "order_id")
    private Integer               orderId;
    
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
    private OrderRecurrence       original;
    
    public Integer getId() {
        return id;
    }

    protected void setId(Integer id) {
        if (DataBaseUtil.isDifferent(id, this.id))
            this.id = id;
    }
    
    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        if (DataBaseUtil.isDifferent(orderId, this.orderId))
            this.orderId = orderId;
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
            original = (OrderRecurrence)this.clone();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Audit getAudit() {
        Audit audit;

        audit = new Audit();
        audit.setReferenceTableId(ReferenceTable.ORDER_RECURRENCE);
        audit.setReferenceId(getId());
        if (original != null)
            audit.setField("id", id, original.id)
                 .setField("orderId", orderId, original.orderId)
                 .setField("isActive", isActive, original.isActive)
                 .setField("activeBegin", activeBegin, original.activeBegin)
                 .setField("activeEnd", activeEnd, original.activeEnd)
                 .setField("frequency", frequency, original.frequency)
                 .setField("unitId", unitId, original.unitId);

        return audit;
    }
}
