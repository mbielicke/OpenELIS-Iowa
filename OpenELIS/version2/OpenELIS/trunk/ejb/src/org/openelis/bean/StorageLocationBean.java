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
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.openelis.domain.StorageLocationDO;
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

	public List autoCompleteLookupById(Integer id) {
		Query query = null;
		query = manager.createNamedQuery("getStorageLocationAutoCompleteById");
		query.setParameter("id",id);
		return query.getResultList();
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

	public StorageLocationDO getStorageLoc(Integer StorageId, boolean unlock) {
		if(unlock){
            Query query = manager.createNamedQuery("getTableId");
            query.setParameter("name", "storage_location");
            lockBean.giveUpLock((Integer)query.getSingleResult(),StorageId);
        }
		
		Query query = manager.createNamedQuery("getStorageLocation");
		query.setParameter("id", StorageId);
		StorageLocationDO storageLocRecord = (StorageLocationDO) query.getResultList().get(0);// getting first storage location record

        return storageLocRecord;
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
        
        Query query = manager.createQuery(sb.toString()+" order by s.name");
        
//      if(first > -1)
     	// query.setFirstResult(first);

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

         List returnList = GetPage.getPage(query.getResultList(), first, max);
         
         if(returnList == null)
        	 throw new LastPageException();
         else
        	 return returnList;
	}

	public Integer updateStorageLoc(StorageLocationDO storageDO) {
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
         storageLocation.setSortOrder(storageDO.getSortOrder());
         storageLocation.setStorageUnit(storageDO.getStorageUnit());
         
         if (storageLocation.getId() == null) {
	        	manager.persist(storageLocation);
         }
         
		} catch (Exception e) {
            e.printStackTrace();
        }
            
		return storageLocation.getId();
	}

}
