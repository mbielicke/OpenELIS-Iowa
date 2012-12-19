package org.openelis.entity;

/**
 * WorksheetItem Entity POJO for database
 */

import java.util.Collection;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.openelis.gwt.common.DataBaseUtil;

@NamedQueries({
    @NamedQuery( name = "WorksheetItem.FetchById",
                query = "select new org.openelis.domain.WorksheetItemDO(wi.id,wi.worksheetId,wi.position) " +
                        "from WorksheetItem wi where wi.id = :id"),
    @NamedQuery( name = "WorksheetItem.FetchByWorksheetId",
                query = "select new org.openelis.domain.WorksheetItemDO(wi.id,wi.worksheetId,wi.position) " +
                        "from WorksheetItem wi where wi.worksheetId = :id")})
@Entity
@Table(name = "worksheet_item")
public class WorksheetItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer                       id;

    @Column(name = "worksheet_id")
    private Integer                       worksheetId;

    @Column(name = "position")
    private Integer                       position;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "worksheet_item_id")
    private Collection<WorksheetAnalysis> worksheetAnalysis;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "worksheet_id", insertable = false, updatable = false)
    private Worksheet                     worksheet;

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

    public Worksheet getWorksheet() {
        return worksheet;
    }

    public void setWorksheet(Worksheet worksheet) {
        this.worksheet = worksheet;
    }
}
