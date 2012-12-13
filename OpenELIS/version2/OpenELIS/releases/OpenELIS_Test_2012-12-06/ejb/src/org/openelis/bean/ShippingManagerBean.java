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
import org.openelis.manager.ShippingItemManager;
import org.openelis.manager.ShippingManager;
import org.openelis.manager.ShippingTrackingManager;
import org.openelis.remote.ShippingManagerRemote;
import org.openelis.utils.EJBFactory;

@Stateless
@SecurityDomain("openelis")
@TransactionManagement(TransactionManagementType.BEAN)

public class ShippingManagerBean implements ShippingManagerRemote {

    @Resource
    private SessionContext ctx;

    @EJB
    private LockLocal      lockBean;

    public ShippingManager fetchById(Integer id) throws Exception {
        return ShippingManager.fetchById(id);
    }   
    
    public ShippingManager fetchWithItemsAndTracking(Integer id) throws Exception {
        return ShippingManager.fetchWithItemsAndTracking(id);
    }

    public ShippingManager fetchWithNotes(Integer id) throws Exception {
        return ShippingManager.fetchWithNotes(id);
    }
    
    public ShippingManager add(ShippingManager man) throws Exception {
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
    
    public ShippingManager update(ShippingManager man) throws Exception {
        UserTransaction ut;
        
        checkSecurity(ModuleFlags.UPDATE);

        man.validate();

        ut = ctx.getUserTransaction();
        try {
            ut.begin();
            lockBean.validateLock(ReferenceTable.SHIPPING, man.getShipping().getId());        
            man.update();
            lockBean.unlock(ReferenceTable.SHIPPING, man.getShipping().getId());
            ut.commit();
        } catch (Exception e) {
            ut.rollback();
            throw e;
        }

        return man;
    }

    public ShippingManager fetchForUpdate(Integer id) throws Exception {
        UserTransaction ut;
        ShippingManager man;
        
        ut = ctx.getUserTransaction();
        try {
            ut.begin();
            lockBean.lock(ReferenceTable.SHIPPING, id);
            man = fetchById(id);
            ut.commit();
            return man;
        } catch (Exception e) {
            ut.rollback();
            throw e;
        }        
    }
    
    public ShippingManager abortUpdate(Integer id) throws Exception {
        lockBean.unlock(ReferenceTable.SHIPPING, id);
        return fetchById(id);
    }

    public ShippingItemManager fetchItemByShippingId(Integer id) throws Exception {
        return ShippingItemManager.fetchByShippingId(id);
    }

    public ShippingTrackingManager fetchTrackingByShippingId(Integer id) throws Exception {
        return ShippingTrackingManager.fetchByShippingId(id);
    }
    
    private void checkSecurity(ModuleFlags flag) throws Exception {
        EJBFactory.getUserCache().applyPermission("shipping", flag);
    }
}
