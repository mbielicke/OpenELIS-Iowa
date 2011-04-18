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

import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.ejb3.annotation.SecurityDomain;
import org.openelis.domain.CategoryDO;
import org.openelis.domain.IdNameVO;
import org.openelis.entity.Category;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.DatabaseException;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.gwt.common.data.QueryData;
import org.openelis.local.CategoryCacheLocal;
import org.openelis.local.CategoryLocal;
import org.openelis.meta.CategoryMeta;
import org.openelis.remote.CategoryRemote;
import org.openelis.util.QueryBuilderV2;

@Stateless
@SecurityDomain("openelis")
@RolesAllowed("dictionary-select")
public class CategoryBean implements CategoryRemote, CategoryLocal {

    @PersistenceContext(unitName = "openelis")
    private EntityManager          manager;

    @EJB
    private CategoryCacheLocal      catCache;
    
    private static CategoryMeta    meta = new CategoryMeta();

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
        query.setParameter("name",name);
        list = query.getResultList();
        
        if(list.isEmpty())
            throw new NotFoundException();
        
        return DataBaseUtil.toArrayList(list);
    }    
    

    public ArrayList<IdNameVO> query(ArrayList<QueryData> fields, int first, int max) throws Exception {
        Query query;
        QueryBuilderV2 builder;
        List list;

        builder = new QueryBuilderV2();
        builder.setMeta(meta);
        builder.setSelect("distinct new org.openelis.domain.IdNameVO(" + CategoryMeta.getId() + ", " +
                          CategoryMeta.getName() + ") ");

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
        if (!data.isChanged())
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
        Integer catId;
        String sysName, name;
        CategoryDO category;

        list = new ValidationErrorsList();
        
        sysName = data.getSystemName();
        name = data.getName();
        catId = null;
        
        if (DataBaseUtil.isEmpty(sysName)) {
            list.add(new FieldErrorException("fieldRequiredException",
                                             CategoryMeta.getSystemName()));
        } else {
            try {
                category = fetchBySystemName(sysName);
                catId = category.getId();
            } catch (NotFoundException ignE) {
                // do nothing
            }            
            
            if (!DataBaseUtil.isEmpty(catId) && !catId.equals(data.getId())) {
                list.add(new FieldErrorException("fieldUniqueException",
                                                 CategoryMeta.getSystemName()));
            }
        }

        if (DataBaseUtil.isEmpty(name)) 
            list.add(new FieldErrorException("fieldRequiredException", 
                                             CategoryMeta.getName()));              
        
        if(list.size() > 0)
            throw list;
    }

}
