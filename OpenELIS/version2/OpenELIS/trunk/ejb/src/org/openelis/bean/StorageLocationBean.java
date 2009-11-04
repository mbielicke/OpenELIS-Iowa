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
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.annotation.security.SecurityDomain;
import org.openelis.domain.ReferenceTable;
import org.openelis.domain.StorageLocationViewDO;
import org.openelis.entity.StorageLocation;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.FormErrorException;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.TableFieldErrorException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.gwt.common.data.deprecated.AbstractField;
import org.openelis.local.LockLocal;
import org.openelis.metamap.StorageLocationMetaMap;
import org.openelis.remote.StorageLocationRemote;
import org.openelis.util.QueryBuilder;
import org.openelis.utilcommon.DataBaseUtil;
import org.openelis.utils.GetPage;

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
    private static int storageLocationRefTableId;
    private static final StorageLocationMetaMap StorageLocationMeta = new StorageLocationMetaMap();

    public StorageLocationBean(){
        storageLocationRefTableId = ReferenceTable.STORAGE_LOCATION;
    }
    
    @PostConstruct
    private void init()
    {
        lockBean =  (LockLocal)ctx.lookup("ejb/Lock");
    }
    
	public ArrayList autoCompleteLookupByName(String name, int maxResults) {
		Query query = null;
		query = manager.createNamedQuery("StorageLocation.AutoCompleteByName");
		query.setParameter("name",name);
        query.setParameter("loc",name);
        query.setParameter("desc",name);
		query.setMaxResults(maxResults);
		return DataBaseUtil.toArrayList(query.getResultList());
	}

    @RolesAllowed("storagelocation-delete")
	public void deleteStorageLoc(Integer StorageLocId) throws Exception {
    	lockBean.getLock(storageLocationRefTableId, StorageLocId);
        
        validateForDelete(StorageLocId);
		
        manager.setFlushMode(FlushModeType.COMMIT);
		StorageLocation storageLocation = null;

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
            StorageLocationViewDO childDO = (StorageLocationViewDO) children.next();
        	StorageLocation child = null;
        	child = manager.find(StorageLocation.class, childDO.getId());
         		
        	if(child != null)
        		manager.remove(child);           		
        }
           
        lockBean.giveUpLock(storageLocationRefTableId, StorageLocId);
	}

	public StorageLocationViewDO getStorageLoc(Integer StorageId) {
		Query query = manager.createNamedQuery("StorageLocation.StorageLocation");
		query.setParameter("id", StorageId);
		StorageLocationViewDO storageLocRecord = (StorageLocationViewDO) query.getResultList().get(0);// getting first storage location record

        return storageLocRecord;
	}
	
    @RolesAllowed("storagelocation-update")
	public StorageLocationViewDO getStorageLocAndLock(Integer StorageId, String session) throws Exception{
		lockBean.getLock(storageLocationRefTableId, StorageId);
        
        return getStorageLoc(StorageId);       
	}
	
	public StorageLocationViewDO getStorageLocAndUnlock(Integer StorageId, String session) {
        lockBean.giveUpLock(storageLocationRefTableId, StorageId);
        
        return getStorageLoc(StorageId);
	}

	public List getStorageLocChildren(Integer StorageId) {
		Query query = manager.createNamedQuery("StorageLocation.GetChildren");
		query.setParameter("id", StorageId);
		return query.getResultList();
	}

	public List query(ArrayList<AbstractField> fields, int first, int max) throws Exception {
		StringBuffer sb = new StringBuffer();
        QueryBuilder qb = new QueryBuilder();
        
        qb.setMeta(StorageLocationMeta);
        
        qb.setSelect("distinct new org.openelis.domain.IdNameDO("+StorageLocationMeta.getId()+", "+StorageLocationMeta.getName() + ") ");
         
//      this method is going to throw an exception if a column doesnt match
        qb.addWhere(fields);      

        qb.setOrderBy(StorageLocationMeta.getName());
        
        qb.addWhere(StorageLocationMeta.getParentStorageLocationId() + " is null");    
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
	public Integer updateStorageLoc(StorageLocationViewDO storageDO, List storageLocationChildren) throws Exception{
        if(storageDO.getId() != null)
            lockBean.validateLock(storageLocationRefTableId,storageDO.getId());
        
        validateStorageLocation(storageDO, storageLocationChildren);
        
		manager.setFlushMode(FlushModeType.COMMIT);
		StorageLocation storageLocation = null;
        
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
	        StorageLocationViewDO childDO = (StorageLocationViewDO) storageLocationChildren.get(i);
	      	 StorageLocation childStorageLoc = null;
		            
		    if (childDO.getId() == null)
		    	childStorageLoc = new StorageLocation();
		    else
		    	childStorageLoc = manager.find(StorageLocation.class, childDO.getId());
		
		    //deleted flag was removed from the DOs
		    //this will be fixed when this screen is rewritten
		    //if(childDO.getDelete() && childStorageLoc.getId() != null){
		    	//delete the child record from the database
			//    manager.remove(childStorageLoc);        	
		    //}else{
                childStorageLoc.setName(storageDO.getName());
		    	childStorageLoc.setSortOrder(sortOrder);
				childStorageLoc.setLocation(childDO.getLocation());
				childStorageLoc.setParentStorageLocationId(storageLocation.getId());
				childStorageLoc.setStorageUnitId(childDO.getStorageUnitId());
				childStorageLoc.setIsAvailable(childDO.getIsAvailable());
						            
				if (childStorageLoc.getId() == null) {
					manager.persist(childStorageLoc);
				}
				sortOrder++;
		    //}
		}
            
        lockBean.giveUpLock(storageLocationRefTableId, storageLocation.getId()); 
        
		return storageLocation.getId();
	}

	public Integer getStorageLocByName(String name) {
		Query query = manager.createNamedQuery("StorageLocation.IdByName");
		query.setParameter("name", name);
		return (Integer) query.getSingleResult();
	}

	public void validateForDelete(Integer storageLocationId) throws Exception {
	    ValidationErrorsList list = new ValidationErrorsList();

	    //make sure no storage rows are pointing to this record
		Query query = null;
		query = manager.createNamedQuery("Storage.IdByStorageLocation");
		query.setParameter("id", storageLocationId);
		List linkedRecords = query.getResultList();

		if(linkedRecords.size() > 0){
		    list.add(new FormErrorException("storageLocationStorageDeleteException"));
		}
		
		//make sure no inventory locations are pointing to this record
		query = manager.createNamedQuery("InventoryLocation.IdByStorageLocation");
		query.setParameter("id", storageLocationId);
		linkedRecords = query.getResultList();

		if(linkedRecords.size() > 0){
		    list.add(new FormErrorException("storageLocationInventoryLocationDeleteException"));
		}
		
		if(list.size() > 0)
            throw list;
	}
	
	private void validateStorageLocation(StorageLocationViewDO storageLocationDO, List childLocs) throws Exception {
	    ValidationErrorsList list = new ValidationErrorsList();
	    
		//name required
		if(storageLocationDO.getName() == null || "".equals(storageLocationDO.getName())){
		    list.add(new FieldErrorException("fieldRequiredException",StorageLocationMeta.getName()));
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
		    list.add(new FieldErrorException("fieldUniqueException",StorageLocationMeta.getName()));
		
		//location required
		if(storageLocationDO.getLocation() == null || "".equals(storageLocationDO.getLocation())){
		    list.add(new FieldErrorException("fieldRequiredException",StorageLocationMeta.getLocation()));
		}
		
		//storage unit required
		if(storageLocationDO.getStorageUnitId() == null || "".equals(storageLocationDO.getStorageUnitId())){
		    list.add(new FieldErrorException("fieldRequiredException",StorageLocationMeta.STORAGE_UNIT_META.getDescription()));
		}
		
		for(int i=0; i<childLocs.size();i++)         
            validateChildStorageLocation((StorageLocationViewDO)childLocs.get(i), i, list);
        
		if(list.size() > 0)
            throw list;
	}
	
	private void validateChildStorageLocation(StorageLocationViewDO storageLocationDO, int rowIndex, ValidationErrorsList exceptionList){
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
