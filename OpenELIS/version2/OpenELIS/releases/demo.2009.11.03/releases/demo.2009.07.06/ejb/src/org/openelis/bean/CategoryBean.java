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
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.EJBs;
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
import org.openelis.entity.Category;
import org.openelis.entity.Dictionary;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.TableFieldErrorException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.gwt.common.SecurityModule.ModuleFlags;
import org.openelis.gwt.common.data.AbstractField;
import org.openelis.gwt.common.data.QueryStringField;
import org.openelis.local.CategoryLocal;
import org.openelis.local.JMSMessageProducerLocal;
import org.openelis.local.LockLocal;
import org.openelis.messages.DictionaryCacheMessage;
import org.openelis.metamap.CategoryMetaMap;
import org.openelis.remote.CategoryRemote;
import org.openelis.util.QueryBuilder;
import org.openelis.utils.GetPage;
import org.openelis.utils.SecurityInterceptor;

@Stateless
@EJBs({
    @EJB(name="ejb/Lock",beanInterface=LockLocal.class),
    @EJB(name="ejb/JMSMessageProducer",beanInterface=JMSMessageProducerLocal.class)
})
@SecurityDomain("openelis")
@RolesAllowed("dictionary-select")
public class CategoryBean implements CategoryRemote,CategoryLocal {
    
    @PersistenceContext(name = "openelis")
    private EntityManager manager;

    @Resource
    private SessionContext ctx;
    
    private LockLocal lockBean;
    private JMSMessageProducerLocal jmsProducer;
    
    
    private static CategoryMetaMap CatMeta = new CategoryMetaMap();

    @PostConstruct
    private void init()
    {
        lockBean =  (LockLocal)ctx.lookup("ejb/Lock");
        jmsProducer = (JMSMessageProducerLocal)ctx.lookup("ejb/JMSMessageProducer");
        
    }
   
    public CategoryDO getCategory(Integer categoryId) {        
        
        Query query = manager.createNamedQuery("Category.Category");
        query.setParameter("id", categoryId);
        CategoryDO category = (CategoryDO)query.getSingleResult();// getting
                                                                    // category                                                                                                                                        

        return category;
    }

    public List getCategoryList() {
        Query query = manager.createNamedQuery("Category.IdName");
        List idNameDOList = query.getResultList();
        return idNameDOList;
    }

    public List query(ArrayList<AbstractField> fields, int first, int max) throws Exception {
        StringBuffer sb = new StringBuffer();
        QueryBuilder qb = new QueryBuilder();

        qb.setMeta(CatMeta);

        qb.setSelect("distinct new org.openelis.domain.IdNameDO(" + CatMeta.getId()
                     + ", "
                     + CatMeta.getName()
                     + ") ");

        qb.addWhere(fields);

        qb.setOrderBy(CatMeta.getName());

        sb.append(qb.getEJBQL());
        
        Query query = manager.createQuery(sb.toString());
       
        if(first > -1 && max > -1)
       	 query.setMaxResults(first+max);
        
        qb.setQueryParams(query);
        
        List returnList = GetPage.getPage(query.getResultList(), first, max);
        if(returnList == null)
       	 throw new LastPageException();
        else
       	 return returnList;        
            
    }

    @RolesAllowed("dictionary-update")
    public Integer updateCategory(CategoryDO categoryDO, List dictEntries) throws Exception {
        SecurityInterceptor.applySecurity(ctx.getCallerPrincipal().getName(), "dictionary", ModuleFlags.UPDATE);
        Query query = manager.createNamedQuery("getTableId");
        query.setParameter("name", "category");
        Integer categoryReferenceId = (Integer)query.getSingleResult();
        Integer categoryId = categoryDO.getId(); 
        if (categoryId != null) {
            // we need to call lock one more time to make sure their lock didnt
            // expire and someone else grabbed the record
            try {
                lockBean.validateLock(categoryReferenceId,categoryDO.getId());
            } catch(Exception ex) {
                throw ex;
            } 
            
        }

        validateCategory(categoryDO, dictEntries);
        
        manager.setFlushMode(FlushModeType.COMMIT);

        Category category = null;
        Dictionary dictionary = null;

        // update the category
        if (categoryDO.getId() == null) {
            category = new Category();
        } else {
            category = manager.find(Category.class, categoryId);
        }

        category.setDescription(categoryDO.getDescription());
        category.setSystemName(categoryDO.getSystemName());
        category.setName(categoryDO.getName());
        category.setSectionId(categoryDO.getSection());

        if (category.getId() == null) {
            manager.persist(category);
        }
        
        if(dictEntries!=null) {
            for (Iterator iter = dictEntries.iterator(); iter.hasNext();) {
                DictionaryDO dictDO = (DictionaryDO)iter.next();
                if (dictDO.getId() == null)
                    dictionary = new Dictionary();
                else
                    dictionary = manager.find(Dictionary.class, dictDO.getId());

                if (dictDO.getDelete() && dictDO.getId() != null) {
                    // delete the dictionary record
                    manager.remove(dictionary);

                } else if(!dictDO.getDelete()){
                    dictionary.setCategoryId(category.getId());
                    dictionary.setEntry(dictDO.getEntry());
                    dictionary.setIsActive(dictDO.getIsActive());
                    dictionary.setLocalAbbrev(dictDO.getLocalAbbrev());
                    dictionary.setRelatedEntryId(dictDO.getRelatedEntryId());
                    dictionary.setSystemName(dictDO.getSystemName());

                    if (dictionary.getId() == null) {
                        manager.persist(dictionary);
                    }
                }
            }
        }
        
        lockBean.giveUpLock(categoryReferenceId,category.getId()); 
        
        //invalidate the cache
        DictionaryCacheMessage msg = new DictionaryCacheMessage();
        msg.setCatDO(categoryDO);
        msg.action = DictionaryCacheMessage.Action.UPDATED;
        jmsProducer.writeMessage(msg);
        
        return  category.getId();
    }

    public List getDictionaryEntries(Integer categoryId) {
        Query query = manager.createNamedQuery("Dictionary.Dictionary");
        query.setParameter("id", categoryId);

        List dictionaryEntries = query.getResultList();// getting list of
                                                        // dictionary entries

        return dictionaryEntries;
    }
    
    public DictionaryDO getDictionaryDOBySystemName(String systemName) { 
        Query query = manager.createNamedQuery("Dictionary.DictionaryByEntrySystemName");
        query.setParameter("name", systemName);
        
        List resultList = query.getResultList();
        
        if(resultList.size() > 0)
            return (DictionaryDO)resultList.get(0);
        else 
            return null;
    }
    
    public DictionaryDO getDictionaryDOByEntryId(Integer entryId) {
        Query query = manager.createNamedQuery("Dictionary.DictionaryByEntryId");
        query.setParameter("name", entryId);
        
        List resultList = query.getResultList();
        
        if(resultList.size() > 0)
            return (DictionaryDO)resultList.get(0);
        else 
            return null;
    }
    
    public List getListByCategoryName(String categoryName) {
        Query query = manager.createNamedQuery("Dictionary.EntriesByCategoryName");
        query.setParameter("name", categoryName);
        
        return query.getResultList();
    }

    public List getDropdownValues(Integer categoryId) {
        Query query = manager.createNamedQuery("Dictionary.DropdownValues");
        query.setParameter("id", categoryId);

        return query.getResultList();
    }
    
    public List getDropdownAbbreviations(Integer categoryId) {
        Query query = manager.createNamedQuery("Dictionary.DropdownAbbreviations");
        query.setParameter("id", categoryId);

        return query.getResultList();
    }
    
    public List getIdEntrySystemNames(Integer categoryId) {
        Query query = manager.createNamedQuery("Dictionary.IdEntrySystemName");
        query.setParameter("id", categoryId);
        return query.getResultList();
    }

    public List getMatchingEntries(String entry, int maxResults) {
        Query query = manager.createNamedQuery("Dictionary.autoCompleteByEntry");
        query.setParameter("entry", entry);
        query.setMaxResults(maxResults);

        List entryList = null;
        try {
            entryList = (List)query.getResultList();
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
        
        if(results.size() == 0)
            return null;
        
        return (Integer)results.get(0);
    }

    public Integer getEntryIdForEntry(String entry) {
        Query query = manager.createNamedQuery("Dictionary.IdByEntry");
        query.setParameter("entry", entry);
        List results;
        
        results = query.getResultList();
        
        if(results.size() == 0)
            return null;
        
        return (Integer)results.get(0);
    }

    public String getSystemNameForEntryId(Integer entryId) {
        Query query = manager.createNamedQuery("Dictionary.SystemNameById");
        query.setParameter("id", entryId);
        List results = null;

        results = query.getResultList();
        
        if(results.size() == 0)
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

    // @RolesAllowed("dictionary-update")
    public CategoryDO getCategoryAndLock(Integer categoryId, String session) throws Exception {
        SecurityInterceptor.applySecurity(ctx.getCallerPrincipal().getName(), "dictionary", ModuleFlags.UPDATE);
        Query query = manager.createNamedQuery("getTableId");
        query.setParameter("name", "category");

        lockBean.getLock((Integer)query.getSingleResult(), categoryId);

        return getCategory(categoryId);
    }

    public CategoryDO getCategoryAndUnlock(Integer categoryId, String session) {

        Query unlockQuery = manager.createNamedQuery("getTableId");
        unlockQuery.setParameter("name", "category");

        lockBean.giveUpLock((Integer)unlockQuery.getSingleResult(), categoryId);

        return getCategory(categoryId);
    }

    private void validateCategory(CategoryDO categoryDO,
                                  ValidationErrorsList exceptionList) {

        if (!("").equals(categoryDO.getSystemName())) {
            Query catIdQuery = manager.createNamedQuery("Category.IdBySystemName");
            catIdQuery.setParameter("systemName", categoryDO.getSystemName());
            Integer catId = null;
            List<Integer> list = catIdQuery.getResultList();
            try {
                if(list.size()>0)
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

        if (("").equals(categoryDO.getName())) {
            exceptionList.add(new FieldErrorException("fieldRequiredException",
                                                      CatMeta.getName()));
        }

    }
    
    private void validateDictionary(List<DictionaryDO> dictList,
                                    ValidationErrorsList exceptionList,
                                    Integer categoryId) { 
      ArrayList<String> systemNames = new ArrayList<String>();
      ArrayList<String> entries = new ArrayList<String>();  
      DictionaryDO dictDO = null; 
      for(int iter = 0; iter < dictList.size(); iter++) {             
        dictDO = dictList.get(iter);  
        if(!dictDO.getDelete()) {
        if (dictDO.getEntry()!=null && !("").equals(dictDO.getEntry())) {
            if (!entries.contains(dictDO.getEntry())) {
                entries.add(dictDO.getEntry());
            } else {

                exceptionList.add(new TableFieldErrorException("fieldUniqueOnlyException",
                                                               iter,
                                                               CatMeta.getDictionary()
                                                                      .getEntry()));
            }
        } else {

            exceptionList.add(new TableFieldErrorException("fieldRequiredException",
                                                           iter,
                                                           CatMeta.getDictionary()
                                                                  .getEntry()));
        }

        if (dictDO.getSystemName() !=null && !("").equals(dictDO.getSystemName())) {
            if (!systemNames.contains(dictDO.getSystemName())) {
                Query catIdQuery = manager.createNamedQuery("Dictionary.CategoryIdBySystemName");
                catIdQuery.setParameter("systemName", dictDO.getSystemName());
                Integer catId = null;
                List<Integer> list = catIdQuery.getResultList();
                try {
                    if(list.size() > 0)
                      catId = list.get(0);
                } catch (NoResultException ex) {
                    ex.printStackTrace();
                } 

                if (catId != null) {
                    if (!catId.equals(categoryId)) {
                        exceptionList.add(new TableFieldErrorException("fieldUniqueException",
                                                                       iter,
                                                                       CatMeta.getDictionary()
                                                                              .getSystemName()));
                    }
                }
                systemNames.add(dictDO.getSystemName());
            } else {
                if (dictDO.getId() == null) {
                    exceptionList.add(new TableFieldErrorException("fieldUniqueOnlyException",
                                                                   iter,
                                                                   CatMeta.getDictionary()
                                                                          .getSystemName()));
                } else {
                    exceptionList.add(new TableFieldErrorException("fieldUniqueException",
                                                                   iter,
                                                                   CatMeta.getDictionary()
                                                                          .getSystemName()));
                }
            }

        }
      } 
     }   
    }
    
    private void validateCategory(CategoryDO categoryDO,
                               List<DictionaryDO> dictDOList) throws Exception{
        ValidationErrorsList exceptionList = new ValidationErrorsList();
        validateCategory(categoryDO, exceptionList);
        validateDictionary(dictDOList, exceptionList, categoryDO.getId());
     
        if(exceptionList.size() > 0)
            throw exceptionList;
    }


    public List getDictionaryListByPatternAndCategory(QueryStringField pattern,
                                                      Integer categoryId) {
        Query query = null;
        if (categoryId != null) {
            query = manager.createNamedQuery("Dictionary.DictionaryListByPatternAndCategory");
            query.setParameter("categoryId", categoryId);
            query.setParameter("pattern", pattern.getParameter().get(0));
        } else {
            query = manager.createNamedQuery("Dictionary.autoCompleteByEntry");
            query.setParameter("entry", pattern.getParameter().get(0));
        }
        List idNameDOList = query.getResultList();
        return   idNameDOList;
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
    
    public String getEntryById(Integer id) {
        String entry = null;
        Query query = manager.createNamedQuery("Dictionary.EntryById");
        query.setParameter("id", id);          
        entry = (String)query.getSingleResult();
        return entry;
    }


}
