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
import org.openelis.domain.CategoryDO;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.DictionaryViewDO;
import org.openelis.domain.IdNameVO;
import org.openelis.entity.Category;
import org.openelis.gwt.common.DatabaseException;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.TableFieldErrorException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.gwt.common.data.QueryData;
import org.openelis.local.CategoryLocal;
import org.openelis.metamap.CategoryMetaMap;
import org.openelis.metamap.DictionaryMetaMap;
import org.openelis.remote.CategoryRemote;
import org.openelis.util.QueryBuilder;
import org.openelis.util.QueryBuilderV2;
import org.openelis.utilcommon.DataBaseUtil;

@Stateless
@SecurityDomain("openelis")
@RolesAllowed("dictionary-select")
public class CategoryBean implements CategoryRemote, CategoryLocal {

    @PersistenceContext(name = "openelis")
    private EntityManager          manager;

    private static CategoryMetaMap meta = new CategoryMetaMap();

    public CategoryBean() {
    }

    public CategoryDO fetchById(Integer id) throws Exception {
        Query query;
        CategoryDO data;

        query = manager.createNamedQuery("Category.FetchById");
        query.setParameter("id", id);
        try {
            data = (CategoryDO)query.getSingleResult();// getting category
        } catch (NoResultException e) {
            throw new NotFoundException();
        } catch (Exception e) {
            throw new DatabaseException(e);
        }
        return data;
    }
    
    public List<IdNameVO> getCategoryList() {
        Query query = manager.createNamedQuery("Category.FetchIdName");
        List idNameDOList = query.getResultList();
        return idNameDOList;
    }

    public ArrayList<DictionaryViewDO> getEntries(Integer categoryId) {
        ArrayList<DictionaryViewDO> entries;
        Query query;

        query = manager.createNamedQuery("Dictionary.FetchByCategoryId");
        query.setParameter("id", categoryId);

        entries = (ArrayList<DictionaryViewDO>)query.getResultList();// getting
        // list of
        // dictionary entries

        return entries;
    }

    public DictionaryDO getDictionaryDOBySystemName(String systemName) {
        Query query = manager.createNamedQuery("Dictionary.FetchBySystemName");
        query.setParameter("name", systemName);

        List resultList = query.getResultList();

        if (resultList.size() > 0)
            return (DictionaryDO)resultList.get(0);
        else
            return null;
    }

    public DictionaryViewDO getDictionaryDOByEntryId(Integer entryId) {
        Query query = manager.createNamedQuery("Dictionary.FetchById");
        query.setParameter("id", entryId);

        List resultList = query.getResultList();

        if (resultList.size() > 0)
            return (DictionaryViewDO)resultList.get(0);
        else
            return null;
    }

    public List getListByCategoryName(String categoryName) {
        Query query = manager.createNamedQuery("Dictionary.FetchByCategorySystemName");
        query.setParameter("name", categoryName);

        return query.getResultList();
    }

    public ArrayList<IdNameVO> getDropdownValues(Integer categoryId) {
        Query query = manager.createNamedQuery("Dictionary.FetchIdNameByCategoryId");
        query.setParameter("id", categoryId);

        return DataBaseUtil.toArrayList(query.getResultList());
    }

    public ArrayList<IdNameVO> autoCompleteByEntry(String entry, int maxResults) {
        Query query = manager.createNamedQuery("Dictionary.FetchIdNameByEntry");
        query.setParameter("entry", entry);
        query.setMaxResults(maxResults);

        ArrayList<IdNameVO> entryList = null;
        try {
            entryList = DataBaseUtil.toArrayList(query.getResultList());
        } catch (Exception ex) {
            ex.printStackTrace();

        }
        return entryList;
    }

    public Integer getEntryIdForSystemName(String systemName) {
        Query query;
        List<DictionaryDO> results;
        query = manager.createNamedQuery("Dictionary.FetchBySystemName");
        query.setParameter("name", systemName);
        
        results = query.getResultList();

        if (results.size() == 0)
            return null;

        return results.get(0).getId();
    }

    public Integer getEntryIdForEntry(String entry) {
        Query query = manager.createNamedQuery("Dictionary.FetchByEntry");
        query.setParameter("entry", entry);
        List<DictionaryDO> results;

        results = query.getResultList();

        if (results.size() == 0)
            return null;

        return results.get(0).getId();
    }

    public String getSystemNameForEntryId(Integer entryId) {
        Query query = manager.createNamedQuery("Dictionary.FetchById");
        query.setParameter("id", entryId);
        List<DictionaryViewDO> results = null;

        results = query.getResultList();

        if (results.size() == 0)
            return null;

        return results.get(0).getSystemName();
    }

    public Integer getCategoryId(String systemName) {
        Query query;
        Integer categoryId;
        CategoryDO catDO;
        
        query = manager.createNamedQuery("Category.FetchBySystemName");
        query.setParameter("systemName", systemName);
        categoryId = null;
        try {
            catDO = (CategoryDO)query.getSingleResult();
            categoryId = catDO.getId();
        } catch (Exception ex) {
            ex.printStackTrace();

        }
        return categoryId;
    }

    public ArrayList getDictionaryListByPatternAndCategory(ArrayList<QueryData> fields) {
        StringBuffer sb = new StringBuffer();
        QueryBuilder qb = new QueryBuilder();
        DictionaryMetaMap dictMeta;
        ArrayList returnList;

        dictMeta = new DictionaryMetaMap();
        qb.setMeta(dictMeta);

        qb.setSelect("distinct new org.openelis.domain.IdNameDO(" + dictMeta.getId() + ", " +
                     dictMeta.getEntry() + ") ");
        try {
            qb.addNewWhere(fields);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        qb.setOrderBy(dictMeta.getEntry());

        sb.append(qb.getEJBQL());
        Query query = manager.createQuery(sb.toString());

        // ***set the parameters in the query
        qb.setNewQueryParams(query, fields);

        returnList = (ArrayList<DictionaryViewDO>)query.getResultList();

        return returnList;
    }

    public Integer getNumResultsAffected(String entry, Integer id) {
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

    public ArrayList<DictionaryDO> getDictionaryListByEntry(String entry) {
        Query query;        

        query = manager.createNamedQuery("Dictionary.FetchByEntry");
        query.setParameter("entry", entry);

        return DataBaseUtil.toArrayList(query.getResultList());
    }
    

    public ArrayList<IdNameVO> query(ArrayList<QueryData> fields, int first, int max)
                                                                                     throws Exception {
        Query query;
        QueryBuilderV2 builder;
        List list;

        builder = new QueryBuilderV2();
        builder.setMeta(meta);
        builder.setSelect("distinct new org.openelis.domain.IdNameVO(" + meta.getId() + ", " +
                          meta.getName() + ") ");

        builder.constructWhere(fields);
        builder.setOrderBy(meta.getName());

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
        Category category;

        manager.setFlushMode(FlushModeType.COMMIT);

        category = new Category();

        category.setDescription(data.getDescription());
        category.setName(data.getName());
        category.setSectionId(data.getSection());
        category.setSystemName(data.getSystemName());

        manager.persist(category);

        data.setId(category.getId());

        return data;
    }

    public CategoryDO update(CategoryDO data) throws Exception {
        Category category;

        if (!data.isChanged())
            return data;

        manager.setFlushMode(FlushModeType.COMMIT);
        category = manager.find(Category.class, data.getId());

        category.setDescription(data.getDescription());
        category.setName(data.getName());
        category.setSectionId(data.getSection());
        category.setSystemName(data.getSystemName());

        return data;
    }
    
    public void validate(CategoryDO data) throws Exception{
        ValidationErrorsList list;
        Query query;
        Integer catId;
        List<Integer> ids;
        String sysName, name;

        list = new ValidationErrorsList();
        
        sysName = data.getSystemName();
        name = data.getName();
        
        if (DataBaseUtil.isEmpty(sysName)) {
            list.add(new FieldErrorException("fieldRequiredException",
                                                      meta.getSystemName()));
        } else {
            catId = getCategoryId(sysName);
            
            if (catId != null && !catId.equals(data.getId())) {
                list.add(new FieldErrorException("fieldUniqueException",
                                                          meta.getSystemName()));
            }
        }

        if (DataBaseUtil.isEmpty(name)) 
            list.add(new FieldErrorException("fieldRequiredException", meta.getName()));
        
        if(list.size() > 0)
            throw list;
    }

}
