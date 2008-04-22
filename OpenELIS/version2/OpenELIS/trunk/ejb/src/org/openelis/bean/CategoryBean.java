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
import org.openelis.meta.AnalyteMeta;
import org.openelis.meta.CategoryMeta;
import org.openelis.meta.DictionaryMeta;
import org.openelis.meta.DictionaryRelatedEntryMeta;
import org.openelis.meta.ProviderMeta;
import org.openelis.remote.CategoryRemote;
import org.openelis.util.Meta;
import org.openelis.util.QueryBuilder;
import org.openelis.utils.GetPage;

import edu.uiowa.uhl.security.domain.SystemUserDO;
import edu.uiowa.uhl.security.local.SystemUserUtilLocal;

@Stateless
public class CategoryBean implements CategoryRemote {
    
    @PersistenceContext(name = "openelis")
    private EntityManager manager;

    
    @EJB
    private SystemUserUtilLocal sysUser;
    
    @Resource
    private SessionContext ctx;
    
    private LockLocal lockBean;
    
    {
        try {
            InitialContext cont = new InitialContext();
            lockBean =  (LockLocal)cont.lookup("openelis/LockBean/local");
        }catch(Exception e){
            e.printStackTrace();
        }
    }
   
    public CategoryDO getCategory(Integer categoryId) {        
        
        Query query = manager.createNamedQuery("getCategory");
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
        QueryBuilder qb = new QueryBuilder();        
        
        //the meta objects which contain the names of the entities and aliases for various fields in the query(in EJBQL)
        //that will be eventually generated by the query builder class 
        CategoryMeta categoryMeta = CategoryMeta.getInstance();
        DictionaryMeta dictionaryMeta = DictionaryMeta.getInstance();
        DictionaryRelatedEntryMeta dicRelatedEntryMeta = DictionaryRelatedEntryMeta.getInstance();
        
        //this is done so that the query builder will be aware of the names of the entities and the various aliases
        qb.addMeta(new Meta[]{categoryMeta, dictionaryMeta, dicRelatedEntryMeta});
        
        //setting the select clause
        qb.setSelect("distinct "+CategoryMeta.ID+", "+CategoryMeta.NAME);
        
        // adding the alias for the category entity
        qb.addTable(categoryMeta);
        
        //this method is going to throw an exception if a column doesnt match
        qb.addWhere(fields);      

        // setting the order by clause
        qb.setOrderBy(CategoryMeta.NAME);
        
        // this is done to make sure that if the generated query contains alias for related entity join attribute(as specified in 
        // the entity class) then the alias for the entry for which this related entry is specified is present, otherwise the query will produce 
        // an exception on the lines of "xyz field/alias not found"
        if(qb.hasTable(dicRelatedEntryMeta.getTable()))
        	qb.addTable(dictionaryMeta);
        
        // this is done such that a string representation of the generated query can be obtained   
        sb.append(qb.getEJBQL());
        
        // create a query object from the string representation of the generated query
        Query query = manager.createQuery(sb.toString());
       
        // done to make sure that only the results for the current page of data are returned
        if(first > -1 && max > -1)
       	 query.setMaxResults(first+max);
        
//      ***set the parameters in the query
        qb.setQueryParams(query);
        
        //getting the results for the current page of data
        List returnList = GetPage.getPage(query.getResultList(), first, max);
        if(returnList == null)
       	 throw new LastPageException();
        else
       	 return returnList;        
            
    }

    public Integer updateCategory(CategoryDO categoryDO, List dictEntries)throws Exception {
        Category category  = null;
       try{ 
        manager.setFlushMode(FlushModeType.COMMIT);
        Query query = manager.createNamedQuery("getTableId");
        query.setParameter("name", "category");
        Integer categoryReferenceId = (Integer)query.getSingleResult();                
        
        //validate the category before adding or updating it
        List<Exception> exceptionList = new ArrayList<Exception>();
        validateCategory(categoryDO,exceptionList);  
        if(exceptionList.size() > 0){
            throw (RPCException)exceptionList.get(0);
        }                             
                                                 
        ArrayList<DictionaryDO> updateList = null;
        ArrayList<DictionaryDO> deleteList = null;
        int index =0;
         ArrayList<String> systemNames = new ArrayList<String>();
        
        ArrayList<String> entries = new ArrayList<String>();
        for (Iterator iter = dictEntries.iterator(); iter.hasNext();) {
            DictionaryDO dictDO = (DictionaryDO)iter.next();
             
            // validate every DO before adding it to the update or delete lists
            exceptionList = new ArrayList<Exception>();
             validateDictionary(dictDO, categoryDO.getId(),index,systemNames,entries,exceptionList);
             if(exceptionList.size() > 0){
                 throw (RPCException)exceptionList.get(0);
             }
                         
             boolean update = false;
             if(dictDO.getDelete()!=null){ 
               if(dictDO.getDelete()){
                   if(deleteList==null){
                       deleteList = new ArrayList<DictionaryDO>();
                       deleteList.add(dictDO);
                   }
               }else{
                   update = true;
               }
              }else{
                   update = true;
             }
             
                         
             if(update){
                 if(updateList ==null){
                     updateList = new ArrayList<DictionaryDO>();
                 }
                 updateList.add(dictDO);                                 
             }
            index++; 
        }
        
        if (categoryDO.getId() == null){
            category = new Category();
        } 
        else{
            category = manager.find(Category.class, categoryDO.getId());
        }
        
        // add update category as specified by the DO's values 
        category.setDescription(categoryDO.getDescription());                       
        category.setSystemName(categoryDO.getSystemName());
        category.setName(categoryDO.getName());
        category.setSection(categoryDO.getSection());
        
        if(category.getId()==null){
            manager.persist(category);
        }
        
        // add or update  dictionary entities as specified in the DO
       if(updateList!=null){ 
        for(int iter = 0; iter < updateList.size();iter++){
            Dictionary dictionary = null;
            DictionaryDO dictDO = updateList.get(iter); 
            if(dictDO.getId() == null){
                dictionary = new Dictionary();
            }else{
                 dictionary  = manager.find(Dictionary.class,dictDO.getId());
           }
            dictionary.setCategory(category.getId());
            dictionary.setEntry(dictDO.getEntry());
            dictionary.setIsActive(dictDO.getIsActive());
            dictionary.setLocalAbbrev(dictDO.getLocalAbbrev());
            dictionary.setRelatedEntryId(dictDO.getRelatedEntryId());                 
            dictionary.setSystemName(dictDO.getSystemName());    
                                     
         if(dictionary.getId()==null){                  
           manager.persist(dictionary);
         }
        }
       }  
       
      // delete dictionary entities as specified by the DO's values
      if(deleteList!=null){  
       for(int iter = 0; iter < deleteList.size(); iter++){
           Dictionary dictionary = null;
           DictionaryDO dictDO = deleteList.get(iter); 
           
           if (dictionary.getId() != null){
               dictionary  = manager.find(Dictionary.class,dictDO.getId());
               //delete the dictionary entry from the database                    
                  manager.remove(dictionary);     
          }    
       } 
      }  
        lockBean.giveUpLock(categoryReferenceId,category.getId()); 
        
       }catch(Exception ex){ 
           ex.printStackTrace();
           throw ex;        
       }
        return  category.getId();
    }

    public List getDictionaryEntries(Integer categoryId) {
        Query query = manager.createNamedQuery("getDictionaryEntries");
        query.setParameter("id", categoryId);
        
        List providerAddresses = query.getResultList();// getting list of dictionary entries  
    
        return providerAddresses;
    }
    
    public List getDropdownValues(Integer categoryId) {
    	Query query = manager.createNamedQuery("getDictionaryDropdownValues");
    	query.setParameter("id", categoryId);
    	
    	return query.getResultList();
    }
    
    public List getMatchingEntries(String entry, int maxResults){
       Query query = manager.createNamedQuery("getMatchingEntries");  
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
        Query query = manager.createNamedQuery("getEntryIdForSystemName");  
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
        Query query = manager.createNamedQuery("getEntryIdForEntry");  
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
        Query query = manager.createNamedQuery("getCategoryIdBySystemName");  
        query.setParameter("systemName", systemName);
        Integer categoryId = null;
        try{ 
            categoryId = (Integer)query.getSingleResult();        
        } catch(Exception ex){
            ex.printStackTrace();
            
        }   
        return categoryId;
    }
    
    public Object[] autoCompleteLookupById(Integer id)throws Exception{
      try{  
        Query query  = manager.createNamedQuery("getEntryAutoCompleteById");
        query.setParameter("id",id);
        return (Object[])query.getSingleResult();
      }catch(NoResultException ex){
          return null;
      } catch(Exception ex){
          throw ex;
      }
      
    }

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
                Query catIdQuery  = manager.createNamedQuery("getCategoryIdForCatSysName");
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
                    exceptionList.add(new FieldErrorException("fieldUniqueException",CategoryMeta.SYSTEM_NAME));
                }  
              }                                 

       }else {
           exceptionList.add(new FieldErrorException("fieldRequiredException",CategoryMeta.SYSTEM_NAME)); 
       } 
            
         if(("").equals(categoryDO.getName())){                            
                exceptionList.add(new FieldErrorException("fieldRequiredException",CategoryMeta.NAME)); 
            }
            
    }
    
    private void validateDictionary(DictionaryDO dictDO, Integer categoryId, int index,List<String>systemNames,List<String>entries,List<Exception> exceptionList){             
              
            if(!("").equals(dictDO.getEntry())){   
             if(!entries.contains(dictDO.getEntry())){
               entries.add(dictDO.getEntry());
              }else{                                  
                  exceptionList.add(new TableFieldErrorException("fieldUniqueException", index,DictionaryMeta.ENTRY));
              } 
            }else{              
                exceptionList.add(new TableFieldErrorException("fieldRequiredException", index,DictionaryMeta.ENTRY));
            }              
           
            
             if(!("").equals(dictDO.getSystemName())){  
                 
              if(!systemNames.contains(dictDO.getSystemName())){
                Query catIdQuery  = manager.createNamedQuery("getCategoryIdForDictSysName");
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
                         exceptionList.add(new TableFieldErrorException("fieldUniqueException", index,DictionaryMeta.SYSTEM_NAME));
                     }
                 }
                systemNames.add(dictDO.getSystemName());                            
            }else{               
                exceptionList.add(new TableFieldErrorException("fieldUniqueException", index,DictionaryMeta.SYSTEM_NAME));
            }
            
       }                 
    }
    
    public List validateForAdd(CategoryDO categoryDO, List<DictionaryDO> dictDOList){
        List<Exception> exceptionList = new ArrayList<Exception>();
        validateCategory(categoryDO,exceptionList);
        int index = 0;
        ArrayList<String> systemNames = new ArrayList<String>();
        
        ArrayList<String> entries = new ArrayList<String>();
        for (Iterator iter = dictDOList.iterator(); iter.hasNext();) {
            DictionaryDO dictDO = (DictionaryDO)iter.next();
            exceptionList = new ArrayList<Exception>();
            validateDictionary(dictDO,categoryDO.getId(),index,systemNames,entries,exceptionList);
            index++;
        }
        
        return exceptionList;
    }
    
    public List validateForUpdate(CategoryDO categoryDO, List<DictionaryDO> dictDOList){
        List<Exception> exceptionList = new ArrayList<Exception>();
        validateCategory(categoryDO,exceptionList);
        int index = 0;
        ArrayList<String> systemNames = new ArrayList<String>();
        
        ArrayList<String> entries = new ArrayList<String>();
        for (Iterator iter = dictDOList.iterator(); iter.hasNext();) {
            DictionaryDO dictDO = (DictionaryDO)iter.next();
            exceptionList = new ArrayList<Exception>();
            validateDictionary(dictDO,categoryDO.getId(),index,systemNames,entries,exceptionList);
            index++;
        }      
        return exceptionList;
    }
    
}
