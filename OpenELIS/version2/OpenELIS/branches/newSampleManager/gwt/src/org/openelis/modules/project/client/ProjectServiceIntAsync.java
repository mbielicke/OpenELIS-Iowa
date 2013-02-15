package org.openelis.modules.project.client;

import java.util.ArrayList;

import org.openelis.domain.IdNameVO;
import org.openelis.domain.ProjectDO;
import org.openelis.domain.ProjectViewDO;
import org.openelis.gwt.common.data.Query;
import org.openelis.manager.ProjectManager;
import org.openelis.manager.ProjectParameterManager;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ProjectServiceIntAsync {

    void abortUpdate(Integer id, AsyncCallback<ProjectManager> callback);

    void add(ProjectManager man, AsyncCallback<ProjectManager> callback);

    void fetchActiveByName(String search, AsyncCallback<ArrayList<ProjectDO>> callback);

    void fetchById(Integer id, AsyncCallback<ProjectManager> callback);

    void fetchDOById(Integer id, AsyncCallback<ProjectViewDO> callback);

    void fetchForUpdate(Integer id, AsyncCallback<ProjectManager> callback);

    void fetchList(AsyncCallback<ArrayList<IdNameVO>> callback);

    void fetchParameterByProjectId(Integer id, AsyncCallback<ProjectParameterManager> callback);

    void fetchSingleByName(String name, AsyncCallback<ProjectDO> callback);

    void fetchWithParameters(Integer id, AsyncCallback<ProjectManager> callback);

    void query(Query query, AsyncCallback<ArrayList<IdNameVO>> callback);

    void update(ProjectManager man, AsyncCallback<ProjectManager> callback);

}
