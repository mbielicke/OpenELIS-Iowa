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
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;

import org.jboss.security.annotation.SecurityDomain;
import org.openelis.bean.LockCacheBean.Lock;
import org.openelis.constants.Messages;
import org.openelis.ui.common.EntityLockedException;
import org.openelis.utils.User;

@Stateless
@SecurityDomain("openelis")
public class LockBean {

    @Resource
    private SessionContext  ctx;

    @EJB
    protected LockCacheBean    lockCache;

    protected static int       DEFAULT_LOCK_TIME = 15 * 60 * 1000, // 15 minutes
                               GRACE_LOCK_TIME = 2 * 60 * 1000;

    protected Logger           log               = Logger.getLogger("openelis");

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
        long expires;
        String user, session;

        expires = System.currentTimeMillis() + lockTimeMillis;
        user = User.getName(ctx);
        session = User.getSessionId(ctx);
        for (Integer id : ids)
            lock(tableId, id, expires, user, session);
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
        long expires;

        expires = System.currentTimeMillis() + lockTimeMillis;
        lock(tableId, id, expires, User.getName(ctx), User.getSessionId(ctx));
    }

    private void lock(int tableId, Integer id, long expires, String user, String session) throws Exception {
        Lock lock;

        lock = lockCache.get(new Lock.Key(tableId, id));
        if (lock == null) {
            lock = new Lock(tableId, id, user, expires, session);
            lockCache.add(lock);
            return;
        }

        checkIfLockedByOther(lock, user, session);
        // if exception not thrown take over lock;
        lock.username = user;
        lock.sessionId = session;
        lock.expires = expires;
    }

    /*
     * create a lock exception message
     */
    private void checkIfLockedByOther(Lock lock, String user, String session) throws Exception {
        String expires, msg;

        if (lock.expires > System.currentTimeMillis() && !ownsLock(lock, user, session)) {
            expires = new Date(lock.expires).toString();
            msg = Messages.get().entityLockException(lock.username, expires);
            throw new EntityLockedException(msg);
        }
    }

    /**
     * This method removes the lock entry for the specified reference table and
     * id. The lock record must be owned by the user before the lock is removed.
     */
    public void unlock(int tableId, List<Integer> ids) {
        String user, session;

        user = User.getName(ctx);
        session = User.getSessionId(ctx);
        for (Integer id : ids) {
            unlock(tableId, id, user, session);
        }
    }

    /**
     * This method removes the lock entry for the specified reference table and
     * id. The lock record must be owned by the user before the lock is removed.
     */
    public void unlock(int tableId, Integer id) {
        unlock(tableId, id, User.getName(ctx), User.getSessionId(ctx));
    }

    private void unlock(int tableId, Integer id, String user, String session) {
        Lock lock;

        lock = lockCache.get(new Lock.Key(tableId, id));
        if (ownsLock(lock, user, session)) {
            lockCache.remove(lock.key);
        }
    }

    private boolean ownsLock(Lock lock, String user, String session) {
        return lock != null && lock.username != null &&
               lock.username.equals(user) && lock.sessionId.equals(session);
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
        String user, session;

        user = User.getName(ctx);
        session = User.getSessionId(ctx);
        for (Integer id : ids) {
            validateLock(tableId, id, user, session);
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
        validateLock(tableId, id, User.getName(ctx), User.getSessionId(ctx));
    }

    private void validateLock(int tableId, Integer id, String user, String session) throws Exception {
        Lock lock;
        Lock.Key key;
        long time;

        key = new Lock.Key(tableId, id);
        lock = lockCache.get(key);
        if (!ownsLock(lock, user, session))
            throw new EntityLockedException(Messages.get()
                                            .expiredLockException());
        time = System.currentTimeMillis();
        if (lock.expires < time - GRACE_LOCK_TIME)
            lock.expires = time + GRACE_LOCK_TIME;
    }

    /**
     * Removes all the locks for a user's session. This action is often called
     * from logout.
     */
    public void removeLocks() {
        removeLocks(User.getSessionId(ctx));
    }

    /**
     * Removes all the locks for a user's session. This action is often called
     * from logout.
     */
    public void removeLocks(String sessionId) {
        if (sessionId.trim().length() == 0)
            return;

        lockCache.clearCacheBySession(sessionId);
    }
}