package org.openelis.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.naming.InitialContext;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.openelis.domain.CategoryDO;
import org.openelis.domain.DictionaryDO;
import org.openelis.entity.Category;
import org.openelis.entity.Dictionary;
import org.openelis.gwt.common.LastPageException;
import org.openelis.local.LockLocal;
import org.openelis.meta.CategoryMeta;
import org.openelis.meta.DictionaryMeta;
import org.openelis.meta.DictionaryRelatedEntryMeta;
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
        CategoryDO category = (CategoryDO) query.getSingleResult();// getting organization with address and contacts

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
        //sb.append("select distinct c.id, c.name from Category c left join c.dictionary d left join d.relatedEntryRow relEntry where 1=1 " );
        
        CategoryMeta categoryMeta = CategoryMeta.getInstance();
        DictionaryMeta dictionaryMeta = DictionaryMeta.getInstance();
        DictionaryRelatedEntryMeta dicRelatedEntryMeta = DictionaryRelatedEntryMeta.getInstance();
        
        qb.addMeta(new Meta[]{categoryMeta, dictionaryMeta, dicRelatedEntryMeta});
        
        qb.setSelect("distinct "+CategoryMeta.ID+", "+CategoryMeta.NAME);
        qb.addTable(categoryMeta);
        
        //this method is going to throw an exception if a column doesnt match
        qb.addWhere(fields);      

        qb.setOrderBy(CategoryMeta.NAME);
        
        if(qb.hasTable(dicRelatedEntryMeta.getTable()))
        	qb.addTable(dictionaryMeta);
        
        sb.append(qb.getEJBQL());
        
        Query query = manager.createQuery(sb.toString());
       
        if(first > -1 && max > -1)
       	 query.setMaxResults(first+max);
        
//      ***set the parameters in the query
        qb.setQueryParams(query);
        
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
        
        if (categoryDO.getId() == null){
            category = new Category();
        } 
        else{
            category = manager.find(Category.class, categoryDO.getId());
        }        
        
        category.setDescription(categoryDO.getDescription());
        
        if(categoryDO.getSystemName()!=null){
         if(!("").equals(categoryDO.getSystemName().trim())){
             Query catIdQuery  = manager.createNamedQuery("getCategoryIdForCatSysName");
             catIdQuery.setParameter("systemName", categoryDO.getSystemName());
             Integer catId = null;
             try{
                 catId = (Integer)catIdQuery.getSingleResult();
             }catch(NoResultException ex){                     
                 ex.printStackTrace();
             }catch(Exception ex){
                 throw ex;
             }
             
           if(catId!=null){
             if(!catId.equals(category.getId())){
                 throw new Exception("This Category System Name belongs to another category. Please choose some other system name."); 
             }  
           }                                 
         } 
        } 
        
        category.setSystemName(categoryDO.getSystemName());
        category.setName(categoryDO.getName());
        category.setSection(categoryDO.getSection());
        
        if(category.getId()==null){
            manager.persist(category);
        }
        
        ArrayList<String> systemNames = new ArrayList<String>();
        
        ArrayList<String> entries = new ArrayList<String>();        
        for (Iterator iter = dictEntries.iterator(); iter.hasNext();) {
            DictionaryDO dictDO = (DictionaryDO)iter.next();
            Dictionary dictionary = null;
             if(dictDO.getId() == null){
                 dictionary = new Dictionary();
             }else{
                  dictionary  = manager.find(Dictionary.class,dictDO.getId());
                 }
                         
             boolean update = false;
             if(dictDO.getDelete()!=null){ 
               if(dictDO.getDelete()){
                   if (dictionary.getId() != null){
                 //delete the dictionary entry from the database                    
                    manager.remove(dictionary);     
                   }
               }else{
                   update = true;
               }
              }else{
                   update = true;
             }
             
            if(dictDO.getEntry()!=null){
             if(!("").equals(dictDO.getEntry().trim())){   
              if(!entries.contains(dictDO.getEntry())){
                entries.add(dictDO.getEntry());
               }else{
                  throw new Exception("Entry texts for a category must be unique");
               } 
             }else{
                 throw new Exception("Entry text must not be blank");
             }              
            }else{
                throw new Exception("Entry text must not be blank");
            }  
             
             if(dictDO.getSystemName()!=null){
              if(!dictDO.getSystemName().trim().equals("")){   
               if(!systemNames.contains(dictDO.getSystemName())){
                 Query catIdQuery  = manager.createNamedQuery("getCategoryIdForDictSysName");
                 catIdQuery.setParameter("systemName", dictDO.getSystemName());
                 Integer catId = null;
                 try{
                     catId = (Integer)catIdQuery.getSingleResult();
                 }catch(NoResultException ex){                     
                     ex.printStackTrace();
                 }catch(Exception ex){
                     throw ex;
                 }
                 
                  if(catId != null){
                      if(!catId.equals(category.getId())){
                          throw new Exception("Dictionary System Name belongs to another category: "+dictDO.getSystemName()); 
                      }
                  }
                 systemNames.add(dictDO.getSystemName());
                 }else{
                  throw new Exception("Dictionary System Names must be unique");
                 }
              }
             }
             
             if(update){                 
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
        
        entries = null;
        systemNames = null;
        
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
}
