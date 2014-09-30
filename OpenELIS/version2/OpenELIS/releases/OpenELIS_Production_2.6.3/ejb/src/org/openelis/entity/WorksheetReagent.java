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

import org.openelis.ui.common.DataBaseUtil;
import org.openelis.utils.AuditUtil;

@NamedQueries({
    @NamedQuery( name = "WorksheetReagent.FetchById",
                query = "select new org.openelis.domain.WorksheetReagentViewDO(wr.id, wr.worksheetId, wr.sortOrder, wr.qcLotId,"
                	  +	"qcl.lotNumber, qcl.locationId, qcl.preparedDate, qcl.preparedVolume, qcl.preparedUnitId,"
                      + "qcl.preparedById, qcl.usableDate, qcl.expireDate, qcl.isActive, l.entry, pu.entry, qc.name)"
                      + " from WorksheetReagent wr join wr.qcLot qcl join qcl.qc qc"
                      + " left join qcl.location l left join qcl.preparedUnit pu"
                      + " where wr.id = :id order by wr.sortOrder"),                       
    @NamedQuery( name = "WorksheetReagent.FetchByWorksheetId",
                query = "select new org.openelis.domain.WorksheetReagentViewDO(wr.id, wr.worksheetId, wr.sortOrder, wr.qcLotId,"
                      + "qcl.lotNumber, qcl.locationId, qcl.preparedDate, qcl.preparedVolume, qcl.preparedUnitId,"
                      + "qcl.preparedById, qcl.usableDate, qcl.expireDate, qcl.isActive, l.entry, pu.entry, qc.name)"
                      + " from WorksheetReagent wr join wr.qcLot qcl join qcl.qc qc"
                      + " left join qcl.location l left join qcl.preparedUnit pu"
                      + " where wr.worksheetId = :id order by wr.sortOrder"),                       
    @NamedQuery( name = "WorksheetReagent.FetchByWorksheetIds",
                query = "select new org.openelis.domain.WorksheetReagentViewDO(wr.id, wr.worksheetId, wr.sortOrder, wr.qcLotId,"
                      + "qcl.lotNumber, qcl.locationId, qcl.preparedDate, qcl.preparedVolume, qcl.preparedUnitId,"
                      + "qcl.preparedById, qcl.usableDate, qcl.expireDate, qcl.isActive, l.entry, pu.entry, qc.name)"
                      + " from WorksheetReagent wr join wr.qcLot qcl join qcl.qc qc"
                      + " left join qcl.location l left join qcl.preparedUnit pu"
                      + " where wr.worksheetId in :ids order by wr.worksheetId, wr.sortOrder")/*,                       
    @NamedQuery( name = "QcLot.FetchByQcId",
                query = "select new org.openelis.domain.QcLotViewDO(qcl.id, qcl.qcId, "
                      + "qcl.lotNumber, qcl.locationId, qcl.preparedDate, qcl.preparedVolume, qcl.preparedUnitId,"
                      + "qcl.preparedById, qcl.usableDate, qcl.expireDate, qcl.isActive, qc.name)"
                      + " from QcLot qcl left join qcl.qc qc where qcl.qcId = :id order by qcl.isActive DESC, qcl.id DESC"),                       
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
                      +	" qc.name like :name order by qc.name, qcl.lotNumber")*/})    
                                       
@Entity
@Table(name = "worksheet_reagent")
@EntityListeners({AuditUtil.class})
public class WorksheetReagent /*implements Auditable, Cloneable*/ {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer    id;

    @Column(name = "worksheet_id")
    private Integer    worksheetId;

    @Column(name = "sort_order")
    private Integer    sortOrder;

    @Column(name = "qc_lot_id")
    private Integer    qcLotId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "qc_lot_id", insertable = false, updatable = false)
    private QcLot      qcLot;

//    @Transient
//    private WorksheetReagent original;

    public Integer getId() {
        return id;
    }

    protected void setId(Integer id) {
        if (DataBaseUtil.isDifferent(id, this.id))
            this.id = id;
    }

    public Integer getWorksheetId() {
        return worksheetId;
    }

    public void setWorksheetId(Integer worksheetId) {
        if (DataBaseUtil.isDifferent(worksheetId, this.worksheetId))
            this.worksheetId = worksheetId;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        if (DataBaseUtil.isDifferent(sortOrder, this.sortOrder))
            this.sortOrder = sortOrder;
    }

    public Integer getQcLotId() {
        return qcLotId;
    }

    public void setQcLotId(Integer qcLotId) {
        if (DataBaseUtil.isDifferent(qcLotId, this.qcLotId))
            this.qcLotId = qcLotId;
    }

    public QcLot getQcLot() {
        return qcLot;
    }

    public void setQcLot(QcLot qcLot) {
        this.qcLot = qcLot;
    }

//    public void setClone() {
//        try {
//            original = (WorksheetReagent)this.clone();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    public Audit getAudit(Integer activity) {
//        Audit audit;
//
//        audit = new Audit(activity);
//        audit.setReferenceTableId(Constants.table().QC_LOT);
//        audit.setReferenceId(getId());
//        if (original != null)
//            audit.setField("id", id, original.id)
//                 .setField("worksheet_id", worksheetId, original.worksheetId)
//                 .setField("sort_order", sortOrder, original.sortOrder)
//                 .setField("qc_lot_id", qcLotId, original.qcLotId);
//
//        return audit;
//    }
}
