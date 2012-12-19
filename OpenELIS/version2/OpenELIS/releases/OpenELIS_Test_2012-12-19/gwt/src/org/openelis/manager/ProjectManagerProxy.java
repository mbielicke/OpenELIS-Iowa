package org.openelis.manager;

import org.openelis.gwt.services.ScreenService;

public class ProjectManagerProxy {
    
	protected static final String MANAGER_SERVICE_URL = "org.openelis.modules.project.server.ProjectService";
    protected ScreenService       service;

    public ProjectManagerProxy() {
        service = new ScreenService("controller?service=" + MANAGER_SERVICE_URL);
    }
    
    public ProjectManager fetchById(Integer id) throws Exception {
        return service.call("fetchById", id);
    }

    public ProjectManager fetchWithParameters(Integer id) throws Exception {
        return service.call("fetchWithParameters", id);
    }

    public ProjectManager add(ProjectManager man) throws Exception {
        return service.call("add", man);
    }

    public ProjectManager update(ProjectManager man) throws Exception {
        return service.call("update", man);
    }

    public ProjectManager fetchForUpdate(Integer id) throws Exception {
        return service.call("fetchForUpdate", id);
    }

    public ProjectManager abortUpdate(Integer id) throws Exception {
        return service.call("abortUpdate", id);
    }

    @SuppressWarnings("unused")
    public void validate(ProjectManager man) throws Exception {
    }
}
