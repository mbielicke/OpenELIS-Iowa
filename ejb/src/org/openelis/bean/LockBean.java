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

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.ejb.LockType;
import javax.ejb.SessionContext;
import javax.ejb.SessionSynchronization;
import javax.ejb.Stateful;

import org.jboss.security.annotation.SecurityDomain;
import org.openelis.constants.Messages;
import org.openelis.ui.common.EntityLockedException;
import org.openelis.utils.User;

@Stateful
@SecurityDomain("openelis")
public class LockBean implements SessionSynchronization {

    @Resource
    private SessionContext ctx;

    @EJB
    private UserCacheBean  userCache;
    
    @EJB
    private LockCacheBean  lockCache;

    private static int     DEFAULT_LOCK_TIME = 15 * 60 * 1000, // 15 M * 60 S *
                    GRACE_LOCK_TIME = 2 * 60 * 1000; // 1000 Millis

    private List<Lock> setLocks = new ArrayList<Lock>();
    
    /**
     * Method creates a new lock entry for the specified table reference and id.
     * If a valid lock currently exist, a EntityLockedException is thrown.
     */
    public void lock(int referenceTableId, Integer referenceId) throws Exception {
        lock(referenceTableId, referenceId, DEFAULT_LOCK_TIME);
    }

    /**
     * Method creates a new lock entry for the specified table reference and id
     * for the specified time on milliseconds. If a valid lock currently exist,
     * a EntityLockedException is thrown.
     */
    public void lock(int referenceTableId, Integer referenceId, long lockTimeMillis) throws Exception {
        ArrayList<Integer> referenceIds;

        referenceIds = new ArrayList<Integer>();
        referenceIds.add(referenceId);
        lock(referenceTableId, referenceIds, lockTimeMillis);
    }

    /**
     * Method creates new lock entries for the specified table reference and
     * ids. If a valid lock currently exist for any of the ids, a
     * EntityLockedException is thrown.
     */
    public void lock(int referenceTableId, ArrayList<Integer> referenceIds) throws Exception {
        lock(referenceTableId, referenceIds, DEFAULT_LOCK_TIME);
    }

    /**
     * Method creates new lock entries for the specified table reference and ids
     * and specified time on milliseconds. If a valid lock currently exist for
     * any of the ids, a EntityLockedException is thrown.
     */
    public void lock(int referenceTableId, ArrayList<Integer> referenceIds, long lockTimeMillis) throws Exception {
        long now;
        Lock lock;
        List<Lock.Key> keys;
        Integer userId;
        String sessionId;

        now = System.currentTimeMillis();

        /*
         * cleanup any expired locks
         */
        keys = createLookUpLocks(referenceTableId,referenceIds);
        for (Lock.Key key : keys) {
        	lock = lockCache.get(key);
            if (lock != null && lock.expires >= now) {
                throw new EntityLockedException(Messages.get()
                                                        .entityLockException(userCache.getSystemUser(lock.systemUserId)
                                                                                      .getLoginName(),
                                                                             new Date(lock.expires).toString()));
            }
            lockCache.remove(key);
        }

        /*
         * insert all the locks
         */
        userId = userCache.getId();
        sessionId = User.getSessionId(ctx);
        for (Integer id : referenceIds) {
            lock = new Lock(referenceTableId,id);
            lock.systemUserId = userId;
            lock.expires = lockTimeMillis + now;
            lock.sessionId = sessionId;
            lockCache.add(lock);
            setLocks.add(lock);
        }
    }
    
    private ArrayList<Lock.Key> createLookUpLocks(Integer referenceTableId, ArrayList<Integer> referenceIds) {
    	ArrayList<Lock.Key> locks = new ArrayList<Lock.Key>();
    	for (Integer id : referenceIds) {
    		locks.add(new Lock.Key(referenceTableId,id));
    	}
    	return locks;
    }

    /**
     * This method removes the lock entry for the specified reference table and
     * id. The lock record must be owned by the user before the lock is removed.
     */
    public void unlock(int referenceTableId, Integer referenceId) {
        ArrayList<Integer> referenceIds;

        referenceIds = new ArrayList<Integer>();
        referenceIds.add(referenceId);
        unlock(referenceTableId, referenceIds);
    }

    public void unlock(int referenceTableId, ArrayList<Integer> referenceIds) {
        Integer userId;
        String sessionId;
        List<Lock.Key> keys;
        Lock lock;

        keys = createLookUpLocks(referenceTableId,referenceIds);
        userId = userCache.getId();
		sessionId = User.getSessionId(ctx);
        for (Lock.Key key : keys) {
        	lock = lockCache.get(key);
        	if (lock != null && lock.systemUserId.equals(userId) && lock.sessionId.equals(sessionId)) {
        		lockCache.remove(key);
   			}
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
    @javax.ejb.Lock(LockType.READ)
    public void validateLock(int referenceTableId, Integer referenceId) throws Exception {
        ArrayList<Integer> referenceIds;

        referenceIds = new ArrayList<Integer>();
        referenceIds.add(referenceId);
        validateLock(referenceTableId, referenceIds);
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
    @javax.ejb.Lock(LockType.READ)
    public void validateLock(int referenceTableId, ArrayList<Integer> referenceIds) throws Exception {
        long timeMillis;
        Integer userId;
        String sessionId;
        List<Lock.Key> keys;
        Lock lock;

        keys = createLookUpLocks(referenceTableId,referenceIds);
        if (!lockCache.containsAllKeys(keys))
            throw new EntityLockedException(Messages.get().expiredLockException());

        userId = userCache.getId();
        sessionId = User.getSessionId(ctx);
        timeMillis = System.currentTimeMillis();
        for (Lock.Key key : keys) {
        	lock = lockCache.get(key);
            if (!lock.systemUserId.equals(userId) || !lock.sessionId.equals(sessionId))
                throw new EntityLockedException(Messages.get().expiredLockException());
            //
            // if the lock has expired, we are going to refresh its expiration
            // time
            //
            if (lock.expires < timeMillis - GRACE_LOCK_TIME) {
                lock.expires = timeMillis + GRACE_LOCK_TIME;
            }
        }
    }

    /**
     * Removes all the locks for a user's session. This action is often called
     * from logout.
     */
    public void removeLocks() {
        String sessionId;

        sessionId = User.getSessionId(ctx);
        removeLocks(sessionId);
    }

    public void removeLocks(String sessionId) {
        if (sessionId == null || sessionId.trim().length() == 0)
            return;

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
	public void afterCompletion(boolean committed) throws EJBException,
			RemoteException {
		if (!committed && setLocks != null) {
			for (Lock lock : setLocks) {
				lockCache.remove(lock);
			}
		}
		setLocks = null;
	}
}