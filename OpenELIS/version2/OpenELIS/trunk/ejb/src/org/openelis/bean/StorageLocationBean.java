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
* Copyright (C) The University of Iowa.  All Rights Reserved.
*/
package org.openelis.bean;

import org.jboss.annotation.security.SecurityDomain;
import org.openelis.domain.StorageLocationDO;
import org.openelis.entity.StorageLocation;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.FormErrorException;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.RPCException;
import org.openelis.gwt.common.TableFieldErrorException;
import org.openelis.local.LockLocal;
import org.openelis.metamap.StorageLocationMetaMap;
import org.openelis.persistence.CachingManager;
import org.openelis.remote.StorageLocationRemote;
import org.openelis.security.domain.SystemUserDO;
import org.openelis.util.QueryBuilder;
import org.openelis.utils.GetPage;

import java.util.ArrayList;
import java.util.HashMap;
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
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

@Stateless
@EJBs({
    @EJB(name="ejb/Lock",beanInterface=LockLocal.class)
})
@SecurityDomain("openelis")
@RolesAllowed("storagelocation-select")
public class StorageLocationBean implements StorageLocationRemote{

	@PersistenceContext(name = "openelis")
    private EntityManager manager;	

	@Resource
	private SessionContext ctx;
	
    private LockLocal lockBean;
    private static final StorageLocationMetaMap StorageLocationMeta = new StorageLocationMetaMap();

    @PostConstruct
    private void init()
    {
        lockBean =  (LockLocal)ctx.lookup("ejb/Lock");
    }
    
	public List autoCompleteLookupByName(String name, int maxResults) {
		Query query = null;
		query = manager.createNamedQuery("StorageLocation.AutoCompleteByName");
		query.setParameter("name",name);
        query.setParameter("loc",name);
        query.setParameter("desc",name);
		query.setMaxResults(maxResults);
		return query.getResultList();
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
		Query query = manager.createNamedQuery("StorageLocation.StorageLocation");
		query.setParameter("id", StorageId);
		StorageLocationDO storageLocRecord = (StorageLocationDO) query.getResultList().get(0);// getting first storage location record

        return storageLocRecord;
	}
	
    @RolesAllowed("storagelocation-update")
	public StorageLocationDO getStorageLocAndLock(Integer StorageId, String session) throws Exception{
		Query query = manager.createNamedQuery("getTableId");
        query.setParameter("name", "storage_location");
        lockBean.getLock((Integer)query.getSingleResult(),StorageId);
        
        return getStorageLoc(StorageId);       
	}
	
	public StorageLocationDO getStorageLocAndUnlock(Integer StorageId, String session) {
        Query query = manager.createNamedQuery("getTableId");
        query.setParameter("name", "storage_location");
        lockBean.giveUpLock((Integer)query.getSingleResult(),StorageId);
        
        return getStorageLoc(StorageId);
	}

	public List getStorageLocChildren(Integer StorageId) {
		Query query = manager.createNamedQuery("StorageLocation.GetChildren");
		query.setParameter("id", StorageId);
		return query.getResultList();
	}

	public List query(HashMap fields, int first, int max) throws Exception {
		StringBuffer sb = new StringBuffer();
        QueryBuilder qb = new QueryBuilder();
        
        qb.setMeta(StorageLocationMeta);
        
        qb.setSelect("distinct new org.openelis.domain.IdNameDO("+StorageLocationMeta.getId()+", "+StorageLocationMeta.getName() + ") ");
         
//      this method is going to throw an exception if a column doesnt match
        qb.addWhere(fields);      

        qb.setOrderBy(StorageLocationMeta.getName());
        
        qb.addWhere(StorageLocationMeta.getParentStorageLocationId() + " is null");    
        System.out.println("before get EJBQL");
        sb.append(qb.getEJBQL());
System.out.println(sb.toString());
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
        Query query = manager.createNamedQuery("getTableId");
        query.setParameter("name", "storage_location");
        Integer storageLocationReferenceId = (Integer)query.getSingleResult();
        
        if(storageDO.getId() != null){
            lockBean.getLock(storageLocationReferenceId,storageDO.getId());
        }
        
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
            
        lockBean.giveUpLock(storageLocationReferenceId, storageLocation.getId()); 
        
		return storageLocation.getId();
	}

	public Integer getStorageLocByName(String name) {
		Query query = manager.createNamedQuery("StorageLocation.IdByName");
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
		List exceptionList = new ArrayList();
		//make sure no storage rows are pointing to this record
		Query query = null;
		query = manager.createNamedQuery("Storage.IdByStorageLocation");
		query.setParameter("id", storageLocationId);
		List linkedRecords = query.getResultList();

		if(linkedRecords.size() > 0){
			exceptionList.add(new FormErrorException("storageLocationStorageDeleteException"));
		}
		
		//make sure no inventory locations are pointing to this record
		query = manager.createNamedQuery("InventoryLocation.IdByStorageLocation");
		query.setParameter("id", storageLocationId);
		linkedRecords = query.getResultList();

		if(linkedRecords.size() > 0){
			exceptionList.add(new FormErrorException("storageLocationInventoryLocationDeleteException"));
		}
		
		return exceptionList;
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
			exceptionList.add(new FieldErrorException("fieldRequiredException",StorageLocationMeta.getName()));
		}
		
		//no name duplicates
		Query query = null;
		//its an add if its null
		if(storageLocationDO.getId() == null){
			query = manager.createNamedQuery("StorageLocation.AddNameCompare");
			query.setParameter("name", storageLocationDO.getName());
		}else{
			query = manager.createNamedQuery("StorageLocation.UpdateNameCompare");
			query.setParameter("name", storageLocationDO.getName());
			query.setParameter("id",storageLocationDO.getId());
		}
		
		if(query.getResultList().size() > 0)
			exceptionList.add(new FieldErrorException("fieldUniqueException",StorageLocationMeta.getName()));
		
		//location required
		if(storageLocationDO.getLocation() == null || "".equals(storageLocationDO.getLocation())){
			exceptionList.add(new FieldErrorException("fieldRequiredException",StorageLocationMeta.getLocation()));
		}
		
		//storage unit required
		if(storageLocationDO.getStorageUnitId() == null || "".equals(storageLocationDO.getStorageUnitId())){
			exceptionList.add(new FieldErrorException("fieldRequiredException",StorageLocationMeta.STORAGE_UNIT_META.getDescription()));
		}		
	}
	
	private void validateChildStorageLocation(StorageLocationDO storageLocationDO, int rowIndex, List exceptionList){
		//storage unit required
		if(storageLocationDO.getStorageUnitId() == null || "".equals(storageLocationDO.getStorageUnitId())){
			exceptionList.add(new TableFieldErrorException("fieldRequiredException", rowIndex, StorageLocationMeta.CHILD_STORAGE_LOCATION_META.STORAGE_UNIT_META.getDescription()));
		}
		
		//location required		
		if(storageLocationDO.getLocation() == null || "".equals(storageLocationDO.getLocation())){
			exceptionList.add(new TableFieldErrorException("fieldRequiredException", rowIndex, StorageLocationMeta.CHILD_STORAGE_LOCATION_META.getLocation()));
		}		
	}
}
