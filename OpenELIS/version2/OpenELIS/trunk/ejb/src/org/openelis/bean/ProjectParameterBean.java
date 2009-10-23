package org.openelis.bean;

import java.util.ArrayList;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.annotation.security.SecurityDomain;
import org.openelis.domain.ProjectParameterDO;
import org.openelis.entity.ProjectParameter;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.local.ProjectParameterLocal;
import org.openelis.metamap.ProjectMetaMap;
import org.openelis.utilcommon.DataBaseUtil;

@Stateless
@SecurityDomain("openelis")
@RolesAllowed("project-select")

public class ProjectParameterBean implements ProjectParameterLocal {
	
    @PersistenceContext(name = "openelis")
    private EntityManager manager;

    @Resource
    private SessionContext ctx;
    
    private static ProjectMetaMap ProjMeta = new ProjectMetaMap();
    
    public ProjectParameterDO fetchById(Integer id) throws Exception {
        Query query;
        
        query = manager.createNamedQuery("ProjectParameter.ProjectParameterById");
        query.setParameter("id",id);        
        
        return (ProjectParameterDO)query.getSingleResult();
    }

    public ArrayList<ProjectParameterDO> findByProject(Integer projectId) throws Exception {
        Query query;
        
        query = manager.createNamedQuery("ProjectParameter.ProjectParameterByProjectId");
        query.setParameter("projectId",projectId);        
        
        return DataBaseUtil.toArrayList(query.getResultList());
    }
    
    public ProjectParameterDO add(ProjectParameterDO paramDO) throws Exception {
    	ProjectParameter param;
    	    	
    	manager.setFlushMode(FlushModeType.COMMIT);
    	
        param = new ProjectParameter();
        param.setParameter(paramDO.getParameter());
        param.setOperationId(paramDO.getOperationId());
        param.setValue(paramDO.getValue());
        param.setProjectId(paramDO.getProjectId());
          
        manager.persist(param);
        
        paramDO.setId(param.getId());
        
        return paramDO;
    }
    
    public ProjectParameterDO update(ProjectParameterDO paramDO) throws Exception {
    	ProjectParameter param;
    	    	
    	manager.setFlushMode(FlushModeType.COMMIT);
    	
        param = manager.find(ProjectParameter.class, paramDO.getId());
        param.setParameter(paramDO.getParameter());
        param.setOperationId(paramDO.getOperationId());
        param.setValue(paramDO.getValue());
        param.setProjectId(paramDO.getProjectId());
                  
        return paramDO;
    }
    
    public void delete(ProjectParameterDO data) throws Exception {
    	ProjectParameter param;
    	
    	manager.setFlushMode(FlushModeType.COMMIT);
    	
    	param = manager.find(ProjectParameter.class, data.getId());
    	
    	manager.remove(param);
    }
    
    
    public void validate(ProjectParameterDO paramDO) throws ValidationErrorsList {
    	String param,value;
    	ValidationErrorsList exceptionList = new ValidationErrorsList();

    	param = paramDO.getParameter();
    	value = paramDO.getValue();
    	if(param == null || "".equals(param)) {
    		exceptionList.add(new FieldErrorException("fieldRequiredException",
    				ProjMeta.getProjectParameter().getParameter()));
    	}

    	if(value == null || "".equals(value)) {
    		exceptionList.add(new FieldErrorException("fieldRequiredException",
    				ProjMeta.getProjectParameter().getValue()));
    	}

    	if(paramDO.getOperationId() == null) {
    		exceptionList.add(new FieldErrorException("fieldRequiredException",
    				ProjMeta.getProjectParameter().getOperationId()));
    	}
    	
    	if(exceptionList.size() > 0)
    		throw exceptionList;
    		
    }

}
