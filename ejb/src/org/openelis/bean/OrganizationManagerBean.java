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
import javax.persistence.PersistenceContext;
import javax.transaction.UserTransaction;

import org.jboss.annotation.security.SecurityDomain;
import org.openelis.domain.ReferenceTable;
import org.openelis.gwt.common.SecurityModule.ModuleFlags;
import org.openelis.local.LockLocal;
import org.openelis.manager.OrganizationContactManager;
import org.openelis.manager.OrganizationManager;
import org.openelis.manager.OrganizationParameterManager;
import org.openelis.remote.OrganizationManagerRemote;
import org.openelis.utils.SecurityInterceptor;

@Stateless
@SecurityDomain("openelis")
@RolesAllowed("organization-select")
@TransactionManagement(TransactionManagementType.BEAN)

public class OrganizationManagerBean implements OrganizationManagerRemote {

    @Resource
    private SessionContext ctx;

    @EJB
    private LockLocal      lockBean;

    public OrganizationManagerBean() {
    }

    public OrganizationManager fetchById(Integer id) throws Exception {
        return OrganizationManager.fetchById(id);
    }

    public OrganizationManager fetchWithContacts(Integer id) throws Exception {
        return OrganizationManager.fetchWithContacts(id);
    }

    public OrganizationManager fetchWithParameters(Integer id) throws Exception {
        return OrganizationManager.fetchWithParameters(id);
    }

    public OrganizationManager fetchWithNotes(Integer id) throws Exception {
        return OrganizationManager.fetchWithNotes(id);
    }

    public OrganizationManager add(OrganizationManager man) throws Exception {
        UserTransaction ut;

        checkSecurity(ModuleFlags.ADD);

        man.validate();

        ut = ctx.getUserTransaction();
        ut.begin();
        man.add();
        ut.commit();

        return man;
    }

    public OrganizationManager update(OrganizationManager man) throws Exception {
        UserTransaction ut;
        
        checkSecurity(ModuleFlags.UPDATE);

        man.validate();

        ut = ctx.getUserTransaction();
        ut.begin();
        lockBean.validateLock(ReferenceTable.ORGANIZATION, man.getOrganization().getId());        
        man.update();
        lockBean.giveUpLock(ReferenceTable.ORGANIZATION, man.getOrganization().getId());
        ut.commit();

        return man;
    }

    public OrganizationManager fetchForUpdate(Integer id) throws Exception {
        lockBean.getLock(ReferenceTable.ORGANIZATION, id);
        return fetchById(id);
    }

    public OrganizationManager abortUpdate(Integer id) throws Exception {
        lockBean.giveUpLock(ReferenceTable.ORGANIZATION, id);
        return fetchById(id);
    }

    public OrganizationContactManager fetchContactByOrganizationId(Integer id) throws Exception {
        return OrganizationContactManager.fetchByOrganizationId(id);
    }
    
    public OrganizationParameterManager fetchParameterByOrganizationId(Integer id) throws Exception {
        return OrganizationParameterManager.fetchByOrganizationId(id);
    }

    private void checkSecurity(ModuleFlags flag) throws Exception {
        SecurityInterceptor.applySecurity(ctx.getCallerPrincipal().getName(), 
                                          "organization", flag);
    }
}