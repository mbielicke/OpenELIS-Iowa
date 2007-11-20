package org.openelis.bean;

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

import org.apache.log4j.Logger;
import org.openelis.entity.Lock;
import org.openelis.local.LockLocal;

import edu.uiowa.uhl.security.domain.SystemUserDO;
import edu.uiowa.uhl.security.local.SystemUserUtilLocal;

@Stateless
//@SecurityDomain("ttSecurity")
public class LockBean implements LockLocal{
	    
@PersistenceContext
private EntityManager manager;

@EJB
private SystemUserUtilLocal sysUser;

@Resource
private SessionContext ctx;

private Logger log = Logger.getLogger(this.getClass());


public boolean isLocked(Integer table, Integer row){
    Query query = manager.createQuery("from Lock where referenceTable = "+table+" and referenceId = "+row);
    boolean locked = false;
    try {
        Lock lock = (Lock)query.getSingleResult();
        if(lock.getExpires().after(Calendar.getInstance().getTime()) && !lock.getSystemUser().equals(getSystemUserId())){
            locked = true;
        }
        if((locked && lock.getSystemUser().equals(getSystemUserId())) || !locked){
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
    if(isLocked(table, row)){
        Query query = manager.createQuery("from Lock where referenceTable = "+table+" and referenceId = "+row);
        try {
            Lock lock = (Lock)query.getSingleResult();
            SystemUserDO user = sysUser.getSystemUser(lock.getSystemUser());
            throw new Exception("Entity Locked by "+user.getFirstName()+" "+user.getLastName()+".  Lock will expire at "+lock.getExpires().toString()+".");
        }catch(Exception e){
            e.printStackTrace();
            throw e;
        }
    }
    Lock lock = new Lock();
    lock.setReferenceTable(table);
    lock.setReferenceId(row);
    lock.setSystemUser(getSystemUserId());
    Calendar cal = Calendar.getInstance();
    cal.add(Calendar.MINUTE,10);
    Date expires = cal.getTime();
    lock.setExpires(expires);
    manager.persist(lock);
    return lock.getId();
}

public void giveUpLock(Integer table, Integer row){
    try {
        Query query = manager.createQuery("from Lock where referenceTable = "+table+" and referenceId = "+row+" and systemUser = "+getSystemUserId());
        Lock lock = (Lock)query.getSingleResult();
        manager.remove(lock);
    }catch(Exception e){
        
    }
}

public void giveUpUserLocks(){
    
}

private Integer getSystemUserId(){
    log.debug(ctx.getCallerPrincipal().getName()+" in LockBean "+ctx.toString());
        try {
            SystemUserDO systemUserDO = sysUser.getSystemUser(ctx.getCallerPrincipal()
                                                                 .getName());
            return systemUserDO.getId();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
        }
        
    }
}
