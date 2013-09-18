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
import org.openelis.domain.Constants;
import org.openelis.domain.InventoryReceiptViewDO;
import org.openelis.domain.OrderViewDO;
import org.openelis.gwt.common.ModulePermission.ModuleFlags;
import org.openelis.local.InventoryReceiptLocal;
import org.openelis.local.LockLocal;
import org.openelis.manager.InventoryReceiptManager;
import org.openelis.remote.InventoryReceiptManagerRemote;
import org.openelis.utils.EJBFactory;

@Stateless
@SecurityDomain("openelis")
@TransactionManagement(TransactionManagementType.BEAN)
public class InventoryReceiptManagerBean implements InventoryReceiptManagerRemote {
    @Resource
    private SessionContext        ctx;

    @EJB
    private LockLocal             lockBean;

    @EJB
    private InventoryReceiptLocal inventoryReceipt;

    public InventoryReceiptManager add(InventoryReceiptManager man) throws Exception {
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
            e.printStackTrace();
            throw e;
        }

        return man;
    }

    public InventoryReceiptManager update(InventoryReceiptManager man) throws Exception {
        UserTransaction ut;
        OrderViewDO order;
        checkSecurity(ModuleFlags.UPDATE);

        man.validate();

        ut = ctx.getUserTransaction();
        order = man.getOrder().getOrder();
        try {
            ut.begin();
            lockBean.validateLock(Constants.table().ORDER, order.getId());
            man.update();
            lockBean.unlock(Constants.table().ORDER, order.getId());
            ut.commit();
        } catch (Exception e) {
            ut.rollback();
            throw e;
        }

        return man;
    }

    public InventoryReceiptManager fetchForUpdate(InventoryReceiptManager man) throws Exception {
        UserTransaction ut;
        InventoryReceiptViewDO data;

        ut = ctx.getUserTransaction();
        try {
            ut.begin();
            for (int i = 0; i < man.count(); i++ ) {
                data = man.getReceiptAt(i);
                man.setReceiptAt(inventoryReceipt.fetchForUpdate(data.getId()), i);
            }
            ut.commit();
            return man;
        } catch (Exception e) {
            ut.rollback();
            throw e;
        }
    }

    public InventoryReceiptManager abortUpdate(InventoryReceiptManager man) throws Exception {
        InventoryReceiptViewDO data;

        for (int i = 0; i < man.count(); i++ ) {
            data = man.getReceiptAt(i);
            man.setReceiptAt(inventoryReceipt.abortUpdate(data.getId()), i);
        }
        return man;
    }

    private void checkSecurity(ModuleFlags flag) throws Exception {
        EJBFactory.getUserCache().applyPermission("inventoryreceipt", flag);
    }
}