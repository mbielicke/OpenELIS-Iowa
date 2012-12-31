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
 * Method Entity POJO for database
 */

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.openelis.domain.Constants;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.Datetime;
import org.openelis.utils.Audit;
import org.openelis.utils.AuditUtil;
import org.openelis.utils.Auditable;

@NamedQueries( {
    @NamedQuery( name = "Method.FetchByName", 
                query = "select distinct new org.openelis.domain.MethodDO(m.id,m.name,m.description," +
                        "m.reportingDescription,m.isActive,m.activeBegin, m.activeEnd)"
                      + " from Method m where m.name = :name order by m.name"),
    @NamedQuery( name = "Method.FetchById",
                query = "select distinct new org.openelis.domain.MethodDO(m.id,m.name,m.description," +
                        "m.reportingDescription,m.isActive,m.activeBegin, m.activeEnd)"
                      + " from Method m where m.id = :id"),
    @NamedQuery( name = "Method.FetchByIds",
                query = "select distinct new org.openelis.domain.MethodDO(m.id, m.name, m.description," +
                        "m.reportingDescription, m.isActive, m.activeBegin, m.activeEnd)"
                      + " from Method m where m.id in (:ids)"),                  
    @NamedQuery( name = "Method.FetchEntityByName",
                query = "from Method m where m.name = :name order by m.name"),
    @NamedQuery( name = "Method.FetchActiveByName",
                query = "select distinct new org.openelis.domain.MethodDO(m.id,m.name,m.description," +
                        "m.reportingDescription,m.isActive,m.activeBegin, m.activeEnd)"
                      + " from Method m where m.name like :name and m.isActive = 'Y' order by m.name ")})
@Entity
@Table(name = "method")
@EntityListeners({AuditUtil.class})
public class Method implements Auditable, Cloneable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "name")
    private String  name;

    @Column(name = "description")
    private String  description;

    @Column(name = "reporting_description")
    private String  reportingDescription;

    @Column(name = "is_active")
    private String  isActive;

    @Column(name = "active_begin")
    private Date    activeBegin;

    @Column(name = "active_end")
    private Date    activeEnd;

    @Transient
    private Method  original;

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

    public String getReportingDescription() {
        return reportingDescription;
    }

    public void setReportingDescription(String reportingDescription) {
        if (DataBaseUtil.isDifferent(reportingDescription, this.reportingDescription))
            this.reportingDescription = reportingDescription;
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

    public void setActiveBegin(Datetime activeBegin) {
        if (DataBaseUtil.isDifferentYD(activeBegin, this.activeBegin))
            this.activeBegin = DataBaseUtil.toDate(activeBegin);
    }

    public Datetime getActiveEnd() {
        return DataBaseUtil.toYD(activeEnd);
    }

    public void setActiveEnd(Datetime activeEnd) {
        if (DataBaseUtil.isDifferentYD(activeEnd, this.activeEnd))
            this.activeEnd = DataBaseUtil.toDate(activeEnd);
    }

    public void setClone() {
        try {
            original = (Method)this.clone();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Audit getAudit(Integer activity) {
        Audit audit;

        audit = new Audit(activity);
        audit.setReferenceTableId(Constants.table().METHOD);
        audit.setReferenceId(getId());
        if (original != null)
            audit.setField("id", id, original.id)
                 .setField("name", name, original.name)
                 .setField("description", description, original.description)
                 .setField("reporting_description", reportingDescription, original.reportingDescription)
                 .setField("is_active", isActive, original.isActive)
                 .setField("active_begin", activeBegin, original.activeBegin)
                 .setField("active_end", activeEnd, original.activeEnd);

        return audit;
    }
}
