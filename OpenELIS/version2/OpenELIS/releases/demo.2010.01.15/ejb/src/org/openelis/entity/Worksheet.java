package org.openelis.entity;

/**
 * Worksheet Entity POJO for database
 */

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
import javax.persistence.Table;
import javax.persistence.Transient;

import org.openelis.domain.ReferenceTable;
import org.openelis.gwt.common.Datetime;
import org.openelis.utilcommon.DataBaseUtil;
import org.openelis.utils.Audit;
import org.openelis.utils.AuditUtil;
import org.openelis.utils.Auditable;

@NamedQueries({
    @NamedQuery( name = "Worksheet.FetchById",
                query = "select new org.openelis.domain.WorksheetDO(w.id,w.createdDate,w.systemUserId,w.statusId,w.formatId,w.relatedWorksheetId) "+
                        "from Worksheet w where w.id = :id")})

@Entity
@Table(name = "worksheet")
@EntityListeners( {AuditUtil.class})
public class Worksheet implements Auditable, Cloneable {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Integer                   id;

    @Column(name = "created_date")
    private Date                      createdDate;

    @Column(name = "system_user_id")
    private Integer                   systemUserId;

    @Column(name = "status_id")
    private Integer                   statusId;

    @Column(name = "format_id")
    private Integer                   formatId;

    @Column(name = "related_worksheet_id")
    private Integer                   relatedWorksheetId;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "worksheet_id")
    private Collection<WorksheetItem> worksheetItem;

    @Transient
    private Worksheet                 original;

    public Integer getId() {
        return id;
    }

    protected void setId(Integer id) {
        if (DataBaseUtil.isDifferent(id, this.id))
            this.id = id;
    }

    public Datetime getCreatedDate() {
        return DataBaseUtil.toYM(createdDate);
    }

    public void setCreatedDate(Datetime created_date) {
        if (DataBaseUtil.isDifferentYM(created_date, this.createdDate))
            this.createdDate = DataBaseUtil.toDate(created_date);
    }

    public Integer getSystemUserId() {
        return systemUserId;
    }

    public void setSystemUserId(Integer systemUserId) {
        if (DataBaseUtil.isDifferent(systemUserId, this.systemUserId))
            this.systemUserId = systemUserId;
    }

    public Integer getStatusId() {
        return statusId;
    }

    public void setStatusId(Integer statusId) {
        if (DataBaseUtil.isDifferent(statusId, this.statusId))
            this.statusId = statusId;
    }

    public Integer getFormatId() {
        return formatId;
    }

    public void setFormatId(Integer formatId) {
        if (DataBaseUtil.isDifferent(formatId, this.formatId))
            this.formatId = formatId;
    }

    public Integer getRelatedWorksheetId() {
        return relatedWorksheetId;
    }

    public void setRelatedWorksheetId(Integer relatedWorksheetId) {
        if (DataBaseUtil.isDifferent(relatedWorksheetId, this.relatedWorksheetId))
            this.relatedWorksheetId = relatedWorksheetId;
    }

    public Collection<WorksheetItem> getWorksheetItem() {
        return worksheetItem;
    }

    public void setWorksheetItem(Collection<WorksheetItem> worksheetItem) {
        this.worksheetItem = worksheetItem;
    }

    public void setClone() {
        try {
            original = (Worksheet)this.clone();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Audit getAudit() {
        Audit audit;

        audit = new Audit();
        audit.setReferenceTableId(ReferenceTable.WORKSHEET);
        audit.setReferenceId(getId());
        if (original != null)
            audit.setField("id", id, original.id)
                 .setField("created_date", createdDate, original.createdDate)
                 .setField("system_user_id", systemUserId, original.systemUserId)
                 .setField("status_id", statusId, original.statusId)
                 .setField("format_id", formatId, original.formatId)
                 .setField("related_worksheet_id", relatedWorksheetId, original.relatedWorksheetId);

        return audit;
    }
}
