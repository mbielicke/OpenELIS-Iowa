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

import java.util.Calendar;
import java.util.Date;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.FlushModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.openelis.entity.Lock;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.Datetime;
import org.openelis.gwt.common.EntityLockedException;
import org.openelis.gwt.common.SystemUserVO;
import org.openelis.local.LockLocal;
import org.openelis.utils.PermissionInterceptor;

@Stateless
public class LockBean implements LockLocal {

    @PersistenceContext
    private EntityManager       manager;

    /**
     * This method returns true if an un-expired lock is found in the table and
     * false if no lock or an expired lock is found.
     */
    public boolean isLocked(Integer table, Integer row) {
        return isLocked(table, row, null);
    }

    public boolean isLocked(Integer table, Integer row, String session) {
        Lock lock;
        Integer userId;
        
        lock = fetchLock(table, row, null, null);
        try {
            userId = PermissionInterceptor.getSystemUserId();
            if (lock != null) {
                return lock.getExpires().after(new Date()) &&
                       DataBaseUtil.isDifferent(lock.getSystemUserId(), userId) &&
                       DataBaseUtil.isDifferent(lock.getSessionId(), session); 
            }
        } catch (Exception e) {
            e.printStackTrace();
            //
            // we want to prevent locking if we get an exception
            //
            return true;
        }

        return false;
    }

    /**
     * This method will look for a lock in the table for the specific user and
     * entity. If none found we assume that they let the lock expire and someone
     * else locked and possible changed the data. If a lock is found we can say
     * that it is either still a valid lock or no one else has changed the data
     * since the first user locker.
     */
    public void validateLock(Integer table, Integer row) throws Exception {
        validateLock(table, row, "");
    }

    public void validateLock(Integer table, Integer row, String session) throws Exception {
        Lock lock;
        Integer userId;
        
        try {
            userId = PermissionInterceptor.getSystemUserId();
            lock = fetchLock(table, row, userId, session);
        } catch (Exception e) {
            lock = null;
        }

        if (lock == null)
            throw new EntityLockedException("expiredLockException");
    }

    /**
     * This method will check for an existing valid lock by another user. If
     * this is locked by the calling user it will refresh the lock.
     */
    public Integer getLock(Integer table, Integer row) throws Exception {
        return getLock(table, row, null);
    }

    public Integer getLock(Integer table, Integer row, String session) throws Exception {
        Lock lock;
        String name;
        SystemUserVO user;
        Calendar now;
        
        lock = fetchLock(table, row, null, null);
        user = PermissionInterceptor.getSystemUser();
        now  = Calendar.getInstance();
        if (lock != null) {
            if (lock.getExpires().after(now.getTime()) &&
                DataBaseUtil.isDifferent(lock.getSystemUserId(), user.getId()) &&
                DataBaseUtil.isDifferent(lock.getSessionId(), session)) { 

                if (DataBaseUtil.isEmpty(user.getLastName())) 
                    name = user.getLoginName();
                else
                    name = user.getFirstName() + " " + user.getLastName();
                throw new EntityLockedException("entityLockException", name,
                                                lock.getExpires().toString());
            } else {
                manager.remove(lock);
                manager.flush();
            }
        }

        //
        // create a new lock and set it to expire in 15 minutes
        //
        now.add(Calendar.MINUTE, 15);

        lock = new Lock();
        lock.setReferenceTableId(table);
        lock.setReferenceId(row);
        lock.setSystemUserId(user.getId());
        lock.setSessionId(session);
        lock.setExpires(new Datetime(Datetime.YEAR, Datetime.MINUTE, now.getTime()));
        manager.persist(lock);

        return lock.getId();
    }

    /**
     * This method will remove the given lock from the table.
     */
    public void giveUpLock(Integer table, Integer row) {
        giveUpLock(table, row, null);
    }

    public void giveUpLock(Integer table, Integer row, String session) {
        Lock lock;
        Integer userId;
        
        manager.setFlushMode(FlushModeType.COMMIT);
        try {
            userId = PermissionInterceptor.getSystemUserId();
            lock = fetchLock(table, row, userId, session);
            manager.remove(lock);
        } catch (EntityNotFoundException e) {
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
     * Returns a lock for this entity by any user.
     */
    private Lock fetchLock(Integer table, Integer row, Integer userId, String session) {
        Query q;
        String s;
        
        s = "from Lock where referenceTableId=" + table + " and referenceId=" + row; 
        if (! DataBaseUtil.isEmpty(userId))
            s += " and systemUserId=" + userId;
        if (!DataBaseUtil.isEmpty(session))
            s += " and sessionId='" + session + "'";
        
        try {
            q = manager.createQuery(s);
            return (Lock)q.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }
}
