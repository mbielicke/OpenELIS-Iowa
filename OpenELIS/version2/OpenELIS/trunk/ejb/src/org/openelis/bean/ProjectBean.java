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
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.ejb3.annotation.SecurityDomain;
import org.openelis.domain.IdNameVO;
import org.openelis.domain.ProjectDO;
import org.openelis.domain.ProjectViewDO;
import org.openelis.entity.Project;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.DatabaseException;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.SystemUserVO;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.gwt.common.data.QueryData;
import org.openelis.local.ProjectLocal;
import org.openelis.meta.ProjectMeta;
import org.openelis.remote.ProjectRemote;
import org.openelis.util.QueryBuilderV2;
import org.openelis.utils.EJBFactory;

@Stateless
@SecurityDomain("openelis")

public class ProjectBean implements ProjectLocal, ProjectRemote {

    @PersistenceContext(unitName = "openelis")
    private EntityManager       manager;

    private static ProjectMeta  meta = new ProjectMeta();

    public ProjectViewDO fetchById(Integer id) throws Exception {
        Query query;
        ProjectViewDO data;
        SystemUserVO user;

        query = manager.createNamedQuery("Project.FetchById");
        query.setParameter("id", id);

        try {
            data = (ProjectViewDO)query.getSingleResult();
            if (data.getOwnerId() != null) {
                user = EJBFactory.getUserCache().getSystemUser(data.getOwnerId());
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

    public ArrayList<IdNameVO> fetchByIds(ArrayList<Integer> ids) throws Exception {
        Query query;

        query = manager.createNamedQuery("Project.FetchByIds");
        query.setParameter("ids", ids);

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

    public ProjectViewDO add(ProjectViewDO data) throws Exception {
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

    public ProjectViewDO update(ProjectViewDO data) throws Exception {
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

    public void validate(ProjectViewDO data) throws ValidationErrorsList {
        ArrayList<ProjectDO> dups;
        ValidationErrorsList list;
        
        list = new ValidationErrorsList();
        if (DataBaseUtil.isEmpty(data.getIsActive()))
            list.add(new FieldErrorException("fieldRequiredException", ProjectMeta.getIsActive()));

        if (DataBaseUtil.isEmpty(data.getOwnerId()))
            list.add(new FieldErrorException("fieldRequiredException", ProjectMeta.getOwnerId()));

        if (DataBaseUtil.isEmpty(data.getStartedDate()))
            list.add(new FieldErrorException("fieldRequiredException", ProjectMeta.getStartedDate()));

        if (DataBaseUtil.isEmpty(data.getCompletedDate()))
            list.add(new FieldErrorException("fieldRequiredException", ProjectMeta.getCompletedDate()));

        if (DataBaseUtil.isAfter(data.getStartedDate(), data.getCompletedDate()))
            list.add(new FieldErrorException("endDateAfterBeginDateException",
                                                      ProjectMeta.getCompletedDate()));
        //
        // check for duplicate name
        //
        if (DataBaseUtil.isEmpty(data.getName())) { 
            list.add(new FieldErrorException("fieldRequiredException", ProjectMeta.getName()));
        } else {
            if ("Y".equals(data.getIsActive())) {
                try {
                    dups = fetchActiveByName(data.getName(), 1);
                    if (dups.size() > 0 && DataBaseUtil.isDifferent(data.getId(), dups.get(0).getId())) 
                        list.add(new FieldErrorException("fieldUniqueException", ProjectMeta.getName()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        if (list.size() > 0)
            throw list;
    }
}
