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

import org.jboss.annotation.security.SecurityDomain;
import org.openelis.domain.StorageUnitDO;
import org.openelis.entity.StorageUnit;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.FormErrorException;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.RPCException;
import org.openelis.local.LockLocal;
import org.openelis.metamap.StorageUnitMetaMap;
import org.openelis.persistence.CachingManager;
import org.openelis.remote.StorageUnitRemote;
import org.openelis.security.domain.SystemUserDO;
import org.openelis.util.QueryBuilder;
import org.openelis.utils.GetPage;

import java.util.ArrayList;
import java.util.HashMap;
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
@RolesAllowed("storageunit-select")
public class StorageUnitBean implements StorageUnitRemote{

	@PersistenceContext(name = "openelis")
    private EntityManager manager;
    //private String className = this.getClass().getName();
   // private Logger log = Logger.getLogger(className);
	
	
	@Resource
	private SessionContext ctx;
	
    private LockLocal lockBean;
    private static final StorageUnitMetaMap StorageUnitMeta = new StorageUnitMetaMap();
    
    @PostConstruct
    private void init()
    {
        lockBean =  (LockLocal)ctx.lookup("ejb/Lock");
    }
    
	public List query(HashMap fields, int first, int max) throws Exception {
		StringBuffer sb = new StringBuffer();
		QueryBuilder qb = new QueryBuilder();
		
		qb.setMeta(StorageUnitMeta);
		
		 qb.setSelect("distinct new org.openelis.domain.IdNameDO("+StorageUnitMeta.getId()+", "+StorageUnitMeta.getDescription() + ") ");
	        
//	      this method is going to throw an exception if a column doesnt match
		 qb.addWhere(fields);      

	     qb.setOrderBy(StorageUnitMeta.getDescription());
        
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
        Query query = manager.createNamedQuery("getTableId");
        query.setParameter("name", "storage_unit");
        Integer storageUnitReferenceId = (Integer)query.getSingleResult();
        
        if(unitDO.getId() != null){
            lockBean.getLock(storageUnitReferenceId,unitDO.getId());
        }
        
		manager.setFlushMode(FlushModeType.COMMIT);
		StorageUnit storageUnit = null;
		
		//validate the analyte record
        List exceptionList = new ArrayList();
        validateStorageUnit(unitDO, exceptionList);
        if(exceptionList.size() > 0){
        	throw (RPCException)exceptionList.get(0);
        }
        
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
		Query query = manager.createNamedQuery("StorageUnit.StorageUnit");
		query.setParameter("id", StorageUnitId);
		StorageUnitDO storageUnitRecord = (StorageUnitDO) query.getResultList().get(0);// getting first storage unit record

        return storageUnitRecord;
	}
	
    @RolesAllowed("storageunit-update")
	public StorageUnitDO getStorageUnitAndLock(Integer StorageUnitId, String session) throws Exception{
		Query query = manager.createNamedQuery("getTableId");
        query.setParameter("name", "storage_unit");
        lockBean.getLock((Integer)query.getSingleResult(),StorageUnitId);
        
        return getStorageUnit(StorageUnitId);
		
	}
	
	public StorageUnitDO getStorageUnitAndUnlock(Integer StorageUnitId, String session) {
        Query unlockQuery = manager.createNamedQuery("getTableId");
        unlockQuery.setParameter("name", "storage_unit");
        lockBean.giveUpLock((Integer)unlockQuery.getSingleResult(),StorageUnitId);
		
        return getStorageUnit(StorageUnitId);
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
		query = manager.createNamedQuery("StorageUnit.AutoCompleteByDesc");
		query.setParameter("desc",desc);
		query.setMaxResults(maxResults);
		return query.getResultList();
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
		query = manager.createNamedQuery("StorageLocation.IdByStorageUnit");
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
			exceptionList.add(new FieldErrorException("fieldRequiredException",StorageUnitMeta.getCategory()));
		}
		
		//description required
		if(storageUnitDO.getDescription() == null || "".equals(storageUnitDO.getDescription())){
			exceptionList.add(new FieldErrorException("fieldRequiredException",StorageUnitMeta.getDescription()));
		}
		
	}
}
