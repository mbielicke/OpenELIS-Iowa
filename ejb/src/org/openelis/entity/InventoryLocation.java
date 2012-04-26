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
 * InventoryLocation Entity POJO for database
 */

import java.util.Date;

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
import org.openelis.gwt.common.Datetime;
import org.openelis.utils.Audit;
import org.openelis.utils.AuditUtil;
import org.openelis.utils.Auditable;

@NamedQueries({
    @NamedQuery( name = "InventoryLocation.FetchByInventoryItemId",
                query = "select new org.openelis.domain.InventoryLocationViewDO(i.id,i.inventoryItemId," +
                        "i.lotNumber,i.storageLocationId,i.quantityOnhand,i.expirationDate," +
                        "i.inventoryItem.name,i.inventoryItem.storeId,s.name,s.storageUnit.description,s.location)"
                      + " from InventoryLocation i left join i.storageLocation s"
                      + " where i.inventoryItemId = :id order by i.quantityOnhand DESC"),
    @NamedQuery( name = "InventoryLocation.FetchByLocationNameAndItemId",
                query = "select new org.openelis.domain.InventoryLocationViewDO(i.id,i.inventoryItemId," +
                        "i.lotNumber,i.storageLocationId,i.quantityOnhand,i.expirationDate," +
                        "i.inventoryItem.name,i.inventoryItem.storeId,s.name,s.storageUnit.description,s.location)"
                      + " from InventoryLocation i left join i.storageLocation s where s.name like :name and i.inventoryItemId = :id and i.quantityOnhand > 0" ),
    @NamedQuery( name = "InventoryLocation.FetchByLocationNameItemIdAndStoreId",
                query = "select new org.openelis.domain.InventoryLocationViewDO(i.id,i.inventoryItemId," +
                        "i.lotNumber,i.storageLocationId,i.quantityOnhand,i.expirationDate," +
                        "i.inventoryItem.name,i.inventoryItem.storeId,s.name,s.storageUnit.description,s.location)"
                      + " from InventoryLocation i left join i.storageLocation s where s.name like :name and i.inventoryItemId = :inventoryItemId and"
                      +	" i.inventoryItem.storeId = :storeId and i.quantityOnhand > 0" ),                  
    @NamedQuery( name = "InventoryLocation.FetchByInventoryReceiptId",
                query = "select new org.openelis.domain.InventoryLocationViewDO(i.id,i.inventoryItemId," +
                        "i.lotNumber,i.storageLocationId,i.quantityOnhand,i.expirationDate," +
                        "i.inventoryItem.name,i.inventoryItem.storeId,s.name,s.storageUnit.description,s.location)"
                      + " from InventoryXPut xp left join xp.inventoryLocation i left join i.storageLocation s "
                      + " where xp.inventoryReceiptId = :id"),                   
    @NamedQuery( name = "InventoryLocation.FetchById",
                query = "select new org.openelis.domain.InventoryLocationViewDO(i.id,i.inventoryItemId," +
                        "i.lotNumber,i.storageLocationId,i.quantityOnhand,i.expirationDate," +
                        "i.inventoryItem.name,i.inventoryItem.storeId,s.name,s.storageUnit.description,s.location)"
                      + " from InventoryLocation i left join i.storageLocation s where i.id = :id"),
    @NamedQuery( name = "InventoryLocation.FetchByInventoryItemName",
                query = "select new org.openelis.domain.InventoryLocationViewDO(i.id,i.inventoryItemId," +
                        "i.lotNumber,i.storageLocationId,i.quantityOnhand,i.expirationDate," +
                        "i.inventoryItem.name,i.inventoryItem.storeId,s.name,s.storageUnit.description,s.location)"
                      + " from InventoryLocation i left join i.storageLocation s" 
                      +	" where i.inventoryItem.isActive='Y' and i.inventoryItem.name like :name and i.quantityOnhand > 0" ),
    @NamedQuery( name = "InventoryLocation.FetchByInventoryItemNameStoreId",
                query = "select new org.openelis.domain.InventoryLocationViewDO(i.id,i.inventoryItemId," +
                        "i.lotNumber,i.storageLocationId,i.quantityOnhand,i.expirationDate," +
                        "i.inventoryItem.name,i.inventoryItem.storeId,s.name,s.storageUnit.description,s.location)"
                      + " from InventoryLocation i left join i.storageLocation s" 
                      + " where i.inventoryItem.isActive='Y' and i.inventoryItem.name like :name and i.inventoryItem.storeId = :id and i.quantityOnhand > 0" )})                                                                                                    

@Entity
@Table(name = "inventory_location")
@EntityListeners( {AuditUtil.class})
public class InventoryLocation implements Auditable, Cloneable {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer           id;

    @Column(name = "inventory_item_id")
    private Integer           inventoryItemId;

    @Column(name = "lot_number")
    private String            lotNumber;

    @Column(name = "storage_location_id")
    private Integer           storageLocationId;

    @Column(name = "quantity_onhand")
    private Integer           quantityOnhand;

    @Column(name = "expiration_date")
    private Date              expirationDate;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inventory_item_id", insertable = false, updatable = false)
    private InventoryItem     inventoryItem;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "storage_location_id", insertable = false, updatable = false)
    private StorageLocation   storageLocation;

    @Transient
    private InventoryLocation original;

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

    public String getLotNumber() {
        return lotNumber;
    }

    public void setLotNumber(String lotNumber) {
        if (DataBaseUtil.isDifferent(lotNumber, this.lotNumber))
            this.lotNumber = lotNumber;
    }

    public Integer getStorageLocationId() {
        return storageLocationId;
    }

    public void setStorageLocationId(Integer storageLocationId) {
        if (DataBaseUtil.isDifferent(storageLocationId, this.storageLocationId))
            this.storageLocationId = storageLocationId;
    }

    public Integer getQuantityOnhand() {
        return quantityOnhand;
    }

    public void setQuantityOnhand(Integer quantityOnhand) {
        if (DataBaseUtil.isDifferent(quantityOnhand, this.quantityOnhand))
            this.quantityOnhand = quantityOnhand;
    }

    public Datetime getExpirationDate() {
        return DataBaseUtil.toYD(expirationDate);
    }

    public void setExpirationDate(Datetime expirationDate) {
        if (DataBaseUtil.isDifferentYD(expirationDate, this.expirationDate))
            this.expirationDate = DataBaseUtil.toDate(expirationDate);
    }

    public StorageLocation getStorageLocation() {
        return storageLocation;
    }

    public void setStorageLocation(StorageLocation storageLocation) {
        this.storageLocation = storageLocation;
    }

    public InventoryItem getInventoryItem() {
        return inventoryItem;
    }

    public void setClone() {
        try {
            original = (InventoryLocation)this.clone();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Audit getAudit() {
        Audit audit;

        audit = new Audit();
        audit.setReferenceTableId(ReferenceTable.INVENTORY_LOCATION);
        audit.setReferenceId(getId());
        if (original != null)
            audit.setField("id", id, original.id)
                 .setField("inventory_item_id", inventoryItemId, original.inventoryItemId, ReferenceTable.INVENTORY_ITEM)
                 .setField("lot_number", lotNumber, original.lotNumber)
                 .setField("storage_location_id", storageLocationId, original.storageLocationId, ReferenceTable.STORAGE_LOCATION)
                 .setField("quantity_onhand", quantityOnhand, original.quantityOnhand)
                 .setField("expiration_date", expirationDate, original.expirationDate);

        return audit;
    }
}
