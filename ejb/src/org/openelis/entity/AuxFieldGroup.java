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
 * AuxFieldGroup Entity POJO for database
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

@NamedQueries( {
    @NamedQuery( name = "AuxFieldGroup.FetchByName",
                query = "select distinct new org.openelis.domain.AuxFieldGroupDO(afg.id,afg.name,afg.description," +
                        "afg.isActive,afg.activeBegin,afg.activeEnd)"
                      + " from AuxFieldGroup afg where afg.name = :name order by afg.name"),
    @NamedQuery( name = "AuxFieldGroup.FetchById",                
                query = "select distinct new org.openelis.domain.AuxFieldGroupDO(afg.id,afg.name,afg.description," +
                        "afg.isActive,afg.activeBegin,afg.activeEnd)"
                      + " from AuxFieldGroup afg where afg.id = :id "),
    @NamedQuery( name = "AuxFieldGroup.FetchActive",
                query = "select distinct new org.openelis.domain.AuxFieldGroupDO(afg.id,afg.name,afg.description," +
                        "afg.isActive,afg.activeBegin,afg.activeEnd)"
                      + " from AuxFieldGroup afg where afg.isActive = 'Y' order by afg.name")})
@Entity
@Table(name = "aux_field_group")
@EntityListeners( {AuditUtil.class})
public class AuxFieldGroup implements Auditable, Cloneable {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Integer              id;

    @Column(name = "name")
    private String               name;

    @Column(name = "description")
    private String               description;

    @Column(name = "is_active")
    private String               isActive;

    @Column(name = "active_begin")
    private Date                 activeBegin;

    @Column(name = "active_end")
    private Date                 activeEnd;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "aux_field_group_id", insertable = false, updatable = false)
    private Collection<AuxField> auxField;

    @Transient
    private AuxFieldGroup        original;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        if (DataBaseUtil.isDifferent(description, this.description))
            this.description = description;
    }

    public String getIsActive() {
        return isActive;
    }

    public void setIsActive(String isActive) {
        if (DataBaseUtil.isDifferent(isActive, this.isActive))
            this.isActive = isActive;
    }

    public Datetime getActiveBegin() {
        return DataBaseUtil.toYD(activeBegin);
    }

    public void setActiveBegin(Datetime active_begin) {
        if (DataBaseUtil.isDifferentYD(active_begin, this.activeBegin))
            this.activeBegin = DataBaseUtil.toDate(active_begin);
    }

    public Datetime getActiveEnd() {
        return DataBaseUtil.toYD(activeEnd);
    }

    public void setActiveEnd(Datetime active_end) {
        if (DataBaseUtil.isDifferentYD(active_end, this.activeEnd))
            this.activeEnd = DataBaseUtil.toDate(active_end);
    }

    public Collection<AuxField> getAuxField() {
        return auxField;
    }

    public void setAuxField(Collection<AuxField> auxField) {
        this.auxField = auxField;
    }

    public void setClone() {
        try {
            original = (AuxFieldGroup)this.clone();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Audit getAudit() {
        Audit audit;

        audit = new Audit();
        audit.setReferenceTableId(ReferenceTable.AUX_FIELD_GROUP);
        audit.setReferenceId(getId());
        if (original != null)
            audit.setField("id", id, original.id)
                 .setField("name", name, original.name)
                 .setField("description", description, original.description)
                 .setField("is_active", isActive, original.isActive)
                 .setField("active_begin", activeBegin, original.activeBegin)
                 .setField("active_end", activeEnd, original.activeEnd);

        return audit;
    }

}
