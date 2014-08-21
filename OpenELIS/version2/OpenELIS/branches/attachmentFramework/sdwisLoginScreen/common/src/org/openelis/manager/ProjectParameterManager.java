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
package org.openelis.manager;

import java.io.Serializable;
import java.util.ArrayList;

import org.openelis.domain.ProjectParameterDO;

public class ProjectParameterManager implements Serializable {

    private static final long                                  serialVersionUID = 1L;

    protected Integer                                          projectId;
    protected ArrayList<ProjectParameterDO>               parameters, deleted;

    protected transient static ProjectParameterManagerProxy proxy;

    protected ProjectParameterManager() {
    }

    /**
     * Creates a new instance of this object.
     */
    public static ProjectParameterManager getInstance() {
        return new ProjectParameterManager();
    }

    public int count() {
        if (parameters == null)
            return 0;
        return parameters.size();
    }

    public ProjectParameterDO getParameterAt(int i) {
        return parameters.get(i);
    }

    public void setParameterAt(ProjectParameterDO parameter, int i) {
        if (parameters == null)
            parameters = new ArrayList<ProjectParameterDO>();
        parameters.set(i, parameter);
    }

    public void addParameter(ProjectParameterDO parameter) {
        if (parameters == null)
            parameters = new ArrayList<ProjectParameterDO>();
        parameters.add(parameter);
    }

    public void addParameterAt(ProjectParameterDO parameter, int i) {
        if (parameters == null)
            parameters = new ArrayList<ProjectParameterDO>();
        parameters.add(i, parameter);
    }

    public void removeParameterAt(int i) {
        ProjectParameterDO tmp;

        if (parameters == null || i >= parameters.size())
            return;

        tmp = parameters.remove(i);
        if (tmp.getId() != null) {
            if (deleted == null)
                deleted = new ArrayList<ProjectParameterDO>();
            deleted.add(tmp);
        }
    }

    // service methods
    public static ProjectParameterManager fetchByProjectId(Integer id) throws Exception {
        return proxy().fetchByProjectId(id);
    }

    public ProjectParameterManager add() throws Exception {
        return proxy().add(this);
    }

    public ProjectParameterManager update() throws Exception {
        return proxy().update(this);
    }

    public void validate() throws Exception {
        proxy().validate(this);
    }

    // friendly methods used by managers and proxies
    Integer getProjectId() {
        return projectId;
    }

    void setProjectId(Integer id) {
        projectId = id;
    }

    ArrayList<ProjectParameterDO> getParameters() {
        return parameters;
    }

    void setParameters(ArrayList<ProjectParameterDO> parameters) {
        this.parameters = parameters;
    }

    int deleteCount() {
        if (deleted == null)
            return 0;
        return deleted.size();
    }

    ProjectParameterDO getDeletedAt(int i) {
        return deleted.get(i);
    }

    private static ProjectParameterManagerProxy proxy() {
        if (proxy == null)
            proxy = new ProjectParameterManagerProxy();
        return proxy;
    }
}
