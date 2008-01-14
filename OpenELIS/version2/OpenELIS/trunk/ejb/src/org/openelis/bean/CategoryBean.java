package org.openelis.bean;

import java.rmi.RemoteException;
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
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.openelis.domain.CategoryDO;
import org.openelis.domain.CategoryTableRowDO;
import org.openelis.domain.DictionaryDO;
import org.openelis.entity.Category;
import org.openelis.entity.Dictionary;
import org.openelis.gwt.common.data.QueryOptionField;
import org.openelis.gwt.common.data.QueryStringField;
import org.openelis.local.LockLocal;
import org.openelis.remote.CategoryRemote;
import org.openelis.util.QueryBuilder;

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

    public CategoryDO getCategory(Integer categoryId, boolean unlock) {
        if(unlock){
            Query query = manager.createNamedQuery("getTableId");
            query.setParameter("name", "category");
            lockBean.giveUpLock((Integer)query.getSingleResult(),categoryId);            
        }
        
        Query query = manager.createNamedQuery("getCategory");
        query.setParameter("id", categoryId);
        CategoryDO category = (CategoryDO) query.getSingleResult();// getting organization with address and contacts

        return category;
    }

    public List getCategoryNameListByLetter(String letter,
                                            int startPos,
                                            int maxResults) {
        Query query = manager.createNamedQuery("getCategorySysNameRowsByLetter");
        query.setParameter("letter", letter);
        
        if(maxResults > 0){
            query.setFirstResult(startPos);
            query.setMaxResults(maxResults);
        }
        
        List<CategoryTableRowDO> catList = query.getResultList();// getting a list of organizations
        
        return catList;
    }

    public CategoryDO getCategoryUpdate(Integer id) throws Exception {
        
        return null;
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

    public List query(HashMap fields, int first, int max) throws RemoteException {
        StringBuffer sb = new StringBuffer();
        sb.append("select distinct c.id, c.systemName from Category c where 1=1 " );
        if(fields.containsKey("systemName"))
         sb.append(QueryBuilder.getQuery((QueryStringField)fields.get("systemName"), "c.systemName"));
        if(fields.containsKey("name"))
         sb.append(QueryBuilder.getQuery((QueryStringField)fields.get("name"), "c.name"));
        if(fields.containsKey("desc"))
         sb.append(QueryBuilder.getQuery((QueryStringField)fields.get("desc"), "c.description"));       
        if(fields.containsKey("secName"))
            sb.append(QueryBuilder.getQuery((QueryOptionField)fields.get("secName"), "c.section"));
        
        Query query = manager.createQuery(sb.toString()+" order by  c.systemName");
        
        if(fields.containsKey("systemName"))
            QueryBuilder.setParameters((QueryStringField)fields.get("systemName"), "c.systemName",query);
        if(fields.containsKey("name"))
            QueryBuilder.setParameters((QueryStringField)fields.get("name"), "c.name",query);
        if(fields.containsKey("desc"))
            QueryBuilder.setParameters((QueryStringField)fields.get("desc"), "c.description",query);  
        if(fields.containsKey("secName"))
            QueryBuilder.setParameters((QueryOptionField)fields.get("secName"), "c.section",query);
        
        return query.getResultList();
            
    }

    public Integer updateCategory(CategoryDO categoryDO, List dictEntries) {
        manager.setFlushMode(FlushModeType.COMMIT);
        Category category  = null;
        
        if (categoryDO.getId() == null){
            category = new Category();
        } 
        else{
            category = manager.find(Category.class, categoryDO.getId());
        }        
        
        category.setDescription(categoryDO.getDescription());
        category.setSystemName(categoryDO.getSystemName());
        category.setName(categoryDO.getName());
        category.setSection(categoryDO.getSection());
        
        if(category.getId()==null){
            manager.persist(category);
        }
        
        for (Iterator iter = dictEntries.iterator(); iter.hasNext();) {
            DictionaryDO dictDO = (DictionaryDO)iter.next();
            Dictionary dictionary = null;
             if(dictDO.getId() == null){
                 dictionary = new Dictionary();
             }else{
                  dictionary  = manager.find(Dictionary.class,dictDO.getId());
                 }
             
             System.out.println("dictionary "+dictionary);
             
             if(dictDO.getDelete()!=null){ 
               if(dictDO.getDelete()&& (dictionary.getId() != null)){
                 //delete the dictionary entry from the database                    
                    manager.remove(dictionary);
                    System.out.println("removed dictionary");
               }else{
                   dictionary.setCategory(category.getId());
                   dictionary.setEntry(dictDO.getEntry());
                   dictionary.setIsActive(dictDO.getIsActive());
                   dictionary.setLocalAbbrev(dictDO.getLocalAbbrev());
                   dictionary.setRelatedEntryKey(dictDO.getRelatedEntry());
                   dictionary.setSystemName(dictDO.getSystemName());    
               
                if(dictionary.getId()==null){
                  manager.persist(dictionary);
                }
               }
              }else{
                   dictionary.setCategory(category.getId());
                   dictionary.setEntry(dictDO.getEntry());
                   dictionary.setIsActive(dictDO.getIsActive());
                   dictionary.setLocalAbbrev(dictDO.getLocalAbbrev());
                   dictionary.setRelatedEntryKey(dictDO.getRelatedEntry());
                   dictionary.setSystemName(dictDO.getSystemName());    
               
               if(dictionary.getId()==null){
                  manager.persist(dictionary);
               }
             }
             
             
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
    	Query query = manager.createNamedQuery("getDropdownValues");
    	query.setParameter("id", categoryId);
    	
    	return query.getResultList();
    }
    
    public List getMatchingEntries(Integer id,String entry){
       Query query = manager.createNamedQuery("getMatchingEntries");  
       query.setParameter("entry", entry);       
       query.setParameter("id", id);
       System.out.println("id "+ id);
       System.out.println("entry "+ "\""+entry+"\"");
       List entryList = null;
       try{ 
           entryList = (List)query.getResultList();
       }catch(Exception ex){
           ex.printStackTrace();
           return null;
       }     
       return entryList;
    }

    public Integer getEntryIdForSystemName(String systemName){
        Query query = manager.createNamedQuery("getEntryIdForSystemName");  
        query.setParameter("systemName", systemName);
        Integer entryId = null;
        try{ 
          entryId = (Integer)query.getSingleResult();
        }catch(Exception ex){
            ex.printStackTrace();
            return null;
        }     
        return entryId;
    } 
    
    public Integer getEntryIdForEntry(String entry){
        Query query = manager.createNamedQuery("getEntryIdForEntry");  
        query.setParameter("entry", entry);
        Integer entryId = null;
        try{ 
          entryId = (Integer)query.getSingleResult();
        }catch(Exception ex){
            ex.printStackTrace();
            return null;
        }     
        return entryId;
    } 
        
    public Integer getCategoryId(String systemName){
        Query query = manager.createNamedQuery("getCategoryIdBySystemName");  
        query.setParameter("systemName", systemName);
        Integer categoryId = null;
        try{ 
            categoryId = (Integer)query.getSingleResult();
        }catch(Exception ex){
            ex.printStackTrace();
            return null;
        }     
        return categoryId;
    }
}
