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
 * TransAdjustmentLocation Entity POJO for database
 */

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
    @NamedQuery( name = "InventoryXAdjust.FetchByInventoryAdjustmentId",
                query = "select distinct new org.openelis.domain.InventoryXAdjustViewDO(trans.id, trans.inventoryAdjustmentId," +
                        "il.id, trans.quantity, trans.physicalCount, il.lotNumber,il.quantityOnhand,"+
                        "s.name,su.description,s.location, ii.id, ii.name)"
                      + " from InventoryXAdjust trans left join trans.inventoryLocation il left join il.inventoryItem ii" 
                      + " left join il.storageLocation s left join s.storageUnit su where trans.inventoryAdjustmentId = :id ORDER BY il.id ")})
@Entity
@Table(name = "inventory_x_adjust")
@EntityListeners({AuditUtil.class})
public class InventoryXAdjust implements Auditable, Cloneable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer           id;

    @Column(name = "inventory_adjustment_id")
    private Integer           inventoryAdjustmentId;

    @Column(name = "inventory_location_id")
    private Integer           inventoryLocationId;

    @Column(name = "quantity")
    private Integer           quantity;

    @Column(name = "physical_count")
    private Integer           physicalCount;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inventory_location_id", insertable = false, updatable = false)
    private InventoryLocation inventoryLocation;

    @Transient
    private InventoryXAdjust  original;

    public Integer getId() {
        return id;
    }

    protected void setId(Integer id) {
        if (DataBaseUtil.isDifferent(id, this.id))
            this.id = id;
    }

    public Integer getInventoryAdjustmentId() {
        return inventoryAdjustmentId;
    }

    public void setInventoryAdjustmentId(Integer inventoryAdjustmentId) {
        if (DataBaseUtil.isDifferent(inventoryAdjustmentId, this.inventoryAdjustmentId))
            this.inventoryAdjustmentId = inventoryAdjustmentId;
    }

    public Integer getInventoryLocationId() {
        return inventoryLocationId;
    }

    public void setInventoryLocationId(Integer inventoryLocationId) {
        if (DataBaseUtil.isDifferent(inventoryLocationId, this.inventoryLocationId))
            this.inventoryLocationId = inventoryLocationId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        if (DataBaseUtil.isDifferent(quantity, this.quantity))
            this.quantity = quantity;
    }

    public Integer getPhysicalCount() {
        return physicalCount;
    }

    public void setPhysicalCount(Integer physicalCount) {
        if (DataBaseUtil.isDifferent(physicalCount, this.physicalCount))
            this.physicalCount = physicalCount;
    }

    public InventoryLocation getInventoryLocation() {
        return inventoryLocation;
    }

    public void setClone() {
        try {
            original = (InventoryXAdjust)this.clone();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Audit getAudit(AuditActivity activity) {
        Audit audit;

        audit = new Audit(activity);
        audit.setReferenceTableId(ReferenceTable.INVENTORY_X_ADJUST);
        audit.setReferenceId(getId());
        if (original != null)
            audit.setField("id", id, original.id)
                 .setField("inventory_adjustment_id", inventoryAdjustmentId, original.inventoryAdjustmentId)
                 .setField("inventory_location_id", inventoryLocationId, original.inventoryLocationId)
                 .setField("quantity", quantity, original.quantity)
                 .setField("physical_count", physicalCount, original.physicalCount);

        return audit;
    }

}
