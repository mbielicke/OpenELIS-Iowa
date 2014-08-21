/** Exhibit A - UIRF Open-source Based Public Software License.
* 
* The contents of this file are subject to the UIRF Open-source Based
* Public Software License(the "License"); you may not use this file except
* in compliance with the License. You may obtain a copy of the License at
* openelis.uhl.uiowa.edu
* 
* Software distributed under the License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
* License for the specific language governing rights and limitations
* under the License.
* 
* The Original Code is OpenELIS code.
* 
* The Initial Developer of the Original Code is The University of Iowa.
* Portions created by The University of Iowa are Copyright 2006-2008. All
* Rights Reserved.
* 
* Contributor(s): ______________________________________.
* 
* Alternatively, the contents of this file marked
* "Separately-Licensed" may be used under the terms of a UIRF Software
* license ("UIRF Software License"), in which case the provisions of a
* UIRF Software License are applicable instead of those above. 
*/
package org.openelis.bean;

import org.apache.log4j.Logger;
import org.openelis.entity.Lock;
import org.openelis.gwt.common.EntityLockedException;
import org.openelis.local.LockLocal;
import org.openelis.local.LoginLocal;
import org.openelis.security.domain.SystemUserDO;
import org.openelis.security.local.SystemUserUtilLocal;
import org.openelis.util.Datetime;

import java.util.Calendar;
import java.util.Date;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.EJBs;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

@EJBs({
    @EJB(name="ejb/Login",beanInterface=LoginLocal.class),
    @EJB(name="ejb/SystemUser",beanInterface=SystemUserUtilLocal.class)
})
@Stateless
//@SecurityDomain("ttSecurity")
public class LockBean implements LockLocal{
    
    private LoginLocal login;
    private SystemUserUtilLocal sysUser;
    
    @PostConstruct
    private void init() {
        login =  (LoginLocal)ctx.lookup("ejb/Login");
        sysUser = (SystemUserUtilLocal)ctx.lookup("ejb/SystemUser");    
    }
	    
@PersistenceContext
private EntityManager manager;

@Resource
private SessionContext ctx;

private Logger log = Logger.getLogger(this.getClass());


public boolean isLocked(Integer table, Integer row){
    return isLocked(table,row,"");
}

/**
 * This method returns true if an unexpred lock is found in the table and false if 
 * no lock or an expired lock is found.
 */
public boolean isLocked(Integer table, Integer row, String session){
    Lock lock = checkForLock(table,row);
    if(lock != null && lock.getExpires().getDate().after(Calendar.getInstance().getTime()) && !lock.getSystemUserId().equals(getSystemUserId()) && (lock.getSessionId() == null || "".equals(lock.getSessionId()) || !lock.getSessionId().equals(session)))
        return true;
    return false;
}

public void validateLock(Integer table, Integer row) throws Exception{
    validateLock(table,row,"");
}

/**
 * This method will look for a lock in the table for the specific user and entity.  If none found we assume that they let the lock expire
 * and someone else locked and possible changed the data.  If a lock is found we can say that it is either still a valid lock or no one else
 * has changed the data since the first user locker. 
 * @param table
 * @param row
 * @param sessionId
 * @throws Exception
 */
public void validateLock(Integer table, Integer row, String sessionId) throws Exception {
    Lock lock = null;
    try {
        Query query = manager.createQuery("from Lock where referenceTableId = "+table+" and referenceId = "+row+" and systemUserId = "+getSystemUserId()+" and sessionId = '"+sessionId+"'");
        lock = (Lock)query.getSingleResult();
    }catch(Exception e){
        
    }
    if(lock == null)
        throw new EntityLockedException("Your Lock on this entity has expired and it is possible that the data your are trying to commit may now be stale.  Please press abort and and try your update again.");
}

public Integer getLock(Integer table, Integer row) throws Exception {
    return getLock(table,row,"");
}

/**
 * This method will check for an existing valid lock by another user.  If this is locked by the calling user it will refresh the lock.
 */
public Integer getLock(Integer table, Integer row, String session) throws Exception{
    Lock lock = checkForLock(table,row);
    if(lock != null){
        if(lock.getExpires().getDate().after(Calendar.getInstance().getTime()) && !lock.getSystemUserId().equals(getSystemUserId()) && (lock.getSessionId() == null || "".equals(lock.getSessionId()) || !lock.getSessionId().equals(session))){
             SystemUserDO user = (SystemUserDO)sysUser.getSystemUser(lock.getSystemUserId());
             throw new EntityLockedException("Entity Locked by "+user.getFirstName()+" "+user.getLastName()+".  Lock will expire at "+lock.getExpires().toString()+".");
        }else{
            manager.remove(lock);
            manager.flush();
        }
    }
    lock = new Lock();
    lock.setReferenceTableId(table);
    lock.setReferenceId(row);
    lock.setSystemUserId(getSystemUserId());
    lock.setSessionId(session);
    Calendar cal = Calendar.getInstance();
    cal.add(Calendar.MINUTE,10);
    Date expires = cal.getTime();
    lock.setExpires(new Datetime(Datetime.YEAR,Datetime.MINUTE,expires));
    manager.persist(lock);
    return lock.getId();
}

/**
 * Returns a lock for this entity by any user.
 * @param table
 * @param row
 * @return
 */
private Lock checkForLock(Integer table, Integer row) {
    Query query = manager.createQuery("from Lock where referenceTableId = "+table+" and referenceId = "+row);
    try {
        return (Lock)query.getSingleResult();
    }catch(Exception e){
        return null;
    }
}

public void giveUpLock(Integer table, Integer row){
    giveUpLock(table,row,"");
}

/**
 * This method will remove the given lock from the table.
 */
public void giveUpLock(Integer table, Integer row, String session) {
    try {
        Query query = manager.createQuery("from Lock where referenceTableId = "+table+" and referenceId = "+row+" and systemUserId = "+getSystemUserId()+" and sessionId = '"+session+"'");
        Lock lock = (Lock)query.getSingleResult();
        manager.remove(lock);
    }catch(Exception e){
        
    }
}

public void giveUpUserLocks(){
    
}

public Integer getSystemUserId(){
        try {
            return login.getSystemUserId();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
        }
        
    }
}
