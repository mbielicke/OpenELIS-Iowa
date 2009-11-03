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
import org.openelis.persistence.JBossCachingManager;
import org.openelis.security.domain.SystemUserDO;
import org.openelis.util.Datetime;

import java.util.Calendar;
import java.util.Date;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

@Stateless
//@SecurityDomain("ttSecurity")
public class LockBean implements LockLocal{
	    
@PersistenceContext
private EntityManager manager;

@EJB
private LoginLocal login;

@Resource
private SessionContext ctx;

private Logger log = Logger.getLogger(this.getClass());


public boolean isLocked(Integer table, Integer row){
    return isLocked(table,row,"");
}

public boolean isLocked(Integer table, Integer row, String session){
    Query query = manager.createQuery("from Lock where referenceTableId = "+table+" and referenceId = "+row);
    boolean locked = false;
    try {
        Lock lock = (Lock)query.getSingleResult();
        if(lock.getExpires().getDate().after(Calendar.getInstance().getTime()) && !lock.getSystemUserId().equals(getSystemUserId()) && (lock.getSessionId() == null || "".equals(lock.getSessionId()) || !lock.getSessionId().equals(session))){
            locked = true;
        }
        if((locked && (lock.getSystemUserId().equals(getSystemUserId()) && lock.getSessionId().equals(session))) || !locked){
            manager.remove(lock);
            manager.flush();
            locked = false;
        }
    }catch(NoResultException e){
        return false;
    }
    return locked;
}

public Integer getLock(Integer table, Integer row) throws Exception {
    return getLock(table,row,"");
}

public Integer getLock(Integer table, Integer row, String session) throws Exception{
    if(isLocked(table, row)){
        Query query = manager.createQuery("from Lock where referenceTableId = "+table+" and referenceId = "+row);
        try {
            Lock lock = (Lock)query.getSingleResult();
            SystemUserDO user = (SystemUserDO)JBossCachingManager.getElement("openelis","security", ctx.getCallerPrincipal().getName()+"userdo");
            throw new EntityLockedException("Entity Locked by "+user.getFirstName()+" "+user.getLastName()+".  Lock will expire at "+lock.getExpires().toString()+".");
        }catch(Exception e){
            e.printStackTrace();
            throw e;
        }
    }
    Lock lock = new Lock();
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

public void giveUpLock(Integer table, Integer row){
    giveUpLock(table,row,"");
}

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
