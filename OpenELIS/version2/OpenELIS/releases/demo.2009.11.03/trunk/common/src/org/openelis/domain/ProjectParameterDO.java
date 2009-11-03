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
package org.openelis.domain;

import org.openelis.utilcommon.DataBaseUtil;

/**
 * Class represents the fields in database table project_parameter.
 */

public class ProjectParameterDO extends DataObject {

    private static final long serialVersionUID = 1L;

    protected Integer         id, projectId, operationId;
    protected String          parameter, value;

    public ProjectParameterDO() {
    }

    public ProjectParameterDO(Integer id, Integer projectId, String parameter, Integer operationId,
                              String value) {
        setId(id);
        setProjectId(projectId);
        setParameter(parameter);
        setOperationId(operationId);
        setValue(value);
        _changed = false;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
        _changed = true;
    }

    public Integer getProjectId() {
        return projectId;
    }

    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
        _changed = true;
    }

    public String getParameter() {
        return parameter;
    }

    public void setParameter(String parameter) {
        this.parameter = DataBaseUtil.trim(parameter);
        _changed = true;
    }

    public Integer getOperationId() {
        return operationId;
    }

    public void setOperationId(Integer operationId) {
        this.operationId = operationId;
        _changed = true;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = DataBaseUtil.trim(value);
        _changed = true;
    }
}
