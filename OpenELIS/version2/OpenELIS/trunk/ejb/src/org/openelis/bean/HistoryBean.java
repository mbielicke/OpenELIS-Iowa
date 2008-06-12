package org.openelis.bean;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.openelis.entity.History;
import org.openelis.local.HistoryLocal;
import org.openelis.utils.Auditable;

import edu.uiowa.uhl.security.domain.SystemUserDO;
import edu.uiowa.uhl.security.local.SystemUserUtilLocal;

@Stateless
public class HistoryBean implements HistoryLocal {

	    @Resource
	    SessionContext ctx;

	    @PersistenceContext(unitName = "openelis")
	    EntityManager manager;

	    @EJB
	    private SystemUserUtilLocal sysUser;

	    private Logger log = Logger.getLogger(this.getClass());

	    public void write(Auditable aud, Integer operation, String changes) {
	        try {
	            log.debug("Entering write");
	            String change = null;
	            //if (operation == 2) {
	                change = changes;
	                log.debug("Changes : " + change);
	            //}
	            History history = new History();
	            history.setReferenceId(aud.getId());
	            Query query = manager.createNamedQuery("getTableId");
	            query.setParameter("name", aud.getTableName());
	            history.setReferenceTableId((Integer)query.getSingleResult());
	            
	            //FIXME activity will be a dictionary entry eventually
	            history.setActivityId(operation);
	            history.setTimestamp(new Date());
	            try{
	                String princ = ctx.getCallerPrincipal().getName();
	                if(princ.equals(""))
	                    history.setSystemUserId(0);
	                else
	                    history.setSystemUserId(getSystemUserId());
	            }catch(Exception e){
	                history.setSystemUserId(0);
	            }
	            if (change != null){
	                history.setChanges(change);
	                manager.persist(history);
	            }
	            log.debug("Exiting write");
	        } catch (Exception e) {
	            log.error(e.getMessage());
	            e.printStackTrace();
	        }
	    }

	    private Integer getSystemUserId(){
	        log.debug(ctx.getCallerPrincipal().getName()+" in LockBean "+ctx.toString());
	        try {
	            SystemUserDO systemUserDO = sysUser.getSystemUser(ctx.getCallerPrincipal().getName());
	                return systemUserDO.getId();
	        } catch (Exception e) {
	            e.printStackTrace();
	            return null;
	        } finally {
            }        
	    }
        
        public List getHistoryEntries(Integer referenceId, Integer referenceTable) {
            Query query = manager.createNamedQuery("getEntries");
            query.setParameter("referenceId", referenceId);
            query.setParameter("referenceTable", referenceTable);
            return query.getResultList();
        }
}
