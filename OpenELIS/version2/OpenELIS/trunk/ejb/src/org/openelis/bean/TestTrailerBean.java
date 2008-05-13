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
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.annotation.security.SecurityDomain;
import org.openelis.domain.TestTrailerDO;
import org.openelis.entity.TestTrailer;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.FormErrorException;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.RPCException;
import org.openelis.local.LockLocal;
import org.openelis.meta.TestTrailerMeta;
import org.openelis.remote.TestTrailerRemote;
import org.openelis.util.QueryBuilder;
import org.openelis.utils.GetPage;

import edu.uiowa.uhl.security.domain.SystemUserDO;
import edu.uiowa.uhl.security.local.SystemUserUtilLocal;

@Stateless
@SecurityDomain("openelis")
@RolesAllowed("testtrailer-select")
public class TestTrailerBean implements TestTrailerRemote{

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
    
    @RolesAllowed("testtrailer-delete")
	public void deleteTestTrailer(Integer testTrailerId) throws Exception {
    	Query lockQuery = manager.createNamedQuery("getTableId");
		lockQuery.setParameter("name", "test_trailer");
		Integer testTrailerTableId = (Integer)lockQuery.getSingleResult();
        lockBean.getLock(testTrailerTableId, testTrailerId);
        
		manager.setFlushMode(FlushModeType.COMMIT);
		TestTrailer testTrailer = null;
		
		//validate the test trailer record
        List exceptionList = new ArrayList();
        exceptionList = validateForDelete(testTrailerId);
        if(exceptionList.size() > 0){
        	throw (RPCException)exceptionList.get(0);
        }
        
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

	public TestTrailerDO getTestTrailer(Integer testTrailerId) {
		Query query = manager.createNamedQuery("getTestTrailer");
		query.setParameter("id", testTrailerId);
		TestTrailerDO testTrailerRecord = (TestTrailerDO) query.getResultList().get(0);// getting first storage unit record

        return testTrailerRecord;
	}

    @RolesAllowed("testtrailer-update")
	public TestTrailerDO getTestTrailerAndLock(Integer testTrailerId) throws Exception {
		Query query = manager.createNamedQuery("getTableId");
        query.setParameter("name", "test_trailer");
        lockBean.getLock((Integer)query.getSingleResult(),testTrailerId);
        
        return getTestTrailer(testTrailerId);
	}

	public TestTrailerDO getTestTrailerAndUnlock(Integer testTrailerId) {
		Query unlockQuery = manager.createNamedQuery("getTableId");
        unlockQuery.setParameter("name", "test_trailer");
        lockBean.giveUpLock((Integer)unlockQuery.getSingleResult(),testTrailerId);
		
        return getTestTrailer(testTrailerId);
	}

	public List query(HashMap fields, int first, int max) throws Exception {
		StringBuffer sb = new StringBuffer();
		QueryBuilder qb = new QueryBuilder();
		
		TestTrailerMeta testTrailerMeta = TestTrailerMeta.getInstance();
		
		qb.addMeta(testTrailerMeta);
		
		 qb.setSelect("distinct "+TestTrailerMeta.ID+", "+TestTrailerMeta.NAME);
		 qb.addTable(testTrailerMeta);
	        
//	      this method is going to throw an exception if a column doesnt match
		 qb.addWhere(fields);      

	     qb.setOrderBy(TestTrailerMeta.NAME);
        
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
            lockBean.getLock(testTrailerReferenceId, testTrailerDO.getId());
        }
        
		manager.setFlushMode(FlushModeType.COMMIT);
		TestTrailer testTrailer = null;
        
        //validate the test trailer record
        List exceptionList = new ArrayList();
        validateTestTrailer(testTrailerDO, exceptionList);
        if(exceptionList.size() > 0){
        	throw (RPCException)exceptionList.get(0);
        }
        
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

	public List validateForAdd(TestTrailerDO testTrailerDO) {
		List exceptionList = new ArrayList();
		
		validateTestTrailer(testTrailerDO, exceptionList);
		
		return exceptionList;
	}

	public List validateForDelete(Integer testTrailerId) {
		List exceptionList = new ArrayList();
		//make sure no tests are pointing to this record
		//getTestByTestTrailerId
		Query query = null;
		query = manager.createNamedQuery("getTestByTestTrailerId");
		query.setParameter("id", testTrailerId);
		List linkedRecords = query.getResultList();

		if(linkedRecords.size() > 0){
			exceptionList.add(new FormErrorException("testTrailerTestDeleteException"));
		}
		
		return exceptionList;
	}

	public List validateForUpdate(TestTrailerDO testTrailerDO) {
		List exceptionList = new ArrayList();
		
		validateTestTrailer(testTrailerDO, exceptionList);
		
		return exceptionList;
	}
	
	private void validateTestTrailer(TestTrailerDO testTrailerDO, List exceptionList){
		//name required	
		if(testTrailerDO.getName() == null || "".equals(testTrailerDO.getName())){
			exceptionList.add(new FieldErrorException("fieldRequiredException",TestTrailerMeta.NAME));
		}
		
		//name not duplicate
		//need to make sure to take update into account...old name versus new name...
		Query query = null;
		//if null then it is an add
		if(testTrailerDO.getId() == null){
			query = manager.createNamedQuery("testTrailerAddNameCompare");
			query.setParameter("name", testTrailerDO.getName());
		}else{
			query = manager.createNamedQuery("testTrailerUpdateNameCompare");
			query.setParameter("name", testTrailerDO.getName());
			query.setParameter("id",testTrailerDO.getId());
		}
		
		if(query.getResultList().size() > 0)
			exceptionList.add(new FieldErrorException("fieldUniqueException",TestTrailerMeta.NAME));
		
		
		//description required
		if(testTrailerDO.getDescription() == null || "".equals(testTrailerDO.getDescription())){
			exceptionList.add(new FieldErrorException("fieldRequiredException",TestTrailerMeta.DESCRIPTION));
		}
		
		//text required
		if(testTrailerDO.getText()== null || "".equals(testTrailerDO.getText())){
			exceptionList.add(new FieldErrorException("fieldRequiredException",TestTrailerMeta.TEXT));
		}
	}
}
