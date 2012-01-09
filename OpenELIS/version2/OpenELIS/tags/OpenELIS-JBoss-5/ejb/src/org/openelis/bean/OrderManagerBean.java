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
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.transaction.UserTransaction;

import org.jboss.ejb3.annotation.SecurityDomain;
import org.openelis.domain.OrderRecurrenceDO;
import org.openelis.domain.ReferenceTable;
import org.openelis.gwt.common.ModulePermission.ModuleFlags;
import org.openelis.local.LockLocal;
import org.openelis.manager.OrderContainerManager;
import org.openelis.manager.OrderFillManager;
import org.openelis.manager.OrderItemManager;
import org.openelis.manager.OrderManager;
import org.openelis.manager.OrderReceiptManager;
import org.openelis.manager.OrderTestManager;
import org.openelis.remote.OrderManagerRemote;
import org.openelis.utils.EJBFactory;

@Stateless
@SecurityDomain("openelis")

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

    public OrderManager fetchWithFills(Integer id) throws Exception {
        return OrderManager.fetchWithFills(id);
    }

    public OrderManager fetchWithNotes(Integer id) throws Exception {
        return OrderManager.fetchWithNotes(id);
    }
    
    public OrderManager fetchWithTestsAndContainers(Integer id) throws Exception {        
        return OrderManager.fetchWithTestsAndContainers(id);
    }
    
    public OrderManager fetchWithRecurring(Integer id) throws Exception {
        return OrderManager.fetchWithRecurrence(id);
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
            lockBean.unlock(ReferenceTable.ORDER, man.getOrder().getId());
            ut.commit();
        } catch (Exception e) {
            ut.rollback();
            throw e;
        }

        return man;
    }

    public OrderManager fetchForUpdate(Integer id) throws Exception {
        UserTransaction ut;
        OrderManager man;

        ut = ctx.getUserTransaction();
        try {
            ut.begin();
            lockBean.lock(ReferenceTable.ORDER, id);
            man = fetchById(id);
            man.getRecurrence();
            ut.commit();
            return man;
        } catch (Exception e) {
            ut.rollback();
            throw e;
        }
    }

    public OrderManager abortUpdate(Integer id) throws Exception {
        lockBean.unlock(ReferenceTable.ORDER, id);
        return fetchById(id);
    }

    public OrderItemManager fetchItemByOrderId(Integer id) throws Exception {
        return OrderItemManager.fetchByOrderId(id);
    }

    public OrderFillManager fetchFillByOrderId(Integer id) throws Exception {
        return OrderFillManager.fetchByOrderId(id);
    }
    
    public OrderReceiptManager fetchReceiptByOrderId(Integer id) throws Exception {
        return OrderReceiptManager.fetchByOrderId(id);
    }
    
    public OrderTestManager fetchTestByOrderId(Integer id) throws Exception {       
        return OrderTestManager.fetchByOrderId(id);
    }
    
    public OrderContainerManager fetchContainerByOrderId(Integer id) throws Exception {        
        return OrderContainerManager.fetchByOrderId(id);
    }
    
    public OrderRecurrenceDO fetchRecurrenceByOrderId(Integer id) throws Exception {        
        return OrderManager.fetchRecurrenceByOrderId(id);
    }
    
    private void checkSecurity(ModuleFlags flag) throws Exception {
        EJBFactory.getUserCache().applyPermission("order", flag);
    }
}