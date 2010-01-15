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
package org.openelis.utils;

import java.util.Date;

import javax.naming.InitialContext;
import javax.persistence.PostLoad;
import javax.persistence.PostPersist;
import javax.persistence.PostRemove;
import javax.persistence.PostUpdate;

import org.openelis.domain.HistoryVO;
import org.openelis.local.HistoryLocal;
import org.openelis.local.LoginLocal;

public class AuditUtil {
    
    private static HistoryLocal local;
    
    public AuditUtil() {
    }

    @PostLoad
    void postLoad(Object entity) {
        if (entity instanceof Auditable)
            ((Auditable)entity).setClone();
    }
    
    @PostPersist
    void postCreate(Object entity) {
        Audit audit;
        HistoryVO data;
        
        if (entity instanceof Auditable) {
            audit = ((Auditable)entity).getAudit();

            data = new HistoryVO(null, audit.getReferenceId(), audit.getReferenceTableId(),
                                 new Date(), 1, getSystemUserId(), null);
            local().add(data);
        }
    }

    @PostUpdate
    void postUpdate(Object entity) {
        Audit audit;
        HistoryVO data;

        if (entity instanceof Auditable) {
            audit = ((Auditable)entity).getAudit();

            data = new HistoryVO(null, audit.getReferenceId(), audit.getReferenceTableId(),
                                 new Date(), 2, getSystemUserId(), audit.getXML(true));
            local().add(data);
        }
    }

    @PostRemove
    void postRemove(Object entity) {
        Audit audit;
        HistoryVO data;

        if (entity instanceof Auditable) {
            audit = ((Auditable)entity).getAudit();

            data = new HistoryVO(null, audit.getReferenceId(), audit.getReferenceTableId(),
                                 new Date(), 3, getSystemUserId(), audit.getXML(false));
            local().add(data);
        }
    }

    /*
     * Returns the user id within this transaction. 
     */
    private Integer getSystemUserId() {
        InitialContext ctx;
        LoginLocal login;

        try {
            ctx = new InitialContext();
            login = (LoginLocal)ctx.lookup("openelis/LoginBean/local");
        } catch (Exception e) {
            return null;
        }
        return login.getSystemUserId();
    }

    private HistoryLocal local() {
        InitialContext ctx;
        
        if (local == null) {
            try {
                ctx = new InitialContext();
                local = (HistoryLocal)ctx.lookup("openelis/HistoryBean/local");
            } catch (Exception e) {
                System.out.println(e.getMessage());
                return null;
            }
        }
        return local;
    }
}