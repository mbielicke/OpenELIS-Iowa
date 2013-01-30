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
import org.openelis.gwt.common.ModulePermission.ModuleFlags;
import org.openelis.manager.AuxFieldGroupManager;
import org.openelis.manager.AuxFieldManager;
import org.openelis.manager.AuxFieldValueManager;

@Stateless
@SecurityDomain("openelis")
@TransactionManagement(TransactionManagementType.BEAN)
public class AuxFieldGroupManagerBean {
    @Resource
    private SessionContext ctx;

    @EJB
    private LockBean     lockBean;
    
    @EJB
    UserCacheBean          userCache;

    public AuxFieldGroupManager fetchById(Integer id) throws Exception {
        return AuxFieldGroupManager.fetchById(id);
    }

    public AuxFieldGroupManager fetchByIdWithFields(Integer id) throws Exception {
        return AuxFieldGroupManager.fetchByIdWithFields(id);
    }

    public AuxFieldGroupManager add(AuxFieldGroupManager man) throws Exception {
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

    public AuxFieldGroupManager update(AuxFieldGroupManager man) throws Exception {
        UserTransaction ut;

        checkSecurity(ModuleFlags.UPDATE);

        man.validate();

        ut = ctx.getUserTransaction();
        try {
            ut.begin();
            lockBean.validateLock(Constants.table().AUX_FIELD_GROUP, man.getGroup()
                                                                        .getId());
            man.update();
            lockBean.unlock(Constants.table().AUX_FIELD_GROUP, man.getGroup().getId());
            ut.commit();
        } catch (Exception e) {
            ut.rollback();
            throw e;
        }

        return man;
    }

    public AuxFieldGroupManager fetchForUpdate(Integer id) throws Exception {
        UserTransaction ut;
        AuxFieldGroupManager man;

        ut = ctx.getUserTransaction();
        try {
            ut.begin();
            lockBean.lock(Constants.table().AUX_FIELD_GROUP, id);
            man = fetchById(id);
            ut.commit();
            return man;
        } catch (Exception e) {
            ut.rollback();
            throw e;
        }
    }

    public AuxFieldGroupManager abortUpdate(Integer id) throws Exception {
        lockBean.unlock(Constants.table().AUX_FIELD_GROUP, id);
        return fetchById(id);
    }

    public AuxFieldManager fetchFieldById(Integer id) throws Exception {
        return AuxFieldManager.fetchById(id);
    }

    public AuxFieldManager fetchFieldByGroupId(Integer id) throws Exception {
        return AuxFieldManager.fetchByGroupId(id);
    }

    public AuxFieldManager fetchFieldByGroupIdWithValues(Integer groupId) throws Exception {
        return AuxFieldManager.fetchByGroupIdWithValues(groupId);
    }

    public AuxFieldValueManager fetchFieldValueByFieldId(Integer fieldId) throws Exception {
        return AuxFieldValueManager.fetchByFieldId(fieldId);
    }

    private void checkSecurity(ModuleFlags flag) throws Exception {
        userCache.applyPermission("auxiliary", flag);
    }
}
