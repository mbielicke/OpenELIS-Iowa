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

import org.openelis.domain.TestTrailerDO;
import org.openelis.entity.TestTrailer;
import org.openelis.gwt.common.LastPageException;
import org.openelis.local.LockLocal;
import org.openelis.meta.TestTrailerMeta;
import org.openelis.remote.TestTrailerRemote;
import org.openelis.util.QueryBuilder;
import org.openelis.utils.GetPage;

import edu.uiowa.uhl.security.domain.SystemUserDO;
import edu.uiowa.uhl.security.local.SystemUserUtilLocal;

@Stateless
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
    
	public void deleteTestTrailer(Integer testTrailerId) throws Exception {
		manager.setFlushMode(FlushModeType.COMMIT);
		TestTrailer testTrailer = null;
		
		//we need to see if this item can be deleted first
		//FIXME we need to code this when the parent screens are coded
		/*Query query = null;
		query = manager.createNamedQuery("getStorageLocationByStorageUnitId");
		query.setParameter("id", storageUnitId);
		List linkedRecords = query.getResultList();
		
		if(linkedRecords.size() > 0){
			throw new RPCDeleteException();
		}*/
		//then we need to delete it
		try {
			testTrailer = manager.find(TestTrailer.class, testTrailerId);
            	if(testTrailer != null)
            		manager.remove(testTrailer);
            	
		} catch (Exception e) {
            e.printStackTrace();
        }	
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

	public Integer updateTestTrailer(TestTrailerDO testTrailerDO) {
		manager.setFlushMode(FlushModeType.COMMIT);
		TestTrailer testTrailer = null;
		
		try {
//			test trailer reference table id
        	Query query = manager.createNamedQuery("getTableId");
            query.setParameter("name", "test_trailer");
            Integer testTrailerReferenceId = (Integer)query.getSingleResult();
            
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
		} catch (Exception e) {
            e.printStackTrace();
        }
            
		return testTrailer.getId();
	}
}
