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

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.EJBs;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.annotation.security.SecurityDomain;
import org.openelis.domain.ProjectDO;
import org.openelis.domain.ProjectParameterDO;
import org.openelis.domain.SecuritySystemUserDO;
import org.openelis.entity.Project;
import org.openelis.entity.ProjectParameter;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.FormErrorException;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.TableFieldErrorException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.gwt.common.data.AbstractField;
import org.openelis.local.LockLocal;
import org.openelis.metamap.ProjectMetaMap;
import org.openelis.remote.ProjectRemote;
import org.openelis.security.domain.SystemUserDO;
import org.openelis.security.local.SystemUserUtilLocal;
import org.openelis.util.Datetime;
import org.openelis.util.QueryBuilder;
import org.openelis.utils.GetPage;

@Stateless
@EJBs( {
        @EJB(name="ejb/SystemUser",beanInterface=SystemUserUtilLocal.class),
        @EJB(name = "ejb/Lock", beanInterface = LockLocal.class)        
        })
@SecurityDomain("openelis")
public class ProjectBean implements ProjectRemote {

    @PersistenceContext(name = "openelis")
    private EntityManager manager;

    @Resource
    private SessionContext ctx;

    private LockLocal lockBean;
    
    private SystemUserUtilLocal sysUser;
    
    private static ProjectMetaMap ProjMeta = new ProjectMetaMap();

    @PostConstruct
    private void init() {
        lockBean = (LockLocal)ctx.lookup("ejb/Lock");
        sysUser = (SystemUserUtilLocal)ctx.lookup("ejb/SystemUser");
    }
    
    public ProjectDO getProject(Integer projectId) {
        ProjectDO projectDO;
        Query query;
        SystemUserDO userDO;
        
        query = manager.createNamedQuery("Project.ProjectById");
        query.setParameter("id", projectId);
        projectDO = (ProjectDO)query.getSingleResult(); 
        userDO = sysUser.getSystemUser(projectDO.getOwnerId());
        projectDO.setOwnerName(userDO.getLoginName());
        return projectDO;
    }

    public ProjectDO getProjectAndLock(Integer projectId, String session) throws Exception {
        //SecurityInterceptor.applySecurity(ctx.getCallerPrincipal().getName(), "project", ModuleFlags.UPDATE);
        Query query; 
                
        query = manager.createNamedQuery("getTableId");
        query.setParameter("name", "project");
        lockBean.getLock((Integer)query.getSingleResult(), projectId);
        return getProject(projectId);
    }

    public ProjectDO getProjectAndUnlock(Integer projectId, String session) {
        Query unlockQuery; 
        
        unlockQuery = manager.createNamedQuery("getTableId");
        unlockQuery.setParameter("name", "project");
        lockBean.giveUpLock((Integer)unlockQuery.getSingleResult(), projectId);
        return getProject(projectId);
    }

    public List<SecuritySystemUserDO> ownerAutocompleteByName(String loginName, int numResult){    
        SecuritySystemUserDO secUserDO;
        SystemUserDO userDO;
        List<SystemUserDO> userDOList;
        List<SecuritySystemUserDO> secUserDOList;
                        
        userDOList = sysUser.systemUserAutocompleteByLoginName(loginName,numResult);
        secUserDOList = new ArrayList<SecuritySystemUserDO>();
        for(int i=0; i < userDOList.size(); i++) {
            userDO = userDOList.get(i);
            secUserDO = new SecuritySystemUserDO(userDO.getId(),userDO.getLoginName(),
                                                 userDO.getLastName(),userDO.getFirstName(),
                                                 userDO.getInitials(),userDO.getIsEmployee(),
                                                 userDO.getIsActive());
            secUserDOList.add(secUserDO);
        } 
        return secUserDOList;        
    }

    public List<ProjectParameterDO> getProjectParameters(Integer projectId) {
        Query query;
        
        query = manager.createNamedQuery("ProjectParameter.ProjectParameterByProjectId");
        query.setParameter("projectId",projectId);        
        return query.getResultList();
    }

    public Integer updateProject(ProjectDO projectDO,         
                                 List<ProjectParameterDO> paramDOList) throws Exception {       
        Query query;
        Integer projectReferenceId,projectId;
        Project project;
        ProjectParameterDO paramDO;
        ProjectParameter param;
        
        query = manager.createNamedQuery("getTableId");
        query.setParameter("name", "project");
        projectReferenceId = (Integer)query.getSingleResult();
        projectId = projectDO.getId();
        
        if (projectId != null) {
            // we need to call lock one more time to make sure their lock
            // didnt expire and someone else grabbed the record
            lockBean.validateLock(projectReferenceId, projectId);
        }

        validateProject(projectDO, paramDOList);
        
        manager.setFlushMode(FlushModeType.COMMIT);
        project = null;
        
        if(projectId == null) {
            project = new Project();
        } else {            
            project = manager.find(Project.class,projectId);
        } 
        
        project.setCompletedDate(projectDO.getCompletedDate());
        project.setDescription(projectDO.getDescription());
        project.setIsActive(projectDO.getIsActive());
        project.setName(projectDO.getName());
        project.setOwnerId(projectDO.getOwnerId());
        project.setReferenceTo(projectDO.getReferenceTo());
        project.setScriptletId(projectDO.getScriptletId());
        project.setStartedDate(projectDO.getStartedDate());
       
        projectId = project.getId();
        if(projectId == null) {
            manager.persist(project);
        }
        
        if(paramDOList != null) {
            for(int i = 0; i < paramDOList.size(); i++) {
                paramDO = paramDOList.get(i);
                if(paramDO.getId() == null) 
                    param = new ProjectParameter();
                else
                    param = manager.find(ProjectParameter.class, paramDO.getId());
                
                if (paramDO.getDelete() && paramDO.getId() != null) {                    
                    manager.remove(param);

                } else if(!paramDO.getDelete()){
                    param.setParameter(paramDO.getParameter());
                    param.setOperationId(paramDO.getOperationId());
                    param.setValue(paramDO.getValue());
                    param.setProjectId(project.getId());
                    
                    if (param.getId() == null) {
                        manager.persist(param);
                    }
                }
            }
        }
        lockBean.giveUpLock(projectReferenceId, projectId);        
        return project.getId();
    }

    public List query(ArrayList<AbstractField> fields, int first, int max) throws Exception {
        StringBuffer sb;
        QueryBuilder qb;
        List returnList;
        Query query;
        
        sb = new StringBuffer();
        qb = new QueryBuilder();
        
        qb.setMeta(ProjMeta);

        qb.setSelect("distinct new org.openelis.domain.IdNameDO(" +ProjMeta.getId()
                     + ", "
                     + ProjMeta.getName()
                     + ") ");               
        
        qb.addWhere(fields);        
        qb.setOrderBy(ProjMeta.getName());

        sb.append(qb.getEJBQL());                                
        query = manager.createQuery(sb.toString());

        if (first > -1 && max > -1)
            query.setMaxResults(first + max);

        // ***set the parameters in the query
        qb.setQueryParams(query);

        returnList = GetPage.getPage(query.getResultList(), first, max);

        if (returnList == null)
            throw new LastPageException();
        else
            return returnList;
    }
    
        
    private void validateProject(ProjectDO projectDO, 
                                 List<ProjectParameterDO> paramDOList) throws Exception{
        ValidationErrorsList exceptionList;
        
        exceptionList = new ValidationErrorsList();
        validateProject(projectDO, exceptionList);        
        validateProjectParameters(paramDOList, exceptionList);
        
        if(exceptionList.size() > 0) 
            throw exceptionList;
    }   
    
    private void validateProject(ProjectDO projectDO, ValidationErrorsList exceptionList) {
        boolean checkDuplicate, overlap;
        Datetime dcompleteDate, dstartDate,qcompleteDate, qstartDate;
        Integer id;
        String active;
        int iter;
        Query query;
        Project project;
        List<Project> list;
        
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
                exceptionList.add(new FormErrorException("endDateAfterBeginDateException"));
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
    }       
    
    private void validateProjectParameters(List<ProjectParameterDO> paramDOList,
                                           ValidationErrorsList exceptionList) {
        ProjectParameterDO paramDO;
        String param,value;
        
        if(paramDOList != null) {
            for(int i = 0; i < paramDOList.size(); i++) {
                paramDO = paramDOList.get(i);
                if(paramDO.getDelete())
                    continue;
                
                param = paramDO.getParameter();
                value = paramDO.getValue();
                if(param == null || "".equals(param)) {
                    exceptionList.add(new TableFieldErrorException("fieldRequiredException",i,
                                                                   ProjMeta.getProjectParameter().getParameter()));
                }
                
                if(value == null || "".equals(value)) {
                    exceptionList.add(new TableFieldErrorException("fieldRequiredException",i,
                                                                   ProjMeta.getProjectParameter().getValue()));
                }
                
                if(paramDO.getOperationId() == null) {
                    exceptionList.add(new TableFieldErrorException("fieldRequiredException",i,
                                                                   ProjMeta.getProjectParameter().getOperationId()));
                }
            }
        }
        
    }
  }