package org.openelis.entity;

/**
 * WorksheetItem Entity POJO for database
 */

import java.util.Collection;

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
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.utils.Audit;
import org.openelis.utils.AuditUtil;
import org.openelis.utils.Auditable;

@NamedQueries({
    @NamedQuery( name = "WorksheetItem.FetchByWorksheetId",
                query = "select new org.openelis.domain.WorksheetItemDO(wi.id,wi.worksheetId,wi.position) " +
                        "from WorksheetItem wi where wi.worksheetId = :id")})
@Entity
@Table(name = "worksheet_item")
@EntityListeners( {AuditUtil.class})
public class WorksheetItem implements Auditable, Cloneable {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Integer                       id;

    @Column(name = "worksheet_id")
    private Integer                       worksheetId;

    @Column(name = "position")
    private Integer                       position;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "worksheet_item_id")
    private Collection<WorksheetAnalysis> worksheetAnalysis;

    @Transient
    private WorksheetItem                 original;

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

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        if (DataBaseUtil.isDifferent(position, this.position != null))
            this.position = position;
    }

    public Collection<WorksheetAnalysis> getWorksheetAnalysis() {
        return worksheetAnalysis;
    }

    public void setWorksheetAnalysis(Collection<WorksheetAnalysis> worksheetAnalysis) {
        this.worksheetAnalysis = worksheetAnalysis;
    }

    public void setClone() {
        try {
            original = (WorksheetItem)this.clone();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Audit getAudit() {
        Audit audit;

        audit = new Audit();
        audit.setReferenceTableId(ReferenceTable.WORKSHEET_ITEM);
        audit.setReferenceId(getId());
        if (original != null)
            audit.setField("id", id, original.id)
                 .setField("worksheet_id", worksheetId, original.worksheetId)
                 .setField("position", position, original.position);

        return audit;
    }
}
