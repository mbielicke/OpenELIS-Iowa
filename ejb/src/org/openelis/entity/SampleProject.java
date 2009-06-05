/** Exhibit A - UIRF Open-source Based Public Software License.
* 
* The contents of this file are subject to the UIRF Open-source Based
* Public Software License(the "License"); you may not use this file except
* in compliance with the License. You may obtain a copy of the License at
* openelis.uhl.uiowa.edu
* 
* Software distributed under the License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
* License for the specific language governing rights and limitations
* under the License.
* 
* The Original Code is OpenELIS code.
* 
* The Initial Developer of the Original Code is The University of Iowa.
* Portions created by The University of Iowa are Copyright 2006-2008. All
* Rights Reserved.
* 
* Contributor(s): ______________________________________.
* 
* Alternatively, the contents of this file marked
* "Separately-Licensed" may be used under the terms of a UIRF Software
* license ("UIRF Software License"), in which case the provisions of a
* UIRF Software License are applicable instead of those above. 
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
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.openelis.util.XMLUtil;
import org.openelis.utils.AuditUtil;
import org.openelis.utils.Auditable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

@NamedQueries( {
    @NamedQuery(name = "SampleProject.SampleProjectBySampleId", query = "select new org.openelis.domain.SampleProjectDO(sp.id, sp.sampleId, " + 
                " sp.projectId, sp.isPermanent, p.name, p.description, p.startedDate, p.completedDate, p.isActive, p.referenceTo, " + 
                " p.ownerId, p.scriptletId) from SampleProject sp LEFT JOIN sp.project p where sp.sampleId = :id order by sp.isPermanent DESC, p.name")})
@Entity
@Table(name="sample_project")
@EntityListeners({AuditUtil.class})
public class SampleProject implements Auditable, Cloneable {
  
  @Id
  @GeneratedValue
  @Column(name="id")
  private Integer id;             

  @Column(name="sample_id")
  private Integer sampleId;             

  @Column(name="project_id")
  private Integer projectId;             

  @Column(name="is_permanent")
  private String isPermanent;             

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "project_id", insertable = false, updatable = false)
  private Project project;
  
  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "sample_id", insertable = false, updatable = false)
  private Sample sample;

  @Transient
  private SampleProject original;

  
  public Integer getId() {
    return id;
  }
  protected void setId(Integer id) {
    if((id == null && this.id != null) || 
       (id != null && !id.equals(this.id)))
      this.id = id;
  }

  public Integer getSampleId() {
    return sampleId;
  }
  public void setSampleId(Integer sampleId) {
    if((sampleId == null && this.sampleId != null) || 
       (sampleId != null && !sampleId.equals(this.sampleId)))
      this.sampleId = sampleId;
  }

  public Integer getProjectId() {
    return projectId;
  }
  public void setProjectId(Integer projectId) {
    if((projectId == null && this.projectId != null) || 
       (projectId != null && !projectId.equals(this.projectId)))
      this.projectId = projectId;
  }

  public String getIsPermanent() {
    return isPermanent;
  }
  public void setIsPermanent(String isPermanent) {
    if((isPermanent == null && this.isPermanent != null) || 
       (isPermanent != null && !isPermanent.equals(this.isPermanent)))
      this.isPermanent = isPermanent;
  }

  
  public void setClone() {
    try {
      original = (SampleProject)this.clone();
    }catch(Exception e){}
  }
  
  public String getChangeXML() {
    try {
      Document doc = XMLUtil.createNew("change");
      Element root = doc.getDocumentElement();
      
      AuditUtil.getChangeXML(id,original.id,doc,"id");

      AuditUtil.getChangeXML(sampleId,original.sampleId,doc,"sample_id");

      AuditUtil.getChangeXML(projectId,original.projectId,doc,"project_id");

      AuditUtil.getChangeXML(isPermanent,original.isPermanent,doc,"is_permanent");

      if(root.hasChildNodes())
        return XMLUtil.toString(doc);
    }catch(Exception e){
      e.printStackTrace();
    }
    return null;
  }
   
  public String getTableName() {
    return "sample_project";
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
  
}   
