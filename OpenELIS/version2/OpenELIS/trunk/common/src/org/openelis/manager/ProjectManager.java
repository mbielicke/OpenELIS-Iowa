package org.openelis.manager;

import java.util.ArrayList;

import org.openelis.domain.ProjectParameterDO;
import org.openelis.domain.ProjectViewDO;
import org.openelis.gwt.common.RPC;

public class ProjectManager implements RPC {
	
    private static final long                            serialVersionUID = 1L;
        
    protected ProjectViewDO	projectView;
    protected ArrayList<ProjectParameterDO> projectParameters;
    protected ArrayList<ProjectParameterDO> deleted;
    
    protected transient static ProjectManagerProxy proxy;
    
    public ProjectManager() {
    	projectView = null;
    	projectParameters = null;
    }
    
    public static ProjectManager getInstance() {
    	ProjectManager manager;
    	
    	manager = new ProjectManager();
    	manager.projectView = new ProjectViewDO();
    	manager.projectParameters = new ArrayList<ProjectParameterDO>();
    	
    	return manager;
    }
    
    public void setProject(ProjectViewDO projectView) {
    	this.projectView = projectView;
    }
    
    public ProjectViewDO getProject() {
    	return projectView;
    }
    
    public void setProjectParameters(ArrayList<ProjectParameterDO> projectParameters) {
    	this.projectParameters = projectParameters;
    }
    
    public ArrayList<ProjectParameterDO> getProjectParameters() {
    	return projectParameters;
    }
    
    public ProjectParameterDO getProjectParameter(int index) {
    	return projectParameters.get(index);
    }
    
    public ProjectParameterDO removeProjectParamter(int index) {
    	if(projectParameters.get(index).getId() != null) {
    		if(deleted == null)
    			deleted = new ArrayList<ProjectParameterDO>();
    	   	deleted.add(projectParameters.get(index));
    	}
    	return projectParameters.remove(index);
    }
    
    public void addProjectParameter(ProjectParameterDO param) {
    	projectParameters.add(param);
    }
    
    public void addProjectParameter(int index, ProjectParameterDO param) {
    	projectParameters.add(index,param);
    }
    
    // service methods
    
    public static ProjectManager fetchById(Integer id) throws Exception {
    	return proxy().fetchById(id);
    }
    
    public ProjectManager add() throws Exception {
    	return proxy().add(this);
    }
    
    public ProjectManager update() throws Exception {
    	return proxy().update(this);
    }
    
    public ProjectManager fetchForUpdate() throws Exception {
    	return proxy().fetchForUpdate(projectView.getId());
    }
    
    public ProjectManager abortUpdate() throws Exception {
    	return proxy().abortUpdate(projectView.getId());
    }
    
    public void validate() throws Exception {
    	proxy().validate(this);
    }
    
    private static ProjectManagerProxy proxy() {
    	if(proxy == null)
    		proxy = new ProjectManagerProxy();
    	
    	return proxy;
    }

}
