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
 * Section Entity POJO for database
 */

import java.util.Collection;

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
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.openelis.domain.ReferenceTable;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.utilcommon.AuditActivity;
import org.openelis.utils.Audit;
import org.openelis.utils.AuditUtil;
import org.openelis.utils.Auditable;

@NamedQueries({
               @NamedQuery( name = "Section.FetchList",
                           query = "select distinct new org.openelis.domain.SectionViewDO(s.id,s.parentSectionId,s.name,s.description,s.isExternal,s.organizationId,ps.name,o.name)"
                                +   " from Section s left join s.organization o left join s.parentSection ps order by s.name"),
                @NamedQuery( name = "Section.FetchByName", 
                            query = "select distinct new org.openelis.domain.SectionDO(s.id,s.parentSectionId,s.name,s.description,s.isExternal,s.organizationId)"
                                +   " from Section s where s.name like :name order by s.name"),
                @NamedQuery( name = "Section.FetchById",
                            query = "select distinct new org.openelis.domain.SectionViewDO(s.id,s.parentSectionId,s.name,s.description,s.isExternal,s.organizationId,ps.name,o.name)"
                                  + " from Section s left join s.organization o left join s.parentSection ps where s.id = :id")})
@Entity
@Table(name = "section")
@EntityListeners({AuditUtil.class})
public class Section implements Auditable, Cloneable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer                      id;

    @Column(name = "parent_section_id")
    private Integer                      parentSectionId;

    @Column(name = "name")
    private String                       name;

    @Column(name = "description")
    private String                       description;

    @Column(name = "is_external")
    private String                       isExternal;

    @Column(name = "organization_id")
    private Integer                      organizationId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", insertable = false, updatable = false)
    private Organization                 organization;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_section_id", insertable = false, updatable = false)
    private Section                      parentSection;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "section_id")
    private Collection<SectionParameter> sectionParameter;

    @Transient
    private Section                      original;

    public Integer getId() {
        return id;
    }

    protected void setId(Integer id) {
        if (DataBaseUtil.isDifferent(id, this.id))
            this.id = id;
    }

    public Integer getParentSectionId() {
        return parentSectionId;
    }

    public void setParentSectionId(Integer parentSectionId) {
        if (DataBaseUtil.isDifferent(parentSectionId, this.parentSectionId))
            this.parentSectionId = parentSectionId;
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

    public String getIsExternal() {
        return isExternal;
    }

    public void setIsExternal(String isExternal) {
        if (DataBaseUtil.isDifferent(isExternal, this.isExternal))
            this.isExternal = isExternal;
    }

    public Integer getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Integer organizationId) {
        if (DataBaseUtil.isDifferent(organizationId, this.organizationId))
            this.organizationId = organizationId;
    }

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    public Section getParentSection() {
        return parentSection;
    }

    public void setParentSection(Section parentSection) {
        this.parentSection = parentSection;
    }

    public Collection<SectionParameter> getSectionParameter() {
        return sectionParameter;
    }

    public void setSectionParameter(Collection<SectionParameter> sectionParameter) {
        this.sectionParameter = sectionParameter;
    }

    public void setClone() {
        try {
            original = (Section)this.clone();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Audit getAudit(AuditActivity activity) {
        Audit audit;

        audit = new Audit(activity);
        audit.setReferenceTableId(ReferenceTable.SECTION);
        audit.setReferenceId(getId());
        if (original != null)
            audit.setField("id", id, original.id)
                 .setField("parent_section_id", parentSectionId, original.parentSectionId, ReferenceTable.SECTION)
                 .setField("name", name, original.name)
                 .setField("description", description, original.description)
                 .setField("is_external", isExternal, original.isExternal)
                 .setField("organization_id", organizationId, original.organizationId, ReferenceTable.ORGANIZATION);

        return audit;
    }
    

}
