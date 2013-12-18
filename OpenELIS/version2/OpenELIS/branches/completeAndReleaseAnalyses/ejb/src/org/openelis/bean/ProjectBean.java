/**
 * Exhibit A - UIRF Open-source Based Public Software License.
 * 
 * The contents of this file are subject to the UIRF Open-source Based Public
 * Software License(the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * openelis.uhl.uiowa.edu
 * 
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 * 
 * The Original Code is OpenELIS code.
 * 
 * The Initial Developer of the Original Code is The University of Iowa.
 * Portions created by The University of Iowa are Copyright 2006-2008. All
 * Rights Reserved.
 * 
 * Contributor(s): ______________________________________.
 * 
 * Alternatively, the contents of this file marked "Separately-Licensed" may be
 * used under the terms of a UIRF Software license ("UIRF Software License"), in
 * which case the provisions of a UIRF Software License are applicable instead
 * of those above.
 */
package org.openelis.bean;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.security.annotation.SecurityDomain;
import org.openelis.constants.Messages;
import org.openelis.domain.IdNameVO;
import org.openelis.domain.ProjectDO;
import org.openelis.domain.ProjectViewDO;
import org.openelis.entity.Project;
import org.openelis.meta.ProjectMeta;
import org.openelis.meta.SampleWebMeta;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.DatabaseException;
import org.openelis.ui.common.FieldErrorException;
import org.openelis.ui.common.LastPageException;
import org.openelis.ui.common.NotFoundException;
import org.openelis.ui.common.SystemUserVO;
import org.openelis.ui.common.ValidationErrorsList;
import org.openelis.ui.common.data.QueryData;
import org.openelis.util.QueryBuilderV2;

@Stateless
@SecurityDomain("openelis")

public class ProjectBean {

    @PersistenceContext(unitName = "openelis")
    private EntityManager       manager;
    
    @EJB
    private UserCacheBean       userCache;

    private static ProjectMeta  meta = new ProjectMeta();
    
    private static final SampleWebMeta webMeta = new SampleWebMeta();

    @SuppressWarnings("unchecked")
    public ArrayList<IdNameVO> fetchList() throws Exception {
        Query query;
        
        query = manager.createNamedQuery("Project.FetchList");

        return DataBaseUtil.toArrayList(query.getResultList());
    }
    
    public ProjectViewDO fetchById(Integer id) throws Exception {
        Query query;
        ProjectViewDO data;
        SystemUserVO user;

        query = manager.createNamedQuery("Project.FetchById");
        query.setParameter("id", id);

        try {
            data = (ProjectViewDO)query.getSingleResult();
            if (data.getOwnerId() != null) {
                user = userCache.getSystemUser(data.getOwnerId());
                if (user != null)
                    data.setOwnerName(user.getLoginName());
            }
        } catch (NoResultException e) {
            throw new NotFoundException();
        } catch (Exception e) {
            throw new DatabaseException(e);
        }
        return data;
    }
    
    @SuppressWarnings("unchecked")
    public ArrayList<ProjectViewDO> fetchByIds(Collection<Integer> ids) throws Exception {
        Query query;
        List<ProjectViewDO> list;
        SystemUserVO user;

        query = manager.createNamedQuery("Project.FetchByIds");
        query.setParameter("ids", ids);
        list = query.getResultList(); 
        
        for (ProjectViewDO data :list) {
            if (data.getOwnerId() != null) {
                user = userCache.getSystemUser(data.getOwnerId());
                if (user != null)
                    data.setOwnerName(user.getLoginName());
            }
        }
        return DataBaseUtil.toArrayList(list);
    }

    @SuppressWarnings("unchecked")
    public ArrayList<ProjectDO> fetchByName(String name, int maxResults) throws Exception {
        Query query;

        query = manager.createNamedQuery("Project.FetchByName");
        query.setParameter("name", name);
        query.setMaxResults(maxResults);

        return DataBaseUtil.toArrayList(query.getResultList());
    }

    @SuppressWarnings("unchecked")
    public ArrayList<ProjectDO> fetchActiveByName(String name, int maxResults) throws Exception {
        Query query;

        query = manager.createNamedQuery("Project.FetchActiveByName");
        query.setParameter("name", name);
        query.setMaxResults(maxResults);

        return DataBaseUtil.toArrayList(query.getResultList());
    }
    
    public ArrayList<IdNameVO> fetchForOrganizations(String clause) throws Exception {
        Query query;
        QueryBuilderV2 builder;
        
        builder = new QueryBuilderV2();
        builder.setMeta(webMeta);        
        builder.setSelect("distinct new org.openelis.domain.IdNameVO(" +
                          SampleWebMeta.getProjectId() + ", " + SampleWebMeta.getProjectName()+ ") ");
        builder.addWhere("("+clause+")");
        builder.addWhere(SampleWebMeta.getSampleProjectProjectId() + "=" + SampleWebMeta.getProjectId());
        query = manager.createQuery(builder.getEJBQL());
        return DataBaseUtil.toArrayList(query.getResultList());
    }
    
    public ArrayList<IdNameVO> fetchForSampleStatusReport(ArrayList<Integer> organizationIds) throws Exception {
        Query query;
        IdNameVO vo;
        ArrayList<Object[]> resultList;
        ArrayList<IdNameVO> projectList;
        
        query = manager.createNamedQuery("Project.FetchForSampleStatusReport");
        query.setParameter("organizationIds", organizationIds);
        resultList = DataBaseUtil.toArrayList(query.getResultList());
        
        projectList = new ArrayList<IdNameVO>();
        for (Object[] result : resultList) {
            vo = new IdNameVO((Integer)result[0], (String)result[1]);
            projectList.add(vo);
        }
        return projectList;
    }

    @SuppressWarnings("unchecked")
    public ArrayList<IdNameVO> query(ArrayList<QueryData> fields, int first, int max) throws Exception {
        Query query;
        QueryBuilderV2 builder;
        List list;

        builder = new QueryBuilderV2();
        builder.setMeta(meta);
        builder.setSelect("distinct new org.openelis.domain.IdNameVO(" +
                          ProjectMeta.getId() + "," + ProjectMeta.getName() + ") ");
        builder.constructWhere(fields);
        builder.setOrderBy(ProjectMeta.getName());

        query = manager.createQuery(builder.getEJBQL());
        query.setMaxResults(first + max);
        builder.setQueryParams(query, fields);

        list = query.getResultList();
        if (list.isEmpty())
            throw new NotFoundException();
        list = (ArrayList<IdNameVO>)DataBaseUtil.subList(list, first, max);
        if (list == null)
            throw new LastPageException();

        return (ArrayList<IdNameVO>)list;
    }

    public ProjectDO add(ProjectDO data) throws Exception {
        Project entity;

        manager.setFlushMode(FlushModeType.COMMIT);

        entity = new Project();
        entity.setCompletedDate(data.getCompletedDate());
        entity.setDescription(data.getDescription());
        entity.setIsActive(data.getIsActive());
        entity.setName(data.getName());
        entity.setOwnerId(data.getOwnerId());
        entity.setReferenceTo(data.getReferenceTo());
        entity.setScriptletId(data.getScriptletId());
        entity.setStartedDate(data.getStartedDate());

        manager.persist(entity);
        data.setId(entity.getId());

        return data;
    }

    public ProjectDO update(ProjectDO data) throws Exception {
        Project entity;

        if ( !data.isChanged())
            return data;

        manager.setFlushMode(FlushModeType.COMMIT);

        entity = manager.find(Project.class, data.getId());

        entity.setCompletedDate(data.getCompletedDate());
        entity.setDescription(data.getDescription());
        entity.setIsActive(data.getIsActive());
        entity.setName(data.getName());
        entity.setOwnerId(data.getOwnerId());
        entity.setReferenceTo(data.getReferenceTo());
        entity.setScriptletId(data.getScriptletId());
        entity.setStartedDate(data.getStartedDate());

        return data;
    }

    public void validate(ProjectDO data) throws Exception {
        ArrayList<ProjectDO> dups;
        ValidationErrorsList list;
        
        list = new ValidationErrorsList();
        if (DataBaseUtil.isEmpty(data.getIsActive()))
            list.add(new FieldErrorException(Messages.get().fieldRequiredException(), ProjectMeta.getIsActive()));

        if (DataBaseUtil.isEmpty(data.getOwnerId()))
            list.add(new FieldErrorException(Messages.get().fieldRequiredException(), ProjectMeta.getOwnerId()));

        if (DataBaseUtil.isEmpty(data.getStartedDate()))
            list.add(new FieldErrorException(Messages.get().fieldRequiredException(), ProjectMeta.getStartedDate()));

        if (DataBaseUtil.isEmpty(data.getCompletedDate()))
            list.add(new FieldErrorException(Messages.get().fieldRequiredException(), ProjectMeta.getCompletedDate()));

        if (DataBaseUtil.isAfter(data.getStartedDate(), data.getCompletedDate()))
            list.add(new FieldErrorException(Messages.get().endDateAfterBeginDateException(),
                                                      ProjectMeta.getCompletedDate()));
        //
        // check for duplicate name
        //
        if (DataBaseUtil.isEmpty(data.getName())) { 
            list.add(new FieldErrorException(Messages.get().fieldRequiredException(), ProjectMeta.getName()));
        } else {
            if ("Y".equals(data.getIsActive())) {
                try {
                    dups = fetchActiveByName(data.getName(), 1);
                    if (dups.size() > 0 && DataBaseUtil.isDifferent(data.getId(), dups.get(0).getId())) 
                        list.add(new FieldErrorException(Messages.get().fieldUniqueException(), ProjectMeta.getName()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        if (list.size() > 0)
            throw list;
    }
}
