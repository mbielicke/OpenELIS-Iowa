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
 * SampleProject Entity POJO for database
 */

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
import org.openelis.utilcommon.AuditActivity;
import org.openelis.utils.Audit;
import org.openelis.utils.AuditUtil;
import org.openelis.utils.Auditable;

@NamedQueries( {
    @NamedQuery( name = "SampleProject.FetchBySampleId",
                query = "select new org.openelis.domain.SampleProjectViewDO(sp.id, sp.sampleId, sp.projectId, sp.isPermanent, p.name, p.description)"
                      + " from SampleProject sp left join sp.project p where sp.sampleId = :id order by sp.isPermanent desc, sp.id asc"),
    @NamedQuery( name = "SampleProject.FetchPermanentBySampleId",
                query = "select new org.openelis.domain.SampleProjectViewDO(sp.id, sp.sampleId, sp.projectId, sp.isPermanent, p.name, p.description)"
                      + " from SampleProject sp left join sp.project p where sp.sampleId = :id and sp.isPermanent = 'Y' order by sp.id"),
    @NamedQuery( name = "SampleProject.FetchPermanentProjectList",
                query = "select new org.openelis.domain.IdNameVO(p.id, p.name)" 
                      + " from SampleProject sp, Project p"
                      + " where sp.projectId = p.id and sp.isPermanent = 'Y' order by p.name")})
@Entity
@Table(name = "sample_project")
@EntityListeners({AuditUtil.class})
public class SampleProject implements Auditable, Cloneable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer       id;

    @Column(name = "sample_id")
    private Integer       sampleId;

    @Column(name = "project_id")
    private Integer       projectId;

    @Column(name = "is_permanent")
    private String        isPermanent;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", insertable = false, updatable = false)
    private Project       project;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sample_id", insertable = false, updatable = false)
    private Sample        sample;

    @Transient
    private SampleProject original;

    public Integer getId() {
        return id;
    }

    protected void setId(Integer id) {
        if (DataBaseUtil.isDifferent(id, this.id))
            this.id = id;
    }

    public Integer getSampleId() {
        return sampleId;
    }

    public void setSampleId(Integer sampleId) {
        if (DataBaseUtil.isDifferent(sampleId, this.sampleId))
            this.sampleId = sampleId;
    }

    public Integer getProjectId() {
        return projectId;
    }

    public void setProjectId(Integer projectId) {
        if (DataBaseUtil.isDifferent(projectId, this.projectId))
            this.projectId = projectId;
    }

    public String getIsPermanent() {
        return isPermanent;
    }

    public void setIsPermanent(String isPermanent) {
        if (DataBaseUtil.isDifferent(isPermanent, this.isPermanent))
            this.isPermanent = isPermanent;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public Sample getSample() {
        return sample;
    }

    public void setSample(Sample sample) {
        this.sample = sample;
    }

    public void setClone() {
        try {
            original = (SampleProject)this.clone();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Audit getAudit(AuditActivity activity) {
        Audit audit;

        audit = new Audit(activity);
        audit.setReferenceTableId(ReferenceTable.SAMPLE_PROJECT);
        audit.setReferenceId(getId());
        if (original != null)
            audit.setField("id", id, original.id)
                 .setField("sample_id", sampleId, original.sampleId)
                 .setField("project_id", projectId, original.projectId, ReferenceTable.PROJECT)
                 .setField("is_permanent", isPermanent, original.isPermanent);

        return audit;
    }
}
