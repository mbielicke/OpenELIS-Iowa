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
 * Qc Entity POJO for database
 */

import java.util.Collection;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.openelis.domain.ReferenceTable;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.utilcommon.AuditActivity;
import org.openelis.utils.Audit;
import org.openelis.utils.AuditUtil;
import org.openelis.utils.Auditable;

@NamedQueries({
    @NamedQuery( name = "Qc.FetchById",
                query = "select new org.openelis.domain.QcViewDO(q.id,q.name,q.typeId,q.inventoryItemId," +
                		"q.source,q.isActive,i.name)"
                	  + " from Qc q left join q.inventoryItem i where q.id = :id"),
    @NamedQuery( name = "Qc.FetchByName",
                query = "select new org.openelis.domain.QcDO(q.id,q.name,q.typeId,q.inventoryItemId," +
                        "q.source,q.isActive)"
                      + " from Qc q where q.name like :name"),
    @NamedQuery( name = "Qc.FetchActiveByName",
                query = "select new org.openelis.domain.QcDO(q.id,q.name,q.typeId,q.inventoryItemId," +
                        "q.source,q.isActive)"
                      + " from Qc q where q.name like :name and q.isActive='Y'")})

@Entity
@Table(name = "qc")
@EntityListeners({AuditUtil.class})
public class Qc implements Auditable, Cloneable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer               id;

    @Column(name = "name")
    private String                name;

    @Column(name = "type_id")
    private Integer               typeId;

    @Column(name = "inventory_item_id")
    private Integer               inventoryItemId;

    @Column(name = "source")
    private String                source;

    @Column(name = "is_active")
    private String                isActive;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inventory_item_id", insertable = false, updatable = false)
    private InventoryItem         inventoryItem;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "qc_id")
    private Collection<QcAnalyte> qcAnalyte;
    
    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "qc_id")
    private Collection<QcLot> qcLot;

    @Transient
    private Qc                    original;

    public Integer getId() {
        return id;
    }

    protected void setId(Integer id) {
        if (DataBaseUtil.isDifferent(id, this.id))
            this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (DataBaseUtil.isDifferent(name, this.name))
            this.name = name;
    }

    public Integer getTypeId() {
        return typeId;
    }

    public void setTypeId(Integer typeId) {
        if (DataBaseUtil.isDifferent(typeId, this.typeId))
            this.typeId = typeId;
    }

    public Integer getInventoryItemId() {
        return inventoryItemId;
    }

    public void setInventoryItemId(Integer inventoryItemId) {
        if (DataBaseUtil.isDifferent(inventoryItemId, this.inventoryItemId))
            this.inventoryItemId = inventoryItemId;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        if (DataBaseUtil.isDifferent(source, this.source))
            this.source = source;
    }

    public String getIsActive() {
        return isActive;
    }

    public void setIsActive(String isActive) {
        if (DataBaseUtil.isDifferent(isActive, this.isActive))
            this.isActive = isActive;
    }
    
    public Collection<QcAnalyte> getQcAnalyte() {
        return qcAnalyte;
    }
    
    public void setQcAnalyte(Collection<QcAnalyte> qcAnalyte) {
        this.qcAnalyte = qcAnalyte;
    }

    public Collection<QcLot> getQcLot() {
        return qcLot;
    }

    public void setQcLot(Collection<QcLot> qcLot) {
        this.qcLot = qcLot;
    }

    public InventoryItem getInventoryItem() {
        return inventoryItem;
    }

    public void setClone() {
        try {
            original = (Qc)this.clone();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Audit getAudit(AuditActivity activity) {
        Audit audit;

        audit = new Audit(activity);
        audit.setReferenceTableId(ReferenceTable.QC);
        audit.setReferenceId(getId());
        if (original != null)
            audit.setField("id", id, original.id)
                 .setField("name", name, original.name)
                 .setField("type_id", typeId, original.typeId, ReferenceTable.DICTIONARY)
                 .setField("inventory_item_id", inventoryItemId, original.inventoryItemId, ReferenceTable.INVENTORY_ITEM)
                 .setField("source", source, original.source)
                 .setField("is_active", isActive, original.isActive);

        return audit;
    }
}
