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

import org.openelis.util.XMLUtil;
import org.openelis.utilcommon.DataBaseUtil;
import org.openelis.utils.AuditUtil;
import org.openelis.utils.Auditable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

@NamedQueries({
    @NamedQuery(name = "StorageLocation.FetchById",
               query = "select new org.openelis.domain.StorageLocationViewDO(s.id,s.sortOrder,s.name, " +
                       "s.location,s.parentStorageLocationId,s.storageUnitId,s.isAvailable,s.storageUnit.description)"
                     + " from StorageLocation s where s.id = :id"),
    @NamedQuery(name = "StorageLocation.FetchByParentStorageLocationId",
               query = "select new org.openelis.domain.StorageLocationViewDO(s.id,s.sortOrder,s.name, " +
                       "s.location,s.parentStorageLocationId,s.storageUnitId,s.isAvailable, s.storageUnit.description)"
                     + " from StorageLocation s where s.parentStorageLocationId = :id order by s.sortOrder"),
    @NamedQuery(name = "StorageLocation.FetchByName",
               query = "select new org.openelis.domain.StorageLocationViewDO(s.id,s.sortOrder,s.name, " +
                       "s.location,s.parentStorageLocationId,s.storageUnitId,s.isAvailable,s.storageUnit.description)"
                     + " from StorageLocation s where s.name = :name"),
})

@NamedNativeQuery(name = "StorageLocation.ReferenceCheck",
                  query = "select storage_location_id as STORAGE_LOCATION_ID from storage where storage_location_id = :id " +
                          "UNION " +
                          "select storage_location_id as STORAGE_LOCATION_ID from inventory_location where storage_location_id = :id ",
                  resultSetMapping="StorageLocation.ReferenceCheckMapping")
@SqlResultSetMapping(name="StorageLocation.ReferenceCheckMapping",
                     columns={@ColumnResult(name="STORAGE_LOCATION_ID")})
                     
/*
     @NamedQuery(name = "StorageLocation.FetchByName",
               query = "select new org.openelis.domain.StorageLocationVO(childLoc.id, childLoc.name, childLoc.location, " +
                       "parentLoc.name, childLoc.storageUnit.description) " 
                     + " from StorageLocation childLoc left join childLoc.parentStorageLocation parentLoc where "
                     + " (childLoc.id not in (select c.parentStorageLocationId from StorageLocation c where c.parentStorageLocationId=childLoc.id))"
                     + " and (childLoc.name like :name OR childLoc.location like :loc OR childLoc.storageUnit.description like :desc) and childLoc.isAvailable = 'Y'"
                     + " order by childLoc.name"), */                     

@Entity
@Table(name = "storage_location")
@EntityListeners( {AuditUtil.class})
public class StorageLocation implements Auditable, Cloneable {

    @Id
    @GeneratedValue
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
    private Collection<StorageLocation> childLocations;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_storage_location_id", insertable = false, updatable = false)
    private StorageLocation             parentStorageLocation;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "storage_unit_id", insertable = false, updatable = false)
    private StorageUnit                 storageUnit;

    @Transient
    private StorageLocation             original;

    public Integer getId() {
        return id;
    }

    protected void setId(Integer id) {
        if (DataBaseUtil.isDifferent(id,this.id))
            this.id = id;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        if (DataBaseUtil.isDifferent(sortOrder,this.sortOrder))
            this.sortOrder = sortOrder;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (DataBaseUtil.isDifferent(name,this.name))
            this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        if (DataBaseUtil.isDifferent(location,this.location))
            this.location = location;
    }

    public Integer getParentStorageLocationId() {
        return parentStorageLocationId;
    }

    public void setParentStorageLocationId(Integer parentStorageLocationId) {
        if (DataBaseUtil.isDifferent(parentStorageLocationId,this.parentStorageLocationId))
            this.parentStorageLocationId = parentStorageLocationId;
    }

    public Integer getStorageUnitId() {
        return storageUnitId;
    }

    public void setStorageUnitId(Integer storageUnitId) {
        if (DataBaseUtil.isDifferent(storageUnitId,this.storageUnitId))
            this.storageUnitId = storageUnitId;
    }

    public String getIsAvailable() {
        return isAvailable;
    }

    public void setIsAvailable(String isAvailable) {
        if (DataBaseUtil.isDifferent(isAvailable,this.isAvailable))
            this.isAvailable = isAvailable;
    }
    
    public Collection<StorageLocation> getChildLocations() {
        return childLocations;
    }

    public void setChildLocations(Collection<StorageLocation> childLocations) {
        this.childLocations = childLocations;
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

    public void setClone() {
        try {
            original = (StorageLocation)this.clone();
        } catch (Exception e) {
        }
    }

    public String getChangeXML() {
        try {
            Document doc = XMLUtil.createNew("change");
            Element root = doc.getDocumentElement();

            AuditUtil.getChangeXML(id, original.id, doc, "id");
            AuditUtil.getChangeXML(sortOrder, original.sortOrder, doc, "sort_order_id");
            AuditUtil.getChangeXML(name, original.name, doc, "name");
            AuditUtil.getChangeXML(location, original.location, doc, "location");
            AuditUtil.getChangeXML(parentStorageLocationId, original.parentStorageLocationId, doc, "parent_storage_location_id");
            AuditUtil.getChangeXML(storageUnitId, original.storageUnitId, doc, "storage_unit_id");
            AuditUtil.getChangeXML(isAvailable, original.isAvailable, doc, "is_available");

            if (root.hasChildNodes())
                return XMLUtil.toString(doc);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getTableName() {
        return "storage_location";
    }

}
