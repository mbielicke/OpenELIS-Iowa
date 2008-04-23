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
import org.openelis.domain.StorageLocationDO;
import org.openelis.entity.StorageLocation;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.RPCException;
import org.openelis.gwt.common.TableFieldErrorException;
import org.openelis.local.LockLocal;
import org.openelis.meta.StorageLocationChildMeta;
import org.openelis.meta.StorageLocationChildStorageUnitMeta;
import org.openelis.meta.StorageLocationMeta;
import org.openelis.meta.StorageLocationStorageUnitMeta;
import org.openelis.remote.StorageLocationRemote;
import org.openelis.util.Meta;
import org.openelis.util.QueryBuilder;
import org.openelis.utils.GetPage;

import edu.uiowa.uhl.security.domain.SystemUserDO;
import edu.uiowa.uhl.security.local.SystemUserUtilLocal;

@Stateless
@SecurityDomain("openelis")
@RolesAllowed("storagelocation-select")
public class StorageLocationBean implements StorageLocationRemote{

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

    @RolesAllowed("storagelocation-delete")
	public void deleteStorageLoc(Integer StorageLocId) throws Exception {
    	Query lockQuery = manager.createNamedQuery("getTableId");
		lockQuery.setParameter("name", "storage_location");
		Integer storageLocTableId = (Integer)lockQuery.getSingleResult();
        lockBean.getLock(storageLocTableId, StorageLocId);
        
		manager.setFlushMode(FlushModeType.COMMIT);
		StorageLocation storageLocation = null;

		//validate the storage loc record
        List exceptionList = new ArrayList();
        exceptionList = validateForDelete(StorageLocId);
        if(exceptionList.size() > 0){
        	throw (RPCException)exceptionList.get(0);
        }
        
		//delete the records				
        //delete the parent record
        storageLocation = manager.find(StorageLocation.class, StorageLocId);
        if(storageLocation != null)
        manager.remove(storageLocation);
            	
        //find the child records
        List childStorageLocs = getStorageLocChildren(StorageLocId);
            	
        //delete the child records if they exist
        Iterator children = childStorageLocs.iterator();
        while(children.hasNext()){
        	StorageLocationDO childDO = (StorageLocationDO) children.next();
        	StorageLocation child = null;
        	child = manager.find(StorageLocation.class, childDO.getId());
         		
        	if(child != null)
        		manager.remove(child);           		
        }
           
        lockBean.giveUpLock(storageLocTableId, StorageLocId);
	}

	public StorageLocationDO getStorageLoc(Integer StorageId) {
		Query query = manager.createNamedQuery("getStorageLocation");
		query.setParameter("id", StorageId);
		StorageLocationDO storageLocRecord = (StorageLocationDO) query.getResultList().get(0);// getting first storage location record

        return storageLocRecord;
	}
	
    @RolesAllowed("storagelocation-update")
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
	
	public List getStorageLocChildren(Integer StorageId) {
		Query query = manager.createNamedQuery("getStorageLocationChildren");
		query.setParameter("id", StorageId);
		return query.getResultList();
	}

	public List query(HashMap fields, int first, int max) throws Exception {
		StringBuffer sb = new StringBuffer();
        QueryBuilder qb = new QueryBuilder();
        
        StorageLocationMeta storageLocationMeta = StorageLocationMeta.getInstance();
        StorageLocationChildMeta StorageLocationChildrenMeta = StorageLocationChildMeta.getInstance();
        StorageLocationChildStorageUnitMeta storageLocationChildrenStorageUnitMeta = StorageLocationChildStorageUnitMeta.getInstance();
        StorageLocationStorageUnitMeta storageUnitMeta = StorageLocationStorageUnitMeta.getInstance();
        
        qb.addMeta(new Meta[]{storageLocationMeta, StorageLocationChildrenMeta, storageLocationChildrenStorageUnitMeta, storageUnitMeta});
        
        qb.setSelect("distinct "+storageLocationMeta.ID+", "+storageLocationMeta.NAME);
        qb.addTable(storageLocationMeta);
        
//      this method is going to throw an exception if a column doesnt match
        qb.addWhere(fields);      

        qb.setOrderBy(storageLocationMeta.NAME);
        
        qb.addWhere(storageLocationMeta.PARENT_STORAGE_LOCATION + " is null");    
        
        if(qb.hasTable(storageLocationChildrenStorageUnitMeta.getTable()))
        	qb.addTable(StorageLocationChildrenMeta);
        
        //if(qb.hasTable(StorageLocationChildrenMeta.getTable()))
        //	qb.addWhere("");
        
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

    @RolesAllowed("storagelocation-update")
	public Integer updateStorageLoc(StorageLocationDO storageDO, List storageLocationChildren) throws Exception{
		manager.setFlushMode(FlushModeType.COMMIT);
		StorageLocation storageLocation = null;
		
		//validate the storage loc record
        List exceptionList = new ArrayList();
        validateStorageLocation(storageDO, exceptionList);
        if(exceptionList.size() > 0){
        	throw (RPCException)exceptionList.get(0);
        }
        
        if (storageDO.getId() == null)
           	storageLocation = new StorageLocation();
        else
           	storageLocation = manager.find(StorageLocation.class, storageDO.getId());
            
	    storageLocation.setIsAvailable(storageDO.getIsAvailable());
	    storageLocation.setLocation(storageDO.getLocation());
	    storageLocation.setName(storageDO.getName());
	    storageLocation.setStorageUnitId(storageDO.getStorageUnitId());
	         
	    if (storageLocation.getId() == null) {
		  	manager.persist(storageLocation);
	    }
	         
	    //update the children
	    int sortOrder=1;
	    for (int i=0; i<storageLocationChildren.size();i++) {
	      	 StorageLocationDO childDO = (StorageLocationDO) storageLocationChildren.get(i);
	      	 StorageLocation childStorageLoc = null;
		            
	      	 //validate the child storage loc record
	         exceptionList = new ArrayList();
	         validateChildStorageLocation(childDO, i, exceptionList);
	         if(exceptionList.size() > 0){
	        	 throw (RPCException)exceptionList.get(0);
	         }
	            
		    if (childDO.getId() == null)
		    	childStorageLoc = new StorageLocation();
		    else
		    	childStorageLoc = manager.find(StorageLocation.class, childDO.getId());
		
		    if(childDO.getDelete() && childStorageLoc.getId() != null){
		    	//delete the child record from the database
			    manager.remove(childStorageLoc);        	
		    }else{
		    	childStorageLoc.setSortOrder(sortOrder);
				childStorageLoc.setLocation(childDO.getLocation());
				childStorageLoc.setParentStorageLocationId(storageLocation.getId());
				childStorageLoc.setStorageUnitId(childDO.getStorageUnitId());
				childStorageLoc.setIsAvailable(childDO.getIsAvailable());
						            
				if (childStorageLoc.getId() == null) {
					manager.persist(childStorageLoc);
				}
				sortOrder++;
		    }
		}
            
		return storageLocation.getId();
	}

	public Integer getStorageLocByName(String name) {
		Query query = manager.createNamedQuery("getStorageLocationByName");
		query.setParameter("name", name);
		return (Integer) query.getSingleResult();
	}

	public List validateForAdd(StorageLocationDO storageLocationDO, List childLocs) {
		List exceptionList = new ArrayList();
		
		validateStorageLocation(storageLocationDO, exceptionList);
		
		for(int i=0; i<childLocs.size();i++){			
			StorageLocationDO childDO = (StorageLocationDO) childLocs.get(i);
			
			validateChildStorageLocation(childDO, i, exceptionList);
		}
		
		return exceptionList;
	}

	public List validateForDelete(Integer storageLocationId) {
		// TODO Auto-generated method stub
		return null;
	}

	public List validateForUpdate(StorageLocationDO storageLocationDO, List childLocs) {
		List exceptionList = new ArrayList();
		
		validateStorageLocation(storageLocationDO, exceptionList);
		
		for(int i=0; i<childLocs.size();i++){			
			StorageLocationDO childDO = (StorageLocationDO) childLocs.get(i);
			
			validateChildStorageLocation(childDO, i, exceptionList);
		}
		
		return exceptionList;	
	}
	
	private void validateStorageLocation(StorageLocationDO storageLocationDO, List exceptionList){
		//name required
		if(storageLocationDO.getName() == null || "".equals(storageLocationDO.getName())){
			exceptionList.add(new FieldErrorException("fieldRequiredException",StorageLocationMeta.NAME));
		}
		
		//no name duplicates
		Query query = null;
		//its an add if its null
		if(storageLocationDO.getId() == null){
			query = manager.createNamedQuery("storageLocationAddNameCompare");
			query.setParameter("name", storageLocationDO.getName());
		}else{
			query = manager.createNamedQuery("storageLocationUpdateNameCompare");
			query.setParameter("name", storageLocationDO.getName());
			query.setParameter("id",storageLocationDO.getId());
		}
		
		if(query.getResultList().size() > 0)
			exceptionList.add(new FieldErrorException("fieldUniqueException",StorageLocationMeta.NAME));
		
		//location required
		if(storageLocationDO.getLocation() == null || "".equals(storageLocationDO.getLocation())){
			exceptionList.add(new FieldErrorException("fieldRequiredException",StorageLocationMeta.LOCATION));
		}
		
		//storage unit required
		if(storageLocationDO.getStorageUnitId() == null || "".equals(storageLocationDO.getStorageUnitId())){
			exceptionList.add(new FieldErrorException("fieldRequiredException",StorageLocationStorageUnitMeta.DESCRIPTION));
		}		
	}
	
	private void validateChildStorageLocation(StorageLocationDO storageLocationDO, int rowIndex, List exceptionList){
		//storage unit required
		if(storageLocationDO.getStorageUnitId() == null || "".equals(storageLocationDO.getStorageUnitId())){
			exceptionList.add(new TableFieldErrorException("fieldRequiredException", rowIndex, StorageLocationChildStorageUnitMeta.DESCRIPTION));
		}
		
		//location required		
		if(storageLocationDO.getLocation() == null || "".equals(storageLocationDO.getLocation())){
			exceptionList.add(new TableFieldErrorException("fieldRequiredException", rowIndex, StorageLocationChildMeta.LOCATION));
		}		
	}
}
