/**
* The contents of this file are subject to the Mozilla Public License
* Version 1.1 (the "License"); you may not use this file except in
* compliance with the License. You may obtain a copy of the License at
* http://www.mozilla.org/MPL/
* 
* Software distributed under the License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
* License for the specific language governing rights and limitations under
* the License.
* 
* The Original Code is OpenELIS code.
* 
* Copyright (C) OpenELIS.  All Rights Reserved.
*/
package org.openelis.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.naming.InitialContext;
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
import org.openelis.gwt.common.RPCException;
import org.openelis.gwt.common.TableFieldErrorException;
import org.openelis.local.LockLocal;
import org.openelis.metamap.CategoryMetaMap;
import org.openelis.remote.CategoryRemote;
import org.openelis.security.domain.SystemUserDO;
import org.openelis.security.local.SystemUserUtilLocal;
import org.openelis.util.NewQueryBuilder;
import org.openelis.utils.GetPage;

@Stateless
@SecurityDomain("openelis")
@RolesAllowed("dictionary-select")
public class CategoryBean implements CategoryRemote {
    
    @PersistenceContext(name = "openelis")
    private EntityManager manager;

    
    @EJB
    private SystemUserUtilLocal sysUser;
    
    @Resource
    private SessionContext ctx;
    
    private LockLocal lockBean;
    
    
    private static CategoryMetaMap CatMeta = new CategoryMetaMap();
    
    {
        try {
            InitialContext cont = new InitialContext();
            lockBean =  (LockLocal)cont.lookup("openelis/LockBean/local");
        }catch(Exception e){
            e.printStackTrace();
        }
    }
   
    public CategoryDO getCategory(Integer categoryId) {        
        
        Query query = manager.createNamedQuery("Category.Category");
        query.setParameter("id", categoryId);
        CategoryDO category = (CategoryDO) query.getSingleResult();// getting category with address and contacts

        return category;
    }

 

    public Integer getSystemUserId() {
        try {
            SystemUserDO systemUserDO = sysUser.getSystemUser(ctx.getCallerPrincipal()
                                                                 .getName());
            return systemUserDO.getId();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
        }
        
    }

    public List query(HashMap fields, int first, int max) throws Exception {
        StringBuffer sb = new StringBuffer();
        NewQueryBuilder qb = new NewQueryBuilder();        
        
        //CategoryMeta categoryMeta = CategoryMeta.getInstance();
        //DictionaryMeta dictionaryMeta = DictionaryMeta.getInstance();
        //DictionaryRelatedEntryMeta dicRelatedEntryMeta = DictionaryRelatedEntryMeta.getInstance();
        
        //qb.addMeta(new Meta[]{categoryMeta, dictionaryMeta, dicRelatedEntryMeta});
        qb.setMeta(CatMeta);
        
        qb.setSelect("distinct new org.openelis.domain.IdNameDO("+CatMeta.getId()+", "+CatMeta.getName() + ") ");
        
        //qb.addTable(categoryMeta);
        
        qb.addWhere(fields);      

        qb.setOrderBy(CatMeta.getName());
        
        //if(qb.hasTable(dicRelatedEntryMeta.getTable()))
        //	qb.addTable(dictionaryMeta);
        
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
    public Integer updateCategory(CategoryDO categoryDO, List dictEntries)throws Exception {
        Query query = manager.createNamedQuery("getTableId");
        query.setParameter("name", "category");
        Integer categoryReferenceId = (Integer)query.getSingleResult();          
        
        if(categoryDO.getId() != null){
//          we need to call lock one more time to make sure their lock didnt expire and someone else grabbed the record
            lockBean.getLock(categoryReferenceId, categoryDO.getId());
        }
        
        manager.setFlushMode(FlushModeType.COMMIT);      
        
        Category category  = null;
        Dictionary dictionary = null;
        
        List<Exception> exceptionList = new ArrayList<Exception>();
        validateCategory(categoryDO,exceptionList);  
        if(exceptionList.size() > 0){
            throw (RPCException)exceptionList.get(0);
        }                             
        
        //update the category
        if (categoryDO.getId() == null){
            category = new Category();
        } 
        else{
            category = manager.find(Category.class, categoryDO.getId());
        }
        
        category.setDescription(categoryDO.getDescription());                       
        category.setSystemName(categoryDO.getSystemName());
        category.setName(categoryDO.getName());
        category.setSectionId(categoryDO.getSection());
        
        if(category.getId()==null){
            manager.persist(category);
        }
        
        int index =0;
         ArrayList<String> systemNames = new ArrayList<String>();
        
        ArrayList<String> entries = new ArrayList<String>();
        for (Iterator iter = dictEntries.iterator(); iter.hasNext();) {
            DictionaryDO dictDO = (DictionaryDO)iter.next();
            
            exceptionList = new ArrayList<Exception>();
                         
             validateDictionary(dictDO, categoryDO.getId(),index,systemNames,entries,exceptionList);
             if(exceptionList.size() > 0){
                 throw (RPCException)exceptionList.get(0);
             }                            
                          
             if (dictDO.getId() == null)
                 dictionary = new Dictionary();
             else
                 dictionary = manager.find(Dictionary.class, dictDO.getId());
             
             if(dictDO.getDelete() && dictDO.getId() != null){
                //delete the dictionary record
                manager.remove(dictionary);
                
             }else{                       
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
        
            index++; 
        }
        
        lockBean.giveUpLock(categoryReferenceId,category.getId()); 
        
        return  category.getId();
    }

    public List getDictionaryEntries(Integer categoryId) {
        Query query = manager.createNamedQuery("Dictionary.Dictionary");
        query.setParameter("id", categoryId);
        
        List dictionaryEntries = query.getResultList();// getting list of dictionary entries  
    
        return dictionaryEntries;
    }
    
    public List getDropdownValues(Integer categoryId) {
    	Query query = manager.createNamedQuery("Dictionary.DropdownValues");
    	query.setParameter("id", categoryId);
    	
    	return query.getResultList();
    }
    
    public List getMatchingEntries(String entry, int maxResults){
       Query query = manager.createNamedQuery("Dictionary.autoCompleteByEntry");  
       query.setParameter("entry", entry);       
       query.setMaxResults(maxResults);       
       
       List entryList = null;
       try{ 
           entryList = (List)query.getResultList();
       }catch(Exception ex){
           ex.printStackTrace();
          
       }     
       return entryList;
    }
    
    public Integer getEntryIdForSystemName(String systemName)throws Exception{ 
        Query query = manager.createNamedQuery("Dictionary.IdBySystemName");  
        query.setParameter("systemName", systemName);
        Integer entryId = null;
        try{     
          entryId = (Integer)query.getSingleResult();
        }catch(NoResultException ex){
            return null;
        } catch(Exception ex){
            ex.printStackTrace();
            throw ex;
        }    
        return entryId;
    } 
     
    public Integer getEntryIdForEntry(String entry)throws Exception{
        Query query = manager.createNamedQuery("Dictionary.IdByEntry");  
        query.setParameter("entry", entry);
        Integer entryId = null;
        try{ 
          entryId = (Integer)query.getSingleResult();
        }catch(NoResultException ex){
            return null;
        } catch(Exception ex){
            ex.printStackTrace();
            throw ex;
        }     
        return entryId;
    } 
        
    public Integer getCategoryId(String systemName){
        Query query = manager.createNamedQuery("Category.IdBySystemName");  
        query.setParameter("systemName", systemName);
        Integer categoryId = null;
        try{ 
            categoryId = (Integer)query.getSingleResult();        
        } catch(Exception ex){          
            ex.printStackTrace();
            
        }   
        return categoryId;
    }
    
    //@RolesAllowed("dictionary-update")
    public CategoryDO getCategoryAndLock(Integer categoryId)throws Exception {
        Query query = manager.createNamedQuery("getTableId");
        query.setParameter("name", "category");

        lockBean.getLock((Integer)query.getSingleResult(),categoryId);
        
        return getCategory(categoryId);
    }

    public CategoryDO getCategoryAndUnlock(Integer categoryId) {
        
        Query unlockQuery = manager.createNamedQuery("getTableId");
        unlockQuery.setParameter("name", "category");

        lockBean.giveUpLock((Integer)unlockQuery.getSingleResult(),categoryId);
        
        return getCategory(categoryId);
    } 
    
    
    private void validateCategory(CategoryDO categoryDO,List<Exception> exceptionList){

            if(!("").equals(categoryDO.getSystemName())){
                Query catIdQuery  = manager.createNamedQuery("Category.IdBySystemName");
                catIdQuery.setParameter("systemName", categoryDO.getSystemName());
                Integer catId = null;
                try{

                    catId = (Integer)catIdQuery.getSingleResult();
                }catch(NoResultException ex){                     
                    ex.printStackTrace();
                }catch(Exception ex){
                    exceptionList.add(ex);
                }
                
              if(catId!=null){  
                if(!catId.equals(categoryDO.getId())){
                    exceptionList.add(new FieldErrorException("fieldUniqueException",CatMeta.getSystemName()));
                }  
              }                                 

       }else {
 
           exceptionList.add(new FieldErrorException("fieldRequiredException",CatMeta.getSystemName())); 
       } 
            
         if(("").equals(categoryDO.getName())){      

                exceptionList.add(new FieldErrorException("fieldRequiredException",CatMeta.getName())); 
            }
            
    }
    
    
    private void validateDictionary(DictionaryDO dictDO, Integer categoryId, int index,List<String>systemNames,List<String>entries,List<Exception> exceptionList){             
             
            if(!("").equals(dictDO.getEntry())){   
             if(!entries.contains(dictDO.getEntry())){
               entries.add(dictDO.getEntry());
              }else{        

                  exceptionList.add(new TableFieldErrorException("fieldUniqueOnlyException", index,CatMeta.getDictionary().getEntry()));
              } 
            }else{              

                exceptionList.add(new TableFieldErrorException("fieldRequiredException", index,CatMeta.getDictionary().getEntry()));
            }              
           
            
             if(!("").equals(dictDO.getSystemName())){ 
               if(!systemNames.contains(dictDO.getSystemName())){     
                 Query catIdQuery  = manager.createNamedQuery("Dictionary.CategoryIdBySystemName");
                 catIdQuery.setParameter("systemName", dictDO.getSystemName());
                 Integer catId = null;
                 try{
                     catId = (Integer)catIdQuery.getSingleResult();
                 }catch(NoResultException ex){                     
                     ex.printStackTrace();
                 }catch(Exception ex){                    
                     exceptionList.add(ex);
                 }
                 
                  if(catId != null){
                      if(!catId.equals(categoryId)){                                                        
                          exceptionList.add(new TableFieldErrorException("fieldUniqueException", index,CatMeta.getDictionary().getSystemName()));
                      }
                  }                           
                systemNames.add(dictDO.getSystemName());                            
            }else{
                if(dictDO.getId() ==null){
                  exceptionList.add(new TableFieldErrorException("fieldUniqueOnlyException", index,CatMeta.getDictionary().getSystemName()));
                }else {
                    exceptionList.add(new TableFieldErrorException("fieldUniqueException", index,CatMeta.getDictionary().getSystemName()));
                }
            }
            
       }                 
    }
    
    public List validateForAdd(CategoryDO categoryDO, List<DictionaryDO> dictDOList){
        
        List<Exception> exceptionList = new ArrayList<Exception>();
        
        validateCategory(categoryDO,exceptionList);        
        ArrayList<String> systemNames = new ArrayList<String>();
        
        ArrayList<String> entries = new ArrayList<String>();

        for (int iter =0; iter< dictDOList.size(); iter++) {
            DictionaryDO dictDO = (DictionaryDO)dictDOList.get(iter);
            validateDictionary(dictDO,categoryDO.getId(),iter,systemNames,entries,exceptionList);            
        }
        
        return exceptionList;
    }
    
    public List validateForUpdate(CategoryDO categoryDO, List<DictionaryDO> dictDOList){
        List<Exception> exceptionList = new ArrayList<Exception>();
        
        validateCategory(categoryDO,exceptionList);        
        ArrayList<String> systemNames = new ArrayList<String>();
        
        ArrayList<String> entries = new ArrayList<String>();

        for (int iter =0; iter< dictDOList.size(); iter++) {
            DictionaryDO dictDO = (DictionaryDO)dictDOList.get(iter);
            validateDictionary(dictDO,categoryDO.getId(),iter,systemNames,entries,exceptionList);            
        }
        
        return exceptionList;
    }
}
