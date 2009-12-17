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

import org.jboss.annotation.security.SecurityDomain;
import org.openelis.domain.ReferenceTable;
import org.openelis.gwt.common.SecurityModule.ModuleFlags;
import org.openelis.local.LockLocal;
import org.openelis.manager.AuxFieldGroupManager;
import org.openelis.manager.AuxFieldManager;
import org.openelis.manager.AuxFieldValueManager;
import org.openelis.remote.AuxFieldGroupManagerRemote;
import org.openelis.utils.SecurityInterceptor;

@Stateless
@SecurityDomain("openelis")
@RolesAllowed("auxiliary-select")
@TransactionManagement(TransactionManagementType.BEAN)

public class AuxFieldGroupManagerBean implements AuxFieldGroupManagerRemote {
    @Resource
    private SessionContext ctx;

    @EJB
    private LockLocal      lockBean;

    public AuxFieldGroupManager fetchById(Integer id) throws Exception {
        return AuxFieldGroupManager.fetchById(id);
    }
    
    public AuxFieldGroupManager fetchWithFields(Integer id) throws Exception {
        return AuxFieldGroupManager.fetchWithFields(id);
        
    }

    public AuxFieldGroupManager add(AuxFieldGroupManager man) throws Exception {
        UserTransaction ut;

        checkSecurity(ModuleFlags.ADD);

        man.validate();

        ut = ctx.getUserTransaction();
        ut.begin();
        man.add();
        ut.commit();

        return man;
    }

    public AuxFieldGroupManager update(AuxFieldGroupManager man) throws Exception {
        UserTransaction ut;
        
        checkSecurity(ModuleFlags.UPDATE);

        man.validate();

        ut = ctx.getUserTransaction();
        ut.begin();
        lockBean.validateLock(ReferenceTable.AUX_FIELD_GROUP, man.getGroup().getId());        
        man.update();
        lockBean.giveUpLock(ReferenceTable.AUX_FIELD_GROUP, man.getGroup().getId());
        ut.commit();

        return man;
    }
    
    public AuxFieldGroupManager fetchForUpdate(Integer id) throws Exception {
        lockBean.getLock(ReferenceTable.AUX_FIELD_GROUP, id);
        return fetchById(id);
    }

    public AuxFieldGroupManager abortUpdate(Integer id) throws Exception {
        lockBean.giveUpLock(ReferenceTable.AUX_FIELD_GROUP, id);
        return fetchById(id);
    }
    
    public AuxFieldManager fetchAuxFieldById(Integer id) throws Exception {
        return AuxFieldManager.fetchById(id);
    }
    
    public AuxFieldManager fetchByGroupId(Integer id) throws Exception {
        return AuxFieldManager.fetchByGroupId(id);
    }

    public AuxFieldValueManager fetchFieldValueByFieldId(Integer id) throws Exception {
        return AuxFieldValueManager.fetchByAuxFieldId(id);
    }
    
    public AuxFieldManager fetchByGroupIdWithValues(Integer groupId) throws Exception {
        return AuxFieldManager.fetchByGroupIdWithValues(groupId);
    }
    
    private void checkSecurity(ModuleFlags flag) throws Exception {
        SecurityInterceptor.applySecurity(ctx.getCallerPrincipal().getName(), 
                                          "auxiliary", flag);
    }    
}
