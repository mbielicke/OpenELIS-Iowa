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
import org.openelis.manager.TestAnalyteManager;
import org.openelis.manager.TestManager;
import org.openelis.manager.TestPrepManager;
import org.openelis.manager.TestReflexManager;
import org.openelis.manager.TestResultManager;
import org.openelis.manager.TestTypeOfSampleManager;
import org.openelis.manager.TestWorksheetManager;

@Stateless
@SecurityDomain("openelis")
@TransactionManagement(TransactionManagementType.BEAN)
public class TestManagerBean {

    @Resource
    private SessionContext ctx;

    @EJB
    private LockBean      lockBean;
    
    @EJB
    private UserCacheBean  userCache;

    public TestManager fetchById(Integer id) throws Exception {
        return TestManager.fetchById(id);
    }

    public TestManager fetchWithSampleTypes(Integer id) throws Exception {
        return TestManager.fetchWithSampleTypes(id);
    }

    public TestManager fetchWithAnalytesAndResults(Integer id) throws Exception {
        return TestManager.fetchWithAnalytesAndResults(id);
    }

    public TestManager fetchWithPrepTestsSampleTypes(Integer id) throws Exception {
        return TestManager.fetchWithPrepTestsSampleTypes(id);
    }

    public TestManager fetchWithPrepTestsAndReflexTests(Integer id) throws Exception {
        return TestManager.fetchWithPrepTestAndReflexTests(id);
    }

    public TestManager fetchWithWorksheet(Integer id) throws Exception {
        return TestManager.fetchWithWorksheet(id);
    }

    public TestManager add(TestManager man) throws Exception {
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

    public TestManager update(TestManager man) throws Exception {
        UserTransaction ut;

        checkSecurity(ModuleFlags.UPDATE);

        man.validate();

        ut = ctx.getUserTransaction();
        try {
            ut.begin();
            lockBean.validateLock(Constants.table().TEST, man.getTest().getId());
            man.update();
            lockBean.unlock(Constants.table().TEST, man.getTest().getId());
            ut.commit();
        } catch (Exception e) {
            ut.rollback();
            throw e;
        }

        return man;
    }

    public TestManager fetchForUpdate(Integer id) throws Exception {
        UserTransaction ut;
        TestManager man;

        ut = ctx.getUserTransaction();
        try {
            ut.begin();
            lockBean.lock(Constants.table().TEST, id);
            man = fetchById(id);
            ut.commit();
            return man;
        } catch (Exception e) {
            ut.rollback();
            throw e;
        }
    }

    public TestManager abortUpdate(Integer id) throws Exception {
        lockBean.unlock(Constants.table().TEST, id);
        return fetchById(id);
    }

    public TestTypeOfSampleManager fetchSampleTypeByTestId(Integer id) throws Exception {
        return TestTypeOfSampleManager.fetchByTestId(id);
    }

    public TestAnalyteManager fetchTestAnalytesByTestId(Integer id) throws Exception {
        return TestAnalyteManager.fetchByTestId(id);
    }

    public TestResultManager fetchTestResultsByTestId(Integer id) throws Exception {
        return TestResultManager.fetchByTestId(id);
    }

    public TestPrepManager fetchPrepTestsByTestId(Integer id) throws Exception {
        return TestPrepManager.fetchByTestId(id);
    }

    public TestReflexManager fetchReflexiveTestsByTestId(Integer id) throws Exception {
        return TestReflexManager.fetchByTestId(id);
    }

    public TestWorksheetManager fetchWorksheetByTestId(Integer id) throws Exception {
        return TestWorksheetManager.fetchByTestId(id);
    }

    private void checkSecurity(ModuleFlags flag) throws Exception {
        userCache.applyPermission("test", flag);
    }

}
