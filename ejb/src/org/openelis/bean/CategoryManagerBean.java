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
import org.openelis.manager.CategoryManager;
import org.openelis.manager.DictionaryManager;
import org.openelis.remote.CategoryManagerRemote;
import org.openelis.utils.SecurityInterceptor;

@Stateless
@SecurityDomain("openelis")
@RolesAllowed("dictionary-select")
@TransactionManagement(TransactionManagementType.BEAN)
public class CategoryManagerBean implements CategoryManagerRemote {

    @PersistenceContext(name = "openelis")
    @Resource
    private SessionContext ctx;

    @EJB
    private LockLocal      lockBean;

    public CategoryManagerBean() {       
    }

    public CategoryManager fetchById(Integer id) throws Exception {
        return CategoryManager.fetchById(id);
    }
    
    public CategoryManager fetchWithEntries(Integer id) throws Exception {
        return CategoryManager.fetchWithEntries(id);
    }

    public CategoryManager add(CategoryManager man) throws Exception {
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

    public CategoryManager update(CategoryManager man) throws Exception {
        UserTransaction ut;

        checkSecurity(ModuleFlags.UPDATE);

        man.validate();

        ut = ctx.getUserTransaction();
        try {
            ut.begin();
            lockBean.validateLock(ReferenceTable.DICTIONARY, man.getCategory().getId());
            man.update();
            lockBean.giveUpLock(ReferenceTable.DICTIONARY, man.getCategory().getId());
            ut.commit();
        } catch (Exception e) {
            ut.rollback();
            throw e;
        }

        return man;
    }

    public CategoryManager fetchForUpdate(Integer categoryId) throws Exception {
        lockBean.getLock(ReferenceTable.DICTIONARY, categoryId);
        return fetchById(categoryId);
    }

    public CategoryManager abortUpdate(Integer categoryId) throws Exception {
        lockBean.giveUpLock(ReferenceTable.DICTIONARY, categoryId);
        return fetchById(categoryId);
    }
    
    public DictionaryManager fetchEntryByCategoryId(Integer id) throws Exception {
        return DictionaryManager.fetchByCategoryId(id);
    }

    private void checkSecurity(ModuleFlags flag) throws Exception {
        SecurityInterceptor.applySecurity(ctx.getCallerPrincipal().getName(),
                                          "dictionary", flag);
    }
}
