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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.validation.ConstraintViolationException;

import org.jboss.security.annotation.SecurityDomain;
import org.openelis.entity.Lock;
import org.openelis.gwt.common.EntityLockedException;

@Stateless
@SecurityDomain("openelis")
public class LockBean {

    @PersistenceContext
    private EntityManager  manager;

    @EJB
    private UserCacheBean userCache;

    private static int     DEFAULT_LOCK_TIME = 15 * 60 * 1000, // 15 M * 60 S *
                           GRACE_LOCK_TIME = 2 * 60 * 1000; // 1000 Millis

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
        List<Lock> locks;
        Query query;
        Integer userId;
        String sessionId;

        now = System.currentTimeMillis();

        /*
         * cleanup any expired locks
         */
        query = manager.createNamedQuery("Lock.FetchByIds");
        query.setParameter("ids", referenceIds);
        query.setParameter("tableId", referenceTableId);
        locks = query.getResultList();
        if (locks.size() > 0) {
            for (Lock l : locks) {
                if (l.getExpires() >= now) {
                    throw new EntityLockedException("entityLockException",
                                                    userCache.getSystemUser(l.getSystemUserId())
                                                             .getLoginName(),
                                                    new Date(l.getExpires()).toString());
                }
                manager.remove(l);
                manager.flush();
            }
        }

        /*
         * insert all the locks
         */
        userId = userCache.getId();
        sessionId = userCache.getSessionId();
        try {
            for (Integer id : referenceIds) {
                lock = new Lock();
                lock.setReferenceTableId(referenceTableId);
                lock.setReferenceId(id);
                lock.setSystemUserId(userId);
                lock.setExpires(lockTimeMillis + now);
                lock.setSessionId(sessionId);
                manager.persist(lock);
            }
            manager.flush();
        } catch (ConstraintViolationException e) {
            throw new EntityLockedException("entityLockException",
                                            "unknown",
                                            new Date(lockTimeMillis + now).toString());
        } catch (PersistenceException e) {
            throw new EntityLockedException("entityLockException",
                                            "unknown",
                                            new Date(lockTimeMillis + now).toString());
        }
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
        Query query;
        Integer userId;
        String sessionId;
        List<Lock> locks;

        query = manager.createNamedQuery("Lock.FetchByIds");
        query.setParameter("ids", referenceIds);
        query.setParameter("tableId", referenceTableId);
        locks = query.getResultList();
        if (locks.size() > 0) {
            userId = userCache.getId();
            sessionId = userCache.getSessionId();
            for (Lock l : locks) {
                if (l.getSystemUserId().equals(userId) && l.getSessionId().equals(sessionId)) {
                    manager.remove(l);
                    manager.flush();
                }
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
    public void validateLock(int referenceTableId, ArrayList<Integer> referenceIds) throws Exception {
        long timeMillis;
        Query query;
        Integer userId;
        String sessionId;
        List<Lock> locks;

        query = manager.createNamedQuery("Lock.FetchByIds");
        query.setParameter("ids", referenceIds);
        query.setParameter("tableId", referenceTableId);
        locks = query.getResultList();
        if (locks.size() != referenceIds.size())
            throw new EntityLockedException("expiredLockException");

        userId = userCache.getId();
        sessionId = userCache.getSessionId();
        timeMillis = System.currentTimeMillis();
        for (Lock l : locks) {
            if ( !l.getSystemUserId().equals(userId) || !l.getSessionId().equals(sessionId))
                throw new EntityLockedException("expiredLockException");
            //
            // if the lock has expired, we are going to refresh its expiration
            // time
            //
            if (l.getExpires() < timeMillis - GRACE_LOCK_TIME) {
                l.setExpires(timeMillis + GRACE_LOCK_TIME);
                manager.flush();
            }
        }
    }

    /**
     * Removes all the locks for a user's session. This action is often called
     * from logout.
     */
    public void removeLocks() {
        String sessionId;

        try {
            sessionId = userCache.getSessionId();
            removeLocks(sessionId);
        } catch (Exception e) {
            // ignore
        }
    }

    public void removeLocks(String sessionId) {
        Query query;
        List<Lock> locks;

        if (sessionId == null || sessionId.trim().length() == 0)
            return;

        query = manager.createNamedQuery("Lock.FetchBySessionId");
        query.setParameter("id", sessionId);
        locks = query.getResultList();
        if (locks.size() != 0) {
            for (Lock lock : locks)
                manager.remove(lock);
            manager.flush();
        }
    }
}