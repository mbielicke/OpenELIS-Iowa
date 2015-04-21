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
 * TransLocationOrder Entity POJO for database
 */

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.openelis.ui.common.DataBaseUtil;

@NamedQueries({
   @NamedQuery(name = "InventoryXUse.FetchByIorderId",
               query = "select new org.openelis.domain.InventoryXUseViewDO(i.id,i.inventoryLocationId,i.iorderItemId,"
                       + "i.quantity,l.lotNumber,l.expirationDate,l.quantityOnhand,s.id,s.name,u.description,s.location,"
                       + "oi.id, oi.name,r.receivedDate,r.unitCost,r.externalReference, o.iorderId)"
                       + " from InventoryXUse i join i.inventoryLocation l join l.storageLocation s"
                       + " join s.storageUnit u join i.iorderItem o join o.inventoryItem oi "
                       + " left join o.inventoryReceipt r where o.iorder.id = :id order by o.id"),
   @NamedQuery(name = "InventoryXUse.FetchByIorderIds",
               query = "select new org.openelis.domain.InventoryXUseViewDO(i.id,i.inventoryLocationId,i.iorderItemId,"
                       + "i.quantity,l.lotNumber,l.expirationDate,l.quantityOnhand,s.id,s.name,u.description,s.location,"
                       + "oi.id, oi.name,r.receivedDate,r.unitCost,r.externalReference, o.iorderId)"
                       + " from InventoryXUse i join i.inventoryLocation l join l.storageLocation s"
                       + " join s.storageUnit u join i.iorderItem o join o.inventoryItem oi "
                       + " left join o.inventoryReceipt r where o.iorder.id in ( :ids ) order by o.id")})
@Entity
@Table(name = "inventory_x_use")
public class InventoryXUse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer           id;

    @Column(name = "inventory_location_id")
    private Integer           inventoryLocationId;

    @Column(name = "iorder_item_id")
    private Integer           iorderItemId;

    @Column(name = "quantity")
    private Integer           quantity;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "iorder_item_id", insertable = false, updatable = false)
    private IOrderItem         iorderItem;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inventory_location_id", insertable = false, updatable = false)
    private InventoryLocation inventoryLocation;

    public Integer getId() {
        return id;
    }

    protected void setId(Integer id) {
        if (DataBaseUtil.isDifferent(id, this.id))
            this.id = id;
    }

    public Integer getInventoryLocationId() {
        return inventoryLocationId;
    }

    public void setInventoryLocationId(Integer inventoryLocationId) {
        if (DataBaseUtil.isDifferent(inventoryLocationId, this.inventoryLocationId))
            this.inventoryLocationId = inventoryLocationId;
    }

    public Integer getIorderItemId() {
        return iorderItemId;
    }

    public void setIorderItemId(Integer iorderItemId) {
        if (DataBaseUtil.isDifferent(iorderItemId, this.iorderItemId))
            this.iorderItemId = iorderItemId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        if (DataBaseUtil.isDifferent(quantity, this.quantity))
            this.quantity = quantity;
    }

    public InventoryLocation getInventoryLocation() {
        return inventoryLocation;
    }

    public IOrderItem getIorderItem() {
        return iorderItem;
    }
    
    public void setIorderItem(IOrderItem iorderItem) {
        this.iorderItem = iorderItem;
    }
}