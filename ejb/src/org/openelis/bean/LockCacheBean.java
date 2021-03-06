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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.ejb.DuplicateKeyException;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.transaction.Status;
import javax.transaction.Synchronization;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;

import org.jboss.ejb3.annotation.SecurityDomain;

/**
 * This class provides the application wide logical locking cache used to
 * prevent multiple users from modifying the same resource.
 */
@Singleton
@javax.ejb.Lock(LockType.READ)
@SecurityDomain("openelis")
public class LockCacheBean {

    private int                                   maxLocks     = 0;

    private HashMap<Lock.Key, Lock>               locks        = new HashMap<Lock.Key, Lock>();
    private HashMap<Transaction, TransactionSync> transactions = new HashMap<Transaction, TransactionSync>();

    private static final Logger                   log          = Logger.getLogger("openelis");
    
    @Resource(lookup="java:/TransactionManager")
    private TransactionManager transactionManager;

    /**
     * Returns a lock a record specified by key
     */
    public Lock get(Lock.Key key) {
        return locks.get(key);
    }

    public Collection<Lock> getAll() {
        return locks.values();
    }

    /**
     * Adds a lock record
     */
    @javax.ejb.Lock(LockType.WRITE)
    public void add(Lock lock) throws Exception {
        if (locks.containsKey(lock.key))
            throw new DuplicateKeyException();

        locks.put(lock.key, lock);
        getSync().add(lock.key);

        maxLocks = Math.max(maxLocks, locks.size());
        log.fine("Added - "+lock.toString());
    }
    
    /**
     * Removes a lock record
     */
    @javax.ejb.Lock(LockType.WRITE)
    public void remove(Lock.Key key) {
        Lock lock;
        
        lock = locks.remove(key);
        if (lock != null)
            log.fine("Removed - " + lock);
    }

    /**
     * 
     */
    public void printStatistics() {
        log.info("Lock size " + locks.size() + " with max of " + maxLocks);
    }

    /**
     * Clear locks from the cache that are past the expiration time specified
     */
    @javax.ejb.Lock(LockType.WRITE)
    public void clearCacheByTime(long timeMillis) {
        long expired;
        ArrayList<Lock> expiredLocks;

        expired = System.currentTimeMillis() - timeMillis;
        expiredLocks = new ArrayList<Lock>();
        for (Lock l : locks.values()) {
            if (l.expires < expired) {
                expiredLocks.add(l);
                log.fine("Cleared lock " + l.toString());
            }
        }

        for (Lock l : expiredLocks)
            locks.remove(l.key);
    }

    /**
     * Clear locks from the cache that are owned by the specified user
     */
    @javax.ejb.Lock(LockType.WRITE)
    public void clearCacheByUser(String username) {
        ArrayList<Lock> userLocks;

        userLocks = new ArrayList<Lock>();
        for (Lock l : locks.values()) {
            if (l.username.equals(username)) {
                userLocks.add(l);
                log.fine("Cleared lock " + l.toString());
            }
        }

        for (Lock l : userLocks)
            locks.remove(l.key);
    }

    /**
     * Clear locks from the cache that are owned by the specified session
     */
    @javax.ejb.Lock(LockType.WRITE)
    public void clearCacheBySession(String sessionId) {
        ArrayList<Lock> userLocks;

        userLocks = new ArrayList<Lock>();
        for (Lock l : locks.values()) {
            if (l.sessionId.equals(sessionId)) {
                userLocks.add(l);
                log.fine("Cleared lock " + l.toString());
            }
        }

        for (Lock l : userLocks)
            locks.remove(l.key);
    }
    
    /*
     * Returns a listening handler for current transaction
     */
    private TransactionSync getSync() throws Exception {
        TransactionSync sync;
        Transaction transaction;

        try {
            transaction = transactionManager.getTransaction();
            sync = transactions.get(transaction);
            if (sync == null) {
                sync = new TransactionSync(transaction);
                transaction.registerSynchronization(sync);
            }
        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage(), e);
            throw e;
        }

        return sync;
    }

    /**
     * A simple class to manage lock records. The class is used internally by
     * LockCacheBean and LockBean.
     */
    public static class Lock {

        protected Key  key;
        protected long expires;
        protected String username, sessionId;

        public Lock(Integer tableId, Integer id) {
            this.key = new Key(tableId, id);
        }

        public Lock(Integer tableId, Integer id, String username, long expires,
                    String sessionId) {
            this(tableId, id);
            this.username = username;
            this.expires = expires;
            this.sessionId = sessionId;
        }

        @Override
        public int hashCode() {
            return key.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Lock) {
                return key.equals( ((Lock)obj).key);
            }
            return false;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(key.tableId);
            sb.append(":");
            sb.append(key.id);
            sb.append(":");
            sb.append(expires);
            sb.append(":");
            sb.append(username);
            sb.append(":");
            sb.append(sessionId);
            return sb.toString();
        }

        /**
         * Unique key is made from table id and serial record id.
         */
        public static class Key {
            protected Integer tableId, id;

            public Key(Integer tableId, Integer id) {
                this.tableId = tableId;
                this.id = id;
            }

            @Override
            public int hashCode() {
                int hash = 5;
                hash = 17 * hash + (tableId == null ? 0 : tableId.hashCode());
                hash = 17 * hash + (id == null ? 0 : id.hashCode());
                return hash;
            }

            @Override
            public boolean equals(Object obj) {
                if (obj instanceof Key) {
                    return tableId.equals( ((Key)obj).tableId) &&
                           id.equals( ((Key)obj).id);
                }
                return false;
            }
        }
    }
    
    /*
     * A simple class to manage locks for transactions. The class is
     * registered to the transaction manager.
     */
    private class TransactionSync implements Synchronization {
        List<Lock.Key> locks;
        Transaction    transaction;

        public TransactionSync(Transaction transaction) {
            this.transaction = transaction;
            locks = new ArrayList<Lock.Key>();
        }

        public void add(Lock.Key lock) {
            locks.add(lock);
        }

        @Override
        public void beforeCompletion() {

        }

        @Override
        public void afterCompletion(int status) {
            if (Status.STATUS_COMMITTED != status) {
                for (Lock.Key key : locks)
                    remove(key);
            }
            transactions.remove(transaction);
        }
    }
}