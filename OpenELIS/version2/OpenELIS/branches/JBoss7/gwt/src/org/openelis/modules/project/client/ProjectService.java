package org.openelis.modules.project.client;

import java.util.ArrayList;

import org.openelis.domain.IdNameVO;
import org.openelis.domain.ProjectDO;
import org.openelis.domain.ProjectViewDO;
import org.openelis.gwt.common.data.Query;
import org.openelis.gwt.screen.Callback;
import org.openelis.manager.ProjectManager;
import org.openelis.manager.ProjectParameterManager;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class ProjectService implements ProjectServiceInt, ProjectServiceIntAsync {
    
    static ProjectService instance;
    
    ProjectServiceIntAsync service;
    
    public static ProjectService get() {
        if(instance == null)
            instance = new ProjectService();
        
        return instance;
    }
    
    private ProjectService() {
        service = (ProjectServiceIntAsync)GWT.create(ProjectServiceInt.class);
    }

    @Override
    public void abortUpdate(Integer id, AsyncCallback<ProjectManager> callback) {
        service.abortUpdate(id, callback);
    }

    @Override
    public void add(ProjectManager man, AsyncCallback<ProjectManager> callback) {
        service.add(man, callback);
    }

    @Override
    public void fetchActiveByName(String search, AsyncCallback<ArrayList<ProjectDO>> callback) {
        service.fetchActiveByName(search, callback);
    }

    @Override
    public void fetchById(Integer id, AsyncCallback<ProjectManager> callback) {
        service.fetchById(id, callback);
    }

    @Override
    public void fetchDOById(Integer id, AsyncCallback<ProjectViewDO> callback) {
        service.fetchDOById(id, callback);
    }

    @Override
    public void fetchForUpdate(Integer id, AsyncCallback<ProjectManager> callback) {
        service.fetchForUpdate(id, callback);
    }

    @Override
    public void fetchList(AsyncCallback<ArrayList<IdNameVO>> callback) {
        service.fetchList(callback);
    }

    @Override
    public void fetchParameterByProjectId(Integer id,
                                          AsyncCallback<ProjectParameterManager> callback) {
        service.fetchParameterByProjectId(id, callback);
    }

    @Override
    public void fetchSingleByName(String name, AsyncCallback<ProjectDO> callback) {
        service.fetchSingleByName(name, callback);
    }

    @Override
    public void fetchWithParameters(Integer id, AsyncCallback<ProjectManager> callback) {
        service.fetchWithParameters(id, callback);
    }

    @Override
    public void query(Query query, AsyncCallback<ArrayList<IdNameVO>> callback) {
        service.query(query, callback);
    }

    @Override
    public void update(ProjectManager man, AsyncCallback<ProjectManager> callback) {
        service.update(man, callback);
    }

    @Override
    public ArrayList<IdNameVO> fetchList() throws Exception {
        Callback<ArrayList<IdNameVO>> callback;
        
        callback = new Callback<ArrayList<IdNameVO>>();
        service.fetchList(callback);
        return callback.getResult();  
    }

    @Override
    public ProjectManager fetchById(Integer id) throws Exception {
        Callback<ProjectManager> callback;
        
        callback = new Callback<ProjectManager>();
        service.fetchById(id, callback);
        return callback.getResult();
    }

    @Override
    public ArrayList<ProjectDO> fetchActiveByName(String search) throws Exception {
        Callback<ArrayList<ProjectDO>> callback;
        
        callback = new Callback<ArrayList<ProjectDO>>();
        service.fetchActiveByName(search, callback);
        return callback.getResult();
    }

    @Override
    public ProjectViewDO fetchDOById(Integer id) throws Exception {
        Callback<ProjectViewDO> callback;
        
        callback = new Callback<ProjectViewDO>();
        service.fetchDOById(id, callback);
        return callback.getResult();
    }

    @Override
    public ArrayList<IdNameVO> query(Query query) throws Exception {
        Callback<ArrayList<IdNameVO>> callback;
        
        callback = new Callback<ArrayList<IdNameVO>>();
        service.query(query, callback);
        return callback.getResult();
    }

    @Override
    public ProjectManager fetchWithParameters(Integer id) throws Exception {
        Callback<ProjectManager> callback;
        
        callback = new Callback<ProjectManager>();
        service.fetchWithParameters(id, callback);
        return callback.getResult();
    }

    @Override
    public ProjectDO fetchSingleByName(String name) throws Exception {
        Callback<ProjectDO> callback;
        
        callback = new Callback<ProjectDO>();
        service.fetchSingleByName(name, callback);
        return callback.getResult();
    }

    @Override
    public ProjectManager add(ProjectManager man) throws Exception {
        Callback<ProjectManager> callback;
        
        callback = new Callback<ProjectManager>();
        service.add(man, callback);
        return callback.getResult();
    }

    @Override
    public ProjectManager update(ProjectManager man) throws Exception {
        Callback<ProjectManager> callback;
        
        callback = new Callback<ProjectManager>();
        service.update(man, callback);
        return callback.getResult();
    }

    @Override
    public ProjectManager fetchForUpdate(Integer id) throws Exception {
        Callback<ProjectManager> callback;
        
        callback = new Callback<ProjectManager>();
        service.fetchForUpdate(id, callback);
        return callback.getResult();
    }

    @Override
    public ProjectManager abortUpdate(Integer id) throws Exception {
        Callback<ProjectManager> callback;
        
        callback = new Callback<ProjectManager>();
        service.abortUpdate(id, callback);
        return callback.getResult();
    }

    @Override
    public ProjectParameterManager fetchParameterByProjectId(Integer id) throws Exception {
        Callback<ProjectParameterManager> callback;
        
        callback = new Callback<ProjectParameterManager>();
        service.fetchParameterByProjectId(id, callback);
        return callback.getResult();
    }

}
