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

import org.jboss.ejb3.annotation.SecurityDomain;
import org.openelis.domain.ReferenceTable;
import org.openelis.gwt.common.SecurityModule.ModuleFlags;
import org.openelis.local.LockLocal;
import org.openelis.manager.PanelItemManager;
import org.openelis.manager.PanelManager;
import org.openelis.remote.PanelManagerRemote;
import org.openelis.utils.SecurityInterceptor;

@Stateless
@SecurityDomain("openelis")
@RolesAllowed("panel-select")
@TransactionManagement(TransactionManagementType.BEAN)

public class PanelManagerBean implements PanelManagerRemote {

    @Resource
    private SessionContext ctx;

    @EJB
    private LockLocal      lockBean;
    
    public PanelManager fetchById(Integer id) throws Exception {       
        return PanelManager.fetchById(id);
    }
    
    public PanelManager fetchWithItems(Integer id) throws Exception {
        return PanelManager.fetchWithItems(id);
    }
    
    public PanelManager add(PanelManager man) throws Exception {
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
            throw e;
        }

        return man;
    }
    
    public PanelManager update(PanelManager man) throws Exception {
        UserTransaction ut;
        
        checkSecurity(ModuleFlags.UPDATE);

        man.validate();

        ut = ctx.getUserTransaction();
        try {
            ut.begin();
            lockBean.validateLock(ReferenceTable.PANEL, man.getPanel().getId());        
            man.update();
            lockBean.giveUpLock(ReferenceTable.PANEL, man.getPanel().getId());
            ut.commit();
        } catch (Exception e) {
            ut.rollback();
            throw e;
        }

        return man;
    }
    
    public PanelManager fetchForUpdate(Integer id) throws Exception {
        lockBean.getLock(ReferenceTable.PANEL, id);
        return fetchById(id);
    }
    
    public PanelManager abortUpdate(Integer id) throws Exception {
        lockBean.giveUpLock(ReferenceTable.PANEL, id);
        return fetchById(id);
    }

    public PanelManager delete(PanelManager man) throws Exception {
        UserTransaction ut;
        
        checkSecurity(ModuleFlags.DELETE);
    
        ut = ctx.getUserTransaction();
        try {
            ut.begin();
            lockBean.validateLock(ReferenceTable.PANEL, man.getPanel().getId());        
            man.delete();
            lockBean.giveUpLock(ReferenceTable.PANEL, man.getPanel().getId());
            ut.commit();
        } catch (Exception e) {
            ut.rollback();
            throw e;
        }
    
        return man;
    }

    public PanelItemManager fetchItemByPanelId(Integer id) throws Exception {
        return PanelItemManager.fetchByPanelId(id);
    }
    
    private void checkSecurity(ModuleFlags flag) throws Exception {
        SecurityInterceptor.applySecurity("panel", flag);
    }

}
