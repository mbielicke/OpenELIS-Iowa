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

import org.jboss.security.annotation.SecurityDomain;
import org.openelis.domain.Constants;
import org.openelis.manager.ShippingItemManager;
import org.openelis.manager.ShippingManager;
import org.openelis.manager.ShippingTrackingManager;
import org.openelis.ui.common.ModulePermission.ModuleFlags;

@Stateless
@SecurityDomain("openelis")
@TransactionManagement(TransactionManagementType.BEAN)
public class ShippingManagerBean {
    @Resource
    private SessionContext ctx;

    @EJB
    private LockBean      lockBean;

    @EJB
    private UserCacheBean  userCache;
    
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
            lockBean.validateLock(Constants.table().SHIPPING, man.getShipping().getId());
            man.update();
            lockBean.unlock(Constants.table().SHIPPING, man.getShipping().getId());
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
            lockBean.lock(Constants.table().SHIPPING, id);
            man = fetchById(id);
            ut.commit();
            return man;
        } catch (Exception e) {
            ut.rollback();
            throw e;
        }
    }

    public ShippingManager abortUpdate(Integer id) throws Exception {
        lockBean.unlock(Constants.table().SHIPPING, id);
        return fetchById(id);
    }

    public ShippingItemManager fetchItemByShippingId(Integer id) throws Exception {
        return ShippingItemManager.fetchByShippingId(id);
    }

    public ShippingTrackingManager fetchTrackingByShippingId(Integer id) throws Exception {
        return ShippingTrackingManager.fetchByShippingId(id);
    }

    private void checkSecurity(ModuleFlags flag) throws Exception {
        userCache.applyPermission("shipping", flag);
    }
}
