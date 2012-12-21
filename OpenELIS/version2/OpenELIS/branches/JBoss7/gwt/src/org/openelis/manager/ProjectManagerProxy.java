package org.openelis.manager;

import org.openelis.modules.project.client.ProjectService;

public class ProjectManagerProxy {
    
    public ProjectManagerProxy() {
    }
    
    public ProjectManager fetchById(Integer id) throws Exception {
        return ProjectService.get().fetchById(id);
    }

    public ProjectManager fetchWithParameters(Integer id) throws Exception {
        return ProjectService.get().fetchWithParameters(id);
    }

    public ProjectManager add(ProjectManager man) throws Exception {
        return ProjectService.get().add(man);
    }

    public ProjectManager update(ProjectManager man) throws Exception {
        return ProjectService.get().update(man);
    }

    public ProjectManager fetchForUpdate(Integer id) throws Exception {
        return ProjectService.get().fetchForUpdate(id);
    }

    public ProjectManager abortUpdate(Integer id) throws Exception {
        return ProjectService.get().abortUpdate(id);
    }

    @SuppressWarnings("unused")
    public void validate(ProjectManager man) throws Exception {
    }
}
