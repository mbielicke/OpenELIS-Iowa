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
 * StorageLocation Entity POJO for database
 */

import java.util.Collection;

import javax.persistence.Column;
import javax.persistence.ColumnResult;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedNativeQuery;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.openelis.domain.ReferenceTable;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.utilcommon.AuditActivity;
import org.openelis.utils.Audit;
import org.openelis.utils.AuditUtil;
import org.openelis.utils.Auditable;

@NamedQueries({
    @NamedQuery( name = "StorageLocation.FetchById",
                query = "select new org.openelis.domain.StorageLocationViewDO(s.id,s.sortOrder,s.name," +
                        "s.location,s.parentStorageLocationId,s.storageUnitId,s.isAvailable,s.storageUnit.description)"
                      + " from StorageLocation s where s.id = :id"),
    @NamedQuery( name = "StorageLocation.FetchByParentStorageLocationId",
                query = "select new org.openelis.domain.StorageLocationViewDO(s.id,s.sortOrder,s.name, " +
                        "s.location,s.parentStorageLocationId,s.storageUnitId,s.isAvailable,s.storageUnit.description)"
                      + " from StorageLocation s where s.parentStorageLocationId = :id order by s.sortOrder"),
    @NamedQuery( name = "StorageLocation.FetchByName",
                query = "select new org.openelis.domain.StorageLocationViewDO(s.id,s.sortOrder,s.name, " +
                        "s.location,s.parentStorageLocationId,s.storageUnitId,s.isAvailable,s.storageUnit.description)"
                      + " from StorageLocation s where s.name = :name"),
   @NamedQuery( name = "StorageLocation.FetchAvailableByName",
               query = "select new org.openelis.domain.StorageLocationViewDO(s.id,s.sortOrder,s.name, " +
                       "s.location,s.parentStorageLocationId,s.storageUnitId,s.isAvailable,s.storageUnit.description)"
                     + " from StorageLocation s where s.name like :name and s.isAvailable = 'Y' and"+
                       " s.id not in (select c.parentStorageLocationId from StorageLocation c where c.parentStorageLocationId = s.id)")})

@NamedNativeQuery(name = "StorageLocation.ReferenceCheck",
                  query = "select storage_location_id as STORAGE_LOCATION_ID from storage where storage_location_id = :id " +
                          "UNION " +
                          "select storage_location_id as STORAGE_LOCATION_ID from inventory_location where storage_location_id = :id ",
                  resultSetMapping="StorageLocation.ReferenceCheckMapping")
@SqlResultSetMapping(name="StorageLocation.ReferenceCheckMapping",
                     columns={@ColumnResult(name="STORAGE_LOCATION_ID")})
                          

@Entity
@Table(name = "storage_location")
@EntityListeners({AuditUtil.class})
public class StorageLocation implements Auditable, Cloneable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer                     id;

    @Column(name = "sort_order")
    private Integer                     sortOrder;

    @Column(name = "name")
    private String                      name;

    @Column(name = "location")
    private String                      location;

    @Column(name = "parent_storage_location_id")
    private Integer                     parentStorageLocationId;

    @Column(name = "storage_unit_id")
    private Integer                     storageUnitId;

    @Column(name = "is_available")
    private String                      isAvailable;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_storage_location_id", insertable = false, updatable = false)
    private Collection<StorageLocation> childStorageLocation;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_storage_location_id", insertable = false, updatable = false)
    private StorageLocation             parentStorageLocation;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "storage_unit_id", insertable = false, updatable = false)
    private StorageUnit                 storageUnit;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "storage_location_id", insertable = false, updatable = false)
    private Collection<Storage>         storage;

    @Transient
    private StorageLocation             original;

    public Integer getId() {
        return id;
    }

    protected void setId(Integer id) {
        if (DataBaseUtil.isDifferent(id, this.id))
            this.id = id;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        if (DataBaseUtil.isDifferent(sortOrder, this.sortOrder))
            this.sortOrder = sortOrder;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (DataBaseUtil.isDifferent(name, this.name))
            this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        if (DataBaseUtil.isDifferent(location, this.location))
            this.location = location;
    }

    public Integer getParentStorageLocationId() {
        return parentStorageLocationId;
    }

    public void setParentStorageLocationId(Integer parentStorageLocationId) {
        if (DataBaseUtil.isDifferent(parentStorageLocationId, this.parentStorageLocationId))
            this.parentStorageLocationId = parentStorageLocationId;
    }

    public Integer getStorageUnitId() {
        return storageUnitId;
    }

    public void setStorageUnitId(Integer storageUnitId) {
        if (DataBaseUtil.isDifferent(storageUnitId, this.storageUnitId))
            this.storageUnitId = storageUnitId;
    }

    public String getIsAvailable() {
        return isAvailable;
    }

    public void setIsAvailable(String isAvailable) {
        if (DataBaseUtil.isDifferent(isAvailable, this.isAvailable))
            this.isAvailable = isAvailable;
    }

    public Collection<StorageLocation> getChildStorageLocation() {
        return childStorageLocation;
    }

    public void setChildStorageLocationn(Collection<StorageLocation> childStorageLocation) {
        this.childStorageLocation = childStorageLocation;
    }

    public StorageUnit getStorageUnit() {
        return storageUnit;
    }

    public void setStorageUnit(StorageUnit storageUnitName) {
        this.storageUnit = storageUnitName;
    }

    public StorageLocation getParentStorageLocation() {
        return parentStorageLocation;
    }

    public void setParentStorageLocation(StorageLocation parentStorageLocation) {
        this.parentStorageLocation = parentStorageLocation;
    }

    public void setStorage(Collection<Storage> storage) {
        this.storage = storage;
    }

    public Collection<Storage> getStorage() {
        return storage;
    }

    public void setClone() {
        try {
            original = (StorageLocation)this.clone();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Audit getAudit(AuditActivity activity) {
        Audit audit;

        audit = new Audit(activity);
        audit.setReferenceTableId(ReferenceTable.STORAGE_LOCATION);
        audit.setReferenceId(getId());
        if (original != null)
            audit.setField("id", id, original.id)
                 .setField("sort_order", sortOrder, original.sortOrder)
                 .setField("name", name, original.name)
                 .setField("location", location, original.location)
                 .setField("parent_storage_location_id", parentStorageLocationId, original.parentStorageLocationId, ReferenceTable.STORAGE_LOCATION)
                 .setField("storage_unit_id", storageUnitId, original.storageUnitId, ReferenceTable.STORAGE_UNIT)
                 .setField("is_available", isAvailable, original.isAvailable);

        return audit;
    }
}
