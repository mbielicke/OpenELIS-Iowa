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

import org.openelis.domain.OrganizationContactDO;
import org.openelis.domain.StorageLocationDO;
import org.openelis.entity.OrganizationContact;
import org.openelis.entity.StorageLocation;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.RPCDeleteException;
import org.openelis.gwt.common.data.CollectionField;
import org.openelis.gwt.common.data.QueryNumberField;
import org.openelis.gwt.common.data.QueryStringField;
import org.openelis.local.LockLocal;
import org.openelis.remote.StorageLocationRemote;
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
        
        sb.append("select distinct s.id,s.name " + "from StorageLocation s where 1=1 ");
        
        //***append the abstract fields to the string buffer
        if(fields.containsKey("id"))
        	sb.append(QueryBuilder.getQuery((QueryNumberField)fields.get("id"), "s.id"));
        if(fields.containsKey("name"))
        	sb.append(QueryBuilder.getQuery((QueryStringField)fields.get("name"), "s.name"));
        if(fields.containsKey("location"))
        	sb.append(QueryBuilder.getQuery((QueryStringField)fields.get("location"), "s.location"));
        if(fields.containsKey("parentStorage"))
        	sb.append(QueryBuilder.getQuery((QueryStringField)fields.get("parentStorage"), "s.parentStorageLocationName.name"));
        if(fields.containsKey("storageUnit"))
        	sb.append(QueryBuilder.getQuery((QueryStringField)fields.get("storageUnit"), "s.storageUnitName.description"));
        if(fields.containsKey("sortOrder") && ((ArrayList)((CollectionField)fields.get("sortOrder")).getValue()).size()>0 &&
        	!(((ArrayList)((CollectionField)fields.get("sortOrder")).getValue()).size() == 1 && "".equals(((ArrayList)((CollectionField)fields.get("sortOrder")).getValue()).get(0))))
        	sb.append(QueryBuilder.getQuery((CollectionField)fields.get("sortOrder"), "s.sortOrder"));
        if(fields.containsKey("isAvailable") && ((ArrayList)((CollectionField)fields.get("isAvailable")).getValue()).size()>0 &&
        	!(((ArrayList)((CollectionField)fields.get("isAvailable")).getValue()).size() == 1 && "".equals(((ArrayList)((CollectionField)fields.get("isAvailable")).getValue()).get(0))))
        	sb.append(QueryBuilder.getQuery((CollectionField)fields.get("isAvailable"), "s.isAvailable"));
        
        //child fields
  /*      if(fields.containsKey("childName"))
        	sb.append(QueryBuilder.getQuery((QueryStringField)fields.get("childName"), ""));
        if(fields.containsKey("childLocation"))
        	sb.append(QueryBuilder.getQuery((QueryStringField)fields.get("childLocation"), ""));
        if(fields.containsKey("childStorageUnit"))
        	sb.append(QueryBuilder.getQuery((QueryStringField)fields.get("childStorageUnit"), ""));
        if(fields.containsKey("childIsAvailable") && ((ArrayList)((CollectionField)fields.get("childIsAvailable")).getValue()).size()>0 &&
    	!(((ArrayList)((CollectionField)fields.get("childIsAvailable")).getValue()).size() == 1 && "".equals(((ArrayList)((CollectionField)fields.get("childIsAvailable")).getValue()).get(0))))
    		sb.append(QueryBuilder.getQuery((CollectionField)fields.get("isAvailable"), ""));   */     	
        	
        Query query = manager.createQuery(sb.toString()+" order by s.name");
        
         if(first > -1 && max > -1)
        	 query.setMaxResults(first+max);
         
//       ***set the parameters in the query
         if(fields.containsKey("id"))
        	 QueryBuilder.setParameters((QueryNumberField)fields.get("id"), "s.id", query);
         if(fields.containsKey("name"))
        	 QueryBuilder.setParameters((QueryStringField)fields.get("name"), "s.name", query);
         if(fields.containsKey("location"))
        	 QueryBuilder.setParameters((QueryStringField)fields.get("location"), "s.location", query);
         if(fields.containsKey("parentStorage"))
        	 QueryBuilder.setParameters((QueryStringField)fields.get("parentStorage"), "s.parentStorageLocationName.name", query);
         if(fields.containsKey("storageUnit"))
        	 QueryBuilder.setParameters((QueryStringField)fields.get("storageUnit"), "s.storageUnitName.description", query);
         if(fields.containsKey("sortOrder") && ((ArrayList)((CollectionField)fields.get("sortOrder")).getValue()).size()>0 &&
         	!(((ArrayList)((CollectionField)fields.get("sortOrder")).getValue()).size() == 1 && "".equals(((ArrayList)((CollectionField)fields.get("sortOrder")).getValue()).get(0))))
        	 QueryBuilder.setParameters((CollectionField)fields.get("sortOrder"), "s.sortOrder", query);
         if(fields.containsKey("isAvailable") && ((ArrayList)((CollectionField)fields.get("isAvailable")).getValue()).size()>0 &&
         	!(((ArrayList)((CollectionField)fields.get("isAvailable")).getValue()).size() == 1 && "".equals(((ArrayList)((CollectionField)fields.get("isAvailable")).getValue()).get(0))))
        	 QueryBuilder.setParameters((CollectionField)fields.get("isAvailable"), "s.isAvailable", query);

         //child fields
         /*if(fields.containsKey("childName"))
        	 QueryBuilder.setParameters((QueryStringField)fields.get("childName"), "", query);
         if(fields.containsKey("childLocation"))
        	 QueryBuilder.setParameters((QueryStringField)fields.get("childLocation"), "", query);
         if(fields.containsKey("childStorageUnit"))
        	 QueryBuilder.setParameters((QueryStringField)fields.get("childStorageUnit"), "", query);
         if(fields.containsKey("childIsAvailable") && ((ArrayList)((CollectionField)fields.get("childIsAvailable")).getValue()).size()>0 &&
     	!(((ArrayList)((CollectionField)fields.get("childIsAvailable")).getValue()).size() == 1 && "".equals(((ArrayList)((CollectionField)fields.get("childIsAvailable")).getValue()).get(0))))
        	 QueryBuilder.setParameters((CollectionField)fields.get("childIsAvailable"), "", query);*/
         
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
         storageLocation.setParentStorageLocation(storageDO.getParentStorageLocation());
         //FIXME this may need to change....
         storageLocation.setSortOrder(0);
         storageLocation.setStorageUnit(storageDO.getStorageUnit());
         
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
	            childStorageLoc.setParentStorageLocation(storageLocation.getId());
	            childStorageLoc.setStorageUnit(childDO.getStorageUnit());
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
