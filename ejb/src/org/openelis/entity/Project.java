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
  * Project Entity POJO for database 
  */

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.openelis.gwt.common.Datetime;
import org.openelis.util.XMLUtil;

import java.util.Collection;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.openelis.utilcommon.DataBaseUtil;
import org.openelis.utils.AuditUtil;
import org.openelis.utils.Auditable;

@NamedQueries( {
    @NamedQuery(name = "Project.FetchById", 
    		    query = "select new org.openelis.domain.ProjectViewDO(p.id, p.name, " + 
                        "p.description, p.startedDate, p.completedDate, p.isActive, p.referenceTo, p.ownerId, s.id,s.name, '')" 
                      + " from Project p left join p.scriptlet s where p.id = :id "),
    @NamedQuery( name = "Project.FetchByName", 
                query = "select new org.openelis.domain.ProjectDO(p.id, p.name," + 
                        "p.description, p.startedDate, p.completedDate, p.isActive, p.referenceTo, p.ownerId, s.id)" 
                      + " from Project p left join p.scriptlet s where p.name like :name"),
    @NamedQuery( name = "Project.FetchActiveByName",
    		    query = "select new org.openelis.domain.ProjectDO(p.id, p.name," + 
                	    "p.description, p.startedDate, p.completedDate, p.isActive, p.referenceTo, p.ownerId, s.id)" 
    			      + " from Project p left join p.scriptlet s where p.name like :name and p.isActive = 'Y'")})
                
@Entity
@Table(name="project")
@EntityListeners({AuditUtil.class})
public class Project implements Auditable, Cloneable {
  
  @Id
  @GeneratedValue
  @Column(name="id")
  private Integer id;             

  @Column(name="name")
  private String name;             

  @Column(name="description")
  private String description;             

  @Column(name="started_date")
  private Date startedDate;             

  @Column(name="completed_date")
  private Date completedDate;             

  @Column(name="is_active")
  private String isActive;             

  @Column(name="reference_to")
  private String referenceTo;             

  @Column(name="owner_id")
  private Integer ownerId;             

  @Column(name="scriptlet_id")
  private Integer scriptletId;             

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "scriptlet_id",insertable = false, updatable = false)
  private Scriptlet scriptlet;

  @OneToMany(fetch = FetchType.LAZY)
  @JoinColumn(name = "project_id")
  private Collection<ProjectParameter> projectParameter;
  
  @Transient
  private Project original;
  
  public Integer getId() {
        return id;
    }

    protected void setId(Integer id) {
        if (DataBaseUtil.isDifferent(this.id, id))
            this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (DataBaseUtil.isDifferent(this.name, name))
            this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        if (DataBaseUtil.isDifferent(this.description, description))
            this.description = description;
    }

    public Datetime getStartedDate() {
        return DataBaseUtil.toYD(startedDate);
    }

    public void setStartedDate(Datetime started_date) {
        if (DataBaseUtil.isDifferentYD(this.startedDate, started_date))
            this.startedDate = DataBaseUtil.toDate(started_date);
    }

    public Datetime getCompletedDate() {
        return DataBaseUtil.toYD(completedDate);
    }

    public void setCompletedDate(Datetime completed_date) {
        if (DataBaseUtil.isDifferentYD(this.completedDate, completed_date))
            this.completedDate = DataBaseUtil.toDate(completed_date);
    }

    public String getIsActive() {
        return isActive;
    }

    public void setIsActive(String isActive) {
        if (DataBaseUtil.isDifferent(this.isActive, isActive))
            this.isActive = isActive;
    }

    public String getReferenceTo() {
        return referenceTo;
    }

    public void setReferenceTo(String referenceTo) {
        if (DataBaseUtil.isDifferent(this.referenceTo, referenceTo))
            this.referenceTo = referenceTo;
    }

    public Integer getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Integer ownerId) {
        if (DataBaseUtil.isDifferent(this.ownerId, ownerId))
            this.ownerId = ownerId;
    }

    public Integer getScriptletId() {
        return scriptletId;
    }

    public void setScriptletId(Integer scriptletId) {
        if (DataBaseUtil.isDifferent(this.scriptletId, scriptletId))
            this.scriptletId = scriptletId;
    }

    public Scriptlet getScriptlet() {
        return scriptlet;
    }

    public void setScriptlet(Scriptlet scriptlet) {
        this.scriptlet = scriptlet;
    }

    public Collection<ProjectParameter> getProjectParameter() {
        return projectParameter;
    }

    public void setProjectParameter(Collection<ProjectParameter> projectParameter) {
        this.projectParameter = projectParameter;
    }

    public void setClone() {
        try {
            original = (Project)this.clone();
        } catch (Exception e) {
        }
    }

    public String getChangeXML() {
        try {
            Document doc = XMLUtil.createNew("change");
            Element root = doc.getDocumentElement();

            AuditUtil.getChangeXML(id, original.id, doc, "id");
            AuditUtil.getChangeXML(name, original.name, doc, "name");
            AuditUtil.getChangeXML(description, original.description, doc, "description");
            AuditUtil.getChangeXML(startedDate, original.startedDate, doc, "started_date");
            AuditUtil.getChangeXML(completedDate, original.completedDate, doc, "completed_date");
            AuditUtil.getChangeXML(isActive, original.isActive, doc, "is_active");
            AuditUtil.getChangeXML(referenceTo, original.referenceTo, doc, "reference_to");
            AuditUtil.getChangeXML(ownerId, original.ownerId, doc, "owner_id");
            AuditUtil.getChangeXML(scriptletId, original.scriptletId, doc, "scriptlet_id");

            if (root.hasChildNodes())
                return XMLUtil.toString(doc);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getTableName() {
        return "project";
    }
}
