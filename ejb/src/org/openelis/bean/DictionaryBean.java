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
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.annotation.security.SecurityDomain;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.DictionaryViewDO;
import org.openelis.domain.IdNameVO;
import org.openelis.entity.Dictionary;
import org.openelis.gwt.common.DatabaseException;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.gwt.common.data.QueryData;
import org.openelis.local.DictionaryLocal;
import org.openelis.metamap.CategoryMetaMap;
import org.openelis.metamap.DictionaryMetaMap;
import org.openelis.remote.DictionaryRemote;
import org.openelis.util.QueryBuilderV2;
import org.openelis.utilcommon.DataBaseUtil;

@Stateless
@SecurityDomain("openelis")
@RolesAllowed("dictionary-select")
public class DictionaryBean implements DictionaryLocal, DictionaryRemote {

    @PersistenceContext(name = "openelis")
    private EntityManager            manager;

    private static DictionaryMetaMap meta    = new DictionaryMetaMap();

    private static CategoryMetaMap   catMeta = new CategoryMetaMap();

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

    public DictionaryViewDO add(DictionaryViewDO data) throws Exception {
        Dictionary dictionary;

        manager.setFlushMode(FlushModeType.COMMIT);

        dictionary = new Dictionary();
        dictionary.setCategoryId(data.getCategoryId());
        dictionary.setEntry(data.getEntry());
        dictionary.setIsActive(data.getIsActive());
        dictionary.setLocalAbbrev(data.getLocalAbbrev());
        dictionary.setRelatedEntryId(data.getRelatedEntryId());
        dictionary.setSystemName(data.getSystemName());
        dictionary.setSortOrder(data.getSortOrder());

        manager.persist(dictionary);
        data.setId(dictionary.getId());

        return data;
    }

    public DictionaryViewDO update(DictionaryViewDO data) throws Exception {
        Dictionary dictionary;

        if ( !data.isChanged())
            return data;

        dictionary = manager.find(Dictionary.class, data.getId());

        dictionary.setCategoryId(data.getCategoryId());
        dictionary.setEntry(data.getEntry());
        dictionary.setIsActive(data.getIsActive());
        dictionary.setLocalAbbrev(data.getLocalAbbrev());
        dictionary.setRelatedEntryId(data.getRelatedEntryId());
        dictionary.setSystemName(data.getSystemName());
        dictionary.setSortOrder(data.getSortOrder());

        return data;
    }

    public void delete(DictionaryViewDO data) throws Exception {
        Dictionary entity;       

        manager.setFlushMode(FlushModeType.COMMIT);

        entity = manager.find(Dictionary.class, data.getId());
        if (entity != null)
            manager.remove(entity);
    }

    public void validate(DictionaryViewDO data) throws Exception {
        ValidationErrorsList list;
        String entry;

        list = new ValidationErrorsList();
        entry = data.getEntry();

        if (DataBaseUtil.isEmpty(entry))
            list.add(new FieldErrorException("fieldRequiredException", catMeta.getDictionary()
                                                                              .getEntry()));

        if (list.size() > 0)
            throw list;

    }
    
    public void validateForDelete(DictionaryViewDO data) throws Exception{
        Query query; 
        ValidationErrorsList list;
        List result;
        boolean valid;
        
        list = new ValidationErrorsList();
        valid = true;
        
        query = manager.createNamedQuery("Dictionary.ReferenceCheckForId");
        query.setParameter("id",data.getId());
        result = query.getResultList();  
        
        if(result.size() > 0) 
            valid = false;
        
        query = manager.createNamedQuery("Dictionary.ReferenceCheckForValue");
        query.setParameter("value",String.valueOf(data.getId()));
        result = query.getResultList();
        
        if(result.size() > 0) 
            valid = false;
        
        query = manager.createNamedQuery("Dictionary.ReferenceCheckForStateCountry");
        query.setParameter("value",data.getEntry());
        result = query.getResultList();
        
        if(result.size() > 0) 
            valid = false;
        
        if(!valid) {
            list.add(new FieldErrorException("dictionaryDeleteException",null));
            throw list;
        }
        
        
    }

    public ArrayList<DictionaryDO> fetchByCategorySystemName(String categoryName) throws Exception {
        Query query;

        query = manager.createNamedQuery("Dictionary.FetchByCategorySystemName");
        query.setParameter("name", categoryName);

        return DataBaseUtil.toArrayList(query.getResultList());
    }

    public ArrayList<DictionaryDO> fetchByEntry(String entry) throws Exception {
        Query query;

        query = manager.createNamedQuery("Dictionary.FetchByEntry");
        query.setParameter("entry", entry);

        return DataBaseUtil.toArrayList(query.getResultList());
    }

    public DictionaryViewDO fetchById(Integer id) throws Exception {
        Query query;
        DictionaryViewDO dictDO;

        query = manager.createNamedQuery("Dictionary.FetchById");
        query.setParameter("id", id);

        try {
            dictDO = (DictionaryViewDO)query.getSingleResult();
        } catch (NoResultException e) {
            throw new NotFoundException();
        } catch (Exception e) {
            throw new DatabaseException(e);
        }

        return dictDO;
    }

    public ArrayList<DictionaryDO> fetchByEntryAndCategoryId(ArrayList<QueryData> fields) throws Exception {
        Query query;
        QueryBuilderV2 qb;
        List list;

        qb = new QueryBuilderV2();
        qb.setMeta(meta);

        qb.setSelect("distinct new org.openelis.domain.DictionaryDO(" + meta.getId() + ", " +
                     meta.getSortOrder() + ", " + meta.getCategoryId() + ", " +
                     meta.getRelatedEntryId() + ", " + meta.getSystemName() + ", " +
                     meta.getIsActive() + ", " + meta.getLocalAbbrev() + ", " +
                     meta.getEntry()+ ") ");

        qb.constructWhere(fields);
        qb.setOrderBy(meta.getSortOrder());

        query = manager.createQuery(qb.getEJBQL());

        // ***set the parameters in the query
        qb.setQueryParams(query, fields);

        list = query.getResultList();

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

    public ArrayList<IdNameVO> fetchIdEntryByCategoryId(Integer categoryId) throws Exception {
        Query query;

        query = manager.createNamedQuery("Dictionary.FetchIdEntryByCategoryId");
        query.setParameter("id", categoryId);

        return DataBaseUtil.toArrayList(query.getResultList());
    }

    public ArrayList<IdNameVO> fetchIdEntryByEntry(String entry, int maxResults) throws Exception {
        Query query;

        query = manager.createNamedQuery("Dictionary.FetchIdEntryByEntry");
        query.setParameter("entry", entry);
        query.setMaxResults(maxResults);

        return DataBaseUtil.toArrayList(query.getResultList());
    }

    public Integer getNumResultsAffected(String entry, Integer id) throws Exception {
        Integer count = null;
        String oldVal = null;
        DictionaryViewDO dictDO;
        Query query = manager.createNamedQuery("TestResult.FetchByValue");
        query.setParameter("value", id.toString());
        
        count = query.getResultList().size();

        if (count > 0) {
            query = manager.createNamedQuery("Dictionary.FetchById");
            query.setParameter("id", id);
            dictDO = (DictionaryViewDO)query.getSingleResult();
            oldVal = (String)dictDO.getEntry();
            if (oldVal.trim().equals(entry.trim()))
                count = 0;
        }
        return count;
    }

}
