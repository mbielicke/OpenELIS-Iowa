package org.openelis.manager;

import java.util.ArrayList;

import javax.naming.InitialContext;

import org.openelis.domain.ProjectParameterDO;
import org.openelis.domain.ProjectViewDO;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.local.ProjectLocal;
import org.openelis.local.ProjectParameterLocal;
import org.openelis.utilcommon.DataBaseUtil;


public class ProjectManagerProxy {
    
    public ProjectManager fetchById(Integer id) throws Exception {
    	ProjectLocal projLocal;
    	ProjectParameterLocal paramLocal;
    	ProjectViewDO data;
    	ArrayList<ProjectParameterDO> parameters;
    	ProjectManager m;
    	
    	projLocal = projectLocal();
    	paramLocal = paramLocal();
    	data = projLocal.fetchById(id);
    	parameters = paramLocal.findByProject(id);
    	
    	m = ProjectManager.getInstance();
    	
    	m.setProject(data);
    	m.setProjectParameters(parameters);
        
    	return m;
    }

    public ProjectManager add(ProjectManager man) throws Exception {
    	ProjectLocal pl;
    	ProjectParameterLocal paramLocal;
    	
    	pl = projectLocal();
    	paramLocal = paramLocal();
    	
    	pl.add(man.getProject());
    	
    	for(ProjectParameterDO paramDO : man.projectParameters) {
    		paramLocal.add(paramDO);
    	}
    	    	
    	return man;
    }

    public ProjectManager update(ProjectManager man) throws Exception {
    	ProjectLocal pl;
    	ProjectParameterLocal paramLocal;
    	
    	pl = projectLocal();
    	paramLocal = paramLocal();
    	
    	pl.update(man.getProject());
    	
    	for(ProjectParameterDO paramDO : man.projectParameters) {
    		if(paramDO.getId() != null)
    	   		paramLocal.update(paramDO);
    		else
    			paramLocal.add(paramDO);
    	}
    	
    	if(man.deleted != null) {
    		for(Integer id : man.deleted) {
    			paramLocal.delete(id);
    		}	
    		man.deleted = null;
    	}
    	
    	return man;
    }

    public ProjectManager fetchForUpdate(Integer id) throws Exception {
        assert false : "not supported";
    	return null;
    }

    public ProjectManager abortUpdate(Integer id) throws Exception {
        assert false : "not supported";
    	return null;
    }

    public void validate(ProjectManager man) throws Exception {
    	ValidationErrorsList list;
    	 
    	list = new ValidationErrorsList();
    	
    	try {
    		projectLocal().validate(man.projectView);
    	}catch(ValidationErrorsList vl) {
    		DataBaseUtil.mergeException(list, vl);
    	}
    	
    	ProjectParameterLocal pl = paramLocal();
    	
    	for(int i = 0; i < man.getProjectParameters().size(); i++){
    		try {
    			pl.validate(man.getProjectParameters().get(i));
    		}catch(ValidationErrorsList vl) {
    			DataBaseUtil.mergeException(list, vl, "parameterTable", i);
    		}
    	}
    	
    	if(list.size() > 0)
    		throw list;
    }
    
    private ProjectLocal projectLocal() {
    	try {
    		InitialContext ctx = new InitialContext();
    		return (ProjectLocal)ctx.lookup("openelis/ProjectBean/local");
    	}catch(Exception e) {
    		e.printStackTrace();
    		return null;
    	}
    }
    
    private ProjectParameterLocal paramLocal() {
    	try {
    		InitialContext ctx = new InitialContext();
    		return (ProjectParameterLocal)ctx.lookup("openelis/ProjectParameterBean/local");
    	}catch(Exception e){
    		e.printStackTrace();
    		return null;
    	}
    }
}
