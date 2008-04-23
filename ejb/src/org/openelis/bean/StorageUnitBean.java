package org.openelis.bean;

import java.util.ArrayList;
import java.util.HashMap;
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
import org.openelis.domain.StorageUnitDO;
import org.openelis.entity.StorageUnit;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.FormErrorException;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.RPCException;
import org.openelis.local.LockLocal;
import org.openelis.meta.AnalyteMeta;
import org.openelis.meta.StorageUnitMeta;
import org.openelis.remote.StorageUnitRemote;
import org.openelis.util.QueryBuilder;
import org.openelis.utils.GetPage;

import edu.uiowa.uhl.security.domain.SystemUserDO;
import edu.uiowa.uhl.security.local.SystemUserUtilLocal;

@Stateless
@SecurityDomain("openelis")
@RolesAllowed("storageunit-select")
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
		QueryBuilder qb = new QueryBuilder();
		
		StorageUnitMeta storageUnitMeta = StorageUnitMeta.getInstance();
		
		qb.addMeta(storageUnitMeta);
		
		 qb.setSelect("distinct "+storageUnitMeta.ID+", "+storageUnitMeta.DESCRIPTION);
		 qb.addTable(storageUnitMeta);
	        
//	      this method is going to throw an exception if a column doesnt match
		 qb.addWhere(fields);      

	     qb.setOrderBy(storageUnitMeta.DESCRIPTION);
        
	     sb.append(qb.getEJBQL());

         Query query = manager.createQuery(sb.toString());
        
         if(first > -1 && max > -1)
        	 query.setMaxResults(first+max);
         
//       ***set the parameters in the query
         qb.setQueryParams(query);
         
         List returnList = GetPage.getPage(query.getResultList(), first, max);
         
         if(returnList == null)
        	 throw new LastPageException();
         else
        	 return returnList;
	}

    @RolesAllowed("storageunit-update")
	public Integer updateStorageUnit(StorageUnitDO unitDO) throws Exception{
		manager.setFlushMode(FlushModeType.COMMIT);
		StorageUnit storageUnit = null;
		
		//validate the analyte record
        List exceptionList = new ArrayList();
        validateStorageUnit(unitDO, exceptionList);
        if(exceptionList.size() > 0){
        	throw (RPCException)exceptionList.get(0);
        }
        
        //storage unit reference table id
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
		    
		return storageUnit.getId();
	}

	public StorageUnitDO getStorageUnit(Integer StorageUnitId) {
		Query query = manager.createNamedQuery("getStorageUnit");
		query.setParameter("id", StorageUnitId);
		StorageUnitDO storageUnitRecord = (StorageUnitDO) query.getResultList().get(0);// getting first storage unit record

        return storageUnitRecord;
	}
	
    @RolesAllowed("storageunit-update")
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

    @RolesAllowed("storageunit-delete")
	public void deleteStorageUnit(Integer storageUnitId) throws Exception {
    	Query lockQuery = manager.createNamedQuery("getTableId");
		lockQuery.setParameter("name", "storage_unit");
		Integer storageUnitTableId = (Integer)lockQuery.getSingleResult();
        lockBean.getLock(storageUnitTableId, storageUnitId);
        
		manager.setFlushMode(FlushModeType.COMMIT);
		StorageUnit storageUnit = null;
		
		//validate the storage unit record
        List exceptionList = new ArrayList();
        exceptionList = validateForDelete(storageUnitId);
        if(exceptionList.size() > 0){
        	throw (RPCException)exceptionList.get(0);
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
		
		lockBean.giveUpLock(storageUnitTableId, storageUnitId);
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

	public List validateForAdd(StorageUnitDO storageUnitDO) {
		List exceptionList = new ArrayList();
		
		validateStorageUnit(storageUnitDO, exceptionList);
		
		return exceptionList;
	}

	public List validateForDelete(Integer storageUnitId) {
		List exceptionList = new ArrayList();
		//make sure no analytes are pointing to this record
		Query query = null;
		query = manager.createNamedQuery("getStorageLocationByStorageUnitId");
		query.setParameter("id", storageUnitId);
		List linkedRecords = query.getResultList();

		if(linkedRecords.size() > 0){
			exceptionList.add(new FormErrorException("storageUnitDeleteException"));
		}
		
		return exceptionList;
	}

	public List validateForUpdate(StorageUnitDO storageUnitDO) {
		List exceptionList = new ArrayList();
		
		validateStorageUnit(storageUnitDO, exceptionList);
		
		return exceptionList;
	}	 
	
	private void validateStorageUnit(StorageUnitDO storageUnitDO, List exceptionList){
		//category required
		if(storageUnitDO.getCategory() == null || "".equals(storageUnitDO.getCategory())){
			exceptionList.add(new FieldErrorException("fieldRequiredException",StorageUnitMeta.CATEGORY));
		}
		
		//description required
		if(storageUnitDO.getDescription() == null || "".equals(storageUnitDO.getDescription())){
			exceptionList.add(new FieldErrorException("fieldRequiredException",StorageUnitMeta.DESCRIPTION));
		}
		
	}
}
