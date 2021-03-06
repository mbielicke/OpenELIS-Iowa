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
import org.openelis.domain.CategoryDO;
import org.openelis.domain.IdNameVO;
import org.openelis.entity.Category;
import org.openelis.meta.CategoryMeta;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.DatabaseException;
import org.openelis.ui.common.FormErrorException;
import org.openelis.ui.common.LastPageException;
import org.openelis.ui.common.NotFoundException;
import org.openelis.ui.common.ValidationErrorsList;
import org.openelis.ui.common.data.QueryData;
import org.openelis.util.QueryBuilderV2;

@Stateless
@SecurityDomain("openelis")
public class CategoryBean {

    @PersistenceContext(unitName = "openelis")
    private EntityManager       manager;

    @EJB
    private CategoryCacheBean   catCache;

    private static CategoryMeta meta = new CategoryMeta();

    public CategoryBean() {
    }

    public CategoryDO fetchById(Integer id) throws Exception {
        Query query;
        CategoryDO data;

        query = manager.createNamedQuery("Category.FetchById");
        query.setParameter("id", id);
        try {
            data = (CategoryDO)query.getSingleResult();
        } catch (NoResultException e) {
            throw new NotFoundException();
        } catch (Exception e) {
            throw new DatabaseException(e);
        }
        return data;
    }

    public ArrayList<CategoryDO> fetchByIds(ArrayList<Integer> ids) {
        Query query;

        query = manager.createNamedQuery("Category.FetchByIds");
        query.setParameter("ids", ids);

        return DataBaseUtil.toArrayList(query.getResultList());
    }

    public CategoryDO fetchBySystemName(String systemName) throws Exception {
        Query query;
        CategoryDO data;

        data = null;
        query = manager.createNamedQuery("Category.FetchBySystemName");
        query.setParameter("systemName", systemName);
        try {
            data = (CategoryDO)query.getSingleResult();
        } catch (NoResultException e) {
            throw new NotFoundException();
        } catch (Exception e) {
            throw new DatabaseException(e);
        }
        return data;
    }

    public ArrayList<IdNameVO> fetchByName(String name) throws Exception {
        Query query;
        List list;

        query = manager.createNamedQuery("Category.FetchByName");
        query.setParameter("name", name);
        list = query.getResultList();

        if (list.isEmpty())
            throw new NotFoundException();

        return DataBaseUtil.toArrayList(list);
    }

    public ArrayList<IdNameVO> query(ArrayList<QueryData> fields, int first, int max) throws Exception {
        Query query;
        QueryBuilderV2 builder;
        List list;

        builder = new QueryBuilderV2();
        builder.setMeta(meta);
        builder.setSelect("distinct new org.openelis.domain.IdNameVO(" + CategoryMeta.getId() +
                          ", " + CategoryMeta.getName() + ") ");

        builder.constructWhere(fields);
        builder.setOrderBy(CategoryMeta.getName());

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

    public CategoryDO add(CategoryDO data) throws Exception {
        Category entity;

        manager.setFlushMode(FlushModeType.COMMIT);

        entity = new Category();

        entity.setDescription(data.getDescription());
        entity.setName(data.getName());
        entity.setSectionId(data.getSectionId());
        entity.setSystemName(data.getSystemName());
        entity.setIsSystem(data.getIsSystem());

        manager.persist(entity);

        data.setId(entity.getId());

        return data;
    }

    public CategoryDO update(CategoryDO data) throws Exception {
        Category entity;

        catCache.evict(data.getSystemName());
        if ( !data.isChanged())
            return data;

        manager.setFlushMode(FlushModeType.COMMIT);
        entity = manager.find(Category.class, data.getId());

        // need to remove it before we change it
        catCache.evict(entity.getSystemName());

        entity.setDescription(data.getDescription());
        entity.setName(data.getName());
        entity.setSectionId(data.getSectionId());
        entity.setSystemName(data.getSystemName());
        entity.setIsSystem(data.getIsSystem());

        return data;
    }

    public void validate(CategoryDO data) throws Exception {
        ValidationErrorsList list;
        Integer catId, cid;
        String sysName;
        CategoryDO category;

        list = new ValidationErrorsList();

        cid = data.getId();
        if (cid == null)
            cid = 0;

        sysName = data.getSystemName();
        catId = null;

        if (DataBaseUtil.isEmpty(sysName)) {
            list.add(new FormErrorException(Messages.get()
                                                    .dictionary_systemNameRequiredException(cid)));
        } else {
            try {
                category = fetchBySystemName(sysName);
                catId = category.getId();
            } catch (NotFoundException ignE) {
                // do nothing
            }
            if ( !DataBaseUtil.isEmpty(catId) && !catId.equals(data.getId())) {
                list.add(new FormErrorException(Messages.get().dictionary_uniqueException(cid)));
            }
        }

        if (DataBaseUtil.isEmpty(data.getName()))
            list.add(new FormErrorException(Messages.get().dictionary_nameRequiredException(cid)));

        if (list.size() > 0)
            throw list;
    }

}
