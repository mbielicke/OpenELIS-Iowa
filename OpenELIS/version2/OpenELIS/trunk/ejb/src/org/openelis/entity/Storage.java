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
 * Storage Entity POJO for database
 */

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.openelis.gwt.common.Datetime;
import org.openelis.util.XMLUtil;

import java.util.Date;
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

import org.openelis.utilcommon.DataBaseUtil;
import org.openelis.utils.AuditUtil;
import org.openelis.utils.Auditable;

@NamedQueries({
    @NamedQuery(name = "Storage.IdByStorageLocation",
               query = "select s.id from Storage s where s.storageLocationId = :id"),
    @NamedQuery(name = "Storage.StorageById",
                query = "select new org.openelis.domain.StorageViewDO(s.id, s.referenceId, s.referenceTableId, " +
                        "s.storageLocationId, s.checkin, s.checkout, s.systemUserId, childLoc.name, childLoc.location, " +
                        "parentLoc.name, childLoc.storageUnit.description)"
                      + " from Storage s left join s.storageLocation childLoc "
                      + " left join childLoc.parentStorageLocation parentLoc where s.referenceTableId = :referenceTable and "
                      + " s.referenceId = :id ORDER BY s.checkout DESC")
})

@Entity
@Table(name = "storage")
@EntityListeners( {AuditUtil.class})
public class Storage implements Auditable, Cloneable {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Integer         id;

    @Column(name = "reference_id")
    private Integer         referenceId;

    @Column(name = "reference_table_id")
    private Integer         referenceTableId;

    @Column(name = "storage_location_id")
    private Integer         storageLocationId;

    @Column(name = "checkin")
    private Date            checkin;

    @Column(name = "checkout")
    private Date            checkout;

    @Column(name = "system_user_id")
    private Integer         systemUserId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "storage_location_id", insertable = false, updatable = false)
    private StorageLocation storageLocation;

    @Transient
    private Storage         original;

    public Integer getId() {
        return id;
    }

    protected void setId(Integer id) {
        if (DataBaseUtil.isDifferent(id,this.id))
            this.id = id;
    }

    public Integer getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(Integer referenceId) {
        if (DataBaseUtil.isDifferent(referenceId,this.referenceId))
            this.referenceId = referenceId;
    }

    public Integer getReferenceTableId() {
        return referenceTableId;
    }

    public void setReferenceTableId(Integer referenceTableId) {
        if (DataBaseUtil.isDifferent(referenceTableId,this.referenceTableId))
            this.referenceTableId = referenceTableId;
    }

    public Integer getStorageLocationId() {
        return storageLocationId;
    }

    public void setStorageLocationId(Integer storageLocationId) {
        if (DataBaseUtil.isDifferent(storageLocationId,this.storageLocationId))
            this.storageLocationId = storageLocationId;
    }

    public Datetime getCheckin() {
        return DataBaseUtil.toYM(checkin);
    }

    public void setCheckin(Datetime checkin) {
        if (DataBaseUtil.isDifferentYM(checkin,this.checkin))
            this.checkin = DataBaseUtil.toDate(checkin);
    }

    public Datetime getCheckout() {
        return DataBaseUtil.toYM(checkout);
    }

    public void setCheckout(Datetime checkout) {
        if (DataBaseUtil.isDifferentYM(checkout,this.checkout))
            this.checkout = DataBaseUtil.toDate(checkout);
    }

    public Integer getSystemUserId() {
        return systemUserId;
    }

    public void setSystemUserId(Integer systemUserId) {
        if (DataBaseUtil.isDifferent(systemUserId,this.systemUserId))
            this.systemUserId = systemUserId;
    }
    
    public StorageLocation getStorageLocation() {
        return storageLocation;
    }

    public void setStorageLocation(StorageLocation storageLocation) {
        this.storageLocation = storageLocation;
    }

    public void setClone() {
        try {
            original = (Storage)this.clone();
        } catch (Exception e) {
        }
    }

    public String getChangeXML() {
        try {
            Document doc = XMLUtil.createNew("change");
            Element root = doc.getDocumentElement();

            AuditUtil.getChangeXML(id, original.id, doc, "id");
            AuditUtil.getChangeXML(referenceId, original.referenceId, doc, "reference_id");
            AuditUtil.getChangeXML(referenceTableId, original.referenceTableId, doc, "reference_table_id");
            AuditUtil.getChangeXML(storageLocationId, original.storageLocationId, doc, "storage_location_id");
            AuditUtil.getChangeXML(checkin, original.checkin, doc, "checkin");
            AuditUtil.getChangeXML(checkout, original.checkout, doc, "checkout");
            AuditUtil.getChangeXML(systemUserId, original.systemUserId, doc, "system_user_id");

            if (root.hasChildNodes())
                return XMLUtil.toString(doc);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getTableName() {
        return "storage";
    }

}
