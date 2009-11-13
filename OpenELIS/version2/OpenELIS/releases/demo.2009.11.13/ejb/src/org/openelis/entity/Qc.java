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

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.openelis.gwt.common.Datetime;
import org.openelis.util.XMLUtil;

import java.util.Collection;
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
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.openelis.utilcommon.DataBaseUtil;
import org.openelis.utils.AuditUtil;
import org.openelis.utils.Auditable;

@NamedQueries({
    @NamedQuery( name = "Qc.FetchById",
                query = "select new org.openelis.domain.QcViewDO(q.id,q.name,q.typeId,q.inventoryItemId," +
                		"q.source,q.lotNumber,q.preparedDate,q.preparedVolume,q.preparedUnitId," +
                		"q.preparedById,q.usableDate,q.expireDate,q.isActive,i.name,'')"
                	  + " from Qc q left join q.inventoryItem i where q.id = :id"),
    @NamedQuery( name = "Qc.FetchByLotNumber",
                query = "select new org.openelis.domain.QcDO(q.id,q.name,q.typeId,q.inventoryItemId," +
                        "q.source,q.lotNumber,q.preparedDate,q.preparedVolume,q.preparedUnitId," +
                        "q.preparedById,q.usableDate,q.expireDate,q.isActive)"
                      + " from Qc q where q.lotNumber = :lotNumber"),
    @NamedQuery( name = "Qc.FetchByName",
                query = "select new org.openelis.domain.QcDO(q.id,q.name,q.typeId,q.inventoryItemId," +
                        "q.source,q.lotNumber,q.preparedDate,q.preparedVolume,q.preparedUnitId," +
                        "q.preparedById,q.usableDate,q.expireDate,q.isActive)"
                      + " from Qc q where q.name like :name"),
    @NamedQuery( name = "Qc.FetchActiveByName",
                query = "select new org.openelis.domain.QcDO(q.id,q.name,q.typeId,q.inventoryItemId," +
                        "q.source,q.lotNumber,q.preparedDate,q.preparedVolume,q.preparedUnitId," +
                        "q.preparedById,q.usableDate,q.expireDate,q.isActive)"
                      + " from Qc q where q.name like :name and q.isActive='Y'")})

@Entity
@Table(name = "qc")
@EntityListeners( {AuditUtil.class})
public class Qc implements Auditable, Cloneable {

    @Id
    @GeneratedValue
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

    @Column(name = "lot_number")
    private String                lotNumber;

    @Column(name = "prepared_date")
    private Date                  preparedDate;

    @Column(name = "prepared_volume")
    private Double                preparedVolume;

    @Column(name = "prepared_unit_id")
    private Integer               preparedUnitId;

    @Column(name = "prepared_by_id")
    private Integer               preparedById;

    @Column(name = "usable_date")
    private Date                  usableDate;

    @Column(name = "expire_date")
    private Date                  expireDate;

    @Column(name = "is_active")
    private String                isActive;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inventory_item_id", insertable = false, updatable = false)
    private InventoryItem         inventoryItem;

    @Transient
    private Qc                    original;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "qc_id")
    private Collection<QcAnalyte> qcAnalyte;

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
        if (DataBaseUtil.isDifferent(inventoryItemId,this.inventoryItemId))
            this.inventoryItemId = inventoryItemId;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        if (DataBaseUtil.isDifferent(source, this.source))
            this.source = source;
    }

    public String getLotNumber() {
        return lotNumber;
    }

    public void setLotNumber(String lotNumber) {
        if (DataBaseUtil.isDifferent(lotNumber, this.lotNumber))
            this.lotNumber = lotNumber;
    }

    public Datetime getPreparedDate() {
        return DataBaseUtil.toYM(preparedDate);
    }

    public void setPreparedDate(Datetime prepared_date) {
        if (DataBaseUtil.isDifferentYM(prepared_date, this.preparedDate))
            this.preparedDate = DataBaseUtil.toDate(prepared_date);
    }

    public Double getPreparedVolume() {
        return preparedVolume;
    }

    public void setPreparedVolume(Double preparedVolume) {
        if (DataBaseUtil.isDifferent(preparedVolume, this.preparedVolume))
            this.preparedVolume = preparedVolume;
    }

    public Integer getPreparedUnitId() {
        return preparedUnitId;
    }

    public void setPreparedUnitId(Integer preparedUnitId) {
        if (DataBaseUtil.isDifferent(preparedUnitId, this.preparedUnitId))
            this.preparedUnitId = preparedUnitId;
    }

    public Integer getPreparedById() {
        return preparedById;
    }

    public void setPreparedById(Integer preparedById) {
        if (DataBaseUtil.isDifferent(preparedById, this.preparedById))
            this.preparedById = preparedById;
    }

    public Datetime getUsableDate() {
        return DataBaseUtil.toYM(usableDate);
    }

    public void setUsableDate(Datetime usableDate) {
        if (DataBaseUtil.isDifferentYM(usableDate, this.usableDate))
            this.usableDate = DataBaseUtil.toDate(usableDate);
    }

    public Datetime getExpireDate() {
        return DataBaseUtil.toYM(expireDate);
    }

    public void setExpireDate(Datetime expireDate) {
        if (DataBaseUtil.isDifferentYM(expireDate, this.expireDate))
            this.expireDate = DataBaseUtil.toDate(expireDate);
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

    public String getChangeXML() {
        try {
            Document doc = XMLUtil.createNew("change");
            Element root = doc.getDocumentElement();

            AuditUtil.getChangeXML(id, original.id, doc, "id");
            AuditUtil.getChangeXML(name, original.name, doc, "name");
            AuditUtil.getChangeXML(typeId, original.typeId, doc, "type_id");
            AuditUtil.getChangeXML(inventoryItemId, original.inventoryItemId, doc, "inventory_item_id");
            AuditUtil.getChangeXML(source, original.source, doc, "source");
            AuditUtil.getChangeXML(lotNumber, original.lotNumber, doc, "lot_number");
            AuditUtil.getChangeXML(preparedDate, original.preparedDate, doc, "prepared_date");
            AuditUtil.getChangeXML(preparedVolume, original.preparedVolume, doc, "prepared_volume");
            AuditUtil.getChangeXML(preparedUnitId, original.preparedUnitId, doc, "prepared_unit_id");
            AuditUtil.getChangeXML(preparedById, original.preparedById, doc, "prepared_by_id");
            AuditUtil.getChangeXML(usableDate, original.usableDate, doc, "usable_date");
            AuditUtil.getChangeXML(expireDate, original.expireDate, doc, "expire_date");
            AuditUtil.getChangeXML(isActive, original.isActive, doc, "is_active");

            if (root.hasChildNodes())
                return XMLUtil.toString(doc);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getTableName() {
        return "qc";
    }
}
