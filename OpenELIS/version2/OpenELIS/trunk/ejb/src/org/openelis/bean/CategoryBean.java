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

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
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
import org.openelis.entity.Dictionary;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.TableFieldErrorException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.gwt.common.SecurityModule.ModuleFlags;
import org.openelis.gwt.common.data.QueryData;
import org.openelis.local.CategoryLocal;
import org.openelis.local.JMSMessageProducerLocal;
import org.openelis.local.LockLocal;
import org.openelis.messages.DictionaryCacheMessage;
import org.openelis.metamap.CategoryMetaMap;
import org.openelis.metamap.DictionaryMetaMap;
import org.openelis.remote.CategoryRemote;
import org.openelis.util.QueryBuilder;
import org.openelis.util.QueryBuilderV2;
import org.openelis.utilcommon.DataBaseUtil;
import org.openelis.utils.ReferenceTableCache;
import org.openelis.utils.SecurityInterceptor;

@Stateless
@SecurityDomain("openelis")
@RolesAllowed("dictionary-select")
public class CategoryBean implements CategoryRemote, CategoryLocal {

    @PersistenceContext(name = "openelis")
    private EntityManager           manager;

    @Resource
    private SessionContext          ctx;

    @EJB
    private LockLocal               lockBean;    

    private static CategoryMetaMap  CatMeta = new CategoryMetaMap();

    private static Integer          catRefTableId;

    public CategoryBean() {
        catRefTableId = ReferenceTableCache.getReferenceTable("category");
    }

    public CategoryDO fetchByCategoryId(Integer categoryId) throws Exception {

        Query query = manager.createNamedQuery("Category.FetchById");
        query.setParameter("id", categoryId);
        CategoryDO category = (CategoryDO)query.getSingleResult();// getting
        // category

        return category;
    }

    public ArrayList<IdNameVO> query(ArrayList<QueryData> fields, int first, int max)
                                                                                     throws Exception {
        Query query;
        QueryBuilderV2 builder;
        List list;

        builder = new QueryBuilderV2();
        builder.setMeta(CatMeta);
        builder.setSelect("distinct new org.openelis.domain.IdNameVO(" + CatMeta.getId() + ", " +
                          CatMeta.getName() + ") ");

        builder.constructWhere(fields);
        builder.setOrderBy(CatMeta.getName());

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
    
    public void add(CategoryDO catDO) throws Exception {
        Category category;

        checkSecurity(ModuleFlags.ADD);

        manager.setFlushMode(FlushModeType.COMMIT);

        category = new Category();

        category.setDescription(catDO.getDescription());
        category.setName(catDO.getName());
        category.setSectionId(catDO.getSection());
        category.setSystemName(catDO.getSystemName());

        manager.persist(category);

        catDO.setId(category.getId());
    }
    
    public void update(CategoryDO catDO) throws Exception {
        Category category;
        
        if (!catDO.isChanged()) {
            lockBean.giveUpLock(catRefTableId, catDO.getId());
            return;
        }

        checkSecurity(ModuleFlags.UPDATE);

        lockBean.validateLock(catRefTableId, catDO.getId());

        manager.setFlushMode(FlushModeType.COMMIT);
        category = manager.find(Category.class, catDO.getId());

        category.setDescription(catDO.getDescription());
        category.setName(catDO.getName());
        category.setSectionId(catDO.getSection());
        category.setSystemName(catDO.getSystemName());

        lockBean.giveUpLock(catRefTableId, catDO.getId());                
    }
    
    public void validate(CategoryDO categoryDO, ArrayList<DictionaryViewDO> entries)throws Exception {
        ValidationErrorsList exceptionList;
        
        exceptionList = new ValidationErrorsList();
        validateCategory(categoryDO, exceptionList);
        validateDictionary(entries, exceptionList, categoryDO.getId());

        if (exceptionList.size() > 0)
            throw exceptionList;
    }
    
    public void addDictionary(DictionaryViewDO dictDO) throws Exception {
        Dictionary dictionary;

        manager.setFlushMode(FlushModeType.COMMIT);

        dictionary = new Dictionary();

        dictionary.setCategoryId(dictDO.getCategoryId());
        dictionary.setEntry(dictDO.getEntry());
        dictionary.setIsActive(dictDO.getIsActive());
        dictionary.setLocalAbbrev(dictDO.getLocalAbbrev());
        dictionary.setRelatedEntryId(dictDO.getRelatedEntryId());
        dictionary.setSystemName(dictDO.getSystemName());
        dictionary.setSortOrder(dictDO.getSortOrder());

        manager.persist(dictionary);

        dictDO.setId(dictionary.getId());

    }

    public void updateDictionary(DictionaryViewDO dictDO) throws Exception {
        Dictionary dictionary;

        if (!dictDO.isChanged())
            return;

        dictionary = manager.find(Dictionary.class, dictDO.getId());

        dictionary.setCategoryId(dictDO.getCategoryId());
        dictionary.setEntry(dictDO.getEntry());
        dictionary.setIsActive(dictDO.getIsActive());
        dictionary.setLocalAbbrev(dictDO.getLocalAbbrev());
        dictionary.setRelatedEntryId(dictDO.getRelatedEntryId());
        dictionary.setSystemName(dictDO.getSystemName());
        dictionary.setSortOrder(dictDO.getSortOrder());
    }
    
    public void deleteDictionary(DictionaryViewDO deletedAt) throws Exception {
        Dictionary dictionary;

        manager.setFlushMode(FlushModeType.COMMIT);

        dictionary = manager.find(Dictionary.class, deletedAt.getId());

        if (dictionary != null)
            manager.remove(dictionary);
    }
    
    public List getCategoryList() {
        Query query = manager.createNamedQuery("Category.IdName");
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

    public List getDropdownValues(Integer categoryId) {
        Query query = manager.createNamedQuery("Dictionary.DropdownValues");
        query.setParameter("id", categoryId);

        return query.getResultList();
    }

    public ArrayList<IdNameVO> autoCompleteByEntry(String entry, int maxResults) {
        Query query = manager.createNamedQuery("Dictionary.AutoCompleteByEntry");
        query.setParameter("entry", entry);
        query.setMaxResults(maxResults);

        ArrayList<IdNameVO> entryList = null;
        try {
            entryList = (ArrayList<IdNameVO>)query.getResultList();
        } catch (Exception ex) {
            ex.printStackTrace();

        }
        return entryList;
    }

    public Integer getEntryIdForSystemName(String systemName) {
        Query query = manager.createNamedQuery("Dictionary.IdBySystemName");
        query.setParameter("systemName", systemName);
        List results;
        results = query.getResultList();

        if (results.size() == 0)
            return null;

        return (Integer)results.get(0);
    }

    public Integer getEntryIdForEntry(String entry) {
        Query query = manager.createNamedQuery("Dictionary.IdByEntry");
        query.setParameter("entry", entry);
        List results;

        results = query.getResultList();

        if (results.size() == 0)
            return null;

        return (Integer)results.get(0);
    }

    public String getSystemNameForEntryId(Integer entryId) {
        Query query = manager.createNamedQuery("Dictionary.SystemNameById");
        query.setParameter("id", entryId);
        List results = null;

        results = query.getResultList();

        if (results.size() == 0)
            return null;

        return (String)results.get(0);
    }

    public Integer getCategoryId(String systemName) {
        Query query = manager.createNamedQuery("Category.IdBySystemName");
        query.setParameter("systemName", systemName);
        Integer categoryId = null;
        try {
            categoryId = (Integer)query.getSingleResult();
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
        Query query = manager.createNamedQuery("TestResult.ResultCountByValue");
        query.setParameter("value", id.toString());
        count = query.getResultList().size();        
        
        if(count > 0) {
           query = manager.createNamedQuery("Dictionary.EntryById");
           query.setParameter("id", id);          
           oldVal = (String)query.getSingleResult();
           if(oldVal.trim().equals(entry.trim())) 
            count = 0;           
        }
        return count;        
    }
    
    public ArrayList<Integer> getDictionaryIdListByEntry(String entry) {
        Query query;
        
        query = manager.createNamedQuery("Dictionary.IdByEntry");
        query.setParameter("entry", entry);
        
        return DataBaseUtil.toArrayList(query.getResultList());
    }   

    private void checkSecurity(ModuleFlags flag) throws Exception {
        SecurityInterceptor.applySecurity(ctx.getCallerPrincipal().getName(), "dictionary", flag);
    }

    private void validateCategory(CategoryDO categoryDO, ValidationErrorsList exceptionList) {

        if ( ! ("").equals(categoryDO.getSystemName())) {
            Query catIdQuery = manager.createNamedQuery("Category.IdBySystemName");
            catIdQuery.setParameter("systemName", categoryDO.getSystemName());
            Integer catId = null;
            List<Integer> list = catIdQuery.getResultList();
            try {
                if (list.size() > 0)
                    catId = list.get(0);
            } catch (NoResultException ex) {
                ex.printStackTrace();
            }

            if (catId != null && !catId.equals(categoryDO.getId())) {
                exceptionList.add(new FieldErrorException("fieldUniqueException",
                                                          CatMeta.getSystemName()));
            }
        } else {
            exceptionList.add(new FieldErrorException("fieldRequiredException",
                                                      CatMeta.getSystemName()));
        }

        if ( ("").equals(categoryDO.getName())) {
            exceptionList.add(new FieldErrorException("fieldRequiredException", CatMeta.getName()));
        }

    }

    private void validateDictionary(List<DictionaryViewDO> dictList,
                                    ValidationErrorsList exceptionList,
                                    Integer categoryId) {
        ArrayList<String> systemNames = new ArrayList<String>();
        ArrayList<String> entries = new ArrayList<String>();
        DictionaryViewDO dictDO = null;
        for (int iter = 0; iter < dictList.size(); iter++ ) {
            dictDO = dictList.get(iter);            
            if (dictDO.getEntry() != null && ! ("").equals(dictDO.getEntry())) {
                if ( !entries.contains(dictDO.getEntry())) {
                    entries.add(dictDO.getEntry());
                } else {

                    exceptionList.add(new TableFieldErrorException("fieldUniqueOnlyException",
                                                                   iter,
                                                                   CatMeta.getDictionary()
                                                                                .getEntry(),
                                                                   "dictEntTable"));
                }
            } else {

                exceptionList.add(new TableFieldErrorException("fieldRequiredException",
                                                               iter,
                                                               CatMeta.getDictionary()
                                                                           .getEntry(),
                                                               "dictEntTable"));
            }

            if (dictDO.getSystemName() != null && ! ("").equals(dictDO.getSystemName())) {
                if ( !systemNames.contains(dictDO.getSystemName())) {
                    Query catIdQuery = manager.createNamedQuery("Dictionary.CategoryIdBySystemName");
                    catIdQuery.setParameter("systemName", dictDO.getSystemName());
                    Integer catId = null;
                    List<Integer> list = catIdQuery.getResultList();
                    try {
                        if (list.size() > 0)
                            catId = list.get(0);
                    } catch (NoResultException ex) {
                        ex.printStackTrace();
                    }

                    if (catId != null) {
                        if ( !catId.equals(categoryId)) {
                            exceptionList.add(new TableFieldErrorException("fieldUniqueException",
                                                                           iter,
                                                                           CatMeta.getDictionary()
                                                                                  .getSystemName(),
                                                                           "dictEntTable"));
                        }
                    }
                    systemNames.add(dictDO.getSystemName());
                } else {
                    if (dictDO.getId() == null) {
                        exceptionList.add(new TableFieldErrorException("fieldUniqueOnlyException",
                                                                       iter,
                                                                       CatMeta.getDictionary()
                                                                              .getSystemName(),
                                                                       "dictEntTable"));
                    } else {
                        exceptionList.add(new TableFieldErrorException("fieldUniqueException",
                                                                       iter,
                                                                       CatMeta.getDictionary()
                                                                              .getSystemName(),
                                                                       "dictEntTable"));
                    }
                }

            }
        }
    } 

}
