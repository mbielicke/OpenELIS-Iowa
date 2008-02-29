package org.openelis.bean;

import java.util.ArrayList;
import java.util.HashMap;
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

import org.openelis.domain.StorageUnitDO;
import org.openelis.entity.StorageUnit;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.RPCDeleteException;
import org.openelis.gwt.common.data.CollectionField;
import org.openelis.gwt.common.data.QueryStringField;
import org.openelis.local.LockLocal;
import org.openelis.remote.StorageUnitRemote;
import org.openelis.util.QueryBuilder;
import org.openelis.utils.GetPage;

import edu.uiowa.uhl.security.domain.SystemUserDO;
import edu.uiowa.uhl.security.local.SystemUserUtilLocal;

@Stateless
public class StorageUnitBean implements StorageUnitRemote{

	@PersistenceContext(name = "openelis")
    private EntityManager manager;
    //private String className = this.getClass().getName();
   // private Logger log = Logger.getLogger(className);
	
	
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
            
        }
    }
    
	public List query(HashMap fields, int first, int max) throws Exception {
		StringBuffer sb = new StringBuffer();
        
        sb.append("select distinct s.id,s.description " + "from StorageUnit s where 1=1 ");
         //***append the abstract fields to the string buffer
        if(fields.containsKey("category") && ((ArrayList)((CollectionField)fields.get("category")).getValue()).size()>0 &&
       		 !(((ArrayList)((CollectionField)fields.get("category")).getValue()).size() == 1 && "".equals(((ArrayList)((CollectionField)fields.get("category")).getValue()).get(0))))
        	sb.append(QueryBuilder.getQuery((CollectionField)fields.get("category"), "s.category"));
        if(fields.containsKey("description"))
        	sb.append(QueryBuilder.getQuery((QueryStringField)fields.get("description"), "s.description"));
        if(fields.containsKey("isSingular") && ((ArrayList)((CollectionField)fields.get("isSingular")).getValue()).size()>0 &&
       		 !(((ArrayList)((CollectionField)fields.get("isSingular")).getValue()).size() == 1 && "".equals(((ArrayList)((CollectionField)fields.get("isSingular")).getValue()).get(0))))
        	sb.append(QueryBuilder.getQuery((CollectionField)fields.get("isSingular"), "s.isSingular"));
        	
         Query query = manager.createQuery(sb.toString()+" order by s.description");
         
//       if(first > -1)
     	// query.setFirstResult(first);

         if(first > -1 && max > -1)
        	 query.setMaxResults(first+max);
         
//       ***set the parameters in the query
         if(fields.containsKey("category") && ((ArrayList)((CollectionField)fields.get("category")).getValue()).size()>0 &&
        		 !(((ArrayList)((CollectionField)fields.get("category")).getValue()).size() == 1 && "".equals(((ArrayList)((CollectionField)fields.get("category")).getValue()).get(0))))
        	 QueryBuilder.setParameters((CollectionField)fields.get("category"), "s.category", query);
         if(fields.containsKey("description"))
        	 QueryBuilder.setParameters((QueryStringField)fields.get("description"), "s.description", query);
         if(fields.containsKey("isSingular") && ((ArrayList)((CollectionField)fields.get("isSingular")).getValue()).size()>0 &&
           		 !(((ArrayList)((CollectionField)fields.get("isSingular")).getValue()).size() == 1 && "".equals(((ArrayList)((CollectionField)fields.get("isSingular")).getValue()).get(0))))
        	 QueryBuilder.setParameters((CollectionField)fields.get("isSingular"), "s.isSingular", query);
         
         List returnList = GetPage.getPage(query.getResultList(), first, max);
         
         if(returnList == null)
        	 throw new LastPageException();
         else
        	 return returnList;
         //return query.getResultList();
	}

	public Integer updateStorageUnit(StorageUnitDO unitDO) {
		manager.setFlushMode(FlushModeType.COMMIT);
		StorageUnit storageUnit = null;
		
		try {
//			organization reference table id
        	Query query = manager.createNamedQuery("getTableId");
            query.setParameter("name", "storage_unit");
            Integer storageUnitReferenceId = (Integer)query.getSingleResult();
            
            if (unitDO.getId() == null)
            	storageUnit = new StorageUnit();
            else
            	storageUnit = manager.find(StorageUnit.class, unitDO.getId());
            
            storageUnit.setCategory(unitDO.getCategory());
            storageUnit.setDescription(unitDO.getDescription());
            storageUnit.setIsSingular(unitDO.getIsSingular());
         
            if (storageUnit.getId() == null) {
	        	manager.persist(storageUnit);
            }
         
            lockBean.giveUpLock(storageUnitReferenceId,storageUnit.getId()); 
		} catch (Exception e) {
            //log.error(e.getMessage());
            e.printStackTrace();
        }
            
		return storageUnit.getId();
	}

	public StorageUnitDO getStorageUnit(Integer StorageUnitId) {
		Query query = manager.createNamedQuery("getStorageUnit");
		query.setParameter("id", StorageUnitId);
		StorageUnitDO storageUnitRecord = (StorageUnitDO) query.getResultList().get(0);// getting first storage unit record

        return storageUnitRecord;
	}
	
	public StorageUnitDO getStorageUnitAndLock(Integer StorageUnitId) throws Exception{
		Query query = manager.createNamedQuery("getTableId");
        query.setParameter("name", "storage_unit");
        lockBean.getLock((Integer)query.getSingleResult(),StorageUnitId);
        
        return getStorageUnit(StorageUnitId);
		
	}
	
	public StorageUnitDO getStorageUnitAndUnlock(Integer StorageUnitId) {
        Query unlockQuery = manager.createNamedQuery("getTableId");
        unlockQuery.setParameter("name", "storage_unit");
        lockBean.giveUpLock((Integer)unlockQuery.getSingleResult(),StorageUnitId);
		
        return getStorageUnit(StorageUnitId);
	}

	public Integer getSystemUserId(){
        try {
            SystemUserDO systemUserDO = sysUser.getSystemUser(ctx.getCallerPrincipal()
                                                                 .getName());
            return systemUserDO.getId();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }      
    }

	public void deleteStorageUnit(Integer storageUnitId) throws Exception {
		manager.setFlushMode(FlushModeType.COMMIT);
		StorageUnit storageUnit = null;
		
		//we need to see if this item can be deleted first
		Query query = null;
		query = manager.createNamedQuery("getStorageLocationByStorageUnitId");
		query.setParameter("id", storageUnitId);
		List linkedRecords = query.getResultList();
		
		if(linkedRecords.size() > 0){
			throw new RPCDeleteException();
		}
		//then we need to delete it
		try {
            	storageUnit = manager.find(StorageUnit.class, storageUnitId);
            	if(storageUnit != null)
            		manager.remove(storageUnit);
            	
		} catch (Exception e) {
            //log.error(e.getMessage());
            e.printStackTrace();
        }		
	}

	public List autoCompleteLookupByDescription(String desc, int maxResults) {
		Query query = null;
		query = manager.createNamedQuery("getStorageUnitAutoCompleteByDesc");
		query.setParameter("desc",desc);
		query.setMaxResults(maxResults);
		return query.getResultList();
	}

	public Object[] autoCompleteLookupById(Integer id) {
		Query query  = manager.createNamedQuery("getStorageUnitAutoCompleteById");
        query.setParameter("id",id);
        try{
        	return (Object[])query.getSingleResult();
        	
        }catch(NoResultException e){
        	//if we hit this exception we want to return an empty array for our servlet
        	Object[] returnArray = new Object[3];
        	return returnArray;
        }
	}	 
}
