/** Exhibit A - UIRF Open-source Based Public Software License.
* 
* The contents of this file are subject to the UIRF Open-source Based
* Public Software License(the "License"); you may not use this file except
* in compliance with the License. You may obtain a copy of the License at
* openelis.uhl.uiowa.edu
* 
* Software distributed under the License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
* License for the specific language governing rights and limitations
* under the License.
* 
* The Original Code is OpenELIS code.
* 
* The Initial Developer of the Original Code is The University of Iowa.
* Portions created by The University of Iowa are Copyright 2006-2008. All
* Rights Reserved.
* 
* Contributor(s): ______________________________________.
* 
* Alternatively, the contents of this file marked
* "Separately-Licensed" may be used under the terms of a UIRF Software
* license ("UIRF Software License"), in which case the provisions of a
* UIRF Software License are applicable instead of those above. 
*/
package org.openelis.bean;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.annotation.security.SecurityDomain;
import org.openelis.domain.IdNameDO;
import org.openelis.domain.IdNameVO;
import org.openelis.domain.ProjectViewDO;
import org.openelis.entity.Project;
import org.openelis.gwt.common.Datetime;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.FormErrorException;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.gwt.common.SecurityModule.ModuleFlags;
import org.openelis.gwt.common.data.QueryData;
import org.openelis.local.LockLocal;
import org.openelis.local.ProjectLocal;
import org.openelis.metamap.ProjectMetaMap;
import org.openelis.remote.ProjectRemote;
import org.openelis.security.domain.SystemUserDO;
import org.openelis.security.local.SystemUserUtilLocal;
import org.openelis.util.QueryBuilderV2;
import org.openelis.utilcommon.DataBaseUtil;
import org.openelis.utils.GetPage;
import org.openelis.utils.SecurityInterceptor;

@Stateless
@RolesAllowed("project-select")
@SecurityDomain("openelis")
public class ProjectBean implements ProjectLocal, ProjectRemote {

    @PersistenceContext(name = "openelis")
    private EntityManager manager;

    @Resource
    private SessionContext ctx;

    @EJB
    private LockLocal lockBean;
    
    @EJB
    private SystemUserUtilLocal sysUser;
    
    private static ProjectMetaMap ProjMeta = new ProjectMetaMap();
    
    private static Integer projRefTableId;
    
    public ProjectBean() {
 
    }
    
    public ProjectViewDO fetchById(Integer id) throws Exception {
        ProjectViewDO data;
        Query query;
        
        query = manager.createNamedQuery("Project.ProjectById");
        query.setParameter("id", id);
        data = (ProjectViewDO)query.getSingleResult(); 
        
        SystemUserDO user= sysUser.getSystemUser(data.getOwnerId());
        data.setSystemUserName(user.getLoginName());
                
        return data;
    }

    public ArrayList<IdNameVO> query(ArrayList<QueryData> fields, int first, int max) throws Exception {
        QueryBuilderV2 qb;
        List list;
        Query query;
        
        qb = new QueryBuilderV2();
        
        qb.setMeta(ProjMeta);

        qb.setSelect("distinct new org.openelis.domain.IdNameVO(" +ProjMeta.getId()
                     + ", "
                     + ProjMeta.getName()
                     + ") ");               
        
        qb.constructWhere(fields);        
        qb.setOrderBy(ProjMeta.getName());

        query = manager.createQuery(qb.getEJBQL());

        if (first > -1 && max > -1)
            query.setMaxResults(first + max);

        // ***set the parameters in the query
        QueryBuilderV2.setQueryParams(query,fields);

        list = GetPage.getPage(query.getResultList(), first, max);

        list = query.getResultList();
        if (list.isEmpty())
            throw new NotFoundException();
        list = (ArrayList<IdNameVO>)DataBaseUtil.subList(list, first, max);
        if (list == null)
            throw new LastPageException();

        return (ArrayList<IdNameVO>)list;
    }

    public ProjectViewDO add(ProjectViewDO data) throws Exception {
        
        Project project;

        checkSecurity(ModuleFlags.ADD);
        
        manager.setFlushMode(FlushModeType.COMMIT);
        
        project = new Project();        
        project.setCompletedDate(data.getCompletedDate());
        project.setDescription(data.getDescription());
        project.setIsActive(data.getIsActive());
        project.setName(data.getName());
        project.setOwnerId(data.getOwnerId());
        project.setReferenceTo(data.getReferenceTo());
        project.setScriptletId(data.getScriptletId());
        project.setStartedDate(data.getStartedDate());
       
        manager.persist(project);
        data.setId(project.getId());

        return data;
    }

    public ProjectViewDO update(ProjectViewDO data) throws Exception {
    	Project project;

        checkSecurity(ModuleFlags.UPDATE);
        
        manager.setFlushMode(FlushModeType.COMMIT);
         
        project = manager.find(Project.class,data.getId());
        
        project.setCompletedDate(data.getCompletedDate());
        project.setDescription(data.getDescription());
        project.setIsActive(data.getIsActive());
        project.setName(data.getName());
        project.setOwnerId(data.getOwnerId());
        project.setReferenceTo(data.getReferenceTo());
        project.setScriptletId(data.getScriptletId());
        project.setStartedDate(data.getStartedDate());
               
        return data;
    }

    public ArrayList<IdNameDO> findByName(String name, int maxResults) {
        Query query = manager.createNamedQuery("Project.ProjectByName");
        query.setParameter("name", name);
        query.setMaxResults(maxResults);
    
        return DataBaseUtil.toArrayList(query.getResultList()); 
    }

    public void validate(ProjectViewDO projectDO) throws ValidationErrorsList {
    	boolean checkDuplicate, overlap;
    	Datetime dcompleteDate, dstartDate,qcompleteDate, qstartDate;
    	Integer id;
    	String active;
    	int iter;
    	Query query;
    	Project project;
    	List<Project> list;
    	ValidationErrorsList exceptionList = new ValidationErrorsList();

    	checkDuplicate = true;
    	dstartDate = projectDO.getStartedDate();
    	dcompleteDate = projectDO.getCompletedDate();
    	id = projectDO.getId();
    	active = projectDO.getIsActive();        
    	if (projectDO.getName() == null || "".equals(projectDO.getName())) {
    		exceptionList.add(new FieldErrorException("fieldRequiredException",
    				ProjMeta.getName()));
    		checkDuplicate = false;
    	}
    	if (projectDO.getIsActive() == null) {
    		exceptionList.add(new FieldErrorException("fieldRequiredException",
    				ProjMeta.getIsActive()));
    		checkDuplicate = false;
    	}

    	if (projectDO.getOwnerId() == null) {
    		exceptionList.add(new FieldErrorException("fieldRequiredException",
    				ProjMeta.getOwnerId()));
    		checkDuplicate = false;
    	}

    	if (checkDuplicate) {
    		if (dcompleteDate != null && dcompleteDate.before(dstartDate)) {
    			exceptionList.add(new FieldErrorException("endDateAfterBeginDateException",
    					              ProjMeta.getCompletedDate()));
    			return;
    		}

    		query = manager.createNamedQuery("Project.ProjectListByName");
    		query.setParameter("name", projectDO.getName());
    		list = query.getResultList();

    		for (iter = 0; iter < list.size(); iter++) {
    			overlap = false;
    			project = (Project)list.get(iter);
    			if (!project.getId().equals(id)) {
    				if (project.getIsActive().equals(active)) {
    					if ("Y".equals(active)) {
    						exceptionList.add(new FormErrorException("projectActiveException"));
    						break;
    					}

    					qcompleteDate = project.getCompletedDate();
    					qstartDate = project.getStartedDate();                           
    					if(qstartDate != null && qcompleteDate != null) {
    						if (qstartDate.before(dcompleteDate) && (qcompleteDate.after(dstartDate))) {
    							overlap = true;
    						} else if (qstartDate.before(dstartDate) && (qcompleteDate.after(dcompleteDate))) {
    							overlap = true;
    						} else if (qstartDate.equals(dcompleteDate) || (qcompleteDate.equals(dstartDate))) {
    							overlap = true;
    						} else if (qstartDate.equals(dstartDate) || (qcompleteDate.equals(dcompleteDate))) {
    							overlap = true;
    						} 
    					}
    					if (overlap) 
    						exceptionList.add(new FormErrorException("projectTimeOverlapException"));


    				}
    			}
    		} 
    	}
    	
    	if(exceptionList.size() > 0)
    		throw exceptionList;
    }       


    
    private void checkSecurity(ModuleFlags flag) throws Exception {
        SecurityInterceptor.applySecurity(ctx.getCallerPrincipal().getName(), 
                                          "project", flag);
    }

  }