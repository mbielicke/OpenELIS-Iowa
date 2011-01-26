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

import java.util.Iterator;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.transaction.UserTransaction;

import org.jboss.ejb3.annotation.SecurityDomain;
import org.openelis.domain.ReferenceTable;
import org.openelis.gwt.common.ModulePermission.ModuleFlags;
import org.openelis.local.LockLocal;
import org.openelis.manager.SampleManager;
import org.openelis.manager.WorksheetAnalysisManager;
import org.openelis.manager.WorksheetItemManager;
import org.openelis.manager.WorksheetManager;
import org.openelis.manager.WorksheetQcResultManager;
import org.openelis.manager.WorksheetResultManager;
import org.openelis.remote.WorksheetManagerRemote;
import org.openelis.utils.PermissionInterceptor;

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

    public WorksheetManager fetchWithItemsAndNotes(Integer id) throws Exception {
        return WorksheetManager.fetchWithItemsAndNotes(id);
    }

    public WorksheetManager add(WorksheetManager man) throws Exception {
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
            man.getLockedManagers().clear();
            throw e;
        }

        return man;
    }

    public WorksheetManager update(WorksheetManager man) throws Exception {
        Iterator<SampleManager> iter;
        UserTransaction ut;
        SampleManager sMan;

        checkSecurity(ModuleFlags.UPDATE);

        man.validate();

        ut = ctx.getUserTransaction();
        try {
            ut.begin();
            lockBean.validateLock(ReferenceTable.WORKSHEET, man.getWorksheet().getId());        
            man.update();
            lockBean.unlock(ReferenceTable.WORKSHEET, man.getWorksheet().getId());
            ut.commit();
        } catch (Exception e) {
            ut.rollback();
            iter = man.getLockedManagers().values().iterator();
            while (iter.hasNext()) {
                sMan = iter.next();
                lockBean.unlock(ReferenceTable.SAMPLE, sMan.getSample().getId());
            }
            man.getLockedManagers().clear();
            throw e;
        }

        return man;
    }

    public WorksheetManager fetchForUpdate(Integer id) throws Exception {
        lockBean.lock(ReferenceTable.WORKSHEET, id);
        return fetchById(id);
    }

    public WorksheetManager abortUpdate(Integer id) throws Exception {
        lockBean.unlock(ReferenceTable.WORKSHEET, id);
        return fetchById(id);
    }

    public WorksheetItemManager fetchWorksheetItemByWorksheetId(Integer id) throws Exception {
        return WorksheetItemManager.fetchByWorksheetId(id);
    }
    
    public WorksheetAnalysisManager fetchWorksheetAnalysisByWorksheetItemId(Integer id) throws Exception {
        return WorksheetAnalysisManager.fetchByWorksheetItemId(id);
    }
    
    public WorksheetResultManager fetchWorksheetResultByWorksheetAnalysisId(Integer id) throws Exception {
        return WorksheetResultManager.fetchByWorksheetAnalysisId(id);
    }
    
    public WorksheetQcResultManager fetchWorksheetQcResultByWorksheetAnalysisId(Integer id) throws Exception {
        return WorksheetQcResultManager.fetchByWorksheetAnalysisId(id);
    }
    
    private void checkSecurity(ModuleFlags flag) throws Exception {
        PermissionInterceptor.applyPermission("worksheet", flag);
    }
}
