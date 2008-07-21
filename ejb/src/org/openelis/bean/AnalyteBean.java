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
import org.openelis.domain.AnalyteDO;
import org.openelis.entity.Analyte;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.FormErrorException;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.RPCException;
import org.openelis.local.LockLocal;
import org.openelis.metamap.AnalyteMetaMap;
import org.openelis.remote.AnalyteRemote;
import org.openelis.security.domain.SystemUserDO;
import org.openelis.security.local.SystemUserUtilLocal;
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
    @EJB(name="ejb/Lock",beanInterface=LockLocal.class),
    @EJB(name="ejb/SystemUser",beanInterface=SystemUserUtilLocal.class)
})
@SecurityDomain("openelis")
@RolesAllowed("analyte-select")
public class AnalyteBean implements AnalyteRemote{

	@PersistenceContext(name = "openelis")
    private EntityManager manager;
   
	private SystemUserUtilLocal sysUser;
	
	@Resource
	private SessionContext ctx;
    
    private static final AnalyteMetaMap Meta = new AnalyteMetaMap();
	
    private LockLocal lockBean;
   
    @PostConstruct
    private void init()
    {
        lockBean =  (LockLocal)ctx.lookup("ejb/Lock");
        sysUser = (SystemUserUtilLocal)ctx.lookup("ejb/SystemUser");
    }
    
	public List autoCompleteLookupByName(String name, int maxResults) {
		Query query = null;
		query = manager.createNamedQuery("Analyte.AutoCompleteByName");
		query.setParameter("name",name);
		query.setMaxResults(maxResults);
		return query.getResultList();
	}

    @RolesAllowed("analyte-delete")
	public void deleteAnalyte(Integer analyteId) throws Exception {
		Query lockQuery = manager.createNamedQuery("getTableId");
		lockQuery.setParameter("name", "analyte");
		Integer analyteTableId = (Integer)lockQuery.getSingleResult();
        lockBean.getLock(analyteTableId, analyteId);
        
		manager.setFlushMode(FlushModeType.COMMIT);
		Analyte analyte = null;
		
//		validate the analyte record
        List exceptionList = new ArrayList();
        exceptionList = validateForDelete(analyteId);
        if(exceptionList.size() > 0){
        	throw (RPCException)exceptionList.get(0);
        }
        
		//then we need to delete it
		try {
			analyte = manager.find(Analyte.class, analyteId);
            	if(analyte != null)
            		manager.remove(analyte);
            	
		} catch (Exception e) {
            e.printStackTrace();
        }
		
		lockBean.giveUpLock(analyteTableId, analyteId);
	}

	public AnalyteDO getAnalyte(Integer analyteId) {
		Query query = manager.createNamedQuery("Analyte.Analyte");
		query.setParameter("id", analyteId);
		AnalyteDO analyteRecord = (AnalyteDO) query.getResultList().get(0);// getting first analyte record

        return analyteRecord;
	}

   @RolesAllowed("analyte-update")
	public AnalyteDO getAnalyteAndLock(Integer analyteId, String session) throws Exception {
		Query query = manager.createNamedQuery("getTableId");
        query.setParameter("name", "analyte");
        lockBean.getLock((Integer)query.getSingleResult(),analyteId);
        
        return getAnalyte(analyteId);
	}

	public AnalyteDO getAnalyteAndUnlock(Integer analyteId, String session) {
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
		
		qb.setMeta(Meta);
		
		qb.setSelect("distinct new org.openelis.domain.IdNameDO("+Meta.getId()+", "+Meta.getName() + ") ");
	        
//	      this method is going to throw an exception if a column doesnt match
		 qb.addWhere(fields);      

	     qb.setOrderBy(Meta.getName());
        
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

    @RolesAllowed("analyte-update")
	public Integer updateAnalyte(AnalyteDO analyteDO) throws Exception{
        Query query = manager.createNamedQuery("getTableId");
        query.setParameter("name", "analyte");
        Integer analyteReferenceId = (Integer)query.getSingleResult();
        
        if(analyteDO.getId() != null){
            lockBean.giveUpLock(analyteReferenceId, analyteDO.getId());
        }
        
		manager.setFlushMode(FlushModeType.COMMIT);
		Analyte analyte = null;
        
        //validate the analyte record
        List exceptionList = new ArrayList();
        validateAnalyte(analyteDO, exceptionList);
        if(exceptionList.size() > 0){
        	throw (RPCException)exceptionList.get(0);
        }
        
        if (analyteDO.getId() == null)
         	analyte = new Analyte();
        else
          	analyte = manager.find(Analyte.class, analyteDO.getId());
            
        analyte.setAnalyteGroupId(analyteDO.getAnalyteGroup());
        analyte.setExternalId(analyteDO.getExternalId());
        analyte.setIsActive(analyteDO.getIsActive());
        analyte.setName(analyteDO.getName());
        analyte.setParentAnalyteId(analyteDO.getParentAnalyteId());
         
        if (analyte.getId() == null) {
	      	manager.persist(analyte);
        }
         
        lockBean.giveUpLock(analyteReferenceId,analyte.getId()); 
		    
		return analyte.getId();
	}
    
	public List validateForAdd(AnalyteDO analyteDO) {
		List exceptionList = new ArrayList();
		
		validateAnalyte(analyteDO, exceptionList);
		
		return exceptionList;
	}

	public List validateForDelete(Integer analyteId) {
		List exceptionList = new ArrayList();
		//make sure no analytes are pointing to this record
		Query query = null;
		query = manager.createNamedQuery("Analyte.AnalyteByParentId");
		query.setParameter("id", analyteId);
		List linkedRecords = query.getResultList();

		if(linkedRecords.size() > 0){
			exceptionList.add(new FormErrorException("analyteDeleteException"));
		}
		
		//make sure no results are pointing to this record
		query = manager.createNamedQuery("Result.ResultByAnalyteId");
		query.setParameter("id", analyteId);
		linkedRecords = query.getResultList();

		if(linkedRecords.size() > 0){
			exceptionList.add(new FormErrorException("analyteResultDeleteException"));
		}
		
		//make sure no tests are pointing to this record
		query = manager.createNamedQuery("TestAnalyte.TestAnalyteByAnalyteId");
		query.setParameter("id", analyteId);
		linkedRecords = query.getResultList();

		if(linkedRecords.size() > 0){
			exceptionList.add(new FormErrorException("analyteTestDeleteException"));
		}
		
		//make sure no methods are pointing to this record
		query = manager.createNamedQuery("MethodAnalyte.MethodAnalyteByAnalyteId");
		query.setParameter("id", analyteId);
		linkedRecords = query.getResultList();

		if(linkedRecords.size() > 0){
			exceptionList.add(new FormErrorException("analyteMethodDeleteException"));
		}
		
		//make sure no qcs are pointing to this record
		query = manager.createNamedQuery("QCAnalyte.QCAnalyteByAnalyteId");
		query.setParameter("id", analyteId);
		linkedRecords = query.getResultList();

		if(linkedRecords.size() > 0){
			exceptionList.add(new FormErrorException("analyteQCDeleteException"));
		}
		
		//make sure no worksheets are pointing to this record
		//FIXME table doesnt exist currently so this will have to be added later
		
		//make sure no aux fields are pointing to this record
		query = manager.createNamedQuery("AuxField.AuxFieldByAnalyteId");
		query.setParameter("id", analyteId);
		linkedRecords = query.getResultList();

		if(linkedRecords.size() > 0){
			exceptionList.add(new FormErrorException("analyteAuxFieldDeleteException"));
		}
		
		return exceptionList;
	}

	public List validateForUpdate(AnalyteDO analyteDO) {
		List exceptionList = new ArrayList();
		
		validateAnalyte(analyteDO, exceptionList);
		
		return exceptionList;
	}
	
	private void validateAnalyte(AnalyteDO analyteDO, List exceptionList){
		//name required	
		if(analyteDO.getName() == null || "".equals(analyteDO.getName())){
			exceptionList.add(new FieldErrorException("fieldRequiredException",Meta.getName()));
		}
		
		//name not duplicate
		//need to make sure to take update into account...
		Query query = null;
		//its an add if its null
		if(analyteDO.getId() == null){
			query = manager.createNamedQuery("Analyte.AddNameCompare");
			query.setParameter("name", analyteDO.getName());
		}else{
			query = manager.createNamedQuery("Analyte.UpdateNameCompare");
			query.setParameter("name", analyteDO.getName());
			query.setParameter("id",analyteDO.getId());
		}
		
		if(query.getResultList().size() > 0)
			exceptionList.add(new FieldErrorException("fieldUniqueException",Meta.getName()));
	}
}
