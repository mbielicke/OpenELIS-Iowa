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
import javax.naming.InitialContext;
import javax.transaction.UserTransaction;

import org.jboss.ejb3.annotation.SecurityDomain;
import org.openelis.domain.InventoryReceiptViewDO;
import org.openelis.domain.OrderViewDO;
import org.openelis.domain.ReferenceTable;
import org.openelis.gwt.common.ModulePermission.ModuleFlags;
import org.openelis.local.InventoryReceiptLocal;
import org.openelis.local.LockLocal;
import org.openelis.manager.InventoryReceiptManager;
import org.openelis.remote.InventoryReceiptManagerRemote;
import org.openelis.utils.EJBFactory;

@Stateless
@SecurityDomain("openelis")
@RolesAllowed("inventoryreceipt-select")
@TransactionManagement(TransactionManagementType.BEAN)

public class InventoryReceiptManagerBean implements InventoryReceiptManagerRemote {   
    @Resource
    private SessionContext ctx;

    @EJB
    private LockLocal      lockBean;
    
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
            lockBean.validateLock(ReferenceTable.ORDER, order.getId());        
            man.update();
            lockBean.unlock(ReferenceTable.ORDER, order.getId());
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
        InventoryReceiptLocal rl;

        rl = local();
        ut = ctx.getUserTransaction();
        try {
            ut.begin();
            for (int i = 0; i < man.count(); i++ ) {
                data = man.getReceiptAt(i);
                man.setReceiptAt(rl.fetchForUpdate(data.getId()), i);
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
        InventoryReceiptLocal rl;
        
        rl = local();
        for (int i = 0; i < man.count(); i++) {
            data = man.getReceiptAt(i);
            man.setReceiptAt(rl.abortUpdate(data.getId()), i);
        }
        return man;
    }
    
    private void checkSecurity(ModuleFlags flag) throws Exception {
        EJBFactory.getUserCache().applyPermission("inventoryreceipt", flag);
    }
    
    private InventoryReceiptLocal local() {
        try {
            InitialContext ctx = new InitialContext();
            return (InventoryReceiptLocal)ctx.lookup("openelis/InventoryReceiptBean/local");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

}
