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

import org.jboss.ejb3.annotation.SecurityDomain;
import org.openelis.domain.CategoryCacheVO;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.DictionaryViewDO;
import org.openelis.domain.IdNameVO;
import org.openelis.entity.Dictionary;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.DatabaseException;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.gwt.common.data.QueryData;
import org.openelis.local.DictionaryCacheLocal;
import org.openelis.local.DictionaryLocal;
import org.openelis.meta.CategoryMeta;
import org.openelis.remote.DictionaryRemote;
import org.openelis.util.QueryBuilderV2;

@Stateless
@SecurityDomain("openelis")

public class DictionaryBean implements DictionaryLocal, DictionaryRemote {

    @PersistenceContext(unitName = "openelis")
    private EntityManager        manager;

    @EJB
    private DictionaryCacheLocal dictCache;        

    private static CategoryMeta  meta = new CategoryMeta();

    public ArrayList<DictionaryViewDO> fetchByCategoryId(Integer id) throws Exception {
        List<DictionaryViewDO> list;
        Query query;

        query = manager.createNamedQuery("Dictionary.FetchByCategoryId");
        query.setParameter("id", id);

        list = query.getResultList();// getting list of dictionary entries
        if (list.isEmpty())
            throw new NotFoundException();

        return DataBaseUtil.toArrayList(list);
    }

    public ArrayList<DictionaryDO> fetchByCategorySystemName(String categoryName) throws Exception {
        Query query;

        query = manager.createNamedQuery("Dictionary.FetchByCategorySystemName");
        query.setParameter("name", categoryName);

        return DataBaseUtil.toArrayList(query.getResultList());
    }

    public ArrayList<DictionaryViewDO> fetchByEntry(String entry, int max) throws Exception {
        Query query;

        query = manager.createNamedQuery("Dictionary.FetchByEntry");
        query.setParameter("entry", entry);
        query.setMaxResults(max);

        return DataBaseUtil.toArrayList(query.getResultList());
    }

    public DictionaryViewDO fetchById(Integer id) throws Exception {
        Query query;
        DictionaryViewDO data;

        query = manager.createNamedQuery("Dictionary.FetchById");
        query.setParameter("id", id);

        try {
            data = (DictionaryViewDO)query.getSingleResult();
        } catch (NoResultException e) {
            throw new NotFoundException();
        } catch (Exception e) {
            throw new DatabaseException(e);
        }

        return data;
    }
    
    public ArrayList<DictionaryViewDO> fetchByIds(Collection<Integer> ids) throws Exception {
        Query query;

        query = manager.createNamedQuery("Dictionary.FetchByIds");
        query.setParameter("ids", ids);

        return DataBaseUtil.toArrayList(query.getResultList());
    }

    public ArrayList<IdNameVO> fetchByEntry(ArrayList<QueryData> fields) throws Exception {
        Query query;
        QueryBuilderV2 builder;
        List list;

        builder = new QueryBuilderV2();
        builder.setMeta(meta);

        builder.setSelect("distinct new org.openelis.domain.IdNameVO(" +
                          CategoryMeta.getDictionaryId() + ", " +
                          CategoryMeta.getDictionaryEntry() + ", " + CategoryMeta.getName() + ") ");

        builder.constructWhere(fields);
        builder.setOrderBy(CategoryMeta.getDictionaryEntry() + ", " + CategoryMeta.getName());

        query = manager.createQuery(builder.getEJBQL());
        builder.setQueryParams(query, fields);

        list = query.getResultList();
        if (list.isEmpty())
            throw new NotFoundException();

        return DataBaseUtil.toArrayList(list);
    }

    public DictionaryDO fetchBySystemName(String systemName) throws Exception {
        Query query;
        DictionaryDO dictDO;

        query = manager.createNamedQuery("Dictionary.FetchBySystemName");
        query.setParameter("name", systemName);
        try {
            dictDO = (DictionaryDO)query.getSingleResult();
        } catch (NoResultException e) {
            throw new NotFoundException();
        } catch (Exception e) {
            throw new DatabaseException(e);
        }

        return dictDO;
    }

    public DictionaryViewDO add(DictionaryViewDO data) throws Exception {
        Dictionary entity;

        manager.setFlushMode(FlushModeType.COMMIT);

        entity = new Dictionary();
        entity.setCategoryId(data.getCategoryId());
        entity.setEntry(data.getEntry());
        entity.setIsActive(data.getIsActive());
        entity.setLocalAbbrev(data.getLocalAbbrev());
        entity.setRelatedEntryId(data.getRelatedEntryId());
        entity.setSystemName(data.getSystemName());
        entity.setSortOrder(data.getSortOrder());

        manager.persist(entity);
        data.setId(entity.getId());

        return data;
    }

    public DictionaryViewDO update(DictionaryViewDO data) throws Exception {
        Dictionary entity;

        if ( !data.isChanged())
            return data;

        manager.setFlushMode(FlushModeType.COMMIT);
        entity = manager.find(Dictionary.class, data.getId());

        // need to remove it before we change it
        dictCache.evict(entity.getId());
        dictCache.evict(entity.getSystemName());

        entity.setCategoryId(data.getCategoryId());
        entity.setEntry(data.getEntry());
        entity.setIsActive(data.getIsActive());
        entity.setLocalAbbrev(data.getLocalAbbrev());
        entity.setRelatedEntryId(data.getRelatedEntryId());
        entity.setSystemName(data.getSystemName());
        entity.setSortOrder(data.getSortOrder());

        return data;
    }

    public void delete(DictionaryViewDO data) throws Exception {
        Dictionary entity;
        
        manager.setFlushMode(FlushModeType.COMMIT);
        entity = manager.find(Dictionary.class, data.getId());
        
        if (entity != null) {
            dictCache.evict(entity.getId());
            dictCache.evict(entity.getSystemName());
            manager.remove(entity);
        }
    }

    public void validate(DictionaryViewDO data) throws Exception {
        ValidationErrorsList list;
        String entry;

        list = new ValidationErrorsList();
        entry = data.getEntry();

        if (entry == null)
            list.add(new FieldErrorException("fieldRequiredException",
                                             CategoryMeta.getDictionaryEntry()));
        if (list.size() > 0)
            throw list;
    }

    public ArrayList<CategoryCacheVO> preLoadBySystemName(ArrayList<CategoryCacheVO> cacheVO) throws Exception {
        CategoryCacheVO catVO;

        for (int i = 0; i < cacheVO.size(); i++ ) {
            catVO = cacheVO.get(i);
            catVO.setDictionaryList(fetchByCategorySystemName(catVO.getSystemName()));
        }

        return cacheVO;
    }

    public void validateForDelete(DictionaryViewDO data) throws Exception {
        Query query;
        ValidationErrorsList list;
        List result;

        list = new ValidationErrorsList();

        query = manager.createNamedQuery("Dictionary.ReferenceCheckForEntry");
        query.setParameter("entry", data.getEntry());
        result = query.getResultList();

        if (result.size() > 0) {
            list.add(new FieldErrorException("dictionaryDeleteException", null));
            throw list;
        }

        query = manager.createNamedQuery("Dictionary.ReferenceCheckForValue");
        query.setParameter("value", String.valueOf(data.getId()));
        result = query.getResultList();

        if (result.size() > 0) {
            list.add(new FieldErrorException("dictionaryDeleteException", null));
            throw list;
        }

        query = manager.createNamedQuery("Dictionary.ReferenceCheckForId");
        query.setParameter("id", data.getId());
        result = query.getResultList();

        if (result.size() > 0) {
            list.add(new FieldErrorException("dictionaryDeleteException", null));
            throw list;
        }

    }

}
