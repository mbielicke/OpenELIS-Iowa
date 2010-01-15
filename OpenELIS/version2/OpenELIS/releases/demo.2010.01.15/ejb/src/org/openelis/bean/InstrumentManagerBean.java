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

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.transaction.UserTransaction;

import org.jboss.annotation.security.SecurityDomain;
import org.openelis.domain.ReferenceTable;
import org.openelis.gwt.common.SecurityModule.ModuleFlags;
import org.openelis.local.LockLocal;
import org.openelis.manager.InstrumentLogManager;
import org.openelis.manager.InstrumentManager;
import org.openelis.remote.InstrumentManagerRemote;
import org.openelis.utils.SecurityInterceptor;

@Stateless
@SecurityDomain("openelis")
@RolesAllowed("instrument-select")
@TransactionManagement(TransactionManagementType.BEAN)

public class InstrumentManagerBean implements InstrumentManagerRemote {

    @Resource
    private SessionContext ctx;

    @EJB
    private LockLocal      lockBean;
    
    public InstrumentManager fetchById(Integer id) throws Exception {
        return InstrumentManager.fetchById(id);
    }
    
    public InstrumentManager fetchWithLogs(Integer id) throws Exception {        
        return InstrumentManager.fetchWithLogs(id);
    }

    public InstrumentManager add(InstrumentManager man) throws Exception {
        UserTransaction ut;

        checkSecurity(ModuleFlags.ADD);

        man.validate();

        ut = ctx.getUserTransaction();
        try {
            ut.begin();
            man.add();
            ut.commit();
        } catch (Exception e) {
            ut.rollback();
        }

        return man;
    }
    
    public InstrumentManager update(InstrumentManager man) throws Exception {
        UserTransaction ut;
        
        checkSecurity(ModuleFlags.UPDATE);

        man.validate();

        ut = ctx.getUserTransaction();
        try {
            ut.begin();
            lockBean.validateLock(ReferenceTable.INSTRUMENT, man.getInstrument().getId());        
            man.update();
            lockBean.giveUpLock(ReferenceTable.INSTRUMENT, man.getInstrument().getId());
            ut.commit();
        } catch (Exception e) {
            ut.rollback();
        }

        return man;
    }

    public InstrumentManager fetchForUpdate(Integer id) throws Exception {
        lockBean.getLock(ReferenceTable.INSTRUMENT, id);
        return fetchById(id);
    }
    
    public InstrumentManager abortUpdate(Integer id) throws Exception {
        lockBean.giveUpLock(ReferenceTable.INSTRUMENT, id);
        return fetchById(id);
    }

    public InstrumentLogManager fetchLogByInstrumentId(Integer id) throws Exception {       
        return InstrumentLogManager.fetchByInstrumentId(id);
    }
    
    private void checkSecurity(ModuleFlags flag) throws Exception {
        SecurityInterceptor.applySecurity(ctx.getCallerPrincipal().getName(), 
                                          "instrument", flag);
    }

}
