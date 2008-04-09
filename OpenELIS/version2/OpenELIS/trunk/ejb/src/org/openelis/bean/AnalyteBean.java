package org.openelis.bean;

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

import org.openelis.domain.AnalyteDO;
import org.openelis.entity.Analyte;
import org.openelis.gwt.common.LastPageException;
import org.openelis.local.LockLocal;
import org.openelis.meta.AnalyteMeta;
import org.openelis.meta.AnalyteParentAnalyteMeta;
import org.openelis.remote.AnalyteRemote;
import org.openelis.util.Meta;
import org.openelis.util.QueryBuilder;
import org.openelis.utils.GetPage;

import edu.uiowa.uhl.security.domain.SystemUserDO;
import edu.uiowa.uhl.security.local.SystemUserUtilLocal;

@Stateless
public class AnalyteBean implements AnalyteRemote{

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
		query = manager.createNamedQuery("getAnalyteAutoCompleteByName");
		query.setParameter("name",name);
		query.setMaxResults(maxResults);
		return query.getResultList();
	}

	public void deleteAnalyte(Integer analyteId) throws Exception {
		/*manager.setFlushMode(FlushModeType.COMMIT);
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
        }*/		
	}

	public AnalyteDO getAnalyte(Integer analyteId) {
		Query query = manager.createNamedQuery("getAnalyte");
		query.setParameter("id", analyteId);
		AnalyteDO analyteRecord = (AnalyteDO) query.getResultList().get(0);// getting first analyte record

        return analyteRecord;
	}

	public AnalyteDO getAnalyteAndLock(Integer analyteId) throws Exception {
		Query query = manager.createNamedQuery("getTableId");
        query.setParameter("name", "analyte");
        lockBean.getLock((Integer)query.getSingleResult(),analyteId);
        
        return getAnalyte(analyteId);
	}

	public AnalyteDO getAnalyteAndUnlock(Integer analyteId) {
		Query unlockQuery = manager.createNamedQuery("getTableId");
        unlockQuery.setParameter("name", "analyte");
        lockBean.giveUpLock((Integer)unlockQuery.getSingleResult(),analyteId);
		
        return getAnalyte(analyteId);
	}

	public Integer getSystemUserId() {
		 try {
	            SystemUserDO systemUserDO = sysUser.getSystemUser(ctx.getCallerPrincipal()
	                                                                 .getName());
	            return systemUserDO.getId();
	        } catch (Exception e) {
	            e.printStackTrace();
	            return null;
	        }    
	}

	public List query(HashMap fields, int first, int max) throws Exception {
		StringBuffer sb = new StringBuffer();
		QueryBuilder qb = new QueryBuilder();
		
		AnalyteMeta analyteMeta = AnalyteMeta.getInstance();
		AnalyteParentAnalyteMeta parentMeta = AnalyteParentAnalyteMeta.getInstance();
		
		qb.addMeta(new Meta[]{analyteMeta,parentMeta});
		
		 qb.setSelect("distinct "+AnalyteMeta.ID+", "+AnalyteMeta.NAME);
		 qb.addTable(analyteMeta);
	        
//	      this method is going to throw an exception if a column doesnt match
		 qb.addWhere(fields);      

	     qb.setOrderBy(AnalyteMeta.NAME);
        
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

	public Integer updateAnalyte(AnalyteDO analyteDO) {
		manager.setFlushMode(FlushModeType.COMMIT);
		Analyte analyte = null;
		
		try {
//			analyte reference table id
        	Query query = manager.createNamedQuery("getTableId");
            query.setParameter("name", "analyte");
            Integer analyteReferenceId = (Integer)query.getSingleResult();
            
            if (analyteDO.getId() == null)
            	analyte = new Analyte();
            else
            	analyte = manager.find(Analyte.class, analyteDO.getId());
            
            analyte.setAnalyteGroup(analyteDO.getAnalyteGroup());
            analyte.setExternalId(analyteDO.getExternalId());
            analyte.setIsActive(analyteDO.getIsActive());
            analyte.setName(analyteDO.getName());
            analyte.setParentAnalyteId(analyteDO.getParentAnalyteId());
         
            if (analyte.getId() == null) {
	        	manager.persist(analyte);
            }
         
            lockBean.giveUpLock(analyteReferenceId,analyte.getId()); 
		} catch (Exception e) {
            e.printStackTrace();
        }
            
		return analyte.getId();
	}

	public List autoCompleteLookupById(Integer id) {
		Query query = null;
		query = manager.createNamedQuery("getAnalyteAutoCompleteById");
		query.setParameter("id",id);
		return query.getResultList();
	}
}
