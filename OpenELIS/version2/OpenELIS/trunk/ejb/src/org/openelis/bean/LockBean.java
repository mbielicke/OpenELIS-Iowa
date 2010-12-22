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

import java.util.Date;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.PersistenceContext;

import org.openelis.entity.Lock;
import org.openelis.entity.Lock.PK;
import org.openelis.gwt.common.EntityLockedException;
import org.openelis.gwt.common.SystemUserVO;
import org.openelis.local.LockLocal;
import org.openelis.utils.PermissionInterceptor;

@Stateless
public class LockBean implements LockLocal {

    @PersistenceContext
    private EntityManager manager;

    private static int    DEFAULT_LOCK_TIME = 15 * 60 * 1000,   // 15 M * 60 S * 1000 Millis
                          GRACE_LOCK_TIME = 2 * 60 * 1000;

    /**
     * Method creates a new lock entry for the specified table reference and id.
     * If a valid lock currently exist, a EntityLockedException is thrown.
     */
    public void lock(int referenceTableId, int referenceId) throws Exception {
        lock(referenceTableId, referenceId, DEFAULT_LOCK_TIME);
    }

    /**
     * Method creates a new lock entry for the specified table reference and id
     * for the specified time on milliseconds. If a valid lock currently exist,
     * a EntityLockedException is thrown.
     */
    public void lock(int referenceTableId, int referenceId, long lockTimeMillis) throws Exception {
        PK pk;
        Lock lock;
        long timeMillis;
        Integer userId;
        SystemUserVO user;

        userId = PermissionInterceptor.getSystemUserId();
        timeMillis = System.currentTimeMillis();
        
        pk = new Lock.PK(referenceTableId, referenceId);
        try {
            lock = manager.find(Lock.class, pk);
            if (lock == null) {
                lock = new Lock();
                lock.setReferenceTableId(referenceTableId);
                lock.setReferenceId(referenceId);
                lock.setSystemUserId(userId);
                lock.setExpires(lockTimeMillis+timeMillis);
                manager.persist(lock);
            } else if (lock.getExpires() < timeMillis) {
                //
                // if the lock has expired, then we can take it over
                //
                lock.setSystemUserId(userId);
                lock.setExpires(lockTimeMillis+timeMillis);
            } else {
                user = PermissionInterceptor.getSystemUser(lock.getSystemUserId());
                throw new EntityLockedException("entityLockException",
                                                user.getLoginName(),
                                                new Date(lock.getExpires()).toString());
            }
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * This method removes the lock entry for the specified reference table and id. The
     * lock record must be owned by the user before the lock is removed.
     */
    public void unlock(int referenceTableId, int referenceId) {
        PK pk;
        Lock lock;
        Integer userId;

        manager.setFlushMode(FlushModeType.COMMIT);

        pk = new Lock.PK(referenceTableId, referenceId);
        try {
            userId = PermissionInterceptor.getSystemUserId();
            lock = manager.find(Lock.class, pk);
            if (lock != null && lock.getSystemUserId().equals(userId))
                manager.remove(lock);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This method will search for an existing lock for the specified reference table and id.
     * If a lock is not found or the lock does not belong to the calling user, the method
     * throws EntityLockException specifying that the lock is not valid.
     * Note that expired locks are valid (because no one else has requested for the
     * same resource to be locked) and this method resets the expiration time of expired or
     * nearly expire locks by a constant grace time.    
     */
    public void validateLock(int referenceTableId, int referenceId) throws Exception {
        PK pk;
        Lock lock;
        long timeMillis;
        Integer userId;

        pk = new Lock.PK(referenceTableId, referenceId);
        try {
            lock = manager.find(Lock.class, pk);
            userId = PermissionInterceptor.getSystemUserId();
        } catch (Exception e) {
            lock = null;
            userId = null;
        }

        if (lock == null || !lock.getSystemUserId().equals(userId))
            throw new EntityLockedException("expiredLockException");
        //
        // if the lock has expired, we are going to refresh its expiration time
        //
        timeMillis = System.currentTimeMillis();
        if (lock.getExpires() < timeMillis-GRACE_LOCK_TIME)
            lock.setExpires(timeMillis+GRACE_LOCK_TIME);
    }
}