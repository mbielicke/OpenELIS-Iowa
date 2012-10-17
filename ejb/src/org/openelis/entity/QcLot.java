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
import org.openelis.utilcommon.AuditActivity;
import org.openelis.utils.Audit;
import org.openelis.utils.AuditUtil;
import org.openelis.utils.Auditable;

@NamedQueries({
    @NamedQuery( name = "QcLot.FetchByQcId",
                query = "select new org.openelis.domain.QcLotViewDO(qcl.id, qcl.qcId, "
                      + "qcl.lotNumber, qcl.locationId, qcl.preparedDate, qcl.preparedVolume, qcl.preparedUnitId,"
                      + "qcl.preparedById, qcl.usableDate, qcl.expireDate, qcl.isActive, qc.name)"
                      + " from QcLot qcl left join qcl.qc qc where qcl.qcId = :id"),                       
   @NamedQuery( name = "QcLot.FetchByLotNumber",
               query = "select new org.openelis.domain.QcLotDO(qcl.id, qcl.qcId, "
                       + "qcl.lotNumber, qcl.locationId, qcl.preparedDate, qcl.preparedVolume, qcl.preparedUnitId,"
                       + "qcl.preparedById, qcl.usableDate, qcl.expireDate, qcl.isActive)"
                       + " from QcLot qcl where qcl.lotNumber = :lotNumber"),
   @NamedQuery( name = "QcLot.FetchActiveByQcName",
                query = "select new org.openelis.domain.QcLotViewDO(qcl.id, qcl.qcId, "
                      + "qcl.lotNumber, qcl.locationId, qcl.preparedDate, qcl.preparedVolume, qcl.preparedUnitId,"
                      + "qcl.preparedById, qcl.usableDate, qcl.expireDate, qcl.isActive, qc.name)"
                      + " from QcLot qcl left join qcl.qc qc where qc.isActive = 'Y' and qcl.isActive = 'Y' and"
                      +	" qc.name like :name order by qc.name, qcl.lotNumber")})    
                                       
@Entity
@Table(name = "qc_lot")
@EntityListeners({AuditUtil.class})
public class QcLot implements Auditable, Cloneable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer               id;

    @Column(name = "qc_id")
    private Integer               qcId;

    @Column(name = "lot_number")
    private String                lotNumber;
    
    @Column(name = "location_id")
    private Integer                locationId;

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
    @JoinColumn(name = "qc_id", insertable = false, updatable = false)
    private Qc                    qc;

    @Transient
    private QcLot                 original;

    public Integer getId() {
        return id;
    }

    protected void setId(Integer id) {
        if (DataBaseUtil.isDifferent(id, this.id))
            this.id = id;
    }

    public Integer getQcId() {
        return qcId;
    }

    public void setQcId(Integer qcId) {
        if (DataBaseUtil.isDifferent(qcId, this.qcId))
            this.qcId = qcId;
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

    public Integer getLocationId() {
        return locationId;
    }

    public void setLocationId(Integer locationId) {
        if (DataBaseUtil.isDifferent(locationId, this.locationId))
            this.locationId = locationId;
    }

    public Qc getQc() {
        return qc;
    }

    public void setQc(Qc qc) {
        this.qc = qc;
    }

    public void setClone() {
        try {
            original = (QcLot)this.clone();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Audit getAudit(AuditActivity activity) {
        Audit audit;

        audit = new Audit(activity);
        audit.setReferenceTableId(ReferenceTable.QC_LOT);
        audit.setReferenceId(getId());
        if (original != null)
            audit.setField("id", id, original.id)
                 .setField("qc_id", qcId, original.qcId)
                 .setField("lot_number", lotNumber, original.lotNumber)
                 .setField("location_id", locationId, original.locationId, ReferenceTable.DICTIONARY)
                 .setField("prepared_date", preparedDate, original.preparedDate)
                 .setField("prepared_volume", preparedVolume, original.preparedVolume)
                 .setField("prepared_unit_id", preparedUnitId, original.preparedUnitId, ReferenceTable.DICTIONARY)
                 .setField("prepared_by_id", preparedById, original.preparedById)
                 .setField("usable_date", usableDate, original.usableDate)
                 .setField("expire_date", expireDate, original.expireDate)
                 .setField("is_active", isActive, original.isActive);

        return audit;
    }
}
