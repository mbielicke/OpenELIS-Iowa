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
 * Worksheet Entity POJO for database
 */

import java.util.Collection;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.openelis.domain.Constants;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.Datetime;
import org.openelis.utils.Audit;
import org.openelis.utils.AuditUtil;
import org.openelis.utils.Auditable;

@NamedQueries({
    @NamedQuery( name = "Worksheet.FetchById",
                query = "select new org.openelis.domain.WorksheetViewDO(w.id,w.createdDate,w.systemUserId,w.statusId,w.formatId,w.subsetCapacity,w.relatedWorksheetId,w.instrumentId,i.name,w.description) "
                      + " from Worksheet w left join w.instrument i where w.id = :id"),
    @NamedQuery( name = "Worksheet.FetchByIds",
                query = "select new org.openelis.domain.WorksheetViewDO(w.id,w.createdDate,w.systemUserId,w.statusId,w.formatId,w.subsetCapacity,w.relatedWorksheetId,w.instrumentId,i.name,w.description) "
                      + " from Worksheet w left join w.instrument i where w.id in (:ids)"),
    @NamedQuery( name = "Worksheet.FetchByAnalysisId",
                query = "select distinct new org.openelis.domain.WorksheetViewDO(w.id,w.createdDate,w.systemUserId,w.statusId,w.formatId,w.subsetCapacity,w.relatedWorksheetId,w.instrumentId,i.name,w.description) "
                      + " from Worksheet w join w.worksheetItem wi join wi.worksheetAnalysis wa left join w.instrument i where wa.analysisId = :id"),
    @NamedQuery( name = "Worksheet.FetchByAnalysisIds",
                query = "select distinct new org.openelis.domain.AnalysisWorksheetVO(w.id,w.createdDate,w.systemUserId,w.statusId,w.formatId,w.subsetCapacity,w.relatedWorksheetId,w.instrumentId,i.name,w.description, wa.analysisId) "
                      + " from Worksheet w join w.worksheetItem wi join wi.worksheetAnalysis wa left join w.instrument i where wa.analysisId in (:ids)")})

@Entity
@Table(name = "worksheet")
@EntityListeners({AuditUtil.class})
public class Worksheet implements Auditable, Cloneable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @Column(name = "subset_capacity")
    private Integer                   subsetCapacity;

    @Column(name = "related_worksheet_id")
    private Integer                   relatedWorksheetId;

    @Column(name = "instrument_id")
    private Integer                   instrumentId;

    @Column(name = "description")
    private String                    description;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "worksheet_id", insertable = false, updatable = false)
    private Collection<WorksheetItem> worksheetItem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "instrument_id", insertable = false, updatable = false)
    private Instrument                instrument;

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

    public Integer getSubsetCapacity() {
        return subsetCapacity;
    }

    public void setSubsetCapacity(Integer subsetCapacity) {
        if (DataBaseUtil.isDifferent(subsetCapacity, this.subsetCapacity))
            this.subsetCapacity = subsetCapacity;
    }

    public Integer getRelatedWorksheetId() {
        return relatedWorksheetId;
    }

    public void setRelatedWorksheetId(Integer relatedWorksheetId) {
        if (DataBaseUtil.isDifferent(relatedWorksheetId, this.relatedWorksheetId))
            this.relatedWorksheetId = relatedWorksheetId;
    }

    public Integer getInstrumentId() {
        return instrumentId;
    }

    public void setInstrumentId(Integer instrumentId) {
        if (DataBaseUtil.isDifferent(instrumentId, this.instrumentId))
            this.instrumentId = instrumentId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        if (DataBaseUtil.isDifferent(description, this.description))
            this.description = description;
    }

    public Collection<WorksheetItem> getWorksheetItem() {
        return worksheetItem;
    }

    public void setWorksheetItem(Collection<WorksheetItem> worksheetItem) {
        this.worksheetItem = worksheetItem;
    }

    public Instrument getInstrument() {
        return instrument;
    }

    public void setInstrument(Instrument instrument) {
        this.instrument = instrument;
    }

    public void setClone() {
        try {
            original = (Worksheet)this.clone();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Audit getAudit(Integer activity) {
        Audit audit;

        audit = new Audit(activity);
        audit.setReferenceTableId(Constants.table().WORKSHEET);
        audit.setReferenceId(getId());
        if (original != null)
            audit.setField("id", id, original.id)
                 .setField("created_date", createdDate, original.createdDate)
                 .setField("system_user_id", systemUserId, original.systemUserId)
                 .setField("status_id", statusId, original.statusId, Constants.table().DICTIONARY)
                 .setField("format_id", formatId, original.formatId, Constants.table().DICTIONARY)
                 .setField("subset_capacity", subsetCapacity, original.subsetCapacity)
                 .setField("related_worksheet_id", relatedWorksheetId, original.relatedWorksheetId, Constants.table().WORKSHEET)
                 .setField("instrument_id", instrumentId, original.instrumentId, Constants.table().INSTRUMENT)
                 .setField("description", description, original.description);

        return audit;
    }
}
