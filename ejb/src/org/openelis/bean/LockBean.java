/**
 * Exhibit A - UIRF Open-source Based Public Software License.
 * 
 * The contents of this file are subject to the UIRF Open-source Based Public
 * Software License(the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * openelis.uhl.uiowa.edu
 * 
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 * 
 * The Original Code is OpenELIS code.
 * 
 * The Initial Developer of the Original Code is The University of Iowa.
 * Portions created by The University of Iowa are Copyright 2006-2008. All
 * Rights Reserved.
 * 
 * Contributor(s): ______________________________________.
 * 
 * Alternatively, the contents of this file marked "Separately-Licensed" may be
 * used under the terms of a UIRF Software license ("UIRF Software License"), in
 * which case the provisions of a UIRF Software License are applicable instead
 * of those above.
 */
package org.openelis.bean;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.ejb.Remove;
import javax.ejb.SessionContext;
import javax.ejb.SessionSynchronization;
import javax.ejb.Stateful;

import org.jboss.security.annotation.SecurityDomain;
import org.openelis.constants.Messages;
import org.openelis.ui.common.EntityLockedException;
import org.openelis.ui.common.SystemUserVO;
import org.openelis.utils.User;

@Stateful
@SecurityDomain("openelis")
public class LockBean implements SessionSynchronization {

    @Resource
    private SessionContext ctx;

    @EJB
    protected UserCacheBean userCache;
    
    @EJB
    protected LockCacheBean lockCache;

    protected static int DEFAULT_LOCK_TIME = 15 * 60 * 1000, // 15 M * 60 S * 1000 Millis 
    		             GRACE_LOCK_TIME = 2 * 60 * 1000;

    protected List<Lock> setLocks;
    
    protected Integer user;
    
    protected String sessionId;
    
    @PostConstruct
    public void init() {
    	setLocks = new ArrayList<Lock>();
    	user = userCache.getId();
    	sessionId = User.getSessionId(ctx);
    }
    
    
    /**
     * Method creates new lock entries for the specified table reference and
     * ids. If a valid lock currently exist for any of the ids, a
     * EntityLockedException is thrown.
     */
    public void lock(int tableId, List<Integer> ids) throws Exception {
        lock(tableId, ids, DEFAULT_LOCK_TIME);
    }
    
    /**
     * Method creates new lock entries for the specified table reference and ids
     * and specified time on milliseconds. If a valid lock currently exist for
     * any of the ids, a EntityLockedException is thrown.
     */
    public void lock(int tableId, List<Integer> ids, long lockTimeMillis) throws Exception {
    	for (Integer id : ids) {
    		lock(tableId,id,lockTimeMillis);
    	}
    }
    
    /**
     * Method creates a new lock entry for the specified table reference and id.
     * If a valid lock currently exist, a EntityLockedException is thrown.
     */
    public void lock(int tableId, Integer id) throws Exception {
        lock(tableId, id, DEFAULT_LOCK_TIME);
    }
    
    /**
     * Method creates a new lock entry for the specified table reference and id
     * for the specified time on milliseconds. If a valid lock currently exist,
     * a EntityLockedException is thrown.
     */
    public void lock(int tableId, Integer id, long lockTimeMillis) throws Exception {
    	long now;
    	
    	now = System.currentTimeMillis();
        checkIfLockedByOther(tableId,id,now);
        createLock(tableId,id,now+lockTimeMillis);
    }

    private void checkIfLockedByOther(Integer tableId, Integer id, long time) throws Exception {
    	Lock lock;
    	
    	lock = getLock(tableId, id);
   		if (lock != null && lock.expires >= time) {
   			throw lockedByOtherException(lock);
   		} else if (lock != null) {
   			lockCache.remove(lock.key);
   		}
    }
    
    private void createLock(Integer tableId, Integer id, long expires) throws Exception {
    	Lock lock;

        lock = new Lock(tableId,id,user,expires,sessionId);
        lockCache.add(lock);
        setLocks.add(lock);
    }
    
    private Lock getLock(Integer tableId, Integer id) {
    	return lockCache.get(createLockKey(tableId,id));
    }
    
    private Lock.Key createLockKey(Integer tableId, Integer id) {
    	return new Lock.Key(tableId,id);
    }
    
    private EntityLockedException lockedByOtherException(Lock lock) throws Exception {
    	String loginName, expires, msg;
    	
    	loginName = getLockedByLoginName(lock.systemUserId);
    	expires = new Date(lock.expires).toString();
    	msg = Messages.get().entityLockException(loginName,expires);
    	return new EntityLockedException(msg);
    }

    private String getLockedByLoginName(Integer id) {
    	SystemUserVO user;
    	
    	user = id != null && id > -1 ? userCache.getSystemUser(id) : null; 
    	return user != null ? user.getLoginName() : "unknown";
    }
    
    public void unlock(int tableId, List<Integer> ids) {
        for (Integer id : ids) {
        	unlock(tableId, id);
   		}
    }
    
    /**
     * This method removes the lock entry for the specified reference table and
     * id. The lock record must be owned by the user before the lock is removed.
     */
    public void unlock(int tableId, Integer id) {
    	Lock lock;
    	
    	lock = getLock(tableId,id);
    	if (ownsLock(lock)) {
    		lockCache.remove(lock.key);
		}
    }

    private boolean ownsLock(Lock lock) {
    	return lock.systemUserId.equals(user) && lock.sessionId.equals(sessionId);
    }
    
    /**
     * This method will search for a list of existing lock for the specified
     * reference table and ids. If locks are not found or they don't belong to
     * the calling user, the method throws EntityLockException specifying that
     * the lock is not valid. Note that expired locks are valid (because no one
     * else has requested for the same resource to be locked) and this method
     * resets the expiration time of expired or nearly expire locks by a
     * constant grace time.
     */
    public void validateLock(int tableId, List<Integer> ids) throws Exception {
    	for (Integer id : ids) {
    		validateLock(tableId,id);
    	}
    }
    
    /**
     * This method will search for an existing lock for the specified reference
     * table and id. If a lock is not found or the lock does not belong to the
     * calling user, the method throws EntityLockException specifying that the
     * lock is not valid. Note that expired locks are valid (because no one else
     * has requested for the same resource to be locked) and this method resets
     * the expiration time of expired or nearly expire locks by a constant grace
     * time.
     */
    public void validateLock(int tableId, Integer id) throws Exception {
    	checkIfLockExist(createLockKey(tableId,id));
        checkIfLockValid(getLock(tableId,id));
    }
    
    private void checkIfLockExist(Lock.Key key) throws Exception {
    	if (lockCache.get(key) == null) {
    		throw new EntityLockedException(Messages.get().expiredLockException());
    	}
    }
    
    private void checkIfLockValid(Lock lock) throws Exception {
        long time;
        
        time = System.currentTimeMillis();
        if (!ownsLock(lock)) {
            throw lockedByOtherException(lock);
        }
        checkAndRefreshExpires(lock,time);
    }
    
    private void checkAndRefreshExpires(Lock lock, long time) {
        if (lock.expires < time - GRACE_LOCK_TIME) {
            lock.expires = time + GRACE_LOCK_TIME;
        }
    }

    /**
     * Removes all the locks for a user's session. This action is often called
     * from logout.
     */
    public void removeLocks() {
        removeLocks(sessionId);
    }

    public void removeLocks(String sessionId) {
        if (sessionId == null || sessionId.trim().length() == 0) {
            return;
        }

        for (Lock lock : lockCache.getAll()) {
        	if (sessionId.equals(lock.sessionId)) {
        		lockCache.remove(lock.key);
        	}
        }
    }
    
	@Override
	public void afterBegin() throws EJBException, RemoteException {
		
	}

	@Override
	public void beforeCompletion() throws EJBException, RemoteException {
	
	}

	@Override
	public void afterCompletion(boolean committed) throws EJBException,RemoteException {
		if (!committed) {
			for (Lock lock : setLocks) {
				lockCache.remove(lock);
			}
		}
		setLocks.clear();
	}
	
	@Remove
	public void destroy() {
		setLocks = null;
		user = null;
		sessionId = null;
	}
}