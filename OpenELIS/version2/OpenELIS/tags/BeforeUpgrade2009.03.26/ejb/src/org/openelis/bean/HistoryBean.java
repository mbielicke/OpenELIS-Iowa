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
import org.openelis.local.LoginLocal;
import org.openelis.utils.Auditable;

@Stateless
public class HistoryBean implements HistoryLocal {

	    @Resource
	    SessionContext ctx;

	    @PersistenceContext(unitName = "openelis")
	    EntityManager manager;
        
        @EJB
        private LoginLocal login;

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
	                    history.setSystemUserId(login.getSystemUserId());
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
        
        public List getHistoryEntries(Integer referenceId, Integer referenceTable) {
            Query query = manager.createNamedQuery("getEntries");
            query.setParameter("referenceId", referenceId);
            query.setParameter("referenceTable", referenceTable);
            return query.getResultList();
        }
}
