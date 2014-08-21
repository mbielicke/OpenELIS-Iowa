package org.openelis.modules.project.client;

import java.util.ArrayList;

import org.openelis.domain.IdNameVO;
import org.openelis.domain.ProjectDO;
import org.openelis.domain.ProjectViewDO;
import org.openelis.gwt.common.data.Query;
import org.openelis.manager.ProjectManager;
import org.openelis.manager.ProjectParameterManager;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("project")
public interface ProjectServiceInt extends RemoteService {

    ArrayList<IdNameVO> fetchList() throws Exception;

    ProjectManager fetchById(Integer id) throws Exception;

    ArrayList<ProjectDO> fetchActiveByName(String search) throws Exception;

    ProjectViewDO fetchDOById(Integer id) throws Exception;

    ArrayList<IdNameVO> query(Query query) throws Exception;

    ProjectManager fetchWithParameters(Integer id) throws Exception;

    ProjectDO fetchSingleByName(String name) throws Exception;

    ProjectManager add(ProjectManager man) throws Exception;

    ProjectManager update(ProjectManager man) throws Exception;

    ProjectManager fetchForUpdate(Integer id) throws Exception;

    ProjectManager abortUpdate(Integer id) throws Exception;

    //
    // support for ProjectParameterManager
    //
    ProjectParameterManager fetchParameterByProjectId(Integer id) throws Exception;

}