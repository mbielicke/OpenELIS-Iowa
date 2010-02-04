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
import javax.transaction.UserTransaction;

import org.jboss.annotation.security.SecurityDomain;
import org.openelis.domain.ReferenceTable;
import org.openelis.gwt.common.SecurityModule.ModuleFlags;
import org.openelis.local.LockLocal;
import org.openelis.manager.OrderItemManager;
import org.openelis.manager.OrderManager;
import org.openelis.remote.OrderManagerRemote;
import org.openelis.utils.SecurityInterceptor;

@Stateless
@SecurityDomain("openelis")
@RolesAllowed("order-select")
@TransactionManagement(TransactionManagementType.BEAN)

public class OrderManagerBean implements OrderManagerRemote {

    @Resource
    private SessionContext ctx;

    @EJB
    private LockLocal      lockBean;

    public OrderManager fetchById(Integer id) throws Exception {
        return OrderManager.fetchById(id);
    }

    public OrderManager fetchWithItems(Integer id) throws Exception {
        return OrderManager.fetchWithItems(id);
    }

    public OrderManager fetchWithReceipts(Integer id) throws Exception {
        return OrderManager.fetchWithReceipts(id);
    }

    public OrderManager fetchWithNotes(Integer id) throws Exception {
        return OrderManager.fetchWithNotes(id);
    }

    public OrderManager add(OrderManager man) throws Exception {
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

    public OrderManager update(OrderManager man) throws Exception {
        UserTransaction ut;
        
        checkSecurity(ModuleFlags.UPDATE);

        man.validate();

        ut = ctx.getUserTransaction();
        try {
            ut.begin();
            lockBean.validateLock(ReferenceTable.ORDER, man.getOrder().getId());        
            man.update();
            lockBean.giveUpLock(ReferenceTable.ORDER, man.getOrder().getId());
            ut.commit();
        } catch (Exception e) {
            ut.rollback();
            throw e;
        }

        return man;
    }

    public OrderManager fetchForUpdate(Integer id) throws Exception {
        lockBean.getLock(ReferenceTable.ORDER, id);
        return fetchById(id);
    }

    public OrderManager abortUpdate(Integer id) throws Exception {
        lockBean.giveUpLock(ReferenceTable.ORDER, id);
        return fetchById(id);
    }

    public OrderItemManager fetchItemByOrderId(Integer id) throws Exception {
        return OrderItemManager.fetchByOrderId(id);
    }

    private void checkSecurity(ModuleFlags flag) throws Exception {
        SecurityInterceptor.applySecurity(ctx.getCallerPrincipal().getName(), 
                                          "order", flag);
    }
}