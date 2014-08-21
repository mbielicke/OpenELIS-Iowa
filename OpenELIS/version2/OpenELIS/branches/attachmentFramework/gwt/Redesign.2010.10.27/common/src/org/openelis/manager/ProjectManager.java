package org.openelis.manager;

import org.openelis.domain.ProjectViewDO;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.RPC;

public class ProjectManager implements RPC {
	
    private static final long serialVersionUID = 1L;
        
    protected ProjectViewDO	project;
    protected ProjectParameterManager parameters;
    
    protected transient static ProjectManagerProxy proxy;
    
    public ProjectManager() {
    	project = null;
    }
    
    public static ProjectManager getInstance() {
    	ProjectManager manager;
    	
    	manager = new ProjectManager();
    	manager.project = new ProjectViewDO();

    	return manager;
    }
    
    public ProjectViewDO getProject() {
        return project;
    }

    public void setProject(ProjectViewDO project) {
    	this.project = project;
    }
    
    // service methods
    public static ProjectManager fetchById(Integer id) throws Exception {
        return proxy().fetchById(id);
    }

    public static ProjectManager fetchWithParameters(Integer id) throws Exception {
        return proxy().fetchWithParameters(id);
    }
    
    public ProjectManager add() throws Exception {
    	return proxy().add(this);
    }
    
    public ProjectManager update() throws Exception {
    	return proxy().update(this);
    }
    
    public ProjectManager fetchForUpdate() throws Exception {
    	return proxy().fetchForUpdate(project.getId());
    }
    
    public ProjectManager abortUpdate() throws Exception {
    	return proxy().abortUpdate(project.getId());
    }
    
    public void validate() throws Exception {
    	proxy().validate(this);
    }
    
    //
    // other managers
    //
    public ProjectParameterManager getParameters() throws Exception {
        if (parameters == null) {
            if (project.getId() != null) {
                try {
                    parameters = ProjectParameterManager.fetchByProjectId(project.getId());
                } catch (NotFoundException e) {
                    // ignore
                } catch (Exception e) {
                    throw e;
                }
            }
            if (parameters == null)
                parameters = ProjectParameterManager.getInstance();
        }
        return parameters;
    }

    private static ProjectManagerProxy proxy() {
    	if(proxy == null)
    		proxy = new ProjectManagerProxy();

    	return proxy;
    }

}
