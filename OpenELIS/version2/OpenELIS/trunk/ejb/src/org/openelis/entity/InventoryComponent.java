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
 * InventoryComponent Entity POJO for database
 */

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.openelis.domain.ReferenceTable;
import org.openelis.utilcommon.DataBaseUtil;
import org.openelis.utils.Audit;
import org.openelis.utils.AuditUtil;
import org.openelis.utils.Auditable;

@NamedQueries({
    @NamedQuery( name = "InventoryComponent.FetchByInventoryItemId",
                 query = "select new org.openelis.domain.InventoryComponentViewDO(c.id,"+
                         "c.inventoryItemId,c.componentId,c.quantity,i.name,i.description)"
                       + " from InventoryComponent c left join c.componentInventoryItem i where c.inventoryItemId = :id")})
//
//                @NamedQuery(name = "InventoryComponent.InventoryComponentsByItem", query = "select new org.openelis.domain.InventoryComponentDO(c.id,c.inventoryItemId,c.componentId,i.name,i.description, "
//                                                                                           + " c.quantity, i.id) from InventoryComponent c left join c.componentInventoryItem i  where c.inventoryItemId = :id")})

@Entity
@Table(name = "inventory_component")
@EntityListeners( {AuditUtil.class})
public class InventoryComponent implements Auditable, Cloneable {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Integer            id;

    @Column(name = "inventory_item_id")
    private Integer            inventoryItemId;

    @Column(name = "component_id")
    private Integer            componentId;

    @Column(name = "quantity")
    private Double             quantity;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "component_id", insertable = false, updatable = false)
    private InventoryItem      componentInventoryItem;

    @Transient
    private InventoryComponent original;

    public Integer getId() {
        return id;
    }

    protected void setId(Integer id) {
        if (DataBaseUtil.isDifferent(id, this.id))
            this.id = id;
    }

    public Integer getInventoryItemId() {
        return inventoryItemId;
    }

    public void setInventoryItemId(Integer inventoryItemId) {
        if (DataBaseUtil.isDifferent(inventoryItemId, this.inventoryItemId))
            this.inventoryItemId = inventoryItemId;
    }

    public Integer getComponentId() {
        return componentId;
    }

    public void setComponentId(Integer componentId) {
        if (DataBaseUtil.isDifferent(componentId, this.componentId))
            this.componentId = componentId;
    }

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        if (DataBaseUtil.isDifferent(quantity, this.quantity))
            this.quantity = quantity;
    }

    public InventoryItem getComponentInventoryItem() {
        return componentInventoryItem;
    }

    public void setComponentInventoryItem(InventoryItem componentInventoryItem) {
        this.componentInventoryItem = componentInventoryItem;
    }

    public void setClone() {
        try {
            original = (InventoryComponent)this.clone();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public Audit getAudit() {
        Audit audit;

        audit = new Audit();
        audit.setReferenceTableId(ReferenceTable.INVENTORY_COMPONENT);
        audit.setReferenceId(getId());
        if (original != null)
            audit.setField("id", id, original.id)
                 .setField("inventory_item_id", inventoryItemId, original.inventoryItemId)
                 .setField("component_id", componentId, original.componentId)
                 .setField("quantity", quantity, original.quantity);

        return audit;
    }
}
