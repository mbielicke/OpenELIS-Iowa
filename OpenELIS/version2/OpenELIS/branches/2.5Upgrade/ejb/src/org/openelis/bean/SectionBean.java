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
import org.openelis.domain.SectionDO;
import org.openelis.domain.SectionViewDO;
import org.openelis.entity.Section;
import org.openelis.meta.SectionMeta;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.DatabaseException;
import org.openelis.ui.common.FieldErrorException;
import org.openelis.ui.common.LastPageException;
import org.openelis.ui.common.NotFoundException;
import org.openelis.ui.common.ValidationErrorsList;
import org.openelis.ui.common.data.QueryData;
import org.openelis.util.QueryBuilderV2;

@Stateless
@SecurityDomain("openelis")
public class SectionBean {

    @PersistenceContext(unitName = "openelis")
    private EntityManager            manager;

    @EJB
    private SectionCacheBean         secCache;

    private static final SectionMeta meta = new SectionMeta();

    public SectionViewDO fetchById(Integer id) throws Exception {
        SectionViewDO data;
        Query query;

        query = manager.createNamedQuery("Section.FetchById");
        query.setParameter("id", id);
        try {
            data = (SectionViewDO)query.getSingleResult();
        } catch (NoResultException e) {
            throw new NotFoundException();
        } catch (Exception e) {
            throw new DatabaseException(e);
        }
        return data;
    }

    public ArrayList<SectionDO> fetchByName(String name, int maxResults) throws Exception {
        Query query;

        query = manager.createNamedQuery("Section.FetchByName");
        query.setParameter("name", name);
        query.setMaxResults(maxResults);

        return DataBaseUtil.toArrayList(query.getResultList());
    }

    public ArrayList<SectionViewDO> fetchList() throws Exception {
        Query query;
        List<SectionViewDO> sections;

        query = manager.createNamedQuery("Section.FetchList");
        sections = query.getResultList();
        return DataBaseUtil.toArrayList(sections);
    }

    public ArrayList<IdNameVO> query(ArrayList<QueryData> fields, int first, int max) throws Exception {
        Query query;
        QueryBuilderV2 builder;
        List list;

        builder = new QueryBuilderV2();
        builder.setMeta(meta);

        builder.setSelect("distinct new org.openelis.domain.IdNameVO(" +
                          SectionMeta.getId() + ", " + SectionMeta.getName() + ") ");

        builder.constructWhere(fields);
        builder.setOrderBy(SectionMeta.getName());

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

    public SectionDO add(SectionDO data) throws Exception {
        Section entity;

        manager.setFlushMode(FlushModeType.COMMIT);

        entity = new Section();

        entity.setDescription(data.getDescription());
        entity.setOrganizationId(data.getOrganizationId());
        entity.setName(data.getName());
        entity.setIsExternal(data.getIsExternal());
        entity.setParentSectionId(data.getParentSectionId());
        manager.persist(entity);

        data.setId(entity.getId());

        // empty the cache so that it gets refreshed on the next fetch
        secCache.evict();

        return data;
    }

    public SectionDO update(SectionDO data) throws Exception {
        Section entity;

        if ( !data.isChanged())
            return data;

        manager.setFlushMode(FlushModeType.COMMIT);
        entity = manager.find(Section.class, data.getId());

        // need to remove it before we change it
        secCache.evict();

        entity.setDescription(data.getDescription());
        entity.setOrganizationId(data.getOrganizationId());
        entity.setName(data.getName());
        entity.setIsExternal(data.getIsExternal());
        entity.setParentSectionId(data.getParentSectionId());

        return data;
    }

    public void validate(SectionDO data) throws Exception {
        String name;
        ValidationErrorsList exceptionList;
        Query query;
        List<SectionDO> list;
        SectionDO sectDO;
        int i;
        Integer psecId;

        name = data.getName();
        exceptionList = new ValidationErrorsList();
        if (name == null) {
            exceptionList.add(new FieldErrorException(Messages.get().fieldRequiredException(),
                                                      SectionMeta.getName()));
        } else {
            query = manager.createNamedQuery("Section.FetchByName");
            query.setParameter("name", name);
            list = query.getResultList();
            for (i = 0; i < list.size(); i++ ) {
                sectDO = list.get(i);
                if ( !sectDO.getId().equals(data.getId())) {
                    exceptionList.add(new FieldErrorException(Messages.get().fieldUniqueException(),
                                                              SectionMeta.getName()));
                    break;
                }
            }
        }

        if ("Y".equals(data.getIsExternal()) && data.getOrganizationId() == null)
            exceptionList.add(new FieldErrorException(Messages.get().orgNotSpecForExtSectionException(),
                                                      null));

        psecId = data.getParentSectionId();
        if (psecId != null && psecId.equals(data.getId())) {
            exceptionList.add(new FieldErrorException(Messages.get().sectItsOwnParentException(),
                                                      SectionMeta.getParentSectionName()));
        }

        if (exceptionList.size() > 0)
            throw exceptionList;
    }
}