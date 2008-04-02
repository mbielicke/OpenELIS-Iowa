package org.openelis.bean;

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

import org.openelis.domain.StorageLocationDO;
import org.openelis.entity.StorageLocation;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.RPCDeleteException;
import org.openelis.local.LockLocal;
import org.openelis.meta.StorageLocationMeta;
import org.openelis.meta.StorageLocationParentMeta;
import org.openelis.meta.StorageLocationStorageUnitMeta;
import org.openelis.remote.StorageLocationRemote;
import org.openelis.util.Meta;
import org.openelis.util.QueryBuilder;
import org.openelis.utils.GetPage;

import edu.uiowa.uhl.security.domain.SystemUserDO;
import edu.uiowa.uhl.security.local.SystemUserUtilLocal;

@Stateless
public class StorageLocationBean implements StorageLocationRemote{

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
    
	public List autoCompleteLookupByName(String name, int maxResults) {
		Query query = null;
		query = manager.createNamedQuery("getStorageLocationAutoCompleteByName");
		query.setParameter("name",name);
		query.setMaxResults(maxResults);
		return query.getResultList();
	}

	public Object[] autoCompleteLookupById(Integer id) {
        Query query  = manager.createNamedQuery("getStorageLocationAutoCompleteById");
        query.setParameter("id",id);
        try{
        	return (Object[])query.getSingleResult();
        	
        }catch(NoResultException e){
        	//if we hit this exception we want to return an empty array for our servlet
        	Object[] returnArray = new Object[3];
        	return returnArray;
        }
	}

	public void deleteStorageLoc(Integer StorageLocId) throws Exception {
		manager.setFlushMode(FlushModeType.COMMIT);
		StorageLocation storageLocation = null;
		
//		we need to see if this item can be deleted first
		Query query = null;
		query = manager.createNamedQuery("getStorageLocationByParentId");
		query.setParameter("id", StorageLocId);
		List linkedRecords = query.getResultList();
		
		if(linkedRecords.size() > 0){
			throw new RPCDeleteException();
		}
		//then we need to delete it
		try {
            	storageLocation = manager.find(StorageLocation.class, StorageLocId);
            	if(storageLocation != null)
            		manager.remove(storageLocation);
            	
		} catch (Exception e) {
            e.printStackTrace();
        }			
	}

	public StorageLocationDO getStorageLoc(Integer StorageId) {
		Query query = manager.createNamedQuery("getStorageLocation");
		query.setParameter("id", StorageId);
		StorageLocationDO storageLocRecord = (StorageLocationDO) query.getResultList().get(0);// getting first storage location record

        return storageLocRecord;
	}
	
	public StorageLocationDO getStorageLocAndLock(Integer StorageId) throws Exception{
		Query query = manager.createNamedQuery("getTableId");
        query.setParameter("name", "storage_location");
        lockBean.getLock((Integer)query.getSingleResult(),StorageId);
        
        return getStorageLoc(StorageId);       
	}
	
	public StorageLocationDO getStorageLocAndUnlock(Integer StorageId) {
        Query query = manager.createNamedQuery("getTableId");
        query.setParameter("name", "storage_location");
        lockBean.giveUpLock((Integer)query.getSingleResult(),StorageId);
        
        return getStorageLoc(StorageId);
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
	
	public List getStorageLocChildren(Integer StorageId, boolean unlock) {
		//FIXME not sure how to handle locks yet
		/*if(unlock){
            Query query = manager.createNamedQuery("getTableId");
            query.setParameter("name", "storage_location");
            lockBean.giveUpLock((Integer)query.getSingleResult(),StorageId);
        }*/
		
		Query query = manager.createNamedQuery("getStorageLocationChildren");
		query.setParameter("id", StorageId);
		return query.getResultList();
	}

	public List query(HashMap fields, int first, int max) throws Exception {
		StringBuffer sb = new StringBuffer();
        QueryBuilder qb = new QueryBuilder();
        
        StorageLocationMeta storageLocationMeta = StorageLocationMeta.getInstance();
        StorageLocationParentMeta parentStorageLocationMeta = StorageLocationParentMeta.getInstance();
        StorageLocationStorageUnitMeta storageUnitMeta = StorageLocationStorageUnitMeta.getInstance();
        
        qb.addMeta(new Meta[]{storageLocationMeta, parentStorageLocationMeta, storageUnitMeta});
        
        qb.setSelect("distinct "+storageLocationMeta.ID+", "+storageLocationMeta.NAME);
        qb.addTable(storageLocationMeta);
        
        //sb.append("select distinct s.id,s.name " + "from StorageLocation s where 1=1 ");
        
//      this method is going to throw an exception if a column doesnt match
        qb.addWhere(fields);      

        qb.setOrderBy(storageLocationMeta.NAME);
        
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

	public Integer updateStorageLoc(StorageLocationDO storageDO, List storageLocationChildren) {
		manager.setFlushMode(FlushModeType.COMMIT);
		StorageLocation storageLocation = null;
		
		try {
            if (storageDO.getId() == null)
            	storageLocation = new StorageLocation();
            else
            	storageLocation = manager.find(StorageLocation.class, storageDO.getId());
            
         storageLocation.setIsAvailable(storageDO.getIsAvailable());
         storageLocation.setLocation(storageDO.getLocation());
         storageLocation.setName(storageDO.getName());
         storageLocation.setParentStorageLocationId(storageDO.getParentStorageLocationId());
         //FIXME this may need to change....
         storageLocation.setSortOrder(0);
         storageLocation.setStorageUnitId(storageDO.getStorageUnitId());
         
         if (storageLocation.getId() == null) {
	        	manager.persist(storageLocation);
         }
         
//       update children
         int i=0;
         for (Iterator childrenItr = storageLocationChildren.iterator(); childrenItr.hasNext();) {
        	 StorageLocationDO childDO = (StorageLocationDO) childrenItr.next();
        	 StorageLocation childStorageLoc = null;
	            
	            if (childDO.getId() == null)
	            	childStorageLoc = new StorageLocation();
	            else
	            	childStorageLoc = manager.find(StorageLocation.class, childDO.getId());

	            //if(contactDO.getDelete() && orgContact.getId() != null){
	            	//delete the contact record and the address record from the database
	            //	manager.remove(orgContact);
	            //	addressBean.deleteAddress(contactDO.getAddressDO());
	            	
	            //}else{
	            childStorageLoc.setSortOrder(i);
	            childStorageLoc.setName(childDO.getName());
	            childStorageLoc.setLocation(childDO.getLocation());
	            childStorageLoc.setParentStorageLocationId(storageLocation.getId());
	            childStorageLoc.setStorageUnitId(childDO.getStorageUnitId());
	            childStorageLoc.setIsAvailable(childDO.getIsAvailable());
			            
			    if (childStorageLoc.getId() == null) {
			       manager.persist(childStorageLoc);
			    }
			    i++;
         }
			//}
         
		} catch (Exception e) {
            e.printStackTrace();
        }
            
		return storageLocation.getId();
	}
}
