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
package org.openelis.modules.project.server;

import java.util.ArrayList;

import javax.ejb.EJB;
import javax.servlet.annotation.WebServlet;

import org.openelis.bean.ProjectBean;
import org.openelis.bean.ProjectManagerBean;
import org.openelis.domain.IdNameVO;
import org.openelis.domain.ProjectDO;
import org.openelis.domain.ProjectViewDO;
import org.openelis.gwt.common.DatabaseException;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.data.Query;
import org.openelis.gwt.server.RemoteServlet;
import org.openelis.manager.ProjectManager;
import org.openelis.manager.ProjectParameterManager;
import org.openelis.modules.project.client.ProjectServiceInt;

@WebServlet("/openelis/project")
public class ProjectServlet extends RemoteServlet implements ProjectServiceInt {
    
    private static final long serialVersionUID = 1L;
    
    @EJB
    ProjectManagerBean projectManager;
    
    @EJB
    ProjectBean        project;
    
    public ArrayList<IdNameVO> fetchList() throws Exception {
        return project.fetchList();
    }
    
    public ProjectManager fetchById(Integer id) throws Exception {
        return projectManager.fetchById(id);
    }
    
    public ArrayList<ProjectDO> fetchActiveByName(String search) throws Exception {
        ArrayList<ProjectDO> list;

        try {
            list = project.fetchActiveByName(search + "%", 50);
        } catch (NotFoundException e) {
            list = new ArrayList<ProjectDO>(0);
        } catch (RuntimeException e) {
            throw new DatabaseException(e);
        }
        return list;
    }
    
    public ProjectViewDO fetchDOById(Integer id) throws Exception {
        return project.fetchById(id);
    }
    
    public ArrayList<IdNameVO> query(Query query) throws Exception {
        return project.query(query.getFields(), query.getPage() * query.getRowsPerPage(), query.getRowsPerPage());
    }

    public ProjectManager fetchWithParameters(Integer id) throws Exception {
        return projectManager.fetchWithParameters(id);
    }

    public ProjectDO fetchSingleByName(String name) throws Exception {
        ArrayList<ProjectDO> list;
        
        list = project.fetchActiveByName(name, 1);
        
        if(list.size() > 0)
            return list.get(0);
        
        return null;
    }

    public ProjectManager add(ProjectManager man) throws Exception {
        return projectManager.add(man);
    }

    public ProjectManager update(ProjectManager man) throws Exception {
        return projectManager.update(man);
    }

    public ProjectManager fetchForUpdate(Integer id) throws Exception {
        return projectManager.fetchForUpdate(id);
    }

    public ProjectManager abortUpdate(Integer id) throws Exception {
        return projectManager.abortUpdate(id);
    }

    //
    // support for ProjectParameterManager
    //
    public ProjectParameterManager fetchParameterByProjectId(Integer id) throws Exception {
        return projectManager.fetchParameterByProjectId(id);
    }
}