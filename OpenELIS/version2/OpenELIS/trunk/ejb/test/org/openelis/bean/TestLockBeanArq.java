package org.openelis.bean;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.Date;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openelis.bean.LockCacheBean.Lock;
import org.openelis.bean.LockCacheBean.Lock.Key;
import org.openelis.deployment.Deployments;
import org.openelis.ui.common.EntityLockedException;

@RunWith(Arquillian.class)
public class TestLockBeanArq {

	@Inject
	LockBean lockBean;
	
	@Inject
	LockCacheBean lockCache;
	
	@Inject
	UserCacheBean userCache;
	
	@Inject
	RollbackLockBean rollback;
	
	@Inject
	UTRollbackLockBean utRollback;

	
	@Deployment
	public static WebArchive createDeployment() {
	  return Deployments.createBaseWithApplication()
		.addClasses(LockBean.class,LockCacheBean.class,UserCacheBean.class,Lock.class,RollbackLockBean.class,UTRollbackLockBean.class);
	}
	
	@Before
	public void clearLocks() {
		/*
		 * Pass negative value below default_lock_time so cache is cleared between test.
		 */
		lockCache.clearCacheByTime(-(LockBean.DEFAULT_LOCK_TIME+100000));
	}
	
	@Test
	public void testLock() throws Exception {
		lockBean.lock(1, 1);
		assertNotNull(lockCache.get(new Lock.Key(1, 1)));
	}
	
	@Test 
	public void testListLock() throws Exception {
		ArrayList<Integer> ids;
		
		ids = new ArrayList<Integer>();
		ids.add(1);
		ids.add(2);
		ids.add(3);
		lockBean.lock(1, ids);
		assertEquals(3,lockCache.getAll().size());
	}
	
	@Test(expected=EntityLockedException.class)
	public void testLockedByOther() throws Exception {
		setLock(createLock(1,3,"demo","session"));
		ArrayList<Integer> ids;
		
		ids = new ArrayList<Integer>();
		ids.add(1);
		ids.add(2);
		ids.add(3);
		lockBean.lock(1, ids);
	}
	
	@Test(expected=EntityLockedException.class)
	public void testLockedByOtherList() throws Exception {
		setLock(createLock(1,1,"demo","session"));
		lockBean.lock(1,1);
	}
	
	@Test
	public void testLockedExpiredByOtherList() throws Exception {
		setExpiredLock(1,1,"demo","session");
		ArrayList<Integer> ids;
		
		ids = new ArrayList<Integer>();
		ids.add(1);
		ids.add(2);
		ids.add(3);
		lockBean.lock(1, ids);
		assertNotNull(lockCache.get(new Lock.Key(1, 1)));
		assertEquals("system",lockCache.get(new Lock.Key(1,1)).username);
		assertEquals(3,lockCache.getAll().size());
	}
	
	@Test
	public void testLockedExpiredByOther() throws Exception {
		setExpiredLock(1,1,"demo","session");
		lockBean.lock(1,1);
		assertNotNull(lockCache.get(new Lock.Key(1, 1)));
		assertEquals("system",lockCache.get(new Lock.Key(1,1)).username);
	}
	
	@Test
	public void testValidateLock() throws Exception {
		setLock(1,1);
		lockBean.validateLock(1, 1);
	}
	
	@Test
	public void testValidateLock_expired() throws Exception {
		setExpiredLock(1,1,userCache.getSystemUser().getLoginName(),userCache.getSessionId());
		lockBean.validateLock(1,1);
		assertNotEquals(new Long(0l),new Long(lockCache.get(new Lock.Key(1,1)).expires));
	}
	
	@Test(expected=EntityLockedException.class)
	public void testValidateLock_diffSession() throws Exception {
		Lock lock = createLock(1,1,userCache.getSystemUser().getLoginName(),"session");
		setLock(lock);
		lockBean.validateLock(1, 1);
	}
	
	@Test(expected=EntityLockedException.class)
	public void testValidateLock_diffUser() throws Exception {
		Lock lock = createLock(1,1,"demo",userCache.getSessionId());
		setLock(lock);
		lockBean.validateLock(1, 1);
	}
	
	@Test
	public void testValidateLockList() throws Exception {
		ArrayList<Integer> ids;
		
		ids = new ArrayList<Integer>();
		ids.add(1);
		ids.add(2);
		ids.add(3);
		setLock(1,1);
		setLock(1,2);
		setLock(1,3);
		lockBean.validateLock(1, ids);
	}
	
	@Test
	public void testValidateLock_expiredList() throws Exception {
		ArrayList<Integer> ids;
		
		ids = new ArrayList<Integer>();
		ids.add(1);
		ids.add(2);
		ids.add(3);
		setExpiredLock(1,1,userCache.getSystemUser().getLoginName(),userCache.getSessionId());
		setExpiredLock(1,2,userCache.getSystemUser().getLoginName(),userCache.getSessionId());
		setExpiredLock(1,3,userCache.getSystemUser().getLoginName(),userCache.getSessionId());
		lockBean.validateLock(1,ids);
		assertNotEquals(new Long(0l),new Long(lockCache.get(new Lock.Key(1,1)).expires));
	}
	
	@Test(expected=EntityLockedException.class)
	public void testValidateLock_diffSessionList() throws Exception {
		ArrayList<Integer> ids;
		
		ids = new ArrayList<Integer>();
		ids.add(1);
		ids.add(2);
		ids.add(3);
		Lock lock = createLock(1,1,userCache.getSystemUser().getLoginName(),"session");
		setLock(lock);
		lockBean.validateLock(1, ids);
	}
	
	@Test(expected=EntityLockedException.class)
	public void testValidateLock_diffUserList() throws Exception {
		ArrayList<Integer> ids;
		
		ids = new ArrayList<Integer>();
		ids.add(1);
		ids.add(2);
		ids.add(3);
		Lock lock = createLock(1,1,"demo",userCache.getSessionId());
		setLock(lock);
		lockBean.validateLock(1, ids);
	}
	
	@Test
	public void testStolen() throws Exception {
		setExpiredLock(1,1,"demo","session");
		lockBean.lock(1, 1);
		assertEquals(1,lockCache.getAll().size());
	}
	
	@Test
	public void testUnlock() throws Exception {
		lockBean.lock(1, 1);
		lockBean.unlock(1, 1);
		assertEquals(0,lockCache.getAll().size());
	}
	
	@Test
	public void testUnlockNotOwned() throws Exception {
		setLock(createLock(1,1,"demo","session"));
		lockBean.unlock(1, 1);
		assertNotNull(lockCache.get(new Lock.Key(1,1)));
	}
	
	@Test(expected=EntityLockedException.class)
	public void testValidateNoLock() throws Exception {
		lockBean.validateLock(1,1);
	}
	
	@Test
	public void testRollback() {
		try {
			rollback.setAndRollBack(1,1);
		} catch (Exception e) {
			
		}	
		assertNull(lockCache.get(new Lock.Key(1,1)));
	}
	
	@Test
	public void testRollbackList() {
		ArrayList<Integer> ids;
		
		ids = new ArrayList<Integer>();
		ids.add(1);
		ids.add(2);
		ids.add(3);
		try {
			rollback.setAndRollBackList(1, ids);
		} catch (Exception e) {
			
		}
		assertEquals(0,lockCache.getAll().size());
	}
	
	@Test 
	public void testMultiLockCall() throws Exception {
		rollback.multiLockCall();
		assertEquals(2,lockCache.getAll().size());
	}
	
	@Test 
	public void testMultiLockCall_rollback() throws Exception {
		try {
			rollback.multiLockCallRollback();
		} catch (Exception e) {
			
		}
		assertEquals(0,lockCache.getAll().size());
	}
	
	@Test
	public void testUTRollback() throws Exception {
		utRollback.setAndRollBack(1, 1);
		assertNull(lockCache.get(new Lock.Key(1,1)));
	}
	
	@Test
	public void testUTLock() throws Exception {
		utRollback.setLock(1,1);
		assertNotNull(lockCache.get(new Lock.Key(1,1)));
	}
		
	private void setLock(Integer tableId, Integer id) throws Exception {
		setLock(createLock(tableId,id,userCache.getSystemUser().getLoginName(),userCache.getSessionId()));
	}
	
	private void setLock(Lock lock) throws Exception {
		lockCache.add(lock);
	}
	
	private void setExpiredLock(Integer tableId, Integer id, String user, String session) throws Exception {
		Lock lock = createLock(tableId,id,user,session);
		lock.expires = 0l;
		lockCache.add(lock);
	}
	
	private Lock createLock(Integer tableId, Integer id, String user, String session) {
		Lock lock = new Lock(tableId,id);
		lock.expires = System.currentTimeMillis() + LockBean.DEFAULT_LOCK_TIME;
		lock.username =  user;
		lock.sessionId = session;
		return lock;
	}

}
