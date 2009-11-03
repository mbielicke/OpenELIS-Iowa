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

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.persistence.PersistenceContext;
import javax.transaction.UserTransaction;

import org.jboss.annotation.security.SecurityDomain;
import org.openelis.domain.ReferenceTable;
import org.openelis.gwt.common.SecurityModule.ModuleFlags;
import org.openelis.local.LockLocal;
import org.openelis.manager.WorksheetManager;
import org.openelis.manager.WorksheetItemManager;
import org.openelis.remote.WorksheetManagerRemote;
import org.openelis.utils.ReferenceTableCache;
import org.openelis.utils.SecurityInterceptor;

@Stateless
@SecurityDomain("openelis")
@RolesAllowed("worksheet-select")
@TransactionManagement(TransactionManagementType.BEAN)

public class WorksheetManagerBean implements WorksheetManagerRemote {

    @Resource
    private SessionContext ctx;

    @EJB
    private LockLocal      lockBean;

    public WorksheetManagerBean() {
    }
    
    public WorksheetManager fetchById(Integer id) throws Exception {
        return WorksheetManager.fetchById(id);
    }

    public WorksheetManager fetchWithItems(Integer id) throws Exception {
        return WorksheetManager.fetchWithItems(id);
    }

    public WorksheetManager fetchWithNotes(Integer id) throws Exception {
        return WorksheetManager.fetchWithNotes(id);
    }

    public WorksheetManager add(WorksheetManager man) throws Exception {
        UserTransaction ut;
        
        checkSecurity(ModuleFlags.ADD);

        man.validate();

        ut = ctx.getUserTransaction();
        ut.begin();
        man.add();
        ut.commit();

        return man;
    }

    public WorksheetManager update(WorksheetManager man) throws Exception {
        UserTransaction ut;

        checkSecurity(ModuleFlags.UPDATE);

        man.validate();

        ut = ctx.getUserTransaction();
        ut.begin();
        lockBean.validateLock(ReferenceTable.WORKSHEET, man.getWorksheet().getId());        
        man.update();
        lockBean.giveUpLock(ReferenceTable.WORKSHEET, man.getWorksheet().getId());
        ut.commit();

        return man;
    }

    public WorksheetManager fetchForUpdate(Integer id) throws Exception {
        lockBean.getLock(ReferenceTable.WORKSHEET, id);
        return fetchById(id);
    }

    public WorksheetManager abortUpdate(Integer id) throws Exception {
        lockBean.giveUpLock(ReferenceTable.WORKSHEET, id);
        return fetchById(id);
    }

    public WorksheetItemManager fetchItemByWorksheetId(Integer id) throws Exception {
        return WorksheetItemManager.fetchByWorksheetId(id);
    }
    
    private void checkSecurity(ModuleFlags flag) throws Exception {
        SecurityInterceptor.applySecurity(ctx.getCallerPrincipal().getName(), 
                                          "worksheet", flag);
    }
}