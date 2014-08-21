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
import org.openelis.domain.TestTrailerDO;
import org.openelis.entity.TestTrailer;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.FormErrorException;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.gwt.common.data.AbstractField;
import org.openelis.local.LockLocal;
import org.openelis.metamap.TestTrailerMetaMap;
import org.openelis.remote.TestTrailerRemote;
import org.openelis.util.QueryBuilder;
import org.openelis.utils.GetPage;

@Stateless
@EJBs({
    @EJB(name="ejb/Lock",beanInterface=LockLocal.class)
})
@SecurityDomain("openelis")
@RolesAllowed("testtrailer-select")
public class TestTrailerBean implements TestTrailerRemote{

	@PersistenceContext(name = "openelis")
    private EntityManager manager;

	@Resource
	private SessionContext ctx;
	
    private LockLocal lockBean;
    private static final TestTrailerMetaMap TestTrailerMap = new TestTrailerMetaMap();

    @PostConstruct
    private void init()
    {
        lockBean =  (LockLocal)ctx.lookup("ejb/Lock");
    }
    
    @RolesAllowed("testtrailer-delete")
	public void deleteTestTrailer(Integer testTrailerId) throws Exception {
    	Query lockQuery = manager.createNamedQuery("getTableId");
		lockQuery.setParameter("name", "test_trailer");
		Integer testTrailerTableId = (Integer)lockQuery.getSingleResult();
        lockBean.validateLock(testTrailerTableId, testTrailerId);
        
        validateForDelete(testTrailerId);
        
		manager.setFlushMode(FlushModeType.COMMIT);
		TestTrailer testTrailer = null;
		
		//then we need to delete it
		try {
			testTrailer = manager.find(TestTrailer.class, testTrailerId);
            	if(testTrailer != null)
            		manager.remove(testTrailer);
            	
		} catch (Exception e) {
            e.printStackTrace();
        }	
		
		lockBean.giveUpLock(testTrailerTableId, testTrailerId);
    }

	public TestTrailerDO getTestTrailer(Integer testTrailerId) {
		Query query = manager.createNamedQuery("TestTrailer.TestTrailer");
		query.setParameter("id", testTrailerId);
		TestTrailerDO testTrailerRecord = (TestTrailerDO) query.getResultList().get(0);// getting first storage unit record

        return testTrailerRecord;
	}

    @RolesAllowed("testtrailer-update")
	public TestTrailerDO getTestTrailerAndLock(Integer testTrailerId, String session) throws Exception {
		Query query = manager.createNamedQuery("getTableId");
        query.setParameter("name", "test_trailer");
        lockBean.getLock((Integer)query.getSingleResult(),testTrailerId);
        
        return getTestTrailer(testTrailerId);
	}

	public TestTrailerDO getTestTrailerAndUnlock(Integer testTrailerId, String session) {
		Query unlockQuery = manager.createNamedQuery("getTableId");
        unlockQuery.setParameter("name", "test_trailer");
        lockBean.giveUpLock((Integer)unlockQuery.getSingleResult(),testTrailerId);
		
        return getTestTrailer(testTrailerId);
	}

	public List query(ArrayList<AbstractField> fields, int first, int max) throws Exception {
		StringBuffer sb = new StringBuffer();
		QueryBuilder qb = new QueryBuilder();
		
		qb.setMeta(TestTrailerMap);
		
		 qb.setSelect("distinct new org.openelis.domain.IdNameDO("+TestTrailerMap.getId()+", "+TestTrailerMap.getName() + ") ");
	        
//	      this method is going to throw an exception if a column doesnt match
		 qb.addWhere(fields);      

	     qb.setOrderBy(TestTrailerMap.getName());
        
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

   @RolesAllowed("testtrailer-update")
	public Integer updateTestTrailer(TestTrailerDO testTrailerDO) throws Exception{
        Query query = manager.createNamedQuery("getTableId");
        query.setParameter("name", "test_trailer");
        Integer testTrailerReferenceId = (Integer)query.getSingleResult();
        
        if(testTrailerDO.getId() != null){
            lockBean.validateLock(testTrailerReferenceId, testTrailerDO.getId());
        }
        
        validateTestTrailer(testTrailerDO);
        
		manager.setFlushMode(FlushModeType.COMMIT);
		TestTrailer testTrailer = null;
        
        if (testTrailerDO.getId() == null)
        	testTrailer = new TestTrailer();
        else
        	testTrailer = manager.find(TestTrailer.class, testTrailerDO.getId());
        
        testTrailer.setDescription(testTrailerDO.getDescription());
        testTrailer.setName(testTrailerDO.getName());
        testTrailer.setText(testTrailerDO.getText());
        
        if (testTrailer.getId() == null) {
        	manager.persist(testTrailer);
        }
     
        lockBean.giveUpLock(testTrailerReferenceId,testTrailer.getId()); 
		    
		return testTrailer.getId();
	}

	public void validateForDelete(Integer testTrailerId) throws Exception {
	    ValidationErrorsList list = new ValidationErrorsList();
	    
		//make sure no tests are pointing to this record
		//getTestByTestTrailerId
		Query query = null;
		query = manager.createNamedQuery("Test.IdByTestTrailer");
		query.setParameter("id", testTrailerId);
		List linkedRecords = query.getResultList();

		if(linkedRecords.size() > 0){
		    list.add(new FormErrorException("testTrailerTestDeleteException"));
		}
		
		if(list.size() > 0)
            throw list;
	}
	
	private void validateTestTrailer(TestTrailerDO testTrailerDO) throws Exception {
	    ValidationErrorsList list = new ValidationErrorsList();
	    
		//name required	
		if(testTrailerDO.getName() == null || "".equals(testTrailerDO.getName())){
		    list.add(new FieldErrorException("fieldRequiredException",TestTrailerMap.getName()));
		}
		
		//name not duplicate
		//need to make sure to take update into account...old name versus new name...
		Query query = null;
		//if null then it is an add
		if(testTrailerDO.getId() == null){
			query = manager.createNamedQuery("TestTrailer.AddNameCompare");
			query.setParameter("name", testTrailerDO.getName());
		}else{
			query = manager.createNamedQuery("TestTrailer.UpdateNameCompare");
			query.setParameter("name", testTrailerDO.getName());
			query.setParameter("id",testTrailerDO.getId());
		}
		
		if(query.getResultList().size() > 0)
		    list.add(new FieldErrorException("fieldUniqueException",TestTrailerMap.getName()));
		
		
		//description required
		if(testTrailerDO.getDescription() == null || "".equals(testTrailerDO.getDescription())){
		    list.add(new FieldErrorException("fieldRequiredException",TestTrailerMap.getDescription()));
		}
		
		//text required
		if(testTrailerDO.getText()== null || "".equals(testTrailerDO.getText())){
		    list.add(new FieldErrorException("fieldRequiredException",TestTrailerMap.getText()));
		}
		
		if(list.size() > 0)
            throw list;
	}
}
