package org.openelis.bean;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Date;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openelis.bean.LockCacheBean.Lock;
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
		lockCache.clearCache(-(LockBean.DEFAULT_LOCK_TIME+100000));
	}
	
	@Test
	public void testLock() throws Exception {
		lockBean.lock(1, 1);
		assertNotNull(lockCache.get(new Lock.Key(1, 1)));
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
	public void testStolen() throws Exception {
		setExpiredLock(1,1,"demo","session");
		lockBean.lock(1, 1);
		assertEquals(1,lockCache.getAll().size());
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
		
	private void setLock(Integer tableId, Integer id) {
		setLock(createLock(tableId,id,userCache.getSystemUser().getLoginName(),userCache.getSessionId()));
	}
	
	private void setLock(Lock lock) {
		lockCache.add(lock);
	}
	
	private void setExpiredLock(Integer tableId, Integer id, String user, String session) {
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
