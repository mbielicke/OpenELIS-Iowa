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
 * ProjectParameter Entity POJO for database
 */

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
import org.openelis.utils.Audit;
import org.openelis.utils.AuditUtil;
import org.openelis.utils.Auditable;

@NamedQueries({
    @NamedQuery( name = "ProjectParameter.FetchByProjectId",
                query = "select distinct new org.openelis.domain.ProjectParameterDO(p.id,p.projectId," +
                        "p.parameter,p.operationId,p.value)"
                      + " from ProjectParameter p where p.projectId = :projectId")})
   
@Entity
@Table(name = "project_parameter")
@EntityListeners({AuditUtil.class})
public class ProjectParameter implements Auditable, Cloneable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer          id;

    @Column(name = "project_id")
    private Integer          projectId;

    @Column(name = "parameter")
    private String           parameter;

    @Column(name = "operation_id")
    private Integer          operationId;

    @Column(name = "value")
    private String           value;

    @Transient
    private ProjectParameter original;

    public Integer getId() {
        return id;
    }

    protected void setId(Integer id) {
        if (DataBaseUtil.isDifferent(id, this.id))
            this.id = id;
    }

    public Integer getProjectId() {
        return projectId;
    }

    public void setProjectId(Integer projectId) {
        if (DataBaseUtil.isDifferent(projectId, this.projectId))
            this.projectId = projectId;
    }

    public String getParameter() {
        return parameter;
    }

    public void setParameter(String parameter) {
        if (DataBaseUtil.isDifferent(parameter, this.parameter))
            this.parameter = parameter;
    }

    public Integer getOperationId() {
        return operationId;
    }

    public void setOperationId(Integer operationId) {
        if (DataBaseUtil.isDifferent(operationId, this.operationId))
            this.operationId = operationId;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        if (DataBaseUtil.isDifferent(value, this.value))
            this.value = value;
    }

    public void setClone() {
        try {
            original = (ProjectParameter)this.clone();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Audit getAudit(Integer activity) {
        Audit audit;

        audit = new Audit(activity);
        audit.setReferenceTableId(Constants.table().PROJECT_PARAMETER);
        audit.setReferenceId(getId());
        if (original != null)
            audit.setField("id", id, original.id)
                 .setField("project_id", projectId, original.projectId)
                 .setField("parameter", parameter, original.parameter)
                 .setField("operation_id", operationId, original.operationId, Constants.table().DICTIONARY)
                 .setField("value", value, original.value);
        
        return audit;
    }

}
