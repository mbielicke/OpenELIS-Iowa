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

import org.jboss.ejb3.annotation.SecurityDomain;
import org.openelis.domain.ReferenceTable;
import org.openelis.gwt.common.ModulePermission.ModuleFlags;
import org.openelis.local.LockLocal;
import org.openelis.manager.InventoryComponentManager;
import org.openelis.manager.InventoryItemManager;
import org.openelis.manager.InventoryLocationManager;
import org.openelis.remote.InventoryItemManagerRemote;
import org.openelis.utils.PermissionInterceptor;

@Stateless
@SecurityDomain("openelis")
@RolesAllowed("inventoryitem-select")
@TransactionManagement(TransactionManagementType.BEAN)

public class InventoryItemManagerBean implements InventoryItemManagerRemote {

    @Resource
    private SessionContext ctx;

    @EJB
    private LockLocal      lockBean;

    public InventoryItemManager fetchById(Integer id) throws Exception {
        return InventoryItemManager.fetchById(id);
    }

    public InventoryItemManager fetchWithComponents(Integer id) throws Exception {
        return InventoryItemManager.fetchWithComponents(id);
    }

    public InventoryItemManager fetchWithLocations(Integer id) throws Exception {
        return InventoryItemManager.fetchWithLocations(id);
    }

    public InventoryItemManager fetchWithManufacturing(Integer id) throws Exception {
        return InventoryItemManager.fetchWithManufacturing(id);
    }

    public InventoryItemManager fetchWithNotes(Integer id) throws Exception {
        return InventoryItemManager.fetchWithNotes(id);
    }

    public InventoryItemManager add(InventoryItemManager man) throws Exception {
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

    public InventoryItemManager update(InventoryItemManager man) throws Exception {
        UserTransaction ut;
        
        checkSecurity(ModuleFlags.UPDATE);

        man.validate();

        ut = ctx.getUserTransaction();
        try {
            ut.begin();
            lockBean.validateLock(ReferenceTable.INVENTORY_ITEM, man.getInventoryItem().getId());        
            man.update();
            lockBean.unlock(ReferenceTable.INVENTORY_ITEM, man.getInventoryItem().getId());
            ut.commit();
        } catch (Exception e) {
            ut.rollback();
            throw e;
        }

        return man;
    }

    public InventoryItemManager fetchForUpdate(Integer id) throws Exception {
        UserTransaction ut;
        InventoryItemManager man;

        ut = ctx.getUserTransaction();
        try {
            ut.begin();
            lockBean.lock(ReferenceTable.INVENTORY_ITEM, id);
            man = fetchById(id);
            ut.commit();
            return man;
        } catch (Exception e) {
            ut.rollback();
            throw e;
        }
    }

    public InventoryItemManager abortUpdate(Integer id) throws Exception {
        lockBean.unlock(ReferenceTable.INVENTORY_ITEM, id);
        return fetchById(id);
    }

    public InventoryComponentManager fetchComponentByInventoryItemId(Integer id) throws Exception {
        return InventoryComponentManager.fetchByInventoryItemId(id);
    }
    
    public InventoryLocationManager fetchLocationByInventoryItemId(Integer id) throws Exception {
        return InventoryLocationManager.fetchByInventoryItemId(id);
    }

    private void checkSecurity(ModuleFlags flag) throws Exception {
        PermissionInterceptor.applyPermission("inventoryitem", flag);
    }
}
