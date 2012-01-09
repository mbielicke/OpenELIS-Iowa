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

import org.openelis.domain.IdNameVO;
import org.openelis.domain.ProjectDO;
import org.openelis.domain.ProjectViewDO;
import org.openelis.gwt.common.DatabaseException;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.data.Query;
import org.openelis.manager.ProjectManager;
import org.openelis.manager.ProjectParameterManager;
import org.openelis.persistence.EJBFactory;
import org.openelis.remote.ProjectManagerRemote;
import org.openelis.remote.ProjectRemote;

public class ProjectService {

    public ArrayList<IdNameVO> query(Query query) throws Exception {
        return remote().query(query.getFields(), query.getPage() * query.getRowsPerPage(), query.getRowsPerPage());
    }

    public ProjectManager fetchById(Integer id) throws Exception {
        return remoteManager().fetchById(id);
    }

    public ProjectViewDO fetchDOById(Integer id) throws Exception {
        return remote().fetchById(id);
    }

    public ProjectManager fetchWithParameters(Integer id) throws Exception {
        return remoteManager().fetchWithParameters(id);
    }

    public ArrayList<ProjectDO> fetchActiveByName(String search) throws Exception {
        ArrayList<ProjectDO> list;

        try {
            list = remote().fetchActiveByName(search + "%", 10);
        } catch (NotFoundException e) {
            list = new ArrayList<ProjectDO>(0);
        } catch (RuntimeException e) {
            throw new DatabaseException(e);
        }
        return list;
    }
    
    public ProjectDO fetchSingleByName(String name) throws Exception {
        ArrayList<ProjectDO> list;
        
        list = remote().fetchActiveByName(name, 1);
        
        if(list.size() > 0)
            return list.get(0);
        
        return null;
    }

    public ProjectManager add(ProjectManager man) throws Exception {
        return remoteManager().add(man);
    }

    public ProjectManager update(ProjectManager man) throws Exception {
        return remoteManager().update(man);
    }

    public ProjectManager fetchForUpdate(Integer id) throws Exception {
        return remoteManager().fetchForUpdate(id);
    }

    public ProjectManager abortUpdate(Integer id) throws Exception {
        return remoteManager().abortUpdate(id);
    }

    //
    // support for ProjectParameterManager
    //
    public ProjectParameterManager fetchParameterByProjectId(Integer id) throws Exception {
        return remoteManager().fetchParameterByProjectId(id);
    }

    private ProjectRemote remote() {
        return (ProjectRemote)EJBFactory.lookup("openelis/ProjectBean/remote");
    }

    private ProjectManagerRemote remoteManager() {
        return (ProjectManagerRemote)EJBFactory.lookup("openelis/ProjectManagerBean/remote");
    }
}